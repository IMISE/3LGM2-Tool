package de.imise.tool3lgm.graphtools.undoredo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;

/**
 * Verwaltet die Undo-/Redo-Steuerung
 *
 * @author N.N., AXS
 */
public class TransactionManager {

    /**
     *
     */
    private final GDCollection gdcoll;

    /**
     * Liste aller <code>TransactionListener</code>
     */
    private final Set<TransactionListener> transactionListeners = new HashSet<>(3);

    /**
     * Liste aller durchgeführten Transaktionen, die sich auch rückgängig machen
     * lassen sollen.
     */
    private final Transaction trans_q[];

    /**
     * Maximale Größe der Transaktionsliste <code>trans_q</code>.
     */
    private static final int TRANSQ_SIZE = 50;

    /**
     * Wert von <code>cur_pos</code>, wenn es keine Transaktion gibt, die sich
     * zurücknehmen lässt.
     */
    public static final int INVALID_POS = -1;

    /**
     * Position der Transaktion in <code>trans_q</code>, die sich per Undo als
     * nächstes zurücknehmen lässt.
     */
    private int cur_pos;

    /**
     * Index der letzten Transaktion in <code>trans_q</code> = Anzahl der
     * Elemente in <code>trans_q</code> -1
     */
    private int last_pos;

    /**
     * Unspezifische Prozess-ID. Mit dieser PID ist immer die letzte Transaktion
     * gemeint.
     */
    public static final int UNSPECIFIC_PID = -1;

    /**
     * Process ID for all processes that do not need a special PID of their own.
     * Only the dialogs should not use this PID here, but create an PID by
     * {@link GraphDocument#createTransactionId()}. All other functions should
     * always use this PID, because only then the function
     * {@link #isDeepInTransaction()} works correctly.
     */
    public static final int STANDARD_PID = 0;

    /**
     * Flag, das nur <code>true</code> ist während ein Undo oder Redo ausgeführt
     * wird. Über dieses Flag wird gesteuert, dass der Transaktionsmanager das
     * Zurückrollen einer Aktion erkennt und die Aktion nicht noch einmal in die
     * Transaktionsliste aufgenommen wird.
     */
    private boolean is_doing;

    /**
     *
     */
    public TransactionManager(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
        cur_pos = last_pos = INVALID_POS;
        trans_q = new Transaction[TRANSQ_SIZE];
        clearTransactionQueue();
    }

    /**
     *
     */
    public final void clearTransactionQueue() {
        if (is_doing) {
            return;
        }
        for (int i = 0; i < TRANSQ_SIZE; i++) {
            trans_q[i] = null;
        }
        cur_pos = last_pos = INVALID_POS;
        is_doing = false;
    }

    /**
     * @param pid
     * @param doc
     */
    private void startTransaction(final int pid, final GraphDocument doc) {
        if (is_doing) {
            return;
        }
        int j = getTransactionIndexForPID(pid, true);

        if (j >= 0 && trans_q[j].isOpen()) {
            trans_q[j].open();
            return;
        }
        if (cur_pos == TRANSQ_SIZE - 1) {
            System.arraycopy(trans_q, 1, trans_q, 0, TRANSQ_SIZE - 1);
            trans_q[TRANSQ_SIZE - 1] = null;
            cur_pos--;
        }
        cur_pos++;
        if (doc.isVerificationMode()) {
            System.out.println("Neue Transaktion an Position " + cur_pos);
        }
        Arrays.fill(trans_q, cur_pos + 1, TRANSQ_SIZE, null);
        trans_q[cur_pos] = new Transaction(pid, doc);
        last_pos = cur_pos;
        trans_q[cur_pos].open();
    }

