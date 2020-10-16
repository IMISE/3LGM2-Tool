package de.imise.tool3lgm.graphtools.consistency.error.solution;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * @author AXS (20.03.2008)
 */
public abstract class ErrorSolution {

    /** Elementklasse, zu der dieser Fehler gehört. */
    private final Class<? extends ModelElement> targetClass;

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
    private final MetaPath pathToPropertyDialogElement;

    /**
     * The metapath that must be {@link MetaPath#isAssignable(MetaPath)} to the MetaPath
     * of the panel which should be opened to solve the error
     */
    private final SimpleMetaPath panelMetaPath;

    /**
     * @param pathToPropertyDialogElement
     * @param panelMetaPath the metapath that must be {@link MetaPath#isAssignable(MetaPath)}
     *            to the MetaPath of the panel which should be opened to solve the error
     */
    public ErrorSolution(final MetaPath pathToPropertyDialogElement, final SimpleMetaPath panelMetaPath) {
        targetClass = pathToPropertyDialogElement != null ? pathToPropertyDialogElement.getStartClass() : panelMetaPath.getStartClass();
        this.pathToPropertyDialogElement = pathToPropertyDialogElement;
        this.panelMetaPath = panelMetaPath;
    }

    /**
     * @return the targetClass
     */
    public Class<? extends ModelElement> getTargetClass() {
        return targetClass;
    }

    /**
     * @return the pathToPropertyDialogElement
     */
    public MetaPath getPathToPropertyDialogElement() {
        return pathToPropertyDialogElement;
    }

    /**
     * @return the panelMetaPath
     */
    public SimpleMetaPath getPanelMetaPath() {
        return panelMetaPath;
    }

}
