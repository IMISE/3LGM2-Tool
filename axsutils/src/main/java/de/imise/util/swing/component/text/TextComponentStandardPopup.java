package de.imise.util.swing.component.text;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import de.imise.util.SimpleResourceHandler;

/**
 * @author AXS, STKR
 * @create 03.07.2012
 */
public class TextComponentStandardPopup extends JPopupMenu implements MouseListener {

    private static final int MENU_SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    /**
     * Komponente, für das das Menü angezeigt werden soll
     */
    private JTextComponent myTargetComponent = null;

    /**
     * Wird <code>true</code>, wenn das Menü initilisiert wurde (Einmalig vor dem ersten anzeigen)
     */
    private boolean initialized = false;

    /**
     * Soll die wird die Erweiterung angezeigt Suche/Ersetzen angezeigt werden?
     */
    private boolean extensionFindReplace = false;

    // Merke die letzte Position des Suche/Ersetzen Items, Suche/Ersetzen soll dort drunter angezeigt werden
    private int findReplaceXPos = 0;
    private int findReplaceYPos = 0;

    /**
     * @param textComponent
     * @param extensionFindReplace
     */

    public TextComponentStandardPopup(final JTextComponent textComponent, final boolean extensionFindReplace) {
        super();
        myTargetComponent = textComponent;
        this.extensionFindReplace = extensionFindReplace;
        textComponent.addMouseListener(this);
    }

