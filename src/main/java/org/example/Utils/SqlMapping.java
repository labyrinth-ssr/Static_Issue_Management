package org.example.Utils;


import org.example.Entity.Iss_case;
import org.example.Entity.Repos;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/**
 * 数据List结构自动化存储进数据库
 * */
public class SqlMapping {
    private Field[] fields;
    private String tableName;
    SqlConnect connection;

    public SqlMapping(SqlConnect connection) {
        this.connection = connection;
    }

    public Connection getConnection(){return connection.getConnection();}

    private List<List<?>> getFields(List<?> objs) throws InvocationTargetException, IllegalAccessException {
        Class<?> c = objs.get(0).getClass();
        tableName = objs.get(0).getClass().getSimpleName().toLowerCase();
        fields = c.getDeclaredFields();
        Method[] methods = c.getMethods();
//        System.out.println(tableName +" " + fields.toString()+" "+ methods.toString());
        List<Method> getMethod = new ArrayList<>();

        List<String> methodName = new ArrayList<>();
        for (Field field : fields) {
            String m = "get" + field.getName().toUpperCase().charAt(0)
                    + field.getName().substring(1);
            methodName.add(m);
        }

        Map<String, Method> methodMap = new HashMap<>();
        for(Method method : methods){
            methodMap.put(method.toString().substring(method.toString().lastIndexOf(".") + 1, method.toString().length() - 2), method);
        }

        for(String method : methodName){
            if(!methodMap.containsKey(method)) {
//                System.out.println("method:"+method);
                throw new RuntimeException("method dosen't exist");

            }
            getMethod.add(methodMap.get(method));
        }
        List<List<?>> list = new ArrayList<>();
        for (Object obj : objs) {
            List<Object> l = new ArrayList<>();
            for (Method method : getMethod) {
                l.add(method.invoke(obj));
            }
            list.add(l);
        }
        return list;
    }

    private String hump2Underline(String str){
        StringBuilder underLine = new StringBuilder();
        String lowerString = str.toLowerCase();
        for(int i = 0; i < str.length(); i++){
            char a = str.charAt(i);
            if(i == 0) underLine.append(lowerString.charAt(0));
            else if(Character.isUpperCase(a)){
                underLine.append("_").append(lowerString.charAt(i));
            }
            else{
                underLine.append(a);
            }
        }
        return underLine.toString();
    }

    private String getInsertSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append("insert ignore into ").append("`").append(tableName).append("`").append(" (");
        for (int i = 0; i < fields.length; i++) {
            sql.append("`").append(hump2Underline(fields[i].getName())).append("`");
            if (i < fields.length - 1) sql.append(",");
        }
        sql.append(") ").append(" values(");
        for (int i = 0; i < fields.length; i++) {
            sql.append("?");
            if (i < fields.length - 1) sql.append(",");
        }
        sql.append("); ");
        return sql.toString();
    }

        private String getUpdateSQL(Object obj, String tableName) {
            // 拼SQL语句
            StringBuffer sql = new StringBuffer();
            sql.append("update ");
            sql.append("`").append(tableName).append("` ");
            sql.append(" set ");
            for (int i = 1; i < fields.length; i++) {
                sql.append("`").append(fields[i].getName()).append("`");
                sql.append("=?");
                if (i < fields.length - 1) {
                    sql.append(",");
                }
            }
            sql.append(" where ");
            sql.append(fields[0].getName());
            sql.append("=?");
            return sql.toString();
        }
        private String getDeleteSQL(Object obj, String tableName) {
            return "delete from " + "`" + tableName + "` " + " where " + fields[0].getName() + "=?";
        }
    private String getSelectSQL(Object want, List<? extends Map.Entry<String, ?>> list, String more) {
        StringBuffer s = new StringBuffer();
        if(want == null) s.append("select * from " + "`").append(tableName).append("` ");
        else {
            s.append("select ");
            Field[] want_fields = want.getClass().getDeclaredFields();
            for(Field field : want_fields){
                s.append(field.getName().toLowerCase()).append(", ");
            }
            s.delete(s.lastIndexOf(","),s.length()).append(" from `").append(tableName).append("` ");
        }
        if(list != null && list.size() != 0){
            s.append(" where ");
            for(Map.Entry<String, ?> l : list){
                s.append(l.getKey()).append(" = ? ").append(" & ");
            }
            String str= s.toString().trim().substring(0, s.length() - 3) + (more==null?"":more) +  ";";
            return str;
        }
        return s.toString() + (more==null?"":more) + ";";
    }

    public boolean save(List<?> objs) throws Exception {
        if(objs == null || objs.size() == 0) return false;
        List<List<?>> list = getFields(objs);
//        System.out.println(list.toString());
        String sql = getInsertSQL();

        Connection conn = connection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        int batchNum = 0;
        int kk = 0;
        for(List<?> l : list) {
            for (int i = 1; i <= l.size(); i++)
                ps.setObject(i, l.get(i-1) instanceof ArrayList ? ((ArrayList<?>) l.get(i-1)).toString() : l.get(i-1));
            ps.addBatch();
            if(batchNum++ == 500){
                int[] ns = ps.executeBatch();
                batchNum = 0;
                ps.clearParameters();
            }
        }
        int[] ns = ps.executeBatch();
        return true;
    }

    public List<?> select(Object obj) throws Exception {
        return select(obj,null,null,null);
    }

    public List<?> select(Object want, String sql) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Connection conn = connection.getConnection();
