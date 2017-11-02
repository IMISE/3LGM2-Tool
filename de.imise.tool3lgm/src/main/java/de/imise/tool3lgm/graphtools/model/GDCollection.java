package de.imise.tool3lgm.graphtools.model;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.Tool3lgm.getLastActionPosition;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.Tool3lgmConstants.isExtension;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.getMinCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.isConnecting;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.isConnectingForward;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.isStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.DOMAIN_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.ELEMENTS_WITH_NAME_EXTENSIONS;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LAYERS;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.UNIQUE_NODES;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getClassForName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getCopyDependencies;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayableName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getEdgeTypes;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getForwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getInitialSubtypes;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getSubordinatedJoinbleTypes;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.hasObjektDialog;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isAlwaysDoubleConnectedEdge;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isDoubleMeaningEdge;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isGenerateName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isInterLayerStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.layerFor;
import static de.imise.tool3lgm.graphtools.model.GDCommands.ADD_ELEMENT_TO_SZENARIO;
import static de.imise.tool3lgm.graphtools.model.GDCommands.CHANGE_ALPHA;
import static de.imise.tool3lgm.graphtools.model.GDCommands.CHANGE_COLOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.CHANGE_FORM;
import static de.imise.tool3lgm.graphtools.model.GDCommands.CHANGE_LAYER_SIZE_FACTOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.COORDINATE_KNOT;
import static de.imise.tool3lgm.graphtools.model.GDCommands.CREATE_KNOT;
import static de.imise.tool3lgm.graphtools.model.GDCommands.CREATE_SZENARIO;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INSERT_BENDING_POINT;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_BENDPOINT_INDEX;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_EDGE_CLASS;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_EDGE_CLASS_NAME;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_EDGE_INDEX;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_HASH_STRING;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_POSITION_X;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_POSITION_Y;
import static de.imise.tool3lgm.graphtools.model.GDCommands.LABEL_HALIGN;
import static de.imise.tool3lgm.graphtools.model.GDCommands.LABEL_VALIGN;
import static de.imise.tool3lgm.graphtools.model.GDCommands.LINK;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_DELETE_FROM_MODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_FONT;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_ALPHA;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_COLOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.REMOVE_SZENARIO;
import static de.imise.tool3lgm.graphtools.model.GDCommands.SET_ICON;
import static de.imise.tool3lgm.graphtools.model.GDCommands.SET_VISIBLE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.UNLINK;
import static de.imise.tool3lgm.graphtools.model.GDCommands.Z_MOVE;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.ACTIVE_LAYER_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.COLORS_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.ELEMENT_ADDED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.ELEMENT_DELETED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.ELEMENT_GRAPHICS_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.GDCOMMAND_TEXT_SURROUNDER;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.GROUP_ORDER_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.LAYOUT_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.SELECTION_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.getDecodedParseSaveString;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.getParseSaveString;
import static de.imise.tool3lgm.graphtools.model.GraphDocumentHandler.getModelItems;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_ELEMENT_LAYOUT;
import static de.imise.tool3lgm.log.Log.ERROR;
import static de.imise.tool3lgm.xml.ToolXMLParser.isParseAbleFileVersion;
import static de.imise.tool3lgm.xml.ToolXMLParser.isXMLFile;
import static de.imise.util.collections.CollectionUtils.getNextIndicatedName;
import static java.lang.Integer.parseInt;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.ModelPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Composition;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.Knickpunkt;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.undoredo.TransactionStackTable;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Prozess;
import de.imise.util.StringUtils;
import de.imise.util.collections.AlphabeticalSet;
import de.imise.util.swing.dialog.NameAndColorInputDialog;

/**
 * @author thomas, AXS
 */
public final class GDCollection extends UserFieldTarget {

    /** Undo- und Redomanager */
    protected TransactionManager tman = new TransactionManager();

    private final TransactionStackTable transStackTable = new TransactionStackTable();

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
    private final List<GraphDocumentListener> listener = new ArrayList<>();

    /**
     * Liste aller <code>GraphDocument</code>s in der Reihenfolge, dass immer das selektierte ganz hinten steht,
     * das davor selektierte direkt davor und so weiter. Jedes <code>GraphDocument</code> der Collection - also
     * auch das Hauptdokument - kommt genau einmal in der Liste vor. Wird ein Teilmodell gelöscht, wird das davor
     * selektierte aktiviert, im <code>ModelBrowser</code> selektiert und sein Grafikfenster in den Vordergrund
     * geholt.
     */
    private final List<LGMGraphDocument> activeGraphDocumentsList = new ArrayList<>();

    /**
     * Set aller Szenarios in alphabetischer Reihenfolge.
     */
    private final AlphabeticalSet<Szenario> szenarios = new AlphabeticalSet<>();

    /** Dokument wurde geaendert */
    private boolean changed;

    /**
     * Zeitpunkt der letzen Änderung
     */
    private long lastModificationTime = System.currentTimeMillis();

    /** Bezeichnung des Dokuments (Dateiname) */
    private String name = "";

    /** Handler zum Speichern und Laden */
    private final GDCollectionFileHandler fileHandler;

    /** Handler für den Im- und Export von (Teil-)Modellen */
    private final GDCollectionImExportHandler imExportHandler;

    public ModelPropertyDialog descriptionFrame;

    /**
     * Verzeichnis der Bitmap-Icons
     */
    private final Map<String, byte[]> iconTable = new HashMap<>();

    /**
     * Wenn <code>true</code>, werden keine Ereignisse gefeuert und keine Undo-/Redo-Commands aufgezeichnet.
     */
    private boolean bulk_mode = false;

    /**
     * COMMENTME
     */
    private int iconCounter = 0;

    /**
     * Dieser Counter berechnet die Verschiebung, mit der die Elemente bei einem Paste in die Grafik kopiert werden.
     * Jedes Mal, wenn gepastet wird ohne eine neue Kopie anzufertigen, dann wird der Counter hochgezählt.
     */
    private int pasteCounter;

    /**
     * COMMENTME
     */
    private int active_layer = DOMAIN_LAYER;

    /**
     *
     */
    public GDCollection() {
        doc = new LGMGraphDocument(this);
        userFieldDefinitions = new UserFieldDefinitions(this);
        doc.addGraphDocumentListener(userFieldDefinitions);
        imExportHandler = new GDCollectionImExportHandler(this);
        fileHandler = new GDCollectionFileHandler(this);
        activeGraphDocumentsList.add(doc);
    }

    public GDCollectionFileHandler getFileHandler() {
        return fileHandler;
    }

    public File getFile() {
        return fileHandler.getFile();
    }

    public String getFileVersion() {
        return fileHandler.getFileVersion();
    }

    public GDCollectionImExportHandler getImExportHandler() {
        return imExportHandler;
    }

    /**
     * @return the transStackTable
     */
    public final TransactionStackTable getTransStackTable() {
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
        return createSzenario(title, askName, description, szenHash, logWithStandardPID, STANDARD_PID);
    }

