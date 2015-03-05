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
import static net.patowen.grapher.ParaCurve3D.*;

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
public class ParaCurve3DWindow extends GraphWindow implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JLabel tMinLabel, tMaxLabel, tResLabel, xCenterLabel, yCenterLabel, zCenterLabel;
	private JTextField tMinField, tMaxField, tResField, xCenterField, yCenterField, zCenterField;
	
	/**
	 * Initializes a ParaGraph3DWindow.
	 * @param owner the owner of the window.
	 */
	public ParaCurve3DWindow(Window owner)
	{
		super(owner, "3D Parametric Curve");
		setLayout(new BorderLayout());
		
		variableList = new char[] {'t', 'u', 'v'};
		
		createPresetLinkDouble("t-min", T_MIN); createPresetLinkDouble("t-max", T_MAX);
		createPresetLinkInt("t-res", T_RES);
		createPresetLinkDouble("x-center", X_CENTER); createPresetLinkDouble("y-center", Y_CENTER);
		createPresetLinkDouble("z-center", Z_CENTER); createPresetLinkDouble("view-dist", VIEW_DISTANCE);
		
		try
		{
			graph = new ParaCurve3D();
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
		tMinLabel = new JLabel("t-min: "); tMaxLabel = new JLabel("t-max: ");
		tResLabel = new JLabel("t-resolution: ");
		xCenterLabel = new JLabel("x-center (view): "); yCenterLabel = new JLabel("y-center (view): ");
		zCenterLabel = new JLabel("z-center (view): ");
		
		alignLabels(tMinLabel, tMaxLabel, tResLabel, xCenterLabel, yCenterLabel, zCenterLabel);
		
		tMinField = new JTextField(10); tMaxField = new JTextField(10);
		tResField = new JTextField(10);
		xCenterField = new JTextField(10); yCenterField = new JTextField(10);
		zCenterField = new JTextField(10);
		
		createLinkDouble(tMinField, T_MIN); createLinkDouble(tMaxField, T_MAX);
		createLinkInt(tResField, T_RES);
		createLinkDouble(xCenterField, X_CENTER); createLinkDouble(yCenterField, Y_CENTER);
		createLinkDouble(zCenterField, Z_CENTER);
		
		setBoundsApplyButton = new JButton("Apply");
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;		
		c.gridx = 0; c.gridy = 0; win.add(tMinLabel, c);
		c.gridx = 1; win.add(tMinField,c);
		c.gridx = 2; win.add(tMaxLabel,c);
		c.gridx = 3; win.add(tMaxField,c);
		
		c.gridx = 0; c.gridy = 1; win.add(tResLabel, c);
		c.gridx = 1; win.add(tResField,c);
		
		c.gridx = 0; c.gridy = 2; win.add(xCenterLabel, c);
		c.gridx = 1; win.add(xCenterField,c);
		c.gridx = 2; win.add(yCenterLabel,c);
		c.gridx = 3; win.add(yCenterField,c);
		
		c.gridx = 0; c.gridy = 3; win.add(zCenterLabel, c);
		c.gridx = 1; win.add(zCenterField,c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridy = 5; c.gridwidth = 4; win.add(setBoundsApplyButton,c);
	}
}
