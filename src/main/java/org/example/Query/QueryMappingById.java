package org.example.Query;

import org.example.Entity.Commit;
import org.example.Mock;
import org.example.Query.Value.*;
import org.example.Utils.MockUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryMappingById {
    public SqlMapping sqlMapping;

    List<String> sql_add = new ArrayList<>();
    List<String> sql_drop = new ArrayList<>();
    public QueryMappingById(SqlConnect connect) throws SQLException {
        sqlMapping = new SqlMapping(connect);
        sql_add.add("create index commit_time on commit (commit_time);");
        sql_add.add("create index committer on commit (committer);");
        sql_add.add("create index repo_path on commit (repo_path);");
        sql_add.add("create index case_id on iss_instance (case_id);");
        sql_add.add("create index case_status on iss_case (case_status);");
        sql_drop.add("drop index commit_time on commit; ");
        sql_drop.add("drop index committer on commit;");
        sql_drop.add("drop index repo_path on commit;");
        sql_drop.add("drop index case_id on iss_instance; ");
        sql_drop.add("drop index case_status on iss_case; ");
    }
    public static boolean index = true;

    public void index() throws SQLException {

        if(index){
            index = false;
            sqlMapping.execute(sql_drop);
            System.out.println(sql_drop);
        }else{
            index = true;
            sqlMapping.execute(sql_add);
            System.out.println(sql_add);
        }
    }


    public void quit() throws SQLException {
        if(!index) sqlMapping.execute(sql_add);
    }

    public List<String2Value> getRepo() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select repo_path stringValue1, concat('入库版本量: ', commit_num) stringValue2 from repos";
        List<String2Value> string2Values = (List<String2Value>) sqlMapping.select(new String2Value(),sql);
        return string2Values;
    }

    public String getCommitLatest(String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select latest_commit_id stringValue from repos where repo_path = '"+repo+"'";
        return ((List<StringValue>)sqlMapping.select(new StringValue(),sql)).get(0).getStringValue();
    }
    public String getCommit_idByCommit_hashAndRepo(String commit_hash, String repo) throws Exception {
        String sql = "select commit_id as stringValue from commit where commit_hash = '"+commit_hash+"' and repo_path='"+repo+"'";
        List<StringValue> stringValues = (List<StringValue>)sqlMapping.select(new StringValue(),sql);
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

    public void getCommits(String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<Commit> commits = (List<Commit>) sqlMapping.select(new Commit(), "select * from commit where repo_path = '"+repo+"'order by commit_time asc");
        for(Commit commit : commits){
            System.out.println("hash: "+ commit.getCommit_hash()+", 发布者: "+commit.getCommitter()+", 发布时间: "+commit.getTime());
        }
    }

    public void getCountInByCommit_id(String commit_id, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql = "select count(*) intValue from iss_case where commit_id_new = '"+ commit_id +"'";
        if(mock) MockUtil.MockEnd(null);
        System.out.println("引入缺陷数量: "+ ((List<IntValue>)sqlMapping.select(new IntValue(), sql)).get(0).getIntValue());
    }

    public void getGCountInTypeByCommit_id(String commit_id, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql = "select count(*) intValue, ic.type_id stringValue, duration(avg(TIMESTAMPDIFF(SECOND, c.commit_time, case ic.case_status when 'SOLVED' then c1.commit_time else localtime() end))) time from " +
                "iss_case ic join commit c on ic.commit_id_new = c.commit_id and ic.commit_id_new = '" + commit_id +"' " +
                "left join commit c1 on c1.commit_id = ic.commit_id_disappear " +
                "group by ic.type_id order by avg(TIMESTAMPDIFF(SECOND, c.commit_time, case ic.case_status when 'SOLVED' then c1.commit_time else localtime() end)) desc, ic.type_id";
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(), sql);
        System.out.println("引入缺陷分类统计: ");
        for(IntStringTime intStringTime : intStringTimes){
            String sql_median = "select duration(TIMESTAMPDIFF(SECOND, c.commit_time, case ic.case_status when 'SOLVED' then c1.commit_time else localtime() end)) time from " +
                    "iss_case ic join commit c on ic.commit_id_new = c.commit_id and ic.commit_id_new = '" + commit_id +"' " +
                    "and ic.type_id = '"+intStringTime.getStringValue()+"' " +
                    "left join commit c1 on ic.commit_id_disappear = c1.commit_id " +
                    "order by TIMESTAMPDIFF(SECOND, c.commit_time, case ic.case_status when 'SOLVED' then c1.commit_time else localtime() end) limit " + ((intStringTime.getIntValue()+1)/2 - 1) + ",1";
            String time = ((List<TimeValue>)sqlMapping.select(new TimeValue(), sql_median)).get(0).getTime();
            if(!mock) System.out.println("类型: "+ intStringTime.getStringValue() +
                    ", 数量: " + intStringTime.getIntValue() +
                    ", 平均存续时长: " + intStringTime.getTime() +
                    ", 存续时长中位数: " + time);
        }
        if(mock) MockUtil.MockEnd("数据量: "+ intStringTimes.size());
    }

    public void getListInByCommit_id(String commit_id, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
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
            if(!mock) System.out.print("缺陷类型: "+ getListInLatestInst.getType_id() +
                    ", 描述: " + getListInLatestInst.getDescription() +
                    ", 文件: " + getListInLatestInst.getFile_path());
            if(!mock) if(list_not_empty(int2String2s)){
                for(Int2String2 int2String2 : int2String2s){
                    System.out.print(", ( 类: " + int2String2.getStringValue1() +
                            ", 方法: " + int2String2.getStringValue2() +
                            ", 起始行列: " + int2String2.getIntValue1()+","+int2String2.getIntValue2()+" )");
                }
            }
            if(!mock)System.out.print("\n");
        }
        if(mock) MockUtil.MockEnd("数据量: " + getListInLatestInsts.size());
    }

    public void getCountDoneByCommit_id(String commit_id, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql = "select count(*) intValue from iss_case where case_status in ('SOLVED','REOPEN') and commit_id_disappear = '" +commit_id+"'";
        IntValue stringValue = ((List<IntValue>)sqlMapping.select(new IntValue(),sql)).get(0);
        if(mock) MockUtil.MockEnd(null);
        System.out.println("解决缺陷数量: " + stringValue.getIntValue());
    }

    public void getCountDoneInTypeByCommit_id(String commit_id, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql = "select count(*) intValue, ic.type_id stringValue, duration(avg(TIMESTAMPDIFF(SECOND, c.commit_time, c1.commit_time))) time from " +
                "iss_case ic join commit c on ic.commit_id_new = c.commit_id and ic.commit_id_disappear = '" + commit_id +"' and ic.case_status = 'SOLVED' " +
                "left join commit c1 on ic.commit_id_disappear = c1.commit_id " +
                "group by ic.type_id order by avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) desc, ic.type_id";
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(), sql);
        System.out.println("解决缺陷分类统计: ");
        for(IntStringTime intStringTime : intStringTimes){
            String sql_median = "select duration(TIMESTAMPDIFF(SECOND, c.commit_time, c1.commit_time)) time from " +
                    "iss_case ic join commit c on ic.commit_id_new = c.commit_id and ic.commit_id_disappear = '" + commit_id +"' " +
                    "and ic.case_status = 'SOLVED' and ic.type_id = '"+intStringTime.getStringValue()+"' " +
                    "join commit c1 on ic.commit_id_disappear = c1.commit_id " +
                    "order by TIMESTAMPDIFF(SECOND, c.commit_time, c1.commit_time) limit " + ((intStringTime.getIntValue()+1)/2 - 1) + ",1";
            String time = ((List<TimeValue>)sqlMapping.select(new TimeValue(), sql_median)).get(0).getTime();
            if(!mock) System.out.println("类型: "+ intStringTime.getStringValue() +
                    ", 数量: " + intStringTime.getIntValue() +
                    ", 平均存续时长: " + intStringTime.getTime() +
                    ", 存续时长中位数: " + time);
        }
        if(mock){
            MockUtil.MockEnd("数据量: " + intStringTimes.size());
        }
    }

    public void getListDoneByCommit_id(String commit_id, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
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
            if(!mock) System.out.print("缺陷类型: "+ getListInLatestInst.getType_id() +
                    ", 描述: " + getListInLatestInst.getDescription() +
                    ", 文件: " + getListInLatestInst.getFile_path());
            if(!mock) if(list_not_empty(int2String2s)){
                for(Int2String2 int2String2 : int2String2s){
                    System.out.print(", ( 类: " + int2String2.getStringValue1() +
                            ", 方法: " + int2String2.getStringValue2() +
                            ", 起始行列: " + int2String2.getIntValue1()+","+int2String2.getIntValue2()+" )");
                }
            }
            if(!mock) System.out.print("\n");
        }
        if(mock) MockUtil.MockEnd("数据量: " + getListInLatestInsts.size());
    }

    public void getGCountUnsolvedByCommit_id(String commit_id, boolean mock) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(mock) MockUtil.MockBegin();
        String sql = "select count(*) intValue, ii.type_id stringValue, duration(avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime()))) time from " +
                "iss_instance ii join iss_case ic on ii.case_id = ic.case_id and ii.commit_id = '" + commit_id +"' and ic.case_status <> 'SOLVED' " +
                "join commit c on ii.commit_id = c.commit_id " +
                "group by ii.type_id order by avg(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) desc, ii.type_id";
        List<IntStringTime> intStringTimes = (List<IntStringTime>) sqlMapping.select(new IntStringTime(), sql);
        System.out.println("现存缺陷类型统计: ");
        for(IntStringTime intStringTime : intStringTimes){
            String sql_median = "select duration(TIMESTAMPDIFF(SECOND, c.commit_time, localtime())) time from " +
                    "iss_instance ii join iss_case ic on ii.case_id = ic.case_id and ii.commit_id = '" + commit_id +"' " +
                    "and ic.case_status <> 'RESOLVED' and ii.type_id = '"+intStringTime.getStringValue()+"' " +
                    "join commit c on ii.commit_id = c.commit_id " +
                    "order by TIMESTAMPDIFF(SECOND, c.commit_time, localtime()) limit " + ((intStringTime.getIntValue()+1)/2 - 1) + ",1";
            String time = ((List<TimeValue>)sqlMapping.select(new TimeValue(), sql_median)).get(0).getTime();
            if(!mock) System.out.println("类型: "+ intStringTime.getStringValue() +
                    ", 数量: " + intStringTime.getIntValue() +
                    ", 平均存续时长: " + intStringTime.getTime() +
                    ", 存续时长中位数: " + time);
        }
        if(mock){
            MockUtil.MockEnd(null);
            return;
        }
    }

    boolean list_not_empty(List<?> list){
        return list!=null && list.size()!=0;
    }

    public boolean repoIn(String repo_path) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select repo_path stringValue from commit group by repo_path having repo_path='"+repo_path+"'";
        List<String> repos = (List<String>) sqlMapping.select(new StringValue(),sql);
        return list_not_empty(repos);
    }
}
