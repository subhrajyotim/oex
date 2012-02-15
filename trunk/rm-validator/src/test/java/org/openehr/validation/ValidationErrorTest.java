package org.openehr.validation;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openehr.rm.support.identification.ArchetypeID;

public class ValidationErrorTest {
    public static final String ARCHETYPE_ID_VALUE = 
            "openEHR-EHR-OBSERVATION.anamnese.v1";
    public static final String DESCRICAO_ERRO = "Erro teste";
    public static final String RUNTIME_PATH = "/attr/attr/runtime";
    public static final String ARCHETYPE_PATH = "/attr/attr/archetype";

    @Test
    public void testConstructors(){
        ValidationError ve = new ValidationError(ARCHETYPE_ID_VALUE, 
                RUNTIME_PATH, ARCHETYPE_PATH,
                ErrorType.UNKNOW_CODE, DESCRICAO_ERRO);
        assertNotNull("ValidationError is null", ve);
        assertEquals("Archetype is different from expected", ARCHETYPE_ID_VALUE,
                ve.getArchetype() );
        assertEquals("Runtime path is different from expected", RUNTIME_PATH,
                ve.getRuntimePath() );
        assertEquals("Archetype path is different from expected", ARCHETYPE_PATH,
                ve.getArchetypePath());
        assertEquals("Description is different from expected", DESCRICAO_ERRO,
                ve.getDescription());

        ve = new ValidationError(new ArchetypeID(ARCHETYPE_ID_VALUE),
                RUNTIME_PATH, ARCHETYPE_PATH,
                ErrorType.UNKNOW_CODE, DESCRICAO_ERRO);
        assertNotNull("ValidationError is null", ve);
        assertEquals("Archetype is different from expected", ARCHETYPE_ID_VALUE,
                ve.getArchetype() );
        assertEquals("Runtime path is different from expected", RUNTIME_PATH,
                ve.getRuntimePath() );
        assertEquals("Archetype path is different from expected", ARCHETYPE_PATH,
                ve.getArchetypePath());
        assertEquals("Description is different from expected", DESCRICAO_ERRO,
                ve.getDescription());
    }
}
