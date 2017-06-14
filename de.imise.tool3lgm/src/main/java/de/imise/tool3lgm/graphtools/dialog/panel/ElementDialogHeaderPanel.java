package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * Panel das im Kopf jedes Eigenschaftsdialogs der Elemente deren Namen, ID usw. anzeigt. Es zeigt
 * die Informationen nur an und ist nicht editierbar.
 *
 * @author AXS
 * @create 27.09.2010
 */
public class ElementDialogHeaderPanel extends ElementDialogPanel {

    private final JLabel typeLabel, labelLabel;
    private JLabel nameLabel;
    private final JLabel idLabel;
    private JLabel submodelLabel;
    private JLabel subModelLabelLabel;

    public ElementDialogHeaderPanel(final ElementPropertyDialog dialog) {
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

        if (ModelConstants.isEdgeType(getModelElement().getClass())) {
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

        if (ModelConstants.isNodeType(getModelElement().getClass())) {
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
        ModelElement modelElement = dialog.getModelElement();
        if (ModelConstants.isNodeType(getModelElement().getClass())) {
            typeLabel.setText(ModelConstants.getDisplayableName(modelElement));
            labelLabel.setText("<html><b>" + modelElement.getClearName() + "</b></html>");
            idLabel.setText(modelElement.getHashString() + "        " + getResString("created") + ": " + modelElement.getCreationDate().toLocaleString());
            GraphDocument vdoc = mainDoc.getCollection().getGraphDocumentCoded(((Knoten) modelElement).getAssociatedDoc());
            if (vdoc == null) {
                subModelLabelLabel.setVisible(false);
                submodelLabel.setVisible(false);
            } else {
                subModelLabelLabel.setVisible(true);
                submodelLabel.setVisible(true);
                submodelLabel.setText(vdoc != null ? "<html>" + vdoc.getTitle() + "</html>" : "----------");
            }
        } else if (ModelConstants.isEdgeType(getModelElement().getClass())) {
            Doppelkante edge = (Doppelkante) modelElement;
            Class<? extends Kante> edgeClass = edge.getClass();

            Class<? extends ModelElement> startClass = Kante.getStartClass(edgeClass);
            Class<? extends ModelElement> endClass = Kante.getEndClass(edgeClass);

            String startElementClassName = ModelConstants.getDisplayableName(Kante.getStartClass(edgeClass));
            String forwardEdgeClassName = "&nbsp;&nbsp;<i>" + ModelConstants.getMetaAssociationName(edgeClass, false, Doppelkante.DOUBLE) + "</i>&nbsp;&nbsp;";
            String endElementClassName = ModelConstants.getDisplayableName(Kante.getEndClass(edgeClass));

            if (ModelConstants.isAssociationClass(getModelElement().getClass())) {
                typeLabel.setText("<html>" + ModelConstants.getDisplayableName(modelElement) + " (" + startElementClassName + "  <i>" + forwardEdgeClassName + "</i>  " + endElementClassName + ")</html>");
            } else {
                typeLabel.setText("<html>" + startElementClassName + "  <i>" + forwardEdgeClassName + "</i>  " + endElementClassName + "</html>");
            }

            String startElementName = edge.getStart().getClearName();
            // nur die tatsächliche Richtung hinschreiben
            boolean connectingSameElementClasses = startClass.isAssignableFrom(endClass) || endClass.isAssignableFrom(startClass);
            if (!connectingSameElementClasses) {
                forwardEdgeClassName = "&nbsp;&nbsp;<i>" + ModelConstants.getMetaAssociationName(edgeClass, false, edge.getDirection()) + "</i>&nbsp;&nbsp;";
            }
            String endElementName = edge.getEnd().getClearName();

            nameLabel.setText("<html><b>" + startElementName + "</b>" + forwardEdgeClassName + "<b>" + endElementName + "</b></html>");
            labelLabel.setText("<html>" + modelElement.getClearName() + "</html>");
            idLabel.setText(modelElement.getHashString() + "        " + getResString("created") + ": " + modelElement.getCreationDate().toLocaleString());
        }
    }

}
