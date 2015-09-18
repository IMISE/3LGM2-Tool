package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.Border;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Bausteintyp;
import de.imise.tool3lgm.graphtools.elements.node.DBVerwaltungssystem;
import de.imise.tool3lgm.graphtools.elements.node.Datenbanksystem;
import de.imise.tool3lgm.graphtools.elements.node.Datensatztyp;
import de.imise.tool3lgm.graphtools.elements.node.Dokumentensammlung;
import de.imise.tool3lgm.graphtools.elements.node.Dokumententyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.graphtools.elements.node.Ereignistyp;
import de.imise.tool3lgm.graphtools.elements.node.EtntEtdtKombination;
import de.imise.tool3lgm.graphtools.elements.node.Kommunikationsstandard;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Nachrichtentyp;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.elements.node.Standort;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author N.N.
 * @create Long time ago
 */
public class DescripPanel extends ElementDialogPanel {

    /**
     * COMMENTME
     */
    private final ExtendedTextPane descriptionTextPane;

    private ExtendedTextPane etDescriptionTextPane;

    private ExtendedTextPane ntDescriptionTextPane;

    /**
     * COMMENTME
     */
    private final LimitedSizeScrollTextPane nameTextPane;

    /**
     * COMMENTME
     */
    private final ArrayList<ElementDialogPanel> panelVector = new ArrayList<ElementDialogPanel>();

    /**
     * @param prop
     */
    public DescripPanel(final ElementPropertyDialog prop) {
        this(prop, true);
    }

    /**
     * @param prop
     * @param callInit
     */
    public DescripPanel(final ElementPropertyDialog prop, final boolean callInit) {
        super(prop);
        int gridy = 0;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 0, 1, 3);

        // Bezeichung und Eingabefeld
        JLabel label2 = new JLabel(Tool3lgmConstants.getResString("bez"));
        add(this, label2, gbc, 0, gridy, 1, 1);

        nameTextPane = new LimitedSizeScrollTextPane(4);
        gbc.weightx = 1;
        add(this, nameTextPane, gbc, 1, gridy++, 1, 1);
        gbc.weightx = 0;

        // Beschreibung und TextPane
        JLabel label = new JLabel(Tool3lgmConstants.getResString("description"));
        add(this, label, gbc, 0, gridy, 1, 1);

        descriptionTextPane = new ExtendedTextPane();
        gbc.weighty = 1;
        add(this, new JScrollPane(descriptionTextPane), gbc, 1, gridy++, 1, 1);
        gbc.weighty = 0;

