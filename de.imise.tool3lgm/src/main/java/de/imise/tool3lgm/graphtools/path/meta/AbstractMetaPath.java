package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Oberklasse für alle Metapfade.
 *
 * @author AXS
 * @create 12.10.2010
 */
public abstract class AbstractMetaPath {

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
     * Anzeigename mit Start- und Endklassen. Kann in Unterlassen geändert werden.
     */
    protected String fullName = null;

    /**
     * MataPath für die Gegenrichtung dieses Pfades. Der ist nur nicht <code>null</code>, wenn er sich tatsächlich
     * feststellen lässt.
     */
    protected AbstractMetaPath otherDirection = null;

    /**
     *
     */
    public AbstractMetaPath() {
        this(null);
    }

    /**
     * @param name
     *            Anzeigenamen
     */
    public AbstractMetaPath(final String name) {
        this((Class<? extends ModelElement>) null, (Class<? extends ModelElement>) null, name);
    }

    /**
     * @param startElementClass
     * @param endElementClass
     */
    public AbstractMetaPath(final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass) {
        this(startElementClass, endElementClass, null);

    }

    /**
     * @param startElementClass
     * @param endElementClass
     * @param name
     */
    public AbstractMetaPath(final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass, final String name) {
        this(startElementClass != null ? ImmutableSet.of(startElementClass) : null, endElementClass != null ? ImmutableSet.of(endElementClass) : null, name);

    }

