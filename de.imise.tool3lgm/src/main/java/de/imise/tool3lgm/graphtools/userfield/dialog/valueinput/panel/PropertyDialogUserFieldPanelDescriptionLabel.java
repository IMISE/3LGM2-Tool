package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import de.imise.util.BrowseUtils;
import de.imise.util.Sys;
import de.imise.util.UrlInStringFinder;
import de.imise.util.UrlInStringFinder.UrlFinderResult;
import de.imise.util.htmlxml.HTMLConverter;
import de.imise.util.swing.component.MinWidthComponent;

/**
 * @author AXS (06.05.2021)
 */
public class PropertyDialogUserFieldPanelDescriptionLabel extends JEditorPane implements MinWidthComponent {

    /** Extracts a link from a text */
    private static final UrlInStringFinder urlFinder = new UrlInStringFinder();

    /**
     * @param description
     */
    public PropertyDialogUserFieldPanelDescriptionLabel(final String description) {
        super(new HTMLEditorKit().getContentType(), getText(description));

        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(final HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    BrowseUtils.browse(e);
                }
            }
        });
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setEditable(false);
    }

    /**
     * @param description
     * @return
     */
    private static final String getText(final String description) {

        String text = encodeAndInsertLinks(description);

        //Font font = UIManager.getFont("Label.font");
        String fontfamily = "Arial Narrow";//font.getFamily();

        //        text = "<html><body style=\"font-family:" + fontfamily + ";font-size:" + "1.0em" + ";\"><b><i>" + text + "</i></b>" + "</body></html>";
        text = "<html><body style=\"font-family:" + fontfamily + ";font-size:" + "1.0em" + ";\"><b>" + text + "</b>" + "</body></html>";

        return text;
    }

    /**
     * @param description
     * @return
     */
    private static String encodeAndInsertLinks(final String description) {
        List<UrlFinderResult> urlResults = urlFinder.getResults(description);
        if (urlResults.isEmpty()) {
            return HTMLConverter.encode(description);
        }
        int index = 0;
        StringBuilder sb = new StringBuilder();
        UrlFinderResult urlResult = null;
        for (int i = 0; i < urlResults.size(); i++) {
            urlResult = urlResults.get(i);
            if (index == urlResult.startIndexInOriginal) {
                sb.append("<a href=\"");
                String url = urlResult.url;
                if (!url.contains("://")) {
                    url = "https://" + url;
                }
                sb.append(url);
                sb.append("\">");
                sb.append(urlResult.url);
                sb.append("</a>");
                index += urlResult.url.length();
            } else {
                String text = description.substring(index, urlResult.startIndexInOriginal);
                index = urlResult.startIndexInOriginal;
                text = HTMLConverter.encode(text);
                sb.append(text);
                i--;
            }
        }
        if (urlResult.endIndexInOriginal < description.length()) {
            String text = description.substring(urlResult.endIndexInOriginal);
            text = HTMLConverter.encode(text);
            sb.append(text);
        }

        Sys.err1(description);
        Sys.err1(sb);
        System.err.println("###############################################################################################################################");
        return sb.toString();
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension size = super.getMaximumSize();
        size.width = getMinWidth();
        return size;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = getMinWidth();
        return size;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        Dimension size = super.getPreferredScrollableViewportSize();
        size.width = getMinWidth();
        return size;
    }

}
