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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
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
 *
 * @author rong.chen
 */
public class DataValidatorImpl implements DataValidator {

    private static final Map<String, Class> TYPES = KnownTypes.getAllTypes();

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

    void validateComplex(CComplexObject ccobj, Object object, String path,
            List<ValidationError> errors, Archetype archetype)
            throws GenericValidationException {

        log.debug("validate CComplexObject of type: " + ccobj.getRmTypeName()
                + " at path: " + path);

        ValidationError error = null;
        String newPath = null;

        // loop through the attributes
        for (CAttribute cattr : ccobj.getAttributes()) {

            Object attribute = null;
            try {
                attribute = fetchAttribute(object, cattr);
            } catch (Exception ex) {
                throw new GenericValidationException(archetype, path, ex);
            }

            log.debug("attribute " + cattr.getRmAttributeName()
                    + " isRequired: " + cattr.isRequired()
                    + ", attribute == null ? " + (attribute == null));
            newPath = updatePath(path);
            newPath += cattr.getRmAttributeName();
            String description = getErrorDescription(archetype, ccobj, null);
            if (cattr.isRequired() && attribute == null) {

                log.debug("ERROR --> attribute missing at " + path);

                error = new ValidationError(archetype, path, cattr.path(),
                        ErrorType.ATTRIBUTE_MISSING, description);
                errors.add(error);

            } else if (!cattr.isAllowed() && attribute != null) {

                error = new ValidationError(archetype, path, cattr.path(),
                        ErrorType.ATTRIBUTE_NOT_ALLOWED, description);
                errors.add(error);

            } else if (attribute != null) {
                if (cattr instanceof CSingleAttribute) {
                    validateSingleAttribute((CSingleAttribute) cattr,
                            attribute, newPath, errors, archetype);
                } else {
                    validateMultipleAttribute((CMultipleAttribute) cattr,
                            attribute, newPath, errors, archetype);
                }

            }
        }
    }

    private String extractTerm(Archetype archetype, String language, String nodeId) {
        ArchetypeOntology ontology = archetype.getOntology();
        String lang = language == null ? "pt-br" : language;
        String description = null;
        List<OntologyDefinitions> ontologyDefinitions = ontology.getTermDefinitionsList();
        List<ArchetypeTerm> archetypeTerms = null;
        for (OntologyDefinitions ontologyDefinition : ontologyDefinitions) {
            if (ontologyDefinition.getLanguage().equals(lang)) {
                archetypeTerms = ontologyDefinition.getDefinitions();
                break;
            }
        }
        if (archetypeTerms != null) {
            for (ArchetypeTerm archetypeTerm : archetypeTerms) {
                String term = archetypeTerm.getCode();
                if (nodeId.equals(term)) {
                    description = archetypeTerm.getDescription();
                    break;
                }
            }
        }
        return description;
    }

    private String updatePath(String path) {
        if (!path.equals(Locatable.PATH_SEPARATOR)) {
            return path + Locatable.PATH_SEPARATOR;
        }
        return path;
    }

    void validateSingleAttribute(CSingleAttribute cattr, Object attribute,
            String path, List<ValidationError> errors, Archetype archetype)
            throws GenericValidationException {

        log.debug("validateSingleAttribute..");

        if (cattr.alternatives().size() > 1) {
            boolean valid = false;
            List<ValidationError> newErrors = null;
            for (CObject cobj : cattr.alternatives()) {
                newErrors = new ArrayList<ValidationError>();
                validateObject(cobj, attribute, path, newErrors, archetype);
                if (newErrors.isEmpty()) {
                    valid = true;
                    return;
                }
            }
            if (!valid) {
                ValidationError error = new ValidationError(archetype, path,
                        path, ErrorType.ALTERNATIVES_NOT_SATISFIED,
                        getErrorDescription(archetype, cattr, null));
                errors.add(error);
            }
        } else if (cattr.alternatives().size() == 1) {
            CObject cobj = cattr.alternatives().get(0);
            validateObject(cobj, attribute, path, errors, archetype);
        }

    }

