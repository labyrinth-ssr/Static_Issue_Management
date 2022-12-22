package org.example;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.eclipse.jgit.api.Git;
import org.example.Entity.*;
import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarLocation;
import org.example.Utils.JgitUtil;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Mock {
    public static void main(String[] args) throws Exception {
        List<SonarRules> sonarRulesList = get_sonar_rules();

        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.execSqlReadFileContent("mock.sql");
        mysqlConnect.useDataBase("sonarissuemock");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);

        List<Commit> commitList = get_commit_list();
        List<String> fileList = get_file_list();
        List<Iss_case> issCaseList = new ArrayList<>();
        List<Iss_location> issLocationList = generate_loaction_list(1500);
        List<Iss_instance> issInstanceList = generate_iss_instance_and_case_list(issCaseList,sqlMapping,1000,sonarRulesList,commitList,fileList);
        List<Instance_location> instanceLocationList = match_inst_location(issInstanceList,issLocationList);
        sqlMapping.save(commitList);
        sqlMapping.save(sonarRulesList);
        sqlMapping.save(issLocationList);
        sqlMapping.save(issInstanceList);
        sqlMapping.save(issCaseList);
        sqlMapping.save(instanceLocationList);


        System.out.println("done");

    }

    public static List<Instance_location> match_inst_location(List<Iss_instance> issInstanceList,List<Iss_location> issLocationList){
        List<Instance_location> instanceLocationList = new ArrayList<>();
        issInstanceList.forEach(iss_instance -> {
            if (Math.random()<0.95){
                Instance_location instanceLocation = new Instance_location();
                instanceLocation.setInst_id(iss_instance.getInst_id());
                instanceLocation.setLocation_id(issLocationList.get((int)(Math.random()*issLocationList.size())).getLocation_id());
                instanceLocationList.add(instanceLocation);
                if (Math.random()<0.3){
                    Instance_location instanceLocation2 = new Instance_location();
                    instanceLocation2.setInst_id(iss_instance.getInst_id());
                    instanceLocation2.setLocation_id(issLocationList.get((int)(Math.random()*issLocationList.size())).getLocation_id());
                    instanceLocationList.add(instanceLocation2);
                }
            }
        });
        return instanceLocationList;
    }
    public static List<Commit> get_commit_list(){
        String pj_path = Constant.MockPath;
        PrintStream console = System.out;
        System.setOut(null);
        Git git = JgitUtil.openRpo(pj_path);
        List<Commit> commitList = JgitUtil.gitLog(git);
        int index = 0;
        for (Commit commit:commitList) {
            commit.setCommit_id(Commit.getUuidFromCommit(commit));
            commit.setRepo_path("MockRepo"+ ((++index)/(commitList.size()/4)));
        }
        System.setOut(console);
        System.out.println("commitList:"+commitList.toString());
        return commitList;
    }

    public static List<String> get_file_list(){
        String pj_path = Constant.MockPath;
        Git git = JgitUtil.openRpo(pj_path);
        List<String> iss_files = JgitUtil.gitFileList(git, pj_path);
        return iss_files;
    }

    public static List<SonarRules> get_sonar_rules() throws Exception {
        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.useDataBase("sonar");
        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);
        List<SonarRules> sonarRulesList= (List<SonarRules>) sqlMapping.select(new SonarRules());
        return sonarRulesList;
    }
    public static List<Iss_instance> generate_iss_instance_and_case_list(List<Iss_case>issCaseList,SqlMapping sqlMapping,int N,List<SonarRules> sonarRulesList,List<Commit> commitList,List<String> fileList) throws Exception {
        List<Iss_instance> issInstanceList = new ArrayList<>();
        int commit_id_range = 10;
        int iss_per_commit = N/commit_id_range;
        for (int i = 0; i < N; i++) {
            Iss_instance iss_instance = new Iss_instance();
            iss_instance.setInst_id(UUID.nameUUIDFromBytes(String.valueOf(i).getBytes()).toString());
            int rand_rule = (int)(Math.random()*sonarRulesList.size());
            int rand_file = (int)(Math.random()*fileList.size());
            iss_instance.setType_id(sonarRulesList.get(rand_rule).getId());
            iss_instance.setFile_path(fileList.get(rand_file));
            iss_instance.setCommit_id(commitList.get(i/iss_per_commit).getCommit_id());
            issInstanceList.add(iss_instance);
        }
        int case_id = 0;
        for (int j = 0; j < iss_per_commit; j++) {
            String str = (case_id++)+"c";
            String case_hash = UUID.nameUUIDFromBytes(str.getBytes()).toString();
            String parent_inst_id = UUID.nameUUIDFromBytes(String.valueOf(j).getBytes()).toString();

            Iss_case iss_case = new Iss_case();
            iss_case.setCase_id(case_hash);
            iss_case.setCommit_id_new(issInstanceList.get(j).getCommit_id());
            iss_case.setCommit_id_last(issInstanceList.get(j).getCommit_id());
            iss_case.setCase_status("NEW");
            iss_case.setType_id(issInstanceList.get(j).getType_id());
            issInstanceList.get(j).setCase_id(case_hash);
            for (int i = 1; i < commit_id_range; i++) {
                    if (Math.random()>0.95){
                    iss_case.setCommit_id_disappear(issInstanceList.get(i*iss_per_commit+j).getCommit_id());
                    iss_case.setCase_status("SOLVED");
                    issCaseList.add(iss_case);

                    iss_case = new Iss_case();
                    str = (case_id++)+"c";
                    case_hash = UUID.nameUUIDFromBytes(str.getBytes()).toString();
                    issInstanceList.get(i*iss_per_commit+j).setCase_id(case_hash);
                    parent_inst_id = UUID.nameUUIDFromBytes(String.valueOf(i*iss_per_commit+j).getBytes()).toString();

                    iss_case.setCase_id(case_hash);
                    iss_case.setCommit_id_new(issInstanceList.get(i*iss_per_commit+j).getCommit_id());
                    iss_case.setCommit_id_last(issInstanceList.get(i*iss_per_commit+j).getCommit_id());
                    iss_case.setCase_status("NEW");
                    iss_case.setType_id(issInstanceList.get(i*iss_per_commit+j).getType_id());
                } else {
                    iss_case.setCase_status("UNDONE");
                    iss_case.setCommit_id_last(issInstanceList.get(i*iss_per_commit+j).getCommit_id());
                    issInstanceList.get(i*iss_per_commit+j).setCase_id(case_hash);
                    issInstanceList.get(i*iss_per_commit+j).setParent_inst_id(parent_inst_id);
                }
            }
            issCaseList.add(iss_case);
        }
        return issInstanceList;
    }

    public static List<Iss_location> generate_loaction_list(int N){
        List<Iss_location> iss_locationList = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            Iss_location issLocation = new Iss_location();
            issLocation.setStart_line((int)(Math.random()*10));
            issLocation.setEnd_line((int)(Math.random()*10));
            issLocation.setStart_col((int)(Math.random()*10));
            issLocation.setEnd_col((int)(Math.random()*10));
            issLocation.setClass_("null");
            issLocation.setCode("null");
            issLocation.setMethod("null");
            issLocation.setLocation_id(Iss_location.getUuidFromLocation(issLocation));
            iss_locationList.add(issLocation);
        }
        return iss_locationList;
    }

