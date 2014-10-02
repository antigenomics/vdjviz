# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                        bigint not null,
  user_name                 varchar(255),
  user_id                   varchar(255),
  constraint pk_account primary key (id))
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

create table user_file (
  id                        bigint not null,
  account_id                bigint,
  file_name                 varchar(255),
  unique_name               varchar(255),
  software_type             integer,
  software_type_name        varchar(255),
  file_path                 varchar(255),
  file_dir_path             varchar(255),
  histogram_data            boolean,
  vdj_usage_data            boolean,
  constraint ck_user_file_software_type check (software_type in (0,1,2,3,4)),
  constraint pk_user_file primary key (id))
;

create sequence account_seq;

create sequence local_token_seq;

create sequence local_user_seq;

create sequence user_file_seq;

alter table account add constraint fk_account_user_1 foreign key (user_id) references local_user (id) on delete restrict on update restrict;
create index ix_account_user_1 on account (user_id);
alter table local_user add constraint fk_local_user_account_2 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_local_user_account_2 on local_user (account_id);
alter table user_file add constraint fk_user_file_account_3 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_user_file_account_3 on user_file (account_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists account;

drop table if exists local_token;

drop table if exists local_user;

drop table if exists user_file;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists account_seq;

drop sequence if exists local_token_seq;

drop sequence if exists local_user_seq;

drop sequence if exists user_file_seq;

