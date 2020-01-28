ALTER TABLE Module DROP COLUMN projecttype;

alter Table Module add project_type integer default 0;