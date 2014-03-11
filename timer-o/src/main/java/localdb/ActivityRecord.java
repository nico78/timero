package localdb;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;



@Entity
@Table( name = "ACTIVITY_RECORD" )
public class ActivityRecord {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private Long id;
	
	private Date startTime;
	
	private Date endTime;
	
	public ActivityRecord(){
		//for hibernate
	}
	
	public ActivityRecord(Task task, Date startTime){
		this.task = task;
		this.startTime = startTime;
	}

	public ActivityRecord(Task task, Date startTime, Date endTime){
		this.task = task;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	@ManyToOne
	@JoinColumn(name="task_id")
	private Task task;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
	
	
	
}
