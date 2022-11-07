package org.example;

import net.sf.json.JSONObject;
import org.example.Utils.HTTPUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws Exception {
        SqlConnect mysqlConnect = new SqlConnect(System.getProperty("user.dir") + "/conf.properties");
        mysqlConnect.execSqlReadFileContent("sql_create.sql");

        mysqlConnect.useDataBase("sonar");

        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);
        boolean a = sqlMapping.save(SonarResult.getSonarIssues());
        boolean b = sqlMapping.save(SonarResult.getSonartype());
        //System.out.println(sqlMapping.select(new Issues()).toString());

        System.out.println("The lab1 is done! success!");

    }
}