import DirectedGraph.DirectedGraph;
import DirectedGraph.MaximumFlow;
import java.util.ArrayList;
import java.util.HashMap;
import DirectedGraph.Edge;
import Entities.NArtifact;
import Entities.NPlan;

public class Limited_AG_test {
    public static void main(String[] args) {

        for (int l = 0; l < 5; l++) {
            int expected_size =1;
            System.out.println(" EXPERIMENT : " + (l + 1));
            long startTime = System.nanoTime();

            //ArtifactGraph AG = new ArtifactGraph();
            //AG.generate_random_graph(10, 10);

            ArtifactGraph AG = new ArtifactGraph();
            //AG_2.generate_random_graph_2(10,10,20);

            AG.generate_random_limited_graph_AND_OR(4, 3, 4, 50, 20);

            long endTime = System.nanoTime();

            System.out.println(" Time to Generate graph in milliseconds " + (endTime - startTime) / 1000000);

            AG.describe();

            ArrayList<NArtifact> rqs = AG.getSomeTerminals(4);
            /*
                            #### HEURISTIC ALGORITHM ####
            */
            startTime = System.nanoTime();


            //AG.printLikeAgraphP();

            AG.updateAdditiveCost();

            int sum = 0;
            for (NArtifact a : rqs) {
                sum = sum + a.getAdditiveCost();
            }

            endTime = System.nanoTime();

            System.out.println(" Time to run Heuristic in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The MIN Heuristic Cost is:" + sum);


/*
                            #### EXHAUSTIVE SEARCH ####
*/
            startTime = System.nanoTime();

            ArrayList<NPlan> NPlans =AG.Exhaustive_Search(0);

            endTime = System.nanoTime();

            //describe(Plans);

            System.out.println(" Exhaustive Search " + ((endTime - startTime) / 1000000));
            System.out.println(" Expected Size: " + AG.ExpectedNumberOfPlans() + " Actual Size = "+ NPlans.size());
            ArrayList<NPlan> validNPlans = validatePlans(NPlans);
            System.out.println(" Valid Plans = " + validNPlans.size());
            NPlan bestNPlan = minimumCostPlan(validNPlans);
            System.out.println(bestNPlan.toString());
            System.out.println(" Best Plan has a cost of = " + bestNPlan.getCost());




               /*
                            #### HELIX MAX_FLOW ALGORITHM SEARCH ####
               */

            startTime = System.nanoTime();

            DirectedGraph g = AG.toHelixGraph(AG.getSink()*2+1);
            MaximumFlow mx = new MaximumFlow(g);

            HashMap<Edge, Integer> flow = mx.getMaxFlow(0, AG.getSink()*2+1);
             mx.printFlow(flow,0);
            System.out.println(mx.getFlowSize(flow,0));

            endTime = System.nanoTime();

            //describe(Plans);

            System.out.println(" The MAX-FLOW " + ((endTime - startTime) / 1000000));

        }
    }


    private static NPlan minimumCostPlan(ArrayList<NPlan> validNPlans) {
        int min = 1000000;
        NPlan bestNPlan = new NPlan();
        for(NPlan p: validNPlans){
            if(p.getCost()<min){
                min = p.getCost();
                bestNPlan = p;
            }
        }
        return bestNPlan;
    }

    private static ArrayList<NPlan> validatePlans(ArrayList<NPlan> NPlans) {
        ArrayList<NPlan> validNPlans = new ArrayList<>();
        for(NPlan p: NPlans){
            if(p.isValid()) {
                NPlan fp=p.removeUnValidNodes();
                validNPlans.add(fp);
            }
        }
        return NPlans;

    }

    private static void describe(ArrayList<NPlan> NPlans){
        for(NPlan p1: NPlans){
            System.out.println(p1.toString());
        }

    }

    private static void PlansGenerationAndExhaustivelyCombiningPLans(long startTime, ArtifactGraph AG_2, ArrayList<NArtifact> rqs) {
        int sum;
        long endTime;
        int i = 0;
        sum = 0;

        ArrayList<NPlan> tmp = new ArrayList<>();
        ArrayList<NPlan> t_N_plans = new ArrayList<>();
        HashMap<NArtifact, ArrayList<NPlan>> plans = new HashMap<>();

        int min_single = 0;
        int sum_cost=0;
        for (NArtifact a : rqs) {
            ArrayList<NPlan> a_N_plans = AG_2.getPlans(a);
            min_single = minCost(a_N_plans);
            sum = sum + a_N_plans.size();
            sum_cost = sum_cost+min_single;
            plans.put(a, a_N_plans);
            if (i == 0) {
                t_N_plans = a_N_plans;
                i++;
            }
            tmp = a_N_plans;
        }
        System.out.println("Average number of plans per request: " + sum / rqs.size() + " Cost of Best Plans  " + sum_cost);

        endTime = System.nanoTime();

        System.out.println(" Time to generate all plans: " + ((endTime - startTime) / 1000000));

            /*
                            #### EXHAUSTIVE SEARCH #####
            */
        startTime = System.nanoTime();


        int min = 100000;
        for (NPlan p1 : t_N_plans) {
            NPlan tmp_p = new NPlan();
            tmp_p.copy(p1);
            for (NPlan p2 : tmp) {
                int combined_cost = tmp_p.findCombinedCost(p2);
                // System.out.println("combined_cost :"+combined_cost);
                if (min > combined_cost) {
                    min = combined_cost;
                }
            }
        }
        endTime = System.nanoTime();

        System.out.println(" Time to find the best plan: " + (endTime - startTime) / 1000000);

        System.out.println("Minimum combined Cost:" + min);
    }

    private static int minCost(ArrayList<NPlan> a_N_plans) {
        int min = 100000;
        for (NPlan p1 : a_N_plans) {
            if (min > p1.getCost()) {
                min = p1.getCost();
            }
        }
        return min;
    }



}

