/**
 * 
 */
package de.imise.util;

/**
 * Ein Range Objekt besitzt eine Position und eine Länge
 * 
 * @author AXS
 */
public class IntRange {

	/**
	 * Anfangspostion des Ranges
	 */
	private int offset = 0;
	
	/**
	 * Länge des Ranges
	 */
	private int length = 0;
	
	/**
	 * 
	 */
	public IntRange() {
		super();
	}

	/**
	 * @param offset
	 * @param length
	 */
	public IntRange(int offset, int length) {
		this();
		this.offset = offset;
		this.length = length;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

}
