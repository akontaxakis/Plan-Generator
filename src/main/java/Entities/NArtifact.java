package Entities;

import java.util.*;

public class NArtifact {

    private UUID id;
    private int position;
    private int additiveCost=0;
    private boolean valid = false;
    private NArtifact.NodeType type;
    private boolean materialized;

    private int materializeCost;
    private int computeCost=0;
    private HashMap<NArtifact, Integer> Parents;
    private HashMap<NArtifact, Integer> Children;


    public UUID getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }



    public NArtifact(NArtifact current, Map.Entry<NArtifact, Integer> parent) {
        id=current.getId();
        position=current.getPosition();
        type=current.getType();
        materialized=false;
        materializeCost=0;
        HashMap<NArtifact, Integer> Parents = new HashMap<>();
        Parents.put(parent.getKey(),parent.getValue());
        HashMap<NArtifact, Integer> Children=null;
    }

    public HashMap<NArtifact, Integer> getParents() {
        return Parents;
    }

    public NArtifact getParent(int i) {
        int j=0;
        for(NArtifact a:Parents.keySet()){
            if(j==i){
                return a;
            }
            j++;
        }
        return null;
    }

    @Override
    public String toString() {
        if (isROOT()) {
            return "Entities.Artifact{" +
                    "id=" + id +
                    ", position=" + position +
                    ", type=" + type +
                    ", materialized=" + materialized +
                    ", materializeCost=" + materializeCost +
                    ", Children=" + Children.size() +
                    '}';
        }else if(isTerminal()){
            return "Entities.Artifact{" +
                    "id=" + id +
                    ", position=" + position +
                    ", type=" + type +
                    ", additiveCost=" + additiveCost +
                    ", materialized=" + materialized +
                    ", materializeCost=" + materializeCost +
                    ", Parents=" + Parents.size() +
                    '}';
        }else{
            return "Entities.Artifact{" +
                    "id=" + id +
                    ", position=" + position +
                    ", type=" + type +
                    ", materialized=" + materialized +
                    ", materializeCost=" + materializeCost +
                    ", additiveCost=" + additiveCost +
                    ", Parents=" + Parents.size() +
                    ", Children=" + Children.size() +
                    '}';
        }
    }

    private boolean isTerminal() {
        return NodeType.LEAF == type;
    }

    private boolean isROOT() {
        return NodeType.ROOT == type;
    }

    public void setChildren(HashMap<NArtifact, Integer> children) {
        Children = children;
    }

    public NArtifact() {
        this.id = UUID.randomUUID();
        this.position = 0;
        this.type = NodeType.LEAF;
        this.materialized = false;
        this.materializeCost = 1000000000;
        Parents = new HashMap<NArtifact, Integer>();
        Children = new HashMap<NArtifact, Integer>();
    }

    public NArtifact(NArtifact.NodeType type) {
        this.id = UUID.randomUUID();
        this.position = 0;
        this.type = type;
        if (type == NodeType.ROOT) {
            this.materialized = true;
            this.materializeCost = 0;
            Parents = null;
            Children = new HashMap<NArtifact, Integer>();
        } else {
            this.materialized = false;
            this.materializeCost = 1000000000;
            Parents = new HashMap<NArtifact, Integer>();
            Children = new HashMap<NArtifact, Integer>();
        }
    }


    public NArtifact(UUID id, int position, NodeType type, boolean materialized, int materializeCost, int computeCost, HashMap<NArtifact, Integer> parents, HashMap<NArtifact, Integer> children) {
        this.id = id;
        this.position = position;
        this.type = type;
        this.materialized = materialized;
        this.computeCost = computeCost;
        this.materializeCost = materializeCost;
        Parents = parents;
        if(children==null){
            Children = new HashMap<NArtifact, Integer>();
        }else {
            Children = children;
        }
    }

    public String printPosAndChildren() {
        String tmp = "!";
        if (isROOT()) {
            for (NArtifact child : Children.keySet()) {
                tmp = tmp + child.getPosition() + "-"+child.getAdditiveCost()+"!";
            }
            return "pos=" + position + ", Children=" + tmp + '}';
        } else if (isTerminal()){
            for (Map.Entry<NArtifact, Integer> child : Parents.entrySet()){
                tmp = tmp + child.getKey().getType() + "!" + child.getKey().getPosition() + "!" + child.getValue().intValue() + "!";
            }
            return "pos=" + position + ", Parents=" + tmp + '}';
        }else{
            for (Map.Entry<NArtifact, Integer> child : Parents.entrySet()) {
                tmp = tmp + child.getKey().getType() + "!" + child.getKey().getPosition() + "!" + child.getValue().intValue() + "!";
            }
            return "pos=" + position + ", Parents=" + tmp + '}';
        }
    }

    public String printPosAndParents() {
        String tmp = "!";
        if (isROOT()){
            for (NArtifact child : Children.keySet()) {
                tmp = tmp + child.getPosition() + "-"+child.getAdditiveCost()+"!";
            }
            return "pos=" + position + ", Children=" + tmp + '}';
        }else if(isTerminal()){
            for (Map.Entry<NArtifact, Integer> child : Parents.entrySet()) {
                tmp = tmp + child.getKey().getType() + "!" + child.getKey().getPosition() + "!" + child.getValue().intValue() + "!";
            }
            return "pos=" + position + ", Parents=" + tmp + '}';
        }else{
            for (Map.Entry<NArtifact, Integer> child : Parents.entrySet()) {
                tmp = tmp + child.getKey().getType() + "!" + child.getKey().getPosition() + "!" + child.getValue().intValue() + "!";
            }
            return "pos=" + position + ", Parents=" + tmp + '}';
        }
    }

    public void addChild(NArtifact tmp, Integer cost) {
        this.Children.put(tmp,cost);
    }

    public boolean isAND() {
        return this.type == NodeType.ANDNODE;
    }

    public boolean isOR() {
        return  this.type == NodeType.ORNODE;
    }

    public enum NodeType {
        ANDNODE,
        ORNODE,
        LEAF,
        ROOT,
        REQUEST;

         NodeType() {
        }
    }

    public static class SortbyPOS implements Comparator<NArtifact> {
        // Method
        // Sorting in ascending order of roll number
        @Override
        public int compare(NArtifact o1, NArtifact o2) {
            return o1.getPosition()-o2.getPosition();
        }
    }

    public int getAdditiveCost() {
        return additiveCost;
    }

    public void setAdditiveCost(int additiveCost) {
        this.additiveCost = additiveCost;
    }
    public void setType(NodeType type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }


    public void setParents(HashMap<NArtifact, Integer> parents) {
        Parents = parents;
    }
    public HashMap<NArtifact, Integer> getChildren() {
        return Children;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getMaterializeCost() {
        return materializeCost;
    }
    public void setMaterializeCost(int materializeCost) {
        this.materializeCost = materializeCost;
    }
    public int getComputeCost() {
        return computeCost;
    }
    public void setComputeCost(int computeCost) {
        this.computeCost = computeCost;
    }
}
