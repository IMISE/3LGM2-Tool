package de.imise.tool3lgm;

public interface Tool3lgmListener {
    public void newFrame(Tool3lgmEvent e);

    public void viewChanged(Tool3lgmEvent e);

    public void fileSaved(Tool3lgmEvent e);

    public void openFrame(Tool3lgmEvent e);

    public void graphicPropertyChanged(Tool3lgmEvent e);

}
