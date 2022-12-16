package org.example.Entity;

import SonarConfig.SonarIssues;
import SonarConfig.SonarLocation;

import java.util.List;

public class Instance_location {
    String inst_id;
    String location_id;

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getInst_id() {
        return inst_id;
    }

    public void setInst_id(String inst_id) {
        this.inst_id = inst_id;
    }

    public static void  setInstanceLocation(List<SonarIssues> sonarIssues, List<Instance_location> instance_locations){
        for (SonarIssues sonarIssue:sonarIssues) {
            for (SonarLocation location:sonarIssue.getLocation()) {
                Iss_location iss_location = new Iss_location();
                Instance_location instance_location = new Instance_location();

                iss_location.setStart_line(Integer.parseInt(location.getStartLine()));
                iss_location.setEnd_line(Integer.parseInt(location.getEndLine()));
                iss_location.setStart_col(Integer.parseInt(location.getStartOffset()));
                iss_location.setEnd_col(Integer.parseInt(location.getEndOffset()));
                iss_location.setFile_path(sonarIssue.getFilePath().split(":")[1]);

                instance_location.setInst_id(sonarIssue.getId());
                instance_location.setLocation_id(Iss_location.getUuidFromLocation(iss_location));
                instance_locations.add(instance_location);
            }
        }
    }
}
