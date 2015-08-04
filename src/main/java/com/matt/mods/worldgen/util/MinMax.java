package com.matt.mods.worldgen.util;

/**
 * Minimum and Maximum Container Class
 * @author Matthieu Parizeau
 */
public class MinMax {
	
	/**
	 * Values
	 */
	protected int min, max;
	
	/**
	 * Creates a container with specified values.
	 * @param min - min value
	 * @param max - max value
	 */
	public MinMax(int min, int max)
	{
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Creates a container initialized to zero.
	 */
	public MinMax()
	{
		this(0, 0);
	}
	
	/**
	 * Sets the minimum value.
	 * @param min - minimum value
	 */
	public void setMin(int min)
	{
		this.min = min;
	}
	
	/**
	 * Gets the minimum value
	 * @return minimum value
	 */
	public int getMin()
	{
		return this.min;
	}

	/**
	 * Sets the max value.
	 * @param max - max value
	 */
	public void setMax(int max)
	{
		this.max = max;
	}

	/**
	 * Gets the max value
	 * @return max value
	 */
	public int getMax()
	{
		return this.max;
	}
	
}
