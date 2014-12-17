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
import static edu.umd.student.powen12.grapher.ParaGraph.*;

import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Represents the window for a parametric graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class ParaGraphWindow extends GraphWindow implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JLabel xMinLabel, xMaxLabel, yMinLabel, yMaxLabel, xScaleLabel, yScaleLabel, tMinLabel, tMaxLabel, tResLabel;
	private JTextField xMinField, xMaxField, yMinField, yMaxField, xScaleField, yScaleField, tMinField, tMaxField, tResField;
	
	/**
	 * Initializes a ParaGraphWindow.
	 * @param owner the owner of the window.
	 */
	public ParaGraphWindow(Window owner)
	{
		super(owner, "Parametric Curve");
		
		variableList = new char[] {'t', 'u', 'v'};
		
		createPresetLinkDouble("x-min", X_MIN); createPresetLinkDouble("x-max", X_MAX);
		createPresetLinkDouble("y-min", Y_MIN); createPresetLinkDouble("y-max", Y_MAX);
		createPresetLinkDouble("x-scale", X_SCALE); createPresetLinkDouble("y-scale", Y_SCALE);
		createPresetLinkDouble("t-min", T_MIN); createPresetLinkDouble("t-max", T_MAX);
		createPresetLinkInt("t-res", T_RES);
		
		presetNames = new String[] {"Limacon", "Logarithmic Spiral"};
		presetFiles = new String[] {"par1.txt", "par2.txt"};
		
		graph = new ParaGraph();
		
		setTextFields(new String[] {"x = ", "y = "}, false);
		
		initialize();
	}
	
	public void prepareBoundsWindow(JDialog win)
	{		
		xMinLabel = new JLabel("x-min: "); xMaxLabel = new JLabel("x-max: ");
		yMinLabel = new JLabel("y-min: "); yMaxLabel = new JLabel("y-max: ");
		xScaleLabel = new JLabel("x-scale: "); yScaleLabel = new JLabel("y-scale: ");
		tMinLabel = new JLabel("t-min: "); tMaxLabel = new JLabel("t-max: ");
		tResLabel = new JLabel("t-resolution: ");
		
		alignLabels(xMinLabel, xMaxLabel, yMinLabel, yMaxLabel, xScaleLabel, yScaleLabel,
				tMinLabel, tMaxLabel, tResLabel);
		
		xMinField = new JTextField(10); xMaxField = new JTextField(10);
		yMinField = new JTextField(10); yMaxField = new JTextField(10);
		xScaleField = new JTextField(10); yScaleField = new JTextField(10);
		tMinField = new JTextField(10); tMaxField = new JTextField(10);
		tResField = new JTextField(10);
		
		createLinkDouble(xMinField, Graph2D.X_MIN); createLinkDouble(xMaxField, Graph2D.X_MAX);
		createLinkDouble(yMinField, Graph2D.Y_MIN); createLinkDouble(yMaxField, Graph2D.Y_MAX);
		createLinkDouble(xScaleField, Graph2D.X_SCALE); createLinkDouble(yScaleField, Graph2D.Y_SCALE);
		createLinkDouble(tMinField, ParaGraph.T_MIN); createLinkDouble(tMaxField, ParaGraph.T_MAX);
		createLinkInt(tResField, ParaGraph.T_RES);
		
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
		
		c.gridx = 0; c.gridy = 2; win.add(xScaleLabel, c);
		c.gridx = 1; win.add(xScaleField,c);
		c.gridx = 2; win.add(yScaleLabel,c);
		c.gridx = 3; win.add(yScaleField,c);
		
		c.gridx = 0; c.gridy = 3; win.add(tMinLabel, c);
		c.gridx = 1; win.add(tMinField,c);
		c.gridx = 2; win.add(tMaxLabel,c);
		c.gridx = 3; win.add(tMaxField,c);
		
		c.gridx = 0; c.gridy = 4; win.add(tResLabel, c);
		c.gridx = 1; win.add(tResField,c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridy = 5; c.gridwidth = 4; win.add(setBoundsApplyButton,c);
	}
}
