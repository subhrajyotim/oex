package org.openehr.validation.exceptions;

import org.junit.Test;
import org.openehr.rm.support.identification.ArchetypeID;
import static org.junit.Assert.*;

public class GenericValidationExceptionTest {

    @Test
    public void constructorsTest(){
        ArchetypeID archetypeID = new ArchetypeID("openEHR-DEMOGRAPHIC-PERSON.person.v1");
        String path = "attr/attr";
        Exception ex = new Exception("lançamento de excessao");
        assertNotNull("Excessão é nula", new GenericValidationException("mens"));
        assertNotNull(new GenericValidationException("mens", null));
        assertNotNull(new GenericValidationException(archetypeID, path, ex));
        assertNotNull(new GenericValidationException(archetypeID, path, "Causa desconhecida"));
    }
}
