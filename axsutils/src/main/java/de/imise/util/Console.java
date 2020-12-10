package de.imise.util;

/**
 * @author AXS
 * @create 11.03.2013
 */
public class Console {

    /**
     * Loggt die aufrufende Klasse und Methode im Stderr
     */
    public static final void syserr() {
        syserr(null);
    }

    /**
     * Loggt die aufrufende Klasse und Methode im Stderr
     * 
     * @param message Nachricht, die zusätzlich angehängt wird
     */
    public static final void syserr(String message) {
        syserr(message, false);
    }

    /**
     * Loggt die aufrufende Klasse und Methode im Stderr
     * 
     * @param message Nachricht, die zusätzlich angehängt wird
     * @param fullClassName wenn <code>true</code> wird der volle Klassenname
     *            ausgegeben, sonst der einfache.
     */
    public static final void syserr(String message, boolean fullClassName) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (trace.length > 1) {
            int traceBackSteps = 2;
            StackTraceElement element = trace[traceBackSteps];
            String className = element.getClassName();
            while (className.equals(Console.class.getName())) {
                element = trace[++traceBackSteps];
                className = element.getClassName();
            }
            StringBuilder sb = new StringBuilder(fullClassName ? className : className.substring(className.lastIndexOf('.') + 1));
            sb.append(".");
            sb.append(element.getMethodName());
            sb.append(" (");
            sb.append(element.getLineNumber());
            sb.append(")");
            if (message != null) {
                sb.append(": ");
                sb.append(message);
            }
            System.err.println(sb.toString());
        }
    }

}
