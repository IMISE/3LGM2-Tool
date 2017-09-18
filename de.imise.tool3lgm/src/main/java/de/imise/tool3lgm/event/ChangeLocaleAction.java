package de.imise.tool3lgm.event;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Alphabetical;

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
 * @author fstephan
 */
class ChangeLocaleAction extends StaticAction {

    /** Schlüssel für die {@link Locale}, die durch diese Action aktiviert wird */
    public static final String LOCALE_KEY = "LocaleKey";

    /** Aktuell ausgewählte Action */
    private static ChangeLocaleAction selectedAction;

    /** Gibt ein Array von {@link ChangeLocaleAction}s zu jeder installierten Sprache wieder */
    public static final ChangeLocaleAction[] getAllActions() {
        Locale[] locales = Tool3lgmConstants.getInstalledLanguages();
        Alphabetical.sort(locales);
        ChangeLocaleAction[] allActions = new ChangeLocaleAction[locales.length];
        for (int i = 0; i < locales.length; i++) { // Wähle die Standard-Sprache aus den
            // UserProperties
            allActions[i] = new ChangeLocaleAction(locales[i]);
            if (locales[i].getLanguage().equals(UserProperties.getLocale().getLanguage())) {
                allActions[i].setSelected(true);
            }
        }
        Alphabetical.sort(allActions);
        return allActions;
    }

    /** Locale, die bei Ausführen dieser Aktion in den UserProperties eingestellt wird. */
    private final Locale locale;

    /**
     * Konstruktor
     *
     * @param locale Sprache, die durch {@link #actionPerformed(ActionEvent)} aktiviert wird
     */
    private ChangeLocaleAction(final Locale locale) {
        super(ActionIdentifier.getIdentifierFor(locale));
        this.locale = locale;
        putValue(LOCALE_KEY, locale);
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
            UserProperties.setLocale(locale);
            setSelected(true);
            return;
        }

        UserProperties.setLocale(locale);
        // Meldung mit neuer und alter Locale anzeigen
        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(locale);
        ResourceBundle newLocaleBundle = ResourceBundle.getBundle(Tool3lgmConstants.RESOURCE_BASE_NAME);
        Locale.setDefault(oldLocale);

        String info_oldLocale = getResString("language_info");
        String info_newLocale = newLocaleBundle.getString("language_info");
        String info = info_oldLocale + "\n\n" + info_newLocale;

        String info_title_oldLocale = getResString("language_info_title");
        String info_title_newLocale = newLocaleBundle.getString("language_info_title");
        String info_title = info_title_oldLocale + " / " + info_title_newLocale;

        JOptionPane.showMessageDialog(getTool(), info, info_title, JOptionPane.INFORMATION_MESSAGE);

        setSelected(true);
    }

    @Override
    public void setSelected(final boolean b) {
        if (b && selectedAction != this) {
            if (selectedAction != null) {
                selectedAction.setSelected(false);
            }
            selectedAction = this;
        }
        super.setSelected(b);
    }

    @Override
    public String toString() {
        return super.toString() + "[locale=" + locale + ", selected=" + isSelected() + "]";
    }
}
