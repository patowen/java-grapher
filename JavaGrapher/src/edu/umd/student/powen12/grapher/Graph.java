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

package edu.umd.student.powen12.grapher;
import edu.umd.student.powen12.grapher.math.Expression;

/**
 * Provides a template for all graphs to follow in their interface
 * with the window they are embedded in.
 * @author Patrick Owen
 */
public interface Graph
{
	/**
	 * Sets whether axes are shown on this graph.
	 * @param enabled whether axes should be shown on this graph
	 */
	public void setShowAxes(boolean enabled);
	
	/**
	 * Sets whether the mouse should affect the view of the graph
	 * rather than adjusting u and v parameters.
	 * @param enabled whether the mouse should affect the view
	 */
	public void setMouseView(boolean enabled);
	
	/**
	 * Sets the Expression of the specified index that the graph uses to
	 * plot data.
	 * @param index the index of the Expression to modify
	 * @param e the new Expression
	 */
	public void setExpression(int index, Expression e);
	
	/**
	 * Returns the Expression of the specified index that the graph uses to
	 * plot data.
	 * @param index the index of the Expression to return
	 * @return the Expression of the specified index that the graph uses to
	 * plot data.
	 */
	public Expression getExpression(int index);
	
	
	/**
	 * Sets the parameter of the specified index that holds a double to the specified value.
	 * @param index the specified index
	 * @param value the new value
	 */
	public void setDouble(int index, double value);
	
	/**
	 * Sets the parameter of the specified index that holds an integer to the specified value.
	 * @param index the specified index
	 * @param value the new value
	 */
	public void setInt(int index, int value);
	
	/**
	 * Returns the value of the parameter of the specified index that holds a double.
	 * @param index the specified index
	 * @return the value of the parameter
	 */
	public double getDouble(int index);
	
	/**
	 * Returns the value of the parameter of the specified index that holds an integer.
	 * @param index the specified index
	 * @return the value of the parameter
	 */
	public int getInt(int index);
	
	/**
	 * Refreshes and redraws the graph.
	 */
	public void updateGraph();
}
