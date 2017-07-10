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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.panel.DescripPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DescriptedSingleConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DoubleMeaningEdgePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogHeaderPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.MutipleCompositionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionLeafPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.StructurePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.TabbedPanel;
import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.undoredo.InTransactionListener;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.collections.CollectionUtils;

/**
 * Eigenschaftsdialog für Modellelemnte, also Knoten und Kanten.<br>
 *
 * @author N.N., AXS
 */
public class ElementPropertyDialog extends AbstractTabbedPropertyDialog implements ActionListener, InTransactionListener {

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

        JComponent tabComponent = getTabComponent();
        tabComponent.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel up = new JPanel(new GridLayout(1, 1));
        headerPanel = new ElementDialogHeaderPanel(this);
        up.add(headerPanel);
        update();

        descripPanel = new DescripPanel(this);
        addTab(Tool3lgmConstants.getResString("general"), descripPanel);
        addPartOfStructurePanel();

        // wenn es mind ein Userfield für diese Klasse gibt -> zeige das USerFieldPanel
        if (doc.getCollection().getUserFieldDefinitions().hasUserFields(modelElement.getClass())) {
            addTab(Tool3lgmConstants.getResString("userfields"), new PropertyDialogUserFieldPanel(this));
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
        getContentPane().add(tabComponent, BorderLayout.CENTER);
        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        addSizeOrPositionChangedListener();
        setSizeAndLocation();
    }

    private void addSizeOrPositionChangedListener() {
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
    }

    private void setSizeAndLocation() {
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
    }

    /**
     * Liefert alle {@link PartOfBeziehung}en, bei denen die Kindelemente auch wieder Kindelemente haben können (also wo die
     * Start- und Endklasse der PartOBeziehung gleich ist).
     *
     * @return
     */
    private List<Class<? extends PartOfBeziehung>> getRealPartOfs() {
        Class<? extends ModelElement> elementClass = modelElement.getClass();
        Class<? extends PartOfBeziehung>[] hasPartsEdgeClasses = ModelConstants.getHasPartsEdgeClasses(elementClass);
        Class<? extends PartOfBeziehung>[] isPartOfEdgeClasses = ModelConstants.getIsPartOfEdgeClasses(elementClass);
        List<Class<? extends PartOfBeziehung>> realPartOfs = Lists.newArrayList();
        for (Class<? extends PartOfBeziehung> partOf : isPartOfEdgeClasses) {
            if (CollectionUtils.arrayContains(hasPartsEdgeClasses, partOf)) {
                realPartOfs.add(partOf);
            }
        }
        return realPartOfs;
    }

