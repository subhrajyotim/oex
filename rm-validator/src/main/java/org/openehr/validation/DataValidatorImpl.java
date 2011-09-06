/*
 * component:   "openEHR Reference Java Implementation"
 * description: "Class DataValidatorImpl"
 * keywords:    "validator"
 *
 * author:      "Rong Chen <rong.acode@gmail.com>"
 * support:     "openEHR Java Project <ref_impl_java@openehr.org>"
 * copyright:   "Copyright (c) 2008 Cambio Healthcare Systems, Sweden"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL$"
 * revision:    "$LastChangedRevision$"
 * last_change: "$LastChangedDate$"
 */
package org.openehr.validation;

import br.ufg.inf.fs.pep.archetypes.PepArchetypeRepository;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;

import org.apache.log4j.Logger;
import org.openehr.am.archetype.*;
import org.openehr.am.archetype.assertion.Assertion;
import org.openehr.am.archetype.assertion.ExpressionBinaryOperator;
import org.openehr.am.archetype.assertion.ExpressionLeaf;
import org.openehr.am.archetype.constraintmodel.*;
import org.openehr.am.archetype.constraintmodel.primitive.CDate;
import org.openehr.am.archetype.constraintmodel.primitive.CDateTime;
import org.openehr.am.archetype.constraintmodel.primitive.CDuration;
import org.openehr.am.archetype.constraintmodel.primitive.CInteger;
import org.openehr.am.archetype.constraintmodel.primitive.CPrimitive;
import org.openehr.am.archetype.constraintmodel.primitive.CReal;
import org.openehr.am.archetype.constraintmodel.primitive.CString;
import org.openehr.am.archetype.constraintmodel.primitive.CTime;
import org.openehr.am.archetype.ontology.ArchetypeOntology;
import org.openehr.am.archetype.ontology.ArchetypeTerm;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.support.basic.Interval;
import org.openehr.rm.support.identification.ArchetypeID;

/**
 * Implementation of an archetype-based data validator
 *
 * @author rong.chen
 */
public class DataValidatorImpl implements DataValidator {

    private static final Map<String, Class> types = KnownTypes.getAllTypes();

