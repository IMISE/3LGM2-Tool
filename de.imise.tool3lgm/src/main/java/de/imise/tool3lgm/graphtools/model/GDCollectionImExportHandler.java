package de.imise.tool3lgm.graphtools.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.SzenarioDialog;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
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

public class GDCollectionImExportHandler {

    private final GDCollection gdcoll;

    public GDCollectionImExportHandler(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
    }

    /**
     * @param file
     */
    public void importModel(final File file) {
        importSzenarios(file, false);
    }

    /**
     * Importiert Szenarios aus einem anderem Modell in dieses Modell
     *
     * @param file
     *            Modelldatei, aus der Szenarios importiert werden sollen
     * @param chooseSzenarioDialog
     *            wenn <code>true</code> kann der
     */
    public void importSzenarios(final File file, final boolean chooseSzenarioDialog) {
        GDCollection sourceGDColl = new GDCollection();

        Static.showProgressDialog();
        Static.setProgressDialogTitle(Tool3lgmConstants.getResString("load_model") + " " + file.getName());
        Static.setProgressDialogStatusLabel("read_progress");

        try {
            GDCollectionFileHandler fileHandler = sourceGDColl.getFileHandler();
            fileHandler.setFile(file);
            fileHandler.loadFromRAF(file);
        } catch (Exception exp) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
        }

        Static.closeProgressDialog();

        ImmutableList.Builder<GraphDocument> importSzenarios = new ImmutableList.Builder<>();
        if (chooseSzenarioDialog) {
            importSzenarios.addAll(SzenarioDialog.showImportDialog(Static.getMainFrame(), sourceGDColl));
        } else {
            importSzenarios.add(sourceGDColl.getMainGraphDocument());
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
        LGMGraphDocument collectionMainDoc = sourceGDColl.getMainGraphDocument();
        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            LayerContainer lc = collectionMainDoc.getLayer(ModelConstants.LAYERS[i]);
            size += lc.getKnotenCount() + lc.getKantenCount() + lc.getKnickpunkteCount();
        }

        /* ModellElemente, die kopiert werden müssen */
        List<ModelElement> elements = new ArrayList<>(size);

        /* HashStrings aller Icons, die kopiert werden müssen */
        Set<String> bitmaps = new HashSet<>();

        bitmaps = new HashSet<>(sourceGDColl.getIconTable().size());
        Set<UserField> userFields = new HashSet<>();

        sourceGDColl.resolveCopyDependencies(importSzenarios, elements, bitmaps, userFields);
        UserFieldDefinitions userFieldDefinitions = gdcoll.getUserFieldDefinitions();
        for (UserField uf : userFields) {
            userFieldDefinitions.add(uf);
        }

        Map<String, byte[]> iconTable = gdcoll.getIconTable();
        for (String bmHash : bitmaps) {
            iconTable.put(bmHash, sourceGDColl.getIconTable().get(bmHash));
        }

