package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Set;

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
    private WrapperMetaPath(final Class<? extends ModelElement> newStartClass, final Class<? extends ModelElement> newEndClass, final AbstractMetaPath wrappedMetaPath) {
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

    /**
     * Wenn die übergebenen und aktuell ausgewählten Klassen nicht die Start- und Endklasse des Pfades ist, dann werden die Start- und Endklassen
     * durch diese übergebenen ersetzt.
     * Das Ersetzten geschieht durch das Anlegen eines Wrapper-Pfades, der den originalen MetaPfad in der Mitte hat und einen einfachen Elementarpfad
     * mit der neuen Startklasse davor bzw. mit der Endlasse danach.
     * Stimmen die Start- und Endklasse des Pfades mit den übergebenen Klassen überein, kommt der origialMetaPath zurück
     *
     * @param newStartClass
     * @param newEndClass
     * @param orginalMetaPath
     */
    public static final AbstractMetaPath wrapMetaPath(final Class<? extends ModelElement> newStartClass, final Class<? extends ModelElement> newEndClass, final AbstractMetaPath orginalMetaPath) {
        Set<Class<? extends ModelElement>> startClasses = orginalMetaPath.getStartClasses();
        boolean wrap = startClasses.size() != 1 || !startClasses.contains(newStartClass);
        if (!wrap) {
            Set<Class<? extends ModelElement>> endClasses = orginalMetaPath.getEndClasses();
            wrap = endClasses.size() != 1 || !endClasses.contains(newEndClass);
        }
        if (wrap) {
            return new WrapperMetaPath(newStartClass, newEndClass, orginalMetaPath);
        }
        return orginalMetaPath;
    }

    @Override
    protected final String createName() {
        return metaPaths.get(1).getName();
    }

}
