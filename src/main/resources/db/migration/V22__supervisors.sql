create table project_supervisors (project_id uuid not null, id uuid, name varchar(255));
alter table project_supervisors add constraint FK4nyjnuxg7lqyxqmw5sbtnbx3o foreign key (project_id) references project;

/* Needs manual work */
INSERT INTO project_supervisors SELECT p.id, '00000000-0000-0000-0000-000000000000'::uuid as id, trim(regexp_split_to_table(p.supervisor_name, '[,&]')) as name FROM project p;
