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
import static net.patowen.grapher.ParaGraph3D.*;

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
 * Represents the window for a 3D parametric graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class ParaGraph3DWindow extends GraphWindow implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JLabel sMinLabel, sMaxLabel, sResLabel, tMinLabel, tMaxLabel, tResLabel, xCenterLabel, yCenterLabel, zCenterLabel;
	private JTextField sMinField, sMaxField, sResField, tMinField, tMaxField, tResField, xCenterField, yCenterField, zCenterField;
	
	/**
	 * Initializes a ParaGraph3DWindow.
	 * @param owner the owner of the window.
	 */
	public ParaGraph3DWindow(Window owner)
	{
		super(owner, "3D Parametric Surface");
		setLayout(new BorderLayout());
		
		variableList = new char[] {'s', 't', 'u', 'v'};
		
		createPresetLinkDouble("s-min", S_MIN); createPresetLinkDouble("s-max", S_MAX);
		createPresetLinkDouble("t-min", T_MIN); createPresetLinkDouble("t-max", T_MAX);
		createPresetLinkInt("s-res", S_RES); createPresetLinkInt("t-res", T_RES);
		createPresetLinkDouble("x-center", X_CENTER); createPresetLinkDouble("y-center", Y_CENTER);
		createPresetLinkDouble("z-center", Z_CENTER); createPresetLinkDouble("view-dist", VIEW_DISTANCE);
		
		presetNames = new String[] {"Sphere", "Torus", "Mobius Strip", "Spiral"};
		presetFiles = new String[] {"par3d1.txt", "par3d2.txt", "par3d3.txt", "par3d4.txt"};
		
		try
		{
			graph = new ParaGraph3D();
		}
		catch (UnsatisfiedLinkError e)
		{
			JOptionPane.showMessageDialog(this, "The program cannot load its native libraries.\nUnless the" +
					" jar file was messed with, this should not have happened.\nPlease contact Patrick Owen" +
					" at superlala32@gmail.com", "Error", JOptionPane.ERROR_MESSAGE);
			dispose();
			throw e;
		}
		
		setTextFields(new String[]{"x = ", "y = ", "z = "}, false);
		
		initialize();
	}
	
	public void prepareBoundsWindow(JDialog win)
	{
		sMinLabel = new JLabel("s-min: "); sMaxLabel = new JLabel("s-max: ");
		sResLabel = new JLabel("s-resolution: ");
		tMinLabel = new JLabel("t-min: "); tMaxLabel = new JLabel("t-max: ");
		tResLabel = new JLabel("t-resolution: ");
		xCenterLabel = new JLabel("x-center (view): "); yCenterLabel = new JLabel("y-center (view): ");
		zCenterLabel = new JLabel("z-center (view): ");
		
		alignLabels(sMinLabel, sMaxLabel, tMinLabel, tMaxLabel, sResLabel, tResLabel,
				xCenterLabel, yCenterLabel, zCenterLabel);
		
		sMinField = new JTextField(10); sMaxField = new JTextField(10);
		sResField = new JTextField(10);
		tMinField = new JTextField(10); tMaxField = new JTextField(10);
		tResField = new JTextField(10);
		xCenterField = new JTextField(10); yCenterField = new JTextField(10);
		zCenterField = new JTextField(10);
		
		createLinkDouble(sMinField, S_MIN); createLinkDouble(sMaxField, S_MAX);
		createLinkDouble(tMinField, T_MIN); createLinkDouble(tMaxField, T_MAX);
		createLinkInt(sResField, S_RES); createLinkInt(tResField, T_RES);
		createLinkDouble(xCenterField, X_CENTER); createLinkDouble(yCenterField, Y_CENTER);
		createLinkDouble(zCenterField, Z_CENTER);
		
		setBoundsApplyButton = new JButton("Apply");
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0; c.gridy = 0; win.add(sMinLabel, c);
		c.gridx = 1; win.add(sMinField,c);
		c.gridx = 2; win.add(sMaxLabel,c);
		c.gridx = 3; win.add(sMaxField,c);
		
		c.gridx = 0; c.gridy = 1; win.add(tMinLabel, c);
		c.gridx = 1; win.add(tMinField,c);
		c.gridx = 2; win.add(tMaxLabel,c);
		c.gridx = 3; win.add(tMaxField,c);
		
		c.gridx = 0; c.gridy = 2; win.add(sResLabel, c);
		c.gridx = 1; win.add(sResField,c);
		c.gridx = 2; win.add(tResLabel, c);
		c.gridx = 3; win.add(tResField,c);
		
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
