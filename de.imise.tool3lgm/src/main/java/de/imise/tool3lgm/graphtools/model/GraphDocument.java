package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LAYER_COUNT;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.NO_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_ADDICT;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_POSITION;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_ACTION_SWAP_EDGE_POSITIONS;
import static de.imise.tool3lgm.graphtools.model.GDCommands.MODEL_OPTION_GDOC_VERIFICATION_MODE;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ACTIVE_LAYER_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ELEMENT_GRAPHICS_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ELEMENT_NAME_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.GROUP_ORDER_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.USER_FIELD_VALUE_CHANGED;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_GRAPH_MOVE_SUBELEMENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_REMOVE_WARNING;
import static de.imise.util.htmlxml.ParseSaveStringHandler.getDecodedParseSaveString;
import static de.imise.util.htmlxml.ParseSaveStringHandler.getParseSaveString;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.event.action.UserPropertyBooleanChangeAction;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.tools.EasyDialogAccess;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.LayerNode;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType;
import de.imise.tool3lgm.graphtools.path.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.CommandParser;
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
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.TextAlignmentHTML;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.TextPositionHorizontal;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.TextPositionVertical;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.gui.MainFrame;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Alphabetical;
import de.imise.util.OptionsSupport;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.htmlxml.ParseSaveStringHandler;
import de.imise.util.swing.dialog.ImageChooser;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Repräsentiert ein Teilmodell. Dieses Teilmodell kann das Hauptmodell sein (= spezielle Teilmodell das alle Elemente enthält, aber keine Grafik
 * besitzt) oder ein Szenario (= eine beliebige Elementauswahl aus allen Elementen mit einer grafischen Repräsentation)
 */
public abstract class GraphDocument extends ElementSelectionContext implements GDCollectionOwner {

    /**
     *
     */
    public static final char GENERATED_NAME_PREFIX = 27; //ESCAPE

    /**
     *
     */
    public final OptionsSupport optionsSupport = new OptionsSupport();

    /**
     * COMMENTME
     */
    protected LayerContainer[] layer;

    /**
     * Contains all {@link ElementContainer} which are a result of a analysis.
     */
    private final List<ElementContainer> analysisResult;

    /**
     * Manages the additional highlight for the current selection.
     */
    private final SelectionHighlighter selectionHighlighter;

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
    public final static int INITIAL_PAGE_HEIGHT = 768;

    /**
     * COMMENTME
     */
    public final static int INITIAL_PAGE_WIDTH = 1024;

    /**
     *
     */
    public static final int MAX_PAGE_SIZE_FACTOR = 5;

    /**
     * X-position of a new created element
     */
    private int next_x_pos = 0;

    /**
     * Y-position of a new created element
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
     * COMMENTME
     */
    protected File process_file;

    /**
     * COMMENTME
     */
    protected final GDCollection gdcoll;

    /**
     * COMMENTME
     */
    protected NodeContainer lastCreated = null;

    /**
     * @param _gdcoll
     */
    protected GraphDocument(@Nonnull final GDCollection _gdcoll) {
        super(_gdcoll.getMetaModel());
        gdcoll = _gdcoll;
        MetaModel metaModel = gdcoll.getMetaModel();
        Date dateNow = new Date();
        long timeNow = dateNow.getTime();
        hashString = "DOC" + "_" + timeNow;

        analysisResult = new ArrayList<>();
        selectionHighlighter = new SelectionHighlighter(this);
        GraphViewDefinition graphViewDefinition = metaModel.getGraphViewDefinition();
        ElementsLayoutDefinition defaultElementsLayout = graphViewDefinition.getDefaultElementsLayout();
        mapping = new ElementsLayoutDefinition(defaultElementsLayout);

        layer = new LayerContainer[LAYER_COUNT];
        for (int c = 0; c < layer.length; c++) {
            LayerNode layerNode = new LayerNode(metaModel, c);
            layer[c] = new LayerContainer(layerNode, this, c);
            layer[c].setColor(Color.white);
        }
        setPageSizeFactor(1.0);
    }

    // Verwaltung globaler Modelldaten --- Anfang ---

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (gdcoll == null ? 0 : gdcoll.hashCode());
        result = prime * result + (hashString == null ? 0 : hashString.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GraphDocument other = (GraphDocument) obj;
        if (gdcoll == null) {
            if (other.gdcoll != null) {
                return false;
            }
        } else if (!gdcoll.equals(other.gdcoll)) {
            return false;
        }
        if (hashString == null) {
            if (other.hashString != null) {
                return false;
            }
        } else if (!hashString.equals(other.hashString)) {
            return false;
        }
        return true;
    }

    /**
     * @return the model category of the {@link GDCollection}
     */
    public ModelCategory getModelCategory() {
        return gdcoll.getModelCategory();
    }

    /**
     * @return <code>true</code> if this Graphdocument is the main GraphDocument of the model, otherwise <code>false</code>
     */
    public final boolean isMainGraphDocument() {
        GraphDocument mainDoc = getMainDoc();
        return mainDoc == this;
    }

    /**
     * Liefert den Zoom-Faktor
     *
     * @return
     *         <code>double</code>-Wert des Zoomfaktors
     */
    public double getPageSizeFactor() {
        return (double) page_height / (double) INITIAL_PAGE_HEIGHT;
    }

    /**
     * Setzt den pageSizeFactor auf den übergebenen Wert ohne es UndoRedo-mäßig zu loggen. Ist dieser Wert kleiner als gebraucht wird, um alle
     * Elemente in der Ebene darzustellen, dann wird der minimale Wert gesetzt, den man unbedingt braucht.
     *
     * @param newPageSizeFactor
     */
    public void setPageSizeFactor(final double newPageSizeFactor) {
        setPageSizeFactor(newPageSizeFactor, false, TransactionManager.STANDARD_PID);
    }

