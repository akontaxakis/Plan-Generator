package Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Plan {
    private ArrayList<Artifact> Artifacts;
    private int cost;

    public Plan(Plan p1) {
        this.cost = p1.getCost();
        Artifacts =  new ArrayList(p1.getArtifacts());
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }


    public Plan() {
        Artifacts = new ArrayList<>();
        cost = 0;
    }


    public void copy(Artifact a, int cost) {
        if(!Artifacts.contains(a)){
            this.cost = this.cost + cost;
            Artifacts.add(a);

        }
    }
    public ArrayList<Artifact> getArtifacts() {
        return Artifacts;
    }

    public void setArtifacts(ArrayList<Artifact> artifacts) {
        Artifacts = artifacts;
    }

    public void add(Artifact artifact, int i) {
        Artifacts.add(artifact);
    }
}
