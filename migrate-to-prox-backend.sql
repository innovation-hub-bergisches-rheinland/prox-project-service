/*
This files contains scripts used to migrate data from the services database to prox-backend database.
*/

/* discipline */
SELECT
    d.key,
    d.name,
    current_timestamp as created_at,
    current_timestamp as modified_at
    FROM specializations d

/* module_type */
SELECT
    m.key,
    m.name,
    true as active,
    current_timestamp as created_at,
    current_timestamp as modified_at
    FROM module_type m

/* module_type_disciplines */
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('BA', 'INF');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('BA', 'ING');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('PP', 'INF');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('IP', 'INF');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('WIP', 'INF');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('ITMP', 'INF');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('MA', 'INF');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('MA', 'ING');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('TP', 'ING');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('PA', 'ING');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('QQ1', 'INF');
INSERT INTO module_type_disciplines(module_type_key, disciplines_key) VALUES ('QQ2', 'INF');








/* curriculum_context */
create temporary table temp_project_curriculum as (
    SELECT
    gen_random_uuid() as id,
    project.id as project_id
    FROM project
);

SELECT tpc.id FROM temp_project_curriculum tpc;

/* curriculum_context_module_types */
SELECT
    tpc.id as curriculum_context_id,
    m.key as module_types_key
    FROM project p
    JOIN project_modules pm on p.id = pm.project_id
    JOIN module_type m on pm.modules_id = m.id
    JOIN temp_project_curriculum tpc on p.id = tpc.project_id;

/* curriculum_context_disciplines */
SELECT
    tpc.id as curriculum_context_id,
    s.key as disciplines_key
    FROM project p
    JOIN project_specializations pm on p.id = pm.project_id
    JOIN specializations s on s.id = pm.specializations_id
    JOIN temp_project_curriculum tpc on p.id = tpc.project_id;

/* project */
create temporary table temp_state_translation as (
    VALUES
        (0, 'AVAILABLE'),
        (1, 'RUNNING'),
        (2, 'FINISHED'),
        (3, 'ARCHIVED')
);

SELECT
    p.id as id,
    p.description as description,
    p.requirement as requirement,
    p.short_description as summary,
    p.name as title,
    tpc.id as curriculum_context_id,
    s.column2 as state,
    org.id as organization_id,
    current_timestamp as created_at,
    current_timestamp as modified_at,
    current_timestamp as updated_at
    FROM project p
    JOIN temp_state_translation s ON s.column1 = p.status
    LEFT JOIN (SELECT * FROM abstract_owner o WHERE o.owner_type = 'organization') org ON org.id = p.owner_id
    LEFT JOIN temp_project_curriculum tpc on p.id = tpc.project_id;

SELECT * FROM proposal WHERE committed_supervisor is null;

create temporary table temp_state_translation_proposal as (
    VALUES
        (0, 'PROPOSED'),
        (1, 'ARCHIVED'),
        (2, 'STALE')
);

SELECT
    p.id as id,
    p.description as description,
    p.requirement as requirement,
    p.name as title,
    tpc.id as curriculum_context_id,
    s.column2 as state,
    org.id as organization_id,
    current_timestamp as created_at,
    current_timestamp as modified_at,
    current_timestamp as updated_at
    FROM proposal p
    JOIN temp_state_translation_proposal s ON s.column1 = p.status
    LEFT JOIN (SELECT * FROM abstract_owner o WHERE o.owner_type = 'organization') org ON org.id = p.owner_id
    LEFT JOIN temp_project_curriculum tpc on p.id = tpc.project_id;

SELECT * FROM project;
