package de.imise.tool3lgm.graphtools.metamodel;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import de.imise.tool3lgm.graphtools.analyse.context.AbstractAnalysis;
import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysisDefinitions;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions;

/**
 * Klasse, über die für ein Metamodell Analysen hinzugefügt werden können. Um
 * {@link NullPointerException}s zu verhindern, wird wenn für ein Metamodell
 * keine solche Klasse angegeben ist, immer eine Instanz dieser Kasse hier
 * zurück gegeben, die aber keine Actions zurück liefert. ATTNENTION: This class
 * will be instanciated by {@link Constructor#newInstance(Object...)}. So don't
 * make it abstract!
 *
 * @author AXS (5 Jun 2018)
 */
public class AnalysesDefinition {

    /** Metamodel der Definition */
    protected final MetaModel metaModel;

    /** Definition aller SimpleRedundancyAnalysis für dieses Metamodell */
    protected final SimpleRedundancyAnalysisDefinitions simpleRedundancyAnalysisDefinitions = new SimpleRedundancyAnalysisDefinitions();

    /**
     * Liefert die Definition aller SimpleRedundancyAnalysis für dieses
     * Metamodell
     */
    public SimpleRedundancyAnalysisDefinitions getSimpleRedundancyAnalysisDefinitions() {
        return simpleRedundancyAnalysisDefinitions;
    }

    /** Definition aller RedundancyAnalysis für dieses Metamodell */
    protected final RedundancyAnalysisDefinitions redundancyAnalysisDefinitions;

    /** Liefert die Definition aller RedundancyAnalysis für dieses Metamodell */
    public RedundancyAnalysisDefinitions getRedundancyAnalysisDefinitions() {
        return redundancyAnalysisDefinitions;
    }

    /**
     * Liste aller Analysen, die im Kontextmenü der Knoten zusätzlich zu denen
     * im AnalysesRepository definierten angezeigt werden sollen
     */
    protected final List<AbstractAnalysis> nodeAnalyses = new ArrayList<>();

    /**
     * @param metaModel
     */
    public AnalysesDefinition(final MetaModel metaModel) {
        this.metaModel = metaModel;
        redundancyAnalysisDefinitions = new RedundancyAnalysisDefinitions(metaModel);
    }

    /**
     * @return
     */
    public List<AbstractAnalysis> getNodeAnalyses() {
        return nodeAnalyses;
    }

    /**
     * Liste von Actions, die ins AnalyseMenü eingefügt werden
     *
     * @return
     */
    public Action[] getAnalysisActions() {
        return new Action[0];
    }

    /**
     * @return Returns the base name of the resource file containing the default
     *         analyses for this metamodel. The default return value is the
     *         empty String.
     */
    public String getXMLAnalysisRepositoryFileName() {
        return "";
    }

}
