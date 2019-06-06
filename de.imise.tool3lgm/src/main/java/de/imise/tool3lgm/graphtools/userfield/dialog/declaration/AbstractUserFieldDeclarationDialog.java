package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.imise.tool3lgm.graphtools.dialog.tools.EasyComponents;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.dialog.AbstractSizeAndPositionRestoringDialog;

public abstract class AbstractUserFieldDeclarationDialog extends AbstractSizeAndPositionRestoringDialog implements ActionListener {

    private static Dimension defaultSize = null;

    /** combobox to select a model-class */
    protected UserFieldDeclarationDialogClassComboBox classComboBox;

    /**
     * list with the defined userFields Es wird hier keine AplphabeticalJList genutzt, weil die Elemente in der Reinhenfolge angezeigt werden sollen,
     * die der User vorgibt.
     */
    protected UserFieldDeclarationDialogFieldList fieldList;

    /** button to edit a userField */
    protected JButton editButton;

    /** button to remove a userField */
    protected JButton deleteButton;

    /** button to add new userField */
    protected JButton newButton;

    /** Buttons zum Vertauschen der Reihenfolge von Attributen */
    protected JButton upButton, downButton;

    /** Buttons für den Im- und Export */
    protected final JButton importButton, exportButton;

    protected final JButton cancelButton, okButton;

    /**
     * ComboBox mit der die Art des neuen benutzerdefinierten Eigenschaftsfeldes festgelegt wird
     */
    protected AlphabeticalComboBox userFieldTypeComboBox;

    public AbstractUserFieldDeclarationDialog(final Frame owner, final UserFieldDefinitions definitions) {
        super(owner, getResString("userfields"), true);
        MetaModel metaModel = definitions.getMetaModel();
        classComboBox = new UserFieldDeclarationDialogClassComboBox(metaModel, 13);
        fieldList = new UserFieldDeclarationDialogFieldList(definitions);
        userFieldTypeComboBox = new AlphabeticalComboBox();

        newButton = createButton("new");
        editButton = createDisabledButton("userFieldDeclarationDialog_editButtonText");
        deleteButton = createDisabledButton("delete");
        downButton = createDisabledButton("runter2.gif");
        upButton = createDisabledButton("hoch2.gif");
        importButton = createButton("importButtonText");
        exportButton = createButton("exportButtonText");
        okButton = createButton("ok");
        cancelButton = createButton("cancel");

        //NORTH: Label und classComboBox
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(createLabel("userFieldDeclarationDialog_elementClass"));
        northPanel.add(classComboBox);
        pane.add(northPanel, BorderLayout.NORTH);

        //Center
        JPanel centerPanel = new JPanel(new BorderLayout());
        Border border = new EmptyBorder(5, 5, 5, 5);
        centerPanel.setBorder(border);
        centerPanel.add(createLabel("userFieldDeclarationDialog_fields"), BorderLayout.NORTH);
        centerPanel.add(createScrollPane(fieldList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        add(buttonPanel, gbc, 0, 0, 3, 1, userFieldTypeComboBox);
        add(buttonPanel, gbc, 3, 0, 1, 1, newButton);
        add(buttonPanel, gbc, 0, 1, 4, 1, editButton);
        add(buttonPanel, gbc, 0, 2, 4, 1, deleteButton);

        JPanel upDownButtonPanel = new JPanel(new GridBagLayout());
        add(upDownButtonPanel, gbc, 0, 0, 1, 1, downButton);
        add(upDownButtonPanel, gbc, 1, 0, 1, 1, upButton);

        add(buttonPanel, gbc, 0, 3, 4, 1, upDownButtonPanel);
        centerPanel.add(buttonPanel, BorderLayout.EAST);
        pane.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));

        JPanel importExportButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        importExportButtonPanel.add(importButton);
        importExportButtonPanel.add(exportButton);
        southPanel.add(importExportButtonPanel);

        JPanel okCancelButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okCancelButtonPanel.add(cancelButton);
        okCancelButtonPanel.add(okButton);
        southPanel.add(okCancelButtonPanel);

        pane.add(southPanel, BorderLayout.SOUTH);

        if (defaultSize == null) {
            pack();
            defaultSize = getSize();
        }
    }

    protected void add(final Container con, final GridBagConstraints gbc, final int x, final int y, final int w, final int h, final Component c) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        con.add(c, gbc);
    }

    private JButton createButton(final String resKey) {
        return EasyComponents.createButton(this, resKey);
    }

    private JButton createDisabledButton(final String resKey) {
        JButton button = createButton(resKey);
        button.setEnabled(false);
        return button;
    }

    private JLabel createLabel(final String resKey) {
        return new JLabel(getResString(resKey) + ": ");
    }

    private JScrollPane createScrollPane(final JComponent view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    @Override
    public final Dimension getDefaultSize() {
        return defaultSize;
    }

}
