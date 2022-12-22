package org.example;

import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.example.Entity.*;
import org.example.Update.RawIssueMatch;
import org.example.Utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JGitTest {
    //获取版本信息和issue。
    public static void main(String[] args) throws Exception {

        String pj_path = Constant.RepoPath;

        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.execSqlReadFileContent("crebas2.sql");
        mysqlConnect.useDataBase("sonarissue");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);

        PrintStream console = System.out;
        System.setOut(null);
        Git git = JgitUtil.openRpo(pj_path);

        List<Commit> commitList = JgitUtil.gitLog(git);
        Commit curCommit = JgitUtil.gitCurLog(git);
        System.setOut(console);
        System.out.println("cur: " + curCommit.getCommit_hash());

        List<Commit> commitList1 = new ArrayList<>();
        List<SonarIssues> sonarIssuesPre = new ArrayList<>();
        Commit commitPre = new Commit();
        List<Iss_match> iss_matchList = new ArrayList<>();
        List<Iss_case> iss_caseList = new ArrayList<>();
        List<Iss_instance> issInstanceList = new ArrayList<>();
        List<Iss_location> iss_locations = new ArrayList<>();
        List<Instance_location> instance_locationList = new ArrayList<>();
        List<SonarRules> sonarRulesList = new ArrayList<>();


        for (int i = commitList.size()-1; i >=commitList.size()-10; i--) {

            System.out.print(i+":"+commitList.get(i).getCommit_msg());

            Ref ref = JgitUtil.gitReset(git, commitList.get(i).getCommit_hash());

            CmdUtil.runProcess(pj_path,"sonar-scanner -D sonar.projectKey=cim");

            List<SonarIssues> sonarIssues = SonarResult.getSonarIssues("cim");
            if(sonarIssues!=null||sonarIssues.size()!=0) System.out.println("saving defects");

            Commit commit = new Commit();
            commit.setCommit(commitList.get(i),pj_path.replace("\\","/"));
            commitList1.add(commit);

            Iss_instance.setInstance(issInstanceList,sonarIssues,commit);
            Instance_location.setInstanceLocation(sonarIssues,instance_locationList);
            SonarRules.setSonarRules(sonarRulesList,sonarIssues);
            System.setOut(null);
            if (commitList1.size()==1) {
                RawIssueMatch.firstMatch(iss_locations,iss_matchList,iss_caseList,sonarIssues,commitList1.get(0));
            }else {
                RawIssueMatch.match(iss_locations,iss_matchList,iss_caseList,sonarIssuesPre,sonarIssues,commitList1.get(commitList1.size()-1),commitList1.get(commitList1.size()-2));
            }
            sonarIssuesPre = new ArrayList<> (sonarIssues);
            System.setOut(console);


        }
        sqlMapping.save(commitList1);
        sqlMapping.save(iss_locations);
        sqlMapping.save(issInstanceList);
        sqlMapping.save(instance_locationList);
        sqlMapping.save(iss_caseList);
        sqlMapping.save(iss_matchList);
        sqlMapping.save(sonarRulesList);

        JgitUtil.gitReset(git, curCommit.getCommit_hash());
    }


}

