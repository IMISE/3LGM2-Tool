package de.imise.util.swing.component.text;

import static de.imise.util.swing.component.ParentComponentFinder.getFrameOrDialog;

import java.awt.Component;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.text.JTextComponent;

import de.imise.util.IntRange;
import de.imise.util.StringUtils;
import de.imise.util.resource.SimpleResourceBundleSourceAdapter;
import de.imise.util.swing.component.HistoryComboBox;

/**
 * @author AXS, STKR
 * @create 26.07.2012
 */
public class FindReplacePanel extends JPanel implements ActionListener {

    /**
     * Suchfeld
     */
    private final HistoryComboBox elementFind = new HistoryComboBox();
    /**
     * Ersetzenfeld
     */
    private final HistoryComboBox elementReplace = new HistoryComboBox();

    /**
     * Komponente, für das das Menü angezeigt werden soll
     */
    private JTextComponent myTargetComponent = null;

    final static SimpleResourceBundleSourceAdapter resHandler = new SimpleResourceBundleSourceAdapter(FindReplacePanel.class);

    /**
     * Groß/Kleinschreibung
     */
    private static boolean ignoreCase = true;
    private final JCheckBox ignoreCase_cb = new JCheckBox(resHandler.getResString("TOOLS_FINDREPLACEPANEL_CB_IC"), ignoreCase);

    /**
     * Reguläre Ausdrücke
     */
    private static boolean allowREs = false;
    private final JCheckBox allowREs_cb = new JCheckBox(resHandler.getResString("TOOLS_FINDREPLACEPANEL_CB_RE"), allowREs);

    /**
     * ganzes Wort
     */
    private static boolean onlyWholeWord = false;
    private final JCheckBox onlyWholeWord_cb = new JCheckBox(resHandler.getResString("TOOLS_FINDREPLACEPANEL_CB_WW"), onlyWholeWord);

    /**
     * Umbrechen
     */
    private final JCheckBox wrapAround_cb = new JCheckBox(resHandler.getResString("TOOLS_FINDREPLACEPANEL_CB_WA"), onlyWholeWord);

    /**
     * normal_direction: true vorwärts, false rückwärts;
     */
    private static boolean normal_direction = true;
    private final JRadioButton normal_direction_rb = new JRadioButton(resHandler.getResString("TOOLS_FINDREPLACEPANEL_RB_F"), normal_direction);
    private final JRadioButton back_direction_rb = new JRadioButton(resHandler.getResString("TOOLS_FINDREPLACEPANEL_RB_B"), !normal_direction);

    // Buttons
    private JButton jbSearch;
    private JButton jbReplaceAll;
    private JButton jbReplaceSearch;
    private JButton jbReplaceOnce;

    /**
     * @param textComponent
     */
    public FindReplacePanel(final JComponent textComponent) {
        super();
        // baue nur das JPanel
        init(textComponent);
    }

    /**
     * baut den Dialog auf mit dem JPanel
     *
     * @param textComponent
     */
    public void showFindReplaceDialog(final JComponent textComponent, final int x, final int y) {

        JDialog dialog = new JDialog(getFrameOrDialog(textComponent) != null && JDialog.class.isAssignableFrom(getFrameOrDialog(textComponent).getClass()) ? (JDialog) getFrameOrDialog(textComponent) : (JFrame) getFrameOrDialog(textComponent));
        dialog.getContentPane().add(new FindReplacePanel(textComponent));
        dialog.setTitle(resHandler.getResString("TOOLS_FINDREPLACEPANEL_TITLE"));
        dialog.setVisible(true);
        dialog.setMinimumSize(new Dimension(400, 250));
        dialog.setLocation(x, y);
        dialog.getContentPane().add(this);
        dialog.validate();
        dialog.repaint();
        dialog.pack();

    }

