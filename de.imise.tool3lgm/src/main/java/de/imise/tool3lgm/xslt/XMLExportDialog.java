package de.imise.tool3lgm.xslt;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.dialog.SearchPathDialog;
import de.imise.tool3lgm.graphtools.dialog.tools.SzenarioTableModel;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Alphabetical;
import de.imise.util.swing.dialog.ExtendedFileChooser;
import de.imise.util.swing.dialog.ProgressDialog;

/**
 * @author Thomas Rudert
 *         Dialog zum Export von Modellen ueber XSLT
 */
public class XMLExportDialog extends JDialog implements ActionListener {

    /**
     * COMMENTME
     */
    private TableModel tableModel;
    /**
     * COMMENTME
     */
    private JTable table;
    /**
     * COMMENTME
     */
    private JButton buttonAction, buttonChange, buttonDelete;
    /**
     * COMMENTME
     */
    private JCheckBox checkBoxShowResult, checkBoxSaveResult;

    /** Tabelle zur Auswahl der Teilmodelle */
    private JTable szenarioTable;

    /** die GDCollection, auf der die Exporte ausgeführt werden */
    private GDCollection collection;

    private final XSLTResourceHandler xsltResourceHandler;

    /* --- swing anfang --- */

    /**
     * Konstruktor
     * 
     * @see java.awt.Window#Window(Frame)
     * @param owner, besitzendes Fenster des Dialogs
     * @param collection, die GDCollection, auf der die Exporte ausgeführt werden
     */
    public XMLExportDialog(final Frame owner, final GDCollection collection) {
        /* Besitzer, Titel, modal anzeigen */
        super(owner, Tool3lgmConstants.getResString("trans_title"), false);
        xsltResourceHandler = new XSLTResourceHandler();

        //da evtl. viele Verzeichnisse nach Scripten durchsucht werden müssen, einen Fortschrittsdialog zeigen
        ProgressDialog progressDialog = new ProgressDialog(owner);
        progressDialog.setStatusLabelText(Tool3lgmConstants.getResString("trans_load_scripts"));

        this.collection = collection;

        getContentPane().setLayout(new BorderLayout(0, 10));

        String[] header = new String[5];
        header[0] = Tool3lgmConstants.getResString("trans_file");
        header[1] = Tool3lgmConstants.getResString("trans_name");
        header[2] = Tool3lgmConstants.getResString("trans_type");
        header[3] = Tool3lgmConstants.getResString("trans_des");
        header[4] = Tool3lgmConstants.getResString("trans_author");

        //Kopie der Standardscripte holen (die Kopie macht Tool3lgmConstants)
        ArrayList<XSLTScript> scripts = xsltResourceHandler.getStandardScripts();
        scripts.addAll(XSLTFileHandler.getXSLTScripts(UserProperties.getXSLSearchDirs()));
        Alphabetical.sort(scripts);
        tableModel = new TableModel(scripts);

        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(final int arg0, final int arg1) {
                return false;
            }

        };

        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        buttonPanel.setPreferredSize(new Dimension(400, 100));

        szenarioTable = new JTable(new SzenarioTableModel(collection, Tool3lgmConstants.getResString("labelInclude")));
        ((SzenarioTableModel) szenarioTable.getModel()).selectAll();
        szenarioTable.setSelectionBackground(table.getBackground());
        szenarioTable.setSelectionForeground(table.getForeground());
        szenarioTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        szenarioTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        szenarioTable.getColumnModel().getColumn(2).setPreferredWidth(500);
        szenarioTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JPanel szenarioPanel = new JPanel(new BorderLayout());
        szenarioPanel.setPreferredSize(new Dimension(400, 120));
        szenarioPanel.add(new JLabel(Tool3lgmConstants.getResString("labelSource") + collection.getName(), JLabel.CENTER), BorderLayout.NORTH);
        szenarioPanel.add(new JLabel(Tool3lgmConstants.getResString("labelXSLTExportSzenario"), JLabel.CENTER), BorderLayout.SOUTH);
        szenarioPanel.add(new JScrollPane(szenarioTable), BorderLayout.CENTER);
        getContentPane().add(szenarioPanel, BorderLayout.NORTH);

