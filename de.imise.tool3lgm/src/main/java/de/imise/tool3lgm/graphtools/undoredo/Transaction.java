package de.imise.tool3lgm.graphtools.undoredo;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;

/**
 * @author N.N.
 */
public class Transaction {

    /**
     * COMMENTME
     */
    private final ArrayList<String> undo;

    /**
     * COMMENTME
     */
    private final ArrayList<String> redo;

    /**
     * COMMENTME
     */
    private final ArrayList<String> pre_selection;

    /**
     * COMMENTME
     */
    private final ArrayList<String> post_selection;

    /**
     * COMMENTME
     */
    private final int pid;

    /**
     * COMMENTME
     */
    private int open;

    /**
     * COMMENTME
     */
    private final GraphDocument doc;

    /**
     * @param pid
     * @param doc
     */
    public Transaction(final int pid, final GraphDocument doc) {
        this.doc = doc;
        undo = new ArrayList<String>(50);
        pre_selection = new ArrayList<String>();
        post_selection = new ArrayList<String>();
        redo = new ArrayList<String>(50);
        this.pid = pid;
        open = 0;
    }

    /**
     * @return ID of the transaction
     */
    final int getProcessID() {
        return pid;
    }

    /**
     * Liefert <code>true</code>, wenn das ausführen des Undo- oder Redokommandos nichts sinnvolles bewirkt.
     * 
     * @return
     */
    boolean isUseless() {
        for (String s : redo) {
            if (isProcessable(s)) {
                return false;
            }
        }
        for (String s : undo) {
            if (isProcessable(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param command
     * @return
     */
    private final static boolean isProcessable(final String command) {
        if (command == null || command.equals("")) {
            return false;
        }
        if (command.startsWith(GDCommands.ELEMENT_PROPERTIES.toString())) {
            return false;
        }
        return true;
    }

    /**
     * @return
     */
    final int getRedoSize() {
        return redo.size();
    }

    /**
     * @param command
     */
    final void addRedoCommand(final String command) {
        if (command != null && !command.trim().equals("")) {
            redo.add(command);
        }
    }

    /**
     * Liefert das Redo-Kommando am Index <code>index</code>.
     * 
     * @param index
     * @return
     */
    final String getRedoCommand(final int index) {
        return redo.get(index).toString();
    }

    /**
     * Löscht das letzte Redo-Kommando, das mit dem <code>commadPrefix</code> beginnt, wenn es ein solches gibt und fügt ein neues Kommando mit dem <code>commadPrefix</code> und den <code>commandArguments</code> am Ende an. <br />
     * Diese Funktion ist gedacht, um z. B. bei Verschiebeoperationen nicht jeden Zwischenschritt zu speichern. Für das Redo benötigt man immer nur die letzte Verschiebeoperation, da sie den endgültigen Ort und Größe eines Elementes eindeutig bestimmt.
     * 
     * @param commandPrefix
     * @param commandArguments
     */
    final void addOrReplaceRedoCommand(final String commandPrefix, final String commandArguments) {
        if (commandPrefix != null && !commandPrefix.trim().equals("")) {
            for (int i = redo.size() - 1; i >= 0; i--) {
                if (redo.get(i).startsWith(commandPrefix)) {
                    redo.remove(i);
                    break;
                }
            }
            redo.add(commandPrefix + " " + commandArguments);
        }
    }

    /**
     * Logt ein Undo-Kommando nur, wenn nicht schon ein Undo-Kommando mit demselben <code>commandpre</code> in dieser Trasaktion vorkommt. Diese Funktion ist gedacht, um z. B. bei Verschiebeoperationen nicht jeden Zwischenschritt zu speichern. Für das
     * Undo benötigt man immer nur das erste Undo-Kommando, da sie den Ausgangs-Ort und -Größe eines Elementes eindeutig bestimmt.
     * 
     * @param commandPrefix
     * @param commandArguments
     */
    final void addUndoCommandIfNotExist(final String commandPrefix, final String commandArguments) {
        if (commandPrefix != null && !commandPrefix.trim().equals("")) {
            for (int i = undo.size() - 1; i >= 0; i--) {
                if (undo.get(i).startsWith(commandPrefix)) {
                    return;
                }
            }
            undo.add(commandPrefix + " " + commandArguments);
        }
    }

    /**
     * @return
     */
    final int getUndoSize() {
        return undo.size();
    }

    /**
     * @param i
     * @return
     */
    final String getUndoCommand(final int i) {
        return undo.get(i);
    }

    /**
     * @param string
     */
    final void addUndoCommand(final String string) {
        if (string != null && !string.equals("")) {
            undo.add(string);
        }
    }

    /**
     * @return
     */
    final int getPreSelectionSize() {
        return pre_selection.size();
    }

    /**
     * @param i
     * @return
     */
    final String getPreSelectionItem(final int i) {
        return pre_selection.get(i);
    }

    /**
     * @param string
     */
    final void addPreSelectionItem(final String string) {
        if (string != null && !string.equals("")) {
            pre_selection.add(string);
        }
    }

    /**
     * @return
     */
    final int getPostSelectionSize() {
        return post_selection.size();
    }

    /**
     * @param i
     * @return
     */
    final String getPostSelectionItem(final int i) {
        return post_selection.get(i);
    }

    /**
     * @param string
     */
    final void addPostSelectionItem(final String string) {
        if (string != null && !string.equals("")) {
            post_selection.add(string);
        }
    }

    /**
	 * 
	 */
    public final void print() {
        for (int i = 0; i < redo.size(); i++) {
            System.out.println("Action (" + (i + 1) + ")\t" + redo.get(i));
        }
        for (int i = 0; i < undo.size(); i++) {
            //		for (int i = undo.size() - 1; i >= 0; i--)
            System.out.println("Undo   (" + (i + 1) + ")\t" + undo.get(i));
        }
    }

    /**
	 * 
	 */
    public void open() {
        open++;
    }

    /**
	 * 
	 */
    public void close() {
        open--;
    }

    /**
     * @return
     */
    public boolean isOpen() {
        return open > 0;
    }

    /**
     * @param transactionString
     */
    private final static String getFullTransactionString(final ArrayList<String> transactionString) {
        //Alle Zwischenschritte von Verschiebungen ein und desselben Containers werden ausgeblendet
        StringBuilder sb = new StringBuilder();
        for (String next : transactionString) {
            sb.append(next);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFullTransactionString(redo));
        sb.append("---\n");
        sb.append(getFullTransactionString(undo));
        return sb.toString();
    }

    /**
     * @return
     */
    public GraphDocument getGraphDocument() {
        return doc;
    }
}