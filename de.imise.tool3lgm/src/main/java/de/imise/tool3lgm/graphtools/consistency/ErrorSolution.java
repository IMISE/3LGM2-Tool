package de.imise.tool3lgm.graphtools.consistency;

import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;

/**
 * @author AXS (20.03.2008)
 */
public class ErrorSolution {

    /**
     * Elementklasse, zu der dieser Fehler gehört.
     */
    private final Class<? extends ModelElement> targetClass;

    /**
     * Assoziationsklasse, deren Anzahl für Elemente der <code>targetClass</code> nicht korrekt sein
     * kann.
     */
    private final Class<? extends Edge> edgeClass;

    /**
     * Pfad ausgehend vom targetElement des Fehlers hin zu den Element(en), in dessen
     * Eigenschaftsdialog man den Fehler anzeigen oder beheben kann. In der Regel wird das nur ein
     * Element sein, aber theoretisch können es beliebig viele sein. Wenn dieser Pfad
     * <code>null</code> ist, dann wird davon ausgegangen, dass das angegebene Panel im
     * Eigeschaftsdialog des Elementes mit der <code>targetClass</code> selbst enthalten ist. Diesen
     * speziellen MetaPath braucht man nur zu setzen, wenn der Fehler bei einem Element auftritt,
     * das keinen eigenen Eigenschaftsdialog hat, wie zum Beipsiel <code>AWBKonfiguration</code>.
     * Sie werden im Eigenschaftsdialog von Aufgaben angezeigt und zusammengesetzt.
     */
    private final SimpleMetaPath pathToPropertyDialogElement;

    /**
     * Klasse des Panels, in dem man den Fehler anzeigen bzw. beheben kann. Die Kombination aus
     * <code>panelClass</code> und <code>panelName</code> sollte eindeutig sein.
     */
    private final Class<? extends ElementDialogPanel> panelClass;

    /**
     * Name des Panels, in dem man den Fehler anzeigen bzw. beheben kann. Die Kombination aus
     * <code>panelClass</code> und <code>panelName</code> sollte eindeutig sein.
     */
    private final String panelName;

    /**
     * @param metaModel
     * @param targetClass
     * @param edgeClass
     * @param pathToPropertyDialogElement
     * @param panelClass
     * @param panelNameResKey
     */
    public ErrorSolution(final MetaModel metaModel, final Class<? extends ModelElement> targetClass, final Class<? extends Edge> edgeClass, final SimpleMetaPath pathToPropertyDialogElement, final Class<? extends ElementDialogPanel> panelClass,
            final String panelNameResKey) {
        this.targetClass = targetClass;
        this.edgeClass = edgeClass;
        this.pathToPropertyDialogElement = pathToPropertyDialogElement;
        this.panelClass = panelClass;
        panelName = metaModel.getResString(panelNameResKey);
    }

    /**
     * @return the targetClass
     */
    public Class<? extends ModelElement> getTargetClass() {
        return targetClass;
    }

    /**
     * @return the edgeClass
     */
    public Class<? extends Edge> getEdgeClass() {
        return edgeClass;
    }

    /**
     * @return the pathToPropertyDialogElement
     */
    public SimpleMetaPath getPathToPropertyDialogElement() {
        return pathToPropertyDialogElement;
    }

    /**
     * @return the panelClass
     */
    public Class<? extends ElementDialogPanel> getPanelClass() {
        return panelClass;
    }

    /**
     * @return the panelName
     */
    public String getPanelName() {
        return panelName;
    }

}
