package org.example;
public class Constant {

//    public static String HttpAuthString = "admin:cherry_123";
//    public static String RepoPath = "C:/Users/31324/Desktop/ss-backend/lab2_back-end";
    public static String RepoPath = "E:\\Blood\\secondyear_spring\\se\\work\\lab2_back-end";
    public static String MockPath = "C:/Users/31324/Desktop/dataset/java";
    public static String jdbc_url = "jdbc:mysql://localhost:3306";
    public static String jdbc_user = "root";
    public static String jdbc_password  = "Xqqzldh@38622";

    public static String HttpAuthString = "admin:1230";
//    public static String RepoPath = "E:/Blood/secondyear_spring/se/work/lab2_back-end";
//    public static String jdbc_url = "jdbc:mysql://localhost:3306";
//    public static String jdbc_user = "root";
//    public static String jdbc_password  = "1230";
    public static String delimiter = "DELIMITER ;; ";
    public static String backDelimiter = "DELIMITER ; ";
    public static String func =
        "CREATE DEFINER=`root`@`%` FUNCTION `duration`(d LONG) RETURNS varchar(30) CHARSET utf8mb4 " +
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
