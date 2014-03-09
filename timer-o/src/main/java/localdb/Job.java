package localdb;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import listSelectionDialog.StringMatcher;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table( name = "JOB" )
public class Job implements Filterable{
    
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private Long id;

	private String reference;
	private String description;
	private String source;
	
	public Job(){
		//for hibernate
	}
	
	public Job(String reference, String description, String source) {
		this.reference = reference;
		this.description = description;
		this.source = source;
	}

	public Long getId() {
	    return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public List<String> matchStrings() {
		return Arrays.asList(reference,description);
	}

	@Override
	public String toString() {
		return getReference() + " -- " + getDescription();
	}
	
	
	
}
