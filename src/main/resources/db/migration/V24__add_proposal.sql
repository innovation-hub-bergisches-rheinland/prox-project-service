create table proposal
(
    id                uuid not null primary key,
    created_at        timestamp,
    description       varchar(10000),
    modified_at       timestamp,
    name              varchar(255),
    status            integer,
    owner_id          uuid not null,
    short_description varchar(10000),
    requirement       varchar(10000)
);

create table proposal_modules
(
    proposal_id uuid not null
        constraint fk_proposal_modules_proposal
            references proposal,
    modules_id uuid not null
        constraint fk_proposal_modules_module_type
            references module_type,
    unique (proposal_id, modules_id)
);

create table proposal_specializations
(
    proposal_id         uuid not null
        constraint fk_proposal_specializations_proposal
            references proposal,
    specializations_id uuid not null
        constraint fkchon0psr1dmb7afwdj90ogi4c
            references specializations,
    unique  (proposal_id, specializations_id)
);
