package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author N.N., AXS (4/2017)
 * @create Long time ago
 */
public class DescripPanel extends ElementDialogPanel implements DocumentListener {

    private final ExtendedTextPane descriptionTextPane;

    private final LimitedSizeScrollTextPane nameTextPane;

    private final ArrayList<ElementDialogPanel> panels = new ArrayList<ElementDialogPanel>();

    private final GridBagConstraints gbc = new GridBagConstraints();

    private int gridy = 0;

    /**
     * @param dialog
     */
    public DescripPanel(final ElementPropertyDialog dialog) {
        super(dialog);

        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 0, 1, 3);

        // Bezeichung und Eingabefeld
        JLabel label2 = new JLabel(Tool3lgmConstants.getResString("bez"));
        add(this, label2, gbc, 0, gridy, 1, 1);

        nameTextPane = new LimitedSizeScrollTextPane(4);
        nameTextPane.setEditable(!ModelConstants.isGenerateName(getModelElement().getClass()));
        gbc.weightx = 1;
        add(this, nameTextPane, gbc, 1, gridy++, 1, 1);
        gbc.weightx = 0;

        // Beschreibung und TextPane
        JLabel label = new JLabel(Tool3lgmConstants.getResString("description"));
        add(this, label, gbc, 0, gridy, 1, 1);

        gbc.weighty = 1;
        descriptionTextPane = new ExtendedTextPane();
        add(this, new JScrollPane(descriptionTextPane), gbc, 1, gridy++, 1, 1);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        init();
    }

    @Override
    protected void init() {
        ModelElement modelElement = getModelElement();
        nameTextPane.setText(modelElement.getName());
        nameTextPane.addDocumentListener(this); //erst nach dem initialen setText den Listener ranhängen, sonst wird gleich commit aufgerufen
        descriptionTextPane.setText(modelElement.getDescription());
        nameTextPane.setCaretPosition(0);
        descriptionTextPane.setCaretPosition(0);

    }

    public void addSingleConnectionInfoPanel(final Class<? extends Kante>... edgeClasses) {
        addSingleConnectionInfoPanel(false, edgeClasses);
    }

    public void addSingleConnectionInfoPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        addSubPanel(new SingleConnectionInfoPanel(dialog, labelLastEdgeName, edgeClasses));
    }

    public void addDescriptedSingleConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addDescriptedSingleConnectionPanel(false, edgeClasses);
    }

    public void addDescriptedSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        addSubPanel(new DescriptedSingleConnectionPanel(dialog, labelLastEdgeName, edgeClasses));
    }

    public void addSingleConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addSingleConnectionPanel(false, edgeClasses);
    }

    public void addSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        addSubPanel(new SingleConnectionPanel(dialog, labelLastEdgeName, edgeClasses));
    }

    private void addSubPanel(final AbstractPathConnectionPanel panel) {
        panels.add(panel);
        if (panel instanceof DescriptedSingleConnectionPanel) {
            addSeparator();
            JLabel westLabel = panel.getWestLabel();
            DescriptedSingleConnectionPanel descriptedPanel = (DescriptedSingleConnectionPanel) panel;
            Border topBorder = BorderFactory.createEmptyBorder(3, 0, 0, 0);
            westLabel.setBorder(topBorder);
            descriptedPanel.setBorder(topBorder);
            gbc.fill = GridBagConstraints.NONE;
            add(this, westLabel, gbc, 0, gridy, 1, 1);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(this, descriptedPanel, gbc, 1, gridy++, 1, 1);
            gbc.fill = GridBagConstraints.NONE;
            add(this, descriptedPanel.getDescriptionWestLabel(), gbc, 0, gridy, 1, 1);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;
            gbc.weightx = 1;
            add(this, new JScrollPane(descriptedPanel.getDescriptionTextPane()), gbc, 1, gridy++, 1, 1);
            gbc.weightx = 0;
            gbc.weighty = 0;
        } else {
            add(this, panel.getWestLabel(), gbc, 0, gridy, 1, 1);
            add(this, panel, gbc, 1, gridy++, 1, 1);
        }
    }

    public void addSeparator() {
        add(this, new JSeparator(), gbc, 0, gridy++, 2, 1);
    }

    @Override
    protected void showFullDialog() {
    }

    @Override
    public void commit() {
        ModelElement me = getModelElement();
        String newName = nameTextPane.getText();
        // nur wenn der Name explizit geändert wurde, dann auch den Namen in einer Transaktion
        // ändern
        String name = me.getName();
        if (newName != null && !newName.equals(name)) {
            doc.setName(me, newName, dialog.getTransactionID());
        } else {
            // wenn der Name gleich geblieben ist, kann aber trotzdem der HTML-Name in der Grafik
            // sich geändert haben, wenn in dem Dialog ein Element verknüpft wurde, das auch im
            // Namen in der Grafik angezeigt wird -> einfach ohne Transaktion in jedem Fall mal
            // setName() mit dem alten Namen für das Element aufrufen
            me.setName(name);
        }
        String newDescrip = descriptionTextPane.getText();
        String descrip = me.getDescription();
        if (newDescrip != null && !newDescrip.equals(descrip)) {
            doc.setDescription(me, GraphDocument.getParseSaveString(newDescrip), dialog.getTransactionID());
        }
        me.refreshText();
    }

    @Override
    public void update() {
        for (int m = 0; m < panels.size(); m++) {
            panels.get(m).update();
        }
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        commit();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        commit();
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        commit();
    }
}