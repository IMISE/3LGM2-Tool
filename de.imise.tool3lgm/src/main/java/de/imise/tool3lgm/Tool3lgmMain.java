package de.imise.tool3lgm;

import java.io.File;

import de.imise.tool3lgm.log.Log;

public class Tool3lgmMain {

    /**
     * Main-Routine
     * 
     * @param args
     *            <table border="1">
     *            <th>Argument-Options</th>
     *            <th>Usage</th></tr>
     *            <td>Visibility of the Tool</td>
     *            <td>
     *            <li><tt>"visible true"</tt>: Tool is visible
     *            <li><tt>"visible false</tt>: Tool is not visible</td></tr>
     *            <td>Relative of the Tool</td>
     *            <td>
     *            <li><tt>"visible true"</tt>: Tool is visible
     *            </table>
     */
    public static void main(final String args[]) {

        //als allererstes müssen die statischen Felder der Tool3lgm-Klasse initialisert werden, damit
        //die Ressourcen gefunden werden
        Tool3lgm.init();

        // Erkennbare Argumente
        boolean visible = true;

        for (String arg : args) {
            //			String[] a = StringUtils.tokenize(arg, " ", false);
            String[] a = arg.split(" ");
            if (a.length != 2) {
                continue;
            }
            String paramterName = a[0];
            String paramterValue = a[1];
            if (paramterName.equalsIgnoreCase("visible")) {
                visible = Boolean.valueOf(paramterValue).booleanValue();
            }
        }

        Tool3lgmConstants.setClipboardPath(System.getProperty("user.home") + File.separator + ".3lgm_clipboard");
        Tool3lgmConstants.setDebugGraphicsOption(false, false, false, 0);

        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            } else {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception ex) {
            Log.show(Log.FATAL, Tool3lgmConstants.getErrString("LookAndFeelLadenException"), ex);
            System.exit(-1);
        }
        Tool3lgm.activateRMI(args, visible);
    }

}
