package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath.Type;
import de.imise.util.ReflectionUtils;
import de.imise.util.collections.CollectionUtils;

/**
 * Pfad der aus einer Hintereianderreihung (Liste) anderer Pfade besteht.
 *
 * @author AXS (26.10.2018) (original AXS (13.10.2010))
 */
public class SequenceMetaPath extends ListMetaPath {

    /**
     * Wahr, wenn sobald einmal verscht wurde, die Gegenrichtung dieses Pfades
     * anzulegen
     */
    protected boolean otherDirectionInitilized = false;

    /**
     * Liste von Elementarpfaden, aus denen dieser Pfad besteht. Diese Liste
     * lässt sich nur anlegen, wenn dieser MetaPfad keine parallelen und keine
     * rekursiven MetaPfade enthählt sondern nur aus {@link ElementaryMetaPath}s
     * besteht oder aus anderen {@link SequenceMetaPath}s, die selbst keine
     * parallelen und keine rekursiven Metapfade enthalten.
     */
    private ImmutableList<ElementaryMetaPath> elementaryMetaPaths = null;

    /**
     * Diese Richtung wird nur zum Erzeugen des Namens gebraucht. Je nachdem
     * welche Rictung hier vermerkt ist, wird an den {@link #baseResKeyOrName}
     * noch "_f" (FORWARD) pder "_b" (BACKWARD) angehängt.
     */
    protected final Direction direction;

    /**
     * @see {@link #isDirected()}
     */
    private final boolean directed;

