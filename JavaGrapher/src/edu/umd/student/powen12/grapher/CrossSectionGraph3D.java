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
 * Represents a cross section graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class CrossSectionGraph3D extends Graph3D
{
	private static final long serialVersionUID = 1973139872093412258L;
	
	public static final int X_MIN = 100, X_MAX = 101, X_RES = 102, CIRC_RES = 103;
	
	//Cross section types
	public static final int CS_CIRC = 0, CS_SEMICIRC = 1, CS_SQUARE = 2, CS_SQUARE_CENTER = 3, CS_TRI = 4;
	
	//Function values
	private Expression function1;
	private Expression function2;
	
	//Info for future drawing
	private double[] y1, y2;
	private double[] dx1, dx2;
	
	//Graph bounds
	private double xMin, xMax;
	private int xRes, circRes;
	
	//Vertex buffers
	private int bufferRes;
	
	private int orderBufferLength;
	private IntBuffer orderBuffer;
	private FloatBuffer vertexBuffer;
	private FloatBuffer normalBuffer;
	
	private int orderBufferCapsLength;
	private IntBuffer orderBufferCaps;
	private FloatBuffer vertexBufferCaps;
	private FloatBuffer normalBufferCaps;
	
	private int crossSection;
	
	/**
	 * Constructs a CrossSectionGraph3D and initializes its defaults.
	 */
	public CrossSectionGraph3D()
	{
		function1 = null;
		function2 = null;
		xMin = -1; xMax = 1;
		xRes = 50; circRes = 50;
		
		crossSection = CS_CIRC;
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
		bufferRes = getCSNumVertices();
		
		float[] vertices = new float[(xRes+1)*(bufferRes+1)*3];
		float[] normals = new float[(xRes+1)*(bufferRes+1)*3];
		int vc = 0; //vertexCounter
		int nc = 0; //normalCounter
		
		int[][] vertexID = new int[xRes+1][bufferRes+1];
		int vic = 0; //vertexIDCounter
		
		int[] order = new int[xRes*bufferRes*6];
		int oc = 0; //orderCounter
		
		//Vertices and normals.
		for (int i=0; i<=xRes; i++)
			for (int j=0; j<=bufferRes; j++)
			{
				double x = xMin + (xMax-xMin)*i/xRes;
				double y = (getCSVertexY(j)+1)*(y2[i]-y1[i])/2 + y1[i];
				double z = getCSVertexZ(j)*(y2[i]-y1[i])/2;
				double dy = (getCSVertexY(j)+1)*(dx2[i]-dx1[i])/2 + dx1[i];
				double dz = (getCSVertexZ(j))*(dx2[i]-dx1[i])/2;
				
				double nx, ny, nz, n1;
				ny = -getCSNormalY(j); nz = -getCSNormalZ(j); nx = -dy*ny - dz*nz;
				n1 = Math.sqrt(nx*nx + ny*ny + nz*nz);
				nx /= n1; ny /= n1; nz /= n1;
				if (y2[i] < y1[i]) {nx = -nx; ny = -ny; nz = -nz;}
				
				vertices[vc++] = (float)x;
				vertices[vc++] = (float)y;
				vertices[vc++] = (float)z;
				
				normals[nc++] = (float)(nx);
				normals[nc++] = (float)(ny);
				normals[nc++] = (float)(nz);
				
				vertexID[i][j] = vic++;
			}
		
		//Order
		for (int i=0; i<xRes; i++)
			for (int j=0; j<bufferRes; j++)
			{
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
	
	/*
	 * Fills all the cap buffers with the correct information for glDrawElements to draw the correct figure.
	 * The caps are the two ends of the graph that make the graph look solid rather than hollow.
	 */
	private void generateArraysCaps(GL2 gl)
	{
		float[] vertices = new float[(bufferRes+1)*6];
		float[] normals = new float[(bufferRes+1)*6];
		int vc = 0; //vertexCounter
		int nc = 0; //normalCounter
		
		int[] order = new int[bufferRes*6];
		int oc = 0; //orderCounter
		
		//Vertices and normals.
		for (int i=0; i<bufferRes; i++)
		{
			double y1Max = (getCSVertexY(i)+1)*(y2[0]-y1[0])/2 + y1[0];
			double z1Max = getCSVertexZ(i)*(y2[0]-y1[0])/2;
			
			double y2Max = (getCSVertexY(i)+1)*(y2[xRes]-y1[xRes])/2 + y1[xRes];
			double z2Max = getCSVertexZ(i)*(y2[xRes]-y1[xRes])/2;
			
			vertices[vc++] = (float)(xMin);
			vertices[vc++] = (float)(y1Max);
			vertices[vc++] = (float)(z1Max);
			
			vertices[vc++] = (float)(xMax);
			vertices[vc++] = (float)(y2Max);
			vertices[vc++] = (float)(z2Max);
			
			normals[nc++] =  1; normals[nc++] = 0; normals[nc++] = 0;
			normals[nc++] = -1; normals[nc++] = 0; normals[nc++] = 0;
		}
		
		//Order
		for (int i=1; i<bufferRes-1; i++)
		{
			int v1 = i*2, v2 = (i+1)*2;
			
			order[oc++] = 0; order[oc++] = v1; order[oc++] = v2;
			order[oc++] = 1; order[oc++] = v2+1; order[oc++] = v1+1;
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
	
	/**
	 * Sets the cross section that is displayed to the specified index. It
	 * should be set to any constant belonging to this class starting with <code>CS_</code>
	 * @param cs
	 */
	public void setCrossSection(int cs)
	{
		crossSection = cs;
		updateGraph();
	}
	
//	/**
//	 * Returns the number corresponding to the cross section picked for this graph.
//	 */
//	public int getCrossSection()
//	{
//		return crossSection;
//	}
	
	/*
	 * Returns the number of vertices in the active cross section. Vertices with
	 * two different sets of normals are listed twice, once for each set of normals. This
	 * is why a square has 8 vertices and a triangle has 6 vertices.
	 */
	private int getCSNumVertices()
	{
		switch (crossSection)
		{
		case CS_CIRC: return circRes;
		case CS_SEMICIRC: return circRes+3;
		case CS_SQUARE: return 8;
		case CS_SQUARE_CENTER: return 8;
		case CS_TRI: return 6;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	//These methods assume the cross section is from -1 to 1. Shapes will always be similar to what is defined in these methods.
	
	//Returns the y-coordinate of the specified vertex in the cross section.
	private double getCSVertexY(int vertex)
	{
		if (vertex == getCSNumVertices())
			vertex = 0;
		
		switch (crossSection)
		{
		case CS_CIRC: return Math.cos(vertex*2*Math.PI/circRes);
		case CS_SEMICIRC:
			if (vertex <= circRes) return Math.cos(vertex*Math.PI/circRes);
			else if (vertex == circRes + 1) return -1;
			else return 1;
		case CS_SQUARE:
		case CS_SQUARE_CENTER:
			if (vertex >= 0 && vertex <= 3) return 1;
			else return -1;
		case CS_TRI:
			if (vertex == 0 || vertex == 1) return 1;
			else if (vertex == 2 || vertex == 3) return 0;
			else return -1;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	//Returns the z-coordinate of the specified vertex in the cross section.
	private double getCSVertexZ(int vertex)
	{
		if (vertex == getCSNumVertices())
			vertex = 0;
		
		switch (crossSection)
		{
		case CS_CIRC: return Math.sin(vertex*2*Math.PI/circRes);
		case CS_SEMICIRC:
			if (vertex <= circRes) return Math.sin(vertex*Math.PI/circRes);
			else return 0;
		case CS_SQUARE:
			if (vertex >= 2 && vertex <= 5) return 2;
			else return 0;
		case CS_SQUARE_CENTER:
			if (vertex >= 2 && vertex <= 5) return 1;
			else return -1;
		case CS_TRI:
			if (vertex == 0 || vertex == 1) return 0;
			else if (vertex == 2 || vertex == 3) return Math.sqrt(3);
			else return 0;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	/*
	 * Returns the y-coordinate of the normal vector for the vertex
	 * of the specified index in the cross section.
	 */
	private double getCSNormalY(int vertex)
	{
		if (vertex == getCSNumVertices())
			vertex = 0;
		
		switch (crossSection)
		{
		case CS_CIRC: return Math.cos(vertex*2*Math.PI/circRes);
		case CS_SEMICIRC:
			if (vertex <= circRes) return Math.cos(vertex*Math.PI/circRes);
			else return 0;
		case CS_SQUARE:
		case CS_SQUARE_CENTER:
			if (vertex == 1 || vertex == 2) return 1;
			else if (vertex == 5 || vertex == 6) return -1;
			else return 0;
		case CS_TRI:
			if (vertex == 1 || vertex == 2) return Math.sqrt(3)/2;
			else if (vertex == 3 || vertex == 4) return -Math.sqrt(3)/2;
			else return 0;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	/*
	 * Returns the z-coordinate of the normal vector for the vertex
	 * of the specified index in the cross section.
	 */
	private double getCSNormalZ(int vertex)
	{
		if (vertex == getCSNumVertices())
			vertex = 0;
		
		switch (crossSection)
		{
		case CS_CIRC: return Math.sin(vertex*2*Math.PI/circRes);
		case CS_SEMICIRC:
			if (vertex <= circRes) return Math.sin(vertex*Math.PI/circRes);
			else return -1;
		case CS_SQUARE:
		case CS_SQUARE_CENTER:
			if (vertex == 7 || vertex == 0) return -1;
			else if (vertex == 3 || vertex == 4) return 1;
			else return 0;
		case CS_TRI:
			if (vertex >= 1 && vertex <= 4) return 0.5;
			else return -1;
		default: throw new IllegalArgumentException("Invalid index");
		}
	}
	
	//Returns the value of y1 at the x-position specified.
	private double value1(double x)
	{
		return function1.eval(new double[]{x, u, v});
	}
	
	//Returns the value of y2 at the x-position specified.
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
		case CIRC_RES: circRes = value; break;
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
		case CIRC_RES: return circRes;
		default: return super.getInt(index);
		}
	}
}
