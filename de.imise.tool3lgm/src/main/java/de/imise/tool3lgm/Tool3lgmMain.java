package de.imise.tool3lgm;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_RMI_PORT;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.DebugGraphics;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import de.imise.tool3lgm.graphtools.dialog.RMIErrorPanel;
import de.imise.tool3lgm.gui.ToolSplashScreen;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.rmi.Tool3lgmServer;
import de.imise.tool3lgm.rmi.Tool3lgmServerImpl;
import de.imise.tool3lgm.userproperties.UserProperties;

public class Tool3lgmMain {

    /**
     * Debug- Optionen fuer Swing-Komponenten; muss fuer Komponente mit setDebugGraphicsOption(int) gesetzt werden
     */
    private static int debugGraphicsOption = DebugGraphics.NONE_OPTION;

    /**
     * Main-Routine
     *
     * @param args
     *            <table border="1">
     *            <th>Argument-Options</th>
     *            <th>Usage</th>
     *            </tr>
     *            <td>Visibility of the Tool</td>
     *            <td>
     *            <li><tt>"visible true"</tt>: Tool is visible
     *            <li><tt>"visible false</tt>: Tool is not visible</td>
     *            </tr>
     *            <td>Relative of the Tool</td>
     *            <td>
     *            <li><tt>"visible true"</tt>: Tool is visible
     *            </table>
     */
    public static void main(final String args[]) {

        //UserProperties initialisieren, damit die richige Locale gesetzt ist
        UserProperties.init();

        //In den <code>SplashScreen</code> die lokalisierten Informationen schreiben
        ToolSplashScreen.update();

        setUIDefaults();

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

        setDebugGraphicsOption(false, false, false, 0);

        setLookAndFeel();

        activateRMI(args, visible);
    }

    private static final void setUIDefaults() {
        /* table of defaults for Swing components */
        UIDefaults defaults = UIManager.getDefaults();
        defaults.put("FileChooser.openButtonText", getResString("open"));
        defaults.put("FileChooser.cancelButtonText", getResString("cancel"));
        defaults.put("FileChooser.filesOfTypeLabelText", getResString("filesOfTypeLabelText"));
        defaults.put("FileChooser.fileNameLabelText", getResString("fileNameLabelText"));
        defaults.put("FileChooser.lookInLabelText", getResString("lookInLabelText"));

        defaults.put("ColorChooser.cancelText", getResString("cancel"));
        defaults.put("ColorChooser.sampleText", getResString("sampleText"));
        defaults.put("ColorChooser.rgbGreenText", getResString("green"));
        defaults.put("ColorChooser.previewText", getResString("previewText"));
        defaults.put("ColorChooser.rgbRedText", getResString("red"));
        defaults.put("ColorChooser.resetText", getResString("resetText"));
        defaults.put("ColorChooser.rgbBlueText", getResString("blue"));
        defaults.put("ColorChooser.swatchesNameText", getResString("swatchesNameText"));
        defaults.put("ColorChooser.swatchesRecentText", getResString("swatchesRecentText"));

        defaults.put("OptionPane.okButtonText", getResString("ok"));
        defaults.put("OptionPane.cancelButtonText", getResString("cancel"));
        defaults.put("OptionPane.noButtonText", getResString("no"));
        defaults.put("OptionPane.yesButtonText", getResString("yes"));
    }

