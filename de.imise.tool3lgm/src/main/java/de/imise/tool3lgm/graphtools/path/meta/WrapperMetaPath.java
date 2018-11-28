package de.imise.tool3lgm.graphtools.path.meta;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Ein MetaPath, der einen anderen umschließt, um die Start und Endklasse zu ändern.
 *
 * @author AXS (28 Nov 2018)
 */
public class WrapperMetaPath extends SequenceMetaPath {

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public WrapperMetaPath(final Class<? extends ModelElement> newStartClass, final Class<? extends ModelElement> newEndClass, final AbstractMetaPath wrappedMetaPath) {
        super(getWrappedMetaPath(newStartClass, newEndClass, wrappedMetaPath));
    }

    /**
     * @param newStartClass
     * @param newEndClass
     * @param wrappedMetaPath
     * @return
     */
    private static final AbstractMetaPath[] getWrappedMetaPath(final Class<? extends ModelElement> newStartClass, final Class<? extends ModelElement> newEndClass, final AbstractMetaPath wrappedMetaPath) {
        AbstractMetaPath[] wrapped = new AbstractMetaPath[3];
        wrapped[0] = new ElementaryMetaPath(newStartClass);
        wrapped[1] = wrappedMetaPath;
        wrapped[2] = new ElementaryMetaPath(newEndClass);
        return wrapped;
    }

    @Override
    protected final String createName() {
        return metaPaths.get(1).getName();
    }

}
