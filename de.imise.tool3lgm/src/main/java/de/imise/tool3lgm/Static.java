/**
 *
 */
package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.GDCollectionChangeListener.GDCollectionChangeType.LAYOUT_CHANGED;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;

import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.InternalGraphFrame;
import de.imise.tool3lgm.gui.MainFrame;
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.util.Sys;
import de.imise.util.swing.dialog.OutputDialog;
import de.imise.util.swing.dialog.ProgressDialog;

/**
 * Klasse, die häufig gebrauchte statische Informationen und Zugriffsfunktionen des Tools liefert.
 *
 * @author AXS
 */
public class Static {

    static Tool3lgm tool;

    /** Progress-Dialog */
    private static ProgressDialog progressDialog;

    /** Holds the current context and generates context menus */
    public static final ContextGenerator contextGenerator = new ContextGenerator();

    /**
     * @return instance of ContextGenerator
     */
    public static ContextGenerator getContextGenerator() {
        return contextGenerator;
    }

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
     * Liefert den MainFrame des Tools
     *
     * @return mainFrame
     */
    public static MainFrame getMainFrame() {
        return tool == null ? null : tool.getMainFrame();
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

    /** Liefert das aktuelle selektierte Modell */
    public static GDCollection getSelectedGDCollection() {
        if (tool == null) {
            return null;
        }
        return tool.getSelectedGDCollection();
    }

    /** Liefert das Modell, das vor dem aktuell selektierten Modell selektiert war */
    public static GDCollection getPreSelectedGDCollection() {
        if (tool == null) {
            return null;
        }
        return tool.getPreSelectedGDCollection();
    }

    /** Gibt das momentan ausgewählte {@link GraphDocument} zurück */
    public static LGMGraphDocument getSelectedDoc() {
        if (tool == null) {
            return null;
        }
        return tool.getSelectedDoc();
    }

    /**
     * Setzt das ausgewählte Modell und holt bei Bedarf den dazugehörigen Frame nach vorne
     *
     * @param doc
     */
    public static void setSelectedDoc(final GraphDocument doc) {
        if (tool == null) {
            return;
        }
        tool.setSelectedDoc(doc);
    }

    /**
     * Liefert die geöffneten Modelle
     *
     * @return
     */
    public static List<GDCollection> getCollections() {
        if (tool == null) {
            return null;
        }
        return tool.getCollections();
    }

    /**
     * return GDCollection with the specified index in ArrayList collections
     *
     * @param index int with index of collection in ArrayList collection
     * @return null if index < 0 or index >= collections.size(); otherwise the GDCollection with specified index
     */
    public static GDCollection getCollection(final int index) {
        if (tool == null) {
            return null;
        }
        return tool.getCollection(index);
    }

    /**
     * Liefert die Anzahl der offenen Modelle
     *
     * @return
     */
    public static int getCollectionCount() {
        if (tool == null) {
            return -1;
        }
        return tool.getCollectionCount();
    }

    /**
     * return all InternalFrames at desktop
     *
     * @return AbstractInternalFrame[]
     */
    public static AbstractInternalFrame[] getAllFrames() {
        if (tool == null) {
            return null;
        }
        return tool.getAllFrames();
    }

    /** Gibt das gerade aktivierte Frame zurück */
    public static AbstractInternalFrame getActiveFrame() {
        if (tool == null) {
            return null;
        }
        return tool.getActiveFrame();
    }

    /** Gibt zurück, ob interne Frames existieren */
    public static boolean isFramesExists() {
        return tool.getAllFrames().length > 0;
    }

    /** Gibt zurück, ob ein aktiver, sichtbarer Grafik-Frame existiert */
    public static boolean isActiveFrameGraphFrame() {
        AbstractInternalFrame f = getActiveFrame();
        return f != null && f instanceof InternalGraphFrame && f.getGraphDocument() instanceof Szenario && f.isVisible();
    }

    /** Gibt zurück, ob ein aktiver, sichtbarer Matrix-Frame existiert */
    public static boolean isActiveFrameMatrixFrame() {
        AbstractInternalFrame f = getActiveFrame();
        return f != null && f instanceof MatrixViewInternalFrame && f.isVisible();
    }

    /**
     * Postion, an der etwas passiert ist. Diese Position wird z. B. gesetzt, wenn der Benutzer irgendwohin mit der Maus klickt, um an
     * der entsprechenden Stelle einen Dialog auf gehen zu lassen.
     */
    private static Point lastActionPosition = null;

    /**
     * Liefert die Postion, an der etwas passiert ist. Diese Position wird z. B. gesetzt, wenn der Benutzer irgendwohin mit der Maus
     * klickt, um an der entsprechenden Stelle einen Dialog auf gehen zu lassen.
     *
     * @return
     */
    public static final Point getLastActionPosition() {
        return lastActionPosition;
    }

    /**
     * Setzt die Postion, an der etwas passiert ist. Diese Position wird z. B. gesetzt, wenn der Benutzer irgendwohin mit der Maus
     * klickt, um an der entsprechenden Stelle einen Dialog auf gehen zu lassen.
     *
     * @param x
     * @param y
     */
    public static final void setLastActionPosition(final int x, final int y) {
        lastActionPosition = new Point(x, y);
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
        GraphDocument doc = getSelectedDoc();
        if (doc != null) {
            doc.distributeEvent(LAYOUT_CHANGED);
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
        GraphDocument doc = getSelectedDoc();
        if (doc == null) {
            return new ArrayList<>(0);
        }
        return doc.getSelectedRealElementContainerIterable();
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
        if (System.getProperty("os.name").toLowerCase().contains("mac") && Sys.stackTraceContains(DropTarget.class)) {
            return true;
        }
        return false;
    }

}
