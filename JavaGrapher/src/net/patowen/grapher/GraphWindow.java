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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;

import net.patowen.grapher.math.Expression;
import net.patowen.grapher.math.ExpressionInput;
import net.patowen.grapher.math.InvalidExpression;

/**
 * GraphWindow takes advantage of all the commonalities between the different graph windows.
 * All graph windows are subclasses of this.
 * @author Patrick Owen
 */
public abstract class GraphWindow extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * The list of valid variables the user can include in formulas, in the correct order based
	 * on how the embedded graph interprets the variables
	 */
	protected char[] variableList;
	
	/**
	 * The embedded graph displayed by the window
	 */
	protected Graph graph;
	
	/**
	 * The menu bar of the window
	 */
	protected JMenuBar menuBar;
	
	/**
	 * The "Graph" menu in the menu bar of the window
	 */
	protected JMenu graphMenu;
	
	private JMenuItem setBoundsButton;
	private JCheckBoxMenuItem axesButton;
	
	//Preset menu
	private JMenu presetsMenu;
	private JMenuItem savePresetButton;
	private JRadioButtonMenuItem loadPresetButton;
	private File defaultPresetPath;
	
	//Expressions
	private int numExpressions;
	private JPanel expressionPanel;
	private JLabel[] expressionLabel;
	private JTextField[] expressionField;
	private JButton expressionApplyButton;
	
	private JCheckBox viewButton;
	
	private JDialog setBoundsWindow;
	
	/**
	 * The "Apply" button in the Bounds window.
	 */
	protected JButton setBoundsApplyButton;
	
	private ArrayList<Link> links;
	private ArrayList<PresetLink> presetLinks;
	private ArrayList<JTextField> fieldsToCheck;
	
	protected JRadioButtonMenuItem[] presetItems;
	protected String[] presetFiles;
	protected String[] presetNames;
	
	/**
	 * Constructs a GraphWindow object. <code>initialize()</code> must be
	 * called to set up the GUI.
	 * @param owner the owner of this window, since it is a JDialog
	 * @param title the String to display in the title bar
	 */
	public GraphWindow(Window owner, String title)
	{
		super(owner, title);
		setLayout(new BorderLayout());
		
		//Make sure all the windows go away when the window is closed
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				dispose();
			}
		});
		
		links = new ArrayList<Link>();
		presetLinks = new ArrayList<PresetLink>();
		fieldsToCheck = new ArrayList<JTextField>();
		
		loadPreferences();
	}
	
	/**
	 * Initializes the text fields where the user types in the expression to be graphed.
	 * @param text an array that determines what the labels for the text fields should be
	 * @param labelAtRight whether the label should be to the right of the text field
	 */
	protected void setTextFields(String[] text, boolean labelAtRight)
	{
		expressionPanel = new JPanel();
		expressionPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		numExpressions = text.length;
		expressionField = new JTextField[numExpressions];
		expressionLabel = new JLabel[numExpressions];
		
		for (int i=0; i<numExpressions; i++)
		{
			expressionField[i] = new JTextField();
			expressionLabel[i] = new JLabel(text[i]);
			
			expressionField[i].getDocument().addDocumentListener(new DocumentListener()
			{
				public void changedUpdate(DocumentEvent e) {}
				
				public void insertUpdate(DocumentEvent e)
				{
					updateExpressionApplyButton();
				}
				
				public void removeUpdate(DocumentEvent e)
				{
					updateExpressionApplyButton();
				}
			});
			
			expressionField[i].addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_ENTER && expressionApplyButton.isEnabled())
					{
						for (int i=0; i<numExpressions; i++)
							graph.setExpression(i, ExpressionInput.getExpressionFromString(expressionField[i].getText(), variableList));
						graph.updateGraph();
					}
				}
			});
			
			if (!labelAtRight)
			{
				c.gridx = 0; c.gridy = i; c.weightx = 0; c.fill = GridBagConstraints.NONE;
				expressionPanel.add(expressionLabel[i], c);
				c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
				expressionPanel.add(expressionField[i], c);
			}
			else
			{
				c.gridx = 0; c.gridy = i; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
				expressionPanel.add(expressionField[i], c);
				c.gridx = 1; c.weightx = 0; c.fill = GridBagConstraints.NONE;
				expressionPanel.add(expressionLabel[i], c);
			}
		}
		
		expressionApplyButton = new JButton("Apply");
		expressionApplyButton.setEnabled(false);
		c.gridx = 2; c.gridy = 0; c.gridheight = numExpressions; c.weightx = 0; c.fill = GridBagConstraints.VERTICAL;
		expressionPanel.add(expressionApplyButton, c);
	}
	
	/**
	 * Initializes the GUI of the GraphWindow. <code>setTextFields</code> should
	 * be called first.
	 */
	protected void initialize()
	{
		menuBar = new JMenuBar();
		graphMenu = new JMenu("Graph");
		setBoundsButton = new JMenuItem("Set Bounds");
		axesButton = new JCheckBoxMenuItem("Show Axes", true);
		viewButton = new JCheckBox("Mouse adjusts view", true);
		viewButton.setToolTipText("Uncheck this to make the mouse adjust the variables u and v.");
		
		graphMenu.add(setBoundsButton);
		graphMenu.add(axesButton);
		menuBar.add(graphMenu);
		
		initializePresetsMenu();
		
		expressionApplyButton.addActionListener(this);
		setBoundsButton.addActionListener(this);
		axesButton.addActionListener(this);
		loadPresetButton.addActionListener(this);
		savePresetButton.addActionListener(this);
		viewButton.addActionListener(this);
		
		if (graph instanceof Component)
			add((Component)graph, BorderLayout.CENTER);
		else
			throw new IllegalArgumentException("Graph must be a Component.");
		
		add(expressionPanel, BorderLayout.NORTH);
		add(viewButton, BorderLayout.SOUTH);
		setJMenuBar(menuBar);
		pack();
		setLocationRelativeTo(getParent());
		setVisible(true);
	}
	
	/**
	 * Right aligns all the labels in the given array.
	 * @param labels the given array of JLabels
	 */
	protected void alignLabels(JLabel... labels)
	{
		for (int i=0; i<labels.length; i++)
			labels[i].setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
	private void initializePresetsMenu()
	{
		presetsMenu = new JMenu("Presets");
		ButtonGroup group = new ButtonGroup();
		
		loadPresetButton = new JRadioButtonMenuItem("Custom");
		savePresetButton = new JMenuItem("Save as custom preset");
		
		if (presetNames != null)
		{
			int length = presetNames.length;
			presetItems = new JRadioButtonMenuItem[length];
			
			ActionListener presetsListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					for (int i=0; i<presetItems.length; i++)
					{
						if (e.getSource() == presetItems[i])
						{
							URL url = this.getClass().getResource("/edu/umd/student/powen12/grapher/presets/" + presetFiles[i]);
							if (url != null)
							{
								try
								{
									loadPreset(new Scanner(url.openStream()));
								}
								catch (FileNotFoundException e1) {}
								catch (IOException e1) {} //Fail silently
							}
						}
					}
				}
			};
			
			for (int i=0; i<length; i++)
			{
				presetItems[i] = new JRadioButtonMenuItem(presetNames[i]);
				group.add(presetItems[i]);
				presetItems[i].addActionListener(presetsListener);
				presetsMenu.add(presetItems[i]);
			}
			
			presetsMenu.addSeparator();
		}
		
		presetsMenu.add(loadPresetButton);
		group.add(loadPresetButton);
		presetsMenu.add(savePresetButton);
		menuBar.add(presetsMenu);
	}
	
	/*
	 * Sets up the Bounds window and allows subclasses to populate it through
	 * the setBoundsWindow method
	 */
	private void initializeBoundsWindow()
	{
		setBoundsWindow = new JDialog(this, "Bounds");
		setBoundsWindow.setLayout(new GridBagLayout());
		setBoundsWindow.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				setBoundsWindow.dispose();
				links.clear();
			}
		});
		setBoundsWindow.setResizable(false);
		
		links.clear();
		fieldsToCheck.clear();
		
		prepareBoundsWindow(setBoundsWindow);
		
		setBoundsApplyButton.addActionListener(this);
		
		for (Link link:links)
		{
			link.setFieldText();
		}
		
		setBoundsWindow.pack();
		setBoundsWindow.setLocationRelativeTo(this);
		setBoundsWindow.setVisible(true);
	}
	
	private void savePresetWithDialog()
	{
		if (!expressionApplyButton.isEnabled())
		{
			JOptionPane.showMessageDialog(this, "The preset being saved is of an invalid equation", "Invalid", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		JFileChooser fc = new JFileChooser(defaultPresetPath);
		fc.setFileFilter(new FileFilterText());
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null)
		{
			String fName = fc.getSelectedFile().getPath();
			
			int i = fName.lastIndexOf('.');
			if (i == -1 || ! fName.substring(i).equals(".txt"))
				fName = fName + ".txt";
			
			boolean shouldSave = true;
			File f = new File(fName);
			defaultPresetPath = f.getParentFile();
			savePreferences();
			
			if (f.exists())
			{
				int option = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to overwrite " + f.getName() + "?",
						"Overwriting " + f.getName(), JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.NO_OPTION)
					shouldSave = false;
			}
			
			if (shouldSave)
			{
				try
				{
					savePreset(f);
					JOptionPane.showMessageDialog(this, "Preset saved successfully", "Saved", JOptionPane.INFORMATION_MESSAGE);
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(this, "Failed to save the preset in this location", "Save failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	private void loadPresetWithDialog()
	{
		JFileChooser fc = new JFileChooser(defaultPresetPath);
		fc.setFileFilter(new FileFilterText());
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null)
		{
			defaultPresetPath = fc.getSelectedFile().getParentFile();
			savePreferences();
			
			try
			{
				loadPreset(new Scanner(fc.getSelectedFile()));
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(this, "Failed to open the file. Does it exist?", "Load failed", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	//Preferences are used to make the file save and load dialogs consistent.
	private void loadPreferences()
	{
		try
		{
			Preferences prefs = Preferences.userRoot().node("edu_umd_student_powen12_grapher_math");
			defaultPresetPath = new File(prefs.get("preset_directory", FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath()));
		}
		catch (SecurityException e) {} //Fail silently. It's not essential.
	}
	
	private void savePreferences()
	{
		try
		{
			Preferences prefs = Preferences.userRoot().node("edu_umd_student_powen12_grapher_math");
			prefs.put("preset_directory", defaultPresetPath.getAbsolutePath());
		}
		catch (SecurityException e) {} //Fail silently. It's not essential.
	}
	
	/**
	 * Saves preset information other than the expressions typed in.
	 */
	protected void saveData(FileWriter w) throws IOException
	{
		
	}
	
	/**
	 * Loads a specific piece of data other than the expressions typed in.
	 */
	protected void loadData(String name, String value)
	{
		
	}
	
	//Saves all info to generate a graph onto a file.
	private void savePreset(File file) throws IOException
	{
		FileWriter w = new FileWriter(file);
		for (int i=0; i<numExpressions; i++)
		{
			w.append("e" + i + ": " + expressionField[i].getText() + System.getProperty("line.separator"));
		}
		for (PresetLink link : presetLinks)
		{
			link.savePreset(w);
		}
		saveData(w);
		w.close();
	}
	
	//Loads all info to generate a graph from a file.
	private void loadPreset(Scanner scan) throws FileNotFoundException
	{
		while (scan.hasNextLine())
		{
			String str = scan.nextLine().trim();
			if (str.isEmpty() || str.charAt(0) == '#')
				continue;
			int commentIndex = str.indexOf('#');
			if (commentIndex != -1)
				str = str.substring(0, commentIndex);
			int colonIndex = str.indexOf(':');
			if (colonIndex == -1)
				continue;
			String name = str.substring(0, colonIndex).trim();
			String value = str.substring(colonIndex+1).trim();
			
			boolean matchFound = false;
			for (int i=0; i<numExpressions; i++)
			{
				if (name.equals("e"+i))
				{
					matchFound = true;
					expressionField[i].setText(value);
					break;
				}
			}
			if (matchFound)
				continue;
			
			for (PresetLink link : presetLinks)
			{
				if (link.loadPreset(name, value))
				{
					matchFound = true;
					break;
				}
			}
			
			if (!matchFound)
				loadData(name, value);
		}
		scan.close();
		
		updateExpressionApplyButton();
		if (expressionApplyButton.isEnabled())
		{
			for (int i=0; i<numExpressions; i++)
				graph.setExpression(i, ExpressionInput.getExpressionFromString(expressionField[i].getText(), variableList));
			graph.updateGraph();
		}
	}
	
	/**
	 * Places components on the "Bounds" window and links necessary text fields to analogous properties for the
	 * graph display. Also initializes the <code>setBoundsApplyButton</code>, which is used to apply the properties
	 * in the text field.
	 * @param win the BoundsWindow being prepared
	 */
	protected abstract void prepareBoundsWindow(JDialog win);
	
	/**
	 * Applies any bounds set in the "Set Bounds" window that are not
	 * covered by links. Called when the <code>setBoundsApplyButton</code> is pressed.
	 */
	protected void applyBounds() {}
	
	/**
	 * Links a text field that holds a double to the embedded graph.
	 * @param field the text field to link
	 * @param index the graph's parameter ID
	 */
	protected void createLinkDouble(JTextField field, int index)
	{
		links.add(new LinkDouble(field, index));
		addFieldToCheck(field);
	}
	
	/**
	 * Links a text field that holds an integer to the embedded graph.
	 * @param field the text field to link
	 * @param index the graph's parameter ID
	 */
	protected void createLinkInt(JTextField field, int index)
	{
		links.add(new LinkInt(field, index));
		addFieldToCheck(field);
	}
	
	/**
	 * Links a double preset value to the embedded graph.
	 * @param name the name of the preset key
	 * @param index the graph's parameter ID
	 */
	protected void createPresetLinkDouble(String name, int index)
	{
		presetLinks.add(new PresetLinkDouble(name, index));
	}
	
	/**
	 * Links a int preset value that to the embedded graph.
	 * @param name the name of the preset key
	 * @param index the graph's parameter ID
	 */
	protected void createPresetLinkInt(String name, int index)
	{
		presetLinks.add(new PresetLinkInt(name, index));
	}
	
	/**
	 * Adds the given text to the list of text fields to check for valid input for the Bounds window
	 * apply button to be active.
	 * @param field the text field to add
	 */
	protected void addFieldToCheck(JTextField field)
	{
		field.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e) {}
			
			public void insertUpdate(DocumentEvent e)
			{
				updateSetBoundsApplyButton();
			}
			
			public void removeUpdate(DocumentEvent e)
			{
				updateSetBoundsApplyButton();
			}
		});
		fieldsToCheck.add(field);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == expressionApplyButton)
		{
			for (int i=0; i<numExpressions; i++)
				graph.setExpression(i, ExpressionInput.getExpressionFromString(expressionField[i].getText(), variableList));
			graph.updateGraph();
		}
		else if (e.getSource() == setBoundsButton)
		{
			initializeBoundsWindow();
		}
		else if (e.getSource() == setBoundsApplyButton)
		{
			for (Link link:links)
			{
				link.setGraphProperty();
			}
			
			applyBounds();
			graph.updateGraph();
		}
		else if (e.getSource() == axesButton)
		{
			graph.setShowAxes(axesButton.isSelected());
		}
		else if (e.getSource() == loadPresetButton)
		{
			loadPresetWithDialog();
		}
		else if (e.getSource() == savePresetButton)
		{
			savePresetWithDialog();
		}
		else if (e.getSource() == viewButton)
		{
			graph.setMouseView(viewButton.isSelected());
		}
	}
	
	/*
	 * Checks for valid input and updates the Bounds window's apply
	 * button accordingly.
	 */
	private void updateSetBoundsApplyButton()
	{
		for (JTextField field:fieldsToCheck)
		{
			if (!isTextFieldValid(field))
			{
				setBoundsApplyButton.setEnabled(false);
				return;
			}
		}
		
		setBoundsApplyButton.setEnabled(true);
	}
	
	/*
	 * Checks for valid formulas and updates the graph's apply
	 * button accordingly.
	 */
	private void updateExpressionApplyButton()
	{
		for (JTextField field:expressionField)
		{
			Expression e = ExpressionInput.getExpressionFromString(field.getText(), variableList);
			if (e instanceof InvalidExpression)
			{
				expressionApplyButton.setEnabled(false);
				return;
			}
		}
		
		expressionApplyButton.setEnabled(true);
	}
	
	/*
	 * Checks whether the given text field has a valid input.
	 */
	private boolean isTextFieldValid(JTextField field)
	{
		try
		{
			ExpressionInput.parseDouble(field.getText());
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}
	
	/*
	 * Provides the link between the preset fields in the
	 * and the graph they describe.
	 */
	private abstract class PresetLink
	{
		protected String name;
		protected int index;
		
		/*
		 * Constructs the link between the given textField and graph setting
		 * specified by the index.
		 */
		public PresetLink(String name, int linkIndex)
		{
			this.name = name;
			index = linkIndex;
		}
		
		public abstract void savePreset(FileWriter w) throws IOException;
		
		public abstract boolean loadPreset(String name, String value);
	}
	
	//See the Link class for method details
	private class PresetLinkDouble extends PresetLink
	{
		public PresetLinkDouble(String name, int linkIndex)
		{
			super(name, linkIndex);
		}
		
		public void savePreset(FileWriter w) throws IOException
		{
			w.append(name + ": " + Double.toString(graph.getDouble(index)) + System.getProperty("line.separator"));
		}
		
		public boolean loadPreset(String name, String value)
		{
			if (name.equals(this.name))
			{
				try
				{
					graph.setDouble(index, ExpressionInput.parseDouble(value));
				}
				catch (NumberFormatException e) {}
				return true;
			}
			return false;
		}
	}
	
	private class PresetLinkInt extends PresetLink
	{
		public PresetLinkInt(String name, int linkIndex)
		{
			super(name, linkIndex);
		}
		
		public void savePreset(FileWriter w) throws IOException
		{
			w.append(name + ": " + graph.getInt(index) + System.getProperty("line.separator"));
		}
		
		public boolean loadPreset(String name, String value)
		{
			if (name.equals(this.name))
			{
				try
				{
					graph.setInt(index, (int)(ExpressionInput.parseDouble(value)));
				}
				catch (NumberFormatException e) {}
				return true;
			}
			return false;
		}
	}
	
	/*
	 * Provides the link between the text fields in the
	 * set bounds window and the embedded graph.
	 */
	private abstract class Link
	{
		protected JTextField field;
		protected int index;
		
		/*
		 * Constructs the link between the given textField and graph setting
		 * specified by the index.
		 */
		public Link(JTextField textField, int linkIndex)
		{
			field = textField;
			index = linkIndex;
		}
		
		//Updates the graph's property to match the text field input
		public abstract void setGraphProperty();
		
		//Updates the text of the text field to match the graph's property
		public abstract void setFieldText();
	}
	
	//See the Link class for method details
	private class LinkDouble extends Link
	{
		public LinkDouble(JTextField textField, int linkIndex)
		{
			super(textField, linkIndex);
		}
		
		public void setGraphProperty()
		{
			try
			{
				double value = ExpressionInput.parseDouble(field.getText());
				graph.setDouble(index, value);
			}
			catch (NumberFormatException e) {}
			
			setFieldText();
		}
		
		public void setFieldText()
		{
			field.setText(formatDouble(graph.getDouble(index)));
		}
		
		private String formatDouble(double d)
		{
			String s = String.format("%.8g", graph.getDouble(index));
			int end = s.indexOf('e'); //Ignore exponential part
			if (end == -1) end = s.length();
			
			int i;
			for (i=end-1; s.charAt(i) == '0'; i--); //Remove training zeros
			if (s.charAt(i) == '.') i--; //Remove decimal point if it is next
			
			return s.substring(0, i+1) + s.substring(end);
		}
	}
	
	private class LinkInt extends Link
	{
		public LinkInt(JTextField textField, int linkIndex)
		{
			super(textField, linkIndex);
		}
		
		public void setGraphProperty()
		{
			try
			{
				int value = (int)(ExpressionInput.parseDouble(field.getText()));
				graph.setInt(index, value);
			}
			catch (NumberFormatException e) {}
			
			setFieldText();
		}
		
		public void setFieldText()
		{
			field.setText(Integer.toString(graph.getInt(index)));
		}
	}
}
