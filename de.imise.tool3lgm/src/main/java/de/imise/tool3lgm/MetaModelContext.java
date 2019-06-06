package de.imise.tool3lgm;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.ReflectionUtils;

/**
 * Klasse, die die Klasse eines Metamodells und das dazugehörige ResouceBundle dieses Metamodells enthält. Es enthält noch nicht das MetaModel
 * sondern das MetaModel wird mit diesem Kontext hier initilisiert. Man braucht das ResourceBundle der Metamodelle bevor man eine Instanz der
 * Metamodell-Klasse bildet, um bei der Auswahl, welches Metmodell überhaupt instanziiert werden soll, den Namen des Metamodells anzeigen zu können.
 * So kann man dafür sorgen, dass die Metamodelle nur instanziiert und damit initialisiert werden, wenn man sie wirklich braucht, um damit eine
 * Modelldatei zu erzeugen.
 *
 * @author AXS (8 May 2019)
 */
public final class MetaModelContext {

    /** Klasse des Metamodells */
    private final Class<? extends MetaModelDefinition> metaModelDefinitionClass;

    /** Anzeigename des Metamodells */
    public final String metaModelName;

    /** Das tatsächlich über die MetaModelDefintion initialiserte Metamodell */
    private MetaModel metaModel;

    /** Das ResourceBundle des MetaModels. Es ist nur nicht <code>null</code>, wenn auch metaModel nicht <code>null</code> ist */
    private ResourceBundle metaModelResourceBundle;

    /**
     * NameBuilder für die metamodellspezifischen Knoten- und Kantenklassen. Auch diese Klasse wird erst initialisiert, wenn das Metamodel geladen
     * bzw. initialisiert wird.
     */
    private ElementsNameBuilder elementsNameBuilder;

    /**
     * Initialisiert den Kontext. Dabei wird das ResourceBundle einmal geladen, um an den Namen des Metamodells zu kommen. Das ResourceBundel wird
     * aber nicht gespeichert.
     *
     * @param metaModelDefinitionClass
     */
    public MetaModelContext(@Nonnull final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        this.metaModelDefinitionClass = metaModelDefinitionClass;
        ResourceBundle resources = getMetaModelResources();
        String metaModelName;
        try {
            metaModelName = getMetaModelDisplayName(resources);
        } catch (Exception e) {
            metaModelName = metaModelDefinitionClass.getSimpleName();
        }
        this.metaModelName = metaModelName;
    }

