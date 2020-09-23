package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.util.htmlxml.ParseSaveStringHandler;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author N.N., AXS (4/2017)
 * @create Long time ago
 */
public final class DescripPanel extends MultiPanelElementDialogPanel /* implements DocumentListener */ {

    /** This TextPane shows the editable name of the modelElement of the dialog */
    private final LimitedSizeScrollTextPane nameTextPane;

    /** This TextPane shows the editable name of the modelElement of the dialog */
    private final ExtendedTextPane descriptionTextPane;

    /** Name des ModelElements beim letzten Update des Dialoges */
    private String lastName = null;

    /** Beschreibung des ModelElements beim letzten Update des Dialoges */
    private String lastDescription = null;

    /**
     * @param dialog
     */
    public DescripPanel(final AbstractElementPropertyDialog dialog) {
        super(dialog);

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
            MetaModel metaModel = getMetaModel();
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
            ElementaryMetaPathHandler emph = metaModel.getElementaryMetaPathHandler();
            ElementaryMetaPath edgeToStartElementMetaPath = emph.getEdgeToStartElementMetaPath(edgeClass);
            ElementaryMetaPath edgeToEndElementMetaPath = emph.getEdgeToEndElementMetaPath(edgeClass);
            SimpleMetaPath edgeToStartElementSimpleMetaPath = new SimpleMetaPath(edgeToStartElementMetaPath);
            SimpleMetaPath edgeToEndElementSimpleMetaPath = new SimpleMetaPath(edgeToEndElementMetaPath);
            addSingleConnectionPanel(LABEL_END_ELEMENT_TYPE, edgeToStartElementSimpleMetaPath);
            addSingleConnectionPanel(LABEL_END_ELEMENT_TYPE, edgeToEndElementSimpleMetaPath);
        }
    }

    @Override
    public void addLGMDragNDropPanel(final LGMDragNDropPanel panel) {
        //on the description panel all LGMDragNDropPanel should be shown without the
        //right side -> show it as an ordinary was label panel
        super.addWestLabelPanel(panel);
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
        super.update();
    }

    @Override
    public final void commit() {
        ModelElement me = getModelElement();
        String newName = nameTextPane.getText();
        // nur wenn der Name explizit geändert wurde, dann auch den Namen in einer Transaktion ändern
        String name = me.getName();
        if (newName != null && !newName.equals(name)) {
            GraphDocument mainDoc = getMainDoc();
            int pid = getTransactionID();
            mainDoc.setName(me, newName, pid);
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
            GraphDocument mainDoc = getMainDoc();
            int pid = getTransactionID();
            mainDoc.setDescription(me, ParseSaveStringHandler.getParseSaveString(newDescrip), pid);
        }
        me.refreshText();
        super.commit();
    }

}