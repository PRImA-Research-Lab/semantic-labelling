package org.primaresearch.clc.phd.workflow.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Collection of recent documents that can be saved to / loaded from the temp dir.
 */
public class RecentDocuments {
	
	private List<String> recentDocs = new ArrayList<String>(10);
	private String lastSaveFolder = null;
	private String savePath;
	private String defaultFileExtension;
	
	public RecentDocuments(String defaultFileExtension) {
		this.defaultFileExtension = defaultFileExtension;
		savePath = java.lang.System.getProperty("java.io.tmpdir") + File.separator + "clc_phd_workflowEditor_recentFiles.dat";
		load();
	}

	public void register(String filePath) {
		if (!recentDocs.contains(filePath))
			recentDocs.add(filePath);
		
		setAsLastSaveFolder(filePath);
		
		save();
	}
	
	private void setAsLastSaveFolder(String filePath) {
		try {
			File f = new File(filePath);
			File p = f.getParentFile();
			lastSaveFolder = p != null ? p.getAbsolutePath() : null;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	private void save() {
		
		try {
			OutputStream fos = new FileOutputStream(savePath);
			OutputStream buffer = new BufferedOutputStream(fos);
			ObjectOutput output = new ObjectOutputStream(buffer);
		
			output.writeObject(recentDocs);
			
			output.close();
		} catch(IOException ex) {
			 ex.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void load() {
	    try {
	    	if (!(new File(savePath)).exists())
	    		return;
	    	InputStream fis = new FileInputStream(savePath);
	    	InputStream buffer = new BufferedInputStream(fis);
	    	ObjectInput input = new ObjectInputStream (buffer);
	
	    	recentDocs = (ArrayList<String>)input.readObject();
	    	
	    	input.close();
	    	
	    	if (recentDocs.size() > 0) {
	    		String p = recentDocs.get(recentDocs.size()-1);
	    		setAsLastSaveFolder(p);
	    	}
	    	
	    	//Check if files exist (and add defaultFileExtension if necessary)
	    	List<String> toRemove = new ArrayList<String>();
	    	for (int i=0; i<recentDocs.size(); i++) {
	    		String filePath = recentDocs.get(i);
	    		File f = new File(filePath);
	    		if (f.exists())
	    			continue;
	    		//Try default extension
	    		String extFilePath = filePath + defaultFileExtension;
	    		f = new File(extFilePath);
	    		if (f.exists())
	    			recentDocs.set(i, extFilePath);
	    		else
	    			toRemove.add(filePath);
	    	}
	    	//Remove all non existing files
	    	for (int i=0; i<toRemove.size(); i++)
	    		recentDocs.remove(toRemove.get(i));
	    	
   	    } catch(Exception ex) {
   	    	ex.printStackTrace();
   	    }
	}
	
	public String getLastSaveFolder() {
		return lastSaveFolder;
	}

	public List<String> getRecentDocs() {
		return recentDocs;
	}
	
	
}
