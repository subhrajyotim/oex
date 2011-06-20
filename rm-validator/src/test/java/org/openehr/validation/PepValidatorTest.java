package org.openehr.validation;

import java.util.Collection;
import java.util.Iterator;
import static org.junit.Assert.*;
import br.ufg.inf.fs.pep.archetypes.ArchetypeRepository;
import br.ufg.inf.fs.pep.archetypes.PepArchetypeRepository;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.datastructure.itemstructure.ItemTree;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.demographic.Party;
import org.openehr.rm.demographic.Person;

/**
 *
 * @author Danillo Guimar�es
 */
public class PepValidatorTest extends PepBaseTest {

    private ArchetypeRepository repository = null;
    DataValidator validator = null;

    @Before
    public void setUp() {
        this.repository = new PepArchetypeRepository();
        this.validator = new DataValidatorImpl();
    }

    @Test
    public void testDvDateTime() throws Exception {
        DvDateTime dateTime = new DvDateTime();
        Element element = new Element("at0000", "Per�odo da �ltima menstrua��o normal", dateTime);
        String archetypeId = "openEHR-EHR-ELEMENT.last_normal_menstrual_period.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;

        errors = this.validator.validate(element, archetype);
        assertTrue("The list must not contain error", errors.isEmpty());
    }

    @Test
    public void testDvText() throws Exception {
        DvText text = new DvText("Universidade Federal de Goi�s");
        Element element = new Element("at0001", "Arqu�tipo de teste DVTEXT", text);
        String archetypeId = "openEHR-EHR-ELEMENT.test_dvtext.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;
        errors = this.validator.validate(element, archetype);
        assertTrue("The list must not contain error", errors.isEmpty());
    }

    @Test
    public void testPerson() throws Exception {
        Person person = this.getPerson();
        String archetypeId = "openEHR-DEMOGRAPHIC-PERSON.person.v1";
        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;
        errors = this.validator.validate(person, archetype);

        for (ValidationError validationError : errors) {
            System.out.println(validationError.getErrorType()
                    + " \nRuntime path : " + validationError.getRuntimePath()
                    + "\nArchetype path : " + validationError.getArchetypePath());
            System.out.println("");
        }
        assertTrue("The list must be null", errors.isEmpty());
    }

    @Ignore
    @Test
    public void testPersonWithoutItems() throws Exception {
        Person person = this.getPerson();
        ((ItemTree) person.getDetails()).setItems(null);
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.ATTRIBUTE_MISSING, person));
    }


    /**
     * Respons�vel por verificar se o validador est� realmente pegando o erro.
     * Para isso � preciso de um Person  com algum inconsist�ncia em rela��o �s
     * restri��es que o arqu�tipo impoem e o ErrorType equivalente � inconsist�ncia.
     * @param errortype Tipo de erro que dever� ser compativel com inconsist�ncia
     * injetada no person.
     * @param person Objeto com alguma inconsist�ncia para ser testado.
     * @throws Exception
     */
    private boolean erroEsperadoFoiEncontrado(ErrorType errortype, Person person) throws Exception {
        boolean erroEsperadoFoiCapturado = false;
        String archetypeId = "openEHR-DEMOGRAPHIC-PERSON.person.v1";
        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;
        errors = this.validator.validate(person, archetype);

        for (ValidationError validationError : errors) {
            if (validationError.getErrorType() == errortype) {
                erroEsperadoFoiCapturado = true;
            }
        }
        return erroEsperadoFoiCapturado;
    }
}




