package core;

import listSelectionDialog.NewItemCreator;
import localdb.DataManager;
import localdb.Job;
import localdb.Task;

public class NewTaskCreator implements NewItemCreator<Task> {

	private DataManager dataManager;
	private Job job;

	public NewTaskCreator(Job job, DataManager dataManager) {
		this.dataManager = dataManager;
		this.job = job;
	}

	@Override
	public Task createItemFor(String text) {
		Task task = new Task(job, text);
		dataManager.save(task);
		return task;
	}

}
