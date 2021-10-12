package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.PathFunctions.PathConnectionState;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.path.paths.SimplePath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public interface MetaPath extends BasicMetaPath {

    /**
     * Empty ElementaryMetaPath list
     */
    ImmutableList<ElementaryMetaPath> EMPTY_ELEMENTARY_PATH_LIST = ImmutableList.of();

    String getBaseResKeyOrName();

    /**
     * @return
     */
    InvalidityCheckResult getInvalidityCheckResult();

    /**
     * Liefert <code>true</code>, wenn der Pfad keine Fehler enthält.
     *
     * @return
     */
    boolean isValid();

    /**
     * Returns <code>true</code> if the path can be created in principle. This
     * is the case if it is a simple association sequence without parallel paths
     * or branches to association classes in between and all intermediate
     * element classes are not abstract.
     *
     * @param if <code>true</code>, then it also checks if the EndElement can be
     *            created when the path is created without violating consistency
     *            (i.e. not abstract and the path creates all other elements for
     *            all elements to exist).
     * @return <code>true</code> if this path is createable
     */
    boolean isCreatable(boolean checkCreateEndElement);

    /**
     * Returns <code>true</code> if the path is also
     * {@link #isCreatable(boolean)} but in addition for each newly created
     * intermediate element no other elements may be linked to it. Thus, the
     * intermediate elements may have a maximum cardinality of 1 to all other
     * elements, which is fully satisfied by this new path. For the newly
     * created end element this does not apply, because here the weaker
     * condition is sufficient that by the creation of the path all connections
     * must be there, which the element needs for its existence. Translated with
     * www.DeepL.com/Translator (free version)
     *
     * @param checkCreateEndElement if <code>true</code>, then it also checks if
     *            the EndElement can be created when the path is created without
     *            violating consistency (i.e. not abstract and the path creates
     *            all other elements for all elements to exist).
     * @return <code>true</code> if this path is createable
     */
    boolean isUnambiguousCreatable(boolean checkCreateEndElement);

    /**
     * Prüft, ob der Pfad ausgehend von der Startelementart entfernt werden
     * kann, ohne dass das Startelement dadurch inkonsistent wird und ebenfalls
     * gelöscht werden würde, wenn man den Pfad entfernt.
     *
     * @param checkEndElement wenn <code>true</code>, wird genauso für das
     *            Endelement geprüft, ob es inkonsistent und damit gelöscht
     *            werden würde, wenn man den Pfad zwischen ihm und einem
     *            Startelement entfernt.
     * @return <code>true</code> wenn sich der Pfad entfernen lässt, ohne dass
     *         das Startelement oder bei <code>checkEndElement == true</code>
     *         auch das Endelement nicht inkonsistent und damit gelöscht werden,
     *         sonst <code>false</code>.
     */
    boolean isRemoveable(boolean checkEndElement);

    /**
     * Liefert <code>true</code>, wenn der Pfad eine einfache Assoziationsfolge
     * ist (also bei {@link #getElementaryMetaPaths()} nicht <code>null</code>
     * zurück gibt und jeder Einzelpfad die maximale Endkardinalität von 1 hat.
     */
    boolean isSingleConnection();

    /**
     * Liefert <code>true</code>, wenn das erste Element des Pfades nur
     * existieren kann, wenn es mit einem auf dem Pfad dahinter liegenden
     * Element verbunden ist. Das wird gebraucht, um zu entscheiden, ob ein neu
     * angelegtes EndElement des Pfades immer sofort verbunden werden muss.
     *
     * @return
     */
    boolean isStartDependent();

    /**
     * Liefert <code>true</code>, wenn das letzte Element des Pfades nur
     * existieren kann, wenn es mit einem auf dem Pfad davor liegenden Element
     * verbunden ist. Das wird gebraucht, um zu entscheiden, ob ein neu
     * angelegtes EndElement des Pfades immer sofort verbunden werden muss.
     *
     * @return
     */
    boolean isEndElementDependent();

    /**
     * Liefert den MetaPfad der die Gegenricthung beschreibt oder
     * <code>null</code>, wenn es einen solchen nicht gibt.
     *
     * @return the otherDirectionPath
     */
    MetaPath getOtherDirection();

    /**
     * Liefert eine Folge von Elementarpfaden, wenn sich dieser Pfad so bilden
     * lässt, ansonsten kommt eine leere Liste zurück. Alle parallelen Pfade
     * geben hier leere Liste zurück. {@link SerialMetaPath} geben nur keine
     * leere Liste zurück, wenn sie im innersten ein einzelner Pfad sind ohne
     * parallele oder rekursive Pfade sind.
     *
     * @return
     */
    List<ElementaryMetaPath> getElementaryMetaPaths();

    /**
     * @return The number of elementary metapaths, if this liset can be formed.
     *         If the list of {@link #getElementaryMetaPaths()} is
     *         <code>null</code> or empty, the 0 is returned.
     * @see #getElementaryMetaPaths()
     */
    int getElementaryMetaPathCount();

    /**
     * @return den ersten ElementaryMetaPath aus
     *         {@link #getElementaryMetaPaths()}, wenn die Liste mind. einen
     *         solchen Elementarpfad enthält.
     */
    ElementaryMetaPath getFirstElementaryMetaPath();

    /**
     * @return den letzten ElementaryMetaPath aus
     *         {@link #getElementaryMetaPaths()}, wenn die Liste mind. einen
     *         solchen Elementarpfad enthält.
     */
    ElementaryMetaPath getLastElementaryMetaPath();

    /**
     * Returns the connection class of the path step with the passed index in
     * the element path list of this path. With index 0, this is the more
     * special of the end class of the first elementary metapath and the start
     * class of the next elementary metapath. The path step with the index of
     * metapath length -1 is the end class of the last elementary metapath = end
     * class of the whole elementary metapath list. The start class of the
     * complete metapath is not accessible through this function.
     *
     * @param pathStepIndex
     * @return
     */
    Class<? extends ModelElement> getElementaryMetaPathStepConnectingClass(int pathStepIndex);

    /**
     * Returns the connection class of the path step with the passed index in
     * the element path list of this path. With index 0, this is the more
     * special of the end class of the first submetapath and the start class of
     * the next submetapath. The path step with the index of metapath length -1
     * is the end class of the last submetapath = end class of the whole
     * sunmetapath list. The start class of the complete metapath is not
     * accessible through this function.
     *
     * @param pathStepIndex
     * @return
     */
    Class<? extends ModelElement> getSubMetaPathStepConnectingClass(int pathStepIndex);

    /**
     * @return Liste aller {@link MetaPath}, die dieser MetaPfad enthält.
     */
    List<MetaPath> getSubMetaPaths();

    /**
     * @param index
     * @return the sub metapath at the index
     */
    MetaPath getSubMetaPath(final int index);

    /**
     * @return the number of contained metapaths
     */
    int getSubMetaPathCount();

    /**
     * @param elementaryMetaPaths If <code>true</code>, then the list of
     *            ElementaryMetaPaths is returned. If false, then the list of
     *            SubMetaPaths is returned.
     * @return
     */
    public List<MetaPath> getSubMetaPaths(final boolean elementaryMetaPaths);

    /**
     * Liefert <code>false</code>, wenn der Pfad in beide Richtungen dasselbe
     * bedeutet. Dafür muss er dieselben Elementarten miteinander verbinden und
     * denselben Namen in beiden Richtungen tragen. Z.B können 2 physische
     * DV-Bausteine über Datenübertragungsverbindungen miteinander verbunden
     * sein. Diese Verbindung heißt in jede der beiden Richtungen "ist verbunden
     * mit" und verbindet dieselbe Elementart miteinander. Der dazugehörige
     * Elementarpfad ist also undirected. Dasselbe ist aber auch für
     * {@link SerialMetaPath}s möglich, wenn z.B. die beiden physischen
     * DV-Bausteine Schnittstellen beitzen würden (was sie im aktuellen
     * Metamodell nicht haben) und diese dann über eine
     * Datenübertragungsverbindung mit der beidseitigen Bedeutung "ist verbunden
     * mit" verbunden sind, dann bedeutet der Pfad auch in beide Richtungen
     * dasselbe, nämlich "Phys. DV-Baustein besitzt Schnittstelle ist verbunden
     * mit Schnittstelle gehört zu Phys. DV-Baustein". Die Umkehrrichtung dieses
     * Pfades ist er selbst und somit ist er undirected.
     *
     * @return <code>true</code> wenn Vorwärts- und Rückwärtsrichtungen
     *         unterschiedliche Bedeutung haben
     */
    boolean isDirected();

    /**
     * Liefert <code>true</code>, wenn der Metapfad irgendwo eine
     * {@link PartOfVerbindung} enthält.
     *
     * @return
     */
    boolean containsPropertyTransferEdge();

    /**
     * @param other
     * @return only <code>true</code> if this and the other metapath have an
     *         assignable start class, an assignable end class, an assignable
     *         edge class, the same direction and the same type. Assignable only
     *         means that one of the class must be a subclass of the other
     *         (which is sub and which super dosn't matters).
     */
    boolean isAssignable(MetaPath other);

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen
     * Elemente.
     *
     * @param me Ausgangselement
     * @param multiple Wenn <code>true</code> sind mehrfach verbundene Element
     *            auch mehrfach in der Ergebnisliste, bei <code>false</code> ist
     *            jedes Element nur einmal enthalten.
     * @return
     */
    List<ModelElement> getConnectedElements(ModelElement me, boolean multiple);

    /**
     * @param me
     * @return
     */
    List<ModelElement> getConnectedElements(ModelElement me);

    /**
     * Liefert eine Sammlung aller Elemente, die über diesen Pfad mit den
     * übergebenen Elementen verbunden sind.
     *
     * @param modelElements
     * @return
     */
    List<ModelElement> getConnectedElements(Collection<ModelElement> modelElements);

    /**
     * Liefert eine Sammlung aller Elemente, die über diesen Pfad mit den
     * übergebenen Elementen verbunden sind.
     *
     * @param modelElements Ausgangselemente
     * @param multiple Wenn <code>true</code> enthält die Rückgabesammlung
     *            dieselben Elemente sooft, wie sie mit Elementen der
     *            Ausgangliste über diesen Pfad verbunden sind. Bei
     *            <code>false</code> ist jedes Element nur einmal enthalten.
     * @return
     */
    List<ModelElement> getConnectedElements(Collection<ModelElement> modelElements, boolean multiple);

    /**
     * @param me
     * @param doc
     * @return
     */
    List<ElementContainer> getConnectedContainer(ModelElement me, GraphDocument doc);

    /**
     * @param me
     * @param doc
     * @param forlast
     * @return
     */
    List<ElementContainer> getConnectedContainer(ModelElement me, GraphDocument doc, boolean forlast);

    /**
     * @param startElement
     * @param endElement
     * @param searchParents
     * @param searchParts
     * @return
     */
    PathConnectionState getPathConnectionState(ModelElement startElement, ModelElement endElement, boolean searchParents, boolean searchParts);

    /**
     * @param startElement
     * @param endElement
     * @return
     */
    PathConnectionState getPathConnectionState(ModelElement startElement, ModelElement endElement);

    /**
     * @param startElement
     * @param endElement
     * @return
     */
    boolean isConnected(ModelElement startElement, ModelElement endElement);

    /**
     * @param startElement
     * @param endElement
     * @return
     */
    boolean isDirectConnected(ModelElement startElement, ModelElement endElement);

    /**
     * Liefert einen Ergebnisbaum, der alle eventuell vorhandenen Pfade
     * ausgehend vom übergebenen Element aufspannt
     *
     * @param startElement
     * @return
     */
    PathResultTreeModel getResultTree(ModelElement startElement);

    /**
     * @param startElement
     * @param endElement
     * @return All paths in a treemodel which exist between the given elements.
     */
    PathResultTreeModel getResultTree(final ModelElement startElement, final ModelElement endElement);

    /**
     * Liefert einen Ergebnisbaum, der alle eventuell vorhandenen Pfade
     * ausgehend vom übergebenen Element aufspannt
     *
     * @param startElement
     * @param keepIncompleteBranches
     * @return
     */
    PathResultTreeModel getResultTree(ModelElement startElement, boolean keepIncompleteBranches);

    /**
     * @param startElements
     * @return
     */
    PathResultTreeModel getResultTree(Collection<ModelElement> startElements);

    /**
     * @param startElements
     * @param keepIncompleteBranches
     * @return
     */
    PathResultTreeModel getResultTree(Collection<ModelElement> startElements, boolean keepIncompleteBranches);

    /**
     * @param startElements
     * @return
     */
    PathResultTreeModel getResultTree(List<Collection<ModelElement>> startElements);

    /**
     * @param startElements
     * @param keepIncompleteBranches
     * @return
     */
    PathResultTreeModel getResultTree(List<Collection<ModelElement>> startElements, boolean keepIncompleteBranches);

    /**
     * Return the Paths between the startElement and endElement specified by the
     * given metaPath if it exists.
     *
     * @param startElement
     * @param endElement
     * @param simpleMetaPath
     * @return
     */
    List<SimplePath> getSimplePaths(final ModelElement startElement, final ModelElement endElement);

}