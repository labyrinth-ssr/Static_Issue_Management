package org.example.Update;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import org.example.Constant;
import org.example.Entity.Iss_case;
import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarLocation;
import cn.edu.fudan.issue.core.process.RawIssueMatcher;
import cn.edu.fudan.issue.entity.dbo.Location;
import cn.edu.fudan.issue.entity.dbo.RawIssue;
import cn.edu.fudan.issue.util.AnalyzerUtil;
import cn.edu.fudan.issue.util.AstParserUtil;
import org.example.Entity.*;
import org.example.Utils.SqlMapping;

public class RawIssueMatch {

    private static final String baseRepoPath = System.getProperty("user.dir");

    public static List<RawIssue> closedRawIssues = new ArrayList<>();

    //private static final String baseRepoPath1 = "C:\\Users\\31324\\Desktop\\ss-backend\\lab2_back-end";
    private static final String baseRepoPath1 = Constant.RepoPath;

    private static final String SEPARATOR = System.getProperty("file.separator");

    private static int case_id_num = -1;

    public static void firstMatch(List<Iss_location> iss_locationList, List<Iss_match> iss_matchList, List<Iss_case> iss_caseList, List<SonarIssues> issInstanceListCur, Commit curCommit){
        List<RawIssue> preRawIssueList = new ArrayList<>();

        for (SonarIssues iss_instance : issInstanceListCur) {
            Iss_case iss_case = new Iss_case(iss_instance.getTypeId(),curCommit.getCommit_id(),curCommit.getCommit_id(),null,"NEW");
            Iss_match iss_match = new Iss_match(iss_instance.getId(), "", iss_case.getCase_id());

            iss_matchList.add(iss_match);
            iss_caseList.add(iss_case);

            RawIssue preRawIssue1 = newRawIssue(iss_instance.getId(),iss_instance.getTypeId(),iss_instance.getMessage(),curCommit.getCommit_id(),iss_instance.getFilePath().split(":")[1]);
            List<Location> locationList = new ArrayList<>();
            for (SonarLocation location : iss_instance.getLocation())
                locationList.add(newLocation(Integer.parseInt(location.getStartLine()),Integer.parseInt(location.getEndLine()),Integer.parseInt(location.getStartOffset()),Integer.parseInt(location.getEndOffset()),""));

            if (locationList.isEmpty()) preRawIssue1.setLocations(Collections.singletonList(newLocation(0,0,0,0,"")));
            else preRawIssue1.setLocations(locationList);

            preRawIssueList.add(preRawIssue1);
        }

        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath1);