    /**
     * @param pageSizeFactor
     * @param logUndoRedo
     * @param pid
     */
    private void setPageSizeFactor(double newPageSizeFactor, final boolean logUndoRedo, final int pid) {
        final double oldPageSizeFactor = getPageSizeFactor();
        double minPageSizeFactor = getMinimalPageSizeFactor();
        if (minPageSizeFactor > newPageSizeFactor) {
            newPageSizeFactor = minPageSizeFactor;
        }
        double correctWidth = newPageSizeFactor * INITIAL_PAGE_WIDTH;
        double correctHeight = newPageSizeFactor * INITIAL_PAGE_HEIGHT;
        page_width = (int) correctWidth;
        page_height = (int) correctHeight;
        //wegen der Rundungsfehler einfach die Ebene immer mind. 1 Pixel größer setzen
        page_width = correctWidth > page_width ? page_width + 1 : page_width;
        page_height = correctHeight > page_height ? page_height + 1 : page_height;
        //wenn der Wert sich geändert hat, dann die UNDO/REDO-Kommandos hinzufügen
        if (logUndoRedo) {
            start_transaction(pid);
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_SIZE_FACTOR + " " + hashString, oldPageSizeFactor, pid);
            addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_LAYER_SIZE_FACTOR + " " + hashString, newPageSizeFactor, pid);
            finish_transaction(pid);
        }
        distributeEvent(LGMChangeType.LAYOUT_CHANGED);
    }

    /**
     * Prüft, ob die übergebene Komponente in die Ebene passt
     *
     * @param c
     * @return <code>true</code>, wenn die übergebene Komponente in die Ebene passt, sonst <code>false</code>
     */
    public boolean pageHasSize(final Component c) {
        double neededPageSizeFactor = getMinimalPageSizeFactor(c);
        //System.err.println("pageSizeFactor=" + getPageSizeFactor() + "   neededPageSizeFactor=" + neededPageSizeFactor);
        double pageSizeFactor = getPageSizeFactor();
        return neededPageSizeFactor <= pageSizeFactor;
    }

    /**
     * Liefert den minimalen PageSizeFacor, den man braucht damit eine Komponene in die Ebene passt. Um sinnvolle Werte zu erhalten muss eine
     * Kombination aus x-Wert der Komponente, deren Breite und die Breite der Ebene ODER y-Wert der Komponente, deren Höhe und die Höhe der Ebene
     * übergeben werden.
     *
     * @param xy x- bzw. y-Wert der Komponente
     * @param wh Breite bzw. Höhe der Komponente
     * @param initialSize initiale Breite bzw. Höhe der Ebene
     * @return
     */
    private double getMinimalPageSizeFactor(int xy, int wh, final int initialSize) {
        xy -= wh / 2; //übergebener Paramter ist der Mittelpunkt -> linke obere Ecke ist Mittelpunkt - halbe Weite bzw. Höhe
        wh = xy + wh; //übergebener Parameter ist die Weite bzw. Höhe -> untere Ecke ist obere Ecke plus Weite bzw. Höhe
        if (xy < 0) { //negative Werte positiv machen
            xy = -xy;
        }
        if (wh < 0) { //negative Werte positiv machen
            wh = -wh;
        }
        if (xy < wh) { //je nachdem, ob obere oder untere Ecke größere Koordinaten haben, dann den größeren Wert nehmen
            xy = wh;
        }
        xy *= 2; //da die Zeichenfläche immer im Mittelpunkt (0,0) hat, muss der maximale x- bzw. y-Wert kleiner sein, als die halbe Ebenengröße. Ist er größer -> Ebene vergrößern
        return (double) xy / (double) initialSize;
    }

    /**
     * Liefert den minimalen pageSizeFactor, der gebraucht wird, um die übergebene Komponente auf der Ebene darzustellen
     *
     * @param c
     * @return
     */
    private double getMinimalPageSizeFactor(final Component c) {
        int x = c.getX();
        int y = c.getY();
        int width = c.getWidth();
        int height = c.getHeight();
        double neededPageSizeFactorX = getMinimalPageSizeFactor(x, width, INITIAL_PAGE_WIDTH);
        double neededPageSizeFactorY = getMinimalPageSizeFactor(y, height, INITIAL_PAGE_HEIGHT);
        return neededPageSizeFactorX > neededPageSizeFactorY ? neededPageSizeFactorX : neededPageSizeFactorY;
    }

    /**
     * Liefert den minimalen pageSizeFactor, der gebraucht wird, um alle Elemente und Knickpunkte auf der Ebene darzustellen
     *
     * @return
     */
    private double getMinimalPageSizeFactor() {
        double minPageSizeFactor = 1.0;
        for (int l : ModelConstants.VISIBLE_LAYERS) {
            LayerContainer lc = layer[l];
            Iterable<NodeContainer> graphNodeContainers = lc.getGraphNodeContainers();
            Iterable<BendpointContainer> bendpointContainers = lc.getBendpointContainers();
            Iterable<NodeContainer> visibleElements = CollectionUtils.getCommonIterable(graphNodeContainers, bendpointContainers);
            for (ElementContainer c : visibleElements) {
                double neededPageSizeFactor = getMinimalPageSizeFactor(c);
                if (neededPageSizeFactor > minPageSizeFactor) {
                    minPageSizeFactor = neededPageSizeFactor;
                }
            }
        }
        return minPageSizeFactor;
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
        boolean isMoveSubElements = OPTION_GRAPH_MOVE_SUBELEMENTS.set(false);
        TransactionManager tman = gdcoll.getTman();
        if (undo) {
            tman.undo(pid);
        } else {
            tman.redo(pid);
        }
        OPTION_GRAPH_MOVE_SUBELEMENTS.set(isMoveSubElements);
        transStackTable.decrease(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param command
     * @param pid
     */
    public void addUndoCommand(final String command, final int pid) {
        if (!gdcoll.isBulkMode()) {
            TransactionManager transactionManager = gdcoll.getTman();
            transactionManager.addUndoCommand(command, pid);
        }
    }

    /**
     * @param command
     * @param pid
     */
    public void addRedoCommand(final String command, final int pid) {
        if (!gdcoll.isBulkMode()) {
            TransactionManager transactionManager = gdcoll.getTman();
            transactionManager.addRedoCommand(command, pid);
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
            TransactionManager transactionManager = gdcoll.getTman();
            String arguments = String.valueOf(commandArguments);
            transactionManager.addOrReplaceRedoCommand(commandPrefix, arguments, pid);
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
            TransactionManager transactionManager = gdcoll.getTman();
            String arguments = String.valueOf(commandArguments);
            transactionManager.addUndoCommandIfNotExist(commandPrefix, arguments, pid);
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
        }
    }

    /**
     * @param ec
     * @return
     */
    public boolean isMyElement(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        return isMyElement(me);
    }

    /**
     * @param me
     * @return
     */
    public boolean isMyElement(final ModelElement me) {
        int layer = me.layerFor();
        if (layer != ModelConstants.NO_LAYER) {
            LayerContainer lc = this.layer[layer];
            return lc.isMyElement(me);
        }
        for (LayerContainer lc : this.layer) {
            if (lc.isMyElement(me)) {
                return true;
            }
        }
        return false;
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
     */
    private final void exec(final String command, final String ucommand, final int pid) {
        if (isVerificationMode()) {
            System.out.println("Kommando: " + command + "\n" + ucommand + "\n\n");
        }
        if (command == null) {
        } else if (command.equals(GDCommands.MODEL_ACTION_PRINT_QUEUE.toString())) {
            GDCollection gdcoll = getCollection();
            TransactionManager transactionManager = gdcoll.getTman();
            transactionManager.printQueue(10);
        } else if (command.startsWith(GDCommands.MODEL_ACTION_COMMAND_LINE.toString())) {
            String title = getResStringWithoutError("tool3lgm");
            String message = getResStringWithoutError(GDCommands.MODEL_ACTION_COMMAND_LINE.name());
            String answer = (String) JOptionPane.showInputDialog(getMainFrame(), message, title, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (answer != null && !answer.equals("") && !answer.equals("COMMAND_LINE")) {
                exec(answer, "", pid);
            }
        } else if (command.equals(GDCommands.MODEL_ACTION_INTERNAL_CHECK_CONSISTENCY.toString())) {
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
        exec(command, "", pid);
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
        else if (this == gdcoll.getMainDoc() || isSelectedOnlyUnique() || isSelectedOnlySlaveRealNodes()) {
            if (OPTION_SHOW_REMOVE_WARNING.is()) {
                MainFrame mainFrame = Static.getMainFrame();
                String dialogTitle = getResString("attention");
                String dialogMessage = getResString("remove_element_warning");
                String dontAskAgainQuestion = getResString("dont_ask_again");
                Boolean answer = MultipleOptionPane.showSingleCheckboxDialog(mainFrame, dialogTitle, dialogMessage, dontAskAgainQuestion, false);
                //es wurde nicht Abbrechen sonder Ok gedrückt
                if (answer != null) {
                    dispatch_command(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL, argv, pid);
                    if (!answer) { // die Checkbox ist nicht selektiert -> Globale Option "Warnmeldung vor dem Löschen" soll true sein
                        UserPropertyBooleanChangeAction action = OPTION_SHOW_REMOVE_WARNING.createAction();
                        action.perform();
                    }
                }
            } else {
                dispatch_command(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL, argv, pid);
            }
        }
        // Auswahl in einem Teilmodell
        else {
            Object[] buttons = new Object[] {
                    getResString("submodel"), getResString("whole_model"), getResString("cancel")
            };
            MainFrame mainFrame = Static.getMainFrame();
            String dialogMessage = getResString("loeschfrage");
            String dialogTitle = getResString("tool3lgm");
            int value = JOptionPane.showOptionDialog(mainFrame, dialogMessage, dialogTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[2]);
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
        MetaModel metaModel = getMetaModel();

        switch (command) {

        case MODEL_ACTION_DELETE:
            remove(argv, pid);
            break;

        case MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON:
        case MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF: {
            //argv[0] = szenHash (optional)
            //argv[1..n] = elementHashes (optional)
            setVisible(command == MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON, argv, pid);
            break;
        }
        case MODEL_ACTION_CREATE_NODE: {
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
            Class<? extends ModelElement> elementClass = metaModel.getClassForName(classname);
            createNodeAndContainer(elementClass, name, description, hashcode, pid);
            break;
        }
        case MODEL_ACTION_DELETE_FROM_MODEL: {
            switch (argc) {
            case 0:
                gdcoll.deleteElements(getSelectedElements(), this, pid);
                break;
            default:
                gdcoll.deleteElements(argv, this, pid);
                break;
            }
            break;
        }
        case MODEL_ACTION_LINK: {
            switch (argc) {
            case 2:
                Direction direction = Enum.valueOf(Direction.class, argv[1]);
                Class<? extends ModelElement> elementClass = metaModel.getClassForName(argv[0]);
                Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
                linkSelected(edgeClass, direction, pid);
                break;
            case 6:
                //Parameter: link(String edgeClassName, String edgeHash, ModelElement k1, ModelElement k2, int edgeIndex, int pid) {
                int startElementEdgeIndex = Integer.parseInt(argv[4]);
                int endElementEdgeIndex = Integer.parseInt(argv[5]);
                /* Edge edge = */gdcoll.link(argv[0], argv[1], argv[2], argv[3], startElementEdgeIndex, endElementEdgeIndex, pid);
                //						System.err.println("<Etxrabllatt>");
                //						System.err.println(edge.getStart() + " (" + edge.getStart().getHashString() + ") " + edge.getEnd() + " (" + edge.getEnd().getHashString() + ")");
                //						System.err.println("</Etxrabllatt>");
                break;
            default:
                break;
            }
            break;
        }
        case MODEL_ACTION_UNLINK: {
            switch (argc) {
            case 2:
                Direction direction = Enum.valueOf(Direction.class, argv[1]);
                Class<? extends ModelElement> elementClass = metaModel.getClassForName(argv[0]);
                Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
                unlinkSelected(edgeClass, direction, pid);
                break;
            case 4:
                elementClass = metaModel.getClassForName(argv[2]);
                edgeClass = elementClass.asSubclass(Edge.class);
                int position = Integer.parseInt(argv[3]);
                gdcoll.unlink(argv[0], argv[1], edgeClass, position, pid);
                break;
            default:
                break;
            }
            break;
        }
        case MODEL_ACTION_SWAP_EDGE_POSITIONS: {
            switch (argc) {
            case 3:
                swapEdgePositions(argv[0], argv[1], argv[2], pid);
                break;
            }
            break;
        }
        case MODEL_ACTION_ADDICT: {
            addict(argv[0], argv[1], argv[2], argv[3], pid);
            break;
        }
        case MODEL_ACTION_CREATE_ADDICTED: {
            GraphDocument doc = gdcoll.getGraphDocumentCoded(argv[0]);
            ModelElement master = doc.findElementCoded(argv[1]);

            Class<? extends ModelElement> elementClass = metaModel.getClassForName(argv[2]);
            Class<? extends CompositionEdge> compositionEdgeClass = elementClass.asSubclass(CompositionEdge.class);
            Class<? extends ModelElement> slaveClass = metaModel.getClassForName(argv[3]);
            createAddicted(doc, master, compositionEdgeClass, slaveClass, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_COLOR: {
            switch (argc) {
            case 0:
                changeColor(pid);
                break;
            case 3:
                try {
                    int colorValue = Integer.parseInt(argv[2]);
                    Color color = new Color(colorValue);
                    changeColor(argv[0], argv[1], color, pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            default:
                break;
            }
            break;
        }
        case MODEL_ACTION_SET_LAYER_COLOR: {
            switch (argc) {
            case 0:
                changeLayerColor(pid);
                break;
            case 3:
                try {
                    int layer_idx = Integer.parseInt(argv[1]);
                    int colorValue = Integer.parseInt(argv[2]);
                    Color color = new Color(colorValue);
                    changeLayerColor(argv[0], layer_idx, color, pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            default:
                break;
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_ALPHA: {
            switch (argc) {
            case 1:
                try {
                    int alpha = Integer.parseInt(argv[0]);
                    changeAlpha(alpha, pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            case 3:
                try {
                    int alpha = Integer.parseInt(argv[2]);
                    changeAlpha(argv[0], argv[1], alpha, pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            default:
                break;
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL: {
            changeAlpha(GraphElementLayout.TRANSPARENCY_FULL, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF: {
            changeAlpha(GraphElementLayout.TRANSPARENCY_HALF, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE: {
            changeAlpha(GraphElementLayout.TRANSPARENCY_NONE, pid);
            break;
        }
        case MODEL_ACTION_SET_LAYER_ALPHA: {
            switch (argc) {
            case 1:
                try {
                    int layerAlpha = Integer.parseInt(argv[0]);
                    changeLayerAlpha(layerAlpha, pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            case 3:
                try {
                    int layer = Integer.parseInt(argv[1]);
                    int alpha = Integer.parseInt(argv[2]);
                    changeLayerAlpha(argv[0], layer, alpha, pid);
                } catch (Exception e) {
                    Log(e);
                }
                break;
            }
            break;
        }
        case MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL: {
            changeLayerAlpha(GraphElementLayout.TRANSPARENCY_FULL, pid);
            break;
        }
        case MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF: {
            changeLayerAlpha(GraphElementLayout.TRANSPARENCY_HALF, pid);
            break;
        }
        case MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE: {
            changeLayerAlpha(GraphElementLayout.TRANSPARENCY_NONE, pid);
            break;
        }
        case MODEL_ACTION_SET_LAYER_SIZE_FACTOR: {
            try {
                GraphDocument szen = gdcoll.getGraphDocumentCoded(argv[0]);
                double pageSizeFactor = Double.parseDouble(argv[1]);
                szen.setPageSizeFactor(pageSizeFactor, true, TransactionManager.STANDARD_PID);
            } catch (Exception e) {
                Log(e);
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT: {
            if (argc == 0) {
                normalizeFontSelected(pid);
            } else if (argc == 2) {
                normalizeFontElement(argv[0], argv[1], pid);
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR: {
            if (argc == 0) {
                normalizeColorSelected(pid);
            } else if (argc == 2) {
                normalizeColorElement(argv[0], argv[1], pid);
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY: {
            if (argc == 0) {
                normalizeTransparencySelected(pid);
            } else if (argc == 2) {
                normalizeTransparencyElement(argv[0], argv[1], pid);
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_DEFAULT_FULL_LAYOUT: {
            if (argc == 0) {
                normalizeSelected(pid);
            }
            break;
        }
        case MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY: {
            if (argc == 0) {
                GraphDocument selectedDoc = gdcoll.getSelectedDoc();
                int activeLayer = gdcoll.getActiveLayer();
                normalizeLayer(selectedDoc.hashString, activeLayer, pid);
            }
            if (argc == 2) {
                try {
                    int layer = Integer.parseInt(argv[1]);
                    normalizeLayer(argv[0], layer, pid);
                } catch (Exception e) {
                    Log(e);
                }
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_NAME: {
            switch (argc) {
            case 2:
                //[0] = elementHash, [1] = newName
                ModelElement me = findElementCoded(argv[0]);
                setName(me, argv[1], pid);
                break;
            default:
                break;
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_DESCRIPTION: {
            //[1] = ElementHashString, [2] = Beschreibung
            ModelElement me = findElementCoded(argv[0]);
            setDescription(me, argv[1], pid);
            break;
        }
        case MODEL_ACTION_SET_USER_FIELD_VALUE: {
            if (argc == 3) {
                setUserFieldValue(argv[0], argv[1], argv[2], pid);
            }
            break;
        }
        case MODEL_ACTION_SET_USER_FIELD_WEIGHT_REPLACEMENT: {
            if (argc == 3) {
                setUserFieldWeightReplacement(argv[0], argv[1], argv[2], pid);
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_OPTIONAL: {
            if (argc == 2) {
                GraphDocument mainDoc = gdcoll.getMainDoc();
                Edge edge = mainDoc.findEdgeCoded(argv[0]);
                boolean bool = Boolean.parseBoolean(argv[1]);
                setOptional(edge, bool, pid);
            }
        }
        case MODEL_ACTION_SET_ELEMENT_POSITION: {
            if (argc == 6) {
                try {
                    String szenHash = argv[0];
                    String hashCode = argv[1];
                    int x = Integer.parseInt(argv[2]);
                    int y = Integer.parseInt(argv[3]);
                    int width = Integer.parseInt(argv[4]);
                    int height = Integer.parseInt(argv[5]);
                    moveNodeContainer(gdcoll, szenHash, hashCode, x, y, width, height, pid);
                } catch (Exception e) {
                    Log(e);
                }
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_EXPANSION_ON:
        case MODEL_ACTION_SET_ELEMENT_EXPANSION_OFF: {
            boolean expand = command == GDCommands.MODEL_ACTION_SET_ELEMENT_EXPANSION_ON;
            if (argc == 2) {
                setExpanded(expand, gdcoll, argv[0], argv[1], !expand, pid);
            } else {
                setExpanded(expand, pid);
            }
            break;
        }
        case MODEL_OPTION_GDOC_VERIFICATION_MODE: {
            if (argc == 0) {
                optionsSupport.switchOption(MODEL_OPTION_GDOC_VERIFICATION_MODE);
            } else {
                boolean verify = Boolean.parseBoolean(argv[0]);
                optionsSupport.setOption(MODEL_OPTION_GDOC_VERIFICATION_MODE, verify);
            }
            break;
        }
        case MODEL_OPTION_GDCOLL_AUTOMATIC_MODE: {
            boolean isInteractiveMode = argc == 0 ? !gdcoll.isAutomaticMode() : Boolean.parseBoolean(argv[0]);
            gdcoll.setAutomaticMode(isInteractiveMode);
            break;
        }
        case MODEL_ACTION_INSERT_BENDING_POINT: {
            //[0] = SzenHash, [1] = HashString der Edge, [2] = HashString des Knickpunktes, [3] = X-Position, [4] = Y-Position, [5] = Index des Knickpuntes auf der Edge,
            int x = Integer.parseInt(argv[3]);
            int y = Integer.parseInt(argv[4]);
            int bendpointIndexOnEdge = Integer.parseInt(argv[5]);
            gdcoll.insertBendingPoint(argv[0], argv[1], argv[2], x, y, bendpointIndexOnEdge, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_CENTER:
        case MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_LEFT:
        case MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_RIGHT:
        case MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_BOTTOM:
        case MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_CENTER:
        case MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_TOP:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH:
        case MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH: {
            align(command, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_LEFT: {
            setTextPositionHorizontal(TextPositionHorizontal.LEFT, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_CENTER: {
            setTextPositionHorizontal(TextPositionHorizontal.CENTER, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_RIGHT: {
            setTextPositionHorizontal(TextPositionHorizontal.RIGHT, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL: {
            if (argc == 1) {
                TextPositionHorizontal textPositionHorizontal = TextPositionHorizontal.valueOf(argv[0]);
                setTextPositionHorizontal(textPositionHorizontal, pid);
            }
            if (argc == 3) {
                //[0] = SzenHash, [1] = HashString der Containers, [2] = align mode
                GraphDocument szen = gdcoll.getGraphDocumentCoded(argv[0]);
                ElementContainer ec = szen.findContainerCoded(argv[1]);
                TextPositionHorizontal textPositionHorizontal = TextPositionHorizontal.valueOf(argv[2]);
                szen.setTextPositionHorizontal(textPositionHorizontal, ec, pid);
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_TOP: {
            setTextPositionVertical(TextPositionVertical.TOP, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_CENTER: {
            setTextPositionVertical(TextPositionVertical.CENTER, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_BOTTOM: {
            setTextPositionVertical(TextPositionVertical.BOTTOM, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL: {
            if (argc == 1) {
                TextPositionVertical textPositionVertical = TextPositionVertical.valueOf(argv[0]);
                setTextPositionVertical(textPositionVertical, pid);
            }
            if (argc == 3) {
                //[0] = SzenHash, [1] = HashString der Containers, [2] = align mode
                GraphDocument szen = gdcoll.getGraphDocumentCoded(argv[0]);
                ElementContainer ec = szen.findContainerCoded(argv[1]);
                TextPositionVertical textPositionVertical = TextPositionVertical.valueOf(argv[2]);
                szen.setTextPositionVertical(textPositionVertical, ec, pid);
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_LEFT: {
            setTextAlignmentHTML(TextAlignmentHTML.LEFT, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_CENTER: {
            setTextAlignmentHTML(TextAlignmentHTML.CENTER, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_RIGHT: {
            setTextAlignmentHTML(TextAlignmentHTML.RIGHT, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_JUSTIFY: {
            setTextAlignmentHTML(TextAlignmentHTML.JUSTIFY, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML: {
            if (argc == 1) {
                TextAlignmentHTML textAlignmentHTML = TextAlignmentHTML.valueOf(argv[0]);
                setTextAlignmentHTML(textAlignmentHTML, pid);
            }
            if (argc == 3) {
                //[0] = SzenHash, [1] = HashString der Containers, [2] = align mode
                GraphDocument szen = gdcoll.getGraphDocumentCoded(argv[0]);
                ElementContainer ec = szen.findContainerCoded(argv[1]);
                TextAlignmentHTML textAlignmentHTML = TextAlignmentHTML.valueOf(argv[2]);
                szen.setTextAlignmentHTML(textAlignmentHTML, ec, pid);
            }
            break;
        }
        case MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION: {
            if (argc == 0) {
                z_move_up(pid);
            } else {
                z_move_up(argv[0], argv[1], pid);
            }
            break;
        }
        case MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION: {
            if (argc == 0) {
                z_move_down(pid);
            } else {
                z_move_down(argv[0], argv[1], pid);
            }
            break;
        }
        case MODEL_ACTION_MOVE_ORDER: {
            try {
                int newElementIndexInGraphOrder = Integer.parseInt(argv[2]);
                z_move(argv[0], argv[1], newElementIndexInGraphOrder, pid);
            } catch (Exception e) {
                Log(e);
            }
            break;
        }
        case MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP: {
            if (argc == 0) {
                z_step_up(pid);
            } else {
                z_step_up(argv[0], argv[1], pid);
            }
            break;
        }
        case MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN: {
            if (argc == 0) {
                z_step_down(pid);
            } else {
                z_step_down(argv[0], argv[1], pid);
            }
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_ICON: {
            switch (argc) {
            case 0:
                chooseIcon(pid);
                break;
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
        }
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

        case MODEL_ACTION_SET_ELEMENT_FONT: {
            String name = "";
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
        }
        case MODEL_ACTION_CREATE_SUBMODEL:
            Szenario szen;
            if (argc == 0) {
                szen = gdcoll.createSzenario(true);
            } else {
                String szenName = getDecodedParseSaveString(argv[1]);
                szen = gdcoll.createSzenario(argv[0], false, szenName, argv[2], pid);
            }
            if (szen == null) {
                finish_transaction(pid);
                return;
            }
            break;

        case MODEL_ACTION_DELETE_SUBMODEL: {
            String szenHash = null;
            if (argc == 0) {
                GraphDocument selectedDoc = gdcoll.getSelectedDoc();
                szenHash = selectedDoc != null ? selectedDoc.hashString : null;
            } else {
                szenHash = argv[0];
            }
            gdcoll.deleteSzenario(szenHash, pid);
            break;
        }
        case MODEL_ACTION_RENAME_SUBMODEL: {
            String szenHash = null;
            String newName = null;
            if (argc == 0) {
                GraphDocument selectedDoc = gdcoll.getSelectedDoc();
                szenHash = selectedDoc != null ? selectedDoc.hashString : null;
            } else {
                szenHash = argv[0];
                newName = argv[1];
            }
            gdcoll.renameSzenario(szenHash, newName, pid);
            break;
        }
        case MODEL_ACTION_ADD_ELEMENT_TO_SUBMODEL: {
            //argv[0] = Ziel-Szenario-Hash
            //argv[1] = Element-Hash
            if (argc == 2) {
                addElementToSzenario(null, argv[0], argv[1], pid);
                //argv[0] = Quell-GraphDocument-Hash (kann auch das Hauptdokument sein)
                //argv[1] = Ziel-Szenario-Hash
                //argv[2] = Element-Hash
            } else if (argc == 3) {
                addElementToSzenario(argv[0], argv[1], argv[2], pid);
            }
            break;
        }
        case MODEL_ACTION_ADD_SELECTED_TO_SUBMODEL: {
            if (argc != 1) {
                return;
            }
            //Selection clonen, weil sie sich während der Ausführung ändert!
            List<ElementContainer> selectionInGraphOrder = getSelectionInGraphOrder();
            addElementsToSzenario(argv[0], selectionInGraphOrder, pid);
            break;
        }
        case MODEL_ACTION_ADD_SELECTED_TO_NEW_SUBMODEL: {
            //Selection clonen, weil sie sich während der Ausführung ändert!
            List<ElementContainer> selectionInGraphOrder = getSelectionInGraphOrder();
            addContainerToNewSzenario(selectionInGraphOrder, pid);
            break;
        }
        case MODEL_ACTION_ADD_SELECTED_TO_ALL_SUBMODELS: {
            //Selection clonen, weil sie sich während der Ausführung ändert!
            addContainerToAllSzenarios(getSelectionInGraphOrder(), pid);
            break;
        }
        case MODEL_ACTION_LINK_ELEMENT_TO_SUBMODEL: {
            if (argc < 2) {
                return;
            }
            linkElementToSzenario(argv[0], argv[1], pid);
            break;
        }
        case MODEL_ACTION_SELECT_LINKED_SUBMODEL: {
            Tool3lgm tool = Static.getTool();
            tool.changeToLinked(this);
            break;
        }
        case MODEL_ACTION_LINK_SELECTED_TO_SUBMODEL: {
            if (argc < 1) {
                return;
            }
            switch (argc) {
            case 1:
                ArrayList<ElementContainer> selectionClone = new ArrayList<>(selectedContainer);
                linkElementsToSzenario(argv[0], selectionClone, pid);
                break;
            default:
                linkElementToSzenario(argv[0], argv[1], pid);
            }
            break;
        }
        case MODEL_ACTION_UNLINK_SELECTED_TO_SUBMODEL: {
            ArrayList<ElementContainer> selectionClone = new ArrayList<>(selectedContainer);
            linkElementsToSzenario(null, selectionClone, pid);
            break;
        }
        case MODEL_ACTION_LINK_SELECTED_TO_NEW_SUBMODEL: {
            ArrayList<ElementContainer> selectionClone = new ArrayList<>(selectedContainer);
            linkElementsToNewSzenario(selectionClone, pid);
            break;
        }
        case MODEL_ACTION_DELETE_FROM_SUBMODEL: {
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
        }
        case MODEL_ACTION_JOIN_SELECTED: {
            joinSelected(pid);
            break;
        }
        case MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_ON:
        case MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_OFF: {
            int activeLayer = gdcoll.getActiveLayer();
            layer[activeLayer].setShowInterLayerConnections(command == GDCommands.MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_ON);
            distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
            break;
        }
        case MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_ON:
        case MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_OFF: {
            for (ElementContainer ec : selectedContainer) {
                ModelElement me = ec.getElement();
                if (metaModel.hasInterLayerStartClass(me)) {
                    InterLayerConnectedNodeContainer interLayerEc = (InterLayerConnectedNodeContainer) ec;
                    interLayerEc.setShowInterLayerConnections(command == GDCommands.MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_ON);
                }
            }
            distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
            break;
        }
        default:
            break;
        }
    }

    private void exec_command(final String line, final int pid) {
        try {
            if (isVerificationMode()) {
                System.out.println(line);
            }
            if (Strings.isNullOrEmpty(line)) {
                return;
            }
            List<String> args = new ArrayList<>();
            String commandName = CommandParser.parseCommandLine(line, args);
            GDCommands command = getCommand(commandName);
            String[] argv = args.toArray(new String[0]);
            dispatch_command(command, argv, pid);
        } catch (Exception e) {
            Class<? extends Exception> errorClass = e.getClass();
            String errorClassName = errorClass.getName();
            String errorMessage = errorClassName + ": " + e.getMessage();
            Log(e, errorMessage);
        }
    }

    /**
     * @param commandName
     * @return
     */
    private static GDCommands getCommand(final String commandName) {
        //je nach globaler Option können die geloggten Kommandos den Namen des GDCommands oder den Index in der Liste aller GDCommands
        //aber wenn die Option auf false steht, dann sind die UNOD-Kommandos als Zahl kodiert, alle anderen sind aber noch lesbar, daher
        //muss man testen, ob sich das Kommando auf int casten lässt.
        GDCommands command = null;
        try {
            //wenn undo und redo mit den Komandoindizes statt den vollständigen Namen geloggt werden
            //versuche den Index zu parsen und das Kommando
            int commandIndex = Integer.parseInt(commandName);
            GDCommands[] gdCommans = GDCommands.getValues();
            command = gdCommans[commandIndex];
        } catch (Exception e) {
            //wenn lesbar geloggt werden soll -> einfach den Kommandonamen nehmen
            command = GDCommands.valueOf(commandName);
        }
        return command;
    }

    /**
     * @param e
     */
    private void Log(final Exception e) {
        Log(e, null);
    }

    /**
     * @param e
     * @param message
     */
    private void Log(final Exception e, final String message) {
        String fullMessage = getResString("FehlerAllgemein");
        fullMessage = Strings.isNullOrEmpty(message) ? fullMessage : fullMessage + " \n" + message;
        Log.show(Log.ERROR, fullMessage, e);
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
        //Sys.err("start " + gdcoll.isBulkMode() + " " + pid + " " + log);
        if (gdcoll.isBulkMode()) {
            return;
        }
        if (log) {
            TransactionManager transactionManager = gdcoll.getTman();
            transactionManager.startTransaction("", "", pid, this);
        }
        Map<Integer, Integer> transStackTable = gdcoll.getTransStackTable();
        Integer transStackInteger = transStackTable.remove(pid);
        if (transStackInteger == null) {
            transStackInteger = 0;
        }
        int transStackInt = transStackInteger.intValue();
        transStackInt++;
        transStackTable.put(pid, transStackInt);

        //		Integer tst = transStackTable.get(pidInteger);
        //		System.err.println(iii++ + " + " + pid + ": "+ tst + " " + this);

        //		if (tst != null && lastTransStackInt == tst)
        //			System.err.println("jetze");

        if (log) {
            if (!(transStackInt > 1)) {
                //				System.err.println("start_transaction " + iii++ + " + " + pid + ": "+ transStackInt + " " + this);
                for (ElementContainer ec : selectedContainer) {
                    TransactionManager transactionManager = gdcoll.getTman();
                    String hash = ec.getHashString();
                    transactionManager.addPreSelectionItem(hash, pid);
                }
            }
        }
        //Sys.err("start " + " " + pid + " " + log);
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
        //Sys.err("finish " + gdcoll.isBulkMode() + " " + pid + " " + log);
        if (gdcoll.isBulkMode()) {
            return;
        }
        Map<Integer, Integer> transStackTable = gdcoll.getTransStackTable();
        Integer transStackInteger = transStackTable.remove(pid);
        if (transStackInteger == null) {
            transStackInteger = 0;
        }
        int transStackInt = transStackInteger.intValue();
        transStackInt--;
        if (transStackInt > 0) {
            transStackInteger = transStackInt;
            transStackTable.put(pid, transStackInteger);
        }
        Integer tst = transStackTable.get(pid);
        //		System.err.println(iii++ + " - " + pid + ": "+ tst + " " + this);
        lastTransStackInt = tst == null ? 0 : tst;
        if (log) {
            //			System.err.println("### " + (iii++) + " " + pid);
            TransactionManager transactionManager = gdcoll.getTman();
            if (transStackInt == 0) {
                //				System.err.println("finish_transaction " + iii++ + " - " + pid + ": "+ transStackInt + " " + this);
                for (ElementContainer ec : selectedContainer) {
                    String hash = ec.getHashString();
                    transactionManager.addPostSelectionItem(hash, pid);
                }
            }
            transactionManager.finishTransaction(pid);
        }
        //Sys.err("finish " + pid + "  " + log);
    }

    /**
     * @return
     */
    public boolean isVerificationMode() {
        return optionsSupport.isOptionTrue(GDCommands.MODEL_OPTION_GDOC_VERIFICATION_MODE) || Tool3lgmConstants.LOG_READABLE_UNDO_REDO_COMMANDS;
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
    public final void setNodeContainerInsertPosition(final int x, final int y) {
        next_x_pos = x;
        next_y_pos = y;
    }

    /**
     * Set the insertPosition to the center of the element if it is not null and
     * has a visible container in this doc.
     *
     * @param me
     */
    protected final void setNodeContainerInsertPosition(final ModelElement me) {
        if (me != null && me.isPaintable()) {
            ElementContainer ec = me.getContainer(this);
            boolean setInserpositionToPathEndElement = ec != null && ec.isVisible();
            if (setInserpositionToPathEndElement) {
                GraphElementLayout layout = ec.get3LGMLayout();
                setNodeContainerInsertPosition(layout.x, layout.y);
            }
        }
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
            for (NodeContainer kc : lc.getGraphNodeContainers()) {
                kc.refreshFont();
                kc.refreshText();
            }
        }
    }

    /**
     * @param mc
     * @param pid
     */
    private final void normalizeElement(final ElementContainer mc, final int pid) {
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
    private final void normalizeFontElement(final ElementContainer ec, final int pid) {
        start_transaction(pid);
        GraphDocument doc = ec.getGraphDocument();
        String szenHash = doc.getHashString();
        String elementHash = ec.getHashString();
        String command = GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT + " " + szenHash + " " + elementHash;
        addRedoCommandOrReplace(command, "", pid);

        String fontName = ec.getFontName();
        if (fontName != null) {
            command = GDCommands.MODEL_ACTION_SET_ELEMENT_FONT + " " + szenHash + " " + elementHash;
            String parseSaveFontName = ParseSaveStringHandler.getParseSaveString(fontName);
            int fontSize = ec.getFontSize();
            int fontStyle = ec.getFontStyle();
            String arguments = parseSaveFontName + " " + fontSize + " " + fontStyle;
            addUndoCommandIfNotExist(command, arguments, pid);
            ec.setFont(null);
        }
        finish_transaction(pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    private final void normalizeFontElement(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            ElementContainer ec = szen.findContainerCoded(elementHash);
            normalizeFontElement(ec, pid);
        }
    }

    /**
     * @param ec
     * @param pid
     */
    private final void normalizeColorElement(final ElementContainer ec, final int pid) {
        start_transaction(pid);
        GraphDocument doc = ec.getGraphDocument();
        String szenHash = doc.getHashString();
        String elementHash = ec.getHashString();
        String command = GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR + " " + szenHash + " " + elementHash;
        addRedoCommandOrReplace(command, "", pid);
        Color color = ec.getColor();
        if (color != null) {
            command = GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + elementHash;
            int rgb = color.getRGB();
            addUndoCommandIfNotExist(command, rgb, pid);
            ec.setColor(null);
        }
        finish_transaction(pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    private final void normalizeColorElement(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            ElementContainer ec = szen.findContainerCoded(elementHash);
            normalizeColorElement(ec, pid);
        }
    }

    /**
     * COMMENTME
     *
     * @param ec
     * @param pid
     */
    private final void normalizeTransparencyElement(final ElementContainer ec, final int pid) {
        start_transaction(pid);
        GraphDocument doc = ec.getGraphDocument();
        String szenHash = doc.hashString;
        String elementHash = ec.getHashString();
        String redoCommand = GDCommands.MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY + " " + szenHash + " " + elementHash;
        addRedoCommandOrReplace(redoCommand, "", pid);
        String undoCommand = GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA + " " + szenHash + " " + elementHash;
        int alpha = ec.getAlpha();
        addUndoCommandIfNotExist(undoCommand, alpha, pid);
        ec.setAlpha(GraphElementLayout.TRANSPARENCY_NONE);
        finish_transaction(pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    private final void normalizeTransparencyElement(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            ElementContainer ec = szen.findContainerCoded(elementHash);
            normalizeTransparencyElement(ec, pid);
        }
    }

    /**
     * @param pid
     */
    private final void normalizeSelected(final int pid) {
        if (selectedContainer.isEmpty()) {
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
    private final void normalizeFontSelected(final int pid) {
        if (selectedContainer.isEmpty()) {
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
    private final void normalizeColorSelected(final int pid) {
        if (selectedContainer.isEmpty()) {
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
    private final void normalizeTransparencySelected(final int pid) {
        if (selectedContainer.isEmpty()) {
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
    private final void setIcon(final String szenHash, final String hashCode, final String iconKey, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        ElementContainer t = szen.findContainerCoded(hashCode);
        if (!(t instanceof NodeContainer)) {
            return;
        }
        NodeContainer nc = (NodeContainer) t;
        szen.start_transaction(pid);
        String elementHash = nc.getHashString();
        Color color = nc.getColor();
        Integer rgb = color == null ? null : color.getRGB();
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + elementHash, rgb, pid);
        int alpha = color == null ? 255 : color.getAlpha();
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA + " " + szenHash + " " + elementHash, alpha, pid);
        if (nc.getIcon() != null) {
            String iconString = nc.getIconString();
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ICON + " " + szenHash + " " + elementHash, iconString, pid);
        } else {
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ICON_NONE + " " + szenHash + " " + elementHash, "", pid);
        }
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_ICON + " " + szenHash + " " + elementHash, iconKey, pid);
        GDCollectionIconTable iconTable = gdcoll.getIconTable();
        nc.setIcon(iconKey, iconTable);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, nc, pid);
    }

    /**
     * @param pid
     */
    private final void chooseIcon(final int pid) {
        MainFrame mainFrame = getMainFrame();
        File iconPath = UserProperties.getIconPath();
        File iconFile = ImageChooser.getImageFile(mainFrame, iconPath);
        if (iconFile != null) {
            setIcon(iconFile, pid);
        }
    }

    /**
     * @param iconKey
     * @param pid
     */
    private final void setIcon(final String iconKey, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            ModelElement me = ec.getElement();
            String elementHash = me.getHashString();
            setIcon(hashString, elementHash, iconKey, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param iconFile
     */
    private final void setIcon(final File iconFile, final int pid) {
        GDCollectionIconTable iconTable = gdcoll.getIconTable();
        String iconKey = iconTable.loadIcon(iconFile);
        if (iconKey == null) {
            return;
        }
        setIcon(iconKey, pid);
    }

    /**
     * @param hashString
     * @param pid
     */
    private final void unsetIcon(final String szenHash, final String hashString, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        ElementContainer ec = szen.findContainerCoded(hashString);
        if (!(ec instanceof NodeContainer)) {
            return;
        }
        NodeContainer nc = (NodeContainer) ec;
        szen.start_transaction(pid);
        String elementHash = nc.getHashString();
        if (nc.getIcon() != null) {
            String iconString = nc.getIconString();
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_ICON + " " + szenHash + " " + elementHash, iconString, pid);
        }
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_ICON_NONE + " " + szenHash + " " + elementHash, "", pid);
        GDCollectionIconTable iconTable = gdcoll.getIconTable();
        nc.setIcon(null, iconTable);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, nc, pid);
    }

    /**
     * @param pid
     */
    private final void unsetIcon(final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            String elementHash = ec.getHashString();
            unsetIcon(hashString, elementHash, pid);
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
    private final void changeColor(final String docHash, final String hashCode, final Color color, final int pid) {
        GraphDocument doc = gdcoll.getGraphDocumentCoded(docHash);
        if (doc == null) {
            return;
        }
        ElementContainer ec = doc.findContainerCoded(hashCode);
        changeColor(ec, color, pid);
    }

    /**
     * Ändert die Farbe aller selektierten Elemente. Die Farbe wird mit einem
     * JColorChooser erfragt.
     *
     * @param pid
     */
    private final void changeColor(final int pid) {
        if (gdcoll.isAutomaticMode()) {
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
        MainFrame mainFrame = getMainFrame();
        String title = getResString("farbe_ausw");
        Color col = JColorChooser.showDialog(mainFrame, title, oldcol);
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
        String elementHash = ec.getHashString();
        String command = GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR + " " + szenHash + " " + elementHash;
        //redo
        Integer rgb = color == null ? null : color.getRGB();
        addRedoCommandOrReplace(command, rgb, pid);
        //undo
        Color undoColor = ec.getColor();
        rgb = undoColor == null ? null : undoColor.getRGB();
        addUndoCommandIfNotExist(command, rgb, pid);
        //do
        ec.setColor(color);
        ecDoc.finish_transaction(pid);
        ecDoc.distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, pid);
    }

    /**
     * @param pid
     */
    private final void changeLayerColor(final int pid) {
        if (gdcoll.isAutomaticMode()) {
            return;
        }
        int activeLayer = gdcoll.getActiveLayer();
        LayerContainer lc = layer[activeLayer];
        Color oldcol = lc.getColor();
        MainFrame mainFrame = getMainFrame();
        String title = getResString("farbe_ausw");
        Color col = JColorChooser.showDialog(mainFrame, title, oldcol);
        if (col == null) {
            return;
        }
        GraphDocument selectedDoc = gdcoll.getSelectedDoc();
        String szenHash = selectedDoc.getHashString();
        changeLayerColor(szenHash, activeLayer, col, pid);
    }

    /**
     * @param layer_idx
     * @param color
     * @param pid
     */
    private final void changeLayerColor(final String szenHash, final int layer_idx, final Color color, final int pid) {
        if (layer_idx < MIN_LAYER_INDEX || layer_idx > MAX_LAYER_INDEX) {
            return;
        }
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        szen.start_transaction(pid);
        String command = GDCommands.MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layer_idx;
        //redo
        Integer rgb = color == null ? null : color.getRGB();
        addRedoCommandOrReplace(command, rgb, pid);
        LayerContainer lc = szen.layer[layer_idx];
        //undo
        Color undoColor = lc.getColor();
        rgb = undoColor == null ? null : undoColor.getRGB();
        addUndoCommandIfNotExist(command, rgb, pid);
        //do
        lc.setColor(color);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, lc, pid);
    }

    /**
     * den Alpha-Wert eines ModelElements aendern
     *
     * @param alphaMode
     * @param pid
     */
    private final void changeAlpha(int alphaMode, final int pid) {
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
    private void changeAlpha(final String szenHash, final String hashCode, final int alphaMode, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            ElementContainer ec = szen.findContainerCoded(hashCode);
            changeAlpha(ec, alphaMode, pid);
        }
    }

    /**
     * @param ec
     * @param alpha
     * @param pid
     */
    private void changeAlpha(final ElementContainer ec, int alpha, final int pid) {
        if (ec.layerFor() < MIN_LAYER_INDEX) {
            return;
        }
        if (alpha < 0) {
            alpha = GraphElementLayout.TRANSPARENCY_FULL;
        } else if (alpha > 255) {
            alpha = GraphElementLayout.TRANSPARENCY_NONE;
        }

        GraphDocument ecDoc = ec.getGraphDocument();
        String szenHash = ecDoc.hashString;

        ecDoc.start_transaction(pid);
        if (ec.getColor() == null) {
            Color standardBackGroundColor = mapping.getStandardBackGroundColor(ec);
            changeColor(ec, standardBackGroundColor, pid);
        }
        String elementHash = ec.getHashString();
        String command = GDCommands.MODEL_ACTION_SET_ELEMENT_ALPHA + " " + szenHash + " " + elementHash;
        //redo
        addRedoCommandOrReplace(command, alpha, pid);
        //undo
        int undoAlpha = ec.getAlpha();
        addUndoCommandIfNotExist(command, undoAlpha, pid);
        //do
        ec.setAlpha(alpha);
        ecDoc.finish_transaction(pid);
        ecDoc.distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, pid);
    }

    /**
     * @param layer_idx
     * @param alphaMode
     * @param pid
     */
    private final void changeLayerAlpha(final String szenHash, final int layer_idx, int alphaMode, final int pid) {
        if (layer_idx < MIN_LAYER_INDEX || layer_idx > MAX_LAYER_INDEX) {
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
        LayerContainer lc = szen.layer[layer_idx];
        szen.start_transaction(pid);
        String command = GDCommands.MODEL_ACTION_SET_LAYER_ALPHA + " " + szenHash + " " + layer_idx;
        addRedoCommandOrReplace(command, alphaMode, pid);
        int undoAlpha = lc.getAlpha();
        addUndoCommandIfNotExist(command, undoAlpha, pid);
        lc.setAlpha(alphaMode);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, lc, pid);
    }

    /**
     * @param alphaMode
     * @param pid
     */
    private final void changeLayerAlpha(final int alphaMode, final int pid) {
        GraphDocument selectedDoc = gdcoll.getSelectedDoc();
        String szenHash = selectedDoc.getHashString();
        int activeLayer = gdcoll.getActiveLayer();
        changeLayerAlpha(szenHash, activeLayer, alphaMode, pid);
    }

    /**
     * @param layer_idx
     * @param pid
     */
    private final void normalizeLayer(final String szenHash, final int layer_idx, final int pid) {
        if (layer_idx < MIN_LAYER_INDEX || layer_idx > MAX_LAYER_INDEX) {
            return;
        }
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        szen.start_transaction(pid);
        //redo
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY + " " + szenHash + " " + layer_idx, "", pid);
        //undo
        LayerContainer lc = szen.layer[layer_idx];
        //undo alpha
        int alpha = lc.getAlpha();
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_ALPHA + " " + szenHash + " " + layer_idx, alpha, pid);
        //undo color
        Color color = lc.getColor();
        Integer rgb = color == null ? null : color.getRGB();
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_LAYER_COLOR + " " + szenHash + " " + layer_idx, rgb, pid);
        //do
        lc.setColor(Color.white);
        lc.setAlpha(GraphElementLayout.TRANSPARENCY_NONE);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, lc, pid);
    }

    /**
     * @param ec
     * @param font
     * @param pid
     */
    private final void changeFont(final ElementContainer ec, final Font font, final int pid) {
        if (ec == null) {
            return;
        }
        GraphDocument doc = ec.getGraphDocument();
        doc.start_transaction(pid);
        String szenHash = doc.hashString;

        String commandPrefix = GDCommands.MODEL_ACTION_SET_ELEMENT_FONT + " " + szenHash + " " + ec.getHashString();
        //undo
        String fontName = ec.getFontName();
        fontName = ParseSaveStringHandler.getParseSaveString(fontName);
        int fontSize = ec.getFontSize();
        int fontStyle = ec.getFontStyle();
        String undoCommandArguments = ec.hasStandardFont() ? "" : fontName + " " + fontSize + " " + fontStyle;
        addUndoCommandIfNotExist(commandPrefix, undoCommandArguments, pid);
        //redo
        fontName = font.getName();
        fontName = ParseSaveStringHandler.getParseSaveString(fontName);
        fontSize = font.getSize();
        fontStyle = font.getStyle();
        String redoCommandArguments = ec.isStandardFont(font) ? "" : fontName + " " + fontSize + " " + fontStyle;
        addRedoCommandOrReplace(commandPrefix, redoCommandArguments, pid);
        //do
        ec.setFont(font);
        ec.refreshText();
        doc.finish_transaction(pid);
        doc.distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, pid);
    }

    /**
     * @param szenHash
     * @param hashCode
     */
    private final void changeFont(final String szenHash, final String hashCode, final int pid) {
        changeFont(szenHash, hashCode, "", 0, 0, pid);
    }

    /**
     * @param hashCode
     * @param name
     * @param size
     * @param style
     * @param pid
     */
    private final void changeFont(final String szenHash, final String hashCode, final String name, final int size, final int style, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        szen.start_transaction(pid);
        boolean invalidFontName = Strings.isNullOrEmpty(name);
        Font font = invalidFontName ? null : new Font(name, style, size);
        ElementContainer ec = szen.findContainerCoded(hashCode);
        changeFont(ec, font, pid);
        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * @param font
     * @param pid
     */
    private final void changeFont(Font font, final int pid) {
        if (selectedContainer.isEmpty()) {
            return;
        }
        start_transaction(pid);
        if (font == null) {
            MainFrame mainFrame = getMainFrame();
            ElementContainer lastSelected = getLastSelected();
            Font lastSelectedFont = lastSelected.getFont();
            font = EasyDialogAccess.getFontByChooser(mainFrame, lastSelectedFont);
        }
        if (font != null) {
            for (ElementContainer ec : selectedContainer) {
                changeFont(ec, font, pid);
            }
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
    public final void moveNodeContainer(final NodeContainer nc, final int x, final int y, final int width, final int height, final int pid) {
        if (nc == null) {
            return;
        }
        ModelElement me = nc.getElement();
        if (!me.isPaintable()) {
            return;
        }
        start_transaction(pid);
        String szenHash = nc.getGraphDocument().hashString;

        String undoCommandArguments = nc.getX() + " " + nc.getY() + " " + nc.getWidth() + " " + nc.getHeight();
        String redoCommandArguments = x + " " + y + " " + width + " " + height;
        String commandPrefix = MODEL_ACTION_SET_ELEMENT_POSITION + " " + szenHash + " " + nc.getHashString();
        addUndoCommandIfNotExist(commandPrefix, undoCommandArguments, pid);
        addRedoCommandOrReplace(commandPrefix, redoCommandArguments, pid);
        nc.setCoordinates(x, y, width, height);

        //wenn NodeContainer verschoben werden (keine BendpointContainer)
        if (!(nc instanceof BendpointContainer)) {
            //bei allen Kanten dieser Node
            for (Edge edge : me.getEdges()) {
                EdgeContainer edgeC = (EdgeContainer) edge.getContainer(this);
                //wenn die Edge keinen Container in diesem Teilmodell hat (dann wird sie
                //auch nicht Grafisch dargestellt und es braucht nichts verschoben werden) -> weiter
                if (edgeC == null) {
                    continue;
                }
                //aktualisiere die Endpunkte der Edge
                edgeC.computeBorderPoints();
            }
        } else {
            BendpointContainer bc = (BendpointContainer) nc;
            Bendpoint bendpoint = bc.getBendpoint();
            EdgeContainer edgeC = bendpoint.getOwner();
            edgeC.computeBorderPoints();
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, nc, pid);
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
        List<ElementContainer> selection = expandSelection(OPTION_GRAPH_MOVE_SUBELEMENTS.is());
        Iterable<NodeContainer> nodeContainers = getSelectedRealElementContainerIterable();
        Iterable<BendpointContainer> bendpointContainers = getSelectedBendpointContainerIterable();
        Iterable<NodeContainer> allNodeContainers = CollectionUtils.getCommonIterable(nodeContainers, bendpointContainers);
        for (NodeContainer nc : allNodeContainers) {
            if (layer == ModelConstants.NO_LAYER || layer == nc.layerFor()) {
                int x = nc.getX();
                int y = nc.getY();
                int width = nc.getWidth();
                int height = nc.getHeight();
                moveNodeContainer(nc, x + deltaX, y + deltaY, width, height, pid);
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
    private static final void moveNodeContainer(final GDCollection gdcoll, final String szenHash, final String elementHashCode, final int x, final int y, final int width, final int height, final int pid) {
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
        szen.moveNodeContainer(k, x, y, width, height, pid);
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
        if (!isMultipleNodeSelection()) {
            return false;
        }
        ElementContainer lastSelected = getLastSelectedGraphVisibleNodeOrBendpoint();
        ModelElement me = lastSelected.getElement();
        return me.isPaintable();
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
        NodeContainer lastSelected = getLastSelectedGraphVisibleNodeOrBendpoint();
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
            case MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_TOP:
                y = lastSelected.getY() - lastSelected.getHeight() / 2 + h / 2;
                break;
            case MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_CENTER:
                y = lastSelected.getY();
                break;
            case MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_BOTTOM:
                y = lastSelected.getY() + lastSelected.getHeight() / 2 - h / 2;
                break;
            case MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_LEFT:
                x = lastSelected.getX() - lastSelected.getWidth() / 2 + w / 2;
                break;
            case MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_CENTER:
                x = lastSelected.getX();
                break;
            case MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_RIGHT:
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
            moveNodeContainer(nc, x, y, w, h, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * Erweitert die Selektion, um alle Teilelemente der selektierten Elemente, die bisher nicht in
     * der Selektion waren.
     *
     * @param addAllParts
     *            Wenn <code>true</code> werden alle über {@link HasPartEdge}en verbunden Elemente
     *            in die Selektion mit aufgenommen.
     * @return <code>null</code>, wenn keine Erweiterung der bestehenden Selektion nötig war, sonst
     *         die alte Selektion
     */
    private List<ElementContainer> expandSelection(final boolean addAllParts) {
        Collection<ElementContainer> container2Select = new HashSet<>();
        for (NodeContainer nc : selectedContainer.iterableRealElementContainer()) {
            ModelElement me = nc.getElement();
            for (ElementContainer partNc : me.getSubordinatedContainers(this, addAllParts)) {
                if (!isSelected(partNc)) {
                    container2Select.add(partNc);
                }
            }
        }
        if (container2Select.isEmpty()) {
            return null;
        }
        List<ElementContainer> oldSelection = getSelectedContainer();
        int lastSelectedIndex = oldSelection.size() - 1;
        ElementContainer lastSelected = oldSelection.get(lastSelectedIndex);
        for (ElementContainer ec : container2Select) {
            addSimpleToSelection(ec);
        }
        addSimpleToSelection(lastSelected);
        //Knickpunkte aller Kanten dazuselektieren, bei denen beide Elemente selektiert sind
        container2Select.clear();
        MetaModel metaModel = getMetaModel();
        for (ElementContainer ec : selectedContainer) {
            ModelElement me = ec.getElement();
            Class<? extends ModelElement> elementClass = me.getClass();
            if (metaModel.isPaintable(elementClass)) {
                for (Edge edge : me.getEdges()) {
                    ModelElement other = edge.getOther(me);
                    Class<? extends ModelElement> otherElementClass = other.getClass();
                    if (metaModel.isPaintable(otherElementClass)) {
                        ElementContainer otherEc = other.getContainer(this);
                        if (selectedContainer.contains(otherEc)) {
                            EdgeContainer edgeC = (EdgeContainer) edge.getContainer(this);
                            if (edgeC != null) {
                                for (BendpointContainer bc : edgeC.iterateBendpointContainers()) {
                                    container2Select.add(bc);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (ElementContainer ec : container2Select) {
            addSimpleToSelection(ec);
        }
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
     * @param pid
     */
    public final void setExpanded(final boolean expand, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            String elementHash = ec.getHashString();
            setExpanded(expand, gdcoll, hashString, elementHash, !expand, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * für vergröbern und verfeinern
     *
     * @param pid
     */
    public final void switchExpansionState(final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            boolean expand = !ec.isExpanded();
            String elementHash = ec.getHashString();
            setExpanded(expand, gdcoll, hashString, elementHash, !expand, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * für vergröbern und verfeinern
     *
     * @param expand
     * @param gdcoll
     * @param szenHash
     * @param elementHash
     * @param doCollapse
     * @param pid
     */
    private static final void setExpanded(final boolean expand, final GDCollection gdcoll, final String szenHash, final String elementHash, final boolean doCollapse, final int pid) {
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

        ModelElement me = ec.getElement();
        List<ElementContainer> directPartContainers = me.getDirectPartContainers(szen);
        if (directPartContainers.isEmpty()) {
            return;
        }

        if (tmpExpandedElements.contains(ec)) {
            return;
        }

        tmpExpansionLevel++;
        tmpExpandedElements.add(ec);

        szen.start_transaction(pid);
        String expandCommand = GDCommands.MODEL_ACTION_SET_ELEMENT_EXPANSION_ON + " " + szenHash + " " + elementHash;
        String collapseCommand = GDCommands.MODEL_ACTION_SET_ELEMENT_EXPANSION_OFF + " " + szenHash + " " + elementHash;
        szen.addRedoCommand(expand ? expandCommand : collapseCommand, pid);
        szen.addUndoCommand(expand ? collapseCommand : expandCommand, pid);

        if (expand) {
            expand(szen, ec, pid);
        } else {
            collapse(szen, ec, doCollapse, pid);
        }

        szen.finish_transaction(pid);
        szen.distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);

        tmpExpansionLevel--;
    }

    /**
     * @param szen
     * @param ec
     * @param pid
     */
    private static final void expand(final GraphDocument szen, final ElementContainer ec, final int pid) {
        ec.setExpanded(true);
        ModelElement me = ec.getElement();
        for (ElementContainer partC : me.getDirectPartContainers(szen)) {
            partC.setVisible(true);
            if (partC.isExpanded()) {
                GDCollection gdcoll = szen.getCollection();
                String szenHash = szen.getHashString();
                String partHash = partC.getHashString();
                setExpanded(true, gdcoll, szenHash, partHash, false, pid);
            }
        }
        // Anpassen der Kanten
        for (Edge edge : me.getEdges()) {
            EdgeContainer kc = (EdgeContainer) edge.getContainer(szen);
            if (kc == null) {
                continue;
            }
            kc.computeBorderPoints();
        }
    }

    /**
     * @param szen
     * @param ec
     * @param doCollapse
     * @param pid
     */
    private static final void collapse(final GraphDocument szen, final ElementContainer ec, final boolean doCollapse, final int pid) {
        if (doCollapse) {
            ec.setExpanded(false);
        }
        ModelElement me = ec.getElement();
        for (ElementContainer partC : me.getDirectPartContainers(szen)) {
            if (tmpExpandedElements.contains(partC)) {
                continue;
            }
            partC.setVisible(false);
            if (partC.isExpanded()) {
                GDCollection gdcoll = szen.getCollection();
                String szenHash = szen.getHashString();
                String partHash = partC.getHashString();
                setExpanded(false, gdcoll, szenHash, partHash, false, pid);
            }
        }
    }

    // --- Event-Verwaltung --- Anfang ---

    /**
     * @param gdl
     */
    public final void addAllTransactionsListener(final LGMChangeListener gdl) {
        //        System.err.println("addAllTransactionsListener " + this);
        //        Sys.err(gdl.getClass().getSimpleName());
        gdcoll.addAllTransactionsListener(gdl);
    }

    /**
     * @param gdl
     */
    public final void removeAllTransactionsListener(final LGMChangeListener gdl) {
        //        System.err.println("removeAllTransactionsListener " + this);
        //        Sys.err(gdl.getClass().getSimpleName());
        gdcoll.removeAllTransactionsListener(gdl);
    }

    /**
     * @param gdl
     */
    public final void addClosedTransactionsListener(final LGMChangeListener gdl) {
        gdcoll.addClosedTransactionsListener(gdl);
    }

    /**
     * @param gdl
     */
    public final void removeClosedTransactionsListener(final LGMChangeListener gdl) {
        gdcoll.removeClosedTransactionsListener(gdl);
    }

    /**
     * Wenn die anderen Parameter aus der Methode <code>distributeEvent(int, ElementContainer, LayerContainer, int)</code> nicht angegeben werden
     * können, kann man hiermit ein allgemeines Ereignis feuern.
     *
     * @param changeType
     */
    public final void distributeEvent(final LGMChangeType changeType) {
        distributeEvent(changeType, TransactionManager.STANDARD_PID);
    }

    /**
     * Wenn die anderen Parameter aus der Methode <code>distributeEvent(int, ElementContainer, LayerContainer, int)</code> nicht angegeben werden
     * können, kann man hiermit ein allgemeines Ereignis feuern.
     *
     * @param changeType
     * @param pid
     */
    public final void distributeEvent(final LGMChangeType changeType, final int pid) {
        distributeEvent(changeType, null, pid);
    }

    public final void distributeEvent(final LGMChangeType changeType, final ElementContainer last_elem, final int pid) {
        gdcoll.distribute(changeType, last_elem, this, pid);
    }

    // --- Event-Verwaltung --- Ende ---

    /**
     * @param args
     * @param pid
     */
    private final void setVisible(final boolean visible, final String[] args, final int pid) {
        //argv[0] = szenHash (optional)
        //argv[1..n] = elementHashes (optional)
        if (args.length == 0) {
            setVisible(visible, pid);
            return;
        }
        String szenHash = args[0];
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        szen.deselectAll(false);
        for (int i = 1; i < args.length; i++) {
            szen.addToSelection(args[i], pid);
        }
        szen.setVisible(visible, pid);
    }

    /**
     * @param visible
     * @param pid
     */
    private final void setVisible(final boolean visible, final int pid) {
        setVisible(visible, hashString, selectedContainer, pid);
    }

    /**
     * @param szenHash
     * @param containerList
     * @param visible
     * @param pid
     */
    private final void setVisible(final boolean visible, final String szenHash, final Collection<ElementContainer> containers, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (!(szen instanceof Szenario)) {
            return;
        }
        if (containers.isEmpty()) {
            return;
        }
        szen.start_transaction(pid);
        StringBuilder commandArgumentsBuilder = new StringBuilder(" ");
        commandArgumentsBuilder.append(szenHash);
        for (ElementContainer ec : containers) {
            ec.setVisible(visible);
            commandArgumentsBuilder.append(" ");
            commandArgumentsBuilder.append(ec.getHashString());
        }
        String commandArguments = commandArgumentsBuilder.toString();

        szen.addUndoCommand((visible ? GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF : GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON) + commandArguments, pid);
        szen.addRedoCommand((visible ? GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON : GDCommands.MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF) + commandArguments, pid);
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
        //alle Kanten im angegebenen Bereich selektieren
        int activeLayerIndex = gdcoll.getActiveLayer();
        LayerContainer activeLayer = layer[activeLayerIndex];
        for (EdgeContainer edgeC : activeLayer.getEdgeContainers()) {
            ElementContainer startC = edgeC.getStartElementContainer();
            if (!startC.isPaintable()) {
                continue;
            }
            ElementContainer endC = edgeC.getEndElementContainer();
            if (!endC.isPaintable()) {
                continue;
            }
            if (isInRect(startC, left_x, left_y, right_x, right_y)) {
                if (isInRect(endC, left_x, left_y, right_x, right_y)) {
                    if (!edgeC.isSelected()) { //this question is much more expensive than the other both, so don't change the order
                        addToSelection(edgeC, PID);
                    }
                }
            }
            //alle Knickpunkte der Edge, die im Auswahlrechteck liegen ebenfall selektieren
            if (edgeC.isVisible()) {
                for (BendpointContainer bc : edgeC.iterateBendpointContainers()) {
                    if (isInRect(bc, left_x, left_y, right_x, right_y)) {
                        addToSelection(bc, PID);
                    }
                }
            }
        }
        //alle Node im angegebenen Bereich selektieren
        for (NodeContainer nc : activeLayer.getGraphNodeContainers()) {
            if (nc.isPaintable()) {
                if (isInRect(nc, left_x, left_y, right_x, right_y)) {
                    if (!nc.isSelected()) {
                        addToSelection(nc, PID);
                    }
                }
            }
        }
        finish_transaction(PID, false);
        distributeEvent(SELECTION_CHANGED, PID);
    }

    /**
     * @param ec
     * @param rect_left_x
     * @param rect_left_y
     * @param rect_right_x
     * @param rect_right_y
     * @return
     */
    private boolean isInRect(final ElementContainer ec, final int rect_left_x, final int rect_left_y, final int rect_right_x, final int rect_right_y) {
        int coord = ec.getX();
        if (coord < rect_right_x && coord > rect_left_x) {
            coord = ec.getY();
            if (coord < rect_right_y && coord > rect_left_y) {
                return true;
            }
        }
        return false;
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
        }
        distributeEvent(SELECTION_CHANGED, TransactionManager.STANDARD_PID);
    }

    /**
     *
     */
    public void selectAll() {
        final int PID = TransactionManager.STANDARD_PID;
        start_transaction(PID, false);
        deselectAll(true);
        for (int i = 0; i < layer.length; i++) {
            LayerContainer lc = layer[i];
            for (ElementContainer ec : lc.getGraphNodeContainers()) {
                gdcoll.addToSelection(ec);
            }
            for (ElementContainer ec : lc.getEdgeContainers()) {
                gdcoll.addToSelection(ec);
            }
            for (ElementContainer ec : lc.getBendpointContainers()) {
                gdcoll.addToSelection(ec);
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
     * @return the {@link SelectionHighlighter}
     */
    public final SelectionHighlighter getSelectionHighlighter() {
        return selectionHighlighter;
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
        distributeEvent(SELECTION_CHANGED, mc, pid);
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
        distributeEvent(SELECTION_CHANGED, ec, pid);
    }

    /**
     * Liefert eine Liste aller selektierten {@link ElementContainer}, bei der alle {@link NodeContainer} am Anfang stehen und genau in der
     * Reihenfolge sind, in der sie in der Grafik dargstellt werden.
     *
     * @return
     */
    private List<ElementContainer> getSelectionInGraphOrder() {
        List<ElementContainer> returnList = new ArrayList<>(selectedContainer.size());
        List<ElementContainer> nodeContainers = getElementContainers(Node.class, true, false);
        for (ElementContainer nc : nodeContainers) {
            if (selectedContainer.contains(nc)) {
                returnList.add(nc);
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
     * @param prefix
     * @param elementClass
     *            Typ der Elemente, für das eine nuer Name generiert werden soll. Der neue Namen ist
     *            in diesem GraphDocument eindeutig und besteht aus dem anzeigbaren Elementnamen, einem Leerzeichen
     *            und einer Zahl. Die Zahl ist die kleinste freie Nummer ab 1.
     * @param appendElementClassName
     *            Only if <code>true</code> the dispalyabe class name of the given class will be appended.
     * @return
     */
    protected String getNextNewName(String prefix, final Class<? extends ModelElement> elementClass, final boolean appendElementClassName) {
        if (prefix == null) {
            prefix = "";
        }
        ElementsNameBuilder elementsNameBuilder = getElementsNameBuilder();
        GraphDocument mainDoc = gdcoll.getMainDoc();
        List<ModelElement> modelItems = mainDoc.getModelItems(elementClass);
        if (!prefix.isEmpty()) {
            int lastCharIndex = prefix.length() - 1;
            char lastChar = prefix.charAt(lastCharIndex);
            if (!Character.isWhitespace(lastChar)) {
                prefix += " ";
            }
        }
        String displayableName = appendElementClassName ? elementsNameBuilder.getDisplayableName(elementClass) : "";
        String name = prefix + displayableName + " ";
        String newName = CollectionUtils.getNextIndicatedName(name, modelItems);
        return newName;
    }

    /**
     * @param elementClass
     *            ModelElement-Klasse für das der nächste nummerierte Standardname zurückgegeben werden soll
     * @return nächste nummerierte Standardname für diese Elementart
     */
    protected String getNextNewName(final Class<? extends ModelElement> elementClass) {
        return getNextNewName("", elementClass, true);
    }

    /**
     * @param elementClassName
     * @param pid
     * @return
     */
    public NodeContainer createNodeAndContainer(final String elementClassName, final int pid) {
        MetaModel metaModel = getMetaModel();
        Class<? extends ModelElement> elementClass = metaModel.getClassForName(elementClassName);
        return createNodeAndContainer(elementClass, pid);
    }

    /**
     * @param elementClass
     * @param pid
     * @return
     */
    public NodeContainer createNodeAndContainer(final Class<? extends ModelElement> elementClass, final int pid) {
        return createNodeAndContainer(elementClass, GDCommands.INVALID_NAME, GDCommands.INVALID_DESCRIPTION, GDCommands.INVALID_HASH_STRING, pid);
    }

    /**
     * @param elementClass
     * @param name
     * @param pid
     * @return
     */
    public NodeContainer createNodeAndContainer(final Class<? extends ModelElement> elementClass, final String name, final int pid) {
        return createNodeAndContainer(elementClass, name, GDCommands.INVALID_DESCRIPTION, GDCommands.INVALID_HASH_STRING, pid);
    }

    /**
     * @param elementClass
     * @param name
     * @param description
     * @param pid
     * @return
     */
    public NodeContainer createNodeAndContainer(final Class<? extends ModelElement> elementClass, final String name, final String description, final int pid) {
        return createNodeAndContainer(elementClass, name, description, GDCommands.INVALID_HASH_STRING, pid);
    }

    /**
     * @param elementClass
     * @param name
     * @param description
     * @param hashString
     * @param pid
     * @return
     */
    public NodeContainer createNodeAndContainer(final Class<? extends ModelElement> elementClass, final String name, final String description, final String hashString, final int pid) {
        return createNodeAndContainer(elementClass, name, description, hashString, GDCommands.INVALID_POSITION_X, GDCommands.INVALID_POSITION_Y, GDCommands.INVALID_WIDTH, GDCommands.INVALID_HEIGHT, GDCommands.INVALID_COLOR_RGB, GDCommands.INVALID_SHAPE,
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
    private NodeContainer createNodeAndContainer(final Class<? extends ModelElement> elementClass, final String name, final String description, final String hashString, int x, int y, final int width, final int height, final int rgb,
            final GraphElementLayout.SHAPE form, final int bendpoint_index, final int pid) {
        lastCreated = null;
        start_transaction(pid);
        if (Node.class.isAssignableFrom(elementClass)) {
            Class<? extends Node> nodeClass = elementClass.asSubclass(Node.class);
            //das neue Element im Hauptdokument anlegen
            lastCreated = gdcoll.createNodeAndContainer(nodeClass, name, description, hashString, pid);
        }
        if (lastCreated != null) {
            ModelElement me = lastCreated.getElement();
            if (!me.isUnique()) {
                if (this instanceof Szenario) {
                    lastCreated = addElementToSzenario(this.hashString, lastCreated, pid);
                    x = x != GDCommands.INVALID_POSITION_X ? x : next_x_pos;
                    y = y != GDCommands.INVALID_POSITION_Y ? y : next_y_pos;
                    moveNodeContainer(lastCreated, x, y, width, height, pid);
                }
            }
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

        return me.getContainer(me.isUnique() ? gdcoll.getMainDoc() : this);
    }

    /**
     * @param hashString
     * @return
     */
    public NodeContainer findNodeContainerCoded(final String hashString) {
        if (hashString == null) {
            return null;
        }

        ModelElement me = findNodeCoded(hashString);
        if (me == null) {
            return null;
        }

        return (NodeContainer) me.getContainer(me.isUnique() ? gdcoll.getMainDoc() : this);
    }

    /**
     * @param hashString
     * @return
     */
    public EdgeContainer findEdgeContainerCoded(final String hashString) {
        if (hashString == null) {
            return null;
        }

        ModelElement me = findEdgeCoded(hashString);
        if (me == null) {
            return null;
        }

        return (EdgeContainer) me.getContainer(me.isUnique() ? gdcoll.getMainDoc() : this);
    }

    /**
     * @param hashString
     * @return
     */
    public BendpointContainer findBendpointContainerCoded(final String hashString) {
        if (hashString == null) {
            return null;
        }

        ModelElement me = findBendpointCoded(hashString);
        if (me == null) {
            return null;
        }

        return (BendpointContainer) me.getContainer(me.isUnique() ? gdcoll.getMainDoc() : this);
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
        GraphDocument mainDoc = gdcoll.getMainDoc();
        if (mainDoc != this) {
            return mainDoc.findElementWithUserField(userFieldName, value);
        }

        if (userFieldName == null || value == null) {
            return null;
        }
        for (LayerContainer lc : layer) {
            List<NodeContainer> nodeContainersAlphabetical = lc.getNodeContainersAlphabetical();
            ModelElement me = findElementWithUserField(nodeContainersAlphabetical, userFieldName, value);
            if (me == null) {
                Iterable<EdgeContainer> edgeContainers = lc.getEdgeContainers();
                me = findElementWithUserField(edgeContainers, userFieldName, value);
            }
            if (me != null) {
                return me;
            }
        }
        return null;
    }

    /**
     * @param elementContainers
     * @param userFieldName
     * @param value
     * @return
     */
    private ModelElement findElementWithUserField(final Iterable<? extends ElementContainer> elementContainers, final String userFieldName, final String value) {
        UserFieldDefinitions ufd = gdcoll.getUserFieldDefinitions();
        for (ElementContainer ec : elementContainers) {
            ModelElement me = ec.getElement();
            Class<? extends ModelElement> elementClass = me.getClass();
            UserField uf = ufd.getUserField(elementClass, userFieldName);
            if (uf == null) {
                continue;
            }
            String userFieldInputValue = me.getUserFieldInputValue(uf);
            if (value.equals(userFieldInputValue)) {
                return me;
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
        ModelElement me = findNodeCoded(hashString);
        if (me != null) {
            return me;
        }
        me = findEdgeCoded(hashString);
        if (me != null) {
            return me;
        }
        me = findBendpointCoded(hashString);
        findBendpointContainerCoded(hashString);
        return me;
    }

    /**
     * @param hashString
     * @return
     */
    public Node findNodeCoded(final String hashString) {
        GraphDocument mainDoc = gdcoll.getMainDoc();
        if (mainDoc != this) {
            return mainDoc.findNodeCoded(hashString);
        }
        if (hashString != null) {
            for (LayerContainer lc : layer) {
                for (NodeContainer ec : lc.getNodeContainersAlphabetical()) {
                    String hash = ec.getHashString();
                    if (hashString.equals(hash)) {
                        return ec.getNode();
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
    public Edge findEdgeCoded(final String hashString) {
        GraphDocument mainDoc = gdcoll.getMainDoc();
        if (mainDoc != this) {
            return mainDoc.findEdgeCoded(hashString);
        }
        if (hashString != null) {
            for (LayerContainer lc : layer) {
                for (EdgeContainer edgeC : lc.getEdgeContainers()) {
                    String hash = edgeC.getHashString();
                    if (hashString.equals(hash)) {
                        return edgeC.getEdge();
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
    public Bendpoint findBendpointCoded(final String hashString) {
        GraphDocument mainDoc = gdcoll.getMainDoc();
        if (mainDoc != this) {
            return mainDoc.findBendpointCoded(hashString);
        }
        if (hashString != null) {
            for (LayerContainer lc : layer) {
                for (BendpointContainer bc : lc.getBendpointContainers()) {
                    String hash = bc.getHashString();
                    if (hashString.equals(hash)) {
                        return bc.getBendpoint();
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
            distributeEvent(GROUP_ORDER_CHANGED, layer[ebene], 0);
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
    private static final ModelElement createAddicted(final GraphDocument doc, final ModelElement master, final Class<? extends CompositionEdge> edgeClass, final Class<? extends ModelElement> slaveClass, final String slaveName, final String slaveHashString,
            final int pid) {
        if (master == null || edgeClass == null || slaveClass == null) {
            return null;
        }
        Class<? extends ModelElement> masterClass = master.getClass();
        if (!MetaModel.isConnecting(edgeClass, masterClass, slaveClass)) {
            return null;
        }
        doc.start_transaction(pid);
        if (master.countConnections(edgeClass) >= MetaModel.getMaxMasterToSlaveCardinality(edgeClass)) {
            return null;
        }
        String name = slaveName;
        if (slaveName == null || slaveName.trim().isEmpty()) {
            String masterName = master.getClearName();
            name = doc.getNextNewName(masterName, slaveClass, true);
        }
        GDCollection gdcoll = doc.getCollection();
        GraphDocument mainDoc = gdcoll.getMainDoc();
        NodeContainer slaveContainer = mainDoc.createNodeAndContainer(slaveClass, name, GDCommands.INVALID_DESCRIPTION, slaveHashString, pid);
        if (slaveContainer == null) {
            doc.finish_transaction(pid);
            return null;
        }
        ModelElement slave = slaveContainer.getElement();
        gdcoll.link(edgeClass, master, slave, pid);

        List<Long> times1 = new ArrayList<>();
        List<Long> times2 = new ArrayList<>();
        for (Szenario szen : gdcoll.getSzenarios()) {
            if (master.getContainer(szen) != null) {
                long l = System.currentTimeMillis();
                String szenHash = szen.getHashString();
                szen.addElementToSzenario(szenHash, slaveContainer, pid);
                times1.add(System.currentTimeMillis() - l);
                l = System.currentTimeMillis();
                szen.addict(master, slave, edgeClass, pid);
                times2.add(System.currentTimeMillis() - l);
            }
        }

        doc.finish_transaction(pid);
        doc.distributeEvent(DATA_CHANGED, slaveContainer, pid);
        if (doc != mainDoc) {
            mainDoc.distributeEvent(DATA_CHANGED, slaveContainer, pid); //das hier muss auch noch sein, weil die Dialoge nur am mainDoc lauschen
        }
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
    public static final ModelElement createAddicted(final GraphDocument doc, final ModelElement master, final Class<? extends CompositionEdge> edgeClass, final Class<? extends ModelElement> slaveClass, final String slaveName, final int pid) {
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
    public static final ModelElement createAddicted(final GraphDocument doc, final ModelElement master, final Class<? extends CompositionEdge> edgeClass, final Class<? extends ModelElement> slaveClass, final int pid) {
        return createAddicted(doc, master, edgeClass, slaveClass, GDCommands.INVALID_NAME, pid);
    }

    /**
     * @param me1
     * @param me2
     * @param edgeClass
     * @param pid
     * @return
     */
    public final Edge addict(final ModelElement me1, final ModelElement me2, final Class<? extends CompositionEdge> edgeClass, final int pid) {
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
    public final Edge addict(final String szenHash, final ModelElement me1, final ModelElement me2, final Class<? extends CompositionEdge> edgeClass, final int pid) {
        String simpleEdgeClassName = edgeClass.getSimpleName();
        return addict(szenHash, simpleEdgeClassName, me1, me2, pid);
    }

    /**
     * @param szenHash
     * @param edgeClassName
     * @param nodehash1
     * @param nodehash2
     * @param pid
     * @return
     */
    public final Edge addict(final String szenHash, final String edgeClassName, final String nodehash1, final String nodehash2, final int pid) {
        ModelElement me1 = findElementCoded(nodehash1);
        ModelElement me2 = findElementCoded(nodehash2);
        return addict(szenHash, edgeClassName, me1, me2, pid);
    }

    /**
     * @param szenHash
     * @param edgeClassName
     * @param masterElement
     * @param slaveElement
     * @param edgeClass
     * @param pid
     * @return
     */
    protected final Edge addict(final String szenHash, final String edgeClassName, final ModelElement masterElement, final ModelElement slaveElement, final int pid) {
        if (masterElement == null || slaveElement == null) {
            return null;
        }
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen == null) {
            return null;
        }

        szen.start_transaction(pid);

        MetaModel metaModel = getMetaModel();
        Class<? extends ModelElement> elementClass = metaModel.getClassForName(edgeClassName);
        Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
        List<Edge> edges = masterElement.getEdgesWith(slaveElement, edgeClass);
        if (edges.isEmpty() || !(szen instanceof Szenario)) {
            finish_transaction(pid);
            return null;
        }
        Edge edge = edges.get(0);
        if (!(masterElement instanceof Node) || !(slaveElement instanceof Node)) {
            return edge;
        }
        NodeContainer masterContainer = (NodeContainer) masterElement.getContainer(szen);
        NodeContainer slaveContainer = (NodeContainer) slaveElement.getContainer(szen);
        //slaveContainer ist null, wenn das untergeordnete Element unique ist und keinen Grafikcontainer in jedem Teilmodell hat
        if (slaveContainer != null) {
            Dimension pos = calculateAddictPosition(masterContainer);
            String edgeHash = edge.getHashString();//eigentlich müsste der hier auch beim Undo auf diesen Wert gesetzt werden, aber das passiert im Moment nicht
            String masterHash = masterElement.getHashString();
            String slaveHash = slaveElement.getHashString();
            int slaveX = slaveContainer.getX();
            int slaveY = slaveContainer.getY();
            int slaveWidth = slaveContainer.getWidth();
            int slaveHeight = slaveContainer.getHeight();
            addRedoCommand(MODEL_ACTION_ADDICT + " " + szenHash + " " + edgeClassName + " " + masterHash + " " + slaveHash, pid);
            addUndoCommand(MODEL_ACTION_SET_ELEMENT_POSITION + " " + szenHash + " " + slaveHash + " " + slaveX + " " + slaveY + " " + slaveWidth + " " + slaveHeight, pid);
            slaveContainer.setCoordinates(pos.width, pos.height, slaveWidth, slaveHeight);
            raiseSlaves(masterContainer);
            for (Szenario szenario : gdcoll.getSzenarios()) {
                EdgeContainer kac = (EdgeContainer) edge.getContainer(szenario);
                if (kac != null) {
                    kac.computeBorderPoints();
                }
            }
        }

        szen.finish_transaction(pid);
        szen.distributeEvent(DATA_CHANGED, pid);
        return edge;
    }

    /**
     * Berechnet die Position untergeordneter Elemente auf einem Oberelementcontainer.
     *
     * @param nc
     *            Oberelementcontainer auf dem untergeordnete Elemente positioniert werden sollen
     * @return
     */
    private static final Dimension calculateAddictPosition(final NodeContainer nc) {
        int addictedCount = -1;
        ModelElement me = nc.getElement();
        for (Edge edge : me.getEdges()) {
            if (edge instanceof CompositionEdge) {
                ModelElement slave = ((CompositionEdge) edge).getSlave();
                if (slave != me && slave.isPaintable()) {
                    addictedCount++;
                }
            }
        }

        int x = nc.getX();
        int y = nc.getY();
        int w = nc.getWidth();
        int h = nc.getHeight();

        Dimension retVal = new Dimension(x, y);

        switch (addictedCount % 34) {
        case 14:
            retVal.width = x - w / 7;
            retVal.height = y - h / 3;
            break;
        case 28:
            retVal.width = x - w / 5;
            retVal.height = y - h / 3;
            break;
        case 8:
            retVal.width = x - w / 4;
            retVal.height = y - h / 3;
            break;
        case 20:
            retVal.width = x - w / 3;
            retVal.height = y - h / 3;
            break;
        case 0:
            retVal.width = x - (int) (w / 2.5);
            retVal.height = y - h / 3;
            break;
        case 15:
            retVal.width = x + w / 7;
            retVal.height = y + h / 3;
            break;
        case 29:
            retVal.width = x + w / 5;
            retVal.height = y + h / 3;
            break;
        case 9:
            retVal.width = x + w / 4;
            retVal.height = y + h / 3;
            break;
        case 21:
            retVal.width = x + w / 3;
            retVal.height = y + h / 3;
            break;
        case 1:
            retVal.width = x + (int) (w / 2.5);
            retVal.height = y + h / 3;
            break;
        case 16:
            retVal.width = x - w / 7;
            retVal.height = y + h / 3;
            break;
        case 30:
            retVal.width = x - w / 5;
            retVal.height = y + h / 3;
            break;
        case 10:
            retVal.width = x - w / 4;
            retVal.height = y + h / 3;
            break;
        case 22:
            retVal.width = x - w / 3;
            retVal.height = y + h / 3;
            break;
        case 2:
            retVal.width = x - (int) (w / 2.5);
            retVal.height = y + h / 3;
            break;
        case 17:
            retVal.width = x + w / 7;
            retVal.height = y - h / 3;
            break;
        case 31:
            retVal.width = x + w / 5;
            retVal.height = y - h / 3;
            break;
        case 11:
            retVal.width = x + w / 4;
            retVal.height = y - h / 3;
            break;
        case 23:
            retVal.width = x + w / 3;
            retVal.height = y - h / 3;
            break;
        case 3:
            retVal.width = x + (int) (w / 2.5);
            retVal.height = y - h / 3;
            break;
        case 18:
            retVal.width = x - w / 7;
            retVal.height = y;
            break;
        case 32:
            retVal.width = x - w / 5;
            retVal.height = y;
            break;
        case 12:
            retVal.width = x - w / 4;
            retVal.height = y;
            break;
        case 24:
            retVal.width = x - w / 3;
            retVal.height = y;
            break;
        case 4:
            retVal.width = x - (int) (w / 2.5);
            retVal.height = y;
            break;
        case 19:
            retVal.width = x + w / 7;
            retVal.height = y;
            break;
        case 33:
            retVal.width = x + w / 5;
            retVal.height = y;
            break;
        case 13:
            retVal.width = x + w / 4;
            retVal.height = y;
            break;
        case 25:
            retVal.width = x + w / 3;
            retVal.height = y;
            break;
        case 5:
            retVal.width = x + (int) (w / 2.5);
            retVal.height = y;
            break;
        case 26:
            retVal.width = x;
            retVal.height = y + h / 5;
            break;
        case 6:
            retVal.width = x;
            retVal.height = y + h / 3;
            break;
        case 27:
            retVal.width = x;
            retVal.height = y - h / 5;
            break;
        case 7:
            retVal.width = x;
            retVal.height = y - h / 3;
            break;
        }

        return retVal;
    }

    /**
     * @param edgeClass
     * @param direction
     * @param pid
     */
    public final void linkSelected(final Class<? extends Edge> edgeClass, final Direction direction, final int pid) {
        start_transaction(pid);
        ElementContainer lastSelectedContainer = getLastSelected();
        ModelElement lastSelecedElement = lastSelectedContainer.getElement();
        if (direction == BACKWARD) {
            for (ElementContainer ec : selectedContainer) {
                ModelElement me = ec.getElement();
                gdcoll.link(edgeClass, me, lastSelecedElement, pid);
            }
        } else {
            for (ElementContainer ec : selectedContainer) {
                ModelElement me = ec.getElement();
                gdcoll.link(edgeClass, lastSelecedElement, me, pid);
            }
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param edgeClass
     * @param direction
     * @param pid
     */
    public final void unlinkSelected(final Class<? extends Edge> edgeClass, final Direction direction, final int pid) {
        start_transaction(pid);
        ElementContainer lastSelectedContainer = getLastSelected();
        ModelElement lastSelecedElement = lastSelectedContainer.getElement();
        if (direction == BACKWARD) {
            for (ElementContainer ec : selectedContainer) {
                ModelElement me = ec.getElement();
                gdcoll.unlink(me, lastSelecedElement, edgeClass, pid);
            }
        } else {
            for (ElementContainer ec : selectedContainer) {
                ModelElement me = ec.getElement();
                gdcoll.unlink(lastSelecedElement, me, edgeClass, pid);
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
     * @param nodehash
     * @param edgeIndex1
     * @param edgeIndex2
     * @param pid
     */
    public final void swapEdgePositions(final String nodehash, final String edgeIndex1, final String edgeIndex2, final int pid) {
        ModelElement me;
        int pos1, pos2;
        try {
            me = findElementCoded(nodehash);
            pos1 = Integer.parseInt(edgeIndex1);
            pos2 = Integer.parseInt(edgeIndex2);
        } catch (Exception e) {
            Log(e);
            return;
        }
        swapEdgePositions(me, pos1, pos2, pid);
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
            String elementHash = me.getHashString();
            addRedoCommand(MODEL_ACTION_SWAP_EDGE_POSITIONS + " " + elementHash + " " + edgeIndex1 + " " + edgeIndex2, pid);
            addUndoCommand(MODEL_ACTION_SWAP_EDGE_POSITIONS + " " + elementHash + " " + edgeIndex2 + " " + edgeIndex1, pid);
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
     * @param ec
     * @param pid
     */
    public final void z_move_up(final ElementContainer ec, final int pid) {
        int layer = ec.layerFor();
        if (layer < 0) {
            return;
        }
        GraphDocument doc = ec.getGraphDocument();
        String szenHash = doc.getHashString();
        String elementHash = ec.getHashString();
        LayerContainer lc = doc.layer[layer];
        int indexOnLayer = lc.indexOf(ec);
        doc.start_transaction(pid);
        addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP + " " + szenHash + " " + elementHash, pid);
        addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER + " " + szenHash + " " + elementHash + " " + indexOnLayer, pid);
        lc.z_move_up(ec);
        doc.finish_transaction(pid);
        distributeEvent(GROUP_ORDER_CHANGED, lc, pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void z_move_up(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            ElementContainer ec = szen.findContainerCoded(elementHash);
            z_move_up(ec, pid);
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
     * @param ec
     * @param pid
     */
    private final void z_move_down(final ElementContainer ec, final int pid) {
        int layer = ec.layerFor();
        if (layer < 0) {
            return;
        }
        GraphDocument doc = ec.getGraphDocument();

        String szenHash = doc.getHashString();
        String elementHash = ec.getHashString();
        LayerContainer lc = doc.layer[layer];
        int indexOnLayer = lc.indexOf(ec);
        doc.start_transaction(pid);
        addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN + " " + szenHash + " " + elementHash, pid);
        addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER + " " + szenHash + " " + elementHash + " " + indexOnLayer, pid);
        lc.z_move_down(ec);
        doc.finish_transaction(pid);
        distributeEvent(GROUP_ORDER_CHANGED, lc, pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void z_move_down(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            ElementContainer ec = szen.findContainerCoded(elementHash);
            z_move_down(ec, pid);
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
        int layer = ec.layerFor();
        if (layer < 0) {
            return;
        }
        LayerContainer lc = szen.layer[layer];
        int indexOnLayer = lc.indexOf(ec);
        szen.start_transaction(pid);
        addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER + " " + szenHash + " " + elementHash + " " + position, pid);
        addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER + " " + szenHash + " " + elementHash + " " + indexOnLayer, pid);
        lc.z_move(ec, position);
        szen.finish_transaction(pid);
        distributeEvent(GROUP_ORDER_CHANGED, lc, pid);
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
            ElementContainer ec = szen.findContainerCoded(elementHash);
            z_step_up(ec, pid);
        }
    }

    /**
     * @param ec
     * @param pid
     * @param log
     */
    private final void z_step_up(final ElementContainer ec, final int pid, final boolean log) {
        int layer = ec.layerFor();
        if (layer < 0) {
            return;
        }
        GraphDocument doc = ec.getGraphDocument();
        doc.start_transaction(pid, log);
        if (log) {
            String szenHash = doc.getHashString();
            String elementHash = ec.getHashString();
            addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP + " " + szenHash + " " + elementHash, pid);
            addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN + " " + szenHash + " " + elementHash, pid);
        }
        LayerContainer lc = doc.layer[layer];
        lc.z_step_up(ec);
        doc.finish_transaction(pid, log);
        distributeEvent(GROUP_ORDER_CHANGED, lc, pid);
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
        int layer = ec.layerFor();
        if (layer < 0) {
            return;
        }
        GraphDocument doc = ec.getGraphDocument();
        doc.start_transaction(pid);
        String szenHash = doc.getHashString();
        String elementHash = ec.getHashString();
        addRedoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN + " " + szenHash + " " + elementHash, pid);
        addUndoCommand(GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP + " " + szenHash + " " + elementHash, pid);
        LayerContainer lc = doc.layer[layer];
        lc.z_step_down(ec);
        doc.finish_transaction(pid);
        distributeEvent(GROUP_ORDER_CHANGED, lc, pid);
    }

    /**
     * @param szenHash
     * @param elementHash
     * @param pid
     */
    public final void z_step_down(final String szenHash, final String elementHash, final int pid) {
        GraphDocument szen = gdcoll.getGraphDocumentCoded(szenHash);
        if (szen instanceof Szenario) {
            ElementContainer ec = szen.findContainerCoded(elementHash);
            z_step_down(ec, pid);
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
     * Sets the horizontal text position of the label for all selected nodes graph.
     *
     * @param mode
     * @param pid
     */
    public final void setTextPositionHorizontal(final TextPositionHorizontal mode, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            setTextPositionHorizontal(mode, ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * Sets the vertical text position of the label for the ElemntContainer.
     *
     * @param mode
     * @param ec
     * @param pid
     */
    private final void setTextPositionHorizontal(final TextPositionHorizontal mode, final ElementContainer ec, final int pid) {
        if (ec == null) {
            return;
        }
        GraphElementLayout layout = ec.get3LGMLayout();
        if (layout == null) {
            return;
        }
        if (layout.textPositionHorizontal == mode) {
            return;
        }
        GraphDocument szen = ec.getGraphDocument();
        if (!(szen instanceof Szenario)) {
            return;
        }
        start_transaction(pid);
        String szenHash = szen.getHashString();
        String elementHash = ec.getHashString();
        TextPositionHorizontal textPositionHorizontal = layout.textPositionHorizontal;
        addUndoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL + " " + szenHash + " " + elementHash + " " + textPositionHorizontal, pid);
        addRedoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL + " " + szenHash + " " + elementHash + " " + mode, pid);
        layout.textPositionHorizontal = mode;
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, pid);
    }

    /**
     * Sets the vertical text position of the label for all selected nodes graph.
     *
     * @param mode {@link TextPositionVertical#TOP}, {@link TextPositionVertical#CENTER} or {@link TextPositionVertical#BOTTOM}
     * @param pid
     */
    public final void setTextPositionVertical(final TextPositionVertical mode, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            setTextPositionVertical(mode, ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * Sets the vertical text position of the label for the ElemntContainer.
     *
     * @param mode
     * @param ec
     * @param pid
     */
    private final void setTextPositionVertical(final TextPositionVertical mode, final ElementContainer ec, final int pid) {
        if (ec == null) {
            return;
        }
        GraphElementLayout layout = ec.get3LGMLayout();
        if (layout == null) {
            return;
        }
        if (layout.textPositionVertical == mode) {
            return;
        }
        GraphDocument szen = ec.getGraphDocument();
        if (!(szen instanceof Szenario)) {
            return;
        }
        start_transaction(pid);
        String szenHash = szen.getHashString();
        String elementHash = ec.getHashString();
        TextPositionVertical textPositionVertical = layout.textPositionVertical;
        addUndoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL + " " + szenHash + " " + elementHash + " " + textPositionVertical, pid);
        addRedoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL + " " + szenHash + " " + elementHash + " " + mode, pid);
        layout.textPositionVertical = mode;
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, pid);
    }

    /**
     * Sets the HTML text alignment of the label for all selected nodes graph.
     *
     * @param mode
     * @param pid
     */
    public final void setTextAlignmentHTML(final TextAlignmentHTML mode, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : selectedContainer) {
            setTextAlignmentHTML(mode, ec, pid);
        }
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, pid);
    }

    /**
     * Sets the vertical text position of the label for the ElemntContainer.
     *
     * @param mode
     * @param ec
     * @param pid
     */
    private final void setTextAlignmentHTML(final TextAlignmentHTML mode, final ElementContainer ec, final int pid) {
        if (ec == null) {
            return;
        }
        GraphElementLayout layout = ec.get3LGMLayout();
        if (layout == null) {
            return;
        }
        if (layout.textAlignmentHTML == mode) {
            return;
        }
        GraphDocument szen = ec.getGraphDocument();
        if (!(szen instanceof Szenario)) {
            return;
        }
        start_transaction(pid);
        String szenHash = szen.getHashString();
        String elementHash = ec.getHashString();
        TextAlignmentHTML textAlignmentHTML = layout.textAlignmentHTML;
        addUndoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML + " " + szenHash + " " + elementHash + " " + textAlignmentHTML, pid);
        addRedoCommand(GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML + " " + szenHash + " " + elementHash + " " + mode, pid);
        layout.textAlignmentHTML = mode;
        ModelElement me = ec.getElement();
        me.updateHTMLName(ec);
        finish_transaction(pid);
        distributeEvent(ELEMENT_GRAPHICS_CHANGED, ec, pid);
    }

    /**
     * @param hashCode
     * @param name
     * @param pid
     */
    public final void setName(final ModelElement me, final String name, final int pid) {
        if (me == null) {
            return;
        }
        start_transaction(pid);
        String elementHash = me.getHashString();
        String newName = getParseSaveString(name);
        String oldName = me.getName();
        oldName = getParseSaveString(oldName);
        //falls in derselben Transaction der Name mehrfach geändert wird, soll das nur 1 Mal geloggt werden
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_NAME + " " + elementHash, newName, pid);
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_NAME + " " + elementHash, oldName, pid);
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

        newName = getDecodedParseSaveString(name);
        me.setName(newName);
        //irgendein Container dieses Elementes muss ins Event gapackt werden. Welcher ist egal, da eigentlich das Element selbst wichtig wäre
        ElementContainer ec = me.getContainer(this);
        finish_transaction(pid);
        distributeEvent(ELEMENT_NAME_CHANGED, ec, pid);
    }

    /**
     * @param hashString
     * @param description
     * @param pid
     */
    public final void setDescription(final ModelElement me, final String description, final int pid) {
        if (me == null) {
            return;
        }
        start_transaction(pid);
        String elementHash = me.getHashString();
        String newDescription = getParseSaveString(description);
        String oldDescription = me.getDescription();
        oldDescription = getParseSaveString(oldDescription);
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_DESCRIPTION + " " + elementHash, newDescription, pid);
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_DESCRIPTION + " " + elementHash, oldDescription, pid);
        newDescription = getDecodedParseSaveString(description);
        me.setDescription(newDescription);
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
        GraphDocument mainDoc = gdcoll.getMainDoc();
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
     * @param value
     * @param pid
     */
    public void setUserFieldValue(final ModelElement me, final UserField userField, final String value, final int pid) {
        start_transaction(pid);
        String elementHash = me.getHashString();
        String userFieldHash = userField.getHashCode();
        String newValue = getParseSaveString(value, true);
        String oldValue = userField.getValue(me);
        oldValue = getParseSaveString(oldValue, true);
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_USER_FIELD_VALUE + " " + elementHash + " " + userFieldHash, newValue, pid);
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_USER_FIELD_VALUE + " " + elementHash + " " + userFieldHash, oldValue, pid);
        newValue = getDecodedParseSaveString(value);
        me.setUserFieldInputValue(userField, newValue);
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
        MetaModel metaModel = getMetaModel();
        Class<? extends ModelElement> edgeElementClass = metaModel.getClassForName(userFieldHashToReplaceOrSimpleEdgeClassName);
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
        addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_USER_FIELD_WEIGHT_REPLACEMENT + " " + modelElementHash + " " + userFieldHashToReplaceOrSimpleEdgeClassName, hashReplacement, pid);
        addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_USER_FIELD_WEIGHT_REPLACEMENT + " " + modelElementHash + " " + userFieldHashToReplaceOrSimpleEdgeClassName, oldUserFieldHashReplacement, pid);
        finish_transaction(pid);

    }

    public final void setOptional(final Edge edge, final boolean value, final int pid) {
        if (!(edge instanceof OptionalEdge)) {
            return;
        }
        OptionalEdge optional = (OptionalEdge) edge;
        //optinality really switched? -> write Undo-Redo-Commands
        if (optional.setOptional(value)) {
            start_transaction(pid);
            String hash = edge.getHashString();
            String newValue = Boolean.toString(value);
            String oldValue = Boolean.toString(!value);
            addRedoCommandOrReplace(GDCommands.MODEL_ACTION_SET_ELEMENT_OPTIONAL + " " + hash, newValue, pid);
            addUndoCommandIfNotExist(GDCommands.MODEL_ACTION_SET_ELEMENT_OPTIONAL + " " + hash, oldValue, pid);
            finish_transaction(pid);
        }
    }

    /**
     * @param removeAllSpecialInfos
     */
    public final void clearHightLighted(final boolean removeAllSpecialInfos) {
        if (removeAllSpecialInfos) {
            for (LayerContainer lc : layer) {
                for (ElementContainer ec : lc.getGraphNodeContainers()) {
                    ec.removeAllSpecialInfosFromThisContainer();
                }
                for (ElementContainer ec : lc.getEdgeContainers()) {
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
        GraphViewParameter graphViewParameter = Static.getGraphViewParameter(szen);
        szen.adaptGraphViewParameter(graphViewParameter);
        double pageSizeFactor = getPageSizeFactor();
        szen.setPageSizeFactor(pageSizeFactor);
        szen.getMapping().adapt(mapping);
        String otherHashString = szen.getHashString();
        addElementsToSzenario(otherHashString, elements, pid);
        finish_transaction(pid);
        szen.distributeEvent(ACTIVE_LAYER_CHANGED, pid);
        szen.distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param elements
     * @param pid
     */
    public final void addContainerToAllSzenarios(final List<ElementContainer> elements, final int pid) {
        start_transaction(pid);
        MetaModel metaModel = getMetaModel();
        for (Szenario szen : gdcoll.getSzenarios()) {
            if (szen == this) {
                continue;
            }
            start_transaction(pid, false);
            for (ElementContainer ec : elements) {
                ModelElement me = ec.getElement();
                Class<? extends ModelElement> elementClass = me.getClass();
                if (!(me instanceof Node) || szen.isMyElement(me) || metaModel.isUnique(elementClass, getModelCategory())) {
                    continue;
                }
                if (metaModel.isSlaveType(elementClass)) {
                    continue;
                }
                String szenHash = szen.getHashString();
                addElementToSzenario(szenHash, (NodeContainer) ec, pid);
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
        String szenHash = szen.getHashString();
        for (ElementContainer ec : elements) {
            String elementHash = ec.getHashString();
            linkElementToSzenario(szenHash, elementHash, pid);
        }
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, pid);
    }

    /**
     * @param szenHash
     * @param pid
     */
    public final void linkElementsToSzenario(final String szenHash, final Collection<ElementContainer> elements, final int pid) {
        start_transaction(pid);
        for (ElementContainer ec : elements) {
            String elementHash = ec.getHashString();
            linkElementToSzenario(szenHash, elementHash, pid);
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
            GraphDocument mainDoc = gdcoll.getMainDoc();
            ec = mainDoc.findContainerCoded(hashCode);
        }
        linkElementToSzenario(szenHashString, ec, pid);
    }

    /**
     * @param sourceDocHash
     * @param targetSzenHash
     * @param elementHashCode
     * @param pid
     * @return
     */
    private final ElementContainer addElementToSzenario(final String sourceDocHash, final String targetSzenHash, final String elementHashCode, final int pid) {
        GraphDocument sourceDoc = sourceDocHash == null ? gdcoll.getMainDoc() : gdcoll.getGraphDocumentCoded(sourceDocHash);
        NodeContainer ec = sourceDoc.findNodeContainerCoded(elementHashCode);
        if (ec != null) {
            return addElementToSzenario(targetSzenHash, ec, pid);
        }
        return null;
    }

    /**
     * @param targetSzenHash
     * @param sourceContainer
     * @param pid
     */
    protected final NodeContainer addElementToSzenario(final String targetSzenHash, final NodeContainer sourceContainer, final int pid) {
        if (sourceContainer == null) {
            return null;
        }
        ModelElement me = sourceContainer.getElement();
        if (me instanceof Bendpoint || me.isUnique()) {
            return null;
        }

        GraphDocument targetDoc = gdcoll.getGraphDocumentCoded(targetSzenHash);
        if (!(targetDoc instanceof Szenario)) {
            return null;
        }
        Szenario targetSzenario = (Szenario) targetDoc;

        targetSzenario.start_transaction(pid);

        NodeContainer targetContainer = (NodeContainer) targetSzenario.addContainerCopy(sourceContainer);
        if (targetContainer != null) {
            if (targetContainer != sourceContainer) {
                String elementHash = me.getHashString();
                GraphDocument sourceDoc = sourceContainer.getGraphDocument();
                String sourceDocHash = sourceDoc.getHashString();
                targetSzenario.addUndoCommand(GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL + " " + targetSzenHash + " " + elementHash, pid);
                //Argumente: 1.) Quell-GraphDoc 2.) Zielszenario 3.) Hash des Elementes
                targetSzenario.addRedoCommand(GDCommands.MODEL_ACTION_ADD_ELEMENT_TO_SUBMODEL + " " + sourceDocHash + " " + targetSzenHash + " " + elementHash, pid);
                targetSzenario.createEdgeContainer(targetContainer, sourceDoc, true, pid);
            }
            targetSzenario.addToSelection(targetContainer, pid);
            targetSzenario.raiseSlaves(targetContainer);
        }
        targetSzenario.finish_transaction(pid);
        targetSzenario.distributeEvent(DATA_CHANGED, pid);
        return targetContainer;
    }

    /**
     * @param szenHashString
     * @param ec
     * @param pid
     */
    public final void linkElementToSzenario(String szenHashString, final ElementContainer ec, final int pid) {
        if (ec instanceof BendpointContainer) {
            return;
        }
        start_transaction(pid);
        ModelElement me = ec.getElement();
        String elementHash = me.getHashString();
        String oldSzen = me.getAssociatedDoc();
        szenHashString = "null".equals(szenHashString) ? null : szenHashString;
        me.setAssociatedDoc(szenHashString);
        String oldSzenHash = oldSzen == null ? "null" : oldSzen;
        addUndoCommand(GDCommands.MODEL_ACTION_LINK_ELEMENT_TO_SUBMODEL + " " + oldSzenHash + " " + elementHash, pid);
        addRedoCommand(GDCommands.MODEL_ACTION_LINK_ELEMENT_TO_SUBMODEL + " " + szenHashString + " " + elementHash, pid);
        finish_transaction(pid);
        distributeEvent(DATA_CHANGED, ec, 0);
    }

    /**
     * @param nc
     * @param pid
     * @param log
     */
    public void moveDependentNodeContainersUp(final NodeContainer nc, final int pid, final boolean log) {
        if (!isMyElement(nc)) {
            return;
        }
        start_transaction(pid, log);
        MetaModel metaModel = getMetaModel();
        Node node = nc.getNode();
        Class<? extends Node> nodeClass = node.getClass();
        for (ElementaryMetaPath elementaryMetaPath : metaModel.getCopyDependencies(nodeClass)) {
            Collection<ElementContainer> dependentObjects = PathFunctions.getConnectedContainer(node, this, elementaryMetaPath);
            for (ElementContainer dependentContainer : dependentObjects) {
                ModelElement dependentNode = dependentContainer.getElement();
                if (!isMyElement(dependentNode)) {
                    continue;
                }
                int nodeLayer = node.layerFor();
                int dependentNodeLayer = dependentNode.layerFor();
                LayerContainer lc1 = getLayer(nodeLayer);
                LayerContainer lc2 = getLayer(dependentNodeLayer);
                if (lc1 == lc2) {
                    while (lc1.indexOf(dependentContainer) < lc1.indexOf(nc)) {
                        z_step_up(dependentContainer, pid, log);
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
        if (!selectedContainer.isEmpty()) {
            ElementContainer lastSelected = getLastSelected();
            String targetHash = lastSelected.getHashString();
            List<ElementContainer> selection = new ArrayList<>(selectedContainer);
            for (ElementContainer ec : new ArrayList<>(selection)) {
                String element2JoinHash = ec.getHashString();
                joinElements(element2JoinHash, targetHash, pid);
            }
            //nach dem Join kann man kein Undo mehr machen -> alle Undo-Kommandos davor auch löschen
            gdcoll.getTransStackTable().clear();
        }
    }

    /**
     * 2 Elemente vereinen, d.h alle Eigenschaften des einen werden an das andere übertragen und ersteres gelöscht.
     *
     * @param removeElementHashString
     *            das abschliessend zu löschende Element
     * @param remainElementHashString
     *            gibt das verbleibende Element an
     * @param pid
     * @return the joined element (this is the element with remainElementHashString) or <code>null</code> if nothing was joined
     */
    public final ModelElement joinElements(final String removeElementHashString, final String remainElementHashString, final int pid) {
        ModelElement joinedElement = gdcoll.join(removeElementHashString, remainElementHashString, null, pid);
        if (joinedElement != null) {
            String hash = joinedElement.getHashString();
            ElementContainer ec = findNodeContainerCoded(hash);
            if (ec != null && ec instanceof NodeContainer) {
                ModelElement me = ec.getElement();
                for (Edge edge : me.getEdges()) {
                    EdgeContainer kac = (EdgeContainer) edge.getContainer(this);
                    if (kac == null) {
                        continue;
                    }
                    kac.computeBorderPoints();
                }
            }
        }
        return joinedElement;
    }

    /**
     * @return
     */
    @Override
    public final GDCollection getCollection() {
        return gdcoll;
    }

    /**
     * @return
     */
    public final GraphDocument getMainDoc() {
        return gdcoll.getMainDoc();
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
    public final List<ElementContainer> getElementContainers(final Class<? extends ModelElement> clazz) {
        return getElementContainers(clazz, true);
    }

    /**
     * Liefert alle ElementContainer von der Startart der Kante, bei denen diese Kantenart nicht zu den RemovedEdges gehärt.
     *
     * @param edgeClass
     * @return
     */
    public final List<ElementContainer> getElementContainersOfStartClass(final Class<? extends Edge> edgeClass) {
        return getElementContainersOfStartOrEndClass(edgeClass, true);
    }

    /**
     * Liefert alle ElementContainer von der Endart der Kante, bei denen diese Kantenart nicht zu den RemovedEdges gehärt.
     *
     * @param edgeClass
     * @return
     */
    public final List<ElementContainer> getElementContainersOfEndClass(final Class<? extends Edge> edgeClass) {
        return getElementContainersOfStartOrEndClass(edgeClass, false);
    }

    /**
     * Liefert alle ElementContainer von der Endart der Kante, bei denen diese Kantenart nicht zu den RemovedEdges gehärt.
     *
     * @param edgeClass
     * @return
     */
    private final List<ElementContainer> getElementContainersOfStartOrEndClass(final Class<? extends Edge> edgeClass, final boolean startClass) {
        Class<? extends ModelElement> elementClass = startClass ? Edge.getStartClass(edgeClass) : Edge.getEndClass(edgeClass);
        List<ElementContainer> elementContainers = getElementContainers(elementClass, true, true);
        for (int i = elementContainers.size() - 1; i >= 0; i--) {
            ElementContainer ec = elementContainers.get(i);
            ModelElement me = ec.getElement();
            elementClass = me.getClass();
            boolean isValidElementForEdge = startClass ? MetaModel.isStartClass(edgeClass, elementClass) : MetaModel.isEndClass(edgeClass, elementClass);
            if (!isValidElementForEdge) {
                elementContainers.remove(i);
            }
        }
        return elementContainers;
    }

    /**
     * Gibt alle ElementContainer zurück, deren gekapseltes Modellelement von
     * der übergebenen Klasse ist.
     *
     * @param clazz Klasse, die der ModelElement-Klasse der Container entspricht
     * @param includeSubClasses wenn <code>true</code>, werden auch Container mit Elementen von Unterklasse zurück gegeben
     * @return Liste mit ElementContainer deren ModelElement con der übergebenen Art ist (immer alphabetisch sortiert)
     */
    public final List<ElementContainer> getElementContainers(final Class<? extends ModelElement> clazz, final boolean includeSubClasses) {
        return getElementContainers(clazz, includeSubClasses, true);
    }

    /**
     * Gibt alle eine nach der <code>toString()</code>-Methode der ElementContainer
     * sortierte Liste von ElementContainern zurück, deren gekapseltes Modellelement
     * von der übergebenen Klasse ist.<br>
     *
     * @param clazz Klasse, die der ModelElement-Klasse der Container entspricht
     * @param includeSubClasses wenn <code>true</code>, werden auch Container mit Elementen von Unterklasse zurück gegeben
     * @param alphabetical wenn <code>true</code> wird ist die Rückgabeliste alphabetisch
     *            sortiert (das betrifft nur die KnotenContainer, aber nicht die KantenContainer)
     * @return
     */
    public final List<ElementContainer> getElementContainers(final Class<? extends ModelElement> clazz, final boolean includeSubClasses, final boolean alphabetical) {

        //		long start = System.currentTimeMillis();

        ModelCategory modelCategory = getModelCategory();
        MetaModel metaModel = getMetaModel();
        GraphDocument document = metaModel.isUnique(clazz, modelCategory) ? gdcoll.getMainDoc() : this;
        List<ElementContainer> objects = new ArrayList<>();
        //Ebene der gesuchten Elementklasse bestimmen
        int layer = metaModel.layerFor(clazz);
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
            List<Iterable<? extends ElementContainer>> layerElements = new ArrayList<>();
            //Knickpunkte
            Iterable<? extends ElementContainer> containers = null;
            if (clazz == Bendpoint.class) {
                containers = lc.getBendpointContainers();
            } else if (MetaModel.isNodeType(clazz)) {
                containers = alphabetical ? lc.getNodeContainersAlphabetical() : lc.getGraphNodeContainers();
                //Kanten
            } else if (MetaModel.isEdgeType(clazz)) {
                containers = lc.getEdgeContainers();
            }
            if (containers != null) {
                layerElements.add(containers);
            }

            //wenn alle Elemente gesucht werden sollen
            if (clazz == ModelElement.class) {
                //wenn keine Unterklassen zu suchen sind, ist man hier fertig
                if (!includeSubClasses) {
                    return objects;
                }
                //alle Elemente sind Unterklassen von ModelElement -> alle Containerlisten können zur Rückgabeliste hinzugefügt werden
                lc.addBendpointContainers(objects);
                lc.addEdgeContainers(objects);
                lc.addNodeContainers(objects, true);
                //wenn eine Unterklasse von ModelElement gesucht werden soll
            } else {
                //dann wurde oben in layerElements wenigstens eine ElementContainerliste hinzugefügt
                for (Iterable<? extends ElementContainer> ecList : layerElements) {
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
        if (alphabetical && (clazz == Bendpoint.class || !MetaModel.isNodeType(clazz))) {
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
     * @param classes
     *            Klassen der gesuchten Elementart (Node oder Kanten). Steht dieselbe Elementart (oder eine Ober- bzw. Unterklasse) mehrfach darin,
     *            sind dieselben Elemente mehrfach in der Ergebnisliste.
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
     * @see #getModelItems(Class, boolean, boolean, boolean)
     */
    public final List<ModelElement> getModelItems(final Iterable<Class<? extends ModelElement>> classes, final boolean includeSubClasses, final boolean absolutePartsOnly, final boolean alphabetical) {
        List<ModelElement> elements = new ArrayList<>();
        for (Class<? extends ModelElement> elementClass : classes) {
            List<ModelElement> modelItems = GraphDocumentHandler.getModelItems(this, elementClass, includeSubClasses, absolutePartsOnly, alphabetical);
            elements.addAll(modelItems);
        }
        return elements;
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
        return modelElement.getContainer(modelElement.isUnique() ? gdcoll.getMainDoc() : this);
    }

    /**
     * Liefert den Container in diesem GraphDocument für einen übergebenen anderen Container
     * aus einem beliebigen GraphDocument.
     *
     * @param modelElement
     * @return
     */
    public ElementContainer getElementContainer(final ElementContainer ec) {
        if (ec == null) {
            return null;
        }
        ModelElement me = ec.getElement();
        return getElementContainer(me);
    }

    /**
     * Liefert alle Container in diesem GraphDocument für eine Liste von <code>ModellElement</code>s oder
     * von anderen Containern aus einem beliebigen GraphDocument.
     *
     * @param modelElementOrContainerList
     * @return
     */
    public List<ElementContainer> getElementContainers(final Collection<?> modelElementOrContainerList) {
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
    public void sortEdgeContainers() {
        for (int i = 0; i < layer.length; i++) {
            layer[i].sortEdgeContainers();
        }
    }

    /**
     *
     */
    public void initNodeContainers() {
        for (int i = 0; i < layer.length; i++) {
            for (NodeContainer kc : layer[i].getGraphNodeContainers()) {
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
    public void initEdgeContainers() {
        for (int i = 0; i < layer.length; i++) {
            for (BendpointContainer kpC : layer[i].getBendpointContainers()) {
                if (kpC == null) {
                    continue;
                }
                Bendpoint kp = kpC.getBendpoint();
                if (kp == null) {
                    continue;
                }
                EdgeContainer kc = layer[i].getEdgeContainer(kp.getEdgeHash());
                if (kc == null) {
                    continue;
                }
                kc.setBendpointContainer(kpC, kp.getIndex());
                kp.addEdge(kc.getEdge());
            }
            for (EdgeContainer kc : layer[i].getEdgeContainers()) {
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

}
