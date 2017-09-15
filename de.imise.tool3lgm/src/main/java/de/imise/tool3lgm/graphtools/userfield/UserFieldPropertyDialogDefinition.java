package de.imise.tool3lgm.graphtools.userfield;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.collections.CollectionUtils;

public class UserFieldPropertyDialogDefinition {

    private final Map<Class<? extends ModelElement>, UserFieldTargetSpecificList<UserFieldPropertyDialogDefinitionTab>> classToTabList = new HashMap<>();

    private final GDCollection gdcoll;

    public UserFieldPropertyDialogDefinition(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
    }

    public UserFieldPropertyDialogDefinitionTab insertUserFieldTab(final Class<? extends ModelElement> elementClass, final int index) {
        UserFieldTargetSpecificList<UserFieldPropertyDialogDefinitionTab> tabList = getOrCreateTabList(elementClass);
        UserFieldPropertyDialogDefinitionTab tab = new UserFieldPropertyDialogDefinitionTab();
        String defaultName = Tool3lgmConstants.getResString("userfields") + " ";
        String nextIndicatedName = CollectionUtils.getNextIndicatedName(defaultName, tabList, true, false);
        tab.setName(nextIndicatedName);
        int newIndex = index < 1 ? 1 : index >= tabList.size() ? tabList.size() : index;
        tabList.insert(tab, newIndex);
        return tab;
    }

    private UserFieldTargetSpecificList<UserFieldPropertyDialogDefinitionTab> getOrCreateTabList(final Class<? extends ModelElement> elementClass) {
        UserFieldTargetSpecificList<UserFieldPropertyDialogDefinitionTab> tabList = classToTabList.get(elementClass);
        if (tabList == null) {
            tabList = new UserFieldTargetSpecificList<>(elementClass);
            classToTabList.put(elementClass, tabList);
        }
        return tabList;
    }

    private List<String> getStandardTabNames(final Class<? extends ModelElement> elementClass) {
        boolean interactiveMode = gdcoll.isInteractiveMode();
        boolean bulkMode = gdcoll.isBulkMode();
        gdcoll.setBulkMode(true);
        gdcoll.setInteractiveMode(false);
        NodeContainer nc = gdcoll.getMainGraphDocument().createKnotenWithContainer(elementClass, TransactionManager.STANDARD_PID);
        ModelElement me = nc.getElement();
        ElementPropertyDialog propertyDialogTemplate = me.getPropertyDialog();
        gdcoll.delete(me);
        gdcoll.setBulkMode(bulkMode);
        gdcoll.setInteractiveMode(interactiveMode);
        List<String> tabNames = propertyDialogTemplate.getTabNames();
        return tabNames;
    }

}
