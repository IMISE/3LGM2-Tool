package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author Thomas Ruder
 */
public class ExtIdPanel extends ElementDialogPanel {

    /**
     * @param pd
     */
    public ExtIdPanel(final ElementPropertyDialog pd) {
        super(pd);
        create();
        init();
    }

    private void create() {
        setLayout(new BorderLayout());
        JPanel mp = new JPanel();
        JScrollPane sp = new JScrollPane(mp);
        add(sp);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mp.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.fill &= GridBagConstraints.VERTICAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0;

        ModelElement me = dialog.getModelElement();

        // HashMap extIDMap = dialog.getModelElement().getExternalIDKeys()s();
        Object[] extIDKeys = me.getExternalIDKeys().toArray();
        Arrays.sort(extIDKeys);
        for (int i = 0; i < extIDKeys.length; i++) {
            constraints.gridy++;
            constraints.gridx = 0;
            constraints.weightx = 0;
            constraints.insets = new Insets(1, 10, 1, 30);
            mp.add(new JLabel(extIDKeys[i].toString()), constraints);
            constraints.gridx = 1;
            constraints.weightx = 100;
            constraints.insets = new Insets(1, 1, 1, 1);
            if (i + 1 == extIDKeys.length) {
                constraints.weighty = 100;
            }

            mp.add(new JLabel(me.getExternalID(extIDKeys[i].toString())), constraints);
        }
    }

    @Override
    protected void init() {
    }

    @Override
    protected void showFullDialog() {
    }

}
