# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                        bigint auto_increment not null,
  user_name                 varchar(255),
  user_id                   varchar(255),
  user_dir_path             varchar(255),
  constraint pk_account primary key (id))
;

create table local_token (
  uuid                      varchar(255) not null,
  email                     varchar(255),
  created_at                datetime,
  expire_at                 datetime,
  is_sign_up                tinyint(1) default 0,
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
  id                        bigint auto_increment not null,
  account_id                bigint,
  file_name                 varchar(255),
  unique_name               varchar(255),
  sample_count              bigint,
  software_type             integer,
  software_type_name        varchar(255),
  file_path                 varchar(255),
  file_dir_path             varchar(255),
  file_extension            varchar(255),
  render_state              integer,
  constraint ck_user_file_software_type check (software_type in (0,1,2,3)),
  constraint ck_user_file_render_state check (render_state in (0,1,2)),
  constraint pk_user_file primary key (id))
;

alter table account add constraint fk_account_user_1 foreign key (user_id) references local_user (id) on delete restrict on update restrict;
create index ix_account_user_1 on account (user_id);
alter table local_user add constraint fk_local_user_account_2 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_local_user_account_2 on local_user (account_id);
alter table user_file add constraint fk_user_file_account_3 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_user_file_account_3 on user_file (account_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table account;

drop table local_token;

drop table local_user;

drop table user_file;

SET FOREIGN_KEY_CHECKS=1;

