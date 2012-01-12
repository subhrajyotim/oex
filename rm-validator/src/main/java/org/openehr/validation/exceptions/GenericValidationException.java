
package org.openehr.validation.exceptions;

import org.openehr.am.archetype.Archetype;

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

    public GenericValidationException(Archetype archetype, String path, String cause) {
        this(cause + " em " + path + " de " + archetype.getArchetypeId().getValue());
    }

    public GenericValidationException(Archetype archetype, String path, Exception ex) {
        this(ex.getMessage() + " em " + path + " de " + archetype.getArchetypeId().getValue(), ex.getCause());
    }


}
