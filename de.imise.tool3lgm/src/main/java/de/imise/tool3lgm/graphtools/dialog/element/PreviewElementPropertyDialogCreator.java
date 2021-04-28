package de.imise.tool3lgm.graphtools.dialog.element;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.tree.ModelBrowserTree;

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
     * @param originalDialog
     */
    public static final void showPreview(final ElementPropertyDialog originalDialog) {
        originalDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        Container contentPane = originalDialog.getContentPane();
        GDCollection gdcoll = originalDialog.getCollection();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        mainDoc.selectAll();
        if (originalDialog.hasDefaultSize()) {
            originalDialog.resize(1.8d, 1.0d);
        }
        ModelBrowserTree modelBrowserTree = new ModelBrowserTree(mainDoc);
        modelBrowserTree.selectObjects();
        TreePath selectionPath = modelBrowserTree.getSelectionPath();
        DefaultMutableTreeNode elementNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        modelBrowserTree.expandNodeFull(elementNode);

        String dialogTitle = gdcoll.getResString("previewText");
        originalDialog.setTitle(dialogTitle);

        JScrollPane modelBrowserTreeScrollPane = new JScrollPane(modelBrowserTree);
        String modelBrowserPanelTitle = gdcoll.getResString("PANEL_LABEL_MODEL_BROWSER_TITLE");
        modelBrowserTreeScrollPane.setBorder(BorderFactory.createTitledBorder(modelBrowserPanelTitle));

        JComponent propertyDialogContent = (JComponent) contentPane.getComponent(0);
        String propertyDialofPanelTitle = gdcoll.getResString("eigensch_dial");
        propertyDialogContent.setBorder(BorderFactory.createTitledBorder(propertyDialofPanelTitle));

        contentPane.removeAll();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modelBrowserTreeScrollPane, propertyDialogContent);
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

        originalDialog.showDialog();
        modelBrowserTree.scrollPathToVisible(selectionPath);
    }

}
