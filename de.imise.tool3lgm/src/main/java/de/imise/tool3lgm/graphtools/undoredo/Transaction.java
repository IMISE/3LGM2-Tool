package de.imise.tool3lgm.graphtools.undoredo;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.util.StringUtils;

/**
 * @author N.N.
 */
public class Transaction {

    /**
     * COMMENTME
     */
    private final List<String> undo;

    /**
     * COMMENTME
     */
    private final List<String> redo;

    /**
     * COMMENTME
     */
    private final List<String> pre_selection;

    /**
     * COMMENTME
     */
    private final List<String> post_selection;

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
        undo = new ArrayList<>(50);
        pre_selection = new ArrayList<>();
        post_selection = new ArrayList<>();
        redo = new ArrayList<>(50);
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
        return !StringUtils.isNullOrEmptyOrBlank(command);
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
        if (isProcessable(command)) {
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
     * Löscht das letzte Redo-Kommando, das mit dem <code>commadPrefix</code> beginnt, wenn es ein solches gibt und fügt ein neues Kommando mit dem
     * <code>commadPrefix</code> und den <code>commandArguments</code> am Ende an. <br />
     * Diese Funktion ist gedacht, um z. B. bei Verschiebeoperationen nicht jeden Zwischenschritt zu speichern. Für das Redo benötigt man immer nur
     * die letzte Verschiebeoperation, da sie den endgültigen Ort und Größe eines Elementes eindeutig bestimmt.
     *
     * @param commandPrefix
     * @param commandArguments
     */
    final void addOrReplaceRedoCommand(final String commandPrefix, final String commandArguments) {
        if (isProcessable(commandPrefix)) {
            for (int i = redo.size() - 1; i >= 0; i--) {
                if (redo.get(i).startsWith(commandPrefix)) {
                    redo.remove(i);
                    break;
                }
            }
            String command = commandArguments.isEmpty() ? commandPrefix : commandPrefix + " " + commandArguments;
            redo.add(command);
        }
    }

    /**
     * Logt ein Undo-Kommando nur, wenn nicht schon ein Undo-Kommando mit demselben <code>commandpre</code> in dieser Trasaktion vorkommt. Diese
     * Funktion ist gedacht, um z. B. bei Verschiebeoperationen nicht jeden Zwischenschritt zu speichern. Für das
     * Undo benötigt man immer nur das erste Undo-Kommando, da sie den Ausgangs-Ort und -Größe eines Elementes eindeutig bestimmt.
     *
     * @param commandPrefix
     * @param commandArguments
     */
    final void addUndoCommandIfNotExist(final String commandPrefix, final String commandArguments) {
        if (isProcessable(commandPrefix)) {
            for (int i = undo.size() - 1; i >= 0; i--) {
                if (undo.get(i).startsWith(commandPrefix)) {
                    return;
                }
            }
            String command = commandArguments.isEmpty() ? commandPrefix : commandPrefix + " " + commandArguments;
            undo.add(command);
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
     * @param command
     */
    final void addUndoCommand(final String command) {
        if (isProcessable(command)) {
            undo.add(command);
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
     * @return <code>true</code> only if the transaction have the
     *         {@link TransactionManager#STANDARD_PID} and the
     *         transaction is still running (open counter must be
     *         at least 1)
     */
    public boolean isOpenStandardPidTransaction() {
        return hasStandardPid() && open > 0;
    }

    /**
     * @return <code>true</code> only if the transaction does not have the
     *         {@link TransactionManager#STANDARD_PID} and at least one
     *         inner transaction is open (open counter must be at least 2)
     */
    public boolean isOpenDialogTransaction() {
        return !hasStandardPid() && open > 1;
    }

    /**
     * @return
     */
    public boolean hasStandardPid() {
        return pid == STANDARD_PID;
    }

    /**
     * @param transactionString
     */
    private final static String getFullTransactionString(final List<String> transactionString) {
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