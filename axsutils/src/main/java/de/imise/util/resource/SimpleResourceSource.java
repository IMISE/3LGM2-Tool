package de.imise.util.resource;

import com.google.common.base.Strings;

/**
 * Interface that provides a default function which returns a resource file name
 * for a class package. Optionally this name can be extended by an arbitrary string.
 *
 * @author AXS (19.05.2020)
 */
public interface SimpleResourceSource {

    /**
     * @param resourcePackageNameSource
     * @param resourceName
     * @return a resource file name for a class package. Optionally this name can be
     *         extended by an arbitrary string.
     */
    public default String getResourceFileName(Class<?> resourcePackageNameSource, final String resourceName) {
        boolean appendSimpleName = !Strings.isNullOrEmpty(resourceName);
        if (resourcePackageNameSource == null) {
            resourcePackageNameSource = getClass();
        }
        String resourceFileName = !appendSimpleName ? resourcePackageNameSource.getName() : resourcePackageNameSource.getPackage().getName();
        if (appendSimpleName) {
            resourceFileName += "." + resourceName;
        }
        resourceFileName = resourceFileName.replace('.', '/');
        return resourceFileName;
    }

}
