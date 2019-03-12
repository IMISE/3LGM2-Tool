package de.imise.tool3lgm.graphtools.metamodel.elements;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * Dieses Interface ist speziell für die Templates eingeführt worden, um der Kante zwischen IheActor und IheInterface die Optionalität mitgeben zu
 * können.
 *
 * @author AXS (11 Mar 2019)
 */
public interface Optional {

    public static final String DEFAUL_RESOURCE_KEY_PREFIX = "OPTIONAL_";

    public static final String DEFAULT_RESOURCE_KEY_HEADER = "OPTIONAL_Optional";

    /**
     * Liefert den Namen der Optionen, den man z.B. in einer Tabelle als Spaltenüberschrift nehmen kann. Soll eine Klasse, die mit diesem Interface
     * versehen ist hier etwas anderes als den Standard "Optional" zurück liefern, dann muss sie diese Funktion überschreiben oder einfach in den
     * Metamodell-Resourcen einen Schlüssel der Form "OPTION_" + Klassenname hinterlegen. Also z.B. wenn die Klasse Tool3lgm dieses Interface
     * impelmentiert, dann müsste Schlüssel in den Resourcen "OPTION_Tool3lgm" heißen.
     *
     * @return
     */
    public default String getOptionName() {
        return getOptionName(getClass());
    }

    public static String getOptionName(final Class<?> clazz) {
        String optionNameResKey = DEFAUL_RESOURCE_KEY_PREFIX + clazz.getSimpleName();
        String optionName = Tool3lgmConstants.getResStringWithoutError(optionNameResKey);
        if (optionName != optionNameResKey) { // Schlüssel gefunden
            return optionName;
        }
        //Schlüssel wurde nicht gefunden -> "Optional" aus den Standard-Resourcen kommt zurück
        return Tool3lgmConstants.getResString(DEFAULT_RESOURCE_KEY_HEADER);
    }

    /**
     * Liefert alle Options-Namen. Standardmäßig wird nach den Optionen in den Metamodell-Resourcen gesucht. Die Schlüssel der Optionen sind bilden
     * sich dabei aus dem "OPTION_" + Klassenname + "_" + Nummer. Die Nummer muss bei 1 beginnen und fortlaufend sein. Bei einer Klasse mit dem Namen
     * "Tool3lgm", die dieses Interface implementiert, würde die default-Implementierung also nach Schlüsseln der Form "OPTION_Tool3lgm_1",
     * "OPTION_Tool3lgm_2" usw.<br>
     * Wenn keine metamodellspezifischen Optionen angegeben sind, dann werden die beiden Standard-Optionen: "Optional" und "Required" geladen.
     */
    public default List<String> getOptions() {
        List<String> options = new ArrayList<>();
        String optionResKeyPrefix = DEFAUL_RESOURCE_KEY_PREFIX + getClass().getSimpleName() + "_";
        for (int i = 1; i <= Integer.MAX_VALUE; i++) {
            String optionResKey = optionResKeyPrefix + i;
            try {
                String optionDisplayName = Tool3lgmConstants.getResString(optionResKey);
                options.add(optionDisplayName);
            } catch (Exception e) {
                break;
            }

        }
        //Standard-Optionen Optional und Required
        if (options.isEmpty()) {
            options.add(Tool3lgmConstants.getResString("OPTIONAL_Optional_1"));
            options.add(Tool3lgmConstants.getResString("OPTIONAL_Optional_2"));
        }
        return options;
    }

}
