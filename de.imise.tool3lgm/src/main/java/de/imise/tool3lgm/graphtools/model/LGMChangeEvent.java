package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * This class is only needed to decide the same/equals event was fired already
 * in the bulk_mode of the {@link GDCollection}.
 *
 * @author AXS (26.06.2020)
 */
public class LGMChangeEvent {

    final LGMChangeType changeType;
    final ElementContainer last_elem;
    final GraphDocument source;
    final int pid;

    /**
     * @param changeType
     * @param last_elem
     * @param source
     * @param pid
     */
    public LGMChangeEvent(final LGMChangeType changeType, final ElementContainer last_elem, final GraphDocument source, final int pid) {
        this.changeType = changeType;
        this.last_elem = last_elem;
        this.source = source;
        this.pid = pid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (changeType == null ? 0 : changeType.hashCode());
        result = prime * result + (last_elem == null ? 0 : last_elem.hashCode());
        result = prime * result + pid;
        result = prime * result + (source == null ? 0 : source.hashCode());
        return result;
    }
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LGMChangeEvent other = (LGMChangeEvent) obj;
        if (changeType != other.changeType) {
            return false;
        }
        if (last_elem == null) {
            if (other.last_elem != null) {
                return false;
            }
        } else if (!last_elem.equals(other.last_elem)) {
            return false;
        }
        if (pid != other.pid) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!source.equals(other.source)) {
            return false;
        }
        return true;
    }

}
