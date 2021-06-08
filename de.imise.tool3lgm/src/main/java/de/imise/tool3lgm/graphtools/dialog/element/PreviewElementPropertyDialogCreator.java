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
    *
    */
    private static int lastDividerLocation = -1;

    /**
     *
     */
    private final JSplitPane splitPane;

    /**
     *
     */
    private final ModelBrowserTree modelBrowserTree;

    /**
     *
     */
    private ModelElement lastDisplayedDialogElement = null;

    /**
     *
     */
    private JComponent standardElementDialogContent;

    /**
     *
     */
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
        for (SubType subType : definitions.getSubTypes(elementClass)) {
            dummyGDCollection.createElement(elementClass, subType);
        }
        ModelElement me = dummyGDCollection.createElement(elementClass, DUMMY_SUBTYPE);
        LGMGraphDocument mainDoc = dummyGDCollection.getMainDoc();
        mainDoc.addToSelection(me.getID(), STANDARD_PID);
        modelBrowserTree = new ModelBrowserTree(mainDoc);
        modelBrowserTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        ElementPropertyDialog previewDialog = initPreviewDialog(dummyGDCollection);
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

        ElementPropertyDialog originalDialog = lastDisplayedDialogElement.getPropertyDialog();
        originalDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        if (originalDialog.hasDefaultSize()) {
            originalDialog.resize(1.8d, 1.0d);
        }

        String dialogTitle = dummyGDColl.getResString("previewText");
        originalDialog.setTitle(dialogTitle);

        JScrollPane modelBrowserTreeScrollPane = new JScrollPane(modelBrowserTree);
        String modelBrowserPanelTitle = dummyGDColl.getResString("PANEL_LABEL_MODEL_BROWSER_TITLE");
        modelBrowserTreeScrollPane.setBorder(BorderFactory.createTitledBorder(modelBrowserPanelTitle));

        standardElementDialogContent = getDialogContent(originalDialog);

        SampleModelBrowserSelectionListener.addModelBrowserTreeSelectionListener(this);
        modelBrowserTree.expandAll();
        modelBrowserTree.selectObjects();

        Container contentPane = originalDialog.getContentPane();
        contentPane.removeAll();

        splitPane.setLeftComponent(modelBrowserTreeScrollPane);
        splitPane.setRightComponent(standardElementDialogContent);
        contentPane.add(splitPane, BorderLayout.CENTER);
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
                        if (lastDisplayedDialogElement.hasSubType()) {
                            Component rightComponent = splitPane.getRightComponent();
                            ElementPropertyDialog propertyDialog = lastDisplayedDialogElement.getPropertyDialog();
                            setDialogContent(propertyDialog, rightComponent);
                        }
                        lastDisplayedDialogElement = me;
                        JComponent dialogContent;
                        if (me.hasSubType()) {
                            ElementPropertyDialog dialog = me.getPropertyDialog();
                            dialogContent = getDialogContent(dialog);
                        } else {
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
