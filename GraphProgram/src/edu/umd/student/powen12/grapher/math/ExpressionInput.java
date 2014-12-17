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

package edu.umd.student.powen12.grapher.math;
import java.util.ArrayList;

/**
 * ExpressionInput is a class that converts a String into its corresponding Expression.
 * It cannot be instantiated, and its main method is getExpressionFromString.
 * @author Patrick Owen
 */
public class ExpressionInput
{
	private static char[] noVariables = {};
	private static double[] noArguments = {};
	
	private ExpressionInput() {} //This class will not be instantiated
	
	/**
	 * Converts a String usually generated by user input into an Expression that can be evaluated.
	 * @param s the string that will be converted into an Expression
	 * @param variableNames an array that determines what the variable names in the string are ('x', 'y', 'z')
	 * The array index of each variable name represents the variable id.
	 * @return The resulting Expression.
	 * @see Expression
	 * @see Variable
	 */
	public static Expression getExpressionFromString(String s, char[] variableNames)
	{
		try
		{
			return parseExpression(s, variableNames);
		}
		catch (InvalidExpressionException e)
		{
			return new InvalidExpression();
		}
	}
	
	/**
	 * This expression converts a String to an Expression. It is made to be called recursively, and if an
	 * error is found in the expression, it throws an InvalidExpressionException to be caught by the
	 * public method getExpressionFromString
	 * @param s the string that will be converted into an Expression
	 * @param variableNames an array that determines what the variable names in the string are ('x', 'y', 'z')
	 * The array index of each variable name represents the variable id.
	 * @return The resulting Expression.
	 * @see Expression
	 * @see Variable
	 */
	private static Expression parseExpression(String s, char[] variableNames) throws InvalidExpressionException
	{
		//Add looseness to the expressions that can be entered and prepare the expression for analysis.
		s = s.toLowerCase();
		s = removeSpaces(s);
		s = removeOuterParentheses(s);
		s = addImpliedMultiplication(s, variableNames);
		
		if (s.length() == 0) throw new InvalidExpressionException(); //expression expected when there is none
		
		//Check to see if the expression is a set of to expressions operated together.
		int level; //Level in parentheses
		for (int k=0; k<3; k++) //Loop through the reverse order of operations
		{
			level = 0;
			
			//-3 treats the minus sign differently than 4-3
			if (k>0 && s.charAt(0) == '-')
				return new Negation(parseExpression(s.substring(1), variableNames));
			for (int i=s.length()-1; i>=0; i--)
			{
				char c = s.charAt(i);
				//Valid operations must be on the outer level of parentheses and must not be mistaken with exponential notation (3e-1, - is not an operation).
				if (isOperation(c,k) && i>0 && !isOperation(s.charAt(i-1)) && (s.charAt(i-1) != 'e' || i==1 || !isDigit(s.charAt(i-2)))
						&& level==0)
					return separateExpression(s,i,c, variableNames); //Pick correct class to return
				else if (c == ')') level++;
				else if (c == '(') level--;
			}
		}
		
		//Check to see if the expression is a predefined function
		String first = firstWord(s);
		int functionNum = FunctionList.getFunctionNumber(first);
		
		if (functionNum != -1)
		{
			if (s.length() == 0 || first.length() > s.length()) throw new InvalidExpressionException();
			
			ArrayList<Expression> argsList = new ArrayList<Expression>();
			
			if (!first.equals(s))
			{
				if (s.charAt(s.length()-1) != ')') throw new InvalidExpressionException();
				if (s.charAt(first.length()) != '(') throw new InvalidExpressionException();
				
				String argsString = s.substring(first.length()+1, s.length()-1);
				if (argsString.equals("")) return new Function(new Expression[0], functionNum);
				
				//Separate arguments
				level = 0;
				for (int i=0; i<argsString.length(); i++)
				{
					char c = argsString.charAt(i);
					if (c == ',' && level == 0)
					{
						argsList.add(parseExpression(argsString.substring(0,i), variableNames));
						argsString = argsString.substring(i+1);
						i = 0;
					}
					else if (c == '(') level++;
					else if (c == ')') level--;
				}
				argsList.add(parseExpression(argsString, variableNames));
			}
			
			if (!FunctionList.isNumArgumentsValid(functionNum, argsList.size())) throw new InvalidExpressionException();
			
			//Convert ArrayList into array
			Expression[] args = new Expression[argsList.size()];
			for (int i=0; i<argsList.size(); i++) args[i] = argsList.get(i);
			
			return new Function(args,functionNum);
		}
		
		//Check to see if the expression is a variable
		for (int i=0; i<variableNames.length; i++)
		{
			if (s.charAt(0) == (variableNames[i]) && s.length() == 1) return new Variable(i);
		}
		
		//Check to see if the expression is a constant
		try
		{
			return new Constant(Double.parseDouble(s));
		}
		catch (NumberFormatException e)
		{
			//Every possible thing the expression could be was covered. It must be invalid.
			throw new InvalidExpressionException();
		}
	}
	
