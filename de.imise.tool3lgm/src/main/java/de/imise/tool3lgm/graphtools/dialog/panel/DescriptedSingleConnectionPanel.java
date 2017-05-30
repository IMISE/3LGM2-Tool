package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author AXS
 *         Dieses Panel ist ein {@link SingleConnectionPanel}, das zusätzlich dazu noch ein Beschreibungfeld
 *         für das searchElement zur Verfügung stellt.
 */
public class DescriptedSingleConnectionPanel extends SingleConnectionPanel {

    private final ExtendedTextPane descriptionTextPane = new ExtendedTextPane();

    private final JLabel descriptionWestLabel = new JLabel(Tool3lgmConstants.getResString("description"));

    /**
     * @param dialog
     * @param edgeClasses
     */
    public DescriptedSingleConnectionPanel(final ElementPropertyDialog dialog, final Class<? extends Kante>... edgeClasses) {
        this(dialog, false, edgeClasses);
    }

    /**
     * @param dialog
     * @param labelLastEdgeName wenn <code>true</code> dann wird ans WestLabel statt des Namens der searchElementClass der Name der
     *            letzten Kante aus den edgeClasses geschrieben.
     * @param edgeClasses
     */
    public DescriptedSingleConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        super(dialog, labelLastEdgeName, edgeClasses);
        descriptionTextPane.setEditable(false);
    }

    public ExtendedTextPane getDescriptionTextPane() {
        return descriptionTextPane;
    }

    public JLabel getDescriptionWestLabel() {
        return descriptionWestLabel;
    }

    @Override
    protected final void init() {
        super.init();
        updateDescription();
    }

    private void updateDescription() {
        if (descriptionTextPane != null) {
            StringBuilder sb = new StringBuilder();
            List<ElementContainer> connected = getConnectedContainer();
            if (connected.size() > 0) {
                ElementContainer kc = connected.get(0);
                sb.append(kc.getElement().getDescription());
                for (int i = 1; i < connected.size(); i++) {
                    ElementContainer lc = connected.get(i);
                    sb.append("\n\n").append(lc.getElement().getDescription());
                }
            }
            descriptionTextPane.setText(sb.toString());
            descriptionTextPane.setCaretPosition(0);
        }
    }

    public int addMe(final Container parent, final GridBagConstraints gbc, final int gridy) {
        int newGridy = gridy;
        gbc.fill = GridBagConstraints.NONE;
        add(parent, westLabel, gbc, 0, newGridy, 1, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(parent, connectedElementViewComponent, gbc, 1, newGridy++, 1, 1);
        gbc.fill = GridBagConstraints.NONE;
        add(parent, getDescriptionWestLabel(), gbc, 0, newGridy, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 1;
        add(parent, new JScrollPane(getDescriptionTextPane()), gbc, 1, newGridy++, 1, 1);
        gbc.weightx = 0;
        gbc.weighty = 0;
        return newGridy;
    }

    public Component addSelf() {
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(new GridBagLayout());
        addMe(this, gbc, 0);
        return this;
    }

}