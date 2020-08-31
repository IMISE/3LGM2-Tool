package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.Iterator;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS
 * @create 13.10.2010
 */
public class DifferenceMetaPath extends ParallelMetaPath {

    /**
     * @param metaPaths
     */
    public DifferenceMetaPath(final AbstractMetaPath... metaPaths) {
        super(metaPaths);
    }

    /**
     * @param name
     * @param metaPaths
     */
    public DifferenceMetaPath(final String name, final AbstractMetaPath... metaPaths) {
        super(name, metaPaths);
    }

    /**
     * @param other
     */
    public DifferenceMetaPath(final ParallelMetaPath other) {
        super(other);
    }

    @Override
    protected void initStartEndClasses() {
        ImmutableSet.Builder<Class<? extends ModelElement>> startElementClassesBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<Class<? extends ModelElement>> endElementClassesBuilder = ImmutableSet.builder();
        startElementClassesBuilder.addAll(subMetaPaths.get(0).startElementClasses);
        endElementClassesBuilder.addAll(subMetaPaths.get(0).endElementClasses);
        startElementClasses = startElementClassesBuilder.build();
        endElementClasses = endElementClassesBuilder.build();
    }

    @Override
    protected String createName() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        Iterator<AbstractMetaPath> it = subMetaPaths.iterator();
        if (it.hasNext()) {
            sb.append(it.next().getFullName());
        }
        sb.append(")");
        if (it.hasNext()) {
            sb.append(" ABER NICHT {auslagern !!!} (");
        }
        sb.append(it.next());
        while (it.hasNext()) {
            sb.append(" UND {auslagern !!!} ");
            sb.append(it.next().getFullName());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    protected boolean canBeRecursive() {
        //rekursiv, wenn der erste MetaPath (der die Elemente festlegt, von denen die der folgenden MetaPfade abgezogen werden) rekursiv ist
        AbstractMetaPath firstMetaPath = subMetaPaths.get(0);
        return firstMetaPath.canBeRecursive();
    }

    @Override
    public boolean isCreatable(final boolean checkCreateEndElement) {
        return false;
    }

    @Override
    public DifferenceMetaPath createInstance(final AbstractMetaPath... subMetaPaths) {
        return new DifferenceMetaPath(subMetaPaths);
    }

}
