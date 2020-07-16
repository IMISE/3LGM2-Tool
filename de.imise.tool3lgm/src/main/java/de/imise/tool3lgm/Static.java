/**
 *
 */
package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.LAYOUT_CHANGED;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;

import java.awt.Component;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.collections4.IterableUtils;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmChangeListener.Tool3lgmChangeType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.gui.MainFrame;
import de.imise.tool3lgm.gui.menu.RegularContextGenerator;
import de.imise.tool3lgm.gui.menu.TemplateContextGenerator;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentListener;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;
import de.imise.util.OperatingSystem;
import de.imise.util.PluginUtils;
import de.imise.util.Sys;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.swing.dialog.OutputDialog;
import de.imise.util.swing.dialog.ProgressDialog;

/**
 * Klasse, die häufig gebrauchte statische Informationen und Zugriffsfunktionen des Tools liefert.
 *
 * @author AXS
 */
public class Static {

    public static final boolean IS_MAC_OS = OperatingSystem.isMacOs();

    static Tool3lgm tool;

    /** Progress-Dialog */
    private static ProgressDialog progressDialog;

    /** Holds the current context and generates context menus */
    public static final RegularContextGenerator contextGenerator = new RegularContextGenerator();

    /** Holds the current context and generates context menus for the template views */
    public static final TemplateContextGenerator templateContextGenerator = new TemplateContextGenerator();

    /** Gibt die gerade laufende Instanz von 3lgm wieder */
    public static Tool3lgm getTool() {
        return tool;
    }

    /** Beendet die Anwendung */
    public static void close() {
        if (tool == null) {
            System.exit(0);
        }
        tool.close();
    }

    /**
     * @param changeType
     * @param source
     */
    public static void distribute(final Tool3lgmChangeType changeType, final GraphDocument source) {
        if (tool != null) {
            tool.distribute(changeType, source);
        }
    }

    /**
     * Liefert den MainFrame des Tools
     *
     * @return mainFrame
     */
    public static MainFrame getMainFrame() {
        return tool == null ? null : tool.getMainFrame();
    }

    /**
     * Liefert den MainFrame des Tools
     *
     * @return mainFrame
     */
    public static ViewPaneFrameComponentListener getMainFrameDesktopPane() {
        MainFrame mainFrame = getMainFrame();
        return mainFrame == null ? null : mainFrame.getMainFrameDesktopPane();
    }

    /** Liefert den MetaModelContext des aktuell selektierten Modells */
    public static MetaModelContext getSelectedMetaModelContext() {
        GDCollection selectedGDCollection = getSelectedGDCollection();
        if (selectedGDCollection == null) {
            return Tool3lgmMetaModelContext.DUMMY_META_MODEL_CONTEXT;
        }
        return selectedGDCollection.getMetaModelContext();
    }

    /** Liefert das MetaModel des aktuell selektierten Modells */
    public static MetaModel getSelectedMetaModel() {
        MetaModelContext selectedMetaModelContext = getSelectedMetaModelContext();
        if (selectedMetaModelContext == null) {
            return Tool3lgmMetaModelContext.DUMMY_META_MODEL;
        }
        return selectedMetaModelContext.getMetaModel();
    }

    /** @return <code>true</code> if the given MetaModelContext is equals to the DUMMY_META_MODEL_CONTEXT */
    public static boolean isDummyMetaModelContext(final MetaModelContext metaModelContext) {
        return Tool3lgmMetaModelContext.DUMMY_META_MODEL_CONTEXT.equals(metaModelContext);
    }

    /** Liefert das aktuelle selektierte Modell */
    public static GDCollection getSelectedGDCollection() {
        return tool == null ? null : tool.getSelectedGDCollection();
    }

    /** Liefert das Modell, das vor dem aktuell selektierten Modell selektiert war */
    public static GDCollection getPreSelectedGDCollection() {
        return tool == null ? null : tool.getPreSelectedGDCollection();
    }

    /** Gibt das momentan ausgewählte {@link GraphDocument} zurück */
    public static LGMGraphDocument getSelectedDoc() {
        return tool == null ? null : tool.getSelectedDoc();
    }

    /**
     * Setzt das ausgewählte Modell und holt bei Bedarf den dazugehörigen Frame nach vorne
     *
     * @param doc
     */
    public static void setSelectedDoc(final GraphDocument doc) {
        if (tool != null) {
            tool.setSelectedDoc(doc);
        }
    }

