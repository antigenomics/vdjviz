# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                        bigint not null,
  login_name                varchar(255),
  user_name                 varchar(255),
  description               varchar(255),
  constraint pk_user primary key (id))
;

create table user_file (
  id                        bigint not null,
  user_id                   bigint,
  test_string               varchar(255),
  file_path                 varchar(255),
  constraint pk_user_file primary key (id))
;

create sequence user_seq;

create sequence user_file_seq;

alter table user_file add constraint fk_user_file_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_file_user_1 on user_file (user_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user;

drop table if exists user_file;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_seq;

drop sequence if exists user_file_seq;

