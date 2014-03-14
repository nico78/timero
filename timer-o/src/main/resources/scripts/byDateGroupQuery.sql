select  job.reference, job.description, task.taskDescription, sum(endtime-starttime)  as time_for_period, min(starttime) as earliest_start, max(endtime) as latest_end  from job inner join task on task.job_id = job.id  inner join activity_record on task_id = task.id
where starttime > DATE '2014-03-14' and endtime < DATE '2014-03-15'
group by job.reference, job.description, task.taskDescription