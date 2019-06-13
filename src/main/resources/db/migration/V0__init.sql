create table module
(
    id       uuid not null,
    self_ref varchar(255),
    name     varchar(255),
    primary key (id)
);

create table project
(
    id              uuid not null,
    created         timestamp,
    creatorid       uuid,
    creator_name    varchar(255),
    description     varchar(3000),
    modified        timestamp,
    name            varchar(255),
    status          int4,
    supervisor_name varchar(255),
    primary key (id)
);

create table project_modules
(
    project_id uuid not null,
    modules_id uuid not null
);

create table study_course
(
    id              uuid not null,
    academic_degree int4,
    self_ref        varchar(255),
    name            varchar(255),
    primary key (id)
);

create table study_course_modules
(
    study_course_id uuid not null,
    modules_id      uuid not null
);

alter table study_course_modules
    add constraint UK_study_course_modules_modules_id
        unique (modules_id);

alter table project_modules
    add constraint FK_project_modules_module
        foreign key (modules_id) references module;

alter table project_modules
    add constraint FK_project_modules_project
        foreign key (project_id) references project;

alter table study_course_modules
    add constraint FK_study_course_modules_module
        foreign key (modules_id) references module;

alter table study_course_modules
    add constraint FK_study_course_modules_study_course
        foreign key (study_course_id) references study_course;
