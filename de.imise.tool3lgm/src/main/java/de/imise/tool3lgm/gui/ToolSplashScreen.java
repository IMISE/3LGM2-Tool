package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.THIRD_PARTY_LICENSES_HTML_FILE;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.Tool3lgmConstants.getResStringWithoutError;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.util.BrowseUtils;

/**
 * @author hboehme
 * @created 02.10.2007
 */
public class ToolSplashScreen {

    /** Das Label in das beim Anzeigen des Dialoges das Bild gezeichnet wird. */
    private final JLabel imageLabel = new JLabel();

    /** Zeitraum der Entwicklung (braucht man nicht auslagern) */
    private static final String DEVELOPMENT_DURATION = "2003 - " + Math.max(2013, Calendar.getInstance().get(Calendar.YEAR));

    /** x-position of all strings displayed in the splash screen or info dialog */
    private static final int stringX = 20;

    /** The color of the title string "3LGM2-Tool" */
    private static final Color TITLE_COLOR = new Color(40, 127, 255);

    /** Normal color of all strings written to the spalsh screen and info dialog */
    private static final Color TEXT_COLOR = new Color(24, 76, 153);

    /** Color for links over which the mouse pointer is */
    private static final Color ACTIVE_LINK_COLOR = Color.ORANGE;

    /**
     * The links that are drawn on the image
     */
    private enum Link {

        /** Link to the 3LGM2 website */
        TOOL_WEBSITE(340),

        /** Link to the file with the thrid party licenses */
        THIRD_PARTY_LICENSES(370, THIRD_PARTY_LICENSES_HTML_FILE);

        /**
         * @param positionY
         */
        Link(final int positionY) {
            this(positionY, null);
        }

        /**
         * @param positionY
         * @param realLink
         */
        Link(final int positionY, final Object realLink) {
            this.positionY = positionY;
            this.realLink = realLink;
        }

        /** @return the y position of the drawed link string */
        public final int positionY;

        /** */
        public final Object realLink;

        /** The position and size of the link on the image */
        public Rectangle2D position;

        @Override
        public String toString() {
            return getResString(name());
        }

        /**
         * @return the object that is the link (e.g. a file, uri or toString()).
         */
        public Object getRealLink() {
            return realLink == null ? toString() : realLink;
        }

    }

    /**
     * In den <code>SplashScreen</code> die lokalisierten Informationen schreiben
     */
    public static final void update() {
        SplashScreen sc = SplashScreen.getSplashScreen();
        if (sc != null) {
            Graphics2D g = sc.createGraphics();
            ToolSplashScreen toolSplashScreen = new ToolSplashScreen();
            toolSplashScreen.update(g);
            sc.update();

        }
    }

    /**
     * Schreibt den Link an in das Infofenster.<br>
     * Wird normal false übergeben, wird der Link schwarz geschrieben, sonst blau.
     *
     * @param linkResKey
     * @param active
     * @return the position of the link
     */
    private final Rectangle2D printLink(final Link linkResKey, final boolean active) {
        return printLink(null, linkResKey, active);
    }

    /**
     * Schreibt den Link an in das Infofenster.<br>
     * Wird normal false übergeben, wird der Link schwarz geschrieben, sonst blau.
     *
     * @param g
     * @param linkResKey
     * @param active
     * @return the position of the link
     */
    private final Rectangle2D printLink(Graphics g, final Link link, final boolean active) {
        if (g == null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image image = icon.getImage();
            g = image.getGraphics();
        }
        Color c = active ? ACTIVE_LINK_COLOR : TEXT_COLOR;
        g.setColor(c);
        setFontSize(g, 13f);
        return drawLink(g, link);
    }

    /**
     * Fügt in das übergebene Graphics-Object den Infotext ein.
     *
     * @param g
     */
    private final void update(final Graphics g) {
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHints(qualityHints);
        g.setColor(TITLE_COLOR);
        setFontSize(g, 19f);
        drawString(g, "tool3lgm", 25);
        setFontSize(g, 16f);
        g.setColor(TEXT_COLOR);
        drawString(g, "fullToolName1", 50);
        drawString(g, "fullToolName2", 70);
        drawString(g, "version" + " " + Tool3lgmConstants.TOOL_VERSION, 100);
        if (!Strings.isNullOrEmpty(Tool3lgmConstants.Branch)) {
            drawString(g, "Branch " + Tool3lgmConstants.Branch, 120);
        }
        drawString(g, "instituteName1", 260);
        drawString(g, "instituteName2", 280);
        drawString(g, "instituteName3", 300);
        drawString(g, DEVELOPMENT_DURATION, 320);
        setFontSize(g, 13f);
        for (Link link : Link.values()) {
            link.position = printLink(g, link, false);
        }
    }

