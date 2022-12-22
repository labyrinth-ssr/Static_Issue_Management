package org.example.Command;

import org.example.Command.QueryUseEntity.GetListInLatestInst;
import org.example.Command.Value.*;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryMappingById {
    public SqlMapping sqlMapping;

    public QueryMappingById(SqlConnect connect) throws SQLException {
        sqlMapping = new SqlMapping(connect);
    }

    public String getCommitLatest() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select commit_id_new stringValue from iss_case where case_status = 'NEW' limit 1";
        return ((List<StringValue>)sqlMapping.select(new StringValue(),sql)).get(0).getStringValue();
    }
    public void getGCountUnsolvedByCommit_id(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select count(*) intValue, ii.type_id stringValue, avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) time from " +
                "iss_instance ii join iss_case ic on ii.case_id = ic.case_id and ii.commit_id = '" + commit_id +"' and ic.case_status <> 'SOLVED' " +
                "join commit c on ii.commit_id = c.commit_id " +
                "group by ii.type_id order by avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) desc, ii.type_id";
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(), sql);
        System.out.println("现存缺陷类型统计: ");
        for(IntStringTime intStringTime : intStringTimes){
                String sql_median = "select TIMESTAMPDIFF(SECOND, c.commit_time, localtime()) time from " +
                        "iss_instance ii join iss_case ic on ii.case_id = ic.case_id and ii.commit_id = '" + commit_id +"' " +
                        "and ic.case_status <> 'RESOLVED' and ii.type_id = '"+intStringTime.getStringValue()+"' " +
                        "join commit c on ii.commit_id = c.commit_id " +
                        "order by TIMESTAMPDIFF(SECOND, c.commit_time, localtime()) limit " + ((intStringTime.getIntValue()+1)/2 - 1) + ",1";
                String time = ((List<TimeValue>)sqlMapping.select(new TimeValue(), sql_median)).get(0).getTime();
                System.out.println("类型: "+ intStringTime.getStringValue() +
                ", 数量: " + intStringTime.getIntValue() +
                ", 平均存续时长: " + intStringTime.getTime() +
                ", 存续时长中位数: " + time);
        }
    }

    public String getCommit_idByCommit_hashAndRepo(String commit_hash, String repo) throws Exception {
        String sql = "select commit_id as stringValue from commit where commit_hash = '"+commit_hash+"' and repo_path='"+repo+"'";
        List<StringValue> stringValues = (List<StringValue>)sqlMapping.select(new StringValue());
        if (list_not_empty(stringValues)) return stringValues.get(0).getStringValue();
        return "";
    }

    public List<String> getCommit_idListByTimeAndRepo(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select commit_id stringValue from commit where ";
        if(begin_time!=null) sql+="commit_time >= '" + begin_time+"' and ";
        if(end_time!=null) sql+="commit_time <= '" + end_time +"' and ";
        sql+="repo_path = '"+repo+"' order by commit_time";
        List<String> strings = new ArrayList<>();
        ((List<StringValue>)sqlMapping.select(new StringValue(), sql)).forEach(stringValue -> strings.add(stringValue.getStringValue()));
        return strings;
    }

    public void getCountInByCommit_id(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select count(*) intValue from iss_case where commit_id_new = '"+ commit_id +"'";
        System.out.println("引入缺陷数量: "+ ((List<IntValue>)sqlMapping.select(new IntValue(), sql)).get(0).getIntValue());
    }

    public void getGCountInTypeByCommit_id(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select count(*) intValue, type_id stringValue from iss_case where commit_id_new='"+commit_id+"' group by type_id order by count(*)";
        List<IntStringValue> intStringValues = (List<IntStringValue>) sqlMapping.select(new IntStringValue(), sql);
        System.out.println("引入缺陷分类统计: ");
        for(IntStringValue intStringValue : intStringValues){
            System.out.println("缺陷类型: "+ intStringValue.getStringValue() +", 数量: "+intStringValue.getIntValue());
        }
    }

    public void getListInByCommit_id(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select ii.inst_id, ic.type_id, sr.description, file_path " +
                "from iss_case ic join iss_instance ii on ic.commit_id_new = ii.commit_id and ic.case_id = ii.case_id " +
                "join sonarrules sr on ic.type_id = sr.id " +
                "where ic.commit_id_new = '"+commit_id+"' order by ic.type_id, file_path";
        List<GetListInLatestInst> getListInLatestInsts = (List<GetListInLatestInst>) sqlMapping.select(new GetListInLatestInst(), sql);
        System.out.println("引入缺陷详情: ");
        for(GetListInLatestInst getListInLatestInst : getListInLatestInsts) {
            String sql1 = "select start_line intValue1, start_col intValue2, class_ stringValue1, method stringValue2 " +
                    "from iss_instance ii left join instance_location ilo on ii.inst_id = ilo.inst_id and ii.inst_id = '" + getListInLatestInst.getInst_id() +"' " +
                    "join iss_location il on ilo.location_id = il.location_id order by start_line, start_col";
            List<Int2String2> int2String2s = (List<Int2String2>) sqlMapping.select(new Int2String2(), sql1);
            System.out.print("缺陷类型: "+ getListInLatestInst.getType_id() +
                    ", 描述: " + getListInLatestInst.getDescription() +
                    ", 文件: " + getListInLatestInst.getFile_path());
            if(list_not_empty(int2String2s)){
                for(Int2String2 int2String2 : int2String2s){
                    System.out.print(", ( 类: " + int2String2.getStringValue1() +
                            ", 方法: " + int2String2.getStringValue2() +
                            ", 起始行列: " + int2String2.getIntValue1()+","+int2String2.getIntValue2()+" )");
                }
            }
            System.out.print("\n");
        }
    }

    public void getCountDoneByCommit_id(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select count(*) intValue from iss_case where case_status in ('SOLVED','REOPEN') and commit_id_disappear = '" +commit_id+"'";
        IntValue stringValue = ((List<IntValue>)sqlMapping.select(new IntValue(),sql)).get(0);
        System.out.println("解决缺陷数量: " + stringValue.getIntValue());
    }

    public void getCountDoneInTypeByCommit_id(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select count(*) intValue, type_id stringValue from iss_case where case_status in ('SOLVED','REOPEN') and commit_id_disappear = '"+commit_id+"' group by type_id order by count(*)";
        List<IntStringValue> list = (List<IntStringValue>) sqlMapping.select(new IntStringValue(),sql);
        System.out.println("解决缺陷分类统计: ");
        for(IntStringValue intStringValue : list){
            System.out.println("类型: "+ intStringValue.getStringValue()+", 数量: "+intStringValue.getIntValue());
        }
    }

    public void getListDoneByCommit_id(String commit_id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select ii.inst_id, ic.type_id, sr.description, file_path " +
                "from iss_case ic join iss_instance ii on ic.commit_id_last = ii.commit_id and ic.case_id = ii.case_id " +
                "join sonarrules sr on ic.type_id = sr.id " +
                "where ic.commit_id_disappear = '"+commit_id+"' order by ic.type_id, file_path";
        List<GetListInLatestInst> getListInLatestInsts = (List<GetListInLatestInst>) sqlMapping.select(new GetListInLatestInst(), sql);
        System.out.println("解决缺陷详情: ");
        for(GetListInLatestInst getListInLatestInst : getListInLatestInsts) {
            String sql1 = "select start_line intValue1, start_col intValue2, class_ stringValue1, method stringValue2 " +
                    "from iss_instance ii left join instance_location ilo on ii.inst_id = ilo.inst_id and ii.inst_id = '" + getListInLatestInst.getInst_id() +"' " +
                    "join iss_location il on ilo.location_id = il.location_id order by start_line, start_col";
            List<Int2String2> int2String2s = (List<Int2String2>) sqlMapping.select(new Int2String2(), sql1);
            System.out.print("缺陷类型: "+ getListInLatestInst.getType_id() +
                    ", 描述: " + getListInLatestInst.getDescription() +
                    ", 文件: " + getListInLatestInst.getFile_path());
            if(list_not_empty(int2String2s)){
                for(Int2String2 int2String2 : int2String2s){
                    System.out.print(", ( 类: " + int2String2.getStringValue1() +
                            ", 方法: " + int2String2.getStringValue2() +
                            ", 起始行列: " + int2String2.getIntValue1()+","+int2String2.getIntValue2()+" )");
                }
            }
            System.out.print("\n");
        }
    }

    boolean list_not_empty(List<?> list){
        return list!=null && list.size()!=0;
    }
}
