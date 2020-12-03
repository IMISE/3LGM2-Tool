package de.imise.tool3lgm.graphtools.undoredo;

import static de.imise.util.htmlxml.ParseSaveStringHandler.getParseSaveString;

import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.util.htmlxml.ParseSaveStringHandler;

public class CommandHandler {

    /**
     * Parst eine Kommando-Zeile, wie sie in Undo-Redo-Kommandos steckt. Ab dem
     * übergebenen firstArgumentBeginIndex werden alle Tokens als Einzelargument
     * der Rückgabeliste hinzugefügt, die zwischen zwei einzelnen Hochkomma '
     * stehen (auch leere Zeichenketten) und/oder die mit Leerzeichen
     * voneinander getrennt sind. Stehen 2 Leerzeichen außerhalb von Hochkommas
     * hintereinander, so wird dazwischen ein leeres Argument erkannt.
     *
     * @param line Die zu parsende Kommandozeile
     * @param args Liste, in die die eventuell in der Zeile stehenden
     *            Kommandoargumente gelegt werden
     * @return Name des Kommandos der übergebenen Zeile (das ist das erste Wort
     *         am Anfang)
     */
    public static final String parseCommandLine(final String line, final List<String> args) {
        int nextWhitespace = line.indexOf(' ');
        String commandName;
        args.clear();
        //kein weiteres Leerzeichen gefunden
        if (nextWhitespace < 0) {
            return line;

        }
        commandName = line.substring(0, nextWhitespace);
        int wordStart = nextWhitespace + 1;
        int lineLength = line.length();
        //für alle Zeichen ab dem Index des Beginns des ersten Argumentes
        for (int i = wordStart; i < lineLength; i++) {
            //hole das Zeichen
            char c = line.charAt(i);
            //Zeichen ist Leerzeichen
            if (c == ' ') {
                //das erste Zeichen des Argumentes ist ein Leerzeichen?
                if (i == wordStart) {
                    //das Argument ist leer -> füge es hinzu
                    args.add("");
                    //das nächste Wort fängt genau ein Zeichen dahinter an
                    wordStart++;
                    //wenn das Leerzeichen das letzte Zeichen war
                    if (wordStart == lineLength) {
                        //dann muss dahinter noch ein leeres Argument erkannt werden
                        args.add("");
                        //komplett raus
                        break;
                    }
                    //weiter mit nächstem Argument
                    continue;
                }
                //Zeichen ist ein Hochkomma
            } else if (c == ParseSaveStringHandler.TEXT_SURROUNDER) {
                //suche dahinter das schließende Hochkomma
                for (int w = i + 1; w < lineLength; w++) {
                    c = line.charAt(w);
                    //wenn das 2. Hochkomma gefunden wurde
                    if (c == ParseSaveStringHandler.TEXT_SURROUNDER) {
                        //schneide das Wort zwischen den Hochkommas aus und speichere es als Argument
                        args.add(line.substring(wordStart + 1, w));
                        //wenn das nächste Zeichen in der Zeile ein Leerzeichen ist
                        if (w + 1 < lineLength && line.charAt(w + 1) == ' ') {
                            //wenn dieses Leerzeichen das letzte Zeichen in der Zeile ist
                            if (w + 2 == line.length()) {
                                //füge ein leeres Argument hinzu
                                args.add("");
                                //setzte i so, dass due äußere for-Schleife abgebrochen wird
                                i = lineLength;
                                //raus aus der inneren for-Schleife für das Einzelwort
                                break;
                                //wenn hinter dem nächsten Leerzeichen noch etwas kommt
                            } else {
                                //beginnt dort ein neues Argument
                                wordStart = w + 2;
                                //i wird beim nächsten Schleifendurchlauf auf denselben Wert gesetzt und alles steht wie auf Anfang
                                i = w + 1;
                            }
                            //wenn das nächste Zeichen nach dem schließenden Hochkomma kein Leezeichen war (das ist eigentlich nicht korrekt, würde aber trotzdem gehen)
                        } else {
                            //hier geht das nächste Wort los
                            wordStart = w + 1;
                            //i wird beim nächsten Schleifendurchlauf auf denselben Wert gesetzt und alles steht wie auf Anfang
                            i = w;
                        }
                        //2. Hochkomma war gefunden -> raus aus der inneren Schleife
                        break;
                    }
                }
                //das Zeichen ist kein Leerzeichen und kein Hochkomma
            } else {
                //suche solange weiter in dem String, bis das nächste Leerzeichen, das nächste öffnende Hochkomma oder das Zeilenende gefunden wurde
                int w;
                for (w = i + 1; w < line.length(); w++) {
                    c = line.charAt(w);
                    if (c == ' ' || c == ParseSaveStringHandler.TEXT_SURROUNDER) {
                        break;
                    }
                }
                //Argument ausschneiden ind speichern
                args.add(line.substring(i, w));
                //wenn es hinter dem Argument noch ein Leerzeichen gibt
                if (w < lineLength && line.charAt(w) == ' ') {
                    //wenn dieses Leerzeichen ganz hinten steht
                    if (w + 1 == line.length()) {
                        //leeres Argument hinzufügen
                        args.add("");
                    }
                }
                //hier geht das nächste Wort los
                wordStart = w + 1;
                //i wird beim nächsten Schleifendurchlauf auf denselben Wert gesetzt und alles steht wie auf Anfang
                i = w;
            }
        }
        return commandName;
    }

    /**
     * @param command
     * @param args
     * @return
     */
    public static final String getCommandLine(final GDCommands command, final Object... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(command);
        appendArguments(sb, args);
        return sb.toString();
    }

    /**
     * @param args
     * @return
     */
    public static final String getArgumentsString(final Object... args) {
        StringBuilder sb = new StringBuilder();
        appendArguments(sb, args);
        return sb.toString();
    }

    /**
     * @param sb
     * @param args
     */
    private static final void appendArguments(final StringBuilder sb, final Object... args) {
        for (Object arg : args) {
            appendArgument(sb, arg);
        }
    }

    /**
     * @param sb
     * @param args
     */
    private static final void appendArgument(final StringBuilder sb, Object arg) {
        if (sb.length() != 0) {
            sb.append(" ");
        }
        if (arg instanceof Collection) {
            Collection<?> innerArgs = (Collection<?>) arg;
            for (Object innerArg : innerArgs) {
                appendArgument(sb, innerArg); //Collections of collections are not allowed
            }
        } else {
            //IDSource ? -> replace arg-Object by its ID
            if (arg != null && arg instanceof IDSource) {
                arg = ((IDSource) arg).getID();
            }
            //not a Number? -> convert arg to String that the command parser
            //understands as one token (numbers are already one token)
            if (arg != null && !(arg instanceof Number)) {
                arg = getParseSaveString(arg);
            }
            sb.append(arg);
        }
    }

}
