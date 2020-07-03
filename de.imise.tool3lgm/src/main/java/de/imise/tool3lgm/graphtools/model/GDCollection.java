package de.imise.tool3lgm.graphtools.model;

import static com.google.common.base.Strings.isNullOrEmpty;
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
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_POSITION;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_SHAPE;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_ALPHA;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_COLOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_LAYER_SIZE_FACTOR;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_UNLINK;
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
import static de.imise.util.htmlxml.ParseSaveStringHandler.getDecodedParseSaveString;
import static de.imise.util.htmlxml.ParseSaveStringHandler.getParseSaveString;
import static java.lang.Integer.parseInt;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

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
import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.dialog.ElemenPropertyDialogsContext;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType;
import de.imise.tool3lgm.graphtools.path.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.paths.AbstractPath;
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
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.ToolXMLParser;
import de.imise.util.StringUtils;
import de.imise.util.collections.AlphabeticalSet;
import de.imise.util.outparam.OutParamObject;
import de.imise.util.swing.dialog.NameAndColorInputDialog;

/**
 * Represents an entire model. GDCollection = {@link GraphDocument} collection - so a collection of all sub-models. A sub-model is a selection of
 * model elements with a seperate graph view.
 *
 * @author thomas, AXS
 */
public final class GDCollection extends UserFieldTarget implements MetaModelSpecific {

    /** Holds the {@link MetaModelContext} and the type of the model ( {@link Tool3lgmModelType.ModelCategory} */
    private Tool3lgmModelType modelType;

    /** Holds all concepts (types of model elements) and the relations between this concepts */
    private MetaModel metaModel;

    /** The undo-redo-manager */
    protected TransactionManager tman = new TransactionManager();

    /** The data structure to store all model changing transactions for the undo-redo-manager */
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
    private LGMGraphDocument mainDoc;

    /** Resolves the dependencies of copied elements */
    private CopyDependencyResolver copyDependencyResolver;

    /**
     * Alle {@link LGMChangeListener}, die immer benachrichtigt werden - egal ob eine Transaktion durch einen Dialog offen ist oder nicht.
     */
    private final List<LGMChangeListener> allListener = new ArrayList<>();

    /**
     * Alle {@link LGMChangeListener}, die nur benachrichtigt werden, wenn keine Transaktion geöffnet ist bzw. die nur auf Transaktionen
     * reagieren, die abgeschlossen sind. Das ist der Fall, wenn das Change-Ereignis nicht durch eine geöffneten Dialog kommt.
     */
    private final List<LGMChangeListener> closedListener = new ArrayList<>();

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
     * Wenn <code>true</code>, werden keine Undo-/Redo-Commands aufgezeichnet. Dieser Modus ist beim Einlesen
     * bzw. vor dem kompletten init eines Modells aktiv und immer dann, wenn UNDO oder REDO ausgeführt wird, da
     * währenddessen die Kommandos nicht noch einmal aufgezeichnet werden müssen.
     * <code>false</code> ist der Default. Nach dem Init muss dieser auf <code>true</code> gesetzt werden,
     * damit die dann Kommandos geloggt werden.
     */
    private boolean bulk_mode = true;

    /**
     * <code>true</code> means the model is in 'automatic mode'. In this mode the user will not be asked
     * for any descision or some model change events are not disributed.
     * If <code>false</code> the user can be asked and all model change events are disributed.
     * Thsi mode is active, e.g. if elements should be generated without aksing the user for the name of
     * the element. This elements get standard names.
     */
    private boolean automatic_mode = true;

    /**
     * Wird <code>true</code> sobald der bulk_mode das erste Mal auf <code>false</code> gesetzt wurde.
     */
    private boolean initialized = false;

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
     * The list of change events which were fired during bulk_mode.
     * This list contains teh same/equals event alsways only one times.
     * The events are collected if bulk_mode ist active and they are
     * fired if bulk_mode becomes inactive.
     */
    private final List<LGMChangeEvent> changeEvents = new ArrayList<>();

    /**
     * @param templateModel
     */
    public GDCollection() {
        fileHandler = new GDCollectionFileHandler(this);
        imExportHandler = new GDCollectionImExportHandler(this);
    }

    /**
     * @param metaModelContext
     * @param templateModel
     */
    public GDCollection(@Nonnull final Tool3lgmModelType modelType) {
        this();
        setModelType(modelType);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Sets the {@link MetaModelContext} for this model.
     *
     * @param modelype
     *            the context that contains the metamodel of this model and the corresponding resource bundle and the type of the model
     */
    public void setModelType(final Tool3lgmModelType modelType) {
        this.modelType = modelType;
        metaModel = modelType.getMetaModel();
        mainDoc = new LGMGraphDocument(this);
        userFieldDefinitions = new UserFieldDefinitions(this);
        addClosedTransactionsListener(userFieldDefinitions);
        activeGraphDocumentsList.add(mainDoc);
        UserfieldResourceHandler.loadDefaultUserfieldDefinition(this);
    }

    /**
     * @return the model type
     */
    public Tool3lgmModelType getModelType() {
        return modelType;
    }

    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return modelType == null ? null : modelType.getMetaModelDefinitionClass();
    }

    /**
     * @return the model category of the model type
     */
    public ModelCategory getModelCategory() {
        return modelType.getModelCategory();
    }

    /**
     * @param modelCategory
     */
    public void setModelCategory(final Tool3lgmModelType.ModelCategory modelCategory) {
        modelType.setModelCategory(modelCategory);
    }

