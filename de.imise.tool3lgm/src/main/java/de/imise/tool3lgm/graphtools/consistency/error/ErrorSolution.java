package de.imise.tool3lgm.graphtools.consistency.error;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPath;

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
     * @param targetClass
     * @param edgeClass
     * @param pathToPropertyDialogElement
     * @param panelClass
     * @param panelNameResKey
     */
    public ErrorSolution(final Class<? extends ModelElement> targetClass, final Class<? extends Edge> edgeClass, final MetaPath pathToPropertyDialogElement, final Class<? extends ElementDialogPanel> panelClass, final String panelNameResKey) {
        super();
        this.targetClass = targetClass;
        this.edgeClass = edgeClass;
        this.pathToPropertyDialogElement = pathToPropertyDialogElement;
        this.panelClass = panelClass;
        panelName = getResString(panelNameResKey);
    }

    /**
     * @param targetClass
     * @param edgeClass
     * @param panelClass
     * @param panelNameResKey
     * @param edgeClassToPanelElement / public ErrorSolution(Class<? extends ModelElement>
     *            targetClass, Class<? extends Edge> edgeClass, Class<? extends ElementDialogPanel>
     *            panelClass, String panelNameResKey, Class<? extends Edge>
     *            edgeClassToPanelElement, int i){ this(targetClass, edgeClass, (MetaPath)null,
     *            panelClass, panelNameResKey); if (edgeClassToPanelElement!=null) try {
     *            pathToPropertyDialogElement = new MetaPath(targetClass,
     *            Edge.getOther(edgeClassToPanelElement, targetClass), edgeClassToPanelElement); }
     *            catch (Exception e) { e.printStackTrace(); } } /**
     * @param targetClass
     * @param edgeClass
     * @param panelClass
     * @param panelNameResKey
     */
    public ErrorSolution(final Class<? extends ModelElement> targetClass, final Class<? extends Edge> edgeClass, final Class<? extends ElementDialogPanel> panelClass, final String panelNameResKey) {
        this(targetClass, edgeClass, (MetaPath) null, panelClass, panelNameResKey);
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
