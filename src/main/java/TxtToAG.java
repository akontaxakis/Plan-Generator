import HyperGraph.HyperGraph;
import util.lib;

public class TxtToAG {

        public static void main(String[] args) {
        lib utils = new lib();

        HyperGraph AG = utils.TxtToHyperGraph("C:\\Users\\adoko\\PycharmProjects\\pythonProject1\\graphs\\EDGES_AG_32_classifier_breast_cancer.txt");
        //AG.print();
        AG.compute_the_latency_of_artifacts();
        AG.printSharableArtifacts();



        }

        }