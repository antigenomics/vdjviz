# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                        bigint not null,
  user_name                 varchar(255),
  user_id                   bigint,
  constraint pk_account primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  sessionhash               varchar(255),
  account_id                bigint,
  constraint pk_user primary key (id))
;

create table user_file (
  id                        bigint not null,
  account_id                bigint,
  file_name                 varchar(255),
  unique_name               varchar(255),
  software_type             integer,
  software_type_name        varchar(255),
  file_path                 varchar(255),
  constraint ck_user_file_software_type check (software_type in (0,1,2,3,4)),
  constraint pk_user_file primary key (id))
;

create sequence account_seq;

create sequence user_seq;

create sequence user_file_seq;

alter table account add constraint fk_account_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_account_user_1 on account (user_id);
alter table user add constraint fk_user_account_2 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_user_account_2 on user (account_id);
alter table user_file add constraint fk_user_file_account_3 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_user_file_account_3 on user_file (account_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists account;

drop table if exists user;

drop table if exists user_file;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists account_seq;

drop sequence if exists user_seq;

drop sequence if exists user_file_seq;

