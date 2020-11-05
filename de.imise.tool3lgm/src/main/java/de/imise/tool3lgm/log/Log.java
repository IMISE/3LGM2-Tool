/*
 * Created on 11.05.2004
 */
package de.imise.tool3lgm.log;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

/**
 * Klasse zum Loggen von Fehlermeldungen. Die Initialisierung erfolgt durch das
 * Skript 'Logger.ini' im tool3lgm Pfad.
 *
 * @author Sebastian Weber
 */

import java.awt.Component;
import java.net.URL;

import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.imise.tool3lgm.Static;

public class Log {

    /** der verwendete Logger */
    static Logger logger;

    /**
     * eine Component, die als Parent angenommen werden soll, wenn Fehler in
     * einer OptionPane angezeigt werden
     */
    private static Component parentComponent;

    /** Fehlerlevel DEBUG */
    public final static Level DEBUG = Level.DEBUG;

    /** Fehlerlevel INFO */
    public final static Level INFO = Level.INFO;

    /** Fehlerlevel WARN */
    public final static Level WARN = Level.WARN;

    /** Fehlerlevel ERROR */
    public final static Level ERROR = Level.ERROR;

    /** Fehlerlevel FATAL */
    public final static Level FATAL = Level.FATAL;

    public static boolean showErrorDialog = false;

    /**
     * Gibt den Logger zurück. Initialisiert den Logger, falls dieser noch nicht
     * initialisiert ist.
     *
     * @return der Logger den diese Klasse verwendet.
     */
    public static Logger getLogger() {
        if (logger == null) {
            try {
                // logger namens 3lgm erzeugen
                logger = Logger.getLogger("3lgm");
                // geladenen Logger mit Hilfe der Datei Logger.ini initialisieren
                URL url = Log.class.getResource("Logger.ini");
                PropertyConfigurator.configure(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return logger;
    }

    /**
     * Logt eine Meldung in die Log-Datei.
     *
     * @param level Level der Fehlermeldung (siehe Fehlerlevel)
     * @param meldung die Meldung, welche geloggt werden soll
     */
    public static void log(final Level level, final String meldung) {

        log(level, meldung, null);

        //erweitert um was zu loggen
        logger.log(level, meldung);

    }

    /**
     * Logt eine Meldung und die zugehörige Throwable in die Log-Datei.
     *
     * @param level Level der Fehlermeldung (siehe Fehlerlevel)
     * @param meldung die Meldung, welche geloggt werden soll
     * @param t die Throwable, welche geloggt werden soll
     */
    public static void log(final Level level, final String meldung, final Throwable t) {
        if (t != null) {
            t.printStackTrace();
            //if (level == DEBUG) getLogger().debug(meldung, t);
            //if (level == INFO) getLogger().info(meldung, t);
            //if (level == WARN) getLogger().warn(meldung, t);
            //if (level == ERROR) getLogger().error(meldung, t);
            //if (level == FATAL) getLogger().fatal(meldung, t);
        }
    }

    /**
     * Logt eine Meldung und zeigt diese in einer JOptionPane an.
     *
     * @param level Level der Fehlermeldung (siehe Fehlerlevel)
     * @param meldung die Meldung, welche geloggt und angezeigt werden soll
     */
    public static void show(final Level level, final String meldung) {
        show(level, meldung, null);
    }

    /**
     * Logt eine Meldung und zeigt diese in einer JOptionPane an.
     *
     * @param level Level der Fehlermeldung (siehe Fehlerlevel)
     * @param t die Throwable, welche geloggt werden soll
     */
    public static void show(final Level level, final Throwable t) {
        show(level, null, t);
    }

    /**
     * Logt eine Meldung und zeigt diese in einer JOptionPane an. Zusätzlich zur
     * Meldung wird die Throwable in die Log-Datei geschrieben.
     *
     * @param Level der Fehlermeldung (siehe Fehlerlevel)
     * @param meldung die Meldung, welche geloggt und angezeigt werden soll
     * @param t die Throwable, welche geloggt werden soll
     */
    public static void show(final Level level, final String meldung, final Throwable t) {

        //if (getLogger().isEnabledFor(level)) {

        String titel = "";
        int typ = JOptionPane.INFORMATION_MESSAGE;
        if (level == DEBUG || level == INFO) {
            log(level, meldung, t);
            titel = getResString("information");
            typ = JOptionPane.INFORMATION_MESSAGE;
        }
        if (level == WARN) {
            log(level, meldung, t);
            titel = getResString("warnung");
            typ = JOptionPane.WARNING_MESSAGE;
        }
        if (level == ERROR || level == FATAL) {
            log(level, meldung, t);
            titel = getResString("fehler");
            typ = JOptionPane.ERROR_MESSAGE;
        }
        if (showErrorDialog) {
            Static.showErrorOutputDialog(meldung, t);
        } else {
            JOptionPane.showConfirmDialog(getParentComponent(), meldung, titel, JOptionPane.DEFAULT_OPTION, typ);
        }
        //}
    }

    /**
     * Setzt die Component, welche beim anzeigen von Fehlermeldungen durch
     * JOptionPane als Parent verwendet werden soll.
     *
     * @param c eine Component als Parent für JOptionPane
     */
    public static void setParentComponent(final Component c) {
        parentComponent = c;
    }

    /**
     * Gibt die Component zurück, welche bei der Anzeige von Fehlermeldungen als
     * Parent verwendet werden soll.
     *
     * @return Parent Component für die Anzeige von Fehlermeldungen
     */
    public static Component getParentComponent() {
        return parentComponent;
    }
}
