package HyperGraph;

import DirectedGraph.DirectedGraph;
import Entities.*;
import Entities.Comparators.Proposition_plan_comparator_MAX;
import Entities.Comparators.Proposition_plan_comparator_MIN;
import Entities.Comparators.Proposition_plan_comparator;
import jdk.nashorn.internal.ir.RuntimeNode;

import java.util.*;
import java.util.stream.Collectors;

public class HyperGraph {
    public ArrayList<Artifact> getArtifacts() {
        return Artifacts;
    }

    public void setArtifacts(ArrayList<Artifact> artifacts) {
        Artifacts = artifacts;
    }

    public ArrayList<HyperEdge> getTasks() {
        return Tasks;
    }

    public void setTasks(ArrayList<HyperEdge> tasks) {
        Tasks = tasks;
    }

    ArrayList<Artifact> Artifacts;
    ArrayList<HyperEdge> Tasks;
    public float numberOfPops = 0;
    public long numberOfPlans = 0;

    public HyperGraph() {
        Artifacts = new ArrayList<>();
        Tasks = new ArrayList<>();
    }

    public HyperGraph(Artifact[] artifacts, HyperEdge[] hyperEdges) {
        Artifacts = new ArrayList<>();
        Tasks = new ArrayList<>();
        Artifacts.addAll(Arrays.asList(artifacts));
        Tasks.addAll(Arrays.asList(hyperEdges));
    }

    public HyperGraph(ArrayList<Artifact> artifacts, ArrayList<HyperEdge> tasks) {
        Artifacts = artifacts;
        Tasks = tasks;
    }

    public HyperGraph(HyperGraph hyperGraph) {
        this.Artifacts = new ArrayList<>(hyperGraph.Artifacts);
        this.Tasks = new ArrayList<>(hyperGraph.Tasks);
    }

    public void generateRandomHG(int number_of_requests, int width, int depth, int materialiazed, int artifact_in_degree, int task_in_degree, int task_out_degree, int default_cost) {
        Random rand = new Random();
        int pos = 0;
        //STAGE 1//
        Artifact r = new Artifact(Artifact.NodeType.ROOT, pos++, 0);
        Artifact a = null;
        HyperEdge e = null;
        int w = rand.nextInt(width) + 1;
        for (int i = 0; i < w; i++) {
            int load_cost = rand.nextInt(20) + default_cost;
            a = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
            e = new HyperEdge(r, a, load_cost);
            a.addIN(e);
            Artifacts.add(a);
            Tasks.add(e);
            r.addOUT(e);

        }
        //STAGE 2//
        for (int i = 0; i <= depth - 1; i++) {
            w = rand.nextInt(width) + 1;
            if (i == depth - 1)
                w = number_of_requests;

            for (int j = 0; j < w; j++) {
                if (i == depth - 1) {
                    a = new Artifact(Artifact.NodeType.REQUEST, pos++);
                } else {
                    a = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
                    if (rand.nextInt(100) < materialiazed) {
                        e = new HyperEdge(r, a, rand.nextInt(20) + default_cost);
                        a.addIN(e);
                        Tasks.add(e);
                        r.addOUT(e);
                    }
                }
                int a_in_degree = rand.nextInt(artifact_in_degree) + 1;
                for (int k = 0; k < a_in_degree; k++) {
                    ArrayList<Artifact> t_in = random_input(rand.nextInt(task_in_degree) + 1);
                    ArrayList<Artifact> t_out = random_output(rand.nextInt(task_out_degree), t_in);
                    t_out.add(a);
                    e = new HyperEdge(t_in, t_out, rand.nextInt(20) + default_cost);
                    a.addIN(e);
                    Tasks.add(e);
                }
                Artifacts.add(a);
            }
        }
        Artifacts.add(0, r);
    }

    private ArrayList<Artifact> random_output(int N, ArrayList<Artifact> t_in) {
        Random rand = new Random();
        ArrayList<Artifact> RA = new ArrayList<>();
        int i = 0;
        if (N < Artifacts.size()) {
            while (i < N) {
                int coin = rand.nextInt(Artifacts.size());
                Artifact a = Artifacts.get(coin);
                if (!RA.contains(a) && t_in.contains(a)) {
                    RA.add(a);
                    i++;
                }
            }
        } else {
            RA = copy(Artifacts);
        }
        return RA;
    }

