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

import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Displays a quick message that shows the copyright information on
 * the program.
 * @author Patrick Owen
 */
public class AboutWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	public AboutWindow(Window owner)
	{
		super(owner, "About Patrick's Grapher");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JTextArea textArea = new JTextArea("Copyright 2013 Patrick Owen\n\n" +
				"Patrick's Grapher v.1.1\n\n" +
				"Patrick's Grapher is free software: you can redistribute it and/or modify " +
				"it under the terms of the GNU General Public License as published by " +
				"the Free Software Foundation, either version 3 of the License, or " +
				"any later version.\n\n" +
				"Patrick's Grapher is distributed in the hope that it will be useful, " +
				"but WITHOUT ANY WARRANTY; without even the implied warranty of " +
				"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
				"GNU General Public License for more details.\n\n" +
				"You should have received a copy of the GNU General Public License" +
				"along with Patrick's Grapher.  If not, see <http://www.gnu.org/licenses/>.", 15, 60);
		textArea.setFont(new Font("Serif", Font.PLAIN, 12));
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		
		textArea.setEditable(false);
		add(new JScrollPane(textArea));
		
		pack();
		
		setLocationRelativeTo(owner);
		setVisible(true);
	}
}
