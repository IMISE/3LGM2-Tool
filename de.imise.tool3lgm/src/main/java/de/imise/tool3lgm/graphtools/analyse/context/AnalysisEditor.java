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

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.log.Log;
import de.imise.util.swing.component.list.AlphabeticalJList;
import de.imise.util.swing.dialog.NameAndColorInputDialog;

/** @author thomas, AXS, xhb */
public class AnalysisEditor extends JDialog implements ActionListener {

    /** Das MetaModel, für das die Analsysen erstellt werden sollen */
    private final MetaModel metaModel;

    /**
     * Diese Componente besteht aus zwei Listen, die Elementklassen enthalten. In der ersten sind
     * die Elemteklassen enthalten, von denen eine Teilanalyse ausgeht. In der zweiten sind die
     * Elemtklassen enthalten, die die Bedingung für eine Teilanalyse darstellen. Es sind
     * Radiobutton enthalten. Die Radiobuttons sind einer ButtonGroup zugeordnet. Die Radiobuttons
     * geben die Ausprägung der Condition an. D.h. Soll ein Element verbunden sein oder nicht? An
     * die erste Liste ist ein <code>ListSelectionListener</code> geknüpft, der die nachfolgende
     * Liste füllt, wenn sie schon existiert. Wenn also eine Elementklasse selektiert wird, werden
     * in der nachfolgenen <code>PathStepComponent</code> die erste Liste mit den möglichen
     * Elementklassen gefüllt, die mit dem selektierten Element verbunden sein können.
     */
    private class PathStepComponent implements ListSelectionListener {

        /**
         * Diese ButtonGroup beinhaltet die zwei Radiobuttons, die die Condition darstellen. Es sind
         * die zwei RadioButtons enthalten, die angeben, ob ein Element mit einem anderen verbunden
         * sein muss oder nicht verbunden sein darf.
         */
        private ButtonGroup bgroup;

        /**
         * Die RadioButtons, die angeben, ob das selektierte Element der ersten Liste mit dem(n)
         * selektierten Element(en) der Eingränzungsliste verbunden sein muss oder nicht sein darf.
         */
        private JRadioButton connectedRadioBut, notConnectedRadioBut;

        /**
         * Enthält die Elementklassen, die start einer XMLAnalyse sein können und die
         * Zwischenschritte in einer Analysekette sein können.
         */
        private AlphabeticalJList pathStepElementTypeList;

        /**
         * Enthält die Elemntklassen, die angeben, ob ein Element mit einem seletierten aus dieser
         * Liste verbunden sein muss/nicht verbunden sein darf.
         */
        private AlphabeticalJList conditionElementTypeList;

        /** In diesem scrollPane sind die Elementklassen */
        private JScrollPane scrollPaneTyp;

        /** In diesem <code>JScrollPane</code> sind die Elementklassen für Einschränkungen */
        private JScrollPane scrollPaneverb;

        /** Die Überschrift für das <code>scrollPaneTyp</code> */
        private JLabel pathElementCaption;

        /** Die Überschrift für die RadioButtons */
        private JLabel limitationCaption;

        /** @param listener */
        private PathStepComponent(final ActionListener listener) {
            super();
            config(listener);
        }

