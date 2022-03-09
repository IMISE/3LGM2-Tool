package de.imise.util;

/**
 * @author AXS (28.03.2020)
 */
public class OperatingSystem {

    /**
     * @return <code>true</code> if the operating system is Mac OS.
     */
    public static boolean isMacOs() {
        return System.getProperty("os.name", "").toUpperCase().startsWith("MAC");
    }

    /**
     * @return
     */
    public static boolean isARMArchtecture() {
        String property = System.getProperty("os.arch", "").toUpperCase();
        return property.contains("ARM");

    }

    /**
     * @return <code>true</code> if the operating system is Mac OS.
     */
    public static boolean isWindowsOs() {
        return System.getProperty("os.name", "").toUpperCase().contains("WINDOWS");
    }

}
