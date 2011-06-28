package org.openehr.validation;

import static org.junit.Assert.*;
import br.ufg.inf.fs.pep.archetypes.ArchetypeRepository;
import br.ufg.inf.fs.pep.archetypes.PepArchetypeRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.datastructure.itemstructure.ItemTree;
import org.openehr.rm.datastructure.itemstructure.representation.Cluster;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
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
        person.getContacts().iterator().next().getAddresses().clear();
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.ATTRIBUTE_MISSING, person));
    }

    @Test
    public void testPersonWithoutContacts() throws Exception {
        Person person = this.getPerson();
        person.setContacts(null);
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.ATTRIBUTE_MISSING, person));
    }

    @Test
    public void testPersonContactsOccurrencesTooMany() throws Exception {
        Person person = this.getPerson();
        Address address = getContact();
        //adiciona um contaco invalido, ele possui o mesmo identificador(at0003)
        //do outro contact que está na lista.
        List listAddresses = new ArrayList();
        listAddresses.add(address);
        Contact contactComMesmoArcheptyNodeId = new Contact(null, "at0003", new DvText("Contact"), null, null, null, person, null, listAddresses);
        person.getContacts().add(contactComMesmoArcheptyNodeId);
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.OCCURRENCES_TOO_MANY, person));
    }

    @Test
    public void testPersonContactsOccurrencesTooFew() throws Exception {
        Person person = this.getPerson();
        person.getContacts().clear();
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.OCCURRENCES_TOO_FEW, person));
    }

    @Test
    public void testPersonWithoutItems() throws Exception {
        Person person = this.getPerson();
        ((ItemTree) person.getDetails()).setItems(null);
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.ATTRIBUTE_MISSING, person));
    }

    @Test
    public void testPersonWithoutIdentities() throws Exception {
        Person person = this.getPerson();
        person.getIdentities().clear();
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.ITEMS_TOO_FEW, person));
    }

    @Ignore
    @Test
    public void testClusterPersonAdditionaData() throws Exception {
        Person person = this.getPerson();
        //adicionei um novo Cluster, mas so pode ter de 0...1 cluster segundo o arquetipo person_additional_data_iso
        ((ItemTree) person.getDetails()).getItems().add(getPersonAdditionalDataIso());
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.OCCURRENCES_TOO_MANY, person));
    }

    @Test
    public void testClusterPersonAdditionaDataWithoutItems() throws Exception {
        Person person = this.getPerson();
        ((ItemTree) person.getDetails()).setItems(null);
        assertTrue(erroEsperadoFoiEncontrado(ErrorType.ATTRIBUTE_MISSING, person));
    }

    @Test
    public void testClusterPersonAdditionaData_ELEMENT_at0001_with_invalid_value() throws Exception {
        Person person = this.getPerson();
        Cluster item = (Cluster) ((ItemTree) person.getDetails()).getItems().get(0);
        //segundo o arquetipo person_additiona_data o element at0001 nao pode ter o valor abaixo
        DvCodedText value = new DvCodedText("Goias", new CodePhrase("local", "at0014"));
        ((Element) item.getItems().get(0)).setValue(value);

        assertTrue(erroEsperadoFoiEncontrado(ErrorType.DOMAIN_TYPE_VALUE_ERROR, person));
        value.getDefiningCode().setCodeString("at0009");
        ((Element) item.getItems().get(0)).setValue(value);
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.DOMAIN_TYPE_VALUE_ERROR));
    }

    @Test
    public void testClusterPersonAdditionaData_ELEMENT_at0001_with_valid_value() throws Exception {
        Person person = this.getPerson();
        Cluster item = (Cluster) ((ItemTree) person.getDetails()).getItems().get(0);
        //segundo o arquetipo person_additiona_data o element at0001 nao pode ter o valor abaixo
        DvCodedText value = new DvCodedText("Goias", new CodePhrase("local", "at0010"));
        ((Element) item.getItems().get(0)).setValue(value);
        assertTrue(getConjuntoDeErros(person).isEmpty());
        value.getDefiningCode().setCodeString("at0011");
        ((Element) item.getItems().get(0)).setValue(value);
        assertTrue(getConjuntoDeErros(person).isEmpty());
        value.getDefiningCode().setCodeString("at0012");
        ((Element) item.getItems().get(0)).setValue(value);
        assertTrue(getConjuntoDeErros(person).isEmpty());
        value.getDefiningCode().setCodeString("at0013");
        ((Element) item.getItems().get(0)).setValue(value);
        assertTrue(getConjuntoDeErros(person).isEmpty());
    }

    @Test
    public void testClusterPersonBirthData_Item_null() throws Exception {
        Person person = this.getPerson();
        Cluster s = (Cluster) ((ItemTree) person.getDetails()).getItems().get(1);
        s.getItems().clear();
        
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.ITEMS_TOO_FEW));
    }

    /**
     * Responsável por verificar se o validador está realmente pegando o erro.
     * Para isso é preciso de um Person  com algum inconsistência em relação às
     * restrições que o arquétipo impoem e o ErrorType equivalente à inconsistência.
     * @param errortypeEsperado Tipo de erro que deverá ser compativel com inconsistência
     * injetada no person.
     * @param person Objeto com alguma inconsistência para ser testado.
     * @throws Exception
     */
    private boolean erroEsperadoFoiEncontrado(ErrorType errortypeEsperado, Person person) throws Exception {
        return getConjuntoDeErros(person).contains(errortypeEsperado);
    }

    private Set<ErrorType> getConjuntoDeErros(Person person) throws Exception {
        String archetypeId = "openEHR-DEMOGRAPHIC-PERSON.person.v1";
        Archetype archetype = this.repository.getArchetype(archetypeId);
        List<ValidationError> errors = null;
        errors = this.validator.validate(person, archetype);

        Set<ErrorType> erros = new HashSet<ErrorType>();
        for (ValidationError validationError : errors) {
            erros.add(validationError.getErrorType());
        }
        return erros;
    }
}
