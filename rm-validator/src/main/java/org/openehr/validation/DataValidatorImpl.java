/*
 * component: "openEHR Reference Java Implementation" description: "Class
 * DataValidatorImpl" keywords: "validator"
 *
 * author: "Rong Chen <rong.acode@gmail.com>" support: "openEHR Java Project
 * <ref_impl_java@openehr.org>" copyright: "Copyright (c) 2008 Cambio Healthcare
 * Systems, Sweden" license: "See notice at bottom of class"
 *
 * file: "$URL$" revision: "$LastChangedRevision$" last_change:
 * "$LastChangedDate$"
 */
package org.openehr.validation;

import br.ufg.inf.fs.pep.archetypes.ArchetypeRepositoryFactory;
import br.ufg.inf.fs.pep.utils.log.Trace;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.assertion.Assertion;
import org.openehr.am.archetype.assertion.ExpressionBinaryOperator;
import org.openehr.am.archetype.assertion.ExpressionLeaf;
import org.openehr.am.archetype.constraintmodel.*;
import org.openehr.am.archetype.constraintmodel.primitive.CString;
import org.openehr.am.archetype.ontology.ArchetypeOntology;
import org.openehr.am.archetype.ontology.ArchetypeTerm;
import org.openehr.am.archetype.ontology.OntologyDefinitions;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.basic.DataValue;
import org.openehr.rm.support.basic.Interval;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.validation.exceptions.GenericValidationException;

/**
 * Implementation of an archetype-based data validator
 * TODO documentar (métodos não estão documentados)
 * @author rong.chen
 */
public class DataValidatorImpl implements DataValidator {

    /**
	 * Validates the data using given archetype
	 * 
	 * @param data
	 * @return errors empty if data pass validation without error
	 */
    @Override
    public List<ValidationError> validate(Locatable data)
            throws GenericValidationException {
        String archetypeId = data.getArchetypeNodeId();
        Archetype archetype = ArchetypeRepositoryFactory.getInstance().getArchetype(archetypeId);
        if (archetype == null) {
            throw new GenericValidationException("Archetype " + archetypeId
                    + " not found.");
        }

        return this.validate(data, archetype);
    }

    /**
	 * Validates the data using given archetype
	 * 
	 * @param data
	 * @param archetype
	 * @return errors empty if data pass validation without error
	 */
    @Override
    public List<ValidationError> validate(Locatable data, Archetype archetype)
            throws GenericValidationException {

        log.debug("validate archetype: " + archetype.getArchetypeId());

        List<ValidationError> errors = new ArrayList<ValidationError>();
        validateComplex(archetype.getDefinition(), data,
                Locatable.PATH_SEPARATOR, errors, archetype);
        return errors;
    }

    /**
     * Valida as restrições contidas em um CComplexObject
     * @param ccobj
     * @param object
     * @param path
     * @param errors
     * @param archetype
     * @throws GenericValidationException
     */
    void validateComplex(CComplexObject ccobj, Object object, String path,
            List<ValidationError> errors, Archetype archetype)
            throws GenericValidationException {

        log.debug("validate CComplexObject of type: " + ccobj.getRmTypeName()
                + " at path: " + path);

        String newPath = null;

        // loop through the attributes
        for (CAttribute cattr : ccobj.getAttributes()) {

            Object attribute = null;
            try {
                attribute = fetchAttribute(object, cattr);
            } catch (Exception ex) {
                continue;
            }

            log.debug("attribute " + cattr.getRmAttributeName()
                    + " isRequired: " + cattr.isRequired()
                    + ", attribute == null ? " + (attribute == null));
            newPath = includePathSeparator(path);
            newPath += cattr.getRmAttributeName();
            String description = getErrorDescription(archetype, ccobj, null);
            validateAttribute(cattr, attribute, path, archetype, description,
                    errors, newPath);

        }
    }

