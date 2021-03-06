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


package org.codehaus.groovy.intellij;

import org.intellij.openapi.testing.MockApplication;
import org.intellij.openapi.testing.MockApplicationManager;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.impl.libraries.LibraryTablesRegistrarImpl;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;

import org.jmock.Mock;

import org.codehaus.groovy.intellij.compiler.GroovyCompiler;

public class GroovyJProjectComponentTest extends GroovyjTestCase {

    private final Mock mockCompilerManager = mock(CompilerManager.class);
    private final Mock mockModuleManager = mock(ModuleManager.class);
    private final Mock mockProject = mock(Project.class);
    private final Project selectedProject = (Project) mockProject.proxy();

    private GroovyJProjectComponent projectComponent;

    protected void setUp() {
        assertNull(GroovyJProjectComponent.getInstance(selectedProject));

        Mock mockEditorAPIFactory = mock(EditorAPIFactory.class);
        projectComponent = new GroovyJProjectComponent(selectedProject, (EditorAPIFactory) mockEditorAPIFactory.proxy()) {
            protected String getPluginVersion() {
                return "1.0." + nextPositiveRandomInt();
            }
        };
        assertSame(projectComponent, GroovyJProjectComponent.getInstance(selectedProject));

        Mock mockEditorAPI = mock(EditorAPI.class);
        mockEditorAPIFactory.stubs().method("createEditorAPI").withAnyArguments().will(returnValue(mockEditorAPI.proxy()));

        mockProject.stubs().method("getComponent").with(same(CompilerManager.class)).will(returnValue(mockCompilerManager.proxy()));
        mockProject.stubs().method("getComponent").with(same(ModuleManager.class)).will(returnValue(mockModuleManager.proxy()));
    }

    protected void tearDown() {
        GroovyJProjectComponent.setInstance(selectedProject, null);
    }

    public void testInitialisesTheGroovyCompilerWithEditorApiWhenTheProjectIsOpened() {
        assertAllProjectLevelDependenciesAreRemoved();
        setExpectationsForRetrievingTheGroovyLibrary();

        expectAdditionOfCompilationSupport();
        expectAdditionOfModuleListener();

        projectComponent.projectOpened();
        assertAllProjectLevelDependenciesHaveBeenInitialised();
    }

    public void testRemovesReferencesToEditorApiAndGroovyCompilerWhenTheProjectIsClosed() {
        expectAdditionOfCompilationSupport();
        expectAdditionOfModuleListener();
        setExpectationsForRetrievingTheGroovyLibrary();

        projectComponent.projectOpened();
        assertAllProjectLevelDependenciesHaveBeenInitialised();

        expectRemovalOfCompilationSupport();
        expectRemovalOfModuleListener();

        projectComponent.projectClosed();
        assertAllProjectLevelDependenciesAreRemoved();
    }

    private void expectAdditionOfCompilationSupport() {
        mockCompilerManager.expects(once()).method("addCompiler").with(isA(GroovyCompiler.class));
        mockCompilerManager.expects(once()).method("addCompilableFileType").with(same(groovyFileType()));
    }

    private void expectRemovalOfCompilationSupport() {
        mockCompilerManager.expects(once()).method("removeCompiler").with(isA(GroovyCompiler.class));
        mockCompilerManager.expects(once()).method("removeCompilableFileType").with(same(groovyFileType()));
    }

    private FileType groovyFileType() {
        return GroovySupportLoader.GROOVY;
    }

    private void expectAdditionOfModuleListener() {
        mockModuleManager.expects(once()).method("addModuleListener").with(isA(GroovyLibraryModuleListener.class));
    }

    private void expectRemovalOfModuleListener() {
        mockModuleManager.expects(once()).method("removeModuleListener").with(isA(GroovyLibraryModuleListener.class));
    }

    private void setExpectationsForRetrievingTheGroovyLibrary() {
        Mock mockLibraryTable = mock(LibraryTable.class);

        MockApplication application = MockApplicationManager.getMockApplication();
        application.registerComponent(LibraryTablesRegistrar.class, new LibraryTablesRegistrarImpl());
        application.registerComponent(LibraryTable.class, mockLibraryTable.proxy());

        mockLibraryTable.stubs().method("getLibraryByName").with(startsWith("Groovy from GroovyJ "));
    }

    private void assertAllProjectLevelDependenciesHaveBeenInitialised() {
        assertNotNull("reference to EditorAPI should have been initialised", projectComponent.editorApi);
        assertNotNull("reference to GroovyLibraryModuleListener should have been initialised", projectComponent.groovyLibraryModuleListener);
    }

    private void assertAllProjectLevelDependenciesAreRemoved() {
        assertNull("reference to EditorAPI should be null", projectComponent.editorApi);
        assertNull("reference to GroovyLibraryModuleListener should be null", projectComponent.groovyLibraryModuleListener);
    }

    public void testDoesNothingWhenInitialisedByIdea() {
        projectComponent.initComponent();
    }

    public void testDoesNothingWhenDisposedByIdea() {
        projectComponent.disposeComponent();
    }

    public void testHasAComponentName() {
        assertEquals("groovyj.project.plugin", projectComponent.getComponentName());
    }
}