    /**
     * @param metaPaths
     */
    public SequenceMetaPath(final MetaPath... metaPaths) {
        this(null, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SequenceMetaPath(final String baseResKeyOrName, final MetaPath... metaPaths) {
        this(baseResKeyOrName, Direction.FORWARD, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param direction
     * @param metaPaths
     */
    protected SequenceMetaPath(final String baseResKeyOrName, final Direction direction, final MetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
        this.direction = direction;
        directed = getIsDirected();
    }

    @Override
    protected void initStartEndClasses() {
        if (subMetaPaths != null && !subMetaPaths.isEmpty()) {
            MetaPath firstSubMetaPath = getFirstSubMetaPath();
            startElementClasses = firstSubMetaPath.startElementClasses;
            MetaPath lastSubMetaPath = getLastSubMetaPath();
            endElementClasses = lastSubMetaPath.endElementClasses;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (directed ? 1231 : 1237);
        result = prime * result + (direction == null ? 0 : direction.hashCode());
        result = prime * result + (subMetaPaths == null ? 0 : subMetaPaths.hashCode());
        result = prime * result + (elementaryMetaPaths == null ? 0 : elementaryMetaPaths.hashCode());
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
        SequenceMetaPath other = (SequenceMetaPath) obj;
        if (directed != other.directed) {
            return false;
        }
        if (direction != other.direction) {
            return false;
        }
        if (subMetaPaths == null) {
            if (other.subMetaPaths != null) {
                return false;
            }
        } else if (!subMetaPaths.equals(other.subMetaPaths)) {
            return false;
        }
        if (elementaryMetaPaths == null) {
            if (other.elementaryMetaPaths != null) {
                return false;
            }
        } else if (!elementaryMetaPaths.equals(other.elementaryMetaPaths)) {
            return false;
        }
        return true;
    }

    @Override
    protected String createName() {
        //zuerst versuche, den Resouceneintrag mit dem übergebenen Schlüssel zu finden, aber ohne "_f" oder "_b" am Ende -> setze den und gehe davon aus, dass es keine Rückrichtung gibt (wenn es ihn gibt)
        String name = super.createName();
        if (Strings.isNullOrEmpty(name)) {//das passiert nur, wenn der baseResKeyOrName null oder leer ist
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            name = elementsNameBuilder.getDirectedName(Edge.class.getSimpleName(), direction); //das sorgt dafür , dass der "ist verbunden mit"-Eintrag gefunden wird und der String nicht null ist
        } else if (name.equals(baseResKeyOrName)) { //wenn der Key nicht leer war und nicht schon ein Resourceneintrag ohne "_f" oder "_b" gefunden wurde
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            name = elementsNameBuilder.getDirectedName(name, direction);//versuche einen mit "_f" oder "_b" zu finden (je nach Richtung)
            if (Strings.isNullOrEmpty(name)) { //wenn keiner gefunden wurde
                name = baseResKeyOrName; // setzte den übergbenen nicht leeren Resourcen-String als Namen
            }
        }
        return name;
    }

    public String getAllMetaPathsName() {
        StringBuilder sb = new StringBuilder();
        for (MetaPath metaPath : subMetaPaths) {
            sb.append(metaPath.getFullName());
            sb.append(" -> ");
        }
        sb.setLength(sb.length() - " -> ".length());
        return sb.toString();
    }

    @Override
    public SequenceMetaPath getOtherDirection() {
        // wenn noch nicht bereits einmal versucht wurde den Gegenrichtungspfad zusammenzubauen
        if (!otherDirectionInitilized) {
            otherDirectionInitilized = true;
            // versuchen, die Gegenrichtung zusammen zu bauen
            MetaPath[] otherDirectionMetaPaths = getOtherDirectionMetaPaths();
            // Gegenrichtung für diesen und den Gegenrichtungspfad setzen, wenn es die Gegenrichtung gibt
            if (otherDirectionMetaPaths != null) {
                SequenceMetaPath other = createOtherDirection(baseResKeyOrName);
                other.otherDirection = this;
                other.otherDirectionInitilized = true;
                super.otherDirection = other;
            }
        }
        return (SequenceMetaPath) super.otherDirection;
    }

    /**
     * Legt den eigentlichen Gegenrichtungspfad an, wenn es ihn gibt (also wenn
     * sich jeder Pfad in der Liste auch umdrehen lässt)
     *
     * @param baseResKeyOrName
     * @return
     */
    protected SequenceMetaPath createOtherDirection(final String baseResKeyOrName) {
        MetaPath[] otherDirectionMetaPaths = getOtherDirectionMetaPaths();
        return new SequenceMetaPath(baseResKeyOrName, Direction.BACKWARD, otherDirectionMetaPaths);
    }

    protected MetaPath[] getOtherDirectionMetaPaths() {
        // versuchen, die Gegenrichtung zusammen zu bauen
        MetaPath[] otherDirectionMetaPaths = new MetaPath[subMetaPaths.size()];
        // Gegenrichtung der enthaltenen Einzelpfade in umgekehrter Reihenfolge einfügen
        for (int i = otherDirectionMetaPaths.length - 1; i >= 0; i--) {
            MetaPath currentMetaPath = subMetaPaths.get(i);
            MetaPath otherDirection = currentMetaPath.getOtherDirection();
            if (otherDirection == null) {
                otherDirectionMetaPaths = null;
                break;
            }
            otherDirectionMetaPaths[otherDirectionMetaPaths.length - i - 1] = otherDirection;
        }
        return otherDirectionMetaPaths;
    }

    @Override
    public boolean isStartClass(final Class<? extends ModelElement> elementClass) {
        //wenn sich der ElementarPfad bilden lässt, dann darf die Kantenklasse bei der Startklasse nicht zu den removedEdges gehören
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        if (!elementaryMetaPaths.isEmpty()) {
            ElementaryMetaPath firstElementaryMetaPath = elementaryMetaPaths.get(0);
            if (!firstElementaryMetaPath.isStartClass(elementClass)) {
                return false;
            }
        }
        return super.isStartClass(elementClass);
    }

    @Override
    public boolean isEndClass(final Class<? extends ModelElement> elementClass) {
        //wenn sich der ElementarPfad bilden lässt, dann darf die Kantenklasse bei der Endklasse nicht zu den removedEdges gehören
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        if (!elementaryMetaPaths.isEmpty()) {
            ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(elementaryMetaPaths.size() - 1);
            if (!lastElementaryMetaPath.isEndClass(elementClass)) {
                return false;
            }
        }
        return super.isEndClass(elementClass);
    }

    @Override
    public final boolean isCreatable(final boolean checkCreateEndElement) {
        if (!isValid()) {
            return false;
        }
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        if (elementaryMetaPaths.isEmpty()) {
            return false;
        }
        if (elementaryMetaPaths.size() == 1) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(0);
            return elementaryMetaPath.isCreatable(checkCreateEndElement);
        }
        Class<? extends ModelElement> startClass = getStartClass();
        Class<? extends ModelElement> endClass = getEndClass();
        if (!metaModel.isEditable(startClass)) {
            return false;
        }
        // prüfen, ob die Zwischenelemente angelegt werden können
        int maxElementaryMetaPathsIndex = elementaryMetaPaths.size() - 1;
        for (int i = 0; i < maxElementaryMetaPathsIndex; i++) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
            Type type = elementaryMetaPath.getType();
            if (i == 0 && type == Type.END_WITH_EDGE || type == Type.START_WITH_EDGE) {
                //das muss ja schon da sein, weil es vom StartElement ausgeht -> also anlegbar
                continue;
            }
            //nur Elementarpfade mit einer Kante dazwischen sind anlegbar, wenn die Kantenklasse nicht abstract ist
            if (!elementaryMetaPath.isCreatable(false)) {
                return false;
            }
            ElementaryMetaPath nextElementaryMetaPath = elementaryMetaPaths.get(i + 1);
            Class<? extends ModelElement> pathStepElementClass = getPathStepElementClass(i);
            if (!metaModel.isCreatable(pathStepElementClass, elementaryMetaPath, nextElementaryMetaPath)) {
                return false;
            }
        }
        //jetzt den letzten holen ElementaryMetaPath und je nach Parameter checkCreateEndElement prüfen
        ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(maxElementaryMetaPathsIndex);
        if (checkCreateEndElement) {
            return metaModel.isCreatable(endClass, lastElementaryMetaPath, null);
        }
        Type type = lastElementaryMetaPath.getType();
        if (type == Type.END_WITH_EDGE || type == Type.START_WITH_EDGE) {
            //wenn das endElement nicht angelegt werden soll und der letzte Elementarpfad vom Start- oder EndElement
            //einer Kante auf diese Kante geht, dann müssen ja auch das Start- und EndElement dieser Kante bereits
            //existieren und dieser Schritt braucht nicht grüft werden
            return true;
        }
        //wenn die letzte Kante eine InstanciationEdge und der Richtung des letzten Elementarmetapfades
        //aber von der Instanz auf den Master ist, dann ist dieser letzte Elementarmetapfad trotzdem
        //anlegbar, weil die Funktion createPath(...) in GDCollection dann einfach den Master ableitet
        //wodurch die Insatnz von alleine entsteht und dann der Pfad nur bis zum vorletzten in Vorwärtsrichtung
        //angelegt wird. Theoretisch müsste man solange von hinten prüfen (und nicht nur 1x), ob man den Pfad
        //auch rückwärts durch Instanziierung erzeugen kann, bis man einen Elementarmetapfad findet, der keine
        //Rückwärtsinstanziierung mehr ist. Im Moment reicht es aber, nur einen Rückwärtsinstanziierungsschritt
        //zu gehen.
        if (lastElementaryMetaPath.hasEdgeClass(InstanciationEdge.class)) {
            if (!lastElementaryMetaPath.hasDirection(InstanciationEdge.MASTER_TO_INSTANCE_DIRECTION)) {
                return true;
            }
        }
        if (!metaModel.isEditable(endClass)) {
            return false;
        }
        return lastElementaryMetaPath.isCreatable(false);
    }

    @Override
    public boolean isRemoveable(final boolean checkEndElement) {
        if (isFirstPathElementDependent()) {
            return false;
        }
        if (checkEndElement) {
            if (isLastPathElementDependent()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public final List<ElementaryMetaPath> getElementaryMetaPaths() {
        if (elementaryMetaPaths == null) {
            ImmutableList.Builder<ElementaryMetaPath> simpleMetaPathBuilder = ImmutableList.builder();
            for (MetaPath metaPath : subMetaPaths) {
                List<ElementaryMetaPath> innerMetaPaths = metaPath.getElementaryMetaPaths();
                if (innerMetaPaths.isEmpty()) { //der aktuelle innere Pfad hat keine einfache Elementarpfadliste
                    elementaryMetaPaths = EMPTY_ELEMENTARY_PATH_LIST;
                    break;
                }
                for (ElementaryMetaPath innerMetaPath : innerMetaPaths) {
                    simpleMetaPathBuilder.add(innerMetaPath);
                }
            }
            if (elementaryMetaPaths == null) {
                elementaryMetaPaths = simpleMetaPathBuilder.build();
            }
        }
        return elementaryMetaPaths;
    }

    /**
     * @return Iterable over all ElementaryMetaPaths if this list can be
     *         created. If not, an empty iterable will be returned.
     */
    public Iterable<ElementaryMetaPath> iterableElementaryMetaPaths() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        return CollectionUtils.iterable(elementaryMetaPaths);
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    public boolean getIsDirected() {
        //Start- und Zielklassen müssen in jedem Fall gleich sein, falls die Kante
        if (!getStartClasses().equals(getEndClasses())) {
            return false;
        }
        int metaPathCount = subMetaPaths == null ? 0 : subMetaPaths.size();
        //bei einer ungeraden Anzahl von Pfaden muss der mittlere Pfad selbst undirected sein
        if (metaPathCount % 2 == 1 && !subMetaPaths.get(metaPathCount / 2).isDirected()) {
            return false;
        }
        //Der Pfad muss zur Mitte hin symmetrisch sein, d.h. alle Pfade müssen von beiden Seiten von Außen
        //nach Innen laufend paarweise bis auf den Namen equals mit der jeweiligen Gegenrichtung des anderen
        //Pfades sein. Also wenn es 5 Pfade sind, dann muss Pfad 3 undirected sein, Pfad 1 muss die Gegenrichtung
        //von Pfad 5 sein und Pfad 2 die Gegenrichtung von Pfad 4. Bei einer geraden Anzahl von Pfaden müssen
        //die Pfade von Außen nach Innen immer paarweise ihre Gegenrichtungen sein.
        for (int i = 0; i < metaPathCount / 2; i++) {
            if (!subMetaPaths.get(i).getOtherDirection().equals(metaPathCount - 1 - i, true)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsPropertyTransferEdge() {
        for (MetaPath metaPath : subMetaPaths) {
            if (metaPath.containsPropertyTransferEdge()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert alle Verbindungsklassen zwischen dem MetaPfad mit dem übergebenen
     * Index und dem darauffolgenden. Beim Index Gesamtpfadlänge -1 kommen die
     * Endklassen des letzten Metapfades zurück, ansonsten werden immer
     * paarweise die Endklassen des MetaPfades mit dem übergebenen Index mit den
     * Startklassen des darauffolgenden Metapfades verglichen und wenn sie
     * zuweisungskompatibel sind die speziellere der beiden zum Ergebnisset
     * hinzugefügt. Bei Pfaden, die nur aus Elementarpfaden bestehen enthält das
     * Set genau die Klasse (oder null), die auch bei
     * {@link #getPathStepElementClass(int)} zurück kommt.
     *
     * @param pathStepIndex
     * @return
     */
    public final Set<Class<? extends ModelElement>> getPathStepConnectingClasses(final int pathStepIndex) {
        MetaPath metaPath = subMetaPaths.get(pathStepIndex);
        Set<Class<? extends ModelElement>> endClasses = metaPath.getEndClasses();
        if (pathStepIndex == subMetaPaths.size() - 1) {
            return endClasses;
        }
        MetaPath nextMetaPath = subMetaPaths.get(pathStepIndex + 1);
        Set<Class<? extends ModelElement>> nextStartClasses = nextMetaPath.getStartClasses();
        Set<Class<? extends ModelElement>> pathStepClasses = new HashSet<>();
        for (Class<? extends ModelElement> endClass : endClasses) {
            for (Class<? extends ModelElement> startClass : nextStartClasses) {
                Class<? extends ModelElement> pathStepClass = ReflectionUtils.getMostSpecialClass(endClass, startClass);
                //null tritt ein, wenn die Elemente der aufeinanderfolgenden Elementarpfade nicht zusammenpassen
                if (pathStepClass != null) {
                    //das hier checkt auch bei ElementaryMetaPaths, ob die Kante für die pathStepClass zu den removedEdgeClasses gehört
                    if (metaPath.isEndClass(pathStepClass)) {
                        //das hier checkt auch bei ElementaryMetaPaths, ob die Kante für die pathStepClass zu den removedEdgeClasses gehört
                        if (nextMetaPath.isStartClass(pathStepClass)) {
                            pathStepClasses.add(pathStepClass);
                        }
                    }
                }
            }
        }
        return pathStepClasses;
    }

    /**
     * Liefert die Verbindungsklasse des Pfadschrittes mit dem übergebenen Index
     * in der Elementarpfadliste dieses Pfades. Dies ist beim Index 0 die
     * speziellere der Endklasse des ersten Elementarpfades und der Startklasse
     * des nächsten Elementarpfades. Der Pfadschritt mit dem Index der Pfadlänge
     * -1 ist die Endklasse des letzten Elementarpfades = Endklasse der gesamten
     * Elementarpfadliste. An die Startklasse des Gesamtpfades kommt man mit
     * dieser Funktion nicht.
     *
     * @param pathStepIndex
     * @return
     */
    public final Class<? extends ModelElement> getPathStepElementClass(final int pathStepIndex) {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        if (elementaryMetaPaths.isEmpty()) {
            return null;
        }
        ElementaryMetaPath elementaryMetaPath1 = pathStepIndex >= 0 ? elementaryMetaPaths.get(pathStepIndex) : null;
        ElementaryMetaPath elementaryMetaPath2 = pathStepIndex == elementaryMetaPaths.size() - 1 ? null : elementaryMetaPaths.get(pathStepIndex + 1);
        Class<? extends ModelElement> connectingClass = MetaPathFunctions.getElementaryPathsConnectingClass(elementaryMetaPath1, elementaryMetaPath2);
        return connectingClass;
    }

    public enum InvalidReason {
        INVALID_SEQUENCE_INCOMPATIBLE_PATH_STEP_AND_EDGE_CLASSES,
    }

    @Override
    public InvalidityCheckResult getInvalidityCheckResult() {
        //wenn der Pfad aus Sicht des MetaPath valide ist
        if (super.getInvalidityCheckResult().invalidReason == null) {
            //jeden Einzelpfad durchgehen
            for (int i = 0; i < subMetaPaths.size(); i++) {
                //Hole alle Elementklassen die einen Pfad mit dem nächsten verbinden
                Set<Class<? extends ModelElement>> pathStepElementClasses = getPathStepConnectingClasses(i);
                //2 aufeinanderfolgende Pfade passen nicht zusmammen
                if (pathStepElementClasses.isEmpty()) {
                    invalidityCheckResult = new InvalidityCheckResult(InvalidReason.INVALID_SEQUENCE_INCOMPATIBLE_PATH_STEP_AND_EDGE_CLASSES, i);
                    break;
                }
            }
        }
        return invalidityCheckResult;
    }

}
