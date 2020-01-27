package de.imise.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

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
    @SuppressWarnings("unchecked")
    public static final <T> Class<? extends T> getCommonSuperClass(final Class<? extends T> class1, final Class<? extends T> class2) {
        if (class1 == null) {
            return class2;
        }
        if (class2 == null) {
            return class1;
        }
        Class<? extends T> classX = class1;
        while (!classX.isAssignableFrom(class2) && classX != Object.class) {
            classX = (Class<? extends T>) classX.getSuperclass();
        }
        return classX;
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
        if (objectListOrClassList == null || objectListOrClassList.isEmpty()) {
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
     * @return
     */
    public static final Class<?> getCommonSuperClass(final Collection<?> objectListOrClassList) {
        if (objectListOrClassList == null || objectListOrClassList.isEmpty()) {
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
    public static final Class<?> getMostSpecialClass(final Object o1, final Object o2) {
        Class<?> class1 = o1 == null ? null : o1 instanceof Class<?> ? (Class<?>) o1 : o1.getClass();
        Class<?> class2 = o2 == null ? null : o2 instanceof Class<?> ? (Class<?>) o2 : o2.getClass();
        return getMostSpecialClass(class1, class2);
    }

    /**
     * Liefert die speziellere von den beiden übergebenen Klassen oder <code>null</code>, wenn sie nicht in der Vererbungshierarchie zusammen hängen.
     *
     * @param class1
     * @param class2
     * @return
     */
    public static final <T> Class<? extends T> getMostSpecialClass(final Class<? extends T> class1, final Class<? extends T> class2) {
        if (class1 != null && class2 != null) {
        if (class1.isAssignableFrom(class2)) {
            return class2;
        }
        if (class2.isAssignableFrom(class1)) {
            return class1;
        }
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
    public static final <T> void removeSubClasses(final List<Class<? extends T>> classList) {
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
     * Entfernt aus der übergebenen Liste alle Klassen, die nicht zuwesiungskompatibel zur übergebenen Klasse sind.
     *
     * @param classList
     * @param classOrSuperClass
     * @return
     */
    public static final <T> void retainSubClasses(final List<Class<? extends T>> classList, final Class<?> classOrSuperClass) {
        for (int i = classList.size() - 1; i >= 0; i--) {
            Class<?> clazz = classList.get(i);
            if (!classOrSuperClass.isAssignableFrom(clazz)) {
                classList.remove(i);
            }
        }
    }

    /**
     * Liefert für die übergebenen Klassen diese Klassen selbst und bis hin zur übergebenen excludeSuperClass
     * auch alle Superklassen. Die excludeSuperClass selbst ist nicht mehr mit dabei. Wird als excludeSuperClass
     * null übergeben, dann geht die Hierarchie hoch bis zu Object.class.
     * ACHTUNG: Das hier funktionier nicht für Interfaces sondern nur für direkte Sub- und Superklassen.
     *
     * @param class
     * @param excludeSuperClass
     * @see ReflectionUtils#getClassWithSuperClasses(Class, Class)
     */
    public static <T> Set<Class<?>> getClassesWithSuperClasses(final Collection<Class<? extends T>> classes, final Class<?> excludeSuperClass) {
        if (classes.size() == 1) {
            return getClassWithSuperClasses(classes.iterator().next(), excludeSuperClass);
        }
        Set<Class<?>> classedWithSuperClasses = new HashSet<>();
        for (Class<?> clazz : classes) {
            classedWithSuperClasses.addAll(getClassWithSuperClasses(clazz, excludeSuperClass));
        }
        return classedWithSuperClasses;
    }

    /**
     * Liefert für die übergebenen Klassen diese Klassen selbst und bis hin zur übergebenen excludeSuperClass
     * auch alle Superklassen. Die excludeSuperClass selbst ist nicht mehr mit dabei. Wird als excludeSuperClass
     * null übergeben, dann geht die Hierarchie hoch bis zu Object.class.
     * ACHTUNG: Das hier funktionier nicht für Interfaces sondern nur für direkte Sub- und Superklassen.
     *
     * @param class
     * @param excludeSuperClass
     * @see ReflectionUtils#getClassWithSuperClasses(Class, Class)
     */
    public static Set<Class<?>> getClassesWithSuperClasses(final Class<?>[] classes, final Class<?> excludeSuperClass) {
        return getClassesWithSuperClasses(Arrays.asList(classes), excludeSuperClass);
    }

    /**
     * Liefert für die übergebene Klasse diese Klasse selbst und bis hin zur übergebenen excludeSuperClass
     * auch alle Superklassen. Die excludeSuperClass selbst ist nicht mehr mit dabei. Wird als excludeSuperClass
     * null übergeben, dann geht die Hierarchie hoch bis zu Object.class.
     * ACHTUNG: Das hier funktionier nicht für Interfaces sondern nur für direkte Sub- und Superklassen.
     *
     * @param class
     * @param excludeSuperClass
     */
    public static Set<Class<?>> getClassWithSuperClasses(final Class<?> clazz, final Class<?> excludeSuperClass) {
        Set<Class<?>> classWithSuperClasses = new HashSet<>();
        Class<?> superClass = clazz;
        while (superClass != null && superClass != excludeSuperClass) {
            classWithSuperClasses.add(superClass);
            superClass = superClass.getSuperclass();
        }
        return classWithSuperClasses;
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
     * Liefert true, wenn die Klassen nicht <code>null</code> und gleich sind oder eine die Oberklasse der anderen ist.
     *
     * @param class1
     * @param class2
     * @return
     */
    public static final boolean isAssignable(final Class<?> class1, final Class<?> class2) {
        return class1 != null && class2 != null && class1.isAssignableFrom(class2) || class2.isAssignableFrom(class1);
    }

    /**
     * Liefert true, wenn die übergebene Klasse gleich sind oder eine die Oberklasse einer in der Collection enthaltenen Klassen ist.
     *
     * @param class1
     * @param classes
     * @return
     */
    public static final <T> boolean isAssignable(final Class<?> class1, final Collection<Class<? extends T>> classes) {
        for (Class<?> class2 : classes) {
            if (isAssignable(class1, class2)) {
                return true;
            }
        }
        return false;
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
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <T> T getField(final Class<?> clazz, final Class<?> maxSuperClass, final String fieldName, final Class<T> type) {
        Class<?> elementClass = clazz;
        try {
            boolean breakWhile = false;
            while (elementClass != null) {
                for (Field fld : elementClass.getDeclaredFields()) {
                    String fldName = fld.getName();
                    if (fldName.equals(fieldName)) {
                        Class<?> fldType = fld.getType();
                        if (type == null || fldType.equals(type)) {
                            return (T) fld.get(fld);
                        }
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

    /**
     * Sucht ausgehend von der übergebenen Klasse über alle direkten Oberklassen (keine Interfaces)
     * bis maximal zur Oberklasse maxSuperClass ein Feld mit dem übergebenen Namen und gibt dessen Wert zurück.
     * Die Klasse maxSuperClass wird selbst nicht mehr durchsucht. Soll bis einschließlich Object.class durchsucht werden,
     * muss als maxSuperClass null angegeben werden.
     *
     * @param clazz
     * @param fieldName
     * @param type
     * @return
     */
    public static final <T> T getField(final Class<?> clazz, final String fieldName, final Class<T> type) {
        return getField(clazz, clazz.getSuperclass(), fieldName, type);
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
     * @param type
     * @return
     */
    public static final Object getField(final Class<?> clazz, final Class<?> maxSuperClass, final String fieldName) {
        return getField(clazz, maxSuperClass, fieldName, null);
    }

    /**
     * Liefert ein Set aller übergebenen Klassen zurück, die eine Methode mit dem übergebenen Namen haben.
     * Oberklassen werden nicht berücksichtigt.
     *
     * @param name
     * @param classes
     * @return
     */
    @SafeVarargs
    public static <T> Set<Class<? extends T>> hasMethod(final String name, final Class<? extends T>... classes) {
        return hasMethod(name, Arrays.asList(classes));
    }

    /**
     * Liefert ein Set aller übergebenen Klassen zurück, die eine Methode mit dem übergebenen Namen haben.
     * Oberklassen werden nicht berücksichtigt.
     *
     * @param name
     * @param classes
     * @return
     */
    public static <T> Set<Class<? extends T>> hasMethod(final String name, final Collection<Class<? extends T>> classes) {
        ImmutableSet.Builder<Class<? extends T>> returnClasses = new ImmutableSet.Builder<>();
        for (Class<? extends T> clazz : classes) {
            try {
                clazz.getDeclaredMethod(name);
                returnClasses.add(clazz);
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }
        return returnClasses.build();
    }

    /**
     * Liefert auch im statischen Kontext das, was getClass() im Objekt-Kontext liefert.
     */
    public static class CurrentClassGetter extends SecurityManager {

        public String getClassName() {
            return getClassContext()[1].getName();
        }

        public Class<?> getCurrentClass() {
            return getClassContext()[1];
        }
    }

}
