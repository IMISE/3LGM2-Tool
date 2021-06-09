package de.imise.tool3lgm.graphtools.dialog.element;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.graphtools.userfield.definition.SubType.DUMMY_SUBTYPE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.DummyGDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.SubType;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.ModelBrowserTree;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * An preview ElementPropertyDialog to show the effects of defining or changing
 * the {@link UserFieldDefinitions}
 *
 * @author AXS (28.04.2021)
 */
public class PreviewElementPropertyDialogCreator {

    /**
     * We need the last dividr location of the splitpane to restore it after
     * changing the content components in the splitpane.
     */
    private static int lastDividerLocation = -1;

    /**
     *
     */
    private final JSplitPane splitPane;

    /**
     * The modelbrowser tree that dislplays the dummy elements
     */
    private final ModelBrowserTree modelBrowserTree;

    /**
     * The last selected/displayed modelelement to detect the selection changed
     */
    private ModelElement lastDisplayedDialogElement = null;

    /**
     * The content component of the element's dialog without sybtypes. Its
     * dialog is used to display the content components also of the elements
     * with subtypes. The content components are taken from its dialog and put
     * back there when another element is to be selected and displayed.
     */
    private JComponent standardElementDialogContent;

    /** Border title for all dialog content components */
    private final String rightBorderTitle;

    /**
     * @param gdcoll
     * @param elementClass
     */
    private PreviewElementPropertyDialogCreator(final GDCollection gdcoll, final Class<? extends ModelElement> elementClass) {
        Tool3lgmModelType modelType = gdcoll.getModelType();
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
        DummyGDCollection dummyGDCollection = new DummyGDCollection(modelType);
        dummyGDCollection.setUserFieldDefinitions(definitions);
        rightBorderTitle = dummyGDCollection.getResString("eigensch_dial");

        //create a dummy element for every subtype
        for (SubType subType : definitions.getSubTypes(elementClass)) {
            dummyGDCollection.createElement(elementClass, subType);
        }

        //create a dummy element without a subtype (this elemnet is alway created)
        ModelElement me = dummyGDCollection.createElement(elementClass, DUMMY_SUBTYPE);
        LGMGraphDocument mainDoc = dummyGDCollection.getMainDoc();
        //select the element without subtype in the dummy model
        mainDoc.addToSelection(me.getID(), STANDARD_PID);
        //init model broweser an split pane
        modelBrowserTree = new ModelBrowserTree(mainDoc);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        //init the dialog with the property dialog of the element without subtype
        ElementPropertyDialog previewDialog = initPreviewDialog(dummyGDCollection);
        //show the dialog
        previewDialog.showDialog();
        TreePath selectionPath = modelBrowserTree.getSelectionPath();
        modelBrowserTree.scrollPathToVisible(selectionPath);

    }

    /**
     * @param gdcoll
     * @param elementClass
     */
    public static void showPreview(final GDCollection gdcoll, final Class<? extends ModelElement> elementClass) {
        new PreviewElementPropertyDialogCreator(gdcoll, elementClass);
    }

    /**
     * @param dialog
     * @return
     */
    private JComponent getDialogContent(final ElementPropertyDialog dialog) {
        Container contentPane = dialog.getContentPane();
        JComponent propertyDialogContent = (JComponent) contentPane.getComponent(0);
        propertyDialogContent.setBorder(BorderFactory.createTitledBorder(rightBorderTitle));
        return propertyDialogContent;
    }

    /**
     * @param dialog
     * @param content
     */
    private static void setDialogContent(final ElementPropertyDialog dialog, final Component content) {
        Container contentPane = dialog.getContentPane();
        contentPane.add(content);
    }

