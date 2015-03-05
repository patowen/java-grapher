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
import static net.patowen.grapher.GeneralGraph.*;

import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Represents the window for a general graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class GeneralGraphWindow extends GraphWindow implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JCheckBoxMenuItem inversionButton;
	
	private JLabel xMinLabel, xMaxLabel, yMinLabel, yMaxLabel, xScaleLabel, yScaleLabel;
	private JTextField xMinField, xMaxField, yMinField, yMaxField, xScaleField, yScaleField;
	
	/**
	 * Initializes a GeneralGraphWindow.
	 * @param owner the owner of the window.
	 */
	public GeneralGraphWindow(Window owner)
	{
		super(owner, "General Graph");

		variableList = new char[] {'x', 'y', 'u', 'v'};
		
		createPresetLinkDouble("x-min", X_MIN); createPresetLinkDouble("x-max", X_MAX);
		createPresetLinkDouble("y-min", Y_MIN); createPresetLinkDouble("y-max", Y_MAX);
		createPresetLinkDouble("x-scale", X_SCALE); createPresetLinkDouble("y-scale", Y_SCALE);
		
		presetNames = new String[] {"Cubic Curve", "Grid", "Inverted Grid", "Conic Section"};
		presetFiles = new String[] {"gen1.txt", "gen2.txt", "gen3.txt", "gen4.txt"};
		
		graph = new GeneralGraph();
		
		inversionButton = new JCheckBoxMenuItem("Invert Graph", false);
		inversionButton.addActionListener(this);
		
		setTextFields(new String[] {" = 0"}, true);
		
		initialize();
		graphMenu.add(inversionButton);
	}
	
	public void prepareBoundsWindow(JDialog win)
	{
		xMinLabel = new JLabel("x-min: "); xMaxLabel = new JLabel("x-max: ");
		yMinLabel = new JLabel("y-min: "); yMaxLabel = new JLabel("y-max: ");
		xScaleLabel = new JLabel("x-scale: "); yScaleLabel = new JLabel("y-scale: ");
		
		alignLabels(xMinLabel, xMaxLabel, yMinLabel, yMaxLabel, xScaleLabel, yScaleLabel);
		
		xMinField = new JTextField(10); xMaxField = new JTextField(10);
		yMinField = new JTextField(10); yMaxField = new JTextField(10);
		xScaleField = new JTextField(10); yScaleField = new JTextField(10);
		
		createLinkDouble(xMinField, Graph2D.X_MIN); createLinkDouble(xMaxField, Graph2D.X_MAX);
		createLinkDouble(yMinField, Graph2D.Y_MIN); createLinkDouble(yMaxField, Graph2D.Y_MAX);
		createLinkDouble(xScaleField, Graph2D.X_SCALE); createLinkDouble(yScaleField, Graph2D.Y_SCALE);
		
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
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridy = 3; c.gridwidth = 4; win.add(setBoundsApplyButton,c);
	}

	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if (e.getSource() == inversionButton)
		{
			((GeneralGraph)graph).setInversion(inversionButton.isSelected());
		}
	}
	
	protected void saveData(FileWriter w) throws IOException
	{
		w.append("inverted: ");
		if (inversionButton.isSelected()) w.append("true");
		else w.append("false");
		
		w.append(System.getProperty("line.separator"));
	}
	
	protected void loadData(String name, String value)
	{
		GeneralGraph graph = (GeneralGraph)(this.graph);
		
		if (name.equals("inverted"))
		{
			if (value.equals("true")) {inversionButton.setSelected(true); graph.setInversion(true);}
			else if (value.equals("false")) {inversionButton.setSelected(false); graph.setInversion(false);}
		}
	}
}
