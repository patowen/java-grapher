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
 * Function is a class that implements Expression and represents any of a list of functions.
 * It stores the expressions that are arguments and the id of the function that it represents.
 * @author Patrick Owen
 * @see Expression
 * @see FunctionList
 */
public class Function implements Expression
{
	private Expression[] args;
	private double[] argValues;
	private double[] derValues;
	private int functionType;
	
	/**
	 * Constructs a Function object given the arguments and the id of the function it represents.
	 * @param args The arguments of the function
	 * @param functionType The id of the function
	 */
	public Function(Expression[] args, int functionType)
	{
		this.args = args;
		argValues = new double[this.args.length];
		derValues = new double[this.args.length];
		this.functionType = functionType;
	}
	
	public double eval(double[] x)
	{
		for (int i=0; i<args.length; i++)
		{
			argValues[i] = args[i].eval(x);
		}
		return FunctionList.eval(argValues, functionType);
	}
	
	public double derivative(double[] x, int var)
	{
		for (int i=0; i<args.length; i++)
		{
			argValues[i] = args[i].eval(x);
			derValues[i] = args[i].derivative(x, var);
		}
		return FunctionList.derivative(argValues, derValues, functionType);
	}
}
