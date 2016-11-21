/*
 * Created on 28.04.2004
 */
package de.imise.tool3lgm;

/**
 * Diese Klasse stellt den HelpBroker und das HelpSet für die gesamte
 * Anwendung zur Verfügung.
 * 
 * @author Sebp
 */

import java.awt.Component;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Bausteintyp;
import de.imise.tool3lgm.graphtools.elements.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.DBVerwaltungssystem;
import de.imise.tool3lgm.graphtools.elements.node.Datenbanksystem;
import de.imise.tool3lgm.graphtools.elements.node.Datensatztyp;
import de.imise.tool3lgm.graphtools.elements.node.Dokumentensammlung;
import de.imise.tool3lgm.graphtools.elements.node.Dokumententyp;
import de.imise.tool3lgm.graphtools.elements.node.Ereignistyp;
import de.imise.tool3lgm.graphtools.elements.node.EtntEtdtKombination;
import de.imise.tool3lgm.graphtools.elements.node.Kommunikationsprozess;
import de.imise.tool3lgm.graphtools.elements.node.Kommunikationsstandard;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Nachrichtentyp;
import de.imise.tool3lgm.graphtools.elements.node.Netzprotokoll;
import de.imise.tool3lgm.graphtools.elements.node.Netztyp;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;
import de.imise.tool3lgm.graphtools.elements.node.Organisationsplan;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.elements.node.Standort;
import de.imise.tool3lgm.graphtools.elements.node.Subnetz;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;

public class Help {

    /** intern verwendete Instanz dieser Klasse */
    private static Help help;
    /** der verwendete HelpBroker */
    private final HelpBroker mainHB;
    /** das verwendete HelpSet */
    private HelpSet mainHS = null;

    /**
     * Einige Methoden der kontextsensitiven Hilfe und des HelpBrokers
     * verlangen zur Anzeige der Hilfe eine Presäntationsform, die als
     * String übergeben wird.
     */
    private static final String DEFAULT_CSH_PRESENTATION = "javax.help.MainWindow";

