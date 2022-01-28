alter table project rename to projects;
alter table projects rename column id to project_id;
alter table projects rename column creatorid to creator_id;
alter table projects rename column created to created_at;
alter table projects rename column modified to modified_at;