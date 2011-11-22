package org.openehr.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.datastructure.itemstructure.ItemTree;
import org.openehr.rm.datastructure.itemstructure.representation.Cluster;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.basic.DvBoolean;
import org.openehr.rm.datatypes.basic.DvIdentifier;
import org.openehr.rm.datatypes.quantity.DvInterval;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.demographic.Address;
import org.openehr.rm.demographic.Contact;
import org.openehr.rm.demographic.PartyIdentity;
import org.openehr.rm.demographic.Person;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UIDBasedID;

/**
 *
 */
public class PepBaseTest {

    @Test
    public void testBase() {
    }

    protected Person getPerson() {

        Person person = null;

        // Details
        Cluster personAdditionalDataIso = getPersonAdditionalDataIso();
        Cluster personBirthDataBr = getPersonOtherBirthDataBr();
        Cluster personBirthData = getPersonBirthDataIso(personBirthDataBr);
        Cluster personOtherDeathData = getPersonOtherDeathData();
        Cluster personDeathDataIso = getPersonDeathDataIso(personOtherDeathData);
        Cluster personIdentifier = getPersonIdentifierIso();

        /**
         * ItemTree de details
         */
        List listDetails = new ArrayList();
        listDetails.add(personAdditionalDataIso);
        listDetails.add(personBirthData);
        listDetails.add(personDeathDataIso);
        listDetails.add(personIdentifier);
        ItemTree itemTreeDetails = new ItemTree("at0001", "Details", listDetails);

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

        person = new Person(objectVersionID, "openEHR-DEMOGRAPHIC-PERSON.person.v1.adl", new DvText("person demographic data"), new Archetyped(new ArchetypeID("openEHR-DEMOGRAPHIC-PERSON.person.v1"), "1.0.0.0"), null, null, identities, contacts, null, null, itemTreeDetails, null, null);

        return person;
    }

    protected Address getContact() {
        /**
         * Contato
         * openEHR-DEMOGRAPHIC-ADDRESS.address.v1
         */
        DvCodedText tipoEnderecoCodedText = new DvCodedText("Comercial", new CodePhrase("local", "at0461"));
        List linhaEnderecosList = new ArrayList();
        Element complexTypeElement = new Element("at0021", "Complex", new DvCodedText("local", new CodePhrase("local", "at0500")));
        linhaEnderecosList.add(complexTypeElement);
        Element nroComplexElement = new Element("at0022", "Nro complex", new DvText("080"));
        linhaEnderecosList.add(nroComplexElement);
        Element siteNameElement = new Element("at0023", "Address Site Name", new DvText("address site name"));
        linhaEnderecosList.add(siteNameElement);
        Element levelNumberElement = new Element("at0024", "Level Number", new DvText("04"));
        linhaEnderecosList.add(levelNumberElement);
        Element levelTypeElement = new Element("at0025", "Level Type", new DvText("Andar"));
        linhaEnderecosList.add(levelTypeElement);
        Element lotNumberElement = new Element("at0026", "Lot number", new DvText("20"));
        linhaEnderecosList.add(lotNumberElement);
        Element streetTypeElement = new Element("at0027", "Street type", new DvCodedText("logradouro", new CodePhrase("local", "at0501")));
        linhaEnderecosList.add(streetTypeElement);
        Element streeNameElement = new Element("at0028", "Street name", new DvText("Rua anhanguera"));
        linhaEnderecosList.add(streeNameElement);
        Element numberElement = new Element("at0029", "Number", new DvText("045"));
        linhaEnderecosList.add(numberElement);
        Element addressLineElement = new Element("at0031", "Linha Endereco", new DvText("Rua anhanguera nro 20, etc."));
        linhaEnderecosList.add(addressLineElement);
        Cluster addressLineCluster = new Cluster("at0002", "Linha de Endere�o", linhaEnderecosList);
        List itemsEnderecoList = new ArrayList();
        itemsEnderecoList.add(addressLineCluster);
        Element deliveryPointIdElement = new Element("at0003", "Ponto de entrega", new DvText("Proximo a praca da cidade"));
        itemsEnderecoList.add(deliveryPointIdElement);
        Element postalCodeElement = new Element("at0004", "Postal Code", new DvText("1022"));
        itemsEnderecoList.add(postalCodeElement);
        Element cityElement = new Element("at0005", "Cidade", new DvText("Goiania"));
        itemsEnderecoList.add(cityElement);
        Element stateElement = new Element("at0006", "Estado", new DvCodedText("Goias", new CodePhrase("local", "at0503")));
        itemsEnderecoList.add(stateElement);
        Element countryElement = new Element("at0007", "Pa�s", new DvCodedText("Brazil", new CodePhrase("local", "at0504")));
        itemsEnderecoList.add(countryElement);
        ItemTree enderecoItemTree = new ItemTree("at0001", new DvText("Itens de endereco"), itemsEnderecoList);
        Address address = new Address(null, "openEHR-DEMOGRAPHIC-ADDRESS.address.v1", tipoEnderecoCodedText, null, null, null, null, enderecoItemTree);
        return address;
    }

