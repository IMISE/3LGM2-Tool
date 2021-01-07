package de.imise.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import de.imise.util.resource.SimpleResourceBundleSource;

/**
 * Extracted general splash screen and about dialog functions from the
 * ToolSplashScreen of hboehme.
 *
 * @author AXS (05.10.2020)
 */
public abstract class BasicSplashScreen {

    /** The label into which the image is drawn when the dialog is displayed. */
    protected final JLabel imageLabel = new JLabel();

    /** Period of development */
    protected static final String DEVELOPMENT_DURATION = "2003 - " + Math.max(2020, Calendar.getInstance().get(Calendar.YEAR)); //2020 is currently the minimum, if the systems date is incorrect

    /** The source of resource strings */
    private final SimpleResourceBundleSource resourceBundleSource;

    /** Collection of all links that should be written on the image */
    protected final Collection<SplashScreenLink> links = new ArrayList<>();

    /** Color of inactive links */
    protected final Color normalLinkColor;

    /** Color of active links */
    protected final Color activeLinkColor;

    /**
     * Data object to store all information about a link
     */
    protected class SplashScreenLink {

        /**
         * This object identifies the resource key string. It is the toString()
         * result of the object or if it is an {@link Enum} then the name()
         * result of the Enum.
         */
        private final Object resKey;

        /**
         * An Object that identifies the link. If it is <code>null</code> then
         * the resource string given by the resKey will be interpreted as the
         * link. This object can be an {@link URI} or {@link File} or its
         * toString() method can return a path or uri.
         */
        private final Object realLink;

        /**
         * X-coordinate at which the link on the image should be drawn
         */
        private final int x;

        /**
         * Y-coordinate at which the link on the image should be drawn
         */
        private final int y;

        /**
         * The position an the size of the link on the imgage. The rectangles x
         * and y coordinates should be the same as the x and y of this. It's
         * redundant because
         * {@link FontMetrics#getStringBounds(String, Graphics)} returns a
         * Rectangle2D.
         */
        private Rectangle2D sizeAndPosition;

        /**
         * The font the link was first drawn with. You have to remember it to
         * write with the same font for every further character.
         */
        private Font font;

        /**
         * @param resKey
         * @param x
         * @param y
         */
        public SplashScreenLink(final Object resKey, final int x, final int y) {
            this(resKey, null, x, y);
        }

        /**
         * @param resKey
         * @param realLink
         * @param x
         * @param y
         */
        public SplashScreenLink(final Object resKey, final Object realLink, final int x, final int y) {
            this.resKey = resKey;
            this.realLink = realLink;
            this.x = x;
            this.y = y;
        }

    }

    /**
     * @param resourceBundleSource
     * @param normalLinkColor
     * @param activeLinkColor
     */
    protected BasicSplashScreen(final SimpleResourceBundleSource resourceBundleSource, final Color normalLinkColor, final Color activeLinkColor) {
        this.resourceBundleSource = resourceBundleSource;
        this.normalLinkColor = normalLinkColor;
        this.activeLinkColor = activeLinkColor;
    }

    /**
     * @param resourceKey
     * @param x
     * @param y
     */
    protected void addLink(final Object resourceKey, final int x, final int y) {
        addLink(resourceKey, null, x, y);
    }

    /**
     * @param resourceKey
     * @param realLink
     * @param x
     * @param y
     */
    protected void addLink(final Object resourceKey, final Object realLink, final int x, final int y) {
        SplashScreenLink splashScreenLink = new SplashScreenLink(resourceKey, realLink, x, y);
        links.add(splashScreenLink);
    }

