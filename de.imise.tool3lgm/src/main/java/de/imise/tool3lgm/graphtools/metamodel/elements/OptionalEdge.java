package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * Dieses Interface ist speziell für die Templates eingeführt worden, um der Kante zwischen IheActor und IheInterface die Optionalität mitgeben zu
 * können. Dieses Interface sollte von Kanten impelementiert werden, die bei einem Template als Optional oder Required gekennzeichnet werden sollen.
 *
 * @author AXS (11 Mar 2019)
 */
public interface OptionalEdge {

    /**
     * @return
     */
    public default String getOptionDisplayName() {
        return isOptional() ? getOptionOptionalDisplayName() : getOptionRequiredDisplayName();
    }

    public default GDCollection getCollection() {
        if (!(this instanceof Edge)) {
            return null;
        }
        Edge me = (Edge) this;
        GDCollection gdcoll = me.getCollection();
        return gdcoll;
    }

    /**
     * @return
     */
    public default boolean isOptional() {
        GDCollection gdcoll = getCollection();
        return gdcoll == null ? false : gdcoll.isOptional(this);
    }

    /**
     * @param value
     */
    public default boolean setOptional(final boolean value) {
        GDCollection gdcoll = getCollection();
        if (gdcoll == null) {
            return false;
        }
        return value ? gdcoll.addOptional(this) : gdcoll.removeOptional(this);
    }

    /**
     * @param edge
     * @return
     */
    public static boolean isOptional(final Edge edge) {
        return edge instanceof OptionalEdge ? ((OptionalEdge) edge).isOptional() : false;
    }

    /**
     * Liefert den Namen der Optionen, den man z.B. in einer Tabelle als Spaltenüberschrift nehmen kann. Default de ist 'Optionalität' und en
     * 'Optionality'.
     *
     * @return
     */
    public static String getOptionalityName() {
        return Tool3lgmConstants.getResString("OPTIONALITY_NAME");
    }

    /**
     * @return de 'O'; en 'O'
     */
    public static String getOptionOptionalDisplayName() {
        return Tool3lgmConstants.getResString("OPTIONALITY_OPTIONAL");
    }

    /**
     * @return de 'R'; en 'R'
     */
    public static String getOptionRequiredDisplayName() {
        return Tool3lgmConstants.getResString("OPTIONALITY_REQUIRED");
    }

    /**
     * @param edge
     * @return
     */
    public static String getOptionDisplayName(final Edge edge) {
        if (!(edge instanceof OptionalEdge)) {
            return null;
        }
        boolean optional = ((OptionalEdge) edge).isOptional();
        return optional ? getOptionOptionalDisplayName() : getOptionRequiredDisplayName();
    }

}