    private Element newItemText(String nodeId, String name, String value) {
        return new Element(nodeId, name, new DvText(value));
    }

    private Element newItemBoolean(String nodeId, String name, boolean value) {
        return new Element(nodeId, name, new DvBoolean(value));
    }

    protected PartyIdentity getPersonName(Person person) {
        /**
         * Party Identity
         * openEHR-DEMOGRAPHIC-PARTY_IDENTITY.person_name.v1
         */
        DvText nameIdentifierDvCodedText = new DvText("legal identity");

        List items = new ArrayList();
        Element givenName = newItemText("at0002", "Given name", "Edson");
        items.add(givenName);

        Element familyName = newItemText("at0003", "family name", "Nascimento");
        items.add(familyName);

        Element title = newItemText("at0004", "Name title", "Sr.");
        items.add(title);

        // /details/items[at0005] (sufixo não se aplica)

        Element periodIntervalElement = new Element("at0019", "Period Interval", new DvInterval<DvDate>(new DvDate(2011, 01), new DvDate(2111, 12)));
        items.add(periodIntervalElement);
        Element IdentifierElement = new Element("at0020", "Usage Identifier", new DvIdentifier("Receita Federal", "PF", "111.111.111-11", "CPF"));
        items.add(IdentifierElement);

        // CLUSTER [at0007]
        String name = "Usage Representative";
        String value = "Uso representativo";
        Element uso = newItemText("at0021", name, value);

        name = "Alternative representation";
        value = "Representação alternativa";
        Element representacao = newItemText("at0022", name, value);

        List itemsAltName = new ArrayList();
        itemsAltName.add(uso);
        itemsAltName.add(representacao);

        name = "Alternative Name Representation";
        Cluster alternativeName = new Cluster("at0007", name, itemsAltName);
        // CLUSTER [at0007]

        items.add(alternativeName);

        Element preferredName = newItemBoolean("at0008", "Prefered Name", true);
        items.add(preferredName);

        String nomeCompleto = "Edson Arantes do Nascimento";
        Element fullName = newItemText("at0010", "Full Name", nomeCompleto);
        items.add(fullName);

        ItemTree details = new ItemTree("at0001", "Name components", items);

        UIDBasedID basedId = null;
        String aNodeId = "openEHR-DEMOGRAPHIC-PARTY_IDENTITY.person_name.v1";
        ArchetypeID archetypeId = new ArchetypeID(aNodeId);
        Archetyped archetyped = new Archetyped(archetypeId, "1.0.0.0");
        PartyIdentity partyIdentity = new PartyIdentity(basedId, aNodeId,
                nameIdentifierDvCodedText, archetyped, null, null,
                person, details);
        return partyIdentity;
    }

    protected Cluster getPersonIdentifierIso() {
        /**
         * person identifier
         * openEHR-DEMOGRAPHIC-CLUSTER.person_identifier.v1.adl
         */
        Cluster personIdentifier = null;
        List itemsPersonIdentifier = new ArrayList();
        DvText designationText = new DvText("Designation");
        Element elementDesignation = new Element("at0001", "Designation", designationText);
        itemsPersonIdentifier.add(elementDesignation);
        DvCodedText geographicAreaCodeText = new DvCodedText("62", new CodePhrase("local", "at0013"));
        Element geographicAreaElement = new Element("at0002", new DvText("Area Geografica"), geographicAreaCodeText);
        itemsPersonIdentifier.add(geographicAreaElement);
        DvText issuerText = new DvText("Issuer");
        Element issuerElement = new Element("at0003", "Issuer", issuerText);
        itemsPersonIdentifier.add(issuerElement);
        DvCodedText typeIdentificationCodeText = new DvCodedText("Receita Federal", new CodePhrase("local", "at0020"));
        Element typeIdentificationElement = new Element("at0004", new DvText("Tipo de identificacao"), typeIdentificationCodeText);
        itemsPersonIdentifier.add(typeIdentificationElement);
        personIdentifier = new Cluster("openEHR-DEMOGRAPHIC-CLUSTER.person_identifier.v1", new DvText("Identificador da pessoa"), itemsPersonIdentifier);
        return personIdentifier;
    }

