package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;

/**
 * A MetaPath that itself contains only MetaPaths of its own kind. This
 * interface should only be implemented by ElementaryMetaPaths and
 * SimpleMetaPaths - or only by those MetaPaths that can always return a
 * non-empty list of ElementaryMetaPaths.
 *
 * @author Ich (12.11.2020)
 */
public class SimpleSerialMetaPath extends SerialMetaPath implements SequenceMetaPath {

    /**
     * @param metaPaths
     */
    public SimpleSerialMetaPath(final SequenceMetaPath... metaPaths) {
        super(metaPaths);
    }

    /**
     * @param metaPaths
     */
    @SuppressWarnings("unchecked")
    public SimpleSerialMetaPath(final List<SequenceMetaPath> metaPaths) {
        super((List<MetaPath>) (List<?>) metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SimpleSerialMetaPath(final String baseResKeyOrName, final SequenceMetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    @SuppressWarnings("unchecked")
    public SimpleSerialMetaPath(final String baseResKeyOrName, final List<SequenceMetaPath> metaPaths) {
        super(baseResKeyOrName, (List<MetaPath>) (List<?>) metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param direction
     * @param metaPaths
     */
    public SimpleSerialMetaPath(final String baseResKeyOrName, final Direction direction, final SequenceMetaPath... metaPaths) {
        super(baseResKeyOrName, direction, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param direction
     * @param metaPaths
     */
    @SuppressWarnings("unchecked")
    public SimpleSerialMetaPath(final String baseResKeyOrName, final Direction direction, final List<SequenceMetaPath> metaPaths) {
        super(baseResKeyOrName, direction, (List<MetaPath>) (List<?>) metaPaths);
    }

}
