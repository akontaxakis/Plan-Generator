package util;

import Entities.Artifact;
import Entities.HyperEdge;
import HyperGraph.HyperGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class lib {


    public HyperGraph TxtToHyperGraph(String fileName) {
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
                double tmp = 10000*Double.parseDouble(fields[2]);
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


}
