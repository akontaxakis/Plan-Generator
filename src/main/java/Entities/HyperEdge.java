package Entities;

import Entities.Artifact;
import HyperGraph.HyperGraph;

import java.util.ArrayList;

public class HyperEdge {


    private ArrayList<Artifact> IN;
    private ArrayList<Artifact> OUT;
    private int cost;
    private int input_latency=0;

    public HyperEdge(int cost) {
        this.IN = new ArrayList<>();
        this.OUT = new ArrayList<>();
        this.cost = cost;
    }

    public HyperEdge(Artifact r, Artifact a, int cost){
        input_latency = r.getLatency_cost();
        this.IN = new ArrayList<>();
        IN.add(r);
        this.OUT = new ArrayList<>();
        OUT.add(a);
        this.cost = cost;
    }

    public HyperEdge(ArrayList<Artifact> al, Artifact a, int cost) {
        input_latency = max_latency(al);
        this.IN = al;
        this.OUT = new ArrayList<>();
        OUT.add(a);
        this.cost = cost;
    }

    private int max_latency(ArrayList<Artifact> al) {
        int max =0;
        for(Artifact a:al){
            if(a.getLatency_cost()>max){
                max = a.getLatency_cost();
            }
        }
        return max;
    }

    public HyperEdge(ArrayList<Artifact> in,ArrayList<Artifact> out, int c) {
        input_latency = max_latency(in);
        this.cost =c;
        this.IN = new ArrayList<>();
        IN.addAll(in);
        this.OUT = new ArrayList<>();
        this.OUT.addAll(out);
    }

    public HyperEdge(ArrayList<Artifact> t_in, boolean add, int i) {
    }

    public void addIN(Artifact a){
          IN.add(a);
    }
    public void addOUT(Artifact a){
        OUT.add(a);
    }
    public void removeIN(Artifact a){
        IN.remove(a);
    }
    public void removeOUT(Artifact a){
        OUT.remove(a);
    }

    public String toString(){
        String str = "IN:";
        for(Artifact a: IN){
            str = str+a.getId()+"!";

        }
        str=str+" OUT:";
        for(Artifact a: OUT){
            str = str+a.getId();

        }
        str=str+" COST:"+this.cost;
        return str;
    }
    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }

    public  ArrayList<Artifact> getIN() {
        return IN;
    }

    public int getINsize() {
        return IN.size();
    }

    public void setIN(ArrayList<Artifact> IN) {
        this.IN = IN;
    }

    public ArrayList<Artifact> getOUT() {
        return OUT;
    }

    public void setOUT(ArrayList<Artifact> OUT) {
        this.OUT = OUT;
    }

    public int getDepth() {
        int min =1000000000;
        for(Artifact a: IN){
            if(a.getDepth()<min){
                min=a.getDepth();
            }
        }
        return min;
    }

    public int getInput_latency() {
        this.input_latency = max_latency(this.IN);
        return this.input_latency;
    }

    public void setInput_latency(int input_latency) {
        this.input_latency = input_latency;
    }

    public int get_recreation_cost() {
        return recreation_cost(this.IN);
    }

    private int recreation_cost(ArrayList<Artifact> al) {
        int sum =0;
        for(Artifact a:al){
                sum = sum + a.getRecreation_cost();
        }
        return sum;
    }

    public int getInput_latency_current_latency(ArrayList<Artifact> already_expanded) {
        this.input_latency = max_latency(this.IN,already_expanded);
        return this.input_latency;
    }

    private int max_latency(ArrayList<Artifact> in, ArrayList<Artifact> already_expanded) {
        int max =0;
        for(Artifact a:in){
            if(!already_expanded.contains(a)){
                if(a.current_latency(already_expanded)>max){
                    max = a.current_latency(already_expanded);
                }
            }
        }
        return max;
    }
}