    /**
     * @param startElementClasses
     * @param endElementClasses
     * @param name
     */
    public AbstractMetaPath(final Set<Class<? extends ModelElement>> startElementClasses, final Set<Class<? extends ModelElement>> endElementClasses, final String name) {
        this.startElementClasses = startElementClasses == null ? ImmutableSet.of() : ImmutableSet.class.isAssignableFrom(startElementClasses.getClass()) ? startElementClasses : ImmutableSet.copyOf(startElementClasses);
        this.endElementClasses = endElementClasses == null ? ImmutableSet.of() : ImmutableSet.class.isAssignableFrom(endElementClasses.getClass()) ? endElementClasses : ImmutableSet.copyOf(endElementClasses);
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
    public final Set<Class<? extends ModelElement>> getEndClasses() {
        return endElementClasses;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine Startklasse des übergebenen Metapfades ist.
     *
     * @param metaPath
     *            MetaPfad dessen Startklassen geprüft werden sollen
     * @param elementClass
     *            Elementklasse, die als Startklasse geprüft werden soll
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebene Elementklasse eine Unterklasse
     *            einer Startklasse ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebene Elementklasse eine Oberklasse
     *            einer Startklasse ist
     * @return
     */
    public static final boolean isStartClass(final AbstractMetaPath metaPath, final Class<? extends ModelElement> elementClass, final boolean asSubClass, final boolean asSuperClass) {
        for (Class<? extends ModelElement> startClass : metaPath.getStartClasses()) {
            if (elementClass == startClass) {
                return true;
            }
            if (asSubClass && startClass.isAssignableFrom(elementClass)) {
                return true;
            }
            if (asSuperClass && elementClass.isAssignableFrom(startClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine Startklasse dieses Metapfades ist.
     *
     * @param elementClass
     *            Elementklasse, die als Startklasse geprüft werden soll
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebene Elementklasse eine Unterklasse
     *            einer Startklasse ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebene Elementklasse eine Oberklasse
     *            einer Startklasse ist
     * @return
     */
    public final boolean isStartClass(final Class<? extends ModelElement> elementClass, final boolean asSubClass, final boolean asSuperClass) {
        return isStartClass(this, elementClass, asSubClass, asSuperClass);
    }

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine Startklasse des übergebenen Metapfades ist.
     *
     * @param metaPath
     *            MetaPfad dessen Startklassen geprüft werden sollen
     * @param elementClasses
     *            Elementklassen, die als Startklasse geprüft werden sollen
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn eine übergebene Elementklasse eine Unterklasse
     *            einer Startklasse ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn eine übergebene Elementklasse eine Oberklasse
     *            einer Startklasse ist
     * @return
     */
    public static final boolean isStartClass(final AbstractMetaPath metaPath, final Collection<Class<? extends ModelElement>> elementClasses, final boolean asSubClass, final boolean asSuperClass) {
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (isStartClass(metaPath, elementClass, asSubClass, asSuperClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine Startklasse dieses Metapfades ist.
     *
     * @param elementClasses
     *            Elementklassen, die als Startklasse geprüft werden soll
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn eine übergebene Elementklasse eine Unterklasse
     *            einer Startklasse ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn eine übergebene Elementklasse eine Oberklasse
     *            einer Startklasse ist
     * @return
     */
    public final boolean isStartClass(final Collection<Class<? extends ModelElement>> elementClasses, final boolean asSubClass, final boolean asSuperClass) {
        return isStartClass(this, elementClasses, asSubClass, asSuperClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau die Endklasse des übergebenen MetaPfades ist.
     *
     * @param metaPath
     *            MetaPfad dessen Endklassen geprüft werden sollen
     * @param elementClass
     *            Elementklasse, die als Endklasse geprüft werden soll
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebene Elementklasse eine Unterklasse
     *            einer Endklasse ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebene Elementklasse eine Oberklasse
     *            einer Endklasse ist
     * @return
     */
    public static final boolean isEndClass(final AbstractMetaPath metaPath, final Class<? extends ModelElement> elementClass, final boolean asSubClass, final boolean asSuperClass) {
        for (Class<? extends ModelElement> endClass : metaPath.getEndClasses()) {
            if (elementClass == endClass) {
                return true;
            }
            if (asSubClass && endClass.isAssignableFrom(elementClass)) {
                return true;
            }
            if (asSuperClass && elementClass.isAssignableFrom(endClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine Endklasse dieses MetaPfades ist.
     *
     * @param elementClass
     *            Elementklasse, die als Endklasse geprüft werden soll
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebene Elementklasse eine Unterklasse
     *            einer Endklasse ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebene Elementklasse eine Oberklasse
     *            einer Endklasse ist
     * @return
     */
    public final boolean isEndClass(final Class<? extends ModelElement> elementClass, final boolean asSubClass, final boolean asSuperClass) {
        return isEndClass(this, elementClass, asSubClass, asSuperClass);
    }

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine Endklasse des übergebenen MetaPfades ist.
     *
     * @param metaPath
     *            MetaPfad dessen Endklassen geprüft werden sollen
     * @param elementClasses
     *            Elementklasseen, die als Endklasse geprüft werden sollen
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn eine übergebene Elementklasse eine Unterklasse
     *            einer Endklasse ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn eine übergebene Elementklasse eine Oberklasse
     *            einer Endklasse ist
     * @return
     */
    public static final boolean isEndClass(final AbstractMetaPath metaPath, final Collection<Class<? extends ModelElement>> elementClasses, final boolean asSubClass, final boolean asSuperClass) {
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (isEndClass(metaPath, elementClass, asSubClass, asSuperClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn eine übergebene Elementklasse genau eine Endklasse dieses MetaPfades ist.
     *
     * @param elementClasses
     *            Elementklasseen, die als Endklasse geprüft werden sollen
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn eine übergebene Elementklasse eine Unterklasse
     *            einer Endklasse ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn eine übergebene Elementklasse eine Oberklasse
     *            einer Endklasse ist
     * @return
     */
    public final boolean isEndClass(final Collection<Class<? extends ModelElement>> elementClasses, final boolean asSubClass, final boolean asSuperClass) {
        return isEndClass(this, elementClasses, asSubClass, asSuperClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau die Endklasse des übergebenen MetaPfades ist.
     *
     * @param metaPath
     *            MetaPfad deren Startklasse geprüft werden soll
     * @param startClass
     *            Elementklasse, die als Startklasse der Kantenklasse geprüft werden soll
     * @param startClass
     *            Elementklasse, die als Endklasse der Kantenklasse geprüft werden soll
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebenen Elementklassen eine Unterklasse der
     *            Start- bzw. Endklasse der Kante ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebenen Elementklassen eine Oberklasse der
     *            Start- bzw. Endklasse der Kante ist
     * @return
     */
    public static final boolean isStartAndEndClass(final AbstractMetaPath metaPath, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final boolean asSubClass, final boolean asSuperClass) {
        return isStartClass(metaPath, startClass, asSubClass, asSuperClass) && isEndClass(metaPath, endClass, asSubClass, asSuperClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau die Endklasse dieses MetaPfades ist.
     *
     * @param startClass
     *            Elementklasse, die als Startklasse der Kantenklasse geprüft werden soll
     * @param startClass
     *            Elementklasse, die als Endklasse der Kantenklasse geprüft werden soll
     * @param asSubClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebenen Elementklassen eine Unterklasse der
     *            Start- bzw. Endklasse der Kante ist
     * @param asSuperClass
     *            wenn <code>true</code> dann komnmt auch <code>true</code> zurück, wenn die übergebenen Elementklassen eine Oberklasse der
     *            Start- bzw. Endklasse der Kante ist
     * @return
     */
    public final boolean isStartAndEndClass(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final boolean asSubClass, final boolean asSuperClass) {
        return isStartAndEndClass(this, startClass, endClass, asSubClass, asSuperClass);
    }

    /**
     * Liefert <code>true</code>, wenn der Pfad keine Fehler enthält.
     *
     * @return
     */
    public abstract boolean isValid();

    /**
     * Liefert <code>true</code>, wenn das übergebene Objekt dieselben Eigenschaften hat, wie this.
     *
     * @param obj
     * @param ignoreName
     *            Wenn <code>true</code> wird die Gleichheit des Namens nicht mitgeprüft
     * @return
     */
    public boolean equals(final Object obj, final boolean ignoreName) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractMetaPath other = (AbstractMetaPath) obj;
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

    protected abstract String createName();

    protected final String createFullName() {
        return ElementsNameBuilder.getDisplayableClassesNames(getStartClasses()) + " " + createName() + " " + ElementsNameBuilder.getDisplayableClassesNames(getEndClasses());
    }

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
            fullName = createFullName();
        }
        return fullName;
    }

    @Override
    public final String toString() {
        return getName();
    }

    /**
     * Liefert <code>true</code>, wenn der Pfad prinzipiell angelegt werden kann. Das ist der Fall, wenn es sich um eine
     * einfache Assoziationsfolge ohne parallele Pfade oder Verweigungen zu Assoziationsklassen dazwischen handelt und alle
     * Zwischenelementklassen nicht abstrakt sind.
     *
     * @return
     */
    public abstract boolean isCreateable();

    /**
     * Liefert <code>true</code>, wenn der Pfad zwischen Elementen der Start- und Zielklasse den prinzipiell angelegt werden kann.
     * Das ist der Fall, wenn es sich um eine einfache Assoziationsfolge ohne parallele Pfade oder Verweigungen zu Assoziationsklassen
     * dazwischen handelt und alle Zwischenelementklassen und Zwischenkantenklassen nicht abstrakt sind. Außerdem müssen die Start-
     * und Zielklasse jeweils die Start- bzw. Zielklasse des Pfades oder eine Unterklasse davon sein.
     *
     * @param startClass
     *            zu testenden Startklasse des Metapfades
     * @param endClass
     *            zu testenden Endklasse des Metapfades
     * @return
     *         /
     *         public final boolean isCreateable(Class<? extends ModelElement> startClass, Class<? extends ModelElement> endClass) {
     *         return isStartClassOrSubClass(startClass) && isEndClassOrSubClass(endClass) && isCreateable();
     *         }
     *         /**
     *         Liefert <code>true</code>, wenn der Pfad zwischen Elementen der Start- und Zielklasse den prinzipiell angelegt werden kann.
     *         Das ist der Fall, wenn es sich um eine einfache Assoziationsfolge ohne parallele Pfade oder Verweigungen zu Assoziationsklassen
     *         dazwischen handelt und alle Zwischenelementklassen und Zwischenkantenklassen nicht abstrakt sind. Außerdem müssen die Start-
     *         und Zielklasse jeweils die Start- bzw. Zielklasse des Pfades oder eine Unterklasse davon sein.
     * @param startClass
     *            zu testenden Startklasse des Metapfades
     * @param endClass
     *            zu testenden Endklasse des Metapfades
     * @return
     */
    public final boolean isCreateable(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        return isStartClass(this, startClass, true, false) && isEndClass(this, endClass, true, false) && isCreateable();
    }

    /**
     * Liefert den MetaPfad der die gegenricthung beschreibt oder <code>null</code>, wenn es einen solchen nith gibt.
     *
     * @return the otherDirectionPath
     */
    public AbstractMetaPath getOtherDirection() {
        return otherDirection;
    }

    /**
     * Liefert eine Folge von Elementarpfaden, wenn sich dieser Pfad so bilden lässt, ansonsten kommt <code>null</code> zurück. Alle parallelen Pfade
     * geben hier <code>null</code> zurück. {@link SequenceMetaPath} geben nur nicht <code>null</code> zurück, wenn sie im innersten ein einzelner
     * Pfad sind ohne parallele oder rekursive Pfade sind.
     *
     * @return
     */
    public abstract List<ElementaryMetaPath> getSimpleMetaPath();

    /**
     * Liefert <code>true</code>, wenn dieser Pfad Elementarten miteinander verbindet, die
     * zueinander zuweisungskompatibel sind. D.h. die Startklasse ist gleich der Endklasse
     * oder die Endklasse eine Unterklasse der Startklasse.
     *
     * @return
     */
    public final boolean isRecursive() {
        for (Class<? extends ModelElement> endClass : getEndClasses()) {
            if (isStartClass(this, endClass, true, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>false</code>, wenn der Pfad in beide Richtungen dasselbe bedeutet. Dafür muss
     * er dieselben Elementarten miteinander verbinden und denselben Namen in beiden Richtungen
     * tragen. Z.B können 2 physische DV-Bausteine über Datenübertragungsverbindungen miteinander
     * verbunden sein. Diese Verbindung heißt in jede der beiden Richtungen "ist verbunden mit" und
     * verbindet dieselbe Elementart miteinander. Der dazugehörige Elementarpfad ist also undirected.
     * Dasselbe ist aber auch für {@link SequenceMetaPath}s möglich, wenn z.B. die beiden physischen
     * DV-Bausteine Schnittstellen beitzen würden (was sie im aktuellen Metamodell nicht haben) und diese
     * dann über eine Datenübertragungsverbindung mit der beidseitigen Bedeutung "ist verbunden mit"
     * verbunden sind, dann bedeutet der Pfad auch in beide Richtungen dasselbe, nämlich
     * "Phys. DV-Baustein besitzt Schnittstelle ist verbunden mit Schnittstelle gehört zu Phys. DV-Baustein".
     * Die Umkehrrichtung dieses Pfades ist er selbst und somit ist er undirected.
     *
     * @return
     *         <code>true</code> wenn Vorwärts- und Rückwärtsrichtungen unterschiedliche Bedeutung haben
     */
    public abstract boolean isDirected();

}
