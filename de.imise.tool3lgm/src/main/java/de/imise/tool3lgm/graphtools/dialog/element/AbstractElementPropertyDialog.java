package de.imise.tool3lgm.graphtools.dialog.element;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jena.ext.com.google.common.collect.Lists;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.dialog.AbstractTabbedPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.panel.DescripPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogHeaderPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.StructurePanel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.metapaths.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.userfield.definition.SubType;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldList;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.Sys;

/**
 * @author AXS (02.12.2019)
 */
public class AbstractElementPropertyDialog extends AbstractTabbedPropertyDialog implements ActionListener, LGMChangeListenerSimple, ChangeListener {

    /**
     * ModelElement its properties are displayed or changable in this dialog.
     */
    private final ModelElement modelElement;

    /**
     * The subtype of the modelelement to detect the change of the subtype
     * during this dialog is open.
     */
    private SubType subType;

    /**
     * If the modelElement is a copy from a template element so this variable
     * strores the source modelElement from the template. If the dialog should
     * be opened for a template element, so the the function
     * {@link Static#showPropertyDialog(ElementContainer)} always opens the
     * dialog for the element in the current selected model and not the template
     * element - but omly if it exists in the model. If not the dialog will be
     * opened for the template element.
     */
    private final ModelElement templateElementSource;

    /**
     * Panel with the type, name, id and ceration date of the dialogs element.
     */
    private final ElementDialogHeaderPanel headerPanel;

    /**
     * Default size of this dialogs
     */
    private static final Dimension DEFAULT_SIZE = new Dimension(600, 500);

    /**
     * Default position of this dialogs relatively to the owner
     */
    private static final Point DEFAULT_POSITION = new Point(100, 100);

    /**
     * Wird im Konstruktor auf <code>true</code> gesetzt und nach dem ersten
     * zeigen des Dialoges auf <code>false</code>. Damit kann sicher gestellt
     * werden, dass die Transaktion des Dialoges nur einmal am Anfang und nicht
     * bei jedem weiteren Zeigen des Dialoges gestartet wird.
     */
    boolean opening = false;

    /**
     * Wird <code>true</code>, wenn der Ok oder der Cancel Button gedrückt wurde
     */
    boolean closing = false;

    /** Das Panel des Allgemein-Reiters */
    protected final DescripPanel descripPanel;

    /** Die Panels für die benutzerdefinierten Eigenschaften */
    private final Set<PropertyDialogUserFieldPanel> propertyDialogUserFieldPanels = new HashSet<>();

    /**
     * Panel in the south with the panel for the buttons OK, Take over and
     * Cancel in the EAST and an additional button that is given by the curren
     * displayed panel in the WEST.
     */
    private final JPanel southButtonsPanel = new JPanel();

    /**
     * One additional button of the currently displayed panel to add to the
     * button panel with the OK, Cancel, TakeOver buttons.
     */
    private JButton panelButton = null;

    /**
     * @param modelElement
     * @param gdcoll
     */
    public AbstractElementPropertyDialog(final ModelElement modelElement) {
        super(modelElement.getCollection());

        subType = modelElement.getSubType();

        //add changeListener for tab changes to updates the displayed panel depending buttons
        //must be added before adding the tabs to get the very first tab change event
        tabbedPane.addChangeListener(this);

        setTitle(getResString("eigensch_dial"));
        this.modelElement = modelElement;
        templateElementSource = Static.getTemplateElement(modelElement);

        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel up = new JPanel(new GridLayout(1, 1));
        headerPanel = new ElementDialogHeaderPanel(this);
        up.add(headerPanel);

        descripPanel = new DescripPanel(this);
        addTab(getResString("general"), descripPanel);
        addPartOfStructurePanel();

        addUserFieldTabs();

        JPanel standardButtonsPanel = new JPanel();
        southButtonsPanel.setLayout(new BorderLayout());

        okButton.addActionListener(this);
        standardButtonsPanel.add(okButton);
        if (!isInfoDialog()) {
            applyButton.addActionListener(this);
            standardButtonsPanel.add(applyButton);
            cancelButton.addActionListener(this);
            standardButtonsPanel.add(cancelButton);
        }
        if (helpButton != null) {
            standardButtonsPanel.add(helpButton);
        }

        southButtonsPanel.add(standardButtonsPanel, BorderLayout.EAST);

        Container realContentPane = getContentPane();
        realContentPane.setLayout(new BorderLayout());
        JPanel contentPane = new JPanel(new BorderLayout());
        realContentPane.add(contentPane, BorderLayout.CENTER);

        contentPane.add(up, BorderLayout.NORTH);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        contentPane.add(southButtonsPanel, BorderLayout.SOUTH);

        restoreSizeAndPosition();
        opening = true;

    }

