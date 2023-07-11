import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;
import HyperGraph.Proposition;
import java.util.ArrayList;

public class TxtToAG_AG_EQ {

    public static void main(String[] args) {
        lib utils = new lib();
        //String uid = "AG_3";
        String uid = "AG_3_1000";
        HyperGraph AG = utils.TxtToHyperGraph("C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\graphs\\"+uid+"_EDGES_AG_100_eq_classifier_breast_cancer.txt","time");
        HyperGraph AG_2 = utils.TxtToHyperGraph("C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\graphs\\"+uid+"_EDGES_AG_100_lt_classifier_breast_cancer.txt","time");
        System.out.println("EQ graph description");
        AG.stats();
        System.out.println("LT graph description");
        System.out.println("LT Graph statistics");
        AG_2.stats();
        int number_of_experiments = 100;

        int number_of_requests = 10;

        //AG.print();


        AG.compute_the_latency_of_artifacts();

        ArrayList<String> requests = new ArrayList<String>();
        requests.add("Ra_0.965");
        requests.add("Lo_0.982");
        requests.add("KN_0.956");
        requests.add("De_0.939");
        requests.add("SV_0.965");
        //AG.setRequests(requests);
        AG.setAllRequests();
        ArrayList<Artifact> a_requests = null;
        int latency = AG.getLatency(a_requests);
        System.out.println("latency EQ equals = "+ latency);
        Proposition_plan best_c_Plan = AG.expand_closest_proposition(a_requests);
        ArrayList<ArrayList<Artifact>> propositions = new ArrayList<>();
        AG.generatePropositionArtifacts(0, propositions,a_requests );
        System.out.println(a_requests.get(0).getLatency_cost());
        System.out.println("EQ Graph statistics");
        AG.stats();



        AG.compute_the_latency_of_artifacts();

        ArrayList<String> requests_2 = new ArrayList<String>();
        requests_2.add("PoSiSeMiKMcl");
        requests_2.add("SiDe");

        AG_2.setRequests(requests_2);
        AG_2.setAllRequests();

        int lt_latency = AG.getLatency(a_requests);
        System.out.println("LT latency equals = "+ lt_latency);

        ArrayList<Artifact> a_requests_2 = null;
        //find the best plan!!!
        int minimum_cost = 1000000;
        Proposition_plan tmp = null;
        for(ArrayList<Artifact> request_names: propositions) {

            ArrayList<Artifact> request_test = AG_2.getRequests(request_names);
            if(request_test.size()<number_of_requests)
                continue;
            Proposition_plan best_c_Plan_2 = AG_2.expand_closest_proposition(request_test);
            if(best_c_Plan_2.getCost() < minimum_cost){
                minimum_cost = best_c_Plan_2.getCost();
                tmp = best_c_Plan_2;
            }
        }
        int propositions_latency  = tmp.getLatency();
        System.out.println("Minimum plan proposition latency  = "+ propositions_latency);
        System.out.println("Equivalent benefit  = "+ (best_c_Plan.getCost()  - tmp.getCost()));

    }

}