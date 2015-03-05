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
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL2ES1.GL_LIGHT_MODEL_AMBIENT;
import static javax.media.opengl.GL2ES1.GL_LIGHT_MODEL_TWO_SIDE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

/**
 * Provides a template for all 3D graphs.
 * @author Patrick Owen
 */
public abstract class Graph3D extends GLCanvas implements Graph
{
	private static final long serialVersionUID = 1973139872093412258L;
	
	private static final int DEFAULT_WIDTH = 640, DEFAULT_HEIGHT = 640;
	
	public static final int X_CENTER = 0, Y_CENTER = 1, Z_CENTER = 2, VIEW_DISTANCE = 3;
	
	/*
	 * u and v are the mouse x and y coordinates when the user is dynamically
	 * editing the graph.
	 */
	protected double u, v;
	
	//Camera controls
	private double mouseSensitivityAngle = 0.01;
	private double mouseSensitivityDistance = 1.003;
	private double scrollSensitivityDistance = 1.3;
//	private double mouseSensitivityCenter = 0.002;
	private int mouseX, mouseY, mouseButton;
	private boolean mouseView, showAxes;
	protected boolean axesInverted; //axesInverted switches y and z axes
	
	//Camera position
	private double horizontalDir;
	private double verticalDir;
	private double distance;
	private double centerX, centerY, centerZ;
	
	//Used to update the graph
	private boolean updateFlag;
	
