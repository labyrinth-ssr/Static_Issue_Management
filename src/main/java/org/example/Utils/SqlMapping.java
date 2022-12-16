package org.example.Utils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class SqlMapping {
    private Field[] fields;
    private String tableName;
    SqlConnect connection;
    public SqlMapping(SqlConnect connection) {
        this.connection = connection;
    }
    private List<List<?>> getFields(List<?> objs) throws InvocationTargetException, IllegalAccessException {
        Class<?> c = objs.get(0).getClass();
        tableName = objs.get(0).getClass().getSimpleName().toLowerCase();
        fields = c.getDeclaredFields();
        Method[] methods = c.getMethods();

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
                System.out.println("method:"+method);
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
    /*
            private String getDeleteSQL(Object obj, String tableName) {
                return "delete from " + "`" + tableName + "` " + " where " + fields[0].getName() + "=?";
            }
        */
    private String getSelectSQL(List<? extends Map.Entry<String, ?>> list) {
        StringBuffer s = new StringBuffer();
        s.append("select * from " + "`").append(tableName).append("` ");
        if(list != null && list.size() != 0){
            s.append(" where ");
            for(Map.Entry<String, ?> l : list){
                s.append(l.getKey()).append(" = ? ").append(" & ");
            }
            return s.toString().trim().substring(0, list.size() - 1) + ";";
        }
        return s.append(";").toString();
    }

    public boolean save(List<?> objs) throws Exception {
        List<List<?>> list = getFields(objs);
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
//                for(int i = 0; i<ns.length; i++){
//                    if(ns[i] <= 0){
//                        //System.out.println(String.valueOf(++kk)+": "+list.get(i).toString());
//
//                    }
//                }
                batchNum = 0;
                ps.clearParameters();
            }
        }
        int[] ns = ps.executeBatch();
//        for(int i = 0; i<ns.length; i++){
//            if(ns[i] <= 0){
//                //System.out.println(String.valueOf(++kk)+": "+list.get(i).toString());
//            }
//        }
        return true;
    }

    public List<?> select(Object obj) throws Exception {
        return select(obj,null);
    }

    public List<?> select(Object obj, List<? extends Map.Entry<String, ?>> list) throws Exception {
        List<Object> l = new ArrayList<>();
        l.add(obj); List<List<?>> ls = getFields(l);
        String sql = getSelectSQL(list);

        Connection conn = connection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        if(list != null){
            for(int i = 1; i <= list.size(); i++) {
                ps.setObject(i, list.get(i-1).getValue());
            }
        }
        ResultSet rs = ps.executeQuery();
        Class<?> c = obj.getClass();
        Method[] method = c.getMethods();
        List<Object> result = new ArrayList<>();
        while (rs.next()) {
            Object o = c.newInstance();
            for (int j = 0; j < fields.length; j++) {
                String m = "set" + fields[j].getName().toUpperCase().charAt(0)
                        + fields[j].getName().substring(1);
                for (Method value : method) {
                    if (value.getName().endsWith(m)) {
                        try {
                            value.invoke(o, rs.getObject(j + 1));
                        } catch (Exception e) {
                            System.out.println(rs.getObject(j + 1).getClass().toString());
                            e.printStackTrace();
                        }
                    }

                }

            }
            result.add(o);
        }
        return result;
    }

//    public boolean update(Object obj) throws Exception {
//        // 获取obj的属性的值
//        List list = getFields(obj);
//        // 获取sql
//        String sql = getUpdateSQL(obj);
//        // 通过DbUtil
//        Connection conn = connection.getConnection();
//        PreparedStatement ps = conn.prepareStatement(sql);
//        for (int i = 1; i < list.size(); i++) {
//            ps.setObject(i, list.get(i));
//        }
//        ps.setInt(list.size(), (Integer) list.get(0));
//        boolean flag = ps.executeUpdate() > 0;
//        return flag;
//    }
/*
    public boolean delete(Object obj,Integer id) throws Exception {
        // 获取obj的属性的值
        List list = getFields(obj);
        // 获取sql
        String sql = getDeleteSQL(obj);
        // 通过DbUtil
        Connection conn = connection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        boolean flag = ps.executeUpdate() > 0;
        return flag;
    }
 */
}

