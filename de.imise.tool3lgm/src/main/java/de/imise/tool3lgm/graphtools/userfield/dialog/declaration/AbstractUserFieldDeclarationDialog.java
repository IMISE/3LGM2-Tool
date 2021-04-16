package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.imise.tool3lgm.graphtools.dialog.tools.EasyComponents;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.dialog.AbstractSizeAndPositionRestoringDialog;

public abstract class AbstractUserFieldDeclarationDialog extends AbstractSizeAndPositionRestoringDialog implements ActionListener {

    /**
     * Stores the last selected class so if the dialog is reopend the last
     * selected view can be restored.
     */
    private static Class<? extends UserFieldTarget> lastSelectedUserFieldTargetClass;

    /** combobox to select a model-class */
    protected UserFieldDeclarationDialogClassComboBox classComboBox;

    /**
     * list with the defined userFields Es wird hier keine AplphabeticalJList
     * genutzt, weil die Elemente in der Reinhenfolge angezeigt werden sollen,
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

    /** Dialogs Cancel button */
    protected final JButton cancelButton;

    /** Dialogs Ok button */
    protected final JButton okButton;

    /**
     * Stores the last selected {@link Style} so if the dialog is reopend the
     * last selected view can be restored.
     */
    protected static Style lastSelectedUserFieldStyle;

    /**
     * ComboBox mit der die Art des neuen benutzerdefinierten Eigenschaftsfeldes
     * festgelegt wird
     */
    protected AlphabeticalComboBox<Style> userFieldTypeComboBox;

    /**
     * @param owner
     * @param definitions
     */
    public AbstractUserFieldDeclarationDialog(final Frame owner, final UserFieldDefinitions definitions) {
        super(owner, getResString("userfields"), true);
        MetaModel metaModel = definitions.getMetaModel();
        classComboBox = createClassCombobox(metaModel);
        fieldList = new UserFieldDeclarationDialogFieldList(definitions);
        userFieldTypeComboBox = createStyleCombobox();

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

    }

    /**
     * @param metaModel
     * @return
     */
    private UserFieldDeclarationDialogClassComboBox createClassCombobox(final MetaModel metaModel) {
        UserFieldDeclarationDialogClassComboBox classComboBox = new UserFieldDeclarationDialogClassComboBox(metaModel, 20);
        if (lastSelectedUserFieldTargetClass != null) {
            classComboBox.setSelectedObject(lastSelectedUserFieldTargetClass);
        }
        classComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                lastSelectedUserFieldTargetClass = classComboBox.getSelectedClass();
            }
        });
        return classComboBox;
    }

    private AlphabeticalComboBox<UserField.Style> createStyleCombobox() {
        AlphabeticalComboBox<UserField.Style> styleCombobox = new AlphabeticalComboBox<>(3);
        styleCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Style selectedStyle = styleCombobox.getSelectedObject();
                if (selectedStyle != null) {
                    lastSelectedUserFieldStyle = selectedStyle;
                }
            }
        });
        return styleCombobox;
    }

    /**
     * @param con
     * @param gbc
     * @param x
     * @param y
     * @param w
     * @param h
     * @param c
     */
    protected void add(final Container con, final GridBagConstraints gbc, final int x, final int y, final int w, final int h, final Component c) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        con.add(c, gbc);
    }

    /**
     * @param resKey
     * @return
     */
    private JButton createButton(final String resKey) {
        return EasyComponents.createButton(this, resKey);
    }

    /**
     * @param resKey
     * @return
     */
    private JButton createDisabledButton(final String resKey) {
        JButton button = createButton(resKey);
        button.setEnabled(false);
        return button;
    }

    /**
     * @param resKey
     * @return
     */
    private JLabel createLabel(final String resKey) {
        return new JLabel(getResString(resKey) + ": ");
    }

    /**
     * @param view
     * @return
     */
    private JScrollPane createScrollPane(final JComponent view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    @Override
    public final Point getDefaultPosition() {
        return new Point(100, 100);
    }

}