        /** Fügt die Listen und Beschriftungen im Panel hinzu */
        public void addElements() {
            // TODO: für unsere englischen Freunde auslagern. Dann aber auch gleich die Analyseeditoren und
            // parser so umschreiben, dass sie nur noch englische Tags schreiben und lesen

            pathStepMainPanelConstraints.gridy++;
            pathStepMainPanelConstraints.gridx = 0;

            // Die Beschriftung für die Liste, die die Elementklassen enthält.
            pathStepMainPanel.add(pathElementCaption, pathStepMainPanelConstraints);

            pathStepMainPanelConstraints.gridx = 1;
            pathStepMainPanelConstraints.gridwidth = 2;

            // Beschriftung für die EingrenzungsRadioButtons
            pathStepMainPanel.add(limitationCaption, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridwidth = 1;

            pathStepMainPanelConstraints.gridx = 0;
            pathStepMainPanelConstraints.gridy++;
            pathStepMainPanelConstraints.gridheight = 2;

            // Die Liste, die die Elementklassen enthält
            pathStepMainPanel.add(scrollPaneTyp, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridheight = 1;
            pathStepMainPanelConstraints.gridx++;

            // Die Radiobuttons, die angeben, ob eine Einschränkung vorliegen soll.
            pathStepMainPanel.add(connectedRadioBut, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridy++;

            pathStepMainPanel.add(notConnectedRadioBut, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridx++;
            pathStepMainPanelConstraints.gridy--;
            pathStepMainPanelConstraints.gridheight = 2;

            // Die Liste, die die Elementklassen enthält, mit den Einschränkungen getroffen werden können.
            pathStepMainPanel.add(scrollPaneverb, pathStepMainPanelConstraints);
            pathStepMainPanelConstraints.gridheight = 1;

            pathStepMainPanel.revalidate();
            repaint();
            mainPanel.revalidate();
        }

        /**
         * Initialisiert die Listen und Labels Ruft initial die Methode auf, die die GUI-Elemente
         * zum <code>pathStepMainPanel</code> hinzufügt.
         *
         * @param listener
         */
        private void config(final ActionListener listener) {

            pathElementCaption = new JLabel(" " + getResString("typeOfPathelement") + ":");
            limitationCaption = new JLabel(" " + getResString("restrictionForPathelement") + ":");
            pathStepElementTypeList = new AlphabeticalJList();
            conditionElementTypeList = new AlphabeticalJList();
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
         * Löscht sich selbst aus der grafischen Anzeige. Es werden die Listen, die RadioButtons
         * sowie die Beschriftungen der zuletzt hinzugefügten Gruppe gelöscht.
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
         * Füllt die nachfolgenden Listen
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
            Object[] selectedPathStepElements = quellPanel.pathStepElementTypeList.getSelectedObjects();

            Class<? extends ModelElement>[] connectable = getConnectableElementClasses(selectedPathStepElements);

            for (int i = 0; i < connectable.length; i++) {
                String simpleElementClassName = connectable[i].getSimpleName();
                String resName = metaModel.getResString(simpleElementClassName);
                if (!newList) {
                    conditionElementTypeList.addItem(connectable[i], resName);
                }
                if (selectedPathStepElements.length > 0 && successorPanel != null) {
                    successorPanel.pathStepElementTypeList.addItem(connectable[i], resName);
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
     * Liefert ein Array der Klassen aller Elemente, die mit Elementen der übergebenen Art über
     * irgendeine Art von Kanten verbunden sein können.
     *
     * @param elementClassArray
     */
    @SuppressWarnings("unchecked")
    private Class<? extends ModelElement>[] getConnectableElementClasses(final Object[] elementClassArray) {
        if (elementClassArray == null || elementClassArray.length == 0) {
            return new Class[0];
        }
        Set<Class<? extends ModelElement>> connectedTypes = new HashSet<>();
        for (int e = 0; e < elementClassArray.length; e++) {
            Class<? extends ModelElement> elementClass = ((Class<?>) elementClassArray[e]).asSubclass(ModelElement.class);
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
     * Zeigt den AnalysisEditor an.
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
     * Zeigt den AnalysisEditor an.
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
     * Liste aller <code>PathStepComponent</code>s, über die weitere Pfadschritte und bedinungen
     * eingegeben werden können
     */
    private final List<PathStepComponent> pathPanels = new ArrayList<>();

    /** Button, mit dem das letzte Panel eines Pfadschrittes wieder entfernt werden kann */
    private final JButton addPathStepPanelBut = new JButton("+"/* getResString("erw") */);

    /** Button, mit dem ein weiteres Panel für einen Pfadschritt hinzugefügt werden kann */
    private final JButton removePathStepPanelBut = new JButton("-"/* getResString("vereinfachen") */);

    /** Panel, das alle <code>PathStepComponent</code>s enthält */
    private JPanel pathStepMainPanel;

    /**
     * Die Constraints mit denen die einzelnen <code>PathStepComponent</code>s in das <code>pathStepMainPanel</code> eingefügt werden.
     */
    private final GridBagConstraints pathStepMainPanelConstraints = new GridBagConstraints();

    /** Das haupt JPanel, in dem die Listen und Buttons untergebracht sind. */
    private JPanel mainPanel;

    /** Die Instanz dieser Klasse, die dann tatsächlich angezeigt wird. */
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
                    XMLAnalysis.createAnalysis(selectedDoc.getMetaModelContext(), getAnalysisString()).setAnalysisResult(selectedDoc);
                } catch (SAXException e1) {
                    Log.log(Log.ERROR, "Can't execute analysis\n" + getAnalysisString());
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
                String val = nd.getInputString();
                if (val == null) {
                    val = "(null)";
                }
                XMLAnalysis toadd = null;
                try {
                    toadd = XMLAnalysis.createAnalysis(Static.getSelectedMetaModelContext(), val, getAnalysisString());
                } catch (SAXException ex) {
                    Log.show(Log.ERROR, getResString("ANALYSIS_CANT_CREATE") + "\n" + ex.getMessage(), ex);
                }
                if (toadd != null) {
                    // wenn der AnalysesRepositoryFrame sichtbar ist, wird die neue XMLAnalyse nicht
                    // gleich ins Standard-Repository übernommen, sondern erst ins
                    if (AnalysesRepositoryFrame.dialog.isVisible()) {
                        AnalysesRepositoryFrame.addAnalysis(toadd, false);
                        AnalysesRepositoryFrame.analysisChanged = true;
                        AnalysesRepositoryFrame.table.update();
                        AnalysesRepositoryFrame.refreshActionStates();
                        // der Editor wurde ohne AnalysesRepositoryFrame gestartet -> neue XMLAnalyse
                        // gleich ins Repository schreiben
                    } else {
                        AnalysesRepository.addAnalysis(toadd);
                        AnalysesRepository.saveRepository();
                        // die Kopie der Analysen des Repositories auch im Dialog updaten
                        AnalysesRepositoryFrame.setAnalyses(AnalysesRepository.getXMLAnalyses());
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        // null setzten, weil nicht fest steht, ob der Editor wieder mit dem gleichen Parent
        // gestartet wird
        // (das kann das Hauptfenster oder der AnalysesRepositoryFrame sein)
        AnalysisEditor.editor = null;
    }

    /**
     * Liefert den XML-String
     *
     * @return XML-String der XMLAnalyse
     */
    public String getAnalysisString() {
        PathStepComponent firstListPanel = pathPanels.get(0);
        StringBuilder querystring = new StringBuilder("<?xml version=\"1.0\" encoding=\"ISO-8559-15\"?>\n<analyse>\n");
        querystring.append("\t<startknoten name=\"");
        Object[] firstSelectionIndices = firstListPanel.pathStepElementTypeList.getSelectedObjects();
        for (int j = 0; j < firstSelectionIndices.length - 1; j++) {
            querystring.append(((Class<?>) firstSelectionIndices[j]).getSimpleName());
            querystring.append(", ");
        }
        querystring.append(((Class<?>) firstSelectionIndices[firstSelectionIndices.length - 1]).getSimpleName());
        querystring.append("\"/>\n");

        // Das Hinzufügen der Startklasse als erstes Suchelement erfolgt,
        // da die Angabe im Tag <startknoten>, in dem ja keine Enischränkungen getroffen werden,
        // nicht ausreicht.
        PathStepComponent first = pathPanels.get(0);
        querystring.append("\t<suche>\n");
        if (!first.pathStepElementTypeList.getSelectedValuesList().isEmpty()) {
            querystring.append("\t\t<typ>\n");
            Object[] selectedIndices = first.pathStepElementTypeList.getSelectedObjects();
            for (int j = 0; j < selectedIndices.length; j++) {
                querystring.append("\t\t\t<eintrag>" + ((Class<?>) selectedIndices[j]).getSimpleName() + "</eintrag>\n");
            }
            querystring.append("\t\t</typ>\n");
        }
        querystring.append("\t\t<verbundenstate>" + (first.getConnectedState() ? "wahr" : "falsch") + "</verbundenstate>\n");
        if (!first.conditionElementTypeList.getSelectedValuesList().isEmpty()) {
            querystring.append("\t\t<verbundene>\n");
            Object[] selectedIndices = first.conditionElementTypeList.getSelectedObjects();
            for (int j = 0; j < selectedIndices.length; j++) {
                querystring.append("\t\t\t<eintrag>" + ((Class<?>) selectedIndices[j]).getSimpleName() + "</eintrag>\n");
            }
            querystring.append("\t\t</verbundene>\n");
        }
        querystring.append("\t</suche>\n");

        for (int i = 1; i < pathPanels.size(); i++) {

            PathStepComponent current = pathPanels.get(i);
            // Wenn Listen angezeigt werden, die entweder leer sind oder in denen nichts selektiert
            // ist.
            if (current.pathStepElementTypeList.getSelectedValuesList().isEmpty()) {
                break;
            }
            querystring.append("\t<suche>\n");
            if (!current.pathStepElementTypeList.getSelectedValuesList().isEmpty()) {
                querystring.append("\t\t<typ>\n");

                Object[] selectedIndices = current.pathStepElementTypeList.getSelectedObjects();
                for (int j = 0; j < selectedIndices.length; j++) {
                    querystring.append("\t\t\t<eintrag>" + ((Class<?>) selectedIndices[j]).getSimpleName() + "</eintrag>\n");
                }
                querystring.append("\t\t</typ>\n");
            }
            querystring.append("\t\t<verbundenstate>" + (current.getConnectedState() ? true : false) + "</verbundenstate>\n");
            if (!current.conditionElementTypeList.getSelectedValuesList().isEmpty()) {
                querystring.append("\t\t<verbundene>\n");
                Object[] selectedIndices = current.conditionElementTypeList.getSelectedObjects();
                for (int j = 0; j < selectedIndices.length; j++) {
                    querystring.append("\t\t\t<eintrag>" + ((Class<?>) selectedIndices[j]).getSimpleName() + "</eintrag>\n");
                }
                querystring.append("\t\t</verbundene>\n");
            }
            querystring.append("\t</suche>\n");
        }
        querystring.append("</analyse>\n");
        return querystring.toString();
    }

    /** Initialisiert die GUI-Kompnenten */
    private void init() {
        setTitle(getResString("analysis"));
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

        for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
            String simpleElementClassName = elementClass.getSimpleName();
            String resName = metaModel.getResString(simpleElementClassName);
            pathComponent.pathStepElementTypeList.addItem(elementClass, resName);
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

        /* Panels Ende */
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
        but = new JButton(getResString("exit"));
        but.addActionListener(this);
        buttons.add(but);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(getOwner());
    }

}