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
 * Variable is a class that implements Expression and stores the id of the variable.
 * In a function, it represents the variables: x, y, z, a, etc.
 * @author Patrick Owen
 * @see Expression
 */
public class Variable implements Expression
{
	private int type; //Variable type
	
	/**
	 * Constructs a Variable object given the id of which variable it is
	 * @param type The id of the variable
	 */
	public Variable(int type)
	{
		this.type = type;
	}
	
	public double eval(double[] x)
	{
		if (x.length > type)
			return x[type]; //Returns the right variable
		return Double.NaN;
	}
	
	public double derivative(double[] x, int var)
	{
		if (type == var)
			return 1;
		else
			return 0;
	}
}
