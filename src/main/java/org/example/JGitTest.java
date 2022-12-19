package org.example;

import org.eclipse.jgit.revwalk.RevCommit;
import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.example.Entity.*;
import org.example.Utils.JgitUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.io.*;
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

//        Repository repository=new Repository();
//        repository.setPath(pj_path.replace("\\","/"));
//        repository.pathToName();
//        boolean a = sqlMapping.save(Collections.singletonList(repository));
        PrintStream console = System.out;
        System.setOut(null);
        Git git = JgitUtil.openRpo(pj_path);

//        List<Iss_file> iss_files = JgitUtil.gitFileList(git, repository.getPath());

        List<RevCommit> revCommitList = JgitUtil.gitLogRev(git);
//        List<Commit> commitList = JgitUtil.gitLog(git);
        List<Commit> commitList = JgitUtil.revCommitList2Commit(revCommitList);
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


        for (int i = commitList.size()-2; i >=commitList.size()-4; i--) {

            System.out.print(i+":"+commitList.get(i).getCommit_msg());

//            Ref ref = JgitUtil.gitReset(git, commitList.get(i).getCommit_hash());
//            System.out.println("size"+ commitList.size()+" size"+revCommitList.size());
            System.setOut(null);
//            List<String> changedFile = JgitUtil.getChangedFileList(revCommitList.get(i),revCommitList.get(i+1), git);
            System.setOut(console);

            runProcess(pj_path,"sonar-scanner -D sonar.projectKey=cim");

//            if(changedFile!=null) System.out.println("changedFile:" + changedFile.toString());
            List<SonarIssues> sonarIssues = SonarResult.getSonarIssues();

            Commit commit = new Commit();
            commit.setCommit(commitList.get(i),pj_path.replace("\\","/"));
            commitList1.add(commit);

            Iss_instance.setInstance(issInstanceList,sonarIssues,commit);
            Instance_location.setInstanceLocation(sonarIssues,instance_locationList);
            SonarRules.setSonarRules(sonarRulesList,sonarIssues);
//            Iss_location.setLocation(iss_locations,sonarIssues);
            System.setOut(null);
            if (commitList1.size()==1) {
                RawIssueMatch.firstMatch(iss_locations,iss_matchList,iss_caseList,sonarIssues,commitList1.get(0));
            }else {
                RawIssueMatch.match(iss_locations,iss_matchList,iss_caseList,sonarIssuesPre,sonarIssues,commitList1.get(commitList1.size()-1),commitList1.get(commitList1.size()-2));
            }
            sonarIssuesPre = new ArrayList<> (sonarIssues);
            System.setOut(console);


        }
//      boolean b = sqlMapping.save(iss_files);
        boolean c =sqlMapping.save(commitList1);
        boolean g = sqlMapping.save(iss_locations);
        boolean d =sqlMapping.save(issInstanceList);
        boolean h = sqlMapping.save(instance_locationList);
        boolean j =sqlMapping.save(iss_caseList);
        boolean f = sqlMapping.save(iss_matchList);
        boolean k = sqlMapping.save(sonarRulesList);


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
//            System.out.println(cmd + " " + line);
        }
    }

    private static void runProcess(String path, String command) throws Exception {


        System.out.print(", Sonarqube scanning ...");
        Process pro = Runtime.getRuntime().exec("cmd /c "+ command, null, new File(path));
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " " + (pro.exitValue()==0?"DONE!":"FAIL"));
    }
}

