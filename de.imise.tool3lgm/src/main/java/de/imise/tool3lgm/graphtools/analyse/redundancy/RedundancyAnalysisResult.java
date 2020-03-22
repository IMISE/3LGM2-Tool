/*
 * Created on 12.11.2007
 */
package de.imise.tool3lgm.graphtools.analyse.redundancy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysisDefinitions.SingleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.util.collections.AlphabeticalSet;

/**
 * Repräsentiert das Ergebnis einer Redundanzanalyse.<br>
 * Es enhält folgende Mengen:
 * <ol>
 * <li>auf jeden Fall benötigte Elemente</li>
 * <li>auf jeden Fall nicht benötigte Elemente</li>
 * <li>äquivalente Elemente, von denen man eigentlich nur 1 braucht</li>
 * <li>Elemente, die man in jedem minmalen Set vorkommen</li>
 * <li>Elemente, die man in keinem minmalen Set vorkommen</li>
 * </ol>
 * Ein solches 'Ergebnis' wird mit den zu analysierenden Klassen und dem MetaPfad initialisiert und
 * dann in den Redundanzalgorithmus geschickt, der das Ergebnis füllt.
 *
 * @author AXS
 */
public class RedundancyAnalysisResult {

    /**
     * Menge aller AWB, die man in jeder minimalen Menge braucht. Sie unterstützen mind. eine
     * Funktion als einziger Baustein.
     */
    AlphabeticalSet<ModelElement> exclusiveAWB = new AlphabeticalSet<>();

    /**
     * Menge aller AWB, die überhaupt keine Aufgabe unterstützen
     */
    AlphabeticalSet<ModelElement> notSupportingAWB = new AlphabeticalSet<>();

    /**
     * Menge aller AWB, auf die man verzichten kann, wenn man alle exklusiven AWB hat
     */
    AlphabeticalSet<ModelElement> uselessAWB = new AlphabeticalSet<>();

    /**
     * Liste aller AWB, auf die man auch verzichten kann, da sie in keinem Minimalset vorkommen
     */
    AlphabeticalSet<ModelElement> moreUselessAWB = new AlphabeticalSet<>();

    /**
     * Liste aller AWB, auf die man auch braucht, da sie in jedem Minimalset vorkommen, die aber nur
     * eine einelementige Äquivalenzklasse bilden.
     */
    AlphabeticalSet<ModelElement> moreNeededAWB = new AlphabeticalSet<>();

    /**
     * Liste von <code>AlphabeticalSet</code>s mit AWB, die jeweils alle dieselben Aufgaben
     * unterstützen.<br>
     * Die betrachteten Aufgaben sind bereits eingeschränkt um alle Aufgaben, die bereits von den
     * exklusiven AWB unterstützt werden.
     */
    List<AlphabeticalSet<ModelElement>> equalsSets;

    /**
     * Mappt von einem überflüssigen AWB auf alle AWB, die nicht verzichtbar sind und die diesen AWB
     * überflüssig machen. Das heißt in der Liste sind alle AWB, die in <code>exclusiveAWB</code>
     * oder <code>moreNeededAWB</code> vorkommen und mind. eine Aufgabe des überflüssigen AWB
     * unterstützen.
     */
    Map<ModelElement, AlphabeticalSet<ModelElement>> uselessToNeeded = new HashMap<>();

    /**
     * Modell das analysiert werden soll
     */
    private final GDCollection gdcoll;

    /**
     * MetaPfad über den Elemente der Art <code>startClass</code> und <code>endClass</code>
     * analysiert werden sollen.
     */
    private final SingleRedundancyAnalysisDefinition definition;

    /**
     * String der bei der Ausgabe des Ergebnisses als Analyseoption angezeigt werden soll
     */
    private final String analysisOptionString;

    /**
     * @param gdcoll Modell, das analysiert werden soll
     * @param definition
     *            Die Definition mit dem MetaPfad, über den Elemente der Art <code>startClass</code> und <code>endClass</code> analysiert werden
     *            sollen. Die startClass des Metapfades ist die Klasse, für deren Elemente Redundanz aufgedeckt werden soll und die endClass
     *            ist die Klasse, bezüglich der Elemente der <code>endClass</code> redundant sein können.
     * @param analysisOptionString
     *            String der bei der Ausgabe des Ergebnisses als Analyseoption angezeigt werden soll
     */
    public RedundancyAnalysisResult(final GDCollection gdcoll, final SingleRedundancyAnalysisDefinition definition, final String analysisOptionString) {
        super();
        this.gdcoll = gdcoll;
        this.definition = definition;
        this.analysisOptionString = analysisOptionString;
    }

    /**
     * @return Returns the gdcoll.
     */
    public GDCollection getCollection() {
        return gdcoll;
    }

    /**
     * @return Returns the definition.
     */
    public SingleRedundancyAnalysisDefinition getDefinition() {
        return definition;
    }

    /**
     * @return Returns the analyseOptionString.
     */
    public String getAnalysisOptionString() {
        return analysisOptionString;
    }
}