    /**
     * Verifica se a quantidade de membros de values é menor do que o mínimo
     * permitido
     * @param interval
     * @param values
     * @return
     */
    private boolean abaixoDoMinimo(Interval interval, Collection values) {

        return interval.getLower() != null &&
                interval.getLower().compareTo(values.size()) > 0;
    }

    /**
     * Verifica se a quantidade de membros de values é maior do que o máximo
     * permitido
     * @param interval
     * @param values
     * @return
     */
    private boolean acimaMaximo(Interval interval, Collection values) {
        return interval.getUpper() != null && 
                interval.getUpper().compareTo(values.size())<0;
    }

    /**
     * A partir de um construtor e de um argumento para o construtor, retorna
     * o objeto construido.
     * @param constructor
     * @param argument
     * @return
     */
    private Object build(Constructor constructor, Object argument) {
        if (constructor != null) {
            try {
                argument = constructor.newInstance(argument);
            } catch (Exception ex) {
                Trace.log(ex.getMessage());
            }
        }
        return argument;
    }

    /**
     * Obtem termo arquétipo
     * @param termosArquetipos
     * @param nodeId
     * @return
     */
    private String getArchetypeTerm(List<ArchetypeTerm> termosArquetipos,
            String nodeId) {
        String description = null;
        if (termosArquetipos != null) {
            for (ArchetypeTerm archetypeTerm : termosArquetipos) {
                String term = archetypeTerm.getCode();
                if (nodeId.equals(term)) {
                    description = archetypeTerm.getDescription();
                    break;
                }
            }
        }
        return description;
    }

    /**
     * Extrai lista de termos arquétipos de um determinado idioma
     * @param ontologyDefinitions
     * @param idioma
     * @return
     */
    private List<ArchetypeTerm> getArchetypeTerms(
            List<OntologyDefinitions> ontologyDefinitions, String idioma) {
        List<ArchetypeTerm> archetypeTerms = null;
        for (OntologyDefinitions ontologyDefinition : ontologyDefinitions) {
            if (ontologyDefinition.getLanguage().equals(idioma)) {
                archetypeTerms = ontologyDefinition.getDefinitions();
                break;
            }
        }
        return archetypeTerms;
    }

    /**
     * Retorna construtor de um parametro {@link String}
     * @param klass
     * @return
     * @throws SecurityException
     */
    private Constructor getConstructorWithStringArgument(Class klass)
            throws SecurityException {
        Constructor constructor = null;
        
        for (Constructor constr : klass.getConstructors()) {
            if (constr.getParameterTypes().length == 1 &&
                    constr.getParameterTypes()[0].equals(String.class)) {
                constructor = constr;
                break;
            }
        }
        return constructor;
    }

    /**
     * Retorna se a restrição é instancia de {@link ArchetypeSlot}
     * @param cObj
     * @return
     */
    private boolean instanciaArchetypeSlot(CObject cObj) {
        return cObj instanceof ArchetypeSlot;
    }

    /**
     * Retorna se uma restrição é atendida no dado
     * @param cObj
     * @param data
     * @return
     */
    private boolean objetoAtendeRestricao(CObject cObj, Locatable data) {
        if (instanciaArchetypeSlot(cObj)) {
            log.debug("ArchetypeSlot : " + data.getArchetypeNodeId());
            ArchetypeSlot slot = (ArchetypeSlot) cObj;
            return validateRootSlot(slot, data);
        } else {
            return cObj.getNodeId().equals(data.getArchetypeNodeId());
        }
    }