    /**
     * Privater Konstruktor um diese Klasse als Singleton zu realisieren.
     * Eine Instanz der Klasse wird über die Methode getInstance() zurück gegeben.
     */
    private Help() {
        try {
            URL url = HelpSet.findHelpSet(Tool3lgm.class.getClassLoader(), "doc/help.hs", UserProperties.getLocale());
            mainHS = new HelpSet(Tool3lgm.class.getClassLoader(), url);
        } catch (Exception ee) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("keinHelpSet"), ee);
        }
        mainHB = mainHS.createHelpBroker();

    }

    /**
     * Gibt die Instanz dieser Klasse zurück.
     * 
     * @return eine Instanz der Klasse Help.
     */
    public static Help getHelp() {
        if (help == null) {
            help = new Help();
        }
        return help;
    }

    /**
     * Ermöglicht die Verwendung der durch <code>id</code> spezifizierten Hilfe innerhalb einer Komponente wie
     * z.B. innerhalb eines {@link JFrame}s.
     * 
     * @param comp
     *            Komponente innerhalb der die Hilfe aktiviert werden soll (z.B. {@link JFrame})
     * @param id
     *            ID der zu aktivierenden Hilfe
     * @see javax.help.HelpBroker#enableHelpKey(java.awt.Component, java.lang.String, javax.help.HelpSet)
     */
    public void enableHelpKey(final Component comp, final String id) {
        //mainHB.enableHelpKey(comp, id, mainHS);
        mainHB.enableHelpKey(comp, id, mainHS);
    }

    /**
     * Ermöglicht das Öffnen der durch <code>id</code> spezifizierten Hilfe durch Click auf die spezifizierte
     * Komponente.
     * 
     * @param comp
     *            Komponente die die Hilfe öffnet (z.B. {@link JMenuItem})
     * @param id
     *            ID der zu aktivierenden Hilfe
     * @see javax.help.HelpBroker#enableHelpOnButton(Component, String, HelpSet)
     */
    public void enableHelpOnButton(final Component comp, final String id) {
        mainHB.enableHelpOnButton(comp, id, mainHS);
    }

    /**
     * Gibt einen {@link ActionListener} zurück, der bei Anfügen die Direkthilfe für einer Swing-Komponente
     * aktiviert.
     * 
     * @see CSH.DisplayHelpAfterTracking
     */
    public CSH.DisplayHelpAfterTracking getDisplayHelpAfterTracking() {
        return new CSH.DisplayHelpAfterTracking(mainHS, DEFAULT_CSH_PRESENTATION, null);
    }

    /**
     * Um die kontextsensitive Hilfe für eine GUI-Komponente zu aktivieren,
     * muß diesem mittels CSH.setHelpIDString() eine HelpID zugewiesen werden.
     * (Die HelpID muß in der Datei map-Datei über eine mapID mit der URL der
     * entsprechenden Hilfe Seite verlinkt sein.)
     * 
     * @return die HelpID für das übergebene Object.
     */
    public String getHelpID(final Object o) {
        if (o == null) {
            return null;
        }

        // Wenn ein ElementPropertyDialog übergeben wurde, wird die Help der
        // Komponente zurück gegeben, die im ElementPropertyDialog angezeigt wird.
        if (o instanceof ElementPropertyDialog) {
            ModelElement me = ((ElementPropertyDialog) o).getModelElement();
            // SEBP anwendungsbausteinkonfiguration
            if (me instanceof Anwendungsbaustein) {
                return "dialoge_gemischt_ab";
            }
            if (me instanceof Anwendungsprogramm) {
                return "dialoge_anwendungsprogramm";
            }
            if (me instanceof Aufgabe) {
                return "dialoge_aufgabe";
            }
            // SEBP auforgkombination
            if (me instanceof Bausteinschnittstelle) {
                return "dialoge_baustein_s";
            }
            if (me instanceof Bausteintyp) {
                return "dialoge_bausteintyp";
            }
            if (me instanceof Benutzungsschnittstelle) {
                return "dialoge_benutzer_s";
            }
            if (me instanceof Datenbanksystem) {
                return "dialoge_dbs";
            }
            if (me instanceof Datensatztyp) {
                return "dialoge_datensatztyp";
            }
            // SEBP dbkonfiguration
            if (me instanceof DBVerwaltungssystem) {
                return "dialoge_db_system";
            }
            if (me instanceof Dokumentensammlung) {
                return "dialoge_dokumentensammlung";
            }
            if (me instanceof Dokumententyp) {
                return "dialoge_dokumententyp";
            }
            if (me instanceof Ereignistyp) {
                return "dialoge_ereignistyp";
            }
            if (me instanceof EtntEtdtKombination) {
                return "dialoge_etnt_kombi";
            }
            if (me instanceof Kommunikationsprozess) {
                return "dialoge_kom_prozess";
            }
            if (me instanceof Kommunikationsstandard) {
                return "dialoge_komm_standard";
            }
            if (me instanceof KonAnwendungsbaustein) {
                return "dialoge_papier_ab";
            }
            if (me instanceof Nachrichtentyp) {
                return "dialoge_nachrichtentyp";
            }
            if (me instanceof Netzprotokoll) {
                return "dialoge_netzprotokoll";
            }
            if (me instanceof Netztyp) {
                return "dialoge_netztyp";
            }
            if (me instanceof Objekttyp) {
                return "dialoge_objekttyp";
            }
            if (me instanceof Organisationseinheit) {
                return "dialoge_organisationseinheit";
            }
            if (me instanceof Organisationsplan) {
                return "dialoge_organisationsplan";
            }
            if (me instanceof PhysischerDVBaustein) {
                return "dialoge_dv_baustein";
            }
            // SEBP Prozess
            if (me instanceof RechAnwendungsbaustein) {
                return "dialoge_rechner_ab";
            }
            if (me instanceof Softwareprodukt) {
                return "dialoge_softwareprodukt";
            }
            if (me instanceof Standort) {
                return "dialoge_standort";
            }
            if (me instanceof Subnetz) {
                return "dialoge_subnetz";
            }
        }

        return null;
    }
}
