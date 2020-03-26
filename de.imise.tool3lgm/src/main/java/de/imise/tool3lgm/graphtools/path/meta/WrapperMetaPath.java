package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Ein MetaPath, der einen anderen umschließt, um die Start und Endklasse zu ändern.
 *
 * @author AXS (28 Nov 2018)
 */
public class WrapperMetaPath extends SequenceMetaPath {

    /**
     * @param newStartClass
     * @param newEndClass
     * @param wrappedMetaPath
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
        int wrappedMetaPathSize = 3;
        int startClassPathIndex = newStartClass == null ? -1 : 0; // -1 oder 0
        wrappedMetaPathSize += startClassPathIndex; // bleibt 3 oder wird 2
        int edgePathIndex = startClassPathIndex + 1; // 0 oder 1
        int endClassPathIndex = newEndClass == null ? -1 : edgePathIndex + 1; // -1 oder 1 oder 2
        wrappedMetaPathSize += endClassPathIndex < 0 ? endClassPathIndex : 0; // bleibt 3 oder wird 2 oder wird 1
        AbstractMetaPath[] wrapped = new AbstractMetaPath[wrappedMetaPathSize];
        if (startClassPathIndex == 0) {
            MetaModel metaModel = wrapped[0].getMetaModel();
            wrapped[0] = new ElementaryMetaPath(metaModel, newStartClass);
            wrapped[1] = wrappedMetaPath;
        } else {
            wrapped[0] = wrappedMetaPath;
        }
        if (endClassPathIndex > 0) {
            MetaModel metaModel = wrapped[endClassPathIndex].getMetaModel();
            wrapped[endClassPathIndex] = new ElementaryMetaPath(metaModel, newEndClass);
        }
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
     * @param originalMetaPath
     */
    public static final AbstractMetaPath wrapMetaPath(final Class<? extends ModelElement> newStartClass, final Class<? extends ModelElement> newEndClass, final AbstractMetaPath originalMetaPath) {
        Set<Class<? extends ModelElement>> startClasses = originalMetaPath.getStartClasses();
        Class<? extends ModelElement> startClass = newStartClass != null && (startClasses.size() != 1 || !startClasses.contains(newStartClass)) ? newStartClass : null;
        Set<Class<? extends ModelElement>> endClasses = originalMetaPath.getEndClasses();
        Class<? extends ModelElement> endClass = newEndClass != null && (endClasses.size() != 1 || !endClasses.contains(newEndClass)) ? newEndClass : null;
        if (startClass != null && endClass != null) {
            if (originalMetaPath instanceof ElementaryMetaPath) {
                //Elementarpfade werden nicht gewrapped sondern neu angelegt, d.h. sie 'wrappen sich selbst'
                MetaModel metaModel = originalMetaPath.getMetaModel();
                ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
                return elementaryMetaPathHandler.getMetaPath(startClass, (ElementaryMetaPath) originalMetaPath, endClass);
            }
            return new WrapperMetaPath(startClass, endClass, originalMetaPath);
        }
        return originalMetaPath;
    }

    @Override
    protected final String createName() {
        AbstractMetaPath firstMetaPath = subMetaPaths.get(0);
        if (firstMetaPath instanceof ElementaryMetaPath && ((ElementaryMetaPath) firstMetaPath).getType() == ElementaryMetaPath.Type.SINGLE_ELEMENT) {
            return subMetaPaths.get(1).createName();
        }
        return firstMetaPath.createName();
    }

}
