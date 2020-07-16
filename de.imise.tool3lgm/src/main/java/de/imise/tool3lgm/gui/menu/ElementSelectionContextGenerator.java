package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Tool3lgmConstants.getIcon;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public abstract class ElementSelectionContextGenerator extends ContextGenerator {

    /** Icon für das Herstellen einer Verbindung */
    static ImageIcon link_icon = getIcon("verbindung_anlegen.gif");

    /** Icon für das Trennen einer Verbindung */
    static ImageIcon unlink_icon = getIcon("verbindung_trennen.gif");

    /**
     * @param menu
     * @param startElement
     * @param endElements
     * @return <code>true</code> if something was added to the menu
     */
    public static boolean addConnectMenuItems(final JPopupMenu menu, final ModelElement startElement) {
        MetaModel metaModel = startElement.getMetaModel();
        Class<? extends ModelElement> startElementClass = startElement.getClass();
        Collection<SimpleMetaPath> creatableMetaPaths = metaModel.getCreatableMetaPaths(startElementClass);
        return addConnectMenuItems(menu, startElement, creatableMetaPaths, null);
    }

    /**
     * @param menu
     * @param startElement
     * @param creatableMetaPath
     * @param endElements
     * @return <code>true</code> if something was added to the menu
     */
    public static boolean addConnectMenuItems(final JPopupMenu menu, final ModelElement startElement, final SimpleMetaPath creatableMetaPath, final Collection<ModelElement> endElements) {
        return addConnectMenuItems(menu, startElement, ImmutableList.of(creatableMetaPath), endElements);
    }

    /**
     * @param menu
     * @param startElement
     * @param creatableMetaPaths
     * @param endElements
     * @return <code>true</code> if something was added to the menu
     */
    private static boolean addConnectMenuItems(final JPopupMenu menu, final ModelElement startElement, final Collection<SimpleMetaPath> creatableMetaPaths, Collection<ModelElement> endElements) {
        JLabel connectLabel = null;
        boolean somethingAdded = false;
        for (SimpleMetaPath metaPath : creatableMetaPaths) {
            if (connectLabel == null) {
                connectLabel = new JLabel(getResString("LABEL_CONNECT"));
                menu.add(connectLabel);
                somethingAdded = true;
            }
            Class<? extends ModelElement> endClass = metaPath.getEndClass();
            String metaPathName = metaPath.getName(false, true);
            JMenu pathConnectableElements = new JMenu(metaPathName);
            pathConnectableElements.setIcon(link_icon);
            GraphDocument doc = Static.getSelectedDoc();
            if (endElements == null) {
                endElements = doc.getModelItems(endClass, true, true);
            }
            pathConnectableElements.setEnabled(!endElements.isEmpty());
            menu.add(pathConnectableElements);
            for (ModelElement endMe : endElements) {
                endClass = endMe.getClass();
                if (metaPath.isEndClass(endClass)) {
                    Action createPathAction = createPathAction(startElement, metaPath, endMe);
                    JMenuItem createPathItem = getItem(createPathAction);
                    pathConnectableElements.add(createPathItem);
                }
            }
            setMenuScroller(pathConnectableElements);
        }
        return somethingAdded;
    }

    /**
     * Liefert eine Action zu einem SimpleMetaPath der zwischen dem zuletzt selektierten Element und allen zum Endelement des Pfades passenden anderen
     * selektierten Elementen angelegt wird.
     *
     * @param path2create
     * @return
     */
    protected static Action createPathAction(final SimpleMetaPath path2create) {
        GraphDocument doc = Static.getSelectedDoc();
        List<ModelElement> selectedElements = doc.getSelectedElements();
        String pathName = path2create.getName(false, true);
        return createPathAction(null, path2create, selectedElements, pathName, link_icon);
    }

    /**
     * Liefert eine Action zu einem SimpleMetaPath der zwischen dem übergebenen StartElement hin zum übergebenen Endelement angelegt wird.
     * Ist das startElement null wird das zuletzt seletierte genommen.
     *
     * @param startElement
     * @param path2create
     * @param endElement
     * @return
     */
    private static Action createPathAction(final ModelElement startElement, final SimpleMetaPath path2create, final ModelElement endElement) {
        return createPathAction(startElement, path2create, ImmutableList.of(endElement), endElement.getName(), null);
    }

    /**
     * Liefert eine Action zu einem SimpleMetaPath der zwischen dem zuletzt selektierten Element und allen zum Endelement des Pfades passenden anderen
     * übergebenen Elementen angelegt wird.
     *
     * @param startElement
     * @param path2create Pfad der angelegt werden soll
     * @param endElements Elemente, zu denen der Pfad vom zuletzt selektierten Element aus angelegt werden soll
     * @param name Name der Action
     * @param icon Icon der Sction
     * @return
     */
    private static Action createPathAction(final ModelElement startElement, final SimpleMetaPath path2create, final Collection<ModelElement> endElements, final String name, final Icon icon) {
        return new AbstractAction(name, icon) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Class<? extends ModelElement> startClass = path2create.getStartClass();
                Class<? extends ModelElement> endClass = path2create.getEndClass();
                LGMGraphDocument selectedDoc = Static.getSelectedDoc();
                GDCollection selectedGDColl = selectedDoc.getCollection();
                GDCollection startElementGDColl = startElement == null ? selectedGDColl : startElement.getCollection();
                //if the startElement is not in the current selected model (because
                //it is from a template in the template browser) copy this element
                //and all of its dependent to the selected model
                ModelElement realStartElement = null;
                if (startElementGDColl != selectedGDColl) {
                    LGMGraphDocument startElementDoc = startElementGDColl.getSelectedDoc();
                    ElementContainer startElementContainer = startElementDoc.getElementContainer(startElement);
                    LGMGraphDocument.copyToModel(startElementContainer, selectedDoc);
                    String startElementHash = startElement.getHashString();
                    realStartElement = selectedDoc.findElementCoded(startElementHash);
                }
                if (realStartElement == null) {
                    realStartElement = startElement;
                }
                if (realStartElement == null) {
                    ElementContainer lastSelected = selectedDoc.getLastSelected();
                    realStartElement = lastSelected.getElement();
                }
                Class<? extends ModelElement> realStartElementClass = realStartElement.getClass();
                if (!startClass.isAssignableFrom(realStartElementClass)) {
                    return;
                }
                for (ModelElement me : endElements) {
                    if (realStartElement == me) {
                        continue;
                    }
                    Class<? extends ModelElement> meClass = me.getClass();
                    if (!endClass.isAssignableFrom(meClass)) {
                        continue;
                    }
                    GDCollection gdcoll = selectedDoc.getCollection();
                    boolean lastAutomaticMode = gdcoll.setAutomaticMode(true);
                    selectedDoc.createPath(realStartElement, me, path2create, STANDARD_PID);
                    gdcoll.setAutomaticMode(lastAutomaticMode);
                }
            }
        };
    }

}
