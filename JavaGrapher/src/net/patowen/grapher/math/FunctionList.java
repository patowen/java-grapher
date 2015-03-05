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

package net.patowen.grapher.math;

/**
 * FunctionList is a class referenced by the Function class. It stores the possible functions that can be applied.
 * The valid function names are: abs, arccos, arccot, arccsc, arcsec, arcsin, arctan, ceil, cos, cot, csc, floor,
 * max, min, round, sec, sin, sqr, sqrt, tan, exp, ln, pi, and e.
 * @author Patrick Owen
 * @see Function
 */
public class FunctionList
{
	//Fill arrays with functions in alpha order
	private static final String[] names = {"abs", "arccos", "arccot", "arccsc", "arcsec", "arcsin", "arctan", "ceil", "cos", "cot", "csc",
		"floor", "max", "min", "mod", "round", "sec", "sin", "sqr", "sqrt", "tan", "exp", "ln", "pi", "e"};
	private static final int[] numArgs =  {1,1,1,1,1,1,1,1,1,1,1,
		1,-1,-1,2,1,1,1,1,1,1,1,1,0,0};
	
	private FunctionList() {} //FunctionList should not be instantiated
	
	/**
	 * Evaluates the function given the variables and the function id
	 * @param x The array of variables that the function uses
	 * @param i The function id
	 * @return The value of the function
	 * @see Variable
	 */
	public static double eval(double[] x, int i)
	{
		if (x.length == numArgs[i] || numArgs[i] == -1)
		{
			switch (i)
			{
			case  0: return Math.abs(x[0]); //abs
			case  1: return Math.acos(x[0]); //arccos
			case  2: return Math.atan(1/x[0]); //arccot
			case  3: return Math.asin(1/x[0]); //arccsc
			case  4: return Math.acos(1/x[0]); //arcsec
			case  5: return Math.asin(x[0]); //arcsin
			case  6: return Math.atan(x[0]); //arctan
			case  7: return Math.ceil(x[0]); //ceil
			case  8: return Math.cos(x[0]); //cos
			case  9: return 1/Math.tan(x[0]); //cot
			case 10: return 1/Math.sin(x[0]); //csc
			case 11: return Math.floor(x[0]); //floor
			case 12: return max(x); //max
			case 13: return min(x); //min
			case 14: return x[0]-Math.floor(x[0]/x[1])*x[1]; //mod
			case 15: return Math.round(x[0]); //round
			case 16: return 1/Math.cos(x[0]); //sec
			case 17: return Math.sin(x[0]); //sin
			case 18: return x[0]*x[0]; //sqr
			case 19: return Math.sqrt(x[0]); //sqrt
			case 20: return Math.tan(x[0]); //tan
			case 21: return Math.exp(x[0]); //exp
			case 22: return Math.log(x[0]); //ln
			case 23: return Math.PI; //pi
			case 24: return Math.E; //e
			default: return Double.NaN;
			}
		}
		else
			return Double.NaN;
	}
	
	/**
	 * Evaluates the derivative of the function given the variables, derivatives, and function id.
	 * Derivatives are used for shading, not mathematical analysis, so it may be defined when it
	 * should not be.
	 * @param x The array of variables that the function uses
	 * @param d The array of derivatives that the function uses
	 * @param i The function id
	 * @return The value of the function
	 * @see Variable
	 */
	public static double derivative(double[] x, double[] d, int i)
	{
		if (x.length == numArgs[i] || numArgs[i] == -1)
		{
			switch (i)
			{
			case  0: return x[0]>=0?d[0]:-d[0]; //abs
			case  1: return -d[0]/Math.sqrt(1-x[0]*x[0]); //arccos
			case  2: return -d[0]/(1+x[0]*x[0]); //arccot
			case  3: return -d[0]/(x[0]*Math.sqrt(x[0]*x[0]-1)); //arccsc
			case  4: return d[0]/(x[0]*Math.sqrt(x[0]*x[0]-1)); //arcsec
			case  5: return d[0]/Math.sqrt(1-x[0]*x[0]); //arcsin
			case  6: return d[0]/(1+x[0]*x[0]); //arctan
			case  7: return 0; //ceil
			case  8: return -d[0]*Math.sin(x[0]); //cos
			case  9: return -d[0]/sqr(Math.sin(x[0])); //cot
			case 10: return -d[0]/(Math.sin(x[0])*Math.tan(x[0])); //csc
			case 11: return 0; //floor
			case 12: return maxd(x, d); //max
			case 13: return mind(x, d); //min
			case 14: return d[0]; //mod
			case 15: return 0; //round
			case 16: return d[0]*Math.tan(x[0])/Math.cos(x[0]); //sec
			case 17: return d[0]*Math.cos(x[0]); //sin
			case 18: return 2*d[0]*x[0]; //sqr
			case 19: return d[0]/(2*Math.sqrt(x[0])); //sqrt
			case 20: return d[0]/sqr(Math.cos(x[0])); //tan
			case 21: return d[0]*Math.exp(x[0]); //exp
			case 22: return d[0]/x[0]; //ln
			case 23: return 0; //pi
			case 24: return 0; //e
			default: return Double.NaN;
			}
		}
		else
			return Double.NaN;
	}
	
	/**
	 * Returns the function id given the string that represents the function name.
	 * @param s The function name
	 * @return The function id
	 */
	public static int getFunctionNumber(String s) //Returns -1 if invalid
	{
		for (int i=0; i<names.length; i++)
			if (names[i].equals(s.toLowerCase())) return i;
		return -1;
	}
	
	/**
	 * Returns whether the number of arguments is suitable for the given function.
	 * @param functionIndex the id of the function being checked
	 * @param numArguments the number of arguments given
	 * @return
	 */
	public static boolean isNumArgumentsValid(int functionIndex, int numArguments)
	{
		return ((numArgs[functionIndex] == -1 && numArguments != 0) || numArgs[functionIndex] == numArguments);
	}
	
	//Helper functions
	private static double max(double[] x)
	{
		if (x.length == 0) return Double.NaN;
		double answer = x[0];
		for (int j=1; j<x.length; j++)
		{
			answer = Math.max(answer, x[j]);
		}
		return answer;
	}
	
	private static double min(double[] x)
	{
		if (x.length == 0) return Double.NaN;
		double answer = x[0];
		for (int j=1; j<x.length; j++)
		{
			answer = Math.min(answer, x[j]);
		}
		return answer;
	}
	
	//max derivative function
	private static double maxd(double[] x, double[] d)
	{
		if (x.length == 0) return Double.NaN;
		double value = x[0];
		int loc = 0;
		for (int j=1; j<x.length; j++)
		{
			if (x[j] > value)
			{
				value = x[j];
				loc = j;
			}
		}
		return d[loc];
	}
	
	//min derivative function
	private static double mind(double[] x, double[] d)
	{
		if (x.length == 0) return Double.NaN;
		double value = x[0];
		int loc = 0;
		for (int j=1; j<x.length; j++)
		{
			if (x[j] < value)
			{
				value = x[j];
				loc = j;
			}
		}
		return d[loc];
	}
	
	private static double sqr(double x)
	{
		return x*x;
	}
}
