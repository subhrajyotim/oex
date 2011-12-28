package org.openehr.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.openehr.rm.RMObject;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.common.changecontrol.Contribution;
import org.openehr.rm.common.changecontrol.OriginalVersion;
import org.openehr.rm.common.changecontrol.Version;
import org.openehr.rm.common.generic.Attestation;
import org.openehr.rm.common.generic.AuditDetails;
import org.openehr.rm.common.generic.Participation;
import org.openehr.rm.common.generic.PartyIdentified;
import org.openehr.rm.common.generic.PartyProxy;
import org.openehr.rm.common.generic.PartyRelated;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.common.resource.AuthoredResource;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.EventContext;
import org.openehr.rm.composition.content.entry.Action;
import org.openehr.rm.composition.content.entry.Activity;
import org.openehr.rm.composition.content.entry.AdminEntry;
import org.openehr.rm.composition.content.entry.Evaluation;
import org.openehr.rm.composition.content.entry.Instruction;
import org.openehr.rm.composition.content.entry.InstructionDetails;
import org.openehr.rm.composition.content.entry.Observation;
import org.openehr.rm.composition.content.navigation.Section;
import org.openehr.rm.datastructure.DataStructure;
import org.openehr.rm.datastructure.history.Event;
import org.openehr.rm.datastructure.history.History;
import org.openehr.rm.datastructure.history.IntervalEvent;
import org.openehr.rm.datastructure.history.PointEvent;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datastructure.itemstructure.ItemSingle;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.datastructure.itemstructure.ItemTable;
import org.openehr.rm.datastructure.itemstructure.ItemTree;
import org.openehr.rm.datastructure.itemstructure.representation.Cluster;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datastructure.itemstructure.representation.Item;
import org.openehr.rm.datatypes.basic.DataValue;
import org.openehr.rm.datatypes.basic.DvBoolean;
import org.openehr.rm.datatypes.basic.DvIdentifier;
import org.openehr.rm.datatypes.basic.DvState;
import org.openehr.rm.datatypes.encapsulated.DvEncapsulated;
import org.openehr.rm.datatypes.encapsulated.DvMultimedia;
import org.openehr.rm.datatypes.encapsulated.DvParsable;
import org.openehr.rm.datatypes.quantity.DvAbsoluteQuantity;
import org.openehr.rm.datatypes.quantity.DvAmount;
import org.openehr.rm.datatypes.quantity.DvCount;
import org.openehr.rm.datatypes.quantity.DvInterval;
import org.openehr.rm.datatypes.quantity.DvOrdered;
import org.openehr.rm.datatypes.quantity.DvOrdinal;
import org.openehr.rm.datatypes.quantity.DvProportion;
import org.openehr.rm.datatypes.quantity.DvQuantified;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.quantity.datetime.DvTemporal;
import org.openehr.rm.datatypes.quantity.datetime.DvTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvParagraph;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.timespec.DvTimeSpecification;
import org.openehr.rm.demographic.Address;
import org.openehr.rm.demographic.Agent;
import org.openehr.rm.demographic.Capability;
import org.openehr.rm.demographic.Contact;
import org.openehr.rm.demographic.Group;
import org.openehr.rm.demographic.Organisation;
import org.openehr.rm.demographic.PartyIdentity;
import org.openehr.rm.demographic.PartyRelationship;
import org.openehr.rm.demographic.Person;
import org.openehr.rm.demographic.Role;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.AccessGroupRef;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.GenericID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.InternetID;
import org.openehr.rm.support.identification.LocatableRef;
import org.openehr.rm.support.identification.ObjectID;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.PartyRef;
import org.openehr.rm.support.identification.TemplateID;
import org.openehr.rm.support.identification.TerminologyID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.identification.VersionTreeID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.CodeSetAccess;
import org.openehr.rm.support.terminology.TerminologyAccess;
import org.openehr.rm.support.terminology.TerminologyService;

/**
 * 
 * @author Danillo Guimarães
 */
