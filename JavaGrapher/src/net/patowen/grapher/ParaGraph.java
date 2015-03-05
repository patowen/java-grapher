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
 * Represents a parametric graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class ParaGraph extends Graph2D
{
	private static final long serialVersionUID = 1L;
	
	public static final int T_MIN = 100, T_MAX = 101, T_RES = 102;
	
	private Expression functionX;
	private Expression functionY;
	
	private double tMin, tMax;
	int tRes;
	
	/**
	 * Constructs a ParaGraph and initializes its defaults.
	 */
	public ParaGraph()
	{
		super();
		
		functionX = null;
		functionY = null;
		
		tMin = -1;
		tMax = 1;
		tRes = 1000;
		
		repaint();
	}
	
	public Expression getExpression(int index)
	{
		switch (index)
		{
		case 0: return functionX;
		case 1: return functionY;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	public void setExpression(int index, Expression value)
	{
		switch (index)
		{
		case 0: functionX = value; break;
		case 1: functionY = value; break;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	public void paintGraph(Graphics g)
	{
		super.paintGraph(g);
		Graphics2D g2 = (Graphics2D)g;
		
		//Draw the graph
		if (functionX != null && functionY != null)
		{
			g2.setColor(Color.BLACK);
			double x1, x2, y1, y2;
			for (int i=0; i<tRes; i++)
			{
				//Draw line segments between the correct points for each t-value.
				double t1 = tMin+i*(tMax-tMin)/tRes, t2 = tMin+(i+1)*(tMax-tMin)/tRes;
				x1 = functionX.eval(new double[]{t1, u, v});
				x2 = functionX.eval(new double[]{t2, u, v});
				y1 = functionY.eval(new double[]{t1, u, v});
				y2 = functionY.eval(new double[]{t2, u, v});
				if (!Double.isNaN(y1) && !Double.isNaN(y2) && !Double.isInfinite(y1) && !Double.isInfinite(y2))
					g2.draw(new Line2D.Double(getI(x1), getJ(y1), getI(x2), getJ(y2)));
			}
		}
	}
	
	public void setDouble(int index, double value)
	{
		switch (index)
		{
		case T_MIN: tMin = value; break;
		case T_MAX: tMax = value; break;
		default: super.setDouble(index, value);
		}
	}
	
	public void setInt(int index, int value)
	{
		switch (index)
		{
		case T_RES: tRes = value; break;
		default: super.setInt(index, value);
		}
	}
	
	public double getDouble(int index)
	{
		switch (index)
		{
		case T_MIN: return tMin;
		case T_MAX: return tMax;
		default: return super.getDouble(index);
		}
	}
	
	public int getInt(int index)
	{
		switch (index)
		{
		case T_RES: return tRes;
		default: return super.getInt(index);
		}
	}
}
