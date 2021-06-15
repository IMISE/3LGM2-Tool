package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.userfield.definition.SubType.DUMMY_SUBTYPE;
import static de.imise.util.IDStringGenerator.getCreationTimeMedium;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.SubType;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;

/**
 * Panel das im Kopf jedes Eigenschaftsdialogs der Elemente deren Namen, ID usw.
 * anzeigt. Es zeigt die Informationen nur an und ist nicht editierbar.
 *
 * @author AXS
 * @create 27.09.2010
 */
public class ElementDialogHeaderPanel extends ElementDialogPanel {

    /**  */
    private final JLabel typeLabel;

    /**  */
    private final JLabel labelLabel;

    /**  */
    private JLabel nameLabel;

    /**  */
    private final JLabel idLabel;

    /**  */
    private JLabel submodelLabel;

    /**  */
    private JLabel subModelLabelLabel;

    /**  */
    private final JComboBox<SubType> subTypeBox;

    /**
     * @param dialog
     */
    public ElementDialogHeaderPanel(final AbstractElementPropertyDialog dialog) {
        super(dialog);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = -1;

        addWestLabel("typ", gbc);
        typeLabel = add(new JLabel(), gbc, 1, 1.0, BOTH, WEST);

        subTypeBox = initSubTypeBox();
        if (subTypeBox != null) {
            add(new JLabel(getResString("SUBTYPE") + ": "), gbc, 1, 0.0, NONE, EAST);
            add(subTypeBox, gbc, 1, 1.0, BOTH, EAST);
        }

        ModelElement me = getModelElement();
        if (me instanceof Edge) {
            addWestLabel("name", gbc);
            nameLabel = add(new JLabel(), gbc);
        }

        addWestLabel("label", gbc);
        labelLabel = add(new JLabel(), gbc);

        addWestLabel("ID", gbc);
        idLabel = add(new JLabel(), gbc);
        if (me instanceof Node) {
            subModelLabelLabel = addWestLabel("verkn_teilmodell", gbc);
            submodelLabel = add(new JLabel(), gbc);
        }
        setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 5));
        update();
    }

    /**
     * @param resKey
     * @param gbc
     */
    private JLabel addWestLabel(final String resKey, final GridBagConstraints gbc) {
        gbc.gridx = -1;
        gbc.gridy++;
        String labelText = getResString(resKey);
        labelText += ": ";
        JLabel label = new JLabel(labelText);
        add(label, gbc, 1, 0.0, NONE, WEST);
        return label;
    }

    /**
     * @param component
     * @param gbc
     */
    private <T extends JComponent> T add(final T component, final GridBagConstraints gbc) {
        return add(component, gbc, 3, 1.0, BOTH, WEST);
    }

    /**
     * @param <T>
     * @param component
     * @param gbc
     * @param gridWidth
     * @param weightx
     * @param fill
     * @param anchor
     * @return
     */
    private <T extends JComponent> T add(final T component, final GridBagConstraints gbc, final int gridWidth, final double weightx, final int fill, final int anchor) {
        gbc.gridx++;
        gbc.gridwidth = gridWidth;
        gbc.weightx = weightx;
        gbc.fill = fill;
        gbc.anchor = anchor;
        super.add(component, gbc);
        return component;
    }

    @Override
    public void update() {
        ModelElement me = getModelElement();
        if (me instanceof Node) {
            Class<? extends ModelElement> dialogElementClass = me.getClass();
            String displayableName = elementsNameBuilder.getDisplayableFullName(dialogElementClass);
            typeLabel.setText(displayableName);
            labelLabel.setText("<html><b>" + me.getClearName() + "</b></html>");
            GDCollection gdcoll = getCollection();
            String associatedDocID = me.getAssociatedSzenID();
            GraphDocument vdoc = gdcoll.getGraphDocumentCoded(associatedDocID);
            if (vdoc == null) {
                subModelLabelLabel.setVisible(false);
                submodelLabel.setVisible(false);
            } else {
                subModelLabelLabel.setVisible(true);
                submodelLabel.setVisible(true);
                submodelLabel.setText(vdoc != null ? "<html>" + vdoc.getTitle() + "</html>" : "----------");
            }
        } else if (me instanceof Edge) {
            Edge edge = (Edge) me;
            Class<? extends Edge> edgeClass = edge.getClass();

            String startElementClassName = elementsNameBuilder.getDisplayableName(getStartClass(edgeClass));
            String forwardEdgeClassName = "&nbsp;&nbsp;<i>" + elementsNameBuilder.getMetaAssociationName(edgeClass, Direction.FORWARD) + "</i>&nbsp;&nbsp;";
            String endElementClassName = elementsNameBuilder.getDisplayableName(getEndClass(edgeClass));
            MetaModel metaModel = getMetaModel();
            Class<? extends ModelElement> elementClass = me.getClass();
            if (metaModel.isAssociationClass(elementClass)) {
                typeLabel.setText("<html>" + elementsNameBuilder.getDisplayableName(me) + " (" + startElementClassName + "  <i>" + forwardEdgeClassName + "</i>  " + endElementClassName + ")</html>");
            } else {
                typeLabel.setText("<html>" + startElementClassName + "  <i>" + forwardEdgeClassName + "</i>  " + endElementClassName + "</html>");
            }

            String startElementName = edge.getStart().getClearName();
            // bei DoubleMeaning-Edges nur die tatsächliche Richtung hinschreiben
            if (MetaModel.isDoubleMeaningEdge(edgeClass)) {
                forwardEdgeClassName = "&nbsp;&nbsp;<i>" + elementsNameBuilder.getMetaAssociationName(edgeClass, Direction.FORWARD, ((DoubleMeaningEdge) edge).getConnectionState()) + "</i>&nbsp;&nbsp;";
            }
            String endElementName = edge.getEnd().getClearName();

            nameLabel.setText("<html><b>" + startElementName + "</b>" + forwardEdgeClassName + "<b>" + endElementName + "</b></html>");
            labelLabel.setText("<html>" + me.getClearName() + "</html>");
        }
        String meID = me.getID();
        idLabel.setText(meID + "        " + getResString("created") + ": " + getCreationTimeMedium(meID));
    }

    /**
     * @return
     */
    private JComboBox<SubType> initSubTypeBox() {
        UserFieldDefinitions userFieldDefinitions = getUserFieldDefinitions();
        Class<? extends ModelElement> modelElementClass = getModelElementClass();
        List<SubType> subTypes = userFieldDefinitions.getSubTypes(modelElementClass);
        JComboBox<SubType> subTypeBox;
        if (subTypes.isEmpty()) {
            return null;
        }
        subTypeBox = new JComboBox<>();
        subTypeBox.addItem(DUMMY_SUBTYPE);
        for (SubType subType : subTypes) {
            subTypeBox.addItem(subType);
        }
        ModelElement me = getModelElement();
        SubType subType = me.getSubType();
        subTypeBox.setSelectedItem(subType);
        subTypeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                SubType selectedSubType = (SubType) subTypeBox.getSelectedItem();
                GraphDocument mainDoc = dialog.getMainDoc();
                int pid = dialog.getTransactionID();
                mainDoc.setSubType(me, selectedSubType, pid);
            }
        });

        return subTypeBox;
    }

}