enum KnownTypes {
	// Container Types
	LIST(List.class), SET(Set.class),

	// RM Types
	PARTYSELF(PartySelf.class), ARCHETYPED(Archetyped.class), ATTESTATION(
			Attestation.class), AUDITDETAILS(AuditDetails.class), PARTICIPATION(
			Participation.class), PARTYIDENTIFIED(PartyIdentified.class), PARTYRELATED(
			PartyRelated.class), ORIGINALVERSION(OriginalVersion.class), CONTRIBUTION(
			Contribution.class), TERMINOLOGYID(TerminologyID.class), ARCHETYPEID(
			ArchetypeID.class), HIEROBJECTID(HierObjectID.class), ACCESSGROUPREF(
			AccessGroupRef.class), GENERICID(GenericID.class), LOCATABLEREF(
			LocatableRef.class), OBJECTVERSIONID(ObjectVersionID.class), OBJECTREF(
			ObjectRef.class), PARTYREF(PartyRef.class), TEMPLATEID(
			TemplateID.class), VERSIONTREEID(VersionTreeID.class), DVBOOLEAN(
			DvBoolean.class), DVSTATE(DvState.class), DVIDENTIFIER(
			DvIdentifier.class), DVTEXT(DvText.class), DVCODEDTEXT(
			DvCodedText.class), DVPARAGRAPH(DvParagraph.class), CODEPHRASE(
			CodePhrase.class), DVCOUNT(DvCount.class), DVORDINAL(
			DvOrdinal.class), DVQUANTITY(DvQuantity.class), DVINTERVAL(
			DvInterval.class), DVPROPORTION(DvProportion.class), DVDATE(
			DvDate.class), DVDATETIME(DvDateTime.class), DVTIME(DvTime.class), DVDURATION(
			DvDuration.class), DVPARSABLE(DvParsable.class), DVMULTIMEDIA(
			DvMultimedia.class), ELEMENT(Element.class), CLUSTER(Cluster.class), ITEMSINGLE(
			ItemSingle.class), ITEMLIST(ItemList.class), ITEMTABLE(
			ItemTable.class), ITEMTREE(ItemTree.class), HISTORY(History.class), INTERVALEVENT(
			IntervalEvent.class), POINTEVENT(PointEvent.class), ACTION(
			Action.class), ACTIVITY(Activity.class), EVALUATION(
			Evaluation.class), INSTRUCTION(Instruction.class), INSTRUCTIONDETAILS(
			InstructionDetails.class), OBSERVATION(Observation.class), ADMINENTRY(
			AdminEntry.class), SECTION(Section.class), COMPOSITION(
			Composition.class), EVENTCONTEXT(EventContext.class), ADDRESS(
			Address.class), PARTYIDENTITY(PartyIdentity.class), AGENT(
			Agent.class), GROUP(Group.class), ORGANISATION(Organisation.class), PERSON(
			Person.class), CONTACT(Contact.class), PARTYRELATIONSHIP(
			PartyRelationship.class), ROLE(Role.class), CAPABILITY(
			Capability.class),

	// Extra Rm types
	EHRSTATUS(EHRStatus.class),

	// Basic types
	BYTE(byte.class), BOOLEAN(boolean.class), CHAR(char.class), DOUBLE(
			double.class), FLOAT(float.class), INT(int.class), LONG(long.class), SHORT(
			short.class), STRING(String.class), INTEGER(Integer.class),

	// Abstract types
	AUTHOREDRESOURCE(AuthoredResource.class), DATASTRUCTURE(DataStructure.class), DATAVALUE(
			DataValue.class), DVABSOLUTEQUANTITY(DvAbsoluteQuantity.class), DVAMOUNT(
			DvAmount.class), DVENCAPSULATED(DvEncapsulated.class), DVORDERED(
			DvOrdered.class), DVQUANTIFIED(DvQuantified.class), DVTEMPORAL(
			DvTemporal.class), DVTIMESPECIFICATION(DvTimeSpecification.class), EVENT(
			Event.class), ITEM(Item.class), ITEMSTRUCTURE(ItemStructure.class), LOCATABLE(
			Locatable.class), OBJECTID(ObjectID.class), PARTYPROXY(
			PartyProxy.class), PATHABLE(Pathable.class), RMOBJECT(
			RMObject.class), UIDBASEDID(UIDBasedID.class), VERSION(
			Version.class),

