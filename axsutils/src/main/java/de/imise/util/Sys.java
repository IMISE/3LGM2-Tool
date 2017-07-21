package de.imise.util;

public class Sys {

    /** Anzahl der StackTrace-Zeilen, die uasgegeben werden sollen */
    public static int maxTraceSteps = 100;

    public static boolean insertBlankLineAfterOutput;

    public static void out1(final Object message) {
        outInternal(message, 1);
    }

    public static void err1(final Object message) {
        errInternal(message, 1);
    }

    public static void out(final Object message) {
        outInternal(message, maxTraceSteps);
    }

    public static void err(final Object message) {
        errInternal(message, maxTraceSteps);
    }

    public static void out(final Object message, final int maxTraceSteps) {
        outInternal(message, maxTraceSteps);
    }

    public static void err(final Object message, final int maxTraceSteps) {
        errInternal(message, maxTraceSteps);
    }

    private static void outInternal(final Object message, final int maxTraceSteps) {
        print(message, maxTraceSteps, false, 4);
    }

    private static void errInternal(final Object message, final int maxTraceSteps) {
        print(message, maxTraceSteps, true, 4);
    }

    private static void print(final Object message, final int maxTraceSteps, final boolean err, final int hideTraceSteps) {
        println(message, err, false);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = hideTraceSteps; i < maxTraceSteps + hideTraceSteps && i < stackTrace.length; i++) {
            println(stackTrace[i], err, true);
        }
        if (insertBlankLineAfterOutput) {
            System.err.println();
        }
    }

    private static final void println(final Object o, final boolean err, final boolean indent) {
        String s = indent ? "      " + o.toString() : o.toString();
        if (err) {
            System.err.println(s);
        } else {
            System.out.println(s);
        }
    }

    public static boolean stackTraceContains(final Class<?> clazz) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = clazz.getCanonicalName();
        for (StackTraceElement traceElement : stackTrace) {
            if (traceElement.toString().startsWith(className)) {
                return true;
            }
        }
        return false;
    }

}