    private static void setLookAndFeel() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            } else {
                //auf dem Mac gibt es mit dem Apple-Glas-Look-And-Feel totale Probleme, wenn man viele InternalFrames
                //verwendet, was unser Tool tut. Viele beginnt hier bereits bei ca. 5. Da beginnt es schlimm zu werden
                //und ab 10 friert das Tool immer mal für ne Minute ein.
                // javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception ex) {
            Log.show(Log.FATAL, getResString("LookAndFeelLadenException"), ex);
            System.exit(-1);
        }
    }

    /**
     * Wenn der Baukasten mit RMI gestartet werden soll, wird <code>activateRMI</code> ausgeführt.
     *
     * @param args
     * @return true, wenn der Baukasten erfolgreich den RMI starten konnte. Wenn Fehler aufgetreten sind, false.
     */
    private static boolean activateRMI(final String args[], final boolean visible) {

        // Der port, auf dem die RMI-Registry lauschen soll
        int regPort = Registry.REGISTRY_PORT;

        // Versuchen auf dem voreingestellten Port die Registry zu starten
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            // hole den vom User eingestellen RegistryPort
            int rmiPort = PROPERTY_INT_RMI_PORT.get();
            String regValue = String.valueOf(rmiPort);

            // hier wird geprüft, ob der Wert ungleich "" ist und mittels regulären Ausdruck, ob nur Ziffern enthalten sind.
            if (!regValue.equals("") && regValue.matches("\\d*")) {
                regPort = Integer.parseInt(regValue);
            }

            // Falls ein Fehler mit dem RegistryPort auftritt, wird hier der alte Portwert gesichert.
            int oldRegPort = regPort;

            // Wenn der RMI-Service erfolgreich gestartet werden konnte, wird <code>bound</code> true
            boolean bound = false;

            Remote remote = null;

            // Im Fehlerfall soll der Dialog angezeigt werden, den den user entscheiden lässt, wie das weitere Vorgehen sein soll.
            boolean showErrorDialog = true;

            // Es wird solange versucht den RMI-Service zu starten, bis ein freier Port gefunden wurde oder der User einen freien eingegeben hat.
            while (!bound) {

                try {
                    registry.list();
                } catch (Exception ex) {
                    try {
                        if (Registry.REGISTRY_PORT != regPort) {
                            registry = LocateRegistry.createRegistry(regPort);
                        } else {
                            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                        }
                    } catch (Exception e) {
                    }
                }
                try {
                    //TODO:############# auf jeden Fall wieder reinnehmen!!!
                    remote = Naming.lookup("//127.0.0.1:" + regPort + "/Tool3lgmServer");
                } catch (Exception innerEx) {
                }
                // Wenn der RMI-Service noch nicht läuft, wird hier weiter gemacht.
                if (remote == null || !(remote instanceof Tool3lgmServer)) {

                    // Wenn der Baukasten schon läuft, wird kein neuer instanziiert, sonst schon.
                    if (Static.tool == null) {
                        //Static.tool wird im Constructor gesetzt
                        new Tool3lgm(visible);
                    }

                    // Hier ist die kritische Stelle. Das Rebind schlägt fehl, wenn ein fremder Service den Port belegt, auf dem der Baukasten lauschen soll.
                    try {
                        // System.err.println("try port: "+regPort);
                        Naming.rebind("//127.0.0.1:" + regPort + "/Tool3lgmServer", new Tool3lgmServerImpl(Static.tool));

                        // Wenn der RMI-Server erfolgreich gestartet werden konnte, wird bound true.
                        // Wenn nicht, ist eine Exception geflogen und ist in die catch () gesprungen. bound wurde nicht true.
                        bound = true;

                        // Wenn der alte regPort ungleich dem neuen ist, wird der neue gespeichert und beim nächsten Programmstart als Standard-Port angewandt.
                        if (regPort != oldRegPort) {
                            PROPERTY_INT_RMI_PORT.set(regPort);
                            JOptionPane.showMessageDialog(getMainFrame(), getResString("rmiNewRegPortIs") + " " + regPort);
                        }

                    } catch (RemoteException e) {
                        // e.printStackTrace();
                        // Sollte der RMI-Server nicht auf dem aktuellen eingestellten regPort lauschen können:
                        // Wenn ein Fehler aufgetreten ist und nicht schon nach einem neuen Port gesucht wird, wird der RMI-FehlerDialog angezeigt.
                        RMIErrorPanel rmip = new RMIErrorPanel();

                        if (showErrorDialog) {
                            if (JOptionPane.showOptionDialog(getMainFrame(), rmip, getResString("rmiError"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                                if (rmip.isRmiAutoNextFreePortCheckBox()) {
                                    // Es wird ein neue Port bis 65500 gesucht, wenn bis dahin keiner frei ist, wird wieder beim standardPort begonnen
                                    if (regPort < 65500) {
                                        regPort++;
                                    } else {
                                        regPort = 1099;
                                    }
                                    showErrorDialog = false;
                                }
                                // Wenn der Benutzer einen Port eingegeben hat, wird er gesichtert und versucht darauf zu verbinden.
                                // Sollte en fehler auftreten, wir dder Errordialog wieder angezeigt.
                                else {
                                    regPort = Integer.parseInt(rmip.getRmiRegistryPortTextFieldValue());
                                    PROPERTY_INT_RMI_PORT.set(regPort);
                                }
                            }
                        }
                        // Wenn schon nach einem neuen Port gescuht wird, wird der ErrorDialog nicht nochmal angezeigt sondern gleich hier weiter gemacht:
                        else {
                            // Es wird ein neuer Port bis 65500 gesucht, wenn bis dahin keiner frei ist, wird wieder beim standardPort begonnen
                            if (regPort < 65500) {
                                regPort++;
                            } else {
                                regPort = 1099;
                            }

                        }
                    }
                    // e.printStackTrace();

                } else {
                    // Wenn schon eine Instanz des Tools läuft, wird hier hergesprungen.
                    // <code>bound</code> muss auf true gesetzt werden, dmit die Schleife beendet werden kann.
                    bound = true;
                }

            }

            remote = Naming.lookup("//127.0.0.1:" + regPort + "/Tool3lgmServer");
            if (remote == null) {
                Log.show(Log.FATAL, "RMI registration failed", new Exception("RMI registration failed"));
                return false;
            }
            // Wenn der RMI-Service erfolgreich auf dem regPort lauscht, wird hier weiter gemacht.
            // Der RMI-Server steht für RMI-Aufrufe bereit.

            Tool3lgmServer tool3lgmServer = (Tool3lgmServer) remote;
            if (args.length != 0) {
                String[] params = new String[args.length - 1];
                for (int i = 0; i < params.length; i++) {
                    params[i] = args[i + 1];
                }
                tool3lgmServer.processCommand(args[0], params);
            }
        } catch (Exception ex) {
            System.err.println(ex);
            Log.show(Log.FATAL, "RMI registration failed", ex);
            return false;
        }
        return true;
    }

    /**
     * gibt die Debug-Option fuer Swing-Komponenten zurueck
     *
     * @return int
     */
    public static int getDebugGraphicsOption() {
        System.out.println(debugGraphicsOption);
        return debugGraphicsOption;
    }

    /**
     * setzt die Debug-Optionen fuer Swing-Komponenten
     *
     * @param LOG
     *            boolean; Ausgabe der Ereignisse?
     * @param FLASH
     *            boolean; Aufleuchten der Aenderungen?
     * @param BUFFERED
     *            boolean; Anzeige des Buffers?
     * @param flashTime
     *            int; Dauer des FLASH
     */
    public static void setDebugGraphicsOption(final boolean LOG, final boolean FLASH, final boolean BUFFERED, final int flashTime) {
        debugGraphicsOption = 0;
        if (LOG) {
            debugGraphicsOption |= DebugGraphics.LOG_OPTION;
        }
        if (FLASH) {
            debugGraphicsOption |= DebugGraphics.FLASH_OPTION;
            DebugGraphics.setFlashTime(flashTime);
        }
        if (BUFFERED) {
            debugGraphicsOption |= DebugGraphics.BUFFERED_OPTION;
        }
    }

}
