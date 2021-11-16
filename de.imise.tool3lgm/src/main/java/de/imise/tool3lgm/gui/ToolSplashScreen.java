package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.THIRD_PARTY_LICENSES_HTML_FILE;
import static de.imise.tool3lgm.Tool3lgmConstants.TOOL_VERSION_GIT_INFO;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.util.BasicSplashScreen;

/**
 * @author hboehme
 * @created 02.10.2007
 */
public class ToolSplashScreen extends BasicSplashScreen {

    /**
     * x-position of all strings displayed in the splash screen or info dialog
     */
    private static final int STRING_X = 20;

    /** The color of the title string "3LGM2-Tool" */
    private static final Color TITLE_COLOR = new Color(40, 127, 255);

    /**
     * Normal color of all strings written to the spalsh screen and info dialog
     */
    private static final Color TEXT_COLOR = new Color(24, 76, 153);

    /** Color for links over which the mouse pointer is */
    private static final Color ACTIVE_LINK_COLOR = Color.ORANGE;

    /**
     *
     */
    public ToolSplashScreen() {
        super(Tool3lgmConstants.RESOURCE_BUNDLE_SOURCE, TEXT_COLOR, ACTIVE_LINK_COLOR);
        addLink("TOOL_WEBSITE", STRING_X, 340);
        addLink("THIRD_PARTY_LICENSES", THIRD_PARTY_LICENSES_HTML_FILE, STRING_X, 370);
    }

    @Override
    protected final void update(final Graphics g) {
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHints(qualityHints);

        g.setColor(TITLE_COLOR);

        setBoldFontSize(g, 19);
        drawString(g, "tool3lgm", 25);
        setBoldFontSize(g, 16);

        g.setColor(TEXT_COLOR);

        drawString(g, "fullToolName1", 50);
        drawString(g, "fullToolName2", 70);

        setBoldFontSize(g, 15);
        int y = 100;
        String versionString = getResString("version") + ": " + TOOL_VERSION_GIT_INFO.version;
        drawString(g, versionString, y);
        y += 5;
        g.setColor(TITLE_COLOR);
        setBoldFontSize(g, 12);
        int textGap = 15;
        if (TOOL_VERSION_GIT_INFO.isDevelopmentBuild()) {
            String commitString = getResString("commit") + ": " + TOOL_VERSION_GIT_INFO.commit + " +" + TOOL_VERSION_GIT_INFO.commitCount;
            drawString(g, commitString, y += textGap);
            String branchString = getResString("branch") + ": " + TOOL_VERSION_GIT_INFO.branch;
            drawString(g, branchString, y += textGap);
        }

        //java.vendor=Oracle Corporation
        //java.vm.name=OpenJDK 64-Bit Server VM
        //java.version=15.0.2
        String javaString = getResString("java") + "";
        drawString(g, javaString, y += textGap);
        String javaVendorKey = getSystemPropertyString("java.vendor");
        drawString(g, javaVendorKey, y += textGap);
        String javaNameKey = getSystemPropertyString("java.vm.name");
        drawString(g, javaNameKey, y += textGap);
        String javaVersionKey = getSystemPropertyString("java.version");
        drawString(g, javaVersionKey, y += textGap);

        g.setColor(TEXT_COLOR);

        setBoldFontSize(g, 16);
        drawString(g, "instituteName1", 260);
        drawString(g, "instituteName2", 280);
        drawString(g, "instituteName3", 300);
        drawString(g, DEVELOPMENT_DURATION, 320);
        setBoldFontSize(g, 13);
        printLinks(g);
    }

    /**
     * @param key
     * @return
     */
    private String getSystemPropertyString(String key) {
        String propertyString = System.getProperty(key);
        if (Strings.isNullOrEmpty(propertyString)) {
            propertyString = "<" + getResString("unknown") + ">";
        }
        return "    " + getResString(key) + ": " + propertyString;
    }

    /**
     *
     */
    @Override
    public final void showAboutDialog() {
        Frame owner = Static.getMainFrame();
        String title = getResString("splash_screen_title");
        ImageIcon imageIcon = Tool3lgmConstants.getImageIcon("splash.gif");
        showAboutDialog(owner, title, imageIcon);
    }

    /**
     * @param g
     * @param reskeyOrString
     * @param y
     */
    protected String drawString(final Graphics g, final String resKeyOrString, final int y) {
        return drawString(g, resKeyOrString, STRING_X, y);
    }

}
