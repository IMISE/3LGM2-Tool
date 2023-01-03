package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.xml.sax.SAXException;

import com.google.common.base.Strings;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Group;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.log.Log;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.swing.component.list.AlphabeticalJList;
import de.imise.util.swing.dialog.NameAndColorInputDialog;
import de.imise.util.swing.event.ConfirmDialogAction.ConfirmDialog;

/** @author thomas, AXS, xhb */
public class AnalysisEditor extends JDialog implements ActionListener, ConfirmDialog {

    /** The MetaModel for which the anal sysys should be created */
    private final MetaModel metaModel;

    /**
     * This component consists of two lists containing element classes. The
     * first one contains the element classes from which a partial analysis
     * starts. The second one contains the element classes that are the
     * condition for a partial analysis. Radio buttons are included. The radio
     * buttons are assigned to a button group. The radio buttons indicate the
     * characteristic of the condition. That means, whether an element should be
     * connected or not. Attached to the first list is a
     * <code>ListSelectionListener</code> that fills the subsequent list if it
     * already exists. Thus, when an element class is selected, in the
     * subsequent <code>PathStepComponent</code> the first list is filled with
     * the possible element classes that can be linked to the selected element.
     */
    private class PathStepComponent implements ListSelectionListener {

        /**
         * This ButtonGroup contains the two radio buttons that represent the
         * Condition. It contains the two RadioButtons that indicate whether an
         * element must or must not be connected to another.
         */
        private ButtonGroup bgroup;

        /**
         * The RadioButtons that indicate whether the selected element of the
         * first list must or must not be connected to the selected element(s)
         * of the addition list.
         */
        private JRadioButton connectedRadioBut, notConnectedRadioBut;

        /**
         * Contains the element classes that can be the start of an XML analysis
         * and that can be intermediate steps in an analysis chain.
         */
        private AlphabeticalJList<Class<? extends ModelElement>> pathStepElementTypeList;

        /**
         * Contains the elemnt classes that specify whether an element must
         * be/not be connected to a selected one from this list.
         */
        private AlphabeticalJList<Class<? extends ModelElement>> conditionElementTypeList;

        /** In this ScrollPane are the element classes */
        private JScrollPane scrollPaneTyp;

        /**
         * In this <code>JScrollPane</code> are the element classes for
         * constraints.
         */
        private JScrollPane scrollPaneverb;

        /** Header for the <code>scrollPaneTyp</code> */
        private JLabel pathElementCaption;

        /** Header for the radio buttons */
        private JLabel limitationCaption;

        /** @param listener */
        private PathStepComponent(final ActionListener listener) {
            config(listener);
        }

