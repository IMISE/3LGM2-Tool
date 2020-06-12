package de.imise.util.transaction;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.Vector;

/**
 * Die Transaction-Manager ist eine Implementierung des {@link AbstractTransactionManager} auf der Basis
 * umkehrbarer Funtionen - den {@link InvertibleFunction}s.
 * <p>
 * Das Prinzip hierbei ist, dass Änderungen des Systemzustandes nicht über das Speichern der jeweils
 * veränderten Werte erfolgt, sondern durch das Festhalten der Aktionen, die eben diese Änderungen
 * ausgelöst haben. <br>
 * Der entscheidende Vorteil bei dieser Herangehensweise ist, dass sich der Transaction-Manager nicht
 * mit Typen, Formaten, etc. der betreffenden Werte auseinandersetzen muss. Klassen, die diesen
 * Transaction-Manager verwenden, können selbst entscheiden, wie Werte verändert werden und was beim
 * Durchführen von UNDO und REDO auf der Ebene der {@link InvertibleFunction}s geschehen soll. <br>
 * Darüberhinaus ist es möglich, {@link FunctionalTransactionManager} geschachtelt zu verwenden, indem
 * untergeordnete Instanzen, selbst wieder in {@link InvertibleFunction}s eingebunden werden. Etwa bei
 * hierarchisch aufgebauten {@link Component}s ist es möglich, den einzelnen Komponenten eigene auf sie
 * zugeschnittene Transaction-Manager zuzuweisen, auf welche dann der Transaction-Manager der
 * übergeordeten Komponente zugreifen kann.<br>
 * Welche Funktionen des {@link FunctionalTransactionManager} dabei zugänglich gemacht werden, kann
 * individuell entschieden werden. Hierzu wurden alle kritischen Methoden auf <code>protected</code>
 * gesetzt.
 * <p>
 * Zur Anwendung des {@link FunctionalTransactionManager} sollte eine von ihm abgeleitete Klasse
 * erstellt werden, welche dann durch das Anpassen der Sichtbarkeit der Methoden die lokalen
 * Zugriffsrechte steuern kann.
 * <p>
 * Sind alle {@link InvertibleFunction}s korrekt definiert, garantiert der {@link FunctionalTransactionManager}
 * die Atomarität und Konsistenzerhaltung für die jeweiligen Transaktionen. <br>
 * <i>Achtung</i>: Dieser Transaction-Manager ist nicht thread-sicher.
 * <p>
 * <b>Code-Beispiel:</b>
 *
 * <pre>
 * 		oldValue = value;
 *
 * 		InvertibleFunction() f = new InvertibleFunction() {

                public void execute() throws RuntimeException {
					value = newValue;
                }

                public void invert() throws RuntimeException {
					value = oldValue;
                }
			};

		// Öffnet die Transaktion
		transactionManager.openTransaction();

		// Setzt die Aktionen für die Transaktion
		transactionManager.fillTransaction(f);

		// Führt durch und beendet die Transaktion
		// Danach ist erst UNDO und REDO möglich
		transactionManager.doTransaction();
 * </pre>
 *
 * @see AbstractTransactionManager
 * @see InvertibleFunction
 * @see TransactionSupport
 * @author fstephan
 */
public class FunctionalTransactionManager extends AbstractTransactionManager {

    /**
     * Der UNDO Stack.
     * <p>
     * Beinhaltet alle bisher durchgeführten Transaktionen.<br>
     * Beim Ausführen von {@link #undo()} wird die letzte Transaktion
     * in den {@link #redoStack} verschoben.
     */
    protected Stack<Transaction> undoStack = new Stack<>();

    /**
     * Der REDO Stack.
     * <p>
     * Beinhaltet alle rückgängig gemachten Transaktionen.<br>
     * Beim Ausführen von {@link #redo()} wird die letzte Transaktion
     * wieder in den {@link #undoStack} verschoben.
     */
    protected Stack<Transaction> redoStack = new Stack<>();

    // Dient der Speicherung des letzten REDO-Stacks, beim Abbrechen einer Transaktion
    private transient Stack<Transaction> redoStack_old = new Stack<>();

    /** Die momentan geöffnete Transaktion */
    protected Transaction currentTransaction = null;

    /** Gibt wieder, ob UNDO verfügbar ist */
    protected boolean isUndoAvailable = false;

    /** Gibt wieder, ob REDO verfügbar ist */
    protected boolean isRedoAvailable = false;