    /**
     * @return Klasse der MetaModell-Definition dieses Kontextes
     */
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return metaModelDefinitionClass;
    }

    /**
     * Lädt das ResoruceBundle zu diesem Metamdoell und gibt es zurück. Wird keines gefunden, kommt ohne Fehler <code>null</code> zurück.
     *
     * @return ResoruceBundle zu diesem Metamdoell
     */
    private final ResourceBundle getMetaModelResources() {
        if (metaModelResourceBundle != null) {
            return metaModelResourceBundle;
        }
        Locale locale = UserProperties.getLocale();
        ClassLoader loader = metaModelDefinitionClass.getClassLoader();
        String baseName = getMetamodelBundleName(metaModelDefinitionClass);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle(baseName, locale, loader);
        } catch (Exception e) {
        }
        return resourceBundle;
    }

    /**
     * Lädt das ResoruceBundle zur übergebenen Metamodell-Klasse und gibt es zurück
     *
     * @param metaModelDefinitionClass
     * @return ResoruceBundle zur übergebenen Metamodell-Klasse
     */
    private static String getMetamodelBundleName(@Nonnull final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        //das Metamodel-Resourcebundle liegt im resource-package unter demselben Pfad, wie die Metamodellklasse des Packages.
        //der ClassLoader, der das package lädt, erwartet relative Pfade ab dem Pfad dieser Klasse hier, die das Bundle lädt.
        //z.B. liegt das speziele Metamodel im package "de.imise.tool3lgm.metamodel.tlgm_v3_0". Diese Klasse Tool3lgmConstants
        //liegt im Hauptpackage "de.imise.tool3lgm". Das Resource-Bundle kann mit dem BundleName "metamodel.tlgm_v3_0.MetamodelResources"
        //geladen werden. Also muss man vom package-Namen des Metamodells den package-Namen der Tool3lgmConstants abziehen und den
        //vorgegebenen Bundle-Name "MetamodelResources" anhängen (mit Punkt dazwischen).
        //        String mainPackageName = Tool3lgmConstants.class.getPackage().getName();
        String metaModelPackageName = metaModelDefinitionClass.getPackage().getName();
        //        String bundleName = metaModelPackageName.substring(mainPackageName.length() + 1) + "." + METAMODEL_RESOURCE_BASE_NAME;
        String bundleName = metaModelPackageName + "." + Tool3lgmConstants.METAMODEL_RESOURCE_BASE_NAME;
        return bundleName;
    }

    /**
     * Liefert den Anzeigenamen des Metamodells aus den übergebenen Resourcen. Der Schlüssel entspricht dem simplen Klassennamen.
     *
     * @param resources
     * @return
     */
    private final String getMetaModelDisplayName(final ResourceBundle resources) {
        String metaModelNameResKey = metaModelDefinitionClass.getSimpleName(); //immer der SimpleName der Klasse ist der Resourcenschlüssel zum Namen des Metamodells
        String metaModelName = resources.getString(metaModelNameResKey);
        return metaModelName;
    }

    /**
     * Liefert den Anzeigenamen des Metamodells
     *
     * @return
     */
    public String getMetaModelDisplayName() {
        return metaModelName;
    }

    @Override
    public String toString() {
        return getMetaModelID();
    }

    /**
     * Diese Funktion mach genau das umgekehrte wie die Funktion {@link Tool3lgmConstants#getResString(String)}. D.h. sie schaut zuerst in die
     * Resourcen des eigenen Metamodells und wenn sie dort den key nicht gefunden hat, dann in die allgemeinen des Tools. Im Unterschied zu der
     * Funktion aus den {@link Tool3lgmConstants} wird hier aber nicht in die Resourcen des aktuell selektierten Modells geschaut, sondern in die
     * dieses Modells hier.
     *
     * @param key
     * @return
     */
    public String getResString(final String key) {
        //das hier darf auf keinen Fall mit try-catch komplett umrandet werden, da mehrere Funktionen auf die
        //MissingResocureException regaieren (z.B. die Funktionen zum heraussuchen der Kantennamen bei
        //Kanten mit doppelter Bedeutung
        ResourceBundle metaModelResources = getMetaModelResources();
        try {
            return metaModelResources.getString(key);
        } catch (Exception e) {
            return Tool3lgmConstants.getResString(key);
        }
    }

    /**
     * Liefert die Klasse, über die alle Knoten- und Kantenklassennamen generiert werden, also die Anzeigenamen in Ein- und Mehrzahl und bei den
     * Kanten die gerichteten Namen.
     * Solange das Metamodell nicht initialisiert wurde, kommt hier immer eine neue Instanz des Builders zurück! Aber ohne dass das Metamodell
     * initilisiert wurde, gibt es eigentlich keinen Grund auf die Elementnamen zuzugreifen, da die vorhandenen Elemente nicht bekannt sind.
     *
     * @return
     */
    public ElementsNameBuilder getElementsNameBuilder() {
        if (elementsNameBuilder == null) {
            return new ElementsNameBuilder(this);
        }
        return elementsNameBuilder;
    }

    /**
     * Liefert die ID der Metamodellklasse. Dies ist ein String aus dem SimpleClassName + "@" + serialVersionUID. Damit sollte die die
     * Metamodellklasse immer eindeutig identifizierbar sein.
     *
     * @return
     */
    public final String getMetaModelID() {
        String name = metaModelDefinitionClass.getSimpleName();
        Long metaModelClassSerialVersionUID = ReflectionUtils.getField(metaModelDefinitionClass, "serialVersionUID", Long.class);
        String idString = metaModelClassSerialVersionUID == null ? "" : "@" + String.valueOf(metaModelClassSerialVersionUID); // ein @ kann nicht im Klassenname vorkommen -> Trenner zwischen Klassenname und UID
        String classID = name + idString;
        return classID;
    }

    /**
     * Liefert die tatsächliche Instanz des MetaModells. Wenn diese noch nicht initialisert ist, dann wird das hier getan. Das ResoruceBundle wird
     * ebenfalls dauerhaft gesetzt.
     *
     * @return
     */
    public MetaModel getMetaModel() {
        if (metaModel == null) {
            try {
                metaModelResourceBundle = getMetaModelResources();
                metaModel = new MetaModel(this);
                elementsNameBuilder = new ElementsNameBuilder(this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return metaModel;
    }

    /**
     * Hiermit kann man die Referenz auf das Metamodell in diesem Kontext löschen. Dadurch wird der Speicher freigegeben. Das ist sinnvoll, wenn man
     * das letzte Modell einer bestimmten Art geschlossen hat, um ein bisschen Platz zu schaffen.
     */
    public void unloadMetaModel() {
        metaModel = null;
    }
}
