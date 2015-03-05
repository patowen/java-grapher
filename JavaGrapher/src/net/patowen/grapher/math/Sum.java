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
 * Sum is a class that implements Expression and represents two numbers being added.
 * It stores the two expressions that are added to yield the result.
 * @author Patrick Owen
 * @see Expression
 */
public class Sum implements Expression
{
	private Expression e1;
	private Expression e2;
	
	/**
	 * Constructs a Sum object given the two expressions to be added.
	 * @param e1 The first addend
	 * @param e2 The second addend
	 */
	public Sum(Expression e1, Expression e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	public double eval(double[] x)
	{
		return e1.eval(x) + e2.eval(x);
	}
	
	public double derivative(double[] x, int var)
	{
		return e1.derivative(x, var) + e2.derivative(x, var);
	}
}
