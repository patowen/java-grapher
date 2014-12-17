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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Provides a template for all 2D graphs.
 * @author Patrick Owen
 */
public abstract class Graph2D extends JPanel implements Graph, ComponentListener
{
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_WIDTH = 640, DEFAULT_HEIGHT = 640;
	
	public static final int X_MIN = 0, X_MAX = 1, X_SCALE = 2, Y_MIN = 3, Y_MAX = 4, Y_SCALE = 5;
	
	/*
	 * u and v are the mouse x and y coordinates when the user is dynamically
	 * editing the graph.
	 */
	protected double u, v;
	
	private double xMin, xMax, xScale;
	private double yMin, yMax, yScale;
	private int axisLength;
	private boolean mouseView, showAxes;
	
	private double mouseSensitivityDistance = 1.003;
	private double scrollSensitivityDistance = 1.3;
	private int mouseX, mouseY, mouseButton;
	private double mouseXBase, mouseYBase;
	
	private BufferedImage graphImage;
	private BufferedImage nextImage;
	
	/**
	 * Constructs a Graph2D with all its default parameters.
	 */
	public Graph2D()
	{
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setFocusable(true);
		
		xMin = -10;
		xMax = 10;
		yMin = -10;
		yMax = 10;
		xScale = 1;
		yScale = 1;
		axisLength = 5;
		mouseView = true;
		showAxes = true;
		
		u = 0;
		v = 0;
		
		//Add mouse controls
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				Graph2D.this.requestFocus();
				
				if (mouseView)
				{
					mouseXBase = getX(e.getX());
					mouseYBase = getY(e.getY());
					mouseX = e.getX();
					mouseY = e.getY();
					mouseButton = e.getButton();
					
					if (e.isShiftDown())
						mouseButton = MouseEvent.BUTTON3;
				}
				else
				{
					setMouseCoordinates(getX(e.getX()), getY(e.getY()));
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				if (!mouseView)
				{
					setMouseCoordinates(getX(e.getX()), getY(e.getY()));
				}
				else if (mouseButton == MouseEvent.BUTTON1)
				{
					double xDiff = -(getX(e.getX())-getX(mouseX));
					double yDiff = -(getY(e.getY())-getY(mouseY));
					
					xMin += xDiff; xMax += xDiff;
					yMin += yDiff; yMax += yDiff;
				}
				else if (mouseButton == MouseEvent.BUTTON3)
				{
					double factor = Math.pow(mouseSensitivityDistance, e.getY()-mouseY);
					xMin = (xMin-mouseXBase)*factor+mouseXBase;
					yMin = (yMin-mouseYBase)*factor+mouseYBase;
					xMax = (xMax-mouseXBase)*factor+mouseXBase;
					yMax = (yMax-mouseYBase)*factor+mouseYBase;
				}
				
				mouseX = e.getX();
				mouseY = e.getY();
				
				updateGraph();
			}
		});
		
		addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				double factor = Math.pow(scrollSensitivityDistance, e.getWheelRotation());
				mouseXBase = getX(e.getX());
				mouseYBase = getY(e.getY());
				xMin = (xMin-mouseXBase)*factor+mouseXBase;
				yMin = (yMin-mouseYBase)*factor+mouseYBase;
				xMax = (xMax-mouseXBase)*factor+mouseXBase;
				yMax = (yMax-mouseYBase)*factor+mouseYBase;
				
				updateGraph();
			}
		});
		
		addComponentListener(this);
		
		graphImage = null;
	}
	
	public void paint(Graphics g)
	{
		if (graphImage != null)
		{
			g.drawImage(graphImage, 0, 0, null);
		}
	}
	
	/**
	 * Draws the graph and its axes. Override this method
	 * and include a call to <code>super(g)</code> to make sure
	 * the axes remain.
	 */
	public void paintGraph(Graphics g)
	{
		//Background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		//Axes
		if (showAxes)
		{
			g.setColor(Color.BLACK);
			drawAxes(g);
		}
		
		//Graph
		g.setColor(Color.BLACK);
	}
	
	//Draw axes with tick marks if appropriate
	private void drawAxes(Graphics g)
	{
		//X-axis
		int xAxisPos = (int)getJ(0);
		if (xAxisPos >= 0 && xAxisPos <= getHeight())
		{
			g.drawLine(0, xAxisPos, getWidth(), xAxisPos);
			double xStart = getI(Math.ceil(getX(0)/xScale)*xScale);
			double xInterval = xScale*getWidth()/(xMax-xMin);
			if (xInterval >= axisLength+1) //Are tick marks wide enough apart?
			{
				for (double xCurrent = xStart; xCurrent<getWidth(); xCurrent+=xInterval)
					if (Math.abs((int)Math.round(xCurrent)-(int)Math.round(getI(0))) > 1)
						g.drawLine((int)Math.round(xCurrent), xAxisPos-axisLength, (int)Math.round(xCurrent), xAxisPos+axisLength);
			}
		}
		
		//Y-axis
		int yAxisPos = (int)getI(0);
		if (yAxisPos >= 0 && yAxisPos <= getWidth())
		{
			g.drawLine(yAxisPos, 0, yAxisPos, getHeight());
			double yStart = getJ(Math.ceil(getY(getHeight())/yScale)*yScale);
			double yInterval = yScale*getHeight()/(yMax-yMin);
			if (yInterval >= axisLength+1) //Are tick marks wide enough apart?
			{
				for (double yCurrent = yStart; yCurrent>0; yCurrent-=yInterval)
					if (Math.abs((int)Math.round(yCurrent)-(int)Math.round(getJ(0))) > 1)
						g.drawLine(yAxisPos-axisLength, (int)Math.round(yCurrent), yAxisPos+axisLength, (int)Math.round(yCurrent));
			}
		}
	}
	
	//Sets u and v properly given the mouse coordinates x and y relative to the viewport.
	private void setMouseCoordinates(double x, double y)
	{
		u = x;
		v = y;
		updateGraph();
	}
	
	public void setShowAxes(boolean showAxes)
	{
		this.showAxes = showAxes;
		updateGraph();
	}
	
	public void setMouseView(boolean enabled)
	{
		mouseView = enabled;
	}
	
	/**
	 * Converts the parameter from an x-coordinate in pixel space to an
	 * x-coordinate in graph space.
	 * @param i the x-coordinate in pixel space
	 * @return the x-coordinate in graph space
	 */
	protected double getX(int i)
	{
		return ((double)i/getWidth())*(xMax-xMin) + xMin;
	}
		
	/**
	 * Converts the parameter from an y-coordinate in pixel space to an
	 * y-coordinate in graph space.
	 * @param j the y-coordinate in pixel space
	 * @return the y-coordinate in graph space
	 */
	protected double getY(int j)
	{
		return ((double)j/getHeight())*(yMin-yMax) + yMax;
	}
		
	/**
	 * Converts the parameter from an x-coordinate in graph space to an
	 * x-coordinate in pixel space.
	 * @param x the x-coordinate in graph space
	 * @return the x-coordinate in pixel space
	 */
	protected double getI(double x)
	{
		return (x-xMin)/(xMax-xMin)*getWidth();
	}
		
	/**
	 * Converts the parameter from an y-coordinate in graph space to an
	 * y-coordinate in pixel space.
	 * @param y the y-coordinate in graph space
	 * @return the y-coordinate in pixel space
	 */
	protected double getJ(double y)
	{
		return (y-yMax)/(yMin-yMax)*getHeight();
	}
	
	public void setDouble(int index, double value)
	{
		switch (index)
		{
		case X_MIN: xMin = value; break;
		case X_MAX: xMax = value; break;
		case X_SCALE: xScale = value; break;
		case Y_MIN: yMin = value; break;
		case Y_MAX: yMax = value; break;
		case Y_SCALE: yScale = value; break;
		}
	}
	
	public void setInt(int index, int value)
	{
		throw new IllegalArgumentException("Invalid index");
	}
	
	public double getDouble(int index)
	{
		switch (index)
		{
		case X_MIN: return xMin;
		case X_MAX: return xMax;
		case X_SCALE: return xScale;
		case Y_MIN: return yMin;
		case Y_MAX: return yMax;
		case Y_SCALE: return yScale;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	public int getInt(int index)
	{
		throw new IllegalArgumentException("Invalid index");
	}
	
	public void updateGraph()
	{
		paintGraph(nextImage.getGraphics());
		BufferedImage temp = graphImage;
		graphImage = nextImage;
		nextImage = temp;
		repaint();
	}
	
	public void componentHidden(ComponentEvent e) {}
	
	public void componentMoved(ComponentEvent e) {}
	
	public void componentResized(ComponentEvent e)
	{
		graphImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		nextImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		updateGraph();
	}
	
	public void componentShown(ComponentEvent e) {}
}
