package org.example;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.example.Entity.*;
import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarResult;
import org.example.Update.Match_Info;
import org.example.Update.Matches;
import org.example.Update.RawIssueMatch;
import org.example.Utils.*;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Save {
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

        List<SonarRules> sonarRulesList = new ArrayList<>();
        sonarRulesList = (List<SonarRules>) sqlMapping.select(new SonarRules());
        HashMap<String, SonarRules> rulesHashMap = new HashMap<>();
        sonarRulesList.forEach(sonarRules -> rulesHashMap.put(sonarRules.getId(),sonarRules));
        List<Matches> matches = new ArrayList<>();
        String sql_str = "select ic.case_id, ic.case_status, ic.commit_id_last, ic.commit_id_disappear, ii.inst_id inst_id_last, ii.type_id, sr.description message, ii.file_path file_name " +
                "from iss_case ic join iss_instance ii on ic.commit_id_disappear = ii.commit_id join sonarrules sr on ii.type_id = sr.id";
        List<Match_Info> matchInfoList =  (List<Match_Info>) sqlMapping.select(new Match_Info(),sql_str);
        matchInfoList.forEach(matchInfo -> {
            String sql = "select il.* from iss_location il join instance_location inl on il.location_id = inl.location_id where inl.inst_id = '" + matchInfo.getInst_id_last()+"'";
            List<Iss_location> locations = null;
            try {
                locations = (List<Iss_location>) sqlMapping.select(new Iss_location(),sql);
            } catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            matches.add(new Matches(matchInfo,locations));
        });
        for (int i = commitList.size()-1; i >=0; i--) {
            System.out.print(i + ":" + commitList.get(i).getCommit_msg());

            Ref ref = JgitUtil.gitReset(git, commitList.get(i).getCommit_hash());
//            Thread.sleep(1000 * 5);
            String key = pj_path.split("/")[pj_path.split("/").length-1]+commitList.get(i).getCommit_hash();
            CmdUtil.runProcess(pj_path,"sonar-scanner -D sonar.projectKey="+key);
//            Thread.sleep(1000 * 2);
            List<SonarIssues> sonarIssues = SonarResult.getSonarIssues(key);

            if(sonarIssues!=null||sonarIssues.size()!=0) System.out.println("saving defects");

            Commit commit = new Commit();
            commit.setCommit(commitList.get(i), pj_path.replace("\\","/"));

//            System.setOut(null);
            RawIssueMatch.myMatch(sqlMapping, matches, rulesHashMap, sonarIssues, commit,true);
//            System.setOut(console);
        }

        JgitUtil.gitReset(git, curCommit.getCommit_hash());

        String full_view = "create or replace view full_view as\n" +
                "select ic.*, sr.type, ii1.inst_id inst_id_new, ii2.inst_id inst_id_last, c1.commit_time time_new, c2.commit_time time_disappear, c1.committer committer_new, c2.committer committer_disappear,\n" +
                "case case_status when 'SOLVED'\n" +
                "then TIMESTAMPDIFF(SECOND,c1.commit_time, c2.commit_time)\n" +
                "else TIMESTAMPDIFF(SECOND,c1.commit_time, LOCALTIME()) END\n" +
                "duration\n" +
                "from iss_case ic join commit c1 on ic.commit_id_new = c1.commit_id\n" +
                "left join commit c2 on ic.commit_id_disappear = c2.commit_id\n" +
                "join iss_instance ii1 on ic.commit_id_new = ii1.commit_id and ii1.case_id = ic.case_id\n" +
                "join iss_instance ii2 on ic.commit_id_last = ii2.commit_id and ii2.case_id = ic.case_id\n" +
                "join sonarrules sr on sr.id = ic.type_id;";

        sqlMapping.execute(full_view);
    }
}
