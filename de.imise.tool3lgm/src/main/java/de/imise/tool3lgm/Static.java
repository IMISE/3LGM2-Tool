/**
 *
 */
package de.imise.tool3lgm;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.matrixview.TableInternalFrame;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.ToolInternalFrame;
import de.imise.util.swing.dialog.ProgressDialog;

/**
 * Klasse, die häufig gebrauchte statische Informationen und Zugriffsfunktionen des Tools liefert.
 *
 * @author AXS
 */
public class Static {

    static Tool3lgm tool;

    private static JFrame mainFrame;

    /** Progress-Dialog */
    private static ProgressDialog progressDialog;

    /** Gibt die gerade laufende Instanz von 3lgm wieder */
    public static Tool3lgm getTool() {
        return tool;
    }

    public static JFrame getMainFrame() {
        if (mainFrame == null) {
            mainFrame = tool;
        }
        return mainFrame;
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

    /** Setzt das ausgewählte Modell und holt bei Bedarf den dazugehörigen Frame nach vorne */
    public static void setSelectedDoc(final GraphDocument doc, final boolean activateGraphView) {
        if (tool == null) {
            return;
        }
        tool.setSelectedDoc(doc, activateGraphView);
    }

    /**
     * Liefert die geöffneten Modelle
     *
     * @return
     */
    public static ArrayList<GDCollection> getCollections() {
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
        return f != null && f instanceof ToolInternalFrame && f.getGraphDocument() instanceof Szenario && f.isVisible();
    }

    /** Gibt zurück, ob ein aktiver, sichtbarer Matrix-Frame existiert */
    public static boolean isActiveFrameMatrixFrame() {
        AbstractInternalFrame f = getActiveFrame();
        return f != null && f instanceof TableInternalFrame && f.isVisible();
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
            doc.distributeEvent(GraphDocument.LAYOUT_CHANGED);
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
     * erstellt einen neuen ProgressDialog mit dem Hauotfenster als owner
     */
    public static void showProgressDialog(final boolean showStatusLabel) {
        //ist null, wenn der Baukasten extern z.B. über den Reporter geladen wird
        if (tool != null) {
            showProgressDialog(tool, showStatusLabel);
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
                label = Tool3lgmConstants.getResString(label);
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
     * Gibt zurück, ob wenigstens ein richtiger Knoten selektiert ist
     *
     * @see GraphDocument#isSelectedAtLeastOneRealNode()
     */
    public static final Iterable<NodeContainer> iterableSelectedRealElementContainer() {
        GraphDocument doc = getSelectedDoc();
        if (doc == null) {
            return new ArrayList<NodeContainer>(0);
        }
        return doc.getSelectedRealElementContainerIterable();
    }

    // MessageDialoge //

    /**
     * Unter OS-X reagieren modale Dialoge, die aus Drag&Drop-Ereignissen heraus gestartet werden nicht mehr auf die
     * Maus. Sie tun es erst wieder, wenn sie in einem eigenen Thread gestartet werden. Daher das hier.
     *
     * @param parenComponent
     * @param messageResKey
     */
    public static void showMessgae(final Component parenComponent, final String messageResKey) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String message = Tool3lgmConstants.getResString(messageResKey);
                JOptionPane.showMessageDialog(parenComponent, message);
            }
        });
    }

}
