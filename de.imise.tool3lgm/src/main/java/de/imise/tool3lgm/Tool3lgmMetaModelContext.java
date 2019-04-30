package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.NamedObjectContainer;
import de.imise.util.pair.Pair;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Klasse, die das Metamodell initialisiert
 *
 * @author AXS (12.06.2018)
 */
public class Tool3lgmMetaModelContext {

    /**
     * Name der Metamodellklasse, die als Default gesetzt werden soll. Das ist auch die Klasse, mit der Modelle geladen werden, bei denen kein
     * Metamodell abgegeben ist.
     */
    private static final String DEFAULT_METAMDOEL_CLASS_NAME = "TLGMOriginalMetaModel";

    private static Class<? extends MetaModel> metaModelClass = null;

    private static ResourceBundle resourceBundle = null;

    /**
     * Alle MetaModel-Klasse, die gefunden werden. Die Standardmetamodellklasse (= die Klasse mit dem Namen DEFAULT_METAMDOEL_NAME) befindet sich
     * immer an Position 0)
     */
    private static List<Class<? extends MetaModel>> META_MODEL_CLASSES = loadMetaModelClasses();

    private static void initMetaModel() {
        boolean showDialog = UserProperties.is(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG_AT_START);
        if (!showDialog) {
            metaModelClass = getUserpropertiesStoredMetaModel();
        }
        while (metaModelClass == null) {
            if (showDialog) {
                metaModelClass = chooseMetaModel(true);
            } else {
                metaModelClass = META_MODEL_CLASSES.get(0); // es war gewünscht worden, dass beim initialen Start das originale Metamodell ausgewählt ist. showDialog ist initial false
            }
        }
    }

    private static String getMetamodelBundleName() {
        //das Metamodel-Resourcebundle liegt im resource-package unter demselben Pfad, wie die Metamodellklasse des Packages.
        //der ClassLoader, der das package lädt, erwartet relative Pfade ab dem Pfad dieser Klasse hier, die das Bundle lädt.
        //z.B. liegt das speziele Metamodel im package "de.imise.tool3lgm.metamodel.tlgm_v3_0". Diese Klasse Tool3lgmConstants
        //liegt im Hauptpackage "de.imise.tool3lgm". Das Resource-Bundle kann mit dem BundleName "metamodel.tlgm_v3_0.MetamodelResources"
        //geladen werden. Also muss man vom package-Namen des Metamodells den package-Namen der Tool3lgmConstants abziehen und den
        //vorgegebenen Bundle-Name "MetamodelResources" anhängen (mit Punkt dazwischen).
        //        String mainPackageName = Tool3lgmConstants.class.getPackage().getName();
        String metaModelPackageName = Tool3lgmMetaModelContext.getMetaModelClass().getPackage().getName();
        //        String bundleName = metaModelPackageName.substring(mainPackageName.length() + 1) + "." + METAMODEL_RESOURCE_BASE_NAME;
        String bundleName = metaModelPackageName + "." + Tool3lgmConstants.METAMODEL_RESOURCE_BASE_NAME;
        return bundleName;
    }

    public static final ResourceBundle getMetaModelResources() {
        if (resourceBundle == null) {
            Locale locale = UserProperties.getLocale();
            ClassLoader loader = metaModelClass.getClassLoader();
            String baseName = getMetamodelBundleName();
            resourceBundle = ResourceBundle.getBundle(baseName, locale, loader);
        }
        return resourceBundle;
    }

    public static final List<Class<? extends MetaModel>> loadMetaModelClasses() {
        List<Class<? extends MetaModel>> metaModelClasses = new ArrayList<>();
        Class<? extends MetaModel> defaultMetaModelClass = null;
        File pluginDir = new File(Tool3lgmConstants.APPLICATION_DIR.getParentFile(), "Plugins");
        //        System.err.println(pluginDir);
        FileNameExtensionFilter jarFileFilter = Tool3lgmConstants.getFileNameExtensionFilter(Tool3lgmConstants.FileFilterType.JAR);
        for (File f : pluginDir.listFiles()) {
            if (!jarFileFilter.accept(f)) {
                continue;
            }
            //System.err.println(f);
            try {
                URL[] urls = {
                        new URL("jar:file:" + f.toString() + "!/")
                };
                URLClassLoader cl = URLClassLoader.newInstance(urls);
                JarFile jarFile = new JarFile(f);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.isDirectory()) {
                        continue;
                    }
                    String entryName = entry.getName();
                    if (entryName.endsWith(".class")) {
                        //System.err.println(entryName);
                        String className = entryName.substring(0, entryName.length() - 6); // ".class" abschneiden
                        className = className.replace('/', '.');
                        Class<?> c = cl.loadClass(className);
                        if (MetaModel.class.isAssignableFrom(c)) {
                            Class<? extends MetaModel> metaModelClass = c.asSubclass(MetaModel.class);
                            if (c.getSimpleName().equals(DEFAULT_METAMDOEL_CLASS_NAME)) {
                                defaultMetaModelClass = metaModelClass;
                            } else {
                                metaModelClasses.add(metaModelClass);
                                //System.err.println(c);
                            }
                        }
                    }
                }
                jarFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        metaModelClasses.add(0, defaultMetaModelClass);
        return metaModelClasses;
    }

    /**
     * Liefert das Standardmetamodell des Tools.
     *
     * @return
     */
    public static final Class<? extends MetaModel> getDefaultMetaModelClass() {
        return META_MODEL_CLASSES.get(0);
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
            if (initialSelection) {
                System.exit(0);
            }
            return null;
        }
        NamedObjectContainer<Class<? extends MetaModel>> choosedMetaModelClassContainer = choosedMetaModelAnswer.getFirstItem();
        Class<? extends MetaModel> choosedMetaModelClass = choosedMetaModelClassContainer.getObject();
        UserProperties.set(StringProperty.META_MODEL, choosedMetaModelClass.getSimpleName());
        UserProperties.set(BooleanProperty.OPTION_SHOW_CHOOSE_METAMODEL_DIALOG_AT_START, Boolean.TRUE.equals(choosedMetaModelAnswer.getSecondItem()));
        return choosedMetaModelClass;
    }

    public static final Class<? extends MetaModel> getMetaModelClassForName(final String simpleClassName) {
        for (Class<? extends MetaModel> metaModelClass : META_MODEL_CLASSES) {
            String name = metaModelClass.getSimpleName();
            if (name.equals(simpleClassName)) {
                return metaModelClass;
            }
        }
        return null;
    }

    public static final String getDisplayableName(final Class<? extends MetaModel> metaModelClass) {
        return getResString(metaModelClass.getSimpleName());
    }

}