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

}
