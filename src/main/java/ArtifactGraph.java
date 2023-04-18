import DirectedGraph.DirectedGraph;
import Entities.NArtifact;
import Entities.NPlan;

import java.util.*;

public class ArtifactGraph {

    private ArrayList<NArtifact> intermediates;
    private NArtifact root;
    private ArrayList<NArtifact> Terminals;
    private int maxDepth = 0;

    public ArtifactGraph(ArrayList<NArtifact> intermediates, NArtifact root, ArrayList<NArtifact> terminals) {
        this.intermediates = intermediates;
        this.root = root;
        Terminals = terminals;
    }

    public ArtifactGraph() {
        intermediates = new ArrayList<NArtifact>();
        Terminals = new ArrayList<NArtifact>();
    }

    //N: number of artifacts to be generated
    //depth of the graph
    //maximum in degree of an artifact
    //how often do artifacts are shared by other artifacts
    public void generate_random_graph(int N, int depth) {
        this.maxDepth = depth;
        Random rand = new Random();
        this.root = new NArtifact(NArtifact.NodeType.ROOT);
        int pos = 0;
        HashMap<NArtifact, Integer> previous = new HashMap<NArtifact, Integer>();
        previous.put(root, rand.nextInt(10));

        for (int i = 0; i <= depth; i++) {
            HashMap<NArtifact, Integer> next = new HashMap<NArtifact, Integer>();
            int maxIN = rand.nextInt(N) + 1;
            for (int j = 0; j < maxIN; j++) {
                NArtifact tmp = new NArtifact(UUID.randomUUID(), ++pos, NArtifact.NodeType.ORNODE, true, 0, 0, previous, null);
                if (i == depth) {
                    tmp.setType(NArtifact.NodeType.LEAF);
                }
                next.put(tmp, rand.nextInt(10));
            }
            for (NArtifact prev : previous.keySet()) {
                prev.setChildren(next);
            }
            if (i < depth & i > 0) {
                intermediates.addAll(previous.keySet());
            } else if (i > 0) {
                intermediates.addAll(previous.keySet());
                Terminals.addAll(next.keySet());
            }
            previous = next;
        }
    }

    public void generate_random_graph_2(int N, int depth, int share_ability) {
        this.maxDepth = depth;
        int randomCost;
        Random rand = new Random();
        this.root = new NArtifact(NArtifact.NodeType.ROOT);
        int pos = 0;
        HashMap<NArtifact, Integer> previous = new HashMap<NArtifact, Integer>();
        previous.put(root, rand.nextInt(10));

        for (int i = 0; i <= depth; i++) {
            HashMap<NArtifact, Integer> next = new HashMap<NArtifact, Integer>();
            int maxIN = rand.nextInt(N) + 1;
            for (int j = 0; j < maxIN; j++) {

                HashMap<NArtifact, Integer> randomSet = randomSubSet(share_ability, previous);
                NArtifact tmp = new NArtifact(UUID.randomUUID(), ++pos, NArtifact.NodeType.ORNODE, false, 0, 0, randomSet, null);
                randomCost = rand.nextInt(100);
                addChildren(randomSet, tmp);

                if (i == depth) {
                    tmp.setType(NArtifact.NodeType.LEAF);
                }
                next.put(tmp, randomCost);
            }
            if (i < depth & i > 0) {
                intermediates.addAll(previous.keySet());
            } else if (i > 0) {
                intermediates.addAll(previous.keySet());
                Terminals.addAll(next.keySet());
            }
            previous = next;
        }
    }

    private void addChildren(HashMap<NArtifact, Integer> randomSet, NArtifact tmp) {
        Random rand = new Random();
        for (Map.Entry<NArtifact, Integer> a : randomSet.entrySet()) {
            a.getKey().addChild(tmp, rand.nextInt(10));
        }
    }

    void updateAdditiveCost() {
        this.root.setAdditiveCost(0);
        findMinAddativeCost(intermediates);
        findMinAddativeCost(Terminals);
    }

    private void findMinAddativeCost(ArrayList<NArtifact> intermediates) {
        for (NArtifact intermediate : intermediates) {
            int min = 1000000;
            if (intermediate.isAND()) {
                intermediate.setAdditiveCost(getSUM(intermediate));
            } else {
                intermediate.setAdditiveCost(getMin(intermediate));
            }
        }
    }

    private int getSUM(NArtifact intermediate) {
        int sum = 0;
        for (Map.Entry<NArtifact, Integer> a : intermediate.getParents().entrySet()) {
            sum = sum + a.getKey().getAdditiveCost() + a.getValue();
        }
        return sum;
    }

