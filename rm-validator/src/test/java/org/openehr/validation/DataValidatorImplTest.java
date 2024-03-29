package org.openehr.validation;

import java.lang.reflect.InvocationTargetException;
import static org.junit.Assert.*;
import br.ufg.inf.fs.pep.archetypes.ArchetypeRepository;
import br.ufg.inf.fs.pep.archetypes.ArchetypeRepositoryFactory;
import br.ufg.inf.fs.pep.archetypes.ArchetypeRepositoryImpl;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.ArchetypeInternalRef;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.CSingleAttribute;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.datastructure.itemstructure.ItemTree;
import org.openehr.rm.datastructure.itemstructure.representation.Cluster;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datastructure.itemstructure.representation.Item;
import org.openehr.rm.datatypes.basic.DvBoolean;
import org.openehr.rm.datatypes.quantity.DvCount;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.quantity.datetime.DvTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.demographic.Address;
import org.openehr.rm.demographic.Contact;
import org.openehr.rm.demographic.PartyIdentity;
import org.openehr.rm.demographic.Person;
import org.openehr.rm.support.basic.Interval;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.ObjectVersionID;

/**
 * Testes de validação de dados baseados em arquétipos.
 */
public class DataValidatorImplTest extends PepBaseTest {

    private ArchetypeRepository repository = null;
    private DataValidator validator = null;

    @Before
    public void setUp() {
        this.repository = ArchetypeRepositoryFactory.getInstance();
        this.validator = new DataValidatorImpl();
    }

    @Test
    public void testDvDateTime() throws Exception {
        DvDateTime dateTime = new DvDateTime();

        Element element = new Element("at0000", "PerÌodo da ˙ltima menstruaÁ„o normal", dateTime);
        String archetypeId = "openEHR-EHR-ELEMENT.last_normal_menstrual_period.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;

        errors = this.validator.validate(element, archetype);
        assertTrue("The list must not contain error", errors.isEmpty());
    }

    @Test
    public void testTodosTiposPrimitivos() throws Exception {
        List todosTiposPrimitivos = new ArrayList();
        todosTiposPrimitivos.add(new Element("at0001", "boolean", new DvBoolean(true)));//CBOOLEAN

        todosTiposPrimitivos.add(new Element("at0003", "datetime", new DvDateTime()));
        todosTiposPrimitivos.add(new Element("at0004", "duration", new DvDuration("P1y2WT2H10m1,60S")));
        todosTiposPrimitivos.add(new Element("at0005", "count", new DvCount(1)));
        todosTiposPrimitivos.add(new Element("at0006", "quantity", new DvQuantity(1)));
        todosTiposPrimitivos.add(new Element("at0007", "text", new DvText("testse")));
        todosTiposPrimitivos.add(new Element("at0008", "time", new DvTime("12:10:45")));
        todosTiposPrimitivos.add(new Element("at0002", "date", new DvDate()));
        Cluster cluster = new Cluster("teste", new DvText("teste"), todosTiposPrimitivos);

        Archetype archetype = this.repository.getArchetype("openEHR-EHR-ELEMENT.archetypeTest.v1");

        List<ValidationError> errors = null;

        errors = this.validator.validate(cluster, archetype);
        assertFalse("The list must not contain error", errors.isEmpty());
    }

    @Test
    public void testDvText() throws Exception {
        DvText text = new DvText("Universidade Federal de Goi·s");
        Element element = new Element("at0001", "ArquÈtipo de teste DVTEXT", text);
        String archetypeId = "openEHR-EHR-ELEMENT.test_dvtext.v1";

        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = null;
        errors = this.validator.validate(element, archetype);
        assertTrue("The list must not contain error", errors.isEmpty());
    }

    @Test
    public void testDvBoolean_Validacao_tipos_primitivos() throws Exception {
        DvBoolean boleano = new DvBoolean(true);
        Element element = new Element("at0001", "ArquÈtipo de teste DVTEXT", boleano);
        String archetypeId = "openEHR-EHR-ELEMENT.TestDvBoolean.v1";
        Archetype archetype = this.repository.getArchetype(archetypeId);
        List<ValidationError> errors = null;
        errors = this.validator.validate(element, archetype);

        assertTrue("The list must not contain error", errors.isEmpty());
    }

