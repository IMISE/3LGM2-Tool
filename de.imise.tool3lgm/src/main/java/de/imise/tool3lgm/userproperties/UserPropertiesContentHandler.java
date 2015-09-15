/*
 * Created on 25.11.2003
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.userproperties;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.XMLCharacterCoder;
import de.imise.util.Locales;
import de.imise.util.io.FileHandler;

/**
 * @author Thomas Rudert, AXS
 *         Die Variablen sind auf protected Gesetzt, damit man einen neuen ContentHandler
 *         von dieser Klasse ableiten kann aber trotzdem noch Zugriff auf alle nötigen
 *         Werte hat. Ich denke, bei kleinen Änderungen (hinzukommen oder wegfallen einzelnener
 *         Felder im Dokument) muß man keinen ganz neuen ContentHandler schreiben sondern muß
 *         nur einen abgeleiteten von diesem bilden. Ich würde aber empfehlen von Zeit zu Zeit
 *         einen völlig neuen ContentHandler zu schreiben.
 *         erkennt Modell mit 3lgm2.dtd Version 1.0
 */
public class UserPropertiesContentHandler implements ContentHandler {

    /** String der in der characters Methode ausgelesen wird (Werte eines Tags) */
    private final StringBuilder elementValue = new StringBuilder();

    /**
	 * 
	 */
    protected UserPropertiesContentHandler() {
        super();
    }

