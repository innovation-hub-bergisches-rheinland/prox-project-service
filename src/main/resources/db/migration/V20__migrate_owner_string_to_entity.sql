INSERT INTO abstract_owner SELECT DISTINCT 'user' as owner_type, p.creatorid as id FROM project p;
ALTER TABLE project RENAME COLUMN creatorid TO owner_id;
ALTER TABLE project ADD CONSTRAINT fk_project_owner FOREIGN KEY (owner_id) REFERENCES abstract_owner;
