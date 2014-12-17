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
 * Quotient is a class that implements Expression and represents two numbers being divided.
 * It stores the two expressions that are divided to yield the result.
 * @author Patrick Owen
 * @see Expression
 */
public class Quotient implements Expression
{
	private Expression e1;
	private Expression e2;
	
	/**
	 * Constructs a Quotient object given the two expressions to be divided.
	 * @param e1 The dividend
	 * @param e2 The divisor
	 */
	public Quotient(Expression e1, Expression e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	public double eval(double[] x)
	{
		return e1.eval(x) / e2.eval(x);
	}
	
	public double derivative(double[] x, int var)
	{
		double v2 = e2.eval(x); //Evaluated beforehand for efficiency
		return (v2*e1.derivative(x, var) - e1.eval(x)*e2.derivative(x, var)) / (v2*v2);
	}
}
