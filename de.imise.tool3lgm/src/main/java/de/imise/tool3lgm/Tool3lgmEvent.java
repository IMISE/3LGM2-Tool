package de.imise.tool3lgm;

public class Tool3lgmEvent {
    public static final int NEW_FRAME = 1 << 0;
    public static final int VIEW_CHANGED = 1 << 1;
    public static final int FILE_SAVED = 1 << 2;
    public static final int OPEN_FRAME = 1 << 3;
    public static final int TOOLTIP_FLAG_CHANGED = 1 << 4;

    private final int event_type;

    public Tool3lgmEvent(final int type) {
        event_type = type;
    }

    public int getType() {
        return event_type;
    }

}
