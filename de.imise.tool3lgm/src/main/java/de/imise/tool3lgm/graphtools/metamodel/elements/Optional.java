package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * Dieses Interface ist speziell für die Templates eingeführt worden, um der Kante zwischen IheActor und IheInterface die Optionalität mitgeben zu
 * können. Dieses Interface sollte von Kanten impelementiert werden, die bei einem Template als Optional oder Required gekennzeichnet werden sollen.
 *
 * @author AXS (11 Mar 2019)
 */
public interface Optional {

    public default String getOptionValue() {
        return isOptional() ? getOptionOptionalDisplayName() : getOptionRequiredDisplayName();
    }

    public boolean isOptional();

    public default boolean isOptional(final ModelElement me) {
        return me instanceof Optional ? ((Optional) me).isOptional() : false;
    }

    /**
     * Liefert den Namen der Optionen, den man z.B. in einer Tabelle als Spaltenüberschrift nehmen kann.
     *
     * @return
     */
    public static String getOptionalityName() {
        return Tool3lgmConstants.getResString("OPTIONALITY_NAME");
    }

    public static String getOptionOptionalDisplayName() {
        return Tool3lgmConstants.getResString("OPTIONALITY_OPTIONAL");
    }

    public static String getOptionRequiredDisplayName() {
        return Tool3lgmConstants.getResString("OPTIONALITY_REQUIRED");
    }

    public static String getOptionDisplayName(final ModelElement me) {
        if (!(me instanceof Optional)) {
            return null;
        }
        boolean optional = ((Optional) me).isOptional();
        if (me instanceof Edge) {
            Edge edge = (Edge) me;
            ModelElement end = edge.getEnd();
            String name = end.getName();
            optional |= name.contains("[ITI-8]") || name.contains("[ITI-44]") || name.contains("[ITI-57]");
        }
        return optional ? getOptionOptionalDisplayName() : getOptionRequiredDisplayName();
    }

}
