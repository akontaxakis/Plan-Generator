import java.util.ArrayList;
import java.util.HashMap;

public class main {
    public static void main(String[] args) {

        for (int l = 0; l < 10; l++) {
            int expected_size =1;
            System.out.println(" EXPERIMENT : " + (l + 1));
            long startTime = System.nanoTime();

            //ArtifactGraph AG = new ArtifactGraph();
            //AG.generate_random_graph(10, 10);

            ArtifactGraph AG_2 = new ArtifactGraph();
            //AG_2.generate_random_graph_2(10,10,20);

            AG_2.generate_random_graph_AND_OR(4, 8, 5, 50, 10);

            long endTime = System.nanoTime();

            System.out.println(" Time to Generate graph in milliseconds " + (endTime - startTime) / 1000000);

            //AG_2.print();
            AG_2.describe();

            ArrayList<Artifact> rqs = AG_2.getSomeTerminals(2);
            /*
                            #### HEURISTIC ALGORITHM ####
            */
            startTime = System.nanoTime();

            AG_2.updateAdditiveCost();

            int sum = 0;
            for (Artifact a : rqs) {
                sum = sum + a.getAdditiveCost();
            }

            endTime = System.nanoTime();

            System.out.println(" Time to run Heuristic in milliseconds " + (endTime - startTime) / 1000000);
            System.out.println("The MIN Heuristic Cost is:" + sum);
            /*
                            #### PLANS GENERATION ####
            */
            startTime = System.nanoTime();

            ArrayList<Plan> Plans=AG_2.Exhaustive_Search(0);

            endTime = System.nanoTime();

            System.out.println(" Exhaustive Search " + ((endTime - startTime) / 1000000));
            System.out.println(" Expected Size: " + AG_2.ExpectedNumberOfPlans() + " Actual Size = "+ Plans.size());
            ArrayList<Plan> validPlans = validatePlans(Plans);
            System.out.println(" Valid Plans = " + validPlans.size());
            Plan bestPlan = minimumCostPlan(validPlans);
            System.out.println(bestPlan.toString());
            System.out.println(" Best Plan has a cost of = " + bestPlan.getCost());
            //PlansGenerationAndExhaustivelyCombiningPLans(startTime, AG_2, rqs);
        }
        //AG.sortArtifacts();
    }

    private static Plan minimumCostPlan(ArrayList<Plan> validPlans) {
        int min = 1000000;
        Plan bestPlan = new Plan();
        for(Plan p:validPlans){
            if(p.getCost()<min){
                min = p.getCost();
                bestPlan = p;
            }
        }
        return bestPlan;
    }

    private static ArrayList<Plan> validatePlans(ArrayList<Plan> plans) {
        ArrayList<Plan> validPlans = new ArrayList<>();
        for(Plan p: plans){
            if(p.isValid()) {
                Plan fp=p.removeUnValidNodes();
                validPlans.add(fp);
            }
        }
        return plans;

    }

    private static void PlansGenerationAndExhaustivelyCombiningPLans(long startTime, ArtifactGraph AG_2, ArrayList<Artifact> rqs) {
        int sum;
        long endTime;
        int i = 0;
        sum = 0;

        ArrayList<Plan> tmp = new ArrayList<>();
        ArrayList<Plan> t_plans = new ArrayList<>();
        HashMap<Artifact, ArrayList<Plan>> plans = new HashMap<>();

        int min_single = 0;
        int sum_cost=0;
        for (Artifact a : rqs) {
            ArrayList<Plan> a_plans = AG_2.getPlans(a);
            min_single = minCost(a_plans);
            sum = sum + a_plans.size();
            sum_cost = sum_cost+min_single;
            plans.put(a, a_plans);
            if (i == 0) {
                t_plans = a_plans;
                i++;
            }
            tmp = a_plans;
        }
        System.out.println("Average number of plans per request: " + sum / rqs.size() + " Cost of Best Plans  " + sum_cost);

        endTime = System.nanoTime();

        System.out.println(" Time to generate all plans: " + ((endTime - startTime) / 1000000));

            /*
                            #### EXHAUSTIVE SEARCH #####
            */
        startTime = System.nanoTime();


        int min = 100000;
        for (Plan p1 : t_plans) {
            Plan tmp_p = new Plan();
            tmp_p.copy(p1);
            for (Plan p2 : tmp) {
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

    private static int minCost(ArrayList<Plan> a_plans) {
        int min = 100000;
        for (Plan p1 : a_plans) {
            if (min > p1.getCost()) {
                min = p1.getCost();
            }
        }
        return min;
   }

}