    public void generateRandomLimitedHG(int number_of_requests, int width, int depth, int materialiazed, int artifact_in_degree, int task_in_degree, int task_out_degree, int default_cost) {
        Random rand = new Random();
        int pos = 0;
        int infinite = 1000000;
        int load_cost;
        int compute_cost;
        //STAGE 1//
        Artifact r = new Artifact(Artifact.NodeType.ROOT, pos++, 0);
        Artifact a = null;
        HyperEdge e = null;
        int w = rand.nextInt(width) + 1;
        for (int i = 0; i < w; i++) {
            load_cost = rand.nextInt(20) * 2 + default_cost;
            a = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
            a.setLoadCost(load_cost);
            a.setComputeCost(infinite);
            e = new HyperEdge(r, a, load_cost);
            a.addIN(e);
            Artifacts.add(a);
            Tasks.add(e);
            r.addOUT(e);
        }
        //STAGE 2//
        for (int i = 0; i <= depth - 1; i++) {
            w = rand.nextInt(width) + 1;
            if (i == depth - 1)
                w = number_of_requests;

            for (int j = 0; j < w; j++) {
                if (i == depth - 1) {
                    a = new Artifact(Artifact.NodeType.REQUEST, pos++);
                    a.setLoadCost(infinite);
                } else {
                    a = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
                    if (rand.nextInt(100) < materialiazed) {
                        load_cost = rand.nextInt(20) * 2 + default_cost;
                        e = new HyperEdge(r, a, load_cost);
                        a.setLoadCost(load_cost);
                        a.addIN(e);
                        Tasks.add(e);
                        r.addOUT(e);
                    } else {
                        a.setLoadCost(infinite);
                    }
                }
                int a_in_degree = 1;
                for (int k = 0; k < a_in_degree; k++) {
                    ArrayList<Artifact> t_in = random_input(rand.nextInt(task_in_degree) + 1);
                    ArrayList<Artifact> t_out = new ArrayList<>();
                    t_out.add(a);
                    compute_cost = rand.nextInt(20) + default_cost;
                    e = new HyperEdge(t_in, t_out, compute_cost);
                    a.setComputeCost(compute_cost);
                    a.addIN(e);
                    Tasks.add(e);
                }
                Artifacts.add(a);
            }
        }
        Artifacts.add(0, r);
    }

