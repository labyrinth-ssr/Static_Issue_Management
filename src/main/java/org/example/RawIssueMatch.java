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
import org.apache.commons.lang.ObjectUtils;
import org.example.Entity.*;

public class RawIssueMatch {

    private static final String baseRepoPath = System.getProperty("user.dir");

    public static List<RawIssue> closedRawIssues = new ArrayList<>();

    //private static final String baseRepoPath1 = "C:\\Users\\31324\\Desktop\\ss-backend\\lab2_back-end";
    private static final String baseRepoPath1 = Constant.RepoPath;

    private static final String SEPARATOR = System.getProperty("file.separator");

    private static int case_id_num = -1;

    public static void firstMatch(List<Iss_location>iss_locationList,List<Iss_match>iss_matchList,List<Iss_case>iss_caseList,List<SonarIssues> issInstanceListCur,Commit curCommit){
        List<RawIssue> preRawIssueList = new ArrayList<>();

        for (SonarIssues iss_instance:issInstanceListCur) {
            Iss_case iss_case = new Iss_case();
            Iss_match iss_match = new Iss_match();

            iss_case.setType_id(iss_instance.getTypeId());
            iss_case.setCase_status("NEW");
            iss_case.setCommit_id_new(curCommit.getCommit_id());
            iss_case.setCommit_id_last(curCommit.getCommit_id());
            String case_id = Iss_case.getUuidFromIssueCase(iss_case,++case_id_num);
            iss_case.setCase_id(case_id);

            iss_match.setParent_inst_id("");
            iss_match.setCase_id(case_id);
            iss_match.setInst_id(iss_instance.getId());

            iss_matchList.add(iss_match);
            iss_caseList.add(iss_case);

            RawIssue preRawIssue1 = new RawIssue();
            //直接将inst_id设成rawIssue的uuid
            preRawIssue1.setUuid(String.valueOf(iss_instance.getId()));
            //rawIssue 中的type设为SonarIssue中的typeId
            preRawIssue1.setType(iss_instance.getTypeId());
            preRawIssue1.setDetail(iss_instance.getMessage());
            preRawIssue1.setCommitId(curCommit.getCommit_id());
            preRawIssue1.setFileName(iss_instance.getFilePath().split(":")[1]);
            List<Location> locationList = new ArrayList<>();
            for (SonarLocation location:iss_instance.getLocation()) {
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
            }
            else {
                preRawIssue1.setLocations(locationList);
            }
            preRawIssueList.add(preRawIssue1);

        }

        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath1);