    @Test
    public void testSingleAtributeAlternatives() throws Exception {
        DvBoolean bol = new DvBoolean(true);
        Element element = new Element("at0001", "Arquétipo de teste DVTEXT", bol);
        String archetypeId = "openEHR-EHR-ELEMENT.test_dvtext.v1";
        Archetype archetype = this.repository.getArchetype(archetypeId);
        List<ValidationError> errors = null;

        errors = this.validator.validate(element, archetype);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void testDvBoolean_Validacao_tipos_Complexo() throws Exception {
        DvText text = new DvText("Universidade Federal de Goi·s");
        Element element = new Element("at0001", "ArquÈtipo de teste DVTEXT", text);
        String archetypeId = "openEHR-EHR-ELEMENT.TestDvBoolean.v1";
        Archetype archetype = this.repository.getArchetype(archetypeId);
        List<ValidationError> errors = null;
        errors = this.validator.validate(element, archetype);

        assertFalse(errors.isEmpty());
    }

    @Test
    public void testPerson() throws Exception {
        Person person = this.getPerson();
        String archetypeId = "openEHR-DEMOGRAPHIC-PERSON.person.v1";
        Archetype archetype = this.repository.getArchetype(archetypeId);

        List<ValidationError> errors = validator.validate(person, archetype);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testInclude() throws Exception {
        List<Item> items = new ArrayList<Item>();
        items.add(new Element("openEHR-EHR-ELEMENT.TestDvBoolean.v1", "Arquétipo de teste DVTEXT", new DvBoolean(true)));
        Cluster cluster = new Cluster("openEHR-EHR-CLUSTER.TestExclude.v1", "Arquétipo de teste", items);

        List<ValidationError> errors = null;
        errors = validator.validate(cluster);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testExclude() throws Exception {
        List items = new ArrayList();
        items.add(new Element("openEHR-EHR-ELEMENT.test_dvtext.v1", "Arquétipo de teste DVTEXT", new DvText("Universidade Federal de Goi·s")));
        Cluster cluster = new Cluster("openEHR-EHR-CLUSTER.TestExclude.v1", "Arquétipo de teste", items);

        List<ValidationError> errors = null;
        errors = validator.validate(cluster);
        assertFalse(errors.isEmpty());
        assertEquals("The error must be of the type OCCURRENCE_NOT_DESCRIBED", ErrorType.OCCURRENCES_NOT_DESCRIBED, errors.get(0).getErrorType());
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

    @Test
    public void testClusterPersonAdditionaData() throws Exception {
        Person person = this.getPerson();
        //adicionei um novo Cluster, mas so pode ter de 0...1 cluster segundo o arquetipo person_additional_data_iso
        ((ItemTree) person.getDetails()).getItems().add(getPersonAdditionalDataIso());

        assertTrue(getConjuntoDeErros(person).isEmpty());
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
    public void testArchetypeSlot() throws Exception {
        Person person = this.getPerson();
        ((ItemTree) person.getDetails()).getItems().remove(0);
        ((ItemTree) person.getDetails()).getItems().remove(1);
        assertTrue(getConjuntoDeErros(person).isEmpty());

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

    @Test
    public void testClusterPersonBirthData_Item_Invalid_Element() throws Exception {
        Person person = this.getPerson();
        Cluster s = (Cluster) ((ItemTree) person.getDetails()).getItems().get(1);
        Element dataNascimentoElementInvalid = new Element("at0007", new DvText("Data de nascimento"), new DvDate(1984, 10, 10));

        s.getItems().add(dataNascimentoElementInvalid);
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.OCCURRENCES_NOT_DESCRIBED));
    }

    @Test
    public void testClusterPersonBirthData_Item_Invalid_Element2() throws Exception {
        Person person = this.getPerson();
        Cluster s = (Cluster) ((ItemTree) person.getDetails()).getItems().get(1);

        Element indicaNascimentoElement = new Element("at0005", new DvText("Indica Nascimento"), new DvBoolean(true));

        s.getItems().add(indicaNascimentoElement);
        assertTrue(getConjuntoDeErros(person).isEmpty());
    }

    @Test
    public void testClusterPersonBirthData_Item_Occurrences_Too_Many() throws Exception {
        Person person = this.getPerson();
        Cluster s = (Cluster) ((ItemTree) person.getDetails()).getItems().get(1);

        Cluster person_other_birth_data = getPersonOtherBirthDataBr();
        s.getItems().add(person_other_birth_data);
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.OCCURRENCES_TOO_MANY));
    }

    @Test
    public void testClusterPersonBirthData_Item_Invalid_Element4() throws Exception {
        Person person = this.getPerson();
        Cluster s = (Cluster) ((ItemTree) person.getDetails()).getItems().get(1);


        DvCodedText paisNascimentoCodeText = new DvCodedText("Brasil", new CodePhrase("local", "at0010"));
        Element paisNascimentoElement = new Element("at0002", new DvText("Pais de nascimento"), paisNascimentoCodeText);
        s.getItems().add(paisNascimentoElement);

        assertTrue(getConjuntoDeErros(person).contains(ErrorType.OCCURRENCES_TOO_MANY));
    }

    @Test
    public void testClusterPersonBirthData_Item_Element_Ocurrence() throws Exception {
        Person person = this.getPerson();
        Cluster s = (Cluster) ((ItemTree) person.getDetails()).getItems().get(1);

        Element postalCodeElement = new Element("at0004", "Postal Code", new DvText("1022"));
        s.getItems().add(postalCodeElement);
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.OCCURRENCES_TOO_MANY));
    }

