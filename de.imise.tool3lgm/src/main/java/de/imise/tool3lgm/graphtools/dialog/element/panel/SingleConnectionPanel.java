package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import javax.swing.UIManager;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMItemListener;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.NamedObjectContainer;
import de.imise.util.htmlxml.HTMLConverter;
import de.imise.util.htmlxml.ParseSaveStringHandler;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;

/**
 * @author AXS
 *         Dieses Panel stellt in einer Combobox ein einzelne Element zur direkten Verknüpfung mit dem
 *         ModelElement des Dialoges zur Auswahl. Je nachdem ob die Verknüpfung über eine normale
 *         {@link Edge} oder eine {@link CompositionEdge} läuft, werden andere im Modell befindliche
 *         Elemente zur Verknüpfung angeboten {@link Edge}) oder nicht ({@link CompositionEdge}).
 *         Die Verknüpfung kann über einen Pfad erfolgen, d.h. es gehen nicht nur direkte Verbindungen.
 *         ABER: Dieses Panel beachtet keine Vererbung. Das heißt es werden immer nur direkt mit
 *         dem Ausgangselement verbundene Elemente angezeigt. Im PathConnectionPanel hingegen werden
 *         auch Elemente, die man durch Vererbung erhält, angezeigt. Das ist hier aber nicht sinnvoll,
 *         da man z.B. jedem Anwendungsbaustein sein eigenes Datenbanksystem geben will, auch wenn ein
 *         übergeordneter Anwendungsbaustein schon eines besitzt.
 */
public class SingleConnectionPanel extends AbstractPathConnectionPanel {

    /** Box, in der die verbindbaren Elemente zur Auswahl gestellt werden, wenn es mehr als eines gibt. */
    private final AlphabeticalComboBox<ElementContainer> connectedElementsBox;

    /** Eingabefeld, in dem der Name des verbundenen Elementes angezeit wird und geändert werden kann. */
    private final LimitedSizeScrollTextPane connectedElementName;

    /** Je nachdem was von beiden (Box oder Textfeld) initialisert wurde, ist dies diese Komponente */
    protected final Component connectedElementViewComponent;

    /** Das verbundene Element das angezeigt wird (wenn es mind. eins gibt) */
    protected ModelElement connectedElement;

    /** Cache zur Speicherung, ob der Name des verbundenen Elementes geändert wurde */
    private String oldname = "";

    /** Menu entry */
    private final NamedObjectContainer<ElementContainer> createNewMenuItem;

    /** Menu entry to unlink (is set to one whitepsace) */
    private final NamedObjectContainer<ElementContainer> unlinkMenuItem;

    /** Menu entry to delete the connetced element */
    private NamedObjectContainer<ElementContainer> deleteConnectedMenuItem;

    /**
     * COMMENTME
     */
    private final ItemListener itemListener;

    /**
     * @param dialog
     * @param metaPath
     */
    public SingleConnectionPanel(final AbstractElementPropertyDialog dialog, final MetaPath metaPath) {
        this(dialog, PanelLabelOption.LABEL_END_ELEMENT_TYPE, metaPath);
    }

