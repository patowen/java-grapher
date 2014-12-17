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
 * Expression is an interface that allows several different types of operations to be nested by implementing this class.
 * All functions that use nested functions call the eval method of its nested functions to evaluate its own parameters.
 * @author Patrick Owen
 */
public interface Expression
{
	/**
	 * Returns the value of the Expression given an array of variables. If the Expression
	 * cannot evaluate to anything (may be an InvalidExpression), Double.NaN is returned.
	 * @see Variable
	 * @param x The array of variables that the Expression uses.
	 * @return The value of the function
	 */
	public double eval(double[] x);
	
	/**
	 * Returns the partial derivative of the Expression given an array of variables and
	 * the variable that the derivative is taken with respect to. If the Expression
	 * cannot evaluate to anything (may be an InvalidExpression), Double.NaN is returned.
	 * @param x The array of variables that the Expression uses.
	 * @param var The variable that the derivative is taken with respect to.
	 * @return The derivative of the function
	 */
	public double derivative(double[] x, int var);
}
