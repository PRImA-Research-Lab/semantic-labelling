package org.primaresearch.clc.phd.repository.io;

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
import java.util.Iterator;

import javax.swing.DefaultListModel;

import org.primaresearch.clc.phd.repository.CombinedRepository;
import org.primaresearch.clc.phd.repository.FileFolderWorkflowRepository;
import org.primaresearch.clc.phd.repository.WorkflowRepository;

/**
 * Index of file folder repositories. Saves and loads the index to the temp directory.
 * Singleton.
 * 
 * @author clc
 *
 */
public class LocalRepositoryIndex {
	
	private static LocalRepositoryIndex instance;
	private ArrayList<String> folderPaths = new ArrayList<String>();
	private String savePath;
	private DefaultListModel<WorkflowRepository> listModel = null;
	private CombinedRepository combinedRepository;


	private LocalRepositoryIndex() {
		savePath = java.lang.System.getProperty("java.io.tmpdir") + File.separator + "clc_phd_workflowRepositories_folderRepositories.dat";
		loadFilePaths();
	}
	
	public static LocalRepositoryIndex getInstance() {
		if (instance == null)
			instance = new LocalRepositoryIndex();
		return instance;
	}
	
	/**
	 * Returns a list model containing all registered repositories.
	 * Creates and fills the list mode on the first call of this function.
	 * @param model List model
	 */
	public DefaultListModel<WorkflowRepository> getListModel() {
		if (listModel == null) {
			listModel = new DefaultListModel<WorkflowRepository>();
		
			//Add repositories
			combinedRepository = new CombinedRepository("All workflows and activities");
			for (int i=0; i<this.getSize(); i++) {
	        	FileFolderWorkflowRepository repository = new FileFolderWorkflowRepository(this.getFolder(i));
	        	combinedRepository.addChildRepository(repository);
	        	listModel.add(listModel.getSize(), repository);
			}
			listModel.add(listModel.getSize(), combinedRepository);
		}
		return listModel;
	}

	private void loadFilePaths() {
	    try {
	    	if (!(new File(savePath)).exists())
	    		return;
	    	InputStream fis = new FileInputStream(savePath);
	    	InputStream buffer = new BufferedInputStream(fis);
	    	ObjectInput input = new ObjectInputStream(buffer);
	
	    	@SuppressWarnings("unchecked")
			ArrayList<String> paths = (ArrayList<String>)input.readObject();
	    	
	    	input.close();

	    	for (Iterator<String> it = paths.iterator(); it.hasNext(); ) {
	    		String path = it.next();
	    		if (new File(path).exists())
	    			folderPaths.add(path);
	    	}   	
   	    } catch(Exception ex) {
   	    	ex.printStackTrace();
   	    }
	}
	
	private void save() {
		try {
			OutputStream fos = new FileOutputStream(savePath);
			OutputStream buffer = new BufferedOutputStream(fos);
			ObjectOutput output = new ObjectOutputStream(buffer);
		
			output.writeObject(folderPaths);
			
			output.close();
		} catch(IOException ex) {
			 ex.printStackTrace();
		}
	}
	
	public int getSize() {
		return folderPaths.size();
	}
	
	public String getFolder(int index) {
		return folderPaths.get(index);
	}
	
	public void addFolder(String folder) {
		if (!folderPaths.contains(folder))
			folderPaths.add(folder);
		save();
	}

	public void removeFolder(int index) {
		folderPaths.remove(index);
		save();
	}
}