//        System.out.println("sql: "+sql);
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        Class<?> c = want.getClass();
        Method[] method = c.getMethods();
        Field[] want_fields = c.getDeclaredFields();
        List<Object> result = new ArrayList<>();
        while (rs.next()) {
            Object o = c.getDeclaredConstructor().newInstance();
            for (int j = 0; j < want_fields.length; j++) {
                String m = "set" + want_fields[j].getName().toUpperCase().charAt(0)
                        + want_fields[j].getName().substring(1);
                for (Method value : method) {
                    if (value.getName().endsWith(m)) {
                        try {
                            //System.out.println("method: "+m);
                            value.invoke(o, rs.getObject(j + 1));
                        } catch (Exception e) {
//                            System.out.println("m: "+m);
//                            System.out.println(rs.getObject(j + 1).getClass().toString());
//                            e.printStackTrace();
                        }
                    }

                }
            }
            result.add(o);
        }
        return result;
    }

    /**
     * 自动生成查询语句并返回结果
     * obj：表对象
     * want：结果对象
     * match：where判断
     * more：附加条件
     * */
    public List<?> select(Object obj, Object want, List<? extends Map.Entry<String, ?>> match, String more) throws Exception {
        List<Object> l = new ArrayList<>();
        l.add(obj);
        List<List<?>> ls = getFields(l);
        String sql = getSelectSQL(want, match, more);
        Connection conn = connection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        if(match != null){
            for(int i = 1; i <= match.size(); i++) {
                ps.setObject(i, match.get(i-1).getValue());
            }
        }
        ResultSet rs = ps.executeQuery();
        Class<?> c = (want == null ? obj.getClass() : want.getClass());
        Field[] want_fields = c.getDeclaredFields();
        Method[] method = c.getMethods();
        List<Object> result = new ArrayList<>();
        while (rs.next()) {
            Object o = c.getDeclaredConstructor().newInstance();
            for (int j = 0; j < want_fields.length; j++) {
                String m = "set" + want_fields[j].getName().toUpperCase().charAt(0)
                        + want_fields[j].getName().substring(1);
                for (Method value : method) {
                    if (value.getName().endsWith(m)) {
                        try {
                            value.invoke(o, rs.getObject(j + 1));
                        } catch (Exception e) {
                            System.out.println(m + rs.getObject(j + 1).getClass().toString());
                            e.printStackTrace();
                        }
                    }
                }
            }
            result.add(o);
        }
        return result;
    }
    public void updateCase(List<Iss_case> issCaseList) throws Exception {
        // 获取obj的属性的值
        // 获取sql
        if(issCaseList == null || issCaseList.size() == 0) return;
        String sql = "update iss_case set commit_id_disappear = ?, commit_id_last = ?, case_status = ? where case_id = ?";
        // 通过DbUtil
        Connection conn = connection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        for(Iss_case iss_case:issCaseList){
            ps.setString(1, iss_case.getCommit_id_disappear());
            ps.setString(2, iss_case.getCommit_id_last());
            ps.setString(3, iss_case.getCase_status());
            ps.setString(4, iss_case.getCase_id());
            ps.addBatch();
        }
        ps.executeBatch();
    }

    public void execute(String sql) throws SQLException {
        SqlConnect.sqlBatch(Collections.singletonList(sql));
    }

    public void updateRepos(Repos repos) throws SQLException {
        String sql = "update repos set latest_commit_id = '"+repos.getLatest_commit_id()+"', commit_num = commit_num + 1 where repo_path = '" + repos.getRepo_path()+"'";
        execute(sql);
    }
}

