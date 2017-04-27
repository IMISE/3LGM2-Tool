package de.imise.tool3lgm.graphtools.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.panel.DescripPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogHeaderPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.StructurePanel;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.InTransactionListener;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.swing.component.TabbedPane;

/**
 * Eigenschaftsdialog für Modellelemnte, also Knoten und Kanten.<br>
 *
 * @author N.N., AXS
 */
public class ElementPropertyDialog extends AbstractPropertyDialog implements ActionListener, InTransactionListener {

    /**
     * COMMENTME
     */
    private final ModelElement modelElement;

    /**
     * COMMENTME
     */
    private final ElementDialogHeaderPanel headerPanel;

    /**
     * COMMENTME
     */
    static int lastWidth = -1;

    /**
     * COMMENTME
     */
    static int lastHeight = -1;

    private static final Dimension DEFAULT_SIZE = new Dimension(600, 500);

    /**
     * Wird <code>true</code>, wenn der Ok oder der Cancel Button gedrückt wurde
     */
    boolean closing = false;

    private final DescripPanel descripPanel;

    /**
     * @param modelElement
     * @param gdcoll
     */
    public ElementPropertyDialog(final ModelElement modelElement, final GDCollection gdcoll) {

        super(gdcoll);

        setTitle(Tool3lgmConstants.getResString("eigensch_dial"));

        getContentPane().setLayout(new BorderLayout());

        this.modelElement = modelElement;

        JPanel up = new JPanel(new GridLayout(1, 1));
        tab = new TabbedPane();
        tab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        headerPanel = new ElementDialogHeaderPanel(this);
        up.add(headerPanel);
        update();

        descripPanel = new DescripPanel(this);
        tab.addTab(Tool3lgmConstants.getResString("general"), descripPanel);

        // wenn es mind ein Userfield für diese Klasse gibt -> zeige das USerFieldPanel
        if (doc.getCollection().getUserFieldDefinitions().hasUserFields(modelElement.getClass())) {
            tab.addTab(Tool3lgmConstants.getResString("userfields"), new PropertyDialogUserFieldPanel(this));
        }

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());

        JPanel bp = new JPanel();
        okButton.addActionListener(this);
        bp.add(okButton);
        applyButton.addActionListener(this);
        bp.add(applyButton);
        cancelButton.addActionListener(this);
        bp.add(cancelButton);
        if (helpButton != null) {
            bp.add(helpButton);
        }

        buttonpanel.add(bp, BorderLayout.EAST);

        getContentPane().add(up, BorderLayout.NORTH);
        getContentPane().add(tab, BorderLayout.CENTER);
        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        pack();
        JFrame mainFrame = Static.getMainFrame();
        int xx = mainFrame.getX() + 100;
        int yy = mainFrame.getY() + 100;
        if (lastWidth == -1) {
            setLastWidth(DEFAULT_SIZE.width);
            setLastHeight(DEFAULT_SIZE.height);
        } else {
            for (int i = 0; i < ModelConstants.dialogs.size(); i++) {
                ElementPropertyDialog pd = ModelConstants.dialogs.get(i);
                if (pd.getLocation().x == xx && pd.getLocation().y == yy) {
                    xx += 20;
                    yy += 20;
                    i = -1;
                }
            }
            if (Toolkit.getDefaultToolkit().getScreenSize().width - xx < 150 || Toolkit.getDefaultToolkit().getScreenSize().height - yy < 150) {
                xx = mainFrame.getX() + 100;
                yy = mainFrame.getY() + 100;
            }
        }

        setLocation(xx, yy);
        setSize(lastWidth, lastHeight);

        addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(final ComponentEvent e) {
            }

            @Override
            public void componentMoved(final ComponentEvent e) {
                dialogPositionOrSizeChanged();
            }

            @Override
            public void componentResized(final ComponentEvent e) {
                dialogPositionOrSizeChanged();
            }

