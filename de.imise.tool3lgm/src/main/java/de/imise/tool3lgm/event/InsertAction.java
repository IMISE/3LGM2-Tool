package de.imise.tool3lgm.event;

import java.awt.event.ActionEvent;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Von {@link AbstractLGMAction} abgeleitete Klasse zum Einfügen von {@link ModelElement}en in die 3
 * Ebenen.
 * <p>
 * Das Erzeugen von Instanzen dieser Klasse erfolgt durch die Spezifizierung der zu generierenden {@link ModelElement}-Klasse. Intern wird dann dazu
 * über {@link ModelConstants} der passende Name für diese Action gesucht.
 *
 * @author fstephan
 */
@Deprecated
class InsertAction extends AbstractLGMAction {

    /** Gibt alle Actions zum Erzeugen von {@link ModelElement}en der spezifizierten Klassen wieder */
    private static InsertAction[] getActions(final Class<? extends ModelElement>[] treeCreatableLayerNodes) {
        InsertAction[] actions = new InsertAction[treeCreatableLayerNodes.length];
        for (int c = 0; c < treeCreatableLayerNodes.length; c++) {
            actions[c] = new InsertAction(treeCreatableLayerNodes[c]);
        }
        return actions;
    }

    /** Gibt alle Actions zum Erzeugen von {@link ModelElement}en der Fachlichen Ebene wieder */
    public static InsertAction[] getDomainLayerActions() {
        return getActions(ModelConstants.CREATABLE_DOMAIN_LAYER_NODES);
    }

    /** Gibt alle Actions zum Erzeugen von {@link ModelElement}en der Logischen Ebene wieder */
    public static InsertAction[] getLogicalToolLayerActions() {
        return getActions(ModelConstants.CREATABLE_LOGICAL_LAYER_NODES);
    }

    /** Gibt alle Actions zum Erzeugen von {@link ModelElement}en der Physischen Ebene wieder */
    public static InsertAction[] getPhysicalToolLayerActions() {
        return getActions(ModelConstants.CREATABLE_PHYSICAL_LAYER_NODES);
    }

    /** Gibt wieder, ob der aktuelle Kontext ein Einfügen von Elementen erlaubt, oder nicht */
    public static boolean isInsertAvailable() {
        return Static.getSelectedDoc() != null;
    }

    /** Die {@link ModelElement}-Klasse die, durch diese Action erzeugt wird */
    private final Class<? extends ModelElement> elementClass;

    /**
     * Konstruktor
     * <p>
     * Erzeugt neue Instanz dieser Klasse anhand des zur spezifizierten {@link ModelElement}-Klasse passenden {@link ActionIdentifier}
     *
     * @param elementClass {@link ModelElement}-Klasse die, durch diese Action erzeugt wird
     */
    private InsertAction(final Class<? extends ModelElement> elementClass) {
        super(ModelConstants.getDisplayableName(elementClass));
        this.elementClass = elementClass;
        putValue(ELEMENT_CLASS_KEY, elementClass);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (!isEnabled()) {
            return;
        }
        createNode(elementClass);
    }

    @Override
    public boolean isEnabled() {
        return getSelectedDoc() != null;
    }
}
