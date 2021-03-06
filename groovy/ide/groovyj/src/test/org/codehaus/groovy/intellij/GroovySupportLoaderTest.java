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

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.options.colors.ColorSettingsPages;
import org.codehaus.groovy.intellij.language.editor.GroovyColourSettingsPage;
import org.intellij.openapi.testing.MockApplication;
import org.intellij.openapi.testing.MockApplicationManager;
import org.jmock.Mock;

import java.util.List;

public class GroovySupportLoaderTest extends GroovyjTestCase {

    private final Mock mockGroovyLibraryManager = mock(GroovyLibraryManager.class);

    protected void tearDown() {
        System.getProperties().remove("groovy.jsr");
    }

    public void testDefinesAComponentName() {
        assertEquals("component name", "groovy.support.loader", new GroovySupportLoader().getComponentName());
    }

    public void testRegistersTheGroovyFileTypeAndColourSettingsPageWhenInitialisedByIntellijIdea() {
        setExpectationsForInitialisationByIntellijIdea();

        GroovySupportLoader groovySupportLoader = new GroovySupportLoader((GroovyLibraryManager) mockGroovyLibraryManager.proxy());
        groovySupportLoader.initComponent();

        MockApplicationManager.getMockApplication().removeComponent(FileTypeManager.class);
    }

    public void testEnablesGroovyJsrWhenInitialisedByIntellijIdea() {
        setExpectationsForInitialisationByIntellijIdea();
        assertEquals("groovy.jsr", null, System.getProperty("groovy.jsr"));

        GroovySupportLoader groovySupportLoader = new GroovySupportLoader((GroovyLibraryManager) mockGroovyLibraryManager.proxy());
        groovySupportLoader.initComponent();
        assertEquals("groovy.jsr", "true", System.getProperty("groovy.jsr"));

        MockApplicationManager.getMockApplication().removeComponent(FileTypeManager.class);
    }

    private void setExpectationsForInitialisationByIntellijIdea() {
        Mock mockFileTypeManager = mock(FileTypeManager.class);
        mockFileTypeManager.expects(once()).method("registerFileType")
//                .with(same(GroovySupportLoader.GROOVY), eq(new String[] { "groovy", "gvy", "gy", "gsh" }));
                .with(same(GroovySupportLoader.GROOVY), isA(List.class));

        Mock mockColorSettingsPages = mock(ColorSettingsPages.class);
        mockColorSettingsPages.expects(once()).method("registerPage").with(isA(GroovyColourSettingsPage.class));

        MockApplication application = MockApplicationManager.getMockApplication();
        application.registerComponent(FileTypeManager.class, mockFileTypeManager.proxy());
        application.registerComponent(ColorSettingsPages.class, mockColorSettingsPages.proxy());

        mockGroovyLibraryManager.expects(once()).method("installOrUpgradeGroovyRuntimeAsAGlobalLibraryIfNecessary");
    }

    public void testRemovesTheGroovyJsrSystemPropertyWhenDisposedByIntellijIdea() {
        new GroovySupportLoader().disposeComponent();
        assertEquals("groovy.jsr", null, System.getProperty("groovy.jsr"));
    }
}
