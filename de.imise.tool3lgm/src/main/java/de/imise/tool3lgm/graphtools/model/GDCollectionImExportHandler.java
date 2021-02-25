package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.Static.getSelectedGDCollection;
import static de.imise.tool3lgm.Tool3lgmConstants.getFileNameExtensionFilters;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.dialog.SzenarioDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.ToolXMLWriter;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * Importiert ein Modell komplett oder teilweise (Teilmodelle) in ein anderes.
 *
 * @author AXS (18 Jun 2010)
 */
public final class GDCollectionImExportHandler {

    /** Das Modell, in das importiert wird */
    private final GDCollection gdcoll;

    /**
     * @param gdcoll Modell, in das importiert wird
     */
    public GDCollectionImExportHandler(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
    }

    /**
     * Zeigt einen Dialog an, um eine Modelldatei zu wählen und stellt dann die
     * darin enthaltenen Teilmodelle zur Auswahl, die dann importiert werden.
     */
    public static final void importSzenarios() {
        importModel(true);
    }

    /**
     * Zeigt einen Dialog an, um eine Modelldatei zu wählen und importiert dann
     * das Gesamtmodell.
     */
    public static final void importModel() {
        importModel(false);
    }

    /**
     * Zeigt einen Dialog an, um eine Modelldatei zu wählen, die importiert
     * werden soll.
     *
     * @param chooseSubmodels wenn <code>true</code> wird auch ein Auswahldialog
     *            für zu importierende Teilmodelle angezeigt. Bei
     *            <code>false</code> wird das Gesamtmodell importiert.
     */
    private static final void importModel(final boolean chooseSubmodels) {
        ExtendedFileChooser oeffnenDialog = new ExtendedFileChooser(null);
        oeffnenDialog.setMultiSelectionEnabled(false);
        oeffnenDialog.setFileFilters(false, getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED));
        if (oeffnenDialog.showOpenDialog(getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            GDCollection selectedGDColl = getSelectedGDCollection();
            GDCollectionImExportHandler imExportHandler = selectedGDColl.getImExportHandler();
            imExportHandler.importSzenarios(oeffnenDialog.getSelectedFile(), chooseSubmodels);
        }
    }