        ModelElement modelElement = getModelElement();
        if (modelElement instanceof Anwendungsbaustein) {
            if (modelElement instanceof RechAnwendungsbaustein) {
                AuswahlPanel ap = new AuswahlPanel(Datenbanksystem.class, dialog);
                panelVector.add(ap);

                add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);

                gbc.fill = GridBagConstraints.HORIZONTAL;
                add(this, ap, gbc, 1, gridy++, 1, 1);

                ap = new AuswahlPanel(Softwareprodukt.class, dialog);
                panelVector.add(ap);

                gbc.fill = GridBagConstraints.NONE;
                add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                add(this, ap, gbc, 1, gridy++, 1, 1);

            } else if (modelElement instanceof KonAnwendungsbaustein) {
                AuswahlPanel ap;
                ap = new AuswahlPanel(Dokumentensammlung.class, dialog);
                panelVector.add(ap);
                gbc.weightx = 0;
                add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
                gbc.weightx = 1;
                add(this, ap, gbc, 1, gridy++, 1, 1);

            } else {
                AuswahlPanel ap;
                ap = new AuswahlPanel(Datenbanksystem.class, dialog);
                panelVector.add(ap);
                gbc.weightx = 0;
                add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
                gbc.weightx = 1;
                add(this, ap, gbc, 1, gridy++, 1, 1);

                ap = new AuswahlPanel(Softwareprodukt.class, dialog);
                panelVector.add(ap);
                gbc.weightx = 0;
                add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
                gbc.weightx = 1;
                add(this, ap, gbc, 1, gridy++, 1, 1);

                ap = new AuswahlPanel(Dokumentensammlung.class, dialog);
                panelVector.add(ap);
                gbc.weightx = 0;
                add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
                gbc.weightx = 1;
                add(this, ap, gbc, 1, gridy++, 1, 1);

            }
        } else if (modelElement instanceof Objekttyp) {
            MasterDBSPanel mp = new MasterDBSPanel(dialog, false);
            panelVector.add(mp);

            add(this, mp.getWestLabel(), gbc, 0, gridy, 1, 1);
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.BOTH;
            add(this, mp, gbc, 1, gridy++, 1, 1);

        } else if (modelElement instanceof Datenbanksystem) {

            add(this, new JLabel(Tool3lgmConstants.getResString("gehawb")), gbc, 0, gridy, 1, 1);
            ArrayList<Kante> traces = ((Knoten) modelElement).getEdges();
            for (int n = 0; n < traces.size(); n++) {
                if (traces.get(n).getStart() instanceof RechAnwendungsbaustein) {
                    add(this, new Label(traces.get(n).getStart().toString()), gbc, 1, gridy++, 1, 1);
                }
            }
            AuswahlPanel ap;
            ap = new AuswahlPanel(DBVerwaltungssystem.class, dialog);
            panelVector.add(ap);
            add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
            add(this, ap, gbc, 1, gridy++, 0, 1);

        } else if (modelElement instanceof Bausteinschnittstelle) {

            add(this, new JLabel(Tool3lgmConstants.getResString("gehawb")), gbc, 0, gridy, 1, 1);

            ArrayList<Kante> traces = ((Knoten) modelElement).getEdges();
            for (int n = 0; n < traces.size(); n++) {
                if (traces.get(n).getStart() instanceof Anwendungsbaustein) {
                    add(this, new Label(traces.get(n).getStart().toString()), gbc, 1, gridy++, 1, 1);
                }
            }
            AuswahlPanel ap;
            ap = new AuswahlPanel(Kommunikationsstandard.class, dialog);
            panelVector.add(ap);
            add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
            add(this, ap, gbc, 1, gridy++, 1, 1);

        } else if (modelElement instanceof EtntEtdtKombination) {
            nameTextPane.setEditable(false);

            Border topBorder = BorderFactory.createEmptyBorder(3, 0, 0, 0);
            JLabel westLabel;

            JSeparator sep = new JSeparator();
            add(this, sep, gbc, 0, gridy++, 2, 0);

            AuswahlPanel etPanel = new AuswahlPanel(Ereignistyp.class, dialog, false);
            etPanel.setBorder(topBorder);
            panelVector.add(etPanel);
            gbc.fill = GridBagConstraints.NONE;

            westLabel = etPanel.getWestLabel();

            westLabel.setBorder(topBorder);
            add(this, westLabel, gbc, 0, gridy, 1, 1);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(this, etPanel, gbc, 1, gridy++, 1, 1);

            label = new JLabel(Tool3lgmConstants.getResString("description"));
            gbc.fill = GridBagConstraints.NONE;
            add(this, label, gbc, 0, gridy, 1, 1);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;
            gbc.weightx = 1;
            etDescriptionTextPane = new ExtendedTextPane();
            etDescriptionTextPane.setEditable(false);
            add(this, new JScrollPane(etDescriptionTextPane), gbc, 1, gridy++, 1, 1);
            gbc.weightx = 0;
            gbc.weighty = 0;

            sep = new JSeparator();
            gridy++;
            add(this, sep, gbc, 0, gridy++, 2, 0);

            if (modelElement instanceof EreignisNachrichtenTyp) {
                AuswahlPanel ntPanel = new AuswahlPanel(Nachrichtentyp.class, dialog, false);
                panelVector.add(ntPanel);
                ntPanel.setBorder(topBorder);
                gbc.fill = GridBagConstraints.NONE;
                gbc.weighty = 0;
                gbc.weightx = 0;
                westLabel = ntPanel.getWestLabel();
                westLabel.setBorder(topBorder);
                add(this, westLabel, gbc, 0, gridy, 1, 1);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                add(this, ntPanel, gbc, 1, gridy++, 1, 1);
            } else if (modelElement instanceof EreignisDokumentenTyp) {

                AuswahlPanel dtPanel = new AuswahlPanel(Dokumententyp.class, dialog, false);
                panelVector.add(dtPanel);
                gbc.fill = GridBagConstraints.NONE;
                gbc.weighty = 0;
                gbc.weightx = 0;
                add(this, dtPanel.getWestLabel(), gbc, 0, gridy, 1, 1);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                add(this, dtPanel, gbc, 1, gridy++, 1, 1);
            }
            label = new JLabel(Tool3lgmConstants.getResString("description"));
            gbc.fill = GridBagConstraints.NONE;
            add(this, label, gbc, 0, gridy, 1, 1);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;
            gbc.weightx = 1;
            ntDescriptionTextPane = new ExtendedTextPane();
            ntDescriptionTextPane.setEditable(false);
            add(this, new JScrollPane(ntDescriptionTextPane), gbc, 1, gridy++, 1, 1);
            gbc.weightx = 0;

        } else if (modelElement instanceof PhysischerDVBaustein) {
            AuswahlPanel ap;
            ap = new AuswahlPanel(Standort.class, dialog);
            panelVector.add(ap);
            gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
            add(this, ap, gbc, 1, gridy++, 1, 1);

            ap = new AuswahlPanel(Bausteintyp.class, dialog);
            panelVector.add(ap);

            add(this, ap.getWestLabel(), gbc, 0, gridy, 1, 1);
            add(this, ap, gbc, 1, gridy++, 1, 1);

        } else if (modelElement instanceof Datensatztyp) {

            add(this, new JLabel(Tool3lgmConstants.getResString("gehdbs")), gbc, 0, gridy++, 1, 1);

            ArrayList<Kante> traces = ((Knoten) modelElement).getEdges();
            for (int n = 0; n < traces.size(); n++) {
                if (traces.get(n).getStart() instanceof Datenbanksystem) {
                    add(this, new Label(traces.get(n).getStart().toString()), gbc, 0, gridy++, 2, 1);
                }
            }

        } else if (modelElement instanceof Dokumentensammlung) {

            add(this, new JLabel(Tool3lgmConstants.getResString("gehawb")), gbc, 0, gridy, 1, 1);
            ArrayList<Kante> traces = ((Knoten) modelElement).getEdges();
            for (int n = 0; n < traces.size(); n++) {
                if (traces.get(n).getStart() instanceof Anwendungsbaustein) {
                    add(this, new Label(traces.get(n).getStart().toString()), gbc, 1, gridy++, 1, 1);
                }
            }
        }
        if (callInit) {
            init();
        }
    }

    @Override
    protected void init() {
        ModelElement modelElement = getModelElement();
        nameTextPane.setText(modelElement.getName());
        descriptionTextPane.setText(modelElement.getDescription());
        nameTextPane.setCaretPosition(0);
        descriptionTextPane.setCaretPosition(0);

        if (modelElement instanceof EtntEtdtKombination) {
            String text = "";
            ArrayList<ElementContainer> all = modelElement.getConnectedContainer(Ereignistyp.class, mainDoc);
            if (all.size() > 0) {
                NodeContainer kc = (NodeContainer) all.get(0);
                text += kc.getKnoten().getDescription();
                for (int i = 1; i < all.size(); i++) {
                    NodeContainer lc = (NodeContainer) all.get(i);
                    text += "\n\n" + lc.getKnoten().getDescription();
                }
            }
            etDescriptionTextPane.setCaretPosition(0);
            etDescriptionTextPane.setText(text);
            text = "";
            all = modelElement.getConnectedContainer(Nachrichtentyp.class, mainDoc);
            all.addAll(modelElement.getConnectedContainer(Dokumententyp.class, mainDoc));
            if (all.size() > 0) {
                NodeContainer kc = (NodeContainer) all.get(0);
                text += kc.getKnoten().getDescription();
                for (int i = 1; i < all.size(); i++) {
                    NodeContainer lc = (NodeContainer) all.get(i);
                    text += "\n\n" + lc.getKnoten().getDescription();
                }
            }
            ntDescriptionTextPane.setText(text);
        }
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
            // wenn der Name gleich gebleiben ist, kann aber trotzdem der HTML-Name in der
            // Grafik sich geändert haben,
            // wenn in dem Dialog ein Element verknüpft wurde, das auch im Namen in der Grafik
            // angezeigt wird -> einfach
            // ohne Transaktion in jedem Fall mal setName() mit dem alten Namen für das Element
            // aufrufen
            me.setName(name);
        }
        String newDescrip = descriptionTextPane.getText();
        String descrip = me.getDescription();
        if (newDescrip != null && !newDescrip.equals(descrip)) {
            doc.setDescription(me.getHashString(), GraphDocument.getParseSaveString(newDescrip), dialog.getTransactionID());
        }
        me.refreshText();
    }

    @Override
    public void update() {
        ModelElement modelElement = getModelElement();
        if (modelElement instanceof EtntEtdtKombination) {
            nameTextPane.setText(modelElement.getName());
        }

        if (modelElement instanceof EtntEtdtKombination) {
            String text = "";
            ArrayList<ElementContainer> all = modelElement.getConnectedContainer(Ereignistyp.class, mainDoc);
            if (all.size() > 0) {
                NodeContainer kc = (NodeContainer) all.get(0);
                text += kc.getKnoten().getDescription();
                for (int i = 1; i < all.size(); i++) {
                    NodeContainer lc = (NodeContainer) all.get(i);
                    text += "\n\n" + lc.getKnoten().getDescription();
                }
            }
            etDescriptionTextPane.setText(text);
            text = "";
            all = modelElement.getConnectedContainer(Nachrichtentyp.class, mainDoc);
            all.addAll(modelElement.getConnectedContainer(Dokumententyp.class, mainDoc));
            if (all.size() > 0) {
                NodeContainer kc = (NodeContainer) all.get(0);
                text += kc.getKnoten().getDescription();
                for (int i = 1; i < all.size(); i++) {
                    NodeContainer lc = (NodeContainer) all.get(i);
                    text += "\n\n" + lc.getKnoten().getDescription();
                }
            }
            ntDescriptionTextPane.setText(text);
        }

        for (int m = 0; m < panelVector.size(); m++) {
            panelVector.get(m).update();
        }
    }

}