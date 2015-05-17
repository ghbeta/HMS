# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  matrikel                  varchar(255) not null,
  firstname                 varchar(255),
  lastname                  varchar(255),
  sha1                      varchar(255),
  constraint pk_user primary key (matrikel))
;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_seq;

