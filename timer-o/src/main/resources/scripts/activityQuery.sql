select  job.description, task.taskDescription, starttime, endtime from job inner join task on task.job_id = job.id  inner join activity_record on task_id = task.id
