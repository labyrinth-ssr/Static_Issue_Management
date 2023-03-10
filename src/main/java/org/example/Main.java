package org.example;

import org.apache.commons.lang.StringUtils;
import org.example.Entity.Repos;
import org.example.Query.Cmd;
import org.example.Update.Save;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("welcome");
        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.execSqlReadFileContent("db.sql");
        mysqlConnect.useDataBase("sonarissue");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);
        sqlMapping.execute(Constant.func);
        System.out.println("func");
        List<Repos> reposList = (List<Repos>) sqlMapping.select(new Repos());
        if(reposList==null || reposList.size() == 0){
//            mysqlConnect.execSqlReadFileContent("data.sql");
            mysqlConnect.execSqlReadFileContent("data/sonarissue_commit.sql");
            mysqlConnect.execSqlReadFileContent("data/sonarissue_commit_inst.sql");
            mysqlConnect.execSqlReadFileContent("data/sonarissue_sonarrules.sql");
            mysqlConnect.execSqlReadFileContent("data/sonarissue_iss_case.sql");
            mysqlConnect.execSqlReadFileContent("data/sonarissue_iss_instance.sql");
            mysqlConnect.execSqlReadFileContent("data/sonarissue_iss_location.sql");
            mysqlConnect.execSqlReadFileContent("data/sonarissue_repos.sql");


        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            args = br.readLine().split(" ");
            int len = args.length;
            if(len > 0) {
                if (Objects.equals(args[0].trim(), "run")) Cmd.run(false);
                else if (Objects.equals(args[0].trim(),"mock")) Mock.mock();
                else if(Objects.equals(args[0].trim(),"mocktest")) Cmd.run("sonarissuemock",true);
                else if (len >= 2 && Objects.equals(args[0].trim(), "save")) {
                    if (len == 2) Save.save(args[1].trim(), null);
                    else if (len == 3 && StringUtils.isNumeric(args[2])) Save.save(args[1], Integer.valueOf(args[2]));
                    else showHelp();
                }else{
                    showHelp();
                }
            } else {
                showHelp();
            }
        }

//
//        Save.save(Constant.RepoPath, null);
//        Cmd.run();
    }

    public static void showHelp(){
        System.out.println("" +
                "run    ????????????\n" +
                "save (repo_path) [num] ????????????????????????????????????????????????num????????????????????????num??????????????????\n" +
                "mock ??????mock?????????mock?????????conf.properties?????????,mock???????????????sonarrulesmock?????????\n" +
                "mocktest mock??????");
    }
}