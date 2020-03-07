package de.axs.deploytools;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class ChangeLineHandler {

    private final String fileName;

    private final String lineStart;

    private final String lineEnd;

    private final String newLineContent;

    public static Robot robot;
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileName
     * @param lineStart
     * @param lineEnd
     * @param newLineContent
     */
    public ChangeLineHandler(final String fileName, final String lineStart, final String lineEnd, final String newLineContent) {
        this.fileName = fileName;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.newLineContent = newLineContent;
        changeLine();
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        if (args.length >= 4) {
            new ChangeLineHandler(args[0], args[1], args[2], args[3]);
        }
        int delay = args.length == 5 ? Integer.parseInt(args[4]) : 0;
        robot.delay(delay);
    }

    /**
     *
     */
    private final void changeLine() {
        System.out.println(getClass().getSimpleName());
        System.out.println("FileName : " + fileName);
        System.out.println("LineStart: " + lineStart);
        System.out.println("LineEnd: " + lineEnd);
        System.out.println("NewLineContent: " + newLineContent);
        System.out.println();
        try {
            File file = new File(fileName);
            File tmpFile = new File(fileName + ".tmp");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            int i = 0;
            System.out.println("ChangeLine " + fileName + ":");
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(lineStart) && line.endsWith(lineEnd)) {
                    System.out.println(++i + " OLD: " + line);
                    line = lineStart + newLineContent + lineEnd;
                    System.out.println(++i + " NEW: " + line);
                }
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            reader.close();
            file.delete();
            tmpFile.renameTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * Extrahiert den Versions-String zwischen den doppelten Anführugszeichen
     *
     * @param originalVersionString
     * @return
     */
    public static String extractVersionString(final String originalVersionString) {
        if (originalVersionString == null || originalVersionString.trim().equals("")) {
            return "";
        }
        int start = originalVersionString.indexOf('\"') + 1;
        int end = originalVersionString.lastIndexOf('\"');
        if (start < end) {
            return originalVersionString.substring(start, end);
        } else if (start == end) {
            return "";
        }
        return originalVersionString;
    }

    /**
     * @param fullVersionString
     * @return
     */
    public static String getIncreasedVersionString(final String fullVersionString) {
        int lastPoint = fullVersionString.lastIndexOf('.');
        if (lastPoint < 0 || lastPoint == fullVersionString.length() - 1) {
            boolean found = false;
            for (int i = 0; i < fullVersionString.length(); i++) {
                char c = fullVersionString.charAt(i);
                if (isDigit(c)) {
                    lastPoint = i - 1;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException(fullVersionString);
            }
        }
        //nach dem letzten Punkt muss eine zahl kommen
        int start = lastPoint + 1;
        char c = fullVersionString.charAt(start);
        if (!isDigit(c)) {
            throw new IllegalArgumentException(fullVersionString);
        }
        int end = getVersionEnd(fullVersionString, start);

        String prefix = fullVersionString.substring(0, start);
        String versionString = fullVersionString.substring(start, end);
        String increasedVersion = getIncreasedNumberString(versionString);
        String postfix = end == fullVersionString.length() ? "" : fullVersionString.substring(end);

        String fullNewVersionString = prefix + increasedVersion + postfix;

        return fullNewVersionString;
    }

    /**
     * @param numberString
     * @return
     */
    private static String getIncreasedNumberString(final String numberString) {
        int number = Integer.parseInt(numberString);
        number++;
        return Integer.valueOf(number).toString();
    }

    /**
     * @param fullVersionString
     * @param versionStartIndex
     * @return
     */
    private static int getVersionEnd(final String fullVersionString, final int versionStartIndex) {
        int end = versionStartIndex + 1;
        while (end < fullVersionString.length()) {
            char c = fullVersionString.charAt(end);
            if (isDigit(c)) {
                end++;
            } else {
                break;
            }
        }
        return end;
    }

    private static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }
}
