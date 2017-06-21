package de.imise.tool3lgm.graphtools;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.ModelPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.SzenarioDialog;
import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Prozess;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.KonfigurationContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMInputStream;
import de.imise.tool3lgm.xml.Base64;
import de.imise.tool3lgm.xml.LGMVersionException;
import de.imise.tool3lgm.xml.LgmXMLParser;
import de.imise.tool3lgm.xml.ToolXMLParser;
import de.imise.tool3lgm.xml.XMLCharacterCoder;
import de.imise.tool3lgm.xml.XMLVersionException;
import de.imise.util.collections.AlphabeticalSet;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.io.FileHandler;
import de.imise.util.swing.dialog.ExtendedFileChooser;
import de.imise.util.swing.dialog.NameAndColorInputDialog;

/**
 * @author thomas, AXS
 */
public final class GDCollection extends UserFieldTarget {

    /** Information für Dateiversion (wird bei jedem Aufruf von getFileVersion() um eins erhoeht) */
    private int fileVersion = 0;

    /** Undo- und Redomanager */
    protected TransactionManager tman = new TransactionManager();

    /**
     * Table, der von der ID einer Transaktion auf die Anzahl der gestarteten Untertransaktionen mit derselben ID mappt.
     * Eine Aktion wie "Lösche Aufgabe X aus Gesamtmodell" hat zur Folge, dass in jedem Teilmodell eine Transaktion
     * "Lösche Aufgabe X aus Teilmodell" gestartet wird. Über diese Map kann man für das Löschen aus dem Teilmodell
     * festestellen, dass es als Unteraktion einer anderen Transaktion gestartet wurde und nicht selbst die äußerste
     * Transaktion war.
     */
    private final Hashtable<Integer, Integer> transStackTable = new Hashtable<Integer, Integer>();
    //	/*{
    //
    //		@Override
    //		public synchronized Integer get(Object key) {
    //			Integer getted = super.get(key);
    //			System.err.println("get(Object key) " + key + " getted=" + getted);
    //			return super.get(key);
    //		}
    //
    //		@Override
    //		public synchronized Integer put(Integer key, Integer value) {
    //			System.err.println("put(Integer key, Integer value):  key=" + key + " value =" + value);
    //			return super.put(key, value);
    //		}
    //
    //		@Override
    //		public synchronized Integer remove(Object key) {
    //			Integer removed = super.remove(key);
    //			System.err.println("remove(Object key): key=" + key + " removed=" + removed);
    //			return removed;
    //		}
    //
    //	}*/;

    /** Definition der benutzerdefinierten Eigenschaften, Kennzahlen, Kanennzahlformel und Formaten */
    private UserFieldDefinitions userFieldDefinitions;

    /** Hauptdokument der Collection */
    private LGMGraphDocument doc;

    /** Liste aller <code>GraphDocumentListener</code> */
    private final ArrayList<GraphDocumentListener> listener = new ArrayList<GraphDocumentListener>();
    /**
     * Liste aller <code>GraphDocument</code>s in der Reihenfolge, dass immer das selektierte ganz hinten steht,
     * das davor selektierte direkt davor und so weiter. Jedes <code>GraphDocument</code> der Collection - also
     * auch das Hauptdokument - kommt genau einmal in der Liste vor. Wird ein Teilmodell gelöscht, wird das davor
     * selektierte aktiviert, im <code>ModelBrowser</code> selektiert und sein Grafikfenster in den Vordergrund
     * geholt.
     */
    private final ArrayList<LGMGraphDocument> activeGraphDocumentsList = new ArrayList<LGMGraphDocument>();

    /**
     * Set aller Szenarios in alphabetischer Reihenfolge.
     */
    private final AlphabeticalSet<Szenario> szenarios = new AlphabeticalSet<Szenario>();

    /** Dokument wurde geaendert */
    private boolean changed;

    /**
     * Zeitpunkt der letzen Änderung
     */
    private long lastModificationTime = System.currentTimeMillis();

    /** Bezeichnung des Dokuments (Dateiname) */
    private String name = "";

    /** the file to load collection from or to save collection in */
    private RandomAccessFile randomAccessFile;
    private File file;
    private FileLock lock;

    /** flag, whether file for this collection is only opened for reading */
    private final boolean isReadOnly = false;

    /** flag, whether the filesystem of the file for this collection supports locking */
    private boolean lockSupported = false;

    /** flag, whether collection will be saved in compressed zip-file or not */
    private boolean isZipFile = true;

    public ModelPropertyDialog descriptionFrame;

    /**
     * Verzeichnis der Bitmap-Icons
     */
    private final Hashtable<String, byte[]> iconTable = new Hashtable<String, byte[]>(100);

    /**
     * Wenn <code>true</code>, werden keine Ereignisse gefeuert und keine Undo-/Redo-Commands aufgezeichnet.
     */
    private boolean bulk_mode = false;

    /**
     * COMMENTME
     */
    private int iconCounter = 0;

    /**
     * COMMENTME
     */
    private int copyAndPaste;

    /**
     * COMMENTME
     */
    private int active_layer = 4;

    /**
     *
     */
    public GDCollection() {
        doc = new LGMGraphDocument(this);
        userFieldDefinitions = new UserFieldDefinitions(this);
        doc.addGraphDocumentListener(userFieldDefinitions);
        createName();
        tman.addTransActionListener(Static.getTool());
        activeGraphDocumentsList.add(doc);
        //		transStackTable.clear();
        //		transStackTable.put(new Integer(0), new Integer(0));
    }

    /**
     * @return the transStackTable
     */
    public final Hashtable<Integer, Integer> getTransStackTable() {
        return transStackTable;
    }

    /**
     * @return
     */
    public Szenario createSzenario() {
        return createSzenario(null, false, "", null, true);
    }

    /**
     * @param askName
     * @return
     */
    public Szenario createSzenario(final boolean askName) {
        return createSzenario(null, askName, "", null, true);
    }

    /**
     * @param pid
     * @return
     */
    public Szenario createSzenario(final int pid) {
        return createSzenario(null, false, "", null, pid);
    }

    /**
     * @param title
     * @param askName
     * @param description
     * @param szenHash
     * @param pid
     * @return
     */
    public Szenario createSzenario(final String title, final boolean askName, final String description, final String szenHash, final int pid) {
        return createSzenario(title, askName, description, szenHash, true, pid);
    }

    /**
     * @param title
     * @param askName
     * @param description
     * @param szenHash
     * @param logWithStandardPID
     * @return
     */
    public Szenario createSzenario(final String title, final boolean askName, final String description, final String szenHash, final boolean logWithStandardPID) {
        return createSzenario(title, askName, description, szenHash, logWithStandardPID, TransactionManager.STANDARD_PID);
    }

    /**
     * @param title
     * @param askName
     * @param szenHash
     * @param pid
     * @return
     */
    private Szenario createSzenario(String title, final boolean askName, final String description, final String szenHash, final boolean log, final int pid) {
        if (title == null || title.trim().equals("")) {
            title = CollectionUtils.getNextIndicatedName(Tool3lgmConstants.getResString("submodel") + " #", activeGraphDocumentsList);
        }
        if (askName) {
            title = askName(title);
        }
        if (title == null) {
            return null;
        }

        Szenario szenario = new Szenario(this, title, description, szenHash);
        szenarios.add(szenario);
        activeGraphDocumentsList.add(szenario);
        if (log) {
            doc.start_transaction(pid);
            doc.addUndoCommand(GDCommands.REMOVE_SZENARIO + " " + szenario.getHashString(), pid);
            doc.addRedoCommand(GDCommands.CREATE_SZENARIO + " " + GraphDocument.getParseSaveString(szenario.getTitle()) + " " + GraphDocument.getParseSaveString(szenario.getDescription()) + " " + szenario.getHashString(), pid);
            doc.finish_transaction(pid);
        }
        szenario.addGraphDocumentListener(userFieldDefinitions);
        if (descriptionFrame != null) {
            descriptionFrame.update();
        }
        //			descriptionFrame.addTab(szenario);
        changed = true;
        return szenario;
    }

