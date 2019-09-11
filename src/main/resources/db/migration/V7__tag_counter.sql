create table tag_counter (id uuid not null, tag1_id uuid, tag2_id uuid, count int, primary key (id));

alter table tag_counter add constraint tag_counter_tag1 foreign key (tag1_id) references tag;
alter table tag_counter add constraint tag_counter_tag2 foreign key (tag2_id) references tag;
