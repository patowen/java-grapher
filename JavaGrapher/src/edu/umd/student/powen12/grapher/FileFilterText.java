package edu.umd.student.powen12.grapher;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilterText extends FileFilter
{
	public boolean accept(File f)
	{	
		if (f.isDirectory()) return true;
		
		String fName = f.getName();
		int i = fName.lastIndexOf('.');
		if (i == -1)
			return false;
		
		if (fName.substring(i).equals(".txt"))
			return true;
		
		return false;
	}
	
	public String getDescription()
	{
		return "Text files";
	}
}