	// UID Identifiers
	UUID(org.openehr.rm.support.identification.UUID.class), INTERNETID(
			InternetID.class), ISO_OID(
			org.openehr.rm.support.identification.ISO_OID.class),

	// Interfaces
	TERMINOLOGYSERVICE(TerminologyService.class), TERMINOLOGYACCESS(
			TerminologyAccess.class), CODESETACCESS(CodeSetAccess.class), MEASUREMENTSERVICE(
			MeasurementService.class);

        

	private static final KnownTypes[] BASIC_TYPES = { BYTE, BOOLEAN, CHAR, DOUBLE,
			FLOAT, INT, LONG, SHORT, STRING, INTEGER };

	private static final KnownTypes[] ABSTRACT_TYPES = { AUTHOREDRESOURCE,
			DATASTRUCTURE, DATAVALUE, DVABSOLUTEQUANTITY, DVAMOUNT,
			DVENCAPSULATED, DVORDERED, DVQUANTIFIED, DVTEMPORAL,
			DVTIMESPECIFICATION, EVENT, ITEM, ITEMSTRUCTURE, LOCATABLE,
			OBJECTID, PARTYPROXY, PATHABLE, RMOBJECT, UIDBASEDID, VERSION };

	private static final KnownTypes[] UID_IDENTIFIERS = { UUID, INTERNETID, ISO_OID };

	private static final KnownTypes[] INTERFACES = { TERMINOLOGYSERVICE,
			TERMINOLOGYACCESS, CODESETACCESS, MEASUREMENTSERVICE };

	private static final KnownTypes[] UID_BASED_ID = { OBJECTVERSIONID, HIEROBJECTID };

	private static final KnownTypes[] RM_TYPES = { PARTYSELF, ARCHETYPED, ATTESTATION,
			AUDITDETAILS, PARTICIPATION, PARTYIDENTIFIED, PARTYRELATED,
			PARTYSELF, ORIGINALVERSION, CONTRIBUTION, TERMINOLOGYID,
			ARCHETYPEID, HIEROBJECTID, ACCESSGROUPREF, GENERICID, INTERNETID,
			ISO_OID, LOCATABLEREF, OBJECTVERSIONID, OBJECTREF, PARTYREF,
			TEMPLATEID, TERMINOLOGYID, UUID, VERSIONTREEID, DVBOOLEAN, DVSTATE,
			DVIDENTIFIER, DVTEXT, DVCODEDTEXT, DVPARAGRAPH, CODEPHRASE,
			DVCOUNT, DVORDINAL, DVQUANTITY, DVINTERVAL, DVPROPORTION, DVDATE,
			DVDATETIME, DVTIME, DVDURATION, DVPARSABLE, DVMULTIMEDIA, ELEMENT,
			CLUSTER, ITEMSINGLE, ITEMLIST, ITEMTABLE, ITEMTREE, HISTORY,
			INTERVALEVENT, POINTEVENT, ACTION, ACTIVITY, EVALUATION,
			INSTRUCTION, INSTRUCTIONDETAILS, OBSERVATION, ADMINENTRY, SECTION,
			COMPOSITION, EVENTCONTEXT, ADDRESS, PARTYIDENTITY, AGENT, GROUP,
			ORGANISATION, PERSON, CONTACT, PARTYRELATIONSHIP, ROLE, CAPABILITY,
			EHRSTATUS };
        
        private final String value;

	private final Class clazz;

	private static final KnownTypes[] CONTAINER_TYPES = { LIST, SET };

        public static Locale getLocale(){
            return Locale.US;
        }

