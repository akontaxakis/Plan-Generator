package util;

import Entities.Artifact;
import Entities.HyperEdge;
import HyperGraph.HyperGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
                if(pipelines == null){
                    pipelines = new ArrayList();
                }
                if(!pipelines.contains(pipeline))
                    pipelines.add(pipeline);
                models.put(model,pipelines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return models;
    }
    public HyperGraph TxtToHyperGraph(String fileName,String cost) {
        int pos = 0;
        HashMap<String,Artifact> artifacts = new HashMap<>();
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

                if (nodes.contains(fields[0])){
                     a1 = artifacts.get(fields[0]);
                }else{
                    nodes.add(fields[0]);
                    if(pos == 0)
                        a1 = new Artifact(Artifact.NodeType.ROOT,pos++, fields[0]);
                    else{
                        a1 = new Artifact(Artifact.NodeType.INTERMEDIATE,pos++, fields[0]);
                    }
                }
                if (nodes.contains(fields[1])){
                    a2 = artifacts.get(fields[1]);
                }else{
                    nodes.add(fields[1]);
                    if(pos == 0)
                        a2 = new Artifact(Artifact.NodeType.ROOT,pos++, fields[1]);
                    else{
                        a2 = new Artifact(Artifact.NodeType.INTERMEDIATE,pos++, fields[1]);
                    }
                }
                double tmp;
                if(cost.startsWith("time"))
                    tmp = 10000*Double.parseDouble(fields[2]);
                else{
                    tmp = 10000*Double.parseDouble(fields[4]);
                }
                //if(tmp == 0)
                //   tmp = 1;
                int l = (int) tmp;
                HyperEdge he = new HyperEdge(a1, a2, l);
                a2.addIN(he);
                a1.addOUT(he);
                artifacts.put(fields[0],a1);
                artifacts.put(fields[1],a2);
                hyperEdges.add(he);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        HyperGraph hg = new HyperGraph(artifacts.values().toArray(new Artifact[0]),hyperEdges.toArray(new HyperEdge[0]));
        return hg;
    }
    public static String extractFirstTwoChars(String s) {
        s = s.replaceAll("\"","");
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
}
