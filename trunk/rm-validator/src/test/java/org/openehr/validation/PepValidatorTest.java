package org.openehr.validation;

import static org.junit.Assert.*;
import br.ufg.inf.fs.pep.archetypeRepository.ArchetypeRepository;
import br.ufg.inf.fs.pep.archetypeRepository.PepArchetypeRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.demographic.Person;

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

    @Ignore
    @Test
    public void testDvDateTime() throws Exception{
        DvDateTime dateTime = new DvDateTime();
        Element element = new Element("at0000", "Período da última menstruação normal", dateTime);
        String archetypeId = "openEHR-EHR-ELEMENT.last_normal_menstrual_period.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;
        
        errors = this.validator.validate(element, archetype);
        assertTrue("The list must be null", errors.isEmpty());
    }

//    @Ignore
    @Test
    public void testDvText() throws Exception{
        DvText text = new DvText("Universidade Federal de Goiás");
        Element element = new Element("at0001", "Arquétipo de teste DVTEXT", text);
        String archetypeId = "openEHR-EHR-ELEMENT.test_dvtext.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;
        errors = this.validator.validate(element, archetype);
        assertTrue("The list must be null", errors.isEmpty());
    }

    @Ignore
    @Test
    public void testPerson() throws Exception{
        Person person = this.getPerson();
        String archetypeId = "openEHR-DEMOGRAPHIC-PERSON.person.v1";
        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;
        errors = this.validator.validate(person, archetype);

        for (ValidationError validationError : errors) {
            System.out.println( validationError.getErrorType() );
        }

//        assertTrue("The list must be null", errors.isEmpty());
    }
    
}