        LGMGraphDocument mainDoc = gdcoll.getMainGraphDocument();
        for (ModelElement element : elements) {
            element.removeAllContainer();
            ElementContainer container = element.createContainer(mainDoc);
            mainDoc.getLayer(element.layerFor()).add(container);
        }
        for (GraphDocument importDoc : importSzenarios) {
            if (!(importDoc instanceof Szenario)) {
                continue;
            }
            Szenario newSzenario = gdcoll.createSzenario(importDoc.getTitle() + " (import)", false, importDoc.getDescription(), importDoc.getHashString(), false, TransactionManager.STANDARD_PID);
            for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
                LayerContainer importLayerContainer = importDoc.getLayer(ModelConstants.LAYERS[i]);
                newSzenario.getLayer(ModelConstants.LAYERS[i]).set3LGMLayout(importLayerContainer.get3LGMLayout());
                for (NodeContainer importKC : importLayerContainer.getKnoten()) {
                    ModelElement element = mainDoc.findKnotenCoded(importKC.getElement().getHashString());
                    ElementContainer container = element.createContainer(newSzenario);
                    container.set3LGMLayout(importKC.get3LGMLayout());
                    container.setE3LGMLayout(importKC.getE3LGMLayout());
                    container.setNE3LGMLayout(importKC.getNE3LGMLayout());
                    container.setExpanded(importKC.isExpanded());
                    newSzenario.getLayer(ModelConstants.LAYERS[i]).add(container);
                    container.refreshText();
                }

                for (EdgeContainer importKC : importLayerContainer.getKanten()) {
                    ModelElement element = mainDoc.findKanteCoded(importKC.getElement().getHashString());
                    if (element == null) {
                        continue;
                    }
                    ((Kante) element).decodeHashStrings(mainDoc);
                    ElementContainer container = element.createContainer(newSzenario);
                    container.set3LGMLayout(importKC.get3LGMLayout());
                    container.setE3LGMLayout(importKC.getE3LGMLayout());
                    container.setNE3LGMLayout(importKC.getNE3LGMLayout());
                    container.setExpanded(importKC.isExpanded());
                    newSzenario.getLayer(ModelConstants.LAYERS[i]).add(container);
                    container.refreshText();
                }

                for (BendpointContainer importKC : importLayerContainer.getKnickpunkte()) {
                    ModelElement element = mainDoc.findKnickpunktCoded(importKC.getElement().getHashString());
                    EdgeContainer kc = newSzenario.findEdgeContainerCoded(((Knickpunkt) element).getKantenHash());
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
                    kc.setKnickpunkt(knC, knC.getKnickpunktKnoten().getIndex());
                    kc.computeBorderPoints();
                }
            }

            Static.getTool().createSzenarioFrame(newSzenario);
        }
        Static.closeProgressDialog();
        gdcoll.getUserFieldDefinitions().hasCrossReferences();
        gdcoll.distribute(GraphDocument.DATA_CHANGED);
    }

    /**
     * exportiert die übergebenen Szenarios in eine neue Datei
     *
     * @param szenarios Liste mit den zu exportierenden Szenarios
     * @param file Datei in die exportiert werden soll
     */
    public void exportSzenarios(final List<Szenario> szenarios, final File file) {
        if (!Static.getTool().checkLicenses()) {
            return;
        }
        if (szenarios.size() == gdcoll.getSzenarioCount()) {
            exportModel(file);
        } else {
            int size = 0;
            GraphDocument mainDoc = gdcoll.getMainGraphDocument();
            for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
                LayerContainer lc = mainDoc.getLayer(ModelConstants.LAYERS[i]);
                size += lc.getKnotenCount() + lc.getKantenCount() + lc.getKnickpunkteCount();
            }
            // hastStrings aller ModellElemente, die kopiert werden müssen
            List<ModelElement> elements = new ArrayList<>(size);
            // HashStrings aller Icons, die kopiert werden müssen
            Map<String, byte[]> iconTable = gdcoll.getIconTable();
            Set<String> iconHashes = new HashSet<>(iconTable.size());
            // HashStrings aller benutzdefinierten Eigenschaftsfelder, die mit kopiert werden müssen
            Set<UserField> userFields = new HashSet<>();
            gdcoll.resolveCopyDependencies(szenarios, elements, iconHashes, userFields);
            ToolXMLWriter.writeExport(gdcoll, file, szenarios, elements, userFields, iconHashes);
        }
    }

    /**
     * Speichert das Modell in der angegeben Datei im XML-Format (File wird
     * geschlossen)
     *
     * @param f
     *            Ziel-Datei beim Speichern
     */
    public void exportModel(final File f) {
        if (!Static.getTool().checkLicenses()) {
            return;
        }
        if (f.exists()) {
            f.delete();
        }
        ToolXMLWriter.write(gdcoll, f, false);
    }

}
