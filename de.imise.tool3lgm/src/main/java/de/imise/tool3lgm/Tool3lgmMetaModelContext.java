package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.PluginUtils;
import de.imise.util.pair.Pair;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Klasse, die die verfügbaeren Metamodelle verwaltet, initialisiert und bereitstellt.
 *
 * @author AXS (12.06.2018)
 */
public final class Tool3lgmMetaModelContext {

    /**
     * Name der Metamodellklasse, die als Default gesetzt werden soll. Das ist auch die Klasse, mit der Modelle geladen werden, bei denen kein
     * Metamodell abgegeben ist.
     */
    private static final String DEFAULT_METAMDOEL_CLASS_NAME = "TLGMOriginalMetaModel";

    /**
     * Alle MetaModel-Klasse, die gefunden werden. Die Standardmetamodellklasse (= die Klasse mit dem Namen DEFAULT_METAMDOEL_NAME) befindet sich
     * immer an Position 0)
     */
    private static final List<MetaModelInstanceContext> metaModelInstanceContexts = loadMetaModelInstanceContexts();

    /** Das Metamodell, mit dem ein neues Modell initialisiert werden soll */
    private static MetaModelInstanceContext metaModelInstanceContext = null;

    /**
     * Liefert einen MetaModelContext. Abhängig davon, ob in den UserProperties eingestellt ist, ob per Dialog nachgefragt werden soll oder nicht,
     * wird der in den den UserProperties gespeicherte einfach zurück gegeben oder per Dialog nachgefragt.
     *
     * @return
     */
    public static MetaModelInstanceContext getNewModelMetaModelInstanceContext() {
        boolean showDialog = UserProperties.is(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG);
        if (!showDialog) {
            metaModelInstanceContext = getUserpropertiesStoredMetaModelInstanceContext();
        }
        while (metaModelInstanceContext == null) {
            if (showDialog) {
                metaModelInstanceContext = chooseMetaModel();
            } else {
                metaModelInstanceContext = metaModelInstanceContexts.get(0); // es war gewünscht worden, dass beim initialen Start das originale Metamodell ausgewählt ist. showDialog ist initial false
            }
        }
        return metaModelInstanceContext;
    }

    /**
     * Lädt alle im Plaugin-Verzeichnis auffindbaren Metamodell-Klassen in einen MetaModelInstanceContext. Die Klasse
     *
     * @return
     */
    public static final List<MetaModelInstanceContext> loadMetaModelInstanceContexts() {
        File pluginDir = new File(Tool3lgmConstants.APPLICATION_DIR, "Plugins");
        List<Class<? extends MetaModel>> metaModelClasses = PluginUtils.loadClasses(pluginDir, MetaModel.class);
        List<MetaModelInstanceContext> metaModelInstanceContexts = new ArrayList<>();
        for (int i = 0; i < metaModelClasses.size(); i++) {
            Class<? extends MetaModel> metaModelClass = metaModelClasses.get(i);
            MetaModelInstanceContext metaModelInstanceContext = new MetaModelInstanceContext(metaModelClass);
            if (metaModelClass.getSimpleName().equals(DEFAULT_METAMDOEL_CLASS_NAME)) {
                metaModelInstanceContexts.add(0, metaModelInstanceContext); // das DefaultMetaModelganz nach vorne holen
            } else {
                metaModelInstanceContexts.add(metaModelInstanceContext);
            }
        }
        return metaModelInstanceContexts;
    }

    /**
     * Liefert das Standardmetamodell des Tools.
     *
     * @return
     */
    public static final MetaModelInstanceContext getDefaultMetaModelContext() {
        return metaModelInstanceContexts.get(0);
    }

    /**
     * Liefert anhand der ID des in den UserProperties gespeicherten MetaModels den zugehörigen Kontext.
     *
     * @return
     */
    private static final MetaModelInstanceContext getUserpropertiesStoredMetaModelInstanceContext() {
        String storedMetaModelID = UserProperties.get(StringProperty.META_MODEL);
        if (!Strings.isNullOrEmpty(storedMetaModelID)) {
            for (MetaModelInstanceContext metaModelInstanceContext : metaModelInstanceContexts) {
                if (metaModelInstanceContext.getMetaModelID().equals(storedMetaModelID)) {
                    return metaModelInstanceContext;
                }
            }
        }
        return null;
    }

    public static final MetaModelInstanceContext chooseMetaModel() {
        int optionsCount = metaModelInstanceContexts.size();
        MetaModelInstanceContext lastMetaModelInstanceContext = getUserpropertiesStoredMetaModelInstanceContext();
        String title = getResString("choose_meta_model_dialog_title");
        String message = null;
        MetaModelInstanceContext[] options = new MetaModelInstanceContext[optionsCount];
        MetaModelInstanceContext selectedOption = null;
        for (int i = 0; i < optionsCount; i++) {
            MetaModelInstanceContext metaModelInstanceContext = metaModelInstanceContexts.get(i);
            options[i] = metaModelInstanceContext;
            if (i == 0 || lastMetaModelInstanceContext == metaModelInstanceContext) {
                selectedOption = options[i];
            }
        }
        String showAgainQuestion = getResString("show_this_dialog_at_start");
        boolean showAgainQuestionSelection = UserProperties.is(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG);
        Pair<MetaModelInstanceContext, Boolean> choosedMetaModelAnswer = MultipleOptionPane.showSingleSelectionOptionDialog(Static.getMainFrame(), title, message, options, selectedOption, showAgainQuestion, showAgainQuestionSelection);
        if (choosedMetaModelAnswer == null) {
            return null;
        }
        MetaModelInstanceContext choosedMetaModelInstanceContext = choosedMetaModelAnswer.getFirstItem();
        UserProperties.set(StringProperty.META_MODEL, choosedMetaModelInstanceContext.getMetaModelID());
        UserProperties.set(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG, Boolean.TRUE.equals(choosedMetaModelAnswer.getSecondItem()));
        return choosedMetaModelInstanceContext;
    }

    /**
     * Liefert den {@link MetaModelInstanceContext} anhand seiner ID. Das ist der Klassennamen@SerialVersionUID. Bei alten Modellen ist es nur der
     * Klassenname.
     *
     * @param metaModelContextID
     * @return
     */
    public static final MetaModelInstanceContext getMetaModelContextForID(String metaModelContextID) {
        if (Strings.isNullOrEmpty(metaModelContextID)) {
            metaModelContextID = DEFAULT_METAMDOEL_CLASS_NAME;
        }
        for (MetaModelInstanceContext metaModelInstanceContext : metaModelInstanceContexts) {
            String otherID = metaModelInstanceContext.getMetaModelID();
            if (otherID.equals(metaModelContextID)) {
                return metaModelInstanceContext;
            }
            //bevor die Metamodelle mit der SerialVersionUID gekennzeichnet wurden, war einzig der SimpleClassName der Metamodellklasse die ID -> für alte Modelle daruf testen
            otherID = metaModelInstanceContext.getMetaModelClass().getSimpleName();
            if (otherID.equals(metaModelContextID)) {
                return metaModelInstanceContext;
            }
        }
        return null;
    }

}