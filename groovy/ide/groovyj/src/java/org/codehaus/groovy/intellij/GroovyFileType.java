/*
 * $Id$
 *
 * Copyright (c) 2005 The Codehaus - http://groovy.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */


package org.codehaus.groovy.intellij;

import javax.swing.Icon;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.openapi.fileTypes.FileTypeSupportCapabilities;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.IconLoader;

public class GroovyFileType extends LanguageFileType {

    public GroovyFileType() {
        super(null);
    }

    public String getName() {
        return "Groovy";
    }

    public String getDescription() {
        return "Groovy Scripts and Classes";
    }

    public String getDefaultExtension() {
        return "groovy";
    }

    public Icon getIcon() {
        return IconLoader.getIcon("/icons/groovy_fileType.png");
    }

    public FileTypeSupportCapabilities getSupportCapabilities() {
        return new FileTypeSupportCapabilities() {
            public boolean hasCompletion() {
                return true;
            }

            public boolean hasValidation() {
                return true;
            }

            public boolean hasFindUsages() {
                return true;
            }

            public boolean hasNavigation() {
                return true;
            }

            public boolean hasRename() {
                return true;
            }
        };
    }

    public StructureViewModel getStructureViewModel(VirtualFile file, Project project) {
        return null;
    }
}