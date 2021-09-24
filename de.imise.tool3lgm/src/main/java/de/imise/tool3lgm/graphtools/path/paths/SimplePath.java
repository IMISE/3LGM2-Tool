package de.imise.tool3lgm.graphtools.path.paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.util.collections.CollectionUtils;

/**
 * Ein SerialPath, der nur aus Elementarpfaden besteht.
 *
 * @author AXS (12 Jun 2019)
 */
public class SimplePath extends SerialPath {

    /** */
    private final List<ElementaryPath> elementaryPaths;

    /** */
    private final SimpleMetaPath simpleMetaPath;

    /**
     * @param simpleMetaPath
     * @param elementaryPaths
     */
    protected SimplePath(final SimpleMetaPath simpleMetaPath, final List<ElementaryPath> elementaryPaths) {
        super(simpleMetaPath, elementaryPaths);
        this.simpleMetaPath = simpleMetaPath;
        this.elementaryPaths = CollectionUtils.ensureImmutable(elementaryPaths);
    }

    /**
     * @param simpleMetaPath
     * @param elementaryPaths
     */
    protected SimplePath(final SimpleMetaPath simpleMetaPath, final ElementaryPath... elementaryPaths) {
        this(simpleMetaPath, Arrays.asList(elementaryPaths));
    }

    /**
     * Erzeugt aus der Liste von Kanten eine Liste von Elementarpfaden und
     * initialisiert damit einen SimpleMetaPath
     *
     * @param simpleMetaPath
     * @param edges
     * @return
     */
    public static SimplePath create(final SimpleMetaPath simpleMetaPath, final List<Edge> edges) {
        List<ElementaryPath> elementaryPaths = new ArrayList<>();
        List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
        for (int i = 0; i < elementaryMetaPaths.size(); i++) {
            Edge edge = edges.get(i);
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
            Direction direction = elementaryMetaPath.getDirection();
            ModelElement startElement = direction == Direction.FORWARD ? edge.getStart() : edge.getEnd();
            ModelElement endElement = edge.getOther(startElement);
            ElementaryPath elementaryPath = new ElementaryPath(elementaryMetaPath, startElement, endElement, edge);
            elementaryPaths.add(elementaryPath);
        }
        return new SimplePath(simpleMetaPath, elementaryPaths);
    }

    /**
     * @param metaPath
     */
    protected static final SimpleMetaPath extractSimpleMetaPath(final ElementaryPath... elementaryPaths) {
        return extractSimpleMetaPath(Arrays.asList(elementaryPaths));
    }

    /**
     * @param metaPath
     */
    protected static final SimpleMetaPath extractSimpleMetaPath(final List<ElementaryPath> elementaryPaths) {
        ElementaryMetaPath[] elementaryMetaPaths = new ElementaryMetaPath[elementaryPaths.size()];
        for (int i = 0; i < elementaryMetaPaths.length; i++) {
            ElementaryPath elementaryPath = elementaryPaths.get(i);
            elementaryMetaPaths[i] = elementaryPath.getMetaPath();
        }
        SimpleMetaPath simpleMetaPath = new SimpleMetaPath(elementaryMetaPaths);
        return simpleMetaPath;
    }

    /**
     * Erzeugt aus den übergebenen Elementarpfaden einen Gesamtpfad.
     *
     * @param elementaryPaths
     * @return
     */
    public static SimplePath create(final List<ElementaryPath> elementaryPaths) {
        SimpleMetaPath simpleMetaPath = extractSimpleMetaPath(elementaryPaths);
        return new SimplePath(simpleMetaPath, elementaryPaths);
    }

    /**
     * Hängt den übergebenen {@link SimplePath} an diesen an und gibt des
     * Gesamtpfad als neue Instanz zurück.
     *
     * @param path
     * @return
     */
    public SimplePath append(final SimplePath path) {
        SimpleMetaPath fullSimpleMetaPath = simpleMetaPath.append(path.simpleMetaPath);
        ImmutableList.Builder<ElementaryPath> elementaryPaths = ImmutableList.builder();
        elementaryPaths.addAll(getElementaryPaths());
        elementaryPaths.addAll(path.getElementaryPaths());
        return new SimplePath(fullSimpleMetaPath, elementaryPaths.build());
    }

    /**
     * @param path
     * @return
     */
    public SimplePath append(final ElementaryPath path) {
        SimpleMetaPath fullSimpleMetaPath = simpleMetaPath.append(path.getMetaPath());
        ImmutableList.Builder<ElementaryPath> elementaryPaths = ImmutableList.builder();
        elementaryPaths.addAll(getElementaryPaths());
        elementaryPaths.add(path);
        return new SimplePath(fullSimpleMetaPath, elementaryPaths.build());
    }

    /**
     * Creates an overall path from the elementary paths passed.
     *
     * @param elementaryPaths
     * @return
     */
    public static SimplePath create(final ElementaryPath... elementaryPaths) {
        return create(Arrays.asList(elementaryPaths));
    }

    /**
     * @return the list of the {@link ElementaryPath}
     */
    public List<ElementaryPath> getElementaryPaths() {
        return elementaryPaths;
    }

    /**
     * @param pathIndex
     * @return the contained {@link ElementaryMetaPath} with the given index
     */
    public ElementaryPath getPathAt(final int pathIndex) {
        return elementaryPaths.get(pathIndex);
    }

    /**
     * @return the first {@link ElementaryPath} from the list of all
     *         ElementaryPaths or <code>null</code> if the list is empty
     */
    public ElementaryPath getFirstElementaryPath() {
        if (elementaryPaths.isEmpty()) {
            return null;
        }
        ElementaryPath firstElementaryPath = elementaryPaths.get(0);
        return firstElementaryPath;
    }

    /**
     * @return the last {@link ElementaryPath} from the list of all
     *         ElementaryPaths or <code>null</code> if the list is empty
     */
    public ElementaryPath getLastElementaryPath() {
        if (elementaryPaths.isEmpty()) {
            return null;
        }
        int lastPathIndex = elementaryPaths.size() - 1;
        ElementaryPath lastElementaryPath = elementaryPaths.get(lastPathIndex);
        return lastElementaryPath;
    }

    @Override
    public SimpleMetaPath getMetaPath() {
        return simpleMetaPath;
    }

    /**
     * TODO: this function and some others should be treated analogous to
     * metapaths
     *
     * @return the path in the other direction
     */
    public SimplePath getOtherDirection() {
        List<ElementaryPath> elementaryPaths = new ArrayList<>();
        for (int i = this.elementaryPaths.size() - 1; i >= 0; i--) {
            ElementaryPath elementaryPath = this.elementaryPaths.get(i);
            elementaryPath = elementaryPath.getOtherDirection();
            elementaryPaths.add(elementaryPath);
        }
        SimpleMetaPath metaPath = simpleMetaPath.getOtherDirection();
        return new SimplePath(metaPath, elementaryPaths);
    }

}
