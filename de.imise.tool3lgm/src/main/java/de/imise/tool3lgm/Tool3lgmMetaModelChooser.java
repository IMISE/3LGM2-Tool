package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.metamodel.tlgm_service.TLGMServiceMetaModel;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.TLGMOriginalMetaModel;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.NamedObjectContainer;
import de.imise.util.Pair;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Klasse, die das Metamodell initialisiert
 *
 * @author AXS (12.06.2018)
 */
public class Tool3lgmMetaModelChooser {

    private static Class<? extends MetaModel> metaModelClass = null;

    private static List<Class<? extends MetaModel>> META_MODEL_CLASSES = ImmutableList.of(TLGMOriginalMetaModel.class, TLGMServiceMetaModel.class);

    private static void initMetaModel() {
        if (!UserProperties.is(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG_AT_START)) {
            metaModelClass = getUserpropertiesStoredMetaModel();
        }
        while (metaModelClass == null) {
            metaModelClass = chooseMetaModel(true);
        }
    }

    public static final Class<? extends MetaModel> getMetaModelClass() {
        if (metaModelClass == null) {
            initMetaModel();
        }
        return metaModelClass;
    }

    private static final Class<? extends MetaModel> getUserpropertiesStoredMetaModel() {
        String metaModelClassName = UserProperties.get(StringProperty.META_MODEL);
        if (metaModelClassName != null) {
            for (Class<? extends MetaModel> metaModelClass : META_MODEL_CLASSES) {
                if (metaModelClass.getName().endsWith(metaModelClassName)) {
                    return metaModelClass;
                }
            }
        }
        return null;
    }

    public static void chooseNextStartMetaModel() {
        Class<? extends MetaModel> oldMetaModelClass = metaModelClass;
        Class<? extends MetaModel> choosedMetaModelClass = chooseMetaModel(false);
        if (choosedMetaModelClass != null && choosedMetaModelClass != oldMetaModelClass) {
            JOptionPane.showMessageDialog(Static.getTool(), getResString("metamodel_changed_info"), getResString("restart_required"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static final Class<? extends MetaModel> chooseMetaModel(final boolean initialSelection) {
        int optionsCount = META_MODEL_CLASSES.size();
        Class<? extends MetaModel> lastMetaModel = getUserpropertiesStoredMetaModel();
        Component owner = initialSelection ? null : Static.getMainFrame();
        String title = getResString("choose_meta_model_dialog_title");
        String message = null;
        @SuppressWarnings("unchecked")
        NamedObjectContainer<Class<? extends MetaModel>>[] options = new NamedObjectContainer[optionsCount];
        NamedObjectContainer<Class<? extends MetaModel>> selectedOption = null;
        for (int i = 0; i < optionsCount; i++) {
            Class<? extends MetaModel> metaModelClass = META_MODEL_CLASSES.get(i);
            options[i] = new NamedObjectContainer<>(metaModelClass, getResString(metaModelClass.getSimpleName()));
            if (i == 0 || lastMetaModel == metaModelClass) {
                selectedOption = options[i];
            }
        }
        String showAgainQuestion = getResString("show_this_dialog_at_start");
        boolean showAgainQuestionSelection = UserProperties.is(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG_AT_START) || initialSelection;
        Pair<NamedObjectContainer<Class<? extends MetaModel>>, Boolean> choosedMetaModelAnswer = MultipleOptionPane.showSingleSelectionOptionDialog(owner, title, message, options, selectedOption, showAgainQuestion, showAgainQuestionSelection);
        if (choosedMetaModelAnswer == null) {
            return null;
        }
        NamedObjectContainer<Class<? extends MetaModel>> choosedMetaModelClassContainer = choosedMetaModelAnswer.getFirstItem();
        Class<? extends MetaModel> choosedMetaModelClass = choosedMetaModelClassContainer.getObject();
        UserProperties.set(StringProperty.META_MODEL, choosedMetaModelClass.getSimpleName());
        UserProperties.set(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG_AT_START, Boolean.TRUE.equals(choosedMetaModelAnswer.getSecondItem()));
        return choosedMetaModelClass;
    }

}