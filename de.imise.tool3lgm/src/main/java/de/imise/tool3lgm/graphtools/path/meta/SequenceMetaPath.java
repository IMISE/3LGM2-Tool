package de.imise.tool3lgm.graphtools.path.meta;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Pfad der aus einer Hintereianderreihung (Liste) anderer Pfade besteht.
 *
 * @author AXS (26.10.2018) (original AXS (13.10.2010))
 */
public class SequenceMetaPath extends ListMetaPath {

    /**
     * Wahr, wenn sobald einmal verscht wurde, die Gegenrichtung dieses Pfades anzulegen
     */
    protected boolean otherDirectionInitilized = false;

    /**
     * Liste von Elementarpfaden, aus denen dieser Pfad besteht. Diese Liste lässt sich nur anlegen,
     * wenn dieser MetaPfad keine parallelen und keine rekursiven MetaPfade enthählt sondern nur
     * aus {@link ElementaryMetaPath}s besteht oder aus anderen {@link SequenceMetaPath}s, die selbst
     * keine parallelen und keine rekursiven Metapfade enthalten.
     */
    private ImmutableList<ElementaryMetaPath> simpleMetaPath = null;

    /**
     * <code>true</code> sobald einmal versucht wurde, den simpleMetaPath anzulegen. Falls das nicht
     * geklappt hat, ist er weiterhin <code>null</code>, aber es muss nicht noch einmal versucht werden,
     * ihn zu initilaisieren.
     */
    private boolean simplePathInitialized = false;

    /**
     * Diese Richtung wird nur zum Erzeugen des Namens gebraucht. Je nachdem welche Rictung hier vermerkt ist, wird an den {@link #baseResKeyOrName}
     * noch "_f" (FORWARD) pder "_b" (BACKWARD) angehängt.
     */
    protected final Direction direction;

    /**
     * @see {@link #isDirected()}
     */
    private final boolean directed;

    /**
     * @see #isCreateable()
     */
    private final boolean createable;

