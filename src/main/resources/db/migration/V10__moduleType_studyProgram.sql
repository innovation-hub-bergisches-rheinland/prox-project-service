create table study_program
(
    id       uuid not null,
    key varchar(10) not null,
    name     varchar(255),
    primary key (id)
);

create table module_type
(
    id       uuid not null,
    key varchar(10) not null,
    name     varchar(255),
    primary key (id)
);

create table study_program_modules
(
    study_program_id uuid not null,
    modules_id      uuid not null
);

alter table study_program_modules
    add constraint FK_study_course_modules_study_course
        foreign key (study_program_id) references study_program;

alter table study_program_modules
    add constraint FK_module_type_study_program
        foreign key (modules_id) references module_type;

INSERT INTO study_program(id, key, name) VALUES ('002997c3-7fcb-4361-868a-cb6530326096', 'WI', 'Wirtschaftsinformatik (Bachelor)');
INSERT INTO study_program(id, key, name) VALUES ('fd3d9641-e8dd-4e4a-b17f-3d3d36159c5a', 'AI', 'Informatik (Bachelor)');
INSERT INTO study_program(id, key, name) VALUES ('ecfad8e5-34fe-43c6-9336-c7d52c957a3e', 'MI', 'Medieninformatik (Bachelor)');
INSERT INTO study_program(id, key, name) VALUES ('e58de578-19be-4cf9-b6d0-605c197dd9ee', 'ITM', 'IT-Management (Bachelor)');
INSERT INTO study_program(id, key, name) VALUES ('39dfa8e2-45fd-408d-bade-06a2773c7b41', 'AIT', 'Automation & IT (Master)');
INSERT INTO study_program(id, key, name) VALUES ('95d3ec54-db45-4ec0-86d1-559269a95de3', 'CS', 'Computer Science (Master)');

INSERT INTO module_type(id, key, name) VALUES ('23c4875e-1c2d-4824-beb4-f0d40d924e30', 'BA', 'Bachelorarbeit');
INSERT INTO module_type(id, key, name) VALUES ('f2fbcdab-32e7-4979-8d3b-b59ef506d052', 'PP', 'Praxisprojekt');
INSERT INTO module_type(id, key, name) VALUES ('69b03459-142a-44b0-9b30-cd44076a7092', 'IP', 'Informatikprojekt');
INSERT INTO module_type(id, key, name) VALUES ('5aacac98-664c-4066-978c-04b9342eba3d', 'WIP', 'WI-Projekt');
INSERT INTO module_type(id, key, name) VALUES ('b8327f06-559b-4115-816d-df9adf65ef40', 'ITMP', 'IT-Management Projekt');
INSERT INTO module_type(id, key, name) VALUES ('2f49b807-46d3-4714-a858-0a276c48a717', 'MA', 'Masterarbeit');

/* Bachelorarbeit zu allen Bachelorstudiengängen */
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('002997c3-7fcb-4361-868a-cb6530326096', '23c4875e-1c2d-4824-beb4-f0d40d924e30');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('fd3d9641-e8dd-4e4a-b17f-3d3d36159c5a', '23c4875e-1c2d-4824-beb4-f0d40d924e30');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('ecfad8e5-34fe-43c6-9336-c7d52c957a3e', '23c4875e-1c2d-4824-beb4-f0d40d924e30');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('e58de578-19be-4cf9-b6d0-605c197dd9ee', '23c4875e-1c2d-4824-beb4-f0d40d924e30');

/* Praxisprojekt zu allen Bachelorstudiengängen */
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('002997c3-7fcb-4361-868a-cb6530326096', 'f2fbcdab-32e7-4979-8d3b-b59ef506d052');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('fd3d9641-e8dd-4e4a-b17f-3d3d36159c5a', 'f2fbcdab-32e7-4979-8d3b-b59ef506d052');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('ecfad8e5-34fe-43c6-9336-c7d52c957a3e', 'f2fbcdab-32e7-4979-8d3b-b59ef506d052');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('e58de578-19be-4cf9-b6d0-605c197dd9ee', 'f2fbcdab-32e7-4979-8d3b-b59ef506d052');

/* Fachspezifische Projektarbeiten zu allen Bachelorstudiengängen */
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('002997c3-7fcb-4361-868a-cb6530326096', '5aacac98-664c-4066-978c-04b9342eba3d');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('fd3d9641-e8dd-4e4a-b17f-3d3d36159c5a', '69b03459-142a-44b0-9b30-cd44076a7092');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('e58de578-19be-4cf9-b6d0-605c197dd9ee', 'b8327f06-559b-4115-816d-df9adf65ef40');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('fd3d9641-e8dd-4e4a-b17f-3d3d36159c5a', '69b03459-142a-44b0-9b30-cd44076a7092');

/* Masterarbeit zu allen Masterstudiengängen */
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('39dfa8e2-45fd-408d-bade-06a2773c7b41', '2f49b807-46d3-4714-a858-0a276c48a717');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('95d3ec54-db45-4ec0-86d1-559269a95de3', '2f49b807-46d3-4714-a858-0a276c48a717');

ALTER TABLE project_modules DROP CONSTRAINT fk_project_modules_module;
UPDATE project_modules SET modules_id = '23c4875e-1c2d-4824-beb4-f0d40d924e30' WHERE modules_id IN(SELECT id FROM module WHERE name LIKE 'Bachelor%');
UPDATE project_modules SET modules_id = 'f2fbcdab-32e7-4979-8d3b-b59ef506d052' WHERE modules_id IN(SELECT id FROM module WHERE name LIKE 'Praxis%');
UPDATE project_modules SET modules_id = '2f49b807-46d3-4714-a858-0a276c48a717' WHERE modules_id IN(SELECT id FROM module WHERE name LIKE 'Master%');

CREATE TABLE project_modules2 (LIKE project_modules INCLUDING ALL);
INSERT INTO project_modules2 SELECT DISTINCT project_id, modules_id FROM project_modules;
DROP TABLE project_modules;
ALTER TABLE project_modules2 RENAME TO project_modules;

ALTER TABLE project_modules ADD CONSTRAINT fk_project_modules_module_type FOREIGN KEY (modules_id) REFERENCES module_type;
