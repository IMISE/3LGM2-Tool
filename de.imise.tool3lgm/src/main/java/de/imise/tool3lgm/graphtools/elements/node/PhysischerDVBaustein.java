package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PDVBKonfPanel2;
import de.imise.tool3lgm.graphtools.dialog.panel.TechnikPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.XMLCharacterCoder;

public class PhysischerDVBaustein extends Knoten {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            Standort.class,
            Bausteintyp.class,
            Subnetz.class,
            DBKonfiguration.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     * COMMENTME
     */
    private String os_type, serial, inventar, disksize, ramsize, processor;

    int typ = 0;

    int downtime = 0;

    public PhysischerDVBaustein() {
        super();
        os_type = "";
        serial = "";
        inventar = "";
        disksize = "";
        ramsize = "";
        processor = "";
    }

    @Override
    public Object clone() {
        PhysischerDVBaustein retVal;
        try {
            retVal = (PhysischerDVBaustein) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        retVal.os_type = os_type;
        retVal.serial = serial;
        retVal.inventar = inventar;
        retVal.disksize = disksize;
        retVal.ramsize = ramsize;
        retVal.processor = processor;
        retVal.typ = typ;
        retVal.downtime = downtime;
        return retVal;
    }

    @Override
    public int layerFor() {
        return ModelConstants.PHYSICAL_LAYER;
    }

    public String getOSType() {
        return os_type;
    }

    public void setOSType(final String in) {
        if (in != null) {
            os_type = in;
        }
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(final String in) {
        serial = in;
    }

    public String getInventar() {
        return inventar;
    }

    public void setInventar(final String in) {
        inventar = in;
    }

    public String getDiskSize() {
        return disksize;
    }

    public void setDiskSize(final String in) {
        disksize = in;
    }

    public String getRamSize() {
        return ramsize;
    }

    public void setRamSize(final String in) {
        ramsize = in;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(final String in) {
        processor = in;
    }

    @Override
    public boolean putXMLFieldString(final String field, final String value) {
        if (field.equals("os_type")) {
            os_type = value;
            return true;
        }
        if (field.equals("serial")) {
            serial = value;
            return true;
        }
        if (field.equals("inventar")) {
            inventar = value;
            return true;
        }
        if (field.equals("disksize")) {
            disksize = value;
            return true;
        }
        if (field.equals("ramsize")) {
            ramsize = value;
            return true;
        }
        if (field.equals("processor")) {
            processor = value;
            return true;
        }
        if (field.equals("downtime")) {
            downtime = Integer.parseInt(value);
            return true;
        }

        return super.putXMLFieldString(field, value);
    }

    @Override
    public StringBuilder getXMLEntities() {
        return super.getXMLEntities().append(os_type.length() > 0 ? "<field name=\"os_type\">" + XMLCharacterCoder.encodeString(os_type) + "</field>" : "")
                .append(serial.length() > 0 ? "<field name=\"serial\">" + XMLCharacterCoder.encodeString(serial) + "</field>" : "").append(inventar.length() > 0 ? "<field name=\"inventar\">" + XMLCharacterCoder.encodeString(inventar) + "</field>" : "")
                .append(disksize.length() > 0 ? "<field name=\"disksize\">" + XMLCharacterCoder.encodeString(disksize) + "</field>" : "").append(ramsize.length() > 0 ? "<field name=\"ramsize\">" + XMLCharacterCoder.encodeString(ramsize) + "</field>" : "")
                .append(processor.length() > 0 ? "<field name=\"processor\">" + XMLCharacterCoder.encodeString(processor) + "</field>" : "")
                .append(downtime > -1 ? "<field name=\"downtime\">" + XMLCharacterCoder.encodeString(new Integer(downtime).toString()) + "</field>" : "");
    }

    @Override
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
        dialog.addTab(getResString("tm"), new TechnikPanel(dialog));
        dialog.addTab(Tool3lgmConstants.getResString("Subnetz"), new NConnectionPanel(Subnetz.class, dialog, true, true));
        dialog.addTab(Tool3lgmConstants.getResString("Anwendungsbaustein_p"), new PDVBKonfPanel2(dialog));
        return dialog;
    }

    @Override
    public boolean hasLayout() {
        return true;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

    /**
     * @return Downtime in Stunden; -1 wenn nicht initialisiert
     */
    public int getDowntime() {
        return downtime;
    }

    /**
     * @return Downtime in Stunden; -1 wenn nicht initialisiert
     */
    public String getDowntimeString() {
        return downtime > -1 ? new Integer(downtime).toString() : "";
    }

    /**
     * @param i Downtime
     */
    public void setDowntime(final int i) {
        downtime = i;
    }

    public float getVerfuegbarkeit(final GraphDocument doc) {
        return typ > -1 ? (8760f - downtime) / 8760f * 100f : -1;
    }

    @Override
    public boolean join(final ModelElement other, final boolean overwriteHashstring) {
        if (!super.join(other, overwriteHashstring)) {
            return false;
        }

        disksize = disksize.concat(" - " + ((PhysischerDVBaustein) other).getDiskSize());
        downtime = ((PhysischerDVBaustein) other).getDowntime();
        inventar = inventar.concat(" - " + ((PhysischerDVBaustein) other).getInventar());
        os_type = os_type.concat(" - " + ((PhysischerDVBaustein) other).getOSType());
        processor = processor.concat(" - " + ((PhysischerDVBaustein) other).getProcessor());
        ramsize = ramsize.concat(" - " + ((PhysischerDVBaustein) other).getRamSize());
        serial = serial.concat("\n-\n" + ((PhysischerDVBaustein) other).getSerial());

        return true;
    }

}
