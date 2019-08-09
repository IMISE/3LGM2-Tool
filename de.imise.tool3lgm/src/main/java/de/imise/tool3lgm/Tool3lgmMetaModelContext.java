package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition.DefaultMetaModelDefinitionAdapter;
import de.imise.tool3lgm.graphtools.metamodel.RegularMetaModelDefinition;
import de.imise.tool3lgm.imexport.ImportMetaModelDefinition;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.NamedObjectContainer;
import de.imise.util.PluginUtils;
import de.imise.util.ReflectionUtils;
import de.imise.util.pair.Pair;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Klasse, die die verfügbaren Metamodelle verwaltet, initialisiert und bereitstellt.
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

    /** Liste aller MetaModelContext, die im Plugins-Ordner gefunden wurden */
    private static final List<MetaModelContext> ALL_METAMODEL_CONTEXTS = loadMetaModelContexts();

    /**
     * Alle MetaModel-Kontexte, die das Interface {@link RegularMetaModelDefinition} implementieren. Das sind die Metamodell-Definitionen, die man zum
     * Modellieren nehmen kann. Die Standardmetamodellklasse (= die Klasse mit dem Namen DEFAULT_METAMDOEL_NAME) befindet sich immer an Position 0)
     */
    private static final List<MetaModelContext> REGULAR_METAMODEL_CONTEXTS = getMetaModelContexts(ALL_METAMODEL_CONTEXTS, RegularMetaModelDefinition.class);

    /**
     * Liefert aus der übergebenen Liste von {@link MetaModelContext} eine neue Liste aller Kontexte zurück, deren Definitionsklasse
     * zuwesiungskompatibel zur übergebenen Klasse ist.
     *
     * @param metaModelContexts
     * @param metaModelDefinitonClassOrSuperClass
     * @return
     */
    private static final List<MetaModelContext> getMetaModelContexts(final List<MetaModelContext> metaModelContexts, final Class<?> metaModelDefinitonClassOrSuperClass) {
        List<MetaModelContext> returnList = new ArrayList<>();
        for (MetaModelContext metaModelContext : metaModelContexts) {
            Class<? extends MetaModelDefinition> metaModelDefinitionClass = metaModelContext.getMetaModelDefinitionClass();
            if (metaModelDefinitonClassOrSuperClass.isAssignableFrom(metaModelDefinitionClass)) {
                returnList.add(metaModelContext);
            }
        }
        return returnList;
    }

    /**
     * @param metaModelDefinitionClass
     * @return <code>true</code>, wenn die <code>metaModelDefinitionClass</code> das Interface {@link RegularMetaModelDefinition} implementiert
     */
    public static boolean isRegularMetaModelDefinition(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        return RegularMetaModelDefinition.class.isAssignableFrom(metaModelDefinitionClass);
    }

    /**
     * Liefert einen MetaModelContext. Abhängig davon, ob in den UserProperties eingestellt ist, ob per Dialog nachgefragt werden soll oder nicht,
     * wird der in den den UserProperties gespeicherte einfach zurück gegeben oder per Dialog nachgefragt.
     *
     * @return gewählten MetaModelContext oder <code>null</code>, wenn im AuswahlDialog auf Abbrechen gdrückt wurde
     */
    public static MetaModelContext getNewModelMetaModelContext() {
        boolean showDialog = OPTION_SHOW_CHOOSE_METAMODEL_DIALOG.is();
        MetaModelContext metaModelContext = null;
        if (!showDialog) {
            metaModelContext = getUserpropertiesStoredMetaModelContext();
        }
        if (metaModelContext == null) {
            if (showDialog) {
                metaModelContext = chooseMetaModel();
            } else {
                metaModelContext = REGULAR_METAMODEL_CONTEXTS.get(0); // es war gewünscht worden, dass beim initialen Start das originale Metamodell ausgewählt ist. showDialog ist initial false
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
        List<Class<? extends MetaModelDefinition>> metaModelClasses = PluginUtils.loadClasses(Tool3lgmConstants.PLUGIN_DIR, MetaModelDefinition.class);
        ReflectionUtils.retainSubClasses(metaModelClasses, RegularMetaModelDefinition.class);
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
        return REGULAR_METAMODEL_CONTEXTS.get(0);
    }

    /**
     * Liefert anhand der ID des in den UserProperties gespeicherten MetaModels den zugehörigen Kontext.
     *
     * @return
     */
    private static final MetaModelContext getUserpropertiesStoredMetaModelContext() {
        String storedMetaModelID = StringProperty.META_MODEL.get();
        if (!Strings.isNullOrEmpty(storedMetaModelID)) {
            for (MetaModelContext metaModelContext : REGULAR_METAMODEL_CONTEXTS) {
                if (metaModelContext.getMetaModelID().equals(storedMetaModelID)) {
                    return metaModelContext;
                }
            }
        }
        return null;
    }

    public static final MetaModelContext chooseMetaModel() {
        int optionsCount = REGULAR_METAMODEL_CONTEXTS.size();
        MetaModelContext lastMetaModelContext = getUserpropertiesStoredMetaModelContext();
        String title = getResString("choose_meta_model_dialog_title");
        String message = null;
        List<NamedObjectContainer<MetaModelContext>> options = new ArrayList<>();
        NamedObjectContainer<MetaModelContext> selectedOption = null;
        for (int i = 0; i < optionsCount; i++) {
            MetaModelContext metaModelContext = REGULAR_METAMODEL_CONTEXTS.get(i);
            NamedObjectContainer<MetaModelContext> metaModelContextContainer = new NamedObjectContainer<>(metaModelContext, metaModelContext.getMetaModelDisplayName());
            options.add(metaModelContextContainer);
            if (i == 0 || lastMetaModelContext == metaModelContext) {
                selectedOption = options.get(i);
            }
        }
        String showAgainQuestion = getResString("show_this_dialog_at_start");
        boolean showAgainQuestionSelection = OPTION_SHOW_CHOOSE_METAMODEL_DIALOG.is();
        Pair<NamedObjectContainer<MetaModelContext>, Boolean> choosedMetaModelAnswer = MultipleOptionPane.showSingleSelectionOptionDialog(Static.getMainFrame(), title, message, options, selectedOption, showAgainQuestion, showAgainQuestionSelection);
        if (choosedMetaModelAnswer == null) {
            return null;
        }
        NamedObjectContainer<MetaModelContext> choosedMetaModelContextContainer = choosedMetaModelAnswer.getFirstItem();
        MetaModelContext choosedMetaModelContext = choosedMetaModelContextContainer.getObject();
        StringProperty.META_MODEL.set(choosedMetaModelContext.getMetaModelID());
        OPTION_SHOW_CHOOSE_METAMODEL_DIALOG.set(Boolean.TRUE.equals(choosedMetaModelAnswer.getSecondItem()));
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
        for (MetaModelContext metaModelContext : REGULAR_METAMODEL_CONTEXTS) {
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

    /**
     * Liefert einen Kontext zur übergebenen Definition. Wenn es diesen Kontext bereits in der Liste aller Kontexte gibt, wird dieser zurück gegeben.
     * Gibt es ihn nicht, wird einer neuer erzeugt und in die Liste aller Kontexte eingefügt. Hier wird die Klasse auf Identität geprüft.
     *
     * @param metaModelDefinitionClass
     * @return
     */
    public static final MetaModelContext getMetaModelContextForDefinitionClass(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        for (MetaModelContext metaModelContext : ALL_METAMODEL_CONTEXTS) {
            if (metaModelContext.hasDefinitionClass(metaModelDefinitionClass)) {
                return metaModelContext;
            }
        }
        MetaModelContext metaModelContext = new MetaModelContext(metaModelDefinitionClass);
        //die Definition nur merken, wenn es keine ImportDefinition ist. Die ImportDefinitionen sind sehr einfach aufgebaut und können einfach immer wieder initialisiert werden
        if (!ImportMetaModelDefinition.class.isAssignableFrom(metaModelDefinitionClass)) {
            ALL_METAMODEL_CONTEXTS.add(metaModelContext);
        }
        return metaModelContext;
    }

}