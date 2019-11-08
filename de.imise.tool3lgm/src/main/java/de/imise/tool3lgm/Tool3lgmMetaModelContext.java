package de.imise.tool3lgm;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition.DefaultMetaModelDefinitionAdapter;
import de.imise.tool3lgm.graphtools.metamodel.RegularMetaModelDefinition;
import de.imise.tool3lgm.gui.Tool3lgmMetaModelContextChooser;
import de.imise.tool3lgm.imexport.ImportMetaModelDefinition;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.ReflectionUtils;

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
    private static final String DEFAULT_METAMODEL_DEFINITION_CLASS_NAME = "TLGMOriginalMetaModel";

    /** Dummy-Instanz eines MetamodelContexts, um Null-Checks zu vermeiden */
    public static final MetaModelContext DUMMY_META_MODEL_CONTEXT = new MetaModelContext(DefaultMetaModelDefinitionAdapter.class);

    /** Dummy-Instanz eines Metamodells, um Null-Checks zu vermeiden */
    public static final MetaModel DUMMY_META_MODEL = DUMMY_META_MODEL_CONTEXT.getMetaModel();

    /** Liste aller MetaModelContext, die im Plugins-Ordner gefunden wurden */
    private static final List<MetaModelContext> ALL_METAMODEL_CONTEXTS = loadMetaModelContexts();

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
            if (ReflectionUtils.isAssignable(metaModelDefinitonClassOrSuperClass, metaModelDefinitionClass)) {
                returnList.add(metaModelContext);
            }
        }
        return returnList;
    }

    /**
     * Alle MetaModel-Kontexte, die das Interface {@link RegularMetaModelDefinition} implementieren. Das sind die Metamodell-Definitionen, die man zum
     * Modellieren nehmen kann. Die Standardmetamodellklasse (= die Klasse mit dem Namen DEFAULT_METAMDOEL_NAME) befindet sich immer an Position 0)
     */
    public static List<MetaModelContext> getRegularMetaModelContexts() {
        return getMetaModelContexts(ALL_METAMODEL_CONTEXTS, RegularMetaModelDefinition.class);
    }

    /**
     * @param metaModelDefinitionClass
     * @return <code>true</code>, wenn die <code>metaModelDefinitionClass</code> das Interface {@link RegularMetaModelDefinition} implementiert
     */
    public static boolean isRegularMetaModelDefinition(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        return ReflectionUtils.isAssignable(RegularMetaModelDefinition.class, metaModelDefinitionClass);
    }

    public static Tool3lgmModelType getDefaultModelType() {
        // es war gewünscht worden, dass beim initialen Start das originale Metamodell ausgewählt ist. showDialog ist initial false
        MetaModelContext metaModelContext = getDefaultMetaModelContext();
        ModelCategory modelCategory = ModelCategory.REGULAR;
        Tool3lgmModelType modelType = new Tool3lgmModelType(metaModelContext, modelCategory);
        return modelType;
    }

    /**
     * Liefert einen MetaModelContext. Abhängig davon, ob in den UserProperties eingestellt ist, ob per Dialog nachgefragt werden soll oder nicht,
     * wird der in den den UserProperties gespeicherte einfach zurück gegeben oder per Dialog nachgefragt.
     *
     * @return gewählten MetaModelContext oder <code>null</code>, wenn im AuswahlDialog auf Abbrechen gdrückt wurde
     */
    public static Tool3lgmModelType getNewModelType() {
        boolean showDialog = OPTION_SHOW_CHOOSE_METAMODEL_DIALOG.is();
        Tool3lgmModelType modelType = null;
        if (!showDialog) {
            modelType = getUserpropertiesStoredModelType();
        }
        if (modelType == null) {
            if (showDialog) {
                Tool3lgmMetaModelContextChooser contextChooserDialog = new Tool3lgmMetaModelContextChooser();
                modelType = contextChooserDialog.chooseModelType();
            } else {
                modelType = getDefaultModelType();
            }
        }
        return modelType;
    }

    /**
     * Lädt alle im Plaugin-Verzeichnis auffindbaren Metamodell-Klassen in jeweils einen MetaModelContext.
     *
     * @return
     */
    public static final List<MetaModelContext> loadMetaModelContexts() {
        List<Class<? extends MetaModelDefinition>> metaModelClasses = Static.loadClasses(MetaModelDefinition.class);
        ReflectionUtils.retainSubClasses(metaModelClasses, RegularMetaModelDefinition.class);
        List<MetaModelContext> metaModelContexts = new ArrayList<>();
        for (int i = 0; i < metaModelClasses.size(); i++) {
            Class<? extends MetaModelDefinition> metaModelClass = metaModelClasses.get(i);
            MetaModelContext metaModelContext = new MetaModelContext(metaModelClass);
            if (metaModelClass.getSimpleName().equals(DEFAULT_METAMODEL_DEFINITION_CLASS_NAME)) {
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
        return getRegularMetaModelContexts().get(0);
    }

    /**
     * Liefert anhand der ID des in den UserProperties gespeicherten MetaModels den zugehörigen Kontext und die {@link ModelCategory}
     *
     * @return
     */
    public static final Tool3lgmModelType getUserpropertiesStoredModelType() {
        String storedMetaModelID = StringProperty.META_MODEL.get();
        MetaModelContext metaModelContext = null;
        if (!Strings.isNullOrEmpty(storedMetaModelID)) {
            for (MetaModelContext context : getRegularMetaModelContexts()) {
                if (context.getMetaModelID().equals(storedMetaModelID)) {
                    metaModelContext = context;
                    break;
                }
            }
        }
        if (metaModelContext == null) {
            return null;
        }
        String storedModelCategory = StringProperty.MODEL_CATEGORY.get();
        ModelCategory modelCategory = null;
        if (!Strings.isNullOrEmpty(storedModelCategory)) {
            for (ModelCategory category : ModelCategory.values()) {
                if (category.name().equals(storedModelCategory)) {
                    modelCategory = category;
                    break;
                }
            }
        }
        return new Tool3lgmModelType(metaModelContext, modelCategory);
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
            metaModelContextID = DEFAULT_METAMODEL_DEFINITION_CLASS_NAME;
        }
        for (MetaModelContext metaModelContext : getRegularMetaModelContexts()) {
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
        if (metaModelDefinitionClass == null) {
            return DUMMY_META_MODEL_CONTEXT;
        }
        for (MetaModelContext metaModelContext : ALL_METAMODEL_CONTEXTS) {
            if (metaModelContext.hasDefinitionClass(metaModelDefinitionClass)) {
                return metaModelContext;
            }
        }
        MetaModelContext metaModelContext = new MetaModelContext(metaModelDefinitionClass);
        //die Definition nur merken, wenn es keine ImportDefinition ist. Die ImportDefinitionen sind sehr einfach aufgebaut und können einfach immer wieder initialisiert werden
        ALL_METAMODEL_CONTEXTS.add(metaModelContext);
        return metaModelContext;
    }

    /**
     * Löscht alle Import-Metamdodelle aus dem Kontext
     */
    public static final void removeAllImportMetaModels() {
        for (int i = ALL_METAMODEL_CONTEXTS.size() - 1; i >= 0; i--) {
            MetaModelContext metaModelContext = ALL_METAMODEL_CONTEXTS.get(i);
            Class<? extends MetaModelDefinition> metaModelDefinitionClass = metaModelContext.getMetaModelDefinitionClass();
            if (ImportMetaModelDefinition.class.isAssignableFrom(metaModelDefinitionClass)) {
                ALL_METAMODEL_CONTEXTS.remove(i);
            }
        }
    }

}