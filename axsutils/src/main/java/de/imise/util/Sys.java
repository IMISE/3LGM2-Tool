package de.imise.util;

import java.io.PrintStream;

public class Sys {

    /** Anzahl der StackTrace-Zeilen, die uasgegeben werden sollen */
    public static int maxTraceSteps = 100;

    public static boolean insertBlankLineAfterOutput;

    public static void out1(final Object... message) {
        outInternal(1, message);
    }

    public static void err1(final Object... message) {
        errInternal(1, message);
    }

    public static void out(final Object... message) {
        outInternal(maxTraceSteps, message);
    }

    public static void err(final Object... message) {
        errInternal(maxTraceSteps, message);
    }

    public static void outn(final int maxTraceSteps, final Object... message) {
        outInternal(maxTraceSteps, message);
    }

    public static void errn(final int maxTraceSteps, final Object... message) {
        errInternal(maxTraceSteps, message);
    }

    private static void outInternal(final int maxTraceSteps, final Object... message) {
        printInternal(maxTraceSteps, System.out, 4, message);
    }

    private static void errInternal(final int maxTraceSteps, final Object... message) {
        printInternal(maxTraceSteps, System.err, 4, message);
    }

    public static void errm(final int maxTraceSteps, final int hideTraceSteps, final Object... message) {
        printInternal(maxTraceSteps, System.err, hideTraceSteps + 3, message);
    }

    private static void printInternal(final int maxTraceSteps, final PrintStream stream, final int hideTraceSteps, final Object... message) {
        if (message == null) {
            println(null, stream, false);
        } else if (message.length == 0) {
            println("", stream, false);
        } else if (message.length == 1) { //ich denke das ist der häufigste Fall und der sollte ohne init einer for-Schleife gehen -> daher extra
            println(message[0], stream, false);
        } else {
            for (int i = 0; i < message.length; i++) {
                println(message[i], stream, false);
            }
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = hideTraceSteps; i < maxTraceSteps + hideTraceSteps && i < stackTrace.length; i++) {
            println(stackTrace[i], stream, true);
        }
        if (insertBlankLineAfterOutput) {
            System.err.println();
        }
    }

    private static final void println(final Object o, final PrintStream stream, final boolean indent) {
        String s = indent ? "      " + o : String.valueOf(o); // nicht über toString() gehen, weil es null sein kann
        stream.println(s);
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