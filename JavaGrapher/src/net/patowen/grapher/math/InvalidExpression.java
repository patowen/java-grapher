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

package net.patowen.grapher.math;

/**
 * InvalidExpression is a class that implements Expression and is used to represent a function that was typed incorrectly.
 * @author Patrick Owen
 */
public class InvalidExpression implements Expression
{
	/**
	 * Returns Double.NaN, or Not-a-Number.
	 */
	public double eval(double[] x)
	{
		return Double.NaN;
	}
	
	/**
	 * Returns Double.NaN, or Not-a-Number.
	 */
	public double derivative(double[] x, int var)
	{
		return Double.NaN;
	}
}
