package org.primaresearch.clc.phd.repository;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.io.XmlWorkflowReader;

/**
 * A simple repository implementation that reads all XML workflow files
 * from a specified disk folder.
 * 
 * @author clc
 *
 */
public class FileFolderWorkflowRepository implements WorkflowRepository {
	
	private String repositoryFolder;
	private List<String> filenames = new ArrayList<String>();
	private SortedMap<String, Workflow> workflows = new TreeMap<String,Workflow>();
	
	public FileFolderWorkflowRepository(String repositoryFolder) {
		this.repositoryFolder = repositoryFolder;
		load();
	}

	@Override
	public int getWorkflowCount() {
		return filenames.size();
	}

	@Override
	public Workflow getWorkflow(int index) {
		String filename = filenames.get(index);
		
		//In map?
		if (workflows.containsKey(filename))
			return workflows.get(filename);
		
		//Load
		XmlWorkflowReader reader = new XmlWorkflowReader();
		String filePath = repositoryFolder + File.separator + filename;
		Workflow wf = reader.read(filePath);
		wf.setLocation(filePath);
		
		
		
		//Add to map
		workflows.put(filename, wf);
		
		return wf;
	}
	
	public String getWorkflowFilePath(int index) {
		String filename = filenames.get(index);
		return repositoryFolder + File.separator + filename;
	}

	public String getRepositoryFolder() {
		return repositoryFolder;
	}

	@Override
	public String toString() {
		File f = new File(repositoryFolder);
		return "Folder Repository '"+f.getName()+"' ("+repositoryFolder+")";
	}

	@Override
	public void deleteWorkflow(int index) {
		String filename = filenames.get(index);
		File f = new File(repositoryFolder + File.separator + filename);
		f.delete();
	}
	
	private void load() {
		//Get XML files
		File folder = new File(repositoryFolder);
		String[] xmlFiles = folder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});
		
		for (String s : xmlFiles) {
			filenames.add(s.toLowerCase());
		}
		
		//Sort
		Collections.sort(filenames);
	}

	@Override
	public void refresh() {
		filenames.clear();
		workflows.clear();
		load();
	}

	@Override
	public Collection<Workflow> getWorkflows() {
		Collection<Workflow> ret = new LinkedList<Workflow>();
		for (int i=0; i<filenames.size(); i++)
			ret.add(getWorkflow(i));
		return ret;
	}

	@Override
	public String getId() {
		return repositoryFolder;
	}
}
