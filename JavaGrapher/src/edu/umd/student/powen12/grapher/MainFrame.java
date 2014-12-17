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
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * the main menu for the Graph Program.
 * @author Patrick Owen
 */
public class MainFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private Thread versionThread;
	private int version = 1;
	private boolean isOutdated;
	private boolean mainURL;
	
	private JMenuBar menuBar;
	
	private JMenu fileMenu;
	private JMenuItem newFunctionGraph;
	private JMenuItem newParaGraph;
	private JMenuItem newGeneralGraph;
	private JMenuItem newFunctionGraph3D;
	private JMenuItem newParaCurve3D;
	private JMenuItem newParaGraph3D;
	private JMenuItem newDiskGraph3D;
	private JMenuItem newShellGraph3D;
	private JMenuItem newCrossSectionGraph3D;
	
	private JMenu helpMenu;
	private JMenuItem menuHelp;
	private JMenuItem menuAbout;
	
	/**
	 * Initializes the main menu for the Graph Program.
	 */
	public MainFrame()
	{
		super("Grapher");
		
		isOutdated = false;
		mainURL = true;
		versionThread = new Thread()
		{
			public void run()
			{
				checkVersion(); //Check version without blocking.
			}
		};
		versionThread.start();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if (isOutdated)
				{
					askToUpdate();
				}
				dispose();
				System.exit(0);
			}
		});
		
		getContentPane().setBackground(Color.GRAY);
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		
		newFunctionGraph = new JMenuItem("New Function Graph");
		newParaGraph = new JMenuItem("New Parametric Curve");
		newGeneralGraph = new JMenuItem("New General Graph"); //Consider naming solution set graph
		newFunctionGraph3D = new JMenuItem("New 3D Function Graph");
		newParaCurve3D = new JMenuItem("New 3D Parametric Curve");
		newParaGraph3D = new JMenuItem("New 3D Parametric Surface");
		newDiskGraph3D = new JMenuItem("New Disk/Washer Graph");
		newShellGraph3D = new JMenuItem("New Cylindrical Shell Graph");
		newCrossSectionGraph3D = new JMenuItem("New Cross Section Graph");
		addItem(newFunctionGraph);
		addItem(newParaGraph);
		addItem(newGeneralGraph);
		fileMenu.addSeparator();
		addItem(newFunctionGraph3D);
		addItem(newParaCurve3D);
		addItem(newParaGraph3D);
		fileMenu.addSeparator();
		addItem(newDiskGraph3D);
		addItem(newShellGraph3D);
		addItem(newCrossSectionGraph3D);
		
		menuBar.add(fileMenu);
		
		helpMenu = new JMenu("Help");
		menuHelp = new JMenuItem("Help");
		menuAbout = new JMenuItem("About");
		menuHelp.addActionListener(this);
		menuAbout.addActionListener(this);
		helpMenu.add(menuHelp);
		helpMenu.add(menuAbout);
		
		menuBar.add(helpMenu);
		
		setJMenuBar(menuBar);
		
		pack();
		setMinimumSize(getSize());
		setSize(800, 600);
	}
	
	private void checkVersion()
	{
		try
		{
			URL url = new URL("http://powen12.student.umd.edu/version"); //Main URL
			InputStream input;
			try
			{
				input = url.openStream();
			}
			catch (IOException e)
			{
				mainURL = false;
				url = new URL("http://patrickowen.co.nf/version"); //Fallback URL
				input = url.openStream();
			}
			
			Scanner scan = new Scanner(input);
			String line = scan.nextLine();
			scan.close();
			int latest = Integer.parseInt(line);
			
			if (version < latest)
			{
				isOutdated = true;
			}
		}
		//Fail checking version silently. It's not necessary to check the version.
		catch (MalformedURLException e)
		{
		}
		catch (IOException e)
		{
		}
		catch (NumberFormatException e)
		{
		}
	}
	
	private void askToUpdate()
	{
		String url;
		if (mainURL)
			url = "http://powen12.student.umd.edu/download.html";
		else
			url = "http://patrickowen.co.nf/download.html";

		int option = JOptionPane.showConfirmDialog(MainFrame.this,
				"A new version is available. Do you want to go to <" + url + "> to download it?",
				"Update Available", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION)
		{
			boolean failure = false;
			if (Desktop.isDesktopSupported())
			{
				Desktop desktop = Desktop.getDesktop();
				try
				{
					desktop.browse(new URI(url));
				}
				catch (IOException e1) {failure = true;}
				catch (URISyntaxException e1) {failure = true;}
			}
			else
			{
				failure = true;
			}
			if (failure)
				JOptionPane.showMessageDialog(MainFrame.this,
						"This application cannot open <" + url + ">. It must be done manually.",
						"Failed to Open Browser", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void openHelp()
	{
		String url;
		if (mainURL)
			url = "http://powen12.student.umd.edu/tutorial.html";
		else
			url = "http://patrickowen.co.nf/tutorial.html";
		
		boolean failure = false;
		if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();
			try
			{
				desktop.browse(new URI(url));
			}
			catch (IOException e1) {failure = true;}
			catch (URISyntaxException e1) {failure = true;}
		}
		else
		{
			failure = true;
		}
		if (failure)
			JOptionPane.showMessageDialog(MainFrame.this,
					"This application cannot open <" + url + ">. It must be done manually.",
					"Failed to Open Browser", JOptionPane.ERROR_MESSAGE);
	}
	
	/*
	 * Shortcut to adding a menu option to the menu and adding this
	 * class as its ActionListener.
	 */
	private void addItem(JMenuItem item)
	{
		fileMenu.add(item);
		item.addActionListener(this);
	}
	
	/*
	 * Showing each graph is done through separate method in case
	 * the process got more complicated, which it has not so far.
	 */
	private void showGraph()
	{
		new FunctionGraphWindow(this);
	}
	
	private void showGeneralGraph()
	{
		new GeneralGraphWindow(this);
	}
	
	private void showGraph3D()
	{
		new FunctionGraph3DWindow(this);
	}
	
	private void showParaGraph()
	{
		new ParaGraphWindow(this);
	}
	
	private void showParaCurve3D()
	{
		new ParaCurve3DWindow(this);
	}
	
	private void showParaGraph3D()
	{
		new ParaGraph3DWindow(this);
	}
	
	private void showDiskGraph3D()
	{
		new DiskGraph3DWindow(this);
	}

	private void showShellGraph3D()
	{
		new ShellGraph3DWindow(this);
	}
	
	private void showCrossSectionGraph3D()
	{
		new CrossSectionGraph3DWindow(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == newFunctionGraph)
			showGraph();
		else if (e.getSource() == newGeneralGraph)
			showGeneralGraph();
		else if (e.getSource() == newFunctionGraph3D)
			showGraph3D();
		else if (e.getSource() == newParaGraph)
			showParaGraph();
		else if (e.getSource() == newParaCurve3D)
			showParaCurve3D();
		else if (e.getSource() == newParaGraph3D)
			showParaGraph3D();
		else if (e.getSource() == newDiskGraph3D)
			showDiskGraph3D();
		else if (e.getSource() == newShellGraph3D)
			showShellGraph3D();
		else if (e.getSource() == newCrossSectionGraph3D)
			showCrossSectionGraph3D();
		else if (e.getSource() == menuHelp)
			openHelp();
		else if (e.getSource() == menuAbout)
			new AboutWindow(this);
	}
}
