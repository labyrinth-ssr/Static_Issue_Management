package SonarConfig;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.example.Utils.HTTPUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SonarResult {

    public static List<SonarIssues> getSonarIssues() {
        String result = HTTPUtil.sendGet("http://127.0.0.1:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
        JSONObject sonarData = JSONObject.parseObject(result);
        JSONArray issueRawData = sonarData.getJSONArray("issues");
        List<SonarIssues> sonarIssueEntities = JSON.parseArray(String.valueOf(issueRawData), SonarIssues.class);
        for(int i = 0; i < sonarIssueEntities.size(); i++){
            JSONObject jsonObject = ((JSONObject)issueRawData.get(i));
            SonarIssues sonarIssueEntity = sonarIssueEntities.get(i);
            JSONArray flows = jsonObject.getJSONArray("flows");
            List<SonarLocation> sonarLocations = new ArrayList<>();
            SonarLocation sonarLocation;
            if(flows != null && flows.size() > 0) {
                for (Object o : flows) {
                    sonarLocation = JSON.parseObject(String.valueOf(((JSONObject)((JSONObject) o).getJSONArray("locations").get(0)).getJSONObject("textRange")), SonarLocation.class);
                    sonarLocations.add(sonarLocation);
                }
                sonarIssueEntity.setLocation(sonarLocations);
            }else{
                sonarLocation = JSON.parseObject(String.valueOf(jsonObject.getJSONObject("textRange")),SonarLocation.class);
                if(sonarLocation != null) sonarLocations.add(sonarLocation);
                sonarIssueEntity.setLocation(sonarLocations);
            }
            sonarIssueEntity.setId(getUuidFromSonarIssue(sonarIssueEntity));
        }
        return sonarIssueEntities;
    }

    public static List<SonarRules> getSonartype(){
        String result1 = HTTPUtil.sendGet("http://127.0.0.1:9000/api/qualityprofiles/search?language=java");
        JSONObject sonarConfig = JSONObject.parseObject(result1);
        JSONObject profile = (JSONObject) sonarConfig.getJSONArray("profiles").get(0);
        String key = profile.getString("key");
       // System.out.println(key);

        String result2 = HTTPUtil.sendGet("http://127.0.0.1:9000/api/rules/search?qprofile=" + key + "&activation=true");
        JSONArray sonarRules = JSONObject.parseObject(result2).getJSONArray("rules");
        return JSON.parseArray(String.valueOf(sonarRules), SonarRules.class);
    }



    public static String getUuidFromSonarIssue(SonarIssues sonarIssueEntity){
            List<String> locationBugLines = null;
            if(sonarIssueEntity.getLocation().size() > 0) locationBugLines = sonarIssueEntity.getLocation().stream().map(SonarLocation::getUuid).sorted().collect(Collectors.toList());
            StringBuilder locationStringBuilder = new StringBuilder();
            if(locationBugLines!=null) locationBugLines.forEach(location -> locationStringBuilder.append(location).append("_"));
            String stringBuilder = locationStringBuilder + sonarIssueEntity .getType() + sonarIssueEntity.getFilePath();
            return UUID.nameUUIDFromBytes(stringBuilder.getBytes()).toString();
    }
}
