/*
 * Created on 12.11.2007
 */
package de.imise.tool3lgm.graphtools.analyse.redundancy;

import java.util.ArrayList;
import java.util.HashMap;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPath;
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
    AlphabeticalSet<ModelElement> exclusiveAWB = new AlphabeticalSet<ModelElement>();

    /**
     * Menge aller AWB, die überhaupt keine Aufgabe unterstützen
     */
    AlphabeticalSet<ModelElement> notSupportingAWB = new AlphabeticalSet<ModelElement>();

    /**
     * Menge aller AWB, auf die man verzichten kann, wenn man alle exklusiven AWB hat
     */
    AlphabeticalSet<ModelElement> uselessAWB = new AlphabeticalSet<ModelElement>();

    /**
     * Liste aller AWB, auf die man auch verzichten kann, da sie in keinem Minimalset vorkommen
     */
    AlphabeticalSet<ModelElement> moreUselessAWB = new AlphabeticalSet<ModelElement>();

    /**
     * Liste aller AWB, auf die man auch braucht, da sie in jedem Minimalset vorkommen, die aber nur
     * eine einelementige Äquivalenzklasse bilden.
     */
    AlphabeticalSet<ModelElement> moreNeededAWB = new AlphabeticalSet<ModelElement>();

    /**
     * Liste von <code>AlphabeticalSet</code>s mit AWB, die jeweils alle dieselben Aufgaben
     * unterstützen.<br>
     * Die betrachteten Aufgaben sind bereits eingeschränkt um alle Aufgaben, die bereits von den
     * exklusiven AWB unterstützt werden.
     */
    ArrayList<AlphabeticalSet<ModelElement>> equalsSets;

    /**
     * Mappt von einem überflüssigen AWB auf alle AWB, die nicht verzichtbar sind und die diesen AWB
     * überflüssig machen. Das heißt in der Liste sind alle AWB, die in <code>exclusiveAWB</code>
     * oder <code>moreNeededAWB</code> vorkommen und mind. eine Aufgabe des überflüssigen AWB
     * unterstützen.
     */
    HashMap<ModelElement, AlphabeticalSet<ModelElement>> uselessToNeeded = new HashMap<ModelElement, AlphabeticalSet<ModelElement>>();

    /**
     * Modell das analysiert werden soll
     */
    private final GDCollection gdcoll;

    /**
     * Klasse für deren Elemente Redundanz aufgedeckt werden soll.
     */
    private final Class<? extends ModelElement> startClass;

    /**
     * Klasse bezüglich der Elemente der <code>startClass</code> redundant sein können.
     */
    private final Class<? extends ModelElement> endClass;

    /**
     * MetaPfad über den Elemente der Art <code>startClass</code> und <code>endClass</code>
     * analysiert werden sollen.
     */
    private final MetaPath metaPath;

    /**
     * String der bei der Ausgabe des Ergebnisses als Analyseoption angezeigt werden soll
     */
    private final String analyseOptionString;

    /**
     * @param gdcoll Modell, das analysiert werden soll
     * @param startClass Klasse für deren Elemente Redundanz aufgedeckt werden soll
     * @param endClass Klasse bezüglich der Elemente der <code>endClass</code> redundant sein können
     * @param metaPath MetaPfad über den Elemente der Art <code>startClass</code> und
     *            <code>endClass</code> analysiert werden sollen.
     * @param analyseOptionString String der bei der Ausgabe des Ergebnisses als Analyseoption
     *            angezeigt werden soll
     */
    public RedundancyAnalysisResult(final GDCollection gdcoll, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final MetaPath metaPath, final String analyseOptionString) {
        super();
        this.gdcoll = gdcoll;
        this.startClass = startClass;
        this.endClass = endClass;
        this.metaPath = metaPath;
        this.analyseOptionString = analyseOptionString;
    }

    /**
     * @return Returns the endClass.
     */
    public Class<? extends ModelElement> getEndClass() {
        return endClass;
    }

    /**
     * @return Returns metaPath.
     */
    public MetaPath getMetaPath() {
        return metaPath;
    }

    /**
     * @return Returns the startClass.
     */
    public Class<? extends ModelElement> getStartClass() {
        return startClass;
    }

    /**
     * @return Returns the gdcoll.
     */
    public GDCollection getGDCollection() {
        return gdcoll;
    }

    /**
     * @return Returns the analyseOptionString.
     */
    public String getAnalyseOptionString() {
        return analyseOptionString;
    }
}
