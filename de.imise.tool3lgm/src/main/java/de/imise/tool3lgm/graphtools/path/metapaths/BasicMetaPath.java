package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath.InvalidityCheckResult;
import de.imise.util.ReflectionUtils;
import de.imise.util.collections.CollectionUtils;

public abstract class BasicMetaPath extends MetaModelSpecificAdapter {

    /**
     * Leere Elementarpfadliste
     */
    protected static final ImmutableList<ElementaryMetaPath> EMPTY_ELEMENTARY_PATH_LIST = ImmutableList.of();

    /**
     * Liste aller Startklassen dieses Pfades.
     */
    protected Set<Class<? extends ModelElement>> startElementClasses = null;

    /**
     * Liste aller Endklassen dieses Pfades.
     */
    protected Set<Class<? extends ModelElement>> endElementClasses = null;

    /**
     * Anzeigename des Pfades. Falls es ein
     */
    protected String name = null;

    /**
     * Anzeigename mit Start- und Endklassen. Kann in Unterlassen geändert
     * werden.
     */
    protected String fullName = null;

    /**
     * MataPath für die Gegenrichtung dieses Pfades. Der ist nur nicht
     * <code>null</code>, wenn er sich tatsächlich feststellen lässt.
     */
    protected MetaPath otherDirection = null;

    /**
     * Dieses Objekt gibt den Grund an, warum ein MetaPath nicht valide ist. Ist
     * der darin enthaltene InvalidReason <code>null</code>, gilt der MetaPath
     * als valide.
     */
    protected InvalidityCheckResult invalidityCheckResult;

    /**
     * If <code>true</code> and the metapath can be recursive (ends with an
     * element type that can be the start element type) then the path is
     * interpreted as recursive.
     */
    private boolean interpretAsRecursive;

    /** the metamodel */
    protected final MetaModel metaModel;

    /**
     * @param metaModel
     */
    public BasicMetaPath(final MetaModel metaModel) {
        this(metaModel, null);
    }

    /**
     * @param metaModel
     * @param name Anzeigenamen
     */
    public BasicMetaPath(final MetaModel metaModel, final String name) {
        this(metaModel, (Class<? extends ModelElement>) null, (Class<? extends ModelElement>) null, name);
    }