    private int getMin(NArtifact intermediate) {
        int min = 10000000;
        for (Map.Entry<NArtifact, Integer> a : intermediate.getParents().entrySet()) {
            if (min > a.getKey().getAdditiveCost() + a.getValue()) {
                min = a.getKey().getAdditiveCost() + a.getValue();
            }
        }
        return min;
    }

    void sortArtifacts() {
        System.out.println("before");
        for (NArtifact intermediate : intermediates) {
            System.out.println(intermediate.getPosition());
        }
        Collections.sort(intermediates, new NArtifact.SortbyPOS());
        System.out.println("after");
        for (NArtifact intermediate : intermediates) {
            System.out.println(intermediate.getPosition());
        }
    }

    void print() {
        System.out.println(root.toString());
        System.out.println("Intermediates.toString()");
        for (NArtifact intermediate : intermediates) {
            System.out.println(intermediate.toString());
        }
        System.out.println("Terminal.toString()");
        for (NArtifact Terminal : Terminals) {
            System.out.println(Terminal.toString());
        }
    }

    void printLikeAgraphC() {
        System.out.println(root.printPosAndChildren());
        System.out.println("Intermediates.toString()");
        for (NArtifact intermediate : intermediates) {
            System.out.println(intermediate.printPosAndChildren());
        }
        System.out.println("Terminal.toString()");
        for (NArtifact Terminal : Terminals) {
            System.out.println(Terminal.printPosAndChildren());
        }
    }
    void printLikeAgraphP() {
        System.out.println(root.printPosAndParents());
        System.out.println("Intermediates.toString()");
        for (NArtifact intermediate : intermediates) {
            System.out.println(intermediate.printPosAndParents());
        }
        System.out.println("Terminal.toString()");
        for (NArtifact Terminal : Terminals) {
            System.out.println(Terminal.printPosAndParents());
        }
    }

    public ArrayList<NArtifact> getSomeTerminals(int i) {
        ArrayList<NArtifact> rqs = new ArrayList<>();
        int j = 0;
        for (NArtifact a : Terminals) {
            if (j == i)
                break;
            rqs.add(a);
            j++;
        }
        return rqs;
    }

    public ArrayList<NPlan> getPlans(NArtifact a) {
        NPlan p = new NPlan();
        ArrayList<NPlan> NPlans = new ArrayList<NPlan>();
        //INITIATE WITH THE LOAD THAT HAS NO PARENTS
        if (a.getParents() == null) {
            p.copy(a, 0);
            NPlans.add(p);
            return NPlans;
        }
        int parents_size = a.getParents().size();
        int i = 0;
        ArrayList<NPlan> tmp_list = new ArrayList<NPlan>();
        for (Map.Entry<NArtifact, Integer> tmp : a.getParents().entrySet()) {
            ArrayList<NPlan> tmp_N_plans = getPlans(tmp.getKey());
            //CURRENT NODE IS AND WE MERGE P1XP2
            if (a.isAND()) {
                for (NPlan p1 : tmp_N_plans) {
                    for (NPlan p2 : NPlans) {
                        NPlan p3 = merge(p1, p2);
                        p3.copy(a, tmp.getValue());
                        tmp_list.add(p3);
                    }
                }
            } else {
                //CURRENT NODE IS AND WE MERGE P1+P2
                for (NPlan p1 : tmp_N_plans) {
                    p1.copy(a, tmp.getValue());
                    NPlans.add(p1);
                }
            }
            if (i == parents_size - 1) {
                if (a.isAND()) {
                    NPlans.clear();
                    NPlans = tmp_list;
                    tmp_list = new ArrayList<NPlan>();
                }
            }
            tmp_N_plans.clear();
            i++;
        }
        return NPlans;
    }


    private NPlan merge(NPlan p1, NPlan p2) {
        NPlan p3 = new NPlan();
        for(Map.Entry<NArtifact, Integer> a1 : p1.getArtifacts().entrySet()){
            p3.copy(a1.getKey(), a1.getValue());
        }
        for(Map.Entry<NArtifact, Integer> a1 : p2.getArtifacts().entrySet()){
            p3.copy(a1.getKey(), a1.getValue());
        }
        return p3;
    }

    public void describe() {
        System.out.print("Intermediates size: ");
        System.out.print(intermediates.size());
    }