    /**
     * Erzeugt einen neuen Transaction-Manager.
     *
     * @param undoStackSize
     *            Maximale Anzahl zu speichernder UNDO-Schritte
     * @param redoStackSize
     *            Maximale Anzahl zu speichernder REDO-Schritte
     */
    public FunctionalTransactionManager(final int undoStackSize, final int redoStackSize) {
        super();
        try {
            Method m = getClass().getMethod("createUndoStack", Integer.TYPE);
            undoStack = (Stack<Transaction>) m.invoke(this, undoStackSize);
        } catch (SecurityException e) {
            // Wird niemals passieren
            throw new InternalError();
        } catch (NoSuchMethodException e) {
            // Passiert immer dann, wenn eine erbende Klasse die
            // createUndoStack-Methode nicht überschreibt
            undoStack = createUndoStack(undoStackSize);
        } catch (IllegalArgumentException e) {
            // Wird niemals passieren
            throw new InternalError();
        } catch (IllegalAccessException e) {
            // Wird niemals passieren
            throw new InternalError();
        } catch (InvocationTargetException e) {
            // Wird niemals passieren
            throw new InternalError();
        }

        try {
            Method m = getClass().getMethod("createRedoStack", Integer.TYPE);
            undoStack = (Stack<Transaction>) m.invoke(this, undoStackSize);
        } catch (SecurityException e) {
            // Wird niemals passieren
            throw new InternalError();
        } catch (NoSuchMethodException e) {
            // Passiert immer dann, wenn eine erbende Klasse die
            // createRedoStack-Methode nicht überschreibt
            redoStack = createRedoStack(redoStackSize);
        } catch (IllegalArgumentException e) {
            // Wird niemals passieren
            throw new InternalError();
        } catch (IllegalAccessException e) {
            // Wird niemals passieren
            throw new InternalError();
        } catch (InvocationTargetException e) {
            // Wird niemals passieren
            throw new InternalError();
        }
    }

    /**
     * Erzeugt einen neuen Transaction-Manager.
     *
     * @param stackSize
     *            Maximale Anzahl zu speichernder UNDO-Schritte und REDO-Schritte
     */
    public FunctionalTransactionManager(final int stackSize) {
        this(stackSize, stackSize);
    }

    /**
     * Erzeugt einen neuen Transaction-Manager mit theoretisch unbegrenzter Anzahl
     * speicherbarer UNDO- und REDO-Schritt.
     */
    public FunctionalTransactionManager() {
        this(-1);
    }

    /**
     * Erzeugt den UNDO-Stack.
     * <p>
     * Erbende Klassen können diese Methode "überschreiben" um einen individuellen
     * Stack zu erzeugen.
     *
     * @param maxSize
     *            Maximale Größe des UNDO-Stack
     * @return Begrenzter Stack für <code>maxSize > 0</code>; unbegrenzter Stack für <code>maxSize == -1</code>
     */
    protected static Stack<Transaction> createUndoStack(final int maxSize) {
        return createStack(maxSize);
    }

    /**
     * Erzeugt den REDO-Stack.
     * <p>
     * Erbende Klassen können diese Methode "überschreiben" um einen individuellen
     * Stack zu erzeugen.
     *
     * @param maxSize
     *            Maximale Größe des REDO-Stack
     * @return Begrenzter Stack für <code>maxSize > 0</code>; unbegrenzter Stack für <code>maxSize == -1</code>
     */
    protected static Stack<Transaction> createRedoStack(final int maxSize) {
        return createStack(maxSize);
    }

    private static Stack<Transaction> createStack(final int maxSize) {
        Stack<Transaction> stack;
        if (maxSize <= 0) {
            stack = new Stack<>();
        } else {
            stack = new Stack<Transaction>() {

                /*
                 * (non-Javadoc)
                 * @see java.util.Stack#push(java.lang.Object)
                 */
                @Override
                public Transaction push(final Transaction item) {
                    if (size() == maxSize) {
                        remove(size() - 1);
                    }
                    return super.push(item);
                }
            };
        }
        stack.ensureCapacity(maxSize);
        return stack;
    }

    /**
     * Öffnet eine neue Transaktion
     */
    public void openTransaction() throws TransactionManagerException {
        if (hasOpenTransaction()) {
            throw TransactionManagerException.alreadyOpenTransaction();
        }
        redoStack_old = (Stack<Transaction>) redoStack.clone();
        redoStack.clear();
        currentTransaction = new Transaction();
        fireTransactionOpened();
    }

    /**
     * Gibt wieder, ob im Moment eine Transaktion geöffnet ist
     */
    public boolean hasOpenTransaction() {
        return currentTransaction != null;
    }

