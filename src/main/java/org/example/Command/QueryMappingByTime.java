package org.example.Command;

import org.example.Command.QueryUseEntity.GetListInLatestInst;
import org.example.Command.Value.*;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryMappingByTime {
    public SqlMapping sqlMapping;

    public QueryMappingByTime(SqlConnect connect) throws SQLException {
        sqlMapping = new SqlMapping(connect);
    }

    String getUniversalSqlCondition(String begin_time, String end_time, String repo, String alis){
        String sql = "";
        if(begin_time!=null) sql+=alis+".commit_time >= '"+begin_time+"' and ";
        if(end_time!=null) sql+=alis+".commit_time <= '" + end_time +"' and ";
        sql+= alis + ".repo_path = '"+repo+"' ";
        return sql;
    }

    public void getGCountUnsolvedByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select count(*) intValue, ii.type_id stringValue, avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) time from " +
                "iss_instance ii join iss_case ic on ii.case_id = ic.case_id and ic.case_status <> 'SOLVED' " +
                "join commit c on ii.commit_id = c.commit_id and " + sql_condition +
                "group by ii.type_id order by avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) desc, ii.type_id";
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(), sql);
        System.out.println("当前版本现存缺陷类型统计: ");
        for(IntStringTime intStringTime : intStringTimes){
            String sql_median = "select TIMESTAMPDIFF(SECOND, c.commit_time, localtime()) time from " +
                    "iss_instance ii join iss_case ic on ii.case_id = ic.case_id " +
                    "and ic.case_status <> 'RESOLVED' and ii.type_id = '"+intStringTime.getStringValue()+"' " +
                    "join commit c on ii.commit_id = c.commit_id and " + sql_condition +
                    "order by TIMESTAMPDIFF(SECOND, c.commit_time, localtime()) limit " + ((intStringTime.getIntValue()+1)/2 - 1) + ",1";
            String time = ((List<TimeValue>)sqlMapping.select(new TimeValue(), sql_median)).get(0).getTime();
            System.out.println("类型: "+ intStringTime.getStringValue() +
                    ",数量: " + intStringTime.getIntValue() +
                    ",平均存续时长: " + intStringTime.getTime() +
                    ",存续时长中位数: " + time);
        }
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

    public void getCountInByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select count(*) intValue from iss_case ic join commit c on ic.commit_id_new = c.commit_id where " + sql_condition;
        System.out.println("当前版本缺陷引入数: "+ ((List<IntValue>)sqlMapping.select(new IntValue(), sql)).get(0).getIntValue());
    }

    public void getGCountInTypeByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select count(*) intValue, type_id stringValue from iss_case join commit c on iss_case.commit_id_new = c.commit_id where " + sql_condition + " group by type_id";
        List<IntStringValue> intStringValues = (List<IntStringValue>) sqlMapping.select(new IntStringValue(), sql);
        System.out.println("分类引入数: ");
        for(IntStringValue intStringValue : intStringValues){
            System.out.println("缺陷类型: "+ intStringValue.getStringValue() +", 数量: "+intStringValue.getIntValue());
        }
    }

    public void getListInByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select ii.inst_id, ic.type_id, sr.description, file_path " +
                "from iss_case ic join iss_instance ii on ic.commit_id_new = ii.commit_id " +
                "join sonarrules sr on ic.type_id = sr.id " +
                "join commit c on c.commit_id = ic.commit_id_new " +
                "where "+ sql_condition +" order by ic.type_id, file_path";
        List<GetListInLatestInst> getListInLatestInsts = (List<GetListInLatestInst>) sqlMapping.select(new GetListInLatestInst(), sql);
        System.out.println("引入缺陷详情: ");
        for(GetListInLatestInst getListInLatestInst : getListInLatestInsts) {
            String sql1 = "select start_line intValue1, start_col intValue2, class_ stringValue1, method stringValue2 " +
                    "from iss_instance ii left join instance_location ilo on ii.inst_id = ilo.inst_id and ii.inst_id = '" + getListInLatestInst.getInst_id() +"' " +
                    "join iss_location il on ilo.location_id = il.location_id order by start_line, start_col";
            List<Int2String2> int2String2s = (List<Int2String2>) sqlMapping.select(new Int2String2(), sql1);
            System.out.print("缺陷类型: "+ getListInLatestInst.getType_id() +
                    "描述: " + getListInLatestInst.getDescription() +
                    "文件: " + getListInLatestInst.getFile_path());
            if(list_not_empty(int2String2s)){
                for(Int2String2 int2String2 : int2String2s){
                    System.out.print("( 类: " + int2String2.getStringValue1() +
                            "方法: " + int2String2.getStringValue2() +
                            "起始行列: " + int2String2.getIntValue1()+","+int2String2.getIntValue2()+" ) ");
                }
            }
            System.out.print("\n");
        }
    }

    public void getCountDoneByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select count(*) intValue from iss_case join commit c on iss_case.commit_id_disappear = c.commit_id where case_status in ('SOLVED','REOPEN') and " +sql_condition;
        System.out.println("当前版本解决缺陷数量: " + ((List<StringValue>)sqlMapping.select(new StringValue(),sql)).get(0).getStringValue());
    }

    public void getCountDoneInTypeByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select count(*) intValue, type_id stringValue from iss_case join commit c on iss_case.commit_id_disappear = c.commit_id where case_status in ('SOLVED','REOPEN') and "+sql_condition+" group by type_id";
        List<IntStringValue> list = (List<IntStringValue>) sqlMapping.select(new IntStringValue(),sql);
        System.out.println("当前版本解决缺陷分类统计: ");
        for(IntStringValue intStringValue : list){
            System.out.println("类型: "+ intStringValue.getIntValue()+", 数量: "+intStringValue.getStringValue());
        }
    }

    public void getListDoneByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select ii.inst_id, ic.type_id, sr.description, file_path " +
                "from iss_case ic join iss_instance ii on ic.commit_id_last = ii.commit_id " +
                "join sonarrules sr on ic.type_id = sr.id " +
                "join commit c on c.commit_id = ic.commit_id_disappear " +
                "where "+sql_condition+" order by ic.type_id, file_path";
        List<GetListInLatestInst> getListInLatestInsts = (List<GetListInLatestInst>) sqlMapping.select(new GetListInLatestInst(), sql);
        System.out.println("解决缺陷详情: ");
        for(GetListInLatestInst getListInLatestInst : getListInLatestInsts) {
            String sql1 = "select start_line intValue1, start_col intValue2, class_ stringValue1, method stringValue2 " +
                    "from iss_instance ii left join instance_location ilo on ii.inst_id = ilo.inst_id and ii.inst_id = '" + getListInLatestInst.getInst_id() +"' " +
                    "join iss_location il on ilo.location_id = il.location_id order by start_line, start_col";
            List<Int2String2> int2String2s = (List<Int2String2>) sqlMapping.select(new IntStringValue(), sql1);
            System.out.print("缺陷类型: "+ getListInLatestInst.getType_id() +
                    "描述: " + getListInLatestInst.getDescription() +
                    "文件: " + getListInLatestInst.getFile_path());
            if(list_not_empty(int2String2s)){
                for(Int2String2 int2String2 : int2String2s){
                    System.out.print("( 类: " + int2String2.getStringValue1() +
                            "方法: " + int2String2.getStringValue2() +
                            "起始行列: " + int2String2.getIntValue1()+","+int2String2.getIntValue2()+" ) ");
                }
            }
            System.out.print("\n");
        }
    }

    public void getAnalysisCountByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c1");
        String sql = "select count(*) intValue1, count(if(iss_case.case_status = 'SOLVED',1,null)) intValue2, " +
                "concat(round( ( count(if(iss_case.case_status = 'SOLVED',1,null))/count(*))*100, 2),'%') stringValue," +
                "avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null)) time from " +
                "iss_case join commit c1 on c1.commit_id = iss_case.commit_id_new " +
                "left join commit c2 on c2.commit_id = iss_case.commit_id_disappear and "+ sql_condition;
        List<Int2StringTime> int2StringTimes = (List<Int2StringTime>) sqlMapping.select(new Int2StringTime(), sql);
        for(Int2StringTime int2StringTime : int2StringTimes) {
            System.out.println("缺陷引入数: " + int2StringTime.getIntValue1() +
                    ", 缺陷解决数: " + int2StringTime.getIntValue2() +
                    ", 缺陷解决率: " + int2StringTime.getStringValue() +
                    ", 平均解决所用时间: " + int2StringTime.getTime());
        }
    }

    public void getAnalysisCountByTypeByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c1");
        String sql = "select count(*) intValue1, count(if(iss_case.case_status = 'SOLVED',1,null)) intValue2, " +
                "concat(round((count(if(iss_case.case_status = 'SOLVED',1,null))*100)/count(*) ,2),'%') stringValue1, " +
                "sr.type stringValue2, " +
                "avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null)) time from " +
                "iss_case join commit c1 on c1.commit_id = iss_case.commit_id_new " +
                "left join commit c2 on c2.commit_id = iss_case.commit_id_disappear " +
                "join sonarrules sr on iss_case.type_id = sr.id and "+ sql_condition + " group by sr.type";
        List<Int2String2Time> int2String2Times = (List<Int2String2Time>) sqlMapping.select(new Int2String2Time(), sql);
        for(Int2String2Time int2String2Time : int2String2Times) {
            System.out.println("缺陷大类: " + int2String2Time.getStringValue2() +
                    ", 缺陷引入数: " + int2String2Time.getIntValue1() +
                    ", 缺陷解决数: " + int2String2Time.getIntValue2() +
                    ", 缺陷解决率: " + int2String2Time.getStringValue1() +
                    ", 平均解决所用时间: " + int2String2Time.getTime());
        }
    }


    public void getAnalysisCountByType_idByCommit_time(String begin_time, String end_time, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c1");
        String sql = "select count(*) intValue1, count(if(iss_case.case_status = 'SOLVED',1,null)) intValue2, " +
                "concat(round( (count(if(iss_case.case_status ='SOLVED',1,null))/count(*))*100,2),'%') stringValue1, " +
                "iss_case.type_id stringValue2, " +
                "avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null)) time from " +
                "iss_case join commit c1 on c1.commit_id = iss_case.commit_id_new " +
                "left join commit c2 on c2.commit_id = iss_case.commit_id_disappear and " +
                sql_condition + " group by iss_case.type_id";
        List<Int2String2Time> int2String2Times = (List<Int2String2Time>) sqlMapping.select(new Int2String2Time(), sql);
        for(Int2String2Time int2String2Time : int2String2Times) {
            System.out.println("缺陷类型: " + int2String2Time.getStringValue2() +
                    ", 缺陷引入数: " + int2String2Time.getIntValue1() +
                    ", 缺陷解决数: " + int2String2Time.getIntValue2() +
                    ", 缺陷解决率: " + int2String2Time.getStringValue1() +
                    ", 平均解决所用时间: " + int2String2Time.getTime());
        }
    }

    public void getDefectListMoreThanDuration(String duration, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String duration_timestamp = getTimeStamp(duration);
        String sql = "select count(*) intValue, type_id stringValue, avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) time " +
                "from (select * from iss_case where iss_case.case_status <> 'SOLVED') ic join commit c on ic.commit_id_new = c.commit_id " +
                "where TIMESTAMPDIFF(SECOND, c.commit_time, localtime()) > '" + duration_timestamp + "' " +
                "group by ic.type_id";
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(),sql);
        System.out.println("超过指定时长的静态缺陷分类情况统计: ");
        for(IntStringTime intStringTime : intStringTimes){
            System.out.println("类型: " + intStringTime.getStringValue() +
                    "现存数量: " + intStringTime.getIntValue() +
                    "平均存续时间: " + intStringTime.getTime());
        }
    }

    String getTimeStamp(String duration){
        duration = duration.trim();
        Long timestamp = 0L;
        String[] t = duration.split("天");
        if(t.length == 2){
            timestamp = Long.valueOf(t[0])*24;
            duration = t[1];
        }else if(t.length == 1) duration = t[0];
        else return "0";
        String[] h = duration.split("时");
        if(h.length == 2){
            timestamp = (timestamp + Long.valueOf(h[0]))*12;
            duration = h[1];
        }else if(h.length == 1) duration = h[0];
        else return "0";
        String[] m = duration.split("分");
        if(m.length == 2){
            timestamp = (timestamp + Long.valueOf(m[0]))*60;
            duration = m[1];
        }else if(m.length == 1) duration = m[0];
        else return "0";
        String[] s = duration.split("秒");
        if(s.length == 2){
            timestamp = timestamp + Long.valueOf(s[0]);
            duration = s[1];
        }
        return timestamp.toString();
    }



    boolean list_not_empty(List<?> list){
        return list!=null && list.size()!=0;
    }
}