    @Test
    public void testClusterPersonBirthData_Item_invalid_Element_() throws Exception {
        Person person = this.getPerson();
        Cluster s = (Cluster) ((ItemTree) person.getDetails()).getItems().get(1);
        Element postalCodeElement = new Element("at0001", "Postal Code", new DvText("1022"));
        s.getItems().add(postalCodeElement);
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.OCCURRENCES_TOO_MANY));
    }

    @Test
    public void testClusterPersonBirthData_ELEMENT_at0002_with_invalid_value() throws Exception {
        Person person = this.getPerson();
        Cluster item = (Cluster) ((ItemTree) person.getDetails()).getItems().get(1);
        DvCodedText paisNascimentoCodeText = new DvCodedText("Brasil", new CodePhrase("local", "at0002"));
        ((Element) item.getItems().get(1)).setValue(paisNascimentoCodeText);
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.DOMAIN_TYPE_VALUE_ERROR));
    }

    @Test
    public void testClusterPersonDeathData_Items_Empty() throws Exception {
        Person person = this.getPerson();
        ((Cluster) ((ItemTree) person.getDetails()).getItems().get(2)).getItems().clear();
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.ITEMS_TOO_FEW));
    }

    @Test//DV_DATE
    public void testClusterPersonDeathData_Items_ELEMENT_at0001() throws Exception {
        Person person = this.getPerson();
        Cluster item = (Cluster) ((ItemTree) person.getDetails()).getItems().get(2);

        Element postalCodeElement = new Element("at0001", "Postal Code", new DvText("1022"));
        item.getItems().add(postalCodeElement);
        assertTrue(getConjuntoDeErros(person).contains(ErrorType.OCCURRENCES_TOO_MANY));
    }

    @Test
    public void testClusterPersonDeathData_Items_ELEMENT_at0002_with_invalid_value() throws Exception {
        Person person = this.getPerson();
        Cluster item = (Cluster) ((ItemTree) person.getDetails()).getItems().get(2);

        //O arquetipo define que o value do Element[at0002] deveria ser um DV_CODED_TEXT,
        //mas estou passando um DV_DATE
        ((Element) item.getItems().get(0)).setValue(new DvDateTime());
        assertFalse(getConjuntoDeErros(person).isEmpty());
    }

    @Test
    public void testClusterPersonDeathData_Items_ELEMENT_at0002() throws Exception {
        Person person = this.getPerson();
        Cluster item = (Cluster) ((ItemTree) person.getDetails()).getItems().get(2);

        //O arquetipo define que o value do Element[at0002] deveria ser um DV_CODED_TEXT,
        //mas estou passando um DV_DATE
        DvCodedText paisNascimentoCodeText = new DvCodedText("at0019", new CodePhrase("local", "at0019"));
        ((Element) item.getItems().get(1)).setValue(paisNascimentoCodeText);

        assertTrue(getConjuntoDeErros(person).contains(ErrorType.DOMAIN_TYPE_VALUE_ERROR));
    }

    @Test
    public void validateArchetypeInternalRefTest() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{

        List<ValidationError> errors = new ArrayList<ValidationError>();
        Method method = getMethod(DataValidatorImpl.class, 
                "validateArchetypeInternalRef", Archetype.class,
                ArchetypeInternalRef.class, Object.class, String.class,
                List.class);
        executeMethod(validator, method, getArchetype(),
                getArchetypeInternalRef(), new DvDateTime(), "", errors);
        assertEquals("Lista contem erros de validacao", 0, errors.size());

    }

    private ArchetypeInternalRef getArchetypeInternalRef() {
        Archetype archetype = getArchetype();
        CObject cObject = (CObject) archetype.node("/value");
        String path = "/";
        String rmTypeName = cObject.getRmTypeName();
        Interval<Integer> occurrences = cObject.getOccurrences();
        String nodeId = cObject.getNodeId();
        CAttribute parent = cObject.getParent();
        String targetPath = cObject.path();
        ArchetypeInternalRef air = new ArchetypeInternalRef(path, rmTypeName, occurrences, nodeId, parent, targetPath);
        return air;
    }

    private Archetype getArchetype() {
        String archetypeID = "openEHR-EHR-ELEMENT.last_normal_menstrual_period.v1";
        Archetype archetype = new ArchetypeRepositoryImpl().getArchetype(archetypeID);
        return archetype;
    }




    /**
     * Testa se o método lançará a excessão
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    public void argumentoNaoVazioENaoNuloTest()
            throws NoSuchMethodException,IllegalAccessException,
            IllegalArgumentException, InvocationTargetException{
        DataValidatorImpl dv = new DataValidatorImpl();
        Method method = getMethod(DataValidatorImpl.class, "argumentoNaoVazioENaoNulo",
                Object.class, String.class);
        executeMethod(dv, method, "objeto", "nome");
    }

    @Test(expected=InvocationTargetException.class)
    public void argumentoNaoVazioENaoNuloTestEmptyArgument()
            throws NoSuchMethodException,IllegalAccessException,
            IllegalArgumentException, InvocationTargetException{
        DataValidatorImpl dv = new DataValidatorImpl();
        Method method = getMethod(DataValidatorImpl.class, "argumentoNaoVazioENaoNulo",
                Object.class, String.class);
        executeMethod(dv, method, StringUtils.EMPTY, "nome");
    }

    @Test(expected=InvocationTargetException.class)
    public void argumentoNaoVazioENaoNuloTestNullArgument() throws NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException{
        DataValidatorImpl dv = new DataValidatorImpl();
        Method method = getMethod(DataValidatorImpl.class, "argumentoNaoVazioENaoNulo",
                Object.class, String.class);
        executeMethod(dv, method, null, "nome");
    }

    /**
     * Respons·vel por verificar se o validador est· realmente pegando o erro.
     * Para isso È preciso de um Person  com algum inconsistÍncia em relaÁ„o ‡s
     * restriÁıes que o arquÈtipo impoem e o ErrorType equivalente ‡ inconsistÍncia.
     * @param errortypeEsperado Tipo de erro que dever· ser compativel com inconsistÍncia
     * injetada no person.
     * @param person Objeto com alguma inconsistÍncia para ser testado.
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

    private Method getMethod(Class klass, String methodName, Class...parametersTypes) throws NoSuchMethodException{
        return klass.getDeclaredMethod(methodName, parametersTypes);
    }

    private Object executeMethod(Object hostObject, Method method,
            Object...arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        method.setAccessible(true);
        return method.invoke(hostObject, arguments);
    }
}