    public void generate_random_graph_AND_OR(int T, int N, int depth, int share_ability, int and_prop) {

        this.maxDepth = depth;
        int randomCost;
        Random rand = new Random();
        this.root = new NArtifact(NArtifact.NodeType.ROOT);
        int pos = 0;
        HashMap<NArtifact, Integer> previous = new HashMap<NArtifact, Integer>();
        previous.put(root, rand.nextInt(10));

        for (int i = 0; i <= depth; i++) {
            HashMap<NArtifact, Integer> next = new HashMap<NArtifact, Integer>();
            int maxIN = rand.nextInt(N) + 1;
            if (i == depth) {
                maxIN = T;
            }
            for (int j = 0; j < maxIN; j++) {

                HashMap<NArtifact, Integer> randomSet = randomSubSet(share_ability, previous);
                randomCost = rand.nextInt(100);
                NArtifact tmp;
                if (randomSet.size() > 1 && rand.nextInt(100) < and_prop) {
                    tmp = new NArtifact(UUID.randomUUID(), ++pos, NArtifact.NodeType.ANDNODE, false, 10, 10, randomSet, null);
                } else {
                    tmp = new NArtifact(UUID.randomUUID(), ++pos, NArtifact.NodeType.ORNODE, false, 10, 10, randomSet, null);
                }
                addChildren(randomSet, tmp);
                if (i == depth){
                    tmp.setType(NArtifact.NodeType.LEAF);
                }
                next.put(tmp, randomCost);
            }
            if (i < depth & i > 0) {
                intermediates.addAll(previous.keySet());
            } else if (i == depth) {
                intermediates.addAll(previous.keySet());
                Terminals.addAll(next.keySet());
            }
            previous = next;
        }
    }


    public void generate_random_limited_graph_AND_OR(int T, int N, int depth, int materialization, int and_prop) {
        this.maxDepth = depth;
        int randomCost;
        Random rand = new Random();
        this.root = new NArtifact(NArtifact.NodeType.ROOT);
        int pos = 0;
        HashMap<NArtifact, Integer> previous = new HashMap<NArtifact, Integer>();
        previous.put(root, rand.nextInt(10));

        for (int i = 0; i <= depth; i++) {
            HashMap<NArtifact, Integer> next = new HashMap<NArtifact, Integer>();
            int maxIN = rand.nextInt(N) + 1;
            if (i == depth) {
                maxIN = T;
            }
            for (int j = 0; j < maxIN; j++) {

                HashMap<NArtifact, Integer> randomSet = null;
                randomCost = rand.nextInt(100);
                NArtifact tmp;
                if (rand.nextInt(100) < and_prop && i > 0) {
                    randomSet = randomSubSet_atleastN(2, previous);
                    tmp = new NArtifact(UUID.randomUUID(), ++pos, NArtifact.NodeType.ANDNODE, false, 0, 10, randomSet, null);
                } else {
                    int l_cost = rand.nextInt(10);
                    int c_cost = rand.nextInt(10);
                    randomSet = randomArtifact_plusRoot(this.root, materialization, l_cost, c_cost, previous);
                    tmp = new NArtifact(UUID.randomUUID(), ++pos, NArtifact.NodeType.ORNODE, false, l_cost, c_cost, randomSet, null);
                }
                addChildren(randomSet, tmp);
                if (i == depth) {
                    tmp.setType(NArtifact.NodeType.LEAF);
                }
                next.put(tmp, randomCost);
            }
            if (i < depth & i > 0) {
                intermediates.addAll(previous.keySet());
            } else if (i == depth) {
                intermediates.addAll(previous.keySet());
                Terminals.addAll(next.keySet());
            }
            previous = next;
        }
    }

    private HashMap<NArtifact, Integer> randomArtifact_plusRoot(NArtifact root, int materialization, int load_cost, int compute_cost, HashMap<NArtifact, Integer> set) {
        Random rand = new Random();
        HashMap<NArtifact, Integer> new_set = new HashMap<NArtifact, Integer>();
        int coin = rand.nextInt(100);
        if (coin < materialization) {
            new_set.put(root, load_cost);
        }
        for (Map.Entry<NArtifact, Integer> a : set.entrySet()) {
            if (coin < 50) {
                new_set.put(a.getKey(), compute_cost);
                break;
            }
            coin = rand.nextInt(100);
        }
        if (new_set.isEmpty()) {
            Map.Entry<NArtifact, Integer> entry = set.entrySet().iterator().next();
            new_set.put(entry.getKey(), entry.getValue());
        }
        return new_set;
    }

