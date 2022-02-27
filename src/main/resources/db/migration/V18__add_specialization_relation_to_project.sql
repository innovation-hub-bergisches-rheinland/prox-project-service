create table project_specializations
(
    project_id         uuid not null,
    specializations_id uuid not null,
    primary key (project_id, specializations_id)
);
alter table project_specializations
    add constraint FKchon0psr1dmb7afwdj90ogi4c foreign key (specializations_id) references specializations;
alter table project_specializations
    add constraint FKq3disnc6k9cqe0qqcsx7br708 foreign key (project_id) references project;