    /**
     * Searches all loaded a models and templates to find the main {@link GraphDocument}
     * or {@link Szenario} with the given hashString.
     *
     * @param hashString
     *            hashString of the GraphDocument
     * @return the {@link GraphDocument} of a model or a template with the given hashString
     *         or <code>null</code>
     */
    public static LGMGraphDocument getGraphDocument(final String hashString) {
        Iterable<GDCollection> allGDCollections = iterableCollections();
        TemplateLibrariesManager templateLibrariesManager = getTemplateLibrariesManager();
        if (templateLibrariesManager != null) {
            Iterable<GDCollection> allTemplates = templateLibrariesManager.iterableTemplates();
            allGDCollections = CollectionUtils.getCommonIterable(allGDCollections, allTemplates);
        }
        for (GDCollection gdcoll : allGDCollections) {
            LGMGraphDocument doc = gdcoll.getGraphDocumentCoded(hashString);
            if (doc != null) {
                return doc;
            }
        }
        return null;
    }

    /**
     * Liefert die geöffneten Modelle
     *
     * @return
     */
    public static Iterable<GDCollection> iterableCollections() {
        return tool == null ? IterableUtils.emptyIterable() : tool.iterableCollections();
    }

    /**
     * return GDCollection with the specified index in ArrayList collections
     *
     * @param index int with index of collection in ArrayList collection
     * @return null if index < 0 or index >= collections.size(); otherwise the GDCollection with specified index
     */
    public static GDCollection getCollection(final int index) {
        return tool == null ? null : tool.getCollection(index);
    }

    /**
     * Liefert die Anzahl der offenen Modelle
     *
     * @return
     */
    public static int getCollectionCount() {
        return tool == null ? -1 : tool.getCollectionCount();
    }

    /**
     * @param metaModelContext
     * @return all models with the given metamodel context
     */
    public static List<GDCollection> getCollections(final MetaModelContext metaModelContext) {
        List<GDCollection> collections = new ArrayList<>();
        if (tool != null) {
            for (GDCollection gdcoll : tool.iterableCollections()) {
                if (gdcoll.hasMetaModelContext(metaModelContext)) {
                    collections.add(gdcoll);
                }
            }
        }
        return collections;
    }

    /**
     * Shows the property dialog for the given element. If the elenents model
     * is a template model and this model is only shown in the template browser
     * (and is not selected) so the function tries to find the same element
     * in the current selected model. If found, the dilog of the element of the
     * selected model will be openend. If the element is not presen in the
     * selected model so the dialog of the original template element will be shown.
     *
     * @param ec
     */
    public static final void showPropertyDialog(final ElementContainer ec) {
        GraphDocument doc = ec.getGraphDocument();
        GraphDocument selectedDoc = getSelectedDoc();
        ModelElement me = ec.getElement();
        if (doc != selectedDoc) {
            ModelCategory modelCategory = doc.getModelCategory();
            if (modelCategory == ModelCategory.TEMPLATE) {
                String elementHash = ec.getHashString();
                ModelElement elementInSelectedDoc = selectedDoc.findElementCoded(elementHash);
                if (elementInSelectedDoc != null) {
                    doc = selectedDoc;
                    me = elementInSelectedDoc;
                }
            }
        }
        ElementPropertyDialog propertyDialog = me.getPropertyDialog();
        propertyDialog.showDialog();
    }

    /**
     * Shows the PropertyDialog of the last selected Element in the current selected
     */
    public static final void showPropertyDialogOfLastSelected() {
        LGMGraphDocument selectedDoc = getSelectedDoc();
        if (selectedDoc != null) {
            showPropertyDialogOfLastSelected(selectedDoc);
        }
    }

    /**
     * Shows the PropertyDialog of the last selected Element of the given {@link GraphDocument}.
     *
     * @doc
     */
    public static final void showPropertyDialogOfLastSelected(final GraphDocument doc) {
        ElementContainer lastSelected = doc.getLastSelected();
        if (lastSelected != null) {
            ModelElement me = lastSelected.getElement();
            me.showPropertyDialog();
        }
    }

