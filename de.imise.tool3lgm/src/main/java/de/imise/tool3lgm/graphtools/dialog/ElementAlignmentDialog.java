package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.Alphabetical;
import de.imise.util.swing.component.RaiseStepSpinner;

public class ElementAlignmentDialog extends JDialog implements ChangeListener {

    private static int rowSpinnerValue = -1;

    private static int colSpinnerValue = -1;

    private static int vGapSpinnerValue = -1;

    private static int hGapSpinnerValue = -1;

    private static boolean sortAlphabetical = false;

    private RaiseStepSpinner rowSpinner;

    private RaiseStepSpinner colSpinner;

    private RaiseStepSpinner vGapSpinner;

    private RaiseStepSpinner hGapSpinner;

    private JCheckBox sortAlphabeticalCheck;

    private List<NodeContainer> selectedElementContainer;

    private final GraphDocument doc;

    public ElementAlignmentDialog() {
        super(Static.getMainFrame(), getResString("ALIGN_DIALOG_TITLE"));
        doc = Static.getSelectedDoc();
        setLocationRelativeTo(getOwner());
        initSelectedElementContainer();

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = initCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        JPanel buttonPanel = initButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
        pack();
    }

    private void initSelectedElementContainer() {
        selectedElementContainer = Lists.newArrayList();
        if (doc != null) {
            for (NodeContainer ec : doc.getSelectedRealElementContainerIterable()) {
                ModelElement me = ec.getElement();
                Class<? extends ModelElement> elementClass = me.getClass();
                if (!ModelConstants.isSlaveType(elementClass)) {
                    selectedElementContainer.add(ec);
                }
            }
        }
    }

    private JPanel initCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        int elementsCount = selectedElementContainer.size();

        if (rowSpinnerValue == -1 || rowSpinnerValue > elementsCount || colSpinnerValue > elementsCount) {
            int initialRows = new Double(Math.round(Math.sqrt(elementsCount))).intValue();
            int initialCols = initialRows - 1;
            while (initialRows * initialCols < elementsCount) {
                initialCols++;
            }
            rowSpinnerValue = initialRows;
            colSpinnerValue = initialCols;
        }

        if (vGapSpinnerValue == -1 || hGapSpinnerValue == -1) {
            vGapSpinnerValue = 10;
            hGapSpinnerValue = 10;
        }
        rowSpinner = new RaiseStepSpinner(Math.min(elementsCount, 1), elementsCount, rowSpinnerValue);
        rowSpinner.addChangeListener(this);

        colSpinner = new RaiseStepSpinner(Math.min(elementsCount, 1), elementsCount, colSpinnerValue);
        colSpinner.addChangeListener(this);

        vGapSpinner = new RaiseStepSpinner(-500, 500, vGapSpinnerValue);
        hGapSpinner = new RaiseStepSpinner(-500, 500, hGapSpinnerValue);
        sortAlphabeticalCheck = new JCheckBox();
        sortAlphabeticalCheck.setSelected(sortAlphabetical);

        centerPanel.add(new JLabel(getResString("ALIGN_DIALOG_COLUMN_COUNT")));
        centerPanel.add(colSpinner);
        centerPanel.add(new JLabel(getResString("ALIGN_DIALOG_ROW_COUNT")));
        centerPanel.add(rowSpinner);
        centerPanel.add(new JLabel(getResString("ALIGN_DIALOG_VGAP")));
        centerPanel.add(vGapSpinner);
        centerPanel.add(new JLabel(getResString("ALIGN_DIALOG_HGAP")));
        centerPanel.add(hGapSpinner);
        centerPanel.add(new JLabel(getResString("ALIGN_DIALOG_SORT_ALPHABETICAL")));
        centerPanel.add(sortAlphabeticalCheck);

        stateChanged(new ChangeEvent(colSpinner));
        return centerPanel;
    }

    private void saveValues() {
        rowSpinnerValue = getRows();
        colSpinnerValue = getCols();
        vGapSpinnerValue = getVGap();
        hGapSpinnerValue = getHGap();
        sortAlphabetical = sortAlphabeticalCheck.isSelected();
    }

    private JPanel initButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton okButton = initOkButton(this);
        JButton cancelButton = initCancelButton(this);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private static JButton initOkButton(final ElementAlignmentDialog dialog) {
        JButton okButton = new JButton(new AbstractAction(Tool3lgmConstants.getResString("ok")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dialog.saveValues();
                dialog.align();
                dialog.dispose();
            }
        });
        return okButton;
    }

    private static JButton initCancelButton(final ElementAlignmentDialog dialog) {
        JButton cancelButton = new JButton(new AbstractAction(Tool3lgmConstants.getResString("cancel")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dialog.dispose();
            }
        });
        return cancelButton;
    }

    public void align() {
        if (sortAlphabeticalCheck.isSelected()) {
            Alphabetical.sort(selectedElementContainer);
        }
        Rectangle largetsDimension = getLargestDimension();
        int rows = getRows();
        int cols = getCols();
        int hGap = getHGap();
        int vGap = getVGap();
        int pid = TransactionManager.STANDARD_PID;
        doc.start_transaction(pid);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int index = r * cols + c;
                if (index == selectedElementContainer.size()) {
                    break;
                }
                NodeContainer ec = selectedElementContainer.get(index);
                int w = largetsDimension.width;
                int h = largetsDimension.height;
                int x = largetsDimension.x + (w + vGap) * c;
                int y = largetsDimension.y + (h + hGap) * r;
                doc.coordinateKnot(ec, x, y, w, h, pid);
            }
        }
        doc.finish_transaction(pid);
        doc.distributeEvent(GraphDocument.DATA_CHANGED);
    }

    private Rectangle getLargestDimension() {
        Rectangle rect = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
        for (ElementContainer ec : selectedElementContainer) {
            rect.x = Math.min(rect.x, ec.getX());
            rect.y = Math.min(rect.y, ec.getY());
            rect.width = Math.max(rect.width, ec.getWidth());
            rect.height = Math.max(rect.height, ec.getHeight());
        }
        return rect;
    }

    private int getHGap() {
        int hGap = hGapSpinner.getValue().intValue();
        return hGap;
    }

    private int getVGap() {
        int vGap = vGapSpinner.getValue().intValue();
        return vGap;
    }

    private int getRows() {
        int rows = rowSpinner.getValue().intValue();
        return rows;
    }

    private int getCols() {
        int cols = colSpinner.getValue().intValue();
        return cols;
    }

    public static Action getElementAlignmentAction() {
        return new AbstractAction(getResString("ALIGN_DIALOG_ACTION_TITLE")) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ElementAlignmentDialog dialog = new ElementAlignmentDialog();
                dialog.setVisible(true);
            }

            @Override
            public boolean isEnabled() {
                return Static.getSelectedDoc() != null;
            }
        };
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        int elementCount = selectedElementContainer.size();
        RaiseStepSpinner changedSpinner = e.getSource() == rowSpinner ? rowSpinner : colSpinner;
        RaiseStepSpinner otherSpinner = changedSpinner == rowSpinner ? colSpinner : rowSpinner;
        int changedValue = changedSpinner.getValue().intValue();
        int otherSpinnerValue = elementCount / changedValue;
        if (elementCount % changedValue != 0) {
            otherSpinnerValue++;
        }
        otherSpinner.removeChangeListener(this);
        otherSpinner.setValue(new Double(otherSpinnerValue));
        otherSpinner.addChangeListener(this);
    }
}
