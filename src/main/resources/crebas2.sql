/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2022/11/24 20:11:19                          */
/*==============================================================*/

drop database if EXISTS SonarIssue;

create database SonarIssue;

use sonarIssue;


drop table if exists commit;

drop table if exists iss_file;

drop table if exists iss_case;

drop table if exists iss_instance;

drop table if exists iss_location;

drop table if exists iss_match;

drop table if exists repository;

/*==============================================================*/
/* Table: sonarrules                                            */
/*==============================================================*/

create table if not exists `sonarrules`(
      id varchar(20),
      description varchar(200),
      severity varchar(10),
      lang varchar(10),
      `type` varchar(20),
      primary key(id)
);
/*==============================================================*/
/* Table: commit                                                */
/*==============================================================*/
create table commit
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
   index(committer)
);

/*==============================================================*/
/* Table: iss_case                                              */
/*==============================================================*/
create table iss_case
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
   CHECK (case_status in ('NEW', 'UNDONE', 'SOLVED','REOPEN')),
   index(case_status)
);

/*==============================================================*/
/* Table: iss_instance                                          */
/*==============================================================*/
create table iss_instance
(
   inst_id              varchar(50) not null,
   type_id              varchar(20) not null,
   commit_id            varchar(50) not null,
   parent_inst_id       varchar(50),
   case_id              varchar(50) not null,
   file_path            varchar(255) not null,
   primary key (inst_id),
   foreign key (commit_id) references commit (commit_id)
        on delete restrict
        on update restrict,
   index(case_id)
);

/*==============================================================*/
/* Table: iss_location                                          */
/*==============================================================*/
create table iss_location
(
   location_id          varchar(50) not null,
   class_               varchar(60) not null,
   method               varchar(60) not null,
   start_line           int unsigned not null,
   end_line             int unsigned not null,
   start_col            int unsigned not null,
   end_col              int unsigned not null,
   code                 varchar(255) not null,
   primary key (location_id)
);

/*==============================================================*/
/* Table: instance_location                                     */
/*==============================================================*/
create table instance_location
(
   inst_id              varchar(50) not null,
   location_id          varchar(50) not null,
   PRIMARY KEY(inst_id, location_id),
   foreign key (inst_id) references iss_instance (inst_id)
        on delete restrict
        on update restrict,
   foreign key (location_id) references iss_location (location_id)
        on delete restrict
        on update restrict
);


delimiter $
create function if not exists get_length(new varchar(50), cur varchar(50)) returns int
begin
    declare len int;
    set len = 0;
    declare tmp varchar(50);
    set tmp = new;
    while new <> cur do
    select parent_inst_id into tmp from iss_instance
    where inst_id = tmp;
    len = len + 1;
    end while;
    return len;
end $

delimiter $
create function if not exists get_length_by_case_id(c_id varchar(50)) returns int
begin
    declare new varchar(50);
    declare `last` varchar(50);
    select (commit_id_new, commit_id_last) into (new, `last`) from iss_case
    where case_id = c_id;
    return get_length(new, `last`);
end $
--
-- alter table commit add constraint FK_repo_commit foreign key (repo_path)
--      references repository (path) on delete restrict on update restrict;
--
-- alter table iss_file add constraint FK_file_from_repo foreign key (repo_path)
--      references repository (path) on delete restrict on update restrict;

-- alter table iss_file add constraint FK_file_in_location foreign key (file_path)
--      references iss_location (file_path) on delete restrict on update restrict;

-- alter table iss_instance add constraint FK_commit foreign key (commit_id)
--     references commit (commit_id) on delete restrict on update restrict;

-- alter table iss_location add constraint FK_instance_locate foreign key (inst_id)
--       references iss_instance (inst_id) on delete restrict on update restrict;

-- alter table instance_location add constraint FK_instance_from foreign key (inst_id)
--       references iss_instance (inst_id) on delete restrict on update restrict;

-- alter table instance_location add constraint FK_locate_in foreign key (location_id)
--       references iss_location (location_id) on delete restrict on update restrict;

-- alter table iss_location add constraint FK_location_from_file foreign key (file_path)
--      references iss_file (file_path) on delete restrict on update restrict;

-- alter table iss_match add constraint FK_instance_match foreign key (inst_id)
--       references iss_instance (inst_id) on delete restrict on update restrict;

-- alter table iss_match add constraint FK_instance_match2 foreign key (parent_inst_id)
--      references iss_instance (inst_id) on delete restrict on update restrict;

