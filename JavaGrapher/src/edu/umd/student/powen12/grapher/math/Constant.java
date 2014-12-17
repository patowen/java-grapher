/*
 * Copyright 2013 Patrick Owen
 * 
 * This file is part of Patrick's Grapher.
 * 
 * Patrick's Grapher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * Patrick's Grapher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Patrick's Grapher.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.umd.student.powen12.grapher.math;

/**
 * Constant is a class that implements Expression and stores a number. In a function, it represents
 * the basic unit: a number.
 * @author Patrick Owen
 * @see Expression
 */
public class Constant implements Expression
{
	private double c;
	
	/**
	 * Constructs a Constant object given the constant it stores.
	 * @param c The constant that this class represents
	 */
	public Constant(double c)
	{
		this.c = c;
	}
	
	public double eval(double[] x)
	{
		return c; //Not affected by the argument
	}
	
	public double derivative(double[] x, int var)
	{
		return 0;
	}
}
