/*
 * component:   "openEHR Reference Implementation"
 * description: "Class Activity"
 * keywords:    "composition"
 *
 * author:      "Yin Su Lim <y.lim@chime.ucl.ac.uk>"
 * support:     "CHIME, UCL"
 * copyright:   "Copyright (c) 2006 UCL, UK"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL: http://svn.openehr.org/ref_impl_java/TRUNK/libraries/src/java/org/openehr/rm/composition/content/entry/Activity.java $"
 * revision:    "$LastChangedRevision: 53 $"
 * last_change: "$LastChangedDate: 2006-08-11 13:20:08 +0100 (Fri, 11 Aug 2006) $"
 */
package org.openehr.rm.composition.content.entry;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openehr.rm.Attribute;
import org.openehr.rm.FullConstructor;
import org.openehr.rm.common.archetyped.*;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.encapsulated.DvParsable;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.support.identification.UIDBasedID;

/**
 * Defines a single activity within an Instruction, such as a medication administration.
 *
 * @author Yin Su Lim
 * @version 1.0
 */
public class Activity extends Locatable {

	/**
	 * Creates an Activity
	 * 
	 * @param uit
	 * @param archetypeNodeId
	 * @param name
	 * @param archetypeDetails
	 * @param feederAudit
	 * @param links
	 * @param parent
	 * @param description		required
	 * @param timing
	 * @param actionArchetypeId
	 * 
	 * @throws IllegalArgumentExeption if required parameters missing
	 */
	@FullConstructor
	public Activity(
			@Attribute(name = "uid") UIDBasedID uid, 
			@Attribute(name = "archetypeNodeId", required = true) String archetypeNodeId, 
			@Attribute(name = "name", required = true) DvText name, 
			@Attribute(name = "archetypeDetails") Archetyped archetypeDetails,
			@Attribute(name = "feederAudit") FeederAudit feederAudit, 
			@Attribute(name = "links") Set<Link> links, 
			@Attribute(name = "parent")	Pathable parent, 
			@Attribute(name = "description", required = true) ItemStructure description, 
			@Attribute(name = "timing", required = true) DvParsable timing,
			@Attribute(name = "actionArchetypeId", required = true) String actionArchetypeId) {
		
		super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links,
				parent);
		if (description == null) {
			throw new IllegalArgumentException("null description");
		}
		if (timing == null) {
			throw new IllegalArgumentException("null timing");
		}
		if(StringUtils.isEmpty(actionArchetypeId)) {
			throw new IllegalArgumentException("empty actionArchetypeId");
		}
		
		this.description = description;
		this.timing = timing;
		this.actionArchetypeId = actionArchetypeId;
	}

	public Activity(String archetypeNodeId, DvText name,
			ItemStructure description, DvParsable timing, 
			String actionArchetypeId) {
		this(null, archetypeNodeId, name, null, null, null, null, description,
				timing, actionArchetypeId);
	}

	/**
	 * Description of the activity, in the form of an archetyped structure.
	 * 
	 * @return description
	 */
	public ItemStructure getDescription() {
		return description;
	}

	/**
	 * Timing of the activity, in the form of a parsable string, such as 
	 * GTS or iCal string
	 * 
	 * @return timing
	 */
	public DvParsable getTiming() {
		return timing;
	}
	
	public String getActionArchetypeId() {
		return actionArchetypeId;
	}

	@Override
	public String pathOfItem(Pathable item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> itemsAtPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean pathExists(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pathUnique(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	//POJO start
	Activity() {
	}

	void setDescription(ItemStructure description) {
		this.description = description;
	}

	void setTiming(DvParsable timing) {
		this.timing = timing;
	}

	//POJO end	

	/* fields */
	private ItemStructure description;
	private DvParsable timing;
	private String actionArchetypeId; 

	/* static fields */
	public static final String DESCRIPTION = "description";	
}

/*
 *  ***** BEGIN LICENSE BLOCK *****
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  1.1 (the 'License'); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an 'AS IS' basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *  for the specific language governing rights and limitations under the
 *  License.
 *
 *  The Original Code is Activity.java
 *
 *  The Initial Developer of the Original Code is Yin Su Lim.
 *  Portions created by the Initial Developer are Copyright (C) 2003-2007
 *  the Initial Developer. All Rights Reserved.
 *
 *  Contributor(s): Rong Chen
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */