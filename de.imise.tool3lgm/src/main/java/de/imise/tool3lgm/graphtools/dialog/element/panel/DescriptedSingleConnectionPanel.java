package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.util.htmlxml.ParseSaveStringHandler;
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
    public DescriptedSingleConnectionPanel(final AbstractElementPropertyDialog dialog, final AbstractMetaPath metaPath) {
        this(dialog, LABEL_END_ELEMENT_TYPE, metaPath);
    }

    /**
     * @param dialog
     * @param panelLabelOption Das Label kann folgende Werte annehmen:
     *            <ul>
     *            <li>{@link PanelLabelOption#LABEL_END_ELEMENT_TYPE} = Anzeigename der EndElement-Art des MetaPfades</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_ELEMENT_NAME} = Anzeigename der Element-Art der letzten Kante des MetaPfades</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_CONNECTION_NAME} = Anzeigename der gerichteten Verbindung der letzten Kante des
     *            MetaPfades</li>
     *            </ul>
     * @param metaPath
     */
    public DescriptedSingleConnectionPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption panelLabelOption, final AbstractMetaPath metaPath) {
        super(dialog, panelLabelOption, metaPath);
    }

    @Override
    public void update() {
        super.update();
        boolean editable = !dialog.isInfoDialog() && connectedElement != null;
        if (descriptionTextPane != null) {
            descriptionTextPane.setEditable(editable);
            updateDescription();
        }
    }

    private void updateDescription() {
        if (connectedElement != null) {
            olddescrip = connectedElement.getDescription();
            descriptionTextPane.setText(olddescrip);
        } else {
            descriptionTextPane.setText("");
        }
        descriptionTextPane.setCaretPosition(0);
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
            GraphDocument mainDoc = getMainDoc();
            int pid = getTransactionID();
            mainDoc.setDescription(connectedElement, ParseSaveStringHandler.getParseSaveString(newDescription), pid);
        }
        connectedElement.refreshText();
    }

    public int addMe(final Container parent, final GridBagConstraints gbc, final int gridy) {
        int newGridy = gridy;
        gbc.anchor = GridBagConstraints.WEST;
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