    protected Cluster getPersonDeathDataIso(Cluster personOtherDeathData) {
        /**
         * Person death data iso
         * openEHR-DEMOGRAPHIC-CLUSTER.person_death_data_iso.v1
         */
        Cluster personDeathDataIso = null;
        List itemspersonDeathDataIso = new ArrayList();
        DvDate deathDate = new DvDate(2011, 10, 2);
        Element deathDateElement = new Element("at0001", new DvText("Data da morte"), deathDate);
        itemspersonDeathDataIso.add(deathDateElement);
        DvCodedText sourceNotificationCodedText = new DvCodedText("Registry", new CodePhrase("local", "at0020"));
        Element elementSourceNotification = new Element("at0002", "Fonte da notificaçãoo", sourceNotificationCodedText);
        itemspersonDeathDataIso.add(elementSourceNotification);
        itemspersonDeathDataIso.add(personOtherDeathData);
        personDeathDataIso = new Cluster("openEHR-DEMOGRAPHIC-CLUSTER.person_death_data_iso.v1", new DvText("Dados da morte"), itemspersonDeathDataIso);
        return personDeathDataIso;
    }

    protected Cluster getPersonOtherDeathData() {
        /**
         * person other death data
         * openEHR-DEMOGRAPHIC-CLUSTER.person_other_death_data.v1
         */
        Cluster personOtherDeathData = null;
        List itemsPersonOtherDeathData = new ArrayList();
        DvCodedText countryOfDeathCodedText = new DvCodedText("Brasil", new CodePhrase("local", "at0010"));
        Element countryOfDeathElement = new Element("at0001", new DvText("Pais de morte"), countryOfDeathCodedText);
        itemsPersonOtherDeathData.add(countryOfDeathElement);
        DvCodedText stateOfDeathCodedText = new DvCodedText("Goias", new CodePhrase("local", "at0021"));
        Element stateOfDeathElement = new Element("at0002", new DvText("Estado de morte"), stateOfDeathCodedText);
        itemsPersonOtherDeathData.add(stateOfDeathElement);
        DvCodedText cityOfDeathCodedText = new DvCodedText("Goiania", new CodePhrase("local", "at0030"));
        Element cityOfDeathElement = new Element("at0003", new DvText("Cidade de morte"), cityOfDeathCodedText);
        itemsPersonOtherDeathData.add(cityOfDeathElement);
        DvText certificateNumberText = new DvText("11a2");
        Element certificateNumberElement = new Element("at0004", new DvText("Número de certificado"), certificateNumberText);
        itemsPersonOtherDeathData.add(certificateNumberElement);
        personOtherDeathData = new Cluster("openEHR-DEMOGRAPHIC-CLUSTER.person_other_death_data.v1", new DvText("Outros dados de morte"), itemsPersonOtherDeathData);
        return personOtherDeathData;
    }

