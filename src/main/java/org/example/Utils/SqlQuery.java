package org.example.Utils;


import org.example.Query.QueryUseEntity.*;
import org.example.Entity.Commit;
import org.example.Entity.Iss_case;
import org.example.Entity.Iss_match;
import org.example.Entity.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用于数据查询操作
 * 使用前您需要对数据库环境进行配置
 * */
public class SqlQuery {
    public static String repo_id;

    public SqlMapping sqlMapping;

    public String FULL_VIEW = "full_view";
    public String full_view_sql = "create or replace view full_view as\n" +
            "select ic.*, sr.type, ii1.inst_id inst_id_new, ii2.inst_id inst_id_last, c1.commit_time time_new, c2.commit_time time_disappear, c1.committer committer_new, c2.committer committer_disappear,\n" +
            "case case_status when 'SOLVED'\n" +
            "then TIMESTAMPDIFF(SECOND,c1.commit_time, c2.commit_time)\n" +
            "else TIMESTAMPDIFF(SECOND,c1.commit_time, LOCALTIME()) END\n" +
            "duration\n" +
            "from iss_case ic join commit c1 on ic.commit_id_new = c1.commit_id\n" +
            "left join commit c2 on ic.commit_id_disappear = c2.commit_id\n" +
            "join iss_instance ii1 on ic.commit_id_new = ii1.commit_id and ii1.case_id = ic.case_id\n" +
            "join iss_instance ii2 on ic.commit_id_last = ii2.commit_id and ii2.case_id = ic.case_id\n" +
            "join sonarrules sr on sr.id = ic.type_id;";

    public SqlQuery(SqlConnect connect) throws SQLException {
        sqlMapping = new SqlMapping(connect);
        sqlMapping.execute(full_view_sql);
    }


