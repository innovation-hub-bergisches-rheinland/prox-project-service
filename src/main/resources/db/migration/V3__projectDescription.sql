alter Table project add short_description varchar(3000);
alter Table project add requirement varchar(3000);

update project set short_description = description;
