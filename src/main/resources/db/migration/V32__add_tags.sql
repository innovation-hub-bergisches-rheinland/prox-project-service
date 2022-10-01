create table project_tags (project_id uuid not null, tags varchar(255));
create table proposal_tags (proposal_id uuid not null, tags varchar(255));

alter table project_tags add constraint FKfvy64usu7e9x7ev6obh91q0qe foreign key (project_id) references project;
alter table proposal_tags add constraint FKjq15fbrihdaprrdcgxk0fults foreign key (proposal_id) references proposal;