    protected Cluster getPersonBirthDataIso(Cluster personBirthDataBr) {
        /**
         * Person birth data
         * openEHR-DEMOGRAPHIC-CLUSTER.person_birth_data_iso.v1
         */
        Cluster personBirthData = null;
        List itemsPersonBirthData = new ArrayList();
        DvDate dataNascimentoDate = new DvDate(1984, 10, 10);
        Element dataNascimentoElement = new Element("at0001", new DvText("Data de nascimento"), dataNascimentoDate);
        itemsPersonBirthData.add(dataNascimentoElement);
        DvCodedText paisNascimentoCodeText = new DvCodedText("Brasil", new CodePhrase("local", "at0010"));
        Element paisNascimentoElement = new Element("at0002", new DvText("Pais de nascimento"), paisNascimentoCodeText);
        itemsPersonBirthData.add(paisNascimentoElement);
        DvCodedText nascimentoMultiploCodeText = new DvCodedText("Gemeos", new CodePhrase("local", "at0020"));
        Element nascimentoMultiploElement = new Element("at0003", new DvText("Nascimento Multiplo"), nascimentoMultiploCodeText);
        itemsPersonBirthData.add(nascimentoMultiploElement);
        DvCodedText ordemNascimentoCodeText = new DvCodedText("Primeiro", new CodePhrase("local", "at0030"));
        Element ordemNascimentoElement = new Element("at0004", new DvText("Ordem nascimento"), ordemNascimentoCodeText);
        itemsPersonBirthData.add(ordemNascimentoElement);
        DvBoolean indicaSeguimetnoNascimento = new DvBoolean(true);
        Element indicaNascimentoElement = new Element("at0005", new DvText("Indica Nascimento"), indicaSeguimetnoNascimento);
        itemsPersonBirthData.add(indicaNascimentoElement);
        itemsPersonBirthData.add(personBirthDataBr);
        personBirthData = new Cluster("openEHR-DEMOGRAPHIC-CLUSTER.person_birth_data_iso.v1",
                new DvText("Dados do nascimento"), itemsPersonBirthData);
        return personBirthData;
    }

    protected Cluster getPersonOtherBirthDataBr() {
        /**
         * Person birth data br
         * openEHR-DEMOGRAPHIC-CLUSTER.person_other_birth_data_br.v1
         */
        Cluster personBirthDataBr = null;
        List itemsPersonBirthDataBr = new ArrayList();
        DvCodedText stateCodedText = new DvCodedText("Goias", new CodePhrase("local", "at0010"));
        Element elementState = new Element("at0001", new DvText("State"), stateCodedText);
        itemsPersonBirthDataBr.add(elementState);
        DvCodedText cityCodedText = new DvCodedText("Goiania", new CodePhrase("local", "at27"));
        Element elementCity = new Element("at0002", new DvText("City"), cityCodedText);
        itemsPersonBirthDataBr.add(elementCity);
        DvText registryOfficeText = new DvText("CARTÓRIO ZECA DO BAR");
        Element elementRegistryOffice = new Element("at0003", new DvText("Cartório de Registros"), registryOfficeText);
        itemsPersonBirthDataBr.add(elementRegistryOffice);
        DvText bookNumberText = new DvText("1984");
        Element bookNumberElement = new Element("at0004", new DvText("Numero do livro"), bookNumberText);
        itemsPersonBirthDataBr.add(bookNumberElement);
        DvText pageNumberText = new DvText("201");
        Element pageNumberElement = new Element("at0005", new DvText("Numero da pagina"), pageNumberText);
        itemsPersonBirthDataBr.add(pageNumberElement);
        DvText sectionNumberText = new DvText("136");
        Element sectionNumberElement = new Element("at0006", new DvText("Numero da secao"), sectionNumberText);
        itemsPersonBirthDataBr.add(sectionNumberElement);
        personBirthDataBr = new Cluster("openEHR-DEMOGRAPHIC-CLUSTER.person_other_birth_data_br.v1", new DvText("Brazilian birth certificate other data"), itemsPersonBirthDataBr);
        return personBirthDataBr;
    }

    protected Cluster getPersonAdditionalDataIso() {
        /**
         * Person additional data iso
         * openEHR-DEMOGRAPHIC-CLUSTER.person_additional_data_iso.v1
         */
        Cluster personAdditionalDataIso = null;
        List personAdditionalList = new ArrayList();
        DvCodedText sexo = new DvCodedText("Masculino", new CodePhrase("local", "at0010"));
        Element sex = new Element("at0001", new DvText("sexo"), sexo);
        personAdditionalList.add(sex);
        DvText textNomeMae = new DvText("Madre Teresa");
        Element elementNomeMae = new Element("at0002", new DvText("Nome da mãe"), textNomeMae);
        personAdditionalList.add(elementNomeMae);
        DvText textComentIdentificacao = new DvText("Baixa estatura e careca");
        Element elementComentIdentificacao = new Element("at0003", new DvText("Comentários de Identificação"), textComentIdentificacao);
        personAdditionalList.add(elementComentIdentificacao);
        personAdditionalDataIso = new Cluster("openEHR-DEMOGRAPHIC-CLUSTER.person_additional_data_iso.v1", new DvText("Dados Adicionais"), personAdditionalList);
        return personAdditionalDataIso;
    }
}
