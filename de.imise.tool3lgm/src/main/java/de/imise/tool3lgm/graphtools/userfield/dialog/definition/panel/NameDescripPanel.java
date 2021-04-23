/*
 * Created on 09.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import de.imise.util.NameAndDescriptionSource;
import de.imise.util.NameAndDescriptionTarget;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * @author AXS
 */
public class NameDescripPanel extends AbstractInputPanel implements NameAndDescriptionSource {

    /**
     * Das UserField, dessen Eigenschaften mit diesem Panel geändert werden
     * sollen.
     */
    private final NameAndDescriptionTarget nameAndDescriptionTarget;

    /**
     * Comment for <code>nameTextField</code>
     */
    private final LimitedSizeScrollTextPane nameTextField = new LimitedSizeScrollTextPane();

    /**
     * Comment for <code>decsripArea</code>
     */
    private final ExtendedTextPane descripPane = new ExtendedTextPane();

    /**
     * @param nameAndDescriptionTarget
     */
    public NameDescripPanel(final NameAndDescriptionTarget nameAndDescriptionTarget) {
        super(new GridBagLayout());
        this.nameAndDescriptionTarget = nameAndDescriptionTarget;

        nameTextField.setText(nameAndDescriptionTarget.getName());
        descripPane.setText(nameAndDescriptionTarget.getDescription());

        setBorder(BorderFactory.createTitledBorder(getResString("general")));
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0);
        //die beiden Labels untereinander in der ersten Spalte einfügen
        add(new JLabel(getResString("bez")), gbc);
        gbc.gridy = 1;
        add(new JLabel(getResString("description")), gbc);

        //jetzt das Namentextfeld in der zweiten Spalte und ersten Zeile einfügen
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(nameTextField, gbc);

        //das Beschreibungsfeld unter das Namensfeld einfügen
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        add(new JScrollPane(descripPane), gbc);
        descripPane.setFont(nameTextField.getFont());
    }

    /**
     * @return Returns the nameTextField.
     */
    public JTextComponent getNameTextField() {
        return nameTextField.getTextPane();
    }

    @Override
    public void cancel() {
    }

    @Override
    public void commit() {
        nameAndDescriptionTarget.setName(nameTextField.getText());
        nameAndDescriptionTarget.setDescription(descripPane.getText());
    }

    @Override
    public String getError() {
        if (nameTextField.getText().trim().equals("")) {
            return getResString("empty_name");
        }
        return super.getError();
    }

    @Override
    public String getName() {
        return nameTextField.getText();
    }

    @Override
    public String getDescription() {
        return descripPane.getText();
    }
}
