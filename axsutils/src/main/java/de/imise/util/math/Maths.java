package de.imise.util.math;


public class Maths {
	
	/**
	 * Errechnet das Maximum der übergegebenen Werte.
	 * @param values
	 * @return
	 */
	public static int max(int... values) {
		int max = Integer.MIN_VALUE;
		for (int value : values)
			max = Math.max(value, max);
		return max;
	}
	
	/**
	 * Errechnet das Minimum der übergegebenen Werte.
	 * @param values
	 * @return
	 */
	public static int min(int... values) {
		int min = Integer.MAX_VALUE;
		for (int value : values)
			min = Math.min(value, min);
		return min;
	}
	

}
