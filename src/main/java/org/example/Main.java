package org.example;

import SonarConfig.SonarResult;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

public class Main {
    public static void main(String[] args) throws Exception {
        SqlConnect mysqlConnect = new SqlConnect(System.getProperty("user.dir") + "/conf.properties");
        mysqlConnect.execSqlReadFileContent("sql_create.sql");

        mysqlConnect.useDataBase("sonar");

        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);

        boolean a = sqlMapping.save(SonarResult.getSonarIssues());
        boolean b = sqlMapping.save(SonarResult.getSonartype());
//        boolean c = sqlMapping.save(SonarResult.getCommit());

        System.out.println("The lab1 is done! success!");
        //System.out.println(SonarResult.getSonarIssues().toString());
    }
}