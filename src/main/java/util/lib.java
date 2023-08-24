package util;

import Entities.Artifact;
import Entities.HyperEdge;
import HyperGraph.HyperGraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class lib {


    public HashMap<String, ArrayList<String>> get_models_from_file(String fileName) {
        HashMap<String, ArrayList<String>> models = new HashMap<>();
        //"PolynomialFeatures()__SVC()__","0.9473684210526315","SV_0.947","0.013032674789428711"
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\",\"");
                String pipeline = extractFirstTwoChars(fields[0]);
                String model = fields[2];
                ArrayList<String> pipelines = models.get(model);
                if (pipelines == null) {
                    pipelines = new ArrayList();
                }
                if (!pipelines.contains(pipeline))
                    pipelines.add(pipeline);
                models.put(model, pipelines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return models;
    }

    public HyperGraph TxtToHyperGraph(String fileName, String cost) {
        int pos = 0;
        HashMap<String, Artifact> artifacts = new HashMap<>();
        ArrayList<HyperEdge> hyperEdges = new ArrayList<>();
        ArrayList<String> nodes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                Artifact a1;
                Artifact a2;
                fields[0] = fields[0].replaceAll("\"", "");
                fields[1] = fields[1].replaceAll("\"", "");

                if (nodes.contains(fields[0])) {
                    a1 = artifacts.get(fields[0]);
                } else {
                    nodes.add(fields[0]);
                    if (pos == 0)
                        a1 = new Artifact(Artifact.NodeType.ROOT, pos++, fields[0]);
                    else {
                        a1 = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++, fields[0]);
                    }
                }
                if (nodes.contains(fields[1])) {
                    a2 = artifacts.get(fields[1]);
                } else {
                    nodes.add(fields[1]);
                    if (pos == 0)
                        a2 = new Artifact(Artifact.NodeType.ROOT, pos++, fields[1]);
                    else {
                        a2 = new Artifact(Artifact.NodeType.INTERMEDIATE, pos++, fields[1]);
                    }
                }
                double tmp;
                if (cost.startsWith("time"))
                    tmp = 10000 * Double.parseDouble(fields[2]);
                else {
                    tmp = 10000 * Double.parseDouble(fields[4]);
                }
                //if(tmp == 0)
                //   tmp = 1;
                int l = (int) tmp;
                HyperEdge he = new HyperEdge(a1, a2, l);
                a2.addIN(he);
                a1.addOUT(he);
                artifacts.put(fields[0], a1);
                artifacts.put(fields[1], a2);
                hyperEdges.add(he);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        HyperGraph hg = new HyperGraph(artifacts.values().toArray(new Artifact[0]), hyperEdges.toArray(new HyperEdge[0]));
        return hg;
    }

    public static String extractFirstTwoChars(String s) {
        s = s.replaceAll("\"", "");
        String[] splitStrings = s.split("__");
        StringBuilder result = new StringBuilder();
        for (String substring : splitStrings) {
            if (substring.length() >= 2) {
                result.append(substring, 0, 2);
            } else if (!substring.isEmpty()) {
                result.append(substring);
            }
        }
        return result.toString();
    }

    public Set<String> getRandomElements(Set<String> originalSet, int n) {
        if (originalSet.size() < n) {
            throw new IllegalArgumentException("n cannot be greater than the size of the original set");
        }

        List<String> list = new ArrayList<>(originalSet);
        Collections.shuffle(list);

        Set<String> randomSet = new HashSet<>();
        for (int i = 0; i < n; i++) {
            randomSet.add(list.get(i));
        }

        return randomSet;
    }

    public List<List<String>> getAllCombinations(HashMap<String, ArrayList<String>> map, Set<String> set) {
        List<List<String>> combinations = new ArrayList<>();
        List<List<String>> lists = new ArrayList<>();

        for (String key : set) {
            ArrayList<String> values = map.get(key);
            if (values != null) {
                lists.add(values);
            }
        }

        generateCombinations(lists, combinations, 0, new ArrayList<>());

        return combinations;
    }

    private static void generateCombinations(List<List<String>> lists, List<List<String>> result, int depth, List<String> current) {
        if (depth == lists.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < lists.get(depth).size(); i++) {
            current.add(lists.get(depth).get(i));
            generateCombinations(lists, result, depth + 1, current);
            current.remove(current.size() - 1);
        }
    }

    public HyperGraph Artifacts_and_edges_ToHyperGraph(String project_path, String uid, int iteration, String type) {
        int pos = 0;
        boolean flag, flag2 = false;
        HashMap<String, Artifact> artifacts = new HashMap<>();
        ArrayList<HyperEdge> hyperEdges = new ArrayList<>();
        ArrayList<String> nodes = new ArrayList<>();
        String graph_path = project_path + "//graphs//iteration_graphs//" + uid + "_" + type + "_" + iteration;

        //Artifacts
        try {
            BufferedReader br = new BufferedReader(new FileReader(graph_path + "//nodes.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                //Pattern pattern = Pattern.compile("'(.*?)', \\{'type': '(.*?)', 'size': (\\d+), 'cc': (\\d+.\\d+)\\}");

                Pattern pattern = Pattern.compile("\\('(.+?)', \\{'type': '(.+?)', 'size': (\\d+), 'cc': (\\d+)\\}\\)");
                Matcher matcher = pattern.matcher(line);
                flag = matcher.find();
                if (!flag) {
                    pattern = Pattern.compile("'(.*?)', \\{'type': '(.*?)', 'size': (\\d+), 'cc': (\\d+.\\d+)\\}");
                    matcher = pattern.matcher(line);
                    flag2 = matcher.find();
                }

                String id = matcher.group(1);
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("type", matcher.group(2));
                attributes.put("size", Long.parseLong((matcher.group(3))));
                attributes.put("cc", Double.parseDouble(matcher.group(4)));
                Artifact a = new Artifact(id, matcher.group(2), Long.parseLong((matcher.group(3))), Double.parseDouble(matcher.group(4)), pos++);
                artifacts.put(id, a);

            }
            BufferedReader br2 = new BufferedReader(new FileReader(graph_path + "//edges.txt"));
            while ((line = br2.readLine()) != null) {
                //Pattern pattern = Pattern.compile("\\('(.+?)', '(.+?)', \\{'type': '(.+?)', 'weight': (.*?), 'execution_time': (.*?), 'memory_usage': (.*?)\\}\\)");
                Pattern pattern = Pattern.compile("\\('(.+?)', '(.+?)', \\{'type': '(.+?)', 'weight': (.*?), 'execution_time': (.*?), 'memory_usage': (.*?), 'platform': \\[(.+?)\\]\\}\\)");

                Matcher matcher = pattern.matcher(line);

                flag = matcher.find();
                if (flag) {
                    String part1 = matcher.group(1);
                    String part2 = matcher.group(2);

                    Artifact a1 = artifacts.get(part1);
                    Artifact a2 = artifacts.get(part2);
                    int l = (int) (Double.parseDouble(matcher.group(4)) * 100);
                    HyperEdge he = new HyperEdge(a1, a2, l);
                    a1.addOUT(he);
                    a2.addIN(he);
                    hyperEdges.add(he);
                    Map<String, Object> part3 = new HashMap<>();
                    part3.put("type", matcher.group(3));
                    part3.put("weight", Double.parseDouble(matcher.group(4)));
                    part3.put("execution_time", Double.parseDouble(matcher.group(5)));
                    part3.put("memory_usage", Integer.parseInt(matcher.group(6)));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        HyperGraph hg = new HyperGraph(artifacts.values().toArray(new Artifact[0]), hyperEdges.toArray(new HyperEdge[0]));
        return hg;
    }

    public HyperGraph TurnToTrueHG(HyperGraph limited) {

        ArrayList<Artifact> artifacts_to_remove = new ArrayList<>();
        ArrayList<HyperEdge> hyperEdge_to_remove = new ArrayList<>();

        for (Artifact a : limited.getArtifacts()) {
            if (a.getId().contains("super")) {
                if(a.getIN(0).size() < 2){
                    int k;
                }
                ArrayList<Artifact> super_in_0 = a.getIN(0);
                ArrayList<Artifact> super_in_1 = a.getIN(1);

                //IN artifacts (a_0, a_1) and OUT artifacts(a_3)
                Artifact a_0 = super_in_0.get(0);
                Artifact a_1 = super_in_1.get(0);
                ArrayList<Artifact> out_list = a.getOUT().get(0).getOUT();
                Artifact a_3 = out_list.get(0);

                //there are two in edges and one out edge that needs to be removed
                HyperEdge he_IN_0 = a_0.getOUT(a.getId());
                HyperEdge he_IN_1 = a_1.getOUT(a.getId());
                HyperEdge he_OUT = a.getOUT().get(0);

                int l = he_OUT.getCost();

                a_0.removeOUT(he_IN_0);
                a_1.removeOUT(he_IN_1);
                a_3.removeIN(he_OUT);


                super_in_0.add(a_1);

                HyperEdge he = new HyperEdge(super_in_0, out_list, l);

                a_0.addOUT(he);
                a_1.addOUT(he);
                a_3.addIN(he);

                hyperEdge_to_remove.add(he_IN_0);
                hyperEdge_to_remove.add(he_IN_1);
                hyperEdge_to_remove.add(he_OUT);

                artifacts_to_remove.add(a);

            }
        }
        limited.removeHyperEdges(hyperEdge_to_remove);
        limited.removeArtifacts(artifacts_to_remove);
        return limited;
    }

    public ArrayList<String> read_requests(String project_path, int iteration, String uid) {
        String graph_path = project_path + "//iterations//" + uid + "_iterations_diff_" + iteration + ".txt";
        BufferedReader br = null;
        ArrayList<String> mySet = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(graph_path));

            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(",");
                for (int i = 0; i < words.length; i++) {
                    mySet.add(words[i]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mySet;
    }

    public ArrayList<String> euqivalentRequests(ArrayList<String> mySet) {
        ArrayList<String> out = new ArrayList<>();
        for (String node : mySet) {
                String modifiedNode = node.replace("GP", "")
                        .replace("TF", "")
                        .replace("TR", "");

                out.add(modifiedNode);
        }
        return out;
    }
}

