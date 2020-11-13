package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.Collection;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

public interface IBasicMetaPath extends MetaModelSpecific {

    /**
     * @return
     */
    Set<Class<? extends ModelElement>> getStartClasses();

    /**
     * @return
     */
    Class<? extends ModelElement> getStartClass();

    /**
     * @return
     */
    Set<Class<? extends ModelElement>> getEndClasses();

    /**
     * @return
     */
    Class<? extends ModelElement> getEndClass();

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine
     * Startklasse dieses Metapfades ist.
     *
     * @param elementClass Elementklasse, die als Startklasse geprüft werden
     *            soll
     * @return
     */
    boolean isStartClass(Class<? extends ModelElement> elementClass);

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine
     * Startklasse dieses Metapfades ist.
     *
     * @param elementClasses Elementklassen, die als Startklasse geprüft werden
     *            soll
     * @return
     */
    boolean isStartClass(Collection<Class<? extends ModelElement>> elementClasses);

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine
     * Endklasse dieses MetaPfades ist.
     *
     * @param elementClass Elementklasse, die als Endklasse geprüft werden soll
     * @return
     */
    boolean isEndClass(Class<? extends ModelElement> elementClass);

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine
     * Endklasse dieses MetaPfades ist.
     *
     * @param elementClasses Elementklasseen, die als Endklasse geprüft werden
     *            sollen
     * @return
     */
    boolean isEndClass(Collection<Class<? extends ModelElement>> elementClasses);

    /**
     * Liefert <code>true</code>, wenn die übergebene startClass Startklasse und
     * die übergeben endCLass Endklasse dieses MetaPfades sein kann.
     *
     * @param startClass Elementklasse, die als Startklasse der Kantenklasse
     *            geprüft werden soll
     * @param startClass Elementklasse, die als Endklasse der Kantenklasse
     *            geprüft werden soll
     * @return
     */
    boolean isStartAndEndClass(Class<? extends ModelElement> startClass, Class<? extends ModelElement> endClass);

    /**
     * @return <code>true</code>, if the metapath is applicable to the
     *         endelements as startelements otherwise <code>false</code>
     */
    boolean isInterpretAsRecursive();

    /**
     * @param
     */
    void setInterpretAsRecursive(boolean interpretAsRecursive);

    /**
     * @return <code>true</code> if at least one endElement class can be the
     *         startElement class of this metaPath
     */
    public boolean canBeRecursive();

    /**
     * Liefert <code>true</code>, wenn das übergebene Objekt dieselben
     * Eigenschaften hat, wie this.
     *
     * @param obj
     * @param ignoreName Wenn <code>true</code> wird die Gleichheit des Namens
     *            nicht mitgeprüft
     * @return
     */
    boolean equals(Object obj, boolean ignoreName);

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    /**
     * @return
     */
    String createName();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    String getFullName();

    /**
     * @param withStartClasses
     * @param withEndClasses
     * @return
     */
    String getName(boolean withStartClasses, boolean withEndClasses);

    @Override
    String toString();

}