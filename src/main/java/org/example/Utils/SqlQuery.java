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
        String sql = "select ii.type_id, sr.description, avg(TIMESTAMPDIFF(SECOND,iss_case.create_time, commit_time)) average_exist_duration " +
                "from (iss_instance ii join sonarrules sr on ii.type_id = sr.id) join commit c on ii.commit_hash = `c`.commit_hash, iss_case " +
                "where ii.commit_hash = '" + commit_id +"' and case_id in (select case_id from iss_match where inst_id = ii.inst_id) "+
                "group by ii.type_id " +
                "order by average_exist_duration desc";
//        System.out.println("getDefectType_sql: "+sql);
        return (List<DefectTypeEntity>) sqlMapping.select(new DefectTypeEntity(), sql);
    }

    public List<DefectEntity> getDefectEntity(String commit_id, String type_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select timestampdiff(SECOND, ic.create_time, c.commit_time) as exist_duration, im.case_id, ii.inst_id, il.file_path, il.start_line, il.end_line, il.start_col, il.end_col, il.class_,il.method,il.code " +
                "from iss_instance ii left outer join instance_location ilo on ii.inst_id = ilo.inst_id " +
                "join iss_location il on ilo.location_id = il.location_id " +
                "LEFT OUTER JOIN iss_match im on ii.inst_id = im.inst_id " +
                "LEFT OUTER JOIN iss_case ic on im.case_id = ic.case_id " +
                "LEFT JOIN commit c on c.commit_hash = ii.commit_hash " +
                "where ii.commit_hash = '"+commit_id +"' and ii.type_id = '"+type_id+"' " +
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
        String drop_sql = "drop view if exists tt;";
        sqlMapping.execute(drop_sql);
        String view_sql =
                "create view tt as " +
                "(select ic.case_id, ic.type_id, sr.type, case_status, timestampdiff(SECOND, ic.create_time, now()) as duration " +
                "from (iss_case ic join commit c on ic.commit_hash_new = c.commit_hash)" +
                "join sonarrules sr on ic.type_id = sr.id ";
        String sql_commit = isEmpty(commit_hash) ? "" : ("c.commit_hash = '" + commit_hash + "' && ");
        String sql_begin_time = isEmpty(begin_time) ? "" : ("c.create_time >= '" + begin_time + "' && ");
        String sql_end_time = isEmpty(end_time) ? "" : ("c.create_time <= '" + end_time + "' && ");
        String sql_type = isEmpty(type_id) ? "" : ("ic.type_id = '" + type_id + "' && ");
        String sql_defect = isEmpty(defect_type) ? "" : ("sr.type = '" + defect_type + "' && ");
        String sql_min = isEmpty(min_duration) ? "" : ("ic.create_time, now()) >= '" + min_duration + "' && ");
        if((sql_commit+sql_begin_time+sql_end_time+sql_type+sql_defect+sql_min).equals("")) view_sql = view_sql + ")";
        else{
            view_sql = view_sql + "where " + sql_commit + sql_begin_time + sql_end_time + sql_type + sql_defect + sql_min;
            view_sql = view_sql.substring(0, view_sql.lastIndexOf("&&"));
            view_sql = view_sql + ")";
        }
        sqlMapping.execute(view_sql);
        String total_sql = "select count(*) as total, (select COUNT(*) from tt where case_status = 'CLOSE') as done,(select COUNT(*) from tt where case_status = 'CLOSE')/count(*) as percentage, null as type from tt;";
        List<AnalysisEntity> totalEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), total_sql);
        String defect_sql = "select count(*) as total, (select COUNT(*) from tt where case_status = 'CLOSE') as done,(select COUNT(*) from tt where case_status = 'CLOSE')/count(*) as percentage, type from tt group by type order by percentage asc, total desc;";
        List<AnalysisEntity> defectEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), defect_sql);
        String type_sql = "select count(*) as total, (select COUNT(*) from tt where case_status = 'CLOSE') as done,(select COUNT(*) from tt where case_status = 'CLOSE')/count(*) as percentage, type_id as type from tt group by type_id order by percentage asc, total desc;";
        List<AnalysisEntity> typeEntity = (List<AnalysisEntity>) sqlMapping.select(new AnalysisEntity(), type_sql);
        String view_destroy = "drop view tt";
        sqlMapping.execute(view_destroy);

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