    /**
     * @param dialog
     * @param panelLabelOption Das Label kann folgende Werte annehmen:
     *            <ul>
     *            <li>{@link PanelLabelOption#LABEL_END_ELEMENT_TYPE} = Anzeigename der EndElement-Art des MetaPfades</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_ELEMENT_NAME} = Anzeigename der Element-Art der letzten Kante des MetaPfades</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_CONNECTION_NAME} = Anzeigename der gerichteten Verbindung der letzten Kante des
     *            MetaPfades</li>
     *            </ul>
     * @param metaPath
     */
    public SingleConnectionPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption panelLabelOption, final MetaPath metaPath) {
        super(dialog, panelLabelOption, panelLabelOption, metaPath);
        setLayout(new BorderLayout());
        update(); //connectedElement initial setzen!
        boolean editable = !dialog.isInfoDialog() && metaPath.isCreatable(false); // für editable reicht es, wenn der Pfad zw. bestehenden Elementen entfernt oder angehängt werden kann. Das zu verbindende Element muss nicht neu erzeugt werden können
        editable &= !(metaPath.getSubMetaPathCount() == 1 && metaPath.isFirstPathElementDependent()); //bei untergeordneten Elementen nicht das Abhängen/Löschen/Ändern des übergeordneten anbieten
        if (!editable) {
            connectedElementsBox = null;
            itemListener = null;
            connectedElementName = new LimitedSizeScrollTextPane(4, false); //wenn man hier true übergibt, kann man den Namen des verbundenen Elementes ändern. Aber dann funktionieren die Maus-Actions nicht mehr, weil dann die Komponente eigene Mausaktionen für den Text macht
            connectedElementViewComponent = connectedElementName;
            //Doppelklick-Action und Kontextmenü anghängen
            addMouseActions(connectedElementName);
            add(connectedElementName, BorderLayout.CENTER);
            unlinkMenuItem = null;
            deleteConnectedMenuItem = null;
        } else {
            connectedElementsBox = new AlphabeticalComboBox();
            itemListener = new LGMItemListener(getItemStateChangedAction(this));
            connectedElementName = null;
            connectedElementViewComponent = connectedElementsBox;
            unlinkMenuItem = new NamedObjectContainer<>(null, " ");

            connectedElementsBox.addItemListener(itemListener);
            //Doppelklick-Action und Kontextmenü anghängen
            addMouseActions(connectedElementsBox);
            add(connectedElementsBox, BorderLayout.CENTER);
        }
        if (editable && metaPath.isCreatable(true)) {
            String createNewMenuItemDisplayString = getResString("new") + ": " + elementsNameBuilder.getDisplayableName(searchElementClass);
            createNewMenuItem = new NamedObjectContainer<>(null, createNewMenuItemDisplayString);
        } else {
            createNewMenuItem = null;
        }

    }

    @Override
    public void update() {
        Collection<ElementContainer> allConnectedContainers = getConnectedContainer();
        ElementContainer connectedContainer = allConnectedContainers.isEmpty() ? null : allConnectedContainers.iterator().next();
        connectedElement = connectedContainer == null ? null : connectedContainer.getElement();

        if (connectedElementsBox != null) {
            Color enabledColor = UIManager.getColor("TextField.background");
            connectedElementsBox.setBackground(enabledColor); //Combobox should have the same background color like Textfields
            boolean isLastPathElementDependent = metaPath.isLastPathElementDependent();
            connectedElementsBox.removeItemListener(itemListener);
            connectedElementsBox.removeAllItems();
            if (unlinkMenuItem != null) {
                connectedElementsBox.addItem(unlinkMenuItem);
                if (connectedElement != null) {
                    connectedElementsBox.addSeparator(false); //prevent reordering of the first 3 entries
                    deleteConnectedMenuItem = connectedElementsBox.addObject(null, getResString("delete") + ": " + connectedElement);
                }
            }
            //bei abhängigen Elementen werden in der Auswahlbox nur die angezeigt, die mit dem Element des Dialoges/Panels verbunden sind, sonst alle bzw. alle, die über den ConditionMetaPath verbunden sind
            Collection<ElementContainer> available = getAvailableConnectables();

            //neues Element anlegen und verknüpfen soll nur gezeigt werden, wenn der Pfad an sich anlegbar ist. Ist die searchElementClass
            //abhängig von der Existenz des Elementes davor im Pfad, dann soll auch kein Neu-Anlegen-Eintrag kommen
            boolean showNewEntry = createNewMenuItem != null;
            if (showNewEntry && isLastPathElementDependent && !allConnectedContainers.isEmpty()) {
                showNewEntry = false;
            }
            if (showNewEntry) {
                connectedElementsBox.addItem(createNewMenuItem);
            }
            connectedElementsBox.addSeparator(true);
            connectedElementsBox.addAllObjects(available);
            for (ElementContainer ec : allConnectedContainers) {
                connectedElementsBox.removeObject(ec);
                connectedElementsBox.addObject(ec);
            }
            connectedElementsBox.setSelectedObject(connectedContainer);
            connectedElementsBox.addItemListener(itemListener);
        } else if (connectedElementName != null) { // beim ersten update() aus dem Konstruktor sind beide (Box und TextArea) null -> nicht einfach nur else hier sondern else-if
            //wir hatten mal ausprobiert, den Hintergund bei nicht änderbaren Elementen auszugrauen -> gefiel mir aber nicht (AXS)
            //            Color disabledColor = UIManager.getColor("Label.background");
            //            connectedElementName.setBackground(disabledColor);
            if (connectedElement != null) {
                oldname = connectedElement.getClearName();
                connectedElementName.setText(oldname);
            } else {
                connectedElementName.setText("");
            }
        }
        if (connectedElementName != null || connectedElementsBox != null) {
            String description = connectedElement == null ? null : connectedElement.getDescription();
            description = description == null || description.trim().isEmpty() ? null : "<HTML>" + HTMLConverter.getDecimalEncodedHTMLString(description, true) + "</HTML>";
            if (connectedElementName != null) {
                connectedElementName.setToolTipText(description);
            } else {
                connectedElementsBox.setToolTipText(description);
            }
        }
    }

    /**
     * @return Das verbundene Element das angezeigt wird (wenn es mind. eins gibt)
     */
    public ModelElement getConnectedElement() {
        return connectedElement;
    }

    @Override
    protected final Object getSelection(final MouseEvent e) {
        //das Mausevent ist egal, da immer nur das eine verbundene Element des Panel selektiert sein kann
        return connectedElementsBox != null ? connectedElementsBox.getSelectedObject() : connectedElement;
    }

    @Override
    public void commit() {
        //der Name kann nicht geändert werden, wenn die ComboBox angezeigt wird (und nicht das EingabeTextfeld)
        //oder wenn kein verbundenes Element vorhanden ist
        if (connectedElementName == null || connectedElement == null) {
            return;
        }
        String newName = connectedElementName.getText();
        if (newName != null && !oldname.equals(newName)) {
            GraphDocument mainDoc = getMainDoc();
            int pid = getTransactionID();
            newName = ParseSaveStringHandler.getParseSaveString(newName);
            mainDoc.setName(connectedElement, newName, pid);
        }
        connectedElement.refreshText();
    }

    /**
     * Dies ist die Action, wenn sich die Combobox-Auswahl ändert
     *
     * @param panel
     * @return
     */
    private static final LGMAction getItemStateChangedAction(final SingleConnectionPanel panel) {
        return new LGMAction() {
            @Override
            public void execute(final EventObject eo) {
                if (!(eo instanceof ItemEvent)) {
                    return;
                }
                ItemEvent e = (ItemEvent) eo;
                int stateChange = e.getStateChange();
                //vor jedem Selected gibt es ein Deselected
                if (stateChange == ItemEvent.DESELECTED) {
                    return;
                }
                GraphDocument mainDoc = panel.getMainDoc();
                ModelElement me = panel.getModelElement();

                Object selected = e.getItem();
                int pid = panel.getTransactionID();
                mainDoc.start_transaction(pid);

                // Neues Element anlegen
                if (selected == panel.createNewMenuItem) {
                    panel.connectToFirstPath(null);
                    panel.update();
                } else if (selected instanceof NodeContainer) { //vorhandemes Element verknüpfen
                    ElementContainer container2Connect = (ElementContainer) selected;
                    ModelElement element2Connect = container2Connect.getElement();
                    panel.connectToFirstPath(element2Connect);
                } else if (selected == panel.unlinkMenuItem) {
                    panel.unlinkAll(false);
                } else if (selected == panel.deleteConnectedMenuItem) {
                    panel.unlinkAll(true);
                }
                ElementContainer ec = me.getContainer(mainDoc);
                ec.refreshText();
                mainDoc.finish_transaction(pid);
                mainDoc.distributeEvent(DATA_CHANGED, pid);
            }
        };
    }

    /**
     * Liefert die mit dem ModelElement des Dialoges über die angegebenen Kanten verbundenen Elemente.
     *
     * @param forelastInPath wenn <code>true</code> werden nicht die letzten, sondern die vorletzten im
     *            Pfad zurück gegeben. Bei Pfaden, die nur aus einer Edge bestehen ist das das
     *            Ausgangselement des Pfades, also das ModelElement des Dialoges.
     * @return
     */
    private Collection<ElementContainer> getConnectedContainer(final boolean forelastInPath) {
        ModelElement me = getModelElement();
        GraphDocument mainDoc = getMainDoc();
        return metaPath.getConnectedContainer(me, mainDoc, forelastInPath);
    }

    /**
     * Liefert alle Elemente der searchElementClass, die mit dem Ausgangselement direkt verbunden sind.
     *
     * @return
     */
    private final Collection<ElementContainer> getConnectedContainer() {
        return getConnectedContainer(false);
    }

    /**
     * Liefert die ElementContainer, die mit den Endelementen des Pfades verbunden sind. Ist der Pfad nur eine Kante lang, sind das immer das/die
     * Ausgeangselement/e. Ist der Pfad länger, sind es immer die StartElemente des letzten Pfadschrittes.
     *
     * @return
     */
    private final Collection<ElementContainer> getForelastConnectedContainer() {
        return getConnectedContainer(true);
    }

    /**
     * Removes all connections between the penultimate and searchElements,
     * which are connected to the dialog element via the panel path.
     *
     * @param deleteUnlinked
     *            if <code>true</code> the previously connected element is deleted
     */
    private final void unlinkAll(final boolean deleteUnlinked) {
        Collection<ElementContainer> searchElementConnectedContainer = getForelastConnectedContainer();
        if (!searchElementConnectedContainer.isEmpty()) {
            GDCollection gdcoll = getCollection();
            //prevent automatically deleting the dialog element and closing the dialog
            //if the removing of the connection causes an inconsistency in the dialog element
            gdcoll.setIgnoreInconsistenciesOnDeleteEgdesMode(true);
            Class<? extends Edge> lastEdgeInPath = getLastEdgeClassInPath();
            Direction lastDirectionInPath = getLastDirectionInPath();
            for (ElementContainer ec : searchElementConnectedContainer) {
                ModelElement me = ec.getElement();
                List<ModelElement> connectedElements = me.getConnectedElements(searchElementClass, lastEdgeInPath, lastDirectionInPath);
                for (ModelElement connected : connectedElements) {
                    int pid = getTransactionID();
                    gdcoll.unlink(me, connected, lastEdgeInPath, lastDirectionInPath, pid);
                    if (deleteUnlinked) {
                        gdcoll.deleteElement(connected, pid);
                    }
                }
            }
            gdcoll.setIgnoreInconsistenciesOnDeleteEgdesMode(false);
        }
    }

}