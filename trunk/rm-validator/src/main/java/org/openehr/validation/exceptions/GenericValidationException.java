
package org.openehr.validation.exceptions;

import org.openehr.am.archetype.Archetype;

/**
 *
 * @author Danillo Guimaraes
 */
public class GenericValidationException extends Exception {

    public GenericValidationException(String mens) {
        super(mens);
    }

    public GenericValidationException(String archetypeId, String path, String cause) {
        this(cause + " em " + path + " de " + archetypeId);
    }

    public GenericValidationException(Archetype archetype, String path, String cause) {
        this(cause + " em " + path + " de " + archetype.getArchetypeId().getValue());
    }


}
