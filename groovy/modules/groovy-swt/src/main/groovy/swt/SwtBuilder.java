/*
 * Created on Feb 15, 2004
 *  
 */
package groovy.swt;

import groovy.lang.Closure;
import groovy.swt.factory.AwtSwtFactory;
import groovy.swt.factory.Fontfactory;
import groovy.swt.factory.FormFactory;
import groovy.swt.factory.FormLayoutDataFactory;
import groovy.swt.factory.ImageFactory;
import groovy.swt.factory.LayoutDataFactory;
import groovy.swt.factory.LayoutFactory;
import groovy.swt.factory.ListenerFactory;
import groovy.swt.factory.SwtFactory;
import groovy.swt.factory.TrayFactory;
import groovy.swt.factory.WidgetFactory;
import groovy.util.BuilderSupport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster </a>
 * @version $Revision$
 */
public class SwtBuilder extends BuilderSupport {

    private Map factories = new HashMap();

    private Logger log = Logger.getLogger(getClass().getName());

    public SwtBuilder() {
        registerWidgets();
    }

    /*
     * @see groovy.util.BuilderSupport#createNode(java.lang.Object)
     */
    protected Object createNode(Object name) {
        return createNode(name, Collections.EMPTY_MAP);
    }

    /* 
     * @see groovy.util.BuilderSupport#createNode(java.lang.Object, java.util.Map, java.lang.Object)
     */
    protected Object createNode(Object name, Map attributes, Object value) {
        return createNode(name, attributes);
    }

    /*
     * @see groovy.util.BuilderSupport#createNode(java.lang.Object,
     *      java.util.Map)
     */
    protected Object createNode(Object name, Map attributes) {

        Closure closure = (Closure) attributes.get("closure");

        Object widget = createWidget(name, attributes, getCurrent());

        if (widget != null) {
            if (widget instanceof ClosureSupport) {
                if (closure != null) {
                    ClosureSupport closureSupport = (ClosureSupport) widget;
                    closureSupport.setClosure(closure);
                }
            }
        }

        return widget;
    }

    /**
     * @param name
     * @param attributes
     * @return
     */
    protected Object createWidget(Object name, Map attributes, Object current) {
        if (name.equals("doCall")) {
            return current;
        }

        Object widget = null;
        SwtFactory factory = (SwtFactory) factories.get(name);
        if (factory != null) {
            try {
                widget = factory.newInstance(attributes, current);
            } catch (Exception e) {
                log.log(Level.WARNING, "Node " + name + " returned : " + e.toString());
            }
        } else {
            log.log(Level.WARNING, "Could not find match for name: " + name);
        }
        return widget;
    }

    /*
     * @see groovy.util.BuilderSupport#createNode(java.lang.Object,
     *      java.lang.Object)
     */
    protected Object createNode(Object name, Object parent) {
        return createWidget(name, Collections.EMPTY_MAP, parent);
    }

    protected void registerBeanFactory(String name, final Class beanClass) {
        registerFactory(name, new WidgetFactory(beanClass));
    }

    protected void registerBeanFactory(String name, final Class beanClass, final int style) {
        registerFactory(name, new WidgetFactory(beanClass, style));
    }

    protected void registerFactory(String name, SwtFactory factory) {
        factories.put(name, factory);
    }

