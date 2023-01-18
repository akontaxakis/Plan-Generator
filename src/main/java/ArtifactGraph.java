import java.util.*;

public class ArtifactGraph {

    private ArrayList<Artifact> intermediates;
    private Artifact root;
    private Artifact Request;
    private ArrayList<Artifact> Terminals;
    private int maxDepth=0;

    public ArtifactGraph(ArrayList<Artifact> intermediates, Artifact root, ArrayList<Artifact> terminals) {
        this.intermediates = intermediates;
        this.root = root;
        Terminals = terminals;
    }

    public ArtifactGraph() {
        intermediates = new ArrayList<Artifact>();
        Terminals = new ArrayList<Artifact>();
    }

    //N: number of artifacts to be generated
    //depth of the graph
    //maximum in degree of an artifact
    //how often do artifacts are shared by other artifacts
    public void generate_random_graph(int N, int depth){
        this.maxDepth = depth;
        Random rand = new Random();
        this.root = new Artifact(Artifact.NodeType.ROOT);
        int pos=0;
        HashMap<Artifact, Integer> previous = new HashMap<Artifact, Integer>();
        previous.put(root,rand.nextInt(10));

        for(int i=0;i<=depth;i++) {
            HashMap<Artifact, Integer> next = new HashMap<Artifact, Integer>();
            int maxIN=rand.nextInt(N)+1;
            for (int j = 0; j < maxIN; j++) {
                    Artifact tmp = new Artifact(UUID.randomUUID(), ++pos, Artifact.NodeType.ORNODE, true, 10, previous, null);
                    if(i==depth) {
                        tmp.setType(Artifact.NodeType.LEAF);
                    }
                    next.put(tmp,rand.nextInt(10));
            }
            for (Artifact prev : previous.keySet()) {
                prev.setChildren(next);
            }
            if(i<depth & i>0){
                intermediates.addAll(previous.keySet());
            }else if(i>0){
                intermediates.addAll(previous.keySet());
                Terminals.addAll(next.keySet());
            }
            previous=next;
        }
    }
    public void generate_random_graph_2(int N, int depth, int share_ability){
        this.maxDepth = depth;
        int randomCost;
        Random rand = new Random();
        this.root = new Artifact(Artifact.NodeType.ROOT);
        int pos=0;
        HashMap<Artifact, Integer> previous = new HashMap<Artifact, Integer>();
        previous.put(root,rand.nextInt(10));

        for(int i=0;i<=depth;i++) {
            HashMap<Artifact, Integer> next = new HashMap<Artifact, Integer>();
            int maxIN=rand.nextInt(N)+1;
            for (int j = 0; j < maxIN; j++) {

                HashMap<Artifact, Integer> randomSet = randomSubSet(share_ability,previous);
                Artifact tmp = new Artifact(UUID.randomUUID(), ++pos, Artifact.NodeType.ORNODE, false, 10,randomSet, null);
                randomCost =rand.nextInt(100);
                addChildren(randomSet,tmp,randomCost);
            if(i==depth) {
                    tmp.setType(Artifact.NodeType.LEAF);
                }
                next.put(tmp,randomCost);
            }
            if(i<depth & i>0){
                intermediates.addAll(previous.keySet());
            }else if(i>0){
                intermediates.addAll(previous.keySet());
                Terminals.addAll(next.keySet());
            }
            previous=next;
        }
}


private void addChildren(HashMap<Artifact, Integer> randomSet, Artifact tmp, Integer cost) {
        for(Map.Entry<Artifact,Integer> a :randomSet.entrySet()){
            a.getKey().addChild(tmp,cost);
        }

    }
    void updateAdditiveCost(){
        this.root.setAdditiveCost(0);
        findMinAddativeCost(intermediates);
        findMinAddativeCost(Terminals);

    }

