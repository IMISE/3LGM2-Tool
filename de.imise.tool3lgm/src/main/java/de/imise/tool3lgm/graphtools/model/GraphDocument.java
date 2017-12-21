package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LAYER_COUNT;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.NO_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Composition.getMaxMasterToSlaveCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isConnecting;
import static de.imise.tool3lgm.graphtools.model.GDCollectionChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GDCollectionChangeType.ELEMENT_GRAPHICS_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GDCollectionChangeType.ELEMENT_NAME_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GDCollectionChangeType.GROUP_ORDER_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GDCollectionChangeType.SELECTION_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GDCollectionChangeType.USER_FIELD_VALUE_CHANGED;
import static de.imise.tool3lgm.graphtools.model.GDCommands.COORDINATE_KNOT;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.LayoutEditor;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.tools.EasyDialogAccess;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Composition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.metamodel.elements.LayerKnoten;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.IsPartOfEdge;
import de.imise.tool3lgm.graphtools.undoredo.InTransactionListener;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.undoredo.TransactionStackTable;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.WeightReplacer;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.ElementsLayoutDefinition;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.gui.InternalGraphFrame;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.Alphabetical;
import de.imise.util.collections.CollectionUtils;

public abstract class GraphDocument extends ElementSelectionContext implements SwingConstants {

    /** Zeichen, das in Kommandos zusammengehörigen Text umschließt, damit er als zusammengehörig erkannt werden kann */
    public static final char GDCOMMAND_TEXT_SURROUNDER = '\'';

    /**
     * COMMENTME
     */
    protected LayerContainer[] layer;

    /**
     * COMMENTME
     */
    private final List<GraphDocumentListener> listener;

    /**
     * COMMENTME
     */
    private final List<InTransactionListener> inlistener;

    /**
     * COMMENTME
     */
    private final List<ElementContainer> analysisResult;

    /**
     * COMMENTME
     */
    private ElementDialogPanel lastActivePanel = null;

    /**
     * COMMENTME
     */
    protected ElementsLayoutDefinition mapping;

    /**
     * COMMENTME
     */
    protected String description = "";

    /**
     * COMMENTME
     */
    private String title = "";

    /**
     * COMMENTME
     */
    protected String hashString = "";

    /**
     * COMMENTME
     */
    private boolean verify_mode = false;

    /**
     * COMMENTME
     */
    public final static int INITIAL_PAGE_HEIGHT = 768;

    /**
     * COMMENTME
     */
    public final static int INITIAL_PAGE_WIDTH = 1024;

    /**
     * COMMENTME
     */
    private int next_x_pos = 0;

    /**
     * COMMENTME
     */
    private int next_y_pos = 0;

    /**
     * COMMENTME
     */
    private int page_height = INITIAL_PAGE_HEIGHT;

    /**
     * COMMENTME
     */
    private int page_width = INITIAL_PAGE_WIDTH;

    /**
     * Faktor um den das Seitenverhältnis dieses GraphDocumentes von der urspünglichen Größe abweicht
     */
    private double pageSizeFactor = 1.0;

    /**
     * COMMENTME
     */
    protected File process_file;

    /**
     * COMMENTME
     */
    protected GDCollection gdcoll;

    /**
     * COMMENTME
     */
    protected NodeContainer lastCreated = null;

    /**
     * COMMENTME
     */
    protected InternalGraphFrame frame = null;

    /**
     * @param _gdcoll
     */
    public GraphDocument(final GDCollection _gdcoll) {
        if (_gdcoll == null) {
            gdcoll = new GDCollection();
        } else {
            gdcoll = _gdcoll;
        }
        hashString = "DOC" + "_" + new Date().getTime();

        analysisResult = new ArrayList<>();
        listener = new ArrayList<>();
        inlistener = new ArrayList<>();
        mapping = new ElementsLayoutDefinition(true);

        layer = new LayerContainer[LAYER_COUNT];
        for (int c = 0; c < layer.length; c++) {
            layer[c] = new LayerContainer(new LayerKnoten(c), this, c);
            layer[c].setColor(Color.white);
        }
        setPageSizeFactor(getPageSizeFactor());
    }

    // Verwaltung globaler Modelldaten --- Anfang ---

    /**
     * Liefert den Zoom-Faktor
     *
     * @return
     *         <code>double</code>-Wert des Zoomfaktors
     */
    public double getPageSizeFactor() {
        return pageSizeFactor;
    }

    /**
     * Setzt den pageSizeFactor auf den übergebenen Wert ohne es UndoRedo-mäßig zu loggen.
     *
     * @param newPageSizeFactor
     */
    public void setPageSizeFactor(final double newPageSizeFactor) {
        setPageSizeFactor(newPageSizeFactor, newPageSizeFactor, false, TransactionManager.STANDARD_PID);
    }

    /**
     * @param pageSizeFactor
     * @param logUndoRedo
     * @param pid
     */
    public void setPageSizeFactor(final double oldPageSizeFactor, final double newPageSizeFactor, final boolean logUndoRedo, final int pid) {
        page_height = new Double(INITIAL_PAGE_HEIGHT * newPageSizeFactor).intValue();
        page_width = new Double(INITIAL_PAGE_WIDTH * newPageSizeFactor).intValue();
        //wenn der Wert sich geändert hat, dann die UNDO/REDO-Kommandos hinzufügen
        if (logUndoRedo) {
            start_transaction(pid);
            addUndoCommandIfNotExist(GDCommands.CHANGE_LAYER_SIZE_FACTOR + " " + hashString, oldPageSizeFactor, pid);
            addRedoCommandOrReplace(GDCommands.CHANGE_LAYER_SIZE_FACTOR + " " + hashString, newPageSizeFactor, pid);
            finish_transaction(pid);
        }
        pageSizeFactor = newPageSizeFactor;
        if (frame != null) {
            frame.layoutChanged(this);
        }
    }

    /**
     * @param _hashString
     */
    public void setHashString(final String _hashString) {
        hashString = _hashString;
    }

    /**
     * @return
     */
    public final String getHashString() {
        return hashString;
    }

    /////////////////////
    // Start Undo/Redo //
    /////////////////////

    /**
     * Zähler für die Transaction-IDs
     */
    private static int transactionId = 1000;

    /**
     * Liefert eine eindeutige Nummer, die als Transaktionsnummer genutzt werden kann.<br>
     * Bei jedem Aufruf wird die Nummer einfach um 1 erhöht.
     *
     * @return
     *         eindeutige Nummer
     */
    public static int createTransactionId() {
        return ++transactionId;
    }

    /**
     * Wiederholt die zuletzt zurückgenommene Transaktion
     */
    public final void redo() {
        redo(TransactionManager.UNSPECIFIC_PID);
    }

    /**
     * Wiederholt das Transaktionskommandp mit der angegebenen ID
     *
     * @param pid
     */
    public final void redo(final int pid) {
        undoRedo(pid, false);
    }

    /**
     * Macht die letzte Transaktion rückgängig
     */
    public final void undo() {
        undo(TransactionManager.UNSPECIFIC_PID);
    }

    /**
     * Macht die Transaktion mit der angegebenen ID rückgängig
     *
     * @param pid
     */
    public final void undo(final int pid) {
        undoRedo(pid, true);
    }