    protected void registerWidgets() {

        // widgets
        registerFactory("awtFrame", new AwtSwtFactory());
        registerBeanFactory("button", Button.class, SWT.BORDER | SWT.PUSH | SWT.CENTER);
        registerBeanFactory("canvas", Canvas.class);
        registerBeanFactory("caret", Caret.class);
        registerBeanFactory("combo", Combo.class, SWT.DROP_DOWN);
        registerBeanFactory("composite", Composite.class);
        registerBeanFactory("scrolledComposite", ScrolledComposite.class, SWT.H_SCROLL
                | SWT.V_SCROLL);
        registerBeanFactory("coolBar", CoolBar.class, SWT.VERTICAL);
        registerBeanFactory("coolItem", CoolItem.class);
        registerBeanFactory("decorations", Decorations.class);
        registerFactory("font", new Fontfactory());
        registerBeanFactory("group", Group.class);
        registerBeanFactory("label", Label.class, SWT.HORIZONTAL | SWT.SHADOW_IN);
        registerBeanFactory("list", List.class);
		registerBeanFactory("link", Link.class);
        registerBeanFactory("menu", Menu.class, SWT.DEFAULT);
        //        registerMenuTag("menuBar", SWT.BAR);

        registerBeanFactory("menuSeparator", MenuItem.class, SWT.SEPARATOR);
        registerBeanFactory("menuItem", MenuItem.class);
        registerBeanFactory("messageBox", MessageBox.class);
        registerBeanFactory("progressBar", ProgressBar.class, SWT.HORIZONTAL);
        registerBeanFactory("sash", Sash.class);
        registerBeanFactory("scale", Scale.class);
        registerBeanFactory("shell", Shell.class, SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX
                | SWT.RESIZE | SWT.TITLE);
        registerBeanFactory("slider", Slider.class);
        registerBeanFactory("tabFolder", TabFolder.class);
        registerBeanFactory("tabItem", TabItem.class);
        registerBeanFactory("table", Table.class, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        registerBeanFactory("tableColumn", TableColumn.class);
        registerBeanFactory("tableItem", TableItem.class);
        registerBeanFactory("text", Text.class);
        registerBeanFactory("toolBar", ToolBar.class, SWT.VERTICAL);
        registerBeanFactory("toolItem", ToolItem.class);
        registerBeanFactory("tracker", Tracker.class);
        registerFactory("tray", new TrayFactory());
        registerBeanFactory("trayItem", TrayItem.class);
        registerBeanFactory("tree", Tree.class, SWT.MULTI);
        registerBeanFactory("treeItem", TreeItem.class);

        // custom widgets
        registerBeanFactory("cBanner", CBanner.class);
        registerBeanFactory("cCombo", CCombo.class);
        registerBeanFactory("cLabel", CLabel.class);
        registerBeanFactory("cTabFolder", CTabFolder.class);
        registerBeanFactory("cTabItem", CTabItem.class);
        registerBeanFactory("sashForm", SashForm.class);
        registerBeanFactory("tableTree", TableTree.class);
        registerBeanFactory("tableTreeItem", TableTreeItem.class);

        // layouts
        registerFactory("fillLayout", new LayoutFactory(FillLayout.class));
        registerFactory("gridLayout", new LayoutFactory(GridLayout.class));
        registerFactory("rowLayout", new LayoutFactory(RowLayout.class));
        registerFactory("formLayout", new LayoutFactory(FormLayout.class));

        // layout data objects
        registerFactory("gridData", new LayoutDataFactory(GridData.class));
        registerFactory("rowData", new LayoutDataFactory(RowData.class));
        registerFactory("formData", new FormLayoutDataFactory());

        // dialogs
        registerBeanFactory("colorDialog", ColorDialog.class);
        registerBeanFactory("directoryDialog", DirectoryDialog.class);
        registerBeanFactory("fileDialog", FileDialog.class);
        registerBeanFactory("fontDialog", FontDialog.class);

        // events
        registerFactory("onEvent", new ListenerFactory(Listener.class));

        // other tags
        registerFactory("image", new ImageFactory());

        // browser tags
        registerBeanFactory("browser", Browser.class, SWT.NONE);
        registerFactory("locationListener", new ListenerFactory(LocationListener.class));
        registerFactory("progressListener", new ListenerFactory(ProgressListener.class));
        registerFactory("statusTextListener", new ListenerFactory(StatusTextListener.class));

        // forms api
        registerFactory("form", new FormFactory("form"));
        registerFactory("scrolledForm", new FormFactory("scrolledForm"));
        registerFactory("formButton", new FormFactory("formButton"));
        registerFactory("formColors", new FormFactory("formColors"));
        registerFactory("formComposite", new FormFactory("formComposite"));
        registerFactory("formCompositeSeparator", new FormFactory("formCompositeSeparator"));
        registerFactory("formExpandableComposite", new FormFactory("formButton"));
        registerFactory("formText", new FormFactory("formText"));
        registerFactory("formHyperlink", new FormFactory("formHyperlink"));
        registerFactory("formImageHyperlink", new FormFactory("formImageHyperlink"));
        registerFactory("formLabel", new FormFactory("formLabel"));
        registerFactory("formPageBook", new FormFactory("formPageBook"));
        registerFactory("formPageBookPage", new FormFactory("formPageBookPage"));
        registerFactory("formSection", new FormFactory("formSection"));
        registerFactory("formSeparator", new FormFactory("formSeparator"));
        registerFactory("formTable", new FormFactory("formTable"));
        registerFactory("formToolkit", new FormFactory("formToolkit"));
        registerFactory("formFormattedText", new FormFactory("formFormattedText"));
        registerFactory("formTree", new FormFactory("formTree"));

        // forms layout
        registerFactory("tableWrapLayout", new LayoutFactory(TableWrapLayout.class));
        registerFactory("tableWrapData", new LayoutDataFactory(TableWrapData.class));
        registerFactory("columnLayout", new LayoutFactory(ColumnLayout.class));
        registerFactory("columnLayoutData", new LayoutDataFactory(ColumnLayoutData.class));

        // forms listeners
        registerFactory("hyperlinkListener", new ListenerFactory(IHyperlinkListener.class));
        registerFactory("expansionListener", new ListenerFactory(IExpansionListener.class));

        // none eclipse widgets
        // registerBeanFactory("tDateText", TDateText.class);
        // registerBeanFactory("tCalendar", TCalendar.class);
        // registerBeanFactory("textEditor", TextEditor.class);
    }

    /*
     * @see groovy.util.BuilderSupport#setParent(java.lang.Object,
     *      java.lang.Object)
     */
    protected void setParent(Object parent, Object child) {

        //  TODO implement this
        // 
        //  if (parent instanceof ScrolledComposite && widget instanceof Control)
        // {
        //  	ScrolledComposite scrolledComposite = (ScrolledComposite) parent;
        //      scrolledComposite.setContent((Control) widget);
        //  }

    }

    /*
     * override to make public
     * 
     * @see groovy.util.BuilderSupport#setCurrent(java.lang.Object)
     */
    public void setCurrent(Object current) {
        super.setCurrent(current);
    }

    /* 
     * override to make public
     * 
     * @see groovy.util.BuilderSupport#getCurrent()
     */
    public Object getCurrent() {
        return super.getCurrent();
    }

}