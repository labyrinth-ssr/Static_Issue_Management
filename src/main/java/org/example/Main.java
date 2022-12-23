package org.example;

import org.apache.commons.lang.StringUtils;
import org.example.Query.Cmd;
import org.example.Update.Save;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("welcome");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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
//
//        Save.save(Constant.RepoPath, null);
//        Cmd.run();
    }

    public static void showHelp(){
        System.out.println("" +
                "run    执行查询\n" +
                "save (repo_path) [num] 扫描并存储指定路径仓库，如果指定num，则从距最新版本num位置开始扫描");
    }
}