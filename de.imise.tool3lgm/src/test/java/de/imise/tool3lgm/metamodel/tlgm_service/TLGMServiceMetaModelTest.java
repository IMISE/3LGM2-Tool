package de.imise.tool3lgm.metamodel.tlgm_service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.testng.annotations.Test;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;

public class TLGMServiceMetaModelTest {

    public static void mains(final String[] args) throws Throwable {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        // locate file system by using the syntax
        // defined in java.net.JarURLConnection
        URI uri = URI.create("jar:file:/codeSamples/zipfs/zipfstest.zip");

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Path externalTxtFile = Paths.get("/codeSamples/zipfs/SomeTextFile.txt");
            Path pathInZipfile = zipfs.getPath("/SomeTextFile.txt");
            // copy a file into the zip file
            Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    public void test() {
        //        File pluginDir = new File(Tool3lgmConstants.APPLICATION_DIR, "Plugins");
        //        System.err.println(pluginDir);
        //        assertTrue(pluginDir.exists());
        //        FileNameExtensionFilter jarFileFilter = Tool3lgmConstants.getFileNameExtensionFilter(Tool3lgmConstants.FileFilterType.JAR);
        //        for (File f : pluginDir.listFiles()) {
        //            if (!jarFileFilter.accept(f)) {
        //                continue;
        //            }
        //            System.err.println(f);
        //            try {
        //                URL[] urls = {
        //                        new URL("jar:file:" + f.toString() + "!/")
        //                };
        //                URLClassLoader cl = URLClassLoader.newInstance(urls);
        //                JarFile jarFile = new JarFile(f);
        //                Enumeration<JarEntry> entries = jarFile.entries();
        //                while (entries.hasMoreElements()) {
        //                    JarEntry entry = entries.nextElement();
        //                    if (entry.isDirectory()) {
        //                        continue;
        //                    }
        //                    String entryName = entry.getName();
        //                    if (entryName.endsWith(".class")) {
        //                        //System.err.println(entryName);
        //                        String className = entryName.substring(0, entryName.length() - 6); // ".class" abschneiden
        //                        className = className.replace('/', '.');
        //                        Class c = cl.loadClass(className);
        //                        if (MetaModelDefinition.class.isAssignableFrom(c)) {
        //                            System.err.println(c);
        //                        }
        //                    }
        //                }
        //                jarFile.close();
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        }
    }

    public void getAllEdges() {
        Class<?> cls = Edge.class;
        Package pkg = cls.getPackage();
        List<Package> allPackages = new ArrayList<>();
        allPackages.add(pkg);
        for (int i = 0; i < allPackages.size(); i++) {
            printPackageClasses(pkg);
            Package[] packages = Package.getPackages();
            List<Package> packagesList = Arrays.asList(packages);
            allPackages.addAll(packagesList);
        }
    }

    private void printPackageClasses(final Package pkg) {
        List<Class<?>> classesForPackage = getClassesForPackage(pkg);
        for (Class<?> c : classesForPackage) {
            System.err.println(c);
        }
    }

    private static List<Class<?>> getClassesForPackage(final Package pkg) {
        String pkgname = pkg.getName();

        List<Class<?>> classes = new ArrayList<>();

        // Get a File object for the package
        File directory = null;
        String fullPath;
        String relPath = pkgname.replace('.', '/');

        //System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);

        URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);

        //System.out.println("ClassDiscovery: Resource = " + resource);
        if (resource == null) {
            throw new RuntimeException("No resource for " + relPath);
        }
        fullPath = resource.getFile();
        //System.out.println("ClassDiscovery: FullPath = " + resource);

        try {
            directory = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
        } catch (IllegalArgumentException e) {
            directory = null;
        }
        //System.out.println("ClassDiscovery: Directory = " + directory);

        if (directory != null && directory.exists()) {

            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {

                // we are only interested in .class files
                if (files[i].endsWith(".class")) {

                    // removes the .class extension
                    String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);

                    //System.out.println("ClassDiscovery: className = " + className);

                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("ClassNotFoundException loading " + className);
                    }
                }
            }
        } else {
            try {
                String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
                JarFile jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(relPath) && entryName.length() > relPath.length() + "/".length()) {

                        //System.out.println("ClassDiscovery: JarEntry: " + entryName);
                        String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");

                        //System.out.println("ClassDiscovery: className = " + className);
                        try {
                            classes.add(Class.forName(className));
                        } catch (ClassNotFoundException e) {
                            jarFile.close();
                            throw new RuntimeException("ClassNotFoundException loading " + className);
                        }
                    }
                }
                jarFile.close();
            } catch (IOException e) {
                throw new RuntimeException(pkgname + " (" + directory + ") does not appear to be a valid package", e);
            }
        }
        return classes;
    }
}