    public HashMap<NArtifact, Integer> randomSubSet(int share_ability, HashMap<NArtifact, Integer> set) {
        Random rand = new Random();
        HashMap<NArtifact, Integer> new_set = new HashMap<NArtifact, Integer>();
        int coin = rand.nextInt(100);
        for (Map.Entry<NArtifact, Integer> a : set.entrySet()) {
            if (coin < share_ability) {
                new_set.put(a.getKey(), a.getValue());
            }
            coin = rand.nextInt(100);
        }
        if (new_set.isEmpty()) {
            Map.Entry<NArtifact, Integer> entry = set.entrySet().iterator().next();
            new_set.put(entry.getKey(), entry.getValue());
        }
        return new_set;
    }

    public HashMap<NArtifact, Integer> randomSubSet_atleastN(int N, HashMap<NArtifact, Integer> set) {
        Random rand = new Random();
        HashMap<NArtifact, Integer> new_set = new HashMap<NArtifact, Integer>();
        int coin = rand.nextInt(100);
        int i = 0;
        for (Map.Entry<NArtifact, Integer> a : set.entrySet()) {
            if (i < N) {
                new_set.put(a.getKey(), a.getValue());
                i++;
            } else if (coin < 50) {
                new_set.put(a.getKey(), a.getValue());
            }
            coin = rand.nextInt(100);
        }
        if (new_set.isEmpty()) {
            Map.Entry<NArtifact, Integer> entry = set.entrySet().iterator().next();
            new_set.put(entry.getKey(), entry.getValue());
        }
        return new_set;
    }

    public int ExpectedNumberOfPlans() {
        int k = 1;
        for (NArtifact intermediate : intermediates) {
            if (intermediate.isOR()) {
                k = k * intermediate.getParents().size();
            }
        }
        return k;
    }

    public ArrayList<NPlan> Exhaustive_Search(int i) {

        if (i == intermediates.size()) {
            ArrayList<NPlan> result1 = new ArrayList<>();
            NPlan p = new NPlan();
            p.copy(root, 0);
            result1.add(p);
            return result1;
        }
        ArrayList<NPlan> next = Exhaustive_Search(i + 1);
        ArrayList<NPlan> result = new ArrayList<>();
        NArtifact current = intermediates.get(i);
        if (current.isAND()) {
            for (NPlan p1 : next) {
                p1.copy(current, 0);
                result.add(p1);
            }
        } else if (current.isOR()) {
            for (Map.Entry<NArtifact, Integer> parent : current.getParents().entrySet()) {
                for (NPlan p1 : next) {
                    NPlan p2 = new NPlan(p1);
                    p2.copy(new NArtifact(current, parent), parent.getValue());
                    result.add(p2);
                }
            }
        } else {
            return next;
        }
        return result;
    }

    private static void describe(ArrayList<NPlan> NPlans) {
        for (NPlan p1 : NPlans) {
            System.out.println(p1.toString());
        }

    }

    public DirectedGraph toHelixGraph(int sink) {
        int infinite = 1000000;
        int source = 0;
        DirectedGraph g = new DirectedGraph();
        for (NArtifact a : intermediates) {

            int l = a.getMaterializeCost();
            int c = a.getComputeCost();
            int a_0 = a.getPosition() * 2;
            int b_0 = a.getPosition() * 2 + 1;

            //a connected to sink
            g.addEdge(a_0, sink, a.getMaterializeCost());
            //b connected to a
            g.addEdge(b_0, a_0, infinite);
            //b connected to sink
            if(a.getComputeCost() > 0){
                if(l - c >= 0){
                    g.addEdge(source, b_0, l - c);
                }else{
                    g.addEdge(b_0, sink, -(l - c));
                }
                //b connected to parents
                for (NArtifact parent : a.getParents().keySet()) {
                    g.addEdge(b_0, parent.getPosition() * 2, infinite);
                }
            }
        }
        for (NArtifact a :Terminals){

            int l =infinite;
            int c = a.getComputeCost();
            int a_0 = a.getPosition() * 2;
            int b_0 = a.getPosition() * 2 + 1;

            //b connected to a
            g.addEdge(b_0, a_0, infinite);
            //b connected to sink
            if(a.getComputeCost() > 0){
                if(l - c >= 0){
                    g.addEdge(source, b_0, l - c);
                }else{
                    g.addEdge(b_0, sink, -(l - c));
                }
                //b connected to parents
                for (NArtifact parent : a.getParents().keySet()) {
                    g.addEdge(b_0, parent.getPosition() * 2, infinite);
                }
            }
        }
        return g;
    }

    public int getSink(){
        int max = 0;
        for (NArtifact Terminal : Terminals) {
           if(max < Terminal.getPosition()){
               max = Terminal.getPosition();
           }
        }
        return max;
    }

    public void printGraph() {
    }
}



