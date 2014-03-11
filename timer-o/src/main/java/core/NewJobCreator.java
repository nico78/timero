package core;

import listSelectionDialog.NewItemCreator;
import localdb.DataManager;
import localdb.Job;

public class NewJobCreator implements NewItemCreator<Job> {

	private DataManager dataManager;

	public NewJobCreator(DataManager dataManager) {
		this.dataManager = dataManager;
	}

	@Override
	public Job createItemFor(String text) {
		Job job = new Job(text, text,"TMO");
		dataManager.save(job);
		return job;
	}

}
