package org.example;

import org.example.Entity.Repository;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.sql.SQLException;
import java.util.*;

public class Query_test {

    public static void main(String[] args) throws Exception {
        String pj_path = Constant.RepoPath;
        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.useDataBase("sonarissue");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);
        List<Map.Entry<String,?>> match = new ArrayList<>();
        Map.Entry<String, ?> t = new AbstractMap.SimpleEntry<>("path","E:/Blood/secondyear_spring/se/work/lab2_back-end");
        match.add(t);

        List<want> repolist = (List<want>) sqlMapping.select(new Repository(), new want(), match, "limit 1");
        System.out.println(repolist.toString());
    }
}

