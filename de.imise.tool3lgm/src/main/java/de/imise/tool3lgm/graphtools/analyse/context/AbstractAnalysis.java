package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_CREATE_NEW_SUBMODEL_FOR_ANALYSIS_RESULT;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author AXS (08.07.2011)
 */
public abstract class AbstractAnalysis extends MetaModelSpecificAdapter implements IDSource {

    /** der Name der Analyse. */
    protected String name;

    /** ID of this analysis (extablished in Tool-Version 4.4.1 (dev) */
    protected final String id;

    /** der Node, bei dem die Analyse beginnt. */
    protected List<Class<? extends ModelElement>> startClasses = new ArrayList<>();

    /**
     * @param metaModelSpecific
     * @param id
     */
    protected AbstractAnalysis(final MetaModelSpecific metaModelSpecific, final String id) {
        super(metaModelSpecific);
        this.id = id;
    }

    /**
     * Gibt den Namen der XMLAnalyse zurück.
     *
     * @return der Name der XMLAnalyse.
     */
    public String getName() {
        return name;
    }

    /**
     * Liefert die Ergenis-Elemente der Analyse für die in diesem GraphDocument
     * selektierten Elemente.
     *
     * @param doc
     * @return
     */
    public abstract List<ElementContainer> getResult(GraphDocument doc);

    /**
     * Gibt die Namen der Knotentypen zurück, auf die die XMLAnalyse angewandt
     * werden kann.<br>
     * ACHTUNG: Diese Liste wird nicht bei der Durchführung der XMLAnalyse
     * verwendet, sondern nur bei der Zuordnung, welche Analysen für wlche Node
     * zur Verfügung stehen.
     *
     * @return eine Liste der Startelementklassen.
     */
    public List<Class<? extends ModelElement>> getStartClasses() {
        return startClasses;
    }

    /**
     * @return Kommaseparierten String aller Startklassen der XMLAnalyse für die
     *         aktuelle Locale
     */
    public String getStartClassesDisplayNames() {
        if (startClasses == null || startClasses.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        MetaModel metaModel = getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        for (int i = 0; i < startClasses.size() - 1; i++) {
            sb.append(elementsNameBuilder.getDisplayableName(metaModel.getClassForName(startClasses.get(i).getName())));
            sb.append(", ");
        }
        sb.append(elementsNameBuilder.getDisplayableName(metaModel.getClassForName(startClasses.get(startClasses.size() - 1).getName())));
        return sb.toString();
    }

    /**
     * @param result
     * @param doc
     */
    public final void setAnalysisResult(final GraphDocument doc) {
        List<ElementContainer> result = getResult(doc);
        if (result != null) {
            if (OPTION_CREATE_NEW_SUBMODEL_FOR_ANALYSIS_RESULT.is()) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (startClasses == null ? 0 : startClasses.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractAnalysis other = (AbstractAnalysis) obj;
        if (!hasEqualsID(other)) {
            return false;
        }
        return hasEqualsContent(other, true);
    }

    /**
     * @param other
     * @param checkNameEquality
     * @return
     */
    public boolean hasEqualsContent(final AbstractAnalysis other, final boolean checkNameEquality) {
        if (checkNameEquality && !Objects.equal(getName(), other.getName())) {
            return false;
        }
        return Objects.equal(getStartClasses(), other.getStartClasses());

    }

    @Override
    public String getID() {
        return id;
    }

}
