package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.THIRD_PARTY_LICENSES_HTML_FILE;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
public class ToolSplashScreen implements MouseMotionListener, MouseListener {

    /**
     * Das Label in das beim Anzeigen des Dialoges das Bild gezeichnet wird.
     */
    private static JLabel imageLabel = new JLabel();

    private static JDialog infoDialog;

    /**
     *
     */
    private static Rectangle2D linkPositionWebsite = null;
    private static Rectangle2D linkPositionThirdPartyLicenses = null;

    /** Zeitraum der Entwicklung (braucht man nicht auslagern) */
    private static final String DEVELOPMENT_DURATION = "2003 - " + Math.max(2013, Calendar.getInstance().get(Calendar.YEAR));

    /**
     * Überwacht die übergebene Componente auf Mausbewegungen
     *
     * @param c
     */
    private ToolSplashScreen(final Component c) {
        super();
        c.addMouseMotionListener(this);
        c.addMouseListener(this);

    }

    /**
     * In den <code>SplashScreen</code> die lokalisierten Informationen schreiben
     */
    public static final void update() {
        SplashScreen sc = SplashScreen.getSplashScreen();
        if (sc != null) {
            Graphics2D g = sc.createGraphics();
            update(g);
            sc.update();

        }
    }

    /**
     * Schreibt den Link an in das Infofenster.<br>
     * Wird normal false übergeben, wird der Link schwarz geschrieben, sonst blau.
     *
     * @param g
     * @param normal
     */
    private static final void printLink(final boolean normal, final String link) {
        Graphics g = ((ImageIcon) imageLabel.getIcon()).getImage().getGraphics();

        g.setFont(g.getFont().deriveFont(Font.BOLD, 13f));
        if (normal) {
            g.setColor(new java.awt.Color(24, 76, 153));
        } else {
            g.setColor(Color.BLUE);

        }
        if (link == "toolWebSite") {
            g.drawString(getResString("toolWebSite"), 20, 340);
        }
        if (link == "thirdPartyLicenses") {
            g.drawString(getResString("thirdPartyLicenses"), 20, 370);
        }
    }

    /**
     * Fügt in das übergebene Graphics-Object den Infotext ein.
     *
     * @param g
     */
    private static final void update(final Graphics g) {
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHints(qualityHints);
        g.setColor(new java.awt.Color(40, 127, 255));

        g.setFont(g.getFont().deriveFont(Font.BOLD, 19f));
        g.drawString(getResString("tool3lgm"), 20, 25);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 16f));
        g.setColor(new java.awt.Color(24, 76, 153));
        g.drawString(getResString("fullToolName1"), 20, 50);
        g.drawString(getResString("fullToolName2"), 20, 70);
        g.drawString(getResString("version") + " " + Tool3lgmConstants.TOOL_VERSION, 20, 100);
        if (!Strings.isNullOrEmpty(Tool3lgmConstants.Branch)) {
            g.drawString("Branch " + Tool3lgmConstants.Branch, 20, 120);
        }
        g.drawString(getResString("instituteName1"), 20, 260);
        g.drawString(getResString("instituteName2"), 20, 280);
        g.drawString(getResString("instituteName3"), 20, 300);
        g.drawString(DEVELOPMENT_DURATION, 20, 320);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 13f));
        g.drawString(getResString("toolWebSite"), 20, 340);
    }

    /**
     * Liefert das Panel für den
     *
     * @return
     */
    public static final void getInfoDialog() {
        infoDialog = new JDialog(Static.getMainFrame(), getResString("splash_screen_title"), true);
        infoDialog.setSize(200, 100);
        infoDialog.setLocationRelativeTo(infoDialog.getOwner());

        ImageIcon ii = Tool3lgmConstants.getImageIcon("splash.gif");

        imageLabel = new JLabel();

        infoDialog.add(imageLabel);
        infoDialog.pack();
        Image image = imageLabel.createImage(ii.getIconWidth(), ii.getIconHeight());
        Graphics g = image.getGraphics();
        g.drawImage(ii.getImage(), 0, 0, imageLabel);
        update(g);
        g.drawString(getResString("thirdPartyLicenses"), 20, 370);
        Rectangle2D r = g.getFontMetrics().getStringBounds(getResString("toolWebSite"), g);
        r.setRect(r.getX() + 20, r.getY() + 340, r.getWidth(), r.getHeight());
        linkPositionWebsite = r;

        Rectangle2D r2 = g.getFontMetrics().getStringBounds(getResString("thirdPartyLicenses"), g);
        r2.setRect(r2.getX() + 20, r2.getY() + 370, r2.getWidth(), r2.getHeight());
        linkPositionThirdPartyLicenses = r2;

        new ToolSplashScreen(imageLabel);
        ii.setImage(image);
        imageLabel.setIcon(ii);
        infoDialog.pack();
        infoDialog.setVisible(true);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if (e.getX() >= linkPositionWebsite.getX() && e.getY() >= linkPositionWebsite.getY() && e.getX() <= linkPositionWebsite.getX() + linkPositionWebsite.getWidth() && e.getY() <= linkPositionWebsite.getY() + linkPositionWebsite.getHeight()) {
            printLink(false, "toolWebSite");
        } else {
            printLink(true, "toolWebSite");
        }
        if (e.getX() >= linkPositionThirdPartyLicenses.getX() && e.getY() >= linkPositionThirdPartyLicenses.getY() && e.getX() <= linkPositionThirdPartyLicenses.getX() + linkPositionThirdPartyLicenses.getWidth()
                && e.getY() <= linkPositionThirdPartyLicenses.getY() + linkPositionThirdPartyLicenses.getHeight()) {
            printLink(false, "thirdPartyLicenses");
        } else {
            printLink(true, "thirdPartyLicenses");
        }
        update();
        imageLabel.revalidate();
        imageLabel.repaint();
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getX() >= linkPositionWebsite.getX() && e.getY() >= linkPositionWebsite.getY() && e.getX() <= linkPositionWebsite.getX() + linkPositionWebsite.getWidth() && e.getY() <= linkPositionWebsite.getY() + linkPositionWebsite.getHeight()) {
            BrowseUtils.browse(getResString("toolWebSite"));
        }
        if (e.getX() >= linkPositionThirdPartyLicenses.getX() && e.getY() >= linkPositionThirdPartyLicenses.getY() && e.getX() <= linkPositionThirdPartyLicenses.getX() + linkPositionThirdPartyLicenses.getWidth()
                && e.getY() <= linkPositionThirdPartyLicenses.getY() + linkPositionThirdPartyLicenses.getHeight()) {
            BrowseUtils.browse(THIRD_PARTY_LICENSES_HTML_FILE);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }
}