    /**
     * return all frame components at desktop
     *
     * @return List<ViewPaneFrameComponent>
     */
    public static List<ViewPaneFrameComponent> getAllFrames() {
        return tool == null ? null : tool.getAllFrames();
    }

    /** Gibt das gerade aktivierte Frame zurück */
    public static ViewPaneFrameComponent getActiveFrame() {
        return tool == null ? null : tool.getActiveFrame();
    }

    /** Gibt zurück, ob interne Frames existieren */
    public static boolean isFramesExists() {
        return tool != null && !tool.getAllFrames().isEmpty();
    }

    /** Gibt zurück, ob ein aktiver, sichtbarer Grafik-Frame existiert */
    public static boolean isActiveFrameGraphFrame() {
        ViewPaneFrameComponent f = getActiveFrame();
        return f != null && f instanceof GraphViewPaneFrameComponent && f.getGraphDocument() instanceof Szenario && f.isVisible();
    }

    /** Gibt zurück, ob ein aktiver, sichtbarer Matrix-Frame existiert */
    public static boolean isActiveFrameMatrixFrame() {
        ViewPaneFrameComponent f = getActiveFrame();
        return f != null && f instanceof MatrixViewPaneFrameComponent && f.isVisible();
    }

    /**
     * @param doc
     * @return the ViewParameter of the szenario. If the szanrio has a frame then
     *         the view parameter of the frame will be retured otherwise the in the
     *         szenario stored view parameters are returned
     */
    public static final GraphViewParameter getGraphViewParameter(final Szenario szen) {
        MainFrame mainFrame = getMainFrame();
        return mainFrame == null ? szen.getGraphViewParameter() : mainFrame.getGraphViewParameter(szen);
    }

    // template library manager

    /**
     * @return the template library manager
     */
    public static TemplateLibrariesManager getTemplateLibrariesManager() {
        return tool == null ? null : tool.getTemplateLibrariesManager();
    }

    /**
     * @param doc
     */
    public static final void setActiveTemplate(final GraphDocument doc) {
        TemplateLibrariesManager templateLibrariesManager = getTemplateLibrariesManager();
        if (templateLibrariesManager != null) {
            templateLibrariesManager.setActiveTemplate(doc);
        }
    }

    /**
     * @param me
     * @return the elemenmt from a currently active template with the same id.
     */
    public static final ModelElement getTemplateElement(final ModelElement me) {
        GDCollection gdcoll = me.getCollection();
        ModelCategory modelCategory = gdcoll.getModelCategory();
        //the element is from a template itself -> no result
        if (modelCategory == ModelCategory.TEMPLATE) {
            return null;
        }
        MetaModel metaModel = gdcoll.getMetaModel();
        Class<? extends ModelElement> elementClass = me.getClass();
        //the element is not a template element -> no result
        if (!metaModel.isPureTemplateElementClass(elementClass)) {
            return null;
        }
        MetaModelContext metaModelContext = gdcoll.getMetaModelContext();
        TemplateLibrariesManager templateLibrariesManager = getTemplateLibrariesManager();
        if (templateLibrariesManager == null) {
            return null;
        }
        Collection<GDCollection> templates = templateLibrariesManager.getTemplates(metaModelContext);
        String elementHash = me.getHashString();
        for (GDCollection template : templates) {
            LGMGraphDocument mainDoc = template.getMainDoc();
            ModelElement templateElement = mainDoc.findElementCoded(elementHash);
            if (templateElement != null) {
                return templateElement;
            }
        }
        return null;
    }

    /** Gibt zurück, ob der ExpertMode aktiv ist */
    public static boolean isExpertMode() {
        return OPTION_ENABLE_EXPERT_MODE.is();
    }

    private static boolean paintSimpleGraph = false;

    public static boolean isPaintSimpleGraph() {
        return paintSimpleGraph;
    }

    public static final void setPaintSimpleGraph(final boolean simple) {
        paintSimpleGraph = simple;
        if (simple) {
            return;
        }
        //Das neu Zeichnen braucht nicht angestoßen werden, wenn auf die
        //Simple Grafik umgeschaltet wurde, denn dann bleibt, solange sich
        //nichts ändert immer noch die volle Grafik in der Anzeige erhalten
        GraphDocument selectedDoc = getSelectedDoc();
        if (selectedDoc != null) {
            selectedDoc.distributeEvent(LAYOUT_CHANGED);
        }
    }

