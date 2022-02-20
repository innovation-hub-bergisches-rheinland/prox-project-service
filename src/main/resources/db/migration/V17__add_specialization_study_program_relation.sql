alter table study_program
    add specialization_id uuid;
alter table study_program
    add constraint FK3un5v7xyrscdmi4s5i5k7tg5k foreign key (specialization_id) references specializations;

update study_program as sp
set specialization_id = c.specialization_id::uuid
from (values ('WI', '80912160-88ec-4742-9ad6-3eaa51f990de'),
             ('AI', '80912160-88ec-4742-9ad6-3eaa51f990de'),
             ('MI', '80912160-88ec-4742-9ad6-3eaa51f990de'),
             ('ITM', '80912160-88ec-4742-9ad6-3eaa51f990de'),
             ('AIT', '80912160-88ec-4742-9ad6-3eaa51f990de'),
             ('CS', '80912160-88ec-4742-9ad6-3eaa51f990de'),
             ('AM', 'c27d3d25-addf-40aa-9d5d-b4af49be8ff0'),
             ('ET', 'c27d3d25-addf-40aa-9d5d-b4af49be8ff0'),
             ('WINGB', 'c27d3d25-addf-40aa-9d5d-b4af49be8ff0'),
             ('WINGM', 'c27d3d25-addf-40aa-9d5d-b4af49be8ff0')
     ) as c(key, specialization_id)
where c.key = sp.key;

alter table study_program
    alter column specialization_id SET NOT NULL;
