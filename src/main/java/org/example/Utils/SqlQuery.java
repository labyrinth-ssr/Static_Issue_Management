package org.example.Utils;


import org.example.Entity.Commit;
import org.example.Entity.Iss_case;
import org.example.Entity.Iss_match;
import org.example.Entity.Repository;
import org.example.QueryUseEntity.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.AbstractMap;
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
    public SqlQuery(SqlConnect connect) {
        sqlMapping = new SqlMapping(connect);
    }


    //获取最新版本commit_id
    public String getLatestCommitId() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        String sql = "select commit_hash from commit where commit_hash not in (select parent_commit_hash from commit);";
        List<commit> c = (List<commit>) sqlMapping.select(new commit(),sql);
        return c.get(0).getCommit_hash();
    }

    public List<DefectCommitEntity> getDefetcCommit(String commit_id, String time_begin, String time_end) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_commit = isEmpty(commit_id) ? "" : ("commit_hash = '" + commit_id + "' && ");
        String sql_time_begin = isEmpty(time_begin) ? "" : ("commit_time >= '" + time_begin + "' && ");
        String sql_time_end = isEmpty(time_end) ? "" : "commit_time <= '" + time_end +"' && ";
        String sql;
        if((sql_commit + sql_time_begin + sql_time_end).equals("")) sql = "select "+getField(new DefectCommitEntity()) +" from commit";
        else {
            sql = "select "+getField(new DefectCommitEntity()) + " from commit where " + sql_commit + sql_time_begin + sql_time_end;
            sql = sql.substring(0, sql.lastIndexOf("&&"));
        }
        sql += " order by commit_time desc;";
//        System.out.println("getDefetcCommit_sql: "+sql);
        return (List<DefectCommitEntity>) sqlMapping.select(new DefectCommitEntity(), sql);
    }

    //! 此处有根据commit_id查找type_id需求
    public List<DefectTypeEntity> getDefectType(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select iss_instance.type_id, description, avg(TIMESTAMPDIFF(SECOND,iss_case.create_time, commit_time)) average_exist_duration " +
                "from iss_instance, iss_case " +
                "where commit_hash = '" + commit_id +"' and case_id in (select case_id from iss_match where inst_id = iss_instance.inst_id) "+
                "group by iss_instance.type_id " +
                "order by average_exist_duration desc";
//        System.out.println("getDefectType_sql: "+sql);
        return (List<DefectTypeEntity>) sqlMapping.select(new DefectTypeEntity(), sql);
    }

    public List<DefectEntity> getDefectEntity(String commit_id, String type_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select timestampdiff(SECOND, iss_case.create_time, commit_time) as exist_duration, iss_match.case_id, iss_instance.inst_id, iss_location.file_path, iss_location.start_line, iss_location.end_line, iss_location.start_col, iss_location.end_col, iss_location.class_,iss_location.method,iss_location.code " +
                "from ((iss_instance left outer join iss_location on iss_instance.inst_id = iss_location.inst_id) LEFT OUTER JOIN iss_match on iss_instance.inst_id = iss_match.inst_id) LEFT OUTER JOIN iss_case on iss_match.case_id = iss_case.case_id " +
                "where iss_instance.commit_hash = '"+commit_id +"' and iss_instance.type_id = '"+type_id+"' " +
                "order by exist_duration desc;";
//        System.out.println("getDefectEntity_sql: "+sql);
        return (List<DefectEntity>) sqlMapping.select(new DefectEntity(), sql);
    }

    public List<Repository> getRepositories() throws Exception {
        return (List<Repository>) sqlMapping.select(new Repository());
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
        return iss_matches == null ? -1 : iss_matches.get(0).getCase_id();
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


    public List<AnalysisEntity> getAnalysisEntities(String commit_hash, String type_id, String begin_time, String end_time, String min_duration) {
        String sql =
                "select count(*) as total, (select count(*) from t where case_status = 'DONE') as done, done/total) " +
                "from (select iss_case.case_id, type_id, case_status, timestampdiff(SECOND, now(),create_time) as duration " +
                        "from iss_case join commit on iss_case.commit_hash_new = commit.cimmit_hash " +
                        "where commit.commit_hash = 'commit_hash' and 'begin_time' <= iss_case.create_time and 'end_time' >= iss_case.create_time and type_id = 'type_id' " +
                        "and duration > 'min_duration') t";

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
