package org.example.Utils;

import org.example.Constant;
import org.example.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 负责执行sql文件，创建数据库
 * */
public class SqlConnect {
    public String JDBC_URL;// = "jdbc:mysql://localhost:3306";
    public String JDBC_USER;// = "root";
    public String JDBC_PASSWORD;// = "1230";

    public static Connection connection;

    public SqlConnect() {
        try {
            Properties properties = new Properties();
            File file =new File(System.getProperty("user.dir") + "/conf.properties");
            FileInputStream fileInputStream =new FileInputStream(file);
            properties.load(fileInputStream);
            JDBC_URL = Constant.jdbc_url;
            JDBC_USER = Constant.jdbc_user;
            JDBC_PASSWORD = Constant.jdbc_password;

            JDBC_URL = properties.getProperty("jdbc_url");
            JDBC_USER = properties.getProperty("jdbc_user");
            JDBC_PASSWORD = properties.getProperty("jdbc_password");

            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (SQLException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SqlConnect(String database) {
        try {
            Properties properties = new Properties();
            File file =new File(System.getProperty("user.dir") + "/conf.properties");
            FileInputStream fileInputStream =new FileInputStream(file);
            properties.load(fileInputStream);
            JDBC_URL = properties.getProperty("jdbc_url");
            JDBC_USER = properties.getProperty("jdbc_user");
            JDBC_PASSWORD = properties.getProperty("jdbc_password");

            String DATABASE_URL = JDBC_URL + "/" + database;
            connection = DriverManager.getConnection(DATABASE_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (SQLException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void useDataBase(String database) throws SQLException {
        releaseConnect();
        String DATABASE_URL = JDBC_URL + "/" + database;

        connection = DriverManager.getConnection(DATABASE_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public Connection getConnection(){
        return  connection;
    }

    public void releaseConnect() throws SQLException {
        if(connection != null) connection.close();
    }

    public static int sqlBatch(List<String> sql) {
        try {
            Statement st = connection.createStatement();
            for (String l : sql) {
                st.addBatch(l);
            }
            st.executeBatch();
            return 1;
        }catch (SQLException e){
//            e.printStackTrace();
            return 0;
        }
    }
    public static List<String> readSqlByFile(String fileName) throws IOException {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(fileName)), StandardCharsets.UTF_8))) {
            String tmpStr = null;
            int flag = 0;
            while ((tmpStr = reader.readLine()) != null) {
                if (tmpStr.trim().equals("")) continue;
                if (tmpStr.trim().substring(tmpStr.trim().length() - 1).equals(";")) {
                    if (flag == 1) {
                        sb.append(tmpStr);
                        list.add(sb.toString());
                        sb.delete(0, sb.length());
                        flag = 0;
                    } else {
                        list.add(tmpStr.trim());
                    }
                } else {
                    flag = 1;
                    sb.append(tmpStr);
                }
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return list;
    }

    public boolean execSqlReadFileContent(String fileName){
        try {
            List<String> sqlStr = readSqlByFile(fileName);
            if (sqlStr.size() > 0) {
                int num = sqlBatch(sqlStr);
                return num > 0;
            } else return false;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
}