    /**
     * Initialisiert JPanel
     *
     * @param textComponent
     */
    private void init(final Object textObject) {
        removeAll();

        Component editorComp = null;
        if (textObject instanceof JComboBox) {
            editorComp = ((JComboBox) textObject).getEditor().getEditorComponent();
            if (editorComp instanceof JTextComponent) {
                myTargetComponent = (JTextComponent) editorComp;
            }
        }
        if (textObject instanceof JTextField) {
            myTargetComponent = (JTextComponent) textObject;
        }
        if (textObject instanceof ExtendedTextArea) {
            myTargetComponent = (ExtendedTextArea) textObject;
        }
        if (textObject instanceof ExtendedTextPane) {
            myTargetComponent = (ExtendedTextPane) textObject;
        }
        if (textObject instanceof ExtendedTextField) {
            myTargetComponent = (ExtendedTextField) textObject;
        }

        if (myTargetComponent == null) {
            return;
        }

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(gbl);

        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        // erste Spalte
        this.add(new JLabel(resHandler.getResString("TOOLS_FINDREPLACEPANEL_FIND")), constraints);
        constraints.gridy++;
        this.add(new JLabel(resHandler.getResString("TOOLS_FINDREPLACEPANEL_R")), constraints);
        constraints.gridy++;

        // nächste Spalte
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx++;
        constraints.gridy = 0;

        this.add(elementFind, constraints);
        constraints.gridy++;
        this.add(elementReplace, constraints);

        // nächste Zeile: Optionen
        ButtonGroup bgDir = new ButtonGroup();
        bgDir.add(back_direction_rb);
        bgDir.add(normal_direction_rb);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy++;

        JPanel directionPanel = new JPanel();
        directionPanel.setBorder(BorderFactory.createTitledBorder(resHandler.getResString("TOOLS_FINDREPLACEPANEL_RBS_SCOPE")));
        directionPanel.setLayout(new BoxLayout(directionPanel, BoxLayout.PAGE_AXIS));

        directionPanel.add(normal_direction_rb);
        directionPanel.add(back_direction_rb);

        this.add(directionPanel, constraints);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy++;

        JPanel optPanel = new JPanel();
        optPanel.setBorder(BorderFactory.createTitledBorder(resHandler.getResString("TOOLS_FINDREPLACEPANEL_CBS_OPT")));
        optPanel.setLayout(new BoxLayout(optPanel, BoxLayout.PAGE_AXIS));

        optPanel.add(ignoreCase_cb);
        optPanel.add(allowREs_cb);
        optPanel.add(onlyWholeWord_cb);

        onlyWholeWord_cb.setToolTipText(resHandler.getResString("TOOLS_FINDREPLACEPANEL_TT_WW"));
        optPanel.add(wrapAround_cb);

        this.add(optPanel, constraints);

        // wenn irgendwas an den Optionen oder der Suchtext geändert wird,
        // muss der enabled-Status der Knöpfe aktualsiert werden
        elementFind.addActionListener(this);
        elementReplace.addActionListener(this);
        ignoreCase_cb.addActionListener(this);
        allowREs_cb.addActionListener(this);
        onlyWholeWord_cb.addActionListener(this);
        normal_direction_rb.addActionListener(this);
        back_direction_rb.addActionListener(this);
        wrapAround_cb.addActionListener(this);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 2, 2));

        JTextComponent elemFindEditor = elementFind.getEditor().getEditorComponent() instanceof JTextComponent ? (JTextComponent) elementFind.getEditor().getEditorComponent() : null;

        jbSearch = new JButton(getSearchAction(elemFindEditor));
        buttonPanel.add(jbSearch);

        jbReplaceSearch = new JButton(getReplaceSearchAction(elemFindEditor, myTargetComponent, true));
        buttonPanel.add(jbReplaceSearch);

        jbReplaceOnce = new JButton(getReplaceSearchAction(elemFindEditor, myTargetComponent, false));
        buttonPanel.add(jbReplaceOnce);

        jbReplaceAll = new JButton(getReplaceAllAction(elemFindEditor, myTargetComponent));
        buttonPanel.add(jbReplaceAll);
        // this.add(jbReplaceAll, constraints);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 1;
        constraints.gridy++;
        this.add(buttonPanel, constraints);

        // Aktion
        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put("search", jbSearch.getAction());

        InputMap keyMap = new ComponentInputMap(jbSearch);
        keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "search");
        SwingUtilities.replaceUIActionMap(jbSearch, actionMap);
        SwingUtilities.replaceUIInputMap(jbSearch, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);

        // Aktion in den beiden Comboboxen setzen, die ausgeführt werden soll, wenn Enter gedrückt wird
        elementFind.setEnterAction(jbSearch.getAction());
        elementReplace.setEnterAction(jbSearch.getAction());

        setFocusTraversalPolicy(new DefaultFocusTraversalPolicy());
    }

    /**
     * Liefert die Aktion, die die Suche ausführt
     *
     * @param elemFindEditor
     * @return
     */
    private Action getSearchAction(final JTextComponent elemFindEditor) {
        return new AbstractAction(resHandler.getResString("TOOLS_FINDREPLACEPANEL_FIND")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                HistoryComboBox.addToHistory(elementFind);
                try {
                    search();
                } catch (Exception e1) {
                }
            }

            @Override
            public boolean isEnabled() {
                return elemFindEditor != null && elemFindEditor.getText().length() > 0;
            }
        };
    }

    /**
     * Liefert die Action, über die man einen vorher über die Suche gefundenen String ersetzen kann.
     *
     * @param elemFindEditor
     * @param targetComponent
     * @param withSearch
     *            wenn <code>true</code> wird nach dem eretzen erneut gesucht
     * @return
     */
    private Action getReplaceSearchAction(final JTextComponent elemFindEditor, final JTextComponent targetComponent, final boolean withSearch) {
        return new AbstractAction(resHandler.getResString(withSearch ? "TOOLS_FINDREPLACEPANEL_RF" : "TOOLS_FINDREPLACEPANEL_R")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    HistoryComboBox.addToHistory(elementReplace);

                    int len = targetComponent.getSelectionEnd() - targetComponent.getSelectionStart();

                    int pos = targetComponent.getSelectionStart();

                    //baue den Ersetzungstext zusammen, dieser besteht aus dem originalTargetText,
                    //der an der Stelle pos mit der Länge len ersetzt wird durch den Text searchReplacement
                    //ersetze den Text von myTargetComponent, durch den Ersetzungstext
                    if (len > 0) {
                        String originalTargetText = myTargetComponent.getText();
                        String searchReplacement = (String) elementReplace.getSelectedItem() == null ? "" : (String) elementReplace.getSelectedItem();
                        // orginalText bis zur Position pos
                        StringBuilder newTargetTextBuilder = new StringBuilder(originalTargetText.subSequence(0, pos));
                        // Ersetzungstext
                        newTargetTextBuilder.append(searchReplacement);
                        // Rest der noch übrig bleibt kommt hinten dran
                        newTargetTextBuilder.append(originalTargetText.subSequence(pos + len, originalTargetText.length()));
                        myTargetComponent.setText(newTargetTextBuilder.toString());
                        // nach ersetzung steht Curser auf Ende der Ersetzung

                        // pumping lemma vermeiden :-)
                        if (normal_direction_rb.isSelected()) {

                            myTargetComponent.setSelectionStart(pos + len);
                            myTargetComponent.setSelectionEnd(pos + len + searchReplacement.length());

                            // caret darf nicht größer sein als gesamtlänge
                            if (pos + len + searchReplacement.length() < newTargetTextBuilder.toString().length()) {
                                myTargetComponent.setCaretPosition(pos + len + searchReplacement.length());
                            }
                        } else {
                            myTargetComponent.setSelectionStart(pos);
                            myTargetComponent.setSelectionEnd(pos);

                            // caret darf nicht kleiner sein als 0
                            myTargetComponent.setCaretPosition(pos);

                        }
                    }
                    if (withSearch) {
                        HistoryComboBox.addToHistory(elementFind);
                        search();
                    }
                    updateEnabledStates();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public boolean isEnabled() {
                if (elemFindEditor == null || targetComponent == null) {
                    return false;
                }
                String text2Find = elemFindEditor.getText();
                if (text2Find == null || text2Find.length() == 0) {
                    return false;
                }
                String foundText = targetComponent.getSelectedText();
                if (foundText == null || foundText.length() == 0) {
                    return false;
                }
                if (ignoreCase_cb.isSelected()) {
                    foundText = foundText.toLowerCase();
                    text2Find = text2Find.toLowerCase();
                }
                return true; // isSearchStringInTarget(foundText, text2Find);
            }
        };
    }

    /**
     * Liefert die Action, über die alle Vorkommen des Suchtextes in der targetComponent ersetzt werden.
     *
     * @param elemFindEditor
     * @param targetComponent
     * @return
     */
    private Action getReplaceAllAction(final JTextComponent elemFindEditor, final JTextComponent targetComponent) {
        return new AbstractAction(resHandler.getResString("TOOLS_FINDREPLACEPANEL_REPLACE_ALL")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                HistoryComboBox.addToHistory(elementFind);
                HistoryComboBox.addToHistory(elementReplace);
                String originalTargetText = myTargetComponent.getText();
                String searchReplacement = (String) elementReplace.getSelectedItem() == null ? "" : (String) elementReplace.getSelectedItem();
                String searchString = elementFind.getSelectedItem().toString() == null ? "" : elementFind.getSelectedItem().toString();
                //Ausführung
                myTargetComponent.setText(originalTargetText.replaceAll(searchString, searchReplacement));
            }

            @Override
            public boolean isEnabled() {
                return elemFindEditor != null && elemFindEditor.getText().length() > 0;
            }
        };
    }

    /**
     * Sucht das nächste Vorkommen im Zielfeld ab der aktuellen Cursorposition in die eingestellte Richtung
     */
    /**
     * @return <code>true</code> wenn etwas gefunden wurde
     * @throws Exception
     */
    private boolean search() throws Exception {
        IntRange range;
        //Rückwärtselektion geht nicht, deswegen bei Rückwärtssuche mit vorhandener Selektion ab dem Startpuunkt der Selektion suchen
        if (back_direction_rb.isSelected() && myTargetComponent.getSelectedText() != null) {
            range = StringUtils.find(myTargetComponent.getText(), elementFind.getSelectedItem().toString(), myTargetComponent.getSelectionStart(), ignoreCase_cb.isSelected(), normal_direction_rb.isSelected(), allowREs_cb.isSelected(),
                    onlyWholeWord_cb.isSelected(), wrapAround_cb.isSelected());
            //Vorwärtssuche mit oder ohne Selektion oder Rückwärtssuche ohne Selektion immer ab CaretPostion suchen
        } else {
            range = StringUtils.find(myTargetComponent.getText(), elementFind.getSelectedItem().toString(), myTargetComponent.getCaretPosition(), ignoreCase_cb.isSelected(), normal_direction_rb.isSelected(), allowREs_cb.isSelected(),
                    onlyWholeWord_cb.isSelected(), wrapAround_cb.isSelected());
        }

        if (range != null) {
            myTargetComponent.setSelectionStart(range.min());
            myTargetComponent.setSelectionEnd(range.min() + range.length());
        }
        // all Knöpfe aktualisieren
        updateEnabledStates();
        return range != null;
    }

    /**
     *
     */
    private void updateEnabledStates() {
        // Enabled Status aller Buttons aktualisieren
        jbReplaceOnce.setEnabled(jbReplaceOnce.getAction().isEnabled());
        jbReplaceSearch.setEnabled(jbReplaceSearch.getAction().isEnabled());
        jbSearch.setEnabled(jbSearch.getAction().isEnabled());
        jbReplaceAll.setEnabled(jbReplaceAll.getAction().isEnabled());

        // statische Variablen der Optionen speichern
        ignoreCase = ignoreCase_cb.isSelected();
        allowREs = allowREs_cb.isSelected();
        onlyWholeWord = onlyWholeWord_cb.isSelected();
        normal_direction = normal_direction_rb.isSelected();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        updateEnabledStates();
    }

}