    /**
     * @param undoCommand
     * @param redoCommand
     * @param pid
     * @param doc
     */
    public final void startTransaction(final String undoCommand, final String redoCommand, final int pid, final GraphDocument doc) {
        if (is_doing) {
            return;
        }
        startTransaction(pid, doc);
        addRedoCommand(pid, undoCommand);
        addUndoCommand(pid, redoCommand);
        if (doc.isVerificationMode()) {
            System.out.println("log :  " + undoCommand);
            System.out.println("ulog:  " + redoCommand);
        }
        for (TransactionListener listener : transactionListeners) {
            listener.transactionStarted();
        }
    }

    /**
     * Prüft, ob das übergebene Kommando als Undo oder Redo-Kommando hinzugefügt
     * werden kann.
     *
     * @param command
     * @return Das getrimmte Kommando, wenn es ein valides ist, oder
     *         <code>null</code> sonst.
     */
    private String getValidCommand(String command) {
        if (is_doing) {
            return null;
        }
        if (command == null) {
            return null;
        }
        command = command.trim();
        if (command.equals("")) {
            return null;
        }
        return command;
    }

    /**
     * Fügt ein neues Kommando als Undo- oder Redo-Kommando hinzu.
     *
     * @param pid ID der Transaktion
     * @param command hinzuzufügendes Kommando
     * @param undo Wenn <code>true</code> wird das Kommando als Undo-Kommando
     *            hinzugefügt, sonst als Redo-Kommando
     */
    private final void addUndoOrRedoCommand(final int pid, String command, final boolean undo) {
        command = getValidCommand(command);
        if (command == null) {
            return;
        }
        int j = getTransactionIndexForPID(pid, true);
        if (j >= 0) {
            if (undo) {
                trans_q[j].addUndoCommand(command);
            } else {
                trans_q[j].addRedoCommand(command);
            }
        }
    }

    /**
     * Fügt der Transaktion mit der angegebenen ID das neue Redo-Kommando hinzu.
     * Die Kommandos werden beim Aufruf der Funktion <code>redo(int)</code> in
     * der gleichen Reihenfolge erneut ausgeführt, in der sie hier hinztugefügt
     * wurden.
     *
     * @param pid
     * @param command
     */
    public final void addRedoCommand(final int pid, final String command) {
        addUndoOrRedoCommand(pid, command, false);
    }

    /**
     * Fügt der Transaktion mit der angegebenen ID das neue Undo-Kommando hinzu.
     * Die Kommandos werden beim Aufruf der Funktion <code>redo(int)</code> in
     * der gleichen Reihenfolge erneut ausgeführt, in der sie hier hinztugefügt
     * wurden.
     *
     * @param pid
     * @param command
     */
    public final void addUndoCommand(final int pid, final String command) {
        addUndoOrRedoCommand(pid, command, true);
    }

    /**
     * Löscht das letzte Redo-Kommando, das mit dem <code>commadPrefix</code>
     * beginnt und fürgt ein neues Kommando mit dem <code>commadPrefix</code>
     * und den <code>commandArguments</code> am Ende an. <br />
     * Diese Funktion ist gedacht, um z. B. bei Verschiebeoperationen nicht
     * jeden Zwischenschritt zu speichern. Für das Redo benötigt man immer nur
     * die letzte Verschiebeoperation, da sie den endgültigen Ort und Größe
     * eines Elementes eindeutig bestimmt.
     *
     * @param pid
     * @param commandPrefix
     * @param commandArguments
     */
    public void addOrReplaceRedoCommand(final int pid, String commandPrefix, String commandArguments) {
        commandPrefix = getValidCommand(commandPrefix);
        if (commandPrefix == null) {
            return;
        }
        commandArguments = commandArguments.trim();
        int j = getTransactionIndexForPID(pid, true);
        if (j >= 0) {
            trans_q[j].addOrReplaceRedoCommand(commandPrefix, commandArguments);
        }
    }