    /**
     * @param metaPaths
     */
    public SequenceMetaPath(final AbstractMetaPath... metaPaths) {
        this(null, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SequenceMetaPath(final String baseResKeyOrName, final AbstractMetaPath... metaPaths) {
        this(baseResKeyOrName, Direction.FORWARD, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param direction
     * @param metaPaths
     */
    protected SequenceMetaPath(final String baseResKeyOrName, final Direction direction, final AbstractMetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
        this.direction = direction;
        directed = getIsDirected();
        createable = getIsCreateable();
    }

    @Override
    protected void initStartEndClasses() {
        if (metaPaths != null && metaPaths.size() > 0) {
            startElementClasses = metaPaths.get(0).startElementClasses;
            endElementClasses = metaPaths.get(metaPaths.size() - 1).endElementClasses;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (createable ? 1231 : 1237);
        result = prime * result + (directed ? 1231 : 1237);
        result = prime * result + (direction == null ? 0 : direction.hashCode());
        result = prime * result + (metaPaths == null ? 0 : metaPaths.hashCode());
        result = prime * result + (simpleMetaPath == null ? 0 : simpleMetaPath.hashCode());
        result = prime * result + (simplePathInitialized ? 1231 : 1237);
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
        if (createable != other.createable) {
            return false;
        }
        if (directed != other.directed) {
            return false;
        }
        if (direction != other.direction) {
            return false;
        }
        if (metaPaths == null) {
            if (other.metaPaths != null) {
                return false;
            }
        } else if (!metaPaths.equals(other.metaPaths)) {
            return false;
        }
        if (simpleMetaPath == null) {
            if (other.simpleMetaPath != null) {
                return false;
            }
        } else if (!simpleMetaPath.equals(other.simpleMetaPath)) {
            return false;
        }
        if (simplePathInitialized != other.simplePathInitialized) {
            return false;
        }
        return true;
    }

    @Override
    protected String createName() {
        //zuerst versuche, den Resouceneintrag mit dem übergebenen Schlüssel zu finden, aber ohne "_f" oder "_b" am Ende -> setze den und gehe davon aus, dass es keine Rückrichtung gibt (wenn es ihn gibt)
        String name = super.createName();
        if (Strings.isNullOrEmpty(name)) {//das passiert nur, wenn der baseResKeyOrName null oder leer ist
            name = ElementsNameBuilder.getDirectedName(Edge.class.getSimpleName(), direction); //das sorgt dafür , dass der "ist verbunden mit"-Eintrag gefunden wird und der String nicht null ist
        } else if (name.equals(baseResKeyOrName)) { //wenn der Key nicht leer war und nicht schon ein Resourceneintrag ohne "_f" oder "_b" gefunden wurde
            name = ElementsNameBuilder.getDirectedName(name, direction);//versuche einen mit "_f" oder "_b" zu finden (je nach Richtung)
            if (Strings.isNullOrEmpty(name)) { //wenn keiner gefunden wurde
                name = baseResKeyOrName; // setzte den übergbenen nicht leeren Resourcen-String als Namen
            }
        }
        return name;
    }

    public String getAllMetaPathsName() {
        StringBuilder sb = new StringBuilder();
        for (AbstractMetaPath metaPath : metaPaths) {
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
            AbstractMetaPath[] otherDirectionMetaPaths = getOtherDirectionMetaPaths();
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
     * Legt den eigentlichen Gegenrichtungspfad an, wenn es ihn gibt (also wenn sich jeder Pfad in der Liste auch umdrehen lässt)
     *
     * @param baseResKeyOrName
     * @return
     */
    protected SequenceMetaPath createOtherDirection(final String baseResKeyOrName) {
        AbstractMetaPath[] otherDirectionMetaPaths = getOtherDirectionMetaPaths();
        return new SequenceMetaPath(baseResKeyOrName, Direction.BACKWARD, otherDirectionMetaPaths);
    }

    protected AbstractMetaPath[] getOtherDirectionMetaPaths() {
        // versuchen, die Gegenrichtung zusammen zu bauen
        AbstractMetaPath[] otherDirectionMetaPaths = new AbstractMetaPath[metaPaths.size()];
        // Gegenrichtung der enthaltenen Einzelpfade in umgekehrter Reihenfolge einfügen
        for (int i = otherDirectionMetaPaths.length - 1; i >= 0; i--) {
            AbstractMetaPath actualMetaPath = metaPaths.get(i);
            AbstractMetaPath otherDirection = actualMetaPath.getOtherDirection();
            if (otherDirection == null) {
                otherDirectionMetaPaths = null;
                break;
            }
            otherDirectionMetaPaths[otherDirectionMetaPaths.length - i - 1] = otherDirection;
        }
        return otherDirectionMetaPaths;
    }

    @Override
    public final boolean isCreateable() {
        return createable;
    }

    private final boolean getIsCreateable() {
        if (!isValid()) {
            return false;
        }
        List<ElementaryMetaPath> simpleMetaPath = getSimpleMetaPath();
        if (simpleMetaPath == null || simpleMetaPath.size() == 0) {
            return false;
        }
        // prüfen, ob die Zwischenelemente angelegt werden können
        for (int i = 0; i < simpleMetaPath.size() - 1; i++) {
            //nur Elementarpfade mit einer Kante dazwischen sind anlegbar, wenn die Kantenklasse nicht abstract ist
            if (!simpleMetaPath.get(i).isCreateable()) {
                return false;
            }
            if (i < simpleMetaPath.size() - 2) {
                Class<? extends ModelElement> connectingClass = getConnectingClass(i);
                if (ModelConstants.isAbstract(connectingClass)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Liefert die speziellere der beiden Elementklassen, die sich aus der Endklasse des einen Elementarpfadschrittes und der Anfangsklasse des
     * nächsten Elementarpfadschrittes ergeben.
     *
     * @param simpleMetaPathStepIndex Indexs des Pfadschrittes. 0 = der erste und simpleMetaPath.length() - 2 = der letzte
     * @return die speziellere der beiden Elementklasse zwischen 2 Elementarpfadschritten
     */
    private Class<? extends ModelElement> getConnectingClass(final int simpleMetaPathStepIndex) {
        ElementaryMetaPath elementaryMetaPath = simpleMetaPath.get(simpleMetaPathStepIndex);
        ElementaryMetaPath nextElementaryMetaPath = simpleMetaPath.get(simpleMetaPathStepIndex + 1);
        Class<? extends ModelElement> lastEndClass = elementaryMetaPath.getEndClass();
        Class<? extends ModelElement> nextStartClass = nextElementaryMetaPath.getStartClass();
        if (lastEndClass.isAssignableFrom(nextStartClass)) {
            return nextStartClass;
        } else if (nextStartClass.isAssignableFrom(lastEndClass)) {
            return lastEndClass;
        }
        return null;
    }

    @Override
    public final List<ElementaryMetaPath> getSimpleMetaPath() {
        if (!simplePathInitialized) {
            simplePathInitialized = true;
            ImmutableList.Builder<ElementaryMetaPath> simpleMetaPathBuilder = ImmutableList.builder();
            for (AbstractMetaPath metaPath : metaPaths) {
                List<ElementaryMetaPath> innerMetaPaths = metaPath.getSimpleMetaPath();
                if (innerMetaPaths == null) {
                    return null;
                }
                for (ElementaryMetaPath innerMetaPath : innerMetaPaths) {
                    simpleMetaPathBuilder.add(innerMetaPath);
                }
            }
            simpleMetaPath = simpleMetaPathBuilder.build();
        }
        return simpleMetaPath;
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
        int metaPathCount = metaPaths == null ? 0 : metaPaths.size();
        //bei einer ungeraden Anzahl von Pfaden muss der mittlere Pfad selbst undirected sein
        if (metaPathCount % 2 == 1 && metaPaths.get(metaPathCount / 2 + 1).isDirected()) {
            return false;
        }
        //Der Pfad muss zur Mitte hin symmetrisch sein, d.h. alle Pfade müssen von beiden Seiten von Außen
        //nach Innen laufend paarweise bis auf den Namen equals mit der jeweiligen Gegenrichtung des anderen
        //Pfades sein. Also wenn es 5 Pfade sind, dann muss Pfad 3 undirected sein, Pfad 1 muss die Gegenrichtung
        //von Pfad 5 sein und Pfad 2 die Gegenrichtung von Pfad 4. Bei einer geraden Anzahl von Pfaden müssen
        //die Pfade von Außen nach Innen immer paarweise ihre Gegenrichtungen sein.
        for (int i = 0; i < metaPathCount / 2; i++) {
            if (!metaPaths.get(i).getOtherDirection().equals(metaPathCount - 1 - i, true)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsHasPartEdge() {
        for (AbstractMetaPath metaPath : metaPaths) {
            if (metaPath.containsHasPartEdge()) {
                return true;
            }
        }
        return false;
    }

}
