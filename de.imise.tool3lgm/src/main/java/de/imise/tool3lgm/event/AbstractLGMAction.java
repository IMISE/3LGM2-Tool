package de.imise.tool3lgm.event;

import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.matrixview.TableInternalFrame;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.util.swing.event.ExtendedAction;

/**
 * Oberklasse aller Actions in diesem Package. Ist die einzige nach außen hin sichtbare Klasse.
 *
 * @author fstephan
 */
public abstract class AbstractLGMAction extends ExtendedAction {

    /** Schlüssel für das Attribut <em>Prüfen ob ein {@link GraphDocument} ausgewählt ist</em> */
    public static final String ENABLED_WHEN_SELECTED_DOC_NOT_NULL_KEY = "DocNotNullKey";

    /** Schlüssel für den {@link ActionIdentifier} dieser Action */
    public static final String IDENTIFIER_KEY = "IdentifierKey";

    /** Key, für das auszuführende Kommando */
    public static final String COMMAND_KEY = "CommandKey";

    /** Key, für das Argument des auszuführenden Kommandos */
    public static final String ARGUMENT_KEY = "ArgumentKey";

    /** Key für die {@link ModelElement}-Klasse die, durch diese Action erzeugt wird */
    public static final String ELEMENT_CLASS_KEY = "ElementClassKey";

    /** Liste aller {@link Action}s, die einen {@link KeyStroke} besitzen */
    protected static final ArrayList<StaticAction> KEYSTROKE_ACTIONS = new ArrayList<StaticAction>();

    /** Gibt die Liste aller {@link Action}s wieder, die einen {@link KeyStroke} besitzen */
    public static ArrayList<StaticAction> getKeyStrokeActions() {
        return KEYSTROKE_ACTIONS;
    }

    AbstractLGMAction(final String text) {
        this(text, null);
    }

    AbstractLGMAction(final String text, final Icon icon) {
        this(text, icon, icon, null, null, null, null, null);
    }

    AbstractLGMAction(final String text, final Icon smallIcon, final Icon largeIcon, final KeyStroke keyStroke, final String shortDescription, final String longDescription, final String actionCommand, final Boolean initialSelectionState) {
        super(text, smallIcon, largeIcon, keyStroke, shortDescription, longDescription, actionCommand, initialSelectionState);
    }

    // TODO:+++ prüfen, ob das mit selectedDoc hier wirlich ok ist oder ob das richtige doc
    // übergeben werden muss

    /** Erzeugt ein Element der spezifizierten Klasse */
    void createNode(final Class<? extends ModelElement> elementClass) {
        getSelectedDoc().createKnotenWithContainer(elementClass, TransactionManager.STANDARD_PID);
    }

    /** Benachrichtigt das Tool über eine Ändeung der Daten */
    void distributeDataChanged() {
        distributeOptionChange(GraphDocument.DATA_CHANGED);
    }

    /** Benachrichtigt das Tool über eine Ändeung der grafischen Darstellung der Elemente */
    void distributeElementGraphicsChanged() {
        distributeOptionChange(GraphDocument.ELEMENT_GRAPHICS_CHANGED);
    }

    /** Benachrichtigt das Tool über das Eintreten des spezifizierten Ereignisses */
    private void distributeOptionChange(final int eventCode) {
        getTool().distributeOptionChange(eventCode);
    }

    /** Teilt dem Tool einen Wechsel der Ebenenansicht mit */
    void distributeViewChanged() {
        getTool().activeLayerChanged(getSelectedDoc());
    }

    /** Führt das spezifizierte Kommando im momentan selektierten {@link GraphDocument} aus */
    void exec(final GDCommands command) {
        exec(command.name());
    }

    /**
     * Führt das spezifizierte Kommando mit dem spezifizierten Argument im momentan selektierten
     * {@link GraphDocument} aus
     */
    void exec(final GDCommands command, final Object argument) {
        exec(command.name() + (argument != null ? " " + argument.toString() : ""));
    }

    /**
     * Führt den spezifizierten Kommando-<code>String</code> im momentan selektierten
     * {@link GraphDocument} aus
     */
    private void exec(final String commandString) {
        getSelectedDoc().exec(commandString, TransactionManager.STANDARD_PID);
    }

    /** Gibt das gerade aktivierte Frame zurück */
    AbstractInternalFrame getActiveFrame() {
        return Static.getActiveFrame();
    }

    /** Gibt die momentan ausgewählte {@link GDCollection} zurück */
    GDCollection getSelectedCollection() {
        return Static.getSelectedGDCollection();
    }

    /** Gibt das momentan ausgewählte {@link GraphDocument} zurück */
    LGMGraphDocument getSelectedDoc() {
        return Static.getSelectedDoc();
    }

    /** Gibt die gerade laufende Instanz von 3lgm wieder */
    Tool3lgm getTool() {
        return Static.getTool();
    }

    /** Gibt zurück, ob ein gültiges aktives Frame existiert */
    boolean hasActiveFrame() {
        AbstractInternalFrame f = getTool().getActiveFrame();
        return f != null && !(f instanceof TableInternalFrame);
    }

    /** Gibt zurück, ob interne Frames existieren */
    boolean hasInternalFrames() {
        return getTool().getAllFrames().length > 0;
    }

    /** Löst ein Neuzeichnen des Tools aus */
    void repaintTool() {
        getTool().repaint();
    }
}