    /**
     * Valida {@link CSingleAttribute} quando a restrição possui mais de uma
     * alternativa
     * @param cattr
     * @param attribute
     * @param path
     * @param archetype
     * @param errors
     * @throws GenericValidationException
     */
    private void validaAlternativasSingleAttribute(CSingleAttribute cattr, 
            Object attribute, String path, Archetype archetype,
            List<ValidationError> errors) throws GenericValidationException {

        List<ValidationError> newErrors = null;
        for (CObject cobj : cattr.alternatives()) {
            newErrors = new ArrayList<ValidationError>();
            validateObject(cobj, attribute, path, newErrors, archetype);
            if (newErrors.isEmpty()) {
                return;
            }
        }
        ValidationError error = new ValidationError(archetype, path, path, ErrorType.ALTERNATIVES_NOT_SATISFIED, getErrorDescription(archetype, cattr, null));
        errors.add(error);
    }

    /**
     * Percorre os objetos em uma lista validando cada um dos membros
     * @param objects
     * @param path
     * @param cobj
     * @param newPath
     * @param errors
     * @param archetype
     * @throws GenericValidationException
     */
    private void validaObjetosEmCollections(List<Object> objects, String path,
            CObject cobj, String newPath, List<ValidationError> errors,
            Archetype archetype) throws GenericValidationException {
        
        for (Object obj : objects) {
            newPath = path + "[" + cobj.getNodeId() + "]";
            validateObject(cobj, obj, newPath, errors, archetype);
        }
    }

