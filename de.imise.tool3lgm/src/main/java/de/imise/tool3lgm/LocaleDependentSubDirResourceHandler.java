package de.imise.tool3lgm;

import java.util.Locale;

import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.resource.ResourceFileNamesFinder;

/**
 * Class for loading resources located in language-dependent subdirectories in
 * the resources folder. This class basically just provides a function that
 * manages access to these resources at runtime from within the development
 * environment and from within the deploited jar file.
 *
 * @author AXS (14.06.2017)
 */
public class LocaleDependentSubDirResourceHandler {

    /**
     * Returns a list of the relative paths of all files with the passed
     * extension in the resource directory of the current locale. If no files
     * are found for the current locale, the paths to the English files are
     * loaded.
     *
     * @param fileExtension the file extension of files to load
     * @param baseDirectory Folder from which the files are to be loaded
     * @param classLoaderSource the classloader to load the resources
     * @return List of all files with the specified extension in the specified
     *         resource directory
     */
    public static final String[] getFileNames(final String fileExtension, final String baseDirectory, final Class<?> classLoaderSource) {
        Locale locale = UserProperties.getLocale();
        return ResourceFileNamesFinder.getFileNames(fileExtension, baseDirectory, locale, Locale.ENGLISH, classLoaderSource);
    }

}
