package org.example;

import SonarConfig.SonarIssues;
import SonarConfig.SonarResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.example.Entity.Commit;
import org.example.Utils.JgitUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.SortedMap;

public class JGitTest {
    //获取版本信息和issue。
    public static void main(String[] args) throws Exception {
        //使用自己库时把我这个注释掉，不要删
        String pj_path = "E:\\Blood\\secondyear_spring\\se\\work\\lab2_back-end";
        Git git = JgitUtil.openRpo(pj_path);
        List<Commit> commitList = JgitUtil.gitLog(git);
        Commit curCommit = JgitUtil.gitCurLog(git);
        System.out.println("cur" + curCommit.getCommit_hash());
        Ref ref = JgitUtil.gitReset(git, commitList.get(commitList.size()-1).getCommit_hash());
        System.out.println(ref.getObjectId().getName());
        runProcess(pj_path,"sonar-scanner -D sonar.projectKey=cim");
        List<SonarIssues> sonarIssues = SonarResult.getSonarIssues();
        System.out.println(sonarIssues.toString());
        JgitUtil.gitReset(git, curCommit.getCommit_hash());
    }

    private static void printLines(String cmd, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(cmd + " " + line);
        }
    }

    private static void runProcess(String path, String command) throws Exception {

        Process pro = Runtime.getRuntime().exec("cmd /c "+ command, null, new File(path));
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
    }
}