    private void findMinAddativeCost(ArrayList<Artifact> intermediates) {
        for (Artifact intermediate : intermediates) {
            int min = 1000000;
            if(intermediate.isAND()){
                intermediate.setAdditiveCost(getSUM(intermediate));
            }
            else{
                intermediate.setAdditiveCost(getMin(intermediate));
            }
        }
    }

    private int getSUM(Artifact intermediate) {
        int sum=0;
        for(Map.Entry<Artifact,Integer> a : intermediate.getParents().entrySet()){
            sum = sum + a.getKey().getAdditiveCost()+a.getValue();
            }
        return sum;
    }

    private int getMin(Artifact intermediate) {
        int min=10000000;
        for(Map.Entry<Artifact,Integer> a : intermediate.getParents().entrySet()){
            if(min >a.getKey().getAdditiveCost()+a.getValue()){
                min =a.getKey().getAdditiveCost()+a.getValue();
            }
        }
        return min;
    }

    void sortArtifacts(){
        System.out.println("before");
        for (Artifact intermediate : intermediates) {
            System.out.println(intermediate.getPosition());
        }
        Collections.sort(intermediates, new Artifact.SortbyPOS());
        System.out.println("after");
        for (Artifact intermediate : intermediates) {
            System.out.println(intermediate.getPosition());
        }
    }

    void print(){
        System.out.println(root.toString());
        System.out.println("Intermediates.toString()");
        for (Artifact intermediate : intermediates) {
            System.out.println(intermediate.toString());
        }
        System.out.println("Terminal.toString()");
        for (Artifact Terminal : Terminals) {
           System.out.println(Terminal.toString());
        }
    }
    void printLikeAgraph(){
        System.out.println(root.printPosAndChildren());
        System.out.println("Intermediates.toString()");
        for (Artifact intermediate : intermediates) {
            System.out.println(intermediate.printPosAndChildren());
        }
        System.out.println("Terminal.toString()");
        for (Artifact Terminal : Terminals) {
            System.out.println(Terminal.printPosAndChildren());
        }
    }

    public HashMap<Artifact, Integer> randomSubSet(int share_ability, HashMap<Artifact, Integer> set){
        Random rand = new Random();
        HashMap<Artifact,Integer> new_set = new HashMap<Artifact,Integer>();
        int coin = rand.nextInt(100);
        for(Map.Entry<Artifact,Integer> a : set.entrySet()){
           if (coin<share_ability){
                new_set.put(a.getKey(),a.getValue());
            }
            coin = rand.nextInt(100);
        }
        if(new_set.isEmpty()){
            Map.Entry<Artifact,Integer> entry = set.entrySet().iterator().next();
            new_set.put(entry.getKey(),entry.getValue());
        }
        return new_set;
    }

    public ArrayList<Artifact> getSomeTerminals(int i) {
    ArrayList<Artifact> rqs = new ArrayList<>();
    int j=0;
    for(Artifact a: Terminals){
        if(j==i)
            break;
        rqs.add(a);
        j++;
    }
        return rqs;
    }

    public ArrayList<Plan> getPlans(Artifact a) {
        Plan p = new Plan();
        ArrayList<Plan> plans = new ArrayList<Plan>();
        //INITIATE WITH THE LOAD THAT HAS NO PARENTS
        if (a.getParents() == null) {
            p.copy(a, 0);
            plans.add(p);
            return plans;
        }
        int parents_size = a.getParents().size();
        int i =0;
        ArrayList<Plan> tmp_list = new ArrayList<Plan>();
        for (Map.Entry<Artifact,Integer> tmp : a.getParents().entrySet()) {
            ArrayList<Plan> tmp_plans = getPlans(tmp.getKey());
            //CURRENT NODE IS AND WE MERGE P1XP2
            if (a.isAND()) {
                for (Plan p1 : tmp_plans) {
                    for (Plan p2 : plans) {
                        Plan p3 = merge(p1, p2);
                        p3.copy(a, tmp.getValue());
                        tmp_list.add(p3);
                    }
                }
            } else {
                //CURRENT NODE IS AND WE MERGE P1+P2
                for (Plan p1 : tmp_plans) {
                    p1.copy(a, tmp.getValue());
                    plans.add(p1);
                }
            }
            if(i==parents_size-1){
                if(a.isAND()){
                    plans.clear();
                    plans=tmp_list;
                    tmp_list = new ArrayList<Plan>();
                }
            }
            tmp_plans.clear();
            i++;
        }

        return plans;
    }


