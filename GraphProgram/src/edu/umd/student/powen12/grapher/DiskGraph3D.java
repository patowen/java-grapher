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
 * Represents a disk/washer graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class DiskGraph3D extends Graph3D
{
	private static final long serialVersionUID = 1973139872093412258L;
	
	public static final int X_MIN = 100, X_MAX = 101, X_RES = 102, ROT_RES = 103;
	
	//Function values
	private Expression function1;
	private Expression function2;
	
	//Info for future drawing
	private double[] y1, y2;
	private double[] dx1, dx2;
	
	//Graph bounds
	private double xMin, xMax;
	private int xRes, rotRes;
	
	//Vertex buffers
	private int orderBufferLength;
	private IntBuffer orderBuffer;
	private FloatBuffer vertexBuffer;
	private FloatBuffer normalBuffer;
	
	private int orderBufferCapsLength;
	private IntBuffer orderBufferCaps;
	private FloatBuffer vertexBufferCaps;
	private FloatBuffer normalBufferCaps;
	
	/**
	 * Constructs a DiskGraph3D and initializes its defaults.
	 */
	public DiskGraph3D()
	{
		function1 = null;
		function2 = null;
		xMin = -1; xMax = 1;
		xRes = 50; rotRes = 50;
	}
	
	public void glInitSpecial(GL2 gl)
	{
		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);
	}
	
	public void renderGraph(GL2 gl)
	{
		if (function1 != null && function2 != null)
		{
			gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
			gl.glNormalPointer(GL_FLOAT, 0, normalBuffer);
			gl.glDrawElements(GL_TRIANGLES, orderBufferLength, GL_UNSIGNED_INT, orderBuffer);
			
			gl.glVertexPointer(3, GL_FLOAT, 0, vertexBufferCaps);
			gl.glNormalPointer(GL_FLOAT, 0, normalBufferCaps);
			gl.glDrawElements(GL_TRIANGLES, orderBufferCapsLength, GL_UNSIGNED_INT, orderBufferCaps);
		}
	}
	
	public void update(GL2 gl)
	{
		if (function1 != null && function2 != null)
		{
			y1 = new double[xRes+1];
			y2 = new double[xRes+1];
			dx1 = new double[xRes+1];
			dx2 = new double[xRes+1];
			
			for (int i=0; i<=xRes; i++)
			{
				double x = xMin + (xMax-xMin)*i/xRes;
				y1[i] = value1(x);
				y2[i] = value2(x);
				dx1[i] = partialX1(x);
				dx2[i] = partialX2(x);
			}
			
			generateArrays(gl);
			generateArraysCaps(gl);
		}
	}
	
	//Fills all the regular buffers with the correct information for glDrawElements to draw the correct figure.
	private void generateArrays(GL2 gl)
	{
		float[] vertices = new float[(xRes+1)*(rotRes+1)*6];
		float[] normals = new float[(xRes+1)*(rotRes+1)*6];
		int vc = 0; //vertexCounter
		int nc = 0; //normalCounter
		
		int[][] vertexID1 = new int[xRes+1][rotRes+1];
		int[][] vertexID2 = new int[xRes+1][rotRes+1];
		int vic = 0; //vertexIDCounter
		
		int[] order = new int[xRes*rotRes*12];
		int oc = 0; //orderCounter
		
		//Vertices and normals.
		for (int i=0; i<=xRes; i++)
			for (int j=0; j<=rotRes; j++)
			{
				double x = xMin + (xMax-xMin)*i/xRes, rot = 2*Math.PI*j/rotRes;
				
				double nx1, ny1, nz1, n1, nx2, ny2, nz2, n2;
				nx1 = dx1[i]; ny1 = -Math.cos(rot); nz1 = -Math.sin(rot);
				n1 = Math.sqrt(nx1*nx1 + ny1*ny1 + nz1*nz1);
				nx1 /= n1; ny1 /= n1; nz1 /= n1;
				if (y1[i] < 0) {nx1 = -nx1; ny1 = -ny1; nz1 = -nz1;}
				
				nx2 = dx2[i]; ny2 = -Math.cos(rot); nz2 = -Math.sin(rot);
				n2 = Math.sqrt(nx2*nx2 + ny2*ny2 + nz2*nz2);
				nx2 /= n2; ny2 /= n2; nz2 /= n2;
				if (y2[i] < 0) {nx2 = -nx2; ny2 = -ny2; nz2 = -nz2;}
				
				vertices[vc++] = (float)x;
				vertices[vc++] = (float)(y1[i]*Math.cos(rot));
				vertices[vc++] = (float)(y1[i]*Math.sin(rot));
				
				vertices[vc++] = (float)x;
				vertices[vc++] = (float)(y2[i]*Math.cos(rot));
				vertices[vc++] = (float)(y2[i]*Math.sin(rot));
				
				normals[nc++] = (float)(nx1);
				normals[nc++] = (float)(ny1);
				normals[nc++] = (float)(nz1);
				
				normals[nc++] = (float)(nx2);
				normals[nc++] = (float)(ny2);
				normals[nc++] = (float)(nz2);
				
				vertexID1[i][j] = vic++;
				vertexID2[i][j] = vic++;
			}
		
		//Order
		for (int i=0; i<xRes; i++)
			for (int j=0; j<rotRes; j++)
			{
				//y1
				order[oc++] = vertexID1[i][j];
				order[oc++] = vertexID1[i+1][j];
				order[oc++] = vertexID1[i+1][j+1];
				
				order[oc++] = vertexID1[i][j];
				order[oc++] = vertexID1[i+1][j+1];
				order[oc++] = vertexID1[i][j+1];
				
				//y2
				order[oc++] = vertexID2[i][j];
				order[oc++] = vertexID2[i+1][j];
				order[oc++] = vertexID2[i+1][j+1];
				
				order[oc++] = vertexID2[i][j];
				order[oc++] = vertexID2[i+1][j+1];
				order[oc++] = vertexID2[i][j+1];
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
	
	/*
	 * Fills all the cap buffers with the correct information for glDrawElements to draw the correct figure.
	 * The caps are the two ends of the graph that make the graph look solid rather than hollow.
	 */
	private void generateArraysCaps(GL2 gl)
	{
		float[] vertices = new float[(rotRes+1)*12];
		float[] normals = new float[(rotRes+1)*12];
		int vc = 0; //vertexCounter
		int nc = 0; //normalCounter
		
		int[] order = new int[rotRes*12];
		int oc = 0; //orderCounter
		
		double y1Min = Math.min(Math.abs(y1[0]), Math.abs(y2[0]));
		double y1Max = Math.max(Math.abs(y1[0]), Math.abs(y2[0]));
		
		double y2Min = Math.min(Math.abs(y1[xRes]), Math.abs(y2[xRes]));
		double y2Max = Math.max(Math.abs(y1[xRes]), Math.abs(y2[xRes]));
		
		//Vertices and normals.
		for (int i=0; i<=rotRes; i++)
		{
			double rot = 2*Math.PI*i/rotRes;
			
			vertices[vc++] = (float)(xMin);
			vertices[vc++] = (float)(y1Min*Math.cos(rot));
			vertices[vc++] = (float)(y1Min*Math.sin(rot));
			
			vertices[vc++] = (float)(xMin);
			vertices[vc++] = (float)(y1Max*Math.cos(rot));
			vertices[vc++] = (float)(y1Max*Math.sin(rot));
			
			vertices[vc++] = (float)(xMax);
			vertices[vc++] = (float)(y2Min*Math.cos(rot));
			vertices[vc++] = (float)(y2Min*Math.sin(rot));
			
			vertices[vc++] = (float)(xMax);
			vertices[vc++] = (float)(y2Max*Math.cos(rot));
			vertices[vc++] = (float)(y2Max*Math.sin(rot));
			
			normals[nc++] =  1; normals[nc++] = 0; normals[nc++] = 0;
			normals[nc++] =  1; normals[nc++] = 0; normals[nc++] = 0;
			normals[nc++] = -1; normals[nc++] = 0; normals[nc++] = 0;
			normals[nc++] = -1; normals[nc++] = 0; normals[nc++] = 0;
		}
		
		//Order
		for (int i=0; i<rotRes; i++)
		{
			int v1 = i*4, v2 = (i+1)*4;
			order[oc++] = v1; order[oc++] = v1+1; order[oc++] = (i+1)*4+1;
			order[oc++] = v1; order[oc++] = (i+1)*4+1; order[oc++] = (i+1)*4;
			
			order[oc++] = v2+2; order[oc++] = v2+3; order[oc++] = v1+3;
			order[oc++] = v2+2; order[oc++] = v1+3; order[oc++] = v1+2;
		}
		
		vertexBufferCaps = Buffers.newDirectFloatBuffer(vertices.length);
		vertexBufferCaps.put(vertices);
		vertexBufferCaps.rewind();
		
		normalBufferCaps = Buffers.newDirectFloatBuffer(normals.length);
		normalBufferCaps.put(normals);
		normalBufferCaps.rewind();
		
		orderBufferCaps = Buffers.newDirectIntBuffer(order.length);
		orderBufferCaps.put(order);
		orderBufferCaps.rewind();
		
		orderBufferCapsLength = oc;
	}
	
	//Returns the value of y1 at the x-position specified.
	private double value1(double x)
	{
		return function1.eval(new double[]{x, u, v});
	}
	
	//Returns the value of y1 at the x-position specified.
	private double value2(double x)
	{
		return function2.eval(new double[]{x, u, v});
	}
	
	//Returns the derivative of y1 at the x-position specified.
	private double partialX1(double x)
	{
		return function1.derivative(new double[]{x, u, v}, 0);
	}
	
	//Returns the derivative of y2 at the x-position specified.
	private double partialX2(double x)
	{
		return function2.derivative(new double[]{x, u, v}, 0);
	}
	
	public void setExpression(int index, Expression e)
	{
		switch (index)
		{
		case 0: function1 = e; break;
		case 1: function2 = e; break;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	public Expression getExpression(int index)
	{
		switch (index)
		{
		case 0: return function1;
		case 1: return function2;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	public void setDouble(int index, double value)
	{
		switch (index)
		{
		case X_MIN: xMin = value; break;
		case X_MAX: xMax = value; break;
		default: super.setDouble(index, value);
		}
	}
	
	public void setInt(int index, int value)
	{
		switch (index)
		{
		case X_RES: xRes = value; break;
		case ROT_RES: rotRes = value; break;
		default: super.setInt(index, value);
		}
	}
	
	public double getDouble(int index)
	{
		switch (index)
		{
		case X_MIN: return xMin;
		case X_MAX: return xMax;
		default: return super.getDouble(index);
		}
	}
	
	public int getInt(int index)
	{
		switch (index)
		{
		case X_RES: return xRes;
		case ROT_RES: return rotRes;
		default: return super.getInt(index);
		}
	}
}