    /**
     * Logt ein Undo-Kommando nur, wenn nicht schon ein Undo-Kommando mit
     * demselben <code>commandPrefix</code> in dieser Transaktion vorkommt.
     * Diese Funktion ist gedacht, um z. B. bei Verschiebeoperationen nicht
     * jeden Zwischenschritt zu speichern. Für das Undo benötigt man immer nur
     * das erste Undo-Kommando, da sie den Ausgangs-Ort und -Größe eines
     * Elementes eindeutig bestimmt.
     *
     * @param pid
     * @param commandPrefix
     * @param commandArguments
     */
    public final void addUndoCommandIfNotExist(final int pid, String commandPrefix, String commandArguments) {
        commandPrefix = getValidCommand(commandPrefix);
        if (commandPrefix == null) {
            return;
        }
        commandArguments = commandArguments.trim();
        int j = getTransactionIndexForPID(pid, true);
        if (j >= 0) {
            trans_q[j].addUndoCommandIfNotExist(commandPrefix, commandArguments);
        }
    }

    /**
     * @param index
     */
    private final void destroyTransaction(final int index) {
        if (is_doing) {
            return;
        }
        // wenn alles richtig läuft, sollte das mit den Indizes hier immer
        // stimmen und nie ein Index kleiner
        // 0 oder größer SIZE auftauchen
        System.arraycopy(trans_q, index + 1, trans_q, index, last_pos - index);
        trans_q[last_pos--] = null;
    }

    /**
     * @param pid
     */
    public final void finishTransaction(final int pid) {
        if (is_doing) {
            return;
        }
        int j = getTransactionIndexForPID(pid, true);
        if (j == INVALID_POS) {
            return;
        }
        GraphDocument doc = trans_q[j].getGraphDocument();
        trans_q[j].close();
        if (!trans_q[j].isOpen() && trans_q[j].isUseless()) {
            destroyTransaction(j);
            cur_pos--;
        }
        if (doc.isVerificationMode()) {
            printQueue(10);
        }
        for (TransactionListener listener : transactionListeners) {
            listener.transactionStopped();
        }
    }

    /**
     * @param pid
     * @param string
     */
    public final void addPreSelectionItem(final int pid, final String string) {
        if (is_doing) {
            return;
        }
        int j = getTransactionIndexForPID(pid, true);
        if (j != INVALID_POS) {
            //			System.err.println("addPreSelectionItem " + string);
            trans_q[j].addPreSelectionItem(string);
        }
    }

    /**
     * @param pid
     * @param string
     */
    public final void addPostSelectionItem(final int pid, final String string) {
        if (is_doing) {
            return;
        }
        int j = getTransactionIndexForPID(pid, true);
        if (j != INVALID_POS) {
            trans_q[j].addPostSelectionItem(string);
        }
    }