    //获取最新版本commit_id
    public String getLatestCommitId() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

//        String sql = "select commit_id as commit_hash from commit where commit_hash not in (select parent_commit_hash from commit);";
        String sql = "SELECT commit_id from commit where commit_time = (select max(commit_time) from commit);";
        List<commit> c = (List<commit>) sqlMapping.select(new commit(),sql);
        return c.get(0).getCommit_hash();
    }

    public List<DefectCommitEntity> getDefectCommit(String commit_id, String time_begin, String time_end) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_commit = isEmpty(commit_id) ? "" : ("c.commit_id = '" + commit_id + "' && ");
        String sql_time_begin = isEmpty(time_begin) ? "" : ("commit_time >= '" + time_begin + "' && ");
        String sql_time_end = isEmpty(time_end) ? "" : "commit_time <= '" + time_end +"' && ";
        String sql = "select c.commit_id as commit_hash,committer,commit_time,commit_msg, average_exist_duration, in_count, out_count\n" +
                "from \n" +
                "(select commit_id, avg(if(status is not null, duration, null)) average_exist_duration, count(if(status='IN',1,null)) in_count, count(if(status='OUT',1,null)) out_count  \n" +
                "from full_view group by commit_id) a join commit c on a.commit_id = c.commit_id";
        if(!(sql_commit + sql_time_begin + sql_time_end).equals("")) {
            sql += " where " + sql_commit + sql_time_begin + sql_time_end;
            sql = sql.substring(0, sql.lastIndexOf("&&"));
        }
        sql += " order by commit_time desc;";
        System.out.println("getDefetcCommit_sql: "+sql);
        return (List<DefectCommitEntity>) sqlMapping.select(new DefectCommitEntity(), sql);
    }

    //! 此处有根据commit_id查找type_id需求
    public List<DefectTypeEntity> getDefectType(String commit_id, String status) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select type_id, description, average_exist_duration, count\n" +
                "from\n" +
                "(select type_id, avg(duration) average_exist_duration, count(*) count\n" +
                "from full_view\n" +
                "where commit_id = '" + commit_id + "'\n" +
                "and status = '" + status + "' "+
                "group by type_id\n" +
                "order by avg(duration) desc, count desc) a, sonarrules sr\n" +
                "where a.type_id = sr.id";
        System.out.println("getDefectType_sql: "+sql);
        return (List<DefectTypeEntity>) sqlMapping.select(new DefectTypeEntity(), sql);
    }


    public List<DefectEntity> getDefectEntity(String commit_id, String type_id, String status) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "SELECT a.exist_duration, a.case_id, a.inst_id, il.file_path, il.class_, il.method, il.`code`, il.start_line, il.start_col, case a.case_status when 'SOLVED' then '已解决' else '未解决' end status\n" +
                "FROM( SELECT duration AS exist_duration, case_id, inst_id, location_id, case_status FROM full_view\n" +
                "\t\tWHERE\n" +
                "\t\t\tcommit_id = '" + commit_id + "'\n" +
                "\t\tAND type_id = '" + type_id + "'\n" +
                "\t\tAND status = '" + status + "'\n" +
                "\t\tORDER BY duration DESC\n" +
                "\t) a left JOIN iss_location il ON a.location_id = il.location_id";
        System.out.println("getDefectEntity_sql: "+sql);
        return (List<DefectEntity>) sqlMapping.select(new DefectEntity(), sql);
    }

    //! repos

    public List<Repository> getRepositories() throws Exception {
        return (List<Repository>) sqlMapping.select(new Commit(), new Repository(), null, " group by repo_path");
    }

    public List<List<AnalysisEntity>> getUserStatistic(String user, int status) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String tmp = FULL_VIEW;
        if(status == 1){
            tmp += " where committer_new = '" + user + "' and status = 'IN'";
        }else if(status == 2){
            tmp += " where committer_new <> '" + user + "' and status = 'OUT' and committer_disappear = '" + user+"'";
        }else if(status == 3) {
            tmp += " where committer_new = '" + user + "' and status = 'IN' and case_status <> 'SOLVED'";
        }else if(status == 4){
            tmp += " where committer_new = '" + user + "' and status = 'IN' and case_status = 'SOLVED' and committer_disappear <> '" + user+"'";
        }
        String total_sql = "select total, done, concat(round((done/total)*100,2),'%') as percentage, type, average_exist_duration\n" +
                "from (select count(*) total ,COUNT(if(case_status = 'SOLVED',1,null)) done, null as type, AVG(duration) average_exist_duration from " + tmp + ") a \n" +
                "order by (done/total) asc, total desc;";
        String defect_sql = "select total, done, concat(round((done/total)*100,2),'%') as percentage, type, average_exist_duration\n" +
                "from \n" +
                "(select count(*) total, count(if(case_status = 'SOLVED',1,null)) done, type, AVG(duration) average_exist_duration from " + tmp + " group by type) a\n" +
                "order by percentage asc, total desc;";
        String type_sql = "select total, done, concat(round((done/total)*100,2),'%') as percentage, type, average_exist_duration\n" +
                "from \n" +
                "(select count(*) total, count(if(case_status = 'SOLVED',1,null)) done, type_id as type, AVG(duration) average_exist_duration from " + tmp + " group by type_id) a\n" +
                "order by percentage asc, total desc;";
        List<AnalysisEntity> totalEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), total_sql);
        List<AnalysisEntity> defectEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), defect_sql);
        List<AnalysisEntity> typeEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), type_sql);

        List<List<AnalysisEntity>> anaysis = new ArrayList<>();
        anaysis.add(totalEntity);
        anaysis.add(defectEntity);
        anaysis.add(typeEntity);
        return anaysis;
    }


    public List<developer> getDevelopers() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select committer, committer_email from commit group by committer, committer_email;";
        return (List<developer>) sqlMapping.select(new developer(), sql);
    }

    public List<Commit> getCommitsInfo() throws Exception {
        return (List<Commit>) sqlMapping.select(new Commit(), null, null, " order by commit_time desc;");
    }

    public int getCase_idByInst_id(String inst_id) throws Exception {
        if (isEmpty(inst_id)) return -1;
        List<Map.Entry<String, ?>> list = new ArrayList<>();
        list.add(new SimpleEntry<>("iss_inst", inst_id));
        List<Iss_match> iss_matches = (List<Iss_match>) sqlMapping.select(new Iss_match(), null, list, null);
        //return iss_matches == null ? -1 : iss_matches.get(0).getCase_id();
        return 0;
    }


    public List<Iss_match> getMatches(int case_id) throws Exception {
        List<Map.Entry<String, ?>> list = new ArrayList<>();
        list.add(new SimpleEntry<>("case_id", case_id));
        return (List<Iss_match>) sqlMapping.select(new Iss_match(), null, list, null);
    }


    public Iss_case getCaseByCase_id(int case_id) throws Exception {
        List<Map.Entry<String, ?>> list = new ArrayList<>();
        list.add(new SimpleEntry<>("case_id", case_id));
        List<Iss_case> iss_cases = (List<Iss_case>) sqlMapping.select(new Iss_case(), null, list, null);
        return  iss_cases == null ? null : iss_cases.get(0);
    }


    public List<List<AnalysisEntity>> getAnalysisEntities(String commit_hash, String type_id, String defect_type, String begin_time, String end_time, String min_duration) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String tmp = "(select * from " + FULL_VIEW+" " + "where ";
        String sql_commit = isEmpty(commit_hash) ? "" : ("commit_id = '" + commit_hash + "' && ");
        String sql_begin_time = isEmpty(begin_time) ? "" : ("create_time >= '" + begin_time + "' && ");
        String sql_end_time = isEmpty(end_time) ? "" : ("create_time <= '" + end_time + "' && ");
        String sql_type = isEmpty(type_id) ? "" : ("type_id = '" + type_id + "' && ");
        String sql_defect = isEmpty(defect_type) ? "" : ("type = '" + defect_type + "' && ");
        String sql_min = isEmpty(min_duration) ? "" : ("duration >= '" + min_duration + "' && ");
        String sql_c = "status = 'IN'";
        tmp = tmp + sql_commit + sql_begin_time + sql_end_time + sql_type + sql_defect + sql_min + sql_c;
        tmp = tmp + ") t ";
        String total_sql = "select total, done, concat(round((done/total)*100,2),'%') as percentage, type, average_exist_duration\n" +
                "from (select count(*) total ,COUNT(if(case_status = 'SOLVED',1,null)) done, null as type, AVG(duration) average_exist_duration from " + tmp + ") a \n" +
                "order by (done/total) asc, total desc;";
        String defect_sql = "select total, done, concat(round((done/total)*100,2),'%') as percentage, type, average_exist_duration\n" +
                "from \n" +
                "(select count(*) total, count(if(case_status = 'SOLVED',1,null)) done, type, AVG(duration) average_exist_duration from " + tmp + " group by type) a\n" +
                "order by percentage asc, total desc;";
        String type_sql = "select total, done, concat(round((done/total)*100,2),'%') as percentage, type, average_exist_duration\n" +
                "from \n" +
                "(select count(*) total, count(if(case_status = 'SOLVED',1,null)) done, type_id as type, AVG(duration) average_exist_duration from " + tmp + " group by type_id) a\n" +
                "order by percentage asc, total desc;";
        List<AnalysisEntity> totalEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), total_sql);
        List<AnalysisEntity> defectEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), defect_sql);
        List<AnalysisEntity> typeEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), type_sql);

        List<List<AnalysisEntity>> anaysis = new ArrayList<>();
        anaysis.add(totalEntity);
        anaysis.add(defectEntity);
        anaysis.add(typeEntity);
        return anaysis;
    }

    public static boolean isEmpty(String str){
        return str == null || str.equals("");
    }

    public String getField(Object j){
        Field[] fields = j.getClass().getDeclaredFields();
        StringBuffer s = new StringBuffer();
        for(Field f : fields){
            s.append(f.getName()).append(",");
        }
        return s.toString().substring(0, s.lastIndexOf(","));
    }

}
