/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2022/11/24 20:11:19                          */
/*==============================================================*/

drop database if EXISTS SonarIssue;

create database SonarIssue;

use sonarIssue;


drop table if exists commit;

drop table if exists iss_file;

/*drop index case_match_FK on iss_case;*/

drop table if exists iss_case;

drop table if exists iss_instance;

drop table if exists iss_location;

/*drop index case_match_FK on iss_match;*/

drop table if exists iss_match;

drop table if exists repository;

/*==============================================================*/
/* Table: commit                                                */
/*==============================================================*/



create table commit
(
   commit_hash          varchar(40) not null,
   repo_path            varchar(255) not null,
   committer            varchar(40) not null,
   commit_time          varchar(50) not null,
   commit_msg           varchar(255),
   committer_email      varchar(255),
   parent_commit_hash   varchar(40),
   primary key (commit_hash)
);

-- create unique index commit_from_file_FK on commit
-- (
--   file_path
-- );

-- create unique index repo_commit_FK on commit
-- (
--    repo_path
-- );

/*==============================================================*/
/* Table: iss_file
   file_id              int not null auto_increment,                                     */
/*==============================================================*/
create table iss_file
(

   file_name            varchar(40) not null,
   repo_path            varchar(255) not null,
   file_path            varchar(255) not null,
   created_time             varchar(50) not null,
   primary key (file_path)
);

/*==============================================================*/
/* Table: iss_case                                              */
/*==============================================================*/
create table iss_case
(
   case_id              int unsigned not null,
   type_id              varchar(20) not null,
   commit_hash_new      varchar(40) not null,
   committer_new        varchar(40) not null,
   commit_hash_last     varchar(40) not null,
   commit_hash_disappear varchar(40) not null,
   committer_disappear  varchar(40) not null,
   time_disappear       varchar(50),
   update_time          varchar(50) not null,
   create_time          varchar(50) not null,
   case_status          varchar(20) not null,
   primary key (case_id)
);

/*==============================================================*/
/* Index: case_match_FK                                         */
/*==============================================================*/
-- create unique index case_match_FK on iss_case
-- (
--    case_id
-- );

/*==============================================================*/
/* Table: iss_instance                                          */
/*==============================================================*/
create table iss_instance
(
   inst_id              varchar(36) not null,
   type_id              varchar(20) not null,
   commit_hash          varchar(40) not null,
   commit_time          varchar(50) not null,
   committer            varchar(40) not null,
   file_path            varchar(255) not null,
   description          varchar(255) not null,
   primary key (inst_id)
);

/*==============================================================*/
/* Table: iss_location                                          */
/*==============================================================*/
create table iss_location
(
   inst_id              varchar(36) not null,
   class_                varchar(60) not null,
   method               varchar(60) not null,
   start_line           int unsigned not null,
   end_line             int unsigned not null,
   start_col            int unsigned not null,
   end_col              int unsigned not null,
   line_offset          int unsigned not null,
   code                 varchar(255) not null,
   file_path            varchar(255) not null,
   primary key (inst_id,start_line,end_line,start_col,end_col,code)
);

/*==============================================================*/
/* Table: iss_match                   
   case_id              int unsigned auto_increment,                          */
/*==============================================================*/
create table iss_match
(
   inst_id              varchar(36) not null,
   parent_inst_id       varchar(36) not null,
   case_id              int unsigned not null,
   commit_hash          varchar(40) not null,
   parent_commit_hash   varchar(40) not null,
   primary key (inst_id, parent_inst_id)
);

/*==============================================================*/
/* Index: case_match_FK                                         */
/*==============================================================*/
-- create index case_match_FK on iss_match
-- (
--    case_id
-- );

/*==============================================================*/
/* Table: repository                        
   repo_id              int not null auto_increment,                    */
/*==============================================================*/
create table repository
(

   repo_name            varchar(30) not null,
   path                 varchar(255) not null,
   primary key (path)
);

 alter table commit add constraint FK_repo_commit foreign key (repo_path)
      references repository (path) on delete restrict on update restrict;

alter table iss_file add constraint FK_file_from_repo foreign key (repo_path)
      references repository (path) on delete restrict on update restrict;

alter table iss_instance add constraint FK_commit foreign key (commit_hash)
     references commit (commit_hash) on delete restrict on update restrict;

 alter table iss_location add constraint FK_instance_locate foreign key (inst_id)
       references iss_instance (inst_id) on delete restrict on update restrict;

alter table iss_location add constraint FK_location_from_file foreign key (file_path)
      references iss_file (file_path) on delete restrict on update restrict;

 alter table iss_match add constraint FK_case_match foreign key (case_id)
       references iss_case (case_id) on delete restrict on update restrict;

 alter table iss_match add constraint FK_instance_match foreign key (inst_id)
       references iss_instance (inst_id) on delete restrict on update restrict;

-- alter table iss_match add constraint FK_instance_match2 foreign key (parent_inst_id)
--      references iss_instance (inst_id) on delete restrict on update restrict;

