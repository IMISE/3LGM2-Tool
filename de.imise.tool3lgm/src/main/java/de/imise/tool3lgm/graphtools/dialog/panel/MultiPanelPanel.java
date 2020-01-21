package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.border.Border;

import de.imise.tool3lgm.graphtools.dialog.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;

/**
 * Panel, das mehrere andere Panels aufnehmen kann.
 *
 * @author AXS (21.01.2020)
 */
public class MultiPanelPanel extends ElementDialogPanel {

    /** Added sub panels in the order in which they were added */
    private final List<AbstractPathConnectionPanel> panels = new ArrayList<>();

    /** GridBagConstraints used to place the sub panel components in this panel */
    protected final GridBagConstraints gbc = new GridBagConstraints();

    /** Row for the {@link GridBagConstraints} to add the components of the next panel */
    protected int gridy = 0;

    /**
     * Bleibt <code>true</code>, wenn keine Unterklasse von {@link AbstractElementPropertyDialog} das Panel erweitert hat, sondern er im
     * Ausgangszustand (Name + Beschreibung) geblieben ist.
     */
    protected boolean isUnchangedDefaultPanel = true;

    /**
     * @param dialog
     */
    public MultiPanelPanel(final AbstractElementPropertyDialog dialog) {
        this(dialog, (String) null);
    }

    /**
     * @param dialog
     * @param name
     */
    public MultiPanelPanel(final AbstractElementPropertyDialog dialog, final String name) {
        super(dialog, name);
        setLayout(new GridBagLayout());
    }

    public final void addDescriptedSingleConnectionPanel(final SimpleMetaPath simpleMetaPath) {
        addDescriptedSingleConnectionPanel(LABEL_END_ELEMENT_TYPE, simpleMetaPath);
    }

    public final void addDescriptedSingleConnectionPanel(final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        addSubPanel(new DescriptedSingleConnectionPanel(dialog, panelLabelOption, simpleMetaPath));
    }

    public final void addSingleConnectionPanel(final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        addSubPanel(new SingleConnectionPanel(dialog, panelLabelOption, simpleMetaPath));
    }

    public final void addListPanel(final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        addSubPanel(new PathConnectionLeafPanel(dialog, panelLabelOption, 4, simpleMetaPath));
    }

    private final void addSubPanel(final AbstractPathConnectionPanel panel) {
        panels.add(panel);
        if (panel instanceof DescriptedSingleConnectionPanel) {
            addSeparator();
            JLabel westLabel = panel.getWestLabel();
            DescriptedSingleConnectionPanel descriptedPanel = (DescriptedSingleConnectionPanel) panel;
            Border topBorder = BorderFactory.createEmptyBorder(3, 0, 0, 0);
            westLabel.setBorder(topBorder);
            descriptedPanel.setBorder(topBorder);
            gridy = descriptedPanel.addMe(this, gbc, gridy);
        } else {
            add(this, panel.getWestLabel(), gbc, 0, gridy, 1, 1);
            add(this, panel, gbc, 1, gridy++, 1, 1);
        }
        isUnchangedDefaultPanel = false;
    }

    public final void addSeparator() {
        add(this, new JSeparator(), gbc, 0, gridy++, 2, 1);
        isUnchangedDefaultPanel = false;
    }

    @Override
    public void update() {
        for (int m = 0; m < panels.size(); m++) {
            panels.get(m).update();
        }
    }

    @Override
    public void commit() {
        for (int m = 0; m < panels.size(); m++) {
            panels.get(m).commit();
        }
    }

    /**
     * @return <code>true</code>, wenn keine Unterklasse von {@link ElementPropertyDialog} das Panel erweitert hat, sondern er im Ausgangszustand
     *         (Name + Beschreibung) geblieben ist.
     */
    public final boolean isUnchangedDefaultPanel() {
        return isUnchangedDefaultPanel;
    }

}
