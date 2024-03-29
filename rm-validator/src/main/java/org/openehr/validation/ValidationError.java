/*
 * component:   "openEHR Reference Java Implementation"
 * description: "Class ValidationError"
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

import org.openehr.rm.support.identification.ArchetypeID;

public class ValidationError {

    /**
     * Creates a validation error
     * 
     * @param archetype
     * @param runtimePath
     * @param archetypePath
     * @param errorType
     */
    public ValidationError(String archetype, String runtimePath, 
            String archetypePath, ErrorType errorType, String description) {
        this.archetype = archetype;
        this.runtimePath = runtimePath;
        this.archetypePath = archetypePath;
        this.errorType = errorType;
        this.description = description;
    }
    
    /**
     * Creates a validation error
     *
     * @param archetype
     * @param runtimePath
     * @param archetypePath
     * @param errorType
     */
    public ValidationError(ArchetypeID archetypeID, String runtimePath, String archetypePath,
            ErrorType errorType, String description) {
        this.archetype = archetypeID.getValue();
        this.runtimePath = runtimePath;
        this.archetypePath = archetypePath;
        this.errorType = errorType;
        this.description = description;
    }

    public String getRuntimePath() {
        return this.runtimePath;
    }

    public String getArchetypePath() {
        return this.archetypePath;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public String getArchetype() {
        return archetype;
    }
    
    private String archetype;
    private String runtimePath;
    private String archetypePath;
    private ErrorType errorType;
    private String description;

    public String getDescription() {
        return description;
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
 *  The Original Code is ValidationError.java
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
