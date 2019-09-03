alter Table project add short_description nvarchar(3000);
alter Table project add requirement nvarchar(3000);

update project set short_description = description;