        JPanel scriptPanel = new JPanel(new BorderLayout());
        scriptPanel.add(new JLabel(Tool3lgmConstants.getResString("labelXSLScript"), JLabel.CENTER), BorderLayout.NORTH);
        scriptPanel.add(new JLabel(Tool3lgmConstants.getResString("labelScriptSelection"), JLabel.CENTER), BorderLayout.SOUTH);
        scriptPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        getContentPane().add(scriptPanel, BorderLayout.CENTER);

        JPanel buttonFramePanel = new JPanel();
        buttonFramePanel.add(buttonPanel);
        buttonFramePanel.add(new JLabel());
        getContentPane().add(buttonFramePanel, BorderLayout.SOUTH);
        buttonAction = newButton("trans_action");
        buttonChange = newButton("trans_change");
        buttonDelete = newButton("trans_delete");
        checkBoxShowResult = new JCheckBox(Tool3lgmConstants.getResString("trans_browser"), true);
        checkBoxSaveResult = new JCheckBox(Tool3lgmConstants.getResString("trans_save"), false);
        buttonPanel.add(buttonAction);
        buttonPanel.add(checkBoxShowResult);
        buttonPanel.add(newButton("trans_new"));
        buttonPanel.add(checkBoxSaveResult);
        buttonPanel.add(buttonChange);
        buttonPanel.add(newButton("trans_path"));
        buttonPanel.add(buttonDelete);
        buttonPanel.add(newButton("trans_close"));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(500);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        buttonAction.setEnabled(false);
        buttonChange.setEnabled(false);
        buttonDelete.setEnabled(false);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (table.getSelectedRow() < 0) {
                    buttonAction.setEnabled(false);
                    buttonChange.setEnabled(false);
                    buttonDelete.setEnabled(false);
                    return;
                }
                if (tableModel.getScript(table.getSelectedRow()).isReadOnly()) {
                    buttonDelete.setEnabled(false);
                } else {
                    buttonDelete.setEnabled(true);
                }
                buttonAction.setEnabled(true);
                buttonChange.setEnabled(true);
            }
        });

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        setSize(dim.width < 1024 ? dim.width : 1024, dim.height < 768 ? dim.height : 768);
        setResizable(true);

        progressDialog.dispose();

        super.setVisible(true);

    }

    /**
     * erzeugt einen neuen Button, und fuegt den Dialog als ActionListener zum
     * Button hinzu
     * 
     * @param text, Schluessel fuer Resourcenpaket fuer die Beschriftung des
     *            Buttons; text wird zusaetzlich als ActionCommand gesetzt
     * @return JButton der erzeugte Button
     */
    private JButton newButton(final String text) {
        JButton button = new JButton(Tool3lgmConstants.getResString(text));
        button.addActionListener(this);
        button.setActionCommand(text);
        return button;
    }

    /**
     * Abfangen der ActionEvents und Behandlung dieser
     * 
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (e.getActionCommand().equals("trans_path")) {
            SearchPathDialog pathDialog = new SearchPathDialog(this, UserProperties.getXSLSearchDirs());
            pathDialog.setVisible(true);
            updateTable();
        }

        if (e.getActionCommand().equals("trans_new")) {
            new XSLTEditor(this, getDefaultXSLFile()).setVisible(true);
            updateTable();
        }

        if (e.getActionCommand().equals("trans_change") && selectedRow >= 0) {
            if (tableModel.getScript(selectedRow).isReadOnly()) {
                try {
                    new XSLTEditor(this, tableModel.getScript(selectedRow).openStream()).setVisible(true);
                } catch (IOException exp) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
                }
            } else {
                new XSLTEditor(this, tableModel.getScript(selectedRow).getFile()).setVisible(true);
            }
        }

        if (e.getActionCommand().equals("trans_delete") && selectedRow >= 0) {
            if (JOptionPane.showConfirmDialog(this, Tool3lgmConstants.getResString("quest_del") + "\n(" + tableModel.getValueAt(selectedRow, 0).toString() + ")", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                tableModel.getScript(selectedRow).getFile().delete();
                tableModel.removeRow(table.getSelectedRow());
            }
        }

        if (e.getActionCommand().equals("trans_action") && selectedRow >= 0) {
            Szenario[] selectedSzenarios = ((SzenarioTableModel) szenarioTable.getModel()).getSelectedSzenarios();
            if (selectedSzenarios.length == 0) {
                return;
            }

            String fileString = Tool3lgmConstants.TEMP_PATH + "temp_3lgm_export_file.html";
            if (checkBoxSaveResult.isSelected()) {
                String fileExtension = tableModel.getValueAt(selectedRow, 2).toString().trim();
                String fileBaseName = Tool3lgmConstants.getResString("export");
                String scriptNumber = tableModel.getValueAt(selectedRow, 0).toString().trim();
                int indexOfExtension = scriptNumber.indexOf(".xsl");
                if (indexOfExtension >= 0) {
                    scriptNumber = scriptNumber.substring(0, indexOfExtension);
                }
                File file = new File(fileBaseName + "_" + scriptNumber + "." + fileExtension);
                int fileIndex = 1;
                if (file.exists()) {
                    do {
                        file = new File(fileBaseName + "_" + scriptNumber + "_" + fileIndex++ + "." + fileExtension);
                    } while (file.exists());
                }

                ExtendedFileChooser saveDialog = new ExtendedFileChooser(XMLExportDialog.class);
                saveDialog.setSelectedFile(file);
                if (saveDialog.showSaveDialog(this, false, new FileNameExtensionFilter("*." + fileExtension, fileExtension)) != ExtendedFileChooser.APPROVE_OPTION) {
                    return;
                }
                file = saveDialog.getSelectedFile();
                fileString = file.toString();
            }

            Tool3lgm.tool.showProgressDialog();
            Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("trans_title"));

            File tempXMLFile = new File(Tool3lgmConstants.TEMP_PATH, "temporary_XMLFile_for_XSLT-Export.xml");

            if (selectedSzenarios.length == collection.getSzenarioCount()) {
                collection.exportModel(tempXMLFile);
            } else {
                collection.exportSzenarios(selectedSzenarios, tempXMLFile);
            }

            try {
                XMLTransformer.transform(tableModel.getScript(selectedRow).openStream(), tableModel.getScript(selectedRow).getSource(), tempXMLFile, fileString);
                if (checkBoxShowResult.isSelected()) {
                    Desktop.getDesktop().browse(new File(fileString).toURI());
                }
            } catch (IOException exp) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein") + "\n" + exp.getMessage() + "\n" + exp.toString(), exp);
            } catch (Exception exp) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein") + "\n" + exp.getMessage() + "\n" + exp.toString(), exp);
            }

            tempXMLFile.delete();
            Tool3lgm.tool.closeProgressDialog();
        }

        if (e.getActionCommand().equals("trans_close")) {
            dispose();
        }

    }

    /* --- swing ende --- */

    /* --- tabelle anfang --- */

    /**
     * aktualisiert die Tabellen Einträge
     */
    protected void updateTable() {
        tableModel.clear();
        tableModel.addScripts(xsltResourceHandler.getStandardScripts());
        tableModel.addScripts(XSLTFileHandler.getXSLTScripts(UserProperties.getXSLSearchDirs()));
        tableModel.fireTableDataChanged();
    }

    /* --- tabelle ende --- */

    private static final String getDefaultXSLFile() {
        //total ineffizient, wird aber nicht sooft aufgerufen. Daher kann man auf das 
        //Zusammenbauen per StringBuilder verzichten
        return "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n\n" + "<!--name: name -->\n" + "<!--type: doctype -->\n" + "<!--description: beschreibung -->\n" + "<!--author: autor -->\n\n" + "<xsl:stylesheet\n"
                + "\txmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" + "\txmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" + "\txmlns:str=\"http://whatever\"\n" + "\tversion=\"2.0\"\n" + "\texclude-result-prefixes=\"str\">\n\n" +

                "<!-- Funktion zum Ersetzen aller Vorkommen eines Teilstrings durch einen anderen Teilstring in einer Zeichenkette -->\n" + "<xsl:function name=\"str:replaceSubstring\" as=\"xs:string\">\n"
                + "\t<xsl:param name=\"inputString\" as=\"xs:string\"/>\n" + "\t<xsl:param name=\"oldSubstring\" as=\"xs:string\"/>\n" + "\t<xsl:param name=\"newSubstring\" as=\"xs:string\"/>\n" + "\t<xsl:choose>\n"
                + "\t\t<xsl:when test=\"contains($inputString, $oldSubstring)\">\n"
                + "\t\t\t<xsl:sequence select=\"str:replaceSubstring(concat(substring-before($inputString, $oldSubstring), $newSubstring, substring-after($inputString, $oldSubstring)), $oldSubstring, $newSubstring)\" />\n" + "\t\t</xsl:when>\n"
                + "\t\t<xsl:otherwise>\n" + "\t\t\t<xsl:sequence select=\"$inputString\" />\n" + "\t\t</xsl:otherwise>\n" + "\t</xsl:choose>\n" + "</xsl:function>\n\n" +

                "<!-- Funktion zum entfernen der manuellen Zeilenumbrueche '\\-' aus einer Zeichenkette -->\n" + "<xsl:function name=\"str:removeLineBreak\" as=\"xs:string\">\n" + "\t<xsl:param name=\"string\" as=\"xs:string\" />\n"
                + "\t<xsl:sequence select=\"str:replaceSubstring($string, '\\-', '')\" />\n" + "</xsl:function>\n\n" +

                "<!-- bei der Ausgabe muss 'disable-output-escaping' auf 'yes' gesetzt werden -->\n" + "<xsl:variable name=\"zeilenumbruch\" select=\"'&lt;br/&gt;'\" />\n\n" +

                "<xsl:template match=\"/\">\n\n\n" + "</xsl:template>\n\n" + "</xsl:stylesheet>\n";
    }

    private class TableModel extends DefaultTableModel {
        private final ArrayList<XSLTScript> xslScripts;

        @Override
        public int getRowCount() {
            return xslScripts == null ? 0 : xslScripts.size();
        }

        /**
		 * 
		 */
        public void clear() {
            xslScripts.clear();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        public TableModel(final ArrayList<XSLTScript> xslScripts) {
            super();
            this.xslScripts = xslScripts;
        }

        public void addScripts(final ArrayList<XSLTScript> newXSLScripts) {
            for (int i = 0; i < newXSLScripts.size(); i++) {
                if (!xslScripts.contains(newXSLScripts.get(i))) {
                    xslScripts.add(newXSLScripts.get(i));
                }
            }
        }

        @Override
        public String getColumnName(final int column) {
            switch (column) {
            case 0:
                return Tool3lgmConstants.getResString("trans_file");
            case 1:
                return Tool3lgmConstants.getResString("trans_name");
            case 2:
                return Tool3lgmConstants.getResString("trans_type");
            case 3:
                return Tool3lgmConstants.getResString("trans_des");
            case 4:
                return Tool3lgmConstants.getResString("trans_author");
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
                return xslScripts.get(row).getSource();
            case 1:
                return xslScripts.get(row).getName();
            case 2:
                return xslScripts.get(row).getType();
            case 3:
                return xslScripts.get(row).getDescription();
            case 4:
                return xslScripts.get(row).getAuthor();
            }
            return null;
        }

        @Override
        public Class<?> getColumnClass(final int col) {
            if (col < 0 || col > 4) {
                return null;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(final int row, final int col) {
            return false;
        }

        @Override
        public void setValueAt(final Object aValue, final int row, final int column) {
        }

        /**
         * @param row
         * @return
         */
        public XSLTScript getScript(final int row) {
            if (row < 0 || row > xslScripts.size() - 1) {
                return null;
            }
            return xslScripts.get(row);
        }

        @Override
        public void removeRow(final int row) {
            super.removeRow(row);
            xslScripts.remove(row);
        }
    }

    /**
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        try {
            finalize();
        } catch (Throwable t) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), t);
        }
    }

}