            @Override
            public void componentShown(final ComponentEvent e) {
            }
        });

        addPartOfStructurePanel();
    }

    private void addPartOfStructurePanel() {
        if (modelElement.canHaveParts()) {
            addTab(new StructurePanel(this));
        }
    }

    /**
     *
     */
    public void showDialog() {
        doc.start_transaction(getTransactionID());
        doc.addInTransactionListener(this);
        setVisible(true);
    }

    /**
     * @return
     */
    public ModelElement getModelElement() {
        return modelElement;
    }

    /**
     *
     */
    private void commit() {
        //alle Panels committen
        for (int m = 0; m < tab.getTabCount(); m++) {
            Component comp = tab.getComponentAt(m);
            if (comp instanceof ElementDialogPanel) {
                ((ElementDialogPanel) tab.getComponentAt(m)).commit();
            }
        }
        //alle anderen Dialoge updaten
        for (ElementPropertyDialog pd : ModelConstants.getDialogs()) {
            TabbedPane tp = pd.tab;
            // this wird in update klargemacht...
            if (pd != this) {
                for (int m = 0; m < tp.getTabCount(); m++) {
                    if (tp.getComponentAt(m) instanceof ElementDialogPanel) {
                        ((ElementDialogPanel) tp.getComponentAt(m)).update();
                    }
                }
            }
        }
    }

    /**
     * @param doUpdate
     */
    public void commit(final boolean doUpdate) {
        // System.out.println("ElementPropertyDialog commit "+ doUpdate);
        commit();
        if (doUpdate) {
            update();
        } else {
            ((ElementDialogPanel) tab.getSelectedComponent()).update();
        }
        doc.finish_transaction(getTransactionID());
        doc.distributeEvent(GraphDocument.DATA_CHANGED, getTransactionID());
        doc.start_transaction(createNewTransactionID());
    }

    public void cancel() {
        doc.finish_transaction(getTransactionID());
        doc.undo(getTransactionID());
        close();
    }

    private void close() {
        ModelConstants.removeDialog(modelElement);
        doc.finish_transaction(getTransactionID());
        doc.removeInTransactionListener(this);
        dispose();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == okButton) {
            closing = true;
            commit(false);
            close();
        } else if (e.getSource() == cancelButton) {
            closing = true;
            cancel();
        } else if (e.getSource() == applyButton) {
            commit(true);
        }
        doc.select(modelElement.getContainer(doc), getTransactionID());
        doc.distributeEvent(GraphDocument.SELECTION_CHANGED, getTransactionID());
    }

    @Override
    protected void processWindowEvent(final WindowEvent e) {
        super.processWindowEvent(e);
        if (!closing && e.getID() == WindowEvent.WINDOW_CLOSING) {
            closing = true;
            cancel();
        }
    }

    // InTransactionListener Begin
    // ###################################################################################

    @Override
    public void dataChanged(final GraphDocument source, final int pid) {
        if (Tool3lgm.DEBUG) {
            System.err.println(getClass().getSimpleName() + "dataChanged() " + modelElement + " " + source + " " + pid);
        }
        update();
    }

    @Override
    public void elementAdded(final GraphDocument source, final ElementContainer element) {
        update();
    }

    @Override
    public void elementDeleted(final GraphDocument source, final ElementContainer element) {
        update();
    }

    @Override
    public void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
    }

    @Override
    public void elementNameChanged(final ElementContainer ec) {
        update();
    }

    @Override
    public void userFieldValueChanged(final ElementContainer ec) {
        update();
    }

    // InTransactionListener Ende
    // ####################################################################################

    /**
     *
     */
    private void dialogPositionOrSizeChanged() {
        lastWidth = getWidth();
        lastHeight = getHeight();
    }

    /**
     *
     */
    public void update() {

        if (closing) {
            return;
        }

        headerPanel.update();
        for (int i = 0; i < tab.getTabCount(); i++) {
            Component c = tab.getComponentAt(i);
            if (c instanceof ElementDialogPanel) {
                ((ElementDialogPanel) c).update();
            }
        }
    }

    /**
     * @param lastHeight The lastHeight to set.
     */
    public static void setLastHeight(final int lastHeight) {
        ElementPropertyDialog.lastHeight = lastHeight;
    }

    /**
     * @param lastWidth The lastWidth to set.
     */
    public static void setLastWidth(final int lastWidth) {
        ElementPropertyDialog.lastWidth = lastWidth;
    }

    @Override
    public Dimension getDefaultSize() {
        return DEFAULT_SIZE;
    }

    ///////////////////////////////////////
    // DescriptionPanel -> add SubPanels //
    ///////////////////////////////////////

    public void addDescripSingleConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addDescripSingleConnectionPanel(false, edgeClasses);
    }

    public void addDescripSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        descripPanel.addSingleConnectionPanel(labelLastEdgeName, edgeClasses);
    }

    public void addDescripSingleConnectionInfoPanel(final Class<? extends Kante>... edgeClasses) {
        addDescripSingleConnectionInfoPanel(false, edgeClasses);
    }

    public void addDescripSingleConnectionInfoPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        descripPanel.addSingleConnectionInfoPanel(labelLastEdgeName, edgeClasses);
    }

    public void addDescripDescriptedSingleConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addDescripDescriptedSingleConnectionPanel(false, edgeClasses);
    }

    public void addDescripDescriptedSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        descripPanel.addDescriptedSingleConnectionPanel(labelLastEdgeName, edgeClasses);
    }

}