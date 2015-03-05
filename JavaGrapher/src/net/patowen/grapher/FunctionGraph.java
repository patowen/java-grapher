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

package net.patowen.grapher;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import net.patowen.grapher.math.Expression;

/**
 * Represents a function graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class FunctionGraph extends Graph2D
{
	private static final long serialVersionUID = 1L;
	private Expression function;
	
	public FunctionGraph()
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
	}
	
	public void paintGraph(Graphics g)
	{
		super.paintGraph(g);
		Graphics2D g2 = (Graphics2D)g;
		
		//Graph
		if (function != null)
		{
			g2.setColor(Color.BLACK);
			double x1, x2, y1, y2;
			for (int i=0; i<getWidth()-1; i++)
			{
				//Actual values
				x1 = getX(i);
				x2 = getX(i+1);
				y1 = function.eval(new double[]{x1, u, v});
				y2 = function.eval(new double[]{x2, u, v});
				if (!Double.isNaN(y1) && !Double.isNaN(y2) && !Double.isInfinite(y1) && !Double.isInfinite(y2))
					g2.draw(new Line2D.Double(i, getJ(y1), (i+1), getJ(y2)));
			}
		}
	}
}
