package de.axs.deploytools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * Provides 2 Functions. First functions comments all "PAUSE" statements in a batch file.
 * Seconds can reverse this.
 *
 * @author AXS (18.12.2019)
 */
public class BatchPauseEnabledSwitch {

    public static void main(final String[] args) {
        if (args.length == 0 || args.length > 0 && args[0].equals("-?")) {
            System.out.println("Usage: '" + BatchPauseEnabledSwitch.class.getSimpleName() + " +batch_file_name' to enable all PAUSE statments in the batch file = remove comments in PAUSE lines or");
            System.out.println("Usage: '" + BatchPauseEnabledSwitch.class.getSimpleName() + " batch_file_name' to enable all PAUSE statments in the batch file = same like first usage or");
            System.out.println("Usage: '" + BatchPauseEnabledSwitch.class.getSimpleName() + " -batch_file_name' to disable all PAUSE statments in the batch file = add comments in PAUSE lines");
            return;
        }
        for (String arg : args) {
            boolean enabled = !arg.startsWith("-");
            String fileName = !enabled || arg.startsWith("+") ? arg.substring(1) : arg;
            BatchPauseEnabledSwitch.changeLastPauseLine(fileName, enabled);
        }
    }

    private final static void changeLastPauseLine(final String fileName, final boolean enabled) {
        try {
            File file = new File(fileName);
            File tmpFile = new File(fileName + ".tmp");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = enabled ? getEnabledPauseLine(line) : getDisabledPauseLine(line);
                System.out.println(line);
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

    private static String getEnabledPauseLine(final String line) {
        String lineContent = line;
        if (line.startsWith("::")) {
            lineContent = line.substring(2);
        } else if (line.toUpperCase().startsWith("REM")) {
            lineContent = line.substring(3);
        }
        if (lineContent.trim().toUpperCase().equals("PAUSE")) {
            return lineContent;
        }
        return line;
    }

    private static String getDisabledPauseLine(final String line) {
        if (line.trim().toUpperCase().equals("PAUSE")) {
            return "::" + line;
        }
        return line;
    }

}