    /**
     * @return
     */
    public final boolean isUndoAvailable() {
        if (cur_pos == INVALID_POS) {
            return false;
        }
        // wenn irgendeine Transaktion noch nicht beendet ist -> kein Undo mgl.
        for (int i = cur_pos; i >= 0; i--) {
            if (trans_q[i].isOpen()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return
     */
    public final boolean isRedoAvailable() {
        if (cur_pos == TRANSQ_SIZE - 1) {
            return false;
        }
        if (cur_pos < TRANSQ_SIZE && trans_q[cur_pos + 1] == null) {
            return false;
        }
        // wenn irgendeine Transaktion noch nicht beendet ist -> kein Redo mgl.
        for (int i = cur_pos; i >= 0; i--) {
            if (trans_q[i].isOpen()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Liefert den Index der Transaktion in der Transaktionsliste
     * <code>trans_q</code>, deren ID der übergebenen <code>pid</code>
     * entspricht.
     *
     * @param pid ID der Transaktion, die gesucht werden soll. Wird
     *            <code>UNSPECIFIC_PID</code> übergeben, kommt der Index der
     *            letzten Transaktion zurück.
     * @param undo bei <code>true</code> werden inklusive des aktuellen
     *            Transaktionsindex auch die davor liegenden Transaktionen
     *            geprüft, bei <code>false</code> alle dahinter leigenden
     *            Transaktionen
     * @return Index der Transaktion oder <code>INVALID_POS</code>
     */
    private int getTransactionIndexForPID(final int pid, final boolean undo) {
        if (pid == UNSPECIFIC_PID) {
            return cur_pos;
        }

        if (undo) {
            for (int j = cur_pos; j >= 0 && j <= last_pos && trans_q[j] != null; j--) {
                if (trans_q[j].getProcessID() == pid) {
                    return j;
                }
            }
        } else {
            for (int j = cur_pos + 1; j >= 0 && j <= last_pos && trans_q[j] != null; j++) {
                if (trans_q[j].getProcessID() == pid) {
                    return j;
                }
            }
        }
        return INVALID_POS;
    }

    /**
     * Nimmt die Transaktion mit der angegebenen ID zurück. Alle Undo-Kommandos
     * der Transaktion werden in der gleichen Reihenfolge ausgeführt, in der sie
     * hinzugefügt wurden.
     *
     * @param pid
     * @return
     */
    public final boolean undo(final int pid) {
        if (is_doing) {
            return true;
        }
        if (!isUndoAvailable()) {
            return false;
        }

        int j = cur_pos;
        j = getTransactionIndexForPID(pid, true);
        if (j < 0) {
            return false;
        }

        GraphDocument doc = trans_q[j].getGraphDocument();
        if (doc.isVerificationMode()) {
            System.out.println("transaction-index: " + j);
        }
        GDCollection gdcoll = doc.getCollection();
        boolean bulkMode = gdcoll.setBulkMode(true);

        is_doing = true;

        doc.deselectAll(true);
        for (int k = 0; k < trans_q[j].getPostSelectionSize(); k++) {
            String postSelectionItem = trans_q[j].getPostSelectionItem(k);
            doc.addToSelection(postSelectionItem, pid);
        }

        int transactions = trans_q[j].getUndoSize();
        boolean showProgressDialog = transactions > 1000;
        if (showProgressDialog) {
            Static.showProgressDialog();
        }

        for (int i2 = transactions - 1; i2 >= 0; i2--) {
            // die Kommandos werden immer im Hauptdokument ausgführt! Alle Transaktionen, die
            // ein Teilmodell betreffen (Layout-Änderungen, Entfernen  Hinzufügen von Elemente usw.)
            // müssen immer die ID des Szenarios beachten, für das sie ausgeführt werden sollen
            String undoCommand = trans_q[j].getUndoCommand(i2);
            LGMGraphDocument mainDoc = gdcoll.getMainDoc();
            mainDoc.exec(undoCommand, pid);
            if (showProgressDialog) {
                Static.setProgressDialogStatusLabel("undo", transactions - i2 + " / " + transactions);
            }
        }
        doc.deselectAll(true);
        for (int j2 = 0; j2 < trans_q[j].getPreSelectionSize(); j2++) {
            String preSelectionItem = trans_q[j].getPreSelectionItem(j2);
            doc.addToSelection(preSelectionItem, pid);
        }

        is_doing = false;
        if (trans_q[j].isOpen()) {
            destroyTransaction(j);
        }
        cur_pos--;

        if (doc.isVerificationMode()) {
            printQueue(10);
        }

        gdcoll.setBulkMode(bulkMode);

        if (showProgressDialog) {
            Static.closeProgressDialog();
        }
        return true;
    }

    /**
     * Führt die Transaktion mit der angegebenen ID erneut aus. Alle
     * Redo-Kommandos der Transaktion werden in der gleichen Reihenfolge
     * ausgeführt, in der sie hinzugefügt wurden.
     *
     * @param pid
     * @return
     */
    public final boolean redo(final int pid) {
        if (is_doing) {
            return true;
        }
        if (!isRedoAvailable()) {
            return true;
        }
        boolean bulkMode = gdcoll.setBulkMode(true);
        is_doing = true;
        cur_pos++;
        int j = getTransactionIndexForPID(pid, false);
        GraphDocument doc = trans_q[j].getGraphDocument();
        GDCollection gdcoll = doc.getCollection();
        boolean lastAutomaticMode = gdcoll.setAutomaticMode(true);
        doc.deselectAll(true);
        for (int k = 0; k < trans_q[j].getPreSelectionSize(); k++) {
            doc.addToSelection(trans_q[j].getPreSelectionItem(k), pid);
        }
        for (int i2 = 0; i2 < trans_q[j].getRedoSize(); i2++) {
            //			System.err.println("12345AXS " + trans_q[j].getRedoCommand(i2));
            String redoCommand = trans_q[j].getRedoCommand(i2);
            doc.exec(redoCommand, pid);
        }
        doc.deselectAll(true);
        for (int j2 = 0; j2 < trans_q[j].getPostSelectionSize(); j2++) {
            doc.addToSelection(trans_q[j].getPostSelectionItem(j2), pid);
        }
        gdcoll.setAutomaticMode(lastAutomaticMode);
        is_doing = false;
        if (doc.isVerificationMode()) {
            printQueue(10);
        }
        gdcoll.setBulkMode(bulkMode);
        return true;
    }

    /**
     * Scheibt <code>entryCount</code> Elemente der Transaktionsliste in die
     * Standardausgabe.
     *
     * @param entryCount Anzahl der Elemente ab dem ersten, die ausgegeben
     *            werden sollen
     */
    public final void printQueue(final int entryCount) {
        System.out.println(getQueue(entryCount));
    }

    /**
     * Liefert eine lesbare Ausgabe der ersten <code>entryCount</code> Einträge
     * der Transaktionsliste <code>trans_q</code>.
     *
     * @param entryCount Anzahl der Elemente ab dem ersten, die ausgegeben
     *            werden sollen
     * @return lesbaren String des Transaktionsstacks
     */
    public final String getQueue(final int entryCount) {
        StringBuilder sb = new StringBuilder("Current transactions of: ");
        sb.append(gdcoll);
        sb.append("\n");
        int i = 0;
        int j = 0;
        for (i = 0; i < TRANSQ_SIZE && j < entryCount; i++) {
            if (trans_q[i] != null) {
                sb.append("(");
                sb.append(i);
                sb.append(")\t");
                sb.append(trans_q[i]);
                sb.append(" (PID ");
                sb.append(trans_q[i].getProcessID());
                sb.append(", ");
                sb.append(trans_q[i].isOpen() ? "open" : "closed");
                sb.append(")\n");
                //            } else {
                //                sb.append("(");
                //                sb.append(i);
                //                sb.append(")\t--- nicht belegt ---\n");
                //                j++;
                if (i == cur_pos) {
                    sb.append("(current undo action)");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * @param listener
     */
    public void addTransActionListener(final TransactionListener listener) {
        transactionListeners.add(listener);
    }

    /**
     * @param listener
     */
    public void removeTransActionListener(final TransactionListener listener) {
        transactionListeners.remove(listener);
    }

    /**
     * @return <code>true</code>, if a transaction with the
     *         {@link #STANDARD_PID} is open or a transaction with another PID,
     *         but which then started at least one other inner transaction.
     *         Normally only dialogs do not have the {@link #STANDARD_PID}. So
     *         this function determines if a real change is happening or if only
     *         one dialog is keeping a transaction open but is not doing
     *         anything.
     */
    public final boolean isDeepInTransaction() {
        if (is_doing) {
            return true;
        }
        for (int i = 0; i < TRANSQ_SIZE; i++) {
            if (trans_q[i] == null) {
                return false;
            } else if (trans_q[i].isOpenStandardPidTransaction() || trans_q[i].isOpenDialogTransaction()) {
                return true;
            }
        }
        return false;
    }

}