package de.imise.util;

/**
 * Functions to calculate the available memory or print memory information to
 * the console.
 *
 * @author AXS (23.09.2020)
 */
public class MemoryHandler {

    /**
     * @param bits
     * @param prefix
     */
    public static void printMemorySizes(final long bytes, final String prefix) {
        long kBytes = bytes / 1024l;
        long mBytes = kBytes / 1024l;
        Sys.errm(1, 1, prefix + " B=" + bytes + "\tKB=" + kBytes + "\tMB=" + mBytes);
    }

    /**
     * @param header
     */
    public static void printMaxNowAvailableMemory() {
        printMaxNowAvailableMemoryInfoInternal("");
    }

    /**
     * @param header
     */
    public static void printMaxNowAvailableMemory(final String prefix) {
        printMaxNowAvailableMemoryInfoInternal(prefix);
    }

    /**
     * @param header
     */
    private static void printMaxNowAvailableMemoryInfoInternal(final String prefix) {
        long bytes = getMaxNowAvailableMemory(0);
        long kBytes = bytes / 1024l;
        long mBytes = kBytes / 1024l;
        Sys.errm(1, 2, prefix + " B=" + bytes + "\tKB=" + kBytes + "\tMB=" + mBytes);
    }

    /**
     * @param buffer this percent value of the really available memory will be
     *            substracted to ensure some memory for other processes
     * @return the available memory
     */
    public static long getMaxNowAvailableMemory(final int buffer) {
        int realBuffer = buffer < 0 ? 0 : buffer > 100 ? 100 : buffer;
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long availableMemory = maxMemory - totalMemory + freeMemory;
        availableMemory = availableMemory / 100 * (100 - realBuffer);
        return availableMemory;
    }

}