	/**
	 * Constructs a Graph3D with all its default parameters.
	 */
	public Graph3D()
	{		
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setFocusable(true);
		
		mouseView = true;
		showAxes = true;
		axesInverted = false;
		
		addGLEventListener(new GLEventListener()
		{
			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
			
			public void init(GLAutoDrawable drawable)
			{
				GL2 gl = drawable.getGL().getGL2();
				gl.glClearColor(0, 0, 0, 1);
				
				gl.glEnable(GL_DEPTH_TEST);
				
				//Makes lighting more realistic. Uncomment this for realistic shading (gamma correction)
				//gl.glEnable(GL_FRAMEBUFFER_SRGB);
				
				gl.glEnable(GL_LIGHTING);
				gl.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, 1);
				gl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, new float[] {0, 0, 0, 1}, 0);
				gl.glEnable(GL_LIGHT0);
				gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[] {0, 0, 0, 1}, 0);
				gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] {0.9f, 0.9f, 0.9f, 1}, 0);
				gl.glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] {0.1f, 0.1f, 0.1f, 1}, 0);
				
				update(gl);
				
				glInitSpecial(gl);
			}
			
			public void dispose(GLAutoDrawable drawable) {}
			
			public void display(GLAutoDrawable drawable)
			{
				render(drawable.getGL().getGL2());
			}
		});
		
		//Add mouse controls
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if (mouseView)
				{
					mouseX = e.getX();
					mouseY = e.getY();
					mouseButton = e.getButton();
					
					if (e.isShiftDown())
						mouseButton = MouseEvent.BUTTON3;
					if (e.isControlDown())
						mouseButton = MouseEvent.BUTTON2;
				}
				else
				{
					setMouseCoordinates(e.getX(), e.getY());
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter()
		{			
			public void mouseDragged(MouseEvent e)
			{
				if (!mouseView)
				{
					setMouseCoordinates(e.getX(), e.getY());
				}
				else if (mouseButton == MouseEvent.BUTTON1) //Rotate
				{
					horizontalDir -= mouseSensitivityAngle*(e.getX()-mouseX);
					verticalDir += mouseSensitivityAngle*(e.getY()-mouseY);
					
					//Vertical limits
					if (verticalDir > Math.PI/2) verticalDir = Math.PI/2;
					else if (verticalDir < -Math.PI/2) verticalDir = -Math.PI/2;
				}
				else if (mouseButton == MouseEvent.BUTTON3) //Zoom
				{
					distance *= Math.pow(mouseSensitivityDistance, e.getY()-mouseY);
				}
				else if (mouseButton == MouseEvent.BUTTON2) //Move camera
				{
//					centerX += Math.sin(horizontalDir)*(e.getX()-mouseX)*distance*mouseSensitivityCenter;
//					centerY += -Math.cos(horizontalDir)*(e.getX()-mouseX)*distance*mouseSensitivityCenter;
//					
//					centerZ += (e.getY()-mouseY)*distance*mouseSensitivityCenter;
				}
				
				mouseX = e.getX();
				mouseY = e.getY();
				
				display();
			}
		});
		
		addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				distance *= Math.pow(scrollSensitivityDistance, e.getWheelRotation());
				
				display();
			}
		});
		
		horizontalDir = Math.PI/4;
		verticalDir = Math.PI/6;
		distance = 4;
		centerX = 0;
		centerY = 0;
		centerZ = 0;
		
		updateFlag = false;
	}
	
	public void updateGraph()
	{
		updateFlag = true;
		display();
	}
	
	/**
	 * Updates the graph but does not yet display the updated graph.
	 */
	protected abstract void update(GL2 gl);
	
	/**
	 * Initializes GL-related attributes other than the defaults.
	 * @param gl The GL2 object used for initialization.
	 */
	protected abstract void glInitSpecial(GL2 gl);
	
	/**
	 * Renders the graph. All the proper transformations of the view have already happened. 3D graphs
	 * generally use derivatives to choose the proper shading.
	 * @param gl The GL2 object used to render the graph.
	 */
	protected abstract void renderGraph(GL2 gl);
	
	//Sets u and v properly given the mouse coordinates x and y relative to the viewport.
	private void setMouseCoordinates(double x, double y)
	{
		u = 2*x/getWidth() - 1;
		v = 1 - 2*y/getHeight();
		updateGraph();
		display();
	}
	
	/*
	 * Renders the graph by setting everything up, showing axes if appropriate,
	 * and calling the renderGraph method, which is implemented by subclasses.
	 */
	private void render(GL2 gl)
	{
		if (updateFlag)
		{
			updateFlag = false;
			update(gl);
		}
		
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		GLU glu = new GLU();
		
		glu.gluPerspective(45, (double)getWidth()/getHeight(), distance/20, distance*50);
		
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(centerX+distance*Math.cos(horizontalDir)*Math.cos(verticalDir),
				centerY+distance*Math.sin(horizontalDir)*Math.cos(verticalDir),
				centerZ+distance*Math.sin(verticalDir),
				centerX, centerY, centerZ,
				-Math.cos(horizontalDir)*Math.sin(verticalDir),
				-Math.sin(horizontalDir)*Math.sin(verticalDir),
				Math.cos(verticalDir));
		
		if (showAxes)
		{
			double dist = Math.abs(centerX);
			if (Math.abs(centerY) > dist) dist = Math.abs(centerY);
			if (Math.abs(centerZ) > dist) dist = Math.abs(centerZ);
			dist += distance;
			
			gl.glDisable(GL_LIGHTING);
			
			gl.glColor3d(1, 0, 0);
			gl.glBegin(GL_LINES);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(dist, 0, 0);
			gl.glEnd();
			
			if (axesInverted)
				gl.glColor3d(0, 0, 1);
			else
				gl.glColor3d(0, 1, 0);
			gl.glBegin(GL_LINES);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(0, dist, 0);
			gl.glEnd();
			
			if (axesInverted)
				gl.glColor3d(0, 1, 0);
			else
				gl.glColor3d(0, 0, 1);
			gl.glBegin(GL_LINES);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(0, 0, dist);
			gl.glEnd();
			
			gl.glEnable(GL_LIGHTING);
		}
		
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, new float[] {1, 1, 1, 1}, 0);
		renderGraph(gl);
	}
	
	public void setDouble(int index, double value)
	{
		switch (index)
		{
		case X_CENTER: centerX = value; break;
		case Y_CENTER: centerY = value; break;
		case Z_CENTER: centerZ = value; break;
		case VIEW_DISTANCE: distance = value; break;
		default: throw new IllegalArgumentException("Invalid index");
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
		case X_CENTER: return centerX;
		case Y_CENTER: return centerY;
		case Z_CENTER: return centerZ;
		case VIEW_DISTANCE: return distance;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	public int getInt(int index)
	{
		throw new IllegalArgumentException("Invalid index");
	}
	
	public void setMouseView(boolean newMouseView)
	{
		mouseView = newMouseView;
	}
	
	public void setShowAxes(boolean enabled)
	{
		showAxes = enabled;
		updateGraph();
	}
}
