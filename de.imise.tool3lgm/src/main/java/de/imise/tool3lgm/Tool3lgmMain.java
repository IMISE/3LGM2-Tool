package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.File;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JOptionPane;

import de.imise.tool3lgm.graphtools.dialog.RMIErrorPanel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.gui.ToolSplashScreen;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.metamodel.tlgm_service.TLGMServiceMetaModel;
import de.imise.tool3lgm.rmi.Tool3lgmServer;
import de.imise.tool3lgm.rmi.Tool3lgmServerImpl;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.IntProperty;

public class Tool3lgmMain {

    //public static final Class<? extends MetaModel> metaModelClass = TLGMOriginalMetaModel.class;
    public static final Class<? extends MetaModel> metaModelClass = TLGMServiceMetaModel.class;

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

        //In den <code>SplashScreen</code> die lokalisierten Informationen schreiben
        ToolSplashScreen.update();

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
        activateRMI(args, visible);
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
            int rmiPort = UserProperties.get(IntProperty.PROPERTY_INT_RMI_PORT);
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
                            UserProperties.set(IntProperty.PROPERTY_INT_RMI_PORT, regPort);
                            JOptionPane.showMessageDialog(Static.tool, getResString("rmiNewRegPortIs") + " " + regPort);
                        }

                    } catch (RemoteException e) {
                        // e.printStackTrace();
                        // Sollte der RMI-Server nicht auf dem aktuellen eingestellten regPort lauschen können:
                        // Wenn ein Fehler aufgetreten ist und nicht schon nach einem neuen Port gesucht wird, wird der RMI-FehlerDialog angezeigt.
                        RMIErrorPanel rmip = new RMIErrorPanel();

                        if (showErrorDialog) {
                            if (JOptionPane.showOptionDialog(Static.tool, rmip, getResString("rmiError"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
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
                                    UserProperties.set(IntProperty.PROPERTY_INT_RMI_PORT, regPort);
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

}
