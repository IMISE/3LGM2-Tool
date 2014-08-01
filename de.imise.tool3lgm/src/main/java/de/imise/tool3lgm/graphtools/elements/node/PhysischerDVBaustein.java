package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PDVBKonfPanel2;
import de.imise.tool3lgm.graphtools.dialog.panel.StructurePanel;
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

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#getCopyDependencies()
	 */
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

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Knoten#clone()
	 */
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

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.PHYSICAL_LAYER;
	}

	public String getOSType() {
		return os_type;
	}

	public void setOSType(String in) {
		if (in != null)
			os_type = in;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String in) {
		serial = in;
	}

	public String getInventar() {
		return inventar;
	}

	public void setInventar(String in) {
		inventar = in;
	}

	public String getDiskSize() {
		return disksize;
	}

	public void setDiskSize(String in) {
		disksize = in;
	}

	public String getRamSize() {
		return ramsize;
	}

	public void setRamSize(String in) {
		ramsize = in;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String in) {
		processor = in;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Knoten#putXMLFieldString(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean putXMLFieldString(String field, String value) {
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

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Knoten#getXMLEntities()
	 */
	@Override
	public StringBuilder getXMLEntities() {
		return super.getXMLEntities().append(os_type.length() > 0 ? "<field name=\"os_type\">" + XMLCharacterCoder.encodeString(os_type) + "</field>" : "").append(
				serial.length() > 0 ? "<field name=\"serial\">" + XMLCharacterCoder.encodeString(serial) + "</field>" : "").append(inventar.length() > 0 ? "<field name=\"inventar\">" + XMLCharacterCoder.encodeString(inventar) + "</field>" : "").append(
				disksize.length() > 0 ? "<field name=\"disksize\">" + XMLCharacterCoder.encodeString(disksize) + "</field>" : "").append(ramsize.length() > 0 ? "<field name=\"ramsize\">" + XMLCharacterCoder.encodeString(ramsize) + "</field>" : "")
				.append(processor.length() > 0 ? "<field name=\"processor\">" + XMLCharacterCoder.encodeString(processor) + "</field>" : "").append(
						downtime > -1 ? "<field name=\"downtime\">" + XMLCharacterCoder.encodeString((new Integer(downtime)).toString()) + "</field>" : "");
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("tm"), new TechnikPanel(dialog));
		dialog.addTab(Tool3lgmConstants.getResString("strukt"), new StructurePanel(dialog));
		dialog.addTab(Tool3lgmConstants.getResString("Subnetz"), new NConnectionPanel(Subnetz.class, dialog, true, true));
		dialog.addTab(Tool3lgmConstants.getResString("Anwendungsbaustein_p"), new PDVBKonfPanel2(dialog));
		return dialog;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasLayout()
	 */
	@Override
	public boolean hasLayout() {
		return true;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasSortedKanten()
	 */
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
		return (downtime > -1 ? (new Integer(downtime)).toString() : "");
	}

	/**
	 * @param i
	 *            Downtime
	 */
	public void setDowntime(int i) {
		downtime = i;
	}

	public float getVerfuegbarkeit(GraphDocument doc) {
		return (typ > -1 ? ((8760f - downtime) / 8760f * 100f) : -1);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#join(tool3lgm.graphtools.elements.ModelElement, boolean)
	 */
	@Override
	public boolean join(ModelElement other, boolean overwriteHashstring) {
		if (!super.join(other, overwriteHashstring))
			return false;

		this.disksize = this.disksize.concat(" - " + ((PhysischerDVBaustein) other).getDiskSize());
		this.downtime = ((PhysischerDVBaustein) other).getDowntime();
		this.inventar = this.inventar.concat(" - " + ((PhysischerDVBaustein) other).getInventar());
		this.os_type = this.os_type.concat(" - " + ((PhysischerDVBaustein) other).getOSType());
		this.processor = this.processor.concat(" - " + ((PhysischerDVBaustein) other).getProcessor());
		this.ramsize = this.ramsize.concat(" - " + ((PhysischerDVBaustein) other).getRamSize());
		this.serial = this.serial.concat("\n-\n" + ((PhysischerDVBaustein) other).getSerial());

		return true;
	}

}
