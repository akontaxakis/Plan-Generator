package HyperGraph;

import Entities.Artifact;

import java.util.ArrayList;

public class  Proposition {
    private ArrayList<Artifact> artifacts;
    private Integer cost;



    private int latency;
    public Proposition(int sum, ArrayList<Artifact> new_current) {
        this.cost = sum;
        this.artifacts = new ArrayList<>(new_current);
    }
    public Proposition(int max, int sum, ArrayList<Artifact> new_current) {
        this.cost = sum;
        this.artifacts = new ArrayList<>(new_current);
        this.latency = max;
    }


    public ArrayList<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(ArrayList<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public void print() {
        System.out.println("Proposition Cost: "+ cost);
        for(Artifact a: artifacts){
            System.out.println(a.print());
        }
    }

    public boolean isROOT() {
        for(Artifact a: artifacts){
            if(!a.isROOT())
                return false;
        }
        return true;
    }

    public void set_tmp_Depth(int i) {
        for(Artifact a: artifacts){
            a.setTmp_depth(i);
        }
    }

    public void setLatency(int i) {
        for(Artifact a: artifacts){
            a.setLatency_cost(i);
        }
    }
    public int getLatency() {
        return latency;
    }
}
