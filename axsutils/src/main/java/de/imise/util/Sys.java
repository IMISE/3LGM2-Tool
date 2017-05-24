package de.imise.util;

public class Sys {

    /** Anzahl der StackTrace-Zeilen, die uasgegeben werden sollen */
    public static int traceSteps = 10;

    public static boolean insertBlankLineAfterOutput;

    public static void out(final String message) {
        System.out.println(message);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < 10; i++) {
            System.out.println(stackTrace[i]);
        }
        if (insertBlankLineAfterOutput) {
            System.out.println();
        }
    }

    public static void err(final String message) {
        System.err.println(message);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < 10; i++) {
            System.err.println(stackTrace[i]);
        }
        if (insertBlankLineAfterOutput) {
            System.err.println();
        }
    }

    public static void out1(final String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.out.println(message);
        System.out.print("      ");
        System.out.println(stackTrace[2]);
        if (insertBlankLineAfterOutput) {
            System.out.println();
        }
    }

    public static void err1(final String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.err.println(message);
        System.err.print("      ");
        System.err.println(stackTrace[2]);
        if (insertBlankLineAfterOutput) {
            System.err.println();
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