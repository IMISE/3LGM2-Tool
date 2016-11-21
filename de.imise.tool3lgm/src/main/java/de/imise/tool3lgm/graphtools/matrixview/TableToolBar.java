package de.imise.tool3lgm.graphtools.matrixview;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector;
import de.imise.tool3lgm.tools.UnfloatableToolBar;

/**
 * Klasse für die Werkzeugleiste eines InternalFrame mit Matrixdarstellung
 * 
 * @author Thomas Rudert, AXS (22.10.07)
 */
public class TableToolBar extends UnfloatableToolBar implements ChangeListener, ActionListener {

    /**
     * Label für die ComboBox zur Klassenauswahl der Matrixzeilen
     */
    private final JLabel rowLabel;

    /**
     * Label für die ComboBox zur Klassenauswahl der Matrixspalten
     */
    private final JLabel colLabel;

    /**
     * Label, das das Zeilenelement anzeigt, über dem sich die Maus in der Matrix befindet
     */
    private final JLabel rowElementLabel;

    /**
     * Label, das das Spaltenelement anzeigt, über dem sich die Maus in der Matrix befindet
     */
    private final JLabel colElementLabel;

    /**
     * Panel für die Legende (gewählter Pfad und Bedeutung der Markierungen in der Matrix)
     */
    private final JPanel legendPanel;

    /**
     * Selektor, der die Comboboxen und die Metapfadauswahlliste zur Festlegung des darzustellenden Metapfades bereitstellt
     */
    private final MetaPathSelector metaPathSelector;

    /**
     * Chekcbox über die eingestellt werden kann, ob nur absolute Kindelemente (also Elemente ohne eigene Teilelemente) angezeigt werden soll. Diese Box ist disabled, wenn weder in den Zeilen noch in den Spalten Elemente angezeigt werden, die in
     * Teil-Von-Beziehung stehen können.
     */
    private final JCheckBox showPartsOnlyCheckBox;

    /**
     * Über diesen <code>boolean</code> wird sich die zuletzt gewählte Option aller geöffneten <code>showPartsOnlyCheckBox</code>es gemerkt, so dass eine neue Checkbox gleich mit diesem Wert initialisiert werden kann.
     */
    private static boolean lastShowPartsOnlyChoice = false;

    /**
     * Frame dessen Darstellung durch diese Toolbar beeinfluss wird
     */
    private final TableInternalFrame controlledFrame;

