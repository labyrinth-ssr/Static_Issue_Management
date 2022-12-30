package org.example;
public class Constant {
    public static String func =
        "CREATE FUNCTION if not exists `duration`(d LONG) RETURNS varchar(30) CHARSET utf8mb4 " +
        "begin " +
        "DECLARE dur varchar(30); " +
        "DECLARE day int; " +
        "DECLARE hour int; " +
        "DECLARE min int; " +
        "DECLARE sec int; " +
        "set day = d/ (24 * 60 * 60); " +
        "set hour = d/ (60 * 60) % 24; " +
        "set min = d/ (60) % 60; " +
        "set sec = d% 60; " +
        "set dur = concat(day,'天',hour,'时',min,'分',sec,'秒'); " +
        "RETURN dur; " +
        "end " +
        ";; ";
}
