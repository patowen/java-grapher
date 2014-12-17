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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;

import com.jogamp.common.nio.Buffers;

import edu.umd.student.powen12.grapher.math.Expression;

/**
 * Represents a 3D parametric graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class ParaCurve3D extends Graph3D
{
	private static final long serialVersionUID = 1973139872093412258L;
	
	public static final int T_MIN = 100, T_MAX = 101, T_RES = 102;
	
	//Function values
	private Expression functionX, functionY, functionZ;
	
	//Info for future drawing
	private double[] x, y, z;
	
	//Graph bounds
	private double tMin, tMax;
	private int tRes;
	
	//Vertex buffers
	private int orderBufferLength;
	private IntBuffer orderBuffer;
	private FloatBuffer vertexBuffer;
	
	/**
	 * Constructs a ParaGraph3D and initializes its defaults.
	 */
	public ParaCurve3D()
	{
		functionX = null;
		functionY = null;
		functionZ = null;
		
		tMin = -1; tMax = 1;
		tRes = 1000;
	}
	
	public void glInitSpecial(GL2 gl)
	{
		gl.glEnableClientState(GL_VERTEX_ARRAY);
	}
	
	public void renderGraph(GL2 gl)
	{
		if (functionX != null && functionY != null && functionZ != null)
		{
			gl.glDisable(GL_LIGHTING);
			gl.glColor3f(1, 1, 1);
			gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
			gl.glDrawElements(GL_LINES, orderBufferLength, GL_UNSIGNED_INT, orderBuffer);
			gl.glEnable(GL_LIGHTING);
		}
	}
	
	public void update(GL2 gl)
	{
		if (functionX != null && functionY != null && functionZ != null)
		{
			x = new double[tRes+1];
			y = new double[tRes+1];
			z = new double[tRes+1];
			
			for (int i=0; i<=tRes; i++)
			{
				double t = tMin + (tMax-tMin)*i/tRes;
				x[i] = value(functionX, t);
				y[i] = value(functionY, t);
				z[i] = value(functionZ, t);
			}
			
			generateArrays(gl);
		}
	}
	
	//Fills all the regular buffers with the correct information for glDrawElements to draw the correct figure.
	public void generateArrays(GL2 gl)
	{
		float[] vertices = new float[(tRes+1)*3];
		int vc = 0; //vertexCounter
		
		int[] vertexID = new int[tRes+1];
		int vic = 0; //vertexIDCounter
		
		int[] order = new int[tRes*2];
		int oc = 0; //orderCounter
		
		for (int i=0; i<=tRes; i++)
		{
			vertexID[i] = -1;
		}
		
		//Vertices, normals, colors, and textures.
		for (int i=0; i<=tRes; i++)
		{			
			vertices[vc++] = (float)x[i];
			vertices[vc++] = (float)y[i];
			vertices[vc++] = (float)z[i];
			
			vertexID[i] = vic++;
		}
		
		//Order
		for (int i=0; i<tRes; i++)
		{
			if (vertexID[i] < 0 || vertexID[i+1] < 0)
				continue;
			
			order[oc++] = vertexID[i];
			order[oc++] = vertexID[i+1];
		}
		
		vertexBuffer = Buffers.newDirectFloatBuffer(vertices.length);
		vertexBuffer.put(vertices);
		vertexBuffer.rewind();
		
		orderBuffer = Buffers.newDirectIntBuffer(order.length);
		orderBuffer.put(order);
		orderBuffer.rewind();
		
		orderBufferLength = oc;
	}
	
	//Returns the value of the specified function with the specified parameters.
	public double value(Expression function, double t)
	{
		if (function == null) return Double.NaN;
		return function.eval(new double[]{t, u, v});
	}
	
	public void setExpression(int index, Expression e)
	{
		switch (index)
		{
		case 0: functionX = e; break;
		case 1: functionY = e; break;
		case 2: functionZ = e; break;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	public Expression getExpression(int index)
	{
		switch (index)
		{
		case 0: return functionX;
		case 1: return functionY;
		case 2: return functionZ;
		default: throw new IllegalArgumentException("Invalid index");
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
