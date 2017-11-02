package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.twatd.licensing.TwatdLicenseLibrary;

import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.util.io.FileNameExtensionFilterAndFileFilter;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/** Lizenzverwaltung */
public class LicenseHandler {

    /**
     * COMMENTME
     */
    private final Properties licenseItems = new Properties();

    private static LicenseHandler licenseHandler = new LicenseHandler();

    /**
     * constructor
     *
     * @see java.lang.Object#Object()
     */
    private LicenseHandler() {
        licenseItems.put("g",
                "174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730");
        licenseItems.put("p",
                "178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239");
        licenseItems.put("q", "864205495604807476120572616017955259175325408501");
        licenseItems.put("y",
                //Key der bis Version 3.2 Beta benutzt wurde
                //"14300627371230228950169601901505470128925284120125584820959612014086342028994560433627164468322417150724888119951832411281916807062298007963159103404336774085891061191128715953217021296723250723500408671825275650987665439945908447793990133826618450011753407968612194841395971300289629133573910203377535518349");
                //Key ab Version 3.2
                "50130353173738973728122117307050982303325240535281983843066456949452369361589860322851251253694135519121623652865168517743221242969658663504859944420599589281036527851748542030299809821557170181016869364580930185375665744420760918986875024780360963498505654004927815814220720267469598144682653844939860413572");
    }

    /**
     * Überprüft, ob die übergeben Datei eine gültige Lizenzdatei ist.
     *
     * @param licenseFile
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws SignatureException
     * @throws IOException
     */
    private final String getLicenseError(final File licenseFile) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, IOException, SocketException {
        if (!TwatdLicenseLibrary.verifyLicenseFile(licenseFile, licenseItems)) {
            return getResString("invalidLicense");
        }
        Properties licenseItems = TwatdLicenseLibrary.readLicenseItems(licenseFile);
        if (!TwatdLicenseLibrary.checkExpiryDate(new Date(Long.parseLong((String) licenseItems.get("expiryDate"))))) {
            return getResString("licenseExpired");
        }
        String licenseHostName = licenseItems.get("hostName").toString();
        if (licenseHostName.length() > 0) {
            boolean correctHostName = true;
            try {
                correctHostName = TwatdLicenseLibrary.checkHostName(licenseHostName);
            } catch (UnknownHostException uhe) {
                correctHostName = false;
            }
            if (!correctHostName) {
                return getResString("wrongHost_1") + licenseHostName + getResString("wrongHost_2");
            }
        }
        return null;
    }

    public static final void importLicenseFile() {
        licenseHandler.importLicenseFileInternal();
    }

    /**
     * Importiert eine Lizenzdatei in das Anwendungsverzeichnis
     */
    private void importLicenseFileInternal() {
        // FileChooser, mit FileFilter für Lizenzdateien (Endung "lic")
        ExtendedFileChooser fileChooser = new ExtendedFileChooser(null);
        FileNameExtensionFilter licenseFileFilter = Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LIC);
        fileChooser.setFileSelectionMode(ExtendedFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        JFrame mainFrame = Static.getTool();
        if (fileChooser.showOpenDialog(mainFrame, false, licenseFileFilter) == ExtendedFileChooser.APPROVE_OPTION) {
            File licenseFile = fileChooser.getSelectedFile();
            // in diese Datei wird die ausgwählte Datei kopiert
            File importedLicenseFile = new File(Tool3lgmConstants.APPLICATION_DIR, licenseFile.getName());
            try {
                String licenseError = getLicenseError(licenseFile);
                if (licenseError != null) {
                    JOptionPane.showMessageDialog(mainFrame, licenseError + "\n\n" + getResString("licenseNotImported"), getResString("invalidLicense"), JOptionPane.WARNING_MESSAGE);
                } else {
                    // Lizenzdatei vom Quellort ins Installationsverzeichnis kopieren
                    // (nicht kopieren, wenn Quell und Ziel-Verzeichnis gleich sind. Das ist
                    // der Fall, wenn Benutzer den gepackten Lizenzkey nach dem Erscheinen der
                    // Abfrage ausgerechnet schon an den richtigen Zielort entpackt haben))
                    if (!licenseFile.equals(importedLicenseFile)) {
                        FileOutputStream fos = new FileOutputStream(importedLicenseFile);
                        FileInputStream fis = new FileInputStream(licenseFile);
                        byte[] bytes = new byte[fis.available()];
                        fis.read(bytes);
                        fos.write(bytes);
                        fis.close();
                        fos.close();
                    }
                    JOptionPane.showMessageDialog(mainFrame, getResString("licenseImported"), getResString("validLicense"), JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                //Hier kann es eigentlich nur noch eine Zugriffsverweigerung geben
                String error = getResString("licenseWriteAccesDenied1") + Tool3lgmConstants.APPLICATION_DIR.getAbsoluteFile() + getResString("licenseWriteAccesDenied2");
                JOptionPane.showMessageDialog(mainFrame, error, getResString("licenseWriteAccesDenied"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @return
     */
    public static final boolean checkLicenses() {
        return licenseHandler.checkLicensesInternal();
    }

    /**
     * @return
     */
    private final boolean checkLicensesInternal() {
        if (true) {
            return true;
        }
        try {
            //            if (TwatdLicenseLibrary.checkHostName("imise.uni-leipzig.de", "medizin.uni-leipzig.de", "AAA2011")) {
            //                return true;
            //            }
        } catch (Exception e) {
        }
        FileNameExtensionFilterAndFileFilter licenseFileFilter = new FileNameExtensionFilterAndFileFilter(Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LIC), false);
        File[] licenseFilesArray = Tool3lgmConstants.APPLICATION_DIR.listFiles(licenseFileFilter);
        List<File> licenseFiles = new ArrayList<>();
        for (File licenseFile : licenseFilesArray) {
            if (licenseFile.isFile()) {
                licenseFiles.add(licenseFile);
            }
        }
        JFrame mainFrame = Static.getTool();
        if (licenseFiles.size() > 0) {
            String[] errorMessages = new String[licenseFiles.size()];
            for (int i = 0; i < licenseFiles.size(); i++) {
                try {
                    if ((errorMessages[i] = getLicenseError(licenseFiles.get(i))) == null) {
                        return true;
                    }
                } catch (Exception ex) {
                    errorMessages[i] = ex.getMessage();
                }
            }
            String errorMessage = errorMessages.length > 1 ? getResString("multipleInvalidLicenses") + "\n\n" : "";
            for (int i = 0; i < errorMessages.length; i++) {
                errorMessage += licenseFiles.get(i).getName() + ": " + errorMessages[i] + "\n\n";
            }
            JOptionPane.showMessageDialog(mainFrame, errorMessage, getResString("invalidLicense"), JOptionPane.ERROR_MESSAGE);
        }
        int answer = JOptionPane.showConfirmDialog(mainFrame, getResString("lizenzfrage"), getResString("tool3lgm"), JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            importLicenseFile();
            return checkLicenses();
        }
        return false;
    }

}