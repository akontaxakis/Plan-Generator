package Entities;

import Entities.Comparators.Artifact_latency_comparator;
import HyperGraph.Proposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Proposition_plan implements Comparable<Proposition_plan>{

   private static int default_cost = 20;
   private Integer cost;
   private ArrayList<Proposition> propositions;
   private ArrayList<Artifact> already_expanded;
   private int heuristic_cost=0;


   private boolean finished=false;

   public Proposition_plan(){
      cost =0;
      finished = false;
      propositions = new ArrayList<>();
      already_expanded = new ArrayList<>();
   }

   public Proposition_plan(Proposition_plan bestPlan) {
      propositions = new ArrayList<>(bestPlan.getP());
      cost = bestPlan.getCost();
      already_expanded = new ArrayList<>(bestPlan.getAlready_expanded());
      finished = false;
   }

   public Proposition_plan(int i) {
      cost =i;
      propositions = new ArrayList<>();
      finished = false;
   }

   public Proposition_plan(ArrayList<Artifact> request) {
      propositions = new ArrayList<>();
      Proposition tmp = new Proposition(0, request);
      cost = 0;
      already_expanded = new ArrayList<>(request);
      propositions.add(tmp);
      finished = false;
   }

   public void add(Integer c, Proposition p) {
      this.cost = this.cost +c;
      already_expanded = new ArrayList<>(p.getArtifacts());
      this.propositions.add(p);
   }
   public ArrayList<Proposition> getP() {
      return propositions;
   }

   public void setP(ArrayList<Proposition> p) {
      this.propositions = p;
   }

   public Integer getCost() {
      return cost;
   }

   public void setCost(Integer cost) {
      this.cost = cost;
   }

   public void print() {
      System.out.println("Plan Cost: "+ cost);
      for(Proposition p: propositions){
         p.print();
      }
   }

   private int heuristic_cost_sch_max(ArrayList<Artifact> unexpanded_proposition) {
      int max = 0;
      for(Artifact a: unexpanded_proposition){
         if(a.getLatency_cost()>=max){
            max = a.getLatency_cost();
         }
      }
      return max;
   }

   private int heuristic_cost_min_sch(ArrayList<Artifact> unexpanded_proposition) {
      int min = 100000;
      for(Artifact a: unexpanded_proposition){
         if(a.getLatency_cost()<=min){
            min = a.getLatency_cost();
         }
      }
      return min;
   }

   public void add(Proposition p) {
      this.cost = this.cost +p.getCost();
      this.propositions.add(p);
      add_in_exanded_list(p);
      if(p.isROOT()){
         finished=true;
      }
   }

   public void add_for_max(Proposition p) {
      this.cost = this.cost +p.getCost();
      this.heuristic_cost = heuristic_cost_max(p.getArtifacts());
      p.set_tmp_Depth(0);
      this.propositions.add(p);
      add_in_exanded_list(p);
      if(p.isROOT()){
         finished=true;
      }
   }
   private int heuristic_cost_max(ArrayList<Artifact> unexpanded_proposition) {
      int max = 100000;
      for(Artifact a: unexpanded_proposition){
         if(a.getLatency_cost()>=max){
            max = a.getLatency_cost();
         }
      }

      return max;
   }

   public void add_for_sch(Proposition p) {
      this.cost = this.cost +p.getCost();
      this.heuristic_cost = heuristic_cost_min_sch(p.getArtifacts());
      this.propositions.add(p);
      add_in_exanded_list(p);
      if(p.isROOT()){
         finished=true;
      }
   }

   public void add_for_sch_max(Proposition p) {
      this.cost = this.cost +p.getCost();
      this.heuristic_cost = heuristic_cost_max_sch(p.getArtifacts(), already_expanded);
      this.propositions.add(p);
      add_in_exanded_list(p);
      if(p.isROOT()){
         finished=true;
      }
   }

   private int heuristic_cost_max_sch(ArrayList<Artifact> artifacts, ArrayList<Artifact> already_expanded) {
      Artifact_latency_comparator comparator = new Artifact_latency_comparator();
      PriorityQueue<Artifact> max_latency = new PriorityQueue<Artifact>(comparator);
      max_latency.addAll(artifacts);
      Artifact tmp = max_latency.peek();
      Artifact tmp_2 = max_latency.peek();
      int current_latency = tmp.current_latency(already_expanded);
      if(tmp.getLatency_cost()==current_latency)
         return tmp.getLatency_cost();
      else if (current_latency < tmp.getLatency_cost()){
         if(current_latency>=tmp_2.getLatency_cost()){
            return current_latency;
         }else{
            return tmp_2.getLatency_cost();
         }
      }
      return tmp_2.current_latency(already_expanded);
   }


   private void add_in_exanded_list(Proposition p) {
      for(Artifact a: p.getArtifacts()){
         if(!already_expanded.contains(a)){
            already_expanded.add(a);
         }
      }
   }

   public boolean isFinished() {
      return finished;
   }

   public void setFinished(boolean finished) {
      this.finished = finished;
   }

   public ArrayList<Artifact> get_unexpanded_proposition() {
      return propositions.get(propositions.size()-1).getArtifacts();
   }

   public ArrayList<Artifact> getAlready_expanded() {
      return already_expanded;
   }

   public void setAlready_expanded(ArrayList<Artifact> already_expanded) {
      this.already_expanded = already_expanded;
   }

   public int getHeuristic_cost() {
      return heuristic_cost;
   }

   public void setHeuristic_cost(int heuristic_cost) {
      this.heuristic_cost = heuristic_cost;
   }

   @Override
   public int compareTo(Proposition_plan o) {
      return this.getCost().compareTo(o.getCost());
   }
}

