package de.imise.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Funktionen zum Laden von Klassen aus jar-Dateien, wie es beim Laden von Plugins vorkommt.
 *
 * @author AXS (8 May 2019)
 */
public class PluginUtils {

    /**
     * Lädt alle Klassen der übergebenen Art aus dem übergebenen Verzeichis oder Jar-File. Ist es ein Verzeichnis, werden alle darin enthaltenen
     * Jar-Files durchsucht, sonst nur das eine übergebene Jar-File. Ist es weder Verzeichnis noch Jar-File, kommt eine leere Liste zurück.
     *
     * @param file Verzeichnis mit Jar-Files oder einzelnes Jar-File
     * @param superClassOfResultClasses
     *            (Ober-)Klasse aller Ergebnisklassen
     * @return
     */
    public static <T> List<Class<? extends T>> loadClasses(final File file, final Class<? extends T> superClassOfResultClasses) {
        List<Class<? extends T>> resultClasses = new ArrayList<>();
        loadClasses(resultClasses, file, superClassOfResultClasses);
        return resultClasses;
    }

    /**
     * @param resultClasses
     * @param directoryOrJarFile
     * @param superClassOfResultClasses
     * @return
     */
    private static <T> List<Class<? extends T>> loadClasses(List<Class<? extends T>> resultClasses, final File directoryOrJarFile, final Class<? extends T> superClassOfResultClasses) {
        if (resultClasses == null) {
            resultClasses = new ArrayList<>();
        }
        File[] files = directoryOrJarFile.isDirectory() ? directoryOrJarFile.listFiles() : new File[] {
                directoryOrJarFile
        };
        for (File file : files) {
            loadClassesFromJar(resultClasses, file, superClassOfResultClasses);
        }
        return resultClasses;
    }

    /**
     * Wenn das übergebene File ein Jar-File ist, dann werden daraus alle Klassen herausgesucht, die zuweisungskompatibel zur übergebenen Klasse sind.
     * Ist das File kein Jar-File oder keine solche Klasse im Jar-File, kommt eine leere Liste zurück.
     *
     * @param resultClasses
     *            Liste, zu der die übergebenen Klassen hinzugefügt werden
     * @param jarFileFile
     *            Pfad zum Jar-File
     * @param superClassOfResultClasses
     *            (Ober-)Klasse, der zu findenden Klassen
     * @return Liste aller Klassen, die im Jar-File gefunden wurden und zuweisungkompatibel zur übergebenen Klasse sind
     */
    private static <T> List<Class<? extends T>> loadClassesFromJar(List<Class<? extends T>> resultClasses, final File jarFileFile, final Class<? extends T> superClassOfResultClasses) {
        if (resultClasses == null) {
            resultClasses = new ArrayList<>();
        }
        if (jarFileFile.getName().endsWith(".jar")) {
            try {
                URL[] urls = {
                        new URL("jar:file:" + jarFileFile.toString() + "!/")
                };
                URLClassLoader cl = URLClassLoader.newInstance(urls);
                JarFile jarFile = new JarFile(jarFileFile);
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
                        if (superClassOfResultClasses.isAssignableFrom(c)) {
                            Class<? extends T> resultClass = c.asSubclass(superClassOfResultClasses);
                            resultClasses.add(resultClass);
                        }
                    }
                }
                jarFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultClasses;
    }

    /**
     * Lädt aus dem übergebenen Verzeichnis alle Klassen der übergebenen Art, ruft dann von jeder Klasse den parameterlosen Konstruktor auf und gibt
     * alle Instanzen zurück, bei denen der Aufruf des Konstruktors ohne Fehler geklappt hat.
     *
     * @param file
     * @param superClassOfResultClasses
     * @param findOnlyOne
     *            wenn <code>true</code> ist in der Rückgabeliste nur 1 Element und zwar das zuerst gefundene. Damit kann man die weitere Suche
     *            abbrechen, wenn klar ist, dass max. 1 Element zurück kommen kann.
     * @return Instanzen der übergebenen Klasse aus dem übergebenen Verzeichnis
     */
    private static <T> List<T> loadInstances(final File file, final Class<T> superClassOfResultClasses, final boolean findOnlyOne) {
        List<Class<? extends T>> classes = PluginUtils.loadClasses(file, superClassOfResultClasses);
        List<T> instances = new ArrayList<>();
        for (Class<? extends T> clazz : classes) {
            T instance = null;
            try {
                instance = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (instance != null) {
                instances.add(instance);
                if (findOnlyOne) {
                    break;
                }
            }
        }
        return instances;
    }

    /**
     * Lädt aus dem übergebenen Verzeichnis alle Klassen der übergebenen Art, ruft
     * dann von jeder Klasse den parameterlosen Konstruktor auf und gibt alle
     * Instanzen zurück, bei denen der Aufruf des Konstruktors ohne Fehler geklappt
     * hat.
     *
     * @param file
     * @param superClassOfResultClasses
     * @return Instanzen der übergebenen Klasse aus dem übergebenen Verzeichnis
     */
    public static <T> List<T> loadInstances(final File file, final Class<T> superClassOfResultClasses) {
        return loadInstances(file, superClassOfResultClasses, false);
    }

    /**
     * Lädt aus dem übergebenen Verzeichnis die alle Klassen der übergebenen Art,
     * ruft dann von der jeweils nächsten Klasse den parameterlosen Konstruktor auf
     * und gibt die erste Instanz zurück, bei denen der Aufruf des Konstruktors
     * ohne Fehler geklappt hat.
     *
     * @param file
     * @param superClassOfResultClasses
     * @return Instanz der übergebenen Klasse aus dem übergebenen Verzeichnis oder
     *         <code>null</code>, wenn keine Instanz gefunden wurde
     */
    public static <T> T loadInstance(final File file, final Class<T> superClassOfResultClasses) {
        List<T> instances = loadInstances(file, superClassOfResultClasses, true);
        return instances.size() > 0 ? instances.get(0) : null;
    }

}
