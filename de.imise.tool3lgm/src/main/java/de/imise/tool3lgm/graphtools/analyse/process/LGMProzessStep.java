/*
 * Created on 26.02.2004 To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.analyse.process;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Konfiguration;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.elements.node.Prozess;

/**
 * @author AXS To change this generated comment go to Window>Preferences>Java>Code Generation>Code
 *         and Comments
 */
public class LGMProzessStep {

    /**
     * Aufgabe, die in desem Schritt <code>objektTyp</code> bearbeitet
     */
    private final Aufgabe startAufgabe;
    /**
     * Aufgabe, die in desem Schritt <code>objektTyp</code> interpretiert
     */
    private final Aufgabe endAufgabe;
    /**
     * Objekttyp, der von <code>startAufgabe</code> bearbeitet und von <code>endAufgabe</code>
     * interpretiert wird
     */
    private final Objekttyp objektTyp;

    /**
     * Konfiguration von <code>startAufgabe</code>
     */
    private ABKonfiguration startAufgabeKonf;
    /**
     * Container einer Konfiguration von endAufgabeCont
     */
    private ABKonfiguration endAufgabeKonf;

    /**
     * Sequenz der Schnittstellen, über die dieser Prozess kommuniziert werden kann
     */
    private List<ModelElement> kommProzessSchnittstellen;

    /**
     * Sequenz der Kanten, über die dieser Prozess kommuniziert werden kann
     */
    private List<Kante> kommProzessKanten;

    /**
     * KonfigurationContainer, an denen die mögliche Kommunikation wirklich startet
     */
    private List<ModelElement> realCommunicationStartKonfConts;

    /**
     * KonfigurationContainer, an dem die mögliche Kommunikation wirklich endet
     */
    private List<ModelElement> realCommunicationEndKonfConts;

    /**
     * die Positionen geben an, wo die startAufgabe und die endAufgabe im Prozess stehen (die selben
     * Aufgaben können mehrfach im Prozess vorkommen)
     */
    private int startPosition = -1, endPosition = -1;

    /**
     * Gibt die Anzahl der Schnitstellen wieder (ist zur Unterscheidung, ob es gar keinen Weg gibt
     * (-1) oder der Weg die Länge 0 hat (beide Konfigurationen überschneiden sich)
     */
    private int kommProzessLength = -1;

    /**
     * Anzahl der Medienbrüche bei der Kommunikation
     */
    private int mediumBreaks = 0;

    /**
     * Prozess, in dem sich dieser Prozessschritt befindet
     */
    private final Prozess parentProzess;

