package Entities;

import java.util.*;

public class Artifact {

    private String id;
    private int position;
    private int additiveCost=0;
    private Artifact.NodeType type;
    private boolean materialized;
    private int loadCost;
    private boolean load = false;
    private int recreation_cost=0;
    private int latency_cost =0;
    private int computeCost=0;
    private int depth = 10000000;



    private int tmp_depth = 0;
    private int heuristic_cost=0;
    private ArrayList<HyperEdge> IN;
    private ArrayList<HyperEdge> OUT;

    public Artifact(Artifact current, ArrayList<Artifact> in, int c) {
        this.id = current.getId();
        position = current.getPosition();
        this.IN = new ArrayList<>();
        this.addIN(new HyperEdge(in,this,c));
    }


    public String getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }



    public Artifact(Artifact current, Map.Entry<NArtifact, Integer> parent) {
        id=current.getId();
        position=current.getPosition();
        type=current.getType();
        materialized=false;
        loadCost =0;
        HashMap<NArtifact, Integer> Parents = new HashMap<>();
        Parents.put(parent.getKey(),parent.getValue());
        HashMap<NArtifact, Integer> Children=null;
    }

    public Artifact(Artifact.NodeType type, int pos, String uid) {
        this.id = uid;
        this.position = pos;
        this.type = type;
        this.materialized = false;
        this.loadCost = 0;
        IN = new ArrayList<>();
        OUT = new ArrayList<>();

    }

    public Artifact(Artifact.NodeType type, int pos) {
        this.id = UUID.randomUUID().toString();
        this.position = pos;
        this.type = type;
        this.materialized = false;
        this.loadCost = 0;
        IN = new ArrayList<>();
        OUT = new ArrayList<>();

    }
    public Artifact(Artifact.NodeType type, int pos, int depth) {
        this.depth = depth;
        this.id = UUID.randomUUID().toString();
        this.position = pos;
        this.type = type;
        this.materialized = false;
        this.loadCost = 0;
        IN = new ArrayList<>();
        OUT = new ArrayList<>();

    }

    @Override
    public String toString() {

            return
                    ", position=" + position +
                    ", type=" + type +
                    ", materialized=" + materialized +
                    ", IN=" + IN.size() +
                     " OUT=" + IN.size() +
                    '}';
    }

    public void addIN(HyperEdge a){
        if(a.getDepth() + 1 < depth) {
            depth = a.getDepth() + 1;
            tmp_depth = depth;
        }
        IN.add(a);
    }
    public void addOUT(HyperEdge a){
        OUT.add(a);
    }
    public void removeIN(HyperEdge a){
        IN.remove(a);
    }
    public void removeOUT(HyperEdge a){
        OUT.remove(a);
    }


    public boolean isINTERMEDIATE() {
        return this.type == NodeType.INTERMEDIATE;
    }

    public boolean isREQUEST() {
        return  this.type == NodeType.REQUEST;
    }

    public boolean isROOT() {
        return  this.type == NodeType.ROOT;
    }

    public int getINsize() {
        return IN.size();
    }

    public ArrayList<HyperEdge> getIN() {
        return IN;
    }

    public String print() {
        return
                "position=" + position +
                        ", NAME=" + id +
                        ", shared_by=" + OUT.size() +
                        ", type=" + type;
    }

    public ArrayList<Artifact> getIN(int i) {
        return this.getIN().get(i).getIN();
    }

    public int get_heuristic_cost(int default_cost) {
        heuristic_cost = depth*default_cost;
        return heuristic_cost;
    }

    public int getDepth() {
        return depth;
    }

    public int getLatency_cost() {
        return latency_cost;
    }

    public void setLatency_cost(int latency_cost) {
        this.latency_cost = latency_cost;
    }

    public HyperEdge getLoadEgde() {
        for(HyperEdge e: IN){
            if(e.getIN().get(0).isROOT())
                return e;
        }
        return null;
    }

    public HyperEdge getComputeEgde() {
        for(HyperEdge e: IN){
            if(!e.getIN().get(0).isROOT())
                return e;
        }
        return null;
    }

    public ArrayList<Artifact>  getComputeIN() {
        for(HyperEdge e: IN){
            if(!e.getIN().get(0).isROOT())
                return e.getIN();
        }
        return new ArrayList<>();
    }

    public int current_latency(ArrayList<Artifact> already_expanded) {

        int min = 10000;
        for(HyperEdge e: IN){
            if(min< e.getCost()+e.getInput_latency_current_latency(already_expanded)){
                min =e.getCost()+e.getInput_latency_current_latency(already_expanded);
            }
        }
        return min;
    }



    public enum NodeType {
        ROOT,
        INTERMEDIATE,
        REQUEST;

        NodeType() {
        }
    }

    public static class SortbyPOS implements Comparator<Artifact> {
        // Method
        // Sorting in ascending order of roll number
        @Override
        public int compare(Artifact o1, Artifact o2) {
            return o1.getPosition()-o2.getPosition();
        }
    }

    public int getAdditiveCost() {
        return additiveCost;
    }
    public void setAdditiveCost(int additiveCost) {
        this.additiveCost = additiveCost;
    }
    public void setType(NodeType type) {
        this.type = type;
    }
    public int getPosition() {
        return position;
    }
    public int getLoadCost() {
        return loadCost;
    }
    public void setLoadCost(int loadCost) {
        this.loadCost = loadCost;
    }
    public int getComputeCost() {
        return computeCost;
    }
    public void setComputeCost(int computeCost) {
        this.computeCost = computeCost;
    }

    public boolean isLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public int getRecreation_cost() {
        return recreation_cost;
    }
    public void setRecreation_cost(int recreation_cost) {
        this.recreation_cost = recreation_cost;
    }

    public int getTmp_depth() {
        return tmp_depth;
    }

    public void setTmp_depth(int tmp_depth) {
        this.tmp_depth = tmp_depth;
    }

    public ArrayList<HyperEdge> getOUT() {
        return OUT;
    }

    public void setOUT(ArrayList<HyperEdge> OUT) {
        this.OUT = OUT;
    }

}