    private void addPartOfStructurePanel() {
        List<Class<? extends PartOfBeziehung>> realPartOfs = getRealPartOfs();
        if (realPartOfs.size() == 1) {
            StructurePanel structurePanel = new StructurePanel(this, realPartOfs.get(0));
            structurePanel.setName(Tool3lgmConstants.getResString("strukt"));
            addTab(structurePanel);
        } else if (realPartOfs.size() > 1) {
            //TODO: hier könnte müsste man ein ExtraPanel mit dem Namen Struktur mit den Teil-Von-Beziehungen als UnterPanels anlegen, die mit der jeweiligen Kante benamt sind
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
        for (int m = 0; m < getTabCount(); m++) {
            Component comp = getTabComponentAt(m);
            if (comp instanceof ElementDialogPanel) {
                ((ElementDialogPanel) comp).commit();
            }
        }
        //alle anderen Dialoge updaten
        for (ElementPropertyDialog pd : ModelConstants.getDialogs()) {
            // this wird in update klargemacht...
            if (pd != this) {
                for (int m = 0; m < getTabCount(); m++) {
                    Component comp = getTabComponentAt(m);
                    if (comp instanceof ElementDialogPanel) {
                        ((ElementDialogPanel) comp).update();
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
            ElementDialogPanel selectedElementDialogPanel = getSelectedElementDialogPanel();
            if (selectedElementDialogPanel != null) {
                selectedElementDialogPanel.update();
            }
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
        for (int i = 0; i < getTabCount(); i++) {
            Component c = getTabComponentAt(i);
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

    @Override
    protected void addTab(final String title, final Icon icon, final Component component) {
        if (component instanceof DescriptedSingleConnectionPanel) {
            DescriptedSingleConnectionPanel panel = (DescriptedSingleConnectionPanel) component;
            panel.addSelf();
        }
        super.addTab(title, icon, component);
    }

    private TabbedPanel lastCreatedTabbedPanel;

    public void addTabbedPanel(final String nameResKey) {
        lastCreatedTabbedPanel = new TabbedPanel(this);
        lastCreatedTabbedPanel.setName(Tool3lgmConstants.getResString(nameResKey));
        addTab(lastCreatedTabbedPanel);
    }

    public void addTabbedPanelPathConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addTabbedPanelPathConnectionPanel(null, edgeClasses);
    }

    public void addTabbedPanelPathConnectionPanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        lastCreatedTabbedPanel.addTab(new PathConnectionPanel(this, true, searchElementClass, edgeClasses));
    }

    public void addDescripSingleConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addDescripSingleConnectionPanel(null, edgeClasses);
    }

    public void addDescripSingleConnectionPanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        addDescripSingleConnectionPanel(false, searchElementClass, edgeClasses);
    }

    public void addDescripSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        addDescripSingleConnectionPanel(labelLastEdgeName, null, edgeClasses);
    }

    public void addDescripSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        descripPanel.addSingleConnectionPanel(labelLastEdgeName, edgeClasses);
    }

    public void addDescripDescriptedSingleConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addDescripDescriptedSingleConnectionPanel(false, edgeClasses);
    }

    public void addDescripDescriptedSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        addDescripDescriptedSingleConnectionPanel(labelLastEdgeName, null, edgeClasses);
    }

    public void addDescripDescriptedSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        descripPanel.addDescriptedSingleConnectionPanel(labelLastEdgeName, edgeClasses);
    }

    public void addDescriptedSingleConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addTab(new DescriptedSingleConnectionPanel(this, edgeClasses));
    }

    public void addPathConnectionPanel(final Class<? extends Kante>... edgeClasses) {
        addPathConnectionPanel(false, edgeClasses);
    }

    public void addPathConnectionPanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        addPathConnectionPanel(false, searchElementClass, edgeClasses);
    }

    public void addPathConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        addPathConnectionPanel(labelLastEdgeName, null, edgeClasses);
    }

    public void addPathConnectionPanel(final boolean labelLastEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        addTab(new PathConnectionPanel(this, labelLastEdgeName, true, searchElementClass, edgeClasses));
    }

    public void addPathConnectionLeafPanel(final Class<? extends Kante>... edgeClasses) {
        addPathConnectionLeafPanel(false, edgeClasses);
    }

    public void addPathConnectionLeafPanel(final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        addTab(new PathConnectionLeafPanel(this, labelLastEdgeName, true, edgeClasses));
    }

    public void addPathConnectionInfoPanel(final Class<? extends Kante>... edgeClasses) {
        addTab(new PathConnectionPanel(this, false, edgeClasses));
    }

    public void addEdgePanel(final Class<? extends Kante> edgeClass) {
        addEdgePanel(null, edgeClass);
    }

    public void addEdgePanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass) {
        if (ModelConstants.isComposition(edgeClass)) {
            addTab(new MutipleCompositionPanel(this, searchElementClass, edgeClass.asSubclass(Composition.class)));
        } else if (ModelConstants.isDoubleMeaningEdge(edgeClass)) {
            addTab(new DoubleMeaningEdgePanel(this, searchElementClass, edgeClass));
        } else {
            addPathConnectionPanel(searchElementClass, edgeClass);
        }
    }

}
