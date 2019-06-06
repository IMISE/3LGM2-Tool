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
        ArrayList<Class<? extends T>> resultClasses = new ArrayList<>();
        loadClasses(resultClasses, file, superClassOfResultClasses);
        return resultClasses;
    }

    /**
     * @param resultClasses
     * @param directoryOrJarFile
     * @param superClassOfResultClasses
     * @return
     */
    private static <T> List<Class<? extends T>> loadClasses(ArrayList<Class<? extends T>> resultClasses, final File directoryOrJarFile, final Class<? extends T> superClassOfResultClasses) {
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
    private static <T> List<Class<? extends T>> loadClassesFromJar(ArrayList<Class<? extends T>> resultClasses, final File jarFileFile, final Class<? extends T> superClassOfResultClasses) {
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

}