    /**
     * @param g
     * @param size
     */
    protected static void setBoldFontSize(final Graphics g, final int size) {
        Font font = g.getFont();
        font = font.deriveFont(Font.BOLD, size);
        g.setFont(font);
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
    private static Rectangle2D getStringPosition(final Graphics g, final String string, final int xOffset, final int yOffset) {
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
    private static boolean isMouseOver(final MouseEvent e, final Rectangle2D rect) {
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

    /**
     * @param g
     * @param reskeyOrString
     * @param x
     * @param y
     */
    protected String drawString(final Graphics g, final Object resKeyOrString, final int x, final int y) {
        String string = resourceBundleSource.getResStringWithoutError(resKeyOrString);
        g.drawString(string, x, y);
        return string;
    }

    /**
     * @param g
     * @param link
     * @return
     */
    private void drawLink(final Graphics g, final SplashScreenLink link) {
        String distplayedString = resourceBundleSource.getResStringWithoutError(link.resKey);
        if (link.font != null) {
            g.setFont(link.font);
        } else {
            link.font = g.getFont();
        }
        distplayedString = drawString(g, distplayedString, link.x, link.y);
        link.sizeAndPosition = getStringPosition(g, distplayedString, link.x, link.y);
    }

    /**
     * Paints all links to g in the normal link color (not active).
     *
     * @param g
     */
    protected void printLinks(final Graphics g) {
        for (SplashScreenLink link : links) {
            printLink(g, link, normalLinkColor);
        }
    }

    /**
     * Writes the link in the given color to the graphics object of the
     * imageLabel.
     *
     * @param link
     * @param color
     */
    protected final void printLink(final SplashScreenLink link, final Color color) {
        printLink(null, link, color);
    }

    /**
     * Writes the link in the given color to the graphics object. If the
     * graphics object is <code>null</code>, then it will be written to the
     * graphics object of the imageLabel.
     *
     * @param g
     * @param link
     * @param color
     */
    protected final void printLink(Graphics g, final SplashScreenLink link, final Color color) {
        if (g == null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image image = icon.getImage();
            g = image.getGraphics();
        }
        g.setColor(color);
        drawLink(g, link);
    }

    /**
     * @param component
     */
    private void addMouseAdapter(final Component component) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                //print the links in active or normal color?
                for (SplashScreenLink link : links) {
                    boolean mouseOver = isMouseOver(e, link.sizeAndPosition);
                    Color color = mouseOver ? activeLinkColor : normalLinkColor;
                    printLink(link, color);
                }
                imageLabel.revalidate();
                imageLabel.repaint();
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                //open links
                for (SplashScreenLink link : links) {
                    if (isMouseOver(e, link.sizeAndPosition)) {
                        BrowseUtils.browse(link.realLink);
                    }
                }
            }
        };

        component.addMouseListener(mouseAdapter);
        component.addMouseMotionListener(mouseAdapter);
    }

    /**
     * Write the localized information to the {@link SplashScreen}
     */
    public final void updateSplashScreen() {
        SplashScreen sc = SplashScreen.getSplashScreen();
        if (sc != null) {
            Graphics2D g = sc.createGraphics();
            update(g);
            sc.update();
        }
    }

    /**
     * Paints all information to the given Graphics object. This graphics object
     * is the underlying image.
     *
     * @param g
     */
    protected abstract void update(Graphics g);

    /**
     * @param owner
     * @param title
     * @param imageIcon
     */
    protected final void showAboutDialog(final Frame owner, final String title, final ImageIcon imageIcon) {
        JDialog infoDialog = new JDialog(owner, title, true);
        infoDialog.add(imageLabel);
        infoDialog.pack(); //without this first pack() the graphics object in not set

        Image image = imageLabel.createImage(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        Graphics g = image.getGraphics();
        g.drawImage(imageIcon.getImage(), 0, 0, imageLabel);
        imageIcon.setImage(image);
        imageLabel.setIcon(imageIcon);
        update(g);
        addMouseAdapter(imageLabel);
        infoDialog.pack();
        infoDialog.setLocationRelativeTo(owner);
        infoDialog.setVisible(true);
    }

    /**
     * Creates and shows the about dialog
     */
    public abstract void showAboutDialog();

}