    /**
     * Ausführen einer Transaktion, die nur aus einer {@link InvertibleFunction}
     * besteht.<br>
     * Hierzu wird eine neue Transaktion geöffnet, die Function angefügt und
     * danach die Transaktion ausgeführt.
     */
    public void fastTransaction(final InvertibleFunction action) throws TransactionManagerException {
        openTransaction();
        fillTransaction(action);
        doTransaction();
    }

    /**
     * Bricht die aktuelle geöffnet Transaktion ab.
     */
    public void abortTransaction() throws TransactionManagerException {
        if (!hasOpenTransaction()) {
            throw TransactionManagerException.noOpenTransaction();
        }
        currentTransaction = null;
        redoStack = (Stack<Transaction>) redoStack_old.clone();
        checkUndoRedo();
        fireTransactionAborted();
    }

    /**
     * Fügt die übergebene Funktion an die aktuell offene Transaktion an.<br>
     * <i>Hinweis:</i> Das Aufrufen der Methoden {@link InvertibleFunction#execute()} oder
     * {@link InvertibleFunction#invert()} erfolgt intern im {@link FunctionalTransactionManager}
     * und darf nicht manuell erfolgen.
     *
     * @param action
     */
    public void fillTransaction(final InvertibleFunction action) {
        if (!hasOpenTransaction()) {
            throw TransactionManagerException.noOpenTransaction();
        }
        currentTransaction.add(action);
    }

    /**
     * Löst das Ausführen der aktuellen Transaktion aus.<br>
     * Dabei werden alle vorher über {@link #fillTransaction(InvertibleFunction)} angefügten
     * {@link InvertibleFunction}s der Reihe nach ausgeführt.<br>
     * Das Aufrufen dieser Methode schließt die aktuelle Transaktion, sodass nun über
     * {@link #openTransaction()} wieder neue geöffnet werden können.
     */
    public void doTransaction() throws TransactionManagerException {
        if (!hasOpenTransaction()) {
            throw TransactionManagerException.noOpenTransaction();
        }
        currentTransaction.execute();
        undoStack.add(currentTransaction);
        currentTransaction = null;
        checkUndoRedo();
        fireTransactionDone();
    }

    /*
     * (non-Javadoc)
     * @see tool3lgm.util.transaction.AbstractTransactionManager#isUndoAvailable()
     */
    @Override
    public boolean isUndoAvailable() {
        return isUndoAvailable;
    }

    /*
     * (non-Javadoc)
     * @see tool3lgm.util.transaction.AbstractTransactionManager#isRedoAvailable()
     */
    @Override
    public boolean isRedoAvailable() {
        return isRedoAvailable;
    }

    private void checkUndoRedo() {
        if (undoStack.empty() && isUndoAvailable) {
            isUndoAvailable = false;
            fireUndoAvailable(Boolean.TRUE, Boolean.FALSE);
        } else if (!undoStack.empty() && !isUndoAvailable) {
            isUndoAvailable = true;
            fireUndoAvailable(Boolean.FALSE, Boolean.TRUE);
        }

        if (redoStack.empty() && isRedoAvailable) {
            isRedoAvailable = false;
            fireRedoAvailable(Boolean.TRUE, Boolean.FALSE);
        } else if (!redoStack.empty() && !isRedoAvailable) {
            isRedoAvailable = true;
            fireRedoAvailable(Boolean.FALSE, Boolean.TRUE);
        }
    }

