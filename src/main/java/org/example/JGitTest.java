package org.example;

import SonarConfig.SonarIssues;
import SonarConfig.SonarResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.example.Entity.*;
import org.example.Utils.JgitUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JGitTest {
    //获取版本信息和issue。
    public static void main(String[] args) throws Exception {

        String pj_path = Constant.RepoPath;

        SqlConnect mysqlConnect = new SqlConnect();
//        mysqlConnect.execSqlReadFileContent("crebas2.sql");
        mysqlConnect.useDataBase("sonarissue");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);

//        Repository repository=new Repository();
//        repository.setPath(pj_path.replace("\\","/"));
//        repository.pathToName();
//        boolean a = sqlMapping.save(Collections.singletonList(repository));

        Git git = JgitUtil.openRpo(pj_path);

//        List<Iss_file> iss_files = JgitUtil.gitFileList(git, repository.getPath());

        List<Commit> commitList = JgitUtil.gitLog(git);
        Commit curCommit = JgitUtil.gitCurLog(git);
        System.out.println("cur" + curCommit.getCommit_hash());

        List<Commit> commitList1 = new ArrayList<>();
        List<SonarIssues> sonarIssuesPre = new ArrayList<>();
        Commit commitPre = new Commit();
        List<Iss_match> iss_matchList = new ArrayList<>();
        List<Iss_case> iss_caseList = new ArrayList<>();
        List<Iss_instance> issInstanceList = new ArrayList<>();
        List<Iss_location> iss_locations = new ArrayList<>();
        List<Instance_location> instance_locationList = new ArrayList<>();
        List<SonarRules> sonarRulesList = new ArrayList<>();

        for (int i = 28; i >=26; i--) {

            System.out.println(i+":"+commitList.get(i).getCommit_msg());

            Ref ref = JgitUtil.gitReset(git, commitList.get(i).getCommit_hash());

            runProcess(pj_path,"sonar-scanner -D sonar.projectKey=cim");

            List<SonarIssues> sonarIssues = SonarResult.getSonarIssues();

            Commit commit = new Commit();
            commit.setCommit(commitList.get(i),pj_path.replace("\\","/"));
            commitList1.add(commit);

            Iss_instance.setInstance(issInstanceList,sonarIssues,commit);
            Instance_location.setInstanceLocation(sonarIssues,instance_locationList);
            SonarRules.setSonarRules(sonarRulesList,sonarIssues);
//            Iss_location.setLocation(iss_locations,sonarIssues);

            if (commitList1.size()==1) {
                RawIssueMatch.firstMatch(iss_locations,iss_matchList,iss_caseList,sonarIssues,commitList1.get(0));
            }else {
                RawIssueMatch.match(iss_locations,iss_matchList,iss_caseList,sonarIssuesPre,sonarIssues,commitList1.get(commitList1.size()-1),commitList1.get(commitList1.size()-2));
            }
            sonarIssuesPre = new ArrayList<> (sonarIssues);
        }
//        boolean b = sqlMapping.save(iss_files);
        boolean g = sqlMapping.save(iss_locations);
        boolean d =sqlMapping.save(issInstanceList);
        boolean h = sqlMapping.save(instance_locationList);
        boolean j =sqlMapping.save(iss_caseList);
        boolean f = sqlMapping.save(iss_matchList);
        boolean k = sqlMapping.save(sonarRulesList);

        boolean c =sqlMapping.save(commitList1);

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