        for (RawIssue i: preRawIssueList) {
            for (Location location: i.getLocations()) {
//                if (preRawIssueList.indexOf(i)==-1) continue;
                Iss_location iss_location = new Iss_location(location.getClassName(),location.getAnchorName(),location.getStartLine(),location.getEndLine(),location.getStartToken(),location.getEndToken(),location.getCode(),i.getFileName());
                iss_locationList.add(iss_location);
            }
        }
    }

    public static void match(List<Iss_location>iss_locationList, List<Iss_match>iss_matchList, List<Iss_case>iss_caseList, List<SonarIssues> issInstanceListPre, List<SonarIssues> issInstanceListCur, Commit preCommit, Commit curCommit)throws IOException {
        List<RawIssue> preRawIssueList = new ArrayList<>();

        for (SonarIssues instance : issInstanceListPre) {
            RawIssue preRawIssue = newRawIssue(instance.getId(), instance.getTypeId(), instance.getMessage(), preCommit.getCommit_id(), instance.getFilePath().split(":")[1]);
            List<Location> locationList = new ArrayList<>();
            for (SonarLocation location : instance.getLocation())
                locationList.add(newLocation(Integer.parseInt(location.getStartLine()), Integer.parseInt(location.getEndLine()), Integer.parseInt(location.getStartOffset()), Integer.parseInt(location.getEndOffset()), ""));
            preRawIssue.setLocations(locationList);
            preRawIssueList.add(preRawIssue);
        }
        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath1);

        List<RawIssue> curRawIssueList = new ArrayList<>();
        for (SonarIssues instance : issInstanceListCur) {
            RawIssue curRawIssue = newRawIssue(instance.getId(), instance.getTypeId(), instance.getMessage(), curCommit.getCommit_id(), instance.getFilePath().split(":")[1]);
            List<Location> locationList = new ArrayList<>();
            for (SonarLocation location : instance.getLocation()) {
                locationList.add(newLocation(Integer.parseInt(location.getStartLine()), Integer.parseInt(location.getEndLine()), Integer.parseInt(location.getStartOffset()), Integer.parseInt(location.getEndOffset()), ""));
            }
            curRawIssue.setLocations(locationList);
            curRawIssueList.add(curRawIssue);
        }
        AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, baseRepoPath1);

        for (RawIssue i : curRawIssueList) {
            for (Location location : i.getLocations()) {
                Iss_location iss_location = new Iss_location(location.getClassName(), location.getAnchorName(), location.getStartLine(), location.getEndLine(), location.getStartToken(), location.getEndToken(), location.getCode(), i.getFileName());
                iss_locationList.add(iss_location);
            }
        }

        RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));
        //对于没有childMap的issue，设置case disappear
        for (RawIssue preRawIssue : preRawIssueList) {
            if (preRawIssue.getMappedRawIssue() == null) {
                Iss_case iss_case = Iss_case.look_up_case(iss_caseList, Iss_match.instIdLookUpCaseId(iss_matchList, preRawIssue.getUuid()));
                if (iss_case == null) continue;
                //直接更新
                setCase(iss_case, "SOLVED", preRawIssue.getCommitId(), curCommit.getCommit_id());
                closedRawIssues.add(preRawIssue);
            } else {
                //找到对应的iss_case
                Iss_case iss_case = Iss_case.look_up_case(iss_caseList, Iss_match.instIdLookUpCaseId(iss_matchList, preRawIssue.getUuid()));
                if (iss_case == null) continue;
                setCase(iss_case, Objects.equals(iss_case.getCase_status(), "REOPEN") ? "REOPEN" : "UNDONE", preRawIssue.getMappedRawIssue().getCommitId(), iss_case.getCommit_id_disappear());
                Iss_match iss_match = new Iss_match(preRawIssue.getMappedRawIssue().getUuid(), preRawIssue.getUuid(), iss_case.getCase_id());
                //在match中设置case_id，发现匹配时，在match list中搜索其parentMappedIssue对应的case_id
                iss_matchList.add(iss_match);
            }
        }

        List<RawIssue> newRawIssueList = new ArrayList<>();

        for (RawIssue curRawIssue : curRawIssueList) {
            if (curRawIssue.getMappedRawIssue() == null) {
                newRawIssueList.add(curRawIssue);
            }
        }

        RawIssueMatcher.match(newRawIssueList, closedRawIssues, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));
        //对没匹配上的curRawIssue，检查reopen
        for (RawIssue newRawIssue : newRawIssueList) {
            Iss_match iss_match = new Iss_match();
            iss_match.setInst_id(newRawIssue.getUuid());
            if (newRawIssue.getMappedRawIssue() != null) {
                RawIssue mappedIssue = newRawIssue.getMappedRawIssue();
                Iss_case iss_case = Iss_case.look_up_case(iss_caseList, Iss_match.instIdLookUpCaseId(iss_matchList, mappedIssue.getUuid()));
                if (iss_case == null) continue;
                setCase(iss_case, "REOPEN", newRawIssue.getCommitId(), iss_case.getCommit_id_disappear());
                setMatch(iss_match, mappedIssue.getUuid(), iss_case.getCase_id());
                iss_matchList.add(iss_match);
//                System.out.println("iss close:"+"case id:"+iss_match.getCase_id()+"inst id:"+iss_match.getInst_id()+iss_match.getParent_inst_id());
            } else {
                Iss_case iss_case = new Iss_case(newRawIssue.getType(), newRawIssue.getCommitId(), newRawIssue.getCommitId(), null, "NEW");
                //允许没有parent的match，用于和case保持一致
                setMatch(iss_match, "", iss_case.getCase_id());
                iss_caseList.add(iss_case);
                iss_matchList.add(iss_match);
            }
        }
    }


    /**
     * 从数据库中提取出所有最新issue信息，包括case_id, case_commit_last, case_status, 对应case_commit_last的inst信息，主要是location信息
     * 最后需要更新的内容：
     * 要更新的case的case状态，last_commit, disappear_commit
     * 新的instance，和parent_instance,
     * 新增的location信息
     * commit信息
     * */
    public static void myMatch(SqlMapping sqlMapping, List<Matches> matches, HashMap<String,SonarRules> rulesHash, List<SonarIssues> issInstanceListCur, Commit curCommit, boolean batch) throws SQLException, IOException {
        List<Iss_case> caseListUpdate = new ArrayList<>();
        List<Iss_location> locationList = new ArrayList<>();
        List<Instance_location> instanceLocationList = new ArrayList<>();
        List<Iss_case> caseList = new ArrayList<>();
        List<Iss_instance> instanceList = new ArrayList<>();
        List<SonarRules> sonarRules = new ArrayList<>();
        HashMap<String, Matches> hashMap = new HashMap<>();

        List<RawIssue> preRawIssueList = new ArrayList<>();
        matches.forEach(match->{
            Match_Info matchInfo = match.getInfo();
            List<Iss_location> locations = match.getLocation();
            RawIssue preRawIssue = newRawIssue(matchInfo.getInst_id_last(),matchInfo.getType_id(),matchInfo.getMessage(),matchInfo.getCommit_id_last(), matchInfo.getFile_name());
            List<Location> tmplocationList = new ArrayList<>();
            locations.forEach(location -> tmplocationList.add(newLocation(location.getStart_line(),location.getEnd_line(),location.getStart_col(),location.getEnd_col(),"")));
            preRawIssue.setLocations(tmplocationList);
            preRawIssueList.add(preRawIssue);
            hashMap.put(matchInfo.getInst_id_last(), match);
        });
        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath1);

        List<RawIssue> curRawIssueList = getRawIssues(issInstanceListCur, curCommit, rulesHash, sonarRules);

        Map<String, List<RawIssue>> preRawIssueMap = preRawIssueList.stream().collect(Collectors.groupingBy(RawIssue::getFileName));
        Map<String, List<RawIssue>> curRawIssueMap = curRawIssueList.stream().collect(Collectors.groupingBy(RawIssue::getFileName));

        Set<String> keyset = curRawIssueMap.keySet();
        for(String file_path : keyset) {
            List<RawIssue> rawIssues = preRawIssueMap.get(file_path);
            if(rawIssues!=null) {
                RawIssueMatcher.match(rawIssues, curRawIssueMap.get(file_path), AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath1 + "/" + file_path));
            }
        }
        //对于没有childMap的issue，设置case disappear
        for (RawIssue curRawIssue : curRawIssueList) {
            Iss_case iss_case;
            List<Iss_location> locations = new ArrayList<>();
            if (curRawIssue.getMappedRawIssue() == null){
                iss_case = new Iss_case(curRawIssue.getType(),curCommit.getCommit_id(),curCommit.getCommit_id(),null,"NEW");
                caseList.add(iss_case);
                Matches matches_ = newMatches(iss_case, curRawIssue, locations);
//                hashMap.put(iss_case.getCase_id(), matches_);
                matches.add(matches_);
            }else{
                System.out.println("ssssssssssssssssss================================");
                String case_status = hashMap.get(curRawIssue.getMappedRawIssue().getUuid()).getInfo().getCase_status();
                if(case_status.equals("SOLVED")) case_status = "REOPEN";
                if(case_status.equals("NEW")) case_status = "UNDONE";
                iss_case = new Iss_case(curRawIssue.getMappedRawIssue().getIssueId(), curRawIssue.getType(), curCommit.getCommit_id(), curCommit.getCommit_id(), hashMap.get(curRawIssue.getIssueId()).getInfo().commit_id_disappear, case_status);
                caseListUpdate.add(iss_case);
                Matches matches_ = hashMap.get(curRawIssue.getMappedRawIssue().getUuid());
                setMatches(matches_, iss_case, curRawIssue, locations);
            }
            curRawIssue.getLocations().forEach(location -> {
                Iss_location issLocation = newIssLocation(location);
                locationList.add(issLocation);
                instanceLocationList.add(new Instance_location(curRawIssue.getUuid(),issLocation.getLocation_id()));
            });
            instanceList.add(new Iss_instance(curRawIssue.getUuid(),curRawIssue.getType(), curCommit.getCommit_id(),curRawIssue.getMappedRawIssue() == null ? null:curRawIssue.getMappedRawIssue().getUuid(), iss_case.getCase_id(), curRawIssue.getFileName()));
        }

        for (RawIssue preRawIssue:preRawIssueList) {
            if (preRawIssue.getMappedRawIssue() == null && !hashMap.get(preRawIssue.getUuid()).getInfo().case_status.equals("SOLVED")){
                Iss_case iss_case = new Iss_case(preRawIssue.getIssueId(),preRawIssue.getType(), null, hashMap.get(preRawIssue.getUuid()).getInfo().commit_id_last, curCommit.getCommit_id(), "SOLVED");
                caseListUpdate.add(iss_case);
                Matches matches_ = hashMap.get(preRawIssue.getUuid());
                setMatchesPre(matches_, iss_case, preRawIssue);
            }else if(preRawIssue.getMappedRawIssue()!=null){
                System.out.println("=========================");
            }
        }
        sqlMapping.execute("start transaction");
        try {
            sqlMapping.save(Collections.singletonList(curCommit));
            sqlMapping.save(sonarRules);
//            System.out.println(caseListUpdate.toString());
            sqlMapping.updateCase(caseListUpdate);
            sqlMapping.save(caseList);
            sqlMapping.save(instanceList);
//            System.out.println(locationList.toString());
            sqlMapping.save(locationList);
            sqlMapping.save(instanceLocationList);
            sqlMapping.execute("commit");
        }catch (Exception e){
            sqlMapping.execute("rollback");
            e.printStackTrace();
        }
    }

    public static List<RawIssue> getRawIssues(List<SonarIssues> sonarIssues, Commit curCommit, HashMap<String, SonarRules> sonarHash, List<SonarRules> sonarRules){
        List<RawIssue> curRawIssueList = new ArrayList<>();
        for (SonarIssues instance : sonarIssues) {
            String type_id = instance.getTypeId();
            if(sonarHash.get(type_id) == null){
                SonarRules sonarRule = new SonarRules(type_id, instance.getMessage(), instance.getSeverity(), "java", instance.getType());
                sonarHash.put(type_id, sonarRule);
                sonarRules.add(sonarRule);
            }
//            System.out.println(sonarIssues.toString());
            RawIssue curRawIssue = newRawIssue(instance.getId(),instance.getTypeId(),instance.getMessage(),curCommit.getCommit_id(), instance.getFilePath().split(":")[1]);
            List<Location> tmplocationList = new ArrayList<>();
            for (SonarLocation location : instance.getLocation()) {
                tmplocationList.add(newLocation(Integer.parseInt(location.getStartLine()),Integer.parseInt(location.getEndLine()),Integer.parseInt(location.getStartOffset()),Integer.parseInt(location.getEndOffset()),""));
            }
            curRawIssue.setLocations(tmplocationList);
            curRawIssueList.add(curRawIssue);
        }
        AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, baseRepoPath1);
