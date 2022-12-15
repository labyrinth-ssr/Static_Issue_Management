package org.example;

import org.example.Command.QueryCommand;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Cmd {
    public static void main(String[] args) throws Exception {
        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.useDataBase("sonarissue");
        QueryCommand queryCommand = new QueryCommand(mysqlConnect);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = null;
        List<String> my_args = null;
        while(true){
            System.out.print("Defect# ");
            str = br.readLine();
            if(str.startsWith("defect")){
                str = str.substring(6).trim();
                my_args = List.of(str.split("(?= -)"));
                queryCommand.ShowDefect(my_args);
            }else if(str.startsWith("commits")){
                queryCommand.ShowCommits();
            }else if(str.startsWith("repos")){
                queryCommand.ShowRepos();
            }else if(str.startsWith("devs")){
                queryCommand.ShowDevelopers();
            }else if(str.startsWith("quit")){
                break;
            }else if(str.startsWith("analysis")){
                str = str.substring(8).trim();
                my_args = List.of(str.split("(?= -)"));
                queryCommand.ShowAnalysis(my_args);
            }

        }
    }
}
