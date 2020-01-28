create table project_tags (project_id uuid not null, tags_id uuid not null);
create table tag (id uuid not null, tag_name varchar(40), primary key (id));

alter table project_tags add constraint FKjf83pxi21aku2npiri4p23vu4 foreign key (tags_id) references tag;
alter table project_tags add constraint FKfvy64usu7e9x7ev6obh91q0qe foreign key (project_id) references project;