    private Plan merge(Plan p1, Plan p2) {
        Plan p3 = new Plan();
        for(Map.Entry<Artifact,Integer> a1:p1.getArtifacts().entrySet()){
              p3.copy(a1.getKey(),a1.getValue());
        }
        for(Map.Entry<Artifact,Integer> a1:p2.getArtifacts().entrySet()){
            p3.copy(a1.getKey(),a1.getValue());
        }
        return p3;
    }

    public void describe() {
       // System.out.println(root.toString());
        System.out.print("Intermediates size: ");
        System.out.print(intermediates.size());

       // System.out.print(" - Terminals size: ");
      //  System.out.print(Terminals.size() +"\n");

    }

    public void generate_random_graph_AND_OR(int T, int N, int depth, int share_ability, int and_prop) {

        this.maxDepth = depth;
        int randomCost;
        Random rand = new Random();
        this.root = new Artifact(Artifact.NodeType.ROOT);
        int pos=0;
        HashMap<Artifact, Integer> previous = new HashMap<Artifact, Integer>();
        previous.put(root,rand.nextInt(10));

        for(int i=0;i<=depth;i++) {
            HashMap<Artifact, Integer> next = new HashMap<Artifact, Integer>();
            int maxIN=rand.nextInt(N)+1;
            if(i==depth){
                maxIN=T;
            }
            for (int j = 0; j < maxIN; j++) {

                HashMap<Artifact, Integer> randomSet = randomSubSet(share_ability,previous);
                randomCost =rand.nextInt(100);
                Artifact tmp;
                if(randomSet.size()>1 && rand.nextInt(100) <and_prop) {
                    tmp = new Artifact(UUID.randomUUID(), ++pos, Artifact.NodeType.ANDNODE, false, 10, randomSet, null);
                }else{
                    tmp = new Artifact(UUID.randomUUID(), ++pos, Artifact.NodeType.ORNODE, false, 10, randomSet, null);
                }

                addChildren(randomSet,tmp,randomCost);
                if(i==depth) {
                    tmp.setType(Artifact.NodeType.LEAF);
                }
                next.put(tmp,randomCost);
            }
            if(i<depth & i>0){
                intermediates.addAll(previous.keySet());
            }else if(i==depth){
                intermediates.addAll(previous.keySet());
                Terminals.addAll(next.keySet());
            }
            previous=next;
        }
    }
    public int ExpectedNumberOfPlans() {
        int k = 1;
        for (Artifact intermediate : intermediates) {
            if (intermediate.isOR()) {
                k = k * intermediate.getParents().size();
            }
        }
        return k;
    }

    public ArrayList<Plan> Exhaustive_Search(int i) {

            if (i == intermediates.size()) {
                ArrayList<Plan> result = new ArrayList<>();
                Plan p = new Plan();
                p.copy(root, 0);
                result.add(p);
                return result;
            }
            ArrayList<Plan> next = Exhaustive_Search(i + 1);

            ArrayList<Plan> result = new ArrayList<>();
            Artifact current = intermediates.get(i);
            if(current.isAND()) {
                for (Plan p1 : next) {
                    p1.copy(current, 0);
                    result.add(p1);
                }
            }else if(current.isOR()){

                for (Map.Entry<Artifact, Integer> parent: current.getParents().entrySet()) {
                    for (Plan p1 : next) {

                        p1.copy(new Artifact(current,parent), parent.getValue());
                        result.add(p1);
                    }
                }
            }else{
                return next;
            }
            return result;
        }
}



