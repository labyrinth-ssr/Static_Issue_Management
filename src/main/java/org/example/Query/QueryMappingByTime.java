package org.example.Query;

import org.apache.commons.lang.StringUtils;
import org.example.Query.Value.*;
import org.example.Utils.MockUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryMappingByTime {
    public SqlMapping sqlMapping;

    public QueryMappingByTime(SqlConnect connect) throws SQLException {
        sqlMapping = new SqlMapping(connect);
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

    String getUniversalSqlCondition(String begin_time, String end_time, String repo, String alis){
        String sql = "";
        if(begin_time!=null && !begin_time.trim().equals("")) sql+=alis+".commit_time >= '"+begin_time+"' and ";
        if(end_time!=null && !end_time.trim().equals("")) sql+=alis+".commit_time <= '" + end_time +"' and ";
        sql+= alis + ".repo_path = '"+repo+"' ";
        return sql;
    }

    public void getCountInByCommit_time(String begin_time, String end_time, String repo, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select count(*) intValue from iss_case ic join commit c on ic.commit_id_new = c.commit_id where " + sql_condition;
        if(mock) MockUtil.MockEnd(null);
        System.out.println("??????????????????: "+ ((List<IntValue>)sqlMapping.select(new IntValue(), sql)).get(0).getIntValue());
    }

    public void getGCountInTypeByCommit_time(String begin_time, String end_time, String repo, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select count(*) intValue, ic.type_id stringValue, duration(avg(TIMESTAMPDIFF(SECOND, c.commit_time, case ic.case_status when 'SOLVED' then c1.commit_time else localtime() end))) time from " +
                "iss_case ic join commit c on ic.commit_id_new = c.commit_id and " + sql_condition +
                "left join commit c1 on c1.commit_id = ic.commit_id_disappear " +
                "group by ic.type_id order by avg(TIMESTAMPDIFF(SECOND, c.commit_time, case ic.case_status when 'SOLVED' then c1.commit_time else localtime() end)) desc, ic.type_id";
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(), sql);
        System.out.println("????????????????????????: ");
        for(IntStringTime intStringTime : intStringTimes){
            String sql_median = "select duration(TIMESTAMPDIFF(SECOND, c.commit_time, case ic.case_status when 'SOLVED' then c1.commit_time else localtime() end)) time from " +
                    "iss_case ic join commit c on ic.commit_id_new = c.commit_id and " + sql_condition +
                    "and ic.type_id = '"+intStringTime.getStringValue()+"' " +
                    "left join commit c1 on ic.commit_id_disappear = c1.commit_id " +
                    "order by TIMESTAMPDIFF(SECOND, c.commit_time, case ic.case_status when 'SOLVED' then c1.commit_time else localtime() end) limit " + ((intStringTime.getIntValue()+1)/2 - 1) + ",1";
            String time = ((List<TimeValue>)sqlMapping.select(new TimeValue(), sql_median)).get(0).getTime();
            if(!mock) System.out.println("??????: "+ intStringTime.getStringValue() +
                    ", ??????: " + intStringTime.getIntValue() +
                    ", ??????????????????: " + intStringTime.getTime() +
                    ", ?????????????????????: " + time);
        }
        if(mock) MockUtil.MockEnd("?????????: "+intStringTimes.size());
    }

    public void getListInByCommit_time(String begin_time, String end_time, String repo,boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"commit");
        String sql = "select inst_id, type_id, description, file_path " +
                "from (select * from commit where "+sql_condition+") c " +
                "join iss_case ic on ic.commit_id_new = c.commit_id " +
                "join commit_inst using(commit_id) " +
                "join iss_instance using(inst_id, case_id) " +
                "join sonarrules using(type_id) " +
                "order by ic.type_id, file_path";
        System.out.println(sql);
        List<GetListInLatestInst> getListInLatestInsts = (List<GetListInLatestInst>) sqlMapping.select(new GetListInLatestInst(), sql);
        System.out.println("??????????????????: ");
        for(GetListInLatestInst getListInLatestInst : getListInLatestInsts) {
            String sql1 = "select start_line intValue1, start_col intValue2, class_ stringValue1, method stringValue2 " +
                    "from (select * from iss_instance where inst_id = '" + getListInLatestInst.getInst_id() +"') ii " +
                    "join iss_location  using(inst_id) " +
                    "order by start_line, start_col";
            List<Int2String2> int2String2s = (List<Int2String2>) sqlMapping.select(new Int2String2(), sql1);
            if(!mock) System.out.print("????????????: "+ getListInLatestInst.getType_id() +
                    "??????: " + getListInLatestInst.getDescription() +
                    "??????: " + getListInLatestInst.getFile_path());
            if(!mock) if(list_not_empty(int2String2s)){
                for(Int2String2 int2String2 : int2String2s){
                    System.out.print(", ( ???: " + int2String2.getStringValue1() +
                            ", ??????: " + int2String2.getStringValue2() +
                            ", ????????????: " + int2String2.getIntValue1()+","+int2String2.getIntValue2()+" )");
                }
            }
            if(!mock) System.out.print("\n");
        }
        if(mock) MockUtil.MockEnd("?????????: " + getListInLatestInsts.size());
    }

    public void getCountDoneByCommit_time(String begin_time, String end_time, String repo, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c");
        String sql = "select count(*) intValue from iss_case join commit c on iss_case.commit_id_disappear = c.commit_id where case_status = 'SOLVED' and " +sql_condition;
        if(mock) MockUtil.MockEnd(null);
        System.out.println("??????????????????: " + ((List<IntValue>)sqlMapping.select(new IntValue(),sql)).get(0).getIntValue());
    }

    public void getCountDoneInTypeByCommit_time(String begin_time, String end_time, String repo, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c1");
        String sql = "select count(*) intValue, ic.type_id stringValue, duration(avg(TIMESTAMPDIFF(SECOND, c.commit_time, c1.commit_time))) time from " +
                "iss_case ic join commit c1 on ic.commit_id_disappear = c1.commit_id and " + sql_condition +
                "and ic.case_status = 'SOLVED' " +
                "join commit c on ic.commit_id_new = c.commit_id " +
                "group by ic.type_id order by avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) desc, ic.type_id";
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(), sql);
        System.out.println("????????????????????????: ");
        for(IntStringTime intStringTime : intStringTimes){
            String sql_median = "select duration(TIMESTAMPDIFF(SECOND, c.commit_time, c1.commit_time)) time from " +
                    "iss_case ic join commit c1 on ic.commit_id_disappear = c1.commit_id and " + sql_condition +
                    "and ic.case_status = 'SOLVED' and ic.type_id = '"+intStringTime.getStringValue()+"' " +
                    "join commit c on ic.commit_id_new = c.commit_id " +
                    "order by TIMESTAMPDIFF(SECOND, c.commit_time, c1.commit_time) limit " + ((intStringTime.getIntValue()+1)/2 - 1) + ",1";
            String time = ((List<TimeValue>)sqlMapping.select(new TimeValue(), sql_median)).get(0).getTime();
            if(!mock) System.out.println("??????: "+ intStringTime.getStringValue() +
                    ", ??????: " + intStringTime.getIntValue() +
                    ", ??????????????????: " + intStringTime.getTime() +
                    ", ?????????????????????: " + time);
        }
        if(mock) MockUtil.MockEnd("?????????: "+intStringTimes.size());
    }

    public void getListDoneByCommit_time(String begin_time, String end_time, String repo,boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"commit");
        String sql = "select inst_id, type_id, description, file_path " +
                "from (select * from commit where "+sql_condition+") c " +
                "join iss_case ic on ic.commit_id_disappear = c.commit_id and ic.case_status = 'SOLVED' " +
                "join commit_inst ci on ci.commit_id = ic.commit_id_last " +
                "join iss_instance using(inst_id, case_id) " +
                "join sonarrules using(type_id) " +
                "order by ic.type_id, file_path";
        System.out.println(sql);
        List<GetListInLatestInst> getListInLatestInsts = (List<GetListInLatestInst>) sqlMapping.select(new GetListInLatestInst(), sql);
        System.out.println("??????????????????: ");
        for(GetListInLatestInst getListInLatestInst : getListInLatestInsts) {
            String sql1 = "select start_line intValue1, start_col intValue2, class_ stringValue1, method stringValue2 " +
                    "from (select * from iss_instance where inst_id = '" + getListInLatestInst.getInst_id() +"') ii " +
                    "join iss_location using(inst_id) " +
                    "order by start_line, start_col";
            List<Int2String2> int2String2s = (List<Int2String2>) sqlMapping.select(new Int2String2(), sql1);
            if(!mock) System.out.print("????????????: "+ getListInLatestInst.getType_id() +
                    ", ??????: " + getListInLatestInst.getDescription() +
                    ", ??????: " + getListInLatestInst.getFile_path());
            if(!mock) if(list_not_empty(int2String2s)){
                for(Int2String2 int2String2 : int2String2s){
                    System.out.print(", ( ???: " + int2String2.getStringValue1() +
                            ", ??????: " + int2String2.getStringValue2() +
                            ", ????????????: " + int2String2.getIntValue1()+","+int2String2.getIntValue2()+" ) ");
                }
            }
            if(!mock) System.out.print("\n");
        }
        if(mock) MockUtil.MockEnd("?????????: " + getListInLatestInsts.size());
    }


    public void getAnalysisCountByCommit_time(String begin_time, String end_time, String repo, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c1");
        String sql = "select count(*) intValue1, count(if(iss_case.case_status = 'SOLVED',1,null)) intValue2, " +
                "concat(round( ( count(if(iss_case.case_status = 'SOLVED',1,null))/count(*))*100, 2),'%') stringValue," +
                "duration(avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null))) time from " +
                "iss_case join commit c1 on c1.commit_id = iss_case.commit_id_new " +
                "left join commit c2 on c2.commit_id = iss_case.commit_id_disappear and "+ sql_condition +
                "order by avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null)) desc";
        List<Int2StringTime> int2StringTimes = (List<Int2StringTime>) sqlMapping.select(new Int2StringTime(), sql);
        for(Int2StringTime int2StringTime : int2StringTimes) {
            System.out.println("???????????????: " + int2StringTime.getIntValue1() +
                    ", ???????????????: " + int2StringTime.getIntValue2() +
                    ", ???????????????: " + int2StringTime.getStringValue() +
                    ", ????????????????????????: " + int2StringTime.getTime());
        }
        if(mock) MockUtil.MockEnd(null);
    }

    public void getAnalysisCountByTypeByCommit_time(String begin_time, String end_time, String repo,boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c1");
        String sql = "select count(*) intValue1, count(if(iss_case.case_status = 'SOLVED',1,null)) intValue2, " +
                "concat(round((count(if(iss_case.case_status = 'SOLVED',1,null))*100)/count(*) ,2),'%') stringValue1, " +
                "sr.type stringValue2, " +
                "duration(avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null))) time from " +
                "iss_case join commit c1 on c1.commit_id = iss_case.commit_id_new and " + sql_condition +
                "left join commit c2 on c2.commit_id = iss_case.commit_id_disappear " +
                "join sonarrules sr using(type_id) group by sr.type " +
                "order by avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null)) desc";
        System.out.println(sql);
        List<Int2String2Time> int2String2Times = (List<Int2String2Time>) sqlMapping.select(new Int2String2Time(), sql);
        System.out.println("?????????????????????");
        for(Int2String2Time int2String2Time : int2String2Times) {
            System.out.println("????????????: " + int2String2Time.getStringValue2() +
                    ", ???????????????: " + int2String2Time.getIntValue1() +
                    ", ???????????????: " + int2String2Time.getIntValue2() +
                    ", ???????????????: " + int2String2Time.getStringValue1() +
                    ", ????????????????????????: " + int2String2Time.getTime());
        }
        if(mock) MockUtil.MockEnd(null);
    }

    public void getAnalysisCountByType_idByCommit_time(String begin_time, String end_time, String repo, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql_condition = getUniversalSqlCondition(begin_time,end_time,repo,"c1");
        String sql = "select count(*) intValue1, count(if(iss_case.case_status = 'SOLVED',1,null)) intValue2, " +
                "concat(round( (count(if(iss_case.case_status ='SOLVED',1,null))/count(*))*100,2),'%') stringValue1, " +
                "iss_case.type_id stringValue2, " +
                "duration(avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null))) time from " +
                "iss_case join commit c1 on c1.commit_id = iss_case.commit_id_new and " + sql_condition +
                "left join commit c2 on c2.commit_id = iss_case.commit_id_disappear group by iss_case.type_id " +
                "order by avg(if(case_status='SOLVED',TIMESTAMPDIFF(SECOND, c1.commit_time, c2.commit_time),null)) desc";
        System.out.println(sql);
        List<Int2String2Time> int2String2Times = (List<Int2String2Time>) sqlMapping.select(new Int2String2Time(), sql);
        System.out.println("?????????????????????: ");
        for(Int2String2Time int2String2Time : int2String2Times) {
            System.out.println("????????????: " + int2String2Time.getStringValue2() +
                    ", ???????????????: " + int2String2Time.getIntValue1() +
                    ", ???????????????: " + int2String2Time.getIntValue2() +
                    ", ???????????????: " + int2String2Time.getStringValue1() +
                    ", ????????????????????????: " + int2String2Time.getTime());
        }
        if(mock) MockUtil.MockEnd(null);
    }

    public void getDefectListMoreThanDuration(String duration, String repo, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String duration_timestamp = getTimeStamp(duration);
        String sql = "select count(*) intValue, type_id stringValue, duration(avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime()))) time " +
                "from (select * from iss_case where iss_case.case_status <> 'SOLVED') ic join commit c on ic.commit_id_new = c.commit_id " +
                "where TIMESTAMPDIFF(SECOND, c.commit_time, localtime()) > '" + duration_timestamp + "' and c.repo_path = '" + repo + "' " +
                "group by ic.type_id order by avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) desc";
        System.out.println(sql);
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(),sql);
        System.out.println("???????????????????????????????????????????????????: ");
        for(IntStringTime intStringTime : intStringTimes){
            System.out.println("??????: " + intStringTime.getStringValue() +
                    ", ????????????: " + intStringTime.getIntValue() +
                    ", ??????????????????: " + intStringTime.getTime());
        }
        if(mock) MockUtil.MockEnd(null);
    }

    String getTimeStamp(String duration){
        duration = duration.trim();
        Long timestamp = 0L;
        String[] t = duration.split("(?<=???)|(?<=???)|(?<=???)|(?<=???)");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, t);
        System.out.println("list: " + list.toString());
        String tmp;
        for(String l : list){
            if(l.endsWith("???") && StringUtils.isNumeric(tmp = l.substring(0, l.indexOf("???")))) timestamp += Long.parseLong(tmp)*24*60*60;
            if(l.endsWith("???") && StringUtils.isNumeric(tmp = l.substring(0, l.indexOf("???")))) timestamp += Long.parseLong(tmp)*60*60;
            if(l.endsWith("???") && StringUtils.isNumeric(tmp = l.substring(0, l.indexOf("???")))) timestamp += Long.parseLong(tmp)*60;
            if(l.endsWith("???") && StringUtils.isNumeric(tmp = l.substring(0, l.indexOf("???")))) timestamp += Long.parseLong(tmp);
        }
        System.out.println(timestamp);
        return timestamp.toString();
    }



    boolean list_not_empty(List<?> list){
        return list!=null && list.size()!=0;
    }
}
