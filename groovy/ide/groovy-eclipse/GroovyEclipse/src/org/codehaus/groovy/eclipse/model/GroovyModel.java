/*
 * Created on Dec 18, 2003
 *  
 */
package org.codehaus.groovy.eclipse.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.eclipse.GroovyPlugin;
import org.codehaus.groovy.eclipse.builder.GroovyNature;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author zohar melamed
 * 
 */
public class GroovyModel {
	private static GroovyModel dasModel;

	private Map projects = new HashMap();

	/**
	 * List of global build listeners, which receive notification for all build
	 * changes.
	 */
	private List buildListeners = new ArrayList();

	/**
	 * The global listener added to each new GroovyProject that forwards
	 * notifications to all build listeners added with the
	 * {@link GroovyModel#addBuildListener(GroovyBuildListener)} method.
	 */
	private GroovyBuildListener buildListener = new GroovyBuildListener() {
		public void fileBuilt(IFile file) {
			for (Iterator iter = buildListeners.iterator(); iter.hasNext();) {
				GroovyBuildListener listener = (GroovyBuildListener) iter.next();
				listener.fileBuilt(file);
			}
		}
	};

	/**
	 * @param dasModel
	 */
	public static GroovyModel getModel() {
		if (dasModel == null) {
			dasModel = new GroovyModel();
		}
		return dasModel;
	}

	/**
	 * @param javaProject
	 */
	public String[] findAllRunnableClasses(final IJavaProject javaProject) {
		return getGroovyProject(javaProject).model().findAllRunnableClasses();
	}

	public GroovyProjectModel getProjectModel(final IJavaProject javaProject) {
		return getGroovyProject(javaProject).model();
	}

	public void runGroovyMain(String projectName, String className, String[] args) throws CoreException {
		for (Iterator iter = projects.values().iterator(); iter.hasNext();) {
			GroovyProject groovyProject = (GroovyProject) iter.next();
			if (groovyProject.getJavaProject().getProject().getName().equals(projectName)) {
				groovyProject.runGroovyMain(className, args);
				return;
			}
		}
		// we only get here if we fail to match a project to the name
		GroovyPlugin.getPlugin().logException(
				"can not find project :" + projectName + " while trying to run :" + className, new Exception());
	}

	public void runGroovyMain(IFile file) {
		try {
			runGroovyMain(file, new String[] {});
		} catch (CoreException e) {
			GroovyPlugin.getPlugin().logException("error trying to run groovy file " + file.getName(), e);
		}
	}

	public void runGroovyMain(IFile file, String[] args) throws CoreException {
		GroovyProject gp = getGroovyProject(JavaCore.create(file.getProject()));
		gp.runGroovyMain(file, args);
	}

	/**
	 * @param javaProject
	 */
	public void buildGroovyContent(IJavaProject javaProject, IProgressMonitor monitor, int kind, ChangeSet changeSet,
			boolean generateClassFiles) {
		GroovyProject gp = getGroovyProject(javaProject);
		gp.buildGroovyContent(monitor, kind, changeSet, generateClassFiles);
	}

	public void buildGroovyContent(IJavaProject javaProject, IProgressMonitor monitor, int kind, ChangeSet changeSet) {
		GroovyProject gp = getGroovyProject(javaProject);
		gp.buildGroovyContent(monitor, kind, changeSet);
	}

	/**
	 * @param javaProject
	 * @return
	 */
	private GroovyProject getGroovyProject(IJavaProject javaProject) {
		GroovyProject gp = (GroovyProject) projects.get(javaProject);
		if (gp == null) {
			gp = new GroovyProject(javaProject);
			gp.addBuildListener(buildListener);
			projects.put(javaProject, gp);
		}
		return gp;
	}

	public void addBuildListener(GroovyBuildListener listener) {
		GroovyPlugin.trace("addingBuildListener:" + listener.toString());
		buildListeners.add(listener);
	}

	public void removeBuildListener(GroovyBuildListener listener) {
		// Thread safe remove.
		List newList = new ArrayList(buildListeners);
		newList.remove(listener);
		buildListeners = newList;
	}

	public List getModuleNodes(final IFile file) {
		return getGroovyProject(JavaCore.create(file.getProject())).model().getModuleNodes(file);
	}

	public void updateProjects(final IProgressMonitor progressMonitor) {
		final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();
		final IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		monitor.beginTask("Updating Groovy Projects", allProjects.length);
		for (int i = 0; i < allProjects.length; i++) {
			final IProject project = allProjects[i];
			try {
				if (!project.isAccessible() || !project.hasNature(GroovyNature.GROOVY_NATURE)) {
					monitor.worked(1);
					continue;
				}
			} catch (final CoreException e) {
				GroovyPlugin.getPlugin().logException(
						"Error checking for groovy nature for project: " + project.getName() + ", " + e, e);
				monitor.worked(1);
				continue;
			}
			final GroovyProject groovyProject = getGroovyProject(JavaCore.create(project));
			if (groovyProject == null) {
				GroovyPlugin.getPlugin().logWarning(
						"Couldn't generate Groovy Project from " + project.getName()
								+ " even though the project definition contains the Groovy Nature");
				monitor.worked(1);
				continue;
			}
			groovyProject.rebuildAll(new SubProgressMonitor(monitor, 1));
		}
		monitor.done();
	}

	/**
	 * @param resource
	 * @throws CoreException
	 */
	public void groovyFileAdded(IResource resource) throws CoreException {
		GroovyProject project = getGroovyProject(JavaCore.create(resource.getProject()));
		project.groovyFileAdded(resource);
	}

	/**
	 * @param project
	 * @return
	 */
	public GroovyProject getProject(IProject project) {
		return getGroovyProject(JavaCore.create(project));
	}

	public IFile getIFileForSrcFile(String srcFileName) {
		IFile f = null;
		for (Iterator iter = projects.values().iterator(); iter.hasNext();) {
			GroovyProject groovyProject = (GroovyProject) iter.next();
			IPath[] srcDirList = groovyProject.getSourceDirectories();
			for (int i = 0; i < srcDirList.length; i++) {
				IPath srcPath = srcDirList[i];
				String absPath = srcPath.makeAbsolute() + "/" + srcFileName;
				IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(absPath));
				if (files.length > 0 && files[0].exists()) {
					f = files[0];
					System.out.println("file found:" + absPath);
				} else { // files outside of the workspace
					System.out.println("file not found=" + absPath);
				}
			}
		}
		return f;
	}
}