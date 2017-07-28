package de.imise.util;

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

    public static void out(final int maxTraceSteps, final Object... message) {
        outInternal(maxTraceSteps, message);
    }

    public static void err(final int maxTraceSteps, final Object... message) {
        errInternal(maxTraceSteps, message);
    }

    private static void outInternal(final int maxTraceSteps, final Object... message) {
        print(maxTraceSteps, false, 4, message);
    }

    private static void errInternal(final int maxTraceSteps, final Object... message) {
        print(maxTraceSteps, true, 4, message);
    }

    private static void print(final int maxTraceSteps, final boolean err, final int hideTraceSteps, final Object... message) {
        if (message == null) {
            println(null, err, false);
        } else if (message.length == 0) {
            println("", err, false);
        } else if (message.length == 1) { //ich denke das ist der häufigste Fall und der sollte ohne init einer for-Schleife gehen -> daher extra
            println(message[0], err, false);
        } else {
            for (int i = 0; i < message.length; i++) {
                println(message[i], err, false);
            }
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = hideTraceSteps; i < maxTraceSteps + hideTraceSteps && i < stackTrace.length; i++) {
            println(stackTrace[i], err, true);
        }
        if (insertBlankLineAfterOutput) {
            System.err.println();
        }
    }

    private static final void println(final Object o, final boolean err, final boolean indent) {
        String s = indent ? "      " + o : String.valueOf(o); // nicht über toString() gehen, weil es null sein kann
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