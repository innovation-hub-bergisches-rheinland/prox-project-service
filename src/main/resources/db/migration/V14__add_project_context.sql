alter table project add column context int4 not null default 1;

alter table project alter column context drop default ;