	/**
	 * Parses a String as an Expression with no variables and returns a double equivalent to the
	 * value of the expression.
	 * @throws NumberFormatException if the expression is invalid
	 * @param s the String to parse
	 * @return a double equivalent to the expression given. It will not be infinite or NaN because
	 * it would instead throw a NumberFormatException.
	 */
	public static double parseDouble(String s)
	{
		double answer = getExpressionFromString(s, noVariables).eval(noArguments);
		if (Double.isInfinite(answer) || Double.isNaN(answer))
			throw new NumberFormatException("Invalid expression");
		else
			return answer;
	}
	
	/**
	 * Removes all spaces from a String.
	 * @param str the String to strip the spaces from
	 * @return a String with the spaces removed
	 */
	private static String removeSpaces(String str)
	{
		String s = str;
		for (int i=0; i<s.length(); i++)
		{
			if (s.charAt(i) == ' ')
				s = s.substring(0,i) + s.substring(i+1);
		}
		return s;
	}
	
	/**
	 * Removes all outer sets of parentheses from a String.
	 * @param str the String to modify.
	 * @return the String with outer parentheses removed.
	 */
	private static String removeOuterParentheses(String str)
	{
		String s = str;
		while (s.length() != 0 && s.charAt(0) == '(' && s.charAt(s.length()-1) == ')')
		{
			int level = 1;
			for (int i=s.length()-2; i>=1; i--)
			{
				char c = s.charAt(i);
				if (c == ')') level++;
				else if (c == '(') level--;
				
				if (level == 0) break;
			}
			if (level == 0)
				break;
			else
				s = s.substring(1, s.length()-1);
		}
		return s;
	}
	
	/**
	 * Returns the String with all letters that are part of a function name with
	 * underscores.
	 * @param str the String in question
	 * @return the String with all letters that are part of a function name with
	 * underscores.
	 */
	private static String findFunctions(String str)
	{
		String s = str;
		for (int i=0; i<str.length(); i++)
		{
			String first = firstWord(s.substring(i));
			if (FunctionList.getFunctionNumber(first) != -1)
			{
				String replace = "";
				for (int j=0; j<first.length(); j++)
				{
					replace += "_";
				}
				s = s.substring(0,i)+replace+s.substring(i+replace.length());
			}
		}
		return s;
	}
	
	/**
	 * Adds the * character to places where implied multiplication should take place in a given String.
	 * @param str the given String
	 * @param variableNames the possible names of a variable
	 * @return the resulting String with * characters added.
	 */
	private static String addImpliedMultiplication(String str, char[] variableNames)
	{
		String s = str;
		String t = findFunctions(str);
		
		for (int i=0; i<s.length()-1; i++)
		{
			char c = t.charAt(i);
			char nextC = t.charAt(i+1);
			if (isNumPart(c, variableNames) && (nextC == '(' || isVariable(nextC, variableNames)) ||
					isNumPart(nextC, variableNames) && (c == ')' || c == 'x') || c == ')' && nextC == '(')
			{
				s = s.substring(0,i+1) + '*' + s.substring(i+1);
				t = t.substring(0,i+1) + '*' + t.substring(i+1);
			}
		}
		return s;
	}
	