        for (RawIssue i: preRawIssueList) {
            for (Location location: i.getLocations()) {
//                if (preRawIssueList.indexOf(i)==-1) continue;
                Iss_location iss_location = new Iss_location();
                iss_location.setMethod(location.getAnchorName());
                iss_location.setClass_(location.getClassName());
                iss_location.setCode(location.getCode());
                iss_location.setFile_path(i.getFileName());
                iss_location.setStart_line(location.getStartLine());
                iss_location.setEnd_line(location.getEndLine());
                iss_location.setStart_col(location.getStartToken());
                iss_location.setEnd_col(location.getEndToken());
                iss_location.setLocation_id(Iss_location.getUuidFromLocation(iss_location));
                iss_locationList.add(iss_location);
            }
        }
    }

    public static void match(List<Iss_location>iss_locationList, List<Iss_match>iss_matchList, List<Iss_case>iss_caseList, List<SonarIssues> issInstanceListPre, List<SonarIssues> issInstanceListCur, Commit preCommit, Commit curCommit)throws IOException{
        List<RawIssue> preRawIssueList = new ArrayList<>();

        for (SonarIssues instance:issInstanceListPre) {
            RawIssue preRawIssue1 = new RawIssue();
            //直接将inst_id设成rawIssue的uuid
            preRawIssue1.setUuid(String.valueOf(instance.getId()));
            preRawIssue1.setType(instance.getTypeId());
            preRawIssue1.setDetail(instance.getMessage());
            preRawIssue1.setCommitId(preCommit.getCommit_id());
            preRawIssue1.setFileName(instance.getFilePath().split(":")[1]);
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
                }
                else {
                    preRawIssue1.setLocations(locationList);
                }
                preRawIssueList.add(preRawIssue1);
        }

        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath1);
        List<RawIssue> curRawIssueList = new ArrayList<>();

        for (SonarIssues instance:issInstanceListCur) {
            RawIssue curRawIssue1 = new RawIssue();

            curRawIssue1.setUuid(String.valueOf(instance.getId()));
            curRawIssue1.setType(instance.getTypeId());
            curRawIssue1.setFileName(instance.getFilePath().split(":")[1]);
            curRawIssue1.setDetail(instance.getMessage());
            curRawIssue1.setCommitId(curCommit.getCommit_id());
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

        for (RawIssue i: curRawIssueList) {
            for (Location location: i.getLocations()) {
//                if (curRawIssueList.indexOf(i)==-1) continue;
                Iss_location iss_location = new Iss_location();
                iss_location.setMethod(location.getAnchorName());
                iss_location.setClass_(location.getClassName());
                iss_location.setCode(location.getCode());
                iss_location.setFile_path(i.getFileName());
                iss_location.setStart_line(location.getStartLine());
                iss_location.setEnd_line(location.getEndLine());
                iss_location.setStart_col(location.getStartToken());
                iss_location.setEnd_col(location.getEndToken());
                iss_location.setLocation_id(Iss_location.getUuidFromLocation(iss_location));

                iss_locationList.add(iss_location);
            }
        }

        for (RawIssue pre:preRawIssueList) {
            if (pre.getCommitId()==null){
                System.out.println(pre);
            }
        }
        for (RawIssue cur:curRawIssueList) {
            if (cur.getCommitId()==null){
                System.out.println(cur);
            }
        }

        RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));


        //对于没有childMap的issue，设置case disappear
        for (RawIssue preRawIssue:preRawIssueList) {
            if (preRawIssue.getMappedRawIssue() == null){
//                Iss_match iss_match = new Iss_match();
//                iss_match.setParent_inst_id(preRawIssue.getUuid());
//                iss_match.setInst_id("");
                Iss_case iss_case = Iss_case.look_up_case(iss_caseList,Iss_match.instIdLookUpCaseId(iss_matchList,preRawIssue.getUuid()));
                //直接更新
                if (iss_case==null){
                    System.out.println("pre raw issue:"+preRawIssue.getUuid()+" not in case list");
                    continue;
                }
//                iss_match.setCase_id(iss_case.getCase_id());
                iss_case.setCase_status("SOLVED");
                iss_case.setCommit_id_last(preRawIssue.getCommitId());
                iss_case.setCommit_id_disappear(preRawIssue.getCommitId());
//                System.out.println("iss close:"+"case id:"+iss_match.getCase_id()+"inst id:"+iss_match.getInst_id()+iss_match.getParent_inst_id());
//                iss_matchList.add(iss_match);
                closedRawIssues.add(preRawIssue);
            }
        }

        List<RawIssue> newRawIssueList = new ArrayList<>();

        for (RawIssue curRawIssue:curRawIssueList) {
            if (curRawIssue.getMappedRawIssue() == null){
//                Iss_case iss_case = new Iss_case();
//                iss_case.setType_id(curRawIssue.getType());
//                iss_case.setCase_status("NEW");
//
//                iss_case.setCommit_id_new(curRawIssue.getCommitId());
//                iss_case.setCommit_id_last(curRawIssue.getCommitId());
//                String case_id = Iss_case.getUuidFromIssueCase(iss_case,++case_id_num);
//                iss_case.setCase_id(case_id);
//                System.out.println("iss case new:"+iss_case.getCase_id()+","+iss_case.getType_id()+","+iss_case.getCommit_id_new()+","+iss_case.getCommit_id_last()+","+iss_case.getCase_status());
//                //允许没有parent的match，用于和case保持一致
//                iss_match.setParent_inst_id("");
//                iss_match.setCase_id(case_id);
//
//                iss_caseList.add(iss_case);
                newRawIssueList.add(curRawIssue);
            }
            else {
//                System.out.println("curRawIssue1:matches " + curRawIssue.getMappedRawIssue().getUuid());
                Iss_match iss_match = new Iss_match();
                iss_match.setInst_id(curRawIssue.getUuid());

                Iss_case iss_case = Iss_case.look_up_case(iss_caseList,Iss_match.instIdLookUpCaseId(iss_matchList,curRawIssue.getMappedRawIssue().getUuid()));

                iss_match.setParent_inst_id(curRawIssue.getMappedRawIssue().getUuid());
                iss_case.setCommit_id_last(curRawIssue.getCommitId());
                iss_case.setCase_status("NONCHG");
                //在match中设置case_id，发现匹配时，在match list中搜索其parentMappedIssue对应的case_id
                iss_match.setCase_id(Iss_match.instIdLookUpCaseId(iss_matchList,curRawIssue.getMappedRawIssue().getUuid()));
                iss_matchList.add(iss_match);

            }
        }

        RawIssueMatcher.match(newRawIssueList, closedRawIssues, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));

        //对没匹配上的curRawIssue，检查reopen
        for (RawIssue newRawIssue:newRawIssueList) {

            Iss_match iss_match = new Iss_match();
            iss_match.setInst_id(newRawIssue.getUuid());

            if (newRawIssue.getMappedRawIssue() != null){
                RawIssue mappedIssue = newRawIssue.getMappedRawIssue();
                iss_match.setParent_inst_id(mappedIssue.getUuid());
                Iss_case iss_case = Iss_case.look_up_case(iss_caseList,Iss_match.instIdLookUpCaseId(iss_matchList,mappedIssue.getUuid()));
                iss_match.setCase_id(iss_case.getCase_id());
                iss_case.setCase_status("REOPEN");
                iss_case.setCommit_id_last(newRawIssue.getCommitId());
//                System.out.println("iss close:"+"case id:"+iss_match.getCase_id()+"inst id:"+iss_match.getInst_id()+iss_match.getParent_inst_id());
                iss_matchList.add(iss_match);
            } else {
                Iss_case iss_case = new Iss_case();
                iss_case.setType_id(newRawIssue.getType());
                iss_case.setCase_status("NEW");
                iss_case.setCommit_id_new(newRawIssue.getCommitId());
                iss_case.setCommit_id_last(newRawIssue.getCommitId());
                String case_id = Iss_case.getUuidFromIssueCase(iss_case,++case_id_num);
                iss_case.setCase_id(case_id);
                System.out.println("iss case new:"+iss_case.getCase_id()+","+iss_case.getType_id()+","+iss_case.getCommit_id_new()+","+iss_case.getCommit_id_last()+","+iss_case.getCase_status());
                //允许没有parent的match，用于和case保持一致
                iss_match.setParent_inst_id("");
                iss_match.setCase_id(case_id);
                System.out.println("add inst:");
                iss_caseList.add(iss_case);
                iss_matchList.add(iss_match);
            }
        }
    }

}
