package de.imise.tool3lgm.graphtools.newmatrixview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.TLGMOriginalPathsDefinition;
import de.imise.util.swing.component.UnfloatableToolBar;

/**
 * *Klasse für die Toolbar eines InternalFrame mit Matrixdarstellung
 *
 * @author Thomas Rudert, AXS (22.10.07)
 */
public class InternalMatrixFrameToolBar extends UnfloatableToolBar implements ChangeListener, ActionListener {

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
     * Label, das den Namen des Pfades anzeigt
     */
    private final JLabel pathNameLabel;

    /**
     * Panel für die Legende (gewählter Pfad und Bedeutung der Markierungen in der Matrix)
     */
    private final JPanel legendPanel;

    /**
     * Selektor, der die Comboboxen und die Metapfadauswahlliste zur Festlegung des darzustellenden
     * Metapfades bereitstellt
     */
    private final MetaPathSelector metaPathSelector;

    /**
     * Chekcbox über die eingestellt werden kann, ob nur absolute Kindelemente (also Elemente ohne
     * eigene Teilelemente) angezeigt werden soll. Diese Box ist disabled, wenn weder in den Zeilen
     * noch in den Spalten Elemente angezeigt werden, die in Teil-Von-Beziehung stehen können.
     */
    private final JCheckBox showPartsOnlyCheckBox;

    /**
     * über diesen <code>boolean</code> wird sich die zuletzt gewählte Option aller geöffneten <code>showPartsOnlyCheckBox</code>es gemerkt, so dass
     * eine neue Checkbox gleich mit diesem
     * Wert initialisiert werden kann.
     */
    private static boolean lastShowPartsOnlyChoice = false;

    /**
     * Frame dessen Darstellung durch diese Toolbar beeinflusst wird
     */
    private MatrixViewInternalFrame controlledFrame;

    /**
     * Das Model mit den auswählbaren Elementklassen und Pfaden für den {@link MetaPathSelector}
     */
    public static final MetaPathDefinition METAPATH_SELECTOR_MODEL = new TLGMOriginalPathsDefinition();

    /**
     * Maximale Anzahle gleichzeitig auswählbarer Metapfade, wenn es mehrere gibt
     */
    public static final int MAX_PARALLEL_SELECTED_METAPATHS = 4;

    /**
     * @param controlledFrame
     *            Frame dessen Darstellung durch diese Toolbar beeinfluss wird.
     */
    public InternalMatrixFrameToolBar(final MatrixViewInternalFrame controlledFrame) {
        super();

        // dieser Constructor sollte nur mit TableInternalFrames aufgerufen werden. ACHTUNG: der
        // wird per Refelction aufgerufen, daher findet man im Code keine 'direkte' Verwendung
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
        pathNameLabel = new JLabel("");
        pathNameLabel.setFont(pathNameLabel.getFont().deriveFont(Font.BOLD));

        metaPathSelector = new MetaPathSelector(METAPATH_SELECTOR_MODEL, MAX_PARALLEL_SELECTED_METAPATHS, true);
        metaPathSelector.addChangeListener(this);

        //ab 6 Legendeneinträgen ist diese Einstellung nicht mehr hoch genug
        //      legendPanel.setPreferredSize(((TitledBorder) legendPanel.getBorder()).getMinimumSize(legendPanel));

        showPartsOnlyCheckBox = new JCheckBox(Tool3lgmConstants.getResString("showAbsolutePartsOnly"), lastShowPartsOnlyChoice);
        showPartsOnlyCheckBox.addActionListener(this);

        JComponent[] child1 = {
                rowLabel, metaPathSelector.getClass1ComboBox(), colLabel, metaPathSelector.getClass2ComboBox(), null, showPartsOnlyCheckBox
        };
        setGridBagLayout(choicePanel, child1, 2);

        JComponent[] child2 = {
                new JLabel(Tool3lgmConstants.getResString("zeile") + ":"), rowElementLabel, new JLabel(), pathNameLabel, new JLabel(Tool3lgmConstants.getResString("spalte") + ":"), colElementLabel
        };
        setGridBagLayout(positionPanel, child2, 2);

        setLayout(new BorderLayout());
        add(choicePanel, BorderLayout.WEST);
        add(legendPanel, BorderLayout.CENTER);
        add(positionPanel, BorderLayout.EAST);

    }

