package de.imise.tool3lgm.plugin.ilv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.ExternalService;
import de.imise.tool3lgm.graphtools.elements.node.InternalService;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.swing.dialog.ExtendedFileChooser;
import de.imise.util.swing.dialog.ProgressDialog;

public class ILVSimpleCSVFileImporter {

    private File lastSelectedFile = null;

    private static String[] header = {
            "CFSID", "IFSID", "CFSName", "IFSName"
    };

    private final GraphDocument doc;

    private final List<ModelElement> allInternalServices;

    private final List<ModelElement> allExternalServices;

    private UserField externalServiceIdUserField;

    private UserField internalServiceIdUserField;

    public ILVSimpleCSVFileImporter() {
        doc = Tool3lgm.tool.getSelectedDoc();
        allInternalServices = doc.getModelItems(InternalService.class);
        allExternalServices = doc.getModelItems(ExternalService.class);
        importFile();
    }

    private void importFile() {
        File file = chooseImportFile();
        if (file != null) {
            ProgressDialog pd = new ProgressDialog(Tool3lgm.tool, "Import", true);
            long start = System.currentTimeMillis();
            long absoluteStart = start;
            int lineIndex = 0;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = null;
                boolean searchForHeader = true;
                while ((line = reader.readLine()) != null) {
                    lineIndex++;
                    //nur einmal nach dem Header suchen, wenn er gefunden wurde -> überpringen
                    if (searchForHeader) {
                        initIdUserFields();
                        searchForHeader = false;
                        if (isHeader(line)) {
                            continue;
                        }
                    }
                    long end = System.currentTimeMillis();
                    if (end - start > 500) {
                        pd.setStatusLabelText("Line " + lineIndex + ": " + line);
                        start = System.currentTimeMillis();
                    }
                    addLine(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
            pd.dispose();
            //System.err.println(System.currentTimeMillis() - absoluteStart);
        }

    }

    /**
     * Falls noch nicht vorhanden, wird den {@link UserFieldDefinitions} in dieser Funktion jeweils
     * eine ID-Feld für den {@link ExternalService} und den {@link InternalService} hinzugefügt.
     * Die Namen der beiden Felder ergeben sich aus den ID-Namen im Header.
     */
    private void initIdUserFields() {
        String externalServiceName = header[0];
        String internalServiceName = header[1];
        findExistingIdUserFields(externalServiceName, internalServiceName);
        createIdUserFields(externalServiceName, internalServiceName);
    }

    private void findExistingIdUserFields(final String externalServiceName, final String internalServiceName) {
        UserFieldDefinitions definitions = doc.getUserFieldDefinitions();
        List<UserField> idUserFields = definitions.getIDUserFields();
        //die beiden ID-UserFields suchen
        for (UserField idUserField : idUserFields) {
            Class<? extends UserFieldTarget> idUserFieldTarget = idUserField.getTargetClass();
            String idUserFieldName = idUserField.getName();
            if (idUserFieldTarget == ExternalService.class && idUserFieldName.equals(externalServiceName)) {
                externalServiceIdUserField = idUserField;
            }
            if (idUserFieldTarget == InternalService.class && idUserFieldName.equals(internalServiceName)) {
                internalServiceIdUserField = idUserField;
            }
            if (externalServiceIdUserField != null && internalServiceIdUserField != null) {
                break;
            }
        }
    }

    private void createIdUserFields(final String externalServiceName, final String internalServiceName) {
        //Wenn sie nicht gefunden wurden -> neu anlegen
        UserFieldDefinitions definitions = doc.getUserFieldDefinitions();
        if (externalServiceIdUserField == null) {
            externalServiceIdUserField = new UserField(ExternalService.class, Style.ID, definitions);
            externalServiceIdUserField.setName(externalServiceName);
            definitions.add(externalServiceIdUserField);
        }
        if (internalServiceIdUserField == null) {
            internalServiceIdUserField = new UserField(InternalService.class, Style.ID, definitions);
            internalServiceIdUserField.setName(internalServiceName);
            definitions.add(internalServiceIdUserField);
        }

    }

    /**
     * Parst eine tabulatorseparierte Zeile und gibt den {@link StringTokenizer} ohne Delimiter-Tokens zurück.
     * 
     * @param line
     * @return
     */
    private StringTokenizer getLineTokens(final String line) {
        StringTokenizer st = new StringTokenizer(line, "\t", false);
        return st;
    }

    private void addLine(final String line) {
        String externalServiceID, internalServiceID, externalServiceName, internalServiceName;
        try {
            StringTokenizer st = getLineTokens(line);
            externalServiceID = st.nextToken().trim();
            internalServiceID = st.nextToken().trim();
            externalServiceName = st.nextToken().trim();
            internalServiceName = st.nextToken().trim();
            addLineContent(externalServiceID, internalServiceID, externalServiceName, internalServiceName);
        } catch (Exception e) {
            System.err.println("Can not parse line: " + line);
        }
    }

    private void addLineContent(final String externalServiceID, final String internalServiceID, final String externalServiceName, final String internalServiceName) {
        ModelElement internalService = getInternalService(internalServiceName, internalServiceID);
        ModelElement externalService = getExternalService(externalServiceName, externalServiceID);
        doc.getCollection().link(externalService, internalService, TransactionManager.STANDARD_PID);
    }

    private ModelElement getInternalService(final String internalServiceName, final String internalServiceID) {
        List<ModelElement> internalServicesWithSameName = findInternalServices(internalServiceName);
        ModelElement internalService = findElementWithUserFieldValue(internalServicesWithSameName, internalServiceIdUserField, internalServiceID);
        //nur wenn ein anderer Interner Service mit demselben Namen und derselben ID gefunden wurde, ist das hier nicht null
        if (internalService == null) {
            internalService = create(doc, InternalService.class, internalServiceName, internalServiceIdUserField, internalServiceID);
            allInternalServices.add(internalService);
        }
        return internalService;
    }

    private ModelElement getExternalService(final String externalServiceName, final String externalServiceID) {
        List<ModelElement> externalServicesWithSameName = findExternalServices(externalServiceName);
        ModelElement externalService = findElementWithUserFieldValue(externalServicesWithSameName, externalServiceIdUserField, externalServiceID);
        //nur wenn ein anderer Externer Service mit demselben Namen und derselben ID gefunden wurde, ist das hier nicht null
        if (externalService == null) {
            externalService = create(doc, ExternalService.class, externalServiceName, externalServiceIdUserField, externalServiceID);
            allExternalServices.add(externalService);
        }
        return externalService;
    }

    private static ModelElement create(final GraphDocument doc, final Class<? extends ModelElement> elementClass, final String name, final UserField userField, final String userFieldValue) {
        NodeContainer nc = doc.createKnotenWithContainer(elementClass, name, "", TransactionManager.UNSPECIFIC_PID);
        ModelElement me = nc.getElement();
        me.setUserFieldInputValue(userField, userFieldValue);
        return me;
    }

    private static ModelElement findElementWithUserFieldValue(final Collection<ModelElement> elements, final UserField userField, final String value) {
        //gibt es einen mit demselben UserField-Wert?
        for (ModelElement me : elements) {
            String meValue = me.getUserFieldInputValue(userField);
            //selben UserField-Wert? 
            if (value.equals(meValue)) {
                return me;
            }
        }
        return null;
    }

    private List<ModelElement> findInternalServices(final String name) {
        List<ModelElement> meList = findElements(name, allInternalServices);
        return meList;
    }

    private List<ModelElement> findExternalServices(final String name) {
        List<ModelElement> meList = findElements(name, allExternalServices);
        return meList;
    }

    private List<ModelElement> findElements(final String name, final List<ModelElement> elements) {
        List<ModelElement> meList = Lists.newArrayList();
        for (ModelElement me : elements) {
            if (me.getName().equals(name)) {
                meList.add(me);
            }
        }
        return meList;
    }

    private File chooseImportFile() {
        ExtendedFileChooser fileChooser = new ExtendedFileChooser(ILVSimpleCSVFileImporter.class);
        FileFilter filter = new FileNameExtensionFilter("CSV-Dateien (*.csv, *.txt, *.prn)", "csv", "txt", "prn");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(lastSelectedFile);
        if (fileChooser.showOpenDialog(Tool3lgm.tool) != ExtendedFileChooser.APPROVE_OPTION) {
            return null;
        }
        lastSelectedFile = fileChooser.getSelectedFile();
        return lastSelectedFile;
    }

    private boolean isHeader(final String line) {
        boolean isHeader = true;
        for (String s : header) {
            if (!line.contains(s)) {
                isHeader = false;
                break;
            }
        }
        return isHeader;
    }

}