    /**
     *
     */
    public final CopyDependencyResolver getCopyDependencyResolver() {
        if (copyDependencyResolver == null) {
            copyDependencyResolver = new CopyDependencyResolver(this);
        }
        return copyDependencyResolver;
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
     * @param automatic_mode
     * @return previous automatic mode
     */
    public boolean setAutomaticMode(final boolean automatic_mode) {
        //Sys.err("automatic_mode: " + this.automatic_mode + " -> " + automatic_mode);
        boolean oldMode = this.automatic_mode;
        this.automatic_mode = automatic_mode;
        return oldMode;
    }

    /**
     * @return
     */
    public boolean isAutomaticMode() {
        return automatic_mode;
    }

    /**
     * @return
     */
    public boolean isInitialzed() {
        return initialized;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
        distribute(MODEL_OR_SZENARIO_NAME_CHANGED, null, getMainDoc(), STANDARD_PID);
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
     * @param gdl
     */
    public final void addAllTransactionsListener(final LGMChangeListener gdl) {
        //System.err.println("addAllTransactionsListener " + this);
        //        Sys.errn(3, allListener.size() + " " + gdl);
        //        if (allListener.contains(gdl)) {
        //            Sys.err1(gdl);
        //        }
        allListener.add(gdl); //Don't prevent adding multiple the same listener! The MainFrameDesktopTabbedPane needs to be added multiple
    }

    /**
     * @param gdl
     */
    public final void removeAllTransactionsListener(final LGMChangeListener gdl) {
        //        System.err.println("removeAllTransactionsListener " + this);
        //        Sys.errn(3, allListener.size() + " " + gdl);
        allListener.remove(gdl);
    }

    /**
     * @param gdl
     */
    public final void addClosedTransactionsListener(final LGMChangeListener gdl) {
        closedListener.add(gdl);
    }

    /**
     * @param gdl
     */
    public final void removeClosedTransactionsListener(final LGMChangeListener gdl) {
        closedListener.remove(gdl);
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
        int activeLayer = getActiveLayer();
        Szenario szenario = new Szenario(this, title, description, szenHash);
        szenarios.add(szenario);
        activeGraphDocumentsList.add(szenario);
        if (log) {
            mainDoc.start_transaction(pid);
            mainDoc.addUndoCommand(MODEL_ACTION_DELETE_SUBMODEL + " " + szenario.getHashString(), pid);
            mainDoc.addRedoCommand(MODEL_ACTION_CREATE_SUBMODEL + " " + getParseSaveString(szenario.getTitle()) + " " + getParseSaveString(szenario.getDescription()) + " " + szenario.getHashString(), pid);
            mainDoc.finish_transaction(pid);
        }
        setChanged(true);
        distribute(SZENARIO_ADDED, null, szenario, pid);
        setActiveLayer(activeLayer);
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
        mainDoc.start_transaction(pid);
        //bei allen Elementen, die mit dem zu löschenden Teilmodell verknüpft sind
        //den Verweis auf dieses Teilmodell löschen (das passiert im Hauptmodell)
        for (LayerContainer layer : mainDoc.getLayers()) {
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
            mainDoc.addUndoCommand(MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layerIndex + " " + szen.layer[layerIndex].getColor().getRGB(), pid);
            mainDoc.addUndoCommand(MODEL_ACTION_SET_LAYER_ALPHA + " " + szenHash + " " + layerIndex + " " + szen.layer[layerIndex].getAlpha(), pid);
            mainDoc.addUndoCommand(MODEL_ACTION_SET_LAYER_SIZE_FACTOR + " " + szenHash + " " + szen.getPageSizeFactor(), pid);
        }
        mainDoc.addUndoCommand(MODEL_ACTION_CREATE_SUBMODEL + " " + getParseSaveString(szen.getTitle()) + " " + getParseSaveString(szen.getDescription()) + " " + szen.hashString, pid);
        mainDoc.addRedoCommand(MODEL_ACTION_DELETE_SUBMODEL + " " + szen.hashString, pid);
        mainDoc.finish_transaction(pid);
        setChanged(true);
        distribute(SZENARIO_REMOVED, null, szen, pid);
    }

    /**
     * @param szenHash
     * @param title
     * @param pid
     */
    public void renameSzenario(final String szenHash, final String title, final int pid) {
        GraphDocument szen = getSzenarioCoded(szenHash);
        if (szen == null) {
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

        mainDoc.start_transaction(pid);
        mainDoc.addUndoCommand(MODEL_ACTION_RENAME_SUBMODEL + " " + szen.hashString + " " + getParseSaveString(oldTitle), pid);
        mainDoc.addRedoCommand(MODEL_ACTION_RENAME_SUBMODEL + " " + szen.hashString + " " + getParseSaveString(szenTitle), pid);
        mainDoc.finish_transaction(pid);

        szen.setTitle(szenTitle);
        for (ModelElement me : szen.getModelItems(ModelElement.class, true)) {
            me.invalidateNameWithSzens();
        }
        setChanged(true);
    }

    /**
     * Setzt das übergebene <code>GraphDocument</code> als das aktuell selektierte.
     *
     * @param doc
     */
    public void setSelectedDoc(final GraphDocument doc) {
        activeGraphDocumentsList.remove(doc);
        activeGraphDocumentsList.add((LGMGraphDocument) doc);
        distribute(LGMChangeType.SELECTED_SZENARIO_CHANGED, null, doc, STANDARD_PID);
    }

    /**
     * Nachdem alle Szenarios eingelesen oder das eine eines neuen Modell erstellt wurden, muss
     * man einmal diese Funktion hier aufrufen, damit das richtige Graphdocument selektiert ist.
     */
    public void initSelectedDocByViewParameterFromFile() {
        for (Szenario szen : szenarios) {
            GraphViewParameter graphViewParameter = szen.getGraphViewParameter();
            if (graphViewParameter != null && graphViewParameter.selected) {
                setSelectedDoc(szen);
                return;
            }
        }
        setSelectedDoc(mainDoc);
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
            boolean showColorChooser = metaModel.hasSortedEdgeClassesToPaintable(me.getClass());
            d.showDialogOnMousePointer(getResString("name_eing"), me.toString(), showColorChooser);
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
        GraphDocument doc = ec.getGraphDocument();
        //im Hauptdokument ist die LayoutInformation der Container egal
        if (doc == mainDoc) {
            return;
        }
        String ecHash = ec.getHashString();
        String ecDocHash = doc.hashString;
        if (ec.getColor() != null) {
            doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_COLOR + " " + ecDocHash + " " + ecHash + " " + ec.getColor().getRGB(), pid);
            doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_ALPHA + " " + ecDocHash + " " + ecHash + " " + ec.getAlpha(), pid);
        }
        if (ec.getForm() != null) {
            doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_SHAPE + " " + ecDocHash + " " + ecHash + " " + ec.getForm(), pid);
        }
        if (!ec.hasStandardFont()) {
            doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_FONT + " " + ecDocHash + " " + ecHash + " " + getParseSaveString(ec.getFontName()) + " " + ec.getFontSize() + " " + ec.getFontStyle(), pid);
        }
        if (ec instanceof NodeContainer) {
            NodeContainer kc = (NodeContainer) ec;
            String iconName = kc.getIconString();
            if (iconName != null) {
                doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_ICON + " " + ecDocHash + " " + ecHash + " " + iconName, pid);
            }
            doc.addUndoCommand(MODEL_ACTION_MOVE_ORDER + " " + ecDocHash + " " + ecHash + " " + doc.layer[ec.layerFor()].indexOf(ec), pid);
            doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_POSITION + " " + ecDocHash + " " + ecHash + " " + ec.getX() + " " + ec.getY() + " " + ec.getWidth() + " " + ec.getHeight(), pid);
            if (!kc.isVisible()) {
                doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF + " " + ecDocHash + " " + ecHash, pid);
            }
            if (ec.getTextPositionHorizontal() != STANDARD_ELEMENT_LAYOUT.textPositionHorizontal) {
                doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL + " " + ecDocHash + " " + ecHash + " " + kc.get3LGMLayout().textPositionHorizontal, pid);
            }
            if (ec.getTextPositionVertical() != STANDARD_ELEMENT_LAYOUT.textPositionVertical) {
                doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL + " " + ecDocHash + " " + ecHash + " " + kc.get3LGMLayout().textPositionVertical, pid);
            }
            if (ec.getTextAlignmentHTML() != STANDARD_ELEMENT_LAYOUT.textAlignmentHTML) {
                doc.addUndoCommand(MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML + " " + ecDocHash + " " + ecHash + " " + kc.get3LGMLayout().textAlignmentHTML, pid);
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
            if (ecDoc == mainDoc || szen != null && szen != ecDoc) {
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
        deleteElement(me, mainDoc, pid);
    }

    /**
     * @param me
     * @param doc
     *            GraphDocument, das die Transaktion starten und beenden soll, also dessen Selektion im Falle
     *            eines Undo wieder hergestellt wird. Das Element selbst wird natürlich aus allen Teilmodellen
     *            und dem Hauptmodell gelöscht.
     * @param pid
     */
    public final void deleteElement(final ModelElement me, final GraphDocument doc, final int pid) {
        List<ModelElement> list = new ArrayList<>();
        list.add(me);
        deleteElements(list, doc, pid);
    }

    /**
     * @param elementHashesToDelete
     * @param pid
     */
    public final void deleteElements(final String[] elementHashesToDelete, final int pid) {
        deleteElements(elementHashesToDelete, mainDoc, pid);
    }

    /**
     * @param elementHashesToDelete
     * @param doc
     *            GraphDocument, das die Transaktion starten und beenden soll, also dessen Selektion im Falle
     *            eines Undo wieder hergestellt wird. Das Element selbst wird natürlich aus allen Teilmodellen
     *            und dem Hauptmodell gelöscht.
     * @param pid
     */
    public final void deleteElements(final String[] elementHashesToDelete, final GraphDocument doc, final int pid) {
        List<ModelElement> elementsToDelete = new ArrayList<>(elementHashesToDelete.length);
        for (String elementHash : elementHashesToDelete) {
            ModelElement me = mainDoc.findElementCoded(elementHash);
            elementsToDelete.add(me);
        }
        deleteElements(elementsToDelete, doc, pid);
    }

    /**
     * @param elementsToDelete
     * @param pid
     */
    public final void deleteElements(final List<? extends ModelElement> elementsToDelete, final int pid) {
        deleteElements(elementsToDelete, mainDoc, pid);
    }

    /**
     * @param elementsToDelete
     * @param doc
     *            GraphDocument, das die Transaktion starten und beenden soll, also dessen Selektion im Falle
     *            eines Undo wieder hergestellt wird. Das Element selbst wird natürlich aus allen Teilmodellen
     *            und dem Hauptmodell gelöscht.
     * @param pid
     */
    public final void deleteElements(final List<? extends ModelElement> elementsToDelete, final GraphDocument doc, final int pid) {
        //das wird die Liste mit allen zu löschenden Elementen. Das sind alle Elemente aus <code>elementsToDelete</code>,
        //alle Kanten dieser Elemente und rekursiv alle von den zu löschenden Elementen abhängigen Elemente (min. Karfinalität=1)
        //sowie deren Kanten
        List<ModelElement> allElementsToDelete = new ArrayList<>(elementsToDelete);
        //In dieses Set kommen alle Elemente, deren Löschen man nicht in den RedoKommandos loggen muss, weil beim Löschen eines
        //anderen Elementes eine minimale Kardinalität unterschritten ist, so dass sie automatisch mitgelöscht werden
        Set<ModelElement> dependentDeletedElements = new HashSet<>();
        //das wird die Liste aller zu löschenden Verbindungen
        List<Edge> edgesToDelete = new ArrayList<>();
        doc.start_transaction(pid);
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
                ElementContainer kpc = me.getContainer(doc);
                if (kpc == null) {
                    kpc = me.getContainer(mainDoc);
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
                    if (elem != null && elem.countConnections(edgeClass) <= MetaModel.getMinCardinality(elem.getClass(), edgeClass)) {
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
            doc.finish_transaction(pid);
            doc.distributeEvent(DATA_CHANGED, pid);
            doc.distributeEvent(SELECTION_CHANGED, pid);
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
                        mainDoc.addUndoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edgeHash + " " + startHash + " " + endHash + " " + startEdgeIndex + " " + endEdgeIndex, pid);
                        mainDoc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + edgeHash, pid);
                        break;
                    case BACKWARD:
                        mainDoc.addUndoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edgeHash + " " + endHash + " " + startHash + " " + endEdgeIndex + " " + startEdgeIndex, pid);
                        mainDoc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + edgeHash, pid);
                        break;
                    case DOUBLE:
                        mainDoc.addUndoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edgeHash + " " + endHash + " " + startHash + " " + endEdgeIndex + " " + startEdgeIndex, pid);
                        mainDoc.addUndoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edgeHash + " " + startHash + " " + endHash + " " + startEdgeIndex + " " + endEdgeIndex, pid);
                        mainDoc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + edgeHash, pid);
                        break;
                    }
                    ks.removeEdge(edge);
                    ke.removeEdge(edge);
                }
                ElementContainer edgeC = edge.getContainer(mainDoc);
                int layer = edge.layerFor();
                LayerContainer lc = mainDoc.layer[layer];
                lc.remove(edgeC);
                //jetzt den Container selbst löschen (kann man sich sparen, weil die Edge seobst nicht mehr gepspeichert wird)
                edge.removeContainer(mainDoc);
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
            mainDoc.addUndoCommand(MODEL_ACTION_CREATE_NODE + " " + meClass.getName() + " " + getParseSaveString(me.getName()) + " " + getParseSaveString(me.getDescription()) + " " + meHash, pid);
            if (!dependentDeletedElements.contains(me)) {
                mainDoc.addRedoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + meHash, pid);
            }
            //den Container des zu löschenden Elementes im Hauptmodell holen
            mainDoc.layer[me.layerFor()].remove(me.getContainer(mainDoc));
            //und danach erst im Table des Elements
            //das Löschen aus dem ContainerTbale des Elementes kann man sich sparen, da das Element nirgends mehr gespeichert werden sollte
            //me.removeContainer(this.doc);
            if (me instanceof OptionalEdge) {
                removeOptional((OptionalEdge) me);
            }
        }
        doc.finish_transaction(pid);
        doc.distributeEvent(DATA_CHANGED, pid);
        doc.distributeEvent(SELECTION_CHANGED, pid);
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
        mainDoc.getLayer(layerIndex).remove(bendpoint.getContainer(mainDoc));
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
        bendpoint.setName(mainDoc.getNextNewName(bendpoint.getClass()));
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
        mainDoc.getLayer(layerNumber).add(new BendpointContainer(bendpoint, mainDoc));
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
            nc = (NodeContainer) me.createContainer(mainDoc);
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
            String newName = nameIsEmpty ? mainDoc.getNextNewName(me.getClass()) : name.substring(1);
            me.setName(newName, false);
            if (!isAutomaticMode() && !metaModel.isGenerateName(me.getClass())) {
                if (!askNameAndColor(nc)) {
                    return null;
                }
            }
        }
        if (description != null && !description.trim().equals("")) {
            me.setDescription(getDecodedParseSaveString(description));
        }
        mainDoc.start_transaction(pid);
        mainDoc.addRedoCommand(MODEL_ACTION_CREATE_NODE + " " + me.getClass().getName() + " " + getParseSaveString(me.getName()) + " " + getParseSaveString(me.getDescription()) + " " + me.getHashString(), pid);
        if (nc.getColor() != null) {
            mainDoc.addRedoCommand(MODEL_ACTION_SET_ELEMENT_COLOR + " " + mainDoc.hashString + " " + me.getHashString() + " " + nc.getColor().getRGB(), pid);
        }
        mainDoc.addUndoCommand(MODEL_ACTION_DELETE_FROM_MODEL + " " + me.getHashString(), pid);
        // den Layer bestimmen auf dem das Element eingefügt werden soll
        int layerNumber = me.layerFor();
        //das hier darf eigentlich nur bei Textfeldern passieren, da diese keinen festen Layer haben. Wahrscheinlich
        //würde dieser Weg auch bei allen anderen Elementen funktionieren, was aber nicht getestet ist.
        if (layerNumber == ModelConstants.NO_LAYER) {
            layerNumber = mainDoc.getActiveLayer().getLayerNumber();
        }
        LayerContainer lc = mainDoc.getLayer(layerNumber);
        if (lc.add(nc) == null) {
            mainDoc.undo(pid);
            return null;
        }
        boolean oldAutomaticMode = setAutomaticMode(true);
        createInitialSubtypes(me, pid);
        setAutomaticMode(oldAutomaticMode);
        mainDoc.finish_transaction(pid);
        mainDoc.distributeEvent(DATA_CHANGED, pid);
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
            Class<? extends ModelElement> subType = MetaModel.isStartClass(subTypeEdgeClass, elementClass) ? getEndClass(subTypeEdgeClass) : getStartClass(subTypeEdgeClass);
            //minimale kardinalität für die Unterelemente
            int minCardForSubType = MetaModel.getMinCardinality(me.getClass(), subTypeEdgeClass);
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
     * Updates all {@link InferenceEdge}s. Missing wil be created and superflous will be reomoved.
     *
     * @param pid
     */
    public void updateInferenceEdges(final int pid) {
        removeInferenceEdges(pid); //first remove, so there must not be checked all potential new created inferenceEgdes if they are superflous
        createInferenceEdges(pid);
    }

    /**
     * If <code>true</code>
     */
    private boolean lockInferenceEgdeCreation = false;

    /**
     * Removes all superflous {@link InferenceEdge}s.
     *
     * @param pid
     */
    void removeInferenceEdges(final int pid) {
        removeInferenceEdges(false, pid);
    }
    /**
     * Removes all superflous {@link InferenceEdge}s.
     *
     * @param lockAndForceDelete if <code>true</code> the global variable lockInferenceEgdeCreation will be
     *            switched to <code>true</code> too, so that the next normal call of {@link #createInferenceEdges(int)}
     *            will not really create these edges until the global variable lockInferenceEgdeCreation will be switched
     *            back to <code>false</code>. Additionally if this parameter here is <code>false</code>, then only those
     *            InferenceEdges are removed whose condition metapaths are not fulfilled. If the parameter is <code>true</code>,
     *            then all InferenceEdges are removed.
     * @param pid
     */
    void removeInferenceEdges(final boolean lockAndForceDelete, final int pid) {
        if (lockInferenceEgdeCreation) {
            return;
        }
        if (lockAndForceDelete) {
            lockInferenceEgdeCreation = true;
        }
        //Sys.outn(15, "REMOVE   Model name=" + getName() + " (" + getModelCategory().name() + ")");
        //get all InferenceEdge classes
        Collection<Class<? extends InferenceEdge>> inferenceEdgeClasses = metaModel.getInferenceEdgeClasses();
        //for every InferenceEdge class
        for (Class<? extends InferenceEdge> inferenceEdgeClass : inferenceEdgeClasses) {
            //InferenceEdge is an interface -> check if this class is really an Edge
            if (Edge.class.isAssignableFrom(inferenceEdgeClass)) {
                //get the InferenceEdge class as Edge class
                Class<? extends Edge> edgeClass = inferenceEdgeClass.asSubclass(Edge.class);
                //get the condition metapath of this InferenceEdge
                AbstractMetaPath conditionMetaPath = metaModel.getInferenceEdgeConditionMetaPath(inferenceEdgeClass);
                //get the start and end element class of this InferencenEdge class
                Class<? extends ModelElement> edgeStartClass = Edge.getStartClass(edgeClass);
                Class<? extends ModelElement> edgeEndClass = Edge.getEndClass(edgeClass);
                //get all instances of this InferenceEdge class
                List<ModelElement> modelItems = mainDoc.getModelItems(edgeClass);
                //for every instance of this InferenceEdge class
                for (ModelElement edgeItem : modelItems) {
                    //get the InferenceEdge as Edge
                    Edge edge = (Edge) edgeItem;
                    //get the start and end element of this edge
                    ModelElement edgeStart = edge.getStart();
                    ModelElement edgeEnd = edge.getEnd();
                    boolean remove = lockAndForceDelete;
                    if (!remove) {
                        //is the condition metapath defined in the same direction like the edge?
                        boolean readEdgeForward = conditionMetaPath.isStartAndEndClass(edgeStartClass, edgeEndClass);
                        //is the condition for this InferenceEdge still fulfilled?
                        remove = !PathFunctions.isDirectConnected(readEdgeForward ? edgeStart : edgeEnd, readEdgeForward ? edgeEnd : edgeStart, conditionMetaPath);
                    }
                    //if not -> remove the InferenceEdge
                    if (remove) {
                        unlink(edgeStart, edgeEnd, edgeClass, pid);
                    }
                }
            }
        }
    }

    /**
     * Creates all missing {@link InferenceEdge}s.
     *
     * @param pid
     */
    void createInferenceEdges(final int pid) {
        createInferenceEdges(false, pid);
    }
    /**
     * Creates all missing {@link InferenceEdge}s.
     *
     * @param unlock if <code>true</code> the global variable {@link #lockInferenceEgdeCreation}
     *            is set or left to <code>true</code>. If <code>false</code> the creation is only executed
     *            if the global variable <code>lockInferenceEgdeCreation</code> is <code>false</code>. This
     *            global variable can be set to <code>false</code> by the corresponding function
     *            {@link #removeInferenceEdges(int)}. This prevents creation of all {@link InferenceEdge}s
     *            until this function is called with unlock <code>true</code>.
     * @param pid
     */
    void createInferenceEdges(final boolean unlock, final int pid) {
        if (unlock) {
            lockInferenceEgdeCreation = false;
        }
        if (lockInferenceEgdeCreation) {
            return;
        }
        //Sys.outn(15, "CREATE   Model name=" + getName() + " (" + getModelCategory().name() + ")");
        //get all InferenceEdge classes
        Collection<Class<? extends InferenceEdge>> inferenceEdgeClasses = metaModel.getInferenceEdgeClasses();
        //for every InferenceEdge class
        for (Class<? extends InferenceEdge> inferenceEdgeClass : inferenceEdgeClasses) {
            //InferenceEdge is an interface -> check if this class is really an Edge
            if (Edge.class.isAssignableFrom(inferenceEdgeClass)) {
                //get the InferenceEdge class as Edge class
                Class<? extends Edge> edgeClass = inferenceEdgeClass.asSubclass(Edge.class);
                //get the condition metapath of this InferenceEdge
                AbstractMetaPath conditionMetaPath = metaModel.getInferenceEdgeConditionMetaPath(inferenceEdgeClass);
                //get all startClasses of the condition metapath
                Set<Class<? extends ModelElement>> startClasses = conditionMetaPath.getStartClasses();
                //get all model elements of this startclasses
                List<ModelElement> pathStartElements = getModelItems(this, startClasses);
                //for every of this model elements
                for (ModelElement pathStartElement : pathStartElements) {
                    //get all elements which are conected over the path with the pathStartElement
                    Collection<ModelElement> pathEndElements = PathFunctions.getConnectedElements(pathStartElement, conditionMetaPath);
                    //for every of this connected elements
                    for (ModelElement pathEndElement : pathEndElements) {
                        //if the resulting InferenceEdge dosn't exists -> create the link
                        List<Edge> edgesWith = pathStartElement.getEdgesWith(pathEndElement, edgeClass);
                        if (edgesWith.isEmpty()) {
                            link(edgeClass, pathStartElement, pathEndElement, pid);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param kc
     * @param layerIndex
     */
    public void addNodeToMainDoc(NodeContainer kc, final int layerIndex) {
        NodeContainer nc = null;
        if (kc.getGraphDocument() == mainDoc) {
            nc = kc;
        } else {
            Node node = kc.getNode();
            if (kc instanceof BendpointContainer) {
                nc = new BendpointContainer((Bendpoint) node, mainDoc);
            } else if (metaModel.hasInterLayerStartClass(node)) {
                kc = new InterLayerConnectedNodeContainer(node, mainDoc);
            } else {
                nc = new NodeContainer(node, mainDoc);
            }
        }
        mainDoc.getLayer(layerIndex).add(nc);
    }

    /**
     * @param edgeContainer
     * @param pid
     */
    public void addEdge(final EdgeContainer edgeContainer, final int pid) {
        Edge edge = edgeContainer.getEdge();
        EdgeContainer ec = new EdgeContainer(edgeContainer, mainDoc);
        int layer = edge.layerFor();
        mainDoc.getLayer(layer).add(ec);
        boolean bulkMode = setBulkMode(true);
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
     * Verbindet die beiden Elemente je nach übergebener Richtung vorwärts oder rückwärts. Alle anderen
     * link()-Funktionen ohne Richtung verbinden vorwärts.
     *
     * @param startElement
     * @param endElement
     * @param edgeClass
     * @param direction
     * @param pid
     */
    public void link(final ModelElement startElement, final ModelElement endElement, final Class<? extends Edge> edgeClass, final Direction direction, final int pid) {
        //das neue Element mit dem startElement verknüpfen
        if (direction == Direction.FORWARD) {
            link(edgeClass, startElement, endElement, false, pid);
        } else {
            link(edgeClass, endElement, startElement, false, pid);
        }
    }

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
        ModelElement me1 = mainDoc.findElementCoded(startElementHash);
        ModelElement me2 = mainDoc.findElementCoded(endElementHash);
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
        return link(edgeClass, k1, k2, true, pid);
    }

    /**
     * @param edgeClass
     * @param edgeHash
     * @param k1
     * @param k2
     * @param pid
     * @return
     */
    public Edge link(final Class<? extends Edge> edgeClass, final ModelElement k1, final ModelElement k2, final boolean linkInferenceEdgesDirect, final int pid) {
        return link(edgeClass, INVALID_HASH_STRING, k1, k2, linkInferenceEdgesDirect, pid);
    }

    /**
     * @param edgeClass
     * @param edgeHash
     * @param k1
     * @param k2
     * @param linkInferenceEdgesDirect
     * @param pid
     * @return
     */
    public Edge link(final Class<? extends Edge> edgeClass, final String edgeHash, final ModelElement k1, final ModelElement k2, final boolean linkInferenceEdgesDirect, final int pid) {
        if (edgeClass == null) {
            return link(INVALID_EDGE_CLASS_NAME, edgeHash, k1, k2, INVALID_EDGE_INDEX, INVALID_EDGE_INDEX, true, linkInferenceEdgesDirect, pid);
        }
        String simpleEdgeClassName = edgeClass.getSimpleName();
        return link(simpleEdgeClassName, edgeHash, k1, k2, INVALID_EDGE_INDEX, INVALID_EDGE_INDEX, true, linkInferenceEdgesDirect, pid);
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
    public Edge link(final String edgeClassName, final String edgeHash, final ModelElement startElement, final ModelElement endElement, final int startElementEdgeIndex, final int endElementEdgeIndex, final boolean ensureConsistency, final int pid) {
        return link(edgeClassName, edgeHash, startElement, endElement, startElementEdgeIndex, endElementEdgeIndex, ensureConsistency, true, pid);
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
     * @param linkInferenceEdgesDirect
     *            wenn <code>true</code> werden InferenceEdges durch diese Funktion direkt angelegt. Bei <code>false</code> werden zuerst ihre
     *            Bedingspfade generiert und erst dann (in einem weiteren Durchlauf der Funktion) die InferenceEdge selbst.
     * @param pid
     *            Transaktions-ID mit der die Änderungen am Model durchgeführt werden
     * @return
     *         die neu angelegte Edge zwischen den beiden Elementen oder die Edge, die bereits existierte
     */
    private Edge link(final String edgeClassName, final String edgeHash, ModelElement startElement, ModelElement endElement, final int startElementEdgeIndex, final int endElementEdgeIndex, final boolean ensureConsistency,
            final boolean linkInferenceEdgesDirect, final int pid) {

        //        Sys.err("edgeClassName=" + edgeClassName + " edgeHash=" + edgeHash + " startElement=" + startElement + " endElement=" + endElement + " startElementEdgeIndex=" + startElementEdgeIndex + " endElementEdgeIndex=" + endElementEdgeIndex
        //                + " ensureConsistency=" + ensureConsistency + " linkInferenceEdgesDirect=" + linkInferenceEdgesDirect + " pid=" + pid);

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
        if (edgeClass != null && !MetaModel.isConnecting(edgeClass, startElement.getClass(), endElement.getClass())) {
            return null;
        }

        mainDoc.start_transaction(pid);
        try {
            if (!MultipleEdge.class.isAssignableFrom(edgeClass)) {
                edge = startElement.getEdgeTo(endElement, edgeClass, startElementEdgeIndex);
                if (edge != null) {
                    mainDoc.finish_transaction(pid);
                    return edge;
                }
            }
            //wenn es schon eine Kante in der Gegenrichtung gibt und diese Kante eine Kante mit doppelter Bedeutung ist -> dann Richtung auf DOUBLE setzen
            boolean doubleMeaningEdge = MetaModel.isDoubleMeaningEdge(edgeClass);
            if (doubleMeaningEdge) { //wenn es bei Kanten mit doppelter Bedeutung schon die Gegenrichtung gibt -> setzte auch die Hinrichtung
                edge = startElement.getEdgeFrom(endElement, edgeClass, startElementEdgeIndex);
            }
            if (edge != null) { //die Kante kann hier nur nicht null sein, wenn die obige Bedingung mit der Kante mit doppelter Bedeutung zutraf
                //bei doubleMeaningEdges jetzt auch die Gegenrichtung setzten
                ((DoubleMeaningEdge) edge).setConnectionState(DOUBLE);
                //bei allen anderen Kanten oder wenn es eine Kante mit doppelter Bedeutung war, bei der die Gegenrichtung nohc nicht ex. -> neue Kante anlegen
            } else {
                //wenn eine Ableitungskante angelegt werden soll
                if (InferenceEdge.class.isAssignableFrom(edgeClass) && !linkInferenceEdgesDirect) {
                    //hole die Bedingungspfade
                    OutParamObject<ModelElement> inferenceEdgeConditionMetaPathStartElement = new OutParamObject<>(startElement);
                    OutParamObject<ModelElement> inferenceEdgeConditionMetaPathEndElement = new OutParamObject<>(endElement);
                    AbstractMetaPath inferenceEdgeConditionMetaPath = getInferenceEdgeConditionMetaPathWithCorrectElementOrder(edgeClass, inferenceEdgeConditionMetaPathStartElement, inferenceEdgeConditionMetaPathEndElement);
                    if (!PathFunctions.isConnected(inferenceEdgeConditionMetaPathStartElement.value, inferenceEdgeConditionMetaPathEndElement.value, inferenceEdgeConditionMetaPath)) {
                        if (!inferenceEdgeConditionMetaPath.isCreatable(false)) {
                            mainDoc.finish_transaction(pid);
                            return null;
                        }
                        AbstractPath createdPath = mainDoc.createPath(inferenceEdgeConditionMetaPathStartElement.value, inferenceEdgeConditionMetaPathEndElement.value, inferenceEdgeConditionMetaPath, pid);
                        mainDoc.finish_transaction(pid);
                        if (createdPath != null) {
                            mainDoc.distributeEvent(DATA_CHANGED, pid);
                            edge = startElement.getEdgeTo(endElement, edgeClass); //the edge will be created through DATA_CHANGED (via updateInferenceEdges)
                        }
                        return edge;
                    }
                }
                edge = metaModel.createElement(edgeClass);
                if (edge == null) {
                    mainDoc.finish_transaction(pid);
                    return null;
                }
                if (!Strings.isNullOrEmpty(edgeHash)) {
                    edge.setHashString(edgeHash);
                }
                ConnectionState connectionState = FORWARD; // wird nur für die DoubleMeaningEdges gebraucht
                if (!MetaModel.isConnectingForward(edgeClass, startElement.getClass(), endElement.getClass())) {
                    ModelElement dummy = startElement;
                    startElement = endElement;
                    endElement = dummy;
                    connectionState = BACKWARD;
                }
                if (doubleMeaningEdge) {
                    ((DoubleMeaningEdge) edge).setConnectionState(connectionState);
                }
                edge.setNodesAndInsert(startElement, startElementEdgeIndex, endElement, endElementEdgeIndex);
                if (edge.getStart() != null && edge.getEnd() != null) {
                    kac = new EdgeContainer(edge, mainDoc);
                    String name = getNewEdgeName(edge);
                    edge.setName(name, false);
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
                        deleteElement(edgeList.get(0), mainDoc, pid);
                    }
                    //beim Endelement dasselbe nur in Rückwärtsrichtung
                    Class<? extends ModelElement> edgeStartClass = edge.getStartClass();
                    int maxBackwardCardinality = edge.getMaxBackwardCardinality();
                    edgeList = endElement.getEdgesFrom(edgeStartClass, edgeClass);
                    edgeList.remove(edge);
                    if (edgeList.size() == maxBackwardCardinality) {
                        deleteElement(edgeList.get(0), mainDoc, pid);
                    }
                }
            }
            String startHash = startElement.getHashString();
            String endHash = endElement.getHashString();
            mainDoc.addRedoCommand(MODEL_ACTION_LINK + " " + edgeClassName + " " + edge.getHashString() + " " + startHash + " " + endHash + " " + startElementEdgeIndex + " " + endElementEdgeIndex, pid);
            mainDoc.addUndoCommand(MODEL_ACTION_UNLINK + " " + startHash + " " + endHash + " " + edgeClassName + " " + startElementEdgeIndex, pid);
        } catch (Exception e) {
            Log.show(ERROR, getResString("FehlerAllgemein"), e);
            mainDoc.undo(pid);
            return null;
        }
        //bei jeder Kante eventuell ableitbare InstanciationEdges ergänzen
        updateInstanciationEdgesForAssociationClasses(edge, pid);
        mainDoc.finish_transaction(pid);
        mainDoc.distributeEvent(DATA_CHANGED, pid);
        return edge;
    }

    /**
     * @param potencialInferenceEdgeClass must impelemnt {@link InferenceEdge} interface (hard cast)
     * @param inferenceEdgeConditionMetaPathStartElement original startElement of a potencial inference edge instance
     * @param inferenceEdgeConditionMetaPathEndElement original endElement of a potencial inference edge instance
     * @return triple with the conditionMetaPath of the inference edge as the first triple element and
     *         the given elements in the correct order that the second triple element is the start element
     *         of the condition path and the third triple element is the end element of the condition path
     */
    private AbstractMetaPath getInferenceEdgeConditionMetaPathWithCorrectElementOrder(final Class<? extends Edge> potencialInferenceEdgeClass, final OutParamObject<ModelElement> inferenceEdgeConditionMetaPathStartElement,
            final OutParamObject<ModelElement> inferenceEdgeConditionMetaPathEndElement) {
        Class<? extends InferenceEdge> inferenceEdgeClass = potencialInferenceEdgeClass.asSubclass(InferenceEdge.class);
        AbstractMetaPath inferenceEdgeConditionMetaPath = metaModel.getInferenceEdgeConditionMetaPath(inferenceEdgeClass);
        //es kann sein, dass die Bedingungspfade genau andersrum als die Kante (also mit verdrehtem Start- und Endelement) definiert sind
        ModelElement startElement = inferenceEdgeConditionMetaPathStartElement.value;
        ModelElement endElement = inferenceEdgeConditionMetaPathEndElement.value;
        Class<? extends ModelElement> startClass = startElement.getClass();
        Class<? extends ModelElement> endClass = endElement.getClass();
        if (!inferenceEdgeConditionMetaPath.isStartAndEndClass(startClass, endClass)) {
            inferenceEdgeConditionMetaPathStartElement.value = endElement;
            inferenceEdgeConditionMetaPathEndElement.value = startElement;
        }
        return inferenceEdgeConditionMetaPath;
    }

    /**
     * @param edge
     * @return
     */
    private String getNewEdgeName(final Edge edge) {
        String name = null;
        Class<? extends Edge> edgeClass = edge.getClass();
        AbstractMetaPath initialCreatedNameSourcePath = metaModel.getInitialCreatedNameSourcePath(edgeClass);
        if (initialCreatedNameSourcePath != null) {
            Collection<ModelElement> nameSources = PathFunctions.getConnectedElements(edge, initialCreatedNameSourcePath);
            if (!nameSources.isEmpty()) {
                name = StringUtils.createCollectionString(nameSources, ", ");
            }
        }
        if (name == null) {
            name = mainDoc.getNextNewName(edgeClass);
        }
        return name;
    }

    /**
     * Löst die Verbindung zwischen Start- und Endelement in der angegebenen Richtung. Alle anderen
     * unlink()-Funktionen unlinken vorwärts.
     *
     * @param startElement
     * @param endElement
     * @param edgeClass
     * @param direction
     * @param pid
     */
    public void unlink(final ModelElement startElement, final ModelElement endElement, final Class<? extends Edge> edgeClass, final Direction direction, final int pid) {
        if (direction == Direction.FORWARD) {
            unlink(startElement, endElement, edgeClass, pid);
        } else {
            unlink(endElement, startElement, edgeClass, pid);
        }
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
        ModelElement me1 = mainDoc.findElementCoded(nodehash1);
        ModelElement me2 = mainDoc.findElementCoded(nodehash2);
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

        //        Sys.err("me1=" + me1 + " me2=" + me2 + " egdeClass=" + edgeClass + " endElement=" + me1EdgeIndex + " pid=" + pid);

        Edge edge = null;
        List<Edge> edges = null;

        Class<? extends ModelElement> me1Class = me1.getClass();
        Class<? extends ModelElement> me2Class = me2.getClass();
        boolean isDirectionImportent = MetaModel.isDoubleMeaningEdge(edgeClass) || MetaModel.isConnecting(edgeClass, me1Class, me2Class) && MetaModel.isConnecting(edgeClass, me2Class, me1Class);
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
            //TODO: statt des OptionPanes hier sollten einfach alle Kanten gelöscht werden. Beim Join muss das OptionPane auch raus, da sowas in der Kernklasse hier nichts zu suchen hat!
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, Y_AXIS));
            ButtonGroup buttonGroup = new ButtonGroup();
            for (int i = 0; i < edges.size(); i++) {
                JRadioButton b = new JRadioButton(getElementsNameBuilder().getForwardMetaAssociationName(edges.get(i).getClass()));
                if (i == 0) {
                    b.setSelected(true);
                }
                b.setActionCommand(String.valueOf(i));
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
        mainDoc.start_transaction(pid);
        mainDoc.addRedoCommand(MODEL_ACTION_UNLINK + " " + me1Hash + " " + me2Hash + " " + edgeClassName + " " + me1EdgeIndex, pid);
        //Undo-Kommando wird in deleteElement gesetzt (s. u.)
        //nur bei Kanten mit doppelter bedeutung kann man in bestimmten Richtungen unlinken. Bei allen anderen
        //ist die Richtung egal und das Unlinken ist das Löschen der Edge
        Class<? extends Edge> absoluteEdgeClass = edge.getClass(); // die übergebene Kanten-Klasse kann null gewesen oder eine Oberklasse sein
        //InferenceEdge? -> delete condition paths
        if (InferenceEdge.class.isAssignableFrom(absoluteEdgeClass)) {
            OutParamObject<ModelElement> inferenceEdgeConditionMetaPathStartElement = new OutParamObject<>(me1);
            OutParamObject<ModelElement> inferenceEdgeConditionMetaPathEndElement = new OutParamObject<>(me2);
            AbstractMetaPath inferenceEdgeConditionMetaPath = getInferenceEdgeConditionMetaPathWithCorrectElementOrder(edgeClass, inferenceEdgeConditionMetaPathStartElement, inferenceEdgeConditionMetaPathEndElement);

            //TODO: Remove Inference Egdes implementieren
            ///////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////
            /// ACHTUNG: hier fehlt jetzt das REMOVE von InferenceEdges bzw. deren Bedingungspfaden ///
            ///////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////

        }
        if (MetaModel.isDoubleMeaningEdge(absoluteEdgeClass)) {
            DoubleMeaningEdge doubleMeaningEdge = (DoubleMeaningEdge) edge;
            if (doubleMeaningEdge.getConnectionState() == DOUBLE) {
                if (edge.getStart() == me1) {
                    mainDoc.addUndoCommand(MODEL_ACTION_LINK + " " + absoluteEdgeClass.getName() + " " + edge.getHashString() + " " + me1Hash + " " + me2Hash + " " + me1.getEdgeIndex(edge) + " " + me2.getEdgeIndex(edge), pid);
                    doubleMeaningEdge.setConnectionState(BACKWARD);
                } else {
                    mainDoc.addUndoCommand(MODEL_ACTION_LINK + " " + absoluteEdgeClass.getName() + " " + edge.getHashString() + " " + me2Hash + " " + me1Hash + " " + me2.getEdgeIndex(edge) + " " + me1.getEdgeIndex(edge), pid);
                    doubleMeaningEdge.setConnectionState(FORWARD);
                }
            } else {
                deleteElement(edge, mainDoc, pid);
            }
        } else {
            deleteElement(edge, mainDoc, pid);
        }
        mainDoc.finish_transaction(pid);
        mainDoc.distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * Voraussetzung: Es gibt 2 Knotenklassen A und B die über eine Kantenklasse K verbunden sind. Dasselbe
     * gibt es nochmal, also die Knotenklassen AA und BB mit der Kantenklasse KK dazwischen. Die Knotenklasse
     * A hat eine InstanciationEdge zur Knotenklasse AA, die Knotenklasse B eine InstanciationEdge zur
     * Knotenklasse BB und die Kantenklasse K eine InstanciationEdge zur Kantenklasse KK. A, B und K sind
     * die Master der jeweiligen InstanciationEdge und AA, BB und KK die jeweiligen Instanzen dieser
     * InstanciationEdge, also die aus dem Master jeweils abgeleitete Klasse.<br>
     * <br>
     * Ziel: Erzeuge die InstanciationEdge zw. 2 Kantenelementen, wenn alle anderen Knoten- und Kantenelemente
     * dieses Graph-Ausschnittes bereits vorhanden sind. D.h. es gibt zwei Knoten von der Art A und B die als
     * Master jeweils über eine InstanciationEdge mit Instanz-Elementen der Art AA und BB verbunden sind und
     * sowohl zwischen den Elementen der Art A und B als auch zwischen denen der Art AA und BB die jeweilige
     * Kante besteht.
     *
     * @param edge
     */
    private void updateInstanciationEdgesForAssociationClasses(final Edge edge, final int pid) {
        //Klasse der übergebenen Kante (CommunicationLink_Edge)
        Class<? extends Edge> edgeClass = edge.getClass();
        //Startklasse der übergebenen Kante (InvokingInterface)
        Class<? extends ModelElement> edgeStartClass = Edge.getStartClass(edgeClass);
        //Endklasse der übergebenen Kante (ProvidingInterface)
        Class<? extends ModelElement> edgeEndClass = Edge.getEndClass(edgeClass);

        //für alle InstanciationEdge-Kanten der EdgeClass, bei denen diese EdgeClass die Instanz und nicht der Master ist (IheCommunicationLink_CommunicationLink_Edge)
        for (Class<? extends InstanciationEdge> instanciationEdgeClassToMaster : metaModel.getInstanciationEdgeTypesAsSlave(edgeClass)) {
            //hole den Master dieser InstanciationEdges (IheCommunicationLink_Edge)
            Class<? extends ModelElement> instanciationEdgeMasterClass = InstanciationEdge.getInstanciationMaster(instanciationEdgeClassToMaster);

            //wenn das auch eine Kante ist (was es im Metamodell sinnvollerweise sein sollte, da man eine Kante nur zu einer Kante 'inszanziieren' kann), dann...
            if (Edge.class.isAssignableFrom(instanciationEdgeMasterClass)) {
                //diese auf Kante casten
                Class<? extends Edge> instanciationEdgeMasterEdgeClass = instanciationEdgeMasterClass.asSubclass(Edge.class);
                //Startklasse dieser Kante holen (IheInvokingInterface)
                Class<? extends ModelElement> instanciationEdgeMasterEdgeClassStartClass = Edge.getStartClass(instanciationEdgeMasterEdgeClass);

                //für alle InstanciationEdge-Kanten von dieser Startklasse, bei denen diese Startklasse der Master ist
                for (Class<? extends InstanciationEdge> instanciationEdgeOfMaster1 : metaModel.getInstanciationEdgeTypesAsMaster(instanciationEdgeMasterEdgeClassStartClass)) {
                    //prüfe, ob der Slave bzw. die Instanz dieser InstanciationEdge-Klasse auch Start- oder End der übergebenen Ausgangskantenart ist
                    Class<? extends ModelElement> instanciationInstance1 = InstanciationEdge.getInstanciationInstance(instanciationEdgeOfMaster1);
                    boolean isStartClass1 = instanciationInstance1.isAssignableFrom(edgeStartClass);
                    boolean isEndClass1 = instanciationInstance1.isAssignableFrom(edgeEndClass);

                    //wenn diese Startklasse der Master
                    if (isStartClass1 || isEndClass1) {
                        //Endklasse der Kante holen (IheProvidingInterface)
                        Class<? extends ModelElement> instanciationEdgeMasterEdgeClassEndClass = Edge.getEndClass(instanciationEdgeMasterEdgeClass);

                        //für alle InstanciationEdge-Kanten von dieser Startklasse, bei denen diese Startklasse der Master ist
                        for (Class<? extends InstanciationEdge> instanciationEdgeOfMaster2 : metaModel.getInstanciationEdgeTypesAsMaster(instanciationEdgeMasterEdgeClassEndClass)) {
                            //prüfe, ob der Slave bzw. die Instanz dieser InstanciationEdge-Klasse auch Ende der übergebenen Ausgangskante ist
                            Class<? extends ModelElement> instanciationInstance2 = InstanciationEdge.getInstanciationInstance(instanciationEdgeOfMaster2);

                            boolean isStartClass2 = instanciationInstance2.isAssignableFrom(edgeStartClass);
                            boolean isEndClass2 = instanciationInstance2.isAssignableFrom(edgeEndClass);

                            //Im Metamodell sind beide Elemente der übergebenen Edge-Klasse auch über InstanciatonEdges verbunden
                            //-> nun prüfen, ob diese MetaPfade als Pfade bei der übergebenen Kante auch vorhanden sind
                            if (isStartClass1 && isEndClass2 || isStartClass2 && isEndClass1) {
                                Class<? extends InstanciationEdge> edgeStartInstanciationEdge = isStartClass1 ? instanciationEdgeOfMaster1 : instanciationEdgeOfMaster2;
                                Class<? extends InstanciationEdge> edgeEndInstanciationEdge = isEndClass1 ? instanciationEdgeOfMaster1 : instanciationEdgeOfMaster2;

                                ModelElement edgeStart = edge.getStart();
                                List<ModelElement> edgeStartMasters = edgeStart.getConnectedElements(ModelElement.class, edgeStartInstanciationEdge, InstanciationEdge.INSTANCE_TO_MASTER_DIRECTION);
                                ModelElement edgeEnd = edge.getEnd();
                                List<ModelElement> edgeEndMasters = edgeEnd.getConnectedElements(ModelElement.class, edgeEndInstanciationEdge, InstanciationEdge.INSTANCE_TO_MASTER_DIRECTION);
                                for (ModelElement edgeStartMaster : edgeStartMasters) {
                                    for (ModelElement edgeEndMaster : edgeEndMasters) {
                                        List<Edge> masterEdgesToLink = edgeStartMaster.getEdgesWith(edgeEndMaster, instanciationEdgeMasterEdgeClass);
                                        for (Edge masterEdgeToLink : masterEdgesToLink) {
                                            @SuppressWarnings("unused")
                                            Edge link = link(instanciationEdgeClassToMaster, edge, masterEdgeToLink, pid);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
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
     * @param removeElementHashString
     * @param remainElementHashString
     * @param source
     * @param pid
     * @return the joined element (this is the element with remainElementHashString) or <code>null</code> if nothing was joined
     */
    public ModelElement join(final String removeElementHashString, final String remainElementHashString, final GraphDocument source, final int pid) {
        Collection<String> elementHashes2ExcludeFromJoin = new ArrayList<>();
        elementHashes2ExcludeFromJoin.add(removeElementHashString);
        elementHashes2ExcludeFromJoin.add(remainElementHashString);
        return joinRecursive(removeElementHashString, remainElementHashString, source, elementHashes2ExcludeFromJoin, pid);
    }

    /**
     * @param removeElementHashString
     * @param remainElementHashString
     * @param source
     * @param elementHashes2ExcludeFromJoin
     * @param pid
     * @return the joined element (this is the element with remainElementHashString) or <code>null</code> if nothing was joined
     */
    private ModelElement joinRecursive(final String removeElementHashString, final String remainElementHashString, final GraphDocument source, final Collection<String> elementHashes2ExcludeFromJoin, final int pid) {
        ModelElement removeElement = mainDoc.findElementCoded(removeElementHashString);
        ModelElement remainElement = mainDoc.findElementCoded(remainElementHashString);
        if (removeElement == null || remainElement == null || removeElement == remainElement) {
            return null;
        }
        //prüfen, ob es sich um Node gleichen Typs handelt (nur diese können vereint werden)
        if (!(removeElement instanceof Node && remainElement instanceof Node)) {
            if (!automatic_mode) {
                JOptionPane.showMessageDialog(getMainFrame(), getResString("nur_knoten_sel"), getResString("tool3lgm"), INFORMATION_MESSAGE);
            }
            return null;
        }
        Node removeNode = (Node) removeElement;
        Node remainNode = (Node) remainElement;
        Class<? extends ModelElement> nodeClass = removeNode.getClass();
        if (nodeClass != remainNode.getClass()) {
            if (!automatic_mode) {
                JOptionPane.showMessageDialog(null, getResString("nur_gleiche_sel"), getResString("tool3lgm"), INFORMATION_MESSAGE);
            }
            return null;
        }
        //Beginne umhängen der Kanten
        mainDoc.start_transaction(pid);
        for (Szenario s : szenarios) {
            s.start_transaction(pid, false);
        }
        //Namen und Beschreibung des zu löschenden Node an den verbleibenden anhängen
        //und ExtIDs und benutzerdef. Eigenschaftsfelder zusammenführen
        remainNode.join(removeNode, false);
        //knoten2.createNameWithSzens(doc);

        for (Class<? extends Edge> edgeClass : metaModel.getSubordinatedJoinbleTypes(remainNode.getClass())) {
            List<ModelElement> removeSubordinatedJoinables = removeNode.getConnectedElements(edgeClass);
            List<ModelElement> remainSubordinatedJoinables = remainNode.getConnectedElements(edgeClass);
            if (removeSubordinatedJoinables.size() == 1 && remainSubordinatedJoinables.size() == 1) {
                ModelElement removeSub = removeSubordinatedJoinables.get(0);
                ModelElement remainSub = remainSubordinatedJoinables.get(0);
                String removeSubHash = removeSub.getHashString();
                if (elementHashes2ExcludeFromJoin.contains(removeSubHash)) {
                    continue;
                }
                String remainSubHash = remainSub.getHashString();
                if (elementHashes2ExcludeFromJoin.contains(remainSubHash)) {
                    continue;
                }
                elementHashes2ExcludeFromJoin.add(removeSubHash);
                elementHashes2ExcludeFromJoin.add(remainSubHash);
                joinRecursive(removeSubHash, remainSubHash, source, elementHashes2ExcludeFromJoin, pid);
            }
        }
        //Das hier ist Hardcore, weil hier das IterableObject zurück auf List gecastet wird-> eigentlich müsste sich Edge selbst irgenwie darum kümmern!
        List<Edge> deleteNodeEdges = (List<Edge>) removeNode.getEdges();//ArrayList der Kanten des zu löschendn Knotens
        List<Edge> remainNodeEdges = (List<Edge>) remainNode.getEdges();//ArrayList der Kanten des verbleibenden Knotens
        //für jede Edge vom zu löschenden Node
        while (deleteNodeEdges.size() > 0) {
            Edge edge = deleteNodeEdges.get(0);
            ModelElement startElement = edge.getStart(); //Startknoten der zu übernehmenden Edge merken
            ModelElement endElement = edge.getEnd(); //Endknoten -"-
            //zu löschenden Node durch den verbleibenden ersetzen
            if (startElement == removeNode) {
                startElement = remainNode;
                endElement = edge.getEnd();
            } else {
                startElement = edge.getStart();
                endElement = remainNode;
            }
            boolean deleteEdge = false;
            if (startElement == endElement) {
                deleteEdge = true;
            } else {
                //abfangen, ob im verbleibenden Node an gleicher Stelle schon eine Edge vorkommt, Edge testKante = new Edge(startKnoten, endKnoten, false);
                Class<? extends Edge> edgeClass = edge.getClass();
                Edge testEdge = metaModel.createElement(edgeClass);
                if (testEdge == null) {
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
                deleteElement(edge, mainDoc, pid);
                //              doc.removeEdge(kante, pid);//Edge einfach komplett löschen
            } else { //Edge muss umgehängt werden
                removeNode.removeEdge(edge); //im zu löschenden Node die Edge entfernen
                edge.setNodes(startElement, endElement);//die Edge wirklich an knoten2 binden
            }
        }
        for (Szenario szen : szenarios) {
            NodeContainer removeContainer = (NodeContainer) removeNode.getContainer(szen);
            NodeContainer remainContainer = (NodeContainer) remainNode.getContainer(szen);
            // jetzt umhängen aller Container von knoten1 auf knoten2 in allen Teilmodellen
            if (remainContainer == null && removeContainer != null) {
                //              szen.removeKnotContainer((NodeContainer) knoten1.getContainer(szen), pid);
                ElementContainer orgRemoveContainer = removeNode.getContainer(szen);
                removeContainerFromSubmodel(orgRemoveContainer, pid);
                removeContainer.setElement(remainNode);
                int layer = remainNode.layerFor();
                LayerContainer lc = szen.getLayer(layer);
                lc.add(removeContainer);
            }
            NodeContainer nc = null;
            if (remainContainer != null) {
                nc = remainContainer;
            } else if (removeContainer != null) {
                nc = removeContainer;
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
        deleteElement(removeNode, mainDoc, pid);
        //      doc.removeNode((NodeContainer)knoten1.getContainer(doc), pid); //alle Kanten umgehängt -> wegfallenden Node komplett löschen
        for (Szenario szen : szenarios) {
            szen.finish_transaction(pid, false);
        }
        //Der TransaktionQueue wird einfach gelöscht. Das muss unbedingt mal geändert werden -> also alles richtig UNDO-/REDO-mässig
        tman.clearTransactionQueue();
        mainDoc.finish_transaction(pid);
        distribute(DATA_CHANGED, null, source, pid);
        return remainNode;
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
        if (mainDoc.getHashString().equals(szenHash)) {
            return mainDoc;
        }
        return getSzenarioCoded(szenHash);
    }

    /**
     * @param szenHash
     * @return
     */
    public Szenario getSzenarioCoded(final String szenHash) {
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
        //Sys.err("changed: " + changed + " -> " + c);
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
     * @param bulk_mode
     * @return the previous bulk mode
     */
    public boolean setBulkMode(final boolean bulk_mode) {
        //Sys.err("bulk_mode: " + this.bulk_mode + " -> " + bulk_mode);
        //das erste Setzten des bulk_mode auf false beendet die Initialisierung
        if (!initialized && !bulk_mode) {
            initialized = true;
        }
        boolean oldMode = this.bulk_mode;
        this.bulk_mode = bulk_mode;
        if (oldMode && !bulk_mode) {
            distributeChangeEvents();
        }
        return oldMode;
    }

    /**
     * @return
     */
    public boolean isBulkMode() {
        return bulk_mode;
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
    private void collectChangeEvent(final LGMChangeType changeType, final ElementContainer last_elem, final GraphDocument source, final int pid) {
        LGMChangeEvent changeEvent = new LGMChangeEvent(changeType, last_elem, source, pid);
        changeEvents.remove(changeEvent);
        changeEvents.add(changeEvent);
    }

    /**
     * Delivers all change events from the list {@link GDCollection#changeEvents}
     * and after this clears the list.
     */
    private void distributeChangeEvents() {
        //Sys.err(changeEvents.size());
        for (LGMChangeEvent changeEvent : changeEvents) {
            distribute(changeEvent.changeType, changeEvent.last_elem, changeEvent.source, changeEvent.pid);
        }
        changeEvents.clear();
    }

    /**
     * Distributes the given event to all listeners. If the bulk_mode is active
     * the events are not distributed to the listeners directly. All different
     * events are collected and fired in the order they occured as last if the
     * bulk_mode will be inactivated.
     *
     * @param changeType
     * @param last_elem
     * @param source
     * @param pid
     */
    public final void distribute(final LGMChangeType changeType, final ElementContainer last_elem, final GraphDocument source, final int pid) {
        if (isBulkMode()) {
            collectChangeEvent(changeType, last_elem, source, pid);
            return;
        }
        Integer pidInteger = pid;
        Integer transStackInteger = getTransStackTable().get(pidInteger);
        if (transStackInteger == null) {
            transStackInteger = 0;
        }
        boolean deliverStatic = true;
        if (transStackInteger <= 1) {
            LGMChangeListener.distributeEvent(changeType, allListener, source, last_elem, deliverStatic);
            deliverStatic = false;
        }
        if (transStackInteger == 0) {
            LGMChangeListener.distributeEvent(changeType, closedListener, source, last_elem, deliverStatic);
        }
        //TODO: das hier ist hässlich und sollte anders laufen. die SimpleRedundancyAnalysis sollte wahrscheinlich selbst Listener sein und das hier allein erledigen
        if (changeType == DATA_CHANGED) {
            mainDoc.updateSimpleRedundancyAnalysis();
            for (Szenario szen : szenarios) {
                szen.updateSimpleRedundancyAnalysis();
            }
            bulk_mode = true;
            updateInferenceEdges(pid);
            bulk_mode = false;
        }
        if (changeType != LGMChangeType.SELECTED_SZENARIO_CHANGED) {
            setChanged(true);
        }
    }

    /**
     * @return Hauptdokument der Collection
     */
    public LGMGraphDocument getMainDoc() {
        return mainDoc;
    }

    /**
     * @return
     */
    public GDCollectionIconTable getIconTable() {
        return iconTable;
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
        ElementContainer mainEc = me.getContainer(mainDoc);
        mainDoc.selectedContainer.add(mainEc);
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
        mainDoc.deselectInElementSelectionContext(ec.getElement());
        for (Szenario szen : szenarios) {
            szen.deselectInElementSelectionContext(ec.getElement());
        }
        return true;
    }

    /**
     * @param ec
     */
    public void deselectAll() {
        mainDoc.clearSelection();
        for (Szenario szen : szenarios) {
            szen.clearSelection();
        }
    }

    /**
     * Selektiert in allen Teilmodellen alle einmaligen Elemente
     */
    public void selectAllUniques() {
        for (Class<? extends ModelElement> elemClass : metaModel.uniqueNodes) {
            for (ElementContainer ec : mainDoc.getElementContainers(elemClass)) {
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
            removeClosedTransactionsListener(userFieldDefinitions);
            userFieldDefinitions = newDef;
            addClosedTransactionsListener(userFieldDefinitions);
            //die eigenen UserFields mit den neuen ersetzen
            replaceUserFields(newDef);
            //hier müssen bei allen UserfieldTargets alle Userfields ausgetauscht werden, die sie über ihre UserField2Value-Maps referenzieren
            for (ModelElement me : getModelItems(mainDoc, ModelElement.class, true, false, false)) {
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
                    for (ModelElement me : mainDoc.getModelItems(elemClass, true)) {
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
