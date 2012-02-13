
package org.openehr.validation.exceptions;

import org.openehr.rm.support.identification.ArchetypeID;

/**
 * TODO documentar
 */
public class GenericValidationException extends Exception {

    public GenericValidationException(String mens) {
        super(mens);
    }

    public GenericValidationException(String mens, Throwable cause) {
        super(mens, cause);
    }

    public GenericValidationException(ArchetypeID archetypeID, String path, String cause) {
        this(cause + " em " + path + " de " + archetypeID.getValue());
    }

    public GenericValidationException(ArchetypeID archetypeID, String path, Exception ex) {
        this(ex.getMessage() + " em " + path + " de " + archetypeID.getValue(), ex.getCause());
    }


}
