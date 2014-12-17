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
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Runs the Graph Program. It was named Revolution because solids of revolution were the main
 * idea behind the creation of this project.
 * @author Patrick Owen
 */
public class GraphProgramRunner
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		
		//This line of code makes sure the pop up menus that appear when the "Graph" menu
		//is pressed are in front of the GLCanvas, which is a heavyweight component.
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		MainFrame frame = new MainFrame();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
