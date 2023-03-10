package org.example.Update;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.example.Constant;
import org.example.Entity.*;
import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarResult;
import org.example.Utils.*;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Save {
    public static void save(String repo_path, Integer num) throws Exception {

        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.useDataBase("sonarissue");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);

        PrintStream console = System.out;
        System.setOut(null);
        Git git = JgitUtil.openRpo(repo_path);
        List<Commit> commitList = JgitUtil.gitLog(git);
        Commit curCommit = JgitUtil.gitCurLog(git);
        System.setOut(console);

        int count = num == null ? commitList.size()-1:num-1;
        if(num!=null && num > commitList.size()){
            System.err.println("当前仓库提交数: "+ commitList.size() + ", 预扫描数: " + num);
            throw new Exception();
        }
        System.out.println("cur: " + curCommit.getCommit_hash());

        repo_path = repo_path.replace("\\","/");
        repoCheck(repo_path, sqlMapping);
        HashMap<String, SonarRules> rulesHashMap = getRules(sqlMapping);
        List<Matches> matches = getMatches(sqlMapping, repo_path);
        boolean first_flag = true;
        System.setOut(null);
        System.setOut(console);
        for (int i = count; i >=0; i--) {
            Commit commit = new Commit();
            commit.setCommit(commitList.get(i), repo_path);
            System.out.print(i + ":" + commit.getCommit_msg() +", hash: "+commit.getCommit_hash());
            Ref ref = JgitUtil.gitReset(git, commit.getCommit_hash());
            List<String> changedFileList = JgitUtil.getChangedFiles(git);
            List<SonarIssues> sonarIssues = resetAndScanAndFetch(repo_path, commit.getCommit_hash(), first_flag, changedFileList);
            System.out.println("changedFileList: "+changedFileList.toString());
            RawIssueMatch.myMatch(sqlMapping, matches, rulesHashMap, sonarIssues, commit, repo_path, changedFileList);
            first_flag = false;
        }
        JgitUtil.gitReset(git, curCommit.getCommit_hash());
    }


    public static List<SonarIssues> resetAndScanAndFetch(String repo_path, String commit_hash, boolean first,List<String> changedFileList) throws Exception {
        String key = repo_path.split("/")[repo_path.split("/").length-1]+commit_hash;
        StringBuilder inclusions = new StringBuilder();
        int cnt = 0;
        for (String file: changedFileList) {
            if (cnt!= changedFileList.size()-1){
                inclusions.append("**/").append(file).append(",");
            } else {
                inclusions.append("**/").append(file);
            }
        }
        if (first){
            CmdUtil.runProcess(repo_path,"sonar-scanner -D sonar.projectKey="+key);
        } else {
            CmdUtil.runProcess(repo_path,"sonar-scanner -D sonar.projectKey="+key+" -D sonar.inclusions="+inclusions);
        }

        return SonarResult.getSonarIssues(key);
    }
//    "**/CourseSelectedController.java,**/PermitResponse.java"

    public static  boolean list_not_empty(List<?> l){
        return l!=null && l.size()!=0;
    }

    public static  void repoCheck(String repo_path, SqlMapping sqlMapping) throws Exception {
        List<Repos> stringValues = (List<Repos>)sqlMapping.select(new Repos(),"select * from repos where repo_path = '"+repo_path+"'");
        if(!list_not_empty(stringValues)){
            Repos repos = new Repos();
            repos.setRepo_path(repo_path);
            sqlMapping.save(Collections.singletonList(repos));
        }
    }

    public static  List<Matches> getMatches(SqlMapping sqlMapping, String repo_path) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<Matches> matches = new ArrayList<>();
        String sql_str = "select ic.case_id, ic.case_status, ic.commit_id_last, ic.commit_id_disappear, ii.inst_id inst_id_last, ic.type_id, sr.description message, ii.file_path file_name " +
                "from iss_case ic join commit_inst ci on ic.commit_id_last = ci.commit_id " +
                "join commit c on c.commit_id = ci.commit_id and c.repo_path = '" + repo_path +"' " +
                "join iss_instance ii on ii.inst_id = ci.inst_id " +
                "join sonarrules sr using(type_id) ";
        List<Match_Info> matchInfoList =  (List<Match_Info>) sqlMapping.select(new Match_Info(),sql_str);
        matchInfoList.forEach(matchInfo -> {
            String sql = "select il.* from iss_location il where il.inst_id = '" + matchInfo.getInst_id_last()+"'";
            List<Iss_location> locations = null;
            try {
                locations = (List<Iss_location>) sqlMapping.select(new Iss_location(),sql);
            } catch (Exception e) {
                e.printStackTrace();
            };
            matches.add(new Matches(matchInfo,locations));
        });
        return matches;
    }

    public static  HashMap<String, SonarRules> getRules(SqlMapping sqlMapping) throws Exception {
        List<SonarRules> sonarRulesList = new ArrayList<>();
        sonarRulesList = (List<SonarRules>) sqlMapping.select(new SonarRules());
        HashMap<String, SonarRules> rulesHashMap = new HashMap<>();
        sonarRulesList.forEach(sonarRules -> rulesHashMap.put(sonarRules.getType_id(),sonarRules));
        return rulesHashMap;
    }
}