    private void undoRedo(final int pid, final boolean undo) {
        TransactionStackTable transStackTable = gdcoll.getTransStackTable();
        transStackTable.increase(pid);
        //wenn Teilelemente mit verschoben wurden, so wurde dieses Verschieben auch mit geloggt
        //-> beim Rückgängigmachen der Verschiebungen dürfen die Unterelemente nicht durch das
        //Zürücksetzen der Größe und Position der Oberelemente mit verschoben werden, sondern nur,
        //wenn sie beim ursprünglichen Kommando mitverschoben wurden, was geloogt wurde
        boolean isMoveSubElements = UserProperties.set(BooleanProperty.OPTION_GRAPH_MOVE_SUBELEMENTS, false);
        TransactionManager tman = getCollection().getTman();
        if (undo) {
            tman.undo(pid);
        } else {
            tman.redo(pid);
        }
        UserProperties.set(BooleanProperty.OPTION_GRAPH_MOVE_SUBELEMENTS, isMoveSubElements);
        transStackTable.decrease(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param command
     * @param pid
     */
    public void addUndoCommand(final String command, final int pid) {
        if (!gdcoll.isBulkMode()) {
            getCollection().getTman().addUndoCommand(command, pid);
        }
    }

    /**
     * @param command
     * @param pid
     */
    public void addRedoCommand(final String command, final int pid) {
        if (!gdcoll.isBulkMode()) {
            getCollection().getTman().addRedoCommand(command, pid);
        }
    }

    /**
     * Wenn bereits bei der selben Transaktion ein Redo-Kommando gespeichert ist, das den
     * gleichen Prefix besitzt, dann wird das vorhandene Kommando durch das übergebene ersetzt.
     *
     * @param commandPrefix
     * @param commandArguments
     * @param pid
     */
    private void addRedoCommandOrReplace(final String commandPrefix, final Object commandArguments, final int pid) {
        if (!gdcoll.isBulkMode()) {
            getCollection().getTman().addOrReplaceRedoCommand(commandPrefix, commandArguments.toString(), pid);
        }
    }

    /**
     * Das übergebene Undo-Kommanmdo wird nur geloggt, wenn nicht bereits ein Undo-Komamndo
     * mit dem gleichen Prefix in derselben Tansaktion geloggt wurde. Hiermit kann man das
     * Logging von Zwischenschritten unterbinden (z.B. beim Draggen von Elementen in der Grafik)
     *
     * @param commandPrefix
     * @param commandArguments
     * @param pid
     */
    private void addUndoCommandIfNotExist(final String commandPrefix, final Object commandArguments, final int pid) {
        if (!gdcoll.isBulkMode()) {
            getCollection().getTman().addUndoCommandIfNotExist(commandPrefix, commandArguments.toString(), pid);
        }
    }

    ////////////////////
    // Ende Undo/Redo //
    ////////////////////

    /**
     * @return String description
     */
    public String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    /**
     * @param String description
     */
    public void setDescription(final String string) {
        if (string == null) {
            return;
        }
        if (description == null) {
            description = string;
        } else if (!string.equals(description)) {
            description = string;
            gdcoll.setChanged(true);
        }
    }

    /**
     * @param mc
     * @return
     */
    private boolean isMyElement(final ElementContainer mc) {
        return isMyElement(mc.getElement());
    }

    /**
     * @param me
     * @return
     */
    public boolean isMyElement(final ModelElement me) {
        return me.getContainer(this) != null;
    }

    /**
     * @return
     */
    public NodeContainer getLastCreated() {
        return lastCreated;
    }

    /**
     * @param command
     * @param ucommand
     * @param pid
     * @param log
     */
    private final void exec(final String command, final String ucommand, final int pid, final boolean log) {
        if (isVerificationMode()) {
            System.out.println("Kommando: " + command + "\n" + ucommand + "\n\n");
        }

        if (command == null) {
        } else if (command.equals(GDCommands.PRINT_QUEUE.toString())) {
            getCollection().getTman().printQueue(10);
        } else if (command.startsWith(GDCommands.UNDO.toString())) {
            undo();
        } else if (command.startsWith(GDCommands.REDO.toString())) {
            redo();
        } else if (command.startsWith(GDCommands.COMMAND_LINE.toString())) {
            String answer = (String) JOptionPane.showInputDialog(null, "Befehlseingabe", "Tool3lgm", JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (answer != null && !answer.equals("") && !answer.equals("COMMAND_LINE")) {
                exec(answer, "", pid, log);
            }
        } else if (command.equals(GDCommands.CHECK_CONSISTENCY.toString())) {
            //            GraphDocument doc = gdcoll.getMainGraphDocument();
            //            List<ModelElement> elements = doc.getModelItems(Aufgabe.class, false);
            //            for (ModelElement me : elements) {
            //                if (me.toString().equals("Dokumentation des Informationssystems")) {
            //                    System.err.println(me.getParentConnectedContainer(AufOrgKombination.class, doc));
            //                }
            //            }

            //	Testausgabe aller Elemente im Modell (kann für Prüfzwecke wieder aktiviert werden
            //			for (Class<? extends ModelElement> meClass : ModelConstants.ALL_NODES){
            //				GraphDocument doc = gdcoll.getGraphDocument();
            //				ArrayList<ModelElement> al = doc.getModelItems(meClass, false);
            //				if (al.size()==0)
            //					continue;
            //				System.err.println(al.size() + "\t" + getResString(meClass.getSimpleName()+"_p"));
            //			}
            //			System.err.println("#############################################\n");

            //			for (Error err : new ConsistencyChecker(gdcoll).getInconsistencies())
            //				System.err.println(err.getMessage());

        } else {
            if (log) {
                addRedoCommand(command, pid);
                addUndoCommand(ucommand, pid);
            }
            exec_command(command, pid);
        }
    }

    /**
     * Führt das angegebene
     *
     * @param command
     * @param pid
     */
    public final void exec(final String command, final int pid) {
        exec(command, "", pid, false);
    }

    /**
     * Methode zur Steuerung des Löschens von Elementen (nur aus Teilmodell,oder aus allen Modellen).<br>
     * Gegebenfalls werden Dialoge angezeigt, die den Nutzer beim Löschen unterstützen.
     *
     * @param argv
     * @param pid
     */
    private void remove(final String[] argv, final int pid) {

        // Textfeld und Knickpunkt löschen
        if (isSelectedOnlySubmodelElements()) {
            dispatch_command(GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL, argv, pid);
        }
        // Elemente sind nicht aus einem Teilmodell sondern nur aus dem Gesamtmodell löschbar, wenn
        // - aktuelles Modell = Hauptdokument
        // - alle selektierten Elemente sind unique (= ohne grafische Repräsentation sind sie immer in allen Teilmodellen)
        // - das Element ist ein untergeordnetes Element, aber sein übergeordnetes ist auch in dem Teilmodell
        else if (this == gdcoll.getMainGraphDocument() || isSelectedOnlyUnique() || isSelectedOnlySlaveRealNodes()) {
            if ((Boolean) ActionLibrary.OptionsActions.Gerneral.SHOW_REMOVE_WARNING.getValue(Action.SELECTED_KEY)) {
                JCheckBox cb = new JCheckBox(getResString("dont_ask_again"));
                cb.setSelected(false);
                Object[] cont = new Object[] {
                        getResString("remove_element_warning"),
                        cb
                };
                int value = JOptionPane.showConfirmDialog(Static.getMainFrame(), cont, getResString("attention"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    dispatch_command(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL, argv, pid);
                }
                ActionLibrary.OptionsActions.Gerneral.SHOW_REMOVE_WARNING.putValue(Action.SELECTED_KEY, !cb.isSelected());
            } else {
                dispatch_command(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL, argv, pid);
            }
        }
        // Auswahl in einem Teilmodell
        else {
            Object[] buttons = new Object[] {
                    getResString("submodel"),
                    getResString("whole_model"),
                    getResString("cancel")
            };
            int value = JOptionPane.showOptionDialog(Static.getMainFrame(), getResString("loeschfrage"), getResString("tool3lgm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[2]);
            if (value == JOptionPane.YES_OPTION) {
                dispatch_command(GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL, argv, pid);
            } else if (value == JOptionPane.NO_OPTION) {
                dispatch_command(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL, argv, pid);
            }
        }
    }

    /**
     * @param command
     * @param argv
     * @param pid
     */
    protected void dispatch_command(final GDCommands command, final String[] argv, final int pid) {

        //		System.err.println(command + " " + Arrays.asList(argv));
        int argc = argv.length;
        switch (command) {

        case MODEL_ACTION_DELETE:
            remove(argv, pid);
            break;

        case SET_VISIBLE:
            //argv[0] = visible = "true" oder "false"
            //argv[1] = szenHash (optional)
            //argv[2..n] = elementHashes (optional)
            setVisible(argv, pid);
            break;

        case MODEL_ACTION_CREATE_NODE:
            String classname = argv[0];
            String name = GDCommands.INVALID_NAME;
            String description = GDCommands.INVALID_DESCRIPTION;
            String hashcode = GDCommands.INVALID_HASH_STRING;
            try {
                name = argv[1];
                description = argv[2];
                hashcode = argv[3];
            } catch (Exception e) {
                //Die Argumente 1-3 sind optional; deshalb keine Fehlermeldung, wenn das Parsen fehlschlägt
            }
            createKnotenWithContainer(ModelConstants.getClassForName(classname), name, description, hashcode, pid);
            break;

        case MODEL_ACTION_DELETE_FROM_MODEL:
            switch (argc) {
            case 0:
                gdcoll.deleteElements(getSelectedElements(), this, pid);
                break;
            default:
                gdcoll.deleteElements(argv, this, pid);
                break;
            }
            break;

        case LINK:
            switch (argc) {
            case 0:
                System.err.println("GraphDocument.dispatchCommand() -> LINK mit 0 Argumenten aufgerufen (veraltet)");
                //linkSelected(pid);
                break;
            case 2:
                linkSelected(ModelConstants.getClassForName(argv[0]).asSubclass(Edge.class), Integer.parseInt(argv[1]), pid);
                break;
            case 6:
                //Parameter: link(String edgeClassName, String edgeHash, ModelElement k1, ModelElement k2, int edgeIndex, int pid) {
                /* Edge edge = */gdcoll.link(argv[0], argv[1], argv[2], argv[3], Integer.parseInt(argv[4]), Integer.parseInt(argv[5]), pid);
                //						System.err.println("<Etxrabllatt>");
                //						System.err.println(edge.getStart() + " (" + edge.getStart().getHashString() + ") " + edge.getEnd() + " (" + edge.getEnd().getHashString() + ")");
                //						System.err.println("</Etxrabllatt>");
                break;
            default:
                break;
            }
            break;

        //			case LINK_REVERSE :
        //				linkSelectedReverse(pid);
        //				break;

        case UNLINK:
            int position = -1;
            Class<? extends Edge> edgeClass = null;
            switch (argc) {
            case 0:
                System.err.println("GraphDocument.dispatchCommand() -> LINK mit 0 Argumenten aufgerufen (veraltet)");
                //						unlinkSelected(pid);
                break;
            case 2:
                unlinkSelected(ModelConstants.getClassForName(argv[0]).asSubclass(Edge.class), Integer.parseInt(argv[1]), pid);
                break;
            default:
                try {
                    position = Integer.parseInt(argv[2]);
                    edgeClass = ModelConstants.getClassForName(argv[3]).asSubclass(Edge.class);
                } catch (Exception e) {
                    //							Log.log(Log.ERROR, Tool3lgmConstants.getErrorString("FehlerAllgemein"), e);
                }
                gdcoll.unlink(argv[0], argv[1], edgeClass, position, pid);
                break;
            }
            break;

        //			case UNLINK_REVERSE :
        //				unlinkSelectedReverse(pid);
        //				break;

        case SWAP_EDGE_POSITIONS:
            switch (argc) {
            case 3:
                swapEdgePositions(argv[0], argv[1], argv[2], pid);
                break;
            }
            break;

        case ADDICT:
            position = -1;
            try {
                position = Integer.parseInt(argv[5]);
            } catch (Exception e) {
                Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
            }
            addict(argv[0], argv[1], argv[2], argv[3], argv[4], position, pid);
            break;

        case CREATE_ADDICTED:
            GraphDocument doc = getCollection().getGraphDocumentCoded(argv[0]);
            ModelElement master = doc.findElementCoded(argv[1]);
            edgeClass = ModelConstants.getClassForName(argv[2]).asSubclass(Edge.class);
            Class<? extends ModelElement> slaveClass = ModelConstants.getClassForName(argv[3]);
            createAddicted(doc, master, edgeClass.asSubclass(Composition.class), slaveClass, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_COLOR:
            switch (argc) {
            case 0:
                changeColor(pid);
                break;
            case 3:
                Color c = null;
                if (!argv[2].equals("null")) {
                    try {
                        c = new Color(Integer.parseInt(argv[2]));
                    } catch (Exception e) {
                        Log(e);
                    }
                }
                changeColor(argv[0], argv[1], c, pid);
                break;
            default:
                break;
            }
            break;

        case MODEL_ACTION_SET_LAYER_COLOR:
            int layer_idx = -1;
            switch (argc) {
            case 0:
                changeLayerColor(pid);
                break;
            case 3:
                Color c = null;
                try {
                    layer_idx = Integer.parseInt(argv[1]);
                    if (!argv[2].equals("null")) {
                        c = new Color(Integer.parseInt(argv[2]));
                    }
                } catch (Exception e) {
                    Log(e);
                }
                changeLayerColor(argv[0], layer_idx, c, pid);
                break;
            default:
                break;
            }
            break;

        case MODEL_ACTION_SET_ELEMENT_ALPHA:
            switch (argc) {
            case 1:
                try {
                    changeAlpha(Integer.parseInt(argv[0]), pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            case 3:
                try {
                    changeAlpha(argv[0], argv[1], Integer.parseInt(argv[2]), pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            default:
                break;
            }
            break;
        case MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL:
            changeAlpha(GraphElementLayout.TRANSPARENCY_FULL, pid);
            break;
        case MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF:
            changeAlpha(GraphElementLayout.TRANSPARENCY_HALF, pid);
            break;
        case MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE:
            changeAlpha(GraphElementLayout.TRANSPARENCY_NONE, pid);
            break;

        case MODEL_ACTION_SET_LAYER_ALPHA:
            switch (argc) {
            case 1:
                try {
                    changeLayerAlpha(Integer.parseInt(argv[0]), pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            case 3:
                try {
                    changeLayerAlpha(argv[0], Integer.parseInt(argv[1]), Integer.parseInt(argv[2]), pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            }
            break;

        case MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL:
            changeLayerAlpha(GraphElementLayout.TRANSPARENCY_FULL, pid);
            break;
        case MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF:
            changeLayerAlpha(GraphElementLayout.TRANSPARENCY_HALF, pid);
            break;
        case MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE:
            changeLayerAlpha(GraphElementLayout.TRANSPARENCY_NONE, pid);
            break;

        case CHANGE_LAYER_SIZE_FACTOR:
            try {
                GraphDocument szen = gdcoll.getGraphDocumentCoded(argv[0]);
                szen.setPageSizeFactor(Double.parseDouble(argv[1]), Double.parseDouble(argv[1]), true, TransactionManager.STANDARD_PID);
            } catch (Exception e) {
                Log(e);
            }
            break;

        case MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT:
            if (argc == 0) {
                normalizeFontSelected(pid);
            } else if (argc == 2) {
                normalizeFontElement(argv[0], argv[1], pid);
            }
            break;

        case MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR:
            if (argc == 0) {
                normalizeColorSelected(pid);
            } else if (argc == 2) {
                normalizeColorElement(argv[0], argv[1], pid);
            }
            break;

        case MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY:
            if (argc == 0) {
                normalizeTransparencySelected(pid);
            } else if (argc == 2) {
                normalizeTransparencyElement(argv[0], argv[1], pid);
            }
            break;

        case MODEL_ACTION_SET_ELEMENT_DEFAULT_FULL_LAYOUT:
            if (argc == 0) {
                normalizeSelected(pid);
            }
            break;

        case MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY:
            if (argc == 0) {
                normalizeLayer(gdcoll.getSelectedDoc().hashString, gdcoll.getActiveLayer(), pid);
            }
            if (argc == 2) {
                try {
                    normalizeLayer(argv[0], Integer.parseInt(argv[1]), pid);
                } catch (Exception e) {
                    Log(e);
                }
            }
            break;

        case SET_NAME:
            switch (argc) {
            case 2:
                //[0] = elementHash, [1] = newName
                setName(findElementCoded(argv[0]), argv[1], pid);
                break;
            default:
                break;
            }
            break;

        case SET_DESCRIPTION:
            //[1] = ElementHashString, [2] = Beschreibung
            setDescription(findElementCoded(argv[0]), argv[1], pid);
            break;

        case SET_USER_FIELD_VALUE:
            if (argc == 3) {
                setUserFieldValue(argv[0], argv[1], argv[2], pid);
            }
            break;

        case SET_USER_FIELD_WEIGHT_REPLACEMENT:
            if (argc == 3) {
                setUserFieldWeightReplacement(argv[0], argv[1], argv[2], pid);
            }
            break;

        case COORDINATE_KNOT:
            if (argc == 6) {
                try {
                    String szenHash = argv[0];
                    String hashCode = argv[1];
                    int x = Integer.parseInt(argv[2]);
                    int y = Integer.parseInt(argv[3]);
                    int width = Integer.parseInt(argv[4]);
                    int height = Integer.parseInt(argv[5]);
                    coordinateKnot(gdcoll, szenHash, hashCode, x, y, width, height, pid);
                } catch (Exception e) {
                    Log(e);
                    break;
                }
                break;
            }
            break;

        case AUFKLAPPEN:
            if (argc == 2) {
                aufklappen(gdcoll, argv[0], argv[1], pid);
            } else {
                aufklappen(pid);
            }
            break;

        case ZUKLAPPEN:
            if (argc == 2) {
                zuklappen(gdcoll, argv[0], argv[1], true, pid);
            } else {
                zuklappen(pid);
            }
            break;

        case VERIFY_ON:
            setVerificationMode(true);
            break;
        case VERIFY_OFF:
            setVerificationMode(false);
            break;
        case INTERACTIVE_MODE_ON:
            gdcoll.setInteractiveMode(true);
            break;
        case INTERACTIVE_MODE_OFF:
            gdcoll.setInteractiveMode(false);
            break;

        case INSERT_BENDING_POINT:
            //[0] = SzenHash, [1] = HashString der Edge, [2] = HashString des Knickpunktes, [3] = X-Position, [4] = Y-Position, [5] = Index des Knickpuntes auf der Edge,
            gdcoll.insertBendingPoint(argv[0], argv[1], argv[2], Integer.parseInt(argv[3]), Integer.parseInt(argv[4]), Integer.parseInt(argv[5]), pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_CENTER:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_LEFT:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_RIGHT:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_BOTTOM:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_CENTER:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_TOP:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH:
            align(command, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_TOP:
            label_valign(TOP, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_CENTER:
            label_valign(CENTER, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_BOTTOM:
            label_valign(BOTTOM, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_LEFT:
            label_halign(LEFT, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_CENTER:
            label_halign(CENTER, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_RIGHT:
            label_halign(RIGHT, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN:
            if (argc == 1) {
                try {
                    label_valign(Integer.parseInt(argv[0]), pid);
                } catch (Exception e) {
                    Log(e);
                }
            }
            if (argc == 3) {
                try {
                    //[0] = SzenHash, [1] = HashString der Containers, [2] = align mode
                    GraphDocument szen = getCollection().getGraphDocumentCoded(argv[0]);
                    ElementContainer ec = szen.findContainerCoded(argv[1]);
                    int mode = Integer.parseInt(argv[2]);
                    szen.label_valign(mode, ec, pid);
                } catch (Exception e) {
                    Log(e);
                }
            }
            break;

        case MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN:
            if (argc == 1) {
                try {
                    label_halign(Integer.parseInt(argv[0]), pid);
                } catch (Exception e) {
                    Log(e);
                }
            }
            if (argc == 3) {
                try {
                    //[0] = SzenHash, [1] = HashString der Containers, [2] = align mode
                    GraphDocument szen = getCollection().getGraphDocumentCoded(argv[0]);
                    ElementContainer ec = szen.findContainerCoded(argv[1]);
                    int mode = Integer.parseInt(argv[2]);
                    szen.label_halign(mode, ec, pid);
                } catch (Exception e) {
                    Log(e);
                }
            }
            break;

        case MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION:
            if (argc == 0) {
                z_move_up(pid);
            } else {
                z_move_up(argv[0], argv[1], pid);
            }
            break;

        case MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION:
            if (argc == 0) {
                z_move_down(pid);
            } else {
                z_move_down(argv[0], argv[1], pid);
            }
            break;

        case MODEL_ACTION_MOVE_ORDER:
            try {
                z_move(argv[0], argv[1], Integer.parseInt(argv[2]), pid);
            } catch (Exception e) {
                Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
            }
            break;

        case MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP:
            if (argc == 0) {
                z_step_up(pid);
            } else {
                z_step_up(argv[0], argv[1], pid);
            }
            break;

        case MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN:
            if (argc == 0) {
                z_step_down(pid);
            } else {
                z_step_down(argv[0], argv[1], pid);
            }
            break;

        case SET_ICON:
            switch (argc) {
            case 1:
                setIcon(argv[0], pid);
                break;
            case 3:
                setIcon(argv[0], argv[1], argv[2], pid);
                break;
            default:
                break;
            }
            break;

        case MODEL_ACTION_SET_ELEMENT_ICON_NONE:
            switch (argc) {
            case 0:
                unsetIcon(pid);
                break;
            case 2:
                unsetIcon(argv[0], argv[1], pid);
                break;
            default:
                break;
            }
            break;

        case MODEL_ACTION_SET_ELEMENT_FONT:
            name = "";
            switch (argc) {
            case 0:
                changeFont(null, pid);
                break;
            case 2:
                changeFont(argv[0], argv[1], pid);
                break;
            default:
                int size = 0;
                int style = 0;
                try {
                    name = argv[2];
                    size = Integer.parseInt(argv[3]);
                    style = Integer.parseInt(argv[4]);
                } catch (Exception e) {
                    Log(e);
                }
                changeFont(argv[0], argv[1], name, size, style, pid);
            }
            break;

        case CHANGE_LINE_STYLE:
            String style = argv[argc - 1];
            int lineStyle = 0; //= normal
            if (style.equals("normal")) {
                lineStyle = 0;
            } else if (style.equals("dashes")) {
                lineStyle = 1;
            } else {
                try {
                    lineStyle = Integer.parseInt(style);
                } catch (Exception e) {
                    Log(e);
                }
            }
            if (argc == 1) {
                changeLineStyle(lineStyle, pid);
            } else if (argc == 3) {
                changeLineStyle(argv[0], argv[1], lineStyle, pid);
            }
            break;

        case CHANGE_GLOBAL_MAPPING:
            //Der Konstruktoraufruf zeigt den Editor auch gleich an
            new LayoutEditor(new javax.swing.JFrame(), this);
            break;

        case CREATE_SZENARIO:

            //				in Teilmodell übernehmen dann rückgangig dann redo -> Fehler

            Szenario szen = gdcoll.createSzenario(argv[0], false, getDecodedParseSaveString(argv[1]), argv[2], pid);
            if (szen == null) {
                finish_transaction(pid);
                return;
            }
            Static.getTool().createSzenarioFrame(szen);
            //Selection clonen, weil sie sich während der Ausführung ändert!
            addElementsToSzenario(szen.getHashString(), new ArrayList<>(selectedContainer), pid);
            break;

        case REMOVE_SZENARIO:
            gdcoll.deleteSzenario(argv[0], pid);
            break;

        case ADD_ELEMENT_TO_SZENARIO:
            //argv[0] = Quell-GraphDocument-Hash (kann auch das Hauptdokument sein)
            //argv[1] = Ziel-Szenario-Hash
            //argv[2] = Element-Hash
            if (argc == 2) {
                addElementToSzenario(null, argv[0], argv[1], pid);
                //argv[0] = Ziel-Szenario-Hash
                //argv[1] = Element-Hash
            } else if (argc == 3) {
                addElementToSzenario(argv[0], argv[1], argv[2], pid);
            }
            break;

        case ADD_SELECTED_TO_SZENARIO:
            if (argc != 1) {
                return;
            }
            //Selection clonen, weil sie sich während der Ausführung ändert!
            addElementsToSzenario(argv[0], getSelectionInGraphOrder(), pid);
            break;

        case ADD_SELECTED_TO_NEW_SZENARIO:
            //Selection clonen, weil sie sich während der Ausführung ändert!
            addContainerToNewSzenario(getSelectionInGraphOrder(), pid);
            break;

        case ADD_SELECTED_TO_ALL_SZENARIOS:
            //Selection clonen, weil sie sich während der Ausführung ändert!
            addContainerToAllSzenarios(getSelectionInGraphOrder(), pid);
            break;

        case LINK_ELEMENT_TO_SZENARIO:
            if (argc < 2) {
                return;
            }
            linkElementToSzenario(argv[0], argv[1], pid);
            break;

        case SELECT_LINKED_SZENARIO:
            Static.getTool().changeToLinked(this);
            break;

        case LINK_SELECTED_TO_SZENARIO:
            if (argc < 1) {
                return;
            }
            switch (argc) {
            case 1:
                linkElementsToSzenario(argv[0], new ArrayList<>(selectedContainer), pid);
                break;
            default:
                linkElementToSzenario(argv[0], argv[1], pid);
            }
            break;

        case LINK_SELECTED_TO_NEW_SZENARIO:
            linkElementsToNewSzenario(new ArrayList<>(selectedContainer), pid);
            break;

        case MODEL_ACTION_DELETE_FROM_SUBMODEL:
            switch (argc) {
            case 0:
                if (!(this instanceof Szenario)) {
                    break;
                }
                gdcoll.removeContainerFromSubmodel(selectedContainer, pid);
                break;
            case 2:
                GraphDocument szenario = gdcoll.getGraphDocumentCoded(argv[0]);
                if (!(szenario instanceof Szenario)) {
                    return;
                }
                ElementContainer ec = szenario.findContainerCoded(argv[1]);
                if (ec != null && ec instanceof NodeContainer) {
                    gdcoll.removeContainerFromSubmodel(ec, pid);
                }
                //							szen.removeContainer((NodeContainer)ec, pid);
                break;
            default:
                break;
            }
            break;

        case SHOW_SZENARIO:
            Static.getTool().createSzenarioFrame(gdcoll.getSzenario(Integer.parseInt(argv[argc - 1])));
            break;

        case JOIN_SELECTED:
            joinSelected(pid);
            break;

        case MODEL_ACTION_HIDE_ALL_LAYER_CONFIGS:
        case MODEL_ACTION_SHOW_ALL_LAYER_CONFIGS:
            layer[gdcoll.getActiveLayer()].setShowInterLayerConnections(command == GDCommands.MODEL_ACTION_SHOW_ALL_LAYER_CONFIGS);
            distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
            break;
        case MODEL_ACTION_SHOW_ELEMENT_CONFIGS:
        case MODEL_ACTION_HIDE_ELEMENT_CONFIGS:
            for (ElementContainer ec : selectedContainer) {
                if (ModelConstants.isInterLayerStartClass(ec.getElement().getClass())) {
                    ((InterLayerConnectedNodeContainer) ec).setShowInterLayerConnections(command == GDCommands.MODEL_ACTION_SHOW_ELEMENT_CONFIGS);
                }
            }
            distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
            break;

        default:
            break;
        }
    }

    //	/** Puffer für Kommandoargumente. Mehr als 100 Argumente sind absolut unwahrscheinlich * /
    //	private static final ArrayList<String> commandBuffer = new ArrayList<String>();
    //
    //	/**
    //	 * AXS dachte mal, dass die Lange Zeit beim Undo ganz vieler Aktionen vielleicht mit dem aufwendigen Parsen der
    //	 * Undo-Kommandos zusammenhängt. Dabei ist diese Funktion entstanden, die dasselbe macht wie die darunter, aber
    //	 * auf ganz andere Weise.
    //	 *
    //	 * @param line
    //	 * 		Eine gültiges Kommando in <code>line</code> besteht immer aus einem GDCommand an Position 0 und dann duch Leerzeichen
    //	 * 		getrennte Argumente. Die Argumente können auch Leerzeichen enthalten. Solche Argumente sollten durch
    //	 * 		{@link GraphDocument#GDCOMMAND_TEXT_SURROUNDER} umschlossen sein, damit sie als zusammenghörig erkannt werden.
    //	 * @param pid
    //	 * /
    //	private void exec_command(String line, int pid) {
    //		long start = System.currentTimeMillis();
    //		try {
    //			if (isVerificationMode())
    //				System.out.println(line);
    //
    //			StringTokenizer st1 = new StringTokenizer(line);
    //
    //			//als erstes das Kommado ganz vorne parsen
    //			int l = line.length();
    //			String commandString = null;
    //			int i1 = 0;
    //			//Alle Leerzeichen am Anfang überspringen
    //			while (i1 < l && line.charAt(i1) <= 32)
    //				i1++;
    //			//es stand nichts in der line -> raus
    //			if (i1 == l) {
    //				System.err.println("GraphDocument.exec_command(String line, int pid): Empty line argument");
    //				return;
    //			}
    //			int i2 = i1 + 1;
    //			//Bis zum Lineend oder dem nächsten char <= 32 suchen -> zwischen i1 und i2 steht das Kommando
    //			while (i2 < l && line.charAt(i2) > 32)
    //				i2++;
    //			commandString = line.substring(i1, i2);
    //			GDCommands command = GDCommands.valueOf(commandString);
    //			if (command == null) {
    //				System.err.println("GraphDocument.exec_command(String line, int pid): wrong line command: " + line);
    //				return;
    //			}
    //
    //			commandBuffer.clear();
    //			i1 = i2;
    //			while(i1 < l) {
    //				char c = ' ';
    //				//Alle Leerzeichen vor einem Argument überspringen
    //				while (i1 < l && (c = line.charAt(i1)) <= 32)
    //					i1++;
    //				//es stand nichts mehr in der line
    //				if (i1 == l)
    //					break;
    //				//wenn das Argument von Hochkommata eingeschlossen ist
    //				if (c == GDCOMMAND_TEXT_SURROUNDER) {
    //					i1++;
    //					i2 = i1;
    //					//nächstes Hochkomma = Argumentende suchen
    //					while (i2 < l && line.charAt(i2) != GDCOMMAND_TEXT_SURROUNDER)
    //						i2++;
    //					//kein schließendes Hochkomma gefunden -> Fehler
    //					if (i2 == l) {
    //						System.err.println("GraphDocument.exec_command(String line, int pid): wrong line argument: " + line);
    //						return;
    //					}
    //				//keine Hochkommata um das Argument -> LineEnd oder nächstes char <= 32 suchen
    //				} else {
    //					i2 = i1;
    //					//LineEnd oder nächstes Leerzeichen suchen
    //					while (++i2 < l && line.charAt(i2) > 32);
    //				}
    //				commandBuffer.add(line.substring(i1, i2));
    //				i1 = i2 + 1;
    //			}
    //			String argv[] = new String[commandBuffer.size()];
    //			for (int d = 0; d < argv.length; d++)
    //				argv[d] = commandBuffer.get(d);
    //			dispatch_command(command, argv, pid);
    //		} catch (Exception e) {
    //			Log(e);
    //			JOptionPane.showMessageDialog(null, e.getClass().getName() + ": " + e.getMessage(), getResString("tool3lgm"), JOptionPane.INFORMATION_MESSAGE);
    //		}
    //		fullTime1 += System.currentTimeMillis() - start;
    //	}
    //*/

    /**
     *
     */
    private void exec_command(final String line, final int pid) {
        try {
            if (isVerificationMode()) {
                System.out.println(line);
            }

            StreamTokenizer st = new StreamTokenizer(new StringReader(line));
            st.wordChars('\\', '\\');
            st.wordChars('_', '_');
            List<String> tokens = new ArrayList<>();
            try {
                int t = st.nextToken();
                while (t != StreamTokenizer.TT_EOF) {
                    tokens.add(t == StreamTokenizer.TT_NUMBER ? new Integer(new Double(st.nval).intValue()).toString() : st.sval);
                    t = st.nextToken();
                }
            } catch (IOException e) {
                Log(e);
            }

            //je nach globaler Option können die geloggten Kommandos den Namen des GDCommands oder den Index in der Liste aller GDCommands
            //aber wenn die Option auf false steht, dann sind die UNOD-Kommandos als Zahl kodiert, alle anderen sind aber noch lesbar, daher
            //muss man testen, ob sich das Kommando auf int casten lässt.
            GDCommands command = null;
            try {
                //wenn undo und redo mit den Komandoindizes statt den vollständigen Namen geloggt werden
                //versuche den Index zu parsen und das Kommando
                command = GDCommands.values()[new Integer(tokens.get(0)).intValue()];
            } catch (Exception e) {
                //wenn lesbar geloggt werden soll -> einfach den Kommandonamen nehmen
                command = tokens.size() > 0 ? GDCommands.valueOf(tokens.get(0)) : null;
            }

            if (command == null) {
                return;
            }

            int argc = tokens.size() - 1;
            String argv[] = new String[argc];
            for (int d = 0; d < argc; d++) {
                argv[d] = tokens.get(d + 1);
            }
            dispatch_command(command, argv, pid);
        } catch (Exception e) {
            Log(e);
            JOptionPane.showMessageDialog(null, e.getClass().getName() + ": " + e.getMessage(), getResString("tool3lgm"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void Log(final Exception e) {
        Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
    }

    /**
     * @return
     */
    public final int getPageWidth() {
        return page_width;
    }

    /**
     * @return
     */
    public final int getPageHeight() {
        return page_height;
    }

    // Verwaltung globaler Modelldaten --- Ende ---

    // --- Transaktions-Verwaltung --- Anfang ---

    /**
     * @param pid
     */
    public void start_transaction(final int pid) {
        start_transaction(pid, true);
    }

    /**
     * @param pid
     * @param log
     */
    public void start_transaction(final int pid, final boolean log) {
        if (gdcoll.isBulkMode()) {
            return;
        }
        if (log) {
            gdcoll.getTman().startTransaction("", "", pid, this);
        }
        Map<Integer, Integer> transStackTable = gdcoll.getTransStackTable();
        Integer pidInteger = new Integer(pid);
        Integer transStackInteger = transStackTable.remove(pidInteger);
        if (transStackInteger == null) {
            transStackInteger = new Integer(0);
        }
        int transStackInt = transStackInteger.intValue();
        transStackInt++;
        transStackInteger = new Integer(transStackInt);
        transStackTable.put(pidInteger, transStackInteger);

        //		Integer tst = transStackTable.get(pidInteger);
        //		System.err.println(iii++ + " + " + pid + ": "+ tst + " " + this);

        //		if (tst != null && lastTransStackInt == tst)
        //			System.err.println("jetze");

        if (log) {
            if (!(transStackInt > 1)) {
                //				System.err.println("start_transaction " + iii++ + " + " + pid + ": "+ transStackInt + " " + this);
                for (ElementContainer ec : selectedContainer) {
                    getCollection().getTman().addPreSelectionItem(ec.getHashString(), pid);
                }
            }
        }
    }

    /**
     * @param pid
     */
    public void finish_transaction(final int pid) {
        finish_transaction(pid, true);
    }

    @SuppressWarnings("unused")
    private static int iii = 1;

    @SuppressWarnings("unused")
    private static int lastTransStackInt = -1;

    /**
     * @param pid
     * @param log
     */
    public void finish_transaction(final int pid, final boolean log) {
        if (gdcoll.isBulkMode()) {
            return;
        }
        Map<Integer, Integer> transStackTable = gdcoll.getTransStackTable();
        Integer pidInteger = new Integer(pid);
        Integer transStackInteger = transStackTable.remove(pidInteger);
        if (transStackInteger == null) {
            transStackInteger = new Integer(0);
        }
        int transStackInt = transStackInteger.intValue();
        transStackInt--;
        if (transStackInt > 0) {
            transStackInteger = new Integer(transStackInt);
            transStackTable.put(pidInteger, transStackInteger);
        }
        Integer tst = transStackTable.get(pidInteger);
        //		System.err.println(iii++ + " - " + pid + ": "+ tst + " " + this);
        lastTransStackInt = tst == null ? 0 : tst;
        if (log) {
            //			System.err.println("### " + (iii++) + " " + pid);
            if (transStackInt == 0) {
                //				System.err.println("finish_transaction " + iii++ + " - " + pid + ": "+ transStackInt + " " + this);
                for (ElementContainer ec : selectedContainer) {
                    getCollection().getTman().addPostSelectionItem(ec.getHashString(), pid);
                }
            }
            getCollection().getTman().finishTransaction(pid);
        }
    }

    /**
     * @param flag
     */
    public void setVerificationMode(final boolean flag) {
        verify_mode = flag;
    }

    /**
     * @return
     */
    public boolean isVerificationMode() {
        return verify_mode;
    }

    // --- Transaktions-Verwaltung --- Ende ---

    // --- Layer-Verwaltung --- Anfang ---

    /**
     * @return
     */
    public final LayerContainer getActiveLayer() {
        return layer[gdcoll.getActiveLayer()];
    }

    /**
     * @param index
     * @return
     */
    public final LayerContainer getLayer(final int index) {
        if (index < 0 || index > 4) {
            return null;
        }
        return layer[index];
    }

    /**
     * @return
     */
    public final List<LayerContainer> getLayers() {
        return Arrays.asList(layer);
    }

    /**
     * @param x
     * @param y
     */
    public final void setKnotInsertPosition(final int x, final int y) {
        next_x_pos = x;
        next_y_pos = y;
    }

    // --- Layer-Verwaltung --- Ende ---

    // --- GraphElementLayout-Verwaltung --- Anfang ---

    /**
     * @param map
     */
    public final void setMapping(final ElementsLayoutDefinition map) {
        if (map != null) {
            mapping = map;
        }
    }

    /**
     * @return
     */
    public final ElementsLayoutDefinition getMapping() {
        return mapping;
    }

    /**
     * @param map
     */
    public final void adaptMapping(final ElementsLayoutDefinition map) {
        mapping.adapt(map);
        for (LayerContainer lc : layer) {
            List<NodeContainer> elementContainers = lc.getKnoten();
            for (NodeContainer kc : elementContainers) {
                kc.refreshFont();
                kc.refreshText();
            }
        }
    }

    /**
     * @param mc
     * @param pid
     */
    protected final void normalizeElement(final ElementContainer mc, final int pid) {
        start_transaction(pid);
        normalizeFontElement(mc, pid);
        normalizeTransparencyElement(mc, pid);
        normalizeColorElement(mc, pid);
        finish_transaction(pid);
    }

    /**
     * @param ec
     * @param pid
     */
    protected final void normalizeFontElement(final ElementContainer ec, final int pid) {
        start_transaction(pid);
        String szenHash = ec.getGraphDocument().hashString;
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT + " " + szenHash + " " + ec.getHashString(), "", pid);

        if (ec.getFontName() != null) {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_FONT + " " + szenHash + " " + ec.getHashString(), GDCOMMAND_TEXT_SURROUNDER + ec.getFontName() + GDCOMMAND_TEXT_SURROUNDER + " " + ec.getFontSize() + " " + ec.getFontStyle(), pid);
            ec.setFont(null);
        }
        finish_transaction(pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void normalizeFontElement(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            normalizeFontElement(szen.findContainerCoded(elementHash), pid);
        }
    }

    /**
     * @param ec
     * @param pid
     */
    protected final void normalizeColorElement(final ElementContainer ec, final int pid) {
        start_transaction(pid);
        String szenHash = ec.getGraphDocument().hashString;
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR + " " + szenHash + " " + ec.getHashString(), "", pid);
        if (ec.getColor() != null) {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + ec.getHashString(), ec.getColor().getRGB(), pid);
            ec.setColor(null);
        }
        finish_transaction(pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    protected final void normalizeColorElement(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            normalizeColorElement(szen.findContainerCoded(elementHash), pid);
        }
    }

    /**
     * COMMENTME
     *
     * @param ec
     * @param pid
     */
    protected final void normalizeTransparencyElement(final ElementContainer ec, final int pid) {
        start_transaction(pid);
        String szenHash = ec.getGraphDocument().hashString;
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY + " " + szenHash + " " + ec.getHashString(), "", pid);
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA + " " + szenHash + " " + ec.getHashString(), ec.getAlpha(), pid);
        ec.setAlpha(GraphElementLayout.TRANSPARENCY_NONE);
        finish_transaction(pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    protected final void normalizeTransparencyElement(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            normalizeTransparencyElement(szen.findContainerCoded(elementHash), pid);
        }
    }

    /**
     * @param pid
     */
    public final void normalizeSelected(final int pid) {
        if (selectedContainer.size() == 0) {
            return;
        }
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            normalizeElement(ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
        return;
    }

    /**
     * @param pid
     */
    public final void normalizeFontSelected(final int pid) {
        if (selectedContainer.size() == 0) {
            return;
        }
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            normalizeFontElement(ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param pid
     */
    public final void normalizeColorSelected(final int pid) {
        if (selectedContainer.size() == 0) {
            return;
        }
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            normalizeColorElement(ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
        return;
    }

    /**
     * @param pid
     */
    public final void normalizeTransparencySelected(final int pid) {
        if (selectedContainer.size() == 0) {
            return;
        }
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            normalizeTransparencyElement(ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
        return;
    }

    /**
     * einem Node ein Symbol zuweisen
     *
     * @param hashCode
     * @param iconKey
     * @param pid
     */
    public final void setIcon(final String szenHash, final String hashCode, final String iconKey, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        ElementContainer t = szen.findContainerCoded(hashCode);
        if (!(t instanceof NodeContainer)) {
            return;
        }
        NodeContainer mc = (NodeContainer) t;
        szen.start_transaction(pid);
        if (mc.getColor() != null) {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + mc.getHashString(), mc.getColor().getRGB(), pid);
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA + " " + szenHash + " " + mc.getHashString(), mc.getAlpha(), pid);
        } else {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + mc.getHashString(), "null", pid);
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA + " " + szenHash + " " + mc.getHashString(), "255", pid);
        }
        if (mc.getIcon() != null) {
            addUndoCommandIfNotExist(GDCommands.SET_ICON + " " + szenHash + " " + mc.getHashString(), mc.getIconString(), pid);
        } else {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ICON_NONE + " " + szenHash + " " + mc.getElement().getHashString(), "", pid);
        }
        addRedoCommandOrReplace(GDCommands.SET_ICON + " " + szenHash + " " + mc.getHashString(), iconKey, pid);
        mc.setIcon(iconKey, gdcoll.getIconTable());
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, mc, null, pid);
    }

    /**
     * @param iconKey
     * @param pid
     */
    public final void setIcon(final String iconKey, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            setIcon(hashString, ec.getElement().getHashString(), iconKey, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param iconFile
     */
    public final void setIcon(final File iconFile) {
        String iconKey = gdcoll.loadIcon(iconFile);
        if (iconKey == null) {
            return;
        }
        setIcon(iconKey, STANDARD_PID);
    }

    /**
     * @param hashString
     * @param pid
     */
    public final void unsetIcon(final String szenHash, final String hashString, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        ElementContainer t = szen.findContainerCoded(hashString);
        if (!(t instanceof NodeContainer)) {
            return;
        }
        NodeContainer mc = (NodeContainer) t;
        szen.start_transaction(pid);
        if (mc.getIcon() != null) {
            addUndoCommandIfNotExist(GDCommands.SET_ICON + " " + szenHash + " " + mc.getHashString(), mc.getIconString(), pid);
        }
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_ICON_NONE + " " + szenHash + " " + mc.getHashString(), "", pid);
        mc.setIcon(null, gdcoll.getIconTable());
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, mc, null, pid);
    }

    /**
     * @param pid
     */
    public final void unsetIcon(final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            unsetIcon(hashString, ec.getHashString(), pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param docHash
     * @param hashCode
     * @param color
     * @param pid
     */
    public final void changeColor(final String docHash, final String hashCode, final Color color, final int pid) {
        GraphDocument gdoc = gdcoll.getGraphDocumentCoded(docHash);
        if (gdoc == null) {
            return;
        }
        changeColor(gdoc.findContainerCoded(hashCode), color, pid);
    }

    /**
     * Ändert die Farbe aller selektierten Elemente. Die Farbe wird mit einem
     * JColorChooser erfragt.
     *
     * @param pid
     */
    public final void changeColor(final int pid) {
        if (!gdcoll.isInteractiveMode()) {
            return;
        }
        Color oldcol = null;
        for (ElementContainer ec : selectedContainer) {
            Color tmpcol = ec.getColor();
            if (tmpcol == null) {
                tmpcol = mapping.getStandardBackGroundColor(ec);
            }
            if (oldcol == null) {
                oldcol = tmpcol;
            } else if (!tmpcol.equals(oldcol)) {
                oldcol = null;
                break;
            }
        }
        Color col = JColorChooser.showDialog(new JFrame(), getResString("farbe_ausw"), oldcol);
        if (col == null) {
            return;
        }
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            changeColor(ec, col, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param ec
     * @param color
     * @param pid
     */
    private final void changeColor(final ElementContainer ec, final Color color, final int pid) {
        if (ec == null) {
            return;
        }
        GraphDocument ecDoc = ec.getGraphDocument();
        ecDoc.start_transaction(pid);
        String szenHash = ecDoc.hashString;
        if (color == null) {
            addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + ec.getHashString(), "null", pid);
        } else {
            addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + ec.getHashString(), color.getRGB(), pid);
        }
        if (ec.getColor() == null) {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + ec.getHashString(), "null", pid);
        } else {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + ec.getHashString(), ec.getColor().getRGB(), pid);
        }
        ec.setColor(color);
        ecDoc.finish_transaction(pid);
        ecDoc.distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, null, pid);
    }

    /**
     * @param col
     * @param pid
     */
    public final void changeLayerColor(final Color col, final int pid) {
        changeLayerColor(gdcoll.getSelectedDoc().hashString, gdcoll.getActiveLayer(), col, pid);
    }

    /**
     * @param pid
     */
    public final void changeLayerColor(final int pid) {
        if (!gdcoll.isInteractiveMode()) {
            return;
        }
        Color oldcol = null;
        for (int c = 1; c <= 3; c++) {
            Color tmpcol = layer[c].getColor();
            if (tmpcol == null) {
                tmpcol = mapping.getStandardBackGroundColor(layer[c]);
            }
            if (oldcol == null) {
                oldcol = tmpcol;
            } else if (!tmpcol.equals(oldcol)) {
                oldcol = null;
                break;
            }
        }
        Color col = JColorChooser.showDialog(null, getResString("farbe_ausw"), oldcol);
        if (col == null) {
            return;
        }
        changeLayerColor(col, pid);
    }

    /**
     * @param layer_idx
     * @param pid
     */
    public final void changeLayerColor(final String szenHash, final int layer_idx, final int pid) {
        if (layer_idx < 0 || layer_idx > 4) {
            return;
        }
        if (!gdcoll.isInteractiveMode()) {
            return;
        }
        Color col = JColorChooser.showDialog(null, getResString("farbe_ausw"), layer[layer_idx].getColor());
        if (col == null) {
            return;
        }
        changeLayerColor(szenHash, layer_idx, col, pid);
    }

    /**
     * @param layer_idx
     * @param col
     * @param pid
     */
    public final void changeLayerColor(final String szenHash, final int layer_idx, final Color col, final int pid) {
        if (layer_idx < 0 || layer_idx > 4) {
            return;
        }
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        szen.start_transaction(pid);
        if (col == null) {
            addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layer_idx, "null", pid);
        } else {
            addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layer_idx, col.getRGB(), pid);
        }
        if (szen.layer[layer_idx].getColor() == null) {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layer_idx, "null", pid);
        } else {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layer_idx, szen.layer[layer_idx].getColor().getRGB(), pid);
        }
        szen.layer[layer_idx].setColor(col);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, null, szen.layer[layer_idx], pid);
    }

    /**
     * den Alpha-Wert eines ModelElements aendern
     *
     * @param alphaMode
     * @param pid
     */
    public final void changeAlpha(int alphaMode, final int pid) {
        if (alphaMode < 0) {
            alphaMode = GraphElementLayout.TRANSPARENCY_FULL;
        } else if (alphaMode > 255) {
            alphaMode = GraphElementLayout.TRANSPARENCY_NONE;
        }
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            changeAlpha(ec, alphaMode, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param hashCode
     * @param alphaMode
     * @param pid
     */
    public void changeAlpha(final String szenHash, final String hashCode, final int alphaMode, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            changeAlpha(szen.findContainerCoded(hashCode), alphaMode, pid);
        }
    }

    /**
     * @param ec
     * @param alphaMode
     * @param pid
     */
    private void changeAlpha(final ElementContainer ec, int alphaMode, final int pid) {
        if (ec.layerFor() < 0) {
            return;
        }
        if (alphaMode < 0) {
            alphaMode = GraphElementLayout.TRANSPARENCY_FULL;
        } else if (alphaMode > 255) {
            alphaMode = GraphElementLayout.TRANSPARENCY_NONE;
        }

        GraphDocument ecDoc = ec.getGraphDocument();
        String szenHash = ecDoc.hashString;

        ecDoc.start_transaction(pid);
        if (ec.getColor() == null) {
            changeColor(ec, mapping.getStandardBackGroundColor(ec), pid);
        }
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA + " " + szenHash + " " + ec.getHashString(), alphaMode, pid);
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA + " " + szenHash + " " + ec.getHashString(), ec.getAlpha(), pid);
        ec.setAlpha(alphaMode);
        ecDoc.finish_transaction(pid);
        ecDoc.distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, null, pid);
    }

    /**
     * @param layer_idx
     * @param alphaMode
     * @param pid
     */
    public final void changeLayerAlpha(final String szenHash, final int layer_idx, int alphaMode, final int pid) {
        if (layer_idx < 0 || layer_idx > 4) {
            return;
        }
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        if (alphaMode < 0) {
            alphaMode = GraphElementLayout.TRANSPARENCY_FULL;
        } else if (alphaMode > 255) {
            alphaMode = GraphElementLayout.TRANSPARENCY_NONE;
        }
        szen.start_transaction(pid);
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_LAYER_ALPHA + " " + szenHash + " " + layer_idx, alphaMode, pid);
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_ALPHA + " " + szenHash + " " + layer_idx, szen.layer[layer_idx].getAlpha(), pid);
        szen.layer[layer_idx].setAlpha(alphaMode);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, null, szen.layer[layer_idx], pid);
    }

    /**
     * @param alphaMode
     * @param pid
     */
    public final void changeLayerAlpha(final int alphaMode, final int pid) {
        changeLayerAlpha(gdcoll.getSelectedDoc().hashString, gdcoll.getActiveLayer(), alphaMode, pid);
    }

    /**
     * @param layer_idx
     * @param pid
     */
    public final void normalizeLayer(final String szenHash, final int layer_idx, final int pid) {
        if (layer_idx < 0 || layer_idx > 4) {
            return;
        }
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        szen.start_transaction(pid);

        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_ALPHA + " " + szenHash + " " + layer_idx, szen.layer[layer_idx].getAlpha(), pid);
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY + " " + szenHash + " " + layer_idx, "", pid);
        if (szen.layer[layer_idx].getColor() == null) {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layer_idx, "null", pid);
        } else {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layer_idx, szen.layer[layer_idx].getColor().getRGB(), pid);
        }
        szen.layer[layer_idx].setColor(Color.white);
        szen.layer[layer_idx].setAlpha(GraphElementLayout.TRANSPARENCY_NONE);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, null, szen.layer[layer_idx], pid);
    }

    /**
     * @param ec
     * @param font
     * @param pid
     */
    public final void changeFont(final ElementContainer ec, final Font font, final int pid) {
        if (ec == null) {
            return;
        }

        GraphDocument ecDoc = ec.getGraphDocument();
        ecDoc.start_transaction(pid);
        String szenHash = ecDoc.hashString;

        String commandPrefix = GDCommands.MODEL_ACTION_SET_ELEMENT_FONT + " " + szenHash + " " + ec.getHashString();
        String undoCommandArguments = ec.hasStandardFont() ? "" : GDCOMMAND_TEXT_SURROUNDER + ec.getFontName() + GDCOMMAND_TEXT_SURROUNDER + " " + ec.getFontSize() + " " + ec.getFontStyle();
        String redoCommandArguments = ec.isStandardFont(font) ? "" : GDCOMMAND_TEXT_SURROUNDER + font.getName() + GraphDocument.GDCOMMAND_TEXT_SURROUNDER + " " + font.getSize() + " " + font.getStyle();
        addUndoCommandIfNotExist(commandPrefix, undoCommandArguments, pid);
        addRedoCommandOrReplace(commandPrefix, redoCommandArguments, pid);

        ec.setFont(font);
        ec.refreshText();
        ecDoc.finish_transaction(pid);
        ecDoc.distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, null, pid);
    }

    /**
     * @param szenHash
     * @param hashCode
     */
    public final void changeFont(final String szenHash, final String hashCode, final int pid) {
        changeFont(szenHash, hashCode, "", 0, 0, pid);
    }

    /**
     * @param hashCode
     * @param name
     * @param size
     * @param style
     * @param pid
     */
    public final void changeFont(final String szenHash, final String hashCode, final String name, final int size, final int style, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        szen.start_transaction(pid);
        Font font = name == null || name.equals("") ? null : new Font(name, style, size);
        ElementContainer ec = szen.findContainerCoded(hashCode);
        changeFont(ec, font, pid);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param font
     * @param pid
     */
    public final void changeFont(Font font, final int pid) {
        start_transaction(pid);
        if (selectedContainer.size() != 0) {
            if (font == null) {
                font = EasyDialogAccess.getFontByChooser(Static.getMainFrame(), getLastSelected().getFont());
            }
            if (font != null) {
                for (ElementContainer ec : selectedContainer) {
                    changeFont(ec, font, pid);
                }
            }
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param hashString
     * @param lineStyle
     * @param pid
     */
    public final void changeLineStyle(final String szenHash, final String hashString, final int lineStyle, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        if (lineStyle < 0 || lineStyle > 1) {
            return;
        }
        ElementContainer mc = szen.findContainerCoded(hashString);
        if (mc == null) {
            return;
        }
        szen.start_transaction(pid);
        String commandPrefix = GDCommands.CHANGE_LINE_STYLE + " " + szenHash + " " + mc.getHashString();
        addUndoCommandIfNotExist(commandPrefix, mc.getLineStyle(), pid);
        addRedoCommandOrReplace(commandPrefix, lineStyle, pid);
        mc.setLineStyle(lineStyle);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, mc, null, pid);
    }

    /**
     * @param lineStyle
     * @param pid
     */
    public final void changeLineStyle(final int lineStyle, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            changeLineStyle(hashString, ec.getHashString(), lineStyle, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    // --- GraphElementLayout-Verwaltung --- Ende ---

    // --- Methoden auf Node --- Anfang ---

    /**
     * @param nc
     * @param x
     * @param y
     * @param width
     * @param height
     * @param pid
     */
    public final void coordinateKnot(final NodeContainer nc, final int x, final int y, final int width, final int height, final int pid) {
        if (nc == null || nc.isUnpaintable()) {
            return;
        }
        start_transaction(pid);
        String szenHash = nc.getGraphDocument().hashString;

        String undoCommandArguments = nc.getX() + " " + nc.getY() + " " + nc.getWidth() + " " + nc.getHeight();
        String redoCommandArguments = x + " " + y + " " + width + " " + height;
        String commandPrefix = COORDINATE_KNOT + " " + szenHash + " " + nc.getHashString();
        addUndoCommandIfNotExist(commandPrefix, undoCommandArguments, pid);
        addRedoCommandOrReplace(commandPrefix, redoCommandArguments, pid);
        nc.setCoordinates(x, y, width, height);

        //wenn NodeContainer verschoben werden (keine KnickpinktContainer)
        if (!(nc instanceof BendpointContainer)) {
            //bei allen Kanten dieser Node
            for (Edge ka : nc.getKnoten().getEdges()) {
                EdgeContainer edgeC = (EdgeContainer) ka.getContainer(this);
                //wenn die Edge keinen Container in diesem Teilmodell hat (dann wird sie
                //auch nicht Grafisch dargestellt und es braucht nichts verschoben werden) -> weiter
                if (edgeC == null) {
                    continue;
                }
                //aktualisiere die Endpunkte der Edge
                edgeC.computeBorderPoints();
            }
        } else {
            ((BendpointContainer) nc).getKnickpunktKnoten().getOwner().computeBorderPoints();
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, nc, null, pid);
    }

    /**
     * Verschiebt alle {@link NodeContainer} und {@link BendpointContainer} in der Selektion. Je nach
     * gewählter Option {@link UserProperties#isMoveSubelements()} werden untergordnete Elemente, die nicht
     * selektiert sind, ebenfalls verschoben. {@link ModelConstants#NO_LAYER}
     *
     * @param deltax
     *            Anzahl der Pixel, um die in X-Richtung verschoben werden soll
     * @param deltay
     *            Anzahl der Pixel, um die in Y-Richtung verschoben werden soll
     * @param layer
     *            Layer, dessen Selektierte Elemente übergeben werden sollen<br>
     *            Mögliche Werte:<br>
     *            <ul>
     *            <li>{@link ModelConstants#NO_LAYER}, wenn Elemente aller Layer verschoben werden sollen</li>
     *            <li>{@link ModelConstants#DOMAIN_LAYER}, wenn Elemente der FE verschoben werden sollen</li>
     *            <li>{@link ModelConstants#LOGICAL_LAYER}, wenn Elemente der LWE verschoben werden sollen</li>
     *            <li>{@link ModelConstants#PHYSICAL_LAYER}, wenn Elemente der PWE verschoben werden sollen</li>
     *            </ul>
     * @param pid
     *            ID der Transaktion
     */
    public final void moveSelectedNodeContainer(final int deltaX, final int deltaY, final int layer, final int pid) {
        if (deltaX == 0 && deltaY == 0) {
            return;
        }
        //Unterelemente ebenfalls selektieren, damit sie mitverschoben werden und ihr Verschieben
        //dann auch als Undo gelogt wird
        boolean moveSubelements = UserProperties.is(BooleanProperty.OPTION_GRAPH_MOVE_SUBELEMENTS);
        List<ElementContainer> selection = expandSelection(moveSubelements, true);
        for (NodeContainer kc : getSelectedRealElementContainerIterable()) {
            if (layer == ModelConstants.NO_LAYER || layer == kc.layerFor()) {
                coordinateKnot(kc, kc.getX() + deltaX, kc.getY() + deltaY, kc.getWidth(), kc.getHeight(), pid);
            }
        }
        for (BendpointContainer kc : getSelectedBendpointContainerIterable()) {
            if (layer == ModelConstants.NO_LAYER || layer == kc.layerFor()) {
                coordinateKnot(kc, kc.getX() + deltaX, kc.getY() + deltaY, kc.getWidth(), kc.getHeight(), pid);
            }
        }
        setSelection(selection);
    }

    /**
     * @param gdcoll
     * @param szenHash
     * @param elementHashCode
     * @param x
     * @param y
     * @param width
     * @param height
     * @param pid
     */
    private static final void coordinateKnot(final GDCollection gdcoll, final String szenHash, final String elementHashCode, final int x, final int y, final int width, final int height, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        NodeContainer mc = szen.findNodeContainerCoded(elementHashCode);
        if (mc == null) {
            mc = szen.findBendpointContainerCoded(elementHashCode);
        }
        if (mc == null) {
            return;
        }
        NodeContainer k = mc;
        szen.coordinateKnot(k, x, y, width, height, pid);
    }

    /**
     * Liefert <code>true</code>, wenn selektierte Node aneinander ausgerichtet
     * werden können.
     *
     * @return
     */
    public boolean isAlignable() {
        //Mehrfach selektierte Node, wobei der zuletzt selektierte ein richtiger Node sein muss (also
        //kein Knickpunkt) und der zuletzt selektierte Node zeichenbar sein muss
        return isMultipleNodeSelection() && getLastSelected() instanceof NodeContainer && !getLastSelected().isUnpaintable();
    }

    /**
     * @param mode
     * @param pid
     */
    public final void align(final GDCommands mode, final int pid) {
        if (!isAlignable()) {
            return;
        }
        start_transaction(pid);
        NodeContainer lastSelected = (NodeContainer) getLastSelected();
        for (ElementContainer ec : selectedContainer) {
            if (!(ec instanceof NodeContainer)) {
                continue;
            }
            NodeContainer nc = (NodeContainer) ec;
            int x = nc.getX();
            int y = nc.getY();
            int w = nc.getWidth();
            int h = nc.getHeight();
            switch (mode) {
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_TOP:
                y = lastSelected.getY() - lastSelected.getHeight() / 2 + h / 2;
                break;
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_CENTER:
                y = lastSelected.getY();
                break;
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_BOTTOM:
                y = lastSelected.getY() + lastSelected.getHeight() / 2 - h / 2;
                break;
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_LEFT:
                x = lastSelected.getX() - lastSelected.getWidth() / 2 + w / 2;
                break;
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_CENTER:
                x = lastSelected.getX();
                break;
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_RIGHT:
                x = lastSelected.getX() + lastSelected.getWidth() / 2 - w / 2;
                break;
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH:
                w = lastSelected.getWidth();
                break;
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH:
                h = lastSelected.getHeight();
                break;
            case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH:
                w = lastSelected.getWidth();
                h = lastSelected.getHeight();
                break;
            default:
                System.out.println("Falscher Orientierungswert.");
                break;
            }
            coordinateKnot(nc, x, y, w, h, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * Erweitert die Selektion, um alle Teilelemente der selektierten Elemente, die bisher nicht in
     * der Selektion waren.
     *
     * @param addAllParts
     *            Wenn <code>true</code> werden alle über {@link IsPartOfEdge}en verbunden Elemente
     *            in die Selektion mit aufgenommen.
     * @param addAllSlaves
     *            Wenn <code>true</code> werden alle über {@link Composition}s verbunden Elemente
     *            in die Selektion mit aufgenommen.
     * @return <code>null</code>, wenn keine Erweiterung der bestehenden Selektion nötig war, sonst
     *         die alte Selektion
     */
    private List<ElementContainer> expandSelection(final boolean addAllParts, final boolean addAllSlaves) {
        List<ElementContainer> container2Select = new ArrayList<>();
        for (NodeContainer nc : selectedContainer.iterableRealElementContainer()) {
            ModelElement me = nc.getElement();
            if (addAllParts) {
                for (ElementContainer partNc : me.getPartContainer(this, false)) {
                    if (!isSelected(partNc)) {
                        container2Select.add(partNc);
                    }
                    //Slaves des Parts auch alle zur Selektion hinzufügen
                    if (addAllSlaves) {
                        for (ElementContainer slaveNc : partNc.getElement().getDirectCompositionSlaveContainer(this)) {
                            if (!isSelected(slaveNc)) {
                                container2Select.add(slaveNc);
                            }
                        }
                    }
                }
            }
            if (addAllSlaves) {
                for (ElementContainer slaveNc : me.getDirectCompositionSlaveContainer(this)) {
                    if (!isSelected(slaveNc)) {
                        container2Select.add(slaveNc);
                    }
                }
            }
        }
        if (container2Select.size() == 0) {
            return null;
        }
        List<ElementContainer> oldSelection = getSelectedContainer();
        ElementContainer lastSelected = oldSelection.get(oldSelection.size() - 1);
        for (ElementContainer ec : container2Select) {
            addSimpleToSelection(ec);
        }
        addSimpleToSelection(lastSelected);
        return oldSelection;
    }

    /**
     * Setzt die übergebene Collection als Selektion. Ist diese Collection eine
     * Liste, wird das letzte Element in der Liste als lastSelected gesetzt, sonst
     * ist es zufällig eines der selektierten.
     *
     * @param selection
     */
    private void setSelection(final List<ElementContainer> selection) {
        if (selection != null) {
            selectedContainer.set(selection);
        }
    }

    // --- Methoden auf Node --- Ende ---

    /**
     * für vergröbern und verfeinern
     */
    public final void aufklappen(final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            aufklappen(gdcoll, hashString, ec.getHashString(), pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * Das rekursive Auf- und Zuklappen merkt sich über diese Liste, welche Elemente bereits in einem
     * Durchgang angefasst wurden.
     */
    private static List<ElementContainer> tmpExpandedElements = new ArrayList<>();

    /**
     * Das rekursive Auf- und Zuklappen merkt sich über diesen Wert, wie oft die Rekursion in einem
     * Durchlauf bereits ausgeführt wurde.
     */
    private static int tmpExpansionLevel = 0;

    /**
     * für vergröbern und verfeinern
     *
     * @param elementHash
     * @param pid
     */
    private static final void aufklappen(final GDCollection gdcoll, final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        if (tmpExpansionLevel == 0) {
            tmpExpandedElements.clear();
        }
        ElementContainer ec = szen.findContainerCoded(elementHash);
        if (ec == null) {
            return;
        }
        if (tmpExpandedElements.contains(ec)) {
            return;
        }
        tmpExpansionLevel++;
        tmpExpandedElements.add(ec);
        szen.start_transaction(pid);
        szen.addRedoCommand(GDCommands.AUFKLAPPEN + " " + szenHash + " " + elementHash, pid);
        szen.addUndoCommand(GDCommands.ZUKLAPPEN + " " + szenHash + " " + elementHash, pid);

        ec.setExpanded(true);
        ModelElement me = ec.getElement();
        for (ElementContainer c : me.getDirectPartContainer(szen)) {
            c.setVisible(true);
            if (c.isExpanded()) {
                aufklappen(gdcoll, szenHash, c.getHashString(), pid);
            }
        }
        // Anpassen der Kanten
        for (Edge edge : ec.getElement().getEdges()) {
            EdgeContainer kc = (EdgeContainer) edge.getContainer(szen);
            if (kc == null) {
                continue;
            }
            kc.computeBorderPoints();
        }

        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
        tmpExpansionLevel--;
    }

    /**
     * für vergröbern und verfeinern
     *
     * @param pid
     */
    public final void zuklappen(final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            zuklappen(gdcoll, hashString, ec.getHashString(), true, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * für vergröbern und verfeinern
     *
     * @param gdcoll
     * @param szenHash
     * @param elementHash
     * @param doCollapse
     * @param pid
     */
    private static final void zuklappen(final GDCollection gdcoll, final String szenHash, final String elementHash, final boolean doCollapse, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        if (tmpExpansionLevel == 0) {
            tmpExpandedElements.clear();
        }

        ElementContainer ec = szen.findContainerCoded(elementHash);
        if (ec == null) {
            return;
        }
        if (ec.getElement().getDirectPartContainer(szen).size() == 0) {
            return;
        }

        if (tmpExpandedElements.contains(ec)) {
            return;
        }

        tmpExpansionLevel++;
        tmpExpandedElements.add(ec);

        szen.start_transaction(pid);
        szen.addRedoCommand(GDCommands.ZUKLAPPEN + " " + szenHash + " " + elementHash, pid);
        szen.addUndoCommand(GDCommands.AUFKLAPPEN + " " + szenHash + " " + elementHash, pid);

        if (doCollapse) {
            ec.setExpanded(false);
        }
        ModelElement me = ec.getElement();
        List<ElementContainer> all = me.getDirectPartContainer(szen);
        for (ElementContainer c : all) {
            if (tmpExpandedElements.contains(c)) {
                continue;
            }
            c.setVisible(false);
            if (c.isExpanded()) {
                zuklappen(gdcoll, szenHash, c.getHashString(), false, pid);
            }
        }

        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);

        tmpExpansionLevel--;
    }

    /**
     * für vergröbern und verfeinern
     *
     * @param pid
     */
    public final void auf_zuklappen(final int pid) {
        start_transaction(pid);
        boolean oldBulkMode = gdcoll.isBulkMode();
        gdcoll.setBulkMode(true);
        for (ElementContainer ec : selectedContainer) {
            if (ec.isExpanded()) {
                zuklappen(gdcoll, hashString, ec.getHashString(), true, pid);
            } else {
                aufklappen(gdcoll, hashString, ec.getHashString(), pid);
            }
        }
        gdcoll.setBulkMode(oldBulkMode);
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    // --- Event-Verwaltung --- Anfang ---

    /**
     * @param gdl
     */
    public final void addInTransactionListener(final InTransactionListener gdl) {
        inlistener.add(gdl);
    }

    /**
     * @param gdl
     */
    public final void removeInTransactionListener(final InTransactionListener gdl) {
        inlistener.remove(gdl);
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
     * Wenn die anderen Parameter aus der Methode <code>distributeEvent(int, ElementContainer, LayerContainer, int)</code> nicht angegeben werden
     * können, kann man hiermit ein allgemeines Ereignis feuern.
     *
     * @param changeType
     */
    public final void distributeEvent(final GDCollectionChangeType changeType) {
        distributeEvent(changeType, TransactionManager.STANDARD_PID);
    }

    /**
     * Wenn die anderen Parameter aus der Methode <code>distributeEvent(int, ElementContainer, LayerContainer, int)</code> nicht angegeben werden
     * können, kann man hiermit ein allgemeines Ereignis feuern.
     *
     * @param changeType
     * @param pid
     */
    public final void distributeEvent(final GDCollectionChangeType changeType, final int pid) {
        distributeEvent(changeType, null, null, pid);
    }

    /**
     * @param changeType
     * @param last_elem
     * @param last_group
     * @param pid
     */
    public final void distributeEvent(final GDCollectionChangeType changeType, final ElementContainer last_elem, final LayerContainer last_group, final int pid) {
        Integer pidInteger = new Integer(pid);
        Integer transStackInteger = gdcoll.getTransStackTable().get(pidInteger);
        if (transStackInteger == null) {
            transStackInteger = new Integer(0);
        }
        if (transStackInteger.intValue() > 0) {
            if (transStackInteger.intValue() == 1) {
                switch (changeType) {
                case DATA_CHANGED:
                    for (InTransactionListener itl : inlistener) {
                        itl.dataChanged(this, pid);
                    }
                    break;
                case ELEMENT_ADDED:
                    for (InTransactionListener itl : inlistener) {
                        itl.elementAdded(this, last_elem);
                    }
                    break;
                case ELEMENT_DELETED:
                    for (InTransactionListener itl : inlistener) {
                        itl.elementDeleted(this, last_elem);
                    }
                    break;
                case ELEMENT_GRAPHICS_CHANGED:
                    for (InTransactionListener itl : inlistener) {
                        itl.elementGraphicsChanged(this, last_elem);
                    }
                    break;
                case ELEMENT_NAME_CHANGED:
                    for (InTransactionListener itl : inlistener) {
                        itl.elementNameChanged(last_elem);
                    }
                    break;
                case USER_FIELD_VALUE_CHANGED:
                    for (InTransactionListener itl : inlistener) {
                        itl.userFieldValueChanged(last_elem);
                    }
                    break;
                default:
                    break;
                }
            }
        } else {
            gdcoll.distribute(changeType, last_elem, last_group, this, pid);
        }
    }

    /**
     * @param changeType
     * @param last_elem
     * @param last_group
     * @param pid
     */
    public void distributeEventIntern(final GDCollectionChangeType changeType, final ElementContainer last_elem, final LayerContainer last_group, final int pid) {
        if (gdcoll.isBulkMode()) {
            return;
        }

        Integer pidInteger = new Integer(pid);
        Integer transStackInteger = gdcoll.getTransStackTable().get(pidInteger);
        if (transStackInteger == null) {
            transStackInteger = new Integer(0);
        }
        if (transStackInteger.intValue() > 0) {
            if (transStackInteger.intValue() == 1) {
                switch (changeType) {
                case DATA_CHANGED:
                    for (InTransactionListener itl : inlistener) {
                        itl.dataChanged(this, pid);
                    }
                    break;
                case ELEMENT_ADDED:
                    for (InTransactionListener itl : inlistener) {
                        itl.elementAdded(this, last_elem);
                    }
                    break;
                case ELEMENT_DELETED:
                    for (InTransactionListener itl : inlistener) {
                        itl.elementDeleted(this, last_elem);
                    }
                    break;
                case ELEMENT_GRAPHICS_CHANGED:
                    for (InTransactionListener itl : inlistener) {
                        itl.elementGraphicsChanged(this, last_elem);
                    }
                    break;
                case ELEMENT_NAME_CHANGED:
                    for (InTransactionListener itl : inlistener) {
                        itl.elementNameChanged(last_elem);
                    }
                    break;
                case USER_FIELD_VALUE_CHANGED:
                    for (InTransactionListener itl : inlistener) {
                        itl.userFieldValueChanged(last_elem);
                    }
                    break;
                default:
                    break;
                }
            }
            return;
        }

        if (isVerificationMode()) {
            System.out.println("distributeEvent: " + changeType);
        }

        switch (changeType) {
        case DATA_CHANGED:
            for (GraphDocumentListener gdl : listener) {
                gdl.dataChanged(this);
            }
            for (InTransactionListener itl : inlistener) {
                itl.dataChanged(this, pid);
            }
            break;
        case ELEMENT_GRAPHICS_CHANGED:
            for (GraphDocumentListener gdl : listener) {
                gdl.elementGraphicsChanged(this, last_elem);
            }
            break;
        case LAYOUT_CHANGED:
            for (GraphDocumentListener gdl : listener) {
                gdl.layoutChanged(this);
            }
            break;
        case ELEMENT_ADDED:
            for (GraphDocumentListener gdl : listener) {
                gdl.elementAdded(this, last_elem);
            }
            break;
        case ELEMENT_DELETED:
            for (GraphDocumentListener gdl : listener) {
                gdl.elementDeleted(this, last_elem);
            }
            break;
        case GROUP_ORDER_CHANGED:
            for (GraphDocumentListener gdl : listener) {
                gdl.groupOrderChanged(this);
            }
            break;
        case ACTIVE_LAYER_CHANGED:
            for (GraphDocumentListener gdl : listener) {
                gdl.activeLayerChanged(this);
            }
            break;
        case COLORS_CHANGED:
            for (GraphDocumentListener gdl : listener) {
                gdl.colorsChanged(this);
            }
            break;
        case SELECTION_CHANGED:
            for (GraphDocumentListener gdl : listener) {
                gdl.selectionChanged(this);
            }
            break;
        default:
            break;
        }
    }

    // --- Event-Verwaltung --- Ende ---

    /**
     * @param args
     * @param pid
     */
    private final void setVisible(final String[] args, final int pid) {
        //argv[0] = visible = "true" oder "false"
        //argv[1] = szenHash (optional)
        //argv[2..n] = elementHashes (optional)
        if (args.length < 1) {
            return;
        }
        boolean visible = Boolean.valueOf(args[0]).booleanValue();
        if (args.length == 1) {
            setVisible(visible, pid);
            return;
        }
        String szenHash = args[1];
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        szen.deselectAll(false);
        for (int i = 2; i < args.length; i++) {
            szen.addToSelection(args[i], pid);
        }
        szen.setVisible(visible, pid);
    }

    /**
     * @param visible
     * @param pid
     */
    private final void setVisible(final boolean visible, final int pid) {
        setVisible(hashString, selectedContainer, visible, pid);
    }

    /**
     * @param szenHash
     * @param containerList
     * @param visible
     * @param pid
     */
    private final void setVisible(final String szenHash, final Collection<ElementContainer> container, final boolean visible, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        if (container.size() == 0) {
            return;
        }
        szen.start_transaction(pid);
        StringBuilder sb = new StringBuilder();
        for (ElementContainer ec : container) {
            ec.setVisible(visible);
            sb.append(" ");
            sb.append(ec.getHashString());
        }
        szen.addUndoCommand(GDCommands.SET_VISIBLE + " " + !visible + " " + szenHash + sb, pid);
        szen.addRedoCommand(GDCommands.SET_VISIBLE + " " + visible + " " + szenHash + sb, pid);
        szen.finish_transaction(pid);
        szen.distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param left_x
     * @param left_y
     * @param right_x
     * @param right_y
     */
    public final void selectArea(int left_x, int left_y, int right_x, int right_y) {
        int tmp;
        if (left_x > right_x) {
            tmp = right_x;
            right_x = left_x;
            left_x = tmp;
        }
        if (left_y > right_y) {
            tmp = right_y;
            right_y = left_y;
            left_y = tmp;
        }

        final int PID = TransactionManager.STANDARD_PID;

        start_transaction(PID, false);
        deselectAll(true);
        //alle Node im angegebenen Bereich selektieren
        for (NodeContainer kn : layer[gdcoll.getActiveLayer()].getKnoten()) {
            if (kn.getElement().isUnpaintable()) {
                continue;
            }
            if (kn.getX() < right_x && kn.getX() > left_x && kn.getY() < right_y && kn.getY() > left_y && !kn.isSelected()) {
                addToSelection(kn, PID);
            }
        }
        //alle Kanten im angegebenen Bereich selektieren
        for (EdgeContainer ka : layer[gdcoll.getActiveLayer()].getKanten()) {
            ElementContainer start = ka.getEdge().getStart().getContainer(this);
            ElementContainer end = ka.getEdge().getEnd().getContainer(this);
            if (start == null || start.getElement().isUnpaintable()) {
                continue;
            }

            if (end == null || end.getElement().isUnpaintable()) {
                continue;
            }

            if (start.getX() < right_x && start.getX() > left_x && start.getY() < right_y && start.getY() > left_y && end.getX() < right_x && end.getX() > left_x && end.getY() < right_y && end.getY() > left_y && !ka.isSelected()) {
                addToSelection(ka, PID);
            }
            //alle Knickpunkte der Edge, die im Auswahlrechteck liegen ebenfall selektieren
            if (ka.isVisible()) {
                for (BendpointContainer kpc : ka.iterateBendpointContainers()) {
                    if (kpc.getX() < right_x && kpc.getX() > left_x && kpc.getY() < right_y && kpc.getY() > left_y && !kpc.isSelected()) {
                        addToSelection(kpc, PID);
                    }
                }
            }

        }
        finish_transaction(PID, false);
        distributeEvent(SELECTION_CHANGED, PID);
    }

    /**
     * @param insideTransaction
     */
    public final void deselectAll(final boolean insideTransaction) {
        if (!insideTransaction) {
            start_transaction(TransactionManager.STANDARD_PID, false);
        }
        gdcoll.deselectAll();
        if (!insideTransaction) {
            finish_transaction(TransactionManager.STANDARD_PID, false);
            distributeEvent(SELECTION_CHANGED, TransactionManager.STANDARD_PID);
        }
    }

    /**
     *
     */
    public void selectAll() {
        final int PID = TransactionManager.STANDARD_PID;
        start_transaction(PID, false);
        deselectAll(true);
        for (int i = 0; i < layer.length; i++) {
            for (int c = 0; c < layer[i].getKnotenCount(); c++) {
                addToSelection(layer[i].getNodeContainer(c), PID);
            }
            for (int c = 0; c < layer[i].getKantenCount(); c++) {
                addToSelection(layer[i].getEdgeContainer(c), PID);
            }
            for (int c = 0; c < layer[i].getKnickpunkteCount(); c++) {
                addToSelection(layer[i].getBendpointContainer(c), PID);
            }
        }
        gdcoll.selectAllUniques();

        finish_transaction(PID, false);
        distributeEvent(SELECTION_CHANGED, PID);
    }

    /**
     * @param _analysisResult
     */
    public final void setAnalysisResult(final List<ElementContainer> _analysisResult) {
        //analysisResult.clear();
        analysisResult.addAll(_analysisResult);
        deselectAll(true);
        for (ElementContainer ec : analysisResult) {
            addToSelection(ec, TransactionManager.STANDARD_PID);
        }
        distributeEvent(SELECTION_CHANGED);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED);
    }

    /**
     *
     */
    public final void clearAnalysisResult() {
        analysisResult.clear();
        deselectAll(true);
        distributeEvent(SELECTION_CHANGED);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED);
    }

    /**
     * @param ec
     * @return
     */
    public final boolean isAnalysisResult(final ElementContainer ec) {
        return analysisResult.contains(ec);
    }

    /**
     * @param mc
     * @param pid
     */
    public final void addToSelection(final ElementContainer mc, final int pid) {
        if (mc == null) {
            return;
        }

        //		ModelElement layerElemMe = mc.getElement();
        //		System.err.println("GraphDocument.addToSelection(): " + layerElemMe.getClass().getSimpleName() + " " + this + " " + layerElemMe.getClearName() + " " + layerElemMe.getHashString() + " " + layerElemMe.getCreationDate().toLocaleString());

        gdcoll.addToSelection(mc);
        distributeEvent(SELECTION_CHANGED, mc, null, pid);
    }

    /**
     * Ein Element selektieren
     *
     * @param mc
     * @param pid
     */
    public final void select(final ElementContainer mc, final int pid) {
        if (mc == null) {
            return;
        }
        deselectAll(true);
        addToSelection(mc, pid);
        distributeEvent(SELECTION_CHANGED, mc, null, pid);
    }

    /**
     * @param hashCode
     * @param pid
     */
    public final void addToSelection(final String hashCode, final int pid) {
        ElementContainer mc = findContainerCoded(hashCode);
        addToSelection(mc, pid);
    }

    /**
     * @param ec
     * @param pid
     */
    public final void deselect(final ElementContainer ec, final int pid) {
        if (ec == null) {
            return;
        }
        gdcoll.deselect(ec);
        distributeEvent(SELECTION_CHANGED, ec, null, pid);
    }

    /**
     * Liefert eine Liste aller selektierten {@link ElementContainer}, bei der alle {@link NodeContainer} am Anfang stehen und genau in der
     * Reihenfolge sind, in der sie in der Grafik dargstellt werden.
     *
     * @return
     */
    private List<ElementContainer> getSelectionInGraphOrder() {
        List<ElementContainer> returnList = new ArrayList<>(selectedContainer.size());
        for (ElementContainer ec : getElementContainer(Node.class)) {
            if (selectedContainer.contains(ec)) {
                returnList.add(ec);
            }
        }
        for (ElementContainer ec : selectedContainer) {
            Class<? extends ElementContainer> ecClass = ec.getClass();
            if (BendpointContainer.class.isAssignableFrom(ecClass) || EdgeContainer.class.isAssignableFrom(ecClass)) {
                returnList.add(ec);
            }
        }
        return returnList;
    }

    // --- Selektions-Verwaltung -- Ende ---

    // --- Operation der Element-Verwaltung --- Anfang ---

    //////////////////////////////////////////////////////////
    /* Ein neues Element anlegen */
    //////////////////////////////////////////////////////////

    /**
     * @param elementClass
     *            Typ der Elemente, für das eine nuer Name generiert werden soll. Der neue Namen ist
     *            in diesem GraphDocument eindeutig und besteht aus dem anzeigbaren Elementnamen, einem Leerzeichen
     *            und einer Zahl. Die Zahl ist die kleinste freie Nummer ab 1.
     */
    protected String getNextNewName(String prefix, final Class<? extends ModelElement> elementClass) {
        if (prefix == null) {
            prefix = "";
        }
        String name = prefix + ModelConstants.getDisplayableName(elementClass) + " ";
        String newName = CollectionUtils.getNextIndicatedName(name, gdcoll.getMainGraphDocument().getModelItems(elementClass));
        return newName;
    }

    /**
     * @param elementClass
     *            ModelElement-Klasse für das der nächste nummerierte Standardname zurückgegeben werden soll
     * @return nächste nummerierte Standardname für diese Elementart
     */
    protected String getNextNewName(final Class<? extends ModelElement> elementClass) {
        return getNextNewName("", elementClass);
    }

    /**
     * @param elementClassName
     * @param pid
     * @return
     */
    public NodeContainer createKnotenWithContainer(final String elementClassName, final int pid) {
        return createKnotenWithContainer(ModelConstants.getClassForName(elementClassName), pid);
    }

    /**
     * @param elementClass
     * @param pid
     * @return
     */
    public NodeContainer createKnotenWithContainer(final Class<? extends ModelElement> elementClass, final int pid) {
        return createKnotenWithContainer(elementClass, GDCommands.INVALID_NAME, GDCommands.INVALID_DESCRIPTION, GDCommands.INVALID_HASH_STRING, pid);
    }

    /**
     * @param elementClass
     * @param name
     * @param description
     * @param pid
     * @return
     */
    public NodeContainer createKnotenWithContainer(final Class<? extends ModelElement> elementClass, final String name, final String description, final int pid) {
        return createKnotenWithContainer(elementClass, name, description, GDCommands.INVALID_HASH_STRING, pid);
    }

    /**
     * @param elementClass
     * @param name
     * @param description
     * @param hashString
     * @param pid
     * @return
     */
    public NodeContainer createKnotenWithContainer(final Class<? extends ModelElement> elementClass, final String name, final String description, final String hashString, final int pid) {
        return createKnotenWithContainer(elementClass, name, description, hashString, GDCommands.INVALID_POSITION_X, GDCommands.INVALID_POSITION_Y, GDCommands.INVALID_WIDTH, GDCommands.INVALID_HEIGHT, GDCommands.INVALID_COLOR_RGB, GDCommands.INVALID_SHAPE,
                GDCommands.INVALID_BENDPOINT_INDEX, pid);
    }

    /**
     * @param elementClass
     * @param name
     * @param hashString
     * @param x
     * @param y
     * @param width
     * @param height
     * @param r
     * @param g
     * @param b
     * @param form
     * @param bendpoint_index
     * @param pid
     * @return
     */
    private NodeContainer createKnotenWithContainer(final Class<? extends ModelElement> elementClass, final String name, final String description, final String hashString, int x, int y, final int width, final int height, final int rgb,
            final GraphElementLayout.SHAPE form, final int bendpoint_index, final int pid) {
        lastCreated = null;
        start_transaction(pid);
        if (Node.class.isAssignableFrom(elementClass)) {
            //das neue Element im Hauptdokument anlegen
            lastCreated = gdcoll.createKnotenWithContainer(elementClass.asSubclass(Node.class), name, description, hashString, pid);
        }
        if (lastCreated != null && !lastCreated.getElement().isUnique() && this instanceof Szenario) {
            lastCreated = addElementToSzenario(this.hashString, lastCreated, pid);
            x = x != GDCommands.INVALID_POSITION_X ? x : next_x_pos;
            y = y != GDCommands.INVALID_POSITION_Y ? y : next_y_pos;
            coordinateKnot(lastCreated, x, y, width, height, pid);
        }
        if (!gdcoll.isBulkMode()) {
            select(lastCreated, pid);
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED);
        return lastCreated;
    }

    /**
     * @param hashString
     * @return
     */
    public ElementContainer findContainerCoded(final String hashString) {
        if (hashString == null) {
            return null;
        }

        ModelElement me = findElementCoded(hashString);
        if (me == null) {
            return null;
        }

        return me.getContainer(me.isUnique() ? getCollection().getMainGraphDocument() : this);
    }

    /**
     * @param hashString
     * @return
     */
    public NodeContainer findNodeContainerCoded(final String hashString) {
        if (hashString == null) {
            return null;
        }

        ModelElement me = findKnotenCoded(hashString);
        if (me == null) {
            return null;
        }

        return (NodeContainer) me.getContainer(me.isUnique() ? getCollection().getMainGraphDocument() : this);
    }

    /**
     * @param hashString
     * @return
     */
    public EdgeContainer findEdgeContainerCoded(final String hashString) {
        if (hashString == null) {
            return null;
        }

        ModelElement me = findKanteCoded(hashString);
        if (me == null) {
            return null;
        }

        return (EdgeContainer) me.getContainer(me.isUnique() ? getCollection().getMainGraphDocument() : this);
    }

    /**
     * @param hashString
     * @return
     */
    public BendpointContainer findBendpointContainerCoded(final String hashString) {
        if (hashString == null) {
            return null;
        }

        ModelElement me = findKnickpunktCoded(hashString);
        if (me == null) {
            return null;
        }

        return (BendpointContainer) me.getContainer(me.isUnique() ? getCollection().getMainGraphDocument() : this);
    }

    /**
     * Finds the first element with the given UserField name and the given id String.
     * If the values are IDs a special element can be detected.
     *
     * @param userFieldName
     * @param value
     * @return
     */
    public ModelElement findElementWithUserField(final String userFieldName, final String value) {
        if (getCollection().getMainGraphDocument() != this) {
            return getCollection().getMainGraphDocument().findElementWithUserField(userFieldName, value);
        }

        if (userFieldName == null || value == null) {
            return null;
        }
        //first loop for nodes, second for edges
        for (int i = 0; i < 2; i++) {
            for (LayerContainer lc : layer) {
                int elementCount = i == 0 ? lc.getKnotenCount() : lc.getKantenCount();
                for (int c = 0; c < elementCount; c++) {
                    ElementContainer ec = i == 0 ? lc.getNodeContainer(c) : lc.getEdgeContainer(c);
                    ModelElement me = ec.getElement();
                    UserFieldDefinitions ufd = getCollection().getUserFieldDefinitions();
                    UserField uf = ufd.getUserField(me.getClass(), userFieldName);
                    if (uf == null) {
                        continue;
                    }
                    if (value.equals(me.getUserFieldInputValue(uf))) {
                        return me;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param hashString
     * @return ModelElement with the given HashString or <code>null</code> if no such ModelElement exists eather in szenario nor in doc
     */
    public ModelElement findElementCoded(final String hashString) {
        if (hashString == null) {
            return null;
        }
        ModelElement me = findKnotenCoded(hashString);
        if (me != null) {
            return me;
        }
        me = findKanteCoded(hashString);
        if (me != null) {
            return me;
        }
        me = findKnickpunktCoded(hashString);
        findBendpointContainerCoded(hashString);
        return me;
    }

    /**
     * @param hashString
     * @return
     */
    public ModelElement findKnotenCoded(final String hashString) {
        if (getCollection().getMainGraphDocument() != this) {
            return getCollection().getMainGraphDocument().findKnotenCoded(hashString);
        }
        if (hashString != null) {
            for (LayerContainer lc : layer) {
                for (ElementContainer ec : lc.getKnoten()) {
                    String ecHash = ec.getHashString();
                    if (hashString.equals(ecHash)) {
                        return ec.getElement();
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param hashString
     * @return
     */
    public Edge findKanteCoded(final String hashString) {
        if (getCollection().getMainGraphDocument() != this) {
            return getCollection().getMainGraphDocument().findKanteCoded(hashString);
        }
        if (hashString != null) {
            for (LayerContainer lc : layer) {
                for (EdgeContainer ec : lc.getKanten()) {
                    if (hashString.equals(ec.getHashString())) {
                        return ec.getEdge();
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param hashString
     * @return
     */
    public Knickpunkt findKnickpunktCoded(final String hashString) {
        if (getCollection().getMainGraphDocument() != this) {
            return getCollection().getMainGraphDocument().findKnickpunktCoded(hashString);
        }

        if (hashString == null) {
            return null;
        }
        if (hashString != null) {
            for (LayerContainer lc : layer) {
                for (BendpointContainer bc : lc.getKnickpunkte()) {
                    if (hashString.equals(bc.getHashString())) {
                        return bc.getKnickpunktKnoten();
                    }
                }
            }
        }

        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //#############################################################################################//
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param kn
     */
    public final void raiseSlaves(final ElementContainer kn) {
        int ebene = kn.layerFor();
        if (!ModelConstants.isInterLayer(ebene)) {
            layer[ebene].raiseSlaves(kn, 0);
            distributeEvent(GROUP_ORDER_CHANGED, null, layer[ebene], 0);
        }
    }

    ////////////////////////////////////////////
    // abhängige bzw. untergeordnete Elemente //
    ////////////////////////////////////////////

    /**
     * Legt ein untergerodnetes Element an und fügt es in jedes Szenario ein, in dem sein übergeordnetes
     * Element vorkommt.
     *
     * @param doc
     * @param master
     * @param edgeClass
     * @param slaveClass
     * @param slaveName
     * @param slaveHashString
     * @param pid
     * @return
     */
    private static final ModelElement createAddicted(final GraphDocument doc, final ModelElement master, final Class<? extends Composition> edgeClass, final Class<? extends ModelElement> slaveClass, final String slaveName, final String slaveHashString,
            final int pid) {
        if (master == null || edgeClass == null || slaveClass == null) {
            return null;
        }
        if (!isConnecting(edgeClass, master.getClass(), slaveClass)) {
            return null;
        }
        doc.start_transaction(pid);
        if (master.countConnections(edgeClass) >= getMaxMasterToSlaveCardinality(edgeClass)) {
            return null;
        }
        String name = slaveName == null || slaveName.trim().equals("") ? doc.getNextNewName(master.getClearName() + "_", slaveClass) : slaveName;
        GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();
        NodeContainer slaveContainer = mainDoc.createKnotenWithContainer(slaveClass, name, GDCommands.INVALID_DESCRIPTION, slaveHashString, pid);
        if (slaveContainer == null) {
            doc.finish_transaction(pid);
            return null;
        }
        ModelElement slave = slaveContainer.getElement();
        GDCollection gdcoll = doc.getCollection();
        gdcoll.link(edgeClass, master, slave, pid);

        List<Long> times1 = new ArrayList<>();
        List<Long> times2 = new ArrayList<>();
        for (Szenario szen : gdcoll.getSzenarios()) {
            if (master.getContainer(szen) != null) {
                long l = System.currentTimeMillis();
                szen.addElementToSzenario(szen.getHashString(), slaveContainer, pid);
                times1.add(new Long(System.currentTimeMillis() - l));
                l = System.currentTimeMillis();
                szen.addict(master, slave, edgeClass, pid);
                times2.add(new Long(System.currentTimeMillis() - l));
            }
        }

        doc.finish_transaction(pid);
        doc.distributeEvent(DATA_CHANGED, slaveContainer, null, pid);
        doc.select(slaveContainer, pid);
        return slaveContainer.getElement();
    }

    /**
     * @param doc
     * @param master
     * @param edgeClass
     * @param slaveClass
     * @param slaveName
     * @param pid
     * @return
     */
    public static final ModelElement createAddicted(final GraphDocument doc, final ModelElement master, final Class<? extends Composition> edgeClass, final Class<? extends ModelElement> slaveClass, final String slaveName, final int pid) {
        return createAddicted(doc, master, edgeClass, slaveClass, slaveName, GDCommands.INVALID_HASH_STRING, pid);
    }

    /**
     * @param doc
     * @param master
     * @param edgeClass
     * @param slaveClass
     * @param pid
     * @return
     */
    public static final ModelElement createAddicted(final GraphDocument doc, final ModelElement master, final Class<? extends Composition> edgeClass, final Class<? extends ModelElement> slaveClass, final int pid) {
        return createAddicted(doc, master, edgeClass, slaveClass, GDCommands.INVALID_NAME, pid);
    }

    /**
     * @param me1
     * @param me2
     * @param edgeClass
     * @param pid
     * @return
     */
    public final Edge addict(final ModelElement me1, final ModelElement me2, final Class<? extends Composition> edgeClass, final int pid) {
        return addict(hashString, me1, me2, edgeClass, pid);
    }

    /**
     * @param szenHash
     * @param me1
     * @param me2
     * @param edgeClass
     * @param pid
     * @return
     */
    public final Edge addict(final String szenHash, final ModelElement me1, final ModelElement me2, final Class<? extends Composition> edgeClass, final int pid) {
        return addict(szenHash, edgeClass.getSimpleName(), null, me1, me2, -1, pid);
    }

    /**
     * @param szenHash
     * @param edgeClassName
     * @param edgeHash
     * @param knothash1
     * @param knothash2
     * @param position
     * @param pid
     * @return
     */
    public final Edge addict(final String szenHash, final String edgeClassName, final String edgeHash, final String knothash1, final String knothash2, final int position, final int pid) {
        ModelElement me1 = findElementCoded(knothash1);
        ModelElement me2 = findElementCoded(knothash2);
        return addict(szenHash, edgeClassName, edgeHash, me1, me2, position, pid);
    }

    /**
     * @param szenHash
     * @param edgeClassName
     * @param edgeHash
     * @param masterElement
     * @param slaveElement
     * @param edgeClass
     * @param position
     * @param pid
     * @return
     */
    protected final Edge addict(final String szenHash, final String edgeClassName, final String edgeHash, final ModelElement masterElement, final ModelElement slaveElement, final int position, final int pid) {
        if (masterElement == null || slaveElement == null) {
            return null;
        }
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen == null) {
            return null;
        }

        szen.start_transaction(pid);

        List<Edge> edges = masterElement.getEdgesWith(slaveElement, ModelConstants.getClassForName(edgeClassName).asSubclass(Edge.class));
        if (edges.size() == 0 || !(szen instanceof Szenario)) {
            finish_transaction(pid);
            return null;
        }
        Edge k = edges.get(0);
        if (!(masterElement instanceof Node) || !(slaveElement instanceof Node)) {
            return k;
        }
        NodeContainer masterContainer = (NodeContainer) masterElement.getContainer(szen);
        NodeContainer slaveContainer = (NodeContainer) slaveElement.getContainer(szen);
        //slaveContainer ist null, wenn das untergeordnete Element unique ist und keinen Grafikcontainer in jedem Teilmodell hat
        if (slaveContainer != null) {
            Dimension pos = calculateAddictPosition(masterContainer);

            addRedoCommand(GDCommands.ADDICT + " " + szenHash + " " + edgeClassName + " " + k.getHashString() + " " + masterElement.getHashString() + " " + slaveElement.getHashString() + " " + position, pid);
            addUndoCommand(GDCommands.COORDINATE_KNOT + " " + szenHash + " " + slaveElement.getHashString() + " " + slaveContainer.getX() + " " + slaveContainer.getY() + " " + slaveContainer.getWidth() + " " + slaveContainer.getHeight(), pid);
            slaveContainer.setCoordinates(pos.width, pos.height, slaveContainer.getWidth(), slaveContainer.getHeight());

            for (Szenario szenario : gdcoll.getSzenarios()) {
                EdgeContainer kac = (EdgeContainer) k.getContainer(szenario);
                if (kac != null) {
                    kac.computeBorderPoints();
                }
            }
        }

        szen.finish_transaction(pid);
        szen.distributeEvent(DATA_CHANGED, pid);
        return k;
    }

    /**
     * Berechnet die Position untergeordneter Elemente auf einem Oberelementcontainer.
     *
     * @param kc
     *            Oberelementcontainer auf dem untergeordnete Elemente positioniert werden sollen
     * @return
     */
    private static final Dimension calculateAddictPosition(final NodeContainer kc) {
        Dimension retVal = new Dimension(kc.getX(), kc.getY());

        int wieviele = -1;
        ModelElement me = kc.getElement();
        for (Edge edge : me.getEdges()) {
            if (edge instanceof Composition) {
                ModelElement slave = ((Composition) edge).getSlave();
                if (slave != me && !slave.isUnpaintable()) {
                    wieviele++;
                }
            }
        }
        switch (wieviele % 34) {
        case 0:
            retVal.width = kc.getX() - kc.getWidth() / 7;
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 1:
            retVal.width = kc.getX() - kc.getWidth() / 5;
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 2:
            retVal.width = kc.getX() - kc.getWidth() / 4;
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 3:
            retVal.width = kc.getX() - kc.getWidth() / 3;
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 4:
            retVal.width = kc.getX() - (int) (kc.getWidth() / 2.5);
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 5:
            retVal.width = kc.getX() + kc.getWidth() / 7;
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 6:
            retVal.width = kc.getX() + kc.getWidth() / 5;
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 7:
            retVal.width = kc.getX() + kc.getWidth() / 4;
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 8:
            retVal.width = kc.getX() + kc.getWidth() / 3;
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 9:
            retVal.width = kc.getX() + (int) (kc.getWidth() / 2.5);
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 10:
            retVal.width = kc.getX() - kc.getWidth() / 7;
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 11:
            retVal.width = kc.getX() - kc.getWidth() / 5;
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 12:
            retVal.width = kc.getX() - kc.getWidth() / 4;
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 13:
            retVal.width = kc.getX() - kc.getWidth() / 3;
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 14:
            retVal.width = kc.getX() - (int) (kc.getWidth() / 2.5);
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 15:
            retVal.width = kc.getX() + kc.getWidth() / 7;
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 16:
            retVal.width = kc.getX() + kc.getWidth() / 5;
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 17:
            retVal.width = kc.getX() + kc.getWidth() / 4;
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 18:
            retVal.width = kc.getX() + kc.getWidth() / 3;
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 19:
            retVal.width = kc.getX() + (int) (kc.getWidth() / 2.5);
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        case 20:
            retVal.width = kc.getX() - kc.getWidth() / 7;
            retVal.height = kc.getY();
            break;
        case 21:
            retVal.width = kc.getX() - kc.getWidth() / 5;
            retVal.height = kc.getY();
            break;
        case 22:
            retVal.width = kc.getX() - kc.getWidth() / 4;
            retVal.height = kc.getY();
            break;
        case 23:
            retVal.width = kc.getX() - kc.getWidth() / 3;
            retVal.height = kc.getY();
            break;
        case 24:
            retVal.width = kc.getX() - (int) (kc.getWidth() / 2.5);
            retVal.height = kc.getY();
            break;
        case 25:
            retVal.width = kc.getX() + kc.getWidth() / 7;
            retVal.height = kc.getY();
            break;
        case 26:
            retVal.width = kc.getX() + kc.getWidth() / 5;
            retVal.height = kc.getY();
            break;
        case 27:
            retVal.width = kc.getX() + kc.getWidth() / 4;
            retVal.height = kc.getY();
            break;
        case 28:
            retVal.width = kc.getX() + kc.getWidth() / 3;
            retVal.height = kc.getY();
            break;
        case 29:
            retVal.width = kc.getX() + (int) (kc.getWidth() / 2.5);
            retVal.height = kc.getY();
            break;
        case 30:
            retVal.width = kc.getX();
            retVal.height = kc.getY() + kc.getHeight() / 5;
            break;
        case 31:
            retVal.width = kc.getX();
            retVal.height = kc.getY() + kc.getHeight() / 3;
            break;
        case 32:
            retVal.width = kc.getX();
            retVal.height = kc.getY() - kc.getHeight() / 5;
            break;
        case 33:
            retVal.width = kc.getX();
            retVal.height = kc.getY() - kc.getHeight() / 3;
            break;
        }

        return retVal;
    }

    public final void linkSelected(final Class<? extends Edge> edgeClass, final int direction, final int pid) {
        start_transaction(pid);
        ModelElement lastSelecedElement = getLastSelected().getElement();
        if (direction == BACKWARD) {
            for (ElementContainer ec : selectedContainer) {
                gdcoll.link(edgeClass, ec.getElement(), lastSelecedElement, pid);
            }
        } else {
            for (ElementContainer ec : selectedContainer) {
                gdcoll.link(edgeClass, lastSelecedElement, ec.getElement(), pid);
            }
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param pid
     */
    public final void unlinkSelected(final Class<? extends Edge> edgeClass, final int direction, final int pid) {
        start_transaction(pid);
        ModelElement lastSelecedElement = getLastSelected().getElement();
        if (direction == BACKWARD) {
            for (ElementContainer ec : selectedContainer) {
                gdcoll.unlink(ec.getElement(), lastSelecedElement, edgeClass, pid);
            }
        } else {
            for (ElementContainer ec : selectedContainer) {
                gdcoll.unlink(lastSelecedElement, ec.getElement(), edgeClass, pid);
            }
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * Tauscht die Kanten des Elementes mit dem übergebenen HashString an den beiden
     * übergebenen Indizes. Diese Funktion spielt nur bei Elementen eine Rolle, bei
     * denen die Reihenfolge der Kanten eine Bedeutung hat. Z.B. Prozesse in Bezug auf
     * Aufgaben = Reihenfolge, in der die Aufgaben in dem Prozess ablaufen.
     *
     * @param knothash
     * @param edgeIndex1
     * @param edgeIndex2
     * @param pid
     */
    public final void swapEdgePositions(final String knothash, final String edgeIndex1, final String edgeIndex2, final int pid) {
        ModelElement knot;
        int pos1, pos2;
        try {
            knot = findElementCoded(knothash);
            pos1 = new Integer(edgeIndex1).intValue();
            pos2 = new Integer(edgeIndex2).intValue();
        } catch (Exception e) {
            Log(e);
            return;
        }
        swapEdgePositions(knot, pos1, pos2, pid);
    }

    /**
     * Tauscht die Kanten des übergebenen Elementes an den beiden
     * übergebenen Positionen. Diese Funktion spielt nur bei Elementen eine Rolle, bei
     * denen die Reihenfolge der Kanten eine Bedeutung hat. Z.B. Prozesse in Bezug auf
     * Aufgaben = Reihenfolge, in der die Aufgaben in dem Prozess ablaufen.
     *
     * @param me
     * @param edgeIndex1
     * @param edgeIndex2
     * @param pid
     */
    public final void swapEdgePositions(final ModelElement me, final int edgeIndex1, final int edgeIndex2, final int pid) {
        if (!me.isValidEdgeIndex(edgeIndex1) || me.isValidEdgeIndex(edgeIndex2) || edgeIndex1 == edgeIndex2) {
            return;
        }
        start_transaction(pid);
        if (me.swapEdges(edgeIndex2, edgeIndex2)) {
            addRedoCommand(GDCommands.SWAP_EDGE_POSITIONS + " " + me.getHashString() + " " + edgeIndex1 + " " + edgeIndex2, pid);
            addUndoCommand(GDCommands.SWAP_EDGE_POSITIONS + " " + me.getHashString() + " " + edgeIndex2 + " " + edgeIndex1, pid);
        }
        /*
         * if (knot.isSpecialInfoKnot()){
         * ElementContainer kc = knot.getContainer(this);
         * kc.switchSpecialInfoTartgets(pos1, pos2, kc.isSelected());
         * }
         */
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param mc
     * @param pid
     */
    public final void z_move_up(final ElementContainer mc, final int pid) {
        int ebene = mc.layerFor();
        if (ebene < 0) {
            return;
        }
        GraphDocument doc = mc.getGraphDocument();
        doc.start_transaction(pid);
        addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP + " " + mc.getGraphDocument().hashString + " " + mc.getHashString(), pid);
        addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER + " " + mc.getGraphDocument().hashString + " " + mc.getHashString() + " " + doc.layer[ebene].indexOf(mc), pid);
        doc.layer[ebene].z_move_up(mc);
        doc.finish_transaction(pid);
        distributeEvent(GROUP_ORDER_CHANGED, null, doc.layer[ebene], pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void z_move_up(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            z_move_up(szen.findContainerCoded(elementHash), pid);
        }
    }

    /**
     * @param pid
     */
    public final void z_move_up(final int pid) {
        if (isSelectedAtLeastOneRealNode()) {
            start_transaction(pid);
            for (ElementContainer ec : selectedContainer) {
                if (ec instanceof NodeContainer && !(ec instanceof BendpointContainer)) {
                    z_move_up(ec, pid);
                }
            }
            finish_transaction(pid);
            distributeEvent(GROUP_ORDER_CHANGED, pid);
        }
    }

    /**
     * @param mc
     * @param pid
     */
    private final void z_move_down(final ElementContainer mc, final int pid) {
        int ebene = mc.layerFor();
        if (ebene < 0) {
            return;
        }
        GraphDocument doc = mc.getGraphDocument();
        doc.start_transaction(pid);
        addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN + " " + mc.getGraphDocument().hashString + " " + mc.getHashString(), pid);
        addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER + " " + mc.getGraphDocument().hashString + " " + mc.getHashString() + " " + doc.layer[ebene].indexOf(mc), pid);
        doc.layer[ebene].z_move_down(mc);
        doc.finish_transaction(pid);
        distributeEvent(GROUP_ORDER_CHANGED, null, doc.layer[ebene], pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void z_move_down(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            z_move_down(szen.findContainerCoded(elementHash), pid);
        }
    }

    /**
     * @param pid
     */
    public final void z_move_down(final int pid) {
        if (isSelectedAtLeastOneRealNode()) {
            start_transaction(pid);
            for (ElementContainer ec : selectedContainer) {
                if (ec instanceof NodeContainer && !(ec instanceof BendpointContainer)) {
                    z_move_down(ec, pid);
                }
            }
            finish_transaction(pid);
            distributeEvent(GROUP_ORDER_CHANGED, pid);
        }
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void z_move(final String szenHash, final String elementHash, final int position, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        ElementContainer ec = szen.findContainerCoded(elementHash);
        int ebene = ec.layerFor();
        if (ebene < 0) {
            return;
        }
        szen.start_transaction(pid);
        addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER + " " + ec.getGraphDocument().hashString + " " + ec.getHashString() + " " + position, pid);
        addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER + " " + ec.getGraphDocument().hashString + " " + ec.getHashString() + " " + szen.layer[ebene].indexOf(ec), pid);
        szen.layer[ebene].z_move(ec, position);
        szen.finish_transaction(pid);
        distributeEvent(GROUP_ORDER_CHANGED, null, szen.layer[ebene], pid);
    }

    /**
     * @param ec
     * @param pid
     */
    private final void z_step_up(final ElementContainer ec, final int pid) {
        z_step_up(ec, pid, true);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void z_step_up(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            z_step_up(szen.findContainerCoded(elementHash), pid);
        }
    }

    /**
     * @param ec
     * @param pid
     * @param log
     */
    private final void z_step_up(final ElementContainer ec, final int pid, final boolean log) {
        int ebene = ec.layerFor();
        if (ebene < 0) {
            return;
        }
        GraphDocument doc = ec.getGraphDocument();
        doc.start_transaction(pid, log);
        if (log) {
            addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP + " " + ec.getGraphDocument().hashString + " " + ec.getHashString(), pid);
            addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN + " " + ec.getGraphDocument().hashString + " " + ec.getHashString(), pid);
        }
        doc.layer[ebene].z_step_up(ec);
        doc.finish_transaction(pid, log);
        distributeEvent(GROUP_ORDER_CHANGED, null, doc.layer[ebene], pid);
    }

    /**
     * @param pid
     */
    public final void z_step_up(final int pid) {
        if (isSelectedAtLeastOneRealNode()) {
            start_transaction(pid);
            for (ElementContainer ec : selectedContainer) {
                if (ec instanceof NodeContainer && !(ec instanceof BendpointContainer)) {
                    z_step_up(ec, pid);
                }
            }
            finish_transaction(pid);
            distributeEvent(GROUP_ORDER_CHANGED, pid);
        }
    }

    /**
     * @param ec
     * @param pid
     */
    private final void z_step_down(final ElementContainer ec, final int pid) {
        int ebene = ec.layerFor();
        if (ebene < 0) {
            return;
        }
        GraphDocument doc = ec.getGraphDocument();
        doc.start_transaction(pid);
        addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN + " " + ec.getGraphDocument().hashString + " " + ec.getHashString(), pid);
        addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP + " " + ec.getGraphDocument().hashString + " " + ec.getHashString(), pid);
        doc.layer[ebene].z_step_down(ec);
        doc.finish_transaction(pid);
        distributeEvent(GROUP_ORDER_CHANGED, null, doc.layer[ebene], pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void z_step_down(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            z_step_down(szen.findContainerCoded(elementHash), pid);
        }
    }

    /**
     * @param pid
     */
    public final void z_step_down(final int pid) {
        if (isSelectedAtLeastOneRealNode()) {
            start_transaction(pid);
            for (ElementContainer ec : selectedContainer) {
                if (ec instanceof NodeContainer && !(ec instanceof BendpointContainer)) {
                    z_step_down(ec, pid);
                }
            }
            finish_transaction(pid);
            distributeEvent(GROUP_ORDER_CHANGED, pid);
        }
    }

    /**
     *
     */
    public void showPropertyDialog() {
        ElementContainer ec = getLastSelected();
        if (ec != null) {
            showPropertyDialog(ec.getElement());
        }
    }

    /**
     * @param me
     */
    public void showPropertyDialog(final ModelElement me) {
        me.getPropertyDialog().showDialog();
    }

    /**
     * @param mode
     * @param pid
     */
    public final void label_valign(final int mode, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            label_valign(mode, ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param mode
     * @param kc
     * @param pid
     */
    private final void label_valign(final int mode, final ElementContainer kc, final int pid) {
        if (kc == null) {
            return;
        }
        if (kc.get3LGMLayout() == null) {
            return;
        }

        start_transaction(pid);
        addUndoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN + " " + kc.get3LGMLayout().valign, pid);
        addRedoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN + " " + mode, pid);
        kc.get3LGMLayout().valign = mode;
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, kc, null, pid);
    }

    /**
     * @param mode
     * @param pid
     */
    public final void label_halign(final int mode, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            label_halign(mode, ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param mode
     * @param kc
     * @param pid
     */
    private final void label_halign(final int mode, final ElementContainer kc, final int pid) {
        if (kc == null) {
            return;
        }
        if (kc.get3LGMLayout() == null) {
            return;
        }

        start_transaction(pid);
        addUndoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN + " " + kc.get3LGMLayout().halign, pid);
        addRedoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN + " " + mode, pid);
        kc.get3LGMLayout().halign = mode;
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, kc, null, pid);
    }

    /**
     * @param hashCode
     * @param newName
     * @param pid
     */
    public final void setName(final ModelElement me, final String newName, final int pid) {
        if (me == null) {
            return;
        }
        start_transaction(pid);
        //falls in derselben Transaction der Name mehrfach geändert wird, soll das nur 1 Mal geloggt werden
        addRedoCommandOrReplace(GDCommands.SET_NAME + " " + me.getHashString(), getParseSaveString(newName), pid);
        addUndoCommandIfNotExist(GDCommands.SET_NAME + " " + me.getHashString(), getParseSaveString(me.getName()), pid);
        //	Das hier sollte man nicht einfach ohne Nachfragen machen! Wenn dann nur mit Bestätigungsdialog
        //      Verbundene Elemente die den Namen dieses Elementes in sich tragen auch updaten
        //		List<ModelElement> connected = me.getConnectedElements(ModelElement.class);
        //		for (ModelElement connMe : connected) {
        //			String name = connMe.getName();
        //			if (name.startsWith(me.getName())) {
        //				String nName = newName + name.substring(me.getName().length(), name.length());
        //				setName(connMe, nName, pid);
        //			}
        //		}

        me.setName(getDecodedParseSaveString(newName));
        //irgendein Container dieses Elementes muss ins Event gapackt werden. Welcher ist egal, da eigentlich das Element selsbt wichtig wäre
        ElementContainer ec = me.getContainer(this);
        finish_transaction(pid);
        distributeEvent(ELEMENT_NAME_CHANGED, ec, null, pid);
    }

    /**
     * @param hashString
     * @param newDescr
     * @param pid
     */
    public final void setDescription(final ModelElement me, final String newDescr, final int pid) {
        if (me == null) {
            return;
        }

        start_transaction(pid);
        addRedoCommandOrReplace(GDCommands.SET_DESCRIPTION + " " + me.getHashString(), getParseSaveString(newDescr), pid);
        addUndoCommandIfNotExist(GDCommands.SET_DESCRIPTION + " " + me.getHashString(), getParseSaveString(me.getDescription()), pid);
        me.setDescription(getDecodedParseSaveString(newDescr));
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param elementHash
     * @param userFieldHash
     * @param newValue
     * @param pid
     */
    public final void setUserFieldValue(final String elementHash, final String userFieldHash, final String newValue, final int pid) {
        GraphDocument mainDoc = gdcoll.getMainGraphDocument();
        ModelElement me = mainDoc.findElementCoded(elementHash);
        if (me == null) {
            return;
        }
        UserFieldDefinitions userFieldDefinitions = gdcoll.getUserFieldDefinitions();
        UserField userField = userFieldDefinitions.getUserField(userFieldHash);
        setUserFieldValue(me, userField, newValue, pid);
    }

    /**
     * Setzt den Wert von <code>uf</code> für <code>me</code> auf <code>newValue</code>.
     * Fügt UNDO- und REDO-Commands hinzu
     *
     * @param me
     * @param userField
     * @param newValue
     * @param pid
     */
    public void setUserFieldValue(final ModelElement me, final UserField userField, final String newValue, final int pid) {
        start_transaction(pid);
        addRedoCommandOrReplace(GDCommands.SET_USER_FIELD_VALUE + " " + me.getHashString() + " " + userField.getHashCode(), getParseSaveString(newValue, true), pid);
        addUndoCommandIfNotExist(GDCommands.SET_USER_FIELD_VALUE + " " + me.getHashString() + " " + userField.getHashCode(), getParseSaveString(userField.getValue(me), true), pid);
        me.setUserFieldInputValue(userField, getDecodedParseSaveString(newValue));
        finish_transaction(pid);
        distributeEvent(USER_FIELD_VALUE_CHANGED, pid);
    }

    /**
     * Setzt für ein ModelElement für ein UserFieldReplacmentWeigth
     * Fügt UNDO- und REDO-Commands hinzu
     * Fall 1: Ersetzung für Gleichverteilung:
     * a.) durch ein anderes Kantengewicht ersetzen -> edgeClass, userFieldReplacementHash
     * b.) löschen -> edgeClass, "" oder emptyArgumentString
     * Fall 2: Ersetzung für Kantengewicht:
     * a.) durch Gleichverteilung ersetzen -> userFieldHashToReplace, "" oder emptyArgumentString
     * b.) durch ein anderes Kantengewicht ersetzen -> userFieldHashToReplace, userFieldReplacementHash
     * c.) löschen -> userFieldHashToReplace, userFieldHashToReplace
     *
     * @param modelElementHash
     * @param userFieldHashToReplaceOrSimpleEdgeClassName
     *            Das hier ist entweder der Hash eines UserFields oder der SimpleClassName einer Kantenklasse. Wird ein Klassenname übergeben, dann
     *            wird beim Replacer der Ersetzungshash für die Gleichverteilung der Edge eingetragen, ansonsten wird der Erstzungshash für das
     *            UserField mit dem angegebenen Hash eingetragen.
     * @param userFieldHashReplacement
     * @param pid
     */
    public void setUserFieldWeightReplacement(final String modelElementHash, final String userFieldHashToReplaceOrSimpleEdgeClassName, final String userFieldHashReplacement, final int pid) {
        String emptyArgument = "null";
        UserFieldDefinitions definitions = getUserFieldDefinitions();
        WeightReplacer replacer = definitions.getWeightReplacer();
        //wurde eine Kantenklasse übergeben?
        Class<? extends ModelElement> edgeElementClass = ModelConstants.getClassForName(userFieldHashToReplaceOrSimpleEdgeClassName);
        //falls ein null oder Leerwert als Ersetzung übergeben wurde, muss der hier in EMPTY_STRING ersetzt werden, damit die
        //Kommandos mit der richtigen Parameteranzahl geparst werden könnnen
        String hashReplacement = Strings.isNullOrEmpty(userFieldHashReplacement) ? emptyArgument : userFieldHashReplacement;
        String oldUserFieldHashReplacement;
        //je nachdem ob die Kantenklasse gefuden wurde oder nicht, wird aus dem Replacer der alte Erstzungshas geladen
        if (edgeElementClass != null) { //Fall 1
            Class<? extends Edge> edgeClass = edgeElementClass.asSubclass(Edge.class);
            oldUserFieldHashReplacement = replacer.getUniformDistributionReplacement(modelElementHash, edgeClass);
            if (emptyArgument.equals(hashReplacement)) { // Fall 1b.)
                replacer.removeUniformDistributionReplacement(modelElementHash, edgeClass);
            } else { //Fall 1a.)
                replacer.setUniformDistributionReplacement(modelElementHash, edgeClass, hashReplacement);
            }
        } else { //Fall 2
            oldUserFieldHashReplacement = replacer.getReplacement(modelElementHash, userFieldHashToReplaceOrSimpleEdgeClassName);
            if (emptyArgument.equals(hashReplacement)) { // Fall 2a.)
                replacer.setUniformDistribution(modelElementHash, userFieldHashToReplaceOrSimpleEdgeClassName);
            } else if (hashReplacement.equals(userFieldHashToReplaceOrSimpleEdgeClassName)) { //Fall 2c.)
                replacer.removeReplacement(modelElementHash, userFieldHashToReplaceOrSimpleEdgeClassName);
            } else { //Fall 2b.)
                replacer.setReplacement(modelElementHash, userFieldHashToReplaceOrSimpleEdgeClassName, hashReplacement);
            }
        }

        //wenn es keinen alten Wert gab, wird er auch auf emptyArgument gesetzt, damit er in den UNDO_REDO-Commands richtig geparst werden kann
        oldUserFieldHashReplacement = Strings.isNullOrEmpty(oldUserFieldHashReplacement) ? emptyArgument : oldUserFieldHashReplacement;

        //UNDO und REDO Commands schreiben
        start_transaction(pid);
        addRedoCommandOrReplace(GDCommands.SET_USER_FIELD_WEIGHT_REPLACEMENT + " " + modelElementHash + " " + userFieldHashToReplaceOrSimpleEdgeClassName, hashReplacement, pid);
        addUndoCommandIfNotExist(GDCommands.SET_USER_FIELD_WEIGHT_REPLACEMENT + " " + modelElementHash + " " + userFieldHashToReplaceOrSimpleEdgeClassName, oldUserFieldHashReplacement, pid);
        finish_transaction(pid);

    }

    /**
     * @param removeAllSpecialInfos
     */
    public final void clearHightLighted(final boolean removeAllSpecialInfos) {
        if (removeAllSpecialInfos) {
            for (LayerContainer lc : layer) {
                for (ElementContainer ec : lc.getKnoten()) {
                    ec.removeAllSpecialInfosFromThisContainer();
                }
                for (ElementContainer ec : lc.getKanten()) {
                    ec.removeAllSpecialInfosFromThisContainer();
                }
            }
        }
    }

    /**
     * @param elements
     * @param pid
     */
    public final void addContainerToNewSzenario(final List<ElementContainer> elements, final int pid) {
        start_transaction(pid);
        Szenario szen = gdcoll.createSzenario(pid);
        if (szen == null) {
            finish_transaction(pid);
            return;
        }
        Static.getTool().createSzenarioFrame(szen);
        szen.setPageSizeFactor(pageSizeFactor);
        szen.getMapping().adapt(getMapping());
        szen.getFrame().getInputGraphArea().adaptSettings(frame.getInputGraphArea());
        addElementsToSzenario(szen.getHashString(), elements, pid);
        Static.getTool().activeLayerChanged(szen);
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param elements
     * @param pid
     */
    public final void addContainerToAllSzenarios(final List<ElementContainer> elements, final int pid) {
        start_transaction(pid);
        for (Szenario szen : gdcoll.getSzenarios()) {
            if (szen == this) {
                continue;
            }
            start_transaction(pid, false);
            for (ElementContainer ec : elements) {
                ModelElement me = ec.getElement();
                Class<? extends ModelElement> elementClass = me.getClass();
                if (!(me instanceof Node) || szen.isMyElement(me) || ModelConstants.isUnique(elementClass)) {
                    continue;
                }
                if (ModelConstants.isSlaveType(elementClass)) {
                    continue;
                }
                addElementToSzenario(szen.getHashString(), (NodeContainer) ec, pid);
            }
            finish_transaction(pid, false);
            distributeEvent(SELECTION_CHANGED, pid);
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * COMMENTME
     *
     * @param szenHashString
     * @param elements
     * @param pid
     */
    public final void addElementsToSzenario(final String szenHashString, final List<ElementContainer> elements, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : elements) {
            if (ec instanceof NodeContainer) {
                addElementToSzenario(szenHashString, (NodeContainer) ec, pid);
            }
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param pid
     */
    public final void linkElementsToNewSzenario(final Collection<ElementContainer> elements, final int pid) {
        start_transaction(pid);
        Szenario szen = gdcoll.createSzenario(pid);
        if (szen == null) {
            finish_transaction(pid);
            return;
        }
        Static.getTool().createSzenarioFrame(szen);
        for (ElementContainer ec : elements) {
            linkElementToSzenario(szen.getHashString(), ec.getHashString(), pid);
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param szenHashString
     * @param pid
     */
    public final void linkElementsToSzenario(final String szenHashString, final Collection<ElementContainer> elements, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : elements) {
            linkElementToSzenario(szenHashString, ec.getHashString(), pid);
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param szenHashString
     * @param hashCode
     * @param pid
     */
    private final void linkElementToSzenario(final String szenHashString, final String hashCode, final int pid) {
        ElementContainer ec = findContainerCoded(hashCode);
        if (ec == null) {
            ec = gdcoll.getMainGraphDocument().findContainerCoded(hashCode);
        }
        linkElementToSzenario(szenHashString, ec, pid);
    }

    /**
     * @param szenHashString
     * @param hashCode
     * @param createTraces
     * @param pid
     */
    private final ElementContainer addElementToSzenario(final String sourceDocHash, final String targetSzenHash, final String elementHashCode, final int pid) {
        GraphDocument sourceDoc = sourceDocHash == null ? gdcoll.getMainGraphDocument() : gdcoll.getGraphDocumentCoded(sourceDocHash);
        NodeContainer ec = sourceDoc.findNodeContainerCoded(elementHashCode);
        if (ec != null) {
            return addElementToSzenario(targetSzenHash, ec, pid);
        }
        return null;
    }

    /**
     * @param szenHash
     * @param ec
     * @param pid
     */
    protected final NodeContainer addElementToSzenario(final String szenHash, final NodeContainer ec, final int pid) {
        if (ec == null) {
            return null;
        }
        ModelElement me = ec.getElement();
        if (me instanceof Knickpunkt || me.isUnique()) {
            return null;
        }

        GraphDocument szenario = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szenario instanceof Szenario)) {
            return null;
        }
        Szenario szen = (Szenario) szenario;

        szen.start_transaction(pid);

        NodeContainer nc = (NodeContainer) szen.addContainerCopy(ec);
        if (nc != null) {
            if (nc != ec) {
                szen.addUndoCommand(GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL + " " + szenHash + " " + ec.getHashString(), pid);
                //Argumente: 1.) Quell-GraphDoc 2.) Zielszenario 3.) Hash des Elementes
                szen.addRedoCommand(GDCommands.ADD_ELEMENT_TO_SZENARIO + " " + ec.getGraphDocument().getHashString() + " " + szenHash + " " + ec.getHashString(), pid);
                szen.createEdgeContainer(nc, ec.getGraphDocument(), true, pid);
            }
            szen.addToSelection(nc, pid);
            szen.raiseSlaves(nc);
        }
        szen.finish_transaction(pid);
        szen.distributeEvent(DATA_CHANGED, pid);
        return nc;
    }

    /**
     * @param szenHashString
     * @param ec
     * @param pid
     */
    public final void linkElementToSzenario(final String szenHashString, final ElementContainer ec, final int pid) {

        if (ec.getElement() instanceof Edge) {
            return;
        }
        start_transaction(pid);
        String oldSzen = ((NodeContainer) ec).getKnoten().getAssociatedDoc();
        ((NodeContainer) ec).getKnoten().setAssociatedDoc(szenHashString.equals("null") ? null : szenHashString);

        String oldSzenHash = oldSzen == null ? "null" : oldSzen;
        addUndoCommand(GDCommands.LINK_ELEMENT_TO_SZENARIO + " " + oldSzenHash + " " + ec.getHashString(), pid);
        addRedoCommand(GDCommands.LINK_ELEMENT_TO_SZENARIO + " " + szenHashString + " " + ec.getHashString(), pid);
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, ec, null, 0);
    }

    /**
     * @param kc
     * @param pid
     * @param log
     */
    public void moveDependentKnotsUp(final NodeContainer kc, final int pid, final boolean log) {
        if (!isMyElement(kc)) {
            return;
        }
        start_transaction(pid, log);
        for (Class<? extends ModelElement> c : ModelConstants.getCopyDependencies(kc.getKnoten().getClass())) {
            List<ElementContainer> dependentObjects = kc.getKnoten().getConnectedContainer(c, this);
            for (int j = 0; j < dependentObjects.size(); j++) {
                NodeContainer sc = (NodeContainer) dependentObjects.get(j);
                Node sk = sc.getKnoten();
                if (!isMyElement(sk)) {
                    continue;
                }
                LayerContainer lc1 = getLayer(kc.getKnoten().layerFor());
                LayerContainer lc2 = getLayer(sk.layerFor());
                if (lc1 == lc2) {
                    while (lc1.indexOf(sc) < lc1.indexOf(kc)) {
                        z_step_up(sc, pid, log);
                    }
                }
            }
        }
        finish_transaction(pid, log);
    }

    /**
     * @param pid
     */
    public final void joinSelected(final int pid) {
        if (selectedContainer.size() > 1) {
            String targetHash = getLastSelected().getHashString();
            List<ElementContainer> selection = new ArrayList<>(selectedContainer);
            for (ElementContainer ec : new ArrayList<>(selection)) {
                joinElements(ec.getHashString(), targetHash, pid);
            }
        }
    }

    /**
     * 2 Elemente vereinen,
     *
     * @param hashString1
     *            das abschliessend zu löschende Element
     * @param hashString2
     *            gibt das verbleibende Element an
     * @param pid
     */
    public final boolean joinElements(final String hashString1, final String hashString2, final int pid) {
        if (!gdcoll.join(hashString1, hashString2, null, pid)) {
            return false;
        }
        ElementContainer kc = findNodeContainerCoded(hashString2);
        if (kc != null && kc instanceof NodeContainer) {
            for (Edge edge : kc.getElement().getEdges()) {
                EdgeContainer kac = (EdgeContainer) edge.getContainer(this);
                if (kac == null) {
                    continue;
                }
                kac.computeBorderPoints();
            }
        }
        return true;
    }

    /**
     * @return
     */
    public final GDCollection getCollection() {
        return gdcoll;
    }

    /**
     * Liefert die {@link UserFieldDefinitions} der {@link GDCollection}
     *
     * @return
     */
    public UserFieldDefinitions getUserFieldDefinitions() {
        return gdcoll.getUserFieldDefinitions();
    }

    /**
     * @return
     */
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }

    ////////////////////////////

    /**
     * Gibt alle ElementContainer zurück, deren gekapseltes Modellelement zuweisungskompatibel
     * zur übergebenen Klasse ist.<br>
     *
     * @param clazz Klasse, die der ModelElement-Klasse der Container entspricht
     * @return Liste mit ElementContainer oder <code>null</code>
     */
    public final List<ElementContainer> getElementContainer(final Class<? extends ModelElement> clazz) {
        return getElementContainer(clazz, true);
    }

    /**
     * Gibt alle ElementContainer zurück, deren gekapseltes Modellelement von
     * der übergebenen Klasse ist.<br>
     * Unterklassen werden nicht beachtet.
     *
     * @param clazz Klasse, die der ModelElement-Klasse der Container entspricht
     * @param subClassElements wenn <code>true</code>, werden auch Container mit Elementen von Unterklasse zurück gegeben
     * @return Liste mit ElementContainer oder <code>null</code>
     */
    public final List<ElementContainer> getElementContainer(final Class<? extends ModelElement> clazz, final boolean subClassElements) {
        return getElementContainer(clazz, subClassElements, false);
    }

    /**
     * Gibt alle eine nach der <code>toString()</code>-Methode der ElementContainer
     * sortierte Liste von ElementContainern zurück, deren gekapseltes Modellelement
     * von der übergebenen Klasse ist.<br>
     *
     * @param clazz
     * @param includeSubClasseswenn <code>true</code> wird ist die Rückgabeliste alphabetisch
     *            sortiert (das betrifft nur die KnotenContainer, aber nicht die KantenContainer)
     * @param alphabetical
     * @return
     */
    public final List<ElementContainer> getElementContainer(final Class<? extends ModelElement> clazz, final boolean includeSubClasses, final boolean alphabetical) {

        //		long start = System.currentTimeMillis();

        GraphDocument document = ModelConstants.isUnique(clazz) ? getCollection().getMainGraphDocument() : this;
        List<ElementContainer> objects = new ArrayList<>();
        //Ebene der gesuchten Elementklasse bestimmen
        int layer = ModelConstants.layerFor(clazz);
        //Indizes der zu durchsuchenden Ebenen
        int minLayer = MIN_LAYER_INDEX;
        int maxLayer = MAX_LAYER_INDEX;
        //wenn die Elementklasse auf genau einer Ebene zu Hause ist
        if (layer != NO_LAYER) {
            //Indizes der Ebenen so anpassen, dass nur die Ebene der Elementklasse durchsucht wird
            minLayer = layer;
            maxLayer = layer;
        }
        //alle zu durchsuchenden Ebenen durchlaufen
        for (int i = minLayer; i <= maxLayer; i++) {
            //Ebene holen
            LayerContainer lc = document.getLayer(i);
            //Liste mit allen Containerlisten der Ebene, die durchsucht werden müssen
            List<List<? extends ElementContainer>> layerElements = new ArrayList<>();
            //Knickpunkte
            if (clazz == Knickpunkt.class) {
                layerElements.add(lc.getKnickpunkte());
            } else if (ModelConstants.isNodeType(clazz)) {
                layerElements.add(alphabetical ? lc.getKnotenAlphabetical() : lc.getKnoten());
                //Kanten
            } else if (ModelConstants.isEdgeType(clazz)) {
                layerElements.add(lc.getKanten());
            }

            //wenn alle Elemente gesucht werden sollen
            if (clazz == ModelElement.class) {
                //wenn keine Unterklassen zu suchen sind, ist man hier fertig
                if (!includeSubClasses) {
                    return objects;
                }
                //alle Elemente sind Unterklassen von ModelElement -> alle Containerlisten können zur Rückgabeliste hinzugefügt werden
                objects.addAll(lc.getKnickpunkte());
                objects.addAll(alphabetical ? lc.getKnotenAlphabetical() : lc.getKnoten());
                objects.addAll(lc.getKanten());
                //wenn eine Unterklasse von ModelElement gesucht werden soll
            } else {
                //dann wurde oben in layerElements wenigstens eine ElementContainerliste hinzugefügt
                for (List<? extends ElementContainer> ecList : layerElements) {
                    //für jede dieser ElementContainerlisten
                    for (ElementContainer ec : ecList) {
                        //wenn das ModelElement des Conatainers der gesuchten Klasse entspricht
                        Class<?> elementClass = ec.getElement().getClass();
                        if (includeSubClasses ? clazz.isAssignableFrom(elementClass) : clazz == elementClass) {
                            //zur Rückgabeliste hinzufügen
                            objects.add(ec);
                        }
                    }
                }
            }
        }

        //wenn alphabetisch sortiert werden soll und andere Elemente als die bereits in der aplhabetisch sortierten
        //Knotenliste enthaltenen zur Rückgabeliste hinzugefügt wurden
        if (alphabetical && (clazz == Knickpunkt.class || !ModelConstants.isNodeType(clazz))) {
            //aplhabetisch sortieren
            Alphabetical.sort(objects);
        }

        //		long end = System.currentTimeMillis();
        //		System.err.println("getElementContainer(" + clazz.getSimpleName() + ", " + includeSubClasses + ", " + alphabetical + ") -> " + (end - start) + " ms " + objects.size() + " Elemente");

        return objects;
    }

    /**
     * Gibt alle Modellelemente (Node oder Kanten) zurück.<br/>
     * Es werden nur Instanzen genau dieser Klasse zurück gegeben.
     *
     * @param clazz Klasse der gesuchten Elementart (Node oder Kanten)
     * @return ArrayList mit allen gefundenen Elementen
     */
    public final List<ModelElement> getModelItems(final Class<? extends ModelElement> clazz) {
        return getModelItems(clazz, false);
    }

    /**
     * Gibt alle Modellelemente (Node oder Kanten) zurück.<br/>
     *
     * @param clazz Klasse der gesuchten Elementart (Node oder Kanten)
     * @param includeSubClasses boolean with true if Vererbung beruecksichtigen; Frage nach allen Anwendungsbausteinen gibt auch
     *            RechAnwendungsbausteine und KonAnwendungsbausteine zurück usw.
     * @return ArrayList mit allen gefundenen Elementen
     */
    public final List<ModelElement> getModelItems(final Class<? extends ModelElement> clazz, final boolean includeSubClasses) {
        return getModelItems(clazz, includeSubClasses, false);
    }

    /**
     * Gibt alle Modellelemente (Node oder Kanten) zurück.<br/>
     *
     * @param clazz Klasse der gesuchten Elementart (Node oder Kanten)
     * @param includeSubClasses
     *            boolean with true if Vererbung beruecksichtigen; Frage nach allen Anwendungsbausteinen gibt
     *            auch RechAnwendungsbausteine und KonAnwendungsbausteine zurück usw.
     * @param alphabetical
     *            wenn <code>true</code> wird eine alphabetisch sortierte Liste zurückgegeben
     * @return ArrayList mit allen gefundenen Elementen
     */
    public final List<ModelElement> getModelItems(final Class<? extends ModelElement> clazz, final boolean includeSubClasses, final boolean alphabetical) {
        return getModelItems(clazz, includeSubClasses, false, alphabetical);
    }

    /**
     * Gibt alle Modellelemente (Node oder Kanten) zurück.<br/>
     *
     * @param clazz Klasse der gesuchten Elementart (Node oder Kanten)
     * @param includeSubClasses
     *            boolean with true if Vererbung beruecksichtigen; Frage nach allen Anwendungsbausteinen gibt
     *            auch RechAnwendungsbausteine und KonAnwendungsbausteine zurück usw.
     * @param absolutePartsOnly
     *            wenn <code>true</code> werden keine Elemente zurückgegeben, denen über eine Part-Of-Beziehung
     *            Teilelemente zugewiesen sind. Die Teil-Von-Eigenschaft wird nicht für dieses Teilmodell sondern
     *            für das Gesamtmodell geprüft.
     * @param alphabetical
     *            wenn <code>true</code> wird eine alphabetisch sortierte Liste zurückgegeben
     * @return List mit allen gefundenen Elementen
     */
    public final List<ModelElement> getModelItems(final Class<? extends ModelElement> clazz, final boolean includeSubClasses, final boolean absolutePartsOnly, final boolean alphabetical) {
        return GraphDocumentHandler.getModelItems(this, clazz, includeSubClasses, absolutePartsOnly, alphabetical);
    }

    /**
     * Liefert den Container in diesem GraphDocument für ein übergebenes ModellElement.
     *
     * @param modelElement
     * @return
     */
    public ElementContainer getElementContainer(final ModelElement modelElement) {
        if (modelElement == null) {
            return null;
        }
        return modelElement.getContainer(modelElement.isUnique() ? getCollection().getMainGraphDocument() : this);
    }

    /**
     * Liefert den Container in diesem GraphDocument für einen übergebenen anderen Container
     * aus einem beliebigen GraphDocument.
     *
     * @param modelElement
     * @return
     */
    public ElementContainer getElementContainer(final ElementContainer elementContainer) {
        if (elementContainer == null) {
            return null;
        }
        return getElementContainer(elementContainer.getElement());
    }

    /**
     * Liefert alle Container in diesem GraphDocument für eine Liste von <code>ModellElement</code>s oder
     * von anderen Containern aus einem beliebigen GraphDocument.
     *
     * @param modelElementOrContainerList
     * @return
     */
    public List<ElementContainer> getElementContainer(final Collection<?> modelElementOrContainerList) {
        if (modelElementOrContainerList == null) {
            return null;
        }
        List<ElementContainer> returnList = new ArrayList<>();
        for (Object o : modelElementOrContainerList) {
            ElementContainer ec = o instanceof ElementContainer ? getElementContainer((ElementContainer) o) : getElementContainer((ModelElement) o);
            if (ec == null) {
                continue;
            }
            returnList.add(ec);
        }
        return returnList;
    }

    ////////////////////////////

    /**
     *
     */
    public void sortKanten() {
        for (int i = 0; i < layer.length; i++) {
            layer[i].sortKanten();
        }
    }

    /**
     *
     */
    public void initKnotContainers() {
        for (int i = 0; i < layer.length; i++) {
            for (NodeContainer kc : layer[i].getKnoten()) {
                if (kc != null) {
                    kc.refreshText();
                    Font f = kc.getFont();
                    if (!kc.isStandardFont(f)) {
                        kc.setFont(f);
                    }
                }
            }
            layer[i].revalidate();
            layer[i].repaint();
        }
    }

    /**
     *
     */
    public void initTraceContainers() {
        for (int i = 0; i < layer.length; i++) {
            for (BendpointContainer kpC : layer[i].getKnickpunkte()) {
                if (kpC == null) {
                    continue;
                }
                Knickpunkt kp = kpC.getKnickpunktKnoten();
                if (kp == null) {
                    continue;
                }
                EdgeContainer kc = layer[i].getEdgeContainer(kp.getKantenHash());
                if (kc == null) {
                    continue;
                }
                kc.setKnickpunkt(kpC, kp.getIndex());
                kp.addEdge(kc.getEdge());
            }
            for (EdgeContainer kc : layer[i].getKanten()) {
                if (kc != null) {
                    kc.computeBorderPoints();
                }
            }
            layer[i].revalidate();
            layer[i].repaint();
        }
    }

    //	/**
    //	 *
    //	 * /
    //	public void refreshSpecialInfoTargets () {
    //		for (int i = 0; i < layer.length; i++) {
    //			for (NodeContainer kc : layer[i].getKnoten()) {
    //				if (kc != null)
    //					kc.dataChanged(this);
    //			}
    //		}
    //	}

    /**
     * @return
     */
    public ElementDialogPanel getLastActivePanel() {
        return lastActivePanel;
    }

    /**
     * @param panel
     */
    public void setLastActivePanel(final ElementDialogPanel panel) {
        lastActivePanel = panel;
    }

    /**
     * @param newTitle
     */
    public void setTitle(final String newTitle) {
        title = newTitle == null ? "" : newTitle;
    }

    /**
     * @return
     */
    public int getNext_x_pos() {
        return next_x_pos;
    }

    /**
     * @return
     */
    public int getNext_y_pos() {
        return next_y_pos;
    }

    /**
     * @param _frame
     */
    public void setFrame(final InternalGraphFrame _frame) {
        if (_frame == null) {
            frame = _frame;
        } else if (_frame.getGraphDocument().equals(this)) {
            frame = _frame;
        }
    }

    /**
     * @return
     */
    public InternalGraphFrame getFrame() {
        return frame;
    }

    /**
     * Liefert den übergeben String eingerahmt in einfache Anführungszeichen ('') sowie
     * kodierten Backslashes. Dies ist der Elementname, der in alle Undo-Redo-Kommandos
     * benutzt werden sollte.
     *
     * @param s
     * @return
     */
    public static String getParseSaveString(final String s, final boolean trim) {
        if (s == null) {
            StringBuilder sb = new StringBuilder(2);
            sb.append(GDCOMMAND_TEXT_SURROUNDER);
            sb.append(GDCOMMAND_TEXT_SURROUNDER);
            return sb.toString();
        }
        String ss = trim ? s.trim() : s;
        return GDCOMMAND_TEXT_SURROUNDER + ss.replace("\r", "").replace('\n', '\u001e').replace(GDCOMMAND_TEXT_SURROUNDER, '´').replaceAll("\\\\", "\\\\\\\\") + GDCOMMAND_TEXT_SURROUNDER;
    }

    /**
     * Liefert den übergeben String eingerahmt in einfache Anführungszeichen ('') sowie
     * kodierten Backslashes. Dies ist der Elementname, der in alle Undo-Redo-Kommandos
     * benutzt werden sollte.
     *
     * @param s
     * @return
     */
    public static String getParseSaveString(final String s) {
        return getParseSaveString(s, false);
    }

    /**
     * Liefert einen übergebenen String, in dem die Transformationen der Zeilenumbrüche durch die Funktion <code>getParseSaveString(String s)</code>
     * wieder rückgängig gemacht werden.
     *
     * @param s
     * @return
     */
    public static String getDecodedParseSaveString(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (s.charAt(0) == GDCOMMAND_TEXT_SURROUNDER && s.charAt(s.length() - 1) == GDCOMMAND_TEXT_SURROUNDER) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace('\u001e', '\n');
    }

}
