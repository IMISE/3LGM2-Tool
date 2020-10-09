package de.imise.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @SuppressWarnings("unchecked")
    public static final <T> Class<? extends T> getCommonSuperClass(final T o1, final T o2) {
        Class<? extends T> o1Class = o1 == null ? null : (Class<? extends T>) o1.getClass();
        Class<? extends T> o2Class = o2 == null ? null : (Class<? extends T>) o2.getClass();
        if (o1 == null) {
            return o2Class;
        }
        if (o2 == null) {
            return o1Class;
        }
        while (!o1Class.isAssignableFrom(o2Class)) {
            o1Class = (Class<? extends T>) o1Class.getSuperclass();
        }
        return o1Class;
    }

    /**
     * @param <T>
     * @param class1
     * @param class2
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <T> Class<? extends T> getCommonSuperClassOfClasses(final Class<? extends T> class1, final Class<? extends T> class2) {
        if (class1 == null) {
            return class2;
        }
        if (class2 == null) {
            return class1;
        }
        Class<? extends T> classX = class1;
        while (!classX.isAssignableFrom(class2)) { // at least Object.class will be assignable
            classX = (Class<? extends T>) classX.getSuperclass();
        }
        return classX;
    }

    /**
     * Liefert für die übergebenen Objekte die speziellste gemeinsame Klasse.<br>
     * Ist die Liste <code>null</code> oder leer , kommt hier <code>null</code> zurück.
     * Ansonsten wird mindestens <code>Object.class</code> zurückgeliefert.
     *
     * @param objectList
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <T> Class<? extends T> getCommonSuperClass(final Collection<? extends T> objects) {
        if (objects == null || objects.isEmpty()) {
            return null;
        }
        Class<? extends T> returnClass = null;
        for (T o : objects) {
            Class<? extends T> oClass = o == null ? null : (Class<? extends T>) o.getClass();
            if (returnClass == null) {
                returnClass = oClass;
            } else {
                returnClass = getCommonSuperClassOfClasses(returnClass, oClass);
            }
        }
        return returnClass;
    }

    /**
     * Liefert für die übergebenen Klassen die speziellste gemeinsame Klasse.<br>
     * Ist die Liste <code>null</code> oder leer , kommt hier <code>null</code> zurück.
     * Ansonsten wird mindestens <code>Object.class</code> zurückgeliefert.
     *
     * @param objectList
     * @return
     */
    public static final <T> Class<? extends T> getCommonSuperClassOfClasses(final Collection<Class<? extends T>> classes) {
        if (classes == null || classes.isEmpty()) {
            return null;
        }
        Class<? extends T> returnClass = null;
        for (Class<? extends T> c : classes) {
            if (returnClass == null) {
                returnClass = c;
            } else {
                returnClass = getCommonSuperClassOfClasses(returnClass, c);
            }
        }
        return returnClass;
    }

    /**
     * Liefert die speziellere Klasse der beiden Objekte, wenn das eine Objekt eine Unterklasse des anderen ist. Wenn sie vererbungstechnisch
     * nicht zusammen hängen, dann kommt <code>null</code> zurück.
     *
     * @param o1
     * @param o2
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <T> Class<? extends T> getMostSpecialClass(final T o1, final T o2) {
        Class<? extends T> class1 = o1 == null ? null : (Class<? extends T>) o1.getClass();
        Class<? extends T> class2 = o2 == null ? null : (Class<? extends T>) o2.getClass();
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
        if (class1 == class2) {
            return class1;
        }
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
     * Removes all classes from the list if a super class is contained in the list too.
     *
     * @param classList
     * @throws NullPointerException if an element in the list is <code>null</code>
     */
    public static final <T> void removeSubClasses(final Collection<Class<? extends T>> classes) {
        List<Class<? extends T>> classList = classes instanceof List ? (List<Class<? extends T>>) classes : new ArrayList<>(classes);
        for (int i = 0; i < classList.size(); i++) {
            Class<?> c1 = classList.get(i);
            if (!c1.equals(null)) { // throws an Nullpointer also if there is only one null value in classes
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
        if (classes != classList) {
            classes.retainAll(classList);
        }
    }

    /**
     * Removes all classes from the list if a subclass is contained in the list too.
     *
     * @param classList
     * @throws NullPointerException if an element in the list is <code>null</code>
     */
    public static final <T> void removeSuperClasses(final Collection<Class<? extends T>> classes) {
        List<Class<? extends T>> classList = classes instanceof List ? (List<Class<? extends T>>) classes : new ArrayList<>(classes);
        for (int i = 0; i < classList.size(); i++) {
            Class<?> c1 = classList.get(i);
            if (!c1.equals(null)) { // throws an Nullpointer also if there is only one null value in classes
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
        if (classes != classList) {
            classes.retainAll(classList);
        }
    }

    /**
     * Entfernt aus der übergebenen Liste alle Klassen, die nicht zuweisungskompatibel zur übergebenen Klasse sind.
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
     * Liefert true, wenn die Klassen nicht <code>null</code> und gleich sind oder eine die Oberklasse der anderen ist.
     *
     * @param class1
     * @param class2
     * @return
     */
    public static final boolean isAssignable(final Class<?> class1, final Class<?> class2) {
        if (class1 == null) {
            return false;
        }
        if (class2 == null) {
            return false;
        }
        if (class1.isAssignableFrom(class2)) {
            return true;
        }
        if (class2.isAssignableFrom(class1)) {
            return true;
        }
        return false;
    }

    /**
     * Liefert den absoluten Pfad zum Verzeichnis, in dem sich die übergebene Klasse im
     * Dateisystem befindet.
     *
     * @return
     */
    public static final String getAbsoluteDirectory(final Class<?> clazz) {
        File classFile = getClassFile(clazz);
        File parentFile = classFile.getParentFile();
        parentFile = parentFile.getParentFile();
        String path = parentFile.getPath();
        String absoluteDirectory = path + File.separator;
        return absoluteDirectory;
    }

    /**
     * Liefert die Datei der übergebenen Klasse.
     *
     * @param clazz
     * @return
     */
    public static final File getClassFile(final Class<?> clazz) {
        String className = clazz.getSimpleName();
        className += ".class";
        URL absolutePath = clazz.getResource(className);
        String path = absolutePath.getPath();
        // die "%20" der URL müssen wieder raus, damit die Dateien gefunden werden
        path = path.replace("%20", " ");
        File file = new File(path);
        return file;
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
     * @param maxSuperClass
     * @param fieldName
     * @param type
     * @return
     */
    public static final <T> T getField(final Class<?> clazz, final Class<?> maxSuperClass, final String fieldName) {
        return getField(clazz, maxSuperClass, fieldName, null);
    }

    /**
     * Sucht in der übergebenen Klasse ein Feld mit dem übergebenen Namen und gibt dessen Wert zurück.
     *
     * @param clazz
     * @param fieldName
     * @param type Typ des Feldes oder <code>null</code>, wenn der Typ automatisch bestimmt werden soll.
     * @return <code>null</code>
     */
    public static final <T> T getField(final Class<?> clazz, final String fieldName, final Class<T> type) {
        Class<?> superclass = clazz.getSuperclass();
        return getField(clazz, superclass, fieldName, type);
    }

    /**
     * Sucht in der übergebenen Klasse ein Feld mit dem übergebenen Namen und gibt dessen Wert zurück.
     *
     * @param clazz
     * @param fieldName
     * @param type Typ des Feldes oder <code>null</code>, wenn der Typ automatisch bestimmt werden soll.
     * @return <code>null</code>
     */
    public static final <T> T getField(final Class<?> clazz, final String fieldName) {
        return getField(clazz, fieldName, null);
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

    /**
     * @param <T>
     * @param clazz
     */
    public static final <T> T newInstance(final Class<? extends T> clazz) {
        try {
            Constructor<? extends T> emptyConstructor = clazz.getDeclaredConstructor();
            return emptyConstructor.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
