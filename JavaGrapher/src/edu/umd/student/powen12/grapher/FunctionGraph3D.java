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
 * Represents a 3D function graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class FunctionGraph3D extends Graph3D
{
	private static final long serialVersionUID = 1973139872093412258L;
	
	public static final int X_MIN = 100, Y_MIN = 101, X_MAX = 102, Y_MAX = 103, X_RES = 104, Y_RES = 105;
	
	//Function values
	private Expression function;
	
	//Info for future drawing
	private double[][] z;
	private double[][] dx;
	private double[][] dy;
	
	//Graph bounds
	private double xMin, yMin, xMax, yMax;
	private int xRes, yRes;
	
	//Vertex buffers
	private int orderBufferLength;
	private IntBuffer orderBuffer;
	private FloatBuffer vertexBuffer;
	private FloatBuffer normalBuffer;
	
	/**
	 * Constructs a FunctionGraph3D and initializes its defaults.
	 */
	public FunctionGraph3D()
	{
		function = null;
		xMin = -1; yMin = -1; xMax = 1; yMax = 1;
		xRes = 50; yRes = 50;
	}
	
	public void glInitSpecial(GL2 gl)
	{
		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);
	}
	
	public void renderGraph(GL2 gl)
	{
		if (function != null)
		{
			gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
			gl.glNormalPointer(GL_FLOAT, 0, normalBuffer);
			gl.glDrawElements(GL_TRIANGLES, orderBufferLength, GL_UNSIGNED_INT, orderBuffer);
		}
	}
	
	public void update(GL2 gl)
	{
		if (function != null)
		{
			z = new double[xRes+1][yRes+1];
			dx = new double[xRes+1][yRes+1];
			dy = new double[xRes+1][yRes+1];
			
			for (int i=0; i<=xRes; i++)
				for (int j=0; j<=yRes; j++)
				{
					double x = xMin + (xMax-xMin)*i/xRes, y = yMin + (yMax-yMin)*j/yRes;
					z[i][j] = value(x,y);
					dx[i][j] = partialX(x,y);
					dy[i][j] = partialY(x,y);
				}
			
			generateArrays(gl);
		}
	}
	
	//Fills all the buffers with the correct information for glDrawElements to draw the correct figure.
	private void generateArrays(GL2 gl)
	{
		float[] vertices = new float[(xRes+1)*(yRes+1)*3];
		float[] normals = new float[(xRes+1)*(yRes+1)*3];
		int vc = 0; //vertexCounter
		int nc = 0; //normalCounter
		
		int[][] vertexID = new int[xRes+1][yRes+1];
		int vic = 0; //vertexIDCounter
		
		int[] order = new int[xRes*yRes*6];
		int oc = 0; //orderCounter
		
		for (int i=0; i<=xRes; i++)
			for (int j=0; j<=yRes; j++)
			{
				vertexID[i][j] = -1;
			}
		
		//Vertices and normals.
		for (int i=0; i<=xRes; i++)
			for (int j=0; j<=yRes; j++)
			{
				double x = xMin + (xMax-xMin)*i/xRes, y = yMin + (yMax-yMin)*j/yRes;
				
				double nx, ny, nz, n;
				nx = -dx[i][j]; ny = -dy[i][j]; nz = 1;
				n = Math.sqrt(nx*nx + ny*ny + nz*nz);
				
				vertices[vc++] = (float)x;
				vertices[vc++] = (float)y;
				vertices[vc++] = (float)z[i][j];
				
				normals[nc++] = (float)(nx/n);
				normals[nc++] = (float)(ny/n);
				normals[nc++] = (float)(nz/n);
				
				vertexID[i][j] = vic++;
			}
		
		//Order
		for (int i=0; i<xRes; i++)
			for (int j=0; j<yRes; j++)
			{
				if (vertexID[i][j] < 0 || vertexID[i+1][j] < 0 || vertexID[i+1][j+1] < 0 || vertexID[i][j+1] < 0)
					continue;
				
				order[oc++] = vertexID[i][j];
				order[oc++] = vertexID[i+1][j];
				order[oc++] = vertexID[i+1][j+1];
				
				order[oc++] = vertexID[i][j];
				order[oc++] = vertexID[i+1][j+1];
				order[oc++] = vertexID[i][j+1];
			}
		
		vertexBuffer = Buffers.newDirectFloatBuffer(vertices.length);
		vertexBuffer.put(vertices);
		vertexBuffer.rewind();
		
		normalBuffer = Buffers.newDirectFloatBuffer(normals.length);
		normalBuffer.put(normals);
		normalBuffer.rewind();
		
		orderBuffer = Buffers.newDirectIntBuffer(order.length);
		orderBuffer.put(order);
		orderBuffer.rewind();
		
		orderBufferLength = oc;
	}
	
	//Returns the value of z at the specified position.
	private double value(double x, double y)
	{
		return function.eval(new double[]{x, y, u, v});
	}
	
	//Returns the partial derivative with respect to x at the specified position.
	private double partialX(double x, double y)
	{
		return function.derivative(new double[]{x, y, u, v}, 0);
	}
	
	//Returns the partial derivative with respect to y at the specified position.
	private double partialY(double x, double y)
	{
		return function.derivative(new double[]{x, y, u, v}, 1);
	}
	
	public void setExpression(int index, Expression e)
	{
		function = e;
	}
	
	public Expression getExpression(int index)
	{
		return function;
	}
	
	public void setDouble(int index, double value)
	{
		switch (index)
		{
		case X_MIN: xMin = value; break;
		case Y_MIN: yMin = value; break;
		case X_MAX: xMax = value; break;
		case Y_MAX: yMax = value; break;
		default: super.setDouble(index, value);
		}
	}
	
	public void setInt(int index, int value)
	{
		switch (index)
		{
		case X_RES: xRes = value; break;
		case Y_RES: yRes = value; break;
		default: super.setInt(index, value);
		}
	}
	
	public double getDouble(int index)
	{
		switch (index)
		{
		case X_MIN: return xMin;
		case Y_MIN: return yMin;
		case X_MAX: return xMax;
		case Y_MAX: return yMax;
		default: return super.getDouble(index);
		}
	}
	
	public int getInt(int index)
	{
		switch (index)
		{
		case X_RES: return xRes;
		case Y_RES: return yRes;
		default: return super.getInt(index);
		}
	}
}