//        curRawIssueList.forEach(rawIssue -> System.out.println(rawIssue.getLocations().toString()));
        return curRawIssueList;
    }

    public static Location newLocation(Integer startLine, Integer endLine, Integer startToken, Integer endToken, String code){
        Location location = new Location();
        location.setStartLine(startLine);
        location.setEndLine(endLine);
        location.setStartToken(startToken);
        location.setEndToken(endToken);
        location.setCode(code);
        return location;
    }

    public static Location newLocation(Integer startLine, Integer endLine, Integer startToken, Integer endToken){
        Location location = new Location();
        location.setStartLine(startLine);
        location.setEndLine(endLine);
        location.setStartToken(startToken);
        location.setEndToken(endToken);
        return location;
    }

    public static Location newLocation(String location_id, Integer startLine, Integer endLine, Integer startToken, Integer endToken, String code, String class_, String method){
        Location location = new Location();
        location.setStartLine(startLine);
        location.setEndLine(endLine);
        location.setStartToken(startToken);
        location.setEndToken(endToken);
        location.setCode(code);
        location.setClassName(class_);
        location.setOffset(0);
        location.setAnchorName(method);
        location.setUuid(location_id);
        return location;
    }

    public static Iss_location newIssLocation(Location l){
        return new Iss_location(l.getClassName(), l.getAnchorName(), l.getStartLine(), l.getEndLine(), l.getStartToken(), l.getEndToken(), l.getCode(), l.getFilePath());
    }

    public static void setMatch(Iss_match iss_match, String parent_inst_id, String case_id){
        iss_match.setParent_inst_id(parent_inst_id);
        iss_match.setCase_id(case_id);
    }

    public static void setCase(Iss_case iss_case, String status, String commit_id_last, String commit_id_disappear){
        iss_case.setCase_status(status);
        iss_case.setCommit_id_last(commit_id_last);
        iss_case.setCommit_id_disappear(commit_id_disappear);
    }

    public static RawIssue newRawIssue(String uuid, String type, String detail, String commit_id, String fileName){
        RawIssue rawIssue = new RawIssue();
        rawIssue.setUuid(uuid);
        rawIssue.setType(type);
        rawIssue.setDetail(detail);
        rawIssue.setCommitId(commit_id);
        rawIssue.setFileName(fileName);
        return  rawIssue;
    }

    public static Matches newMatches(Iss_case iss_case, RawIssue rawIssue, List<Iss_location> locations){
        Match_Info matchInfo = new Match_Info(iss_case.getCase_id(), iss_case.getCase_status(), iss_case.getCommit_id_last(),iss_case.getCommit_id_disappear(),rawIssue.getUuid(),rawIssue.getType(),rawIssue.getDetail(),rawIssue.getFileName());
        return new Matches(matchInfo, locations);
    }
    public static void setMatches(Matches match, Iss_case iss_case, RawIssue rawIssue, List<Iss_location> locations){
        Match_Info matchInfo = new Match_Info(iss_case.getCase_id(), iss_case.getCase_status(), iss_case.getCommit_id_last(),iss_case.getCommit_id_disappear(),rawIssue.getUuid(),rawIssue.getType(),rawIssue.getDetail(),rawIssue.getFileName());
        match.setInfo(matchInfo);
        match.setLocation(locations);
    }

    public static void setMatchesPre(Matches match, Iss_case iss_case, RawIssue rawIssue){
        Match_Info matchInfo = new Match_Info(iss_case.getCase_id(), iss_case.getCase_status(), iss_case.getCommit_id_last(),iss_case.getCommit_id_disappear(),rawIssue.getUuid(),rawIssue.getType(),rawIssue.getDetail(),rawIssue.getFileName());
        match.setInfo(matchInfo);
    }
}