    /*
     * (non-Javadoc)
     * @see tool3lgm.util.transaction.AbstractTransactionManager#undo()
     */
    @Override
    public boolean undo() {
        if (hasOpenTransaction()) {
            throw TransactionManagerException.currentTransactionNotExecuted();
        }
        if (!isUndoAvailable()) {
            throw TransactionManagerException.undoAlreadyPerformed();
        }
        Transaction t = undoStack.peek();
        if (t.revert()) {
            undoStack.pop();
            redoStack.push(t);
            checkUndoRedo();
            fireUndoPerformed();
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see tool3lgm.util.transaction.AbstractTransactionManager#redo()
     */
    @Override
    public boolean redo() {
        if (hasOpenTransaction()) {
            throw TransactionManagerException.currentTransactionNotExecuted();
        }
        if (!isRedoAvailable()) {
            throw TransactionManagerException.redoAlreadyPerformed();
        }
        Transaction t = redoStack.peek();
        if (t.execute()) {
            redoStack.pop();
            undoStack.push(t);
            checkUndoRedo();
            fireRedoPerformed();
            return true;
        }
        return false;
    }

    /**
     * Eine {@link Transaction} ist eine Folge von {@link InvertibleFunction}s, deren
     * sukzessive Ausführung die durch diese Transaktion beschriebene Änderung des
     * Systemzustandes bewirkt. <br>
     * Diese Änderungen sind ausschließlich in den {@link InvertibleFunction}s selbst
     * definiert. Die Transaktion sorgt dann für deren geordnetes Ausführen und kontrolliert
     * UNDO und REDO.
     *
     * @see InvertibleFunction
     */
    class Transaction extends Vector<InvertibleFunction> {

        private boolean isExecutable = true;

        /**
         * Führt alle enthaltenen {@link InvertibleFunction}s der Reihe nach aus.
         * <p>
         * Tritt dabei ein Fehler auf, so wird diese Transaktion versuchen, die bereits
         * gemachten Änderungen rückgängig zu machen, sodass der ursprüngliche Systemzustand
         * wieder erreicht wird. Dazu wird jeweils die {@link InvertibleFunction#invert()}-
         * Methode benutzt. Kommt es dabei erneut zu einem Fehler, befindet sich das System
         * möglicherweise in einem ungültigen Zustand und muss beendet werden.
         * <p>
         *
         * @return <code>true</code>, wenn Ausführung erfolgreich;
         *         <code>false</code>, sonst
         * @throws TransactionManagerException
         *             Wird geworfen, wenn das Durchführen der Transaktion nicht möglich war,
         *             das System aber wieder im gültigen Ausgangszustand ist.
         */
        public boolean execute() throws TransactionManagerException {
            if (!isExecutable) {
                return false;
            }
            for (int i = 0; i < size(); i++) {
                try {
                    get(i).execute();
                    // Wenn beim Ausführen der Transaktionen Fehler auftreteten,
                    // wird versucht diese rückgängig zu machen
                } catch (RuntimeException e1) {
                    for (; i >= 0; i--) {
                        try {
                            get(i).invert();
                            // Kann die fehlerhafte Transaktion nicht rückgängig gemacht werden
                            // befindet sich das System jetzt in einem ungültigen Zustand
                        } catch (RuntimeException e2) {
                            throw new Error("Fehlerhafte Transaktion konnte nicht rückgängig gemacht werden.\n" + "Das Sytsem befindet sich eventuell in einem ungültigen Zustand", e2);
                        } /*
                           * finally {
                           * throw TransactionManagerException.transactionNotProcessable(e1);
                           * }
                           */

                    }
                }
            }
            isExecutable = false;
            return true;
        }

        /**
         * Macht die durch diese Transaktion ausgelösten Änderungen rückgängig.
         * <p>
         * Dazu wird die {@link InvertibleFunction#invert()}-Methode verwendet. Kommt es
         * dabei zu Fehlern, wird versucht die bereits rückgängig gemachten Änderungen
         * wiederherzustellen. Dies erfolgt dann wieder über die
         * {@link InvertibleFunction#execute()}- Methode. Treten dabei erneut Fehler auf,
         * befindet sich das System möglicherweise in einem ungültigen Zustand und muss
         * beendet werden.
         * <p>
         * Anmerkung:<br>
         * Der Aufruf dieser Methode kann nur nach dem Ausführen von {@link #execute()}
         * erfolgen.
         *
         * @return <code>true</code>, wenn Rückgängigmachen erfolgreich;
         *         <code>false</code>, sonst
         * @throws TransactionManagerException
         *             Wird geworfen, wenn das Rückgängigmachen der Transaktion nicht möglich war,
         *             das System aber wieder im gültigen Zustand - wie es nach dem Durchführen von
         *             {@link #execute()} war - ist.
         */
        public boolean revert() throws TransactionManagerException {
            if (isExecutable) {
                return false;
            }

            for (int i = size() - 1; i >= 0; i--) {
                try {
                    // Wenn beim Rückgängigmachen der Transaktionen Fehler auftreteten,
                    // wird versucht die gemachten Änderungen wiederherzustellen
                    get(i).invert();
                } catch (RuntimeException e1) {
                    for (; i < size(); i++) {
                        try {
                            get(i).execute();
                            // Können die die gemachten Änderungen nicht wiederhergestellt werden
                            // befindet sich das System jetzt in einem ungültigen Zustand
                        } catch (RuntimeException e2) {
                            throw new Error("Die Transaktion konnte nicht rückgängig gemacht werden.\n" + "Das Sytsem befindet sich eventuell in einem ungültigen Zustand", e2);
                        } /*
                           * finally {
                           * throw TransactionManagerException.transactionNotProcessable(e1);
                           * }
                           */
                    }
                }
            }
            isExecutable = true;
            return true;
        }
    }
}
