/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.geogebramobile.client.kernel;

import geogebra.geogebramobile.client.io.MyXMLio;
import geogebra.geogebramobile.client.main.Application;

import java.util.LinkedList;
import java.util.ListIterator;

import com.google.gwt.core.client.GWT;

/**
 * UndoManager handles undo information for a Construction. 
 * It uses an undo info list with construction snapshots in temporary files.
 * @author Markus Hohenwarter
 */
public class UndoManager {

	private static final String TEMP_FILE_PREFIX = "GeoGebraUndoInfo";

	// maximum capacity of undo info list: you can undo MAX_CAPACITY - 1 steps
	private static final int MAX_CAPACITY = 100; 

	private Construction construction;
	private LinkedList undoInfoList;	      
	private ListIterator iterator;  // invariant: iterator.previous() is the current state
	private MyXMLio xmlio;

	private Application app;

	/**
	 * Creates a new UndowManager for the given Construction.	 
	 */	
	public UndoManager(Construction c) {				
		construction = c;
		xmlio = new MyXMLio(c.getKernel(), c);	
		undoInfoList = new LinkedList();		

		app = c.getApplication();
	}

	private void updateUndoActions() {
		//AGif (app.hasFullGui())
			//AGapp.getGuiManager().updateActions();		
	}

	/**
	 * Clears undo info list and adds current state to the undo info list.	 
	 */
	synchronized void initUndoInfo() {
		clearUndoInfo();
		storeUndoInfo();
	}       

	private synchronized void clearUndoInfo() {
		undoInfoList.clear();
		iterator = undoInfoList.listIterator();
		System.gc();
	}

	/**
	 * Loads previous construction state from undo info list.
	 */
	public synchronized void undo() {
		
		if (undoPossible()) {		
			iterator.previous();
			loadUndoInfo(iterator.previous());     
			iterator.next();  
			updateUndoActions();
		}				         			     
	}

	/**
	 * Loads next construction state from undo info list.
	 */
	public synchronized void redo() {           
		if (redoPossible()) {
			loadUndoInfo(iterator.next());	  
			updateUndoActions();
		}		   
	}           


	/**
	 * Reloads construction state at current position of undo list
	 * (this is needed for "cancel" actions).
	 */
	final public synchronized void restoreCurrentUndoInfo() {		
		loadUndoInfo(iterator.previous()); 
		iterator.next();   
		updateUndoActions();
	} 	

	/**
	 * Adds construction state to undo info list.
	 */
	public void storeUndoInfo() {	
		// this can cause a java.lang.OutOfMemoryError for very large constructions
		final StringBuilder currentUndoXML = construction.getCurrentUndoXML();
		
//AG		Thread undoSaverThread = new Thread() {
//AG			public void run() {
				doStoreUndoInfo(currentUndoXML);
//AG				System.gc();
//AG			}
//AG		};
//AG		undoSaverThread.start();
	}

	private synchronized void doStoreUndoInfo(final StringBuilder undoXML) {			
			// avoid security problems calling from JavaScript ie setUndoPoint()
			//AGAccessController.doPrivileged(new PrivilegedAction() {
				//AGpublic Object run() {
				//AG	try {			
					
					// perform the security-sensitive operation here
					
					// save to file
				//AG	File undoInfo = createTempFile(undoXML);

					// insert undo info 
				//AG	iterator.add(undoInfo);				 

					// remove everything after the insert position until end of list
			//AG		while (iterator.hasNext()) {
				//AG		undoInfo = (File) iterator.next();
					//AG	iterator.remove();	
					//AG	undoInfo.delete();
			//AG		}

					// delete first if too many in list
			//AG		if (undoInfoList.size() > MAX_CAPACITY) {                						
						// use iterator to delete to avoid ConcurrentModificationException		
						// go to beginning of list
			//AG			while (iterator.hasPrevious())
			//AG				undoInfo = (File) iterator.previous();

			//AG			iterator.remove();	
			//AG			undoInfo.delete();

			//AG			while (iterator.hasNext())
							iterator.next();											
			//AG		}										

			//AG		} 
			//AG		catch (Exception e) {		
			//AG			Application.debug("storeUndoInfo: " + e.toString());
			//AG			e.printStackTrace();
			//AG		}     	
			//AG		catch (java.lang.OutOfMemoryError err) {
			//AG			Application.debug("UndoManager.storeUndoInfo: " + err.toString());
			//AG			err.printStackTrace();
			//AG			System.gc();
			//AG		}
					
					
					
			//AG		return null;
			//AG	}
			//AG});


		//AGupdateUndoActions();
		GWT.log("UndoManager.doStoreUndoInfo must be implemented");
	}		

	/**
	 * Creates a temporary file containing the zipped undoXML.
	 */
	/*AGprivate synchronized File createTempFile(StringBuilder undoXML) throws IOException {
		// create temp file
		File tempFile = File.createTempFile(TEMP_FILE_PREFIX, ".ggb");
		// Remove when program ends
		tempFile.deleteOnExit();

		// create file
		FileOutputStream fos = new FileOutputStream(tempFile);
		MyXMLio.writeZipped(fos, undoXML); 		
		fos.close();

		return tempFile;
	}*/

	/**
	 * restore info at position pos of undo list
	 */
	final private synchronized void loadUndoInfo(final Object info) { 
	/*AG			try {    
					// load from file
					File tempFile = (File) info;
					InputStream is = new FileInputStream(tempFile);	
					
					// load undo info
					app.getScriptManager().disableListeners();
					xmlio.readZipFromMemory(is);					
					app.getScriptManager().enableListeners();
					
					is.close();
				} 
				catch (Exception e) {
					System.err.println("setUndoInfo: " + e.toString());
					e.printStackTrace();      
					restoreCurrentUndoInfo();
				}   
				catch (java.lang.OutOfMemoryError err) {
					System.err.println("UndoManager.loadUndoInfo: " + err.toString());
					System.gc();							
				}*/
		GWT.log("UndoManager.loadUndoInfo must be implemented");

	} 		       

	/**
	 * Returns whether undo operation is possible or not.	 
	 */
	public boolean undoPossible() {  
		if (!app.isUndoActive()) return false;
		return iterator.nextIndex() > 1;	
	}

	/**
	 * Returns whether redo operation is possible or not.	 
	 */
	public boolean redoPossible() {
		if (!app.isUndoActive()) return false;
		return iterator.hasNext();
	}

	/**
	 * Processes xml string. Note: this will change the construction.
	 */
	synchronized void processXML(String strXML) throws Exception {	
		xmlio.processXMLString(strXML, true, false);
	}		

}
