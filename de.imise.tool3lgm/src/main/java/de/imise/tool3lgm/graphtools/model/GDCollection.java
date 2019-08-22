package de.imise.tool3lgm.graphtools.model;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.imise.tool3lgm.Static.getLastActionPosition;
import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.Tool3lgmConstants.isExtension;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.DOMAIN_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LAYERS;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_BENDPOINT_INDEX;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_EDGE_CLASS_NAME;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_EDGE_INDEX;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_HASH_STRING;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_POSITION_X;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_POSITION_Y;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_ADD_ELEMENT_TO_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_CREATE_NODE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_CREATE_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_DELETE_FROM_MODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_DELETE_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_INSERT_BENDING_POINT;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_LINK;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_MOVE_ORDER;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_RENAME_SUBMODEL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_FONT;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_ICON;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_POSITION;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_SHAPE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_ALPHA;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_COLOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_SIZE_FACTOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_UNLINK;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.GDCOMMAND_TEXT_SURROUNDER;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.getDecodedParseSaveString;
import static de.imise.tool3lgm.graphtools.model.GraphDocument.getParseSaveString;
import static de.imise.tool3lgm.graphtools.model.GraphDocumentHandler.getModelItems;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ACTIVE_LAYER_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.MODEL_OR_SZENARIO_NAME_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SZENARIO_ADDED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SZENARIO_REMOVED;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_ELEMENT_LAYOUT;
import static de.imise.tool3lgm.log.Log.ERROR;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.google.common.base.Strings;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElemenPropertyDialogsContext;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.undoredo.TransactionStackTable;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.UserfieldResourceHandler;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.ToolXMLParser;
import de.imise.util.StringUtils;
import de.imise.util.collections.AlphabeticalSet;
import de.imise.util.swing.dialog.NameAndColorInputDialog;

/**
 * Repräsentiert ein Gesamtmodell. GDColllection = GrapDocument-Sammlung - also Sammlung aller Teilmodelle, die als Gesamtmodell gesehen werden.
 *
 * @author thomas, AXS
 */
public final class GDCollection extends UserFieldTarget {

    /** Das Metamodel, auf dem dieses Modell basiert */
    private MetaModelContext metaModelContext;

    /** Das Metamodel des Modells */
    private MetaModel metaModel;

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

    /** Liste aller {@link LGMChangeListener} */
    private final List<LGMChangeListener> listener = new ArrayList<>();

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

    /** Elemente, die in diesem Set sind, gelten als optional */
    private final Set<OptionalEdge> optionalElements = new HashSet<>();

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

    /**
     * Verzeichnis der Bitmap-Icons
     */
    private final GDCollectionIconTable iconTable = new GDCollectionIconTable();