    public void setFrame(final MatrixViewInternalFrame frame) {
        controlledFrame = frame;
    }

    /**
     * Erstellt fügt einer Componente das GridBagLayout und eine Menge von child- Componenten hinzu.
     * Bei Anordnung der child-Componenten wird in der linken oberen Ecke angefangen und dann
     * zeilenweise angeordnet
     *
     * @param owner
     *            Componente, die die child-Componenten besitzen soll
     * @param childs
     *            Array mit den child-Componenten
     * @param columns
     *            Anzahl der Spalten in jeder Zeile
     * @return GridBagConstraints welches genutzt wurde.
     */
    private GridBagConstraints setGridBagLayout(final JComponent owner, final JComponent[] childs, final int columns) {
        GridBagConstraints constraints = new GridBagConstraints();
        owner.setLayout(new GridBagLayout());
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets.right = 5;

        for (int i = 0; i < childs.length; i++) {
            //erste Spalte immer so klein wie möglich aller anderen können gestreckt werden
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
     * Aktualisiert die Matrix und die Einträge in der Legende (gewählter Pfad und Bedeutung der
     * Markierungen in der Matrix)
     */
    private void update() {
        // Tabelle updaten
        Class<? extends ModelElement> c1 = metaPathSelector.getSelectedClass1();
        Class<? extends ModelElement> c2 = metaPathSelector.getSelectedClass2();
        AbstractMetaPath[] metaPaths = metaPathSelector.getSelectedMetaPaths();
        // den Frame und somit die Tabelle mit den gewählten Klassen und dem MetaPfad neu aufbauen
        controlledFrame.update(c1, c2, metaPaths, lastShowPartsOnlyChoice);

        // Legende updaten
        legendPanel.removeAll();
        if (metaPathSelector.isValidSelection()) {
            int combinations = (1 << metaPaths.length) - 1;
            String and = " " + Tool3lgmConstants.getResString("und") + " ";
            for (int i = 0; i < combinations; i++) {
                String startClassName = ElementsNameBuilder.getDisplayableName(c1);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < metaPaths.length; j++) {
                    if ((i + 1 >> j & 1) == 1) {
                        sb.append(metaPaths[j].getName());
                        sb.append(and);
                    }
                }
                sb.setLength(sb.length() - and.length());
                String endClassName = ElementsNameBuilder.getDisplayableName(c2);
                legendPanel.add(new TableToolBarLegendItem(startClassName, sb.toString(), endClassName, TableModel.pathColors[i]));
            }
        }
        legendPanel.revalidate();
        legendPanel.repaint();
    }

    /**
     * Aktualisiert die Positionslabels, die die Elemente der Zeile und Spalte darstellen. (Wird
     * aufgerufen, wenn sich die Mausposition in der Matrix ändert)
     *
     * @param col
     *            {@link ModelElement}, das in der Spalte dargestellt werden soll
     * @param row
     *            {@link ModelElement}, das in der Zeile dargestellt werden soll
     * @param pathName
     *            Name des MetaPtafades der aktuell angezeigt wird
     */
    public void positionChanged(final ModelElement col, final ModelElement row, final String pathName) {
        rowElementLabel.setText(row == null ? "" : row.toString());
        pathNameLabel.setText(pathName == null ? " " : pathName);
        colElementLabel.setText(col == null ? "" : col.toString());
    }

    /**
     * @return Returns the metaPathSelector.
     */
    public MetaPathSelector getMetaPathSelector() {
        return metaPathSelector;
    }

    /**
     * Liefert den String mit dem Pfadnamen, der für die übergebene Farbe angezeigt wird.
     *
     * @param legendItemColor
     * @return
     */
    public String getPathName(final Color legendItemColor) {
        for (int i = 0; i < legendPanel.getComponentCount(); i++) {
            TableToolBarLegendItem item = (TableToolBarLegendItem) legendPanel.getComponent(i);
            if (item.getColor().equals(legendItemColor)) {
                return item.getDescription();
            }
        }
        return null;
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        update();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        lastShowPartsOnlyChoice = showPartsOnlyCheckBox.isSelected();
        update();
    }

}