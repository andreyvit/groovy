/*
 * $Id$
 *
 * Copyright (c) 2004 The Codehaus - http://groovy.codehaus.org
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import com.intellij.openapi.project.Project;

import org.jmock.cglib.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class GroovyJProjectComponentTest extends MockObjectTestCase {

    private final Mock mockProject = new Mock(Project.class);
    private final Mock mockEditorAPIFactory = new Mock(EditorAPIFactory.class);
    private final Mock mockEditorAPI = new Mock(EditorAPI.class);
    private Project projectMock = (Project) mockProject.proxy();
    private GroovyJProjectComponent projectComponent;

    protected void setUp() throws Exception {
        super.setUp();

        assertNull(GroovyJProjectComponent.getInstance(projectMock));
        projectComponent = new GroovyJProjectComponent(projectMock, (EditorAPIFactory) mockEditorAPIFactory.proxy());
        assertSame(projectComponent, GroovyJProjectComponent.getInstance(projectMock));

        mockEditorAPIFactory.stubs().method("getEditorAPI").withAnyArguments().will(returnValue(mockEditorAPI.proxy()));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        GroovyJProjectComponent.setInstance(projectMock, null);
    }

    public void testHasTheConstructorRequiredByIntellijIdea() throws NoSuchMethodException {
        Constructor constructor = GroovyJProjectComponent.class.getConstructor(new Class[]{Project.class});
        assertTrue(Modifier.isPublic(constructor.getModifiers()));

        GroovyJProjectComponent groovyJProjectComponent = new GroovyJProjectComponent(projectMock);
        assertNotSame(projectComponent, GroovyJProjectComponent.getInstance(projectMock));
        assertSame(groovyJProjectComponent, GroovyJProjectComponent.getInstance(projectMock));
    }

    public void testInitialisesEditorApiAndGroovyControllerReferencesWhenTheProjectIsOpened() {
        assertNull("reference to EditorAPI should not have been initialised", projectComponent.getEditorAPI());
        assertNull("reference to GroovyController should not have been initialised", projectComponent.getGroovyController());

        projectComponent.projectOpened();

        assertNotNull("reference to EditorAPI should have been initialised", projectComponent.getEditorAPI());
        assertNotNull("reference to GroovyController should have been initialised", projectComponent.getGroovyController());
    }

    public void testRemovesReferencesToEditorApiAndGroovyControllerWhenTheProjectIsClosed() {
        projectComponent.projectOpened();
        assertNotNull("reference to EditorAPI should have been initialised", projectComponent.getEditorAPI());
        assertNotNull("reference to GroovyController should have been initialised", projectComponent.getGroovyController());

        projectComponent.projectClosed();
        assertNull("reference to EditorAPI should have been removed", projectComponent.getEditorAPI());
        assertNull("reference to GroovyController should have been removed", projectComponent.getGroovyController());
    }

    public void testDoesNothingWhenInitialisedByIdea() {
        projectComponent.initComponent();
        assertTrue(true);
    }

    public void testDoesNothingWhenDisposedByIdea() {
        projectComponent.disposeComponent();
        assertTrue(true);
    }

    public void testHasAComponentName() {
        assertEquals("groovyj.project.plugin", projectComponent.getComponentName());
    }
}
