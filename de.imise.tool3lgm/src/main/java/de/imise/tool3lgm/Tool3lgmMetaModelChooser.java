package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Frame;

import javax.swing.JDialog;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.NamedObjectContainer;
import de.imise.util.Pair;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Klasse, die das Metamodell initialisiert
 *
 * @author AXS (12.06.2018)
 */
public class Tool3lgmMetaModelChooser extends JDialog {

    private Tool3lgmMetaModelChooser(final Frame owner) {
        super(owner);
    }

    public static final Class<? extends MetaModel> getLastMetaModel() {
        String metaModelClassName = UserProperties.get(StringProperty.META_MODEL);
        if (metaModelClassName != null) {
            for (Class<? extends MetaModel> metaModelClass : Tool3lgmMain.META_MODEL_CLASSES) {
                if (metaModelClass.getName().endsWith(metaModelClassName)) {
                    return metaModelClass;
                }
            }
        }
        return null;
    }

    public static final Class<? extends MetaModel> chooseMetaModel(final boolean showDontAskAgain) {
        int optionsCount = Tool3lgmMain.META_MODEL_CLASSES.size();
        @SuppressWarnings("unchecked")
        NamedObjectContainer<Class<? extends MetaModel>>[] options = new NamedObjectContainer[optionsCount];
        for (int i = 0; i < optionsCount; i++) {
            Class<? extends MetaModel> metaModelClass = Tool3lgmMain.META_MODEL_CLASSES.get(i);
            options[i] = new NamedObjectContainer<>(metaModelClass, getResString(metaModelClass.getSimpleName()));
        }
        Class<? extends MetaModel> lastMetaModel = getLastMetaModel();
        Pair<NamedObjectContainer<Class<? extends MetaModel>>, Boolean> choosedMetaModelAnswer = MultipleOptionPane.showSingleSelectionOptionDialog(Static.getMainFrame(), getResString("CHOOSE_META_MODEL_DIALOG_TITLE"), null, options, lastMetaModel,
                showDontAskAgain ? getResString("dont_ask_again") : null, false);
        if (choosedMetaModelAnswer == null) {
            return null;
        }
        NamedObjectContainer<Class<? extends MetaModel>> choosedMetaModelClassContainer = choosedMetaModelAnswer.getFirstItem();
        Class<? extends MetaModel> choosedMetaModelClass = choosedMetaModelClassContainer.getObject();
        if (!showDontAskAgain || Boolean.TRUE.equals(choosedMetaModelAnswer.getSecondItem())) {
            UserProperties.set(StringProperty.META_MODEL, choosedMetaModelClass.getSimpleName());
        } else {
            UserProperties.remove(StringProperty.META_MODEL);
        }
        return choosedMetaModelClass;
    }

    public static final void setMetaModel(final Class<? extends MetaModel> metaModelClass) {
        Tool3lgmMain.setMetaModelClass(metaModelClass, false);
    }

}