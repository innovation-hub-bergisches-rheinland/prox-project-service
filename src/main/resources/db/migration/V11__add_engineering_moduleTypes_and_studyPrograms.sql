INSERT INTO study_program(id, key, name) VALUES ('12902abf-bd21-4e3b-8752-601b227666da', 'AM', 'Allgemeiner Maschinenbau (Bachelor)');
INSERT INTO study_program(id, key, name) VALUES ('3f7245e3-1117-4660-a887-bec0c20ff577', 'AIT', 'Automation & IT (Master)');
INSERT INTO study_program(id, key, name) VALUES ('bad5f8dc-3ff5-44ec-b375-065824c2edb6', 'ET', 'Elektrotechnik (Bachelor)');
INSERT INTO study_program(id, key, name) VALUES ('c120ce1b-8996-4c14-a9bb-fbcfd5215746', 'WINGB', 'Wirtschaftsingenieurwesen (Bachelor)');
INSERT INTO study_program(id, key, name) VALUES ('bd681f40-7537-480a-9b8a-09e29fd7d584', 'WINGM', 'Wirtschaftsingenieurwesen (Master)');

INSERT INTO module_type(id, key, name) VALUES ('5c86296a-c57b-4c8f-b4e0-aa47b6b7aeaa', 'TP', 'Teamprojekt');
INSERT INTO module_type(id, key, name) VALUES ('a1ff0c04-c8b0-45d8-b48d-36c00d49e23a', 'PA', 'Projektarbeit');

/* Bachelorarbeit zu allen Bachelorstudiengängen */
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('12902abf-bd21-4e3b-8752-601b227666da', '23c4875e-1c2d-4824-beb4-f0d40d924e30');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('bad5f8dc-3ff5-44ec-b375-065824c2edb6', '23c4875e-1c2d-4824-beb4-f0d40d924e30');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('c120ce1b-8996-4c14-a9bb-fbcfd5215746', '23c4875e-1c2d-4824-beb4-f0d40d924e30');

/* Masterarbeit zu allen Masterstudiengängen */
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('3f7245e3-1117-4660-a887-bec0c20ff577', '2f49b807-46d3-4714-a858-0a276c48a717');
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('bd681f40-7537-480a-9b8a-09e29fd7d584', '2f49b807-46d3-4714-a858-0a276c48a717');

/* Teamprojekt zu Elektrotechnik */
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('bad5f8dc-3ff5-44ec-b375-065824c2edb6', '5c86296a-c57b-4c8f-b4e0-aa47b6b7aeaa');

/* Projektarbeit zu Wirtschaftsingenieurwesen (Master) */
INSERT INTO study_program_modules(study_program_id, modules_id) VALUES ('bd681f40-7537-480a-9b8a-09e29fd7d584', 'a1ff0c04-c8b0-45d8-b48d-36c00d49e23a');