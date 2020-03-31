package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.ReflectionUtils;
import de.imise.util.collections.CollectionUtils;

/**
 * Oberklasse für alle Metapfade.
 *
 * @author AXS
 * @create 12.10.2010
 */
public abstract class AbstractMetaPath extends MetaModelSpecificAdapter {

    /**
     * Leere Elementarpfadliste
     */
    protected static final ImmutableList<ElementaryMetaPath> EMPTY_ELEMENTARY_PATH_LIST = ImmutableList.of();

    /**
     * Liste aller Startklassen dieses Pfades.
     */
    protected Set<Class<? extends ModelElement>> startElementClasses = null;

    /**
     * Liste aller Endklassen dieses Pfades.
     */
    protected Set<Class<? extends ModelElement>> endElementClasses = null;

    /**
     * Anzeigename des Pfades. Falls es ein
     */
    protected String name = null;

    /**
     * Anzeigename mit Start- und Endklassen. Kann in Unterlassen geändert werden.
     */
    protected String fullName = null;

    /**
     * MataPath für die Gegenrichtung dieses Pfades. Der ist nur nicht <code>null</code>, wenn er sich tatsächlich
     * feststellen lässt.
     */
    protected AbstractMetaPath otherDirection = null;

    /**
     * Dieses Objekt gibt den Grund an, warum ein MetaPath nicht valide ist. Ist der darin enthaltene InvalidReason <code>null</code>, gilt der
     * MetaPath als valide.
     */
    protected InvalidityCheckResult invalidityCheckResult;

    /**
     * If <code>true</code> and the metapath can be recursive (ends with an element type that can be the start element type) then the path is
     * interpreted as recursive.
     */
    private boolean interpretAsRecursive;

    /** the metamodel */
    protected final MetaModel metaModel;

    /**
     * @param metaModel
     */
    public AbstractMetaPath(final MetaModel metaModel) {
        this(metaModel, null);
    }

    /**
     * @param metaModel
     * @param name
     *            Anzeigenamen
     */
    public AbstractMetaPath(final MetaModel metaModel, final String name) {
        this(metaModel, (Class<? extends ModelElement>) null, (Class<? extends ModelElement>) null, name);
    }

