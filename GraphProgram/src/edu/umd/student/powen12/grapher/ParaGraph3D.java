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
public class ParaGraph3D extends Graph3D
{
	private static final long serialVersionUID = 1973139872093412258L;
	
	public static final int S_MIN = 100, T_MIN = 101, S_MAX = 102, T_MAX = 103, S_RES = 104, T_RES = 105;
	
	//Function values
	private Expression functionX, functionY, functionZ;
	
	//Info for future drawing
	private double[][] x, y, z;
	private double[][] nx, ny, nz;
	
	//Graph bounds
	private double sMin, sMax, tMin, tMax;
	private int sRes, tRes;
	
	//Vertex buffers
	private int orderBufferLength;
	private IntBuffer orderBuffer;
	private FloatBuffer vertexBuffer;
	private FloatBuffer normalBuffer;
	
	/**
	 * Constructs a ParaGraph3D and initializes its defaults.
	 */
	public ParaGraph3D()
	{
		functionX = null;
		functionY = null;
		functionZ = null;
		
		sMin = -1; sMax = 1;
		tMin = -1; tMax = 1;
		sRes = 50; tRes = 50;
	}
	
	public void glInitSpecial(GL2 gl)
	{
		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);
	}
	
	public void renderGraph(GL2 gl)
	{
		if (functionX != null && functionY != null && functionZ != null)
		{
			gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
			gl.glNormalPointer(GL_FLOAT, 0, normalBuffer);
			gl.glDrawElements(GL_TRIANGLES, orderBufferLength, GL_UNSIGNED_INT, orderBuffer);
		}
	}
	
	public void update(GL2 gl)
	{
		if (functionX != null && functionY != null && functionZ != null)
		{
			x = new double[sRes+1][tRes+1];
			y = new double[sRes+1][tRes+1];
			z = new double[sRes+1][tRes+1];
			
			double dx1, dy1, dz1, dx2, dy2, dz2;
			nx = new double[sRes+1][tRes+1];
			ny = new double[sRes+1][tRes+1];
			nz = new double[sRes+1][tRes+1];
			
			for (int i=0; i<=sRes; i++)
				for (int j=0; j<=tRes; j++)
				{
					double s = sMin + (sMax-sMin)*i/sRes, t = tMin + (tMax-tMin)*j/tRes;
					x[i][j] = value(functionX, s,t);
					y[i][j] = value(functionY, s,t);
					z[i][j] = value(functionZ, s,t);
					
					dx1 = partialS(functionX, s,t);
					dy1 = partialS(functionY, s,t);
					dz1 = partialS(functionZ, s,t);
					
					dx2 = partialT(functionX, s,t);
					dy2 = partialT(functionY, s,t);
					dz2 = partialT(functionZ, s,t);
					
					nx[i][j] = dy1*dz2 - dz1*dy2;
					ny[i][j] = dz1*dx2 - dx1*dz2;
					nz[i][j] = dx1*dy2 - dy1*dx2;
				}
			
			generateArrays(gl);
		}
	}
	
	//Fills all the regular buffers with the correct information for glDrawElements to draw the correct figure.
	public void generateArrays(GL2 gl)
	{
		float[] vertices = new float[(sRes+1)*(tRes+1)*3];
		float[] normals = new float[(sRes+1)*(tRes+1)*3];
		int vc = 0; //vertexCounter
		int nc = 0; //normalCounter
		
		int[][] vertexID = new int[sRes+1][tRes+1];
		int vic = 0; //vertexIDCounter
		
		int[] order = new int[sRes*tRes*6];
		int oc = 0; //orderCounter
		
		for (int i=0; i<=sRes; i++)
			for (int j=0; j<=tRes; j++)
			{
				vertexID[i][j] = -1;
			}
		
		//Vertices, normals, colors, and textures.
		for (int i=0; i<=sRes; i++)
			for (int j=0; j<=tRes; j++)
			{				
				double n;
				n = Math.sqrt(nx[i][j]*nx[i][j] + ny[i][j]*ny[i][j] + nz[i][j]*nz[i][j]);
				
				vertices[vc++] = (float)x[i][j];
				vertices[vc++] = (float)y[i][j];
				vertices[vc++] = (float)z[i][j];
				
				normals[nc++] = (float)(nx[i][j]/n);
				normals[nc++] = (float)(ny[i][j]/n);
				normals[nc++] = (float)(nz[i][j]/n);
				
				vertexID[i][j] = vic++;
			}
		
		//Order
		for (int i=0; i<sRes; i++)
			for (int j=0; j<tRes; j++)
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
	
	//Returns the value of the specified function with the specified parameters.
	public double value(Expression function, double s, double t)
	{
		if (function == null) return Double.NaN;
		return function.eval(new double[]{s, t, u, v});
	}
	
	/*
	 * Returns the partial derivative with respect to s of the
	 * specified function with the specified parameters.
	 */
	public double partialS(Expression function, double s, double t)
	{
		if (function == null) return Double.NaN;
		return function.derivative(new double[]{s, t, u, v}, 0);
	}
	
	/*
	 * Returns the partial derivative with respect to t of the
	 * specified function with the specified parameters.
	 */
	public double partialT(Expression function, double s, double t)
	{
		if (function == null) return Double.NaN;
		return function.derivative(new double[]{s, t, u, v}, 1);
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
		case S_MIN: sMin = value; break;
		case T_MIN: tMin = value; break;
		case S_MAX: sMax = value; break;
		case T_MAX: tMax = value; break;
		default: super.setDouble(index, value);
		}
	}
	
	public void setInt(int index, int value)
	{
		switch (index)
		{
		case S_RES: sRes = value; break;
		case T_RES: tRes = value; break;
		default: super.setInt(index, value);
		}
	}
	
	public double getDouble(int index)
	{
		switch (index)
		{
		case S_MIN: return sMin;
		case T_MIN: return tMin;
		case S_MAX: return sMax;
		case T_MAX: return tMax;
		default: return super.getDouble(index);
		}
	}
	
	public int getInt(int index)
	{
		switch (index)
		{
		case S_RES: return sRes;
		case T_RES: return tRes;
		default: return super.getInt(index);
		}
	}
}
