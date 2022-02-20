create table specializations
(
    id   uuid        not null,
    key  varchar(10) not null,
    name varchar(255),
    primary key (id),
    unique (key)
);

insert into specializations(id, "key", name)
VALUES ('80912160-88ec-4742-9ad6-3eaa51f990de', 'INF', 'Informatik'),
       ('c27d3d25-addf-40aa-9d5d-b4af49be8ff0', 'ING', 'Ingenieurwesen');
