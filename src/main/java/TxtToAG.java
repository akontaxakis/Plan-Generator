import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;

public class TxtToAG {

    public static void main(String[] args) {
        lib utils = new lib();

        HyperGraph AG = utils.TxtToHyperGraph("C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\graphs\\447856ae_EDGES_AG_177_eq_classifier_breast_cancer.txt","time");
        //AG.print();
        AG.compute_the_latency_of_artifacts();
        AG.printSharableArtifacts();
        AG.printEquivilantArtifacts();

        ArrayList<String> requests = new ArrayList<String>();
        requests.add("Ra_0.939");
        requests.add("SV_0.886");

        AG.setRequests(requests);
        ArrayList<Artifact> a_requests = AG.getRequests();
        Proposition_plan best_c_Plan = AG.expand_closest_proposition(a_requests);

        best_c_Plan.print();
        System.out.println(a_requests.get(0).getLatency_cost());
        System.out.println(a_requests.get(1).getLatency_cost());


        HyperGraph AG_2 = utils.TxtToHyperGraph("C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\graphs\\447856ae_EDGES_AG_177_classifier_breast_cancer.txt","time");
        ArrayList<String> requests_2 = new ArrayList<String>();
        requests_2.add("PoStSiPCcoSestRa");
        requests_2.add("PoTrcoSiStSestSV");

        AG_2.setRequests(requests_2);
        ArrayList<Artifact> a_requests_2 = AG_2.getRequests();

        ArrayList<Artifact> loaded_artifacts = AG_2.collab_forward_pass();
        HyperGraph collab_plan = AG_2.collab_backward_pass(loaded_artifacts, a_requests_2);
        collab_plan.print();
        System.out.println("collab Cost: " + collab_plan.EdgeCost());
    }

}