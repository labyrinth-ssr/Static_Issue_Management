package org.example;

import org.example.Command.QueryMappingByDev;
import org.example.Command.QueryMappingById;
import org.example.Command.QueryMappingByTime;
import org.example.Utils.SqlConnect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class QueryTest {
    static String pj_path = Constant.RepoPath;
    static SqlConnect mysqlConnect = new SqlConnect();
    static QueryMappingById queryMappingById;
    static QueryMappingByTime queryMappingByTime;
    static QueryMappingByDev queryMappingByDev;
    public static void main(String[] args) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, IOException, InterruptedException {
        mysqlConnect.useDataBase("sonarissue");
        queryMappingById = new QueryMappingById(mysqlConnect);
        queryMappingByTime = new QueryMappingByTime(mysqlConnect);
        queryMappingByDev = new QueryMappingByDev(mysqlConnect);

//        MappingIdTest();
        MappingTimeTest();
        MappingDevTest();
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        System.in.read();
    }

    public static void MappingIdTest() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String latest = queryMappingById.getCommitLatest();
        queryMappingById.getCountDoneByCommit_id(latest);
        queryMappingById.getCountDoneInTypeByCommit_id(latest);
        queryMappingById.getGCountInTypeLatest();
        queryMappingById.getCountInLatest();
        queryMappingById.getListInLatest();
        queryMappingById.getListInByCommit_id(latest);
        queryMappingById.getListDoneByCommit_id(latest);
        queryMappingById.getCountDoneInTypeByCommit_id(latest);
        queryMappingById.getGCountUnsolvedByCommit_id(latest);
    }
    public static void MappingTimeTest() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String begin_time = "2020-12-12 23:12:12";
        String end_time = "2022-12-12 23:12:12";
//        queryMappingByTime.getCountDoneByCommit_time(begin_time,end_time,pj_path);
//        queryMappingByTime.getCountDoneInTypeByCommit_time(begin_time,end_time,pj_path);
//        queryMappingByTime.getGCountInTypeByCommit_time(begin_time,end_time,pj_path);
//        queryMappingByTime.getListInByCommit_time(begin_time,end_time,pj_path);
        queryMappingByTime.getGCountUnsolvedByCommit_time(begin_time,end_time,pj_path);
        queryMappingByTime.getListDoneByCommit_time(begin_time,end_time,pj_path);
        queryMappingByTime.getCountInByCommit_time(begin_time,end_time,pj_path);
        queryMappingByTime.getAnalysisCountByCommit_time(begin_time,end_time,pj_path);
        queryMappingByTime.getAnalysisCountByType_idByCommit_time(begin_time,end_time,pj_path);
        queryMappingByTime.getAnalysisCountByTypeByCommit_time(begin_time,end_time,pj_path);
        queryMappingByTime.getCommit_idListByTimeAndRepo(begin_time,end_time,pj_path);
        String duration = "1天1时1分";
        queryMappingByTime.getDefectListMoreThanDuration(duration,pj_path);
    }
    public static void MappingDevTest() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        queryMappingByDev.getDevCountByDevs("alex",pj_path);
        queryMappingByDev.getDevTypeCountByDevs("alex",pj_path);
    }
}
