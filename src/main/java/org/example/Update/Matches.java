package org.example.Update;

import org.example.Entity.Iss_location;

import java.util.List;

public class Matches {
    Match_Info info;
    List<Iss_location> location;

    public Match_Info getInfo() {
        return info;
    }

    public void setInfo(Match_Info info) {
        this.info = info;
    }

    public List<Iss_location> getLocation() {
        return location;
    }

    public void setLocation(List<Iss_location> location) {
        this.location = location;
    }

    public Matches(Match_Info info, List<Iss_location> location) {
        this.info = info;
        this.location = location;
    }
}
