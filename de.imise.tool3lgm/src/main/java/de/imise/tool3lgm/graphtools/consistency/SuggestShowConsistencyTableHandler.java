package de.imise.tool3lgm.graphtools.consistency;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_CONSISTENCY_TABLE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.TRANSIENT_OPTION_ASK_SHOW_CONSISTENCY_TABLE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.dialog.tools.GeneralDialogCreator;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author AXS (25.08.2020)
 */
public class SuggestShowConsistencyTableHandler implements PropertyChangeListener {

    /**
     *
     */
    private static long lastDialogCloseTime = 0;

    /**
     * To prevent the user from being asked too often, at least 10 seconds
     * must elapse between 2 requests.
     */
    private static final long minTimeBetweenLastShowDialogAndNextShowDialog = 10000;

    /**
     * Single instance of this
     */
    private static SuggestShowConsistencyTableHandler singleton;

    /**
     * If <code>true</code> the dialog will not be shown.
     *
     * @see #propertyChange(PropertyChangeEvent)
     */
    private boolean ignore = false;

    /**
     *
     */
    private SuggestShowConsistencyTableHandler() {
        UserProperties.addPropertyChangeListener(this);
    }

    /**
     *
     */
    public static final void suggestShowConsistencyTable() {
        if (singleton == null) {
            singleton = new SuggestShowConsistencyTableHandler();
        }
        if (singleton.ignore) { //see #propertyChange(PropertyChangeEvent)
            singleton.ignore = false;
            return;
        }
        if (OPTION_SHOW_CONSISTENCY_TABLE.is() || !TRANSIENT_OPTION_ASK_SHOW_CONSISTENCY_TABLE.is()) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastDialogCloseTime < minTimeBetweenLastShowDialogAndNextShowDialog) {
            return;
        }

        ConsistencyChecker consistencyChecker = ConsistencyChecker.getConsistencyChecker();
        if (consistencyChecker.hasInconsistencies()) {
            JComponent message = GeneralDialogCreator.getLabelPanel("SUGGEST_SHOW_CONSISTENCY_TABLE_DIALOG_MESSAGE", true);
            String title = getResString("message_do_not_ask_again");
            JCheckBox dontAskAgain = new JCheckBox(title, false);
            int answer = GeneralDialogCreator.showDialog("SUGGEST_SHOW_CONSISTENCY_TABLE_DIALOG_TITLE", message, dontAskAgain);
            lastDialogCloseTime = System.currentTimeMillis();
            TRANSIENT_OPTION_ASK_SHOW_CONSISTENCY_TABLE.set(!dontAskAgain.isSelected());
            if (answer == JOptionPane.OK_OPTION) {
                OPTION_SHOW_CONSISTENCY_TABLE.set(true);
            }
        }

    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        //If the option OPTION_SHOW_CONSISTENCY_TABLE is changed to false, a
        //DATA_CHANGED event is fired and the MainFrameDesktopPane calls
        //suggestShowConsistencyTable(). To avoid that the system immediately
        //asks again whether you want to see the consistency error table after closing it,
        //ignore is set to false here, so the next call of suggestShowConsistencyTable()
        //will not show the question dialog.
        if (OPTION_SHOW_CONSISTENCY_TABLE.isChanged(evt)) {
            if (!OPTION_SHOW_CONSISTENCY_TABLE.is()) {
                ignore = true;
            }
        }
    }

}
