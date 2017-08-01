package de.imise.tool3lgm;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.RandomAccessFile;

import javax.swing.JWindow;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.util.swing.component.text.ExtendedTextArea;

// TODO:prüfen wo das hier gebraucht wird (anscheinend gar nicht)
public class DirectHelp extends JWindow {
    //   static private boolean open;

    public DirectHelp() {
        GraphDocument doc = Static.getSelectedDoc();
        if (doc == null) {
            return;
        }
        if (!doc.isSingleSelection()) {
            return;
        }
        ElementContainer me = doc.getLastSelected();
        Point p = Static.getMainFrame().getLocationOnScreen();
        ExtendedTextArea area = new ExtendedTextArea(30, 10);
        area.setBackground(new Color(240, 210, 125));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        //area.addMouseListener(this);
        setSize(250, 100);
        setLocation((int) p.getX() + 10, (int) p.getY() + 10);
        getContentPane().setBackground(Color.black);

        String url = System.getProperty("user.dir") + File.separator + "doc" + File.separator + "dh.txt";
        //		File file = new File(url);
        boolean flag = true;
        String str = "Fehler";
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(url, "r");
            while (flag) {
                str = raf.readLine();
                int index = str.indexOf(":");
                String teilstr = str.substring(0, index);
                if (teilstr.equals(me.getClass().getSimpleName())) {
                    flag = false;
                    str = str.substring(index + 2);
                    area.setText(str);
                    raf.close();
                    getContentPane().add(area);
                    setVisible(true);
                    return;
                }
            }
        } catch (Exception e) {
            Log.show(Log.FATAL, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
        }
        dispose();
    }

    //    public void mouseClicked(final MouseEvent e) {
    //    }
    //
    //    public void mouseEntered(final MouseEvent e) {
    //    }
    //
    //    public void mouseExited(final MouseEvent e) {
    //    }
    //
    //    public void mousePressed(final MouseEvent e) {
    //        dispose();
    //    }
    //
    //    public void mouseReleased(final MouseEvent e) {
    //    }
    //
}
