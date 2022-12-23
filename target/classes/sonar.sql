create database if not exists sonar;
use sonar;
create table if not exists `sonarissues`(
    id varchar(36),
    `type_id` varchar(30),
    severity varchar(20),
    file_path varchar(200),
    repo_id varchar(20),
    location varchar(400),
    message varchar(200),
    `type` varchar(20),
    primary key(id)
);

create table if not exists `sonarrules`(
      id varchar(20),
      description varchar(200),
      severity varchar(10),
      lang varchar(10),
      `type` varchar(20),
      primary key(id)
);