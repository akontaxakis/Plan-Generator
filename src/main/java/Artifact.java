import java.util.*;

public class Artifact {

    private UUID id;
    private int position;
    private int additiveCost=0;
    private boolean valid = false;



    public UUID getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }

    private Artifact.NodeType type;
    private boolean materialized;
    private int materializeCost;
    private HashMap<Artifact, Integer> Parents;
    private HashMap<Artifact, Integer> Children;

    public Artifact(Artifact current, Map.Entry<Artifact, Integer> parent) {
        id=current.getId();
        position=current.getPosition();
        type=current.getType();
        materialized=false;
        materializeCost=0;
        HashMap<Artifact, Integer> Parents = new HashMap<>();
        Parents.put(parent.getKey(),parent.getValue());
        HashMap<Artifact, Integer> Children=null;

    }

    public HashMap<Artifact, Integer> getParents() {
        return Parents;
    }

    public Artifact getParent(int i) {
        int j=0;
        for(Artifact a:Parents.keySet()){
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
            return "Artifact{" +
                    "id=" + id +
                    ", position=" + position +
                    ", type=" + type +
                    ", materialized=" + materialized +
                    ", materializeCost=" + materializeCost +
                    ", Children=" + Children.size() +
                    '}';
        }else if(isTerminal()){
            return "Artifact{" +
                    "id=" + id +
                    ", position=" + position +
                    ", type=" + type +
                    ", additiveCost=" + additiveCost +
                    ", materialized=" + materialized +
                    ", materializeCost=" + materializeCost +
                    ", Parents=" + Parents.size() +
                    '}';
        }else{
            return "Artifact{" +
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

    public void setChildren(HashMap<Artifact, Integer> children) {
        Children = children;
    }



    public Artifact() {
        this.id = UUID.randomUUID();
        this.position = 0;
        this.type = NodeType.LEAF;
        this.materialized = false;
        this.materializeCost = 1000000000;
        Parents = new HashMap<Artifact, Integer>();
        Children = new HashMap<Artifact, Integer>();
    }

    public Artifact(Artifact.NodeType type) {
        this.id = UUID.randomUUID();
        this.position = 0;
        this.type = type;
        if (type == NodeType.ROOT) {
            this.materialized = true;
            this.materializeCost = 0;
            Parents = null;
            Children = new HashMap<Artifact, Integer>();
        } else {
            this.materialized = false;
            this.materializeCost = 1000000000;
            Parents = new HashMap<Artifact, Integer>();
            Children = new HashMap<Artifact, Integer>();
        }
    }


    public Artifact(UUID id, int position, NodeType type, boolean materialized, int materializeCost, HashMap<Artifact, Integer> parents, HashMap<Artifact, Integer> children) {
        this.id = id;
        this.position = position;
        this.type = type;
        this.materialized = materialized;
        this.materializeCost = materializeCost;
        Parents = parents;
        if(children==null){
            Children = new HashMap<Artifact, Integer>();
        }else {
            Children = children;
        }
    }

    public String printPosAndChildren() {
        String tmp = "!";
        if (isROOT()) {
            for (Artifact child : Children.keySet()) {
                tmp = tmp + child.getPosition() + "-"+child.getAdditiveCost()+"!";
            }
            return
                    "pos=" + position +
                            ", Children=" + tmp +
                            '}';
        } else if (isTerminal()) {
            return "pos=" + position;
        } else {
            for (Artifact child : Children.keySet()) {
                tmp = tmp + child.getPosition() + "!";
            }
            return
                    "pos=" + position +
                            ", Children=" + tmp +
                            '}';
        }
    }

    public void addChild(Artifact tmp, Integer cost) {
        this.Children.put(tmp,cost);
    }

    public boolean isAND() {
        return this.type == NodeType.ANDNODE;
    }

    public boolean isOR() {
        return  this.type == NodeType.ORNODE;
    }




    enum NodeType {
        ANDNODE,
        ORNODE,
        LEAF,
        ROOT,
        REQUEST;

         NodeType() {
        }
    }

    static class SortbyPOS implements Comparator<Artifact> {

        // Method
        // Sorting in ascending order of roll number
        @Override
        public int compare(Artifact o1, Artifact o2) {
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


    public void setParents(HashMap<Artifact, Integer> parents) {
        Parents = parents;
    }
    public HashMap<Artifact, Integer> getChildren() {
        return Children;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
