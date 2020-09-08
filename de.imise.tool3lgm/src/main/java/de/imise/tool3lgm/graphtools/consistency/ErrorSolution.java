package de.imise.tool3lgm.graphtools.consistency;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;

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
    private final MetaPath pathToPropertyDialogElement;

    /**
     * The metapath that must be {@link MetaPath#isAssignable(MetaPath)} to the MetaPath
     * of the panel which should be opened to solve the error
     */
    private final SimpleMetaPath panelMetaPath;

    /**
     * @param metaModel
     * @param targetClass
     * @param edgeClass
     * @param pathToPropertyDialogElement
     * @param edgeClass an edge class that is equivalent to the metapath that must be
     *            {@link MetaPath#isAssignable(MetaPath)} to the MetaPath of the panel
     *            which should be opened to solve the error
     */
    public ErrorSolution(final MetaModel metaModel, final Class<? extends ModelElement> targetClass, final Class<? extends Edge> edgeClass, final MetaPath pathToPropertyDialogElement, final Class<? extends Edge> egdeClass) {
        this(metaModel, targetClass, edgeClass, pathToPropertyDialogElement, createPanelMetaPath(metaModel, pathToPropertyDialogElement, egdeClass));
    }

    /**
     * Creates a {@link SimpleMetaPath} from the end class of the given
     * pathToPropertyDialogElement as the start class of the given edge
     * class over the edge class to the end class of the edge class.
     * The direction of the edge class is derived by the start class.
     *
     * @param metaModel
     * @param pathToPropertyDialogElement
     * @param edgeClass
     * @return
     */
    public static SimpleMetaPath createPanelMetaPath(final MetaModel metaModel, final MetaPath pathToPropertyDialogElement, final Class<? extends Edge> edgeClass) {
        Class<? extends ModelElement> startClass = pathToPropertyDialogElement.getEndClass();
        SimpleMetaPath panelMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, startClass, ModelElement.class, edgeClass);
        return panelMetaPath;
    }

    /**
     * @param metaModel
     * @param targetClass
     * @param edgeClass
     * @param pathToPropertyDialogElement
     * @param panelMetaPath the metapath that must be {@link MetaPath#isAssignable(MetaPath)}
     *            to the MetaPath of the panel which should be opened to solve the error
     */
    public ErrorSolution(final MetaModel metaModel, final Class<? extends ModelElement> targetClass, final Class<? extends Edge> edgeClass, final MetaPath pathToPropertyDialogElement, final SimpleMetaPath panelMetaPath) {
        this.targetClass = targetClass;
        this.edgeClass = edgeClass;
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
     * @return the edgeClass
     */
    public Class<? extends Edge> getEdgeClass() {
        return edgeClass;
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
