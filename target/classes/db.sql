/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2022/11/24 20:11:19                          */
/*==============================================================*/

 drop database if EXISTS SonarIssue;

create database if not exists SonarIssue;
use sonarIssue;


/*==============================================================*/
/* Table: sonarrules                                            */
/*==============================================================*/

create table if not exists `sonarrules`(
      type_id varchar(20),
      description varchar(200),
      severity varchar(10),
      lang varchar(10),
      `type` varchar(20),
      primary key(type_id)
);
/*==============================================================*/
/* Table: commit                                                */
/*==============================================================*/
create table if not exists commit
(
   commit_id            varchar(50) not null,
   commit_hash          varchar(40) not null,
   committer            varchar(40) not null,
   committer_email      varchar(255),
   commit_msg           varchar(255),
   commit_time          datetime not null,
   repo_path            varchar(255) not null,
   parent_commit_hash   varchar(40),
   primary key (commit_id),
   index(commit_time),
   index(committer),
   index(repo_path)
);

/*==============================================================*/
/* Table: iss_case                                              */
/*==============================================================*/
create table if not exists iss_case
(
   case_id              varchar(50) not null,
   type_id              varchar(20) not null,
   commit_id_new        varchar(50) not null,
   commit_id_disappear  varchar(50),
   commit_id_last       varchar(50) not null,
   case_status          varchar(20) not null,
   primary key (case_id),
   foreign key (commit_id_new) references commit(commit_id),
   foreign key (commit_id_disappear) references commit(commit_id),
   foreign key (commit_id_last) references commit(commit_id),
   CHECK (case_status in ('NEW', 'UNDONE', 'SOLVED','REOPEN','NONCHG')),
   index(case_status)
);

/*==============================================================*/
/* Table: iss_instance                                          */
/*==============================================================*/
create table if not exists iss_instance
(
   inst_id              varchar(50) not null,
   parent_inst_id       varchar(50),
   case_id              varchar(50) not null,
   file_path            varchar(255) not null,
   primary key (inst_id),
   index(case_id)
);

/*==============================================================*/
/* Table: iss_location                                          */
/*==============================================================*/
create table if not exists iss_location
(
   location_id          varchar(50) not null,
   inst_id              varchar(50) not null,
   class_               varchar(60),
   method               varchar(60),
   start_line           int unsigned not null,
   end_line             int unsigned not null,
   start_col            int unsigned not null,
   end_col              int unsigned not null,
   code                 varchar(255),
   primary key (location_id),
   foreign key (inst_id) references iss_instance (inst_id)
        on delete restrict
        on update restrict
);

 create table if not exists commit_inst
 (
   inst_id            varchar(50) not null,
   commit_id          varchar(50) not null,
   PRIMARY KEY(inst_id, commit_id),
   foreign key (inst_id) references iss_instance (inst_id)
        on delete restrict
        on update restrict,
   foreign key (commit_id) references commit (commit_id)
        on delete restrict
        on update restrict
 );

/*==============================================================*/
/* Table: instance_location                                     */
/*==============================================================*/
-- create table if not exists instance_location
-- (
--   inst_id              varchar(50) not null,
--   location_id          varchar(50) not null,
--   PRIMARY KEY(inst_id, location_id),
--   foreign key (inst_id) references iss_instance (inst_id)
--        on delete restrict
--        on update restrict,
--   foreign key (location_id) references iss_location (location_id)
--        on delete restrict
--        on update restrict
-- );


/*==============================================================*/
/* Table: repos                                                 */
/*==============================================================*/
create table if not exists repos
(
   repo_path              varchar(50) not null,
   latest_commit_id       varchar(50) not null,
   commit_num             INT unsigned not null default 0,
   PRIMARY KEY(repo_path)
);

set global log_bin_trust_function_creators = 1;
