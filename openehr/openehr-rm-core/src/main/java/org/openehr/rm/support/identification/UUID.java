/*
 * component:   "openEHR Reference Implementation"
 * description: "Class UUID"
 * keywords:    "common"
 *
 * author:      "Rong Chen <rong@acode.se>"
 * support:     "Acode HB <support@acode.se>"
 * copyright:   "Copyright (c) 2004 Acode HB, Sweden"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL: http://svn.openehr.org/ref_impl_java/BRANCHES/RM-1.0-update/libraries/src/java/org/openehr/rm/support/identification/UUID.java $"
 * revision:    "$LastChangedRevision: 2 $"
 * last_change: "$LastChangedDate: 2005-10-12 23:20:08 +0200 (Wed, 12 Oct 2005) $"
 */
package org.openehr.rm.support.identification;

import org.openehr.rm.Attribute;
import org.openehr.rm.FullConstructor;

/**
 * Purpose Model of the DCE Universal Unique Identifier or UUID which
 * takes the form of hexadecimal integers separated by hyphens,
 * following the pattern 8-4-4-4-12 as defined by the Open Group,
 * CDE 1.1 Remote Procedure Call specification,
 *
 * @author Rong Chen
 * @version 1.0
 */
public class UUID extends UID {

    public UUID() {}
	
	/**
	 * Simple UUID pattern
	 */
	public static final String SIMPLE_UUID_PATTERN = "([0-9a-fA-F])+(-([0-9a-fA-F])+)*";
	
    /**
     * Constructs an UUID
     *
     * @param value
     */
	@FullConstructor
    public UUID(@Attribute(name = "value", required = true)String value) {
        super(value);
        // kind of validation
        java.util.UUID.fromString(value);
    }    
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
 *  The Original Code is UUID.java
 *
 *  The Initial Developer of the Original Code is Rong Chen.
 *  Portions created by the Initial Developer are Copyright (C) 2003-2004
 *  the Initial Developer. All Rights Reserved.
 *
 *  Contributor(s):
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */