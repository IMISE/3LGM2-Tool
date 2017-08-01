/*
 * Created on 16.12.2003
 */
package de.imise.tool3lgm.xslt;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.xml.transform.TransformerException;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.dialog.SearchPathDialog;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionImExportHandler;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.BrowseUtils;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.htmlxml.XMLCharacterCoder;
import de.imise.util.image.ComponentAsImageExportHandler;
import de.imise.util.io.FileHandler;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.dialog.DirectoryChooser;
import de.imise.util.swing.dialog.ProgressDialog;

/**
 * @author Thomas Rudert
 */
public class WebExportDialog extends JDialog {

    /** Rssourcendateien, die der Webexportdialog braucht. Achtung: die Reihenfolge ist wichtig! index.html muss als erste stehen. */
    public static final String[] WEB_EXPORT_RESOURCES_FILES = {
            "index.html",
            "start.html",
            "tool3lgm.gif",
            "icon.jpg",
    };

    private final GDCollection collection;
    private final TableModel tableModel;
    private JTable table;
    private ExtendedTextField destination;
    private final JCheckBox checkBoxShowResult;

    /**
     * @param owner
     * @throws java.awt.HeadlessException
     */
    public WebExportDialog(final JFrame owner, final GDCollection collection) throws HeadlessException {
        super(owner, Tool3lgmConstants.getResString("webExport"), true);

        // da evtl. viele Verzeichnisse nach Scripten durchsucht werden müssen, einen Fortschrittsdialog zeigen
        ProgressDialog progressDialog = new ProgressDialog(owner);
        progressDialog.setStatusLabelText(Tool3lgmConstants.getResString("trans_load_scripts"));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.collection = collection;

        getContentPane().setLayout(new BorderLayout());

        final XSLTResourceHandler xsltResourceHandler = new XSLTResourceHandler();

        checkBoxShowResult = new JCheckBox(Tool3lgmConstants.getResString("trans_browser"), true);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(checkBoxShowResult);
        panel.add(new JButton(new AbstractAction(Tool3lgmConstants.getResString("ok")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                commit();
                dispose();
            }
        }));

        panel.add(new JButton(new AbstractAction(Tool3lgmConstants.getResString("trans_path")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new SearchPathDialog(owner, UserProperties.getXSLSearchDirs()).setVisible(true);
                tableModel.clear();
                tableModel.addScripts(xsltResourceHandler.getStandardScripts());
                tableModel.addScripts(XSLTFileHandler.getXSLTScripts(UserProperties.getXSLSearchDirs()));
                tableModel.fireTableDataChanged();
            }
        }));

        panel.add(new JButton(new AbstractAction(Tool3lgmConstants.getResString("cancel")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        }));
        getContentPane().add(panel, BorderLayout.SOUTH);

        panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(new JLabel(Tool3lgmConstants.getResString("labelDestination")), BorderLayout.WEST);
        panel.add(destination = new ExtendedTextField(), BorderLayout.CENTER);

        String modelName = collection.getName();
        modelName = modelName.replace(' ', '_');
        modelName = modelName.replace("<", "");
        modelName = modelName.replace(">", "");
        // die evtl. vorhandene Dateiendung wegschneiden (nur vorhanden, wenn das Modell schon mal gespeichert wurde)
        File tmpFile = new File(modelName);
        if (Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LGM3).accept(tmpFile)) {
            modelName = modelName.substring(0, modelName.lastIndexOf("."));
        }

        destination.setText(UserProperties.getUserHomePath().toString() + File.separator + "3LGM_export_" + modelName);
        destination.setEditable(false);
        panel.add(new JButton(new AbstractAction(Tool3lgmConstants.getResString("explore")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                File path = DirectoryChooser.showDialog(WebExportDialog.this, "web");// den String braucht man nicht auslagern
                if (path != null) {
                    destination.setText(path.toString());
                }
            }

        }), BorderLayout.EAST);
        getContentPane().add(panel, BorderLayout.NORTH);

        getContentPane().add(new JScrollPane(table = new JTable()), BorderLayout.CENTER);

        tableModel = new TableModel(xsltResourceHandler.getStandardScripts());
        tableModel.addScripts(XSLTFileHandler.getXSLTScripts(UserProperties.getXSLSearchDirs()));
        table.setModel(tableModel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(500);

        pack();

        int x = getOwner().getX() + (getOwner().getWidth() - getWidth()) / 2;
        int y = getOwner().getY() + (getOwner().getHeight() - getHeight()) / 2;
        x = x > 0 ? x : 0;
        y = y > 0 ? y : 0;
        x = x + getHeight() < getGraphicsConfiguration().getDevice().getDisplayMode().getWidth() ? x : getGraphicsConfiguration().getDevice().getDisplayMode().getWidth() - getWidth();
        y = y + getHeight() < getGraphicsConfiguration().getDevice().getDisplayMode().getHeight() ? y : getGraphicsConfiguration().getDevice().getDisplayMode().getHeight() - getHeight();
        setLocation(x, y);

        progressDialog.dispose();

    }

    public static void showWebExportDialog(final JFrame owner, final GDCollection collection) {
        new WebExportDialog(owner, collection).setVisible(true);

    }

    private void commit() {
        File path = null;
        try {
            path = new File(destination.getText());
        } catch (Exception e) {
            try {
                path = new File(UserProperties.getUserHomePath().getCanonicalPath() + File.separator + "_tool3lgm_webexport");
            } catch (Exception ex) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), ex);
                return;
            }
        }

        Static.showProgressDialog(this);
        Static.setProgressDialogStatusLabel("webExport");

        if (!path.isDirectory()) {
            path.mkdir();
        }
        File filesDir = new File(path, "files");
        if (!filesDir.isDirectory()) {
            filesDir.mkdir();
        }

        // die index.html ins Oberverzeichnis kopieren und alle anderen ins files-Verzeichnis kopieren
        for (int i = 0; i < WEB_EXPORT_RESOURCES_FILES.length; i++) {
            InputStream resourceStream = ClassLoader.getSystemResourceAsStream(Tool3lgmConstants.WEB_EXPORT_RESOURCE_DIR_NAME + WEB_EXPORT_RESOURCES_FILES[i]);
            File dest = new File(i == 0 ? path : filesDir, WEB_EXPORT_RESOURCES_FILES[i]);
            FileHandler.copyFile(resourceStream, dest);
        }

        try {
            ArrayList<XSLTScript> selected = tableModel.getSelectedScripts();

            RandomAccessFile raf = new RandomAccessFile(new File(filesDir, "head.html"), "rw");
            raf.setLength(0);
            raf.writeBytes(getHeadHTMLString());
            raf.close();

            raf = new RandomAccessFile(new File(filesDir, "description.html"), "rw");
            raf.setLength(0);
            raf.writeBytes(getDescriptionHTMLString(collection.getMainGraphDocument()));
            raf.close();

            raf = new RandomAccessFile(new File(filesDir, "menu.html"), "rw");
            raf.setLength(0);
            raf.writeBytes(getMenuHTMLString());
            raf.close();

            File tempXMLFile = new File(Tool3lgmConstants.TEMP_PATH + "temporary_XMLFile_for_XSLT-Export.xml");
            GDCollectionImExportHandler imExportHandler = collection.getImExportHandler();
            imExportHandler.exportModel(tempXMLFile);
            for (int i = 0; i < selected.size(); i++) {
                try {
                    XMLTransformer.transform(selected.get(i).openStream(), selected.get(i).getSource(), tempXMLFile, filesDir + "/xslt" + i + ".html");
                } catch (IOException e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"));
                    return;
                } catch (TransformerException e) {
                    if (hasInconsistencies()) {
                        Log.show(Log.ERROR, Tool3lgmConstants.getErrString("webExportInconsistencyError"));
                    } else {
                        Log.show(Log.ERROR, Tool3lgmConstants.getErrString("webExportXSLError"));
                    }
                    return;
                }
            }
            tempXMLFile.delete();

            Szenario[] szenarios = new Szenario[1];
            int j = 0;
            for (Szenario szen : collection.getSzenarios()) {
                szenarios[0] = szen;
                Static.setProgressDialogStatusLabel("webExport", ": " + szenarios[0].getTitle());

                raf = new RandomAccessFile(new File(filesDir, "szen" + j + "_description.html"), "rw");
                raf.setLength(0);
                raf.writeBytes(getDescriptionHTMLString(szen));
                raf.close();

                tempXMLFile = new File(Tool3lgmConstants.TEMP_PATH + "temporary_XMLFile_for_XSLT-Export.xml");
                imExportHandler.exportSzenarios(szenarios, tempXMLFile);
                for (int i = 0; i < selected.size(); i++) {
                    try {
                        XSLTScript selectedScript = selected.get(i);
                        InputStream openStream = selectedScript.openStream();
                        String sourceName = selectedScript.getSource();
                        String destinationName = filesDir + "/szen" + j + "_xslt" + i + ".html";
                        XMLTransformer.transform(openStream, sourceName, tempXMLFile, destinationName);
                    } catch (Error e) {
                        Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    }
                }
                tempXMLFile.delete();

                GDCollection gdcoll = szenarios[0].getCollection();
                int activeLayer = gdcoll.getActiveLayer();

                BasicGraphArea area = new BasicGraphArea(szen);
                createImage(area, filesDir.toString() + "/szen" + j + "_3layerView.jpg", 0.8, ModelConstants.NO_LAYER);
                gdcoll.setActiveLayer(ModelConstants.DOMAIN_LAYER);
                createImage(area, filesDir.toString() + "/szen" + j + "_domainLayer.jpg", 0.8, ModelConstants.DOMAIN_LAYER);
                gdcoll.setActiveLayer(ModelConstants.LOGICAL_LAYER);
                createImage(area, filesDir.toString() + "/szen" + j + "_logicalLayer.jpg", 0.8, ModelConstants.LOGICAL_LAYER);
                gdcoll.setActiveLayer(ModelConstants.PHYSICAL_LAYER);
                createImage(area, filesDir.toString() + "/szen" + j + "_physicalLayer.jpg", 0.8, ModelConstants.PHYSICAL_LAYER);
                gdcoll.setActiveLayer(activeLayer);
                j++;
            }

        } catch (Exception exp) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
        }

        Static.closeProgressDialog();

        if (checkBoxShowResult.isSelected()) {
            File file = new File(path, WEB_EXPORT_RESOURCES_FILES[0]);
            BrowseUtils.browseAbsoluteFile(file);
        }
    }

    /**
     * export layer of gcdollection to jpg-File
     *
     * @param area
     * @param filename
     *            String with jpg-File
     * @param zoomFactor
     *            0 < x < 1
     * @param layer
     *            -1 for 3layerView; 0 for physical layer; 2 for logical layer;
     *            4 for domain layer
     */
    public static final void createImage(final BasicGraphArea area, final String filename, final double zoomFactor, final int layer) {
        if (layer < 0) {
            area.setInterLayerSpace(new Double(400 * area.getDocument().getPageSizeFactor()).intValue());
            area.setDegree(45);
            area.setMultiViewEnabled(true);
        } else {
            area.setDegree(0);
            area.setInterLayerSpace(0);
            area.setMultiViewEnabled(false);
        }
        area.setZoom(zoomFactor);
        area.setSize(area.getPreferredSize());
        area.setPaintState(PaintState.SAVE_IMAGE_AS_FILE);
        ComponentAsImageExportHandler.createFile(area, filename);
        area.setPaintState(PaintState.REGULAR);
    }

    /**
     * @return
     */
    private String getMenuHTMLString() {
        ArrayList<XSLTScript> selected = tableModel.getSelectedScripts();
        String[] xslScriptNames = new String[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            xslScriptNames[i] = selected.get(i).getName();
        }
        StringBuilder returnValue = new StringBuilder(
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\t\"http://www.w3.org/TR/html4/loose.dtd\">\n<html>\n<head>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<META content=\"Tool3lgm\" name=\"author\">\n<title>menu</title>\n<script type=\"text/javascript\">\n<!--\nfunction changeSelection(location, position) {\n parent.content.location.href = location;\n parent.head.document.getElementById(\"position\").firstChild.data = position;\n}\nfunction showPicture(pictureSource, position) {\nparent.content.document.open();\nparent.content.document.write(\"<img src=\\\"\"+pictureSource+\"\\\" />\");\nparent.content.document.close();\nparent.head.document.getElementById(\"position\").firstChild.data = position;\n }\n -->\n</script>\n<style type=\"text/css\">\nul { margin-left:0.5cm; }\n</style>\n</head>\n<body>\n");

        returnValue.append("<ul><li><a href=\"javascript:changeSelection('start.html','/" + XMLCharacterCoder.encodeString(collection.getName()) + "')\">" + "Hauptmodell" + "\n<ul><li><a href=\"javascript:changeSelection('description.html','/"
                + XMLCharacterCoder.encodeString(collection.getName()) + "/Beschreibung')\">Beschreibung</a></li>\n");
        for (int i = 0; i < xslScriptNames.length; i++) {
            returnValue.append("<li><a href=\"javascript:changeSelection('xslt" + i + ".html','/" + XMLCharacterCoder.encodeString(collection.getName()) + "/" + XMLCharacterCoder.encodeString(xslScriptNames[i]) + "')\">"
                    + XMLCharacterCoder.encodeString(xslScriptNames[i]) + "</a></li>\n");
        }
        returnValue.append("</ul>");
        int j = 0;
        for (Szenario szen : collection.getSzenarios()) {
            returnValue.append("<li>" + XMLCharacterCoder.encodeString(szen.getTitle()) + "<ul><li><a href=\"javascript:changeSelection('szen" + j + "_description.html','/" + XMLCharacterCoder.encodeString(collection.getName()) + "/"
                    + XMLCharacterCoder.encodeString(szen.getTitle()) + "/Beschreibung')\">Beschreibung</a></li>\n");
            returnValue.append(
                    "<li><a href=\"javascript:showPicture('szen" + j + "_3layerView.jpg','/" + XMLCharacterCoder.encodeString(collection.getName()) + "/" + XMLCharacterCoder.encodeString(szen.getTitle()) + "/3-EbenenSicht')\">3-Ebenen-Sicht</a></li>\n");
            returnValue.append("<li><a href=\"javascript:showPicture('szen" + j + "_domainLayer.jpg','/" + XMLCharacterCoder.encodeString(collection.getName()) + "/" + XMLCharacterCoder.encodeString(szen.getTitle())
                    + "/Fachliche Ebene')\">Fachliche Ebene</a></li>\n");
            returnValue.append("<li><a href=\"javascript:showPicture('szen" + j + "_logicalLayer.jpg','/" + XMLCharacterCoder.encodeString(collection.getName()) + "/" + XMLCharacterCoder.encodeString(szen.getTitle())
                    + "/Logische Werkzeugebene')\">Logische Werkzeugebene</a></li>\n");
            returnValue.append("<li><a href=\"javascript:showPicture('szen" + j + "_physicalLayer.jpg','/" + XMLCharacterCoder.encodeString(collection.getName()) + "/" + XMLCharacterCoder.encodeString(szen.getTitle())
                    + "/Physische Werkzeugebene')\">Physische Werkzeugebene</a></li>\n");
            for (int i = 0; i < xslScriptNames.length; i++) {
                returnValue.append("<li><a href=\"javascript:changeSelection('szen" + j + "_xslt" + i + ".html','/" + XMLCharacterCoder.encodeString(collection.getName()) + "/" + szen.getTitle() + "/" + XMLCharacterCoder.encodeString(xslScriptNames[i])
                        + "')\">" + XMLCharacterCoder.encodeString(xslScriptNames[i]) + "</a></li>\n");
            }
            returnValue.append("</ul></li>\n");
            j++;
        }
        returnValue.append("</ul>\n</body>\n</html>\n");

        return returnValue.toString();
    }

    /**
     * @return
     */
    private String getHeadHTMLString() {
        StringBuilder sb = new StringBuilder(
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\t\"http://www.w3.org/TR/html4/loose.dtd\">\n<html>\n<head>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<META content=\"Tool3lgm\" name=\"author\">\n<title>head</title>\n<style type=\"text/css\">\n.standard { margin-top:0px; margin-left:0px; margin-bottom:0px; margin-right:10px; }\n</style>\n</head>\n<body class=\"standard\">\n<table width=\"100%\">\n<tr>\n<td rowspan=\"2\"><img src=\"icon.jpg\" width=\"89\" height=\"68\" alt=\"icon.jpg\" class=\"standard\"/></td>\n<td colspan=\"2\"><h2 class=\"standard\">Web-Darstellung 3LGM&#178;</h2></td>\n</tr><tr>\n<td><p class=\"standard\">Auswahl:</p></td>\n<td width=\"100%\"><h4 id=\"position\" class=\"standard\">/");
        sb.append(XMLCharacterCoder.encodeString(collection.getName()));
        sb.append("</h4></td>\n</tr>\n</table>\n</body>\n</html>");
        return sb.toString();
    }

    /**
     * @param doc
     * @return
     */
    private static final String getDescriptionHTMLString(final GraphDocument doc) {
        StringBuilder sb = new StringBuilder(
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\t\"http://www.w3.org/TR/html4/loose.dtd\">\n<html>\n<head>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<META content=\"Tool3lgm\" name=\"author\">\n<title>Beschreibung</title>\n</head>\n<body>\n<p>");
        sb.append(XMLCharacterCoder.encodeString(doc.getDescription()));
        sb.append("</p>\n</body>\n</html>");
        return sb.toString();
    }

    /**
     * Gibt wieder, ob Inkonsistenzen im Modell bestehen
     *
     * @return
     */
    private boolean hasInconsistencies() {
        return new ConsistencyChecker(collection).hasCardinalityInconsistencies();
    }

    /**
     */
    private class TableModel extends AbstractTableModel {
        private Boolean[] selections;
        private final ArrayList<XSLTScript> xslScripts;

        @Override
        public int getRowCount() {
            return xslScripts.size();
        }

        /**
         *
         */
        public void clear() {
            xslScripts.clear();
            selections = new Boolean[0];
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        /**
         * @param xslScripts
         */
        public TableModel(final ArrayList<XSLTScript> xslScripts) {
            super();
            this.xslScripts = xslScripts;
            selections = new Boolean[xslScripts.size()];
            for (int i = 0; i < xslScripts.size(); i++) {
                selections[i] = new Boolean(true);
            }
        }

        /**
         * @param newXSLScripts
         */
        public void addScripts(final ArrayList<XSLTScript> newXSLScripts) {
            for (int i = 0; i < newXSLScripts.size(); i++) {
                if (!xslScripts.contains(newXSLScripts.get(i))) {
                    xslScripts.add(newXSLScripts.get(i));
                }
            }
            Boolean[] newSelections = new Boolean[xslScripts.size()];
            int i = 0;
            for (; i < selections.length; i++) {
                newSelections[i] = selections[i];
            }
            for (; i < newSelections.length; i++) {
                newSelections[i] = new Boolean(true);
            }
            selections = newSelections;
        }

        @Override
        public String getColumnName(final int column) {
            switch (column) {
            case 0:
                return Tool3lgmConstants.getResString("labelInclude");
            case 1:
                return Tool3lgmConstants.getResString("trans_file");
            case 2:
                return Tool3lgmConstants.getResString("trans_name");
            case 3:
                return Tool3lgmConstants.getResString("trans_des");
            default:
                return null;
            }
        }

        @Override
        public Object getValueAt(final int row, final int col) {
            if (row >= getRowCount()) {
                return null;
            }
            switch (col) {
            case 0:
                return selections[row];
            case 1:
                return xslScripts.get(row).getSource();
            case 2:
                return xslScripts.get(row).getName();
            case 3:
                return xslScripts.get(row).getDescription();
            }
            return null;
        }

        @Override
        public Class<?> getColumnClass(final int col) {
            switch (col) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(final int row, final int col) {
            if (col == 0 && row < getRowCount()) {
                return true;
            }
            return false;
        }

        @Override
        public void setValueAt(final Object aValue, final int row, final int column) {
            if (!isCellEditable(row, column)) {
                return;
            }
            selections[row] = (Boolean) aValue;
        }

        //		/**
        //		 * selektiert alle XSL-Skripte
        //		 * /
        //		public void selectAll() {
        //			for (int i = 0; i < getRowCount(); i++)
        //				setValueAt(new Boolean(true), i, 0);
        //		}

        /**
         * gibt die ausgewählten Seznarios zurück
         *
         * @return Array mit den selektierten Szenarios
         */
        public ArrayList<XSLTScript> getSelectedScripts() {
            ArrayList<XSLTScript> selected = new ArrayList<>();
            for (int i = 0; i < selections.length; i++) {
                if (selections[i].booleanValue()) {
                    selected.add(xslScripts.get(i));
                }
            }

            return selected;
        }

    }

}