    @Override
    public void setDocumentLocator(final Locator arg0) {

    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startPrefixMapping(final String arg0, final String arg1) throws SAXException {
    }

    @Override
    public void endPrefixMapping(final String arg0) throws SAXException {
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        elementValue.setLength(0);
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        //alle Werte braucht man als String ohne Leerzeichen am Angang und Ende
        String value = elementValue.toString().trim();

        if (equals(qName, "Tool3lgmUserInfoFile")) {

        } else if (equals(qName, "toolVersion")) {

        } else if (equals(qName, "user")) {

        } else if (equals(qName, "locale")) {
            UserProperties.setLocale(Locales.getSystemLanguageLocale(value));

        } else if (equals(qName, "renderingHints")) {
            try {
                UserProperties.setRenderingHints(Integer.parseInt(value));
            } catch (NumberFormatException exp) {
                UserProperties.setRenderingHints(137);
            }

        } else if (equals(qName, "showLinks")) {
            UserProperties.setShowLinks(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "searchParts")) {
            UserProperties.setSearchParts(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "searchParents")) {
            UserProperties.setSearchParents(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "moveSubelements")) {
            UserProperties.setMoveSubelements(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "enableSubmodelBrowser")) {
            UserProperties.setEnableSubmodelBrowser(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "showModelsInSeparateBrowser")) {
            UserProperties.setShowModelsInSeparateBrowser(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "showSubModelsInBrowserSideBySide")) {
            UserProperties.setShowSubModelsInBrowserSideBySide(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "showUserDefinedPropertiesInModelBrowser")) {
            UserProperties.setShowUserDefinedPropertiesInModelBrowser(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "showPartOfHierarchy")) {
            UserProperties.setShowPartOfHierarchy(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "paintEdgesOnlyForSelectedElements")) {
            UserProperties.setPaintEdgesOnlyForSelectedElements(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "usePropertyColors")) {
            UserProperties.setUsePropertyColors(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "useRaster")) {
            UserProperties.setUseRaster(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "showRaster")) {
            UserProperties.setShowRaster(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "rasterWidth")) {
            UserProperties.setRasterWidth(Integer.parseInt(value));

        } else if (equals(qName, "assignConfigurationColors")) {
            UserProperties.setAssignConfigurationColors(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "showToolTips")) {
            UserProperties.setShowToolTips(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "newSubmodelForAnalysis")) {
            UserProperties.setNewSubmodelForAnalysis(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "enableClassificationNumberCalculation")) {
            UserProperties.setEnableClassificationNumberCalculation(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "RMIRegistryPort")) {
            UserProperties.setRMIRegistryPort(value);

        } else if (equals(qName, "checkConsistency")) {
            UserProperties.setCheckConsistency(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "showRemoveWarning")) {
            UserProperties.setShowRemoveWarning(Boolean.valueOf(value).booleanValue());

        } else if (equals(qName, "usedFile")) {
            File f = new File(XMLCharacterCoder.decodeString(value));
            if (f.exists()) {
                UserProperties.addUsedFile(f);
            }

        } else if (equals(qName, "xslSearchDir")) {
            File f = new File(XMLCharacterCoder.decodeString(value));
            if (f.isDirectory()) {
                UserProperties.addXslSearchDir(f);
            }

        } else {
            //throw new SAXException("Unknown xml-tag: " + qName);
        }
    }

    /**
     * Vergleicht die 2 Strings auf Gleichheit ohne Beachtung der Groß- und Kleinschreibung.
     * 
     * @param s1
     * @param s2
     * @return
     */
    private static final boolean equals(final String s1, final String s2) {
        return s1.compareToIgnoreCase(s2) == 0;
    }

    @Override
    public void characters(final char[] arg0, final int arg1, final int arg2) throws SAXException {
        elementValue.append(String.valueOf(arg0, arg1, arg2));
    }

    @Override
    public void ignorableWhitespace(final char[] arg0, final int arg1, final int arg2) throws SAXException {
    }

    @Override
    public void processingInstruction(final String arg0, final String arg1) throws SAXException {
    }

    @Override
    public void skippedEntity(final String arg0) throws SAXException {
    }

    /////////////////////////////////////////////////////
    // Einlesen und Herausschreiben der UserProperties //
    /////////////////////////////////////////////////////
    /**
     * Ließt die benutzerspezifischen Informationen aus dem Benutzer-Home-Verzeichnis
     * oder die Defaultdatei aus den Ressourcen.
     */
    protected static void readUserInfo() {
        File userInfoFile = Tool3lgmConstants.USER_INFO_FILE;
        //wird true, wenn die Default-Benutzereinstellungen aus den Ressourcen geladen wurden
        boolean isDefault = false;
        if (!userInfoFile.canRead()) {
            FileHandler.copyFile(Tool3lgmConstants.DEFAULT_USER_INFO_FILE, userInfoFile);
            isDefault = true;
        }

        try {
            if (!UserPropertiesParser.isParseAbleFileVersion(userInfoFile)) {
                return;
            }

            UserPropertiesParser parser = new UserPropertiesParser(userInfoFile);
            parser.parseDocument();

        } catch (Exception exp) {
            //wenn die Datei nicht gelesen werden konnte und es sich nicht um die Standardeinstellungsdatei
            //handelt (dann hat irgendwer was in die Porperties-Datei des Benutzers geschrieben, was da nicht
            //reingehört -> Standarddatei laden)
            if (!isDefault) {
                userInfoFile.delete();
                readUserInfo();
            }
            //nicht loggen, da ToollgmConstants noch nicht da ist!
            //Log.show(Log.ERROR, "Exception while initilising user properties", exp);
            //exp.printStackTrace();
            return;
        }
    }

    /**
     * schreibt die benutzerspezifischen Informationen in das Benutzer-Home-Verzeichnis
     */
    protected static void writeUserInfo() {
        try {
            RandomAccessFile raf = new RandomAccessFile(Tool3lgmConstants.USER_INFO_FILE, "rw");
            raf.setLength(0);
            raf.writeBytes("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            raf.writeBytes("<Tool3lgmUserInfoFile>\n");
            raf.writeBytes("\t<toolVersion>" + Tool3lgmConstants.TOOL_VERSION + "</toolVersion>\n");
            raf.writeBytes("\t<user>" + XMLCharacterCoder.encodeString(System.getProperty("user.name")) + "</user>\n");
            raf.writeBytes("\t<locale>" + UserProperties.getLocale().getLanguage() + "</locale>\n");
            raf.writeBytes("\t<renderinghints>" + UserProperties.getRenderingHints() + "</renderinghints>\n");
            raf.writeBytes("\t<showLinks>" + UserProperties.isShowLinks() + "</showLinks>\n");
            raf.writeBytes("\t<searchParts>" + UserProperties.isSearchParts() + "</searchParts>\n");
            raf.writeBytes("\t<searchParents>" + UserProperties.isSearchParents() + "</searchParents>\n");
            raf.writeBytes("\t<moveSubelements>" + UserProperties.isMoveSubelements() + "</moveSubelements>\n");
            raf.writeBytes("\t<enableSubmodelBrowser>" + UserProperties.isEnableSubmodelBrowser() + "</enableSubmodelBrowser>\n");
            raf.writeBytes("\t<showModelsInSeparateBrowser>" + UserProperties.isShowModelsInSeparateBrowser() + "</showModelsInSeparateBrowser>\n");
            raf.writeBytes("\t<showSubModelsInBrowserSideBySide>" + UserProperties.isShowSubModelsInBrowserSideBySide() + "</showSubModelsInBrowserSideBySide>\n");
            raf.writeBytes("\t<showUserDefinedPropertiesInModelBrowser>" + UserProperties.isShowUserDefinedPropertiesInModelBrowser() + "</showUserDefinedPropertiesInModelBrowser>\n");
            raf.writeBytes("\t<showPartOfHierarchy>" + UserProperties.isShowPartOfHierarchy() + "</showPartOfHierarchy>\n");
            raf.writeBytes("\t<paintEdgesOnlyForSelectedElements>" + UserProperties.isPaintEdgesOnlyForSelectedElements() + "</paintEdgesOnlyForSelectedElements>\n");
            raf.writeBytes("\t<usePropertyColors>" + UserProperties.isUsePropertyColors() + "</usePropertyColors>\n");
            raf.writeBytes("\t<useRaster>" + UserProperties.isUseRaster() + "</useRaster>\n");
            raf.writeBytes("\t<showRaster>" + UserProperties.isShowRaster() + "</showRaster>\n");
            raf.writeBytes("\t<rasterWidth>" + UserProperties.getRasterWidth() + "</rasterWidth>\n");
            raf.writeBytes("\t<assignConfigurationColors>" + UserProperties.isAssignConfigurationColors() + "</assignConfigurationColors>\n");
            raf.writeBytes("\t<showToolTips>" + UserProperties.isShowToolTips() + "</showToolTips>\n");
            raf.writeBytes("\t<newSubmodelForAnalysis>" + UserProperties.isNewSubmodelForAnalysis() + "</newSubmodelForAnalysis>\n");
            raf.writeBytes("\t<enableClassificationNumberCalculation>" + UserProperties.isEnableClassificationNumberCalculation() + "</enableClassificationNumberCalculation>\n");
            raf.writeBytes("\t<RMIRegistryPort>" + UserProperties.getRMIRegistryPort() + "</RMIRegistryPort>\n");
            raf.writeBytes("\t<checkConsistency>" + UserProperties.isCheckConsistency() + "</checkConsistency>\n");
            raf.writeBytes("\t<showRemoveWarning>" + UserProperties.isShowRemoveWarning() + "</showRemoveWarning>\n");

            ArrayList<File> usedFiles = UserProperties.getLastUsedFiles();
            while (usedFiles.size() > 0) {
                raf.writeBytes("\t<usedFile>" + XMLCharacterCoder.encodeString(usedFiles.remove(usedFiles.size() - 1).toString()) + "</usedFile>\n");
            }
            ArrayList<File> xslSearchDirs = UserProperties.getXSLSearchDirs();
            while (xslSearchDirs.size() > 0) {
                raf.writeBytes("\t<xslSearchDir>" + XMLCharacterCoder.encodeString(xslSearchDirs.remove(0).toString()) + "</xslSearchDir>\n");
            }
            raf.writeBytes("</Tool3lgmUserInfoFile>");

            raf.close();
        } catch (IOException exp) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
        }
    }

}