    /**
     * @param metaModel
     * @param startElementClass
     * @param endElementClass
     */
    public BasicMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass) {
        this(metaModel, startElementClass, endElementClass, null);

    }

    /**
     * @param metaModel
     * @param startElementClass
     * @param endElementClass
     * @param name
     */
    public BasicMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass, final String name) {
        this(metaModel, startElementClass != null ? ImmutableSet.of(startElementClass) : null, endElementClass != null ? ImmutableSet.of(endElementClass) : null, name);

    }

    /**
     * @param metaModel
     * @param startElementClasses
     * @param endElementClasses
     * @param name
     */
    public BasicMetaPath(final MetaModel metaModel, final Set<Class<? extends ModelElement>> startElementClasses, final Set<Class<? extends ModelElement>> endElementClasses, final String name) {
        super(metaModel);
        this.metaModel = metaModel;
        this.startElementClasses = CollectionUtils.ensureImmutable(startElementClasses);
        this.endElementClasses = CollectionUtils.ensureImmutable(endElementClasses);
        this.name = name;
    }

    /**
     * @return
     */
    public final Set<Class<? extends ModelElement>> getStartClasses() {
        return startElementClasses;
    }

    /**
     * @return
     */
    public Class<? extends ModelElement> getStartClass() {
        Set<Class<? extends ModelElement>> startClasses = getStartClasses();
        Class<? extends ModelElement> commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(startClasses);
        return commonSuperClass;
    }

    /**
     * @return
     */
    public final Set<Class<? extends ModelElement>> getEndClasses() {
        return endElementClasses;
    }

    /**
     * @return
     */
    public Class<? extends ModelElement> getEndClass() {
        Set<Class<? extends ModelElement>> endClasses = getEndClasses();
        Class<? extends ModelElement> commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(endClasses);
        return commonSuperClass;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine
     * Startklasse dieses Metapfades ist.
     *
     * @param elementClass Elementklasse, die als Startklasse geprüft werden
     *            soll
     * @return
     */
    public boolean isStartClass(final Class<? extends ModelElement> elementClass) {
        for (Class<? extends ModelElement> startClass : getStartClasses()) {
            if (startClass.isAssignableFrom(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine
     * Startklasse dieses Metapfades ist.
     *
     * @param elementClasses Elementklassen, die als Startklasse geprüft werden
     *            soll
     * @return
     */
    public final boolean isStartClass(final Collection<Class<? extends ModelElement>> elementClasses) {
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (isStartClass(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine
     * Endklasse dieses MetaPfades ist.
     *
     * @param elementClass Elementklasse, die als Endklasse geprüft werden soll
     * @return
     */
    public boolean isEndClass(final Class<? extends ModelElement> elementClass) {
        for (Class<? extends ModelElement> endClass : getEndClasses()) {
            if (endClass.isAssignableFrom(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine
     * Endklasse dieses MetaPfades ist.
     *
     * @param elementClasses Elementklasseen, die als Endklasse geprüft werden
     *            sollen
     * @return
     */
    public final boolean isEndClass(final Collection<Class<? extends ModelElement>> elementClasses) {
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (isEndClass(elementClass)) {
                return true;
            }
        }
        return false;
    }

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
    public final boolean isStartAndEndClass(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        return isStartClass(startClass) && isEndClass(endClass);
    }

    /**
     * @return <code>true</code>, if the metapath is applicable to the
     *         endelements as startelements otherwise <code>false</code>
     */
    public final boolean isInterpretAsRecursive() {
        return interpretAsRecursive;
    }

    /**
     * @param
     */
    public final void setInterpretAsRecursive(final boolean interpretAsRecursive) {
        this.interpretAsRecursive = interpretAsRecursive;
    }

    /**
     * @return <code>true</code> if at least one endElement class can be the
     *         startElement class of this metaPath
     */
    protected boolean canBeRecursive() {
        Set<Class<? extends ModelElement>> endClasses = getEndClasses();
        for (Class<? extends ModelElement> endClass : endClasses) {
            if (isStartClass(endClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn das übergebene Objekt dieselben
     * Eigenschaften hat, wie this.
     *
     * @param obj
     * @param ignoreName Wenn <code>true</code> wird die Gleichheit des Namens
     *            nicht mitgeprüft
     * @return
     */
    public boolean equals(final Object obj, final boolean ignoreName) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MetaPath other = (MetaPath) obj;
        if (endElementClasses == null) {
            if (other.endElementClasses != null) {
                return false;
            }
        } else if (!endElementClasses.equals(other.endElementClasses)) {
            return false;
        }
        if (!ignoreName) {
            if (fullName == null) {
                if (other.fullName != null) {
                    return false;
                }
            } else if (!fullName.equals(other.fullName)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
        }
        if (startElementClasses == null) {
            if (other.startElementClasses != null) {
                return false;
            }
        } else if (!startElementClasses.equals(other.startElementClasses)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (endElementClasses == null ? 0 : endElementClasses.hashCode());
        result = prime * result + (fullName == null ? 0 : fullName.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (startElementClasses == null ? 0 : startElementClasses.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        return equals(obj, true);
    }

    /**
     * @return
     */
    protected abstract String createName();

    /**
     * @return
     */
    public final String getName() {
        if (Strings.isNullOrEmpty(name)) {
            name = createName();
        }
        return name;
    }

    /**
     * @return
     */
    public final String getFullName() {
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = getName(true, true);
        }
        return fullName;
    }

    /**
     * @param withStartClasses
     * @param withEndClasses
     * @return
     */
    public String getName(final boolean withStartClasses, final boolean withEndClasses) {
        if (withStartClasses || withEndClasses) {
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            if (withStartClasses && withEndClasses) {
                return elementsNameBuilder.getDisplayableClassesNames(getStartClasses()) + " " + getName() + " " + elementsNameBuilder.getDisplayableClassesNames(getEndClasses());
            } else if (withStartClasses) {
                return elementsNameBuilder.getDisplayableClassesNames(getStartClasses()) + " " + getName();
            } else if (withEndClasses) {
                return getName() + " " + elementsNameBuilder.getDisplayableClassesNames(getEndClasses());
            }
        }
        return getName();
    }

    @Override
    public String toString() {
        return getName();
    }

}
