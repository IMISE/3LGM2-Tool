package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Ein {@link SerialMetaPath}, der immer nur aus einer einfachen Folge von
 * Kanten bzw. {@link ElementaryMetaPath} besteht.
 *
 * @author AXS (15 Nov 2018)
 */
public class SimpleMetaPath extends SerialMetaPath {

    /**
     * Index des Elementarpfadschrittes, der den Namen des Gesamtpfades
     * festlegt. Ist er kleiner 0 läuft die Namensgenerierung über den
     * super-Namensmechanismus, der den baseResKeyOrName auswertet und wenn er
     * damit auch nichts findet "ist verbunden mit" ausgibt.
     */
    private int metaPathStepWithPathName = -1;

    /**
     * @param metaPaths
     */
    public SimpleMetaPath(final List<ElementaryMetaPath> metaPaths) {
        this(metaPaths.toArray(new ElementaryMetaPath[0]));
    }

    /**
     * @param metaPaths
     */
    public SimpleMetaPath(final ElementaryMetaPath... metaPaths) {
        super(metaPaths);
    }

    /**
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param metaPaths
     */
    public SimpleMetaPath(final int metaPathStepWithPathName, final ElementaryMetaPath... metaPaths) {
        super(metaPathStepWithPathName < 0 ? null : metaPaths[metaPathStepWithPathName].toString(), metaPaths);
        this.metaPathStepWithPathName = metaPathStepWithPathName;
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SimpleMetaPath(final String baseResKeyOrName, final ElementaryMetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param direction
     * @param metaPaths
     */
    protected SimpleMetaPath(final String baseResKeyOrName, final Direction direction, final ElementaryMetaPath... metaPaths) {
        super(baseResKeyOrName, direction, metaPaths);
    }

    /**
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param direction
     * @param metaPaths
     */
    protected SimpleMetaPath(final int metaPathStepWithPathName, final Direction direction, final ElementaryMetaPath... metaPaths) {
        super(metaPathStepWithPathName < 0 ? null : metaPaths[metaPathStepWithPathName].name, direction, metaPaths);
        this.metaPathStepWithPathName = metaPathStepWithPathName;
    }

    /**
     * Hängt den übergebenen {@link SimpleMetaPath} an diesen
     * {@link SimpleMetaPath} an und gibt den Gesamtpfad als neue Instanz
     * zurück.
     *
     * @param simpleMetaPath
     * @return
     */
    public SimpleMetaPath append(final SimpleMetaPath simpleMetaPath) {
        return createJoined(getElementaryMetaPaths(), simpleMetaPath.getElementaryMetaPaths());
    }

    /**
     * Hängt den übergebenen {@link ElementaryMetaPath} an diesen
     * {@link SimpleMetaPath} an und gibt den Gesamtpfad als neue Instanz
     * zurück.
     *
     * @param simpleMetaPath
     * @return
     */
    public SimpleMetaPath append(final ElementaryMetaPath elementaryMetaPath) {
        return createJoined(getElementaryMetaPaths(), ImmutableList.of(elementaryMetaPath));
    }

    /**
     * Hängt de beiden Listen zu einem {@link SimpleMetaPath} zusammen.
     *
     * @param elementaryMetaPaths1
     * @param elementaryMetaPaths2
     * @return
     */
    private SimpleMetaPath createJoined(final List<ElementaryMetaPath> elementaryMetaPaths1, final List<ElementaryMetaPath> elementaryMetaPaths2) {
        List<ElementaryMetaPath> allMetaPaths = new ArrayList<>(elementaryMetaPaths1.size() + elementaryMetaPaths2.size());
        allMetaPaths.addAll(elementaryMetaPaths1);
        allMetaPaths.addAll(elementaryMetaPaths2);
        return new SimpleMetaPath(allMetaPaths);
    }

    /**
     * @return the startClass
     */
    @Override
    public Class<? extends ModelElement> getStartClass() {
        return getElementaryMetaPaths().get(0).getStartClass();
    }

    /**
     * @return the endClass
     */
    @Override
    public Class<? extends ModelElement> getEndClass() {
        List<ElementaryMetaPath> simpleMetaPath = getElementaryMetaPaths();
        return simpleMetaPath.get(simpleMetaPath.size() - 1).getEndClass();
    }

    @Override
    public SimpleMetaPath getOtherDirection() {
        return (SimpleMetaPath) super.getOtherDirection();
    }

    @Override
    protected SimpleMetaPath createOtherDirection(final String baseResKeyOrName) {
        ElementaryMetaPath[] otherDirectionMetaPaths = getOtherDirectionMetaPaths();
        return otherDirectionMetaPaths != null ? new SimpleMetaPath(baseResKeyOrName, Direction.BACKWARD, getOtherDirectionMetaPaths()) : null;
    }

    @Override
    protected ElementaryMetaPath[] getOtherDirectionMetaPaths() {
        ElementaryMetaPath[] otherDirectionElementaryMetaPaths = null;
        MetaPath[] otherDirectionMetaPaths = super.getOtherDirectionMetaPaths();
        if (otherDirectionMetaPaths != null) {
            otherDirectionElementaryMetaPaths = Arrays.copyOf(otherDirectionMetaPaths, otherDirectionMetaPaths.length, ElementaryMetaPath[].class);
        }
        return otherDirectionElementaryMetaPaths;
    }

    /**
     * Liefert einen Sub-Path beginnend vom angegebenen Start-Index bis zur
     * letzten Kante.
     *
     * @param pathStepStartIndex
     * @return
     */
    public SimpleMetaPath getSubPath(final int pathStepStartIndex) {
        return getSubPath(pathStepStartIndex, getSubMetaPathCount());
    }

    /**
     * Liefert einen Sub-Path beginnend vom angegebenen Start-Index bis zum
     * MetaPath vor dem End-Index (exklusive).
     *
     * @param pathStepStartIndex
     * @param pathStepEndIndex
     * @return
     */
    public SimpleMetaPath getSubPath(final int pathStepStartIndex, final int pathStepEndIndex) {
        int fullPathLength = subMetaPaths.size();
        if (pathStepStartIndex >= pathStepEndIndex || pathStepStartIndex < 0 || pathStepStartIndex >= fullPathLength || pathStepEndIndex < 0 || pathStepEndIndex > fullPathLength) {
            throw new IllegalArgumentException("Invalid pathStepStartIndex=" + pathStepStartIndex + " and pathStepEndIndex=" + pathStepEndIndex);
        }
        ElementaryMetaPath[] metaPathsArray = subMetaPaths.toArray(new ElementaryMetaPath[0]);
        ElementaryMetaPath[] subMetaPathsArray = new ElementaryMetaPath[pathStepEndIndex - pathStepStartIndex];
        System.arraycopy(metaPathsArray, pathStepStartIndex, subMetaPathsArray, 0, subMetaPathsArray.length);
        //Hier wird noch vor der ersten und nach der letzten Kante die Elementart eingeschränkt, weil die Elementart der "weggeschnittenen" Kante die Start- oder Zielklasse des verkürzten Pfades eigentlich einschränken kann
        ElementaryMetaPathHandler elementaryMetaPathHandler = getMetaModel().getElementaryMetaPathHandler();
        if (pathStepStartIndex > 0) {
            subMetaPathsArray[0] = elementaryMetaPathHandler.getMetaPath(metaPathsArray[pathStepStartIndex - 1].getEndClass(), subMetaPathsArray[0]);
        }
        if (pathStepEndIndex < fullPathLength) {
            subMetaPathsArray[subMetaPathsArray.length - 1] = elementaryMetaPathHandler.getMetaPath(subMetaPathsArray[subMetaPathsArray.length - 1], metaPathsArray[pathStepEndIndex].getStartClass());
        }
        return new SimpleMetaPath(subMetaPathsArray);
    }

    @Override
    public boolean isAssignable(final MetaPath other) {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        List<ElementaryMetaPath> otherElementaryMetaPaths = other.getElementaryMetaPaths();
        int subMetaPathCount = elementaryMetaPaths.size();
        if (subMetaPathCount != otherElementaryMetaPaths.size()) {
            return false;
        }
        for (int i = 0; i < subMetaPathCount; i++) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
            ElementaryMetaPath otherElementaryMetaPath = otherElementaryMetaPaths.get(i);
            if (!elementaryMetaPath.isAssignable(otherElementaryMetaPath)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Liefert den Index des Elementarpfadschrittes, der den Namen des
     * Gesamtpfades festlegt. Ist er kleiner 0 läuft die Namensgenerierung über
     * den super-Namensmechanismus, der den baseResKeyOrName auswertet und wenn
     * er damit auch nichts findet "ist verbunden mit" ausgibt.
     *
     * @return
     */
    public int getMetaPathStepWithPathName() {
        return metaPathStepWithPathName;
    }

    public final String getFullPathString() {
        StringBuilder sb = new StringBuilder();
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        for (int i = 0; i < elementaryMetaPaths.size(); i++) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
            sb.append(elementaryMetaPath.getFullName());
            if (i + 1 < elementaryMetaPaths.size()) {
                sb.append(" + ");
            }
        }
        return sb.toString();
    }

    /**
     * @return getMetaPathCount()
     * @see #getSubMetaPathCount()
     */
    public int length() {
        return getSubMetaPathCount();
    }

}
