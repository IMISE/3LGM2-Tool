package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.UnionMetaPath;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * @author AXS (20 Mar 2019)
 */
public class ConnectPathDialog implements ActionListener {

    /**
     *
     */
    private final AlphabeticalComboBox pathChooser;

    /**
     *
     */
    private final AlphabeticalComboBox endElementChooser;

    /**
     *
     */
    private final GraphDocument doc;

    /**
     *
     */
    private Class<? extends ModelElement> lastPathEndClass = null;

    /**
     * @param unionMetaPath UnionMetaPath der nur aus SimpleMetaPaths bestehen sollte
     * @return
     */
    private static final SimpleMetaPath[] getSimpleMetaPathArray(final UnionMetaPath unionMetaPath) {
        SimpleMetaPath[] simpleMetaPaths = new SimpleMetaPath[unionMetaPath.getMetaPathCount()];
        int index = 0;
        for (AbstractMetaPath metaPath : unionMetaPath.iterableMetaPaths()) {
            simpleMetaPaths[index++] = metaPath instanceof SimpleMetaPath ? (SimpleMetaPath) metaPath : null;
        }
        return simpleMetaPaths;
    }

    /**
     * @param doc
     * @param simpleMetaPathsInUnionMetaPath
     *            UnionMetaPath der nur aus SimpleMetaPaths bestehen sollte
     */
    public ConnectPathDialog(final GraphDocument doc, final UnionMetaPath simpleMetaPathsInUnionMetaPath) {
        this(doc, getSimpleMetaPathArray(simpleMetaPathsInUnionMetaPath));
    }

    /**
     * @param owner
     * @param metaPaths
     */
    public ConnectPathDialog(final GraphDocument doc, final SimpleMetaPath... metaPaths) {
        this.doc = doc.getCollection().getMainGraphDocument();
        pathChooser = new AlphabeticalComboBox(metaPaths);
        endElementChooser = new AlphabeticalComboBox();
        pathChooser.addActionListener(this);
        pathChooser.setSelectedIndex(0);
    }

    private Object[] jOptionPaneMessage() {
        //den PathChooser nur anzeigen, wenn es auch eine Auswahl bei den Pfaden gibt
        if (pathChooser.getItemCount() > 1) {
            return new Object[] {
                    getResString("CONNECT_PATH_PATH_CHOOSER_LABEL"), pathChooser, getResString("CONNECT_PATH_ELEMENT_CHOOSER_LABEL"), endElementChooser
            };
        }
        return new Object[] {
                getResString("CONNECT_PATH_ELEMENT_CHOOSER_LABEL"), endElementChooser
        };
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == pathChooser) {
            SimpleMetaPath selectedPath = getSelectedPath();
            if (selectedPath != null) {
                Class<? extends ModelElement> newEndClass = selectedPath.getEndClass();
                if (lastPathEndClass != newEndClass) {
                    lastPathEndClass = newEndClass;
                    Set<ModelElement> endElements = new HashSet<>();
                    List<ModelElement> endClassElements = doc.getModelItems(newEndClass);
                    endElements.addAll(endClassElements);
                    MetaModel metaModel = doc.getMetaModel();
                    ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                    String createNew = getResString("new") + ": " + elementsNameBuilder.getDisplayableName(newEndClass);
                    endElementChooser.removeAllItems();
                    endElementChooser.addItem(createNew);
                    endElementChooser.addSeparator(false);
                    endElementChooser.addAll(endElements);
                }
            }
        }
    }

    public SimpleMetaPath getSelectedPath() {
        return pathChooser.getSelected(SimpleMetaPath.class);
    }

    public ModelElement getSelectedEndElement() {
        return endElementChooser.getSelected(ModelElement.class);
    }

    public boolean isCreateNewEndElementSelected() {
        //erstes ist NEU-Eintrag
        return endElementChooser.getSelectedIndex() == 0;
    }

    public boolean hasValidSelection() {
        return pathChooser.getSelectedIndex() >= 0 && endElementChooser.getSelectedIndex() >= 0;
    }

    public final boolean createDialog(final Component parentComponent) {
        JOptionPane pane = new JOptionPane(jOptionPaneMessage(), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        String title = getResString("CONNECT_PATH_TITLE");
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setVisible(true);
        return new Integer(JOptionPane.OK_OPTION).equals(pane.getValue());
    }

}