    /**
     *
     */
    private void addUserFieldTabs() {
        // wenn es mind ein Userfield für diese Klasse gibt -> zeige das USerFieldPanel
        UserFieldDefinitions userFieldDefinitions = gdcoll.getUserFieldDefinitions();
        Class<? extends ModelElement> modelElementClass = getModelElementClass();
        SubType subType = modelElement instanceof Node ? ((Node) modelElement).getSubType() : null;
        List<UserFieldList> tabSubLists = userFieldDefinitions.getTabSubLists(modelElementClass, subType);
        for (UserFieldList userFieldList : tabSubLists) {
            PropertyDialogUserFieldPanel userFieldPanel = new PropertyDialogUserFieldPanel(this, userFieldList);
            propertyDialogUserFieldPanels.add(userFieldPanel);
            addTab(userFieldPanel);
        }
    }

    /**
     * @return the ModelElement this dialog is shown for
     */
    public final ModelElement getModelElement() {
        return modelElement;
    }

    /**
     * @return the class of the ModelElement this dialog is shown for
     */
    public final Class<? extends ModelElement> getModelElementClass() {
        return modelElement.getClass();
    }

    /**
     * @return the template element the dialogs model element is created from or
     *         <code>null</code> if it not created by a template element
     */
    public final ModelElement getTemplateElementSource() {
        return templateElementSource;
    }

    /**
     * @return a collection with at least the modelElement from this dialog and
     *         if exists additionally the template element the modelElement was
     *         created from
     */
    public final Collection<ModelElement> getModelElementWithTemplateElement() {
        if (templateElementSource == null) {
            return Lists.newArrayList(modelElement);
        }
        return Lists.newArrayList(modelElement, templateElementSource);
    }

    /**
     * @return <code>true</code> if this dialog only presents information but
     *         nothing is ediable/changeable:
     */
    public final boolean isInfoDialog() {
        if (gdcoll.getModelCategory() == ModelCategory.TEMPLATE) {
            return true;
        }
        if (Static.isExpertMode()) {
            return false;
        }
        Class<? extends ModelElement> dialogElementClass = modelElement.getClass();
        MetaModel metaModel = getMetaModel();
        return !metaModel.isEditable(dialogElementClass);
    }

    /**
     * Bleibt <code>true</code>, wenn keine Unterklasse den Dialog erweitert,
     * sondern der Dialog nur aus dem Allgemein-Reiter besteht, auf dem auch
     * nichts durch eine Unterklasse hinzugefügt wurde.
     */
    protected final boolean isUnchangedDefaultDialog() {
        if (!descripPanel.isUnchangedDefaultPanel()) {
            return false;
        }
        int tabCount = getTabCount();
        if (tabCount == 0 || tabCount > 2) {
            return false;
        }
        if (getTabComponentAt(0) != descripPanel) {
            return false;
        }
        if (tabCount == 2 && !propertyDialogUserFieldPanels.contains(getTabComponentAt(1))) {
            return false;
        }
        return true;
    }

    /**
     * Adds the panel 'Structure' to the dialog, which shows the
     * part-of-hierarchy of the element of the dialog, if this element has a
     * {@link HasPartEdge}.
     */
    private void addPartOfStructurePanel() {
        List<Class<? extends HasPartEdge>> realPartOfs = getRecursiveHasPartEdges();
        if (realPartOfs.size() == 1) {
            StructurePanel structurePanel = new StructurePanel(this, realPartOfs.get(0));
            structurePanel.setName(getResString("strukt"));
            addTab(structurePanel);
        } else if (realPartOfs.size() > 1) {
            //TODO: hier könnte müsste man ein ExtraPanel mit dem Namen Struktur mit den Teil-Von-Beziehungen als UnterPanels anlegen, die mit der jeweiligen Edge benamt sind
        }
    }

    /**
     * Liefert alle {@link HasPartEdge}en, bei denen die Kindelemente auch
     * wieder Kindelemente haben können (also wo die Start- und Endklasse der
     * PartOBeziehung gleich ist).
     *
     * @return
     */
    private List<Class<? extends HasPartEdge>> getRecursiveHasPartEdges() {
        List<Class<? extends HasPartEdge>> recursiveHasPartEdges = new ArrayList<>();
        Class<? extends ModelElement> elementClass = modelElement.getClass();
        MetaModel metaModel = modelElement.getMetaModel();
        Class<? extends Edge>[] edgeTypes = metaModel.getEdgeTypes(elementClass);
        for (Class<? extends Edge> edgeClass : edgeTypes) {
            if (metaModel.isRecursiveHasPartEdge(edgeClass)) {
                Class<? extends HasPartEdge> hasPartEdgeClass = edgeClass.asSubclass(HasPartEdge.class);
                if (HasPartEdge.isParentClass(hasPartEdgeClass, elementClass)) {
                    recursiveHasPartEdges.add(hasPartEdgeClass);
                }
            }
        }
        return recursiveHasPartEdges;
    }

