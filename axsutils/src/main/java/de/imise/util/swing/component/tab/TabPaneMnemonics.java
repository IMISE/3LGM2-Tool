package de.imise.util.swing.component.tab;

import java.util.ArrayList;

import javax.swing.JTabbedPane;

/**
 * Enables Tab panes to have ALT+<Character> Shortcuts for switching between
 * tabs
 *
 * @author Hyeon Ung Kim
 */
public class TabPaneMnemonics extends JTabbedPane {

    // this list contains the already set Characters for the mnemonics
    private final ArrayList<Character> usedMnemonics = new ArrayList<>();

    /**
     * redirects Constructor to JTabbedPane
     *
     * @param tabPlacement
     * @param tabLayoutPolicy
     */
    public TabPaneMnemonics(final int tabPlacement, final int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
    }

    /**
     * sets the shortcut ALT+ <Character> for the Tabs
     * checks if a letter has been used or not
     *
     * @param tabIndex
     * @param tabName
     */
    public void setMnemonic(final int tabIndex, final String tabName) {
        for (int n = 0; n < tabName.length(); n++) {
            char mnemonicChar = Character.toLowerCase(tabName.charAt(n));
            if (!usedMnemonics.contains(mnemonicChar)) {
                usedMnemonics.add(mnemonicChar);
                int mnemonic = mnemonicChar;
                if (mnemonic >= 'a' && mnemonic <= 'z') {
                    mnemonic -= 'a' - 'A';
                }
                super.setMnemonicAt(tabIndex, mnemonic);
                break;
            }
        }
    }
}
