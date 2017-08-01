package de.imise.tool3lgm.graphtools.gdcollection;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
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
import de.imise.tool3lgm.xml.Base64;
import de.imise.tool3lgm.xml.ToolXMLParser;
import de.imise.tool3lgm.xml.XMLCharacterCoder;

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

        LGMGraphDocument[] importSzenarios = null;
        if (chooseSzenarioDialog) {
            importSzenarios = SzenarioDialog.showImportDialog(Static.getMainFrame(), sourceGDColl);
        } else {
            importSzenarios = new LGMGraphDocument[sourceGDColl.getSzenarioCount() + 1];
            importSzenarios[0] = sourceGDColl.getMainGraphDocument();
            int i = 1;
            for (Szenario szenario : sourceGDColl.getSzenarios()) {
                importSzenarios[i++] = szenario;
            }
        }
        importSzenarios(importSzenarios, sourceGDColl);
    }

    /**
     * @param importSzenarios
     * @param sourceGDColl
     */
    private void importSzenarios(final LGMGraphDocument[] importSzenarios, final GDCollection sourceGDColl) {

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

        for (int szenarioIndex = 0; szenarioIndex < importSzenarios.length; szenarioIndex++) {
            LGMGraphDocument importDoc = importSzenarios[szenarioIndex];
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
     * @param export Array mit den zu exportierenden Szenarios
     * @param file Datei in die exportiert werden soll
     */
    public void exportSzenarios(final Szenario[] export, final File file) {
        if (!Static.getTool().checkLicenses()) {
            return;
        }
        int size = 0;
        GraphDocument mainDoc = gdcoll.getMainGraphDocument();
        for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
            LayerContainer lc = mainDoc.getLayer(ModelConstants.LAYERS[i]);
            size += lc.getKnotenCount() + lc.getKantenCount() + lc.getKnickpunkteCount();
        }

        /* hastStrings aller ModellElemente, die kopiert werden müssen */
        List<ModelElement> elements = new ArrayList<>(size);

        /* HashStrings aller Icons, die kopiert werden müssen */
        Map<String, byte[]> iconTable = gdcoll.getIconTable();
        Set<String> bitmaps = new HashSet<>(iconTable.size());

        /* HashStrings aller benutzdefinierten Eigenschaftsfelder, die mit kopiert werden müssen */
        Set<UserField> userFields = new HashSet<>();

        gdcoll.resolveCopyDependencies(export, elements, bitmaps, userFields);

        String name = gdcoll.getName() + "(export)";
        name = XMLCharacterCoder.encodeString(name);
        UserFieldDefinitions userFieldDefinitions = gdcoll.getUserFieldDefinitions();
        String description = mainDoc.getDescription();
        description = XMLCharacterCoder.encodeString(description);
        String fileVersion = gdcoll.getFileVersion();
        fileVersion = XMLCharacterCoder.encodeString(fileVersion);
        /* Datei erstellen und gefundenen Element schreiben */
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(0);
            raf.setLength(0);
            raf.writeBytes(ToolXMLParser.getCurrentVersionString());
            raf.writeBytes("<modell_3lgm_2><header><title>" + name + "</title>" + "<description>" + description + "</description>" + "<version>" + fileVersion + "</version></header>" + userFieldDefinitions.getCopyString(userFields) + "<objects>");

            try {
                for (ModelElement me : elements) {
                    raf.writeBytes(me.toXMLString());
                }
            } catch (NullPointerException e) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            }

            raf.writeBytes("</objects>");

            /* xmlString der Szenarios schreiben */
            for (int szenarioIndex = 0; szenarioIndex < export.length; szenarioIndex++) {
                raf.writeBytes(export[szenarioIndex].toXMLString());
            }

            /* xmlString der Icons schreiben */

            raf.writeBytes("<images>");

            for (String hashString : bitmaps) {
                raf.writeBytes("<bitmap type=\"gif/base64\" hash=\"" + hashString + "\">" + Base64.encode(gdcoll.getIconTable().get(hashString)) + "</bitmap>");
            }

            raf.writeBytes("</images></modell_3lgm_2>");
            raf.close();
        } catch (IOException e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
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
        try {
            RandomAccessFile file = new RandomAccessFile(f, "rw");
            file.writeBytes(ToolXMLParser.getCurrentVersionString() + gdcoll.toXMLString());
            file.close();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
        }
    }

}
