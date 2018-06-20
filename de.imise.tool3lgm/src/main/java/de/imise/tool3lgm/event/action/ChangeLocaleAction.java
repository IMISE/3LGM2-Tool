package de.imise.tool3lgm.event.action;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Alphabetical;
import de.imise.util.swing.event.ExtendedAction;

/**
 * Von {@link AbstractAction} abgeleitete Klasse für das Umschalten der Sprache im Programm.
 * <p>
 * Diese Klasse generiert automatisch über {@link #getAllActions()} die durch die Anzahl installierter Sprachen beschränkte Liste der möglichen
 * Instanzen dieser Klasse. Jede dieser Instanzen sorgt für das Umschalten auf die jeweils zugeordnete Sprache.<br>
 * Durch das Überschreiben von {@link #setSelected(boolean)} wird garantiert, dass nur genau eine Sprache aktiviert ist. Diese Sprache ist dann unter
 * {@link #selectedAction} abgespeichert und besitzt als einziges den Wert <code>true</code> für die {@link #SELECTED_KEY}-Property.
 * <p>
 * Das Umschalten erfolgt beim Aufruf von {@link #actionPerformed(ActionEvent)}.
 *
 * @see AbstractAction
 * @see StateProviderAction
 * @author fstephan, AXS
 */
public class ChangeLocaleAction extends ExtendedAction {

    /** Aktuell ausgewählte Action */
    private static ChangeLocaleAction selectedAction;

    /** Locale, die bei Ausführen dieser Aktion in den UserProperties eingestellt wird. */
    private final Locale locale;

    /**
     * Konstruktor
     *
     * @param locale
     *            Sprache, die durch {@link #actionPerformed(ActionEvent)} aktiviert wird
     */
    private ChangeLocaleAction(final Locale locale) {
        super(locale.getDisplayLanguage(locale));
        this.locale = locale;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (locale.getLanguage().equals(UserProperties.getLocale().getLanguage())) {
            setSelected(true);
            return;
        }

        // wenn die Locale wieder zurückgestellt wurde auf die Locale mit der der Baukasten
        // grade läuft)
        if (locale.getLanguage().equals(Tool3lgmConstants.START_LOCALE.getLanguage())) {
            UserProperties.setLocale(locale.getLanguage());
            setSelected(true);
            return;
        }
        UserProperties.setLocale(locale.getLanguage());
        showLocaleChangeNeedsRestartMessage();
        setSelected(true);
    }

    private void showLocaleChangeNeedsRestartMessage() {
        // Meldung mit neuer und alter Locale anzeigen
        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(locale);
        ResourceBundle newLocaleBundle = ResourceBundle.getBundle(Tool3lgmConstants.RESOURCE_BASE_NAME);
        Locale.setDefault(oldLocale);

        String info_oldLocale = getResString("language_changed_info");
        String info_newLocale = newLocaleBundle.getString("language_changed_info");
        String info = info_oldLocale + "\n\n" + info_newLocale;

        String info_title_oldLocale = getResString("restart_required");
        String info_title_newLocale = newLocaleBundle.getString("restart_required");
        String info_title = info_title_oldLocale + " / " + info_title_newLocale;

        JOptionPane.showMessageDialog(Static.getTool(), info, info_title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void setSelected(final boolean b) {
        selectedAction = this;
    }

    public boolean isSelected() {
        return selectedAction == this;
    }

    /** Gibt ein Array von {@link ChangeLocaleAction}s zu jeder installierten Sprache wieder */
    public static final ChangeLocaleAction[] getAllActions() {
        Locale[] locales = getInstalledLanguages();
        Alphabetical.sort(locales);
        ChangeLocaleAction[] allActions = new ChangeLocaleAction[locales.length];
        for (int i = 0; i < locales.length; i++) { // Wähle die Standard-Sprache aus den UserProperties
            allActions[i] = new ChangeLocaleAction(locales[i]);
            if (locales[i].getLanguage().equals(UserProperties.getLocale().getLanguage())) {
                allActions[i].setSelected(true);
            }
        }
        Alphabetical.sort(allActions);
        return allActions;
    }

    @Override
    public String toString() {
        return super.toString() + "[locale=" + locale + ", selected=" + isSelected() + "]";
    }

    /**
     * Liefert alle <code>Locale</code>s, für die Resourcen hinterlegt wurden.<br>
     * Diese werden durch Auslesen der Dateien "Tool3lgmResources_LANGUAGECODE.properties" aus dem resource-Package ermittelt.
     * Es wird davon ausgegangen, dass auf jeden Fall englische Ressourcen existieren, die in der Datei
     * "Tool3lgmResources.properties" hinterlegt sind.<br>
     *
     * @return alle Locales, für die Ressourcen existieren
     */
    public static final Locale[] getInstalledLanguages() {
        StringBuilder sb = new StringBuilder(Tool3lgmConstants.RESOURCE_BASE_NAME);
        // den Namen vervollständigen; die Zeichen an "XX" werden immer durch einen Ländercode ersetzt
        sb.append("_");
        String appendix = "XX";
        sb.append(appendix);
        // Positionen der Xe bestimmen
        int firstXIndex = sb.length() - appendix.length();
        // alle im System verfügbaren Locale-Sprachcodes holen (die sind immer 2 Zeichen lang)
        String[] allLocales = Locale.getISOLanguages();
        // Array für die gefundenen Ergebnislocales
        Locale[] allFoundLocales = new Locale[allLocales.length];
        // Anzahl der gefundenen Ergebnislocales
        int foundLocales = 0;
        // die erste immer auf Englisch setzen
        allFoundLocales[foundLocales++] = Locale.ENGLISH;

        Locale[] systemLocales = Locale.getAvailableLocales();

        // alle Locales durchprobieren und nach den Ressourcendateien suchen
        for (int i = 0; i < allLocales.length; i++) {
            sb.setCharAt(firstXIndex, allLocales[i].charAt(0));
            sb.setCharAt(firstXIndex + 1, allLocales[i].charAt(1));
            boolean found = false;
            try {
                ResourceBundle.getBundle(sb.toString());
                found = true;
            } catch (MissingResourceException e) {
            }
            // wenn ein ResoruceBundle für die aktuelle Sprache gefunden wurde
            if (found) {
                // Suche die Systemlocale zum gefundenen ResourceBundle
                for (int j = 0; j < systemLocales.length; j++) {
                    if (systemLocales[j].toString().equals(allLocales[i])) {
                        allFoundLocales[foundLocales++] = systemLocales[j];
                    }
                }
            }
        }
        Locale[] returnArray = new Locale[foundLocales];
        System.arraycopy(allFoundLocales, 0, returnArray, 0, foundLocales);
        return returnArray;
    }

}
