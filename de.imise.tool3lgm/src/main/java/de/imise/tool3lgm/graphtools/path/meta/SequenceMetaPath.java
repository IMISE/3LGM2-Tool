package de.imise.tool3lgm.graphtools.path.meta;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS (26.10.2018)
 * @author AXS
 * @create 13.10.2010
 */
public class SequenceMetaPath extends AbstractMetaPath {

    /**
     * Liste der Pfade, die dieser Metapfad hintereinander enthält.
     */
    protected List<AbstractMetaPath> metaPaths;

    /**
     * Wahr, wenn sobald einmal verscht wurde, die Gegenrichtung dieses Pfades anzulegen
     */
    private boolean otherDirectionInitilized = false;

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

    private final String baseResKeyOrName;

    private final Direction direction;

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
    private SequenceMetaPath(final String baseResKeyOrName, final Direction direction, final AbstractMetaPath... metaPaths) {
        super();
        this.baseResKeyOrName = baseResKeyOrName;
        this.direction = direction;
        if (metaPaths != null && metaPaths.length > 0) {
            this.metaPaths = ImmutableList.copyOf(metaPaths);
            startElementClasses = metaPaths[0].startElementClasses;
            endElementClasses = metaPaths[metaPaths.length - 1].endElementClasses;
        }
        if (!isValid()) {
            throw new Error("Metapfad ist nicht korrekt");
        }
        directed = getIsDirected();
        createable = getIsCreateable();
    }

    @Override
    public boolean isValid() {
        int metaPathCount = metaPaths.size();
        for (int i = 0; i < metaPathCount; i++) {
            AbstractMetaPath metaPath = metaPaths.get(i);
            // alle enthaltenen Pfade müssen selbst valide sein
            if (!metaPath.isValid()) {
                return false;
            }
            // wenn nach dem aktuellen noch ein weiterer MetaPfad in der Liste steht, dann muss wenigstens eine Endklasse des aktuellen zu den Startklassen des folgenden Metapfades passen
            if (i < metaPathCount - 1) {
                if (!metaPaths.get(i + 1).isStartClass(metaPath.getEndClasses(), true, true)) {
                    return false;
                }
            }
        }
        return true;
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

    /**
     * @return the metaPaths
     */
    public List<AbstractMetaPath> getMetaPaths() {
        return metaPaths;
    }

    @Override
    protected String createName() {
        String name = baseResKeyOrName;
        name = Tool3lgmConstants.getResStringWithoutError(name);
        if (Strings.isNullOrEmpty(name)) {
            name = Edge.class.getSimpleName();//das sorgt dafür , dass der "ist verbunden mit"-Eintrag gefunden wird und der String nicht null ist
        }
        //es gibt einen Resouceneintrag mit dem übergebenen Schlüssel, aber ohne "_f" oder "_b" am Ende -> setze den und gehe davon aus, dass es keine Rückrichtung gibt
        if (!name.equals(baseResKeyOrName)) {
            this.name = name;
        }
        name = ElementsNameBuilder.getDirectedName(name, direction);
        if (Strings.isNullOrEmpty(name)) {
            name = baseResKeyOrName;
        }
        this.name = name;
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
    public AbstractMetaPath getOtherDirection() {
        // wenn noch nicht bereits einmal versucht wurde den Gegenrichtungspfad zusammenzubauen
        if (!otherDirectionInitilized) {
            otherDirectionInitilized = true;
            // versuchen, die Gegenrichtung zusammen zu bauen
            AbstractMetaPath[] otherDirectionMetaPaths = new AbstractMetaPath[metaPaths.size()];
            // Gegenrichtung der enthaltenen Einzelpfade in umgekehrter Reihenfolge einfügen
            for (int i = otherDirectionMetaPaths.length - 1; i >= 0; i--) {
                AbstractMetaPath actualMetaPath = metaPaths.get(i);
                AbstractMetaPath otherDirection = actualMetaPath.getOtherDirection();
                if (otherDirection == null) {
                    break;
                }
                otherDirectionMetaPaths[otherDirectionMetaPaths.length - i - 1] = otherDirection;
            }
            // Gegenrichtung für diesen und den Gegenrichtungspfad setzen
            SequenceMetaPath other = new SequenceMetaPath(baseResKeyOrName, Direction.BACKWARD, otherDirectionMetaPaths);
            other.otherDirection = this;
            other.otherDirectionInitilized = true;
            super.otherDirection = other;
        }
        return otherDirection;
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
        if (simpleMetaPath == null) {
            return false;
        }
        // prüfen, ob die Zwischenelemente angelegt werden können
        for (int i = 0; i < simpleMetaPath.size() - 1; i++) {
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
    public List<ElementaryMetaPath> getSimpleMetaPath() {
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