    /**
     * Validates the data using given archetype
     *
     * @param data
     * @return errors empty if data pass validation without error
     */
    public List<ValidationError> validate(Locatable data) throws Exception {
        String archetypeId = data.getArchetypeNodeId();
        Archetype archetype = new PepArchetypeRepository().getArchetype(archetypeId);
        if (archetype == null) {
            throw new Exception("Archetype " + archetypeId + " not found.");
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
    public List<ValidationError> validate(Locatable data, Archetype archetype)
            throws Exception {

        log.debug("validate archetype: " + archetype.getArchetypeId());

        List<ValidationError> errors = new ArrayList<ValidationError>();
        validateComplex(archetype.getDefinition(), data,
                Locatable.PATH_SEPARATOR, errors, archetype);
        return errors;
    }

    void validateComplex(CComplexObject ccobj, Object object, String path,
            List<ValidationError> errors, Archetype archetype) throws Exception {

        log.debug("validate CComplexObject of type: " + ccobj.getRmTypeName()
                + " at path: " + path);

        ValidationError error = null;
        String newPath = null;

        // loop through the attributes
        for (CAttribute cattr : ccobj.getAttributes()) {

            Object attribute = fetchAttribute(object, cattr);

            log.debug("attribute " + cattr.getRmAttributeName()
                    + " isRequired: " + cattr.isRequired()
                    + ", attribute == null ? " + (attribute == null));

            newPath = path;
            if (!path.equals(Locatable.PATH_SEPARATOR)) {
                newPath += Locatable.PATH_SEPARATOR;
            }
            newPath += cattr.getRmAttributeName();

            if (cattr.isRequired() && attribute == null) {

                log.debug("ERROR --> attribute missing at " + path);

                error = new ValidationError(archetype, path, cattr.path(),
                        ErrorType.ATTRIBUTE_MISSING);
                errors.add(error);

            } else if (!cattr.isAllowed() && attribute != null) {

                error = new ValidationError(archetype, path, cattr.path(),
                        ErrorType.ATTRIBUTE_NOT_ALLOWED);
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

    // TODO how to handle alternatives of single attribute
    void validateSingleAttribute(CSingleAttribute cattr, Object attribute,
            String path, List<ValidationError> errors, Archetype archetype) throws Exception {

        log.debug("validateSingleAttribute..");

        if (cattr.alternatives().size() > 1) {
            List<ValidationError> newErrors = null;
            for (CObject cobj : cattr.alternatives()) {
                newErrors = new ArrayList<ValidationError>();
                validateObject(cobj, attribute, path, newErrors, archetype);
                if (newErrors.size() == 0) {
                    return;
                }
            }
        } else if (cattr.alternatives().size() == 1) {
            CObject cobj = cattr.alternatives().get(0);
            validateObject(cobj, attribute, path, errors, archetype);
        }

    }

    void validateMultipleAttribute(CMultipleAttribute cattr, Object attribute,
            String path, List<ValidationError> errors, Archetype archetype)
            throws Exception {

        log.debug("validateMultipleAttribute..");

        Cardinality cardinality = cattr.getCardinality();
        List<CObject> children = cattr.getChildren();
        Collection<Object> values = (Collection<Object>) attribute;

        Interval<Integer> interval = cardinality.getInterval();

        log.debug("cardinality.interval: " + interval);

        ValidationError error = null;

        if (children == null) {
            return;
        }

        if (interval.getLower() != null
                && interval.getLower() > values.size()) {

            error = new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ITEMS_TOO_FEW);
            errors.add(error);
            return;

        } else if (interval.getUpper() != null
                && interval.getUpper() < values.size()) {

            error = new ValidationError(archetype, path, cattr.path(),
                    ErrorType.ITEMS_TOO_MANY);
            errors.add(error);
            return;
        }

        // Cardinality must validate if the container is ordered.
        if (cardinality.isList() && values instanceof Set) {
            error = new ValidationError(archetype, path, cattr.path(), ErrorType.ITEMS_NOT_ORDERED);
        } else if (cardinality.isSet() && values instanceof List) {
            Set<Object> set = new HashSet<Object>(values);
            if (set.size() != values.size()) {
                error = new ValidationError(archetype, path, cattr.path(), ErrorType.ITEMS_NOT_UNIQUE);
            }
        } else if (cardinality.isBag() && values instanceof Set) {
            error = new ValidationError(archetype, path, cattr.path(), ErrorType.ITEMS_NOT_NON_UNIQUE);
        }

        log.debug("validating total cobj: " + children.size()
                + ", values: " + values.size());

        List<Object> objects = null;
        String newPath = null;
        int contador = 0;
        for (CObject cobj : children) {

            log.debug("validating sub-cobj at: " + cobj.path());

            objects = findMatchingNodes(values, cobj);
            contador += objects.size();
            // TODO occurrences should be checked here
            // the difficult is to locate possible match
            // for objects constrained by occurrences

            Interval<Integer> occurrences = cobj.getOccurrences();

            if (occurrences != null) {
                if (occurrences.getLower() != null
                        && occurrences.getLower() > objects.size()) {

                    error = new ValidationError(archetype, path, cobj.path(),
                            ErrorType.OCCURRENCES_TOO_FEW);
                    errors.add(error);
                    return;

                } else if (occurrences.getUpper() != null
                        && occurrences.getUpper() < objects.size()) {

                    error = new ValidationError(archetype, path, cobj.path(),
                            ErrorType.OCCURRENCES_TOO_MANY);
                    errors.add(error);
                    return;
                }
            }

            for (Object obj : objects) {
                newPath = path + "[" + cobj.getNodeId() + "]";
                validateObject(cobj, obj, newPath, errors, archetype);
            }
        }
        if (contador != values.size()) {

            errors.add(new ValidationError(archetype, path, cattr.path(), ErrorType.OCCURRENCES_NOT_DESCRIBED));
        }
    }

    /*
     * Finds objects by matching nodeId
     */
    private List<CObject> findObjectsByNodeId(List<CObject> objects,
            String nodeId) {

        List<CObject> list = new ArrayList<CObject>();
        for (CObject obj : objects) {
            if (nodeId.matches(obj.getNodeID())) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * Finds matching value node using AT code of CObject
     *
     * @param values
     * @param code
     * @return empty list if not found
     */
    List<Object> findMatchingNodes(Collection<Object> values, CObject cObj) {
        List<Object> objects = new ArrayList<Object>();
        Locatable lo = null;

        for (Object value : values) {
            if (value instanceof Locatable) {
                lo = (Locatable) value;

                if (cObj instanceof ArchetypeSlot) {
                    log.debug("ArchetypeSlot : " + lo.getArchetypeNodeId());
                    ArchetypeSlot slot = (ArchetypeSlot)cObj;
                    if(validateRootSlot(slot, lo)){
                        objects.add(lo);
                    }
                } else if (cObj instanceof ArchetypeInternalRef) {
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

    void validateObject(CObject cobj, Object value, String path,
            List<ValidationError> errors, Archetype archetype) throws Exception {

        log.debug("validate CObject..");

        if (!cobj.isAnyAllowed()) {

            Class klass = value.getClass();
            String restrictionType = cobj.getRmTypeName().replace("_", "").toUpperCase();
            Class restClass = types.get(restrictionType);

            if (!restClass.isAssignableFrom(klass) && (!(cobj instanceof CPrimitiveObject))) {
                //verificar se o tipo eh primitivo e se o dado eh String
                errors.add(new ValidationError(archetype, path, cobj.path(), ErrorType.RM_TYPE_INVALID));
            } else if (cobj instanceof CComplexObject) {

                validateComplex((CComplexObject) cobj, value, path, errors, archetype);

            } else if (cobj instanceof CDomainType) {

                validateDomain(archetype, (CDomainType) cobj, value, path, errors);

            } else if (cobj instanceof CPrimitiveObject) {

                validatePrimitive(archetype, (CPrimitiveObject) cobj, value, path, errors);

            } else if (cobj instanceof ArchetypeSlot) {

                this.validateArchetypeSlot((ArchetypeSlot) cobj, value, path, errors);

            } else if (cobj instanceof ArchetypeInternalRef) {

                this.validateArchetypeInternalRef((ArchetypeInternalRef) cobj, value, path, errors);

            } else {
                log.error("Unknown CObject type..");
            }
        }

    }

    void validateDomain(Archetype archetype, CDomainType cdomain, Object value, String path,
            List<ValidationError> errors) {

        log.debug("validate CDomaingType..");
        if (!cdomain.validValue(value)) {

            log.debug("error found at " + cdomain.path());
            java.lang.String a = "";
            errors.add(new ValidationError(archetype, path, cdomain.path(),
                    ErrorType.DOMAIN_TYPE_VALUE_ERROR)); // DUMMY ERROR TYPE
        }
    }

    void validatePrimitive(Archetype archetype, CPrimitiveObject cpo, Object value, String path,
            List<ValidationError> errors) {

        log.debug("validate CPrimitiveObject..");

        Class klass = types.get(cpo.getItem().getType().toUpperCase());
        if (value instanceof String) {

            Constructor constructor = null;
            for (Constructor constr : klass.getConstructors()) {
                if (constr.getParameterTypes().length == 1 && constr.getParameterTypes()[0].equals(String.class)) {
                    constructor = constr;
                    break;
                }
            }
            if (constructor != null) {
                try {
                    Object[] params = new Object[1];
                    params[0] = value;
                    value = constructor.newInstance(params);
                } catch (InstantiationException ex) {
                    java.util.logging.Logger.getLogger(DataValidatorImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(DataValidatorImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    java.util.logging.Logger.getLogger(DataValidatorImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    java.util.logging.Logger.getLogger(DataValidatorImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (!cpo.getItem().validValue(value)) {
            errors.add(new ValidationError(archetype, path, cpo.path(),
                    ErrorType.PRIMITIVE_TYPE_VALUE_ERROR)); // DUMMY ERROR TYPE
        }
    }

    void validateArchetypeSlot(ArchetypeSlot slot, Object value, String path,
            List<ValidationError> errors) throws Exception {

        log.debug("validate ArchetypeSlot..");
        Locatable lo = (Locatable) value;
        String archetypeId = lo.getArchetypeNodeId();
        Archetype archetype = new PepArchetypeRepository().getArchetype(archetypeId);

        List<ValidationError> errorsSlot = new ArrayList<ValidationError>();

        if (!validateRootSlot(slot, lo)){
            errorsSlot.add( new ValidationError(archetype, path, slot.path(), ErrorType.OCCURRENCES_NOT_DESCRIBED));
        }
        errorsSlot = this.validate(lo, archetype);

        errors.addAll(errorsSlot);


    }

    void validateArchetypeInternalRef(ArchetypeInternalRef internalRef, Object value, String path,
            List<ValidationError> errors) {
        log.debug("validate ArchetypeInternalRef..");
        List<ValidationError> errorsInternalRef = null;
    }

    private boolean validateRootSlot(ArchetypeSlot slot, Locatable locatable){

        String archetypeName = locatable.getArchetypeNodeId();
        ArchetypeID archetypeID = null;
        try{
            archetypeID = new ArchetypeID(archetypeName);
        } catch (Exception ex){
            return false;
        }
        String conceptName = archetypeID.getValue().substring(archetypeID.qualifiedRmEntity().length()+1);

        if(slot.getExcludes()!=null){
            for (Assertion assertion : slot.getExcludes()) {
                ExpressionBinaryOperator operator = (ExpressionBinaryOperator) assertion.getExpression();
                ExpressionLeaf rightLeaf = (ExpressionLeaf) operator.getRightOperand();
                CString cString = (CString) rightLeaf.getItem();
                if (cString.validValue(conceptName) || cString.validValue(archetypeName)){
                    return false;
                }
            }
        }
        if(slot.getIncludes()!=null){
            for (Assertion assertion : slot.getIncludes()) {
                ExpressionBinaryOperator operator = (ExpressionBinaryOperator) assertion.getExpression();
                ExpressionLeaf rightLeaf = (ExpressionLeaf) operator.getRightOperand();
                CString cString = (CString) rightLeaf.getItem();
                if (cString.validValue(conceptName) || cString.validValue(archetypeName)){
                    return true;
                }
            }
        }
        return false;
    }

    Object fetchAttribute(Object object, CAttribute cattr) throws Exception {
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
}

/*
 *  ***** BEGIN LICENSE BLOCK *****
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  1.1 (the 'License'); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an 'AS IS' basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *  for the specific language governing rights and limitations under the
 *  License.
 *
 *  The Original Code is DataValidatorImpl.java
 *
 *  The Initial Developer of the Original Code is Rong Chen.
 *  Portions created by the Initial Developer are Copyright (C) 2008
 *  the Initial Developer. All Rights Reserved.
 *
 *  Contributor(s):
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */
