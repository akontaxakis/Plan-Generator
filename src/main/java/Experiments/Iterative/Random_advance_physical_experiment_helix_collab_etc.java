package Experiments.Iterative;

import Entities.Artifact;
import Entities.Proposition_plan;
import HyperGraph.HyperGraph;
import util.lib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Random_advance_physical_experiment_helix_collab_etc {


    public static void main(String[] args) {
        lib utils = new lib();
       // double dataset_size_bytes = 4480000; // your dataset size here
       // double dataset_size = 4480000/1000000;

        double dataset_size_bytes = 1600000; // your dataset size here
        double dataset_size = 1600000/1000000;

        //double dataset_size_bytes = 17600000; // your dataset size here
        //double dataset_size = 17600000/1000000;


        // Define the budget array
        //double[] Budget = new double[] {
        //        0, dataset_size / 1000, dataset_size / 500, dataset_size / 200, dataset_size / 100, dataset_size / 50, dataset_size / 20, dataset_size / 10, dataset_size / 2, dataset_size, dataset_size*2,dataset_size * 5, dataset_size * 10, dataset_size * 100
        //};
        double[] Budget = new double[] {
                dataset_size / 10
        };
        int[] iterations2 = new int[] {
                10, 20, 50, 100, 200
        };

        String uid = "ex100";
        uid = "materialization_2";
        double edge_cost_modifier = 1000;
        int N =5;
        int iterations =200;
        //[0,dataset_size / 100000, dataset_size / 10000, dataset_size / 1000, dataset_size / 100, dataset_size / 10,dataset_size, dataset_size * 10, dataset_size * 100]
        int b = Budget.length;
        int I = iterations2.length;
        Set<String> lt_uniqueRequests = new HashSet<>();
        Set<String> eq_uniqueRequests = new HashSet<>();

        String project_path= "C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\";

        for(int j=0;j< iterations2.length;j++){
            iterations = iterations2[j];
        for(int budget=0; budget<b;budget++) {

            double t_shared_cummulative_cost = 0;
            double t_collab_cummulative_cost = 0;
            double t_helix_cummulative_cost = 0;
            double t_equivalent_cummulative_cost = 0;
            double t_mat_cummulative_cost = 0;
            double t_c_shared_cummulative_cost = 0;
            double t_c_collab_cummulative_cost = 0;
            double t_c_mat_cummulative_cost = 0;
            double t_c_helix_cummulative_cost = 0;
            double t_c_equivalent_cummulative_cost = 0;

            for (int i = 0; i < N; i++) {


                //System.out.println("Budgets -> " + budget);
                double shared_cummulative_cost = 0;
                double helix_cummulative_cost = 0;
                double collab_cummulative_cost = 0;
                double mat_cummulative_cost = 0;
                double equivalent_cummulative_cost = 0;

                for (int iteration = 0; iteration < iterations; iteration++) {
                    ////////////////////////////READ THE GRAPHS////////////////////////////////////
                    HyperGraph shared = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + i, iteration, "shared");
                    //HyperGraph limited = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget+ "_" +i+"_1", iteration, "limited");
                    //HyperGraph equivalent = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget+ "_" +i+"_1", iteration, "equivalent");

                    HyperGraph helix = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + i, iteration, "helix");
                    HyperGraph collab = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + i, iteration, "limited");
                    HyperGraph mat = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + i, iteration, "limited");
                    HyperGraph equivalent = utils.Artifacts_and_edges_ToHyperGraph(project_path, uid + "_" + budget + "_" + i, iteration, "equivalent");

                    ////////////////////////////REMOVE SUPER NODES/////////////////////////////////
                    HyperGraph true_helix = utils.TurnToTrueHG(helix);
                    HyperGraph true_collab = utils.TurnToTrueHG(collab);
                    HyperGraph true_mat = utils.TurnToTrueHG(mat);
                    HyperGraph true_shared = utils.TurnToTrueHG(shared);
                    HyperGraph true_equivalent = utils.TurnToTrueHG(equivalent);

                    ////////////////////////////READ REQUESTS///////////////////////////////////
                    //ArrayList<String> ltSet = utils.read_requests(project_path, iteration, uid + "_" + budget+ "_" +i+"_1");
                    //ArrayList<String> eqSet_ = utils.read_requests(project_path, iteration, uid + "_eq_" + budget+ "_" +i+"_1");

                    ArrayList<String> ltSet = utils.read_requests(project_path, iteration, uid + "_" + budget + "_" + i);
                    ArrayList<String> eqSet_ = utils.read_requests(project_path, iteration, uid + "_eq_" + budget + "_" + i);
                    String operation = ltSet.remove(ltSet.size() - 1);
                    double lt_extra_cost = Double.parseDouble(ltSet.remove(ltSet.size() - 1));

                    if (ltSet.size() == 0) {
                        ltSet.add(operation);
                    }

                    eqSet_.remove(eqSet_.size() - 1);
                    double eq_extra_cost = Double.parseDouble(eqSet_.remove(eqSet_.size() - 1));
                    if (eqSet_.size() == 0) {
                        eqSet_.add(operation);
                    }


                    ArrayList<String> eqSet = utils.euqivalentRequests(eqSet_);
                    //String operation = "X_SiStPCLi_predict";

                    ArrayList<Artifact> he_rq = true_helix.getRequests(new HashSet<>(ltSet));
                    ArrayList<Artifact> mat_rq = true_mat.getRequests(new HashSet<>(ltSet));
                    ArrayList<Artifact> co_rq = true_collab.getRequests(new HashSet<>(ltSet));

                    ArrayList<Artifact> sh_rq = true_shared.getRequests(operation);
                    ArrayList<Artifact> eq_rq = true_equivalent.getRequests(new HashSet<>(eqSet));

                    Proposition_plan sh_plan = true_shared.expand_closest_proposition(sh_rq);
                    Proposition_plan mat_plan = true_mat.expand_closest_proposition(mat_rq); //update algo


                    ArrayList<Artifact> loaded_artifacts = true_collab.collab_forward_pass();
                    HyperGraph collab_plan = true_collab.collab_backward_pass(loaded_artifacts, co_rq);


                    Proposition_plan he_plan = true_collab.expand_closest_proposition(he_rq);//update algo
                    Proposition_plan eq_plan = true_equivalent.expand_closest_proposition(eq_rq);

                    double sh_cost = sh_plan.getCost();
                    double co_cost = collab_plan.EdgeCost() + (lt_extra_cost * 1000);
                    double mat_cost = mat_plan.getCost() + (lt_extra_cost * 1000);
                    double he_cost = he_plan.getCost() + (lt_extra_cost * 1000);
                    double eq_cost = eq_plan.getCost() + (eq_extra_cost * 1000);


                    shared_cummulative_cost = shared_cummulative_cost + sh_cost;
                    collab_cummulative_cost = collab_cummulative_cost + co_cost;
                    mat_cummulative_cost = mat_cummulative_cost + mat_cost;
                    helix_cummulative_cost = helix_cummulative_cost + he_cost;
                    equivalent_cummulative_cost = equivalent_cummulative_cost + eq_cost;
                    if (iteration < iterations - 1)
                        lt_uniqueRequests.add(operation);


                    if (iteration == iterations - 1) {
                        //System.out.println(budget + "," + N + "," + "Equivalent");
                        //utils.Stats_on_Stored_Artifacts(true_limited);
                    }
                }
                //System.out.println(budget + "," + shared_cummulative_cost + "," + limited_cummulative_cost + "," + equivalent_cummulative_cost);
                double budget_cost = 0;
                double dataset_budget_cost = dataset_size * 0.023;
                double computation_cost_per_hour = 0.63;
                double storage_cost_per_gb = 0.023;
                double computation_cost_per_second = computation_cost_per_hour / 3600;
                double shared_cost = shared_cummulative_cost / edge_cost_modifier * computation_cost_per_second;
                //   Budget = [dataset_size / 10, dataset_size, dataset_size * 10, dataset_size * 100,  dataset_size * 1000]


                budget_cost = ((Budget[budget]) * storage_cost_per_gb);
                double collab_cost = collab_cummulative_cost / edge_cost_modifier * computation_cost_per_second + budget_cost;
                double mat_cost = mat_cummulative_cost / edge_cost_modifier * computation_cost_per_second + budget_cost;
                double helix_cost = helix_cummulative_cost / edge_cost_modifier * computation_cost_per_second + budget_cost;
                double equivalent_cost = equivalent_cummulative_cost / edge_cost_modifier * computation_cost_per_second + budget_cost;
                //System.out.println(i + "," + shared_cummulative_cost / edge_cost_modifier + "," + limited_cummulative_cost / edge_cost_modifier + "," + equivalent_cummulative_cost / edge_cost_modifier + "," + shared_cost + "," + limited_cost + "," + equivalent_cost);
                //System.out.println(i + "," + shared_cummulative_cost / edge_cost_modifier + "," + limited_cummulative_cost / edge_cost_modifier + "," + equivalent_cummulative_cost / edge_cost_modifier );
                //Artifact Retrieval Example

                t_shared_cummulative_cost = t_shared_cummulative_cost + shared_cummulative_cost / edge_cost_modifier;
                t_mat_cummulative_cost = t_mat_cummulative_cost + collab_cummulative_cost / edge_cost_modifier;
                t_equivalent_cummulative_cost = t_equivalent_cummulative_cost + equivalent_cummulative_cost / edge_cost_modifier;
                t_helix_cummulative_cost = t_helix_cummulative_cost + helix_cummulative_cost / edge_cost_modifier;
                t_collab_cummulative_cost = t_collab_cummulative_cost + collab_cummulative_cost / edge_cost_modifier;

                t_c_shared_cummulative_cost = t_c_shared_cummulative_cost + shared_cost;
                t_c_equivalent_cummulative_cost = t_c_equivalent_cummulative_cost + equivalent_cost;
                t_c_mat_cummulative_cost = t_c_mat_cummulative_cost + collab_cost;
                t_c_collab_cummulative_cost = t_c_collab_cummulative_cost + collab_cost;
                t_c_helix_cummulative_cost = t_c_helix_cummulative_cost + helix_cost;

            }
            //System.out.println(budget + "," + t_shared_cummulative_cost/N+ "," + t_helix_cummulative_cost/N + ","+t_collab_cummulative_cost/N + ","+t_mat_cummulative_cost/N +","+ t_equivalent_cummulative_cost/N + "," + t_c_shared_cummulative_cost/N + "," + t_c_collab_cummulative_cost/N + "," + t_c_helix_cummulative_cost/N + "," + t_c_equivalent_cummulative_cost/N);
            //System.out.println(iterations + "," + t_shared_cummulative_cost / N + "," + t_helix_cummulative_cost / N + "," + t_collab_cummulative_cost / N + "," + t_mat_cummulative_cost / N + "," + t_equivalent_cummulative_cost / N);
            System.out.println(iterations + "," + t_c_shared_cummulative_cost/N + "," +t_c_helix_cummulative_cost /N + "," + t_c_collab_cummulative_cost/N +"," + t_c_mat_cummulative_cost / N + "," + t_c_equivalent_cummulative_cost/N);



        }
        }






    }

}
