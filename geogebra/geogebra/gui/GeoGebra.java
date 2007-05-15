/* 
 GeoGebra - Dynamic Geometry and Algebra
 Copyright Markus Hohenwarter, http://www.geogebra.at

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation; either version 2 of the License, or 
 (at your option) any later version.
 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra.gui;

import geogebra.Application;
import geogebra.util.Util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class GeoGebra extends JFrame implements WindowFocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static ArrayList instances = new ArrayList();

	private static GeoGebra activeInstance;

	private Application app;

	public GeoGebra() {
		instances.add(this);
		activeInstance = this;
	}

	public void dispose() {
		instances.remove(this);
		super.dispose();
	}

	public Application getApplication() {
		return app;
	}

	public void setApplication(Application app) {
		this.app = app;
	}

	public int getInstanceNumber() {
		for (int i = 0; i < instances.size(); i++) {
			if (this == instances.get(i))
				return i;
		}
		return -1;
	}

	public void windowGainedFocus(WindowEvent arg0) {
		activeInstance = this;		
		app.updateMenubar();	
		System.gc();
	}

	public void windowLostFocus(WindowEvent arg0) {	
	}

	public void initFrame() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = 900; // width
		// int w = 1020; // width
		int h = 650; // height
		if (dim.width < w)
			w = dim.width - 10;
		if (dim.height < h)
			h = (int) (dim.height * 0.9);

		/*
		 * int offset = 20 * (instances.size() - 1); setLocation((dim.width - w) /
		 * 2 + offset, (dim.height - h) / 2 + offset);
		 */		
		setSize(w, h);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println(e);
		}

		// check java version
		double javaVersion = Util.getJavaVersion();
		if (javaVersion < 1.42) {
			JOptionPane
					.showMessageDialog(
							null,
							"Sorry, GeoGebra cannot be used with your Java version "
									+ javaVersion
									+ "\nPlease visit http://www.java.com to get a newer version of Java.");
			return;
		}

		createNewWindow(args);		

		// check if we run on a Mac
		String lcOSName = System.getProperty("os.name").toLowerCase();
		boolean MAC_OS = lcOSName.startsWith("mac");

		if (MAC_OS) {
			// handle MacOS X file opening when ggb file is double clicked
			net.roydesign.app.Application.getInstance()
					.addOpenDocumentListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							net.roydesign.event.ApplicationEvent mac_evt = (net.roydesign.event.ApplicationEvent) evt;
							// get filename and build args array
							File fileToOpen = mac_evt.getFile();
							boolean isMacroFile = Application.FILE_EXT_GEOGEBRA_TOOL
									.equals(Application
											.getExtension(fileToOpen)
											.toLowerCase());
							activeInstance.getApplication().loadFile(
									fileToOpen, isMacroFile);
						}
					});
		}

	}

	public static GeoGebra createNewWindow(String[] args) {
		// set Application's size, position and font size
		GeoGebra wnd = new GeoGebra();
		Application app = new Application(args, wnd, true);		
		
		// init GUI
		wnd.app = app;
		wnd.getContentPane().add(app.buildApplicationPanel());
		// wnd.addComponentListener(app.getGUIController());
		wnd.initFrame();
		updateAllTitles();
		wnd.addWindowFocusListener(wnd);
		wnd.setDropTarget(new DropTarget(wnd, new FileDropTargetListener(app)));		
		
		// show window
		wnd.setVisible(true);
		
		app.initInBackground();
		return wnd;
	}

	public static int getInstanceCount() {
		return instances.size();
	}

	public static ArrayList getInstances() {
		return instances;
	}

	static GeoGebra getInstance(int i) {
		return (GeoGebra) instances.get(i);
	}

	public static void updateAllTitles() {
		for (int i = 0; i < instances.size(); i++) {
			Application app = ((GeoGebra) instances.get(i)).app;
			app.updateTitle();
		}
	}

	/**
	 * Checks all opened GeoGebra instances if their current file is the given
	 * file.
	 * 
	 * @param file
	 * @return GeoGebra instance with file open or null
	 */
	public static GeoGebra getInstanceWithFile(File file) {
		if (file == null)
			return null;

		String absPath = file.getAbsolutePath();
		for (int i = 0; i < instances.size(); i++) {
			GeoGebra inst = (GeoGebra) instances.get(i);
			Application app = inst.app;

			File currFile = app.getCurrentFile();
			if (currFile != null) {
				if (absPath.equals(currFile.getAbsolutePath()))
					return inst;
			}
		}
		return null;
	}

	

}