    /**
     * @param title
     * @param askName
     * @param szenHash
     * @param pid
     * @return
     */
    public Szenario createSzenario(String title, final boolean askName, final String description, final String szenHash, final boolean log, final int pid) {
        if (title == null || title.trim().equals("")) {
            title = getNextIndicatedName(getResString("submodel") + " #", activeGraphDocumentsList);
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
            doc.addUndoCommand(REMOVE_SZENARIO + " " + szenario.getHashString(), pid);
            doc.addRedoCommand(CREATE_SZENARIO + " " + getParseSaveString(szenario.getTitle()) + " " + getParseSaveString(szenario.getDescription()) + " " + szenario.getHashString(), pid);
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
        GraphDocument szen = getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        doc.start_transaction(pid);
        //bei allen Elementen, die mit dem zu löschenden Teilmodell verknüpft sind
        //den Verweis auf dieses Teilmodell löschen (das passiert im Hauptmodell)
        for (LayerContainer layer : doc.getLayers()) {
            for (NodeContainer nc : layer.getKnoten()) {
                Node node = nc.getKnoten();
                String associatedDoc = node.getAssociatedDoc();
                if (associatedDoc != null && associatedDoc.equals(szenHash)) {
                    node.setAssociatedDoc(null);
                }
            }
        }
        //alle Elemente des Szenarios löschen -> das kann man dann auch wieder zurück nehmen
        List<ElementContainer> elementsToDelete = new ArrayList<>();
        for (LayerContainer layer : szen.getLayers()) {
            elementsToDelete.addAll(layer.getKnickpunkte());
            elementsToDelete.addAll(layer.getKanten());
            elementsToDelete.addAll(layer.getKnoten());
        }
        removeContainerFromSubmodel(elementsToDelete, pid);
        szenarios.remove(szen);
        activeGraphDocumentsList.remove(szen);
        for (int layerIndex : LAYERS) {
            doc.addUndoCommand(MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layerIndex + " " + szen.layer[layerIndex].getColor().getRGB(), pid);
            doc.addUndoCommand(MODEL_ACTION_SET_LAYER_ALPHA + " " + szenHash + " " + layerIndex + " " + szen.layer[layerIndex].getAlpha(), pid);
            doc.addUndoCommand(CHANGE_LAYER_SIZE_FACTOR + " " + szenHash + " " + szen.getPageSizeFactor(), pid);
        }
        doc.addUndoCommand(CREATE_SZENARIO + " " + getParseSaveString(szen.getTitle()) + " " + getParseSaveString(szen.getDescription()) + " " + szen.hashString, pid);
        doc.addRedoCommand(REMOVE_SZENARIO + " " + szen.hashString, pid);
        //wenn das Beschreibungsfenster offen ist -> den Tab des zu löschenden Teimodells löschen
        if (descriptionFrame != null) {
            descriptionFrame.update();
            //			descriptionFrame.removeTab(szenario);
        }
        Static.getTool().closeFrame(szen);
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
        distribute(DATA_CHANGED, null, null, doc, STANDARD_PID);
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
        NameAndColorInputDialog d = new NameAndColorInputDialog(getMainFrame());
        d.showDialog(getResString("szenario_name_anfrage"), szenname);
        return d.getInputString();
    }

    /**
     * @param ec
     * @return
     */
    private static final boolean askNameAndColor(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        while (true) {
            NameAndColorInputDialog d = new NameAndColorInputDialog(getMainFrame());
            //TODO:Prozess gegen etwas allg. ersetzen (z. B. coloredElement als Eigenschaft von Element-Klassen)
            Point dialogPosition = getLastActionPosition();
            if (dialogPosition == null) {
                dialogPosition = new Point(100, 100);
            }
            boolean showColorChooser = me instanceof Prozess;
            d.showDialog(getResString("name_eing"), me.toString(), dialogPosition.x, dialogPosition.y, showColorChooser);
            String inputString = d.getInputString();
            if (inputString == null) {
                return false;
            }
            if (d.getInputColor() != null) {
                ec.get3LGMLayout().bg_color = d.getInputColor();
            }
            if (inputString.equals("") || inputString.equals(me.toString())) {
                return true;
            }
            me.setName(inputString);
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
        String ecDocHash = ecDoc.hashString;
        if (ec.getColor() != null) {
            ecDoc.addUndoCommand(CHANGE_COLOR + " " + ecDocHash + " " + ecHash + " " + ec.getColor().getRGB(), pid);
            ecDoc.addUndoCommand(CHANGE_ALPHA + " " + ecDocHash + " " + ecHash + " " + ec.getAlpha(), pid);
        }
        if (ec.getForm() != null) {
            ecDoc.addUndoCommand(CHANGE_FORM + " " + ecDocHash + " " + ecHash + " " + ec.getForm(), pid);
        }
        if (!ec.hasStandardFont()) {
            ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_FONT + " " + ecDocHash + " " + ecHash + " " + GDCOMMAND_TEXT_SURROUNDER + ec.getFontName() + GDCOMMAND_TEXT_SURROUNDER + " " + ec.getFontSize() + " " + ec.getFontStyle(), pid);
        }
        if (ec instanceof NodeContainer) {
            NodeContainer kc = (NodeContainer) ec;
            String iconName = kc.getIconString();
            if (iconName != null) {
                ecDoc.addUndoCommand(SET_ICON + " " + ecDocHash + " " + ecHash + " " + iconName, pid);
            }
            ecDoc.addUndoCommand(Z_MOVE + " " + ecDocHash + " " + ecHash + " " + ecDoc.layer[ec.layerFor()].indexOf(ec), pid);
            ecDoc.addUndoCommand(COORDINATE_KNOT + " " + ecDocHash + " " + ecHash + " " + ec.getX() + " " + ec.getY() + " " + ec.getWidth() + " " + ec.getHeight(), pid);
            if (!kc.isVisible()) {
                ecDoc.addUndoCommand(SET_VISIBLE + " " + false + " " + ecDocHash + " " + ecHash, pid);
            }
            if (ec.getValign() != STANDARD_ELEMENT_LAYOUT.valign) {
                ecDoc.addUndoCommand(LABEL_VALIGN + " " + ecDocHash + " " + ecHash + " " + kc.get3LGMLayout().valign, pid);
            }
            if (ec.getHalign() != STANDARD_ELEMENT_LAYOUT.halign) {
                ecDoc.addUndoCommand(LABEL_HALIGN + " " + ecDocHash + " " + ecHash + " " + kc.get3LGMLayout().halign, pid);
            }
        }
    }

    /**
     * @param ec
     * @param pid
     */
    public void removeContainerFromSubmodel(final ElementContainer ec, final int pid) {
        List<ElementContainer> container = new ArrayList<>();
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
        List<ElementContainer> reallyContainerToRemove = new ArrayList<>();
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
            for (Edge edge : me.getEdges()) {
                //den Container der Edge mit allen Knickpunkten im aktuellen Teilmodell löschen
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
        //die bendPointContainerList wird beim removeBendpoint-Aufruf selbst geändert -> daher einfach von hinten die
        //Knickpunkte löschen, dann muss nichts kopiert werden
        for (int k = edgeContainer.getBendpointContainerCount() - 1; k >= 0; k--) {
            removeBendpoint(edgeContainer.getBendpointContainer(k).getKnickpunktKnoten(), pid);
        }
        Edge edge = edgeContainer.getEdge();
        GraphDocument doc = edgeContainer.getGraphDocument();
        //jetzt den KantenContainer einfach löschen
        edge.removeContainer(doc);
        doc.layer[layerFor(edge.getClass())].remove(edgeContainer);
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
            ecDoc.addRedoCommand(MODEL_ACTION_DELETE_FROM_SUBMODEL + " " + ecDoc.hashString + " " + me.getHashString(), pid);
            me.removeContainer(ecDoc);
            ecDoc.layer[layerFor(me.getClass())].remove(ec);
        }
        //das Undo das die Container wieder einfügt muss als letztes kommen, weil es als erstes beim
        //Rückgängig machen wieder ausgeführt wird
        for (ElementContainer ec : containerToRemove) {
            ModelElement me = ec.getElement();
            if (logSubElements || !ModelConstants.isSlaveType(me.getClass())) {
                ecDoc.addUndoCommand(ADD_ELEMENT_TO_SZENARIO + " " + ecDoc.hashString + " " + me.getHashString(), pid);
            }
        }
        if (!transActionStarted) {
            return;
        }
        ecDoc.finish_transaction(pid);
        ecDoc.distributeEvent(DATA_CHANGED, pid);
        ecDoc.distributeEvent(SELECTION_CHANGED, pid);
    }

    /**
     * @param me
     */
    public void delete(final ModelElement me) {
        deleteElement(me, STANDARD_PID);
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
        List<ModelElement> list = new ArrayList<>();
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
        List<ModelElement> elementsToDelete = new ArrayList<>(elementHashesToDelete.length);
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
    public final void deleteElements(final List<? extends ModelElement> elementsToDelete, final int pid) {
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
    public final void deleteElements(final List<? extends ModelElement> elementsToDelete, final GraphDocument gdoc, final int pid) {
        //das wird die Liste mit allen zu löschenden Elementen. Das sind alle Elemente aus <code>elementsToDelete</code>,
        //alle Kanten dieser Elemente und rekursiv alle von den zu löschenden Elementen abhängigen Elemente (min. Karfinalität=1)
        //sowie deren Kanten
        List<ModelElement> allElementsToDelete = new ArrayList<>(elementsToDelete);
        //In dieses Set kommen alle Elemente, deren Löschen man nicht in den RedoKommandos loggen muss, weil beim Löschen eines
        //anderen Elementes eine minimale Kardinalität unterschritten ist, so dass sie automatisch mitgelöscht werden
        Set<ModelElement> dependentDeletedElements = new HashSet<>();
        //das wird die Liste aller zu löschenden Verbindungen
        List<Edge> edgesToDelete = new ArrayList<>();
        gdoc.start_transaction(pid);
        for (int i = 0; i < allElementsToDelete.size(); i++) {
            ModelElement me = allElementsToDelete.get(i);
            if (me == null) {
                allElementsToDelete.remove(i--);
                continue;
            }
            //den evtl. geöffneten Dialog des Elementes scließen
            ElementPropertyDialog dialog = hasObjektDialog(me);
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
            } else if (me instanceof Edge) {
                Edge edge = (Edge) me;
                edgesToDelete.add(edge);
                //wenn durch das Löschen der Edge auch die Kardinalität für eins oder beide der durch die Edge verbundenen
                //Elemente unterschritten wurde -> die Elemente auch löschen
                ModelElement[] startEnd = {
                        edge.getStart(),
                        edge.getEnd()
                };
                for (ModelElement elem : startEnd) {
                    //wenn die Anzahl der bestehenden Kanten der zu löschenden Art für das verbundene Element gleich
                    //der minimalen Kardinalität für diese Kantenart ist, dann muss das verbundene Element auch gelöscht werden
                    //auf Gleichheit muss getestet werden, weil die Edge ja noch nicht wirklich gelöscht ist und somit mitgezählt wird
                    Class<? extends Edge> edgeClass = edge.getClass();
                    if (elem != null && elem.countConnections(edgeClass) <= getMinCardinality(elem.getClass(), edgeClass)) {
                        if (!allElementsToDelete.contains(elem)) {
                            allElementsToDelete.add(elem);
                            dependentDeletedElements.add(elem);
                        }
                    }
                }
            }
            for (Edge edge : me.getEdges()) {
                //auch Kanten können Kanten haben usw., daher müssen sie diese Schleife auch durchlaufen
                if (!allElementsToDelete.contains(edge)) {
                    allElementsToDelete.add(edge);
                }
            }
        }
        if (allElementsToDelete.isEmpty()) {
            gdoc.finish_transaction(pid);
            gdoc.distributeEvent(DATA_CHANGED, pid);
            gdoc.distributeEvent(SELECTION_CHANGED, pid);
            return;
        }
        //alle Elemente einfach aus den Szenarien löschen
        for (Szenario szen : szenarios) {
            Set<ElementContainer> elementContainer = new HashSet<>();
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
        while (!edgesToDelete.isEmpty()) {
            for (int i = 0; i < edgesToDelete.size(); i++) {
                Edge edge = edgesToDelete.get(i);
                //immer erst nur Kanten ohne Kanten löschen
                if (edge.hasEdges()) {
                    continue;
                }
                ModelElement ks = edge.getStart();
                ModelElement ke = edge.getEnd();
                //bei inkonsistenten Kanten nicht loggen
                if (ks != null && ke != null) {
                    String edgeClassName = edge.getClass().getName();
                    String edgeHash = edge.getHashString();
                    String startHash = ks.getHashString();
                    String endHash = ke.getHashString();
                    int startEdgeIndex = ks.getEdgeIndex(edge);
                    int endEdgeIndex = ke.getEdgeIndex(edge);
                    int direction = edge.getDirection();
                    switch (direction) {
                    case FORWARD:
                        doc.addUndoCommand(LINK + " " + edgeClassName + " " + edgeHash + " " + startHash + " " + endHash + " " + startEdgeIndex + " " + endEdgeIndex, pid);
                        doc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + edgeHash, pid);
                        break;
                    case BACKWARD:
                        doc.addUndoCommand(LINK + " " + edgeClassName + " " + edgeHash + " " + endHash + " " + startHash + " " + endEdgeIndex + " " + startEdgeIndex, pid);
                        doc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + edgeHash, pid);
                        break;
                    case DOUBLE:
                        doc.addUndoCommand(LINK + " " + edgeClassName + " " + edgeHash + " " + endHash + " " + startHash + " " + endEdgeIndex + " " + startEdgeIndex, pid);
                        doc.addUndoCommand(LINK + " " + edgeClassName + " " + edgeHash + " " + startHash + " " + endHash + " " + startEdgeIndex + " " + endEdgeIndex, pid);
                        doc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + edgeHash, pid);
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
                doc.layer[layerFor(edge.getClass())].remove(edge.getContainer(doc));
                //jetzt den Container selbst löschen (kann man sich sparen, weil die Edge seobst nicht mehr gepsüeichert wird)
                edge.removeContainer(doc);
                edgesToDelete.remove(i--);
            }
        }
        //jetzt alle Node im Hauptmodell löschen
        for (ModelElement me : allElementsToDelete) {
            if (me instanceof Edge || me instanceof Knickpunkt) {
                continue;
            }
            Class<? extends ModelElement> meClass = me.getClass();
            String meHash = me.getHashString();
            doc.addUndoCommand(CREATE_KNOT + " " + meClass.getName() + " " + getParseSaveString(me.getName()) + " " + getParseSaveString(me.getDescription()) + " " + meHash, pid);
            if (!dependentDeletedElements.contains(me)) {
                doc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + meHash, pid);
            }
            //den Container des zu löschenden Elementes im Hauptmodell holen
            doc.layer[layerFor(meClass)].remove(me.getContainer(doc));
            //und danach erst im Table des Elements
            //das Löschen aus dem ContainerTbale des Elementes kann man sich sparen, da das Element nirgends mehr gespeichert werden sollte
            //me.removeContainer(this.doc);
        }
        gdoc.finish_transaction(pid);
        gdoc.distributeEvent(DATA_CHANGED, pid);
        gdoc.distributeEvent(SELECTION_CHANGED, pid);
    }

    /**
     * Entfernt den übergebenen {@link Knickpunkt} aus dem Haupt-{@link GraphDocument} und
     * dem Szenario, in dem er dargestellt wird (das ist immer nur 1). Es werden die Undo-Redo-Kommandos geloggt.
     *
     * @param kpk
     * @param pid
     */
    public final void removeBendpoint(final Knickpunkt bendpoint, final int pid) {
        BendpointContainer bendpointContainer = bendpoint.getBendpointContainer();
        if (bendpointContainer == null) {
            return;
        }
        //das GraphDocument holen, aus dem der übergebene Container stammt (das ist immer ein Szenario)
        GraphDocument szen = bendpointContainer.getGraphDocument();
        szen.start_transaction(pid);
        //hole den Container der Edge, auf der der Knickpunkt angezeigt wird (Dieser EdgeContainer ist
        //immer in einem Szenario)
        EdgeContainer edgeC = bendpoint.getOwner();
        //fuer das UndoKommando die Position merken, an der sich der Knickpunkt auf der Edge befunden hat.
        int oldIndex = edgeC.getIndexOfKnickpunkt(bendpoint);
        //entferne den Knickpunkt von der Edge
        edgeC.removeKnickpunkt(bendpoint);
        edgeC.computeBorderPoints();
        int layerIndex = edgeC.layerFor();
        //den Knickpunkt im Teilmodell löschen
        szen.getLayer(layerIndex).remove(bendpointContainer);
        //den Knickpunkt im Hauptmodell löschen
        doc.getLayer(layerIndex).remove(bendpoint.getContainer(doc));
        szen.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + bendpoint.getHashString(), pid);
        szen.addUndoCommand(INSERT_BENDING_POINT + " " + szen.getHashString() + " " + edgeC.getHashString() + " " + bendpointContainer.getHashString() + " " + bendpointContainer.getX() + " " + bendpointContainer.getY() + " " + oldIndex, pid);
        szen.finish_transaction(pid);
        LayerContainer lc = doc.layer[bendpoint.layerFor()];
        szen.distributeEvent(ELEMENT_DELETED, bendpointContainer, lc, pid);
        szen.distributeEvent(SELECTION_CHANGED, bendpointContainer, lc, pid);
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
        BendpointContainer bendpointContainer = szen.findBendpointContainerCoded(bendpointHashString);
        if (bendpointContainer != null) {
            return bendpointContainer;
        }
        EdgeContainer edgeContainer = null;
        if (!isNullOrEmpty(kanteHashString)) {
            edgeContainer = szen.findEdgeContainerCoded(kanteHashString);
        }
        if (edgeContainer != null) {
            szen.select(edgeContainer, pid);
        } else {
            if (!szen.isSelectedOnlyEdges()) {
                return null;
            }
            edgeContainer = (EdgeContainer) szen.getLastSelected();
        }
        Knickpunkt bendpoint = new Knickpunkt();
        bendpoint.setName(doc.getNextNewName(bendpoint.getClass()));
        bendpointContainer = new BendpointContainer(bendpoint, szen);
        if (!isNullOrEmpty(bendpointHashString)) {
            bendpointContainer.getKnoten().setHashString(bendpointHashString);
        }
        szen.start_transaction(pid);
        if (bendpointIndex == INVALID_BENDPOINT_INDEX) {
            bendpointIndex = edgeContainer.getKnickpunktInsertIndex(x, y);
        }
        //[0] = SzenHash, [1] = HashString der Edge, [2] = HashString des Knickpunktes, [3] = X-Position, [4] = Y-Position, [5] = Index des Knickpuntes auf der Edge,
        szen.addRedoCommand(INSERT_BENDING_POINT + " " + szenHashString + " " + edgeContainer.getHashString() + " " + bendpoint.getHashString() + " " + x + " " + y + " " + bendpointIndex, pid);
        szen.addUndoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + bendpoint.getHashString(), pid);
        // den Layer bestimmen auf dem der Knickpunkt eingefügt werden soll (= der Layer der Edge)
        int layerNumber = edgeContainer.getElement().layerFor();
        if (szen.getLayer(layerNumber).add(bendpointContainer) == null) {
            szen.undo(pid);
            return null;
        }
        doc.getLayer(layerNumber).add(new BendpointContainer(bendpoint, doc));
        edgeContainer.addKnickpunkt(bendpointContainer, bendpointIndex);
        if (x != INVALID_POSITION_X && y != INVALID_POSITION_Y) {
            bendpointContainer.setLocation(x, y);
        }
        szen.select(bendpointContainer, pid);
        szen.finish_transaction(pid);
        szen.distributeEvent(DATA_CHANGED, pid);
        szen.distributeEvent(SELECTION_CHANGED, pid);
        edgeContainer.computeBorderPoints();
        return bendpointContainer;
    }

    /**
     * @param elementClass
     * @param name
     * @param description
     * @param hashString
     * @param pid
     * @return
     */
    public NodeContainer createKnotenWithContainer(final Class<? extends Node> elementClass, final String name, final String description, final String hashString, final int pid) {
        //Knickpunkte kann man über diese Funktion nicht anlegen
        if (Knickpunkt.class.isAssignableFrom(elementClass)) {
            return null;
        }
        Node me = null;
        NodeContainer nc = null;
        try {
            me = elementClass.newInstance();
            nc = (NodeContainer) me.createContainer(doc);
        } catch (Exception ex) {
            Log.show(ERROR, getResString("FehlerAllgemein"), ex);
            return null;
        }
        if (StringUtils.isValid(hashString, "null")) {
            me.setHashString(hashString);
        }
        if (!Strings.isNullOrEmpty(name)) {
            me.setName(getDecodedParseSaveString(name));
        } else {
            me.setName(doc.getNextNewName(me.getClass()), false);
            if (isInteractiveMode() && !isGenerateName(me.getClass())) {
                if (!askNameAndColor(nc)) {
                    return null;
                }
            }
        }
        if (description != null && !description.trim().equals("")) {
            me.setDescription(getDecodedParseSaveString(description));
        }
        doc.start_transaction(pid);
        doc.addRedoCommand(CREATE_KNOT + " " + me.getClass().getName() + " " + getParseSaveString(me.getName()) + " " + getParseSaveString(me.getDescription()) + " " + me.getHashString(), pid);
        if (nc.getColor() != null) {
            doc.addRedoCommand(CHANGE_COLOR + " " + doc.hashString + " " + me.getHashString() + " " + nc.getColor().getRGB(), pid);
        }
        doc.addUndoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + me.getHashString(), pid);
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
        setInteractiveMode(old_mode);
        doc.finish_transaction(pid);
        doc.distributeEvent(DATA_CHANGED, pid);
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
        for (Class<? extends Edge> subTypeEdgeClass : getInitialSubtypes(elementClass)) {
            Class<? extends ModelElement> subType = isStartClass(subTypeEdgeClass, elementClass) ? getEndClass(subTypeEdgeClass) : getStartClass(subTypeEdgeClass);
            //minimale kardinalität für die Unterelemente
            int minCardForSubType = getMinCardinality(me.getClass(), subTypeEdgeClass);
            //bisher verbundene Anzahl von Unterelementen
            List<ModelElement> connectedSubTypes = me.getConnectedElements(subType, subTypeEdgeClass);
            //soviele Unterelemente wie fehlen neu anlegen
            for (int i = connectedSubTypes.size(); i < minCardForSubType; i++) {
                String name;
                //wenn mehrere Unterelemene existieren können, dann durchnummerieren
                if (minCardForSubType > 1) {
                    name = getNextIndicatedName(getDisplayableName(subType) + " ", " " + getResString("fuer") + " " + me.getName(), connectedSubTypes);
                } else {
                    name = getDisplayableName(subType) + " " + getResString("fuer") + " " + me.getName();
                }
                ModelElement skC = createKnotenWithContainer(subType.asSubclass(Node.class), name, "", null, pid).getElement();
                link(subTypeEdgeClass, me, skC, pid);
                connectedSubTypes.add(skC);
            }
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
            } else if (isInterLayerStartClass(kc.getElement().getClass())) {
                kc = new InterLayerConnectedNodeContainer(kc.getKnoten(), doc);
            } else {
                nc = new NodeContainer(kc.getKnoten(), doc);
            }
        }
        doc.getLayer(layerIndex).add(nc);
    }

    /**
     * @param edgeContainer
     * @param pid
     */
    public void addEdge(final EdgeContainer edgeContainer, final int pid) {
        Edge edge = edgeContainer.getEdge();
        EdgeContainer ec = new EdgeContainer(edgeContainer, doc);
        int layer = edge.layerFor();
        doc.getLayer(layer).add(ec);
        boolean bulkMode = isBulkMode();
        setBulkMode(true);
        for (Szenario szen : szenarios) {
            szen.createEdgeContainer(edge.getStart().getContainer(szen), szen, false, pid);
        }
        setBulkMode(bulkMode);
    }

    //ENDE ADD //
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //#############################################################################################//
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //START LINK //
    /**
     * Verbindet die beiden Modellelemente miteinander, wenn noch keine Edge zwischen ihnen existiert.<br>
     *
     * @param edgeClassName
     * @param hashString
     * @param startElementHash
     * @param endElementHash
     * @param startElementEdgeIndex
     * @param endElementEdgeIndex
     * @param pid
     * @return
     *         die neu angelegte Edge zwischen den beiden Elementen oder die Edge, die bereits existierte
     * @see #link(String, String, ModelElement, ModelElement, int, int)
     */
    public final Edge link(final String edgeClassName, final String hashString, final String startElementHash, final String endElementHash, final int startElementEdgeIndex, final int endElementEdgeIndex, final int pid) {
        ModelElement me1 = doc.findElementCoded(startElementHash);
        ModelElement me2 = doc.findElementCoded(endElementHash);
        return link(edgeClassName, hashString, me1, me2, startElementEdgeIndex, endElementEdgeIndex, true, pid);
    }

    /**
     * Verbindet die beiden Modellelemente miteinander, wenn noch keine Edge zwischen ihnen existiert.<br>
     *
     * @param startElement
     * @param endElement
     * @param pid
     * @return
     *         die neu angelegte Edge zwischen den beiden Elementen oder die Edge, die bereits existierte
     * @see #link(String, String, ModelElement, ModelElement, int, int)
     */
    public final Edge link(final ModelElement startElement, final ModelElement endElement, final int pid) {
        return link(INVALID_EDGE_CLASS_NAME, INVALID_HASH_STRING, startElement, endElement, INVALID_EDGE_INDEX, INVALID_EDGE_INDEX, true, pid);
    }

    /**
     * @param edgeClass
     * @param k1
     * @param k2
     * @param pid
     * @return
     */
    public Edge link(final Class<? extends Edge> edgeClass, final ModelElement k1, final ModelElement k2, final int pid) {
        return link(edgeClass, INVALID_HASH_STRING, k1, k2, pid);
    }

    /**
     * @param edgeClass
     * @param edgeHash
     * @param k1
     * @param k2
     * @param pid
     * @return
     */
    public Edge link(final Class<? extends Edge> edgeClass, final String edgeHash, final ModelElement k1, final ModelElement k2, final int pid) {
        if (edgeClass == null) {
            return link(INVALID_EDGE_CLASS_NAME, edgeHash, k1, k2, INVALID_EDGE_INDEX, INVALID_EDGE_INDEX, true, pid);
        }
        return link(edgeClass.getSimpleName(), edgeHash, k1, k2, INVALID_EDGE_INDEX, INVALID_EDGE_INDEX, true, pid);
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
    public Edge link(final Class<? extends Edge> edgeClass, final ModelElement startElement, final ModelElement endElement, final int startElementEdgeIndex, final int endElementEdgeIndex, final int pid) {
        return link(edgeClass.getSimpleName(), INVALID_HASH_STRING, startElement, endElement, startElementEdgeIndex, endElementEdgeIndex, true, pid);
    }

    /**
     * Verbindet die beiden Modellelemente miteinander, wenn noch keine Edge zwischen ihnen existiert. Die Verbindung
     * entsteht immer in Vorwärtsrichtung von Element <code>me1</code> zu Element <code>me2</code><br>
     *
     * @param edgeClassName
     *            Klassenname der kante, die angelegt werden soll. Ist nur relevant, wenn es mehrere Kantenarten zwischen den Elementen geben kann.
     * @param edgeHash
     *            Wird ein Wert ungleich <code>null</code> übergeben, wird dieser als HasWert der neuen Edge gesetzt
     * @param startElement
     *            Startknoten der Edge
     * @param endElement
     *            Endknoten der Edge
     * @param edgeIndex
     * @param startElementEdgeIndex
     *            Position, an der die Edge beim Startelement in die Kantenliste eingefügt werden soll. Bei ungeordneten Listen sollte hier -1
     *            übergeben werden.
     * @param endElementEdgeIndex
     *            Position, an der die Edge beim Endelement in die Kantenliste eingefügt werden soll. Bei ungeordneten Listen sollte hier -1
     *            übergeben werden.
     * @param ensureConsistency
     *            wenn <code>true</code> wird für die verbundenen Elemente geprüft, ob die Kardinalität mit der neuen Edge
     *            überschritten wird. Wenn ja, werden überzählige Verbindungen gelöscht
     * @param pid
     *            Transaktions-ID mit der die Änderungen am Model durchgeführt werden
     * @return
     *         die neu angelegte Edge zwischen den beiden Elementen oder die Edge, die bereits existierte
     */
    public Edge link(String edgeClassName, final String edgeHash, ModelElement startElement, ModelElement endElement, final int startElementEdgeIndex, final int endElementEdgeIndex, final boolean ensureConsistency, final int pid) {
        //		System.err.println("GDCollection.link() " + me1 + "\t" + me2);
        if (startElement == null || endElement == null || startElement == endElement) {
            return null;
        }
        Edge edge = null;
        EdgeContainer kac = null;
        Class<? extends ModelElement> edgeClassOrNull = ModelConstants.getClassForName(edgeClassName);
        Class<? extends Edge> edgeClass = edgeClassOrNull == null ? null : edgeClassOrNull.asSubclass(Edge.class);
        if (edgeClass != null && !isConnecting(edgeClass, startElement.getClass(), endElement.getClass())) {
            return null;
        }
        doc.start_transaction(pid);
        try {
            //wenn keine Kantenklasse angegeben wurde, muss diese ermittelt werden. Wenn sie nicht eindeutig ist, wird der Benutzer per Dialog gefragt.
            if (edgeClass == null) {
                Class<? extends Edge>[] edgeClasses = getEdgeTypes(startElement.getClass(), endElement.getClass());
                if (edgeClasses == null || edgeClasses.length == 0) {
                    return null;
                }
                edgeClass = edgeClasses[0];
                if (edgeClasses.length > 1) {
                    JPanel messagePanel = new JPanel();
                    messagePanel.setLayout(new BoxLayout(messagePanel, Y_AXIS));
                    ButtonGroup buttonGroup = new ButtonGroup();
                    for (int i = 0; i < edgeClasses.length; i++) {
                        JRadioButton b = new JRadioButton(getForwardMetaAssociationName(edgeClasses[i]));
                        b.setActionCommand(edgeClasses[i].getName());
                        messagePanel.add(b);
                        buttonGroup.add(b);
                        if (i == 0) {
                            b.setSelected(true);
                        }
                    }
                    JOptionPane optionPane = new JOptionPane(messagePanel, PLAIN_MESSAGE, DEFAULT_OPTION);
                    JDialog dialog = optionPane.createDialog(Static.getMainFrame(), getResString("choose_trace"));
                    dialog.setVisible(true);
                    edgeClassName = buttonGroup.getSelection().getActionCommand();
                    edgeClass = getClassForName(edgeClassName).asSubclass(Edge.class);
                }
            }
            edge = startElement.getEdgeTo(endElement, edgeClass, startElementEdgeIndex);
            if (edge != null) {
                doc.finish_transaction(pid);
                return edge;
            }
            edge = startElement.getEdgeFrom(endElement, edgeClass, startElementEdgeIndex);
            if (edge != null) {
                edge.setDirection(DOUBLE);
                String startHash = startElement.getHashString();
                String endHash = endElement.getHashString();
                doc.addRedoCommand(LINK + " " + edgeClass.getName() + " " + edge.getHashString() + " " + startHash + " " + endHash + " " + startElementEdgeIndex + " " + endElementEdgeIndex, pid);
                doc.addUndoCommand(UNLINK + " " + startHash + " " + endHash + " " + startElementEdgeIndex, pid);
            } else {
                try {
                    edge = edgeClass.newInstance();
                } catch (Exception e) {
                    Log.show(ERROR, getResString("FehlerAllgemein"), e);
                    doc.undo(pid);
                    return null;
                }
                if (edgeHash != null && !edgeHash.equals("")) {
                    edge.setHashString(edgeHash);
                }
                //AXS: geändert am 21.06.2017: jetzt sind immer alle Kanten, die nicht DoubleMeaning, PartOf oder Composition sind automatisch DOUBLE
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
                if (isAlwaysDoubleConnectedEdge(edgeClass)) {
                    edge.setDirection(DOUBLE);
                } else {
                    int dir = FORWARD;
                    //AXS: auch am 21.06.2017 geändert
                    //                    if (!(edgeStartClass.isAssignableFrom(startClass) && edgeEndClass.isAssignableFrom(endClass))) {
                    if (!isConnectingForward(edgeClass, startElement.getClass(), endElement.getClass())) {
                        ModelElement dummy = startElement;
                        startElement = endElement;
                        endElement = dummy;
                        dir = BACKWARD;
                    }
                    edge.setDirection(dir);
                }
                edge.setKnotsAndInsert(startElement, startElementEdgeIndex, endElement, endElementEdgeIndex);
                if (edge.getStart() != null && edge.getEnd() != null) {
                    kac = new EdgeContainer(edge, doc);
                    edge.setName(doc.getNextNewName(edge.getClass()), false);
                    addEdge(kac, pid);
                } else {
                    if (edge.getStart() == null && edge.getEnd() != null) {
                        edge.getEnd().removeEdge(edge);
                    }
                    if (edge.getEnd() == null && edge.getStart() != null) {
                        edge.getStart().removeEdge(edge);
                    }
                }
                String startHash = startElement.getHashString();
                String endHash = endElement.getHashString();
                doc.addRedoCommand(LINK + " " + edgeClass.getName() + " " + edge.getHashString() + " " + startHash + " " + endHash + " " + startElementEdgeIndex + " " + endElementEdgeIndex, pid);
                doc.addUndoCommand(UNLINK + " " + startHash + " " + endHash + " " + startElementEdgeIndex, pid);
                //Falls bereits Beziehungen der anzulegenden Art bestehen und durch die neue Beziehung die Kardinalitäten
                //verletzt wären -> lösche solange bestehende Beziehungen, bis die Kardinaltitäten eingehalten werden
                //Dies muss nach dem Hinzufügen der anderen Undo-Komamndos erfolgen, sonst stimmt die Reihenfolge der Kommandos nicht.
                if (ensureConsistency) {
                    Class<? extends ModelElement> startClass = startElement.getClass();
                    Class<? extends ModelElement> endClass = endElement.getClass();
                    boolean startElementIsEdgeStart = edge.isStartClass(startClass);
                    boolean endElementIsEdgeStart = edge.isStartClass(endClass);
                    Class<? extends ModelElement> edgeStartClass = edge.getStartClass();
                    Class<? extends ModelElement> edgeEndClass = edge.getEndClass();
                    int maxForwardCardinality = edge.getMaxForwardCardinality();
                    int maxBackwardCardinality = edge.getMaxBackwardCardinality();
                    int maxElemCardinality = startElementIsEdgeStart ? maxForwardCardinality : maxBackwardCardinality;
                    List<Edge> edgeList = startElement.getEdgesWith(startElementIsEdgeStart ? edgeEndClass : edgeStartClass, edgeClass);
                    edgeList.remove(edge);
                    if (edgeList.size() > 0 && edgeList.size() == maxElemCardinality) {
                        deleteElement(edgeList.get(0), doc, pid);
                    }
                    maxElemCardinality = endElementIsEdgeStart ? maxForwardCardinality : maxBackwardCardinality;
                    edgeList = endElement.getEdgesWith(endElementIsEdgeStart ? edgeEndClass : edgeStartClass, edgeClass);
                    edgeList.remove(edge);
                    if (edgeList.size() > 0 && edgeList.size() == maxElemCardinality) {
                        deleteElement(edgeList.get(0), doc, pid);
                    }
                }
            }
        } catch (Exception e) {
            Log.show(ERROR, getResString("FehlerAllgemein"), e);
            doc.undo(pid);
            return null;
        }
        doc.finish_transaction(pid);
        doc.distributeEvent(DATA_CHANGED, pid);
        return edge;
    }

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
    public void unlink(final String knothash1, final String knothash2, final Class<? extends Edge> edgeClass, final int edgeIndex, final int pid) {
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
        unlink(k1, k2, INVALID_EDGE_INDEX, pid);
    }

    /**
     * @param k1
     * @param k2
     * @param edgeIndex
     * @param pid
     */
    public final void unlink(final ModelElement k1, final ModelElement k2, final int edgeIndex, final int pid) {
        unlink(k1, k2, INVALID_EDGE_CLASS, edgeIndex, pid);
    }

    /**
     * @param k1
     * @param k2
     * @param edgeClass
     * @param pid
     */
    public final void unlink(final ModelElement k1, final ModelElement k2, final Class<? extends Edge> edgeClass, final int pid) {
        unlink(k1, k2, edgeClass, INVALID_EDGE_INDEX, pid);
    }

    /**
     * @param me1
     * @param me2
     * @param edgeClass
     * @param edgeIndex
     * @param pid
     */
    public final void unlink(final ModelElement me1, ModelElement me2, final Class<? extends Edge> edgeClass, final int edgeIndex, final int pid) {
        if (me1 == null || me2 == null) {
            return;
        }
        Edge edge = null;
        List<Edge> edges = me1.getEdgesWith(me2, edgeClass, edgeIndex);
        if (edges.isEmpty()) {
            return;
        } else if (edges.size() == 1) {
            edge = edges.get(0);
        } else {
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, Y_AXIS));
            ButtonGroup buttonGroup = new ButtonGroup();
            for (int i = 0; i < edges.size(); i++) {
                JRadioButton b = new JRadioButton(getForwardMetaAssociationName(edges.get(i).getClass()));
                if (i == 0) {
                    b.setSelected(true);
                }
                b.setActionCommand(new Integer(i).toString());
                messagePanel.add(b);
                buttonGroup.add(b);
            }
            JOptionPane optionPane = new JOptionPane(messagePanel, PLAIN_MESSAGE, DEFAULT_OPTION);
            JDialog dialog = optionPane.createDialog(getMainFrame(), getResString("choose_trace"));
            dialog.setVisible(true);
            int index = parseInt(buttonGroup.getSelection().getActionCommand());
            edge = edges.get(index);
        }
        if (me2 == me1) {
            me2 = edge.getStart();
        }
        String me1Hash = me1.getHashString();
        String me2Hash = me2.getHashString();
        String edgeClassName = edgeClass == null ? "null" : edgeClass.getName();
        doc.start_transaction(pid);
        doc.addRedoCommand(UNLINK + " " + me1Hash + " " + me2Hash + " " + edgeClassName + " " + edgeIndex, pid);
        //Undo-Kommando wird in deleteElement gesetzt (s. u.)
        //nur bei Kanten mit doppelter bedeutung kann man in bestimmten Richtungen unlinken. Bei allen anderen
        //ist die Richtung egal und das Unlinken ist das Löschen der Edge
        Class<? extends Edge> absoluteEdgeClass = edge.getClass(); // die übergebene Kanten-Klasse kann null gewesen oder eine Oberklasse sein
        if (isDoubleMeaningEdge(absoluteEdgeClass)) {
            if (edge.getDirection() == DOUBLE) {
                if (edge.getStart() == me1) {
                    doc.addUndoCommand(LINK + " " + absoluteEdgeClass.getName() + " " + edge.getHashString() + " " + me1Hash + " " + me2Hash + " " + me1.getEdgeIndex(edge) + " " + me2.getEdgeIndex(edge), pid);
                    edge.setDirection(BACKWARD);
                } else {
                    doc.addUndoCommand(LINK + " " + absoluteEdgeClass.getName() + " " + edge.getHashString() + " " + me2Hash + " " + me1Hash + " " + me2.getEdgeIndex(edge) + " " + me1.getEdgeIndex(edge), pid);
                    edge.setDirection(FORWARD);
                }
            } else {
                deleteElement(edge, doc, pid);
            }
        } else {
            deleteElement(edge, doc, pid);
        }
        doc.finish_transaction(pid);
        doc.distributeEvent(DATA_CHANGED, pid);
    }

    private void updateElementNames() {
        List<ModelElement> modelItemsWithNameExtensions = getModelItems(this, ELEMENTS_WITH_NAME_EXTENSIONS);
        for (ModelElement me : modelItemsWithNameExtensions) {
            me.updateNameExtensions();
        }
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
        //prüfen, ob es sich um Node gleichen Typs handelt (nur diese können vereint werden)
        if (!(modelElement1 instanceof Node && modelElement2 instanceof Node)) {
            JOptionPane.showMessageDialog(getMainFrame(), getResString("nur_knoten_sel"), getResString("tool3lgm"), INFORMATION_MESSAGE);
            return false;
        }
        Node knoten1 = (Node) modelElement1;
        Node knoten2 = (Node) modelElement2;
        if (knoten1.getClass() != knoten2.getClass()) {
            JOptionPane.showMessageDialog(null, getResString("nur_gleiche_sel"), getResString("tool3lgm"), INFORMATION_MESSAGE);
            return false;
        }
        //Beginne umhängen der Kanten
        doc.start_transaction(pid);
        for (Szenario s : szenarios) {
            s.start_transaction(pid, false);
        }
        //Namen und Beschreibung des zu löschenden Node an den verbleibenden anhängen
        //und ExtIDs und benutzerdef. Eigenschaftsfelder zusammenführen
        knoten2.join(knoten1, false);
        //knoten2.createNameWithSzens(doc);
        for (Class<? extends ModelElement> clazz : getSubordinatedJoinbleTypes(knoten2.getClass())) {
            List<ModelElement> sjt1 = knoten1.getConnectedElements(clazz);
            List<ModelElement> sjt2 = knoten2.getConnectedElements(clazz);
            if (sjt1.size() > 0 && sjt2.size() > 0) {
                ModelElement me1 = sjt1.get(0);
                ModelElement me2 = knoten2.getConnectedElements(clazz).get(0);
                join(me1.getHashString(), me2.getHashString(), source, pid);
            }
        }
        //Das hier ist Hardcore, weil hier das IterableObject zurück auf List gecastet wird-> eigentlich müsste sich Edge selbst irgenwie darum kümmern!
        List<Edge> kantenVector1 = (List<Edge>) knoten1.getEdges();//ArrayList der Kanten des zu löschendn Knotens
        List<Edge> kantenVector2 = (List<Edge>) knoten2.getEdges();//ArrayList der Kanten des verbleibenden Knotens
        ModelElement startKnoten, endKnoten;
        //für jede Edge vom zu löschenden Node
        while (kantenVector1.size() > 0) {
            Edge kante = kantenVector1.get(0);
            startKnoten = kante.getStart(); //Startknoten der zu übernehmenden Edge merken
            endKnoten = kante.getEnd(); //Endknoten -"-
            //zu löschenden Node durch den verbleibenden ersetzen
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
                //abfangen, ob im verbleibenden Node an gleicher Stelle schon eine Edge vorkommt, Edge testKante = new Edge(startKnoten, endKnoten, false);
                Edge testKante;
                try {
                    testKante = kante.getClass().newInstance();
                } catch (Exception e) {
                    Log.show(ERROR, getResString("FehlerAllgemein"), e);
                    continue;
                }
                testKante.setKnots(startKnoten, endKnoten, false);
                //TODO:AXS: ich glaube hier fliegen Kanten raus, die in unterschiedliche Richtungen zeigen, weil isEqualTo nur die Elemente und die Kanteklasse prüft
                for (int i = 0; i < kantenVector2.size(); i++) {
                    //für jede Edge des verbleibenden Elementes prüfen, ob umzuhängende Edge und eine Edge in
                    // kantenVector2 dieselben Elemente verbindet
                    if (kantenVector2.get(i).isEqualTo(testKante)) {
                        deleteKante = true;
                        break;
                    }
                }
            }
            if (deleteKante) { //wenn die Edge doppelt vorkommen würde
                deleteElement(kante, doc, pid);
                //				doc.removeEdge(kante, pid);//Edge einfach komplett löschen
            } else { //Edge muss umgehängt werden
                knoten1.removeEdge(kante); //im zu löschenden Node die Edge entfernen
                kante.setKnots(startKnoten, endKnoten);//die Edge wirklich an knoten2 binden
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
                // alle abhängigen Node vor den zusammengeführten stellen
                szen.start_transaction(TransactionManager.STANDARD_PID, false);
                szen.moveDependentKnotsUp(nc, TransactionManager.STANDARD_PID, false);
                szen.finish_transaction(TransactionManager.STANDARD_PID, false);
            }
        }
        deleteElement(knoten1, doc, pid);
        //		doc.removeNode((NodeContainer)knoten1.getContainer(doc), pid); //alle Kanten umgehängt -> wegfallenden Node komplett löschen
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
     * Wenn die anderen Parameter aus der Methode <code>distribute(int, ElementContainer, LayerContainer, GraphDocument, int)</code> nicht angegeben
     * werden können, kann man hiermit ein allgemeines Ereignis feuern.
     *
     * @param bitmask
     */
    public final void distribute(final int bitmask) {
        distribute(bitmask, null, null, null, STANDARD_PID);
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
            Log.show(ERROR, getResString("FehlerAllgemein"), e);
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
    public Map<String, byte[]> getIconTable() {
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

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return setzt den Title auf dasselbe wie {@link #getName()}, aber ohne die Dateiendung
     */
    public String getTitle() {
        int lastPointIndex = name.lastIndexOf('.');
        String title = name;
        if (lastPointIndex > 0 && lastPointIndex < title.length() - 1) {
            String extension = title.substring(lastPointIndex + 1);
            if (isExtension(extension)) {
                title = title.substring(0, lastPointIndex);
            }
        }
        return title;
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
    public void resolveCopyDependencies(final List<? extends GraphDocument> export, final List<ModelElement> elements, final Set<String> bitmaps, final Set<UserField> userFields) {
        /* alle übergebenen Szenarios durchgehen und copyDependcies auflösen */
        for (LayerContainer lc : doc.getLayers()) {
            for (NodeContainer nc : lc.getKnoten()) {
                Node node = nc.getKnoten();
                for (GraphDocument doc : export) {
                    if (doc.isMyElement(node)) {
                        ElementContainer container = node.getContainer(doc);
                        if (!elements.contains(node)) {
                            elements.add(node);
                            String iconName = ((NodeContainer) container).getIconString();
                            if (iconName != null) {
                                bitmaps.add(iconName);
                            }
                            resolveCopyDependencies(node, elements, userFields);
                        }
                    }
                }
            }
            for (EdgeContainer ec : lc.getKanten()) {
                Edge edge = ec.getEdge();
                for (GraphDocument doc : export) {
                    if (doc.isMyElement(edge)) {
                        if (!elements.contains(edge)) {
                            elements.add(edge);
                            resolveCopyDependencies(edge, elements, userFields);
                        }
                    }
                }
            }
            for (BendpointContainer bc : lc.getKnickpunkte()) {
                Knickpunkt bendpoint = bc.getKnickpunktKnoten();
                for (GraphDocument doc : export) {
                    if (doc.isMyElement(bendpoint)) {
                        if (!elements.contains(bendpoint)) {
                            elements.add(bendpoint);
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
    public void resolveCopyDependencies(final Collection<ElementContainer> elements, final List<ModelElement> result, final Set<UserField> userFields) {
        for (ElementContainer ec : elements) {
            ModelElement me = ec.getElement();
            if (!result.contains(me)) {
                if (!(me instanceof Knickpunkt)) {
                    result.add(me);
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
    private void resolveCopyDependencies(final ModelElement me, final List<ModelElement> elements, final Set<UserField> userFields) {
        if (me instanceof Knickpunkt) {
            return;
        }
        for (UserField userField : me.getUserFieldInputValueKeys()) {
            userFields.add(userField);
        }
        if (me instanceof Edge) {
            for (BendpointContainer kpC : doc.getLayer(me.layerFor()).getKnickpunkte()) {
                Knickpunkt kp = kpC.getKnickpunktKnoten();
                String kantenHash = kp.getKantenHash();
                if (kantenHash != null && kantenHash.equals(me.getHashString())) {
                    if (!elements.contains(kp)) {
                        elements.add(kp);
                    }
                }
            }
            Edge edge = (Edge) me;
            ModelElement start = edge.getStart();
            if (!elements.contains(start)) {
                elements.add(start);
                resolveCopyDependencies(start, elements, userFields);
            }
            ModelElement end = edge.getEnd();
            if (!elements.contains(end)) {
                elements.add(end);
                resolveCopyDependencies(end, elements, userFields);
            }
        }
        for (Class<? extends ModelElement> elemClass : getCopyDependencies(me.getClass())) {
            for (ElementContainer ec : me.getConnectedContainer(elemClass, doc)) {
                ModelElement connected = ec.getElement();
                if (!elements.contains(connected)) {
                    elements.add(connected);
                    resolveCopyDependencies(connected, elements, userFields);
                }
                for (Edge e : me.getEdgesWith(connected)) {
                    if (!elements.contains(e)) {
                        elements.add(e);
                        resolveCopyDependencies(e, elements, userFields);
                    }
                }
            }
        }
        //elements wird in der Schleife vergrößert -> nicht über Iterable gehen
        for (int i = 0; i < elements.size(); i++) {
            ModelElement m = elements.get(i);
            for (Edge ka : me.getEdgesWith(m)) {
                if (!elements.contains(ka)) {
                    elements.add(ka);
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
     * @param file
     */
    public void loadClipboard(final File file) {
        //		System.err.println(file);
        try {
            setBulkMode(true);
            FileInputStream clipStream = new FileInputStream(file);
            if (isXMLFile(clipStream) && isParseAbleFileVersion(clipStream)) {
                clipStream.getChannel().position(0);
                fileHandler.loadXMLFile(clipStream, true);
            }
            clipStream.close();
            setBulkMode(false);
        } catch (Exception e) {
            Log.show(ERROR, getResString("FehlerAllgemein"), e);
        }
    }

    public void loadFile(final InputStream istream) {
        try {
            setBulkMode(true);
            fileHandler.loadXMLFile(istream, true);
            setBulkMode(false);
        } catch (Exception e) {
            Log.show(ERROR, getResString("FehlerAllgemein"), e);
        }
    }

    /**
     *
     */
    public int resetPasteCounter() {
        pasteCounter = 1;
        return pasteCounter;
    }

    public int increasePasteCounter() {
        return ++pasteCounter;
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
        switch (bitmask) {
        case DATA_CHANGED:
            for (GraphDocumentListener l : listener) {
                l.dataChanged(source);
            }
            updateElementNames();
            break;
        case ELEMENT_GRAPHICS_CHANGED:
            for (GraphDocumentListener l : listener) {
                l.elementGraphicsChanged(source, last_elem);
            }
            break;
        case LAYOUT_CHANGED:
            for (GraphDocumentListener l : listener) {
                l.layoutChanged(source);
            }
            break;
        case ELEMENT_ADDED:
            for (GraphDocumentListener l : listener) {
                l.elementAdded(source, last_elem);
            }
            break;
        case ELEMENT_DELETED:
            for (GraphDocumentListener l : listener) {
                l.elementDeleted(source, last_elem);
            }
            break;
        case GROUP_ORDER_CHANGED:
            for (GraphDocumentListener l : listener) {
                l.groupOrderChanged(source);
            }
            break;
        case ACTIVE_LAYER_CHANGED:
            for (GraphDocumentListener l : listener) {
                l.activeLayerChanged(source);
            }
            break;
        case COLORS_CHANGED:
            for (GraphDocumentListener l : listener) {
                l.colorsChanged(source);
            }
            break;
        case SELECTION_CHANGED:
            for (GraphDocumentListener l : listener) {
                l.selectionChanged(source);
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
        for (Class<? extends ModelElement> elemClass : UNIQUE_NODES) {
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
    public static final List<ModelElement> getModelElements(final Collection<? extends ElementContainer> elementContainer) {
        List<ModelElement> al = new ArrayList<>(elementContainer.size());
        for (ElementContainer ec : elementContainer) {
            al.add(ec.getElement());
        }
        return al;
    }

    /**
     * @param layer
     */
    public void setActiveLayer(final int layer) {
        if (layer < MIN_LAYER_INDEX || layer > MAX_LAYER_INDEX || active_layer == layer) {
            return;
        }
        active_layer = layer;
        distribute(ACTIVE_LAYER_CHANGED);
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
            //die eigenen UserFields mit den neuen ersetzen
            replaceUserFields(newDef);
            //hier müssen bei allen UserfieldTargets alle Userfields ausgetauscht werden, die sie über ihre UserField2Value-Maps referenzieren
            for (ModelElement me : getModelItems(doc, ModelElement.class, true, false, false)) {
                me.replaceUserFields(newDef);
            }
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
    public void removeUserFieldValues(final List<UserField> userFieldsToRemove) {
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
