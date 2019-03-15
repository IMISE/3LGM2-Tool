package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author AXS
 *         Dieses Panel ist ein {@link SingleConnectionPanel}, das zusätzlich dazu noch ein Beschreibungfeld
 *         für das searchElement zur Verfügung stellt.
 */
public class DescriptedSingleConnectionPanel extends SingleConnectionPanel {

    private final ExtendedTextPane descriptionTextPane = new ExtendedTextPane();

    private final JLabel descriptionWestLabel = new JLabel(getResString("description"));

    /** Cache zur Speicherung, ob die Beschreibung des verbundenen Elementes geändert wurde */
    private String olddescrip = "";

    /**
     * @param dialog
     * @param simpleMetaPath
     */
    public DescriptedSingleConnectionPanel(final ElementPropertyDialog dialog, final SimpleMetaPath simpleMetaPath) {
        this(dialog, false, simpleMetaPath);
    }

    /**
     * @param dialog
     * @param labelLastEdgeName wenn <code>true</code> dann wird ans WestLabel statt des Namens der searchElementClass der Name der
     *            letzten Edge aus den edgeClasses geschrieben.
     * @param simpleMetaPath
     */
    public DescriptedSingleConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        super(dialog, labelLastEdgeName, false, simpleMetaPath);
    }

    @Override
    protected final void init() {
        super.init();
        updateDescription();
        if (descriptionTextPane != null) {
            descriptionTextPane.setEditable(connectedElement != null);
        }
    }

    private void updateDescription() {
        if (descriptionTextPane != null) {
            if (connectedElement != null) {
                olddescrip = connectedElement.getDescription();
                descriptionTextPane.setText(olddescrip);
            } else {
                descriptionTextPane.setText("");
            }
            descriptionTextPane.setCaretPosition(0);
        }
    }

    @Override
    public void commit() {
        super.commit();
        // Ist null, wenn kein verbundenes Element vorhanden ist -> Beschreibung nicht änderbar
        if (connectedElement == null) {
            return;
        }
        String newDescription = descriptionTextPane.getText();
        if (newDescription != null && !olddescrip.equals(newDescription)) {
            doc.setDescription(connectedElement, GraphDocument.getParseSaveString(newDescription), dialog.getTransactionID());
        }
        connectedElement.refreshText();
    }

    public int addMe(final Container parent, final GridBagConstraints gbc, final int gridy) {
        int newGridy = gridy;
        gbc.fill = GridBagConstraints.NONE;
        add(parent, westLabel, gbc, 0, newGridy, 1, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(parent, connectedElementViewComponent, gbc, 1, newGridy++, 1, 1);
        gbc.fill = GridBagConstraints.NONE;
        add(parent, descriptionWestLabel, gbc, 0, newGridy, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 1;
        add(parent, new JScrollPane(descriptionTextPane), gbc, 1, newGridy++, 1, 1);
        gbc.weightx = 0;
        gbc.weighty = 0;
        return newGridy;
    }

    public Component addSelf() {
        GridBagConstraints gbc = new GridBagConstraints();
        //wenn das Panel als alleine steht, dann soll vor dem Westlabel nur "Bezeichnung" stehen
        westLabel.setText(getResString("bez"));
        setLayout(new GridBagLayout());
        addMe(this, gbc, 0);
        return this;
    }

}