package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.elements.Kante.DOUBLE;
import static de.imise.tool3lgm.graphtools.elements.Kante.getEndClass;
import static de.imise.tool3lgm.graphtools.elements.Kante.getStartClass;
import static de.imise.tool3lgm.graphtools.elements.Kante.isConnectingSameElementClasses;
import static de.imise.tool3lgm.graphtools.elements.ModelConstants.getDisplayableName;
import static de.imise.tool3lgm.graphtools.elements.ModelConstants.getMetaAssociationName;
import static de.imise.util.HashStringGenerator.getCreationTimeMedium;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;

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
        ModelElement me = dialog.getModelElement();
        if (me instanceof Knoten) {
            typeLabel.setText(getDisplayableName(me));
            labelLabel.setText("<html><b>" + me.getClearName() + "</b></html>");
            GraphDocument vdoc = mainDoc.getCollection().getGraphDocumentCoded(me.getAssociatedDoc());
            if (vdoc == null) {
                subModelLabelLabel.setVisible(false);
                submodelLabel.setVisible(false);
            } else {
                subModelLabelLabel.setVisible(true);
                submodelLabel.setVisible(true);
                submodelLabel.setText(vdoc != null ? "<html>" + vdoc.getTitle() + "</html>" : "----------");
            }
        } else if (me instanceof Kante) {
            Kante edge = (Kante) me;
            Class<? extends Kante> edgeClass = edge.getClass();

            String startElementClassName = getDisplayableName(getStartClass(edgeClass));
            String forwardEdgeClassName = "&nbsp;&nbsp;<i>" + getMetaAssociationName(edgeClass, false, DOUBLE) + "</i>&nbsp;&nbsp;";
            String endElementClassName = getDisplayableName(getEndClass(edgeClass));

            if (ModelConstants.isAssociationClass(getModelElement().getClass())) {
                typeLabel.setText("<html>" + getDisplayableName(me) + " (" + startElementClassName + "  <i>" + forwardEdgeClassName + "</i>  " + endElementClassName + ")</html>");
            } else {
                typeLabel.setText("<html>" + startElementClassName + "  <i>" + forwardEdgeClassName + "</i>  " + endElementClassName + "</html>");
            }

            String startElementName = edge.getStart().getClearName();
            // nur die tatsächliche Richtung hinschreiben
            if (!isConnectingSameElementClasses(edgeClass)) {
                forwardEdgeClassName = "&nbsp;&nbsp;<i>" + getMetaAssociationName(edgeClass, false, edge.getDirection()) + "</i>&nbsp;&nbsp;";
            }
            String endElementName = edge.getEnd().getClearName();

            nameLabel.setText("<html><b>" + startElementName + "</b>" + forwardEdgeClassName + "<b>" + endElementName + "</b></html>");
            labelLabel.setText("<html>" + me.getClearName() + "</html>");
        }
        String hashString = me.getHashString();
        idLabel.setText(hashString + "        " + getResString("created") + ": " + getCreationTimeMedium(hashString));
    }

}