    // ----------------------------------------------------------------------------------------------------------------------------------
    /**
	 * 
	 */
    public LGMProzessStep(final ModelElement parentProzess, final ModelElement startAufgabe, final ModelElement endAufgabe, final ModelElement objektTyp, final ABKonfiguration startAufgabeKonf, final ABKonfiguration endAufgabeKonf,
            final int startPosition, final int endPosition) {
        super();
        this.parentProzess = (Prozess) parentProzess;
        this.startAufgabe = (Aufgabe) startAufgabe;
        this.endAufgabe = (Aufgabe) endAufgabe;
        this.objektTyp = (Objekttyp) objektTyp;
        this.startAufgabeKonf = startAufgabeKonf;
        this.endAufgabeKonf = endAufgabeKonf;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    /**
	 * 
	 */
    public LGMProzessStep(final ModelElement parentProzess, final ModelElement startAufgabe, final ModelElement endAufgabe, final ModelElement objektTyp, final int startPosition, final int endPosition) {
        this(parentProzess, startAufgabe, endAufgabe, objektTyp, null, null, startPosition, endPosition);
    }

    /**
	 * 
	 */
    public LGMProzessStep clone(final Object o) {
        LGMProzessStep s = (LGMProzessStep) o;
        LGMProzessStep returnStep = new LGMProzessStep(s.getParentProzess(), s.startAufgabe, s.endAufgabe, s.objektTyp, s.startAufgabeKonf, s.endAufgabeKonf, s.startPosition, s.endPosition);
        // TODO:AXS:prüfen, warum bei diesen komischen clones die Konfigs doppelt gesetzt werden und
        // warum nicht gleich im Konstruktor
        returnStep.setStartAufgabeKonf(s.getStartAufgabeKonf());
        returnStep.setEndAufgabeKonf(s.getEndAufgabeKonf());
        return returnStep;
    }

    /**
	 * 
	 */
    public static LGMProzessStep cloneAndSetKonfigs(final Object step, final Object startKonfig, final Object endKonfig) {
        LGMProzessStep s = (LGMProzessStep) step;
        LGMProzessStep returnStep = new LGMProzessStep(s.getParentProzess(), s.startAufgabe, s.endAufgabe, s.objektTyp, s.startAufgabeKonf, s.endAufgabeKonf, s.startPosition, s.endPosition);
        returnStep.setStartAufgabeKonf((ABKonfiguration) startKonfig);
        returnStep.setEndAufgabeKonf((Konfiguration) endKonfig);
        return returnStep;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * @return <code>true</code>, wenn dieser Step eine gültige Start-Konfiguration mit einem
     *         Anwendungssystem besitzt
     */
    public boolean hasStartKonfiguration() {
        return startAufgabeKonf != null && startAufgabeKonf.getConnectedElements(Anwendungsbaustein.class).size() != 0;
    }

    /**
     * @return <code>true</code>, wenn dieser Step eine gültige End-Konfiguration mit einem
     *         Anwendungssystem besitzt
     */
    public boolean hasEndKonfiguration() {
        return endAufgabeKonf != null && endAufgabeKonf.getConnectedElements(Anwendungsbaustein.class).size() != 0;
    }

    // hasStartAufgabe() und hasObjekttyp() sollten, wenn alles wie gedacht funktioniert, immer
    // dasselbe liefern
    // d.h. es ex. eine Startaufgabe gdw. ein Objekttyp ex.

    /**
     * Prüft, ob für den Step eine gültige Start-Aufgabe besitzt
     * 
     * @return <code>true</code>, wenn der Step eine gültige Start-Aufgabe besitzt sonst
     *         <code>false</code>
     */
    public boolean hasStartAufgabe() {
        return startAufgabe != null;
    }

    /**
     * Prüft, ob für den Step eine gültige End-Aufgabe besitzt
     * 
     * @return <code>true</code>, wenn der Step eine gültige End-Aufgabe besitzt sonst
     *         <code>false</code>
     */
    public boolean hasObjekttyp() {
        return objektTyp != null;
    }

    /**
     * Prüft, ob alle Variablen mit gültigen Werten belegt sind, also nicht <code>null</code> sind.
     * 
     * @return <code>true</code>, wenn alle Variablen nicht <code>null</code> sind.
     */
    public boolean isCorrect() {
        return hasStartAufgabe() && hasObjekttyp() && hasStartKonfiguration() && hasEndKonfiguration();
    }

    // ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * @param list
     */
    public void setKommProzessKanten(final List<Kante> list) {
        kommProzessKanten = list;
    }

    /**
     * @param list
     */
    public void setKommProzessSchnittstellen(final List<ModelElement> list) {
        kommProzessSchnittstellen = list;
    }

    // die sollte man nicht von aussen setzen
    /**
     * @param konf
     */
    private void setStartAufgabeKonf(final ModelElement konf) {
        startAufgabeKonf = (ABKonfiguration) konf;
    }

    /**
     * @param konf
     */
    private void setEndAufgabeKonf(final ModelElement konf) {
        endAufgabeKonf = (ABKonfiguration) konf;
    }

    /**
     * @return
     */
    public Aufgabe getStartAufgabe() {
        return startAufgabe;
    }

    /**
     * @return
     */
    public Aufgabe getEndAufgabe() {
        return endAufgabe;
    }

    /**
     * @return
     */
    public Objekttyp getObjektTyp() {
        return objektTyp;
    }

    /**
     * @return
     */
    public ABKonfiguration getStartAufgabeKonf() {
        return startAufgabeKonf;
    }

    /**
     * @return
     */
    public ABKonfiguration getEndAufgabeKonf() {
        return endAufgabeKonf;
    }

    /**
     * @return
     */
    public List<ModelElement> getRealCommunicationEndKonf() {
        return realCommunicationEndKonfConts;
    }

    /**
     * @return
     */
    public List<ModelElement> getRealCommunicationStartKonf() {
        return realCommunicationStartKonfConts;
    }

    /**
     * @param list
     */
    public void setRealCommunicationEndKonf(final List<ModelElement> list) {
        realCommunicationEndKonfConts = list;
    }

    /**
     * @param list
     */
    public void setRealCommunicationStartKonf(final List<ModelElement> list) {
        realCommunicationStartKonfConts = list;
    }

    /**
     * @return
     */
    public Prozess getParentProzess() {
        return parentProzess;
    }

    /**
     * @return
     */
    public List<Kante> getKommProzessKanten() {
        if (kommProzessKanten == null) {
            return new ArrayList<Kante>(1);
        }
        return kommProzessKanten;
    }

    /**
     * @return
     */
    public List<ModelElement> getKommProzessSchnittstellen() {
        if (kommProzessSchnittstellen == null) {
            return new ArrayList<ModelElement>(1);
        }
        return kommProzessSchnittstellen;
    }

    /**
     * @return
     */
    public List<ModelElement> getStartAufgabeKonfBausteine() {
        if (startAufgabeKonf == null) {
            return new ArrayList<ModelElement>(1);
        }
        List<ModelElement> al = startAufgabeKonf.getConnectedElements(Anwendungsbaustein.class);
        return al;
    }

    /**
     * @return
     */
    public List<ModelElement> getEndAufgabeKonfBausteine() {
        if (endAufgabeKonf == null) {
            return new ArrayList<ModelElement>(1);
        }
        List<ModelElement> al = endAufgabeKonf.getConnectedElements(Anwendungsbaustein.class);
        return al;
    }

    /**
     * @return
     */
    public int getKommProzessLength() {
        return kommProzessLength;
    }

    /**
     * @param i
     */
    public void setKommProzessLength(final int i) {
        kommProzessLength = i;
    }

    /**
     * @return
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * @return
     */
    public int getEndPosition() {
        return endPosition;
    }

    /**
     * @return
     */
    public AufOrgKombination getStartAufOrgKombination() {
        if (startAufgabeKonf == null) {
            return null;
        }
        return (AufOrgKombination) startAufgabeKonf.getConnectedElements(AufOrgKombination.class).get(0);
    }

    public AufOrgKombination getEndAufOrgKombination() {
        if (endAufgabeKonf == null) {
            return null;
        }
        return (AufOrgKombination) endAufgabeKonf.getConnectedElements(AufOrgKombination.class).get(0);
    }

    /**
     * @return Returns the mediumBreaks.
     */
    public int getMediumBreaks() {
        return mediumBreaks;
    }

    /**
     * @param i
     */
    public void setMediumBreaks(final int i) {
        mediumBreaks = i;
    }
}
