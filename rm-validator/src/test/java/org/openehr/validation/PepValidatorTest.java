package org.openehr.validation;

import static org.junit.Assert.*;
import br.ufg.inf.fs.pep.archetypes.ArchetypeRepository;
import br.ufg.inf.fs.pep.archetypes.PepArchetypeRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.datastructure.itemstructure.ItemTree;
import org.openehr.rm.datastructure.itemstructure.representation.Cluster;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.demographic.Address;
import org.openehr.rm.demographic.Contact;
import org.openehr.rm.demographic.PartyIdentity;
import org.openehr.rm.demographic.Person;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.ObjectVersionID;

/**
 *
 * @author Danillo Guimarães
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
        Element element = new Element("at0000", "Período da última menstruação normal", dateTime);
        String archetypeId = "openEHR-EHR-ELEMENT.last_normal_menstrual_period.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;

        errors = this.validator.validate(element, archetype);
        assertTrue("The list must not contain error", errors.isEmpty());
    }

    @Test
    public void testDvText() throws Exception {
        DvText text = new DvText("Universidade Federal de Goiás");
        Element element = new Element("at0001", "Arquétipo de teste DVTEXT", text);
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

    @Test
    public void testPersonWithoutDetails() throws Exception {
        Person person = null;
        // PartyIdentity
        PartyIdentity partyIdentity = getPersonName(person);
        Set<PartyIdentity> identities = new HashSet<PartyIdentity>();
        identities.add(partyIdentity);
        Address address = getContact();
        List listAddresses = new ArrayList();
        listAddresses.add(address);
        Contact contact = new Contact(null, "at0003", new DvText("Contact"), null, null, null, person, null, listAddresses);
        Set<Contact> contacts = new HashSet();
        contacts.add(contact);
        ObjectVersionID objectVersionID = new ObjectVersionID("939cec48-d629-4a3f-89f1-28c573387680::"
                + "10aec661-5458-4ff6-8e63-c2265537196d::1");
        person = new Person(objectVersionID, "openEHR-DEMOGRAPHIC-PERSON.person.v1.adl", new DvText("person demographic data"), new Archetyped(new ArchetypeID("openEHR-DEMOGRAPHIC-PERSON.person.v1"), "1.0.0.0"), null, null, identities, contacts, null, null, null, null, null);

        assertTrue(erroEsperadoFoiEncontrado(ErrorType.ATTRIBUTE_MISSING, person));
    }

    @Test
    public void testPersonWithoutItems() throws Exception {
        Person person = this.getPerson();
        ((ItemTree) person.getDetails()).setItems(null);
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.ATTRIBUTE_MISSING, person));
    }

    /**
     * Responsável por verificar se o validador está realmente pegando o erro.
     * Para isso é preciso de um Person  com algum inconsistência em relação às
     * restrições que o arquétipo impoem e o ErrorType equivalente à inconsistência.
     * @param errortype Tipo de erro que deverá ser compativel com inconsistência
     * injetada no person.
     * @param person Objeto com alguma inconsistência para ser testado.
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
