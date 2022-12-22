package org.example;

import org.example.Entity.Commit;
import org.example.Entity.Iss_location;
import org.example.Entity.SonarRules;
import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarLocation;
import org.example.Update.Match_Info;
import org.example.Update.Matches;
import org.example.Update.RawIssueMatch;
import org.example.Utils.JgitUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SaveTest {
    public static void main(String[] args) throws Exception {

        String pj_path = Constant.RepoPath;

        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.execSqlReadFileContent("crebas2.sql");
        mysqlConnect.useDataBase("sonarissue");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);

        HashMap<String, SonarRules> rulesHashMap = new HashMap<>();

        Match_Info matchInfo = new Match_Info();
        matchInfo.setCase_id("ed1fb18b-f6e2-3a69-a3e3-1d9c9bbb1376");
        matchInfo.setMessage("message");
        matchInfo.setCommit_id_last("bd20dc70-5784-3caf-a637-7e383e1f9334");
        matchInfo.setType_id("type");
        matchInfo.setCase_status("UNDONE");
        matchInfo.setFile_name(".mvn/wrapper/MavenWrapperDownloader.java");
        matchInfo.setInst_id_last("123");
        Iss_location issLocation = new Iss_location();
        issLocation.setClass_("1");
        issLocation.setCode("1");
        issLocation.setMethod("1");
        issLocation.setStart_line(0);
        issLocation.setEnd_line(2);
        issLocation.setStart_col(0);
        issLocation.setEnd_col(3);
        Matches matches = new Matches(matchInfo,Collections.singletonList(issLocation));
        List<Matches> matchesList= new ArrayList<>();
        matchesList.add(matches);
        SonarIssues sonarIssues = new SonarIssues();
        sonarIssues.setId("124");
        sonarIssues.setTypeId("type");
        sonarIssues.setMessage("message");
        sonarIssues.setSeverity("severity");
        sonarIssues.setFilePath("cim:.mvn/wrapper/MavenWrapperDownloader.java");
        SonarLocation sonarLocation = new SonarLocation();
        sonarLocation.setStartLine("10");
        sonarLocation.setEndLine("2");
        sonarLocation.setStartOffset("2");
        sonarLocation.setEndOffset("10");
        sonarIssues.setLocation(Collections.singletonList(sonarLocation));


        Commit commit = new Commit();
        commit.setCommit_id("cm_id");

        RawIssueMatch.myMatch(sqlMapping, matchesList, rulesHashMap, Collections.singletonList(sonarIssues), commit);
    }
}
