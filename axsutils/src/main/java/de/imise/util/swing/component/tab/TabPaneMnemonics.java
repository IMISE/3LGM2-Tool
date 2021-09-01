package de.imise.util.swing.component.tab;

import java.util.ArrayList;

import javax.swing.JTabbedPane;

public class TabPaneMnemonics extends JTabbedPane {

    private final ArrayList<Character> usedMnemonics = new ArrayList<>();

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
