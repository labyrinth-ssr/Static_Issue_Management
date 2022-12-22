package org.example.Command;

import org.example.Command.Value.Int4Time4String1Value;
import org.example.Command.Value.Int4Value;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class QueryMappingByDev {
    public SqlMapping sqlMapping;

    public QueryMappingByDev(SqlConnect connect) throws SQLException {
        sqlMapping = new SqlMapping(connect);
    }

    public void getDevCountByDevs(String name, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String sql = "select (select count(if(c1.committer = '"+name+"',1,null))) intValue1, " +
                "(select count(if(c2.committer = '" + name +"' and c1.committer <> '"+name+"',1,null))) intValue2, " +
                "(select count(if(c1.committer = '" + name +"' and ic.case_status <> 'SOLVED',1,null))) intValue3, " +
                "(select count(if(c1.committer = '" + name +"' and c2.committer <> '"+name+"',1,null))) intValue4 " +
                "from iss_case ic join commit c1 on ic.commit_id_new = c1.commit_id " +
                "left join commit c2 on ic.commit_id_last = c2.commit_id " +
                "where c1.repo_path = '"+repo+"'";
        Int4Value int4Value = ((List<Int4Value>) sqlMapping.select(new Int4Value(), sql)).get(0);

        System.out.println("开发人员: " + name +
                ", 引入缺陷数量: " + int4Value.getIntValue1() +
                ", 解决他人缺陷数量: " + int4Value.getIntValue2() +
                ", 引入但尚未解决缺陷数量: " + int4Value.getIntValue3() +
                ", 引入被他人解决缺陷数量: " + int4Value.getIntValue4());
    }

    public void getDevTypeCountByDevs(String name, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        //如果要计算准确的存活周期，需要再维护一张表，记录reopen的new,last,disappear的历史，或者说使用case_id和case_id_parent表
        //这里先不考虑reopen情况。
        String sql = "select (select count(if( c1.committer = '"+name+"',1,null))) intValue1, " +
                "(select count(if( c2.committer = '" + name +"' and c1.committer <> '"+name+"',1,null))) intValue2, " +
                "(select count(if( c1.committer = '" + name +"' and ic.case_status <> 'SOLVED',1,null))) intValue3, " +
                "(select count(if( c1.committer = '" + name +"' and c2.committer <> '"+name+"',1,null))) intValue4, " +

                "(select avg(if(c1.committer = '"+name+"',timestampdiff(SECOND,c1.commit_time, case ic.case_status when 'SOLVED' then c2.commit_time else localtime() end),null))) time1, " +
                "(select avg(if( c2.committer = '" + name +"' and c1.committer <> '"+name+"',timestampdiff(SECOND,c1.commit_time, case ic.case_status when 'SOLVED' then c2.commit_time else localtime() end),null))) time2, " +
                "(select avg(if(c1.committer = '" + name +"' and ic.case_status <> 'SOLVED',timestampdiff(SECOND,c1.commit_time, case ic.case_status when 'SOLVED' then c2.commit_time else localtime() end),null))) time3, " +
                "(select avg(if( c1.committer = '" + name +"' and c2.committer <> '"+name+"',timestampdiff(SECOND,c1.commit_time, case ic.case_status when 'SOLVED' then c2.commit_time else localtime() end),null))) time4, " +
                " ic.type_id stringValue " +
                "from iss_case ic join commit c1 on ic.commit_id_new = c1.commit_id " +
                "left join commit c2 on ic.commit_id_last = c2.commit_id " +
                "where c1.repo_path = '"+repo+"' group by ic.type_id";
        List<Int4Time4String1Value> int4Float4String1Values = (List<Int4Time4String1Value>) sqlMapping.select(new Int4Time4String1Value(), sql);
        System.out.println("\n引入缺陷: ");
        for(Int4Time4String1Value int4Float4String1Value : int4Float4String1Values){
            if(int4Float4String1Value.getIntValue1() > 0) System.out.println("类型: "+ int4Float4String1Value.getStringValue() + ", 数量: "+int4Float4String1Value.getIntValue1() + ", 平均存活周期: " + int4Float4String1Value.getTime1());
        }
        System.out.println("\n解决他人引入缺陷: ");
        for(Int4Time4String1Value int4Float4String1Value : int4Float4String1Values){
            if(int4Float4String1Value.getIntValue2() > 0) System.out.println("类型: "+ int4Float4String1Value.getStringValue() + ", 数量: "+int4Float4String1Value.getIntValue2() + ", 平均存活周期: " + int4Float4String1Value.getTime2());
        }
        System.out.println("\n引入且尚未解决缺陷: ");
        for(Int4Time4String1Value int4Float4String1Value : int4Float4String1Values){
            if(int4Float4String1Value.getIntValue3() > 0) System.out.println("类型: "+ int4Float4String1Value.getStringValue() + ", 数量: "+int4Float4String1Value.getIntValue3() + ", 平均存活周期: " + int4Float4String1Value.getTime3());
        }
        System.out.println("\n引入且被他人解决缺陷: ");
        for(Int4Time4String1Value int4Float4String1Value : int4Float4String1Values){
            if(int4Float4String1Value.getIntValue4() > 0) System.out.println("类型: "+ int4Float4String1Value.getStringValue() + ", 数量: "+int4Float4String1Value.getIntValue4() + ", 平均存活周期: " + int4Float4String1Value.getTime4());
        }
    }

}
