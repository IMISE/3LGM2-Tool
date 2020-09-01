package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import de.imise.tool3lgm.graphtools.dialog.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * Panel, which can hold several other panels.
 *
 * @author AXS (21.01.2020)
 */
public class MultiPanelElementDialogPanel extends ElementDialogPanel implements ActionListener {

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

    /** Liste mit allen Buttons, die die geaddeten Panels jeweils über ihre {@link #getPanelButton()} Methode zurück liefern. */
    private final List<JButton> panelButtons = new ArrayList<>();

    /**
     * The button this panel accumulates of the {@link #getPanelButton()} result buttons of the
     * panels in this multi panel.
     */
    private JButton panelButton;

    /**
     * Panel, which can hold several other panels.
     *
     * @param dialog dialog the dialog which contains this panel
     */
    public MultiPanelElementDialogPanel(final AbstractElementPropertyDialog dialog) {
        this(dialog, (String) null);
    }

    /**
     * Panel, which can hold several other panels.
     *
     * @param dialog the dialog which contains this panel
     * @param name displayed panel name on the tab
     */
    public MultiPanelElementDialogPanel(final AbstractElementPropertyDialog dialog, final String name) {
        super(dialog, name);
        setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.BOTH;
    }

    public final void addDescriptedSingleConnectionPanel(final SimpleMetaPath simpleMetaPath) {
        addDescriptedSingleConnectionPanel(LABEL_END_ELEMENT_TYPE, simpleMetaPath);
    }

    public final void addDescriptedSingleConnectionPanel(final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        addPanel(new DescriptedSingleConnectionPanel(dialog, panelLabelOption, simpleMetaPath));
    }

    public final void addSingleConnectionPanel(final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        addPanel(new SingleConnectionPanel(dialog, panelLabelOption, simpleMetaPath));
    }

    public final void addListPanel(final PanelLabelOption westLabelOption, final SimpleMetaPath simpleMetaPath) {
        addPanel(new PathConnectionLeafPanel(dialog, westLabelOption, westLabelOption, 4, simpleMetaPath));//die titleLabelOption ist egal -> auch einfach die westLabelOption übergeben
    }

    public final void addPanel(final AbstractPathConnectionPanel panel) {
        //muss sein, weil ElementPropertyDialog#getAddableEdgePanel(...) null-Panels
        //erzeugt, wenn die Kante oder der Pfad nur im Expert-Model sichtbar ist und
        //dieser gerade aus ist
        if (panel == null) {
            return;
        }
        panels.add(panel);
        if (panel instanceof DescriptedSingleConnectionPanel) {
            addDescriptedSingleConnectionPanel((DescriptedSingleConnectionPanel) panel);
        } else if (panel instanceof MutipleCompositionPanel) {
            addMutipleCompositionPanel((MutipleCompositionPanel) panel);
        } else if (panel instanceof LGMDragNDropPanel) {
            addLGMDragNDropPanel((LGMDragNDropPanel) panel);
        } else {
            addWestLabelPanel(panel);
        }
        //nur LGMDragNDropPanels, die editierbar sind, haben eine rechte Seite und diesen Button
        JButton panelButton = panel.getPanelButton();
        if (panelButton != null) {
            addPanelButton(panelButton);
        }

        isUnchangedDefaultPanel = false;
    }

    public void addDescriptedSingleConnectionPanel(final DescriptedSingleConnectionPanel panel) {
        addSeparator();
        JLabel westLabel = panel.getWestLabel();
        Border topBorder = BorderFactory.createEmptyBorder(3, 0, 0, 0);
        westLabel.setBorder(topBorder);
        panel.setBorder(topBorder);
        gridy = panel.addMe(this, gbc, gridy);
    }

    public void addMutipleCompositionPanel(final MutipleCompositionPanel panel) {
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 1;
        gbc.weightx = 1;
        TitledBorder panelBorder = BorderFactory.createTitledBorder(panel.getName());
        panel.setBorder(panelBorder);
        add(this, panel, gbc, 0, gridy++, 1, 1);
    }

    public void addLGMDragNDropPanel(final LGMDragNDropPanel panel) {
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 1;
        gbc.weightx = 1;
        add(this, panel, gbc, 0, gridy++, 2, 1); //2 breit, falls man das mal auf einem DescripPanel hinzufügen möchte (bsiher nicht ausprobiert)
    }

    public void addWestLabelPanel(final AbstractPathConnectionPanel panel) {
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        JLabel westLabel = panel.getWestLabel();
        add(this, westLabel, gbc, 0, gridy, 1, 1);
        gbc.weightx = 1;
        add(this, panel, gbc, 1, gridy++, 1, 1);
    }

    public final void addSeparator() {
        Insets oldInsets = gbc.insets;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(this, new JSeparator(), gbc, 0, gridy++, 2, 1);
        gbc.insets = oldInsets;
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

    /**
     * @param panelButton
     */
    private void addPanelButton(final JButton panelButton) {
        //first button is the only that will be displayed. If this button
        //is clicked, the action listener clicks all the other buttons.
        panelButtons.add(panelButton);
        if (panelButtons.size() == 1) {
            this.panelButton = panelButton;
            panelButton.addActionListener(this);
        } else {
            Container buttonParent = panelButton.getParent();
            if (buttonParent != null) {
                buttonParent.remove(panelButton);
            }
        }

    }

    @Override
    public JButton getPanelButton() {
        return panelButton;
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        if (e.getSource() == panelButton) {
            for (int i = 1; i < panelButtons.size(); i++) {
                JButton hiddenPanelButton = panelButtons.get(i);
                hiddenPanelButton.doClick();
            }
        }
    }

}