//    public static List<SonarIssues> generate_sonar_issue_list(int N) throws Exception {
//        SqlConnect mysqlConnect = new SqlConnect();
//        mysqlConnect.useDataBase("sonar");
//        SqlMapping sqlMapping = new SqlMapping(mysqlConnect);
//        List<SonarRules> sonarRulesList= (List<SonarRules>) sqlMapping.select(new SonarRules());
//        List<String> fileList = get_file_list();
//        List<SonarIssues> sonarIssuesList = new ArrayList<>();
//
//        for (int i = 0; i < N; i++) {
//            SonarIssues sonarIssues = new SonarIssues();
//            int rand_rule = (int)(Math.random()*sonarRulesList.size());
//            int rand_file = (int)(Math.random()*fileList.size());
//
//            SonarRules sonarRules = sonarRulesList.get(rand_rule);
//            sonarIssues.setMessage(sonarRules.getDescription());
//            sonarIssues.setSeverity(sonarRules.getSeverity());
//            sonarIssues.setTypeId(sonarRules.getId());
//            sonarIssues.setType(sonarRules.getType());
//            sonarIssues.setRepoId("MockRepo"+(i/100));
//            sonarIssues.setFilePath("MockRepo"+(i/100)+":"+fileList.get(rand_file));
//
//            List<SonarLocation> sonarLocations = new ArrayList<>();
//            SonarLocation sonarLocation = new SonarLocation();
//            if(Math.random()<0.9) {
//                for (int j = 0;j<Math.random()*5;j++) {
//                    for (int k = 0; k < 4; k++) {
//                        sonarLocation.setStartLine(String.valueOf((int)(Math.random()*10)));
//                    }
//                    sonarLocation = JSON.parseObject(String.valueOf(((JSONObject)((JSONObject) o).getJSONArray("locations").get(0)).getJSONObject("textRange")), SonarLocation.class);
//                    sonarLocations.add(sonarLocation);
//                }
//                sonarIssueEntity.setLocation(sonarLocations);
//            }else{
//                sonarLocation = JSON.parseObject(String.valueOf(jsonObject.getJSONObject("textRange")),SonarLocation.class);
//                if(sonarLocation != null) sonarLocations.add(sonarLocation);
//                sonarIssueEntity.setLocation(sonarLocations);
//            }
//
//        }
//        return null;
//    }


}
