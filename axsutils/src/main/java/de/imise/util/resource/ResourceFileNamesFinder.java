package de.imise.util.resource;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.imise.util.ReflectionUtils;

/**
 * Finds all files in a given folder of the resources. Works at runtime and in
 * devolopment time. Can handle localized resources.<br>
 * <br>
 * Class for loading resources located in language-dependent subdirectories in
 * the resources folder. This class basically just provides a function that
 * manages access to these resources at runtime from within the development
 * environment and from within the deploited jar file.
 *
 * @author AXS (14.06.2017)
 */
public class ResourceFileNamesFinder {

    /**
     * Returns a list of the relative paths of all files with the passed
     * extension in the resource directory.
     *
     * @param fileExtension the file extension of files to load
     * @param baseDirectory Folder from which the files are to be loaded
     * @param classLoaderSource the classloader to load the resources
     * @return List of all files with the specified extension in the specified
     *         resource directory
     */
    public static final String[] getFileNames(final String fileExtension, final String baseDirectory, final Class<?> classLoaderSource) {
        return getFileNames(fileExtension, baseDirectory, null, null, classLoaderSource);
    }

    /**
     * Returns a list of the relative paths of all files with the passed
     * extension in the resource directory of the current locale. If no files
     * are found for the current locale, the paths to the English files are
     * loaded.
     *
     * @param fileExtension the file extension of files to load
     * @param baseDirectory Folder from which the files are to be loaded
     * @param locale if not <code>null</code> a subfolder with the locales
     *            language will be searched
     * @param fallbackLocale if not <code>null</code> and the subfolder with the
     *            locales language is not found then a subfolder with this
     *            fallback locales language will be searched
     * @param classLoaderSource the classloader to load the resources
     * @return List of all files with the specified extension in the specified
     *         resource directory
     */
    public static final String[] getFileNames(final String fileExtension, final String baseDirectory, final Locale locale, final Locale fallbackLocale, final Class<?> classLoaderSource) {
        String language = locale != null ? locale.getLanguage() : "";
        File classMainSourceDirOrJar = ReflectionUtils.getClassMainSourceFolderOrJar(classLoaderSource);
        String[] fileNames = new String[0];
        try {
            Enumeration<JarEntry> entries;
            JarFile jarFile;
            jarFile = new JarFile(classMainSourceDirOrJar);
            entries = jarFile.entries();
            String packagePattern = getJarPackagePattern(baseDirectory, language, fileExtension);
            List<JarEntry> jarEntries = new ArrayList<>();

            if (entries != null) {
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.matches(packagePattern)) {
                        jarEntries.add(jarEntry);
                    }
                }
                // if no files are found for the current locale language -> load the English ones
                if (jarEntries.isEmpty() && fallbackLocale != null) {
                    language = fallbackLocale.getLanguage();
                    packagePattern = getJarPackagePattern(baseDirectory, language, fileExtension);
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        String jarEntryName = jarEntry.getName();
                        if (jarEntryName.matches(packagePattern)) {
                            jarEntries.add(jarEntry);
                        }
                    }
                }
                fileNames = new String[jarEntries.size()];
                for (int i = 0; i < fileNames.length; i++) {
                    JarEntry jarEntry = jarEntries.get(i);
                    fileNames[i] = jarEntry.toString();
                }
            }
            try {
                jarFile.close();
            } catch (Exception ex) {
                //do nothing, whether NullPointer or IOException
            }
        } catch (Exception e) {
            File baseDir = new File(classMainSourceDirOrJar, baseDirectory);
            File localizedDir = new File(baseDir, language);
            File[] files = localizedDir.listFiles();
            // if no scripts were found for the locale -> load the english ones
            if (files.length == 0 && fallbackLocale != null) {
                language = fallbackLocale.getLanguage();
                localizedDir = new File(baseDir, language);
                files = localizedDir.listFiles();
            }
            List<String> fileNameList = new ArrayList<>(files.length);
            String classMainSourceDirPath = classMainSourceDirOrJar.getPath();
            int classMainSourceDirPathLength = classMainSourceDirPath.length() + 1; // + 1 = the fileSeparator
            for (File file : files) {
                try {
                    String filePath = file.getCanonicalPath();
                    if (filePath.endsWith("." + fileExtension)) {
                        String fileName = filePath.substring(classMainSourceDirPathLength);
                        fileNameList.add(fileName);
                    }
                } catch (Exception e2) {
                    //ignore
                }
            }
            fileNames = new String[fileNameList.size()];
            System.arraycopy(fileNameList.toArray(), 0, fileNames, 0, fileNames.length);
        }
        return fileNames;
    }

    /**
     * @param jarResourceBaseDirName base name of the jar file
     * @param language the name of the language subdirectory in the jar file
     * @param fileExtension the extension of the files to be loaded
     * @return the full file pattern to load files from a jar file
     */
    private static String getJarPackagePattern(final String jarResourceBaseDirName, String language, final String fileExtension) {
        language = isNullOrEmpty(language) ? "" : language + "/";
        String packagePattern = jarResourceBaseDirName + language + "[^/]+\\." + fileExtension;
        return packagePattern;
    }

}
