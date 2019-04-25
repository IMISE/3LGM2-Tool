package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.util.BrowseUtils;

/**
 * Action, die eine URL öffnet. Dabei wird erwartet dass für den übergebenen Identifier ein Resourceneintrag existiert der die Form hat:
 * "AnzeigeNameDerAction @URL:Link". Der Link darf im Moment nur ein Weblink sein. Über diese Action könnte man aber auch File-Links öffnen, wenn man
 * sie entsprechend anpasst -> später.
 *
 * @author AXS (25 Apr 2019)
 */
public class OpenUrlAction extends StaticAction {

    /**
     * Enthält der für diesen Action-Identifier geladene Resource-String diesen Delimiter, dann wird alles davor als Anzeige-Name der Action
     * interpretiert und alles danach als die URL, die beim Ausführen der Action geöffnet werden soll
     */
    public static final String RESOURCE_NAME_AND_URL_DELIMITER = "@URL:";

    private final String url;

    /**
     * @param identifier
     */
    public OpenUrlAction(final Object identifier) {
        super(identifier, null, ""); //der Text der Action wird in super nicht gesetzt, weil ein leerer String und nicht null übergeben wurde
        String resKey = getActionCommand();
        String actionNameAndLink = Tool3lgmConstants.getResStringWithoutError(resKey);
        int urlDelimiterIndex = actionNameAndLink.toUpperCase().indexOf(RESOURCE_NAME_AND_URL_DELIMITER);
        if (urlDelimiterIndex > -1) {
            setText(actionNameAndLink.substring(0, urlDelimiterIndex).trim() + PPP);
            url = actionNameAndLink.substring(urlDelimiterIndex + RESOURCE_NAME_AND_URL_DELIMITER.length()).trim();
        } else {
            setText(actionNameAndLink + PPP);
            url = "";
        }
    }

    @Override
    protected void actionPerformed() {
        BrowseUtils.browse(url);
    }

}
