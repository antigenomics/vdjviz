# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                        bigint not null,
  user_name                 varchar(255),
  user_id                   varchar(255),
  user_dir_path             varchar(255),
  max_files_size            integer,
  max_files_count           integer,
  max_clonotypes_count      integer,
  max_shared_files          integer,
  privelegies               boolean,
  constraint pk_account primary key (id))
;

create table ipaddress (
  ip                        varchar(255) not null,
  count                     bigint,
  banned                    boolean,
  constraint pk_ipaddress primary key (ip))
;

create table local_token (
  uuid                      varchar(255) not null,
  email                     varchar(255),
  created_at                timestamp,
  expire_at                 timestamp,
  is_sign_up                boolean,
  constraint pk_local_token primary key (uuid))
;

create table local_user (
  id                        varchar(255) not null,
  provider                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  account_id                bigint,
  constraint pk_local_user primary key (id))
;

create table shared_file (
  id                        bigint not null,
  group_id                  bigint,
  file_name                 varchar(255),
  unique_name               varchar(255),
  clonotypes_count          integer,
  sample_count              bigint,
  software_type             integer,
  software_type_name        varchar(255),
  file_path                 varchar(255),
  file_dir_path             varchar(255),
  created_at                timestamp,
  constraint ck_shared_file_software_type check (software_type in (0,1,2,3,4,5,6,7)),
  constraint pk_shared_file primary key (id))
;

create table shared_group (
  id                        bigint not null,
  account_id                bigint,
  unique_name               varchar(255),
  cache_path                varchar(255),
  link                      varchar(255),
  description               TEXT,
  constraint pk_shared_group primary key (id))
;

create table tag (
  id                        bigint not null,
  account_id                bigint,
  description               TEXT,
  color                     varchar(255),
  tag_name                  varchar(255),
  constraint pk_tag primary key (id))
;

create table user_file (
  id                        bigint not null,
  account_id                bigint,
  file_name                 varchar(255),
  unique_name               varchar(255),
  clonotypes_count          integer,
  sample_count              bigint,
  software_type             integer,
  software_type_name        varchar(255),
  file_path                 varchar(255),
  file_dir_path             varchar(255),
  file_extension            varchar(255),
  render_state              integer,
  created_at                timestamp,
  constraint ck_user_file_software_type check (software_type in (0,1,2,3,4,5,6,7)),
  constraint ck_user_file_render_state check (render_state in (0,1,2)),
  constraint pk_user_file primary key (id))
;


create table tag_user_file (
  tag_id                         bigint not null,
  user_file_id                   bigint not null,
  constraint pk_tag_user_file primary key (tag_id, user_file_id))
;
create sequence account_seq;

create sequence ipaddress_seq;

create sequence local_token_seq;

create sequence local_user_seq;

create sequence shared_file_seq;

create sequence shared_group_seq;

create sequence tag_seq;

create sequence user_file_seq;

alter table account add constraint fk_account_user_1 foreign key (user_id) references local_user (id) on delete restrict on update restrict;
create index ix_account_user_1 on account (user_id);
alter table local_user add constraint fk_local_user_account_2 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_local_user_account_2 on local_user (account_id);
alter table shared_file add constraint fk_shared_file_group_3 foreign key (group_id) references shared_group (id) on delete restrict on update restrict;
create index ix_shared_file_group_3 on shared_file (group_id);
alter table shared_group add constraint fk_shared_group_account_4 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_shared_group_account_4 on shared_group (account_id);
alter table tag add constraint fk_tag_account_5 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_tag_account_5 on tag (account_id);
alter table user_file add constraint fk_user_file_account_6 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_user_file_account_6 on user_file (account_id);



alter table tag_user_file add constraint fk_tag_user_file_tag_01 foreign key (tag_id) references tag (id) on delete restrict on update restrict;

alter table tag_user_file add constraint fk_tag_user_file_user_file_02 foreign key (user_file_id) references user_file (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists account;

drop table if exists ipaddress;

drop table if exists local_token;

drop table if exists local_user;

drop table if exists shared_file;

drop table if exists shared_group;

drop table if exists tag;

drop table if exists tag_user_file;

drop table if exists user_file;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists account_seq;

drop sequence if exists ipaddress_seq;

drop sequence if exists local_token_seq;

drop sequence if exists local_user_seq;

drop sequence if exists shared_file_seq;

drop sequence if exists shared_group_seq;

drop sequence if exists tag_seq;

drop sequence if exists user_file_seq;