    public void generateRandomHG_2(int N, int depth, int IN_DEGREE, int TASK_INPUT) {
        Random rand = new Random();
        int pos = 0;
        //STAGE 1//
        Artifact r = new Artifact(Artifact.NodeType.ROOT, pos++);
        Artifact a = null;
        HyperEdge e = null;
        int w = rand.nextInt(N) + 1;
        for (int i = 0; i < w; i++) {
            int cost = rand.nextInt(20) + 1;
            a = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
            e = new HyperEdge(r, a, cost);
            a.addIN(e);
            Artifacts.add(a);
            Tasks.add(e);
            r.addOUT(e);
        }

        //STAGE 2//
        for (int i = 0; i <= depth - 1; i++) {
            w = rand.nextInt(N) + 1;
            for (int j = 0; j < w; j++) {
                if (i == depth - 1) {
                    a = new Artifact(Artifact.NodeType.REQUEST, pos++);
                } else {
                    a = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++);
                }
                int in_degree = rand.nextInt(IN_DEGREE) + 1;
                for (int k = 0; k < in_degree; k++) {
                    ArrayList<Artifact> AL = random_input(rand.nextInt(TASK_INPUT) + 1);
                    e = new HyperEdge(AL, a, rand.nextInt(20) + 1);
                    a.addIN(e);
                    Tasks.add(e);
                }
                Artifacts.add(a);
            }
        }
        Artifacts.add(0, r);
    }

    private ArrayList<Artifact> random_input(int N) {
        Random rand = new Random();
        ArrayList<Artifact> RA = new ArrayList<>();
        int i = 0;
        if (N < Artifacts.size()) {
            while (i < N) {
                int coin = rand.nextInt(Artifacts.size());
                if (!RA.contains(Artifacts.get(coin))) {
                    RA.add(Artifacts.get(coin));
                    i++;
                }
            }
        } else {
            RA = copy(Artifacts);
        }
        return RA;
    }

    private ArrayList<Artifact> copy(ArrayList<Artifact> artifacts) {
        ArrayList<Artifact> new_a = new ArrayList<>();
        for (Artifact a : artifacts) {
            new_a.add(a);
        }
        return new_a;
    }

    public void print() {
        System.out.println(Tasks.size());
        for (HyperEdge t : Tasks) {
            System.out.println(t.toString());
        }
    }

    //TO-DO
    private void find_minimum_retrieve_cost() {
        for (Artifact a : Artifacts) {
            int cost = 100000;
            for (HyperEdge e : a.getIN()) {
                a.setAdditiveCost(e.getCost());
            }
        }
    }

    public Proposition_plan iterative_exhaustive(ArrayList<Artifact> request) {
        Proposition_plan pp = new Proposition_plan(request);
        Proposition_plan minimum_finished_plan = new Proposition_plan(100000);
        ArrayList<Proposition_plan> unfinished_plans = new ArrayList<>();
        unfinished_plans.add(pp);
        while (unfinished_plans.size() > 0) {
            Proposition_plan current = unfinished_plans.remove(0);
            ArrayList<Proposition> new_propositions = new ArrayList<Proposition>();
            generatePropositions(0, 0, 0, new_propositions, current.get_unexpanded_proposition(), current.getAlready_expanded());
            numberOfPops++;
            for (Proposition p : new_propositions) {
                Proposition_plan next = new Proposition_plan(current);
                next.add(p);
                if (next.isFinished()) {
                    numberOfPlans++;
                    minimum_finished_plan = minimum_plan(minimum_finished_plan, next);
                } else {
                    unfinished_plans.add(0, next);
                }
            }
        }
        return minimum_finished_plan;
    }

    public Proposition_plan best_plan_so_far(ArrayList<Artifact> request) {
        Proposition_plan pp = new Proposition_plan(request);
        Proposition_plan minimum_finished_plan = new Proposition_plan(100000);
        ArrayList<Proposition_plan> unfinished_plans = new ArrayList<>();
        unfinished_plans.add(pp);
        while (unfinished_plans.size() > 0) {
            Proposition_plan current = unfinished_plans.remove(0);
            ArrayList<Proposition> new_propositions = new ArrayList<Proposition>();
            generatePropositions(0, 0, 0, new_propositions, current.get_unexpanded_proposition(), current.getAlready_expanded());
            numberOfPops++;
            for (Proposition p : new_propositions) {
                Proposition_plan next = new Proposition_plan(current);
                next.add(p);
                if (next.isFinished()) {
                    numberOfPlans++;
                    minimum_finished_plan = minimum_plan(minimum_finished_plan, next);
                } else {
                    if (next.getCost() < minimum_finished_plan.getCost()) {
                        unfinished_plans.add(0, next);
                    }
                }
            }
        }
        return minimum_finished_plan;
    }

    public Proposition_plan expand_closest_proposition(ArrayList<Artifact> request) {
        Proposition_plan pp = new Proposition_plan(request);
        PriorityQueue<Proposition_plan> unfinished_plans = new PriorityQueue<>();
        if(pp == null){
            System.out.println("wtf");
        }
        while (!pp.isFinished()) {
            ArrayList<Proposition> new_propositions = new ArrayList<Proposition>();
            generatePropositions(0, 0, 0, new_propositions, pp.get_unexpanded_proposition(), pp.getAlready_expanded());
            numberOfPops++;
            if (new_propositions.size() == 0)
                System.out.println("wtf");
            for (Proposition p : new_propositions) {
                Proposition_plan next = new Proposition_plan(pp);
                next.add(p);
                unfinished_plans.add(next);
            }
            pp = unfinished_plans.poll();
            if(pp == null){
                System.out.println("wtf");
            }
        }
        return pp;
    }

    public void compute_the_latency_of_artifacts() {

        Collections.sort(Artifacts, Comparator.comparingInt(Artifact::getPosition));

        for (Artifact a : Artifacts) {
            if (a.isROOT()) {
                a.setDepth(0);
                a.setLatency_cost(0);
            } else {
                int latency_cost = 100000;
                for (HyperEdge e : a.getIN()) {
                    if (latency_cost > e.getCost() + e.getInput_latency()) {
                        latency_cost = e.getCost() + e.getInput_latency();
                    }
                }
                a.setLatency_cost(latency_cost);
                a.setDepth(a.INDepth() + 1);
            }
        }
    }

    public Proposition_plan expand_closest_heuristic_search_MIN(ArrayList<Artifact> request) {
        Proposition_plan pp = new Proposition_plan(request);
        Proposition_plan_comparator_MIN comparator = new Proposition_plan_comparator_MIN();
        PriorityQueue<Proposition_plan> unfinished_plans = new PriorityQueue<>(comparator);
        while (!pp.isFinished()) {
            ArrayList<Proposition> new_propositions = new ArrayList<Proposition>();
            generatePropositions(0, 0, 0, new_propositions, pp.get_unexpanded_proposition(), pp.getAlready_expanded());
            numberOfPops++;
            for (Proposition p : new_propositions) {
                Proposition_plan next = new Proposition_plan(pp);
                next.add(p);
                unfinished_plans.add(next);
            }
            pp = unfinished_plans.poll();
        }
        return pp;
    }

    public Proposition_plan expand_closest_heuristic_search_MAX(ArrayList<Artifact> request) {
        Proposition_plan pp = new Proposition_plan(request);
        Proposition_plan_comparator comparator = new Proposition_plan_comparator();
        PriorityQueue<Proposition_plan> unfinished_plans = new PriorityQueue<>(comparator);
        while (!pp.isFinished()) {
            ArrayList<Proposition> new_propositions = new ArrayList<Proposition>();
            generatePropositions(0, 0, 0, new_propositions, pp.get_unexpanded_proposition(), pp.getAlready_expanded());
            numberOfPops++;
            for (Proposition p : new_propositions) {
                Proposition_plan next = new Proposition_plan(pp);
                next.add_for_max(p);
                unfinished_plans.add(next);
            }
            pp = unfinished_plans.poll();
        }
        return pp;
    }

    public Proposition_plan expand_closest_heuristic_search_Latency_MIN(ArrayList<Artifact> request) {
        Proposition_plan pp = new Proposition_plan(request);
        Proposition_plan_comparator comparator = new Proposition_plan_comparator();
        PriorityQueue<Proposition_plan> unfinished_plans = new PriorityQueue<>(comparator);
        while (!pp.isFinished()) {
            ArrayList<Proposition> new_propositions = new ArrayList<Proposition>();
            generatePropositions(0, 0, 0, new_propositions, pp.get_unexpanded_proposition(), pp.getAlready_expanded());
            numberOfPops++;
            for (Proposition p : new_propositions) {
                Proposition_plan next = new Proposition_plan(pp);
                next.add_for_sch(p);
                unfinished_plans.add(next);
            }
            pp = unfinished_plans.poll();
        }
        return pp;
    }

    public Proposition_plan expand_closest_heuristic_search_Latency_MAX(ArrayList<Artifact> requests) {
        Proposition_plan pp = new Proposition_plan(requests);
        Proposition_plan_comparator comparator = new Proposition_plan_comparator();
        PriorityQueue<Proposition_plan> unfinished_plans = new PriorityQueue<>(comparator);
        while (!pp.isFinished()) {
            ArrayList<Proposition> new_propositions = new ArrayList<Proposition>();
            generatePropositions(0, 0, 0, new_propositions, pp.get_unexpanded_proposition(), pp.getAlready_expanded());
            numberOfPops++;
            for (Proposition p : new_propositions) {
                Proposition_plan next = new Proposition_plan(pp);
                next.add_for_sch_max(p);
                unfinished_plans.add(next);
            }
            pp = unfinished_plans.poll();
        }
        return pp;
    }

    private Proposition_plan minimum_plan(Proposition_plan minimum_finished_plan, Proposition_plan next) {
        if (minimum_finished_plan.getCost() >= next.getCost()) {
            return next;
        } else {
            return minimum_finished_plan;
        }
    }

    private Proposition_plan minimum_cost_plan(ArrayList<Proposition_plan> finished_plans) {
        int min = 10000000;
        Proposition_plan best_plan = new Proposition_plan();
        for (Proposition_plan p : finished_plans) {
            if (p.getCost() < min) {
                best_plan = p;
            }
        }
        return best_plan;
    }

    public void PrintNumberOfProposition(ArrayList<Artifact> current) {
        int TotalnumberOfProposition = 1;
        for (Artifact a : current) {
            TotalnumberOfProposition = TotalnumberOfProposition * a.getINsize();
        }

        System.out.println(TotalnumberOfProposition);

    }

    public ArrayList<Artifact> getRequests(String operation) {
        ArrayList<Artifact> rq = new ArrayList<>();
        if(operation=="final") {
            for (Artifact a : Artifacts) {
                if (a.isREQUEST()) {
                    rq.add(a);
                }
            }
        }else{
            for (Artifact a : Artifacts) {
                if (a.getId().endsWith(operation)) {
                    a.setType(Artifact.NodeType.REQUEST);
                    rq.add(a);
                }
            }
        }
        return rq;
    }
    public ArrayList<Artifact> getRequests() {
        ArrayList<Artifact> rq = new ArrayList<>();
            for (Artifact a : Artifacts) {
                if (a.getId().endsWith("operation")) {
                    a.setType(Artifact.NodeType.REQUEST);
                    rq.add(a);
                }
            }
        return rq;
    }

    public ArrayList<Artifact> getRequests(Set<String> request) {
        int i = 0;
        ArrayList<Artifact> rq = new ArrayList<>();
            for (Artifact a : Artifacts) {
                if (request.contains(a.getId())) {
                    rq.add(a);
                    i++;

                }
            }
        return rq;
    }

    public Proposition_plan exhaustiveSearch(Proposition_plan BestPlan, ArrayList<Artifact> rq, ArrayList<Artifact> already_expanded) {
        if (isROOT(rq)) {
            //System.out.println("BP: " + BestPlan.getCost());
            return BestPlan;
        }
        ArrayList<Proposition> propositions = new ArrayList<>();
        generatePropositions(0, 0, 0, propositions, rq, already_expanded);
        numberOfPops++;
        already_expanded.addAll(rq);
        //System.out.println(propositions.size());
        Proposition_plan best_candidate_plan = new Proposition_plan(100000);
        for (Proposition p : propositions) {
            Proposition_plan tmp_Proposition_plan = new Proposition_plan(BestPlan);
            tmp_Proposition_plan.add(p.getCost(), p);

            Proposition_plan candidate_plan = exhaustiveSearch(tmp_Proposition_plan, p.getArtifacts(), new ArrayList<>(already_expanded));
            if (best_candidate_plan.getCost() > candidate_plan.getCost()) {
                best_candidate_plan = candidate_plan;
            }
        }
        return best_candidate_plan;


    }

    private boolean isROOT(ArrayList<Artifact> rq) {
        for (Artifact a : rq) {
            if (!a.isROOT())
                return false;
        }
        return true;
    }

    public void generatePropositions(int max, int sum, int i, ArrayList<Proposition> propositions, ArrayList<Artifact> current, ArrayList<Artifact> already_expanded) {
        if (i >= current.size()) {
            if (propositions.size() == 0)
                propositions.add(new Proposition(max, sum, current));
            return;
        }
        if (!current.get(i).isROOT()) {
            if (!already_expanded.contains(current.get(i))) {
                already_expanded.add(current.get(i));
            }
            for (HyperEdge e : current.get(i).getIN()) {
                ArrayList<Artifact> new_current = new ArrayList<>(current);
                new_current.remove(current.get(i));
                int k = addAll(new_current, i, e.getIN(), already_expanded);
                if (i >= current.size() - 1)
                    propositions.add(new Proposition(max, sum + e.getCost(), new_current));
                if (k > 0) {
                    if (e.getCost() > max)
                        max = e.getCost();
                    generatePropositions(max, sum + e.getCost(), i + k, propositions, new_current, already_expanded);
                } else {
                    if (e.getCost() > max)
                        max = e.getCost();
                    generatePropositions(max, sum + e.getCost(), i + 1, propositions, new_current, already_expanded);
                }
            }
        } else {
            generatePropositions(max, sum, i + 1, propositions, current, already_expanded);
        }
    }

    private int addAll(ArrayList<Artifact> new_current, int i, ArrayList<Artifact> in, ArrayList<Artifact> already_expaned) {
        int k = 0;
        for (Artifact a : in) {
            if (!new_current.contains(a) || a.isROOT()) {
                if (!already_expaned.contains(a)) {
                    new_current.add(i + k, a);
                    k++;
                }
            }
        }
        return k;
    }

    public float PrintNumberOfPlans() {
        float TotalnumberOfProposition = 1;
        for (Artifact a : Artifacts) {
            if (!a.isROOT()) {
                if (a.getINsize() > 1) {
                    TotalnumberOfProposition = TotalnumberOfProposition * a.getINsize();
                }
            }
        }

        return TotalnumberOfProposition;
    }

    public Proposition_plan Naive(Proposition_plan BestPlan, ArrayList<Artifact> rq, ArrayList<Artifact> already_expaned) {
        if (isROOT(rq)) {
            return BestPlan;
        }
        ArrayList<Proposition> propositions = new ArrayList<>();

        findMinProposition(0, 0, propositions, rq, already_expaned);
        //System.out.println("Propositions: " + propositions.size());
        //  print(propositions);
        Proposition_plan best_candidate_plan = new Proposition_plan(100000000);
        for (Proposition p : propositions) {
            Proposition_plan tmp_Proposition_plan = new Proposition_plan(BestPlan);
            tmp_Proposition_plan.add(p.getCost(), p);
            Proposition_plan candidate_plan = Naive(tmp_Proposition_plan, p.getArtifacts(), new ArrayList<>(already_expaned));
            if (best_candidate_plan.getCost() > candidate_plan.getCost()) {
                best_candidate_plan = candidate_plan;
            }
        }
        return best_candidate_plan;
    }

    public Proposition_plan Naive(ArrayList<Artifact> request) {
        Proposition_plan pp = new Proposition_plan(request);
        Proposition_plan minimum_finished_plan = new Proposition_plan(100000);
        ArrayList<Proposition_plan> unfinished_plans = new ArrayList<>();
        unfinished_plans.add(pp);
        while (unfinished_plans.size() > 0) {
            Proposition_plan current = unfinished_plans.remove(0);
            ArrayList<Proposition> new_propositions = new ArrayList<Proposition>();
            findMinProposition(0, 0, new_propositions, current.get_unexpanded_proposition(), current.getAlready_expanded());
            numberOfPops++;
            for (Proposition p : new_propositions) {
                Proposition_plan next = new Proposition_plan(current);
                next.add(p);
                if (next.isFinished()) {
                    numberOfPlans++;
                    minimum_finished_plan = minimum_plan(minimum_finished_plan, next);
                } else {
                    unfinished_plans.add(0, next);
                }
            }
        }
        return minimum_finished_plan;
    }

    public void findMinProposition(int sum, int i, ArrayList<Proposition> propositions, ArrayList<Artifact> current, ArrayList<Artifact> already_expanded) {
        if (i >= current.size()) {
            if (propositions.size() == 0)
                propositions.add(new Proposition(sum, current));
            return;
        }
        if (!current.get(i).isROOT()) {
            int min = 10000000;
            HyperEdge e = null;
            for (HyperEdge tmp_e : current.get(i).getIN()) {
                if (tmp_e.getCost() < min) {
                    min = tmp_e.getCost();
                    e = tmp_e;
                }
            }
            if (!already_expanded.contains(current.get(i))) {
                already_expanded.add(current.get(i));
            }
            ArrayList<Artifact> new_current = new ArrayList<>(current);
            new_current.remove(current.get(i));
            int k = addAll(new_current, i, e.getIN(), already_expanded);
            if (i + k >= new_current.size())
                propositions.add(new Proposition(sum + e.getCost(), new_current));
            findMinProposition(sum + e.getCost(), i + k, propositions, new_current, already_expanded);

        } else {
            findMinProposition(sum, i + 1, propositions, current, already_expanded);
        }
    }

    public ArrayList<Plan> Simple_Exhaustive_Search(int i) {

        if (i == Artifacts.size()) {
            ArrayList<Plan> result1 = new ArrayList<>();
            Plan p = new Plan();
            p.add(Artifacts.get(0), 0);
            result1.add(p);
            return result1;
        }
        ArrayList<Plan> next = Simple_Exhaustive_Search(i + 1);
        ArrayList<Plan> result = new ArrayList<>();
        Artifact current = Artifacts.get(i);
        for (HyperEdge e : current.getIN()) {
            for (Plan p1 : next) {
                Plan p2 = new Plan(p1);
                p2.copy(new Artifact(current, e.getIN(), e.getCost()), e.getCost());
                result.add(p2);
            }

        }
        return result;
    }

    public Proposition_plan ClosestProposition(Proposition_plan BestPlan, ArrayList<Artifact> rq, ArrayList<Artifact> already_expanded) {
        if (isROOT(rq)) {
            //System.out.println("BP: " + BestPlan.getCost());
            return BestPlan;
        }
        ArrayList<Proposition> propositions = new ArrayList<>();
        generatePropositions(0, 0, 0, propositions, rq, already_expanded);
        already_expanded.addAll(rq);
        //System.out.println(propositions.size());
        Proposition_plan best_candidate_plan = new Proposition_plan(100000);
        for (Proposition p : propositions) {
            Proposition_plan tmp_Proposition_plan = new Proposition_plan(BestPlan);
            tmp_Proposition_plan.add(p.getCost(), p);

            Proposition_plan candidate_plan = ClosestProposition(tmp_Proposition_plan, p.getArtifacts(), new ArrayList<>(already_expanded));
            if (best_candidate_plan.getCost() > candidate_plan.getCost()) {
                best_candidate_plan = candidate_plan;
            }
        }
        return best_candidate_plan;


    }

    public DirectedGraph toHelixGraph(int sink) {
        int infinite = 100000000;
        int source = 0;
        DirectedGraph g = new DirectedGraph();
        for (Artifact a : Artifacts) {

            int l = a.getLoadCost();
            int c = a.getComputeCost();
            int a_0 = a.getPosition() * 2;
            int b_0 = a.getPosition() * 2 + 1;
            if (a.isINTERMEDIATE()) {
                //a connected to sink
                g.addEdge(a_0, sink, l);
                //b connected to a
                g.addEdge(b_0, a_0, infinite);
                //b connected to sink
                if (a.getComputeCost() > 0) {
                    if (l - c >= 0) {
                        g.addEdge(source, b_0, l - c);
                    } else {
                        g.addEdge(b_0, sink, -(l - c));
                    }
                    //b connected to parents
                    for (Artifact parent : a.getComputeIN()) {
                        if (parent.getPosition() != 0) {
                            g.addEdge(b_0, parent.getPosition() * 2, infinite);
                        }
                    }
                }
            } else if (a.isREQUEST()) {
                //b connected to a

                //b connected to sink
                if (a.getComputeCost() > 0) {
                    if (l - c >= 0) {
                        g.addEdge(source, a_0, l - c);
                    } else {
                        g.addEdge(a_0, sink, -(l - c));
                    }
                    //b connected to parents
                    for (Artifact parent : a.getIN(0)) {
                        if (parent.getPosition() != 0) {
                            g.addEdge(a_0, parent.getPosition() * 2, infinite);
                        }
                    }
                }
            }
        }
        return g;
    }


    public HyperGraph flowToAG(ArrayList<Integer> flow) {


        ArrayList<Integer> final_nodes = (ArrayList<Integer>) flow.stream().distinct().sorted().collect(Collectors.toList());
        final_nodes.remove(0);

        ArrayList<Artifact> Artifacts = new ArrayList<>();
        for (int i = 0; i < final_nodes.size(); i++) {
            if (final_nodes.get(i) % 2 == 0) {
                Artifact tmp = this.getPosition(final_nodes.get(i) / 2);
                if (final_nodes.contains(final_nodes.get(i) + 1)) {
                    tmp.setLoad(false);
                } else {
                    if (tmp.isINTERMEDIATE())
                        tmp.setLoad(true);
                }
                Artifacts.add(tmp);
            }

        }
        return this.newHyperGraph(Artifacts);
    }

    private HyperGraph newHyperGraph(ArrayList<Artifact> artifacts) {
        HashSet<HyperEdge> Tasks = new HashSet<>();
        for (Artifact a : artifacts) {
            if (a.isLoad()) {
                Tasks.add(a.getLoadEgde());
            } else {
                Tasks.add(a.getComputeEgde());
            }
        }
        return new HyperGraph(artifacts, new ArrayList<>(Tasks));
    }

    private Artifact getPosition(Integer integer) {
        return Artifacts.get(integer);
    }

    public int EdgeCost() {
        int sum = 0;
        for (HyperEdge e : Tasks) {
            sum = sum + e.getCost();
        }
        return sum;
    }

    public String print_EdgeCost() {
        int sum = 0;
        String str = "";
        for (HyperEdge e : Tasks) {
            sum = sum + e.getCost();
            str = str + e.toString();
        }
        return str + " total cost " + sum;
    }

    public ArrayList<Artifact> collab_forward_pass() {
        ArrayList<Artifact> R = new ArrayList<>();
        for (Artifact a : Artifacts) {
            if (a.isROOT()) {
                a.setRecreation_cost(0);
            } else {
                int recreation_cost = 1000000;
                HyperEdge tmp = null;
                for (HyperEdge e : a.getIN()) {
                    if (recreation_cost > e.getCost() + e.get_recreation_cost()) {
                        recreation_cost = e.getCost() + e.get_recreation_cost();
                        tmp = e;
                    }
                }
                a.setRecreation_cost(recreation_cost);
                if (tmp.getIN().get(0).isROOT()) {
                    R.add(a);
                    a.setLoad(true);
                } else {
                    a.setLoad(false);
                }
            }
        }
        return R;
    }

    public HyperGraph collab_backward_pass(ArrayList<Artifact> loaded_artifacts, ArrayList<Artifact> request) {
        ArrayList<Artifact> expand = new ArrayList<>(request);
        ArrayList<Artifact> visited = new ArrayList<>();
        while (!expand.isEmpty()) {
            Artifact a = expand.remove(0);
            if (loaded_artifacts.contains(a)) {
                visited.add(a);
            } else {
                visited.add(a);
                expand.addAll(a.getComputeIN());
            }
        }
        return this.newHyperGraph(visited);


    }

    public long getArtifactsSize() {
        return Artifacts.size();
    }

    public long getEdgeSize() {
        return Tasks.size();
    }


    public void printSharableArtifacts() {
        System.out.println("SHARABLE");
        for (Artifact a : Artifacts) {
            System.out.println(a.print());
        }


    }

    public void printEquivilantArtifacts() {
        System.out.println("EQUIVALENT");
        for (Artifact a : Artifacts) {
            System.out.println(a.print());

        }

    }


    public void setRequests(ArrayList<String> requests) {
        for (Artifact a : Artifacts) {
            if (requests.contains(a.getId())) {
                System.out.println(a.print());
                a.setType(Artifact.NodeType.REQUEST);
            }
        }
    }

    public void stats() {
        System.out.println("NumberOfNodes,	NumberOfEdges,	sharability,	degree of equivalent" );
        int equivalent =0;
        int sharability =0;
        int n_intermediates = 0;
        int n_equivalent = 0;
        for (Artifact a : Artifacts) {
            if(a.isINTERMEDIATE()){
                sharability = sharability + a.getOUT().size();
                n_intermediates = n_intermediates+1;
            }
            if(!a.isROOT()) {
                equivalent = equivalent + a.getIN().size();
                n_equivalent=n_equivalent+1;
            }
        }

        System.out.println(Artifacts.size()+","+ Tasks.size()+","+(double)sharability/n_intermediates+","+(double)equivalent/n_equivalent );
    }

    public void setAllRequests() {
        for (Artifact a : Artifacts) {
            if (a.getOUT().size() == 0) {
                a.setType(Artifact.NodeType.REQUEST);
                System.out.println(a.getId() + ", " + a.getINsize());
            }
        }
    }

    public int getLatency(ArrayList<Artifact> a_requests) {
        int latency = 0;
        for (Artifact a : a_requests) {
            if (latency < a.getLatency_cost()) {
                latency = a.getLatency_cost();
            }
        }
        return latency;
    }

    public void generatePropositionArtifacts(int i, ArrayList<ArrayList<Artifact>> propositions, ArrayList<Artifact> current) {
        if (i >= current.size()) {
            if (propositions.size() == 0) {
                ArrayList<Artifact> tmp = new ArrayList<>();
                tmp.addAll(current);
                propositions.add(tmp);
            }
            return;
        }
        if (!current.get(i).isROOT()) {

            for (HyperEdge e : current.get(i).getIN()) {
                ArrayList<Artifact> new_current = new ArrayList<>(current);
                String tmp_name = current.get(i).getId().split("_")[0];
                new_current.remove(current.get(i));
                new_current.add(i, new Artifact(tmp_name, e.getIN().get(0)));
                if (i >= current.size() - 1) {
                    ArrayList<Artifact> tmp2 = new ArrayList<>();
                    tmp2.addAll(new_current);
                    propositions.add(tmp2);
                }
                generatePropositionArtifacts(i + 1, propositions, new_current);

            }
        } else {
            generatePropositionArtifacts(i + 1, propositions, current);
        }
    }

    public ArrayList<Artifact> getRequests(ArrayList<Artifact> request_names) {
        ArrayList<String> names = new ArrayList<>();
        for (Artifact a : request_names) {
            names.add(a.getId());
        }
        ArrayList<Artifact> rq = new ArrayList<>();
        for (Artifact a : Artifacts) {
            if (names.contains(a.getId())) {
                a.setType(Artifact.NodeType.REQUEST);
                rq.add(a);
            }
        }
        return rq;
    }

    public int getNumberOfRequests() {
        int sum = 0;
        for (Artifact a : Artifacts) {
            if (a.isREQUEST()) {
                sum ++;
            }
        }
        return sum;
    }

    public void createHyperEdges() {
        ArrayList<Artifact> merge_artifacts = new ArrayList<>();
        for(Artifact a: Artifacts){
            if(a.getId().startsWith("classifiers"))
                merge_artifacts.add(a);
        }
        for(Artifact a: merge_artifacts){
            String id  = a.getId();
            ArrayList<Artifact> outArtifacts = new ArrayList<>();
            ArrayList<Artifact> inArtifacts = new ArrayList<>();
            inArtifacts.add(a.getIN().get(0).getIN().get(0));
            for(HyperEdge e: a.getOUT()) {
                outArtifacts.add(e.getOUT().get(0));
            }
            int cost =a.getIN().get(0).getCost();
            Artifacts.remove(a);
            HyperEdge he = new HyperEdge(inArtifacts,outArtifacts,cost);
            Tasks.add(he);
            Tasks.remove(a.getIN().get(0));
            Tasks.remove(a.getOUT().get(0));
            for(Artifact a1: Artifacts){
                if (outArtifacts.contains(a1)){
                    a1.removeIN(null);
                    a1.addIN(he);
                }
            }
            for(Artifact a1: Artifacts){
                if (inArtifacts.contains(a1)){
                    a1.removeOUT(null);
                    a1.addOUT(he);
                }
            }
            ArrayList<HyperEdge> to_remove = new ArrayList<>();
            for(HyperEdge e:Tasks){
                if(e.getIN().get(0).getId().startsWith(id)){
                    to_remove.add(e);
                }
            }
            for(HyperEdge e: to_remove)
                Tasks.remove(e);

        }

    }
    public void createHyperEdges_2() {
        ArrayList<Artifact> merge_artifacts = new ArrayList<>();
        for(Artifact a: Artifacts){
            if(a.getId().startsWith("ensemble"))
                merge_artifacts.add(a);
        }
        for(Artifact a: merge_artifacts){
            String id = a.getId();
            ArrayList<Artifact> outArtifacts = new ArrayList<>();
            ArrayList<Artifact> inArtifacts = new ArrayList<>();
            for(HyperEdge e: a.getIN()) {
                inArtifacts.add(e.getIN().get(0));
            }
            outArtifacts.add(a.getOUT().get(0).getOUT().get(0));
            int cost =a.getOUT().get(0).getCost();
            Artifacts.remove(a);
            HyperEdge he = new HyperEdge(inArtifacts,outArtifacts,cost);
            Tasks.add(he);
            Tasks.remove(a.getIN().get(0));
            Tasks.remove(a.getOUT().get(0));
            ArrayList<HyperEdge> to_remove = new ArrayList<>();
            for(HyperEdge e:Tasks){
                if(e.getIN().get(0).getId().startsWith(id)){
                    to_remove.add(e);
                }
            }
            for(HyperEdge e: to_remove)
                Tasks.remove(e);

            for(Artifact a1: Artifacts){
                if (inArtifacts.contains(a1)){
                    a1.removeOUT(null);
                    a1.addOUT(he);
                }
            }
            for(Artifact a1: Artifacts){
                if (outArtifacts.contains(a1)){
                    a1.removeIN(null);
                    a1.addIN(he);
                }
            }
        }

    }


    public void change_cost() {
        ArrayList<Artifact> merge_artifacts = new ArrayList<>();
        for(Artifact a: Artifacts){
            if(a.getId().startsWith("classifiers"))
                merge_artifacts.add(a);
        }
        for(Artifact a: merge_artifacts){

            ArrayList<Artifact> inArtifacts = new ArrayList<>();
            inArtifacts.add(a.getIN().get(0).getIN().get(0));
            int cost =a.getIN().get(0).getCost();
            for(HyperEdge e: a.getOUT()) {
                ArrayList<Artifact> outArtifacts = new ArrayList<>();
                outArtifacts.add(e.getOUT().get(0));

                HyperEdge he = new HyperEdge(inArtifacts,outArtifacts,cost);
                Tasks.add(he);
                for(Artifact a1: Artifacts){
                    if (outArtifacts.contains(a1)){
                        a1.removeIN(null);
                        a1.addIN(he);
                    }
                }
            }


            Artifacts.remove(a);
            Tasks.remove( a.getIN().get(0));
            Tasks.remove( a.getOUT().get(0));
            ArrayList<HyperEdge> to_remove = new ArrayList<>();
            for(HyperEdge e:Tasks){
                if(e.getIN().get(0).getId().startsWith("classifiers")){
                    to_remove.add(e);
                }
            }
            for(HyperEdge e: to_remove)
                Tasks.remove(e);

        }


    }

    public void artifacts_computeCost() {
        for(Artifact a: Artifacts){
            if(!a.isROOT()) {
                a.setComputeCost(a.getIN().get(0).getCost());
                a.setLoadCost(100000);
            }
        }
    }
}

