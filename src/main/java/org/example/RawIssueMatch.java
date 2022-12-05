package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.Entity.Iss_case;
import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarLocation;
import cn.edu.fudan.issue.core.process.RawIssueMatcher;
import cn.edu.fudan.issue.entity.dbo.Location;
import cn.edu.fudan.issue.entity.dbo.RawIssue;
import cn.edu.fudan.issue.util.AnalyzerUtil;
import cn.edu.fudan.issue.util.AstParserUtil;
import org.example.Entity.Commit;
import org.example.Entity.Iss_match;

public class RawIssueMatch {

    private static final String baseRepoPath = System.getProperty("user.dir");

    //private static final String baseRepoPath1 = "C:\\Users\\31324\\Desktop\\ss-backend\\lab2_back-end";
    private static final String baseRepoPath1 = Constant.RepoPath;

    private static final String SEPARATOR = System.getProperty("file.separator");

    private static int case_id = -1;

    public static void firstMatch(List<Iss_match>iss_matchList,List<Iss_case>iss_caseList,List<SonarIssues> issInstanceListCur,Commit curCommit){

        for (SonarIssues iss_instance:issInstanceListCur) {
            Iss_case iss_case = new Iss_case();
            Iss_match iss_match = new Iss_match();

            iss_case.setType_id(iss_instance.getTypeId());
            iss_case.setUpdate_time(curCommit.getCommit_time());
            iss_case.setCase_status("new");
            iss_case.setCommit_hash_new(curCommit.getCommit_hash());
            iss_case.setCommit_hash_last(curCommit.getCommit_hash());
            iss_case.setCommitter_new(curCommit.getCommitter());
            iss_case.setCreate_time(curCommit.getCommit_time());
            iss_case.setCase_id(++case_id);

            iss_match.setParent_inst_id("");
            iss_match.setParent_commit_hash("");
            iss_match.setCase_id(case_id);
            iss_match.setInst_id(iss_instance.getId());
            iss_match.setCommit_hash(curCommit.getCommit_hash());

            iss_matchList.add(iss_match);
            iss_caseList.add(iss_case);
        }
    }

    public static void match(List<Iss_match>iss_matchList,List<Iss_case>iss_caseList,List<SonarIssues> issInstanceListPre, List<SonarIssues> issInstanceListCur, Commit preCommit,Commit curCommit)throws IOException{
        List<RawIssue> preRawIssueList = new ArrayList<>();

        for (SonarIssues instance:issInstanceListPre) {
            RawIssue preRawIssue1 = new RawIssue();
            //直接将inst_id设成rawIssue的uuid
            preRawIssue1.setUuid(String.valueOf(instance.getId()));
            //rawIssue 中的type设为SonarIssue中的typeId
            preRawIssue1.setType(instance.getTypeId());
            preRawIssue1.setDetail(instance.getMessage());
            preRawIssue1.setCommitId(preCommit.getCommit_hash());
            preRawIssue1.setFileName(instance.getFilePath().split(":")[1].replace("/","\\"));
                List<Location> locationList = new ArrayList<>();
                for (SonarLocation location:instance.getLocation()) {
                    Location preLocation1 = new Location();
                    preLocation1.setStartLine(Integer.parseInt(location.getStartLine()));
                    preLocation1.setEndLine(Integer.parseInt(location.getEndLine()));
                    preLocation1.setStartToken(Integer.parseInt(location.getStartOffset()));
                    preLocation1.setEndToken(Integer.parseInt(location.getEndOffset()));
                    preLocation1.setCode("");
                    locationList.add(preLocation1);
                }
                if (locationList.isEmpty()){
                    Location preLocation1 = new Location();
                    preLocation1.setStartLine(0);
                    preLocation1.setEndLine(0);
                    preLocation1.setStartToken(0);
                    preLocation1.setCode("");
                    preRawIssue1.setLocations(Collections.singletonList(preLocation1));
                    System.out.println("empty");
                }
                else {
                    preRawIssue1.setLocations(locationList);
                    System.out.println("not empty");
                }
                preRawIssueList.add(preRawIssue1);
        }
        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath1);

        List<RawIssue> curRawIssueList = new ArrayList<>();