    /**
     * @param searchElementClass
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public final SequenceMetaPath createSequenceMetaPath(@Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        if (edgeClasses.length == 1) {
            return createSequenceMetaPath(searchElementClass, edgeClasses[0]);
        }
        return SimpleMetaPathCreator.createSimpleMetaPath(modelElement, searchElementClass, edgeClasses);
    }

    /**
     * @param searchElementClass
     * @param edgeClass
     * @return
     */
    public final SequenceMetaPath createSequenceMetaPath(@Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        ElementaryMetaPathHandler emph = modelElement.getElementaryMetaPathHandler();
        Class<? extends ModelElement> metaPathStartClass = modelElement.getClass();
        return emph.getMetaPath(metaPathStartClass, edgeClass, searchElementClass);
    }

    /**
     *
     */
    public void showDialog() {
        if (opening) {
            mainDoc.start_transaction(transactionID);
            mainDoc.addAllTransactionsListener(this);
            opening = false;
        }
        setVisible(true);
    }

    /**
     *
     */
    public void update() {
        if (closing) {
            return;
        }

        SubType oldSubType = subType;
        subType = modelElement.getSubType();

        if (oldSubType != subType) {
            Sys.err1("Jetzt alle Panels neu machen: Subtyp alt = " + oldSubType);
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
        for (ElementPropertyDialog pd : ElementPropertyDialogsContext.iterateDialogs()) {
            // this wird in update klargemacht...
            if (pd != this) {
                for (int m = 0; m < pd.getTabCount(); m++) {
                    Component comp = pd.getTabComponentAt(m);
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
        mainDoc.finish_transaction(transactionID);
        mainDoc.distributeEvent(DATA_CHANGED, transactionID);
        mainDoc.start_transaction(createNewTransactionID());
    }

    /**
     *
     */
    public void cancel() {
        mainDoc.finish_transaction(transactionID);
        mainDoc.undo(transactionID);
        close();
    }

    /**
     *
     */
    private void close() {
        ElementPropertyDialogsContext.removeDialog(modelElement);
        mainDoc.finish_transaction(transactionID);
        mainDoc.removeAllTransactionsListener(this);
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
        ElementContainer ec = modelElement.getContainer(mainDoc);
        mainDoc.select(ec, transactionID);
        mainDoc.distributeEvent(SELECTION_CHANGED, transactionID);
    }

    @Override
    protected void processWindowEvent(final WindowEvent e) {
        super.processWindowEvent(e);
        if (!closing && e.getID() == WindowEvent.WINDOW_CLOSING) {
            closing = true;
            cancel();
        }
    }

    // GDCollectionChangeListener Begin
    // ###################################################################################

    @Override
    public final void dataChanged(final GraphDocument source) {
        update();
    }

    @Override
    public final void elementNameChanged(final ElementContainer ec) {
        update();
    }

    @Override
    public final void userFieldValueChanged(final UserFieldTarget userFieldTarget) {
        update();
    }

    // GDCollectionChangeListener End
    // ####################################################################################

    // Size and Location Begin
    // ####################################################################################

    @Override
    public final void stateChanged(final ChangeEvent e) {
        //if the final must be removed because subclasses will overwite it - so don't forget
        //to call this super implementation!

        //tab changed -> change the view collapse/expand button in the west of the south panel
        if (e.getSource() == tabbedPane) {
            Component selectedTabComponent = tabbedPane.getSelectedComponent();
            if (panelButton != null) {
                southButtonsPanel.remove(panelButton);
                panelButton = null;
                //revalidateRepaint is needed to really remove the button of the last panel
                southButtonsPanel.revalidate();
                southButtonsPanel.repaint();
            }
            if (selectedTabComponent instanceof ElementDialogPanel) {
                ElementDialogPanel elementDialogPanel = (ElementDialogPanel) selectedTabComponent;
                JButton currentPanelButton = elementDialogPanel.getPanelButton();
                if (currentPanelButton != null) {
                    panelButton = currentPanelButton;
                    southButtonsPanel.add(panelButton, BorderLayout.WEST);
                }
            }
        }
    }

    @Override
    public Dimension getDefaultSize() {
        return DEFAULT_SIZE;
    }

    /**
     * @return <code>true</code> if the dialog has the default size
     */
    public boolean hasDefaultSize() {
        Dimension size = getSize();
        Dimension defaultSize = getDefaultSize();
        return size.equals(defaultSize);
    }

    /**
     * @param xFactor Changes the width by multiplying the current width by the
     *            passed xFactor.
     * @param yFactor Changes the height by multiplying the current height by
     *            the passed yFactor.
     */
    public void resize(final double xFactor, final double yFactor) {
        Dimension size = getSize();
        size.width *= xFactor;
        size.height *= yFactor;
        setSize(size);
    }

    @Override
    public Point getDefaultPosition() {
        return DEFAULT_POSITION;
    }

    @Override
    public int getNextDialogPositionOffset() {
        return 20;
    }

}