    /**
     * @param dummyGDColl
     * @return
     */
    private final ElementPropertyDialog initPreviewDialog(final GDCollection dummyGDColl) {
        LGMGraphDocument mainDoc = dummyGDColl.getMainDoc();
        ElementContainer ec = mainDoc.getLastSelected();
        lastDisplayedDialogElement = ec.getElement();

        //we use the dialog of the element without subtypes and replace only the
        //dialog content if a subtyped element is selected
        ElementPropertyDialog originalDialog = lastDisplayedDialogElement.getPropertyDialog();
        originalDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        //dialogs title -> "Preview"
        String dialogTitle = dummyGDColl.getResString("previewText");
        originalDialog.setTitle(dialogTitle);

        //resize the dialog to get space display the modelbrowser
        if (originalDialog.hasDefaultSize()) {
            originalDialog.resize(1.8d, 1.0d);
        }

        //init model browser sctollpane) and titled border
        JScrollPane modelBrowserTreeScrollPane = new JScrollPane(modelBrowserTree);
        String modelBrowserPanelTitle = dummyGDColl.getResString("PANEL_LABEL_MODEL_BROWSER_TITLE");
        modelBrowserTreeScrollPane.setBorder(BorderFactory.createTitledBorder(modelBrowserPanelTitle));

        //subtype elements have their dialog an we can put the content
        //back in the dialog to reuse it if the element is selected again
        //but we use here the dialog of the element without subtypes so
        //we must store the dialog content
        standardElementDialogContent = getDialogContent(originalDialog);

        //add the selection listener to the modelbrowser that updates
        //the dialog content component if the selected element in the
        //modelbrowser has changed
        SampleModelBrowserSelectionListener.addModelBrowserTreeSelectionListener(this);
        modelBrowserTree.expandAll();
        modelBrowserTree.selectObjects();

        //replace the dialogs content by the splitpane with the modelbrowser
        //at the left and the original element property dialog components
        //at the rigtht
        Container contentPane = originalDialog.getContentPane();
        contentPane.removeAll();

        splitPane.setLeftComponent(modelBrowserTreeScrollPane);
        splitPane.setRightComponent(standardElementDialogContent);
        contentPane.add(splitPane, BorderLayout.CENTER);

        //first start of the preview = static lastDividerLocation < 0
        //so give the modelbrowser one third
        if (lastDividerLocation < 0) {
            lastDividerLocation = Double.valueOf(0.3d * originalDialog.getWidth()).intValue();
        }
        splitPane.setDividerLocation(lastDividerLocation);

        //store last divider location to restore the value on next showing the dialog
        splitPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                try {
                    String propertyName = evt.getPropertyName();
                    if ("dividerLocation".equals(propertyName)) {
                        lastDividerLocation = (Integer) evt.getNewValue();
                    }
                } catch (Exception e) {
                    // ignore: handle exception
                }
            }
        });
        return originalDialog;
    }

    /**
     * @param selectedNode
     */
    private void update(final LGMTreeNode<?> selectedNode) {
        if (selectedNode != null) {
            TreeNode[] path = selectedNode.getPath();
            for (int i = path.length - 1; i > 0; i--) {
                TreeNode node = path[i];
                if (node instanceof ElementContainerTreeNode) {
                    ElementContainerTreeNode elementTreeNode = (ElementContainerTreeNode) node;
                    ModelElement me = elementTreeNode.getModelElement();
                    if (me != lastDisplayedDialogElement) {
                        //for subtype elements we must replace the dialogs content in the
                        //dialog to get it back if the same element will be selected again
                        if (lastDisplayedDialogElement.hasSubType()) {
                            Component rightComponent = splitPane.getRightComponent();
                            ElementPropertyDialog propertyDialog = lastDisplayedDialogElement.getPropertyDialog();
                            setDialogContent(propertyDialog, rightComponent);
                        }
                        lastDisplayedDialogElement = me;
                        JComponent dialogContent;
                        //from subtype elements we take the dialog content
                        if (me.hasSubType()) {
                            ElementPropertyDialog dialog = me.getPropertyDialog();
                            dialogContent = getDialogContent(dialog);
                        } else {
                            //the full dialog is the dialog of the element without subtypes -> set
                            //this special stored dialog content component again as dialogs content
                            dialogContent = standardElementDialogContent;
                        }
                        int dividerLocation = lastDividerLocation;
                        splitPane.setRightComponent(dialogContent);
                        splitPane.setDividerLocation(dividerLocation);

                    }
                }
            }
        }
    }

    /**
     * @author AXS (08.06.2021)
     */
    private static class SampleModelBrowserSelectionListener implements TreeSelectionListener {

        /**
         *
         */
        final PreviewElementPropertyDialogCreator dialogCreator;

        /**
         * @param dialogCreator
         */
        private SampleModelBrowserSelectionListener(final PreviewElementPropertyDialogCreator dialogCreator) {
            this.dialogCreator = dialogCreator;
            ModelBrowserTree modelBrowserTree = dialogCreator.modelBrowserTree;
            //only single selection is allowed
            modelBrowserTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            //remove all other selection liseteners which are isntalled by default for the modelbrowser tree
            for (TreeSelectionListener listener : modelBrowserTree.getTreeSelectionListeners()) {
                modelBrowserTree.removeTreeSelectionListener(listener);
            }
            modelBrowserTree.addTreeSelectionListener(this);
        }

        public static void addModelBrowserTreeSelectionListener(final PreviewElementPropertyDialogCreator dialogCreator) {
            new SampleModelBrowserSelectionListener(dialogCreator);
        }

        @Override
        public void valueChanged(final TreeSelectionEvent e) {
            TreePath newLeadSelectionPath = e.getNewLeadSelectionPath();
            if (newLeadSelectionPath != null) {
                Object lastPathComponent = newLeadSelectionPath.getLastPathComponent();
                if (lastPathComponent instanceof LGMTreeNode) {
                    dialogCreator.update((LGMTreeNode<?>) lastPathComponent);
                }
            }
        }

    }

}