    /**
     * Wenn <code>true</code>, werden keine Ereignisse gefeuert und keine Undo-/Redo-Commands aufgezeichnet.
     */
    private boolean bulk_mode = false;

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
        fileHandler = new GDCollectionFileHandler(this);
        imExportHandler = new GDCollectionImExportHandler(this);
        //Standard-Userfield-Definition laden
    }

    /**
     * @param metaModelContext
     */
    public GDCollection(@Nonnull final MetaModelContext metaModelContext) {
        this();
        setMetaModelContext(metaModelContext);
    }

    public void setMetaModelContext(final MetaModelContext metaModelContext) {
        this.metaModelContext = metaModelContext;
        metaModel = metaModelContext.getMetaModel();
        doc = new LGMGraphDocument(this);
        userFieldDefinitions = new UserFieldDefinitions(this);
        doc.addClosedTransactionsListener(userFieldDefinitions);
        activeGraphDocumentsList.add(doc);
        UserfieldResourceHandler.loadDefaultUserfieldDefinition(this);
    }

    /** Liefert den MetaModelContext, auf dem dieses Modell basiert */
    public MetaModelContext getMetaModelContext() {
        return metaModelContext;
    }

    /** Liefert das MetaModel, auf dem dieses Modell basiert */
    public MetaModel getMetaModel() {
        return metaModel;
    }

    /**
     * Diese Funktion mach genau das umgekehrte wie die Funktion {@link Tool3lgmConstants#getResString(String)}. D.h. sie schaut zuerst in die
     * Resourcen des eigenen Metamodells und wenn sie dort den key nicht gefunden hat, dann in die allgemeinen des Tools. Im Unterschied zu der
     * Funktion aus den {@link Tool3lgmConstants} wird hier aber nicht in die Resourcen des aktuell selektierten Modells geschaut, sondern in die
     * dieses Modells hier.
     *
     * @param key
     * @return
     * @see MetaModelContext#getResString(String)
     */
    public String getResString(final String key) {
        return metaModelContext.getResString(key);
    }

    /**
     * Liefert die Klasse, über die alle Knoten- und Kantenklassennamen generiert werden, also die Anzeigenamen in Ein- und Mehrzahl und bei den
     * Kanten die gerichteten Namen.
     *
     * @return
     * @see MetaModelContext#getElementsNameBuilder()
     */
    public ElementsNameBuilder getElementsNameBuilder() {
        return metaModelContext.getElementsNameBuilder();
    }

    /**
     * @return
     */
    public GDCollectionFileHandler getFileHandler() {
        return fileHandler;
    }

    /**
     * @return
     */
    public File getFile() {
        return fileHandler.getFile();
    }

    /**
     * @return
     */
    public String getFileVersion() {
        return fileHandler.getFileVersion();
    }

    /**
     * @return
     */
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
            doc.addUndoCommand(MODEL_ACTION_DELETE_SUBMODEL + " " + szenario.getHashString(), pid);
            doc.addRedoCommand(MODEL_ACTION_CREATE_SUBMODEL + " " + getParseSaveString(szenario.getTitle()) + " " + getParseSaveString(szenario.getDescription()) + " " + szenario.getHashString(), pid);
            doc.finish_transaction(pid);
        }
        szenario.addClosedTransactionsListener(userFieldDefinitions);
        setChanged(true);
        distribute(SZENARIO_ADDED, null, szenario, pid);
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
            for (NodeContainer nc : layer.getNodeContainersAlphabetical()) {
                Node node = nc.getNode();
                String associatedDoc = node.getAssociatedDoc();
                if (associatedDoc != null && associatedDoc.equals(szenHash)) {
                    node.setAssociatedDoc(null);
                }
            }
        }
        //alle Elemente des Szenarios löschen -> das kann man dann auch wieder zurück nehmen
        List<ElementContainer> elementsToDelete = new ArrayList<>();
        for (LayerContainer layer : szen.getLayers()) {
            layer.addAllContainers(elementsToDelete);
        }
        removeContainerFromSubmodel(elementsToDelete, pid);
        szenarios.remove(szen);
        activeGraphDocumentsList.remove(szen);
        for (int layerIndex : LAYERS) {
            doc.addUndoCommand(MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layerIndex + " " + szen.layer[layerIndex].getColor().getRGB(), pid);
            doc.addUndoCommand(MODEL_ACTION_SET_LAYER_ALPHA + " " + szenHash + " " + layerIndex + " " + szen.layer[layerIndex].getAlpha(), pid);
            doc.addUndoCommand(MODEL_ACTION_SET_LAYER_SIZE_FACTOR + " " + szenHash + " " + szen.getPageSizeFactor(), pid);
        }
        doc.addUndoCommand(MODEL_ACTION_CREATE_SUBMODEL + " " + getParseSaveString(szen.getTitle()) + " " + getParseSaveString(szen.getDescription()) + " " + szen.hashString, pid);
        doc.addRedoCommand(MODEL_ACTION_DELETE_SUBMODEL + " " + szen.hashString, pid);
        doc.finish_transaction(pid);
        setChanged(true);
        distribute(SZENARIO_REMOVED, null, szen, pid);
    }

    /**
     * @param szenHash
     * @param title
     * @param pid
     */
    public void renameSzenario(final String szenHash, final String title, final int pid) {
        GraphDocument szen = getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        String szenTitle = title;
        if (Strings.isNullOrEmpty(title)) {
            szenTitle = askName(szen.getTitle());
        }
        String oldTitle = szen.getTitle();
        if (szenTitle == null || szenTitle.equals(oldTitle)) {
            return;
        }

        doc.start_transaction(pid);
        doc.addUndoCommand(MODEL_ACTION_RENAME_SUBMODEL + " " + szen.hashString + " " + getParseSaveString(oldTitle), pid);
        doc.addRedoCommand(MODEL_ACTION_RENAME_SUBMODEL + " " + szen.hashString + " " + getParseSaveString(szenTitle), pid);
        doc.finish_transaction(pid);

        szen.setTitle(szenTitle);
        GraphDocument mainDoc = szen.getCollection().getMainGraphDocument();
        for (ModelElement me : mainDoc.getModelItems(ModelElement.class, true)) {
            me.invalidateNameWithSzens();
        }
        setChanged(true);
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

    private boolean selectedDocInitialized = false;

    /**
     * Gibt das aktuell selektierte <code>GraphDocument</code> zurück.
     */
    public LGMGraphDocument getSelectedDoc() {
        if (activeGraphDocumentsList.size() < 1) {
            return null;
        }
        //bei der allerersten Abfrage sollte das aktive GraphDocument aus den eingelesenen ViewParametern kommen
        if (!selectedDocInitialized) {
            for (Szenario szen : szenarios) {
                ViewParameter viewParameter = szen.getViewParameter();
                if (viewParameter != null && viewParameter.selected) {
                    setActiveGraphDocument(szen);
                    break;
                }
            }
            selectedDocInitialized = true;
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

    /**
     * @param szenname
     * @return
     */
    private final String askName(final String szenname) {
        NameAndColorInputDialog d = new NameAndColorInputDialog(getMainFrame());
        d.showDialog(getResString("szenario_name_anfrage"), szenname);
        return d.getInputString();
    }

    /**
     * @param ec
     * @return
     */
    private final boolean askNameAndColor(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        while (true) {
            NameAndColorInputDialog d = new NameAndColorInputDialog(getMainFrame());
            Point dialogPosition = getLastActionPosition();
            if (dialogPosition == null) {
                dialogPosition = new Point(100, 100);
            }
            boolean showColorChooser = metaModel.hasSortedEdgeClassesToPaintable(me.getClass());
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
            ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_COLOR + " " + ecDocHash + " " + ecHash + " " + ec.getColor().getRGB(), pid);
            ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_ALPHA + " " + ecDocHash + " " + ecHash + " " + ec.getAlpha(), pid);
        }
        if (ec.getForm() != null) {
            ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_SHAPE + " " + ecDocHash + " " + ecHash + " " + ec.getForm(), pid);
        }
        if (!ec.hasStandardFont()) {
            ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_FONT + " " + ecDocHash + " " + ecHash + " " + GDCOMMAND_TEXT_SURROUNDER + ec.getFontName() + GDCOMMAND_TEXT_SURROUNDER + " " + ec.getFontSize() + " " + ec.getFontStyle(), pid);
        }
        if (ec instanceof NodeContainer) {
            NodeContainer kc = (NodeContainer) ec;
            String iconName = kc.getIconString();
            if (iconName != null) {
                ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_ICON + " " + ecDocHash + " " + ecHash + " " + iconName, pid);
            }
            ecDoc.addUndoCommand(MODEL_ACTION_MOVE_ORDER + " " + ecDocHash + " " + ecHash + " " + ecDoc.layer[ec.layerFor()].indexOf(ec), pid);
            ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_POSITION + " " + ecDocHash + " " + ecHash + " " + ec.getX() + " " + ec.getY() + " " + ec.getWidth() + " " + ec.getHeight(), pid);
            if (!kc.isVisible()) {
                ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF + " " + ecDocHash + " " + ecHash, pid);
            }
            if (ec.getValign() != STANDARD_ELEMENT_LAYOUT.valign) {
                ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN + " " + ecDocHash + " " + ecHash + " " + kc.get3LGMLayout().valign, pid);
            }
            if (ec.getHalign() != STANDARD_ELEMENT_LAYOUT.halign) {
                ecDoc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN + " " + ecDocHash + " " + ecHash + " " + kc.get3LGMLayout().halign, pid);
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
                removeBendpoint(((BendpointContainer) ec).getBendpoint(), pid);
                continue;
            }
            //keine Kanten löschen
            if (ec instanceof EdgeContainer) {
                continue;
            }
            ModelElement me = ec.getElement();
            //keine untergerodneten Elemente einfach so aus der Grafik löschen
            if (metaModel.isSlaveType(me.getClass())) {
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
                if (!(edge instanceof CompositionEdge)) {
                    continue;
                }
                CompositionEdge comp = (CompositionEdge) edge;
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
        szen.distributeEvent(DATA_CHANGED, pid);
        szen.distributeEvent(SELECTION_CHANGED, pid);
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
            removeBendpoint(edgeContainer.getBendpointContainer(k).getBendpoint(), pid);
        }
        Edge edge = edgeContainer.getEdge();
        GraphDocument doc = edgeContainer.getGraphDocument();
        //jetzt den KantenContainer einfach löschen
        edge.removeContainer(doc);
        doc.layer[edgeContainer.layerFor()].remove(edgeContainer);
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
            ecDoc.layer[ec.layerFor()].remove(ec);
        }
        //das Undo das die Container wieder einfügt muss als letztes kommen, weil es als erstes beim
        //Rückgängig machen wieder ausgeführt wird
        for (ElementContainer ec : containerToRemove) {
            ModelElement me = ec.getElement();
            if (logSubElements || !metaModel.isSlaveType(me.getClass())) {
                ecDoc.addUndoCommand(MODEL_ACTION_ADD_ELEMENT_TO_SUBMODEL + " " + ecDoc.hashString + " " + me.getHashString(), pid);
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
            ElementPropertyDialog dialog = ElemenPropertyDialogsContext.hasOpenDialog(me);
            if (dialog != null) {
                dialog.performOK();
            }
            //Knickpunkte kann man gleich löschen
            if (me instanceof Bendpoint) {
                ElementContainer kpc = me.getContainer(gdoc);
                if (kpc == null) {
                    kpc = me.getContainer(doc);
                }
                removeBendpoint((Bendpoint) me, pid);
                allElementsToDelete.remove(i--);
                continue;
            } else if (me instanceof Edge) {
                Edge edge = (Edge) me;
                edgesToDelete.add(edge);
                //wenn durch das Löschen der Edge auch die Kardinalität für eins oder beide der durch die Edge verbundenen
                //Elemente unterschritten wurde -> die Elemente auch löschen
                ModelElement[] startEnd = {
                        edge.getStart(), edge.getEnd()
                };
                for (ModelElement elem : startEnd) {
                    //wenn die Anzahl der bestehenden Kanten der zu löschenden Art für das verbundene Element gleich
                    //der minimalen Kardinalität für diese Kantenart ist, dann muss das verbundene Element auch gelöscht werden
                    //auf Gleichheit muss getestet werden, weil die Edge ja noch nicht wirklich gelöscht ist und somit mitgezählt wird
                    Class<? extends Edge> edgeClass = edge.getClass();
                    if (elem != null && elem.countConnections(edgeClass) <= metaModel.getMinCardinality(elem.getClass(), edgeClass)) {
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
                    ConnectionState connectionState = edge instanceof DoubleMeaningEdge ? ((DoubleMeaningEdge) edge).getConnectionState() : ConnectionState.FORWARD;
                    switch (connectionState) {
                    case FORWARD:
                        doc.addUndoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edgeHash + " " + startHash + " " + endHash + " " + startEdgeIndex + " " + endEdgeIndex, pid);
                        doc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + edgeHash, pid);
                        break;
                    case BACKWARD:
                        doc.addUndoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edgeHash + " " + endHash + " " + startHash + " " + endEdgeIndex + " " + startEdgeIndex, pid);
                        doc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + edgeHash, pid);
                        break;
                    case DOUBLE:
                        doc.addUndoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edgeHash + " " + endHash + " " + startHash + " " + endEdgeIndex + " " + startEdgeIndex, pid);
                        doc.addUndoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edgeHash + " " + startHash + " " + endHash + " " + startEdgeIndex + " " + endEdgeIndex, pid);
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
                doc.layer[edge.layerFor()].remove(edge.getContainer(doc));
                //jetzt den Container selbst löschen (kann man sich sparen, weil die Edge seobst nicht mehr gepsüeichert wird)
                edge.removeContainer(doc);
                edgesToDelete.remove(i--);
            }
        }
        //jetzt alle Node im Hauptmodell löschen
        for (ModelElement me : allElementsToDelete) {
            if (me instanceof Edge || me instanceof Bendpoint) {
                continue;
            }
            Class<? extends ModelElement> meClass = me.getClass();
            String meHash = me.getHashString();
            doc.addUndoCommand(MODEL_ACTION_CREATE_NODE + " " + meClass.getName() + " " + getParseSaveString(me.getName()) + " " + getParseSaveString(me.getDescription()) + " " + meHash, pid);
            if (!dependentDeletedElements.contains(me)) {
                doc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + meHash, pid);
            }
            //den Container des zu löschenden Elementes im Hauptmodell holen
            doc.layer[me.layerFor()].remove(me.getContainer(doc));
            //und danach erst im Table des Elements
            //das Löschen aus dem ContainerTbale des Elementes kann man sich sparen, da das Element nirgends mehr gespeichert werden sollte
            //me.removeContainer(this.doc);
            if (me instanceof OptionalEdge) {
                removeOptional((OptionalEdge) me);
            }
        }
        gdoc.finish_transaction(pid);
        gdoc.distributeEvent(DATA_CHANGED, pid);
        gdoc.distributeEvent(SELECTION_CHANGED, pid);
    }

    /**
     * Entfernt den übergebenen {@link Bendpoint} aus dem Haupt-{@link GraphDocument} und
     * dem Szenario, in dem er dargestellt wird (das ist immer nur 1). Es werden die Undo-Redo-Kommandos geloggt.
     *
     * @param kpk
     * @param pid
     */
    public final void removeBendpoint(final Bendpoint bendpoint, final int pid) {
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
        int oldIndex = edgeC.getIndexOfBendpoint(bendpoint);
        //entferne den Knickpunkt von der Edge
        edgeC.removeBendpoint(bendpoint);
        edgeC.computeBorderPoints();
        int layerIndex = edgeC.layerFor();
        //den Knickpunkt im Teilmodell löschen
        szen.getLayer(layerIndex).remove(bendpointContainer);
        //den Knickpunkt im Hauptmodell löschen
        doc.getLayer(layerIndex).remove(bendpoint.getContainer(doc));
        szen.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + bendpoint.getHashString(), pid);
        szen.addUndoCommand(MODEL_ACTION_INSERT_BENDING_POINT + " " + szen.getHashString() + " " + edgeC.getHashString() + " " + bendpointContainer.getHashString() + " " + bendpointContainer.getX() + " " + bendpointContainer.getY() + " " + oldIndex, pid);
        szen.finish_transaction(pid);
        szen.distributeEvent(DATA_CHANGED, bendpointContainer, pid);
        szen.distributeEvent(SELECTION_CHANGED, bendpointContainer, pid);
    }

    //ENDE REMOVE //
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //#############################################################################################//
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //ANFANG ADD //
    /**
     * @param szenHash
     * @param edgeHash
     * @param bendpointHash
     * @param x
     * @param y
     * @param bendpointIndex
     *            Index des Knickpunktes auf dem {@link EdgeContainer}
     * @param pid
     */
    public final BendpointContainer insertBendingPoint(final String szenHash, final String edgeHash, final String bendpointHash, final int x, final int y, int bendpointIndex, final int pid) {
        GraphDocument szen = getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return null;
        }
        BendpointContainer bendpointContainer = szen.findBendpointContainerCoded(bendpointHash);
        if (bendpointContainer != null) {
            return bendpointContainer;
        }
        EdgeContainer edgeContainer = null;
        if (!isNullOrEmpty(edgeHash)) {
            edgeContainer = szen.findEdgeContainerCoded(edgeHash);
        }
        if (edgeContainer != null) {
            szen.select(edgeContainer, pid);
        } else {
            if (!szen.isSelectedOnlyEdges()) {
                return null;
            }
            edgeContainer = (EdgeContainer) szen.getLastSelected();
        }
        Bendpoint bendpoint = metaModel.createElement(Bendpoint.class);
        bendpoint.setName(doc.getNextNewName(bendpoint.getClass()));
        bendpointContainer = new BendpointContainer(bendpoint, szen);
        if (!isNullOrEmpty(bendpointHash)) {
            bendpointContainer.getNode().setHashString(bendpointHash);
        }
        szen.start_transaction(pid);
        if (bendpointIndex == INVALID_BENDPOINT_INDEX) {
            bendpointIndex = edgeContainer.getBendpointInsertIndex(x, y);
        }
        //[0] = SzenHash, [1] = HashString der Edge, [2] = HashString des Knickpunktes, [3] = X-Position, [4] = Y-Position, [5] = Index des Knickpuntes auf der Edge,
        szen.addRedoCommand(MODEL_ACTION_INSERT_BENDING_POINT + " " + szenHash + " " + edgeContainer.getHashString() + " " + bendpoint.getHashString() + " " + x + " " + y + " " + bendpointIndex, pid);
        szen.addUndoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + bendpoint.getHashString(), pid);
        // den Layer bestimmen auf dem der Knickpunkt eingefügt werden soll (= der Layer der Edge)
        int layerNumber = edgeContainer.getElement().layerFor();
        if (szen.getLayer(layerNumber).add(bendpointContainer) == null) {
            szen.undo(pid);
            return null;
        }
        doc.getLayer(layerNumber).add(new BendpointContainer(bendpoint, doc));
        edgeContainer.addBendpoint(bendpointContainer, bendpointIndex);
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
    public NodeContainer createNodeAndContainer(final Class<? extends Node> elementClass, final String name, final String description, final String hashString, final int pid) {
        //Knickpunkte kann man über diese Funktion nicht anlegen
        if (Bendpoint.class.isAssignableFrom(elementClass)) {
            return null;
        }
        Node me = null;
        NodeContainer nc = null;
        try {
            me = metaModel.createElement(elementClass);
            nc = (NodeContainer) me.createContainer(doc);
        } catch (Exception ex) {
            Log.show(ERROR, getResString("FehlerAllgemein"), ex);
            return null;
        }
        if (StringUtils.isValid(hashString, "null")) {
            me.setHashString(hashString);
        }
        boolean nameIsEmpty = Strings.isNullOrEmpty(name);
        boolean nameIsValidAndNotMarkedAsGenerated = !nameIsEmpty && name.charAt(0) != GraphDocument.GENERATED_NAME_PREFIX;
        if (nameIsValidAndNotMarkedAsGenerated) {
            me.setName(getDecodedParseSaveString(name));
        } else {
            String newName = nameIsEmpty ? doc.getNextNewName(me.getClass()) : name.substring(1);
            me.setName(newName, false);
            if (isInteractiveMode() && !metaModel.isGenerateName(me.getClass())) {
                if (!askNameAndColor(nc)) {
                    return null;
                }
            }
        }
        if (description != null && !description.trim().equals("")) {
            me.setDescription(getDecodedParseSaveString(description));
        }
        doc.start_transaction(pid);
        doc.addRedoCommand(MODEL_ACTION_CREATE_NODE + " " + me.getClass().getName() + " " + getParseSaveString(me.getName()) + " " + getParseSaveString(me.getDescription()) + " " + me.getHashString(), pid);
        if (nc.getColor() != null) {
            doc.addRedoCommand(MODEL_ACTION_SET_ELEMENT_COLOR + " " + doc.hashString + " " + me.getHashString() + " " + nc.getColor().getRGB(), pid);
        }
        doc.addUndoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + me.getHashString(), pid);
        // den Layer bestimmen auf dem das Element eingefügt werden soll
        int layerNumber = me.layerFor();
        //das hier darf eigentlich nur bei Textfeldern passieren, da diese keinen festen Layer haben. Wahrscheinlich
        //würde dieser Weg auch bei allen anderen Elementen funktionieren, was aber nicht getestet ist.
        if (layerNumber == ModelConstants.NO_LAYER) {
            layerNumber = doc.getActiveLayer().getLayerNumber();
        }
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
        for (Class<? extends Edge> subTypeEdgeClass : metaModel.getInitialSubtypes(elementClass)) {
            Class<? extends ModelElement> subType = metaModel.isStartClass(subTypeEdgeClass, elementClass) ? getEndClass(subTypeEdgeClass) : getStartClass(subTypeEdgeClass);
            //minimale kardinalität für die Unterelemente
            int minCardForSubType = metaModel.getMinCardinality(me.getClass(), subTypeEdgeClass);
            //bisher verbundene Anzahl von Unterelementen
            List<ModelElement> connectedSubTypes = me.getConnectedElements(subType, subTypeEdgeClass);
            //soviele Unterelemente wie fehlen neu anlegen
            for (int i = connectedSubTypes.size(); i < minCardForSubType; i++) {
                String name;
                //wenn mehrere Unterelemene existieren können, dann durchnummerieren
                if (minCardForSubType > 1) {
                    name = getNextIndicatedName(getElementsNameBuilder().getDisplayableName(subType) + " ", " " + getResString("fuer") + " " + me.getName(), connectedSubTypes);
                } else {
                    name = getElementsNameBuilder().getDisplayableName(subType) + " " + getResString("fuer") + " " + me.getName();
                }
                ModelElement skC = createNodeAndContainer(subType.asSubclass(Node.class), name, "", null, pid).getElement();
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
            Node node = kc.getNode();
            if (kc instanceof BendpointContainer) {
                nc = new BendpointContainer((Bendpoint) node, doc);
            } else if (metaModel.hasInterLayerStartClass(node)) {
                kc = new InterLayerConnectedNodeContainer(node, doc);
            } else {
                nc = new NodeContainer(node, doc);
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
    public Edge link(final String edgeClassName, final String edgeHash, ModelElement startElement, ModelElement endElement, final int startElementEdgeIndex, final int endElementEdgeIndex, final boolean ensureConsistency, final int pid) {
        //		System.err.println("GDCollection.link() " + me1 + "\t" + me2);
        if (startElement == null || endElement == null || startElement == endElement) {
            return null;
        }

        Edge edge = null;
        EdgeContainer kac = null;
        Class<? extends ModelElement> edgeClassOrNull = metaModel.getClassForName(edgeClassName);
        if (edgeClassOrNull == null) {
            return null;
        }
        Class<? extends Edge> edgeClass = edgeClassOrNull == null ? null : edgeClassOrNull.asSubclass(Edge.class);
        if (edgeClass != null && !metaModel.isConnecting(edgeClass, startElement.getClass(), endElement.getClass())) {
            return null;
        }

        doc.start_transaction(pid);
        try {
            if (!MultipleEdge.class.isAssignableFrom(edgeClass)) {
                edge = startElement.getEdgeTo(endElement, edgeClass, startElementEdgeIndex);
                if (edge != null) {
                    doc.finish_transaction(pid);
                    return edge;
                }
            }
            edge = startElement.getEdgeFrom(endElement, edgeClass, startElementEdgeIndex);
            //wenn es schon eine Kante in der Gegenrichtung gibt und diese Kante eine Kante mit doppelter Bedeutung ist -> dann Richtung auf DOUBLE setzen
            if (MetaModel.isDoubleMeaningEdge(edgeClass) && edge != null) {
                ((DoubleMeaningEdge) edge).setConnectionState(DOUBLE);
            } else {
                edge = metaModel.createElement(edgeClass);
                if (edge == null) {
                    return null;
                }
                if (!Strings.isNullOrEmpty(edgeHash)) {
                    edge.setHashString(edgeHash);
                }
                ConnectionState connectionState = FORWARD; // wird nur für die DoubleMeaningEdges gebraucht
                if (!metaModel.isConnectingForward(edgeClass, startElement.getClass(), endElement.getClass())) {
                    ModelElement dummy = startElement;
                    startElement = endElement;
                    endElement = dummy;
                    connectionState = BACKWARD;
                }
                if (MetaModel.isDoubleMeaningEdge(edgeClass)) {
                    ((DoubleMeaningEdge) edge).setConnectionState(connectionState);
                }
                edge.setNodesAndInsert(startElement, startElementEdgeIndex, endElement, endElementEdgeIndex);
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
                //Falls bereits Beziehungen der anzulegenden Art bestehen und durch die neue Beziehung die Kardinalitäten
                //verletzt wären -> lösche solange bestehende Beziehungen, bis die Kardinaltitäten eingehalten werden
                //Dies muss nach dem Hinzufügen der anderen Undo-Komamndos erfolgen, sonst stimmt die Reihenfolge der Kommandos nicht.
                if (ensureConsistency) {
                    //beim Startelement eine alte Kante löschen, falls die neue Kante die Anzahl der Verbindungen auf dem MAX-Wert gebracht hat
                    //War der Wert vorher schon höher, wird hier nicht gelöscht! Das wäre ein Fall für die Konsistenzprüfung
                    Class<? extends ModelElement> edgeEndClass = edge.getEndClass();
                    int maxForwardCardinality = edge.getMaxForwardCardinality();
                    List<Edge> edgeList = startElement.getEdgesTo(edgeEndClass, edgeClass);
                    edgeList.remove(edge);
                    if (edgeList.size() == maxForwardCardinality) {
                        deleteElement(edgeList.get(0), doc, pid);
                    }
                    //beim Endelement dasselbe nur in Rückwärtsrichtung
                    Class<? extends ModelElement> edgeStartClass = edge.getStartClass();
                    int maxBackwardCardinality = edge.getMaxBackwardCardinality();
                    edgeList = endElement.getEdgesFrom(edgeStartClass, edgeClass);
                    edgeList.remove(edge);
                    if (edgeList.size() == maxBackwardCardinality) {
                        deleteElement(edgeList.get(0), doc, pid);
                    }
                }
            }
            String startHash = startElement.getHashString();
            String endHash = endElement.getHashString();
            doc.addRedoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edge.getHashString() + " " + startHash + " " + endHash + " " + startElementEdgeIndex + " " + endElementEdgeIndex, pid);
            doc.addUndoCommand(MODEL_ACTION_UNLINK + " " + startHash + " " + endHash + " " + edgeClassName + " " + startElementEdgeIndex, pid);
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
     * @param nodehash1
     * @param nodehash2
     * @param edgeIndex
     * @param pid
     */
    public void unlink(final String nodehash1, final String nodehash2, final int edgeIndex, final int pid) {
        unlink(nodehash1, nodehash2, null, edgeIndex, pid);
    }

    /**
     * @param nodehash1
     * @param nodehash2
     * @param edgeClass
     * @param edgeIndex
     * @param pid
     */
    public void unlink(final String nodehash1, final String nodehash2, final Class<? extends Edge> edgeClass, final int edgeIndex, final int pid) {
        ModelElement me1 = doc.findElementCoded(nodehash1);
        ModelElement me2 = doc.findElementCoded(nodehash2);
        unlink(me1, me2, edgeClass, edgeIndex, pid);
    }

    /**
     * @param me1
     * @param me2
     * @param edgeClass
     * @param pid
     */
    public final void unlink(final ModelElement me1, final ModelElement me2, final Class<? extends Edge> edgeClass, final int pid) {
        unlink(me1, me2, edgeClass, INVALID_EDGE_INDEX, pid);
    }

    /**
     * ACHTUNG: DIESE BEIDEN UNINK-FUNKTIONEN HABE ICH AM 24.10.2018 HINZUGEFÜGT. DAS PROZESSSTRUKTURPANEL MÜSSTE ÜBER EINE SOLCHE FUNKTION ARBEITEN.
     * DAS HIER IST DAZU DA, MICH DARAN ZU ERINNERN!
     *
     * @param edge
     * @param pid
     */
    public final void _unlink(final Edge edge, final int pid) {
        _unlink(edge, Direction.FORWARD, pid);
    }

    /**
     * ACHTUNG: DIESE BEIDEN UNINK-FUNKTIONEN HABE ICH AM 24.10.2018 HINZUGEFÜGT. DAS PROZESSSTRUKTURPANEL MÜSSTE ÜBER EINE SOLCHE FUNKTION ARBEITEN.
     * DAS HIER IST DAZU DA, MICH DARAN ZU ERINNERN!
     *
     * @param edge
     * @param direction
     * @param pid
     */
    public final void _unlink(final Edge edge, final Direction direction, final int pid) {
        ModelElement start = edge.getStart();
        ModelElement end = edge.getEnd();
        if (direction == Direction.FORWARD) {
            unlink(start, end, edge.getClass(), start.getEdgeIndex(edge), pid);
        } else {
            unlink(end, start, edge.getClass(), end.getEdgeIndex(edge), pid);
        }
    }

    /**
     * Anders als bei link() ist hier die Richtung, also die Reihenfolge der beiden ModelElemente nur wichtig, wenn es eine {@link DoubleMeaningEdge}
     * ist oder die übergebenen Elemente beide jeweils Start- und EndElement der Kantenklasse sein können. In allen anderen Fällen wird sonst auch
     * einfach versucht irgendeine Kante dieser Art zwischen den beiden übergebenen Elementen zu löschen.
     *
     * @param me1
     * @param me2
     * @param edgeClass
     * @param me1EdgeIndex
     * @param pid
     */
    public final void unlink(final ModelElement me1, ModelElement me2, final Class<? extends Edge> edgeClass, final int me1EdgeIndex, final int pid) {
        if (me1 == null || me2 == null) {
            return;
        }
        Edge edge = null;
        List<Edge> edges = null;

        Class<? extends ModelElement> me1Class = me1.getClass();
        Class<? extends ModelElement> me2Class = me2.getClass();
        boolean isDirectionImportent = MetaModel.isDoubleMeaningEdge(edgeClass) || metaModel.isConnecting(edgeClass, me1Class, me2Class) && metaModel.isConnecting(edgeClass, me2Class, me1Class);
        if (isDirectionImportent) {
            edges = me1.getEdgesTo(me2, edgeClass, me1EdgeIndex);
        } else {
            edges = me1.getEdgesWith(me2, edgeClass, me1EdgeIndex);
        }
        if (edges.isEmpty()) {
            return;
        } else if (edges.size() == 1) {
            edge = edges.get(0);
        } else {
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, Y_AXIS));
            ButtonGroup buttonGroup = new ButtonGroup();
            for (int i = 0; i < edges.size(); i++) {
                JRadioButton b = new JRadioButton(getElementsNameBuilder().getForwardMetaAssociationName(edges.get(i).getClass()));
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
        doc.addRedoCommand(MODEL_ACTION_UNLINK + " " + me1Hash + " " + me2Hash + " " + edgeClassName + " " + me1EdgeIndex, pid);
        //Undo-Kommando wird in deleteElement gesetzt (s. u.)
        //nur bei Kanten mit doppelter bedeutung kann man in bestimmten Richtungen unlinken. Bei allen anderen
        //ist die Richtung egal und das Unlinken ist das Löschen der Edge
        Class<? extends Edge> absoluteEdgeClass = edge.getClass(); // die übergebene Kanten-Klasse kann null gewesen oder eine Oberklasse sein
        if (MetaModel.isDoubleMeaningEdge(absoluteEdgeClass)) {
            DoubleMeaningEdge doubleMeaningEdge = (DoubleMeaningEdge) edge;
            if (doubleMeaningEdge.getConnectionState() == DOUBLE) {
                if (edge.getStart() == me1) {
                    doc.addUndoCommand(MODEL_ACTION_LINK + " " + absoluteEdgeClass.getName() + " " + edge.getHashString() + " " + me1Hash + " " + me2Hash + " " + me1.getEdgeIndex(edge) + " " + me2.getEdgeIndex(edge), pid);
                    doubleMeaningEdge.setConnectionState(BACKWARD);
                } else {
                    doc.addUndoCommand(MODEL_ACTION_LINK + " " + absoluteEdgeClass.getName() + " " + edge.getHashString() + " " + me2Hash + " " + me1Hash + " " + me2.getEdgeIndex(edge) + " " + me1.getEdgeIndex(edge), pid);
                    doubleMeaningEdge.setConnectionState(FORWARD);
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
        List<ModelElement> modelItemsWithNameExtensions = getModelItems(this, metaModel.getElementClassesWithNameExtensionPath());
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
        Collection<String> elementHashes2ExcludeFromJoin = new ArrayList<>();
        elementHashes2ExcludeFromJoin.add(hashString1);
        elementHashes2ExcludeFromJoin.add(hashString2);
        return joinRecursive(hashString1, hashString2, source, elementHashes2ExcludeFromJoin, pid);
    }

    /**
     * @param hashString1
     * @param hashString2
     * @param source
     * @param elementHashes2ExcludeFromJoin
     * @param pid
     * @return
     */
    private boolean joinRecursive(final String hashString1, final String hashString2, final GraphDocument source, final Collection<String> elementHashes2ExcludeFromJoin, final int pid) {
        ModelElement modelElement1 = doc.findElementCoded(hashString1);
        ModelElement modelElement2 = doc.findElementCoded(hashString2);
        if (modelElement1 == null || modelElement2 == null || modelElement1 == modelElement2) {
            return false;
        }
        //prüfen, ob es sich um Node gleichen Typs handelt (nur diese können vereint werden)
        if (!(modelElement1 instanceof Node && modelElement2 instanceof Node)) {
            if (interactive_mode) {
                JOptionPane.showMessageDialog(getMainFrame(), getResString("nur_knoten_sel"), getResString("tool3lgm"), INFORMATION_MESSAGE);
            }
            return false;
        }
        Node node1 = (Node) modelElement1;
        Node node2 = (Node) modelElement2;
        Class<? extends ModelElement> nodeClass = node1.getClass();
        if (nodeClass != node2.getClass()) {
            if (interactive_mode) {
                JOptionPane.showMessageDialog(null, getResString("nur_gleiche_sel"), getResString("tool3lgm"), INFORMATION_MESSAGE);
            }
            return false;
        }
        //Beginne umhängen der Kanten
        doc.start_transaction(pid);
        for (Szenario s : szenarios) {
            s.start_transaction(pid, false);
        }
        //Namen und Beschreibung des zu löschenden Node an den verbleibenden anhängen
        //und ExtIDs und benutzerdef. Eigenschaftsfelder zusammenführen
        node2.join(node1, false);
        //knoten2.createNameWithSzens(doc);

        for (Class<? extends Edge> edgeClass : metaModel.getSubordinatedJoinbleTypes(node2.getClass())) {
            List<ModelElement> sjt1 = node1.getConnectedElements(edgeClass);
            List<ModelElement> sjt2 = node2.getConnectedElements(edgeClass);
            if (sjt1.size() == 1 && sjt2.size() == 1) {
                ModelElement me1 = sjt1.get(0);
                ModelElement me2 = sjt2.get(0);
                String hash1 = me1.getHashString();
                if (elementHashes2ExcludeFromJoin.contains(hash1)) {
                    continue;
                }
                String hash2 = me2.getHashString();
                if (elementHashes2ExcludeFromJoin.contains(hash2)) {
                    continue;
                }
                elementHashes2ExcludeFromJoin.add(hash1);
                elementHashes2ExcludeFromJoin.add(hash2);
                joinRecursive(hash1, hash2, source, elementHashes2ExcludeFromJoin, pid);
            }
        }
        //Das hier ist Hardcore, weil hier das IterableObject zurück auf List gecastet wird-> eigentlich müsste sich Edge selbst irgenwie darum kümmern!
        List<Edge> deleteNodeEdges = (List<Edge>) node1.getEdges();//ArrayList der Kanten des zu löschendn Knotens
        List<Edge> remainNodeEdges = (List<Edge>) node2.getEdges();//ArrayList der Kanten des verbleibenden Knotens
        //für jede Edge vom zu löschenden Node
        while (deleteNodeEdges.size() > 0) {
            Edge egde = deleteNodeEdges.get(0);
            ModelElement startElement = egde.getStart(); //Startknoten der zu übernehmenden Edge merken
            ModelElement endElement = egde.getEnd(); //Endknoten -"-
            //zu löschenden Node durch den verbleibenden ersetzen
            if (startElement == node1) {
                startElement = node2;
                endElement = egde.getEnd();
            } else {
                startElement = egde.getStart();
                endElement = node2;
            }
            boolean deleteEdge = false;
            if (startElement == endElement) {
                deleteEdge = true;
            } else {
                //abfangen, ob im verbleibenden Node an gleicher Stelle schon eine Edge vorkommt, Edge testKante = new Edge(startKnoten, endKnoten, false);
                Edge testEdge;
                try {
                    testEdge = egde.getClass().newInstance();
                } catch (Exception e) {
                    Log.show(ERROR, getResString("FehlerAllgemein"), e);
                    continue;
                }
                testEdge.setNodes(startElement, endElement, false);
                //TODO:AXS: ich glaube hier fliegen Kanten raus, die in unterschiedliche Richtungen zeigen, weil isEqualTo nur die Elemente und die Kanteklasse prüft
                for (int i = 0; i < remainNodeEdges.size(); i++) {
                    //für jede Edge des verbleibenden Elementes prüfen, ob umzuhängende Edge und eine Edge in
                    // kantenVector2 dieselben Elemente verbindet
                    if (remainNodeEdges.get(i).isEqualTo(testEdge)) {
                        deleteEdge = true;
                        break;
                    }
                }
            }
            if (deleteEdge) { //wenn die Edge doppelt vorkommen würde
                deleteElement(egde, doc, pid);
                //              doc.removeEdge(kante, pid);//Edge einfach komplett löschen
            } else { //Edge muss umgehängt werden
                node1.removeEdge(egde); //im zu löschenden Node die Edge entfernen
                egde.setNodes(startElement, endElement);//die Edge wirklich an knoten2 binden
            }
        }
        for (Szenario szen : szenarios) {
            NodeContainer nc1 = (NodeContainer) node1.getContainer(szen);
            NodeContainer nc2 = (NodeContainer) node2.getContainer(szen);
            // jetzt umhängen aller Container von knoten1 auf knoten2 in allen Teilmodellen
            if (nc2 == null && nc1 != null) {
                //              szen.removeKnotContainer((NodeContainer) knoten1.getContainer(szen), pid);
                removeContainerFromSubmodel(node1.getContainer(szen), pid);
                nc1.setElement(node2);
                szen.getLayer(node2.layerFor()).add(nc1);
            }
            NodeContainer nc = null;
            if (nc2 != null) {
                nc = nc2;
            } else if (nc1 != null) {
                nc = nc1;
            }
            if (nc != null) {
                szen.createEdgeContainer(nc, szen, false, pid);
                nc.refreshText();
                // alle abhängigen Node vor den zusammengeführten stellen
                szen.start_transaction(TransactionManager.STANDARD_PID, false);
                szen.moveDependentNodeContainersUp(nc, TransactionManager.STANDARD_PID, false);
                szen.finish_transaction(TransactionManager.STANDARD_PID, false);
            }
        }
        deleteElement(node1, doc, pid);
        //      doc.removeNode((NodeContainer)knoten1.getContainer(doc), pid); //alle Kanten umgehängt -> wegfallenden Node komplett löschen
        for (Szenario szen : szenarios) {
            szen.finish_transaction(pid, false);
        }
        //Der TransaktionQueue wird einfach gelöscht. Das muss unbedingt mal geändert werden -> also alles richtig UNDO-/REDO-mässig
        tman.clearTransactionQueue();
        doc.finish_transaction(pid);
        distribute(DATA_CHANGED, null, source, pid);
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
     * @see #setChanged(boolean) mit false
     */
    public void setUnchanged() {
        setChanged(false);
    }

    /**
     * @param c
     */
    private void setChanged(final boolean c) {
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
     * @param changeType
     */
    public final void distribute(final LGMChangeType changeType) {
        distribute(changeType, null, null, STANDARD_PID);
    }

    /**
     * @param changeType
     * @param last_elem
     * @param source
     * @param pid
     */
    public final void distribute(final LGMChangeType changeType, final ElementContainer last_elem, final GraphDocument source, final int pid) {
        setChanged(true);
        if (isBulkMode()) {
            return;
        }
        boolean deliverStatic = true;
        if (source != null) {
            source.distributeEventIntern(changeType, last_elem, deliverStatic, pid);
            deliverStatic = false;
        }
        LGMChangeListener.distributeEvent(changeType, listener, source == null ? doc : source, last_elem, false);
        if (!changeType.isSzenarioSpecific()) {
            if (doc != source) {
                doc.distributeEventIntern(changeType, last_elem, deliverStatic, pid);
                deliverStatic = false;
            }
            for (Szenario s : szenarios) {
                if (s != source) {
                    s.distributeEventIntern(changeType, last_elem, deliverStatic, pid);
                    deliverStatic = false;
                }
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
     * @return
     */
    public GDCollectionIconTable getIconTable() {
        return iconTable;
    }

    /**
     * @param bm
     * @return the previous bulk mode
     */
    public boolean setBulkMode(final boolean bm) {
        boolean oldBulkMode = bulk_mode;
        bulk_mode = bm;
        return oldBulkMode;
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
        distribute(MODEL_OR_SZENARIO_NAME_CHANGED, null, getMainGraphDocument(), STANDARD_PID);
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
     * COMMENTME
     */
    private boolean interactive_mode = true;

    /**
     * @param flag
     * @return previous interactive mode
     */
    public boolean setInteractiveMode(final boolean flag) {
        boolean oldMode = interactive_mode;
        interactive_mode = flag;
        return oldMode;
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
            for (NodeContainer nc : lc.getGraphNodeContainers()) {
                Node node = nc.getNode();
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
            for (EdgeContainer ec : lc.getEdgeContainers()) {
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
            for (BendpointContainer bc : lc.getBendpointContainers()) {
                Bendpoint bendpoint = bc.getBendpoint();
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
                if (!(me instanceof Bendpoint)) {
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
    /**
     * @param me Element dessen abhängige Elemente gefunden werden sollen
     * @param elements
     * @param userFields
     */
    private void resolveCopyDependencies(final ModelElement me, final List<ModelElement> elements, final Set<UserField> userFields) {
        if (me instanceof Bendpoint) {
            return;
        }
        for (UserField userField : me.getUserFieldInputValueKeys()) {
            userFields.add(userField);
        }
        if (me instanceof Edge) {
            for (BendpointContainer bpc : doc.getLayer(me.layerFor()).getBendpointContainers()) {
                Bendpoint bendpoint = bpc.getBendpoint();
                String edgeHash = bendpoint.getEdgeHash();
                if (edgeHash != null && edgeHash.equals(me.getHashString())) {
                    if (!elements.contains(bendpoint)) {
                        elements.add(bendpoint);
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
        for (Class<? extends ModelElement> elementClass : metaModel.getCopyDependencies(me.getClass())) {
            for (ElementContainer ec : me.getConnectedContainer(elementClass, doc)) {
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
            if (ToolXMLParser.isParsableXMLFile(clipStream)) {
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
    public final void addGDCollectionChangeListener(final LGMChangeListener gdl) {
        listener.add(gdl);
    }

    /**
     * @param gdl
     */
    public final void removeGDCollectionChangeListener(final LGMChangeListener gdl) {
        listener.remove(gdl);
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
        for (Class<? extends ModelElement> elemClass : metaModel.uniqueNodes) {
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

    //////////////
    // Optional //
    //////////////

    public final boolean addOptional(final OptionalEdge optional) {
        return optionalElements.add(optional);
    }

    public final boolean removeOptional(final OptionalEdge optional) {
        return optionalElements.remove(optional);
    }

    public final boolean isOptional(final Object o) {
        return optionalElements.contains(o);
    }

}