	@SuppressWarnings("rawtypes")
	public static Map<String, Class> getUidBasedIds() {
		Map<String, Class> types = new HashMap<String, Class>();
		for (int i = 0; i < UID_BASED_ID.length; i++) {
			types.put(UID_BASED_ID[i].getValue(), UID_BASED_ID[i].getClazz());
		}
		return types;
	}

	public static Map<String, Class> getBasicTypes() {
		Map<String, Class> types = new HashMap<String, Class>();
		for (int i = 0; i < BASIC_TYPES.length; i++) {
			types.put(BASIC_TYPES[i].getValue(), BASIC_TYPES[i].getClazz());
		}
		return types;
	}

	public static Map<String, Class> getAbstractTypes() {
		Map<String, Class> types = new HashMap<String, Class>();
		for (int i = 0; i < ABSTRACT_TYPES.length; i++) {
			types.put(ABSTRACT_TYPES[i].getValue(), ABSTRACT_TYPES[i].getClazz());
		}
		return types;
	}

	public static Map<String, Class> getUidIdentifiers() {
		Map<String, Class> types = new HashMap<String, Class>();
		for (int i = 0; i < UID_IDENTIFIERS.length; i++) {
			types.put(UID_IDENTIFIERS[i].getValue(),
					UID_IDENTIFIERS[i].getClazz());
		}
		return types;
	}

	public static Map<String, Class> getInterfacesTypes() {
		Map<String, Class> types = new HashMap<String, Class>();
		for (int i = 0; i < INTERFACES.length; i++) {
			types.put(INTERFACES[i].getValue(), INTERFACES[i].getClazz());
		}
		return types;
	}

	public static Map<String, Class> getContainerTypes() {
		Map<String, Class> types = new HashMap<String, Class>();
		for (int i = 0; i < CONTAINER_TYPES.length; i++) {
			types.put(CONTAINER_TYPES[i].getValue(),
					CONTAINER_TYPES[i].getClazz());
		}
		return types;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Class<RMObject>> getRmTypes() {
		Map<String, Class<RMObject>> types = new HashMap<String, Class<RMObject>>();
		for (int i = 0; i < RM_TYPES.length; i++) {
			types.put(RM_TYPES[i].getValue(), RM_TYPES[i].getClazz());
		}
		return types;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, Class> getAllTypes() {
		Map<String, Class> types = new HashMap<String, Class>();
		for (int i = 0; i < UID_BASED_ID.length; i++) {
			types.put(UID_BASED_ID[i].getValue(), UID_BASED_ID[i].getClazz());
		}
		for (int i = 0; i < BASIC_TYPES.length; i++) {
			types.put(BASIC_TYPES[i].getValue(), BASIC_TYPES[i].getClazz());
		}
		for (int i = 0; i < ABSTRACT_TYPES.length; i++) {
			types.put(ABSTRACT_TYPES[i].getValue(), ABSTRACT_TYPES[i].getClazz());
		}
		for (int i = 0; i < UID_IDENTIFIERS.length; i++) {
			types.put(UID_IDENTIFIERS[i].getValue(),
					UID_IDENTIFIERS[i].getClazz());
		}
		for (int i = 0; i < INTERFACES.length; i++) {
			types.put(INTERFACES[i].getValue(), INTERFACES[i].getClazz());
		}
		for (int i = 0; i < CONTAINER_TYPES.length; i++) {
			types.put(CONTAINER_TYPES[i].getValue(),
					CONTAINER_TYPES[i].getClazz());
		}
		for (int i = 0; i < RM_TYPES.length; i++) {
			types.put(RM_TYPES[i].getValue(), RM_TYPES[i].getClazz());
		}
		return types;
	}


        private KnownTypes(Class clazz) {
		this.value = clazz.getSimpleName().toUpperCase(getLocale());
		this.clazz = clazz;
	}

        private String getValue() {
		return value;
	}

	@SuppressWarnings("rawtypes")
	private Class getClazz() {
		return clazz;
	}
}