    /**
     * Initialisiert das Menü
     *
     * @param textComponent
     */
    private void init(final JTextComponent textComponent) {
        final SimpleResourceHandler resHandler = new SimpleResourceHandler(TextComponentStandardPopup.class);

        final String cutString = resHandler.getString("TOOLS_CONTEXTMENU_CUT");
        final JMenuItem cutIt = new JMenuItem(cutString);
        cutIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU_SHORTCUT_KEY_MASK));
        cutIt.setAction(new AbstractAction(cutString) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (e.getActionCommand().equals(cutString)) {
                    textComponent.cut();
                }
            }

            @Override
            public boolean isEnabled() {
                // enabled wenn es eine Selketion im Text gibt
                return textComponent.isEditable() && textComponent.isEnabled() && textComponent.getSelectionStart() < textComponent.getSelectionEnd();
            }

        });

        final String pasteString = resHandler.getString("TOOLS_CONTEXTMENU_PASTE");
        final JMenuItem pasteIt = new JMenuItem(pasteString);
        pasteIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, MENU_SHORTCUT_KEY_MASK));
        pasteIt.setAction(new AbstractAction(pasteString) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (e.getActionCommand().equals(pasteString)) {
                    textComponent.paste();
                }
            }

            @Override
            public boolean isEnabled() {
                // enabled wenn Text in der Zwischenablage ist
                return textComponent.isEditable() && textComponent.isEnabled() && getFromClipboard() != null;
            }

        });

        final String deleteString = resHandler.getString("TOOLS_CONTEXTMENU_DELETE_ALL");
        final JMenuItem deleteIt = new JMenuItem(deleteString);
        deleteIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, MENU_SHORTCUT_KEY_MASK));
        deleteIt.setAction(new AbstractAction(deleteString) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (e.getActionCommand().equals(deleteString)) {
                    textComponent.setText("");
                }
            }

            @Override
            public boolean isEnabled() {
                // enabled wenn es Text gibt
                return textComponent.isEditable() && textComponent.isEnabled() && textComponent.getText().length() != 0;
            }

        });

        final String copyString = resHandler.getString("TOOLS_CONTEXTMENU_COPY");
        final JMenuItem copyIt = new JMenuItem(copyString);
        copyIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, MENU_SHORTCUT_KEY_MASK));
        copyIt.setAction(new AbstractAction(copyString) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (e.getActionCommand().equals(copyString)) {
                    textComponent.copy();
                }
            }

            @Override
            public boolean isEnabled() {
                // enabled wenn es eine Selketion im Text gibt
                return textComponent.getSelectionStart() != textComponent.getSelectionEnd();
            }

        });

        final String selectAllString = resHandler.getString("TOOLS_CONTEXTMENU_SELECT_ALL");
        final JMenuItem selectAllIt = new JMenuItem(selectAllString);
        selectAllIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, MENU_SHORTCUT_KEY_MASK));
        selectAllIt.setAction(new AbstractAction(selectAllString) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (e.getActionCommand().equals(selectAllString)) {
                    textComponent.select(0, textComponent.getText().length());
                }
            }

        });

        add(cutIt);
        add(copyIt);
        add(pasteIt);
        addSeparator();
        add(deleteIt);
        add(selectAllIt);

        // Optionales suche und ersetzen
        final String findString = resHandler.getString("TOOLS_CONTEXTMENU_FIND");
        final JMenuItem findIt = new JMenuItem(findString);
        if (isExtensionFindReplace()) {

            findIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, MENU_SHORTCUT_KEY_MASK));
            findIt.setAction(new AbstractAction(findString) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (e.getActionCommand().equals(findString)) {
                        FindReplacePanel findreplace = new FindReplacePanel(myTargetComponent);
                        findreplace.showFindReplaceDialog(myTargetComponent, findReplaceXPos, findReplaceYPos);
                    }
                }

            });
            addSeparator();
            add(findIt);
        }

        // dafür sorgen, dass bei jedem Anzeigen der enabled-Status aktualisiert wird
        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {

                cutIt.setEnabled(cutIt.getAction().isEnabled());
                copyIt.setEnabled(copyIt.getAction().isEnabled());
                pasteIt.setEnabled(pasteIt.getAction().isEnabled());
                deleteIt.setEnabled(deleteIt.getAction().isEnabled());
                selectAllIt.setEnabled(selectAllIt.getAction().isEnabled());
                selectAllIt.setEnabled(selectAllIt.getAction().isEnabled());

                if (isExtensionFindReplace()) {
                    selectAllIt.setEnabled(findIt.getAction().isEnabled());

                    // Workaround für Exception in thread "AWT-EventQueue-0" java.awt.IllegalComponentStateException:
                    // component must be showing on the screen to determine its location
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            findReplaceXPos = findIt.getLocationOnScreen().x;
                            findReplaceYPos = findIt.getLocationOnScreen().y;
                        }
                    });
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {
            }
        });

    }

    /**
     * Kontextmenü für Textkomponenten mit Cut, Copy, Insert, Delete, SelectAll. Wenn die Textkomponente nicht editable oder nicht enabled ist, wird
     * nur SelectAll und Copy angeboten.
     *
     * @param textComponent
     * @return
     */
    @SuppressWarnings("unused")
    public static boolean addPopupMenuTo(final JTextComponent textComponent) {
        return addPopupMenuTo(textComponent, true);
    }

    /**
     * Kontextmenü für Textkomponenten mit Cut, Copy, Insert, Delete, SelectAll, Find and Replace optional Wenn die Textkomponente nicht editable oder
     * nicht enabled ist, wird nur SelectAll und Copy
     * angeboten.
     *
     * @param textComponent
     * @param extensionFindReplace
     *            Find/Replace eingefügen
     * @return
     */
    @SuppressWarnings("unused")
    public static boolean addPopupMenuTo(final JTextComponent textComponent, final boolean extensionFindReplace) {
        // nur 1 x hinzufügen
        for (MouseListener ml : textComponent.getMouseListeners()) {
            if (ml instanceof TextComponentStandardPopup) {
                return false;
            }
        }
        //mit diesem Aufruf wird der textComponent das Popuzp angehängt
        new TextComponentStandardPopup(textComponent, extensionFindReplace);
        registerMacShortcuts(textComponent);
        return true;
    }

    /**
     * Sorgt dafür, dass für die übergebene Textkomponente auf einem MAC für die Standard-Aktionen SelectAll, Copy, Paste Cut auch die
     * Shortcuts über die CMD-Taste funktionieren.
     *
     * @param textComponent
     */
    public static final void registerMacShortcuts(final JTextComponent textComponent) {
        InputMap im = textComponent.getInputMap();
        if (System.getProperty("os.name", "").toUpperCase().startsWith("MAC")) {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
        }
    }

    /**
     * Liefert den aktuellen Inhalt des System-Clipboards als String.
     *
     * @return String-Repräsentation des Inhalts oder <code>null</code>, wenn nichts enthalten ist.
     */
    private static final String getFromClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            doPop(e);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            doPop(e);
        }
    }

    /**
     * Zeigt das Popupmenu für die SourceKomponente des übergebenen Ereignisses an, wenn es eine {@link JTextComponent} ist.
     *
     * @param e
     */
    private void doPop(final MouseEvent e) {
        if (e.getSource() instanceof JTextComponent) {
            JTextComponent source = (JTextComponent) e.getSource();
            // wenn die Komponente nicht enabled ist, kann man gar nichts machen, also braucht das Menü auch nicht gezeigt zu werden
            if (!source.isEnabled()) {
                return;
            }
            source.requestFocus();
            if (!initialized) {
                initialized = true;
                init(myTargetComponent);
            }
            show(source, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    public boolean isExtensionFindReplace() {
        return extensionFindReplace;
    }

    public void setExtensionFindReplace(final boolean extensionFindReplace) {
        this.extensionFindReplace = extensionFindReplace;
    }

}