    /**
     * @param controlledFrame Frame dessen Darstellung durch diese Toolbar beeinfluss wird.
     */
    public TableToolBar(final TableInternalFrame controlledFrame) {
        super();

        this.controlledFrame = controlledFrame;

        legendPanel = new JPanel();
        JPanel positionPanel = new JPanel(new GridLayout(2, 2, 0, 0));
        JPanel choicePanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));

        legendPanel.setBorder(new TitledBorder(Tool3lgmConstants.getResString("legend")));
        positionPanel.setBorder(new TitledBorder(Tool3lgmConstants.getResString("position")));
        choicePanel.setBorder(new TitledBorder(Tool3lgmConstants.getResString("choice")));

        rowLabel = new JLabel(Tool3lgmConstants.getResString("zeilen") + ":");
        colLabel = new JLabel(Tool3lgmConstants.getResString("spalten") + ":");
        rowElementLabel = new JLabel("");
        colElementLabel = new JLabel("");

        metaPathSelector = MetaPathSelector.createComponents();
        metaPathSelector.addChangeListener(this);

        legendPanel.setPreferredSize(((TitledBorder) legendPanel.getBorder()).getMinimumSize(legendPanel));

        showPartsOnlyCheckBox = new JCheckBox(Tool3lgmConstants.getResString("showAbsolutePartsOnly"), lastShowPartsOnlyChoice);
        showPartsOnlyCheckBox.addActionListener(this);

        JComponent[] child1 = {
                rowLabel, metaPathSelector.getClass1ComboBox(), colLabel, metaPathSelector.getClass2ComboBox(), null, showPartsOnlyCheckBox
        };
        setGridBagLayout(choicePanel, child1, 2);

        JComponent[] child2 = {
                new JLabel(Tool3lgmConstants.getResString("zeile") + ":"), rowElementLabel, new JLabel(Tool3lgmConstants.getResString("spalte") + ":"), colElementLabel
        };
        setGridBagLayout(positionPanel, child2, 2);

        setLayout(new BorderLayout());
        add(choicePanel, BorderLayout.WEST);
        add(legendPanel, BorderLayout.CENTER);
        add(positionPanel, BorderLayout.EAST);

    }

    /**
     * erstellt fügt einer Componente das GridBagLayout und eine Menge von child- Componenten hinzu. Bei Anordnung der child-Componenten wird in der linken oberen Ecke angefangen und dann zeilenweise angeordnet
     * 
     * @param owner Componente, die die child-Componenten besitzen soll
     * @param childs Array mit den child-Componenten
     * @param columns Anzahl der Spalten in jeder Zeile
     * @return GridBagConstraints welches genutzt wurde.
     */
    private static final GridBagConstraints setGridBagLayout(final JComponent owner, final JComponent[] childs, final int columns) {
        GridBagConstraints constraints = new GridBagConstraints();
        owner.setLayout(new GridBagLayout());
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets.right = 5;

        for (int i = 0; i < childs.length; i++) {

            /*
             * erste Spalte immer so klein wie möglich aller anderen können gestreckt werden
             */
            if (i % columns == 0) {
                constraints.weightx = 0;
            } else {
                constraints.weightx = 100;
            }

            constraints.gridx = i % columns;
            constraints.gridy = i / columns;
            if (childs[i] != null) {
                owner.add(childs[i], constraints);
            }
        }
        return constraints;
    }

    /**
     * Aktualisiert die Matrix und die Einträge in der Legende (gewählter Pfad und Bedeutung der Markierungen in der Matrix)
     */
    private void update() {
        //Tabelle updaten
        Class<? extends ModelElement> c1 = metaPathSelector.getSelectedClass1();
        Class<? extends ModelElement> c2 = metaPathSelector.getSelectedClass2();
        MetaPath mp = metaPathSelector.getSelectedMetaPath();
        //den Frame und somit die Tabelle mit den gewählten Klassen und dem MetaPfad neu aufbauen
        controlledFrame.update(c1, c2, mp, lastShowPartsOnlyChoice);

        //Legende updaten
        legendPanel.removeAll();
        MetaPath metaPath = metaPathSelector.getSelectedMetaPath();
        if (metaPath != null) {
            for (int i = 0; i < metaPath.countOptions(); i++) {
                legendPanel.add(new TableToolBarLegendItem(metaPath.getDescription(i), metaPath.getColor(i)));
            }
        }
        legendPanel.revalidate();
        legendPanel.repaint();
    }

    /**
     * Aktualisiert die Positionslabels, die die Elemente der Zeile und Spalte darstellen. (Wird aufgerufen, wenn sich die Mausposition in der Matrix ändert)
     * 
     * @param col {@link ModelElement}, das in der Spalte dargestellt werden soll
     * @param row {@link ModelElement}, das in der Zeile dargestellt werden soll
     */
    public void positionChanged(final ModelElement col, final ModelElement row) {
        rowElementLabel.setText(row == null ? "" : row.toString());
        colElementLabel.setText(col == null ? "" : col.toString());
    }

    /**
     * @return Returns the metaPathSelector.
     */
    public MetaPathSelector getMetaPathSelector() {
        return metaPathSelector;
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        if (e.getSource() == metaPathSelector.getClass2ComboBox()) {
            //Dialog anzeigen, in dem man einen Pfad auswählen kann
            if (metaPathSelector.getSelectableMetaPathes() != null && metaPathSelector.getSelectableMetaPathes().length > 1) {
                Object[] msg = {
                        Tool3lgmConstants.getResString("text_path"), metaPathSelector.getMetaPathJList()
                };
                JOptionPane optionPane = new JOptionPane();
                optionPane.setMessage(msg);
                JDialog dialog = optionPane.createDialog(Tool3lgm.tool, Tool3lgmConstants.getResString("choice"));
                dialog.setVisible(true);
            }
            update();
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        //wenn die Option nur Teilelemente anzuzeigen umgestellt wurde
        if (e.getSource() == showPartsOnlyCheckBox) {
            lastShowPartsOnlyChoice = showPartsOnlyCheckBox.isSelected();
            update();
        }
    }
}