    // ProgressDialog //

    /**
     * erstellt einen neuen ProgressDialog mit dem Hauotfenster als owner
     */
    public static void showProgressDialog() {
        showProgressDialog(true);
    }

    /**
     * erstellt einen neuen ProgressDialog mit dem Hauptfenster als owner
     */
    public static void showProgressDialog(final boolean showStatusLabel) {
        //ist null, wenn der Baukasten extern z.B. über den Reporter geladen wird
        if (tool != null) {
            showProgressDialog(getMainFrame(), showStatusLabel);
        }
    }

    /**
     * erstellt einen neuen ProgressDialog mit dem übergebenen Fenster als owner
     */
    public static void showProgressDialog(final JFrame owner) {
        showProgressDialog(owner, true);
    }

    /**
     * erstellt einen neuen ProgressDialog mit dem übergebenen Dialog als owner
     */
    public static void showProgressDialog(final JDialog owner) {
        showProgressDialog(owner, true);
    }

    /**
     * erstellt einen neuen ProgressDialog mit dem übergebenen Fenster als owner
     */
    public static void showProgressDialog(final JFrame owner, final boolean showStatusLabel) {
        closeProgressDialog();
        progressDialog = new ProgressDialog(owner, showStatusLabel);
    }

    /**
     * erstellt einen neuen ProgressDialog mit dem übergebenen Dialog als owner
     */
    public static void showProgressDialog(final JDialog owner, final boolean showStatusLabel) {
        closeProgressDialog();
        progressDialog = new ProgressDialog(owner, showStatusLabel);
    }