    /**
     * @return the full dialog
     */
    public final void getAboutDialog() {
        JDialog infoDialog = new JDialog(Static.getMainFrame(), getResString("splash_screen_title"), true);
        infoDialog.setSize(200, 100);
        infoDialog.setLocationRelativeTo(infoDialog.getOwner());

        ImageIcon imageIcon = Tool3lgmConstants.getImageIcon("splash.gif");

        infoDialog.add(imageLabel);
        infoDialog.pack();
        Image image = imageLabel.createImage(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        Graphics g = image.getGraphics();
        g.drawImage(imageIcon.getImage(), 0, 0, imageLabel);
        imageIcon.setImage(image);
        imageLabel.setIcon(imageIcon);
        update(g);

        addMouseAdapter(imageLabel);
        infoDialog.pack();
        infoDialog.setVisible(true);
    }

    /**
     * @param component
     */
    private void addMouseAdapter(final Component component) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                //print the links in active or normal color?
                for (Link link : Link.values()) {
                    boolean mouseOver = isMouseOver(e, link.position);
                    printLink(link, mouseOver);
                }
                update();
                imageLabel.revalidate();
                imageLabel.repaint();
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                //open links
                for (Link link : Link.values()) {
                    if (isMouseOver(e, link.position)) {
                        Object realLink = link.getRealLink();
                        BrowseUtils.browse(realLink);
                    }
                }
            }
        };

        component.addMouseListener(mouseAdapter);
        component.addMouseMotionListener(mouseAdapter);
    }

    /**
     * @param g
     * @param size
     */
    private static void setFontSize(final Graphics g, final float size) {
        Font font = g.getFont();
        font = font.deriveFont(Font.BOLD, size);
        g.setFont(font);
    }

    /**
     * @param g
     * @param reskeyOrString
     * @param x
     * @param y
     */
    private static String drawString(final Graphics g, final String resKeyOrString, final int y) {
        String string = getResStringWithoutError(resKeyOrString);
        g.drawString(string, stringX, y);
        return string;
    }

    /**
     * @param g
     * @param link
     * @return
     */
    private static Rectangle2D drawLink(final Graphics g, final Link link) {
        int y = link.positionY;
        String linkName = link.name();
        linkName = drawString(g, linkName, y);
        Rectangle2D linkPosition = getLinkPosition(g, linkName, stringX, y);
        return linkPosition;
    }

    /**
     * @param g
     * @param string
     * @return
     */
    private static Rectangle2D getStringBounds(final Graphics g, final String string) {
        FontMetrics fontMetrics = g.getFontMetrics();
        Rectangle2D stringBounds = fontMetrics.getStringBounds(string, g);
        return stringBounds;
    }

    /**
     * @param g
     * @param string
     * @param xOffset
     * @param yOffset
     * @return
     */
    private static Rectangle2D getLinkPosition(final Graphics g, final String string, final int xOffset, final int yOffset) {
        Rectangle2D r = getStringBounds(g, string);
        double x = r.getX() + xOffset;
        double y = r.getY() + yOffset;
        double w = r.getWidth();
        double h = r.getHeight();
        r.setRect(x, y, w, h);
        return r;
    }

    /**
     * @param e
     * @param rect
     * @return
     */
    private boolean isMouseOver(final MouseEvent e, final Rectangle2D rect) {
        int coord = e.getX();
        double leftCoord = rect.getMinX();
        if (coord > leftCoord) {
            double rightCoord = rect.getMaxX();
            if (coord < rightCoord) {
                coord = e.getY();
                leftCoord = rect.getMinY();
                if (coord > leftCoord) {
                    rightCoord = rect.getMaxY();
                    if (coord < rightCoord) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
