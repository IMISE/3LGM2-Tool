package de.imise.util.swing.component;

import javax.swing.Action;
import javax.swing.JCheckBox;

/**
 * Simply a {@link JCheckBox} that stores an addiotinal boolean. This boolean
 * can be used to store the options value since the last commit in a dialog.
 *
 * @author Ich (15.04.2021)
 */
public class StoreLastValueCheckbox extends JCheckBox {

    /**
    *
    */
    private boolean storedState;

    /**
     * Sets the selected value and stores it
     *
     * @param text
     * @param selected
     */
    public StoreLastValueCheckbox(final String text, final boolean selected) {
        super(text, selected);
        storeCurrentState();
    }

    /**
     * Sets the selected value and stores it
     *
     * @param action
     * @param selected
     */
    public StoreLastValueCheckbox(final Action action, final boolean selected) {
        super(action);
        setSelectedAndStoreState(selected);
    }

    /**
     * @return the storedState
     */
    public final boolean isStoredState() {
        return storedState;
    }

    /**
     * @param storedState the storedState to set
     */
    public final void setStoredState(final boolean storedState) {
        this.storedState = storedState;
    }

    /**
     * Stores the current selected value
     *
     * @return the current selected value
     */
    public boolean storeCurrentState() {
        boolean selected = isSelected();
        setStoredState(selected);
        return selected;
    }

    /**
     * @param selected
     */
    public void setSelectedAndStoreState(final boolean selected) {
        setSelected(selected);
        storeCurrentState();
    }

}
