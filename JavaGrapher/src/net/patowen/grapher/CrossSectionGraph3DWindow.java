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
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import static net.patowen.grapher.CrossSectionGraph3D.*;

/**
 * Represents the window for a cross section graph. The manual contains information regarding this graph type.
 * @author Patrick Owen
 */
public class CrossSectionGraph3DWindow extends GraphWindow implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JLabel xMinLabel, xMaxLabel, xResLabel, circResLabel, xCenterLabel, yCenterLabel, zCenterLabel;
	private JTextField xMinField, xMaxField, xResField, rotResField, xCenterField, yCenterField, zCenterField;
	
	private JMenu csMenu;
	private ButtonGroup csButtonGroup;
	private JRadioButtonMenuItem csCirc, csSemicirc, csSquare, csSquareCenter, csTri;
	
	/**
	 * Initializes a CrossSectionGraph3DWindow.
	 * @param owner the owner of the window.
	 */
	public CrossSectionGraph3DWindow(Window owner)
	{
		super(owner, "Cross Section Graph");
		setLayout(new BorderLayout());
		
		variableList = new char[] {'x', 'u', 'v'};
		
		createPresetLinkDouble("x-min", X_MIN); createPresetLinkDouble("x-max", X_MAX);
		createPresetLinkInt("x-res", X_RES); createPresetLinkInt("rot-res", CIRC_RES);
		createPresetLinkDouble("x-center", X_CENTER); createPresetLinkDouble("y-center", Y_CENTER);
		createPresetLinkDouble("z-center", Z_CENTER); createPresetLinkDouble("view-dist", VIEW_DISTANCE);
		
		presetNames = new String[] {"Circular"};
		presetFiles = new String[] {"cross3d1.txt"};
		
		try
		{
			graph = new CrossSectionGraph3D();
		}
		catch (UnsatisfiedLinkError e)
		{
			JOptionPane.showMessageDialog(this, "The program cannot load its native libraries.\nUnless the" +
					" jar file was messed with, this should not have happened.\nPlease contact Patrick Owen" +
					" at superlala32@gmail.com", "Error", JOptionPane.ERROR_MESSAGE);
			dispose();
			throw e;
		}
		
		setTextFields(new String[]{"y1 = ", "y2 = "}, false);
		
		initialize();
		
		csMenu = new JMenu("Cross Section");
		csButtonGroup = new ButtonGroup();
		
		csCirc = new JRadioButtonMenuItem("Circle");
		csSemicirc = new JRadioButtonMenuItem("Semicircle");
		csSquare = new JRadioButtonMenuItem("Square (base)");
		csSquareCenter = new JRadioButtonMenuItem("Square (center)");
		csTri = new JRadioButtonMenuItem("Triangle (base)");
		
		addCSMenuItems(new JRadioButtonMenuItem[] {csCirc, csSemicirc, csSquare, csSquareCenter, csTri});
		
		csCirc.setSelected(true);
		
		graphMenu.add(csMenu);
	}
	
	//Adds the items in the given array to the csMenu.
	private void addCSMenuItems(JRadioButtonMenuItem[] items)
	{
		for (JRadioButtonMenuItem item: items)
		{
			item.addActionListener(this);
			csButtonGroup.add(item);
			csMenu.add(item);
		}
	}
	
	public void prepareBoundsWindow(JDialog win)
	{
		xMinLabel = new JLabel("x-min: "); xMaxLabel = new JLabel("x-max: ");
		xResLabel = new JLabel("x-resolution: "); circResLabel = new JLabel("Circle resolution: ");
		xCenterLabel = new JLabel("x-center (view): "); yCenterLabel = new JLabel("y-center (view): ");
		zCenterLabel = new JLabel("z-center (view): ");
		
		alignLabels(xMinLabel, xMaxLabel, xResLabel, circResLabel,
				xCenterLabel, yCenterLabel, zCenterLabel);
		
		xMinField = new JTextField(10); xMaxField = new JTextField(10);
		xResField = new JTextField(10); rotResField = new JTextField(10);
		xCenterField = new JTextField(10); yCenterField = new JTextField(10);
		zCenterField = new JTextField(10);
		
		createLinkDouble(xMinField, X_MIN); createLinkDouble(xMaxField, X_MAX);
		createLinkInt(xResField, X_RES); createLinkInt(rotResField, CIRC_RES);
		createLinkDouble(xCenterField, X_CENTER); createLinkDouble(yCenterField, Y_CENTER);
		createLinkDouble(zCenterField, Z_CENTER);
		
		setBoundsApplyButton = new JButton("Apply");
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0; c.gridy = 0; win.add(xMinLabel, c);
		c.gridx = 1; win.add(xMinField,c);
		c.gridx = 2; win.add(xMaxLabel,c);
		c.gridx = 3; win.add(xMaxField,c);
		
		c.gridx = 0; c.gridy = 1; win.add(xResLabel, c);
		c.gridx = 1; win.add(xResField,c);
		c.gridx = 2; win.add(circResLabel,c);
		c.gridx = 3; win.add(rotResField,c);
		
		c.gridx = 0; c.gridy = 2; win.add(xCenterLabel, c);
		c.gridx = 1; win.add(xCenterField,c);
		c.gridx = 2; win.add(yCenterLabel,c);
		c.gridx = 3; win.add(yCenterField,c);
		
		c.gridx = 0; c.gridy = 3; win.add(zCenterLabel, c);
		c.gridx = 1; win.add(zCenterField,c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridy = 4; c.gridwidth = 4; win.add(setBoundsApplyButton,c);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if (e.getSource() == csCirc) ((CrossSectionGraph3D)graph).setCrossSection(CS_CIRC);
		else if (e.getSource() == csSemicirc) ((CrossSectionGraph3D)graph).setCrossSection(CS_SEMICIRC);
		else if (e.getSource() == csSquare) ((CrossSectionGraph3D)graph).setCrossSection(CS_SQUARE);
		else if (e.getSource() == csSquareCenter) ((CrossSectionGraph3D)graph).setCrossSection(CS_SQUARE_CENTER);
		else if (e.getSource() == csTri) ((CrossSectionGraph3D)graph).setCrossSection(CS_TRI);
	}
	
	protected void saveData(FileWriter w) throws IOException
	{
		w.append("cross-section: ");
		if (csCirc.isSelected()) w.append("circle");
		else if (csSemicirc.isSelected()) w.append("semicircle");
		else if (csSquare.isSelected()) w.append("square-base");
		else if (csSquareCenter.isSelected()) w.append("square-center");
		else if (csTri.isSelected()) w.append("triangle");
		
		w.append(System.getProperty("line.separator"));
	}
	
	protected void loadData(String name, String value)
	{
		if (name.equals("cross-section"))
		{
			CrossSectionGraph3D graph = (CrossSectionGraph3D)(this.graph);
			if (value.equals("circle")) {csCirc.setSelected(true); graph.setCrossSection(CS_CIRC);}
			else if (value.equals("semicircle")) {csSemicirc.setSelected(true); graph.setCrossSection(CS_SEMICIRC);}
			else if (value.equals("square-base")) {csSquare.setSelected(true); graph.setCrossSection(CS_SQUARE);}
			else if (value.equals("square-center")) {csSquareCenter.setSelected(true); graph.setCrossSection(CS_SQUARE_CENTER);}
			else if (value.equals("triangle")) {csTri.setSelected(true); graph.setCrossSection(CS_TRI);}
		}
	}
}