    /**
     * schließt den ProgressDialog, sofern dieser überhaupt existiert; ansonsten passiert nichts
     */
    public static void closeProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        progressDialog.dispose();
        progressDialog = null;
    }

    /**
     * Setzt einen neuen Titel des ProgressDialog, sofern dieser überhaupt existiert; ansonsten
     * passiert nichts.
     *
     * @param text
     *            String mit dem neuen Titel
     */
    public static void setProgressDialogTitle(final String text) {
        if (progressDialog == null) {
            return;
        }
        progressDialog.setTitle(text);
    }

    /**
     * Setzt einen neuen Stautstext des ProgressDialog, sofern dieser überhaupt existiert;
     * ansonsten passiert nichts. Wenn der übergebene String in den Resourcen gefunden wird,
     * dann wird er durch den Resourcen-String eretzt, ansonsten wird er direkt angezeigt.
     *
     * @param text String mit neuen Statustext
     */
    public static void setProgressDialogStatusLabel(final String resourceKeyOrText) {
        setProgressDialogStatusLabel(resourceKeyOrText, null);
    }

    /**
     * Setzt einen neuen Stautstext des ProgressDialog, sofern dieser überhaupt existiert;
     * ansonsten passiert nichts.
     *
     * @param resourceKey
     * @param text
     */
    public static void setProgressDialogStatusLabel(final String resourceKey, final String text) {
        if (progressDialog == null) {
            return;
        }
        //wenn null als resourceKey angegeben wurde, dann das Label leer initilaisieren, ansonsten mit dem resourceKey
        String label = resourceKey == null ? "" : resourceKey;
        //wenn ein echter String als resoruceKey angegeben wurde
        if (!label.isEmpty()) {
            try {
                //versuche den resourceKey zu laden
                label = getResString(label);
            } catch (Exception e) {
                //tue nichts
            }
        }
        //wenn ein nicht leerer zusätzlicher Text angegeben wurde
        if (!Strings.isNullOrEmpty(text)) {
            //wenn oben irgendein nicht leerer String als Label zusammen gebaut wurde
            if (!label.isEmpty()) {
                //hänge den zusätzlichen Text mit Leerzeichen an
                label += " " + text;
            } else {
                //das label war bis hierher leer -> setze nur den zusätzlichen Text als Label
                label = text;
            }
        }
        progressDialog.setStatusLabelText(label);
    }

    // Selektionen //

    /**
     * Gibt zurück, ob wenigstens ein richtiger Node selektiert ist
     *
     * @see GraphDocument#isSelectedAtLeastOneRealNode()
     */
    public static final Iterable<NodeContainer> iterableSelectedRealElementContainer() {
        GraphDocument selectedDoc = getSelectedDoc();
        if (selectedDoc == null) {
            return new ArrayList<>(0);
        }
        return selectedDoc.getSelectedRealElementContainerIterable();
    }

    // MessageDialoge //

    /**
     * Unter OS-X reagieren modale Dialoge, die aus Drag&Drop-Ereignissen heraus gestartet werden nicht mehr auf die
     * Maus. Sie tun es erst wieder, wenn sie in einem eigenen Thread gestartet werden. Daher das hier.
     *
     * @param parentComponent
     * @param messageResKey
     */
    public static final void showMessage(final Component parentComponent, final String messageResKey) {
        SwingUtilities.invokeLater(() -> {
            String message = getResString(messageResKey);
            JOptionPane.showMessageDialog(parentComponent, message);
        });
    }

    private static OutputDialog errorOutputDialog;

    public static final void showErrorOutputDialog(final Object mainMessage, final Object... message) {
        if (errorOutputDialog == null) {
            errorOutputDialog = new OutputDialog(getMainFrame(), getResString("FehlerAllgemein"));
        }
        errorOutputDialog.setVisible(true);
        errorOutputDialog.appendln(mainMessage, message);
    }

    /**
     * Liefert <code>true</code>, wenn auf dem MAC gerade ein DragNDrop ausgeführt wurde. Dann darf möglichst kein Dialog geöffnet werden, weil der
     * dann wegen eines Java-Bugs auf dem MAC nicht mehr per Maus sondern nur noch per Tatstatur bedienbar ist.
     *
     * @return
     */
    public static boolean isDragNDropOnMac() {
        //Ausnahme für Mac-Java-Bug: wenn Dialoge aus einem Drag&Drop-Ereignis heraus gestartet werden, kann man sie nicht mehr mit der Maus ansprechen. Nur mit Tasten.
        //Dieser Bug ist nicht zu umgehen.
        if (IS_MAC_OS && Sys.stackTraceContains(DropTarget.class)) {
            return true;
        }
        return false;
    }

    // Plugin //

    /**
     * Lädt aus dem Plugin-Verzeichnis alle Klassen der übergebenen Art, ruft dann von
     * jeder Klasse den parameterlosen Konstruktor auf und gibt alle Instanzen zurück,
     * bei denen der Aufruf des Konstruktors ohne Fehler geklappt hat.
     *
     * @param superClassOfResultClasses
     * @return Instanzen der übergebenen Klasse aus dem übergebenen Verzeichnis
     */
    public static <T> List<T> loadPlugins(final Class<T> superClassOfResultClasses) {
        List<T> instances = PluginUtils.loadInstances(Tool3lgmConstants.PLUGIN_DIR, superClassOfResultClasses);
        return instances;
    }

    /**
     * Lädt aus dem Plugin-Verzeichnis die alle Klassen der übergebenen Art, ruft
     * dann von der jeweils nächsten Klasse den parameterlosen Konstruktor auf und
     * gibt die erste Instanz zurück, bei denen der Aufruf des Konstruktors ohne
     * Fehler geklappt hat.
     *
     * @param superClassOfResultClasses
     * @return Instanz der übergebenen Klasse aus dem übergebenen Verzeichnis oder <code>null</code>, wenn keine Instanz gefunden wurde
     */
    public static <T> T loadPlugin(final Class<T> superClassOfResultClasses) {
        T instance = PluginUtils.loadInstance(Tool3lgmConstants.PLUGIN_DIR, superClassOfResultClasses);
        return instance;
    }

    /**
     * Lädt alle Klassen der übergebenen Art aus dem übergebenen Verzeichis oder Jar-File. Ist es ein Verzeichnis, werden alle darin enthaltenen
     * Jar-Files durchsucht, sonst nur das eine übergebene Jar-File. Ist es weder Verzeichnis noch Jar-File, kommt eine leere Liste zurück.
     *
     * @param superClassOfResultClasses
     *            (Ober-)Klasse aller Ergebnisklassen
     * @return
     */
    public static <T> List<Class<? extends T>> loadClasses(final Class<? extends T> superClassOfResultClasses) {
        return PluginUtils.loadClasses(Tool3lgmConstants.PLUGIN_DIR, superClassOfResultClasses);
    }

}
