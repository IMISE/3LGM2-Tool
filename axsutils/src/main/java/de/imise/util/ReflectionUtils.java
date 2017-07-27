package de.imise.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ReflectionUtils {

    /**
     * Liefert die gemeinsame Oberklasse beider Objekte. Wenn die Objekte selbst Klassen sind,
     * dann werden ihre Oberklassen gesucht. Der Rückgabewert ist wenigstens {@link Object}, wenn beide
     * übergebenen Objekte keine speziellere gemeinsame Oberklasse besitzen.
     * <code>null</code> kommt nur zurück, wenn beide übergebenen Objekte <code>null</code> waren.
     *
     * @param o1
     * @param o2
     * @return
     */
    public static final Class<?> getCommonSuperClass(final Object o1, final Object o2) {
        if (o1 == null) {
            return o2 == null ? null : o2 instanceof Class<?> ? (Class<?>) o2 : o2.getClass();
        }
        if (o2 == null) {
            return o1 instanceof Class<?> ? (Class<?>) o1 : o1.getClass();
        }
        Class<?> c1 = o1 instanceof Class<?> ? (Class<?>) o1 : o1.getClass();
        Class<?> c2 = o2 instanceof Class<?> ? (Class<?>) o2 : o2.getClass();
        while (!c1.isAssignableFrom(c2) && c1 != Object.class) {
            c1 = c1.getSuperclass();
        }
        return c1;
    }

    /**
     * @param <T>
     * @param class1
     * @param class2
     * @return
     */
    public static final <T> Class<? extends T> getCommonSuperClass(Class<? extends T> class1, final Class<? extends T> class2) {
        if (class1 == null) {
            return class2;
        }
        if (class2 == null) {
            return class1;
        }
        while (!class1.isAssignableFrom(class2) && class1 != Object.class) {
            class1 = (Class<? extends T>) class1.getSuperclass();
        }
        return class1;
    }

    /**
     * Liefert für die übergebene <code>ArrayList</code> die speziellste gemeinsame Klasse aller
     * enthaltenen Elemente ab einschließlich dem Index startIndex bis einschließlich zum Index endIndex.<br>
     * Handelt es sich bei einem übergebenen Object selbst um eine Klasse, so wird von ihr und allen
     * anderen Elementen die speziellste gemeinsame Klasse gesucht.<br>
     * Ist die Liste <code>null</code> oder leer oder die Indices ungültig, kommt hier <code>null</code> zurück.
     * Ansonsten wird mindestens <code>Object.class</code> zurückgeliefert.
     *
     * @param objectList
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static final Class<?> getCommonSuperClass(final List<?> objectListOrClassList, final int startIndex, final int endIndex) {
        if (objectListOrClassList == null || objectListOrClassList.size() == 0) {
            return null;
        }
        if (startIndex < 0 || startIndex >= objectListOrClassList.size() || endIndex < 0 || endIndex >= objectListOrClassList.size() || endIndex < startIndex) {
            return null;
        }
        return getCommonSuperClass(objectListOrClassList.subList(startIndex, endIndex));
    }

    /**
     * Liefert für die übergebenen Objekte die speziellste gemeinsame Klasse.<br>
     * Handelt es sich bei einem übergebenen Object in der Collection selbst um eine Klasse, so wird von ihr und allen
     * anderen Elementen die speziellste gemeinsame Klasse gesucht.<br>
     * Ist die Liste <code>null</code> oder leer , kommt hier <code>null</code> zurück.
     * Ansonsten wird mindestens <code>Object.class</code> zurückgeliefert.
     *
     * @param objectList
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static final Class<?> getCommonSuperClass(final Collection<?> objectListOrClassList) {
        if (objectListOrClassList == null || objectListOrClassList.size() == 0) {
            return null;
        }
        Class<?> returnClass = null;
        for (Object o : objectListOrClassList) {
            if (returnClass == null) {
                returnClass = o instanceof Class<?> ? (Class<?>) o : o.getClass();
            } else {
                returnClass = getCommonSuperClass(returnClass, o);
            }
            if (returnClass == Object.class) {
                return Object.class;
            }
        }
        return returnClass;
    }

    /**
     * Liefert für das übergebene <code>Object</code>-Array die speziellste gemeinsame Klasse aller
     * enthaltenen Elemente.<br>
     * Ist die Liste <code>null</code> oder leer, kommt hier <code>null</code> zurück. Ansonsten
     * wird mindestens <code>Object.class</code> zurückgeliefert.
     *
     * @param objectArray
     * @return
     */
    public static final Class<?> getCommonSuperClass(final Object[] objectArray) {
        if (objectArray == null || objectArray.length == 0) {
            return null;
        }
        Class<?> returnClass = objectArray[0] instanceof Class<?> ? (Class<?>) objectArray[0] : objectArray[0].getClass();
        for (int i = 1; i < objectArray.length; i++) {
            returnClass = getCommonSuperClass(returnClass, objectArray[i]);
            if (returnClass == Object.class) {
                return Object.class;
            }
        }
        return returnClass;
    }

    /**
     * Liefert die speziellere Klasse der beiden Objekte, wenn das eine Objekt eine Unterklasse des anderen ist. Wenn sie vererbungstechnisch
     * nicht zusammen hängen, dann kommt <code>null</code> zurück. Werden als Objekte Klasse übergeben, dann wird für diese Klassen dieser
     * Zusammenhang gerpüft.
     *
     * @param o1
     * @param o2
     * @return
     */
    public static final Class<?> getMostSpecialElementClass(final Object o1, final Object o2) {
        Class<?> class1 = o1 instanceof Class<?> ? (Class<?>) o1 : o1.getClass();
        Class<?> class2 = o2 instanceof Class<?> ? (Class<?>) o2 : o2.getClass();
        if (class1.isAssignableFrom(class2)) {
            return class2;
        }
        if (class2.isAssignableFrom(class1)) {
            return class1;
        }
        return null;
    }

    /**
     * Entfernt alle Klassen aus der übergebenen Klassenliste,
     * von denen eine Oberklasse in der Liste vorkommt.
     *
     * @param classList
     * @return
     */
    public static final void removeSubClasses(final List<Class<?>> classList) {
        for (int i = 0; i < classList.size(); i++) {
            Class<?> c1 = classList.get(i);
            for (int j = i + 1; j < classList.size(); j++) {
                Class<?> c2 = classList.get(j);
                if (c1.isAssignableFrom(c2)) {
                    classList.remove(j--);
                } else if (c2.isAssignableFrom(c1)) {
                    classList.remove(i--);
                    break;
                }
            }
        }
    }

    /**
     * Entfernt alle Klassen aus der übergebenen Klassenliste,
     * von denen eine Unterklasse in der Liste vorkommt.
     *
     * @param classList
     * @return
     */
    public static final void removeSuperClasses(final List<Class<?>> classList) {
        for (int i = 0; i < classList.size(); i++) {
            Class<?> c1 = classList.get(i);
            for (int j = i + 1; j < classList.size(); j++) {
                Class<?> c2 = classList.get(j);
                if (c2.isAssignableFrom(c1)) {
                    classList.remove(j--);
                } else if (c1.isAssignableFrom(c2)) {
                    classList.remove(i--);
                    break;
                }
            }
        }
    }

    /**
     * Gibt <code>true</code> zurück, wenn mind. ein Element in der übergebenen Liste enthalten ist
     * und alle Elemente in der Liste dieselbe Klasse besitzen, sonst false.
     *
     * @param objectList
     * @return
     */
    public static boolean hasSameClass(final Iterable<?> objectList) {
        Iterator<?> it = objectList.iterator();
        if (!it.hasNext()) {
            return false;
        }
        Class<?> firstClass = it.next().getClass();
        while (it.hasNext()) {
            if (!firstClass.equals(it.next().getClass())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gibt den ersten Index des Objektes zurück, das mit der übergebenen Klasse zuweisungskompatibel ist. Ist ein
     * enthaltenes Objekt selbst eine Klasse, dass wird deren Zuweisungskompatibilität überprüft.
     *
     * @param objectList
     * @param clazz
     * @return
     */
    public static final int getFirstAssignableIndex(final Object[] objectList, final Class<?> clazz) {
        for (int i = 0; i < objectList.length; i++) {
            if (objectList[i] instanceof Class<?>) {
                if (clazz.isAssignableFrom((Class<?>) objectList[i])) {
                    return i;
                }
            } else {
                if (clazz.isAssignableFrom(objectList[i].getClass())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Objektliste wenigstens ein zu der übergebenen Klasse
     * zuweisungskompatibles Objekt enthält. Sind Elemente in der Objektliste selbst Klassen, so wird deren
     * Zuweisungskompatibilität geprüft.
     *
     * @param objectList
     * @param clazz
     * @return
     * @see #getFirstAssignableIndex(Object[], Class)
     */
    public static final boolean containsAssignable(final Object[] objectList, final Class<?> clazz) {
        return getFirstAssignableIndex(objectList, clazz) >= 0;
    }

    /**
     * Gibt wieder, ob die übergebene Klasse <code>abstract</code> ist.
     *
     * @param clazz
     * @return
     */
    public static final boolean isAbstract(final Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * Liefert den absoluten Pfad zum Verzeichnis, in dem sich die übergebene Klasse im
     * Dateisystem befindet.
     *
     * @return
     */
    public static final String getAbsoluteDirectory(final Class<?> clazz) {
        String absoluteDirectory = getClassFile(clazz).getParentFile().getParentFile().getPath() + File.separator;
        // die "%20" der URL müssen wieder raus, damit die Dateien gefunden werden
        return absoluteDirectory.replace("%20", " ");
    }

    /**
     * Liefert die Datei der übergebenen Klasse.
     *
     * @param clazz
     * @return
     */
    public static final File getClassFile(final Class<?> clazz) {
        URL absolutePath = clazz.getResource(clazz.getSimpleName() + ".class");
        return new File(absolutePath.getPath());
    }

    /**
     * Sucht ausgehend von der übergebenen Klasse über alle direkten Oberklassen (keine Interfaces)
     * bis maximal zur Oberklasse maxSuperClass ein Feld mit dem übergebenen Namen und gibt dessen Wert zurück.
     * Die Klasse maxSuperClass wird selbst nicht mehr durchsucht. Soll bis einschließlich Object.class durchsucht werden,
     * muss als maxSuperClass null angegeben werden.
     *
     * @param clazz
     * @param maxSuperClass
     * @param fieldName
     * @return
     */
    public static final Object getField(final Class<?> clazz, final Class<?> maxSuperClass, final String fieldName) {
        Class<?> elementClass = clazz;
        try {
            boolean breakWhile = false;
            while (elementClass != null) {
                for (Field fld : elementClass.getDeclaredFields()) {
                    if (fld.getName().equals(fieldName)) {
                        return fld.get(fld);
                    }
                }
                if (breakWhile) {
                    break;
                }
                elementClass = elementClass.getSuperclass();
                if (elementClass == maxSuperClass) {
                    breakWhile = true;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