    /**
     * @param szenHash
     * @param pid
     */
    public void deleteSzenario(final String szenHash, final int pid) {
        GraphDocument szenario = getGraphDocumentCoded(szenHash);
        if (!(szenario instanceof Szenario)) {
            return;
        }

        doc.start_transaction(pid);

        //bei allen Elementen, die mit dem zu löschenden Teilmodell verknüpft sind
        //den Verweis auf dieses Teilmodell löschen (das passiert im Hauptmodell)
        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            LayerContainer layer = doc.getLayer(ModelConstants.LAYERS[i]);
            for (int j = 0; j < layer.getKnotenCount(); j++) {
                Knoten knoten = layer.getNodeContainer(j).getKnoten();
                String associatedDoc = knoten.getAssociatedDoc();
                if (associatedDoc != null && associatedDoc.equals(szenHash)) {
                    knoten.setAssociatedDoc(null);
                }
            }
        }
        //alle Elemente des Szenarios löschen -> das kann man dann auch wieder zurück nehmen
        ArrayList<ElementContainer> elementsToDelete = new ArrayList<ElementContainer>(100);

        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            LayerContainer layer = szenario.getLayer(ModelConstants.LAYERS[i]);
            elementsToDelete.addAll(layer.getKnickpunkte());
            elementsToDelete.addAll(layer.getKanten());
            elementsToDelete.addAll(layer.getKnoten());
        }
        removeContainerFromSubmodel(elementsToDelete, pid);

        szenarios.remove(szenario);
        activeGraphDocumentsList.remove(szenario);

        for (int layerID = 0; layerID < ModelConstants.LAYERS.length; layerID++) {
            doc.addUndoCommand(GDCommands.CHANGE_LAYER_COLOR + " " + szenHash + " " + layerID + " " + szenario.layer[layerID].getColor().getRGB(), pid);
            doc.addUndoCommand(GDCommands.CHANGE_LAYER_ALPHA + " " + szenHash + " " + layerID + " " + szenario.layer[layerID].getAlpha(), pid);
            doc.addUndoCommand(GDCommands.CHANGE_LAYER_SIZE_FACTOR + " " + szenHash + " " + szenario.getPageSizeFactor(), pid);

        }
        doc.addUndoCommand(GDCommands.CREATE_SZENARIO + " " + GraphDocument.getParseSaveString(szenario.getTitle()) + " " + GraphDocument.getParseSaveString(szenario.getDescription()) + " " + szenario.hashString, pid);
        doc.addRedoCommand(GDCommands.REMOVE_SZENARIO + " " + szenario.hashString, pid);

        //wenn das Beschreibungsfenster offen ist -> den Tab des zu löschenden Teimodells löschen
        if (descriptionFrame != null) {
            descriptionFrame.update();
            //			descriptionFrame.removeTab(szenario);
        }

        Static.getTool().closeFrame(szenario);

        doc.finish_transaction(pid);
        changed = true;
    }

    /**
     * @param szen
     * @return
     */
    public boolean renameSzenario(final Szenario szen) {
        String szenTitle = askName(szen.getTitle());
        if (szenTitle == null) {
            return false;
        }
        szen.getCollection().setChanged(true);
        szen.setTitle(szenTitle);
        if (descriptionFrame != null) {
            descriptionFrame.update();
        }
        //			descriptionFrame.renameTab(szen);
        Static.getTool().getModelBrowserPanel().updateTitle(szen);
        distribute(GraphDocument.DATA_CHANGED, null, null, doc, TransactionManager.STANDARD_PID);
        return true;
    }

    /**
     * Setzt das übergebene <code>GraphDocument</code> als das aktuell selektierte.
     *
     * @param doc
     */
    public void setActiveGraphDocument(final GraphDocument doc) {
        activeGraphDocumentsList.remove(doc);
        activeGraphDocumentsList.add((LGMGraphDocument) doc);
    }

    /**
     * Gibt das aktuell selektierte <code>GraphDocument</code> zurück.
     */
    public LGMGraphDocument getSelectedDoc() {
        if (activeGraphDocumentsList.size() < 1) {
            return null;
        }
        return activeGraphDocumentsList.get(activeGraphDocumentsList.size() - 1);
    }

    /**
     * Entfernt das alle <code>GraphDocument</code>s aus der Liste der Teilmodelle. Sonst passiert
     * hier nichts!
     *
     * @return
     */
    public void simpleRemoveGraphDocuments() {
        activeGraphDocumentsList.clear();
    }

    public void removeGraphDocuments() {
        for (Szenario szen : szenarios) {
            szen.gdcoll = null;
        }
        doc.gdcoll = null;
        szenarios.clear();
        doc = null;
        activeGraphDocumentsList.clear();
    }

    /**
     * @param szenname
     * @return
     */
    private static final String askName(final String szenname) {
        NameAndColorInputDialog d = new NameAndColorInputDialog(Static.getMainFrame());
        d.showDialog(Tool3lgmConstants.getResString("szenario_name_anfrage"), szenname);
        return d.getInputString();
    }

    /**
     * @param ec
     * @return
     */
    private static final boolean askNameAndColor(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        while (true) {
            NameAndColorInputDialog d = new NameAndColorInputDialog(Static.getMainFrame());
            //TODO:Prozess gegen etwas allg. ersetzen (z. B. coloredElement als Eigenschaft von Element-Klassen)

            Point dialogPosition = Tool3lgm.getLastActionPosition();
            if (dialogPosition == null) {
                dialogPosition = new Point(100, 100);
            }
            boolean showColorChooser = me instanceof Prozess;
            d.showDialog(Tool3lgmConstants.getResString("name_eing"), me.toString(), dialogPosition.x, dialogPosition.y, showColorChooser);
            if (d.getInputString() == null) {
                return false;
            }
            if (d.getInputColor() != null) {
                ec.get3LGMLayout().bg_color = d.getInputColor();
            }
            if (d.getInputString().equals("") || d.getInputString().equals(me.toString())) {
                return true;
            }
            me.setName(d.getInputString());
            return true;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //#############################################################################################//
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fügt unter der angegebenen PID die UndoKommandos ein, um das Layout des übergebenen Containers
     * wieder herzustellen.
     *
     * @param ec
     * @param pid
     */
    private void addLayoutUndoCommands(final ElementContainer ec, final int pid) {
        GraphDocument ecDoc = ec.getGraphDocument();
        //im Hauptdokument ist die LayoutInformation der Container egal
        if (ecDoc == doc) {
            return;
        }
        String ecHash = ec.getHashString();
        if (ec.getColor() != null) {
            ecDoc.addUndoCommand(GDCommands.CHANGE_COLOR + " " + ecDoc.hashString + " " + ecHash + " " + ec.getColor().getRGB(), pid);
            ecDoc.addUndoCommand(GDCommands.CHANGE_ALPHA + " " + ecDoc.hashString + " " + ecHash + " " + ec.getAlpha(), pid);
        }
        if (ec.getForm() != null) {
            ecDoc.addUndoCommand(GDCommands.CHANGE_FORM + " " + ecDoc.hashString + " " + ecHash + " " + ec.getForm(), pid);
        }
        if (!ec.hasStandardFont()) {
            ecDoc.addUndoCommand(
                    GDCommands.CHANGE_FONT + " " + ecDoc.hashString + " " + ecHash + " " + GraphDocument.GDCOMMAND_TEXT_SURROUNDER + ec.getFontName() + GraphDocument.GDCOMMAND_TEXT_SURROUNDER + " " + ec.getFontSize() + " " + ec.getFontStyle(), pid);
        }
        if (ec instanceof NodeContainer) {
            NodeContainer kc = (NodeContainer) ec;
            String iconName = kc.getIconString();
            if (iconName != null) {
                ecDoc.addUndoCommand(GDCommands.SET_ICON + " " + ecDoc.hashString + " " + ecHash + " " + iconName, pid);
            }
            ecDoc.addUndoCommand(GDCommands.Z_MOVE + " " + ecDoc.hashString + " " + ecHash + " " + ecDoc.layer[ec.layerFor()].indexOf(ec), pid);
            ecDoc.addUndoCommand(GDCommands.COORDINATE_KNOT + " " + ecDoc.hashString + " " + ecHash + " " + ec.getX() + " " + ec.getY() + " " + ec.getWidth() + " " + ec.getHeight(), pid);
            if (!kc.isVisible()) {
                ecDoc.addUndoCommand(GDCommands.SET_VISIBLE + " " + false + " " + ecDoc.hashString + " " + ecHash, pid);
            }
            if (ec.getValign() != GraphElementLayout.STANDARD_ELEMENT_LAYOUT.valign) {
                ecDoc.addUndoCommand(GDCommands.LABEL_VALIGN + " " + ecDoc.hashString + " " + ecHash + " " + kc.get3LGMLayout().valign, pid);
            }
            if (ec.getHalign() != GraphElementLayout.STANDARD_ELEMENT_LAYOUT.halign) {
                ecDoc.addUndoCommand(GDCommands.LABEL_HALIGN + " " + ecDoc.hashString + " " + ecHash + " " + kc.get3LGMLayout().halign, pid);
            }
        }
    }

    /**
     * @param ec
     * @param pid
     */
    public void removeContainerFromSubmodel(final ElementContainer ec, final int pid) {
        ArrayList<ElementContainer> container = new ArrayList<ElementContainer>();
        container.add(ec);
        removeContainerFromSubmodel(container, pid);
    }

    /**
     * Entfernt die übergebenen Container aus ihrem GraphDocument. Es werden nur {@link NodeContainer} entfernt sowie
     * deren {@link EdgeContainer} und alle von den übergebenen Elementen abhängigen Elemente. Abhängige Elemente
     * selbst oder Kanten in der übergebenen Liste werden übergangen und nicht gelöscht, wenn das Element
     * von dem sie abhängen nicht gelöscht wird.
     *
     * @param elementsToRemove
     * @param pid
     */
    public void removeContainerFromSubmodel(final Collection<ElementContainer> elementsToRemove, final int pid) {
        boolean transctionStarted = false;
        GraphDocument szen = null;
        ArrayList<ElementContainer> reallyContainerToRemove = new ArrayList<ElementContainer>();
        for (ElementContainer ec : elementsToRemove) {
            GraphDocument ecDoc = ec.getGraphDocument();
            //man kann hier nur Elemente aus demselben Szenario (also nicht aus dem Hauptmodell und alle aus dem
            //gleichen Teilmodell löschen)
            if (ecDoc == doc || szen != null && szen != ecDoc) {
                continue;
            }
            if (ec instanceof BendpointContainer) {
                if (!transctionStarted) {
                    szen = ecDoc;
                    szen.start_transaction(pid);
                    transctionStarted = true;
                }
                //				removeBendpoint((BendpointContainer)ec, pid);
                removeBendpoint(((BendpointContainer) ec).getKnickpunktKnoten(), pid);
                continue;
            }
            //keine Kanten löschen
            if (ec instanceof EdgeContainer) {
                continue;
            }
            ModelElement me = ec.getElement();
            //keine untergerodneten Elemente einfach so aus der Grafik löschen
            if (ModelConstants.isSlaveType(me.getClass())) {
                continue;
            }
            if (!transctionStarted) {
                szen = ecDoc;
                szen.start_transaction(pid);
                transctionStarted = true;
            }
            //dieser Container kann wirklich gelöscht werden -> merken
            reallyContainerToRemove.add(ec);
            for (Kante edge : me.getEdges()) {

                //den Container der Kante mit allen Knickpunkten im aktuellen Teilmodell löschen
                if (!simpleRemoveEdgeContainer((EdgeContainer) edge.getContainer(szen), pid)) {
                    continue;
                }
                //alle untergeordneten ElementContainer ebenfalls löschen
                if (!(edge instanceof Composition)) {
                    continue;
                }
                Composition comp = (Composition) edge;
                if (comp.getMaster() != me) {
                    continue;
                }
                ElementContainer slaveContainer = comp.getSlave().getContainer(szen);
                if (slaveContainer != null) {
                    reallyContainerToRemove.add(slaveContainer);
                }
            }
        }

        //wenn es nichts zu löschen gab -> raus
        if (!transctionStarted) {
            return;
        }
        simpleRemoveContainerFromSzenario(reallyContainerToRemove, false, pid);
        szen.finish_transaction(pid);
        szen.distributeEvent(GraphDocument.DATA_CHANGED, pid);
        szen.distributeEvent(GraphDocument.SELECTION_CHANGED, pid);
    }

    /**
     * Entfernt den {@link EdgeContainer} und alle seine Knickpunkte aus seinem GraphDocument.
     *
     * @param edgeContainer
     * @param pid
     * @return <code>true</code> wenn der Container nicht <code>null</code> war, sonst <code>false</code>
     */
    private boolean simpleRemoveEdgeContainer(final EdgeContainer edgeContainer, final int pid) {
        if (edgeContainer == null) {
            return false;
        }
        //erstmal alle Knickpunkte löschen
        ArrayList<BendpointContainer> bendPointContainerList = edgeContainer.getBendpointContainerList();
        //die bendPointContainerList wird beim removeBendpoint-Aufruf selbst geändert -> daher einfach von hinten die
        //Knickpunkte löschen.
        for (int k = bendPointContainerList.size() - 1; k >= 0; k--) {
            removeBendpoint(bendPointContainerList.get(k).getKnickpunktKnoten(), pid);
        }

        Kante edge = edgeContainer.getEdge();
        GraphDocument doc = edgeContainer.getGraphDocument();
        //jetzt den KantenContainer einfach löschen
        edge.removeContainer(doc);
        doc.layer[ModelConstants.layerFor(edge.getClass())].remove(edgeContainer);
        return true;
    }

    /**
     * Entfernt die übergebenen ElementContainer aus dem Szenario des ersten Containers in der Liste ohne dabei
     * irgendwelche Konsistenzprüfungen vorzunehmen.
     *
     * @param containerToRemove
     * @param pid
     */
    private void simpleRemoveContainerFromSzenario(final Collection<ElementContainer> containerToRemove, final boolean logSubElements, final int pid) {
        boolean transActionStarted = false;
        GraphDocument ecDoc = null;
        for (ElementContainer ec : containerToRemove) {
            ecDoc = ec.getGraphDocument();
            if (!transActionStarted) {
                ecDoc.start_transaction(pid);
                transActionStarted = true;
            }
            addLayoutUndoCommands(ec, pid);
            ModelElement me = ec.getElement();
            ecDoc.addRedoCommand(GDCommands.REMOVE_ELEMENT_FROM_SZENARIO + " " + ecDoc.hashString + " " + me.getHashString(), pid);
            me.removeContainer(ecDoc);
            ecDoc.layer[ModelConstants.layerFor(me.getClass())].remove(ec);
        }
        //das Undo das die Container wieder einfügt muss als letztes kommen, weil es als erstes beim
        //Rückgängig machen wieder ausgeführt wird
        for (ElementContainer ec : containerToRemove) {
            ModelElement me = ec.getElement();
            if (logSubElements || !ModelConstants.isSlaveType(me.getClass())) {
                ecDoc.addUndoCommand(GDCommands.ADD_ELEMENT_TO_SZENARIO + " " + ecDoc.hashString + " " + me.getHashString(), pid);
            }
        }
        if (!transActionStarted) {
            return;
        }
        ecDoc.finish_transaction(pid);
        ecDoc.distributeEvent(GraphDocument.DATA_CHANGED, pid);
        ecDoc.distributeEvent(GraphDocument.SELECTION_CHANGED, pid);
    }

    /**
     * @param me
     */
    public void delete(final ModelElement me) {
        deleteElement(me, TransactionManager.STANDARD_PID);
    }

    /**
     * @param me
     * @param pid
     */
    public final void deleteElement(final ModelElement me, final int pid) {
        deleteElement(me, doc, pid);
    }

    /**
     * @param me
     * @param doc
     *            GraphDocument, das die Transaktion starten und beenden soll, also dessen Selektion im Falle
     *            eines Undo wieder hergestellt wird. Das Element selbst wird natürlich aus allen Teilmodellen
     *            und dem Hauptmodell gelöscht.
     * @param pid
     */
    public final void deleteElement(final ModelElement me, final GraphDocument gdoc, final int pid) {
        ArrayList<ModelElement> list = new ArrayList<ModelElement>();
        list.add(me);
        deleteElements(list, gdoc, pid);
    }

    /**
     * @param elementHashesToDelete
     * @param pid
     */
    public final void deleteElements(final String[] elementHashesToDelete, final int pid) {
        deleteElements(elementHashesToDelete, doc, pid);
    }

    /**
     * @param elementHashesToDelete
     * @param doc
     *            GraphDocument, das die Transaktion starten und beenden soll, also dessen Selektion im Falle
     *            eines Undo wieder hergestellt wird. Das Element selbst wird natürlich aus allen Teilmodellen
     *            und dem Hauptmodell gelöscht.
     * @param pid
     */
    public final void deleteElements(final String[] elementHashesToDelete, final GraphDocument gdoc, final int pid) {
        ArrayList<ModelElement> elementsToDelete = new ArrayList<ModelElement>(elementHashesToDelete.length);
        for (String elementHash : elementHashesToDelete) {
            ModelElement me = doc.findElementCoded(elementHash);
            elementsToDelete.add(me);
        }
        deleteElements(elementsToDelete, gdoc, pid);
    }

    /**
     * @param elementsToDelete
     * @param pid
     */
    public final void deleteElements(final ArrayList<? extends ModelElement> elementsToDelete, final int pid) {
        deleteElements(elementsToDelete, doc, pid);
    }

    /**
     * @param elementsToDelete
     * @param doc
     *            GraphDocument, das die Transaktion starten und beenden soll, also dessen Selektion im Falle
     *            eines Undo wieder hergestellt wird. Das Element selbst wird natürlich aus allen Teilmodellen
     *            und dem Hauptmodell gelöscht.
     * @param pid
     */
    public final void deleteElements(final ArrayList<? extends ModelElement> elementsToDelete, final GraphDocument gdoc, final int pid) {
        //das wird die Liste mit allen zu löschenden Elementen. Das sind alle Elemente aus <code>elementsToDelete</code>,
        //alle Kanten dieser Elemente und rekursiv alle von den zu löschenden Elementen abhängigen Elemente (min. Karfinalität=1)
        //sowie deren Kanten
        ArrayList<ModelElement> allElementsToDelete = new ArrayList<ModelElement>(elementsToDelete);
        //In dieses Set kommen alle Elemente, deren Löschen man nicht in den RedoKommandos loggen muss, weil beim Löschen eines
        //anderen Elementes eine minimale Kardinalität unterschritten ist, so dass sie automatisch mitgelöscht werden
        HashSet<ModelElement> dependentDeletedElements = new HashSet<ModelElement>();
        //das wird die Liste aller zu löschenden Verbindungen
        ArrayList<Kante> edgesToDelete = new ArrayList<Kante>();

        gdoc.start_transaction(pid);

        for (int i = 0; i < allElementsToDelete.size(); i++) {
            ModelElement me = allElementsToDelete.get(i);

            if (me == null) {
                allElementsToDelete.remove(i--);
                continue;
            }

            //den evtl. geöffneten Dialog des Elementes scließen
            ElementPropertyDialog dialog = ModelConstants.hasObjektDialog(me);
            if (dialog != null) {
                dialog.performOK();
            }

            //Knickpunkte kann man gleich löschen
            if (me instanceof Knickpunkt) {
                ElementContainer kpc = me.getContainer(gdoc);
                if (kpc == null) {
                    kpc = me.getContainer(doc);
                }
                removeBendpoint((Knickpunkt) me, pid);
                allElementsToDelete.remove(i--);
                continue;
            } else if (me instanceof Kante) {
                Kante edge = (Kante) me;
                edgesToDelete.add(edge);
                //wenn durch das Löschen der Kante auch die Kardinalität für eins oder beide der durch die Kante verbundenen
                //Elemente unterschritten wurde -> die Elemente auch löschen
                ModelElement[] startEnd = {
                        edge.getStart(),
                        edge.getEnd()
                };
                for (ModelElement elem : startEnd) {
                    //wenn die Anzahl der bestehenden Kanten der zu löschenden Art für das verbundene Element gleich
                    //der minimalen Kardinalität für diese Kantenart ist, dann muss das verbundene Element auch gelöscht werden
                    //auf Gleichheit muss getestet werden, weil die Kante ja noch nicht wirklich gelöscht ist und somit mitgezählt wird
                    if (elem != null && elem.countConnections(edge.getClass()) <= Kante.getMinCardinality(elem.getClass(), edge.getClass())) {
                        if (!allElementsToDelete.contains(elem)) {
                            allElementsToDelete.add(elem);
                            dependentDeletedElements.add(elem);
                        }
                    }
                }
            }

            for (Kante edge : me.getEdges()) {
                //auch Kanten können Kanten haben usw., daher müssen sie diese Schleife auch durchlaufen
                if (!allElementsToDelete.contains(edge)) {
                    allElementsToDelete.add(edge);
                }
            }
        }
        if (allElementsToDelete.size() == 0) {
            gdoc.finish_transaction(pid);
            gdoc.distributeEvent(GraphDocument.DATA_CHANGED, pid);
            gdoc.distributeEvent(GraphDocument.SELECTION_CHANGED, pid);
            return;
        }

        //alle Elemente einfach aus den Szenarien löschen
        for (Szenario szen : szenarios) {
            HashSet<ElementContainer> elementContainer = new HashSet<ElementContainer>();
            for (ModelElement me : allElementsToDelete) {
                ElementContainer ec = me.getContainer(szen);
                if (ec instanceof EdgeContainer) {
                    simpleRemoveEdgeContainer((EdgeContainer) ec, pid);
                } else if (ec != null) {
                    elementContainer.add(ec);
                }
            }
            simpleRemoveContainerFromSzenario(elementContainer, true, pid);
        }
        while (edgesToDelete.size() > 0) {
            for (int i = 0; i < edgesToDelete.size(); i++) {
                Kante edge = edgesToDelete.get(i);
                //immer erst nur Kanten ohne Kanten löschen
                if (edge.hasEdges()) {
                    continue;
                }
                ModelElement ks = edge.getStart();
                ModelElement ke = edge.getEnd();
                //bei inkonsistenten Kanten nicht loggen
                if (ks != null && ke != null) {
                    //alle Kanten sind Doppelkanten, deswegen hier keine Prüfung
                    switch (((Doppelkante) edge).getDirection()) {
                    case Doppelkante.FORWARD:
                        doc.addUndoCommand(GDCommands.LINK + " " + edge.getClass().getName() + " " + edge.getHashString() + " " + ks.getHashString() + " " + ke.getHashString() + " " + ks.getEdgeIndex(edge) + " " + ke.getEdgeIndex(edge), pid);
                        doc.addRedoCommand(GDCommands.DELETE + " " + edge.getHashString(), pid);
                        break;
                    case Doppelkante.BACKWARD:
                        doc.addUndoCommand(GDCommands.LINK + " " + edge.getClass().getName() + " " + edge.getHashString() + " " + ke.getHashString() + " " + ks.getHashString() + " " + ke.getEdgeIndex(edge) + " " + ks.getEdgeIndex(edge), pid);
                        doc.addRedoCommand(GDCommands.DELETE + " " + edge.getHashString(), pid);
                        break;
                    case Doppelkante.DOUBLE:
                        doc.addUndoCommand(GDCommands.LINK + " " + edge.getClass().getName() + " " + edge.getHashString() + " " + ke.getHashString() + " " + ks.getHashString() + " " + ke.getEdgeIndex(edge) + " " + ks.getEdgeIndex(edge), pid);
                        doc.addUndoCommand(GDCommands.LINK + " " + edge.getClass().getName() + " " + edge.getHashString() + " " + ks.getHashString() + " " + ke.getHashString() + " " + ks.getEdgeIndex(edge) + " " + ke.getEdgeIndex(edge), pid);
                        doc.addRedoCommand(GDCommands.DELETE + " " + edge.getHashString(), pid);
                        break;
                    }
                    ks.removeEdge(edge);
                    ke.removeEdge(edge);
                }
                //TODO:AXS:
                //				ElementContainer edgeCont = edge.getContainer(this.doc);
                //				int layer = ModelConstants.layerFor(edge.getClass());
                //				this.doc.layer[layer].remove(edgeCont);
                //				System.err.println("GDCollection.deleteElements() line 848");
                doc.layer[ModelConstants.layerFor(edge.getClass())].remove(edge.getContainer(doc));
                //jetzt den Container selbst löschen (kann man sich sparen, weil die Kante seobst nicht mehr gepsüeichert wird)
                edge.removeContainer(doc);
                edgesToDelete.remove(i--);
            }
        }

        //jetzt alle Knoten im Hauptmodell löschen
        for (ModelElement me : allElementsToDelete) {
            if (me instanceof Kante || me instanceof Knickpunkt) {
                continue;
            }
            doc.addUndoCommand(GDCommands.CREATE_KNOT + " " + me.getClass().getName() + " " + GraphDocument.getParseSaveString(me.getName()) + " " + GraphDocument.getParseSaveString(me.getDescription()) + " " + me.getHashString(), pid);
            if (!dependentDeletedElements.contains(me)) {
                doc.addRedoCommand(GDCommands.DELETE + " " + me.getHashString(), pid);
            }
            //den Container des zu löschenden Elementes im Hauptmodell holen
            doc.layer[ModelConstants.layerFor(me.getClass())].remove(me.getContainer(doc));
            //und danach erst im Table des Elements
            //das Löschen aus dem ContainerTbale des Elementes kann man sich sparen, da das Element nicrgends mehr gespecihert werden sollte
            //me.removeContainer(this.doc);
        }
        gdoc.finish_transaction(pid);
        gdoc.distributeEvent(GraphDocument.DATA_CHANGED, pid);
        gdoc.distributeEvent(GraphDocument.SELECTION_CHANGED, pid);

    }

    /**
     * Entfernt den übergebenen {@link Knickpunkt} aus dem Haupt-{@link GraphDocument} und
     * dem Szenario, in dem er dargestellt wird (das ist immer nur 1). Es werden die Undo-Redo-Kommandos geloggt.
     *
     * @param kpk
     * @param pid
     */
    public final void removeBendpoint(final Knickpunkt kpk, final int pid) {
        BendpointContainer kc = kpk.getBendpointContainer();
        if (kc == null) {
            return;
        }
        //das GraphDocument holen, aus dem der übergebene Container stammt
        GraphDocument kcDoc = kc.getGraphDocument();
        kcDoc.start_transaction(pid);
        Knickpunkt kp = kc.getKnickpunktKnoten();
        //hole den Container der Kante, auf der der Knickpunkt angezeigt wird (Dieser EdgeContainer ist
        //immer in einem Szenario)
        EdgeContainer edgeC = kp.getOwner();
        //fuer das UndoKommando die Position merken, an der sich der Knickpunkt auf der Kante befunden hat.
        int oldIndex = edgeC.getIndexOfKnickpunkt(kp);
        //hole das Teilmodell in dem der Knickpunkt angezeigt wird
        GraphDocument szenario = edgeC.getGraphDocument();
        //entferne den Knickpunkt von der Kante
        edgeC.removeKnickpunkt(kp);
        edgeC.computeBorderPoints();
        int layerIndex = edgeC.layerFor();
        //den Knickpunkt im Teilmodell löschen
        szenario.getLayer(layerIndex).remove(kc);
        //den Knickpunkt im Hauptmodell löschen
        doc.getLayer(layerIndex).remove(kp.getContainer(doc));
        kcDoc.addRedoCommand(GDCommands.DELETE + " " + kp.getHashString(), pid);
        kcDoc.addUndoCommand(GDCommands.INSERT_BENDING_POINT + " " + szenario.getHashString() + " " + edgeC.getHashString() + " " + kc.getHashString() + " " + kc.getX() + " " + kc.getY() + " " + oldIndex, pid);
        kcDoc.finish_transaction(pid);
        kcDoc.distributeEvent(GraphDocument.ELEMENT_DELETED, kc, doc.layer[kp.layerFor()], pid);
        kcDoc.distributeEvent(GraphDocument.SELECTION_CHANGED, kc, doc.layer[kp.layerFor()], pid);
    }

    //ENDE REMOVE //
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //#############################################################################################//
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //ANFANG ADD //

    /**
     * @param szenHashString
     * @param kanteHashString
     * @param bendpointHashString
     * @param x
     * @param y
     * @param bendpointIndex
     *            Index des Knickpunktes auf dem {@link EdgeContainer}
     * @param pid
     */
    public final BendpointContainer insertBendingPoint(final String szenHashString, final String kanteHashString, final String bendpointHashString, final int x, final int y, int bendpointIndex, final int pid) {
        GraphDocument szen = getGraphDocumentCoded(szenHashString);
        if (!(szen instanceof Szenario)) {
            return null;
        }

        BendpointContainer kpc = szen.findBendpointContainerCoded(bendpointHashString);
        if (kpc != null) {
            return kpc;
        }

        EdgeContainer kc = null;
        if (kanteHashString != null && !kanteHashString.equals("")) {
            kc = szen.findEdgeContainerCoded(kanteHashString);
        }
        if (kc != null) {
            szen.select(kc, pid);
        } else {
            if (!szen.isSelectedOnlyEdges()) {
                return null;
            }
            kc = (EdgeContainer) szen.getLastSelected();
        }

        Knickpunkt kp = new Knickpunkt();
        kp.setName(doc.getNextNewName(kp.getClass()));
        kpc = new BendpointContainer(kp, szen);

        if (bendpointHashString != null && !bendpointHashString.equals("")) {
            kpc.getKnoten().setHashString(bendpointHashString);
        }

        szen.start_transaction(pid);
        if (bendpointIndex == GDCommands.INVALID_BENDPOINT_INDEX) {
            bendpointIndex = kc.getKnickpunktInsertIndex(x, y);
        }

        //[0] = SzenHash, [1] = HashString der Kante, [2] = HashString des Knickpunktes, [3] = X-Position, [4] = Y-Position, [5] = Index des Knickpuntes auf der Kante,
        szen.addRedoCommand(GDCommands.INSERT_BENDING_POINT + " " + szenHashString + " " + kc.getHashString() + " " + kp.getHashString() + " " + x + " " + y + " " + bendpointIndex, pid);
        szen.addUndoCommand(GDCommands.DELETE + " " + kp.getHashString(), pid);

        // den Layer bestimmen auf dem der Knickpunkt eingefügt werden soll (= der Layer der Kante)
        int layerNumber = kc.getElement().layerFor();
        if (szen.getLayer(layerNumber).add(kpc) == null) {
            szen.undo(pid);
            return null;
        }
        doc.getLayer(layerNumber).add(new BendpointContainer(kp, doc));

        kc.addKnickpunkt(kpc, bendpointIndex);
        if (x != GDCommands.INVALID_POSITION_X && y != GDCommands.INVALID_POSITION_Y) {
            kpc.setLocation(x, y);
        }
        szen.select(kpc, pid);
        szen.finish_transaction(pid);
        szen.distributeEvent(GraphDocument.DATA_CHANGED, pid);
        szen.distributeEvent(GraphDocument.SELECTION_CHANGED, pid);
        kc.computeBorderPoints();
        return kpc;
    }

    /**
     * @param elementClass
     * @param name
     * @param description
     * @param hashString
     * @param pid
     * @return
     */
    public NodeContainer createKnotenWithContainer(final Class<? extends Knoten> elementClass, final String name, final String description, final String hashString, final int pid) {

        //Knickpunkte kann man über diese Funktion nicht anlegen
        if (Knickpunkt.class.isAssignableFrom(elementClass)) {
            return null;
        }

        Knoten me = null;
        NodeContainer nc = null;
        try {
            me = elementClass.newInstance();
            nc = (NodeContainer) me.createContainer(doc);
        } catch (Exception ex) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), ex);
            return null;
        }

        if (hashString != null && !hashString.trim().equals("") && !hashString.equals("null")) {
            me.setHashString(hashString);
        }
        if (name != null && !name.equals("")) {
            me.setName(GraphDocument.getDecodedParseSaveString(name));
        } else {
            me.setName(doc.getNextNewName(me.getClass()), false);

            if (isInteractiveMode() && !ModelConstants.isGenerateName(me.getClass())) {
                if (!askNameAndColor(nc)) {
                    return null;
                }
            }
        }

        if (description != null && !description.trim().equals("")) {
            me.setDescription(GraphDocument.getDecodedParseSaveString(description));
        }

        doc.start_transaction(pid);
        doc.addRedoCommand(GDCommands.CREATE_KNOT + " " + me.getClass().getName() + " " + GraphDocument.getParseSaveString(me.getName()) + " " + GraphDocument.getParseSaveString(me.getDescription()) + " " + me.getHashString(), pid);
        if (nc.getColor() != null) {
            doc.addRedoCommand(GDCommands.CHANGE_COLOR + " " + doc.hashString + " " + me.getHashString() + " " + nc.getColor().getRGB(), pid);
        }
        doc.addUndoCommand(GDCommands.DELETE + " " + me.getHashString(), pid);

        // den Layer bestimmen auf dem das Element eingefügt werden soll
        int layerNumber = me.layerFor();
        LayerContainer lc = doc.getLayer(layerNumber);
        if (lc.add(nc) == null) {
            doc.undo(pid);
            return null;
        }

        boolean old_mode = isInteractiveMode();
        setInteractiveMode(false);
        createInitialSubtypes(me, pid);

        // Sichtbarkeit der Interebenenbeziehungen prüfen
        if (nc instanceof KonfigurationContainer) {
            checkInterLayerConnectionVisibility((KonfigurationContainer) nc);
        }

        setInteractiveMode(old_mode);
        doc.finish_transaction(pid);
        doc.distributeEvent(GraphDocument.DATA_CHANGED, pid);
        return nc;
    }

    /**
     * Legt für das übergebene Element alle initialen Unterelemente an, wenn diese noch nicht vorhanden sind.
     *
     * @param me
     * @param pid
     */
    public void createInitialSubtypes(final ModelElement me, final int pid) {
        Class<? extends ModelElement> elementClass = me.getClass();
        for (Class<? extends Kante> subTypeEdgeClass : ModelConstants.getInitialSubtypes(elementClass)) {
            Class<? extends ModelElement> subType = Kante.isStartClass(subTypeEdgeClass, elementClass) ? Kante.getEndClass(subTypeEdgeClass) : Kante.getStartClass(subTypeEdgeClass);
            //minimale kardinalität für die Unterelemente
            int minCardForSubType = Kante.getMinCardinality(me.getClass(), subTypeEdgeClass);
            //bisher verbundene Anzahl von Unterelementen
            ArrayList<ModelElement> connectedSubTypes = me.getConnectedElements(subType, subTypeEdgeClass);
            //soviele Unterelemente wie fehlen neu anlegen
            for (int i = connectedSubTypes.size(); i < minCardForSubType; i++) {
                String name;
                //wenn mehrere Unterelemene existieren können, dann durchnummerieren
                if (minCardForSubType > 1) {
                    name = CollectionUtils.getNextIndicatedName(ModelConstants.getDisplayableName(subType) + " ", " " + Tool3lgmConstants.getResString("fuer") + " " + me.getName(), connectedSubTypes);
                } else {
                    name = ModelConstants.getDisplayableName(subType) + " " + Tool3lgmConstants.getResString("fuer") + " " + me.getName();
                }
                ModelElement skC = createKnotenWithContainer(subType.asSubclass(Knoten.class), name, "", null, pid).getElement();
                link(subTypeEdgeClass, me, skC, pid);
                connectedSubTypes.add(skC);
            }
        }
    }

    /**
     * Überprüft, in welchen {@link Szenario}s, Interebenenbeziehung angezeigt werden sollen und aktiviert
     * gegeben Falls das Anzeigen der neu anglegten Instanz.
     *
     * @param interLayerEndClass
     */
    void checkInterLayerConnectionVisibility(final KonfigurationContainer konfC) {

        Class<? extends ModelElement> clazz = konfC.getElement().getClass();

        for (Szenario szen : szenarios) {
            Set<Class<? extends ModelElement>> startClasses = ModelConstants.getInterLayerStartClasses(clazz);
            for (Class<? extends ModelElement> startClass : startClasses) {
                ArrayList<ElementContainer> startECs = szen.getElementContainer(startClass);
                for (ElementContainer ec : startECs) {
                    if (((InterLayerConnectedNodeContainer) ec).isShowInterLayerConnections()) {
                        konfC.setVisible(true);
                        return;
                    }
                }
            }
            konfC.setVisible(false);
            szen.getFrame().repaint();
        }
    }

    /**
     * @param kc
     * @param layerIndex
     */
    public void addNodeToMainDoc(NodeContainer kc, final int layerIndex) {
        NodeContainer nc = null;
        if (kc.getGraphDocument() == doc) {
            nc = kc;
        } else {
            if (kc instanceof BendpointContainer) {
                nc = new BendpointContainer((Knickpunkt) kc.getKnoten(), doc);
            } else if (ModelConstants.isInterLayerStartClass(kc.getElement().getClass())) {
                kc = new InterLayerConnectedNodeContainer(kc.getKnoten(), doc);
            } else {
                nc = new NodeContainer(kc.getKnoten(), doc);
            }
        }
        doc.getLayer(layerIndex).add(nc);
    }

    /**
     * @param kc
     * @param l
     * @param pid
     */
    public void addEdge(final EdgeContainer kc, final int l, final int pid) {
        Kante k = kc.getEdge();
        EdgeContainer ec = new EdgeContainer(kc, doc);
        doc.getLayer(l).add(ec);

        boolean bulkMode = isBulkMode();
        setBulkMode(true);
        for (Szenario szen : szenarios) {
            szen.createEdgeContainer(k.getStart().getContainer(szen), szen, false, pid);
        }
        setBulkMode(bulkMode);
    }

    //ENDE ADD //
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //#############################################################################################//
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //START LINK //

    /**
     * Verbindet die beiden Modellelemente miteinander, wenn noch keine Kante zwischen ihnen existiert.<br>
     *
     * @param edgeClassName
     * @param hashString
     * @param startElementHash
     * @param endElementHash
     * @param startElementEdgeIndex
     * @param endElementEdgeIndex
     * @param pid
     * @return
     *         die neu angelegte Kante zwischen den beiden Elementen oder die Kante, die bereits existierte
     * @see #link(String, String, ModelElement, ModelElement, int, int)
     */
    public final Kante link(final String edgeClassName, final String hashString, final String startElementHash, final String endElementHash, final int startElementEdgeIndex, final int endElementEdgeIndex, final int pid) {
        ModelElement me1 = doc.findElementCoded(startElementHash);
        ModelElement me2 = doc.findElementCoded(endElementHash);
        return link(edgeClassName, hashString, me1, me2, startElementEdgeIndex, endElementEdgeIndex, true, pid);
    }

    /**
     * Verbindet die beiden Modellelemente miteinander, wenn noch keine Kante zwischen ihnen existiert.<br>
     *
     * @param startElement
     * @param endElement
     * @param pid
     * @return
     *         die neu angelegte Kante zwischen den beiden Elementen oder die Kante, die bereits existierte
     * @see #link(String, String, ModelElement, ModelElement, int, int)
     */
    public final Kante link(final ModelElement startElement, final ModelElement endElement, final int pid) {
        return link(GDCommands.INVALID_EDGE_CLASS_NAME, GDCommands.INVALID_HASH_STRING, startElement, endElement, GDCommands.INVALID_EDGE_INDEX, GDCommands.INVALID_EDGE_INDEX, true, pid);
    }

    /**
     * @param edgeClass
     * @param k1
     * @param k2
     * @param pid
     * @return
     */
    public Kante link(final Class<? extends Kante> edgeClass, final ModelElement k1, final ModelElement k2, final int pid) {
        return link(edgeClass, GDCommands.INVALID_HASH_STRING, k1, k2, pid);
    }

    /**
     * @param edgeClass
     * @param edgeHash
     * @param k1
     * @param k2
     * @param pid
     * @return
     */
    public Kante link(final Class<? extends Kante> edgeClass, final String edgeHash, final ModelElement k1, final ModelElement k2, final int pid) {
        if (edgeClass == null) {
            return link(GDCommands.INVALID_EDGE_CLASS_NAME, edgeHash, k1, k2, GDCommands.INVALID_EDGE_INDEX, GDCommands.INVALID_EDGE_INDEX, true, pid);
        }
        return link(edgeClass.getSimpleName(), edgeHash, k1, k2, GDCommands.INVALID_EDGE_INDEX, GDCommands.INVALID_EDGE_INDEX, true, pid);
    }

    /**
     * @param edgeClass
     * @param startElement
     * @param endElement
     * @param startElementEdgeIndex
     * @param endElementEdgeIndex
     * @param pid
     * @return
     */
    public Kante link(final Class<? extends Kante> edgeClass, final ModelElement startElement, final ModelElement endElement, final int startElementEdgeIndex, final int endElementEdgeIndex, final int pid) {
        return link(edgeClass.getSimpleName(), GDCommands.INVALID_HASH_STRING, startElement, endElement, startElementEdgeIndex, endElementEdgeIndex, true, pid);
    }

    /**
     * Verbindet die beiden Modellelemente miteinander, wenn noch keine Kante zwischen ihnen existiert. Die Verbindung
     * entsteht immer in Vorwärtsrichtung von Element <code>me1</code> zu Element <code>me2</code><br>
     *
     * @param edgeClassName
     *            Klassenname der kante, die angelegt werden soll. Ist nur relevant, wenn es mehrere Kantenarten zwischen den Elementen geben kann.
     * @param edgeHash
     *            Wird ein Wert ungleich <code>null</code> übergeben, wird dieser als HasWert der neuen Kante gesetzt
     * @param startElement
     *            Startknoten der Kante
     * @param endElement
     *            Endknoten der Kante
     * @param edgeIndex
     * @param startElementEdgeIndex
     *            Position, an der die Kante beim Startelement in die Kantenliste eingefügt werden soll. Bei ungeordneten Listen sollte hier -1
     *            übergeben werden.
     * @param endElementEdgeIndex
     *            Position, an der die Kante beim Endelement in die Kantenliste eingefügt werden soll. Bei ungeordneten Listen sollte hier -1
     *            übergeben werden.
     * @param ensureConsistency
     *            wenn <code>true</code> wird für die verbundenen Elemente geprüft, ob die Kardinalität mit der neuen Kante
     *            überschritten wird. Wenn ja, werden überzählige Verbindungen gelöscht
     * @param pid
     *            Transaktions-ID mit der die Änderungen am Model durchgeführt werden
     * @return
     *         die neu angelegte Kante zwischen den beiden Elementen oder die Kante, die bereits existierte
     */
    public Kante link(String edgeClassName, final String edgeHash, ModelElement startElement, ModelElement endElement, final int startElementEdgeIndex, final int endElementEdgeIndex, final boolean ensureConsistency, final int pid) {

        //		System.err.println("GDCollection.link() " + me1 + "\t" + me2);

        if (startElement == null || endElement == null) {
            return null;
        }
        if (startElement == endElement) {
            return null;
        }

        Kante edge = null;
        EdgeContainer kac = null;

        Class<? extends ModelElement> edgeClassOrNull = ModelConstants.getClassForName(edgeClassName);
        Class<? extends Kante> edgeClass = edgeClassOrNull == null ? null : edgeClassOrNull.asSubclass(Kante.class);

        if (edgeClass != null && !Kante.isConnecting(edgeClass, startElement.getClass(), endElement.getClass())) {
            return null;
        }

        doc.start_transaction(pid);

        try {
            //wenn keine Kantenklasse angegeben wurde, muss diese ermittelt werden. Wenn sie nicht eindeutig ist, wird der Benutzer per Dialog gefragt.
            if (edgeClass == null) {
                Class<? extends Kante>[] edgeClasses = ModelConstants.getEdgeTypes(startElement.getClass(), endElement.getClass());
                if (edgeClasses == null || edgeClasses.length == 0) {
                    return null;
                }
                edgeClass = edgeClasses[0];
                if (edgeClasses.length > 1) {
                    JPanel messagePanel = new JPanel();
                    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
                    ButtonGroup buttonGroup = new ButtonGroup();
                    for (int i = 0; i < edgeClasses.length; i++) {
                        JRadioButton b = new JRadioButton(ModelConstants.getForwardMetaAssociationName(edgeClasses[i]));
                        b.setActionCommand(edgeClasses[i].getName());
                        messagePanel.add(b);
                        buttonGroup.add(b);
                        if (i == 0) {
                            b.setSelected(true);
                        }
                    }
                    JOptionPane optionPane = new JOptionPane(messagePanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
                    JDialog dialog = optionPane.createDialog(Static.getMainFrame(), Tool3lgmConstants.getResString("choose_trace"));
                    dialog.setVisible(true);
                    edgeClassName = buttonGroup.getSelection().getActionCommand();
                    edgeClass = ModelConstants.getClassForName(edgeClassName).asSubclass(Kante.class);
                }
            }

            edge = startElement.getEdgeTo(endElement, edgeClass, startElementEdgeIndex);

            if (edge != null) {
                doc.finish_transaction(pid);
                return edge;
            }

            edge = startElement.getEdgeFrom(endElement, edgeClass, startElementEdgeIndex);
            if (edge != null) {
                if (edge instanceof Doppelkante) {
                    ((Doppelkante) edge).setDirection(Doppelkante.DOUBLE);
                    doc.addRedoCommand(GDCommands.LINK + " " + edgeClass.getName() + " " + edge.getHashString() + " " + startElement.getHashString() + " " + endElement.getHashString() + " " + startElementEdgeIndex + " " + endElementEdgeIndex, pid);
                    doc.addUndoCommand(GDCommands.UNLINK + " " + startElement.getHashString() + " " + endElement.getHashString() + " " + startElementEdgeIndex, pid);
                }

            } else {

                try {
                    edge = edgeClass.newInstance();
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    doc.undo(pid);
                    return null;
                }

                if (edgeHash != null && !edgeHash.equals("")) {
                    edge.setHashString(edgeHash);
                }

                //AXS: geändert am 21.06.2017: jetzt sind immer alle Kanten, die nicht DoubleMeaning, PartOf oder Compisition sind automatisch DOUBLE
                //Kanten die dieselben Elemente verbinden
                //                Class<? extends ModelElement> edgeStartClass = edge.getStartClass();
                //                Class<? extends ModelElement> startClass = startElement.getClass();
                //                Class<? extends ModelElement> edgeEndClass = edge.getEndClass();
                //                Class<? extends ModelElement> endClass = endElement.getClass();
                //                boolean doubleDir = edgeStartClass.isAssignableFrom(startClass) && edgeStartClass.isAssignableFrom(endClass);
                //                doubleDir = doubleDir && edgeEndClass.isAssignableFrom(startClass) && edgeEndClass.isAssignableFrom(endClass);
                //                doubleDir = doubleDir && !edgeClass.isAssignableFrom(PartOfBeziehung.class);
                //                doubleDir = doubleDir && !edgeClass.isAssignableFrom(Composition.class);
                //                doubleDir = doubleDir && !ModelConstants.isDoubleMeaningEdge(edgeClass);

                if (ModelConstants.isAlwaysDoubleConnectedEdge(edgeClass)) {
                    ((Doppelkante) edge).setDirection(Doppelkante.DOUBLE);
                } else {
                    int dir = Doppelkante.FORWARD;
                    //AXS: auch am 21.06.2017 geändert
                    //                    if (!(edgeStartClass.isAssignableFrom(startClass) && edgeEndClass.isAssignableFrom(endClass))) {
                    if (!Kante.isConnectingForward(edgeClass, startElement.getClass(), endElement.getClass())) {
                        ModelElement dummy = startElement;
                        startElement = endElement;
                        endElement = dummy;
                        dir = Doppelkante.BACKWARD;
                    }
                    ((Doppelkante) edge).setDirection(dir);
                }

                edge.setKnotsAndInsert(startElement, startElementEdgeIndex, endElement, endElementEdgeIndex);

                if (edge.getStart() != null && edge.getEnd() != null) {
                    kac = new EdgeContainer(edge, doc);
                    edge.setName(doc.getNextNewName(edge.getClass()), false);

                    addEdge(kac, kac.layerFor(), pid);
                } else {
                    if (edge.getStart() == null && edge.getEnd() != null) {
                        edge.getEnd().removeEdge(edge);
                    }
                    if (edge.getEnd() == null && edge.getStart() != null) {
                        edge.getStart().removeEdge(edge);
                    }
                }

                doc.addRedoCommand(GDCommands.LINK + " " + edgeClass.getName() + " " + edge.getHashString() + " " + startElement.getHashString() + " " + endElement.getHashString() + " " + startElementEdgeIndex + " " + endElementEdgeIndex, pid);
                doc.addUndoCommand(GDCommands.UNLINK + " " + startElement.getHashString() + " " + endElement.getHashString() + " " + startElementEdgeIndex, pid);

                //Falls bereits Beziehungen der anzulegenden Art bestehen und durch die neue Beziehung die Kardinalitäten
                //verletzt wären -> lösche solange bestehende Beziehungen, bis die Kardinaltitäten eingehalten werden
                //Dies muss nach dem Hinzufügen der anderen Undo-Komamndos erfolgen, sonst stimmt die Reihenfolge der Kommandos nicht.
                if (ensureConsistency) {
                    int maxElemCardinality = edge.isStartClass(startElement.getClass()) ? edge.getMaxStartToEndCardinality() : edge.getMaxEndToStartCardinality();
                    ArrayList<Kante> edgeList = startElement.getEdgesWith(edge.isStartClass(startElement.getClass()) ? edge.getEndClass() : edge.getStartClass(), edgeClass);
                    edgeList.remove(edge);
                    if (edgeList.size() > 0 && edgeList.size() == maxElemCardinality) {
                        deleteElement(edgeList.get(0), doc, pid);
                    }
                    maxElemCardinality = edge.isStartClass(endElement.getClass()) ? edge.getMaxStartToEndCardinality() : edge.getMaxEndToStartCardinality();
                    edgeList = endElement.getEdgesWith(edge.isStartClass(endElement.getClass()) ? edge.getEndClass() : edge.getStartClass(), edgeClass);
                    edgeList.remove(edge);
                    if (edgeList.size() > 0 && edgeList.size() == maxElemCardinality) {
                        deleteElement(edgeList.get(0), doc, pid);
                    }
                }
            }
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            doc.undo(pid);
            return null;
        }

        doc.finish_transaction(pid);
        doc.distributeEvent(GraphDocument.DATA_CHANGED, pid);
        return edge;
    }

    //	public Kante link(String edgeClassName, String edgeHash, ModelElement me1, ModelElement me2, int direction, int edgeIndex, int pid) {
    //		if ((me1 == null) || (me2 == null))
    //			return null;
    //		if (me1 == me2)
    //			return null;
    //
    //		Kante edge = null;
    //		EdgeContainer kac = null;
    //
    //		Class<? extends ModelElement> edgeClassOrNull = ModelConstants.getClassForName(edgeClassName);
    //		Class<? extends Kante> edgeClass = edgeClassOrNull==null ? null : edgeClassOrNull.asSubclass(Kante.class);
    //
    //		if (edgeClass!=null && !Kante.isConnecting(edgeClass, me1.getClass(), me2.getClass()))
    //			return null;
    //
    //		doc.start_transaction(pid);
    //
    //		try {
    //			if (edgeClass==null) {
    //				Class<? extends Kante>[] edgeClasses = ModelConstants.getEdgeTypes(me1.getClass(), me2.getClass());
    //				if ((edgeClasses == null) || (edgeClasses.length == 0))
    //				    return null;
    //				edgeClass = edgeClasses[0];
    //				if (edgeClasses.length > 1) {
    //					JPanel messagePanel = new JPanel();
    //					messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
    //					ButtonGroup buttonGroup = new ButtonGroup();
    //					for (int i = 0; i < edgeClasses.length; i++) {
    //						JRadioButton b = new JRadioButton(ModelConstants.getForwardMetaAssociationName(edgeClasses[i]));
    //						b.setActionCommand(edgeClasses[i].getName());
    //						messagePanel.add(b);
    //						buttonGroup.add(b);
    //						if (i == 0)
    //							b.setSelected(true);
    //					}
    //					JOptionPane optionPane = new JOptionPane(messagePanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
    //					JDialog dialog = optionPane.createDialog(Tool3lgm.tool, Tool3lgmConstants.getResString("choose_trace"));
    //					dialog.setVisible(true);
    //					edgeClassName = buttonGroup.getSelection().getActionCommand();
    //					edgeClass = ModelConstants.getClassForName(edgeClassName).asSubclass(Kante.class);
    //				}
    //			}
    //
    //			edge = me1.getEdgeTo(me2, edgeClass, edgeIndex);
    //
    //			if (edge!=null) {
    //				doc.finish_transaction(pid);
    //				return edge;
    //			}
    //
    //			edge = me1.getEdgeFrom(me2, edgeClass, edgeIndex);
    //			if (edge!=null) {
    //				if (edge instanceof Doppelkante)
    //					 ((Doppelkante) edge).setDirection(Doppelkante.DOUBLE);
    //			}else {
    //
    //				try {
    //					edge = (Kante) edgeClass.newInstance();
    //				} catch (Exception e) {
    //					Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
    //					doc.undo(pid);
    //					return null;
    //				}
    //
    //				if ((edgeHash != null) && (!edgeHash.equals("")))
    //					edge.setHashString(edgeHash);
    //
    //				if (edgeIndex != GDCommands.INVALID_EDGE_INDEX)
    //					edge.setKnotsAndInsert(me1, edgeIndex, me2);
    //				else
    //					edge.setKnots(me1, me2);
    //
    //				if ((edge.getStart() != null) && (edge.getEnd() != null)) {
    //					kac = new EdgeContainer(edge, doc);
    //					edge.setName(doc.getNextNewName(edge), false);
    //
    //					addEdge(kac, kac.layerFor(), pid);
    //				} else {
    //					if ((edge.getStart() == null) && (edge.getEnd() != null))
    //						edge.getEnd().removeEdge(edge);
    //					if ((edge.getEnd() == null) && (edge.getStart() != null))
    //						edge.getStart().removeEdge(edge);
    //				}
    //
    //				doc.addRedoCommand(GDCommands.LINK + " " + edgeClass.getName() + " " + edge.getHashString() + " " + me1.getHashString() + " " + me2.getHashString() + " " + edgeIndex, pid);
    //				doc.addUndoCommand(GDCommands.UNLINK + " " + me1.getHashString() + " " + me2.getHashString() + " " + edgeIndex, pid);
    //
    //				//Falls bereits Beziehungen der anzulegenden Art bestehen und durch die neue Beziehung die Kardinalitäten
    //				//verletzt wären -> lösche solange bestehende Beziehungen, bis die Kardinaltitäten eingehalten werden
    //				//Dies muss nach dem Hinzufügen der anderen Undo-Komamndos erfolegn, sonst stimmt die Reihenfolge der Kommando nicht.
    //				int maxElemCardinality = edge.isStartClass(me1.getClass()) ? edge.getMaxStartToEndCardinality() : edge.getMaxEndToStartCardinality();
    //				ArrayList<Kante> edgeList = me1.getEdgesWith(edge.isStartClass(me1.getClass()) ? edge.getEndClass() : edge.getStartClass(), edgeClass);
    //				edgeList.remove(edge);
    //				if (edgeList.size()>0 && edgeList.size() == maxElemCardinality)
    //					deleteElement(edgeList.get(0), doc, pid);
    //				maxElemCardinality = edge.isStartClass(me2.getClass()) ? edge.getMaxStartToEndCardinality() : edge.getMaxEndToStartCardinality();
    //				edgeList = me2.getEdgesWith(edge.isStartClass(me2.getClass()) ? edge.getEndClass() : edge.getStartClass(), edgeClass);
    //				edgeList.remove(edge);
    //				edgeList.remove(edge);
    //				if (edgeList.size()>0 && edgeList.size() == maxElemCardinality)
    //					deleteElement(edgeList.get(0), doc, pid);
    //
    //
    //			}
    //		} catch (Exception e) {
    //			Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
    //			doc.undo(pid);
    //			return null;
    //		}
    //
    //
    //		doc.finish_transaction(pid);
    //		doc.distributeEvent(GraphDocument.DATA_CHANGED, pid);
    //		return edge;
    //	}

    /**
     * @param knothash1
     * @param knothash2
     * @param edgeIndex
     * @param pid
     */
    public void unlink(final String knothash1, final String knothash2, final int edgeIndex, final int pid) {
        unlink(knothash1, knothash2, null, edgeIndex, pid);
    }

    /**
     * @param knothash1
     * @param knothash2
     * @param edgeClass
     * @param edgeIndex
     * @param pid
     */
    public void unlink(final String knothash1, final String knothash2, final Class<? extends Kante> edgeClass, final int edgeIndex, final int pid) {
        ModelElement me1 = doc.findElementCoded(knothash1);
        ModelElement me2 = doc.findElementCoded(knothash2);
        unlink(me1, me2, edgeClass, edgeIndex, pid);
    }

    /**
     * @param k1
     * @param k2
     * @param pid
     */
    public final void unlink(final ModelElement k1, final ModelElement k2, final int pid) {
        unlink(k1, k2, GDCommands.INVALID_EDGE_INDEX, pid);
    }

    /**
     * @param k1
     * @param k2
     * @param edgeIndex
     * @param pid
     */
    public final void unlink(final ModelElement k1, final ModelElement k2, final int edgeIndex, final int pid) {
        unlink(k1, k2, GDCommands.INVALID_EDGE_CLASS, edgeIndex, pid);
    }

    /**
     * @param k1
     * @param k2
     * @param edgeClass
     * @param pid
     */
    public final void unlink(final ModelElement k1, final ModelElement k2, final Class<? extends Kante> edgeClass, final int pid) {
        unlink(k1, k2, edgeClass, GDCommands.INVALID_EDGE_INDEX, pid);
    }

    /**
     * @param k1
     * @param k2
     * @param edgeClass
     * @param edgeIndex
     * @param pid
     */
    public final void unlink(final ModelElement k1, ModelElement k2, final Class<? extends Kante> edgeClass, final int edgeIndex, final int pid) {
        if (k1 == null || k2 == null) {
            return;
        }

        Kante ka = null;
        ArrayList<Kante> edges = k1.getEdgesWith(k2, edgeClass, edgeIndex);

        if (edges.size() == 0) {
            return;
        } else if (edges.size() == 1) {
            ka = edges.get(0);
        } else {
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            ButtonGroup buttonGroup = new ButtonGroup();
            for (int i = 0; i < edges.size(); i++) {
                JRadioButton b = new JRadioButton(ModelConstants.getForwardMetaAssociationName(edges.get(i).getClass()));
                if (i == 0) {
                    b.setSelected(true);
                }
                b.setActionCommand(new Integer(i).toString());
                messagePanel.add(b);
                buttonGroup.add(b);
            }
            JOptionPane optionPane = new JOptionPane(messagePanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
            JDialog dialog = optionPane.createDialog(Static.getMainFrame(), Tool3lgmConstants.getResString("choose_trace"));
            dialog.setVisible(true);
            int index = Integer.parseInt(buttonGroup.getSelection().getActionCommand());
            ka = edges.get(index);
        }

        if (k2 == k1) {
            k2 = ka.getStart();
        }

        doc.start_transaction(pid);
        doc.addRedoCommand(GDCommands.UNLINK + " " + k1.getHashString() + " " + k2.getHashString() + " " + (edgeClass == null ? "null" : edgeClass.getName()) + " " + edgeIndex, pid);
        //Undo-Kommando wird in deleteElement gesetzt (s. u.)

        //nur bei Kanten mit doppelter bedeutung kann man in bestimmten Richtungen unlinken. Bei allen anderen
        //ist die Richtung egal und das Unlinken ist das Löschen der Kante
        if (ModelConstants.isDoubleMeaningEdge(ka.getClass())) {
            Doppelkante dlk = (Doppelkante) ka;
            if (dlk.getDirection() == Doppelkante.DOUBLE) {
                if (dlk.getStart() == k1) {
                    doc.addUndoCommand(GDCommands.LINK + " " + dlk.getClass().getName() + " " + dlk.getHashString() + " " + k1.getHashString() + " " + k2.getHashString() + " " + k1.getEdgeIndex(dlk) + " " + k2.getEdgeIndex(dlk), pid);
                    dlk.setDirection(Doppelkante.BACKWARD);
                } else {
                    doc.addUndoCommand(GDCommands.LINK + " " + dlk.getClass().getName() + " " + dlk.getHashString() + " " + k2.getHashString() + " " + k1.getHashString() + " " + k2.getEdgeIndex(dlk) + " " + k1.getEdgeIndex(dlk), pid);
                    dlk.setDirection(Doppelkante.FORWARD);
                }
            } else {
                deleteElement(ka, doc, pid);
            }
        } else {
            deleteElement(ka, doc, pid);
        }
        doc.finish_transaction(pid);
        doc.distributeEvent(GraphDocument.DATA_CHANGED, pid);
    }

    //ENDE LINK //
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //#############################################################################################//
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param hashString1
     * @param hashString2
     * @param source
     * @param pid
     */
    public boolean join(final String hashString1, final String hashString2, final GraphDocument source, final int pid) {
        ModelElement modelElement1 = doc.findElementCoded(hashString1);
        ModelElement modelElement2 = doc.findElementCoded(hashString2);

        if (modelElement1 == null || modelElement2 == null || modelElement1 == modelElement2) {
            return false;
        }

        //prüfen, ob es sich um Knoten gleichen Typs handelt (nur diese können vereint werden)
        if (!(modelElement1 instanceof Knoten && modelElement2 instanceof Knoten)) {
            JOptionPane.showMessageDialog(Static.getMainFrame(), Tool3lgmConstants.getResString("nur_knoten_sel"), Tool3lgmConstants.getResString("tool3lgm"), JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        Knoten knoten1 = (Knoten) modelElement1;
        Knoten knoten2 = (Knoten) modelElement2;
        if (knoten1.getClass() != knoten2.getClass()) {
            JOptionPane.showMessageDialog(null, Tool3lgmConstants.getResString("nur_gleiche_sel"), Tool3lgmConstants.getResString("tool3lgm"), JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        //Beginne umhängen der Kanten
        doc.start_transaction(pid);
        for (Szenario s : szenarios) {
            s.start_transaction(pid, false);
        }

        //Namen und Beschreibung des zu löschenden Knoten an den verbleibenden anhängen
        //und ExtIDs und benutzerdef. Eigenschaftsfelder zusammenführen
        knoten2.join(knoten1, false);
        //knoten2.createNameWithSzens(doc);

        for (Class<? extends ModelElement> clazz : ModelConstants.getSubordinatedJoinbleTypes(knoten2.getClass())) {
            List<ModelElement> sjt1 = knoten1.getConnectedElements(clazz);
            List<ModelElement> sjt2 = knoten2.getConnectedElements(clazz);
            if (sjt1.size() > 0 && sjt2.size() > 0) {
                ModelElement me1 = sjt1.get(0);
                ModelElement me2 = knoten2.getConnectedElements(clazz).get(0);
                join(me1.getHashString(), me2.getHashString(), source, pid);
            }
        }

        //Das hier ist Hardcore, weil hier das IterableObject zurück auf List gecastet wird-> eigentlich müsste sich Kante selbst irgenwie darum kümmern!
        List<Kante> kantenVector1 = (List<Kante>) knoten1.getEdges();//ArrayList der Kanten des zu löschendn Knotens
        List<Kante> kantenVector2 = (List<Kante>) knoten2.getEdges();//ArrayList der Kanten des verbleibenden Knotens
        ModelElement startKnoten, endKnoten;

        //für jede Kante vom zu löschenden Knoten
        while (kantenVector1.size() > 0) {
            Kante kante = kantenVector1.get(0);
            startKnoten = kante.getStart(); //Startknoten der zu übernehmenden Kante merken
            endKnoten = kante.getEnd(); //Endknoten -"-

            //zu löschenden Knoten durch den verbleibenden ersetzen
            if (startKnoten == knoten1) {
                startKnoten = knoten2;
                endKnoten = kante.getEnd();
            } else {
                startKnoten = kante.getStart();
                endKnoten = knoten2;
            }

            boolean deleteKante = false;
            if (startKnoten == endKnoten) {
                deleteKante = true;
            } else {
                //abfangen, ob im verbleibenden Knoten an gleicher Stelle schon eine Kante vorkommt, Kante testKante = new Kante(startKnoten, endKnoten, false);
                Kante testKante;
                try {
                    testKante = kante.getClass().newInstance();
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    continue;
                }
                testKante.setKnots(startKnoten, endKnoten, false);
                //TODO:AXS: ich glaube hier fliegen Kanten raus, die in unterschiedliche Richtungen zeigen, weil isEqualTo nur die Elemente und die Kanteklasse prüft
                for (int i = 0; i < kantenVector2.size(); i++) {
                    //für jede Kante des verbleibenden Elementes prüfen, ob umzuhängende Kante und eine Kante in
                    // kantenVector2 dieselben Elemente verbindet
                    if (kantenVector2.get(i).isEqualTo(testKante)) {
                        deleteKante = true;
                        break;
                    }
                }
            }

            if (deleteKante) { //wenn die Kante doppelt vorkommen würde

                deleteElement(kante, doc, pid);
                //				doc.removeEdge(kante, pid);//Kante einfach
                // komplett löschen
            } else { //Kante muss umgehängt werden
                knoten1.removeEdge(kante); //im zu löschenden Knoten die
                                           // Kante entfernen
                kante.setKnots(startKnoten, endKnoten);//die Kante wirklich an
                                                       // knoten2 binden
            }
        }

        for (Szenario szen : szenarios) {
            NodeContainer kc1 = (NodeContainer) knoten1.getContainer(szen);
            NodeContainer kc2 = (NodeContainer) knoten2.getContainer(szen);

            // jetzt umhängen aller Container von knoten1 auf knoten2 in allen Teilmodellen
            if (kc2 == null && kc1 != null) {
                //				szen.removeKnotContainer((NodeContainer) knoten1.getContainer(szen), pid);
                removeContainerFromSubmodel(knoten1.getContainer(szen), pid);
                kc1.setElement(knoten2);
                szen.getLayer(knoten2.layerFor()).add(kc1);
            }
            NodeContainer nc = null;
            if (kc2 != null) {
                nc = kc2;
            } else if (kc1 != null) {
                nc = kc1;
            }
            if (nc != null) {
                szen.createEdgeContainer(nc, szen, false, pid);
                nc.refreshText();
                // alle abhängigen Knoten vor den zusammengeführten stellen
                szen.start_transaction(TransactionManager.STANDARD_PID, false);
                szen.moveDependentKnotsUp(nc, TransactionManager.STANDARD_PID, false);
                szen.finish_transaction(TransactionManager.STANDARD_PID, false);
            }
        }

        deleteElement(knoten1, doc, pid);
        //		doc.removeNode((NodeContainer)knoten1.getContainer(doc), pid); //alle Kanten umgehängt -> wegfallenden Knoten komplett löschen

        for (Szenario szen : szenarios) {
            szen.finish_transaction(pid, false);
        }

        //Der TransaktionQueue wird einfach gelöscht. Das muss unbedingt mal geändert werden -> also alles richtig UNDO-/REDO-mässig
        tman.clearTransactionQueue();

        doc.finish_transaction(pid);
        distribute(GraphDocument.DATA_CHANGED, null, null, source, pid);
        return true;
    }

    public int getSzenarioCount() {
        return szenarios.size();
    }

    public Szenario getSzenario(final int index) {
        return szenarios.get(index);
    }

    public Iterable<Szenario> getSzenarios() {
        return szenarios;
    }

    public boolean hasSzenario(final Szenario szen) {
        return szenarios.contains(szen);
    }

    /**
     * @param szenHash
     * @return
     */
    public LGMGraphDocument getGraphDocumentCoded(final String szenHash) {
        if (doc.getHashString().equals(szenHash)) {
            return doc;
        }
        for (Szenario szen : szenarios) {
            if (szen.getHashString().equals(szenHash)) {
                return szen;
            }
        }
        return null;
    }

    /**
     * @param c
     */
    public void setChanged(final boolean c) {
        changed = c;
        lastModificationTime = System.currentTimeMillis();
    }

    /**
     * @return
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @return Returns the lastModificationTime.
     */
    public long getLastModificationTime() {
        return lastModificationTime;
    }

    /**
     * @param elementClass
     */
    public final void clearTextRightDown(final Class<? extends Knoten> elementClass) {
        if (doc != null) {
            doc.clearTextRightDown(elementClass);
            doc.clearLayerText(elementClass);
        }
        for (Szenario szen : szenarios) {
            szen.clearTextRightDown(elementClass);
            szen.clearLayerText(elementClass);
        }
    }

    /**
     * show wird in LGMGraphDocument ausgewertet und legt fest, ob ein Text bezüglich der
     * allgemeinen Redundanz über dem Layer angezeigt werden soll (momentan nur bei Aufgabe)
     *
     * @param elementClass
     * @param show
     */
    public final void computeRedundance(final Class<? extends Knoten> elementClass, final boolean show) {
        if (doc != null) {
            doc.getSimpleRedundancyAnalysis().computeRedundance(elementClass, show);
        }
        for (Szenario s : szenarios) {
            s.getSimpleRedundancyAnalysis().computeRedundance(elementClass, show);
        }
    }

    /**
     * Wenn die anderen Parameter aus der Methode <code>distribute(int, ElementContainer, LayerContainer, GraphDocument, int)</code> nicht angegeben
     * werden können, kann man hiermit ein allgemeines Ereignis feuern.
     *
     * @param bitmask
     */
    public final void distribute(final int bitmask) {
        distribute(bitmask, null, null, null, TransactionManager.STANDARD_PID);
    }

    /**
     * @param bitmask
     * @param last_elem
     * @param last_group
     * @param source
     * @param pid
     */
    public final void distribute(final int bitmask, final ElementContainer last_elem, final LayerContainer last_group, final GraphDocument source, final int pid) {
        if (isBulkMode()) {
            return;
        }
        if (source != null) {
            source.distributeEventIntern(bitmask, last_elem, last_group, pid);
        }
        distributeEventIntern(source, bitmask, last_elem, last_group, pid);
        if (doc != source) {
            doc.distributeEventIntern(bitmask, last_elem, last_group, pid);
        }
        for (Szenario s : szenarios) {
            if (s != source) {
                s.distributeEventIntern(bitmask, last_elem, last_group, pid);
            }
        }
    }

    /**
     * @return Hauptdokument der Collection
     */
    public LGMGraphDocument getMainGraphDocument() {
        return doc;
    }

    /**
     * @param iconPath
     * @return
     */
    public final String loadIcon(final File iconPath) {
        String iconKey = null;
        try {
            RandomAccessFile imf = new RandomAccessFile(iconPath, "r");
            byte[] img = new byte[(int) imf.length()];
            imf.read(img);
            iconKey = KeyOf(img);
            if (iconKey == null) {
                iconKey = "IMG_" + new Date().getTime() + iconCounter++ + ".gif";
                getIconTable().put(iconKey, img);
            }
            imf.close();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
        }
        return iconKey;
    }

    /**
     * @param entry
     * @return
     */
    private final String KeyOf(final byte[] entry) {
        for (String key : getIconTable().keySet()) {
            if (Arrays.equals(getIconTable().get(key), entry)) {
                return key;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public Hashtable<String, byte[]> getIconTable() {
        return iconTable;
    }

    /**
     * @param bm
     */
    public void setBulkMode(final boolean bm) {
        bulk_mode = bm;
    }

    /**
     * @return
     */
    public boolean isBulkMode() {
        return bulk_mode;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return setzt den Title auf dasselbe wie {@link #getName()}, aber ohne die Dateiendung
     */
    public String getTitle() {
        int lastPointIndex = name.lastIndexOf('.');
        String title = name;
        if (lastPointIndex > 0 && lastPointIndex < title.length() - 1) {
            String extension = title.substring(lastPointIndex + 1);
            if (Tool3lgmConstants.isExtension(extension)) {
                title = title.substring(0, lastPointIndex);
            }
        }
        return title;
    }

    /**
     *
     */
    private void createName() {
        int counter = 0;
        String newName = createName(counter);

        int collectionsCount = Static.getCollectionCount();
        if (collectionsCount < 0) {
            return;
        }

        GDCollection temp;
        for (int index = 0; index < collectionsCount; index++) {
            temp = Static.getCollection(index);
            if (temp.equals(this)) {
                continue;
            }
            if (!temp.getName().equals(newName)) {
                continue;
            }
            index = 0;
            newName = createName(++counter);
        }

        name = newName + (isReadOnly ? " " + Tool3lgmConstants.getResString("text_readOnly") : "");

        for (AbstractInternalFrame f : Static.getAllFrames()) {
            if (f.getCollection().equals(this)) {
                f.setTitle(getName());
                Static.getTool().getModelBrowserPanel().updateTitle(this);
            }
        }
    }

    /**
     * @param counter
     * @return
     */
    private String createName(final int counter) {
        if (getFile() == null) {
            return "<" + Tool3lgmConstants.getResString("unbenannt") + (counter > 0 ? " #" + counter : "") + ">";
        }
        return getFile().getName() + (isReadOnly ? " <" + Tool3lgmConstants.getResString("text_readOnly") + ">" : "") + (counter > 0 ? " #" + counter : "");
    }

    /**
     * Öffnet oder schliesst den Frame mit den Modellbeschreibungen
     */
    public void showDescriptionFrame(final boolean b) {
        if (b) {
            if (descriptionFrame == null) {
                descriptionFrame = new ModelPropertyDialog(this);
            }
            descriptionFrame.setVisible(true);
        } else {
            descriptionFrame.dispose();
            descriptionFrame = null;
        }
    }

    /**
     * build the String for saving the collection
     *
     * @return byte[] String with FileVersionInfo and toXMLString()
     */
    private byte[] getSaveString() {
        try {
            return ToolXMLParser.getCurrentVersionString().concat(toXMLString()).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.show(Log.FATAL, Tool3lgmConstants.getErrString("error_convert_utf8"));
        }
        return null;
    }

    /**
     * @author Thomas Rudert
     * @param preString
     *            der Tag wird mit diesen String eingerueckt
     * @return String der vollstaendige XML-Tag zu diesem Objekt
     */
    public String toXMLString() {
        StringBuilder xmlString = new StringBuilder("<modell_3lgm_2>" + "<header>" + "<title>" + XMLCharacterCoder.encodeString(name) + "</title>" + "<description>" + XMLCharacterCoder.encodeString(doc.getDescription()) + "</description>" + "<version>"
                + XMLCharacterCoder.encodeString(getFileVersion()) + "</version>" + "</header>" + userFieldDefinitions.toXMLString(true) + "<objects>");
        xmlString.append("<model>");
        appendUserFieldXMLString(xmlString);
        xmlString.append("</model>");
        for (LayerContainer lc : doc.layer) {
            for (NodeContainer kc : lc.getKnoten()) {
                try {
                    xmlString.append(kc.getElement().toXMLString());
                } catch (Exception ex) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("error"), ex);
                }
            }
            doc.sortKanten();
            for (EdgeContainer kc : lc.getKanten()) {
                try {
                    xmlString.append(kc.getElement().toXMLString());
                } catch (Exception ex) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("error"), ex);
                }
            }
            for (BendpointContainer kc : lc.getKnickpunkte()) {
                try {
                    xmlString.append(kc.getElement().toXMLString());
                } catch (Exception ex) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("error"), ex);
                }
            }
        }

        xmlString.append("</objects>");

        for (Szenario szen : szenarios) {
            xmlString.append(szen.toXMLString());
        }

        xmlString.append("<images>");

        for (String iconHashString : getIconTable().keySet()) {
            if (xmlString.indexOf(iconHashString) == -1) {
                continue;
            }
            xmlString.append("<bitmap type=\"gif/base64\" hash=\"" + iconHashString + "\">");
            byte[] icon = getIconTable().get(iconHashString);
            try {
                xmlString.append(Base64.encode(icon));
            } catch (Exception e) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            }
            xmlString.append("</bitmap>");
        }

        xmlString.append("</images></modell_3lgm_2>");

        return xmlString.toString();
    }

    /**
     * Speichert das Modell in der angegeben Datei im XML-Format (File wird
     * geschlossen)
     *
     * @param f
     *            Ziel-Datei beim Speichern
     */
    public void exportModel(final File f) {
        if (!Static.getTool().checkLicenses()) {
            return;
        }
        if (f.exists()) {
            f.delete();
        }
        try {
            RandomAccessFile file = new RandomAccessFile(f, "rw");
            file.writeBytes(ToolXMLParser.getCurrentVersionString() + toXMLString());
            file.close();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
        }
    }

    /**
     * Speichert das Modell in der angegeben Datei im XML-Format
     * (RandomAccessFile wird nicht geschlossen, um Datei zu sperren)
     *
     * @param f
     *            Ziel-Datei beim Speichern
     */
    public void saveXMLFile(final RandomAccessFile f) throws IOException {
        if (!Static.getTool().checkLicenses()) {
            return;
        }
        f.seek(0);
        f.setLength(0);
        f.writeBytes(ToolXMLParser.getCurrentVersionString() + toXMLString());
    }

    /**
     * COMMENTME
     */
    boolean interactive_mode = true;

    /**
     * @param flag
     */
    public void setInteractiveMode(final boolean flag) {
        interactive_mode = flag;
    }

    /**
     * @return
     */
    public boolean isInteractiveMode() {
        return interactive_mode;
    }

    /**
     * @param file
     */
    public void importModel(final File file) {
        importSzenarios(file, false);
    }

    /**
     * Importiert Szenarios aus einem anderem Modell in dieses Modell
     *
     * @param file
     *            Modelldatei, aus der Szenarios importiert werden sollen
     * @param chooseSzenarioDialog
     *            wenn <code>true</code> kann der
     */
    public void importSzenarios(final File file, final boolean chooseSzenarioDialog) {
        GDCollection collection = new GDCollection();

        Static.showProgressDialog();
        Static.setProgressDialogTitle(Tool3lgmConstants.getResString("load_model") + " " + file.getName());
        Static.setProgressDialogStatusLabel("read_progress");

        try {
            collection.setFile(file);
            collection.loadFromRAF(file);
        } catch (Exception exp) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
        }

        Static.closeProgressDialog();

        LGMGraphDocument[] importSzenarios = null;
        if (chooseSzenarioDialog) {
            importSzenarios = SzenarioDialog.showImportDialog(Static.getMainFrame(), collection);
        } else {
            importSzenarios = new LGMGraphDocument[collection.szenarios.size() + 1];
            importSzenarios[0] = collection.getMainGraphDocument();
            int i = 1;
            for (Szenario szenario : collection.szenarios) {
                importSzenarios[i++] = szenario;
            }
        }
        importSzenarios(importSzenarios, collection);
    }

    /**
     * @param importSzenarios
     * @param collection
     */
    private void importSzenarios(final LGMGraphDocument[] importSzenarios, final GDCollection collection) {

        Static.showProgressDialog();
        Static.setProgressDialogStatusLabel("importSzenario");
        int size = 0;
        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            LayerContainer lc = collection.doc.getLayer(ModelConstants.LAYERS[i]);
            size += lc.getKnotenCount() + lc.getKantenCount() + lc.getKnickpunkteCount();
        }

        /* ModellElemente, die kopiert werden müssen */
        ArrayList<ModelElement> elements = new ArrayList<ModelElement>(size);

        /* HashStrings aller Icons, die kopiert werden müssen */
        Set<String> bitmaps = new HashSet<String>();

        bitmaps = new HashSet<String>(collection.getIconTable().size());
        HashSet<UserField> userFields = new HashSet<UserField>();

        collection.resolveCopyDependencies(importSzenarios, elements, bitmaps, userFields);
        for (UserField uf : userFields) {
            userFieldDefinitions.add(uf);
        }

        for (String bmHash : bitmaps) {
            iconTable.put(bmHash, collection.getIconTable().get(bmHash));
        }

        for (ModelElement element : elements) {
            element.removeAllContainer();
            ElementContainer container = element.createContainer(doc);
            doc.getLayer(element.layerFor()).add(container);
        }

        for (int szenarioIndex = 0; szenarioIndex < importSzenarios.length; szenarioIndex++) {
            LGMGraphDocument importDoc = importSzenarios[szenarioIndex];
            if (!(importDoc instanceof Szenario)) {
                continue;
            }
            Szenario newSzenario = createSzenario(importDoc.getTitle() + " (import)", false, importDoc.description, importDoc.hashString, false, TransactionManager.STANDARD_PID);
            for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
                LayerContainer importLayerContainer = importDoc.getLayer(ModelConstants.LAYERS[i]);
                newSzenario.getLayer(ModelConstants.LAYERS[i]).set3LGMLayout(importLayerContainer.get3LGMLayout());
                for (NodeContainer importKC : importLayerContainer.getKnoten()) {
                    ModelElement element = doc.findKnotenCoded(importKC.getElement().getHashString());
                    ElementContainer container = element.createContainer(newSzenario);
                    container.set3LGMLayout(importKC.get3LGMLayout());
                    container.setE3LGMLayout(importKC.getE3LGMLayout());
                    container.setNE3LGMLayout(importKC.getNE3LGMLayout());
                    container.setExpanded(importKC.isExpanded());
                    newSzenario.getLayer(ModelConstants.LAYERS[i]).add(container);
                    container.refreshText();
                }

                for (EdgeContainer importKC : importLayerContainer.getKanten()) {
                    ModelElement element = doc.findKanteCoded(importKC.getElement().getHashString());
                    if (element == null) {
                        continue;
                    }
                    ((Kante) element).decodeHashStrings(doc);
                    ElementContainer container = element.createContainer(newSzenario);
                    container.set3LGMLayout(importKC.get3LGMLayout());
                    container.setE3LGMLayout(importKC.getE3LGMLayout());
                    container.setNE3LGMLayout(importKC.getNE3LGMLayout());
                    container.setExpanded(importKC.isExpanded());
                    newSzenario.getLayer(ModelConstants.LAYERS[i]).add(container);
                    container.refreshText();
                }

                for (BendpointContainer importKC : importLayerContainer.getKnickpunkte()) {
                    ModelElement element = doc.findKnickpunktCoded(importKC.getElement().getHashString());
                    EdgeContainer kc = newSzenario.findEdgeContainerCoded(((Knickpunkt) element).getKantenHash());
                    if (kc == null) {
                        continue;
                    }
                    element.addEdge(kc.getEdge());
                    ElementContainer container = element.createContainer(newSzenario);
                    container.set3LGMLayout(importKC.get3LGMLayout());
                    container.setE3LGMLayout(importKC.getE3LGMLayout());
                    container.setNE3LGMLayout(importKC.getNE3LGMLayout());
                    container.setExpanded(importKC.isExpanded());
                    newSzenario.getLayer(ModelConstants.LAYERS[i]).add(container);
                    container.refreshText();
                    BendpointContainer knC = (BendpointContainer) container;
                    kc.setKnickpunkt(knC, knC.getKnickpunktKnoten().getIndex());
                    kc.computeBorderPoints();
                }
            }

            Static.getTool().createSzenarioFrame(newSzenario);
        }
        Static.closeProgressDialog();
        userFieldDefinitions.hasCrossReferences();
        distribute(GraphDocument.DATA_CHANGED);
    }

    /**
     * exportiert die übergebenen Szenarios in eine neue Datei
     *
     * @param export Array mit den zu exportierenden Szenarios
     * @param file Datei in die exportiert werden soll
     */
    public void exportSzenarios(final Szenario[] export, final File file) {
        if (!Static.getTool().checkLicenses()) {
            return;
        }
        int size = 0;
        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            LayerContainer lc = doc.getLayer(ModelConstants.LAYERS[i]);
            size += lc.getKnotenCount() + lc.getKantenCount() + lc.getKnickpunkteCount();
        }

        /* hastStrings aller ModellElemente, die kopiert werden müssen */
        ArrayList<ModelElement> elements = new ArrayList<ModelElement>(size);

        /* HashStrings aller Icons, die kopiert werden müssen */
        HashSet<String> bitmaps = new HashSet<String>(iconTable.size());

        /* HashStrings aller benutzdefinierten Eigenschaftsfelder, die mit kopiert werden müssen */
        HashSet<UserField> userFields = new HashSet<UserField>();

        resolveCopyDependencies(export, elements, bitmaps, userFields);

        /* Datei erstellen und gefundenen Element schreiben */
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(0);
            raf.setLength(0);
            raf.writeBytes(ToolXMLParser.getCurrentVersionString());
            raf.writeBytes("<modell_3lgm_2><header><title>" + XMLCharacterCoder.encodeString(name + "(export)") + "</title>" + "<description>" + XMLCharacterCoder.encodeString(doc.getDescription()) + "</description>" + "<version>"
                    + XMLCharacterCoder.encodeString(getFileVersion()) + "</version></header>" + userFieldDefinitions.getCopyString(userFields) + "<objects>");

            try {
                for (ModelElement me : elements) {
                    raf.writeBytes(me.toXMLString());
                }
            } catch (NullPointerException e) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            }

            raf.writeBytes("</objects>");

            /* xmlString der Szenarios schreiben */
            for (int szenarioIndex = 0; szenarioIndex < export.length; szenarioIndex++) {
                raf.writeBytes(export[szenarioIndex].toXMLString());
            }

            /* xmlString der Icons schreiben */

            raf.writeBytes("<images>");

            for (String hashString : bitmaps) {
                raf.writeBytes("<bitmap type=\"gif/base64\" hash=\"" + hashString + "\">" + Base64.encode(getIconTable().get(hashString)) + "</bitmap>");
            }

            raf.writeBytes("</images></modell_3lgm_2>");
            raf.close();
        } catch (IOException e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
        }
    }

    /**
     * Sucht alle Element und Icons, die kopiert werden müssen
     *
     * @param export
     *            Array von Szenarios, die zu kopieren sind
     * @param elements
     *            Set, in welches die zu kopierenden Element geschrieben werden
     * @param bitmaps
     *            Set, in welches die HashStrings der zu kopierenden Icons geschrieben werden
     * @param userFields
     *            Set, in welches die zu kopierenden benutzdefinierten Eigenschaftsfelder geschrieben werden
     */
    private void resolveCopyDependencies(final GraphDocument[] export, final ArrayList<ModelElement> elements, final Set<String> bitmaps, final Set<UserField> userFields) {
        /* alle übergebenen Szenarios durchgehen und copyDependcies auflösen */
        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            LayerContainer lc = doc.getLayer(ModelConstants.LAYERS[i]);
            for (int knotenIndex = 0; knotenIndex < lc.getKnotenCount(); knotenIndex++) {
                Knoten knoten = lc.getNodeContainer(knotenIndex).getKnoten();
                for (int szenarioIndex = 0; szenarioIndex < export.length; szenarioIndex++) {
                    if (export[szenarioIndex].isMyElement(knoten)) {
                        ElementContainer container = knoten.getContainer(export[szenarioIndex]);
                        if (!elements.contains(knoten)) {
                            elements.add(knoten);
                            String iconName = ((NodeContainer) container).getIconString();
                            if (iconName != null) {
                                bitmaps.add(iconName);
                            }
                            userFields.addAll(knoten.getUserFieldInputValueKeys());
                            resolveCopyDependencies(knoten, elements, userFields);
                        }
                    }
                }
            }
            for (int kantenIndex = 0; kantenIndex < lc.getKantenCount(); kantenIndex++) {
                Kante kante = lc.getEdgeContainer(kantenIndex).getEdge();
                for (int szenarioIndex = 0; szenarioIndex < export.length; szenarioIndex++) {
                    if (export[szenarioIndex].isMyElement(kante)) {
                        if (!elements.contains(kante)) {
                            elements.add(kante);
                            userFields.addAll(kante.getUserFieldInputValueKeys());
                            resolveCopyDependencies(kante, elements, userFields);
                        }
                    }
                }
            }
            for (int knpIndex = 0; knpIndex < lc.getKnickpunkteCount(); knpIndex++) {
                Knickpunkt knp = lc.getBendpointContainer(knpIndex).getKnickpunktKnoten();
                for (int szenarioIndex = 0; szenarioIndex < export.length; szenarioIndex++) {
                    if (export[szenarioIndex].isMyElement(knp)) {
                        ElementContainer container = knp.getContainer(export[szenarioIndex]);
                        if (!elements.contains(knp)) {
                            elements.add(knp);
                            if (container.get3LGMLayout() != null && container.get3LGMLayout().icon != null && container.get3LGMLayout().icon != "") {
                                bitmaps.add(container.get3LGMLayout().icon);
                            }
                            userFields.addAll(knp.getUserFieldInputValueKeys());
                            // resolveCopyDependencies(knp,
                            // elements,userFields);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param elements ArrayList with ElementContainer
     * @param result ArrayList with hastStrings
     * @param userFields
     */
    public void resolveCopyDependencies(final Collection<ElementContainer> elements, final ArrayList<ModelElement> result, final Set<UserField> userFields) {
        for (ElementContainer ec : elements) {
            ModelElement me = ec.getElement();
            if (!result.contains(me)) {
                if (!(me instanceof Knickpunkt)) {
                    result.add(me);
                    userFields.addAll(me.getUserFieldInputValueKeys());
                    resolveCopyDependencies(me, result, userFields);
                }
            }
        }
    }

    /**
     * sucht alle Element, die beim kopieren eines Knotens ebenfalls kopiert werden sollen (rekursiv, auch für die gefundenen Element)
     *
     * @param knoten der dessen abhängige Element gefunden werden sollen
     * @return HashSet mit den HashStrings der gefundenen Elementen
     */
    private void resolveCopyDependencies(final ModelElement me, final ArrayList<ModelElement> elements, final Set<UserField> userFields) {
        if (me instanceof Knickpunkt) {
            return;
        }

        if (me instanceof Kante) {
            for (BendpointContainer kpC : doc.getLayer(me.layerFor()).getKnickpunkte()) {
                Knickpunkt kp = kpC.getKnickpunktKnoten();
                String kantenHash = kp.getKantenHash();
                if (kantenHash != null && kantenHash.equals(me.getHashString())) {
                    if (!elements.contains(kp)) {
                        elements.add(kp);
                    }
                }
            }
            Kante edge = (Kante) me;
            ModelElement start = edge.getStart();
            if (!elements.contains(start)) {
                elements.add(start);
                userFields.addAll(start.getUserFieldInputValueKeys());
                resolveCopyDependencies(start, elements, userFields);
            }
            ModelElement end = edge.getEnd();
            if (!elements.contains(end)) {
                elements.add(end);
                userFields.addAll(end.getUserFieldInputValueKeys());
                resolveCopyDependencies(end, elements, userFields);
            }
        }

        for (Class<? extends ModelElement> elemClass : me.getCopyDependencies()) {
            for (ElementContainer ec : me.getConnectedContainer(elemClass, doc)) {
                ModelElement connected = ec.getElement();
                if (!elements.contains(connected)) {
                    elements.add(connected);
                    userFields.addAll(connected.getUserFieldInputValueKeys());
                    resolveCopyDependencies(connected, elements, userFields);
                }

                for (Kante e : me.getEdgesWith(connected)) {
                    if (!elements.contains(e)) {
                        elements.add(e);
                        userFields.addAll(e.getUserFieldInputValueKeys());
                        resolveCopyDependencies(e, elements, userFields);
                    }
                }
            }
        }
        //elements wird in der Schleife vergrößert -> nicht über den Iterator gehen
        for (int i = 0; i < elements.size(); i++) {
            ModelElement m = elements.get(i);
            for (Kante ka : me.getEdgesWith(m)) {
                if (!elements.contains(ka)) {
                    elements.add(ka);
                    userFields.addAll(ka.getUserFieldInputValueKeys());
                    resolveCopyDependencies(ka, elements, userFields);
                }
            }
        }

    }

    //	/*
    //	nach getCopyDependencies() suchen
    //
    //	Aufgabe
    //	AufOrgKombination
    //
    //Organisationseinheit
    //
    //
    //Input: Liste aller Elemente, die kopiert werden sollen
    //
    //Rekursive Funktion mit der Liste aller bereits kopierten Elemente
    //
    //
    //
    //
    //Beim Kopieren muss man immer eine Liste aller bereits neu angelegten Elemente mitführen und damit evtl. Kreise prüfen
    //
    //lege neues Element an
    //für alle Verbindungen des alten Elementes
    //	prüfe ob eine weitere Verbindung dieser Art die Card überschreitet
    //	wenn nein
    //		lege neue Verbindung zum gleichen Element an
    //	wenn ja
    //		kopiere das ganze Element rekursiv
    //
    //*/

    /**
     * gibt String mit Versionsdaten der Datei zurück<br>
     * setzt sich zusammen aus fileVersion_Benutzername_currentTimeMillis()
     *
     * @return String mit Versionsdaten der Datei
     */
    private String getFileVersion() {
        return (++fileVersion) + "_" + System.getProperty("user.name") + "_" + System.currentTimeMillis();
    }

    /**
     * setzt die int-Variable mit der Dateiversion
     *
     * @param String der dim Aufbau dem Rueckgabe-String der Methode getFileVersion() gleicht
     */
    public void setFileVersion(final String string) {
        try {
            fileVersion = Integer.parseInt(string.substring(0, string.indexOf('_')));
        } catch (Exception exp) {
            //			Log.show(Log.ERROR, Tool3lgmConstants.getErrorString("FehlerAllgemein"), exp);
        }
    }

    /**
     * @param file
     */
    public void loadClipboard(final File file) {
        //		System.err.println(file);
        try {
            setBulkMode(true);
            FileInputStream clipStream = new FileInputStream(file);
            if (LgmXMLParser.isXMLFile(clipStream) && ToolXMLParser.isParseAbleFileVersion(clipStream)) {
                clipStream.getChannel().position(0);
                loadXMLFile(clipStream, true);
            }

            clipStream.close();
            setBulkMode(false);
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
        }
    }

    public void loadFile(final InputStream istream) {
        try {
            setBulkMode(true);
            loadXMLFile(istream, true);
            setBulkMode(false);
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
        }
    }

    /**
     * @return
     */
    public int getCopyAndPaste() {
        return copyAndPaste;
    }

    /**
     * @param i
     */
    public void setCopyAndPaste(final int i) {
        copyAndPaste = i;
    }

    /**
     * set the file modelElement for this collection
     *
     * @param _file the new File for this collection
     * @return boolean with false, if file is shared (--> readOnly) otherwise true
     * @author Thomas Rudert
     */
    public boolean setFile(final File _file) throws IOException {
        if (file != null && file.equals(_file)) {
            return true;
        }

        if (randomAccessFile != null) {
            if (lock != null) {
                lock.release();
            }
            randomAccessFile.close();
            file = null;
        }

        lockSupported = Tool3lgmConstants.lockSupportedByFileSystem(_file);

        RandomAccessFile raf = null;
        boolean copiedToUserDir = false;
        try {
            raf = new RandomAccessFile(_file, "rw");
        } catch (IOException e) {
            File writableFile = new File(Tool3lgmConstants.USER_HOME_DIR_NAME + "/3LGM2Tool", _file.getName());
            FileHandler.copyFile(_file, writableFile);
            setFile(writableFile);
            copiedToUserDir = true;
        }
        if (copiedToUserDir) {
            return true;
        }
        if (lockSupported) {
            lock = raf.getChannel().tryLock(0, Long.MAX_VALUE, true);
            if (lock == null) {
                return false;
            }
        }

        file = _file;
        randomAccessFile = raf;

        createName();

        return !isReadOnly;
    }

    /**
     * sets flag isZipFile<br/>
     * if isZipFile is true, collection will be saved into packed zip-File, when saveToFile() is called next time
     *
     * @param _zipFile new value for isZipFile
     * @author Thomas Rudert
     */
    public void setSaveAsZipFile(final boolean _zipFile) {
        isZipFile = _zipFile;
    }

    /**
     * @return
     * @throws Exception
     */
    public boolean loadFromRAF() throws Exception {
        return loadFromRAF(null);
    }

    /**
     * Load collection from file which is specified in field file
     *
     * @return true if reading was successful
     * @throws Exception; throws all exceptions happens during reading
     * @author Thomas Rudert
     */
    public boolean loadFromRAF(final File file) throws Exception {
        Static.getTool().setCursor(Tool3lgmConstants.getWaitCursor());

        RandomAccessFile randomAccessFile;
        if (file != null) {
            randomAccessFile = new RandomAccessFile(file, "rw");
        } else {
            randomAccessFile = this.randomAccessFile;
        }
        setBulkMode(true);
        boolean readingSuccessful = false;
        try {
            randomAccessFile.seek(0);
            String line = randomAccessFile.readLine();
            if (line != null) {
                LGMInputStream fis = new LGMInputStream(randomAccessFile.getFD());
                if (line.startsWith("<!--ziped Tool3lgmFile-->")) {
                    readingSuccessful = loadZipFile(fis);
                    if (readingSuccessful) {
                        isZipFile = true;
                    }
                } else if (line.startsWith("PK")) {
                    fis.getChannel().position(0);
                    readingSuccessful = loadZipFile(fis);
                    if (readingSuccessful) {
                        isZipFile = true;
                    }
                } else {
                    fis.getChannel().position(0);
                    readingSuccessful = loadFromFileInputStream(fis);
                    if (readingSuccessful) {
                        isZipFile = false;
                    }
                }
                fis.close();
            } else {
                randomAccessFile.close();
                throw new IOException("Could not read file...");
            }
        } catch (Exception e) {
            setBulkMode(false);
            if (file != null) {
                randomAccessFile.close();
            }
            Log.show(Log.FATAL, Tool3lgmConstants.getErrString("FehlerAllgemein") + e, e);
        }

        if (file != null) {
            randomAccessFile.close();
        }
        setBulkMode(false);
        Static.getTool().setCursor(Tool3lgmConstants.getNormalCursor());
        return readingSuccessful;

    }

    /**
     * load collection from packed zipFile
     *
     * @param fileStream the FileInputStream to the file which will be read
     * @return true, if reading was successful
     * @throws IOException if something wrong with the FileInputStream or the zip-format
     * @author Thomas Rudert
     */
    public boolean loadZipFile(final InputStream fileStream) throws IOException {
        ZipInputStream zipStream = new ZipInputStream(fileStream) {
            @Override
            public void close() {
            }
        };
        zipStream.getNextEntry();
        boolean retVal = loadXMLFile(zipStream, false);
        //		zipStream.close();
        return retVal;
    }

    /**
     * load collection from (not packed) file
     *
     * @param fileStream the FileInputStream to the File to load
     * @return true, if reading was successful
     * @throws IOException
     * @throws LGMVersionException, if file-version is not readable
     * @throws XMLVersionException, if xml-version is not readable
     * @throws FileNotFoundException
     * @throws DataFormatException
     * @author Thomas Rudert
     */
    private boolean loadFromFileInputStream(final FileInputStream fileStream) throws IOException, LGMVersionException, XMLVersionException, FileNotFoundException {

        if (!LgmXMLParser.isXMLFile(fileStream) || !ToolXMLParser.isParseAbleFileVersion(fileStream)) {
            throw new LGMVersionException(Tool3lgmConstants.getResString("to_old_file_format"));
        }
        fileStream.getChannel().position(0);
        return loadXMLFile(fileStream, false);
    }

    /**
     * load collection from xml-source
     *
     * @param inputStream an InputStream to the xml-source
     * @return true, if reading was successful
     * @author Thomas Rudert
     */
    private boolean loadXMLFile(final InputStream inputStream, final boolean paste) {
        try {
            ToolXMLParser parser = new ToolXMLParser(this, inputStream, paste);
            parser.parseDocument();
        } catch (Exception exp) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein") + exp, exp);
            return false;
        }
        userFieldDefinitions.hasCrossReferences();
        return true;
    }

    /**
     * @return
     */
    public boolean chooseFile() {
        ExtendedFileChooser fileChooser = new ExtendedFileChooser(null);
        FileNameExtensionFilter lgmZippedFileFiler = Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LGM3_ZIP);
        FileNameExtensionFilter lgmUnzippedFileFiler = Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LGM3_UNZIPPED);
        fileChooser.setFileFilters(false, lgmZippedFileFiler, lgmUnzippedFileFiler);
        fileChooser.setFileFilter(isZipFile() ? lgmZippedFileFiler : lgmUnzippedFileFiler);
        if (getFile() != null) {
            fileChooser.setSelectedFile(getFile());
        }
        if (fileChooser.showSaveDialog(Static.getMainFrame()) != ExtendedFileChooser.APPROVE_OPTION) {
            return false;
        }
        File pfad = fileChooser.getSelectedFile();

        try {
            setFile(pfad);
        } catch (IOException exp) {
            Log.show(Log.FATAL, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
            exp.printStackTrace();
            return false;
        }
        setSaveAsZipFile(fileChooser.getFileFilter() == lgmZippedFileFiler);
        return true;
    }

    /**
     * save collection to file<br/>
     * if isReadOnly is true do nothing and return false<br/>
     * if isZipFile write content into compressed file<br/>
     * create temporary file for writing and if all actions are completed successfully overwrites original file
     *
     * @return boolean with true, if and only if filewriting was successful
     * @author Thomas Rudert
     */
    public boolean saveToFile() throws IOException {
        if (isReadOnly || file == null) {
            if (!chooseFile()) {
                return false;
            }
        }

        File tempFile = new File(file.getParentFile(), ".tempTool3lgmSaveFile");

        tempFile.delete();

        tempFile.deleteOnExit();

        if (!tempFile.createNewFile()) {
            return false;
        }

        FileOutputStream outStream = new FileOutputStream(tempFile);

        if (isZipFile) {
            saveZipFile(outStream);
        } else {
            outStream.write(getSaveString());
        }

        outStream.close();

        if (tempFile.length() <= 0) {
            throw new IOException("Empty file!");
        }

        @SuppressWarnings("resource")
        //der wird geclosed in forceClose()
        LGMInputStream tmpIStream = new LGMInputStream(tempFile);
        randomAccessFile.seek(0);
        randomAccessFile.setLength(0);

        long l = tempFile.length();
        int length = new Long(l).intValue();
        byte[] data = new byte[length];
        tmpIStream.read(data);
        ByteBuffer byteBuf = ByteBuffer.wrap(data);
        //TW
        if (lockSupported && lock != null) {
            randomAccessFile.getChannel().write(byteBuf);
        } else {
            randomAccessFile.write(data);
        }
        tmpIStream.forceClose();

        tempFile.delete();

        return true;
    }

    /**
     * save collection as xml-string into an packed zip-file
     *
     * @param fileStream FileOutputStream to write in
     * @throws IOException
     */
    private void saveZipFile(final FileOutputStream fileStream) throws IOException {
        //fileStream.write(new String("<!--ziped Tool3lgmFile-->\n").getBytes());

        byte[] xmlString = getSaveString();

        CRC32 crc = new CRC32();
        crc.reset();
        crc.update(xmlString);

        ZipEntry entry = new ZipEntry(getName().substring(0, getName().length() - 5) + "3lgm");
        entry.setMethod(ZipEntry.DEFLATED);
        entry.setCrc(crc.getValue());

        ZipOutputStream zipStream = new ZipOutputStream(fileStream);
        zipStream.setMethod(ZipOutputStream.DEFLATED);
        zipStream.setLevel(9);
        zipStream.putNextEntry(entry);
        zipStream.write(xmlString);
        zipStream.closeEntry();
        zipStream.finish();
    }

    /**
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * @return
     */
    public boolean isZipFile() {
        return isZipFile;
    }

    /**
     * @return
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     *
     */
    public void close() {
        try {
            if (lock != null) {
                lock.release();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } catch (Exception exp) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
        }
    }

    /**
     * @param gdl
     */
    public final void addGraphDocumentListener(final GraphDocumentListener gdl) {
        listener.add(gdl);
    }

    /**
     * @param gdl
     */
    public final void removeGraphDocumentListener(final GraphDocumentListener gdl) {
        listener.remove(gdl);
    }

    /**
     * @param source
     * @param bitmask
     * @param last_elem
     * @param last_group
     * @param pid
     */
    public final void distributeEventIntern(GraphDocument source, final int bitmask, final ElementContainer last_elem, final LayerContainer last_group, final int pid) {
        if (isBulkMode()) {
            return;
        }

        if (source == null) {
            source = doc;
        }

        int c;
        int anzahl = listener.size();
        switch (bitmask) {
        case GraphDocument.DATA_CHANGED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).dataChanged(source);
            }
            break;
        case GraphDocument.ELEMENT_GRAPHICS_CHANGED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).elementGraphicsChanged(source, last_elem);
            }
            break;
        case GraphDocument.LAYOUT_CHANGED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).layoutChanged(source);
            }
            break;
        case GraphDocument.ELEMENT_ADDED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).elementAdded(source, last_elem);
            }
            break;
        case GraphDocument.ELEMENT_DELETED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).elementDeleted(source, last_elem);
            }
            break;
        case GraphDocument.GROUP_ORDER_CHANGED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).groupOrderChanged(source);
            }
            break;
        case GraphDocument.ACTIVE_LAYER_CHANGED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).activeLayerChanged(source);
            }
            break;
        case GraphDocument.COLORS_CHANGED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).colorsChanged(source);
            }
            break;
        case GraphDocument.SELECTION_CHANGED:
            for (c = 0; c < anzahl; c++) {
                listener.get(c).selectionChanged(source);
            }
            break;
        default:
            break;
        }
    }

    /**
     * @return
     */
    public TransactionManager getTman() {
        return tman;
    }

    /**
     * @param ec
     */
    public void addToSelection(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        ElementContainer mainEc = me.getContainer(doc);
        doc.selectedContainer.add(mainEc);
        for (Szenario szen : szenarios) {
            ElementContainer szenEc = me.getContainer(szen);
            if (szenEc == null) {
                szenEc = mainEc;
            }
            szen.selectedContainer.add(szenEc);
        }
    }

    /**
     * @param ec
     */
    public boolean deselect(final ElementContainer ec) {
        if (ec == null) {
            return false;
        }
        doc.deselectInElementSelectionContext(ec.getElement());
        for (Szenario szen : szenarios) {
            szen.deselectInElementSelectionContext(ec.getElement());
        }
        return true;
    }

    /**
     * @param ec
     */
    public void deselectAll() {
        doc.clearSelection();
        for (Szenario szen : szenarios) {
            szen.clearSelection();
        }
    }

    /**
     * Selektiert in allen Teilmodellen alle einmaligen Elemente
     */
    public void selectAllUniques() {
        for (Class<? extends ModelElement> elemClass : ModelConstants.UNIQUE_NODES) {
            for (ElementContainer ec : doc.getElementContainer(elemClass)) {
                addToSelection(ec);
            }
        }
    }

    /**
     * Bildet aus der Liste der Container eine Liste von Elementen.
     *
     * @param elementContainer
     * @return
     */
    public static final ArrayList<ModelElement> getModelElements(final Collection<? extends ElementContainer> elementContainer) {
        ArrayList<ModelElement> al = new ArrayList<ModelElement>(elementContainer.size());
        for (ElementContainer ec : elementContainer) {
            al.add(ec.getElement());
        }
        return al;
    }

    /**
     * @param layer
     */
    public void setActiveLayer(final int layer) {
        if (layer < 0 || layer > 4 || active_layer == layer) {
            return;
        }
        active_layer = layer;
        distribute(GraphDocument.ACTIVE_LAYER_CHANGED);
    }

    /**
     * @return
     */
    public int getActiveLayer() {
        return active_layer;
    }

    /**
     * @return
     */
    public UserFieldDefinitions getUserFieldDefinitions() {
        return userFieldDefinitions;
    }

    /**
     * @param newDef
     */
    public void setUserFieldDefinitions(final UserFieldDefinitions newDef) {
        if (newDef != null && newDef != userFieldDefinitions) {
            userFieldDefinitions = newDef;
            //TODO:AXS:USERFIELD
            //hier müssen bei allen USerfieldTargets alle Userfields ausgetauscht werden, die sie über ihre UserField2Value-Maps referenzieren
        }

        setChanged(true);
    }

    /**
     * Löscht aus allen <code>UserFieldTarget</code> s der Collection die
     * Eingabewerte der übergebenen <code>UserField</code>s.
     *
     * @param userFieldsToRemove
     *            <code>UserField</code> s deren Eingabewerte gelöscht werden
     *            sollen
     */
    public void removeUserFieldValues(final ArrayList<UserField> userFieldsToRemove) {
        for (UserField userField : userFieldsToRemove) {
            if (userField.isGlobalOrFormat()) {
                removeUserField(userField);
            } else {
                Class<? extends ModelElement> elemClass = null;
                if (ModelElement.class.isAssignableFrom(userField.getTargetClass())) {
                    elemClass = userField.getTargetClass().asSubclass(ModelElement.class);
                    for (ModelElement me : doc.getModelItems(elemClass, true)) {
                        me.removeUserField(userField);
                    }
                }
            }
        }
    }

}
