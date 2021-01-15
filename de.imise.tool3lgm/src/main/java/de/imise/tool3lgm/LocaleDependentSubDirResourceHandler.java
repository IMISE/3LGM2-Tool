package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.ABSOLUTE_TOOL_JAR_PATH;
import static de.imise.tool3lgm.Tool3lgmConstants.APPLICATION_DIR;
import static de.imise.tool3lgm.Tool3lgmConstants.DEV_RESOURCE_DIR_NAME;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Class for loading resources located in language-dependent subdirectories in
 * the resources folder. This class basically just provides a function that
 * manages access to these resources at runtime from within the development
 * environment and from within the deploited jar file.
 *
 * @author AXS (14.06.2017)
 */
public class LocaleDependentSubDirResourceHandler {

    public static final String DEV_RESOURCE_BASE_DIR_NAME = APPLICATION_DIR + DEV_RESOURCE_DIR_NAME;

    /**
     * Returns a list of the relative paths of all files with the passed
     * extension in the resource directory of the current locale. If no files
     * are found for the current locale, the paths to the English files are
     * loaded.
     *
     * @param fileExtension the file extension of files to load
     * @param devTimeResourceBaseDirName Folder from which the files are to be
     *            loaded if they come from the file system at development time
     * @param jarResourceBaseDirName Folder from which the files are to be
     *            loaded, if they are to be inside a jar file for the
     *            post-deploy process
     * @return List of all files with the specified extension in the specified
     *         resource directory
     */
    public static final String[] getFileNames(final String fileExtension, final String devTimeResourceBaseDirName, final String jarResourceBaseDirName) {
        Locale locale = UserProperties.getLocale();
        String language = locale.getLanguage();
        // At development time the files are in a folder -> load files from there, BUT when
        // the tool is released the files are in the jar file in the resource path -> catch case
        try {
            //            String baseDirName = DEV_RESOURCE_BASE_USERPROPERTIES_DIR_NAME;
            String path = devTimeResourceBaseDirName + language;
            File dir = new File(path);
            File[] files = dir.listFiles();
            // if no scripts were found for the locale -> load the english ones
            if (files.length == 0) {
                path = devTimeResourceBaseDirName + "en";
                dir = new File(path);
            }
            List<String> fileNameList = new ArrayList<>(files.length);
            for (File file : files) {
                String filePath = file.getCanonicalPath();
                if (!filePath.endsWith("." + fileExtension)) {
                    continue;
                }
                int baseDirNameLength = DEV_RESOURCE_BASE_DIR_NAME.length();
                String fileName = filePath.substring(baseDirNameLength);
                fileNameList.add(fileName);
            }
            String[] fileNames = new String[fileNameList.size()];
            System.arraycopy(fileNameList.toArray(), 0, fileNames, 0, fileNames.length);
            return fileNames;
            // if the folder with the files was not found, because it is surely located
            // in the issued jar file -> read the files from the jar file
        } catch (Exception e) {
            Enumeration<JarEntry> entries = null;
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(ABSOLUTE_TOOL_JAR_PATH);
                entries = jarFile.entries();
            } catch (IOException e1) {
                // e1.printStackTrace();
            }
            String packagePattern = getJarPackagePattern(jarResourceBaseDirName, language, fileExtension);
            List<JarEntry> jarEntries = new ArrayList<>();

            String[] fileNames = new String[0];

            if (entries != null) {
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.matches(packagePattern)) {
                        jarEntries.add(jarEntry);
                    }
                }
                // if no files are found for the current locale language -> load the English ones
                if (jarEntries.isEmpty()) {
                    packagePattern = getJarPackagePattern(jarResourceBaseDirName, "en", fileExtension);
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
            return fileNames;
        }
    }

    /**
     * @param jarResourceBaseDirName base name of the jar file
     * @param language the name of the language subdirectory in the jar file
     * @param fileExtension the extension of the files to be loaded
     * @return the full file pattern to load files from a jar file
     */
    protected static String getJarPackagePattern(final String jarResourceBaseDirName, final String language, final String fileExtension) {
        String packagePattern = jarResourceBaseDirName + language + "/[^/]+\\." + fileExtension;
        return packagePattern;
    }

}
