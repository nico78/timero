create table job(id bigint not null,reference varchar(10), description varchar(255), source char(3));
create table task(id bigint not null, taskDescription varchar(255), jobreference varchar(10));
create table activityRecord(id bigint not null, startTime timestamp, endTime timestamp, taskid bigint not null);