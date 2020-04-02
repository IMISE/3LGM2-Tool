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
 * Ein SequencePath, der nur aus Elementarpfaden besteht.
 *
 * @author AXS (12 Jun 2019)
 */
public class SimplePath extends SequencePath {

    /** */
    private final List<ElementaryPath> elementaryPaths;

    /** */
    private final SimpleMetaPath simpleMetaPath;

    /**
     * @param simpleMetaPath
     * @param elementaryPaths
     */
    private SimplePath(final SimpleMetaPath simpleMetaPath, final List<ElementaryPath> elementaryPaths) {
        super(simpleMetaPath, elementaryPaths);
        this.simpleMetaPath = simpleMetaPath;
        this.elementaryPaths = CollectionUtils.ensureImmutable(elementaryPaths);
    }

    /**
     * @param simpleMetaPath
     * @param elementaryPaths
     */
    private SimplePath(final SimpleMetaPath simpleMetaPath, final ElementaryPath... elementaryPaths) {
        this(simpleMetaPath, Arrays.asList(elementaryPaths));
    }

    /**
     * Erzeugt aus der Liste von Kanten eine Liste von Elementarpfaden und initialisiert damit einen SimpleMetaPath
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
     * Erzeugt aus den übergebenen Elementarpfaden einen Gesamtpfad.
     *
     * @param elementaryPaths
     * @return
     */
    public static SimplePath create(final List<ElementaryPath> elementaryPaths) {
        ElementaryMetaPath[] elementaryMetaPaths = new ElementaryMetaPath[elementaryPaths.size()];
        for (int i = 0; i < elementaryMetaPaths.length; i++) {
            ElementaryPath elementaryPath = elementaryPaths.get(i);
            elementaryMetaPaths[i] = elementaryPath.getMetaPath();
        }
        SimpleMetaPath simpleMetaPath = new SimpleMetaPath(elementaryMetaPaths);
        return new SimplePath(simpleMetaPath, elementaryPaths);
    }

    /**
     * Hängt den übergebenen {@link SimplePath} an diesen an und gibt des Gesamtpfad als neue Instanz zurück.
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
     * Erzeugt aus den übergebenen Elementarpfaden einen Gesamtpfad.
     *
     * @param elementaryPaths
     * @return
     */
    public static SimplePath create(final ElementaryPath... elementaryPaths) {
        return create(Arrays.asList(elementaryPaths));
    }

    public List<ElementaryPath> getElementaryPaths() {
        return elementaryPaths;
    }

    @Override
    public SimpleMetaPath getMetaPath() {
        return simpleMetaPath;
    }

    /**
     * @param pathStepIndex
     * @return the path step with the given index
     */
    public ElementaryPath getPathStep(final int pathStepIndex) {
        return elementaryPaths.get(pathStepIndex);
    }

}
