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
 * Thrown to indicate that the application has attempted to convert a string to an Expression,
 * but that the string does not have the appropriate format. This is only seen from the inside
 * of edu.mbhs.powen.grapher.math, since the exception is then caught, and an
 * InvalidExpression is returned.
 * @author Patrick Owen
 */
public class InvalidExpressionException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs an InvalidExpressionException with no detail message.
	 */
	public InvalidExpressionException() {}
	
	/**
	 * Constructs an InvalidExpressionException with the specified detail message.
	 * @param message the detail message
	 */
	public InvalidExpressionException(String message)
	{
		super(message);
		
		new NumberFormatException("");
	}
}