        /** Adds the lists and labels in the panel */
        public void addElements() {
            // TODO: Rewrite the analysis editors and parsers to write and read only English tags

            pathStepMainPanelConstraints.gridy++;
            pathStepMainPanelConstraints.gridx = 0;

            // The label for the list containing the element classes.
            pathStepMainPanel.add(pathElementCaption, pathStepMainPanelConstraints);

            pathStepMainPanelConstraints.gridx = 1;
            pathStepMainPanelConstraints.gridwidth = 2;

            // Label for the Condition RadioButtons
            pathStepMainPanel.add(limitationCaption, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridwidth = 1;

            pathStepMainPanelConstraints.gridx = 0;
            pathStepMainPanelConstraints.gridy++;
            pathStepMainPanelConstraints.gridheight = 2;

            // The list containing the element classes
            pathStepMainPanel.add(scrollPaneTyp, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridheight = 1;
            pathStepMainPanelConstraints.gridx++;

            // The radio buttons that indicate whether there should be a restriction.
            pathStepMainPanel.add(connectedRadioBut, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridy++;

            pathStepMainPanel.add(notConnectedRadioBut, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridx++;
            pathStepMainPanelConstraints.gridy--;
            pathStepMainPanelConstraints.gridheight = 2;

            // The list that contains the element classes with which constraints can be made.
            pathStepMainPanel.add(scrollPaneverb, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridheight = 1;

            pathStepMainPanel.revalidate();
            repaint();
            mainPanel.revalidate();
        }

        /**
         * Initializes the lists and labels Initially calls the method that adds
         * the GUI elements to the <code>pathStepMainPanel</code>.
         *
         * @param listener
         */
        private void config(final ActionListener listener) {

            pathElementCaption = new JLabel(" " + getResString("typeOfPathelement") + ":");
            limitationCaption = new JLabel(" " + getResString("restrictionForPathelement") + ":");
            pathStepElementTypeList = new AlphabeticalJList<>();
            conditionElementTypeList = new AlphabeticalJList<>();
            scrollPaneTyp = new JScrollPane(pathStepElementTypeList);

            scrollPaneverb = new JScrollPane(conditionElementTypeList);
            connectedRadioBut = new JRadioButton(getResString("connected_with"), true);
            notConnectedRadioBut = new JRadioButton(getResString("not_connected_with"), false);

            bgroup = new ButtonGroup();
            bgroup.add(connectedRadioBut);
            bgroup.add(notConnectedRadioBut);

            pathPanels.add(this);
            pathStepElementTypeList.addListSelectionListener(this);
            addElements();
        }

        /** @return */
        private boolean getConnectedState() {
            return connectedRadioBut.isSelected();
        }

        /**
         * Deletes itself from the graphical display. The lists, the
         * RadioButtons as well as the labels of the last added group are
         * deleted.
         */
        public void removeLastInsertetLists() {
            pathStepMainPanel.remove(scrollPaneverb);
            pathStepMainPanel.remove(scrollPaneTyp);
            pathStepMainPanel.remove(notConnectedRadioBut);
            pathStepMainPanel.remove(connectedRadioBut);
            pathStepMainPanel.remove(pathElementCaption);
            pathStepMainPanel.remove(limitationCaption);
            pathStepMainPanel.revalidate();
            pathStepMainPanelConstraints.gridy = pathStepMainPanelConstraints.gridy - 3;
        }

        /**
         * Fills the following lists
         *
         * @param newList
         */
        public void setValuesInNewList(final boolean newList) {

            int index = pathPanels.indexOf(this);

            if (newList) {
                index--;
            }

            PathStepComponent successorPanel = null;
            int panelElementNumber = pathPanels.size() - 1;
            if (newList) {
                panelElementNumber--;
            }

            if (this != pathPanels.get(panelElementNumber)) {
                successorPanel = pathPanels.get(index + 1);
            }
            if (successorPanel != null) {
                successorPanel.pathStepElementTypeList.removeAllElements();
            }
            conditionElementTypeList.removeAllElements();

            PathStepComponent quellPanel = pathPanels.get(index);
            List<Class<? extends ModelElement>> selectedPathStepElements = quellPanel.pathStepElementTypeList.getSelectedObjects();

            Class<? extends ModelElement>[] connectable = getConnectableElementClasses(selectedPathStepElements);

            for (int i = 0; i < connectable.length; i++) {
                String simpleElementClassName = connectable[i].getSimpleName();
                String resName = metaModel.getResString(simpleElementClassName);
                if (!newList) {
                    conditionElementTypeList.addObject(connectable[i], resName);
                }
                if (!selectedPathStepElements.isEmpty() && successorPanel != null) {
                    successorPanel.pathStepElementTypeList.addObject(connectable[i], resName);
                }

                conditionElementTypeList.revalidate();
                conditionElementTypeList.repaint();
            }
            if (successorPanel != null) {
                successorPanel.pathStepElementTypeList.repaint();
            }
        }

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            setValuesInNewList(false);
        }
    }

    /**
     * Returns an array of the classes of all elements that can be connected to
     * elements of the given type via some kind of edges.
     *
     * @param elementClasses
     */
    @SuppressWarnings("unchecked")
    private Class<? extends ModelElement>[] getConnectableElementClasses(final List<Class<? extends ModelElement>> elementClasses) {
        if (elementClasses == null || elementClasses.isEmpty()) {
            return new Class[0];
        }
        Set<Class<? extends ModelElement>> connectedTypes = new HashSet<>();
        for (int e = 0; e < elementClasses.size(); e++) {
            Class<? extends ModelElement> elementClass = elementClasses.get(e);
            Class<? extends Edge>[] edgeClasses = metaModel.getEdgeTypes(elementClass);
            for (int i = 0; i < edgeClasses.length; i++) {
                Class<? extends ModelElement> edgeElementClass = getStartClass(edgeClasses[i]);
                boolean selectedPathStartClassIsEdgeStartClass = false;
                if (edgeElementClass.isAssignableFrom(elementClass)) {
                    selectedPathStartClassIsEdgeStartClass = true;
                }
                if (selectedPathStartClassIsEdgeStartClass) {
                    edgeElementClass = getEndClass(edgeClasses[i]);
                }
                connectedTypes.add(edgeElementClass);
            }
        }
        Set<Class<? extends ModelElement>> allNonAbstractClasses = new HashSet<>(connectedTypes.size());
        for (Class<? extends ModelElement> c : connectedTypes) {
            allNonAbstractClasses.addAll(metaModel.getInstanciableAssignableClasses(c));
        }
        Class<? extends ModelElement>[] returnClasses = new Class[allNonAbstractClasses.size()];
        System.arraycopy(allNonAbstractClasses.toArray(), 0, returnClasses, 0, returnClasses.length);
        return returnClasses;
    }

    /**
     * Displays the AnalysisEditor.
     *
     * @param owner
     * @param metaModel
     */
    public static void showDialog(final JDialog owner, final MetaModel metaModel) {
        if (editor == null) {
            editor = new AnalysisEditor(owner, metaModel);
        }
        editor.setVisible(true);
    }

    /**
     * Displays the AnalysisEditor.
     *
     * @param owner
     * @param metaModel
     */
    public static void showDialog(final JFrame owner, final MetaModel metaModel) {
        if (editor == null) {
            editor = new AnalysisEditor(owner, metaModel);
        }
        editor.setVisible(true);
    }

    /**
     * List of all <code>PathStepComponent</code>s that can be used to enter
     * further path steps and conditions.
     */
    private final List<PathStepComponent> pathPanels = new ArrayList<>();

    /**
     * Button with which the last panel of a path step can be removed again
     */
    private final JButton addPathStepPanelBut = new JButton("+");

    /**
     * Button to add another panel for a path step
     */
    private final JButton removePathStepPanelBut = new JButton("-");

    /**
     * Button to exit the panel
     */
    private JButton exitButton;

    /** Panel containing all <code>PathStepComponent</code>s. */
    private JPanel pathStepMainPanel;

    /**
     * The Constraints with which the individual <code>PathStepComponent</code>s
     * are inserted into the <code>pathStepMainPanel</code>.
     */
    private final GridBagConstraints pathStepMainPanelConstraints = new GridBagConstraints();

    /** The main JPanel, where the lists and buttons are located. */
    private JPanel mainPanel;

    /** The instance of this class that will actually be displayed. */
    static AnalysisEditor editor = null;

    /**
     * @param owner
     * @param metaModel
     */
    private AnalysisEditor(final Frame owner, final MetaModel metaModel) {
        super(owner);
        this.metaModel = metaModel;
        init();
    }

    /**
     * @param owner
     * @param metaModel
     */
    private AnalysisEditor(final JDialog owner, final MetaModel metaModel) {
        super(owner);
        this.metaModel = metaModel;
        init();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(getResString("exit"))) {
            dispose();
        } else if (command.equals(getResString("ana_start"))) {
            GraphDocument selectedDoc = Static.getSelectedDoc();
            if (selectedDoc == null || !selectedDoc.getMetaModel().equals(metaModel)) {
                return;
            }
            PathStepComponent first = pathPanels.get(0);
            if (!(first.pathStepElementTypeList.isSelectionEmpty() && first.conditionElementTypeList.isSelectionEmpty())) {
                try {
                    MetaModelContext selectedMetaModelContext = selectedDoc.getMetaModelContext();
                    String analysisString = getAnalysisString(null);
                    XMLAnalysis analysis = XMLAnalysis.createAnalysis(selectedMetaModelContext, analysisString, IDSource.createIDString("ANA"));
                    analysis.setAnalysisResult(selectedDoc);
                } catch (SAXException e1) {
                    Log.log(Log.ERROR, "Can't execute analysis\n" + getAnalysisString(null));
                    // e1.printStackTrace();
                }
            }
        } else if (e.getSource().equals(addPathStepPanelBut)) {
            pathStepMainPanelConstraints.gridy++;
            PathStepComponent pathStepPanel = new PathStepComponent(this);

            removePathStepPanelBut.setEnabled(true);
            if (pathPanels.size() > 3) {
                pathStepMainPanel.revalidate();
                repaint();
            } else {
                pack();
            }
            pathStepPanel.setValuesInNewList(true);
        } else if (e.getSource().equals(removePathStepPanelBut)) {
            int c = pathPanels.size();
            if (c > 1) {
                PathStepComponent panelToRemove = pathPanels.get(c - 1);
                panelToRemove.removeLastInsertetLists();
                pathPanels.remove(c - 1);
                if (pathPanels.size() == 1) {
                    removePathStepPanelBut.setEnabled(false);
                }
            }
            if (pathPanels.size() > 3) {
                repaint();
            } else {
                pack();
            }
        } else if (command.equals(getResString("ana_insert_to_repository"))) {
            PathStepComponent first = pathPanels.get(0);
            if (!(first.pathStepElementTypeList.isSelectionEmpty() && first.conditionElementTypeList.isSelectionEmpty())) {
                NameAndColorInputDialog nd = new NameAndColorInputDialog(this);
                nd.showDialog(getResString("ana_name_title"), "");
                String name = nd.getInputString();
                if (name == null) {
                    name = "(null)";
                }
                XMLAnalysis toadd = null;
                MetaModelContext selectedMetaModelContext = Static.getSelectedMetaModelContext();
                try {
                    toadd = XMLAnalysis.createAnalysis(selectedMetaModelContext);
                    toadd.setName(name);
                    String analysisString = getAnalysisString(name);
                    toadd.setXMLText(analysisString);
                } catch (SAXException ex) {
                    Log.show(Log.ERROR, getResString("ANALYSIS_CANT_CREATE") + "\n" + ex.getMessage(), ex);
                }
                if (toadd != null) {
                    // if the AnalysesRepositoryFrame is visible, the new XML analysis is not
                    // immediately transferred to the standard repository, but first to the
                    if (AnalysesRepositoryFrame.dialog.isVisible()) {
                        AnalysesRepositoryFrame.addAnalysis(toadd, false);
                        AnalysesRepositoryFrame.table.update();
                        AnalysesRepositoryFrame.refreshActionStates();
                        // the editor was started without AnalysesRepositoryFrame -> write new
                        // XMLAnalysis immediately into the repository
                    } else {
                        AnalysesRepository.addAnalysis(toadd);
                        AnalysesRepository.saveRepository();
                        // update the copy of the analyses of the repository in the dialog as well
                        AnalysesRepositoryFrame.refreshAnalyses();
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        // set to null, because it is not certain whether the editor will be started again with
        // the same parent (this can be the main window or the AnalysesRepositoryFrame)
        AnalysisEditor.editor = null;
    }

    /**
     * @return XML-String of the XMLAnalysis
     */
    public String getAnalysisString(String name) {
        PathStepComponent firstListPanel = pathPanels.get(0);
        StringBuilder querystring = new StringBuilder("<?xml version=\"1.0\" encoding=\"ISO-8559-15\"?>\n<analyse>\n");
        if (!Strings.isNullOrEmpty(name)) {
            querystring.append("\t<name>\n");
            for (String lang : List.of("en", "de")) {
                querystring.append("\t\t<");
                querystring.append(lang);
                querystring.append(">");
                querystring.append(name);
                querystring.append("</");
                querystring.append(lang);
                querystring.append(">\n");
            }
            querystring.append("\t</name>\n");
        }
        querystring.append("\t<startknoten name=\"");
        List<Class<? extends ModelElement>> firstSelectionIndices = firstListPanel.pathStepElementTypeList.getSelectedObjects();
        int lastIndex = firstSelectionIndices.size() - 1;
        for (int j = 0; j < lastIndex; j++) {
            Class<? extends ModelElement> elementClass = firstSelectionIndices.get(j);
            String simpleElementClassName = elementClass.getSimpleName();
            querystring.append(simpleElementClassName);
            querystring.append(", ");
        }
        querystring.append(((Class<?>) firstSelectionIndices.get(lastIndex)).getSimpleName());
        querystring.append("\"/>\n");

        // The addition of the start class as the first search element is done because the specification in
        // the <startknoten> tag, in which no restrictions are made, is not sufficient.
        PathStepComponent first = pathPanels.get(0);
        querystring.append("\t<suche>\n");
        if (!first.pathStepElementTypeList.getSelectedValuesList().isEmpty()) {
            querystring.append("\t\t<typ>\n");
            List<Class<? extends ModelElement>> selectedIndices = first.pathStepElementTypeList.getSelectedObjects();
            for (int j = 0; j < selectedIndices.size(); j++) {
                Class<? extends ModelElement> elementClass = selectedIndices.get(j);
                String simpleElementClassName = elementClass.getSimpleName();
                querystring.append("\t\t\t<eintrag>" + simpleElementClassName + "</eintrag>\n");
            }
            querystring.append("\t\t</typ>\n");
        }
        querystring.append("\t\t<verbundenstate>" + (first.getConnectedState() ? "wahr" : "falsch") + "</verbundenstate>\n");
        if (!first.conditionElementTypeList.getSelectedValuesList().isEmpty()) {
            querystring.append("\t\t<verbundene>\n");
            List<Class<? extends ModelElement>> selectedIndices = first.conditionElementTypeList.getSelectedObjects();
            for (int j = 0; j < selectedIndices.size(); j++) {
                Class<? extends ModelElement> elementClass = selectedIndices.get(j);
                String simpleElementClassName = elementClass.getSimpleName();
                querystring.append("\t\t\t<eintrag>" + simpleElementClassName + "</eintrag>\n");
            }
            querystring.append("\t\t</verbundene>\n");
        }
        querystring.append("\t</suche>\n");

        for (int i = 1; i < pathPanels.size(); i++) {

            PathStepComponent current = pathPanels.get(i);
            // When lists are displayed that are either empty or have nothing selected.
            if (current.pathStepElementTypeList.getSelectedValuesList().isEmpty()) {
                break;
            }
            querystring.append("\t<suche>\n");
            if (!current.pathStepElementTypeList.getSelectedValuesList().isEmpty()) {
                querystring.append("\t\t<typ>\n");

                List<Class<? extends ModelElement>> selectedIndices = current.pathStepElementTypeList.getSelectedObjects();
                for (int j = 0; j < selectedIndices.size(); j++) {
                    Class<? extends ModelElement> elementClass = selectedIndices.get(j);
                    String simpleElementClassName = elementClass.getSimpleName();
                    querystring.append("\t\t\t<eintrag>" + simpleElementClassName + "</eintrag>\n");
                }
                querystring.append("\t\t</typ>\n");
            }
            querystring.append("\t\t<verbundenstate>" + (current.getConnectedState() ? true : false) + "</verbundenstate>\n");
            if (!current.conditionElementTypeList.getSelectedValuesList().isEmpty()) {
                querystring.append("\t\t<verbundene>\n");
                List<Class<? extends ModelElement>> selectedIndices = current.conditionElementTypeList.getSelectedObjects();
                for (int j = 0; j < selectedIndices.size(); j++) {
                    Class<? extends ModelElement> elementClass = selectedIndices.get(j);
                    String simpleElementClassName = elementClass.getSimpleName();
                    querystring.append("\t\t\t<eintrag>" + simpleElementClassName + "</eintrag>\n");
                }
                querystring.append("\t\t</verbundene>\n");
            }
            querystring.append("\t</suche>\n");
        }
        querystring.append("</analyse>\n");
        return querystring.toString();
    }

    /** Initializes the GUI-Compnents */
    private void init() {
        setTitle(getResString("analysis"));
        registerCtrlEnterKey();
        mainPanel = new JPanel();
        /* Panel "Basis-XMLAnalyse" */
        pathStepMainPanel = new JPanel();
        pathStepMainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pathStepMainPanel.setLayout(new GridBagLayout());
        pathStepMainPanelConstraints.insets = new Insets(0, 2, 6, 6);
        pathStepMainPanelConstraints.gridy = 0;
        pathStepMainPanelConstraints.gridx = 0;

        pathStepMainPanelConstraints.fill = GridBagConstraints.BOTH;
        pathStepMainPanelConstraints.weightx = 1.0;
        pathStepMainPanelConstraints.anchor = GridBagConstraints.NORTHWEST;

        PathStepComponent pathComponent = new PathStepComponent(this);

        for (Class<? extends ModelElement> elementClass : CollectionUtils.getCommonIterable(metaModel.allNodesSet, List.of(Group.class))) {
            String simpleElementClassName = elementClass.getSimpleName();
            String resName = metaModel.getResString(simpleElementClassName);
            pathComponent.pathStepElementTypeList.addObject(elementClass, resName);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addPathStepPanelBut);
        buttonPanel.add(removePathStepPanelBut);
        removePathStepPanelBut.setEnabled(false);
        removePathStepPanelBut.addActionListener(this);
        addPathStepPanelBut.addActionListener(this);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JScrollPane(pathStepMainPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        /* Panels End */
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        JButton but = new JButton(AnalysesRepositoryFrameActions.ACTION_RESET_ANALYSIS_RESULT);
        buttons.add(but);
        but = new JButton(getResString("ana_start"));
        but.addActionListener(this);
        buttons.add(but);
        but = new JButton(getResString("ana_insert_to_repository"));
        but.addActionListener(this);
        buttons.add(but);
        exitButton = new JButton(getResString("exit"));
        exitButton.addActionListener(this);
        buttons.add(exitButton);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(getOwner());
    }

    @Override
    public void confirm() {
        exitButton.doClick();
    }

}
