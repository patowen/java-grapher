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
 * Negation is a class that implements Expression and represents a number being negated.
 * It stores the number that is negated to yield the result.
 * @author Patrick Owen
 * @see Expression
 */
public class Negation implements Expression
{
	private Expression e;
	
	/**
	 * Constructs a Negation object given the expression to be negated.
	 * @param e The number to be negated
	 */
	public Negation(Expression e)
	{
		this.e = e;
	}
	
	public double eval(double[] x)
	{
		return -e.eval(x);
	}
	
	public double derivative(double[] x, int var)
	{
		return -e.derivative(x, var);
	}
}
