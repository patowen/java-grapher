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
import static edu.umd.student.powen12.grapher.FunctionGraph3D.*;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 * Represents the window for a 3D function graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class FunctionGraph3DWindow extends GraphWindow implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JLabel xMinLabel, xMaxLabel, yMinLabel, yMaxLabel, xResLabel, yResLabel, xCenterLabel, yCenterLabel, zCenterLabel;
	private JTextField xMinField, xMaxField, yMinField, yMaxField, xResField, yResField, xCenterField, yCenterField, zCenterField;
	
	/**
	 * Initializes a FunctionGraph3DWindow.
	 * @param owner the owner of the window.
	 */
	public FunctionGraph3DWindow(Window owner)
	{
		super(owner, "3D Function Graph");
		setLayout(new BorderLayout());
		
		variableList = new char[] {'x', 'y', 'u', 'v'};
		
		createPresetLinkDouble("x-min", X_MIN); createPresetLinkDouble("x-max", X_MAX);
		createPresetLinkDouble("y-min", Y_MIN); createPresetLinkDouble("y-max", Y_MAX);
		createPresetLinkInt("x-res", X_RES); createPresetLinkInt("y-res", Y_RES);
		createPresetLinkDouble("x-center", X_CENTER); createPresetLinkDouble("y-center", Y_CENTER);
		createPresetLinkDouble("z-center", Z_CENTER); createPresetLinkDouble("view-dist", VIEW_DISTANCE);
		
		presetNames = new String[] {"Hyperbolic Paraboloid", "Quartic Function", "Bumpy Surface"};
		presetFiles = new String[] {"fun3d1.txt", "fun3d2.txt", "fun3d3.txt"};
		
		try
		{
			graph = new FunctionGraph3D();
		}
		catch (UnsatisfiedLinkError e)
		{
			JOptionPane.showMessageDialog(this, "The program cannot load its native libraries.\nUnless the" +
					" jar file was messed with, this should not have happened.\nPlease contact Patrick Owen" +
					" at superlala32@gmail.com", "Error", JOptionPane.ERROR_MESSAGE);
			dispose();
			throw e;
		}
		
		setTextFields(new String[]{"z = "}, false);
		
		initialize();
	}
	
	public void prepareBoundsWindow(JDialog win)
	{
		xMinLabel = new JLabel("x-min: "); xMaxLabel = new JLabel("x-max: ");
		yMinLabel = new JLabel("y-min: "); yMaxLabel = new JLabel("y-max: ");
		xResLabel = new JLabel("x-resolution: "); yResLabel = new JLabel("y-resolution: ");
		xCenterLabel = new JLabel("x-center (view): "); yCenterLabel = new JLabel("y-center (view): ");
		zCenterLabel = new JLabel("z-center (view): ");
		
		alignLabels(xMinLabel, xMaxLabel, yMinLabel, yMaxLabel, xResLabel, yResLabel,
				xCenterLabel, yCenterLabel, zCenterLabel);
		
		xMinField = new JTextField(10); xMaxField = new JTextField(10);
		yMinField = new JTextField(10); yMaxField = new JTextField(10);
		xResField = new JTextField(10); yResField = new JTextField(10);
		xCenterField = new JTextField(10); yCenterField = new JTextField(10);
		zCenterField = new JTextField(10);
		
		createLinkDouble(xMinField, FunctionGraph3D.X_MIN); createLinkDouble(xMaxField, FunctionGraph3D.X_MAX);
		createLinkDouble(yMinField, FunctionGraph3D.Y_MIN); createLinkDouble(yMaxField, FunctionGraph3D.Y_MAX);
		createLinkInt(xResField, FunctionGraph3D.X_RES); createLinkInt(yResField, FunctionGraph3D.Y_RES);
		createLinkDouble(xCenterField, FunctionGraph3D.X_CENTER); createLinkDouble(yCenterField, FunctionGraph3D.Y_CENTER);
		createLinkDouble(zCenterField, FunctionGraph3D.Z_CENTER);
		
		setBoundsApplyButton = new JButton("Apply");
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0; c.gridy = 0; win.add(xMinLabel, c);
		c.gridx = 1; win.add(xMinField,c);
		c.gridx = 2; win.add(xMaxLabel,c);
		c.gridx = 3; win.add(xMaxField,c);
		
		c.gridx = 0; c.gridy = 1; win.add(yMinLabel, c);
		c.gridx = 1; win.add(yMinField,c);
		c.gridx = 2; win.add(yMaxLabel,c);
		c.gridx = 3; win.add(yMaxField,c);
		
		c.gridx = 0; c.gridy = 2; win.add(xResLabel, c);
		c.gridx = 1; win.add(xResField,c);
		c.gridx = 2; win.add(yResLabel,c);
		c.gridx = 3; win.add(yResField,c);
		
		c.gridx = 0; c.gridy = 3; win.add(xCenterLabel, c);
		c.gridx = 1; win.add(xCenterField,c);
		c.gridx = 2; win.add(yCenterLabel,c);
		c.gridx = 3; win.add(yCenterField,c);
		
		c.gridx = 0; c.gridy = 4; win.add(zCenterLabel, c);
		c.gridx = 1; win.add(zCenterField,c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridy = 5; c.gridwidth = 4; win.add(setBoundsApplyButton,c);
	}
}
