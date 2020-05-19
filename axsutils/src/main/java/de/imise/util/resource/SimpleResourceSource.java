package de.imise.util.resource;

import com.google.common.base.Strings;

/**
 * @author AXS (19.05.2020)
 */
public interface SimpleResourceSource {

    /**
     * @param ressourcePackageNameSource
     * @param resourceName
     * @return
     */
    public default String getResourceFileName(Class<?> ressourcePackageNameSource, final String resourceName) {
        boolean appendSimpleName = !Strings.isNullOrEmpty(resourceName);
        if (ressourcePackageNameSource == null) {
            ressourcePackageNameSource = getClass();
        }
        String resourceFileName = !appendSimpleName ? ressourcePackageNameSource.getName() : ressourcePackageNameSource.getPackage().getName();
        if (appendSimpleName) {
            resourceFileName += "." + resourceName;
        }
        resourceFileName = resourceFileName.replace('.', '/');
        return resourceFileName;
    }

}
