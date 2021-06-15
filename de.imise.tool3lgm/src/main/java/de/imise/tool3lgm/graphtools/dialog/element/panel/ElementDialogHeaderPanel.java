package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
import static de.imise.util.IDStringGenerator.getCreationTimeMedium;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
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

    /**
     * @param dialog
     */
    public ElementDialogHeaderPanel(final AbstractElementPropertyDialog dialog) {
        super(dialog);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel(getResString("typ") + ": "), gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        typeLabel = new JLabel();
        add(typeLabel, gbc);

        ModelElement me = getModelElement();
        if (me instanceof Edge) {
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            add(new JLabel(getResString("name") + ": "), gbc);
            gbc.gridx++;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            nameLabel = new JLabel();
            add(nameLabel, gbc);
        }

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel(getResString("label") + ": "), gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        labelLabel = new JLabel();
        add(labelLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("ID: "), gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        idLabel = new JLabel();
        add(idLabel, gbc);
        if (me instanceof Node) {
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            add(subModelLabelLabel = new JLabel(getResString("verkn_teilmodell") + ": "), gbc);
            gbc.gridx++;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            submodelLabel = new JLabel();
            add(submodelLabel, gbc);
        }
        setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 5));
        update();
    }

    @Override
    public void update() {
        ModelElement me = getModelElement();
        if (me instanceof Node) {
            Node node = (Node) me;
            Class<? extends ModelElement> dialogElementClass = me.getClass();
            String displayableName = elementsNameBuilder.getDisplayableFullName(dialogElementClass);
            SubType subType = node.getSubType();
            if (!SubType.isDummy(subType)) {
                displayableName += "    (" + subType + ")";
            }
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

}