	/**
	 * Returns the first word in the given String, or an empty String if the
	 * String does not start with a word. A word is something that starts with a letter
	 * or underscore and only contains letters, underscores, or digits.
	 * @param str the String in question
	 * @return the first word in the given String
	 */
	private static String firstWord(String str)
	{
		if (str.length() == 0 || isDigit(str.charAt(0)))
			return "";
		for (int i=0; i<str.length(); i++)
		{
			char c = str.charAt(i);
			if (!(isLetter(c) || c=='_' || isDigit(c)))
			{
				return str.substring(0,i);
			}
		}
		return str;
	}
	
	/**
	 * Returns whether the character may possibly be part of a number or variable.
	 * @param c the character in question
	 * @param variableNames the possible names of a variable
	 * @return whether the character may possibly be part of a number or variable. Includes
	 * decimal points, digits, and variable names
	 */
	private static boolean isNumPart(char c, char[] variableNames)
	{
		return isDigit(c) || c == '.' || isVariable(c, variableNames);
	}
	
	/**
	 * Returns whether the given character is a digit.
	 * @param c the character in question
	 * @return whether the given character is a digit (0 through 9)
	 */
	private static boolean isDigit(char c)
	{
		return c>='0' && c<='9';
	}
	
	/**
	 * Returns whether the given character is a letter.
	 * @param c the character in question
	 * @return whether the given character is a letter (capital or lowercase letters accepted)
	 */
	private static boolean isLetter(char c)
	{
		return c>='A' && c<='Z' || c>='a' && c<='z';
	}
	
	/**
	 * Returns whether the given character is the name of a variable.
	 * @param c the character in question
	 * @param variableNames the possible names of a variable.
	 * @return whether the given character is in the set of variable names
	 */
	private static boolean isVariable(char c, char[] variableNames)
	{
		for (int i=0; i<variableNames.length; i++)
			if (c == variableNames[i]) return true;
		
		return false;
	}
	
	/**
	 * Returns whether the given character is an operation. Operations
	 * include +, -, *, /, and ^.
	 * @param c the character in question
	 * @return whether the given character is an operation
	 */
	private static boolean isOperation(char c)
	{
		return c=='+' || c=='-' || c=='*' || c=='/' || c=='^';
	}
	
	/**
	 * Returns whether the given character is an operation of a given level. Operations
	 * include + and - in level 0, * and / in level 1 and ^ in level 2. Operations
	 * of higher levels are ones that are evaluated earlier in the order of operations.
	 * @param c the character in question
	 * @param level the level to check
	 * @return whether the given character is an operation in the specified level
	 */
	private static boolean isOperation(char c, int level)
	{
		switch (level)
		{
		case 0:
			return c=='+' || c=='-'; //Break statements unneeded due to return statements
		case 1:
			return c=='*' || c=='/';
		case 2:
			return c=='^';
		default:
			return false;
		}
	}
	
	/**
	 * Calls parseExpression twice, once for each side of an operation, and glues them together with that
	 * operation.
	 * @param s the String being parsed
	 * @param pos the position of the operation
	 * @param operation the operation symbol (+, -, *, /, or ^)
	 * @param variableNames the possible names of a variable
	 * @return the final Expression parsed from the String using the given operation and position
	 * @throws InvalidExpressionException if the operation was not actually an operation
	 */
	private static Expression separateExpression(String s, int pos, char operation, char[] variableNames) throws InvalidExpressionException
	{
		switch (operation)
		{
		case '+':
			return new Sum(parseExpression(s.substring(0,pos), variableNames), parseExpression(s.substring(pos+1), variableNames));
		case '-':
			return new Difference(parseExpression(s.substring(0,pos), variableNames), parseExpression(s.substring(pos+1), variableNames));
		case '*':
			return new Product(parseExpression(s.substring(0,pos), variableNames), parseExpression(s.substring(pos+1), variableNames));
		case '/':
			return new Quotient(parseExpression(s.substring(0,pos), variableNames), parseExpression(s.substring(pos+1), variableNames));
		case '^':
			return new Power(parseExpression(s.substring(0,pos), variableNames), parseExpression(s.substring(pos+1), variableNames));
		default:
			throw new InvalidExpressionException();
		}
	}
}
