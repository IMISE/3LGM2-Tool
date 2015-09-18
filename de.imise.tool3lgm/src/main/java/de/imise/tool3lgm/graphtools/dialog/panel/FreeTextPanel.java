package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;

/**
 * @author N.N
 * @create Long Time ago
 */
public class FreeTextPanel extends ElementDialogPanel {

    /**
     * COMMENTME
     */
    private final LimitedSizeScrollTextPane nameTextPane, descriptionTextPane;

    /**
     * COMMENTME
     */
    private final Class<? extends ModelElement> searchElementClass;

    /**
     * COMMENTME
     */
    private ModelElement dataElement;

    /**
     * COMMENTME
     */
    private String oldname = "", olddescrip = "";

    /**
     * @param searchElementClass
     * @param p
     */
    public FreeTextPanel(final Class<? extends ModelElement> searchElementClass, final ElementPropertyDialog p) {
        super(p);

        this.searchElementClass = searchElementClass;

        // setPreferredSize(new Dimension(450, 280));
        JPanel up = new JPanel();
        JPanel down = new JPanel();

        setLayout(new BorderLayout());
        up.setLayout(new BorderLayout());
        down.setLayout(new BorderLayout());

        JLabel label2 = new JLabel(Tool3lgmConstants.getResString("bez"));
        nameTextPane = new LimitedSizeScrollTextPane(4);

        up.add(label2, BorderLayout.WEST);
        up.add(nameTextPane, BorderLayout.CENTER);

        JLabel label = new JLabel(Tool3lgmConstants.getResString("description"));
        descriptionTextPane = new LimitedSizeScrollTextPane();
        down.add(label, BorderLayout.WEST);
        down.add(descriptionTextPane, BorderLayout.CENTER);

        add(up, BorderLayout.NORTH);
        add(down, BorderLayout.CENTER);

        nameTextPane.setText("");
        descriptionTextPane.setText("");

        // addMouseListener(this);
        init();
    }

    @Override
    protected final void init() {
        super.init();
        ArrayList<ModelElement> all = getModelElement().getConnectedElements(searchElementClass);
        if (all.size() > 0) {
            dataElement = all.get(0);
            oldname = dataElement.getName();
            nameTextPane.setText(oldname);
            olddescrip = dataElement.getDescription();
            descriptionTextPane.setText(olddescrip);
        }
        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {
        super.showFullDialog();
    }

    /**
     * @return
     */
    public LimitedSizeScrollTextPane getNameArea() {
        return nameTextPane;
    }

    /**
     * @return
     */
    public LimitedSizeScrollTextPane getBezArea() {
        return descriptionTextPane;
    }

    /**
     * @return
     */
    public Class<? extends ModelElement> getSearchElementClass() {
        return searchElementClass;
    }

    @Override
    public void update() {
        init();
    }

    // public void mousePressed(MouseEvent e) {
    // if (isPopupTrigger(e)) {
    // if (dataElement != null) {
    // if (!(dataElement.isSelected())) {
    // doc.select(dataElement, dialog.getTransactionID());
    // }
    // Tool3lgm.getContextGenerator().getTreeKnotContextMenu().show(e.getComponent(), e.getX() + 3,
    // e.getY() + 3);
    // }
    // }
    // }

    @Override
    public void commit() {
        // Ist null, wenn kein verbundenes Element vorhanden ist
        if (dataElement == null) {
            return;
        }
        String newName = nameTextPane.getText();
        if (newName != null && !oldname.equals(newName)) {
            doc.setName(dataElement, GraphDocument.getParseSaveString(newName), dialog.getTransactionID());
        }
        String newDescrip = descriptionTextPane.getText();
        if (newDescrip != null && !olddescrip.equals(newDescrip)) {
            doc.setDescription(dataElement, GraphDocument.getParseSaveString(newDescrip), dialog.getTransactionID());
        }
        dataElement.refreshText();
    }
}
