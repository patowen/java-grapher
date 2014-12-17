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
import java.awt.Color;
import java.awt.Graphics;

import edu.umd.student.powen12.grapher.math.Expression;

/**
 * Represents a general graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class GeneralGraph extends Graph2D
{
	private static final long serialVersionUID = 1L;
	private Expression function;
	private boolean inversion;
	
	/**
	 * Constructs a GeneralGraph and initializes its defaults.
	 */
	public GeneralGraph()
	{
		super();
		
		function = null;
		
		repaint();
	}
	
	public Expression getExpression(int index)
	{
		return function;
	}
	
	public void setExpression(int index, Expression e)
	{
		function = e;
		repaint();
	}
	
	/**
	 * Sets whether the graph will display the inverted version of
	 * the given function.
	 */
	public void setInversion(boolean invert)
	{
		inversion = invert;
		repaint();
	}
	
	public void paintGraph(Graphics g)
	{
		super.paintGraph(g);
		
		//Graph
		g.setColor(Color.BLACK);
		
		if (function != null)
		{
			drawColumns(g);
			drawRows(g);
		}
	}
	
	/*
	 * Scans each column for when the graph switches from positive to negative or vice versa, does some checks,
	 * and, if appropriate, plots a point.
	 */
	private void drawColumns(Graphics g)
	{
		double x, y1, y2, val1, val2, val3;
		for (int i=0; i<getWidth(); i++)
		{
			x = getX(i);
			for (int j=0; j<getHeight()-1; j++)
			{
				y1 = getY(j);
				y2 = getY(j+1);
				val1 = function.eval(transform(x,y1));
				val2 = function.eval(transform(x,y2));
				
				//Plots a point at the specified position if it should.
				if (val1 > 0 && val2 < 0)
				{
					val3 = function.eval(transform(x,(y1+y2)/2));
					
					/*
					 * The first condition (e.g. val3 <= 0) in each clause makes sure the closest point to the actual point that hits
					 * 0 is plotted. This prevents lines in this graph from thickening.
					 * 
					 * The second condition (e.g. val3 > val2) in each clause makes sure the curve is going
					 * the right direction not to be an infinite discontinuity and is actually crossing 0. It
					 * is not perfect, but it helps with most cases.
					 */
					if (val3 <= 0 && val3 > val2)
						g.drawLine(i, j, i, j);
					else if (val3 > 0 && val3 < val1)
						g.drawLine(i, j+1, i, j+1);
				}
				else if (val1 < 0 && val2 > 0)
				{
					val3 = function.eval(transform(x,(y1+y2)/2));
					if (val3 >= 0 && val3 < val2)
						g.drawLine(i, j, i, j);
					else if (val3 < 0 && val3 > val1)
						g.drawLine(i, j+1, i, j+1);
				}
				else if (val1 == 0)
					g.drawLine(i, j, i, j);
				else if (val2 == 0)
					g.drawLine(i, j+1, i, j+1);
				
			}
		}
	}
	
	/*
	 * Scans each row for when the graph switches from positive to negative or vice versa, does some checks,
	 * and, if appropriate, plots a point. See drawColumns for in-method details.
	 */
	private void drawRows(Graphics g)
	{
		double y, x1, x2, val1, val2, val3;
		for (int i=0; i<getHeight(); i++)
		{
			y = getY(i);
			for (int j=0; j<getWidth()-1; j++)
			{
				x1 = getX(j);
				x2 = getX(j+1);
				val1 = function.eval(transform(x1,y));
				val2 = function.eval(transform(x2,y));
				
				//Plots a point at the specified position if it should.
				if (val1 > 0 && val2 < 0)
				{
					val3 = function.eval(transform((x1+x2)/2,y));
					if (val3 <= 0 && val3 > val2)
						g.drawLine(j, i, j, i);
					else if (val3 > 0 && val3 < val1)
						g.drawLine(j+1, i, j+1, i);
				}
				else if (val1 < 0 && val2 > 0)
				{
					val3 = function.eval(transform((x1+x2)/2,y));
					if (val3 >= 0 && val3 < val2)
						g.drawLine(j, i, j, i);
					else if (val3 < 0 && val3 > val1)
						g.drawLine(j+1, i, j+1, i);
				}
				else if (val1 == 0)
					g.drawLine(j, i, j, i);
				else if (val2 == 0)
					g.drawLine(j+1, i, j+1, i);
				
			}
		}
	}
	
	private double[] transform(double x, double y)
	{
		if (inversion)
		{
			return new double[]{x/(x*x+y*y), y/(x*x+y*y), u, v};
		}
		return new double[]{x, y, u, v};
	}
}
