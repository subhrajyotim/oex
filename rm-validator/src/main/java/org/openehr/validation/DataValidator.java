/*
 * component:   "openEHR Reference Java Implementation"
 * description: "Class DataValidator"
 * keywords:    "validator"
 *
 * author:      "Rong Chen <rong.acode@gmail.com>"
 * support:     "openEHR Java Project <ref_impl_java@openehr.org>"
 * copyright:   "Copyright (c) 2008 Cambio Healthcare Systems, Sweden"
 * license:     "See notice at bottom of class"
 *
 * file:        "$URL$"
 * revision:    "$LastChangedRevision$"
 * last_change: "$LastChangedDate$"
 */
package org.openehr.validation;

import java.util.List;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.validation.exceptions.GenericValidationException;

/**
 * openeEHR referece model validator interface
 *
 * @author Rong Chen
 */
public interface DataValidator {

    List<ValidationError> validate(Locatable data, Archetype archetype)
            throws GenericValidationException;

    List<ValidationError> validate(Locatable data)
            throws GenericValidationException;
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
 *  The Original Code is DataValidator.java
 *
 *  The Initial Developer of the Original Code is Rong Chen.
 *  Portions created by the Initial Developer are Copyright (C) 2008
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