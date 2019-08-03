package de.imise.tool3lgm;

import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.ReflectionUtils;
import de.imise.util.SimpleResourceHandler;

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
    private final SimpleResourceHandler metaModelResourceBundle;

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
        Locale locale = UserProperties.getLocale();
        String metaModelName;
        SimpleResourceHandler metaModelResourceBundle;
        try {
            metaModelResourceBundle = new SimpleResourceHandler(metaModelDefinitionClass, Tool3lgmConstants.METAMODEL_RESOURCE_BASE_NAME, locale);
            String metaModelNameResKey = metaModelDefinitionClass.getSimpleName(); //immer der SimpleName der Klasse ist der Resourcenschlüssel zum Namen des Metamodells
            metaModelName = metaModelResourceBundle.getString(metaModelNameResKey);
        } catch (Exception e) {
            metaModelResourceBundle = null;
            metaModelName = metaModelDefinitionClass.getSimpleName();
        }
        this.metaModelResourceBundle = metaModelResourceBundle;
        this.metaModelName = metaModelName;
    }

    /**
     * @return Klasse der MetaModell-Definition dieses Kontextes
     */
    public final Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return metaModelDefinitionClass;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Definitionklasse des Metamodells dieselbe ist, die für diesen Kontext gesetzt ist.
     *
     * @param metaModelDefinitionClass
     * @return
     */
    public final boolean hasDefinitionClass(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        return Objects.equals(this.metaModelDefinitionClass, metaModelDefinitionClass);
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
        try {
            return metaModelResourceBundle.getString(key);
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
        Long metaModelClassSerialVersionUID = ReflectionUtils.getField(metaModelDefinitionClass, "serialVersionUID", Long.TYPE);
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
