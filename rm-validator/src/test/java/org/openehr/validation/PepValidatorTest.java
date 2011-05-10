package org.openehr.validation;

import br.ufg.inf.fs.pep.archetypeRepository.ArchetypeRepository;
import br.ufg.inf.fs.pep.archetypeRepository.PepArchetypeRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.DvText;

/**
 *
 * @author Danillo Guimarães
 */
public class PepValidatorTest extends PepBaseTest {

    private ArchetypeRepository repository = null;
    DataValidator validator = null;

    @Before
    public void setUp(){
        this.repository = new PepArchetypeRepository();
        this.validator = new DataValidatorImpl();
    }

    @Test
    public void testDvDateTime() throws Exception{
        DvDateTime dateTime = new DvDateTime();
        Element element = new Element("at0000", "Período da última menstruação normal", dateTime);
        String archetypeId = "openEHR-EHR-ELEMENT.last_normal_menstrual_period.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;
        
        errors = this.validator.validate(element, archetype);
        System.out.println("testDvDateTime");
        for (ValidationError validationError : errors) {
            System.out.println( validationError.getErrorType() );
        }
        System.out.println("");
    }

    @Test
    public void testDvText() throws Exception{
        DvText text = new DvText("Universidade Federal de Goiás");
        Element element = new Element("at0001", "Arquétipo de teste DVTEXT", text);
        String archetypeId = "openEHR-EHR-ELEMENT.test_dvtext.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;

        errors = this.validator.validate(element, archetype);
        System.out.println("testDvText");
        for (ValidationError validationError : errors) {
            System.out.println( validationError.getErrorType());
        }
        System.out.println("");
    }

}