    /**
     * @param metaModel
     * @param startElementClass
     * @param endElementClass
     */
    public AbstractMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass) {
        this(metaModel, startElementClass, endElementClass, null);

    }

    /**
     * @param metaModel
     * @param startElementClass
     * @param endElementClass
     * @param name
     */
    public AbstractMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass, final String name) {
        this(metaModel, startElementClass != null ? ImmutableSet.of(startElementClass) : null, endElementClass != null ? ImmutableSet.of(endElementClass) : null, name);

    }

    /**
     * @param metaModel
     * @param startElementClasses
     * @param endElementClasses
     * @param name
     */
    public AbstractMetaPath(final MetaModel metaModel, final Set<Class<? extends ModelElement>> startElementClasses, final Set<Class<? extends ModelElement>> endElementClasses, final String name) {
        super(metaModel);
        this.metaModel = metaModel;
        this.startElementClasses = CollectionUtils.ensureImmutable(startElementClasses);
        this.endElementClasses = CollectionUtils.ensureImmutable(endElementClasses);
        this.name = name;
    }

    /**
     * @return
     */
    public final Set<Class<? extends ModelElement>> getStartClasses() {
        return startElementClasses;
    }

    /**
     * @return
     */
    public Class<? extends ModelElement> getStartClass() {
        Set<Class<? extends ModelElement>> startClasses = getStartClasses();
        Class<?> commonSuperClass = ReflectionUtils.getCommonSuperClass(startClasses);
        return commonSuperClass.asSubclass(ModelElement.class);
    }

    /**
     * @return
     */
    public final Set<Class<? extends ModelElement>> getEndClasses() {
        return endElementClasses;
    }

    /**
     * @return
     */
    public Class<? extends ModelElement> getEndClass() {
        Set<Class<? extends ModelElement>> endClasses = getEndClasses();
        Class<?> commonSuperClass = ReflectionUtils.getCommonSuperClass(endClasses);
        return commonSuperClass.asSubclass(ModelElement.class);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine Startklasse dieses Metapfades ist.
     *
     * @param elementClass
     *            Elementklasse, die als Startklasse geprüft werden soll
     * @return
     */
    public boolean isStartClass(final Class<? extends ModelElement> elementClass) {
        for (Class<? extends ModelElement> startClass : getStartClasses()) {
            if (startClass.isAssignableFrom(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine Startklasse dieses Metapfades ist.
     *
     * @param elementClasses
     *            Elementklassen, die als Startklasse geprüft werden soll
     * @return
     */
    public final boolean isStartClass(final Collection<Class<? extends ModelElement>> elementClasses) {
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (isStartClass(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine Endklasse dieses MetaPfades ist.
     *
     * @param elementClass
     *            Elementklasse, die als Endklasse geprüft werden soll
     * @return
     */
    public boolean isEndClass(final Class<? extends ModelElement> elementClass) {
        for (Class<? extends ModelElement> endClass : getEndClasses()) {
            if (endClass.isAssignableFrom(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine Endklasse dieses MetaPfades ist.
     *
     * @param elementClasses
     *            Elementklasseen, die als Endklasse geprüft werden sollen
     * @return
     */
    public final boolean isEndClass(final Collection<Class<? extends ModelElement>> elementClasses) {
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (isEndClass(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene startClass Startklasse und die übergeben endCLass Endklasse
     * dieses MetaPfades sein kann.
     *
     * @param startClass
     *            Elementklasse, die als Startklasse der Kantenklasse geprüft werden soll
     * @param startClass
     *            Elementklasse, die als Endklasse der Kantenklasse geprüft werden soll
     * @return
     */
    public final boolean isStartAndEndClass(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        return isStartClass(startClass) && isEndClass(endClass);
    }

    /**
     * @return <code>true</code>, if the metapath is applicable to the endelements as startelements otherwise <code>false</code>
     */
    public final boolean isInterpretAsRecursive() {
        return interpretAsRecursive;
    }

    /**
     * @param
     */
    public final void setInterpretAsRecursive(final boolean interpretAsRecursive) {
        this.interpretAsRecursive = interpretAsRecursive;
    }

    /**
     * @return <code>true</code> if at least one endElement class can be the startElement class
     *         of this metaPath
     */
    protected boolean canBeRecursive() {
        Set<Class<? extends ModelElement>> endClasses = getEndClasses();
        for (Class<? extends ModelElement> endClass : endClasses) {
            if (isStartClass(endClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Repräsentiert den Validitätszustand eines MetaPath. Ist der invalidReason <code>null</code>, dann gilt der MetaPath als valide, sonst nicht.
     *
     * @author AXS (6 Dec 2018)
     */
    public class InvalidityCheckResult {

        /**
         * Ein beliebiger Enum, der einen FehlerKey enthält. Über diesen Key-Name kann ein Ressourcenstring geladen werden, der dem Benutzer einen
         * Hinweis auf den Fehler gibt.
         */
        public final Enum<?> invalidReason;

        /**
         * Falls der Fehler mit irgendeinem Index zusammen hängt, kann man diesen hier speichern (z.B. Index des Pfades mit dem Fehler)
         */
        public final int index1;

        /**
         * Falls der Fehler mit irgendeinem weiteren Index zusammen hängt, kann man diesen hier speichern (z.B. Index des Elementarpfades mit dem
         * Fehler)
         */
        public final int index2;

        public InvalidityCheckResult(final Enum<?> invalidReason) {
            this(invalidReason, -1, -1);
        }

        public InvalidityCheckResult(final Enum<?> invalidReason, final int index1) {
            this(invalidReason, index1, -1);
        }

        public InvalidityCheckResult(final Enum<?> invalidReason, final int index1, final int index2) {
            this.invalidReason = invalidReason;
            this.index1 = index1;
            this.index2 = index2;
        }

    }

    /**
     *
     */
    public enum InvalidReason {
        INVALID_START_CLASSES,
        INVALID_END_CLASSES;
    }

    /**
     * @return
     */
    public InvalidityCheckResult getInvalidityCheckResult() {
        if (invalidityCheckResult == null) {
            InvalidReason invalidReason = null;
            if (startElementClasses == null || startElementClasses.size() == 0) {
                invalidReason = InvalidReason.INVALID_START_CLASSES;
            } else if (endElementClasses == null || endElementClasses.size() == 0) {
                invalidReason = InvalidReason.INVALID_END_CLASSES;
            } else {
                invalidReason = null;
            }
            invalidityCheckResult = new InvalidityCheckResult(invalidReason);
        }
        return invalidityCheckResult;
    }

    /**
     * Liefert <code>true</code>, wenn der Pfad keine Fehler enthält.
     *
     * @return
     */
    public final boolean isValid() {
        return getInvalidityCheckResult().invalidReason == null;
    }

    /**
     * Liefert <code>true</code>, wenn das übergebene Objekt dieselben Eigenschaften hat, wie this.
     *
     * @param obj
     * @param ignoreName
     *            Wenn <code>true</code> wird die Gleichheit des Namens nicht mitgeprüft
     * @return
     */
    public boolean equals(final Object obj, final boolean ignoreName) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractMetaPath other = (AbstractMetaPath) obj;
        if (endElementClasses == null) {
            if (other.endElementClasses != null) {
                return false;
            }
        } else if (!endElementClasses.equals(other.endElementClasses)) {
            return false;
        }
        if (!ignoreName) {
            if (fullName == null) {
                if (other.fullName != null) {
                    return false;
                }
            } else if (!fullName.equals(other.fullName)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
        }
        if (startElementClasses == null) {
            if (other.startElementClasses != null) {
                return false;
            }
        } else if (!startElementClasses.equals(other.startElementClasses)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (endElementClasses == null ? 0 : endElementClasses.hashCode());
        result = prime * result + (fullName == null ? 0 : fullName.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (startElementClasses == null ? 0 : startElementClasses.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        return equals(obj, true);
    }

    /**
     * @return
     */
    protected abstract String createName();

    /**
     * @return
     */
    public final String getName() {
        if (Strings.isNullOrEmpty(name)) {
            name = createName();
        }
        return name;
    }

    /**
     * @return
     */
    public final String getFullName() {
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = getName(true, true);
        }
        return fullName;
    }

    /**
     * @param withStartClasses
     * @param withEndClasses
     * @return
     */
    public String getName(final boolean withStartClasses, final boolean withEndClasses) {
        if (withStartClasses || withEndClasses) {
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            if (withStartClasses && withEndClasses) {
                return elementsNameBuilder.getDisplayableClassesNames(getStartClasses()) + " " + getName() + " " + elementsNameBuilder.getDisplayableClassesNames(getEndClasses());
            } else if (withStartClasses) {
                return elementsNameBuilder.getDisplayableClassesNames(getStartClasses()) + " " + getName();
            } else if (withEndClasses) {
                return getName() + " " + elementsNameBuilder.getDisplayableClassesNames(getEndClasses());
            }
        }
        return getName();
    }

    @Override
    public final String toString() {
        return getName();
    }

    /**
     * Liefert <code>true</code>, wenn der Pfad prinzipiell angelegt werden kann. Das ist der Fall, wenn es sich um eine
     * einfache Assoziationsfolge ohne parallele Pfade oder Verweigungen zu Assoziationsklassen dazwischen handelt und alle
     * Zwischenelementklassen nicht abstrakt sind.
     *
     * @param checkCreateEndElement
     *            wenn <code>true</code>, dann wird auch geprüft, ob das EndElement angelegt werden kann, wenn der Pfad angelegt
     *            wird, ohne die Konsistenz zu verletzten (also nicht abstract und durch den Pfad entstehen für alle Elemente
     *            alle anderen Elemente, die sie für ihre Existenz brauchen).
     * @return <code>true</code> wenn dieser Pfad anlegbar ist
     */
    public abstract boolean isCreatable(boolean checkCreateEndElement);

    /**
     * Prüft, ob der Pfad ausgehend von der Startelementart entfernt werden kann, ohne dass das Startelement dadurch inkonsistent
     * wird und ebenfalls gelöscht werden würde, wenn man den Pfad entfernt.
     *
     * @param checkEndElement wenn <code>true</code>, wird genauso für das Endelement geprüft, ob es inkonsistent und damit gelöscht
     *            werden würde, wenn man den Pfad zwischen ihm und einem Startelement entfernt.
     * @return <code>true</code> wenn sich der Pfad entfernen lässt, ohne dass das Startelement oder bei <code>checkEndElement == true</code>
     *         auch das Endelement nicht inkonsistent und damit gelöscht werden, sonst <code>false</code>.
     */
    public abstract boolean isRemoveable(boolean checkEndElement);

    /**
     * Liefert <code>true</code>, wenn der Pfad eine einfache Assoziationsfolge ist (also bei {@link #getElementaryMetaPaths()} nicht
     * <code>null</code> zurück gibt und jeder Einzelpfad die maximale Endkardinalität von 1 hat.
     */
    public final boolean isSingleConnection() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        for (ElementaryMetaPath elementaryMetaPath : elementaryMetaPaths) {
            if (elementaryMetaPath.getForwardCardinality().max() != 1) {
                return false;
            }
        }
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass mehrere Verbindungen mgl. sind
        return !elementaryMetaPaths.isEmpty();
    }

    /**
     * Liefert <code>true</code>, wenn das erste Element des Pfades nur existieren kann, wenn es mit einem
     * auf dem Pfad dahinter liegenden Element verbunden ist. Das wird gebraucht, um zu entscheiden, ob ein neu
     * angelegtes EndElement des Pfades immer sofort verbunden werden muss.
     *
     * @return
     */
    public final boolean isFirstPathElementDependent() {
        ElementaryMetaPath firstElementaryMetaPathInPath = getFirstElementaryMetaPath();
        if (firstElementaryMetaPathInPath == null) {
            return false;
        }
        //Verbindungen, die durch InstanciationEgdes bestehen, kann man nicht einfach lösen/ändern und gelten als existenznotwendig
        Class<? extends Edge> edgeClass = firstElementaryMetaPathInPath.getEdgeClass();
        if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
            return true;
        }
        EdgeCardinality forwardCardinality = firstElementaryMetaPathInPath.getForwardCardinality();
        int minCardinality = forwardCardinality.min();
        return minCardinality > 0;
    }

    /**
     * Liefert <code>true</code>, wenn das letzte Element des Pfades nur existieren kann, wenn es mit einem
     * auf dem Pfad davor liegenden Element verbunden ist. Das wird gebraucht, um zu entscheiden, ob ein neu
     * angelegtes EndElement des Pfades immer sofort verbunden werden muss.
     *
     * @return
     */
    public final boolean isLastPathElementDependent() {
        ElementaryMetaPath lastElementaryMetaPathInPath = getLastElementaryMetaPath();
        if (lastElementaryMetaPathInPath == null) {
            return false;
        }
        //Verbindungen, die durch InstanciationEgdes bestehen, kann man nicht einfach lösen/ändern und gelten als existenznotwendig
        Class<? extends Edge> edgeClass = lastElementaryMetaPathInPath.getEdgeClass();
        if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
            return true;
        }
        EdgeCardinality backwardCardinality = lastElementaryMetaPathInPath.getBackwardCardinality();
        int minCardinality = backwardCardinality.min();
        return minCardinality > 0;
    }

    /**
     * Liefert den MetaPfad der die Gegenricthung beschreibt oder <code>null</code>, wenn es einen solchen nicht gibt.
     *
     * @return the otherDirectionPath
     */
    public AbstractMetaPath getOtherDirection() {
        return otherDirection;
    }

    /**
     * Liefert eine Folge von Elementarpfaden, wenn sich dieser Pfad so bilden lässt, ansonsten kommt eine leere Liste zurück. Alle parallelen Pfade
     * geben hier leere Liste zurück. {@link SequenceMetaPath} geben nur keine leere Liste zurück, wenn sie im innersten ein einzelner Pfad sind ohne
     * parallele oder rekursive Pfade sind.
     *
     * @return
     */
    public List<ElementaryMetaPath> getElementaryMetaPaths() {
        return EMPTY_ELEMENTARY_PATH_LIST;
    }

    /**
     * @return den ersten ElementaryMetaPath aus {@link #getElementaryMetaPaths()}, wenn die Liste mind. einen solchen Elementarpfad enthält.
     */
    public ElementaryMetaPath getFirstElementaryMetaPath() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass das letzte Pfadelement gebraucht wird
        if (elementaryMetaPaths.isEmpty()) {
            return null;
        }
        ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(0);
        return lastElementaryMetaPath;
    }

    /**
     * @return den letzten ElementaryMetaPath aus {@link #getElementaryMetaPaths()}, wenn die Liste mind. einen solchen Elementarpfad enthält.
     */
    public ElementaryMetaPath getLastElementaryMetaPath() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass das letzte Pfadelement gebraucht wird
        if (elementaryMetaPaths.isEmpty()) {
            return null;
        }
        ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(elementaryMetaPaths.size() - 1);
        return lastElementaryMetaPath;
    }

    /**
     * @return Liste aller {@link AbstractMetaPath}, die dieser MetaPfad enthält.
     */
    public abstract List<AbstractMetaPath> getSubMetaPaths();

    /**
     * @return the number of contained metapaths
     */
    public abstract int getSubMetaPathCount();

    /**
     * Liefert <code>false</code>, wenn der Pfad in beide Richtungen dasselbe bedeutet. Dafür muss
     * er dieselben Elementarten miteinander verbinden und denselben Namen in beiden Richtungen
     * tragen. Z.B können 2 physische DV-Bausteine über Datenübertragungsverbindungen miteinander
     * verbunden sein. Diese Verbindung heißt in jede der beiden Richtungen "ist verbunden mit" und
     * verbindet dieselbe Elementart miteinander. Der dazugehörige Elementarpfad ist also undirected.
     * Dasselbe ist aber auch für {@link SequenceMetaPath}s möglich, wenn z.B. die beiden physischen
     * DV-Bausteine Schnittstellen beitzen würden (was sie im aktuellen Metamodell nicht haben) und diese
     * dann über eine Datenübertragungsverbindung mit der beidseitigen Bedeutung "ist verbunden mit"
     * verbunden sind, dann bedeutet der Pfad auch in beide Richtungen dasselbe, nämlich
     * "Phys. DV-Baustein besitzt Schnittstelle ist verbunden mit Schnittstelle gehört zu Phys. DV-Baustein".
     * Die Umkehrrichtung dieses Pfades ist er selbst und somit ist er undirected.
     *
     * @return
     *         <code>true</code> wenn Vorwärts- und Rückwärtsrichtungen unterschiedliche Bedeutung haben
     */
    public abstract boolean isDirected();

    /**
     * Liefert <code>true</code>, wenn der Metapfad irgendwo eine {@link PartOfVerbindung} enthält.
     *
     * @return
     */
    public abstract boolean containsPropertyTransferEdge();

}