    void validateMultipleAttribute(CMultipleAttribute cattr, Object attribute,
            String path, List<ValidationError> errors, Archetype archetype)
            throws GenericValidationException {

        log.debug("validateMultipleAttribute..");

        Cardinality cardinality = cattr.getCardinality();
        List<CObject> children = cattr.getChildren();
        if (children == null) {
            return;
        }
        Collection<Object> values = (Collection<Object>) attribute;

        ValidationError errorCardinality = validateCardinality(cardinality, values, archetype, path, cattr);
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

            ValidationError occurrenceError = validateOccurrence(cobj, objects, archetype, path);
            if(occurrenceError!=null){
                errors.add(occurrenceError);
                return;
            }

            for (Object obj : objects) {
                newPath = path + "[" + cobj.getNodeId() + "]";
                validateObject(cobj, obj, newPath, errors, archetype);
            }
        }
        if (contador != values.size()) {
            errors.add(new ValidationError(archetype, path, cattr.path(),
                    ErrorType.OCCURRENCES_NOT_DESCRIBED, null));
        }
    }

    private ValidationError validateOccurrence(CObject cobj, List<Object> objects, Archetype archetype, String path) {
        Interval<Integer> occurrences = cobj.getOccurrences();
        if (occurrences != null) {
            if (occurrences.getLower() != null && occurrences.getLower() > objects.size()) {
                return new ValidationError(archetype, path, cobj.path(), ErrorType.OCCURRENCES_TOO_FEW, null);
            } else if (occurrences.getUpper() != null && occurrences.getUpper() < objects.size()) {
                return new ValidationError(archetype, path, cobj.path(), ErrorType.OCCURRENCES_TOO_MANY, null);
            }
        }
        return null;
    }

    private ValidationError validateCardinality(
            Cardinality cardinality, Collection<Object> values, Archetype archetype,
            String path, CAttribute cattr){
        Interval<Integer> interval = cardinality.getInterval();

        log.debug("cardinality.interval: " + interval);

        if (interval.getLower() != null && interval.getLower() > values.size()) {

            return new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ITEMS_TOO_FEW, null);

        } else if (interval.getUpper() != null
                && interval.getUpper() < values.size()) {

            return new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ITEMS_TOO_MANY, null);
        }
        return null;
        
        // The archetype must respect the constraints on reference model
        /**
        if (cardinality.isList() && values instanceof Set) {
            ValidationError error = new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ITEMS_NOT_ORDERED, null);
            errors.add(error);
        } else if (cardinality.isSet() && values instanceof List) {
            Set<Object> set = new HashSet<Object>(values);
            if (set.size() != values.size()) {
                ValidationError error = new ValidationError(archetype, path, cattr.path(),
                        ErrorType.ITEMS_NOT_UNIQUE, null);
                errors.add(error);
            }
        } else if (cardinality.isBag() && values instanceof Set) {
            ValidationError error = new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ITEMS_NOT_NON_UNIQUE, null);
            errors.add(error);
        }
         *
         */
    }

    /**
	 * Finds matching value node using AT code of CObject
	 * 
	 * @param values
	 * @param code
	 * @return empty list if not found
	 */
    List<Object> findMatchingNodes(Collection<Object> values, CObject cObj) {
        String type = cObj.getRmTypeName().toUpperCase().replace("_", "");
        Class klasse = TYPES.get(type);

        if(Locatable.class.isAssignableFrom(klasse)){
            return findMatchingLocatables(values, cObj);
        }
        
        return findMatchingDataValue(values, cObj, klasse);
    }

    private List<Object> findMatchingLocatables(Collection<Object> values, CObject cObj) {
        List<Object> objects = new ArrayList<Object>();
        Locatable lo = null;
        for (Object value : values) {
            if (value instanceof Locatable) {
                lo = (Locatable) value;
                if (cObj instanceof ArchetypeSlot) {
                    log.debug("ArchetypeSlot : " + lo.getArchetypeNodeId());
                    ArchetypeSlot slot = (ArchetypeSlot) cObj;
                    if (validateRootSlot(slot, lo)) {
                        objects.add(lo);
                    }
                } else {
                    if (cObj.getNodeId().equals(lo.getArchetypeNodeId())) {
                        log.debug("value found for code: " + cObj.getNodeId());
                        objects.add(lo);
                    }
                }
            } else {
                log.warn("trying to find matching value on un-pathable obj..");
            }
        }
        return objects;
    }

    private List<Object> findMatchingDataValue(Collection<Object> values, CObject cObj, Class klass) {
        List<Object> objects = new ArrayList<Object>();
        DataValue dv = null;
        for (Object value : values) {
            if (value instanceof DataValue) {
                dv = (DataValue) value;
                if(klass.isAssignableFrom(value.getClass())){
                    log.debug("value found for code: " + cObj.getNodeId());
                    objects.add(dv);
                }
            } else {
                log.warn("trying to find matching value on un-pathable obj..");
            }
        }
        return objects;
    }

    void validateObject(CObject cobj, Object value, String path,
            List<ValidationError> errors, Archetype archetype)
            throws GenericValidationException {

        log.debug("validate CObject..");
        Class klass = value.getClass();
        String restrictionType = cobj.getRmTypeName().replace("_", "").toUpperCase();
        restrictionType = restrictionType.split("<")[0];

        Class restClass = TYPES.get(restrictionType);
        if (!restClass.isAssignableFrom(klass)
                && (!(cobj instanceof CPrimitiveObject))) {
            // verificar se o tipo eh primitivo e se o dado eh String
            errors.add(new ValidationError(archetype, path, cobj.path(),
                    ErrorType.RM_TYPE_INVALID, getErrorDescription(archetype,
                    cobj, null)));
        } else if (!cobj.isAnyAllowed()) {
            if (cobj instanceof CComplexObject) {
                validateComplex((CComplexObject) cobj, value, path, errors,
                        archetype);
            } else if (cobj instanceof CDomainType) {
                validateDomain(archetype, (CDomainType) cobj, value, path,
                        errors);
            } else if (cobj instanceof CPrimitiveObject) {
                validatePrimitive(archetype, (CPrimitiveObject) cobj, value,
                        path, errors);
            } else if (cobj instanceof ArchetypeSlot) {
                this.validateArchetypeSlot((ArchetypeSlot) cobj, value, path,
                        errors);
            } else if (cobj instanceof ArchetypeInternalRef) {
                this.validateArchetypeInternalRef(archetype,
                        (ArchetypeInternalRef) cobj, value, path, errors);
            } else {
                log.error("Unknown CObject type..");
            }
        }
    }

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

    void validatePrimitive(Archetype archetype, CPrimitiveObject cpo,
            Object value, String path, List<ValidationError> errors) {
        log.debug("validate CPrimitiveObject..");
        Object primitiveValue = value;

        Class klass = TYPES.get(cpo.getItem().getType().toUpperCase());
        if (primitiveValue instanceof String) {
            Constructor constructor = null;
            for (Constructor constr : klass.getConstructors()) {
                if (constr.getParameterTypes().length == 1
                        && constr.getParameterTypes()[0].equals(String.class)) {
                    constructor = constr;
                    break;
                }
            }
            if (constructor != null) {
                try {
                    Object[] params = new Object[1];
                    params[0] = primitiveValue;
                    primitiveValue = constructor.newInstance(params);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(DataValidatorImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (!cpo.getItem().validValue(primitiveValue)) {
            errors.add(new ValidationError(archetype, path, cpo.path(),
                    ErrorType.PRIMITIVE_TYPE_VALUE_ERROR, getErrorDescription(
                    archetype, cpo, null))); // DUMMY ERROR TYPE
        }
    }

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
            throw new GenericValidationException(archetype, path,
                    "CmultipleAttribute to CSingleAttribute impossible");
        } else if (constraint instanceof CObject) {
            throw new GenericValidationException(archetype, path,
                    "CSingleAttribute to CObject impossible");
        }
    }

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
                ExpressionBinaryOperator operator = (ExpressionBinaryOperator) assertion.getExpression();
                ExpressionLeaf rightLeaf = (ExpressionLeaf) operator.getRightOperand();
                CString cString = (CString) rightLeaf.getItem();
                if (cString.validValue(conceptName)
                        || cString.validValue(archetypeName)) {
                    return false;
                }
            }
        }
        if (slot.getIncludes() != null) {
            for (Assertion assertion : slot.getIncludes()) {
                ExpressionBinaryOperator operator = (ExpressionBinaryOperator) assertion.getExpression();
                ExpressionLeaf rightLeaf = (ExpressionLeaf) operator.getRightOperand();
                CString cString = (CString) rightLeaf.getItem();
                if (cString.validValue(conceptName)
                        || cString.validValue(archetypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    Object fetchAttribute(Object object, CAttribute cattr)
            throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String[] names = cattr.getRmAttributeName().split("_");
        StringBuilder attrName = new StringBuilder();
        for (String name : names) {
            attrName.append(upperFirstLetter(name));
        }

        String getter = "get" + attrName.toString();
        Method method = null;
        method = object.getClass().getMethod(getter, null);
        return method.invoke(object, null);
    }

    String upperFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    /**
	 * Fetches name of the archetype term of given AT code
	 * 
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
            nodeId = ((CSingleAttribute) constraint).getChildren().get(0).getNodeId();
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
