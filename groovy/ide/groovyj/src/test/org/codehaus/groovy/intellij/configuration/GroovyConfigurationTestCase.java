/*
 * $Id$
 *
 * Copyright (c) 2005-2006 The Codehaus - http://groovy.codehaus.org
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


package org.codehaus.groovy.intellij.configuration;

import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.WebModuleType;
import com.intellij.openapi.project.Project;

import org.codehaus.groovy.intellij.EditorAPI;
import org.codehaus.groovy.intellij.GroovyjTestCase;

class GroovyConfigurationTestCase extends GroovyjTestCase {

    protected GroovyRunConfiguration createRunConfiguration(EditorAPI editorApi, String vmParameters, String scriptPath,
                                                            String scriptParameters, String workingDirectoryPath) {
        Module[] projectModules = new Module[] {
                module().isA(new JavaModuleType()).withProjectJdk().build(),
                module().isA(new WebModuleType()).withProjectJdk().build()
        };
        Project stubbedProject = project().withProjectFile().withModules(projectModules).build();
        GroovyConfigurationFactory configurationFactory = new GroovyConfigurationFactory(null, null, null);
        GroovyRunConfiguration runConfiguration = new GroovyRunConfiguration(null, stubbedProject, configurationFactory, editorApi);
        runConfiguration.setVmParameters(vmParameters);
        runConfiguration.setScriptPath(scriptPath);
        runConfiguration.setScriptParameters(scriptParameters);
        runConfiguration.setModule(ModuleManager.getInstance(stubbedProject).getSortedModules()[0]);
        runConfiguration.setWorkingDirectoryPath(workingDirectoryPath);
        return runConfiguration;
    }
}