        for (SonarIssues instance:issInstanceListCur) {
            RawIssue curRawIssue1 = new RawIssue();
            curRawIssue1.setUuid(String.valueOf(instance.getId()));
            curRawIssue1.setType(instance.getTypeId());
            curRawIssue1.setFileName(instance.getFilePath().split(":")[1].replace("/","\\"));
            curRawIssue1.setDetail(instance.getMessage());
            curRawIssue1.setCommitId(curCommit.getCommit_hash());
                List<Location> locationList = new ArrayList<>();
                for (SonarLocation location : instance.getLocation()) {
                    Location curLocation1 = new Location();
                    curLocation1.setStartLine(Integer.parseInt(location.getStartLine()));
                    curLocation1.setEndLine(Integer.parseInt(location.getEndLine()));
                    curLocation1.setStartToken(Integer.parseInt(location.getStartOffset()));
                    curLocation1.setEndToken(Integer.parseInt(location.getEndOffset()));
                    curLocation1.setCode("");

                    locationList.add(curLocation1);
                }
                if (locationList.isEmpty()){
                    Location curLocation1 = new Location();
                    curLocation1.setStartLine(0);
                    curLocation1.setEndLine(0);
                    curLocation1.setStartToken(0);
                    curLocation1.setCode("");

                    curRawIssue1.setLocations(Collections.singletonList(curLocation1));
                }
                else {
                    curRawIssue1.setLocations(locationList);
                }
                curRawIssueList.add(curRawIssue1);
        }
        AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, baseRepoPath1);
        RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));

        for (RawIssue curRawIssue:curRawIssueList) {

            Iss_match iss_match = new Iss_match();
            iss_match.setInst_id(curRawIssue.getUuid());
            iss_match.setCommit_hash(curRawIssue.getCommitId());


            if (curRawIssue.getMappedRawIssue() == null){
                Iss_case iss_case = new Iss_case();
                iss_case.setType_id(curRawIssue.getType());
                iss_case.setUpdate_time(curCommit.getCommit_time());

                iss_case.setCase_status("new");
                iss_case.setCommit_hash_new(curRawIssue.getCommitId());
                iss_case.setCommitter_new(curCommit.getCommitter());
                iss_case.setCreate_time(curCommit.getCommit_time());
                iss_case.setCommit_hash_last(curRawIssue.getCommitId());
                iss_case.setCase_id(++case_id);
                //允许没有parent的match，用于和case保持一致
                iss_match.setParent_inst_id("");
                iss_match.setParent_commit_hash("");
                iss_match.setCase_id(case_id);

                iss_caseList.add(iss_case);
            }
            else {
                System.out.println("curRawIssue1:matches " + curRawIssue.getMappedRawIssue().getUuid());

                Iss_case iss_case = Iss_case.look_up_case(iss_caseList,Iss_match.instIdLookUpCaseId(iss_matchList,curRawIssue.getMappedRawIssue().getUuid()));

                iss_match.setParent_inst_id(curRawIssue.getMappedRawIssue().getUuid());
                iss_match.setParent_commit_hash(curRawIssue.getMappedRawIssue().getCommitId());
                iss_case.setCommit_hash_last(curRawIssue.getCommitId());
                iss_case.setCase_status("remain");
                //在match中设置case_id，发现匹配时，在match list中搜索其parentMappedIssue对应的case_id
                iss_match.setCase_id(Iss_match.instIdLookUpCaseId(iss_matchList,curRawIssue.getMappedRawIssue().getUuid()));
            }
            iss_matchList.add(iss_match);
        }

        //对于没有childMap的issue，设置case disappear
        for (RawIssue preRawIssue:preRawIssueList) {
            if (preRawIssue.getMappedRawIssue() == null){
                //通过matchList中的case_id找到对应的case
                Iss_case iss_case = Iss_case.look_up_case(iss_caseList,Iss_match.instIdLookUpCaseId(iss_matchList,preRawIssue.getUuid()));
                if (iss_case==null){
                    System.out.println("case not found in match");
                    continue;
                }
                //直接更新
                iss_case.setCase_status("solved");
                iss_case.setCommit_hash_disappear(preCommit.getCommit_hash());
                iss_case.setCommitter_disappear(preCommit.getCommitter());
            }
        }
    }
}
