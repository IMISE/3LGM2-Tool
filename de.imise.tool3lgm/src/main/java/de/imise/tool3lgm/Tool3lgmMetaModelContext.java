package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition.DefaultMetaModelDefinitionAdapter;
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

    /** Dummy-Instanz eines MetamodelContexts, um Null-Checks zu vermeiden */
    public static final MetaModelContext DUMMY_META_MODEL_CONTEXT = new MetaModelContext(DefaultMetaModelDefinitionAdapter.class);

    /** Dummy-Instanz eines Metamodells, um Null-Checks zu vermeiden */
    public static final MetaModel DUMMY_META_MODEL = DUMMY_META_MODEL_CONTEXT.getMetaModel();

    /**
     * Alle MetaModel-Klassen, die gefunden werden. Die Standardmetamodellklasse (= die Klasse mit dem Namen DEFAULT_METAMDOEL_NAME) befindet sich
     * immer an Position 0)
     */
    private static final List<MetaModelContext> metaModelContexts = loadMetaModelContexts();

    /** Das Metamodell, mit dem ein neues Modell initialisiert werden soll */
    private static MetaModelContext metaModelContext = null;

    /**
     * Liefert einen MetaModelContext. Abhängig davon, ob in den UserProperties eingestellt ist, ob per Dialog nachgefragt werden soll oder nicht,
     * wird der in den den UserProperties gespeicherte einfach zurück gegeben oder per Dialog nachgefragt.
     *
     * @return
     */
    public static MetaModelContext getNewModelMetaModelContext() {
        boolean showDialog = UserProperties.is(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG);
        if (!showDialog) {
            metaModelContext = getUserpropertiesStoredMetaModelContext();
        }
        while (metaModelContext == null) {
            if (showDialog) {
                metaModelContext = chooseMetaModel();
            } else {
                metaModelContext = metaModelContexts.get(0); // es war gewünscht worden, dass beim initialen Start das originale Metamodell ausgewählt ist. showDialog ist initial false
            }
        }
        return metaModelContext;
    }

    /**
     * Lädt alle im Plaugin-Verzeichnis auffindbaren Metamodell-Klassen in jeweils einen MetaModelContext.
     *
     * @return
     */
    public static final List<MetaModelContext> loadMetaModelContexts() {
        File pluginDir = new File(Tool3lgmConstants.APPLICATION_DIR, "Plugins");
        List<Class<? extends MetaModelDefinition>> metaModelClasses = PluginUtils.loadClasses(pluginDir, MetaModelDefinition.class);
        List<MetaModelContext> metaModelContexts = new ArrayList<>();
        for (int i = 0; i < metaModelClasses.size(); i++) {
            Class<? extends MetaModelDefinition> metaModelClass = metaModelClasses.get(i);
            MetaModelContext metaModelContext = new MetaModelContext(metaModelClass);
            if (metaModelClass.getSimpleName().equals(DEFAULT_METAMDOEL_CLASS_NAME)) {
                metaModelContexts.add(0, metaModelContext); // das DefaultMetaModelganz nach vorne holen
            } else {
                metaModelContexts.add(metaModelContext);
            }
        }
        return metaModelContexts;
    }

    /**
     * Liefert das Standardmetamodell des Tools.
     *
     * @return
     */
    public static final MetaModelContext getDefaultMetaModelContext() {
        return metaModelContexts.get(0);
    }

    /**
     * Liefert anhand der ID des in den UserProperties gespeicherten MetaModels den zugehörigen Kontext.
     *
     * @return
     */
    private static final MetaModelContext getUserpropertiesStoredMetaModelContext() {
        String storedMetaModelID = UserProperties.get(StringProperty.META_MODEL);
        if (!Strings.isNullOrEmpty(storedMetaModelID)) {
            for (MetaModelContext metaModelContext : metaModelContexts) {
                if (metaModelContext.getMetaModelID().equals(storedMetaModelID)) {
                    return metaModelContext;
                }
            }
        }
        return null;
    }

    public static final MetaModelContext chooseMetaModel() {
        int optionsCount = metaModelContexts.size();
        MetaModelContext lastMetaModelContext = getUserpropertiesStoredMetaModelContext();
        String title = getResString("choose_meta_model_dialog_title");
        String message = null;
        MetaModelContext[] options = new MetaModelContext[optionsCount];
        MetaModelContext selectedOption = null;
        for (int i = 0; i < optionsCount; i++) {
            MetaModelContext metaModelContext = metaModelContexts.get(i);
            options[i] = metaModelContext;
            if (i == 0 || lastMetaModelContext == metaModelContext) {
                selectedOption = options[i];
            }
        }
        String showAgainQuestion = getResString("show_this_dialog_at_start");
        boolean showAgainQuestionSelection = UserProperties.is(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG);
        Pair<MetaModelContext, Boolean> choosedMetaModelAnswer = MultipleOptionPane.showSingleSelectionOptionDialog(Static.getMainFrame(), title, message, options, selectedOption, showAgainQuestion, showAgainQuestionSelection);
        if (choosedMetaModelAnswer == null) {
            return null;
        }
        MetaModelContext choosedMetaModelContext = choosedMetaModelAnswer.getFirstItem();
        UserProperties.set(StringProperty.META_MODEL, choosedMetaModelContext.getMetaModelID());
        UserProperties.set(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG, Boolean.TRUE.equals(choosedMetaModelAnswer.getSecondItem()));
        return choosedMetaModelContext;
    }

    /**
     * Liefert den {@link MetaModelContext} anhand seiner ID. Das ist der Klassennamen@SerialVersionUID. Bei alten Modellen ist es nur der
     * Klassenname.
     *
     * @param metaModelContextID
     * @return
     */
    public static final MetaModelContext getMetaModelContextForID(String metaModelContextID) {
        if (Strings.isNullOrEmpty(metaModelContextID)) {
            metaModelContextID = DEFAULT_METAMDOEL_CLASS_NAME;
        }
        for (MetaModelContext metaModelContext : metaModelContexts) {
            String otherID = metaModelContext.getMetaModelID();
            if (otherID.equals(metaModelContextID)) {
                return metaModelContext;
            }
            //bevor die Metamodelle mit der SerialVersionUID gekennzeichnet wurden, war einzig der SimpleClassName der Metamodellklasse die ID -> für alte Modelle daruf testen
            otherID = metaModelContext.getMetaModelDefinitionClass().getSimpleName();
            if (otherID.equals(metaModelContextID)) {
                return metaModelContext;
            }
        }
        return null;
    }

}