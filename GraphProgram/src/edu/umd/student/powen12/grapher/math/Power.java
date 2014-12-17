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
 * Power is a class that implements Expression and represents a number being raised to the power of another number.
 * It stores the two expressions used as the base and the exponent.
 * @author Patrick Owen
 * @see Expression
 */
public class Power implements Expression
{
	private Expression e1;
	private Expression e2;
	
	/**
	 * Constructs a Power object given the base and the exponent
	 * @param e1 The base
	 * @param e2 The exponent
	 */
	public Power(Expression e1, Expression e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	public double eval(double[] x)
	{
		return Math.pow(e1.eval(x), e2.eval(x));
	}
	
	public double derivative(double[] x, int var)
	{
		//Multiple-case scenario to help prevent issues with division by zero.
		double d1 = e1.derivative(x, var), d2 = e2.derivative(x, var);
		double v1 = e1.eval(x), v2 = e2.eval(x);
		
		if (d1 == 0 && d2 == 0)
			return 0;
		else if (d1 == 0) //Exponential function
			return Math.pow(v1, v2) * d2 * Math.log(v1);
		else if (d2 == 0) //Power function
			return Math.pow(v1, v2-1) * d1 * v2;
		else //Both the base and exponent are changing
			return Math.pow(v1, v2-1) * (d1*v2 + d2*v1*Math.log(v1));
	}
}
