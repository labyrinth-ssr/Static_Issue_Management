package org.example;

import SonarConfig.SonarIssues;
import SonarConfig.SonarResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.example.Entity.Commit;
import org.example.Entity.Iss_file;
import org.example.Entity.Iss_instance;
import org.example.Entity.Repository;
import org.example.Utils.JgitUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JGitTest {
    //获取版本信息和issue。
    public static void main(String[] args) throws Exception {
        SqlConnect mysqlConnect = new SqlConnect(System.getProperty("user.dir") + "/conf.properties");
        mysqlConnect.useDataBase("sonarissue");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);
//        使用自己库时把我这个注释掉，不要删
//        String pj_path = "E:\\Blood\\secondyear_spring\\se\\work\\lab2_back-end";
        String pj_path = "C:\\Users\\31324\\Desktop\\ss-backend\\lab2_back-end";

        List<Repository> repositoryList=new ArrayList<>();
        Repository repository=new Repository();
        repository.setPath(pj_path);
        repository.pathToName();
        repositoryList.add(repository);
        boolean a = sqlMapping.save(repositoryList);


        Git git = JgitUtil.openRpo(pj_path);

        List<Iss_file> iss_files = JgitUtil.gitFileList(git,pj_path);
        boolean b = sqlMapping.save(iss_files);

        List<Commit> commitList = JgitUtil.gitLog(git);
        Commit curCommit = JgitUtil.gitCurLog(git);
        System.out.println("cur" + curCommit.getCommit_hash());

        List<Commit> commitList1 = new ArrayList<>();
        List<Iss_instance> issInstanceList = new ArrayList<>();
        List<SonarIssues> sonarIssuesPre = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Ref ref = JgitUtil.gitReset(git, commitList.get(i).getCommit_hash());
            System.out.println("ref:"+ ref.getObjectId().getName());
            System.out.println("commit"+commitList.get(i).toString());
            runProcess(pj_path,"sonar-scanner -D sonar.projectKey=cim");
            List<SonarIssues> sonarIssues = SonarResult.getSonarIssues();
            Commit commit = new Commit();
            commit.setCommit(commitList.get(i),pj_path);
            System.out.println(commit);
            commitList1.add(commit);

            Iss_instance iss_instance=new Iss_instance();
            iss_instance.setInstance(sonarIssues,commit,issInstanceList);

            if (i>0){
                System.out.println(sonarIssuesPre);
                System.out.println(sonarIssues);
                RawIssueMatch.match(sonarIssuesPre,sonarIssues,commitList.get(i-1).getCommit_hash(),commitList.get(i).getCommit_hash());
            }
            sonarIssuesPre = new ArrayList<> (sonarIssues);
        }

        boolean c =sqlMapping.save(commitList1);
        boolean d =sqlMapping.save(issInstanceList);

        JgitUtil.gitReset(git, curCommit.getCommit_hash());
//        List<Iss_file> issfileList = new ArrayList<>();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        File file = new File(pj_path);		//获取其file对象
//        File[] fs = file.listFiles();
//        for(File f:fs)
//        {
//            if(f.isFile())
//            {
//                String f1 = f.toString().replace("\\","/");
//                Iss_file fti = new Iss_file();
//                fti.setFile_path(f1);
//                String[] temp2 = f1.split("/");
//                fti.setFile_name(temp2[temp2.length-1]);
//                fti.setlast_modified_time();
//                fti.setRepo_path(pj_path);
//                issfileList.add(fti);
//            }
//        }
//        boolean b = sqlMapping.save(issfileList);



////        System.out.println(sonarIssues.toString());
//
//
//
//
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

