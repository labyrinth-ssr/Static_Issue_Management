package org.example.Command;

import org.example.Command.Value.Int4Float4String1Value;
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
        String sql = "select (select count(*) where c1.committer = '"+name+"') intValue1, " +
                "(select count(*) where c2.committer = '" + name +"' and c1.committer <> '"+name+"') intValue2, " +
                "(select count(*) where c1.committer = '" + name +"' and ic.case_status <> 'SOLVED') intValue3, " +
                "(select count(*) where c1.committer = '" + name +"' and c2.committer <> '"+name+"') intValue4 " +
                "join commit c1 on ic.commit_id_new = c1.commit_id " +
                "left join commit c2 on ic.commit_id_last = c2.commit_id " +
                "where c1.repo_path = '"+repo+"'";
        Int4Value int4Value = ((List<Int4Value>) sqlMapping.select(new Int4Value(), sql)).get(0);

        System.out.println("开发人员: " + name +
                "引入缺陷数量: " + int4Value.getIntValue1() +
                ", 解决他人缺陷数量: " + int4Value.getIntValue2() +
                ", 引入但尚未解决缺陷数量: " + int4Value.getIntValue3() +
                ", 引入被他人解决缺陷数量: " + int4Value.getIntValue4());
    }

    public void getDevTypeCountByDevs(String name, String repo) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        //如果要计算准确的存活周期，需要再维护一张表，记录reopen的new,last,disappear的历史，或者说使用case_id和case_id_parent表
        //这里先不考虑reopen情况。
        String sql = "select (select count(*) where c1.committer = '"+name+"') intValue1, " +
                "(select avg(get_length_by_case_id(ic.case_id)) where c1.committer = '"+name+"') floatValue1, " +

                "(select count(*) where c2.committer = '" + name +"' and c1.committer <> '"+name+"') intValue2, " +
                "(select avg(get_length_by_case_id(ic.case_id)) where c2.committer = '" + name +"' and c1.committer <> '"+name+"') floatValue2, " +

                "(select count(*) where c1.committer = '" + name +"' and ic.case_status <> 'SOLVED') intValue3, " +
                "(select avg(get_length_by_case_id(ic.case_id)) where c1.committer = '" + name +"' and ic.case_status <> 'SOLVED') floatValue3, " +

                "(select count(*) where c1.committer = '" + name +"' and c2.committer <> '"+name+"') intValue4, " +
                "(select avg(get_length_by_case_id(ic.case_id)) where c1.committer = '" + name +"' and c2.committer <> '"+name+"') floatValue4, " +
                " ic.type_id stringValue " +
                "join commit c1 on ic.commit_id_new = c1.commit_id " +
                "left join commit c2 on ic.commit_id_last = c2.commit_id " +
                "where c1.repo_path = '"+repo+"' group by ic.type_id";
        List<Int4Float4String1Value> int4Float4String1Values = (List<Int4Float4String1Value>) sqlMapping.select(new Int4Float4String1Value(), sql);
        System.out.println("\n引入缺陷: ");
        for(Int4Float4String1Value int4Float4String1Value : int4Float4String1Values){
            if(int4Float4String1Value.getIntValue1() > 0) System.out.println("类型: "+ int4Float4String1Value.getStringValue() + ", 数量: "+int4Float4String1Value.getIntValue1() + ", 平均存活周期: " + int4Float4String1Value.getFloatValue1());
        }
        System.out.println("\n解决他人引入缺陷: ");
        for(Int4Float4String1Value int4Float4String1Value : int4Float4String1Values){
            if(int4Float4String1Value.getIntValue2() > 0) System.out.print("类型: "+ int4Float4String1Value.getStringValue() + ", 数量: "+int4Float4String1Value.getIntValue2() + ", 平均存活周期: " + int4Float4String1Value.getFloatValue2());
        }
        System.out.println("\n引入且尚未解决缺陷: ");
        for(Int4Float4String1Value int4Float4String1Value : int4Float4String1Values){
            if(int4Float4String1Value.getIntValue3() > 0) System.out.print("类型: "+ int4Float4String1Value.getStringValue() + ", 数量: "+int4Float4String1Value.getIntValue3() + ", 平均存活周期: " + int4Float4String1Value.getFloatValue3());
        }
        System.out.println("\n引入且被他人解决缺陷: ");
        for(Int4Float4String1Value int4Float4String1Value : int4Float4String1Values){
            if(int4Float4String1Value.getIntValue4() > 0) System.out.print("类型: "+ int4Float4String1Value.getStringValue() + ", 数量: "+int4Float4String1Value.getIntValue4() + ", 平均存活周期: " + int4Float4String1Value.getFloatValue4());
        }
    }

}
