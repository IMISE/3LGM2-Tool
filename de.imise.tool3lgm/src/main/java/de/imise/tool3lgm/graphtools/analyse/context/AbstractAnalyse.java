package de.imise.tool3lgm.graphtools.analyse.context;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.userproperties.UserProperties;

public abstract class AbstractAnalyse {

    /** der Name der Analyse. */
    protected String name;

    /** der Knoten, bei dem die Analyse beginnt. */
    protected ArrayList<Class<? extends ModelElement>> startknoten = new ArrayList<Class<? extends ModelElement>>();

    /**
     * Gibt den Namen der XMLAnalyse zurück.
     *
     * @return der Name der XMLAnalyse.
     */
    public String getName() {
        return name;
    }

    /**
     * Liefert die Ergenis-Elemente der Analyse für die in diesem GraphDocument selektierten
     * Elemente.
     *
     * @param doc
     * @return
     */
    public abstract List<ElementContainer> getResult(GraphDocument doc);

    /**
     * Gibt die Namen der Knotentypen zurück, auf die die XMLAnalyse angewandt werden kann.<br>
     * ACHTUNG: Diese Liste wird nicht bei der Durchführung der XMLAnalyse verwendet, sondern nur
     * bei der Zuordnung, welche Analysen für wlche Knoten zur Verfügung stehen.
     *
     * @return eine ArrayList der Startknoten.
     */
    public ArrayList<Class<? extends ModelElement>> getStartknoten() {
        return startknoten;
    }

    /**
     * @return Kommaseparierten String aller Startklassen der XMLAnalyse für die aktuelle Locale
     */
    public String getStartknotenString() {
        if (startknoten == null || startknoten.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < startknoten.size() - 1; i++) {
            sb.append(ModelConstants.getDisplayableName(ModelConstants.getClassForName(startknoten.get(i).getName())));
            sb.append(", ");
        }
        sb.append(ModelConstants.getDisplayableName(ModelConstants.getClassForName(startknoten.get(startknoten.size() - 1).getName())));
        return sb.toString();
    }

    /**
     * @param result
     * @param doc
     */
    public final void setAnalysisResult(final GraphDocument doc) {
        List<ElementContainer> result = getResult(doc);
        if (result != null) {
            if (UserProperties.isNewSubmodelForAnalysis()) {
                doc.addContainerToNewSzenario(result, TransactionManager.STANDARD_PID);
            } else {
                doc.setAnalysisResult(result);
            }

        }
    }

    /**
     * Gibt der XMLAnalyse einen neuen Namen.
     *
     * @param name neuer Name der XMLAnalyse.
     */
    public void setName(final String name) {
        this.name = name;
    }

}