    /**
     * Valida as restrições contidas em um CAttribute
     * @param cattr
     * @param attribute
     * @param path
     * @param archetype
     * @param description
     * @param errors
     * @param newPath
     * @throws GenericValidationException
     */
    private void validateAttribute(CAttribute cattr, Object attribute,
            String path, Archetype archetype, String description,
            List<ValidationError> errors,String newPath)
            throws GenericValidationException {

        if (cattr.isRequired() && attribute == null) {
            log.debug("ERROR --> attribute missing at " + path);
            errors.add(new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ATTRIBUTE_MISSING, description));

        } else if (!cattr.isAllowed() && attribute != null) {
            errors.add(new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ATTRIBUTE_NOT_ALLOWED, description));
            
        } else if (attribute != null) {
            defineTipoAtributoParaValidar(cattr, attribute, newPath, errors,
                    archetype);
        }
    }

    /**
     * Decide se a restrição é um CSingleAttribute ou CMultipleAttribute
     * @param cattr
     * @param attribute
     * @param newPath
     * @param errors
     * @param archetype
     * @throws GenericValidationException
     */
    private void defineTipoAtributoParaValidar(CAttribute cattr,
            Object attribute, String newPath, List<ValidationError> errors,
            Archetype archetype) throws GenericValidationException {

        if (cattr instanceof CSingleAttribute) {
            validateSingleAttribute((CSingleAttribute) cattr, attribute,
                    newPath, errors, archetype);
        } else {
            validateMultipleAttribute((CMultipleAttribute) cattr, attribute,
                    newPath, errors, archetype);
        }
    }

    /**
     * Executar o método de obtenção do valor de um atributo
     * @param object
     * @param getter
     * @return
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private Object executeGetter(Object object, String getter) throws 
            SecurityException, IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {

        Method method = null;
        method = object.getClass().getMethod(getter, null);
        return method.invoke(object, null);
    }

    /**
     * Extrai determinado termo arquétipo de um respectivo idioma
     * @param archetype
     * @param language
     * @param nodeId
     * @return
     */
    private String extractTerm(Archetype archetype, String language,
            String nodeId) {
        ArchetypeOntology ontology = archetype.getOntology();
        String lang = language == null ? "pt-br" : language;
        String description = null;
        List<OntologyDefinitions> ontologyDefinitions =
                ontology.getTermDefinitionsList();
        List<ArchetypeTerm> archetypeTerms = null;
        archetypeTerms = getArchetypeTerms(ontologyDefinitions, lang);
        description = getArchetypeTerm(archetypeTerms, nodeId);
        return description;
    }

    /**
     * Inclui path separator "/" no path
     * @param path
     * @return
     */
    private String includePathSeparator(String path) {
        if (!path.equals(Locatable.PATH_SEPARATOR)) {
            return path + Locatable.PATH_SEPARATOR;
        }
        return path;
    }

    /**
     * Valida a conformidade de restrições descritas em {@link CSingleAttribute}
     * @param cattr
     * @param attribute
     * @param path
     * @param errors
     * @param archetype
     * @throws GenericValidationException
     */
    void validateSingleAttribute(CSingleAttribute cattr, Object attribute,
            String path, List<ValidationError> errors, Archetype archetype)
            throws GenericValidationException {

        log.debug("validateSingleAttribute..");

        if (cattr.alternatives().size() > 1) {
            validaAlternativasSingleAttribute(cattr, attribute, path,
                    archetype, errors);
        } else if (cattr.alternatives().size() == 1) {
            CObject cobj = cattr.alternatives().get(0);
            validateObject(cobj, attribute, path, errors, archetype);
        }

    }

    /**
     * Verificar conformidade de objetos {@link Collection} com as restrições
     * contidas em {@link CMultipleAttribute}
     * @param cattr
     * @param attribute
     * @param path
     * @param errors
     * @param archetype
     * @throws GenericValidationException
     */
    void validateMultipleAttribute(CMultipleAttribute cattr, Object attribute,
            String path, List<ValidationError> errors, Archetype archetype)
            throws GenericValidationException {
        log.debug("validateMultipleAttribute..");

        
        List<CObject> children = cattr.getChildren();
        if (children == null) {
            return;
        }
        Collection<Object> values = (Collection<Object>) attribute;

        ValidationError errorCardinality = validateCardinality(values,
                archetype, path, cattr);
        if(errorCardinality!=null){
            errors.add(errorCardinality);
            return;
        }

        log.debug("validating total cobj: " + children.size() + ", values: "
                + values.size());

        List<Object> objects = null;
        String newPath = null;
        int contador = 0;
        for (CObject cobj : children) {

            log.debug("validating sub-cobj at: " + cobj.path());

            objects = findMatchingNodes(values, cobj);
            contador += objects.size();

            ValidationError occurrenceError = validateOccurrence(cobj, objects,
                    archetype, path);
            if(occurrenceError!=null){
                errors.add(occurrenceError);
                return;
            }
            validaObjetosEmCollections(objects, path, cobj, newPath, errors,
                    archetype);
        }
        if (contador != values.size()) {
            errors.add(new ValidationError(archetype, path, cattr.path(),
                    ErrorType.OCCURRENCES_NOT_DESCRIBED, null));
        }
    }

    /**
     * Valida a ocorrencia de cada {@link CObject} em um {@link Collection}
     * @param cobj
     * @param objects
     * @param archetype
     * @param path
     * @return
     */
    private ValidationError validateOccurrence(CObject cobj, 
            Collection<Object> objects, Archetype archetype, String path) {

        Interval<Integer> occurrences = cobj.getOccurrences();
        if (occurrences == null) {
            return null;
        }

        ValidationError error = null;
        if (abaixoDoMinimo(occurrences, objects)) {
            error = new ValidationError(archetype, path, cobj.path(),
                    ErrorType.OCCURRENCES_TOO_FEW, null);
        } else if (acimaMaximo(occurrences, objects)) {
            error = new ValidationError(archetype, path, cobj.path(),
                    ErrorType.OCCURRENCES_TOO_MANY, null);
        }
        return error;
    }

    /**
     *
     * @param values
     * @param archetype
     * @param path
     * @param cattr
     * @return
     */
    private ValidationError validateCardinality(Collection<Object> values, 
            Archetype archetype, String path, CMultipleAttribute cattr){
        Cardinality cardinality = cattr.getCardinality();
        Interval<Integer> interval = cardinality.getInterval();

        log.debug("cardinality.interval: " + interval);

        if (abaixoDoMinimo(interval, values)){

            return new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ITEMS_TOO_FEW, null);

        } else if (acimaMaximo(interval, values)) {

            return new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ITEMS_TOO_MANY, null);
        }
        return null;
        
    }

    /**
	 * Finds matching value node using AT code of CObject
	 * 
	 * @param values
	 * @param code
	 * @return empty list if not found
	 */
    List<Object> findMatchingNodes(Collection<Object> values, CObject cObj) {
        String type = cObj.getRmTypeName().
                toUpperCase(KnownTypes.getLocale()).replace("_", "");
        Class klasse = KnownTypes.getAllTypes().get(type);

        if(Locatable.class.isAssignableFrom(klasse)){
            return findMatchingObjects(values, cObj);
        }
        
        return findMatchingDataValue(values, cObj, klasse);
    }

    /**
     * Captura objetos em um {@link Collection} que esteja em conformidade com
     * uma restrição
     * @param dados
     * @param constraint
     * @return
     */
    private List<Object> findMatchingObjects(Collection<Object> dados,
            CObject constraint) {
        List<Object> objects = new ArrayList<Object>();
        Locatable lo = null;
        for (Object value : dados) {
            if (!(value instanceof Locatable)) {
                log.warn("trying to find matching value on un-pathable obj..");
                continue;
            }

            lo = (Locatable) value;
            if(objetoAtendeRestricao(constraint, lo)){
                objects.add(lo);
            }
        }
        return objects;
    }

    /**
     * Captura os objetos que herdam de {@link DataValue}
     * @param values
     * @param cObj
     * @param klass
     * @return
     */
    private List<Object> findMatchingDataValue(Collection<Object> values,
            CObject cObj, Class klass) {
        List<Object> objects = new ArrayList<Object>();
        DataValue dv = null;
        for (Object value : values) {
            if (!(value instanceof DataValue)) {
                log.warn("trying to find matching value on un-pathable obj..");
                continue;
            }
            dv = (DataValue) value;
            if(klass.isAssignableFrom(value.getClass())){
                log.debug("value found for code: " + cObj.getNodeId());
                objects.add(dv);
            }
        }
        return objects;
    }

    /**
     * Confronta um dado com um objeto de restrição
     * @param cobj
     * @param value
     * @param path
     * @param errors
     * @param archetype
     * @throws GenericValidationException
     */
    void validateObject(CObject cobj, Object value, String path,
            List<ValidationError> errors, Archetype archetype)
            throws GenericValidationException {

        log.debug("validate CObject..");
        Class dataClass = value.getClass();
        String restrictionType = cobj.getRmTypeName().replace("_", "").
                toUpperCase(KnownTypes.getLocale());
        restrictionType = restrictionType.split("<")[0];

        Class restClass = KnownTypes.getAllTypes().get(restrictionType);
        if (!restClass.isAssignableFrom(dataClass)
                && (!(cobj instanceof CPrimitiveObject))) {
            // verificar se o tipo eh primitivo e se o dado eh String
            errors.add(new ValidationError(archetype, path, cobj.path(),
                    ErrorType.RM_TYPE_INVALID, getErrorDescription(archetype,
                    cobj, null)));
            return;
        }
        if(cobj.isAnyAllowed()){
            return;
        }
        
        if (cobj instanceof CComplexObject) {
            validateComplex((CComplexObject) cobj, value, path, errors,
                    archetype);
        } else if (cobj instanceof CDomainType) {
            validateDomain(archetype, (CDomainType) cobj, value, path,
                    errors);
        } else if (cobj instanceof CPrimitiveObject) {
            validatePrimitive(archetype, (CPrimitiveObject) cobj, value,
                    path, errors);
        } else if (instanciaArchetypeSlot(cobj)) {
            this.validateArchetypeSlot((ArchetypeSlot) cobj, value, path,
                    errors);
        } else if (cobj instanceof ArchetypeInternalRef) {
            this.validateArchetypeInternalRef(archetype,
                    (ArchetypeInternalRef) cobj, value, path, errors);
        } else {
            log.error("Unknown CObject type..");
        }
        
    }

    /**
     * Confronta um dado com uma restrição {@link CDomainType}
     * @param archetype
     * @param cdomain
     * @param value
     * @param path
     * @param errors
     */
    void validateDomain(Archetype archetype, CDomainType cdomain, Object value,
            String path, List<ValidationError> errors) {

        log.debug("validate CDomaingType..");
        if (!cdomain.validValue(value)) {
            log.debug("error found at " + cdomain.path());
            errors.add(new ValidationError(archetype, path, cdomain.path(),
                    ErrorType.DOMAIN_TYPE_VALUE_ERROR, getErrorDescription(
                    archetype, cdomain, null))); // DUMMY ERROR TYPE
        }
    }

    /**
     * Confronta um dado com uma restrição {@link CPrimitiveObject}
     * @param archetype
     * @param cpo
     * @param value
     * @param path
     * @param errors
     */
    void validatePrimitive(Archetype archetype, CPrimitiveObject cpo,
            Object value, String path, List<ValidationError> errors) {
        log.debug("validate CPrimitiveObject..");
        Object primitiveValue = value;

        Class klass = KnownTypes.getAllTypes().get(cpo.getItem().getType().
                toUpperCase(KnownTypes.getLocale()));
        if (primitiveValue instanceof String) {
            Constructor constructor = getConstructorWithStringArgument(klass);
            primitiveValue = build(constructor, primitiveValue);
        }

        if (!cpo.getItem().validValue(primitiveValue)) {
            errors.add(new ValidationError(archetype, path, cpo.path(),
                    ErrorType.PRIMITIVE_TYPE_VALUE_ERROR, getErrorDescription(
                    archetype, cpo, null))); // DUMMY ERROR TYPE
        }
    }

    /**
     * Confronta um dado com uma restrição {@link ArchetypeSlot}
     * @param slot
     * @param value
     * @param path
     * @param errors
     * @throws GenericValidationException
     */
    void validateArchetypeSlot(ArchetypeSlot slot, Object value, String path,
            List<ValidationError> errors) throws GenericValidationException {

        log.debug("validate ArchetypeSlot..");
        Locatable lo = (Locatable) value;
        String archetypeId = lo.getArchetypeNodeId();
        Archetype archetype = ArchetypeRepositoryFactory.getInstance().getArchetype(archetypeId);

        List<ValidationError> errorsSlot = new ArrayList<ValidationError>();

        if (!validateRootSlot(slot, lo)) {
            errorsSlot.add(new ValidationError(archetype, path, slot.path(),
                    ErrorType.OCCURRENCES_NOT_DESCRIBED, getErrorDescription(
                    archetype, slot, null)));
        }
        errorsSlot = this.validate(lo, archetype);

        errors.addAll(errorsSlot);

    }

    /**
     * Confronta um dado com uma restrição {@link ArchetypeInternalRef}
     * @param archetype
     * @param internalRef
     * @param value
     * @param path
     * @param errors
     * @throws GenericValidationException
     */
    void validateArchetypeInternalRef(Archetype archetype,
            ArchetypeInternalRef internalRef, Object value, String path,
            List<ValidationError> errors) throws GenericValidationException {
        log.debug("validate ArchetypeInternalRef..");
        ArchetypeConstraint constraint = archetype.node(internalRef.getTargetPath());
        if (constraint instanceof CSingleAttribute) {
            CSingleAttribute singleAttribute = (CSingleAttribute) constraint;
            validateSingleAttribute(singleAttribute, value, path, errors,
                    archetype);
        } else if (constraint instanceof CMultipleAttribute) {
            throw new GenericValidationException(archetype.getArchetypeId(), 
                    path, "CmultipleAttribute to CSingleAttribute impossible");
        } else if (constraint instanceof CObject) {
            throw new GenericValidationException(archetype.getArchetypeId(), 
                    path, "CSingleAttribute to CObject impossible");
        }
    }

    /**
     * Valida um objeto que seja raíz de um arquétipo
     * @param slot
     * @param locatable
     * @return
     */
    private boolean validateRootSlot(ArchetypeSlot slot, Locatable locatable) {

        String archetypeName = locatable.getArchetypeNodeId();
        ArchetypeID archetypeID = null;
        try {
            archetypeID = new ArchetypeID(archetypeName);
        } catch (Exception ex) {
            return false;
        }
        String conceptName = archetypeID.getValue().substring(
                archetypeID.qualifiedRmEntity().length() + 1);

        if (slot.getExcludes() != null) {
            for (Assertion assertion : slot.getExcludes()) {
                ExpressionBinaryOperator operator =
                        (ExpressionBinaryOperator) assertion.getExpression();
                ExpressionLeaf rightLeaf =
                        (ExpressionLeaf) operator.getRightOperand();
                CString cString = (CString) rightLeaf.getItem();
                if (cString.validValue(conceptName)
                        || cString.validValue(archetypeName)) {
                    return false;
                }
            }
        }
        if (slot.getIncludes() != null) {
            for (Assertion assertion : slot.getIncludes()) {
                ExpressionBinaryOperator operator =
                        (ExpressionBinaryOperator) assertion.getExpression();
                ExpressionLeaf rightLeaf =
                        (ExpressionLeaf) operator.getRightOperand();
                CString cString = (CString) rightLeaf.getItem();
                if (cString.validValue(conceptName)
                        || cString.validValue(archetypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retora o objeto que representa o valor de um {@link CAttribute}
     * @param object
     * @param cattr
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object fetchAttribute(Object object, CAttribute cattr)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException{
        String[] names = cattr.getRmAttributeName().split("_");
        StringBuilder attrName = new StringBuilder();
        for (String name : names) {
            attrName.append(upperFirstLetter(name));
        }

        String getter = "get" + attrName.toString();
        return executeGetter(object, getter);
    }

    /**
     * Retorna a {@link String} capitular, ou seja, o primeiro caracter em
     * maiúsculo
     * @param value
     * @return
     */
    private String upperFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase(KnownTypes.getLocale()) +
                value.substring(1);
    }

    /**
     * Busca determinado termo em um arquétipo
     * @param code
     * @param archetype
     * @return
     */
    String getTermText(String code, Archetype archetype) {
        ArchetypeOntology ont = archetype.getOntology();
        String lang = archetype.getOriginalLanguage().getCodeString();
        ArchetypeTerm term = ont.termDefinition(lang, code);
        if (term != null) {
            return term.getText();
        }
        return null;

    }

    private static Logger log = Logger.getLogger(DataValidator.class);

    /**
     * Retorna a descrição do erro
     * @param archetype
     * @param constraint
     * @param language
     * @return
     */
    private String getErrorDescription(Archetype archetype,
            ArchetypeConstraint constraint, String language) {
        if (archetype == null) {
            throw new IllegalArgumentException("Archetype is null");
        } else if (constraint == null) {
            throw new IllegalArgumentException("Constraint is null");
        }

        String nodeId = null;
        if (constraint instanceof CObject) {
            nodeId = ((CObject) constraint).getNodeId();
        } else if (constraint instanceof CSingleAttribute) {
            nodeId = ((CSingleAttribute)
                    constraint).getChildren().get(0).getNodeId();
        }

        String description = null;
        if (nodeId != null && !nodeId.isEmpty()) {
            description = extractTerm(archetype, language, nodeId);
        }
        
        return description;
    }
}

/*
 * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the 'License'); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is DataValidatorImpl.java
 * 
 * The Initial Developer of the Original Code is Rong Chen. Portions created by
 * the Initial Developer are Copyright (C) 2008 the Initial Developer. All
 * Rights Reserved.
 * 
 * Contributor(s):
 * 
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * ***** END LICENSE BLOCK *****
 */
