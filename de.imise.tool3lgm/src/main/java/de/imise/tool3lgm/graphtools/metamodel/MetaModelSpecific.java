package de.imise.tool3lgm.graphtools.metamodel;

import java.util.MissingResourceException;
import java.util.Objects;

import com.google.common.base.Strings;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.ErrorSolutionLibrary;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;
import de.imise.util.ReflectionUtils;

/**
 * @author AXS (02.09.2019)
 */
public interface MetaModelSpecific {

    /**
     * @return the class with the defintion for this metamodel
     */
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass();

    /**
     * @return the metamodel context
     */
    public default MetaModelContext getMetaModelContext() {
        Class<? extends MetaModelDefinition> metaModelDefintionClass = getMetaModelDefinitionClass();
        return Tool3lgmMetaModelContext.getMetaModelContextForDefinitionClass(metaModelDefintionClass);
    }

    /**
     * Returns the metamodel. If the context has not initialzed the metamodel,
     * it will be initialzed.
     *
     * @return the metamodel
     */
    public default MetaModel getMetaModel() {
        MetaModelContext metaModelContext = getMetaModelContext();
        MetaModel metaModel = metaModelContext.getMetaModel();
        return metaModel;
    }

    /**
     * Liefert die ID der Metamodellklasse. Dies ist ein String aus dem SimpleClassName + "@" + serialVersionUID. Damit sollte die die
     * Metamodellklasse immer eindeutig identifizierbar sein.
     *
     * @return
     */
    public default String getMetaModelID() {
        Class<? extends MetaModelDefinition> metaModelDefinitionClass = getMetaModelDefinitionClass();
        String name = metaModelDefinitionClass.getSimpleName();
        Long metaModelClassSerialVersionUID = ReflectionUtils.getField(metaModelDefinitionClass, "serialVersionUID");
        String idString = metaModelClassSerialVersionUID == null ? "" : "@" + String.valueOf(metaModelClassSerialVersionUID); // ein @ kann nicht im Klassenname vorkommen -> Trenner zwischen Klassenname und UID
        String classID = name + idString;
        return classID;
    }

    /**
     * @return the elements name builder for this metamodel
     */
    public default ElementsNameBuilder getElementsNameBuilder() {
        MetaModelContext metaModelContext = getMetaModelContext();
        ElementsNameBuilder elementsNameBuilder = metaModelContext.getElementsNameBuilder();
        return elementsNameBuilder;
    }

    /**
     * Handler für das einfache und nicht redundante Anlegen von Elementar-Metapfaden für dieses MetaModel
     *
     * @return
     */
    public default ElementaryMetaPathHandler getElementaryMetaPathHandler() {
        MetaModelContext metaModelContext = getMetaModelContext();
        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModelContext.getElementaryMetaPathHandler();
        return elementaryMetaPathHandler;
    }

    /**
     * @param resKey
     *            resource key
     * @return the string from the resources for the given resKey
     * @throws MissingResourceException
     */
    public default String getResString(final String resKey) {
        MetaModelContext metaModelContext = getMetaModelContext();
        String resString = metaModelContext.getResString(resKey);
        return resString;
    }

    /**
     * @param o
     * @return
     */
    public default String getResStringWithoutError(final Object o) {
        return getResStringWithoutError(null, o);
    }

    /**
     * @param prefix
     * @param o
     * @return
     */
    public default String getResStringWithoutError(final String prefix, final Object o) {
        boolean classObject = o != null && o instanceof Class<?>;
        String oString = classObject ? ((Class<?>) o).getSimpleName() : String.valueOf(o);
        String resKey = Strings.isNullOrEmpty(prefix) ? oString : String.valueOf(prefix) + oString;
        return getResStringWithoutError(resKey);
    }

    /**
     * @param resKey
     *            resource key
     * @return the string from the resources for the given resKey or the key if
     *         there is no string in the resources for the given key
     */
    public default String getResStringWithoutError(final String resKey) {
        try {
            String resString = getResString(resKey); //if getResString(..) is overwritten
            return resString;
        } catch (Exception e) {
            MetaModelContext metaModelContext = getMetaModelContext();
            String resString = metaModelContext.getResStringWithoutError(resKey);
            return resString;
        }
    }

    /**
     * @param metaModelContext
     * @return
     */
    public default boolean hasMetaModelContext(final MetaModelContext metaModelContext) {
        MetaModelContext thisMetaModelContext = getMetaModelContext();
        return Objects.equals(metaModelContext, thisMetaModelContext);
    }

    /**
     * @param metaModelDefinitionClass
     * @return <code>true</code> if the given class is the same or a superclass of of
     *         the metamodel definition class of this server
     */
    public default boolean hasMetaModelDefinitionClass(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        Class<? extends MetaModelDefinition> myMetaModelDefinitionClass = getMetaModelDefinitionClass();
        boolean assignable = ReflectionUtils.isAssignable(myMetaModelDefinitionClass, metaModelDefinitionClass);
        return assignable;
    }

    /**
     * @return the {@link ErrorSolutionLibrary} of the metamodel
     */
    public default ErrorSolutionLibrary getErrorSolutionLibrary() {
        MetaModel metaModel = getMetaModel();
        return metaModel.getErrorSolutionLibrary();
    }

}
