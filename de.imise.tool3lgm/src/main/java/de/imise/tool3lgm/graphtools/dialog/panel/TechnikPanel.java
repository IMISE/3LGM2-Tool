package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.util.swing.component.text.ExtendedTextField;

public class TechnikPanel extends ElementDialogPanel {

    ExtendedTextField os, sn, in, fg, as, pr, downtime;

    JLabel verfuegbarkeit;

    public TechnikPanel(final ElementPropertyDialog pd) {
        super(pd);
        // setPreferredSize(new Dimension(450,200));

        JPanel area = new JPanel();
        ModelElement modelElement = getModelElement();
        area.setLayout(new GridLayout(8, 2));
        JLabel label = new JLabel(Tool3lgmConstants.getResString("os"));
        area.add(label);
        os = new ExtendedTextField(15);
        os.setText(((PhysischerDVBaustein) modelElement).getOSType());
        area.add(os);
        label = new JLabel(Tool3lgmConstants.getResString("snr"));
        area.add(label);
        sn = new ExtendedTextField(15);
        sn.setText(((PhysischerDVBaustein) modelElement).getSerial());
        area.add(sn);
        label = new JLabel(Tool3lgmConstants.getResString("inr"));
        area.add(label);
        in = new ExtendedTextField(15);
        in.setText(((PhysischerDVBaustein) modelElement).getInventar());
        area.add(in);
        label = new JLabel(Tool3lgmConstants.getResString("hdsize"));
        area.add(label);
        fg = new ExtendedTextField(15);
        fg.setText(((PhysischerDVBaustein) modelElement).getDiskSize());
        area.add(fg);
        label = new JLabel(Tool3lgmConstants.getResString("ram"));
        area.add(label);
        as = new ExtendedTextField(15);
        as.setText(((PhysischerDVBaustein) modelElement).getRamSize());
        area.add(as);
        label = new JLabel(Tool3lgmConstants.getResString("cpu"));
        area.add(label);
        pr = new ExtendedTextField(15);
        pr.setText(((PhysischerDVBaustein) modelElement).getProcessor());
        area.add(pr);
        label = new JLabel(Tool3lgmConstants.getResString("downtime"));
        area.add(label);
        downtime = new ExtendedTextField(15);
        downtime.setText(((PhysischerDVBaustein) modelElement).getDowntimeString());
        area.add(downtime);

        label = new JLabel(Tool3lgmConstants.getResString("verfuegbarkeit"));
        area.add(label);
        verfuegbarkeit = new JLabel(Float.toString(((PhysischerDVBaustein) modelElement).getVerfuegbarkeit(getDialog().getGraphDocument().getCollection().getMainGraphDocument())));
        area.add(verfuegbarkeit);

        add(area);
    }

    @Override
    protected void init() {
        verfuegbarkeit.setText(Float.toString(((PhysischerDVBaustein) getModelElement()).getVerfuegbarkeit(getDialog().getGraphDocument().getCollection().getMainGraphDocument())));
    }

    @Override
    protected void showFullDialog() {
    }

    @Override
    public void commit() {
        ModelElement modelElement = getModelElement();
        if (modelElement instanceof PhysischerDVBaustein) {
            doc.select(modelElement.getContainer(doc), dialog.getTransactionID());
            LGMGraphDocument ldoc = (LGMGraphDocument) doc;
            if (os.getText() != null) {
                ldoc.changeOSType(modelElement.getHashString(), os.getText(), dialog.getTransactionID());
            }
            if (sn.getText() != null) {
                ldoc.changeSerial(modelElement.getHashString(), sn.getText(), dialog.getTransactionID());
            }
            if (in.getText() != null) {
                ldoc.changeInventar(modelElement.getHashString(), in.getText(), dialog.getTransactionID());
            }
            if (fg.getText() != null) {
                ldoc.changeDiskSize(modelElement.getHashString(), fg.getText(), dialog.getTransactionID());
            }
            if (as.getText() != null) {
                ldoc.changeRamSize(modelElement.getHashString(), as.getText(), dialog.getTransactionID());
            }
            if (pr.getText() != null) {
                ldoc.changeProcessor(modelElement.getHashString(), pr.getText(), dialog.getTransactionID());
            }
            if (downtime.getText() != null) {
                String downtimeString = downtime.getText();
                try {
                    Integer downtimeInt = new Integer(downtimeString);
                    ldoc.changeDowntime(modelElement.getHashString(), downtimeInt.toString(), dialog.getTransactionID());
                } catch (Exception e) {
                }
            }
        }

    }

}
