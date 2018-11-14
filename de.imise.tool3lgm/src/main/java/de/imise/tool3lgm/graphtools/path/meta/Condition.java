package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS
 * @create 12.10.2011
 */
public class Condition {

    public static enum CountCondition {
        GREATER,
        LOWER,
        EQUALS,
        GREATER_OR_EQUALS,
        LOWER_OR_EQUALS
    };

    private final boolean countConnectionsAndNotConnectedElements = false;
    private final boolean ignoreDoubleConnectionsToSameElements = true;

    public static enum ConnectionStateCondition {
        CONNECTED,
        NOT_CONNECTED
    };

    private int connectionCount = -1;

    private Collection<ModelElement> connectedElements = null;

    private AbstractMetaPath metaPath = null;

    private final CountCondition countCondition;

    private final ConnectionStateCondition connectionStateCondition;

    /**
     *
     */
    private Condition(final AbstractMetaPath metaPath, final CountCondition countCondition, final ConnectionStateCondition connectionStateCondition, final int connectionCount, final Collection<ModelElement> connectedElements) {
        super();
        this.metaPath = metaPath;
        this.countCondition = countCondition;
        this.connectionStateCondition = connectionStateCondition;
        this.connectionCount = connectionCount;
        this.connectedElements = connectedElements;
    }

    /**
     * @return the connectionCount
     */
    public final int getConnectionCount() {
        return connectionCount;
    }

    /**
     * @return the connectedElements
     */
    public final Collection<ModelElement> getConnectedElements() {
        return connectedElements;
    }

    public static final Condition getElementCountGreaterCondition(final AbstractMetaPath metaPath, final int elementCount) {
        //        Condition condition = new Condition(CountCondition.GREATER);
        //        condition.connectionCount = elementCount;
        //        return condition;
        return null;
    }
}
