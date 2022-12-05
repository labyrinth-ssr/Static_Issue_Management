package org.example.Utils;


import org.example.QueryUseEntity.DefectCommitEntity;
import org.example.QueryUseEntity.DefectEntity;
import org.example.QueryUseEntity.DefectTypeEntity;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

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

    public class commit{
        String commit_hash;
        public String getCommit_hash() {return commit_hash;}
        public void setCommit_hash(String commit_hash) {this.commit_hash = commit_hash;}
    }
    //获取最新版本commit_id
    public String getLatestCommitId() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        String sql = "selec commit_hash from commit where commit_hash not in (select parent_commit_hash from commit);";
        List<commit> c = (List<commit>) sqlMapping.select(new commit(),sql);
        return c.get(0).getCommit_hash();
    }

    public List<DefectCommitEntity> getDefetcCommit(String commit_id, String time_begin, String time_end) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_commit = isEmpty(commit_id) ? "" : ("commit_hash = " + commit_id + " && ");
        String sql_time_begin = isEmpty(time_begin) ? "" : ("commit_time >= " + time_begin + " && ");
        String sql_time_end = isEmpty(time_end) ? "" : "commit_time <= " + time_end;
        String sql;
        if((sql_commit + sql_time_begin + sql_time_end).equals("")) sql = "select * from commit";
        else sql = "select * from commit where " + sql_commit + sql_time_begin + sql_time_end;
        sql += " order by commit_time desc;";
        return (List<DefectCommitEntity>) sqlMapping.select(new DefectCommitEntity(), sql);
    }

    //! 此处有根据commit_id查找type_id需求
    public List<DefectTypeEntity> getDefectType(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select iss_instance.type_id, description, avg(timestampdiff(null, create_time_id, commit_time)) average_exist_duration" +
                "from iss_instance, iss_case " +
                "where commit_hash = " + commit_id +" and case_id = (select case_id from iss_match where inst_id = iss_instance.inst_id)"+
                "group by iss_instance.type_id " +
                "order by average_exist_duration desc";
        return (List<DefectTypeEntity>) sqlMapping.select(new DefectTypeEntity(), sql);
    }

    public List<DefectEntity> getDefectEntity(String commit_id, String type_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select iss_instance.* , case_id, timestampdiff(null, create_time_id, commit_time) exist_duration, iss_location.* " +
                "from iss_instance, iss_match, iss_location " +
                "where iss_instance.commit_hash = '"+commit_id +"' and iss_instance.type_id = '"+type_id+"' and iss_match.inst_id = iss_instance.inst_id and iss_location.inst_id = iss_instance.inst_id "+
                "order by exist_duration dsec";
        return (List<DefectEntity>) sqlMapping.select(new DefectEntity(), sql);
    }

    public static boolean isEmpty(String str){
        return str == null || str.equals("");
    }
}
