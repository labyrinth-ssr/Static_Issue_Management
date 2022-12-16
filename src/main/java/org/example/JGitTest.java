package org.example;

import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarResult;
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
import java.util.Objects;

public class JGitTest {
    //获取版本信息和issue。
    public static void main(String[] args) throws Exception {

        String pj_path = Constant.RepoPath;

        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.execSqlReadFileContent("crebas2.sql");
        mysqlConnect.useDataBase("sonarissue");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);

        Repository repository=new Repository();
        repository.setPath(pj_path.replace("\\","/"));
        repository.pathToName();
        boolean a = sqlMapping.save(Collections.singletonList(repository));

        Git git = JgitUtil.openRpo(pj_path);

        List<Iss_file> iss_files = JgitUtil.gitFileList(git, repository.getPath());

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


        for (int i = commitList.size()-2; i >=2; i--) {

            Ref ref = JgitUtil.gitReset(git, commitList.get(i).getCommit_hash());

            runProcess(pj_path,"sonar-scanner -D sonar.projectKey=cim");

            List<SonarIssues> sonarIssues = SonarResult.getSonarIssues();

            Commit commit = new Commit();
            commit.setCommit(commitList.get(i),repository.getPath());
            System.out.println(commit);
            commitList1.add(commit);

            Iss_instance.setInstance(issInstanceList,sonarIssues,commit);
            Instance_location.setInstanceLocation(sonarIssues,instance_locationList);
            SonarRules.setSonarRules(sonarRulesList,sonarIssues);
//            Iss_location.setLocation(iss_locations,sonarIssues);

            if (i==1) {
                RawIssueMatch.firstMatch(iss_locations,iss_matchList,iss_caseList,sonarIssues,commitList.get(1));
            }else {
                RawIssueMatch.match(iss_locations,iss_matchList,iss_caseList,sonarIssuesPre,sonarIssues,commitList.get(i+1),commitList.get(i));
            }
            sonarIssuesPre = new ArrayList<> (sonarIssues);

            if(list_not_empty(iss_files)) sqlMapping.save(iss_files);
            if(list_not_empty(iss_locations)) sqlMapping.save(iss_locations);
            if(list_not_empty(issInstanceList)) sqlMapping.save(issInstanceList);
            if(list_not_empty(instance_locationList)) sqlMapping.save(instance_locationList);
            if(list_not_empty(iss_caseList)) sqlMapping.save(iss_caseList);
            if(list_not_empty(iss_matchList)) sqlMapping.save(iss_matchList);
            if(list_not_empty(sonarRulesList)) sqlMapping.save(sonarRulesList);
        }

        boolean c =sqlMapping.save(commitList1);

        JgitUtil.gitReset(git, curCommit.getCommit_hash());
    }

    static public boolean list_not_empty(List<?> obj){
        return !(obj == null || obj.size() == 0);
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

