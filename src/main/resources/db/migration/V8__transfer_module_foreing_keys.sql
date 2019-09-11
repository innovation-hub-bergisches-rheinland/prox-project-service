
alter table module add study_course_id uuid;


alter table module
    add constraint FK_study_course_modules
        foreign key (study_course_id) references study_course;


UPDATE module
SET study_course_id = study_course_modules.study_course_id
FROM
study_course_modules, study_course
WHERE
study_course_modules.study_course_id = study_course.id and study_course_modules.modules_id=module.id;



ALTER TABLE module ALTER COLUMN study_course_id set NOT NULL;



drop table  study_course_modules;