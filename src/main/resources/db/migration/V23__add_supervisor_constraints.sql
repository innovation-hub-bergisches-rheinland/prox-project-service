alter table project drop column supervisor_name;
alter table project_supervisors add constraint UK_project_id_id unique(project_id, id);