    /**
     * Importiert Szenarios aus einem anderem Modell in dieses Modell
     *
     * @param file Modelldatei, aus der Szenarios importiert werden sollen
     * @param chooseSzenarioDialog wenn <code>true</code> kann der
     */
    private void importSzenarios(final File file, final boolean chooseSzenarioDialog) {
        GDCollection sourceGDColl = new GDCollection(gdcoll.getModelType());

        Static.showProgressDialog();
        Static.setProgressDialogTitle("load_model", file.getName());
        Static.setProgressDialogStatusLabel("read_progress");

        try {
            GDCollectionFileHandler fileHandler = sourceGDColl.getFileHandler();
            fileHandler.setFile(file);
            fileHandler.loadFromRAF(file);
        } catch (Exception exp) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), exp);
        }

        Static.closeProgressDialog();

        ImmutableList.Builder<GraphDocument> importSzenarios = new ImmutableList.Builder<>();
        if (chooseSzenarioDialog) {
            importSzenarios.addAll(SzenarioDialog.showImportDialog(Static.getMainFrame(), sourceGDColl));
        } else {
            importSzenarios.add(sourceGDColl.getMainDoc());
            importSzenarios.addAll(sourceGDColl.getSzenarios());
        }
        importSzenarios(importSzenarios.build(), sourceGDColl);
    }

    /**
     * @param importSzenarios
     * @param sourceGDColl
     */
    private void importSzenarios(final List<GraphDocument> importSzenarios, final GDCollection sourceGDColl) {

        Static.showProgressDialog();
        Static.setProgressDialogStatusLabel("importSzenario");
        int size = 0;
        LGMGraphDocument collectionMainDoc = sourceGDColl.getMainDoc();
        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            LayerContainer lc = collectionMainDoc.getLayer(ModelConstants.LAYERS[i]);
            size += lc.getNodeContainerCount() + lc.getEdgeContainerCount() + lc.getBendpointContainerCount();
        }

        /* ModellElemente, die kopiert werden müssen */
        List<ModelElement> elements = new ArrayList<>(size);

        /* IDs aller Icons, die kopiert werden müssen */
        Set<String> iconIDs = new HashSet<>();

        iconIDs = new HashSet<>(sourceGDColl.getIconTable().size());
        Set<UserField> userFields = new HashSet<>();

        CopyDependencyResolver copyDependencyResolver = gdcoll.getCopyDependencyResolver();
        copyDependencyResolver.resolveCopyDependencies(importSzenarios, elements, iconIDs, userFields);
        UserFieldDefinitions userFieldDefinitions = gdcoll.getUserFieldDefinitions();
        for (UserField uf : userFields) {
            userFieldDefinitions.add(uf);
        }

        Map<String, byte[]> iconTable = gdcoll.getIconTable();
        for (String iconID : iconIDs) {
            iconTable.put(iconID, sourceGDColl.getIconTable().get(iconID));
        }

        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        for (ModelElement element : elements) {
            element.removeAllContainer();
            ElementContainer container = element.createContainer(mainDoc);
            mainDoc.getLayer(element.layerFor()).add(container);
        }
        for (GraphDocument importDoc : importSzenarios) {
            if (!(importDoc instanceof Szenario)) {
                continue;
            }
            Szenario newSzenario = gdcoll.createSzenario(importDoc.getTitle() + " (import)", false, importDoc.getDescription(), importDoc.getID(), false, TransactionManager.STANDARD_PID);
            for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
                LayerContainer importLayerContainer = importDoc.getLayer(ModelConstants.LAYERS[i]);
                newSzenario.getLayer(ModelConstants.LAYERS[i]).set3LGMLayout(importLayerContainer.get3LGMLayout());
                for (NodeContainer importKC : importLayerContainer.getGraphNodeContainers()) {
                    ModelElement element = mainDoc.findNodeCoded(importKC.getElement().getID());
                    ElementContainer container = element.createContainer(newSzenario);
                    container.set3LGMLayout(importKC.get3LGMLayout());
                    container.setE3LGMLayout(importKC.getE3LGMLayout());
                    container.setNE3LGMLayout(importKC.getNE3LGMLayout());
                    container.setExpanded(importKC.isExpanded());
                    newSzenario.getLayer(ModelConstants.LAYERS[i]).add(container);
                    container.refreshText();
                }

                for (EdgeContainer importKC : importLayerContainer.getEdgeContainers()) {
                    ModelElement element = mainDoc.findEdgeCoded(importKC.getElement().getID());
                    if (element == null) {
                        continue;
                    }
                    ((Edge) element).decodeIDs(mainDoc);
                    ElementContainer container = element.createContainer(newSzenario);
                    container.set3LGMLayout(importKC.get3LGMLayout());
                    container.setE3LGMLayout(importKC.getE3LGMLayout());
                    container.setNE3LGMLayout(importKC.getNE3LGMLayout());
                    container.setExpanded(importKC.isExpanded());
                    newSzenario.getLayer(ModelConstants.LAYERS[i]).add(container);
                    container.refreshText();
                }

                for (BendpointContainer importKC : importLayerContainer.getBendpointContainers()) {
                    ModelElement element = mainDoc.findBendpointCoded(importKC.getElement().getID());
                    EdgeContainer kc = newSzenario.findEdgeContainerCoded(((Bendpoint) element).getEdgeID());
                    if (kc == null) {
                        continue;
                    }
                    element.addEdge(kc.getEdge());
                    ElementContainer container = element.createContainer(newSzenario);
                    container.set3LGMLayout(importKC.get3LGMLayout());
                    container.setE3LGMLayout(importKC.getE3LGMLayout());
                    container.setNE3LGMLayout(importKC.getNE3LGMLayout());
                    container.setExpanded(importKC.isExpanded());
                    newSzenario.getLayer(ModelConstants.LAYERS[i]).add(container);
                    container.refreshText();
                    BendpointContainer knC = (BendpointContainer) container;
                    kc.setBendpointContainer(knC, knC.getBendpoint().getIndex());
                    kc.computeBorderPoints();
                }
            }
        }
        Static.closeProgressDialog();
        gdcoll.getUserFieldDefinitions().hasCrossReferences();
        gdcoll.distribute(DATA_CHANGED);
    }

    /**
     * exportiert die übergebenen Szenarios in eine neue Datei
     *
     * @param szenarios Liste mit den zu exportierenden Szenarios
     * @param file Datei in die exportiert werden soll
     */
    public void exportSzenarios(final List<Szenario> szenarios, final File file) {
        //        if (!LicenseHandler.checkLicenses()) {
        //            return;
        //        }
        if (szenarios.size() == gdcoll.getSzenarioCount()) {
            exportModel(file);
        } else {
            int size = 0;
            GraphDocument mainDoc = gdcoll.getMainDoc();
            for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
                LayerContainer lc = mainDoc.getLayer(ModelConstants.LAYERS[i]);
                size += lc.getNodeContainerCount() + lc.getEdgeContainerCount() + lc.getBendpointContainerCount();
            }
            // hastStrings aller ModellElemente, die kopiert werden müssen
            List<ModelElement> elements = new ArrayList<>(size);
            // IDs aller Icons, die kopiert werden müssen
            Map<String, byte[]> iconTable = gdcoll.getIconTable();
            Set<String> iconIDs = new HashSet<>(iconTable.size());
            // IDs aller benutzdefinierten Eigenschaftsfelder, die mit kopiert werden müssen
            Set<UserField> userFields = new HashSet<>();
            CopyDependencyResolver copyDependencyResolver = gdcoll.getCopyDependencyResolver();
            copyDependencyResolver.resolveCopyDependencies(szenarios, elements, iconIDs, userFields);
            ToolXMLWriter.writeExport(gdcoll, file, szenarios, elements, userFields, iconIDs);
        }
    }

    /**
     * Speichert das Modell in der angegeben Datei im XML-Format (File wird
     * geschlossen)
     *
     * @param f Ziel-Datei beim Speichern
     */
    public void exportModel(final File f) {
        //        if (!LicenseHandler.checkLicenses()) {
        //            return;
        //        }
        if (f.exists()) {
            f.delete();
        }
        ToolXMLWriter.write(gdcoll, f);
    }

}
