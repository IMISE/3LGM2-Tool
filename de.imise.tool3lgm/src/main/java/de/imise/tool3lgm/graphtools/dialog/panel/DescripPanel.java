package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.Border;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author N.N., AXS (4/2017)
 * @create Long time ago
 */
public class DescripPanel extends ElementDialogPanel /* implements DocumentListener */ {

    private final ExtendedTextPane descriptionTextPane;

    private final LimitedSizeScrollTextPane nameTextPane;

    private final List<ElementDialogPanel> panels = new ArrayList<>();

    private final GridBagConstraints gbc = new GridBagConstraints();

    private int gridy = 0;

    /** Name des ModelElements beim letzten Update des Dialoges */
    private String lastName = null;

    /** Beschreibung des ModelElements beim letzten Update des Dialoges */
    private String lastDescription = null;
    /**
     * Bleibt <code>true</code>, wenn keine Unterklasse von {@link ElementPropertyDialog} das Panel erweitert hat, sondern er im Ausgangszustand (Name
     * + Beschreibung) geblieben ist.
     */
    private boolean isUnchangedDefaultPanel = true;

    /**
     * @param dialog
     */
    public DescripPanel(final ElementPropertyDialog dialog) {
        super(dialog);

        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 0, 1, 3);

        // Bezeichung und Eingabefeld
        JLabel label2 = new JLabel(getResString("bez"));
        add(this, label2, gbc, 0, gridy, 1, 1);
        boolean isEditableDialog = !dialog.isInfoDialog();
        nameTextPane = new LimitedSizeScrollTextPane(4);

        //Name editable?
        boolean editableName = isEditableDialog;
        if (editableName) {
            MetaModel metaModel = dialog.getMetaModel();
            ModelElement me = getModelElement();
            Class<? extends ModelElement> elementClass = me.getClass();
            editableName = !metaModel.isGenerateName(elementClass);
        }
        nameTextPane.setEditable(isEditableDialog);

        gbc.weightx = 1;
        add(this, nameTextPane, gbc, 1, gridy++, 1, 1);
        gbc.weightx = 0;

        // Beschreibung und TextPane
        JLabel label = new JLabel(getResString("description"));
        add(this, label, gbc, 0, gridy, 1, 1);

        gbc.weighty = 1;
        descriptionTextPane = new ExtendedTextPane();
        add(this, new JScrollPane(descriptionTextPane), gbc, 1, gridy++, 1, 1);
        descriptionTextPane.setEditable(isEditableDialog);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        addEdgeStartEndPanel();
    }

    /**
     *
     */
    private void addEdgeStartEndPanel() {
        ModelElement me = getModelElement();
        if (me instanceof Edge) {
            Class<? extends ModelElement> meClass = me.getClass();
            Class<? extends Edge> edgeClass = meClass.asSubclass(Edge.class);
            MetaModel metaModel = getMetaModel();
            ElementaryMetaPath edgeToStartElementMetaPath = ElementaryMetaPath.createEdgeToStartElementMetaPath(metaModel, edgeClass);
            ElementaryMetaPath edgeToEndElementMetaPath = ElementaryMetaPath.createEdgeToEndElementMetaPath(metaModel, edgeClass);
            SimpleMetaPath edgeToStartElementSimpleMetaPath = new SimpleMetaPath(edgeToStartElementMetaPath);
            SimpleMetaPath edgeToEndElementSimpleMetaPath = new SimpleMetaPath(edgeToEndElementMetaPath);
            addSingleConnectionPanel(false, false, edgeToStartElementSimpleMetaPath);
            addSingleConnectionPanel(false, false, edgeToEndElementSimpleMetaPath);
        }
    }

    /**
     * @return <code>true</code>, wenn keine Unterklasse von {@link ElementPropertyDialog} das Panel erweitert hat, sondern er im Ausgangszustand
     *         (Name + Beschreibung) geblieben ist.
     */
    public boolean isUnchangedDefaultPanel() {
        return isUnchangedDefaultPanel;
    }

    @Override
    public void update() {
        ModelElement me = getModelElement();
        //nur den Namen und die Beschreibung updaten, wenn sie anders sind als das, was im Textfeld steht
        //das sollte nur beim ersten Update nach dem Init der Fall sein oder falls diese Felder außerhalb
        //des Dialoges geändert wurden
        String name = me.getName();
        if (!name.equals(lastName)) {
            lastName = name;
            nameTextPane.setText(name);
            nameTextPane.setCaretPosition(0);
        }
        String description = me.getDescription();
        if (!description.equals(lastDescription)) {
            lastDescription = description;
            descriptionTextPane.setText(description);
            descriptionTextPane.setCaretPosition(0);
        }
        for (int m = 0; m < panels.size(); m++) {
            panels.get(m).update();
        }
    }

    public final void addDescriptedSingleConnectionPanel(final SimpleMetaPath simpleMetaPath) {
        addDescriptedSingleConnectionPanel(false, simpleMetaPath);
    }

    public final void addDescriptedSingleConnectionPanel(final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        addSubPanel(new DescriptedSingleConnectionPanel(dialog, labelLastEdgeName, simpleMetaPath));
    }

    public final void addSingleConnectionPanel(final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        addSubPanel(new SingleConnectionPanel(dialog, labelLastEdgeName, simpleMetaPath));
    }

    public final void addListPanel(final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        addSubPanel(new PathConnectionLeafPanel(dialog, labelLastEdgeName, false, 4, simpleMetaPath));
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
    public final void commit() {
        ModelElement me = getModelElement();
        String newName = nameTextPane.getText();
        // nur wenn der Name explizit geändert wurde, dann auch den Namen in einer Transaktion ändern
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
        for (int m = 0; m < panels.size(); m++) {
            panels.get(m).commit();
        }
    }

}