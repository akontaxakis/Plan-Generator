import java.util.ArrayList;
import java.util.UUID;

public class Artifact {

    private UUID id;
    private int position;
    private Artifact.NodeType type;
    private boolean materialized;
    private int materializeCost;

    private ArrayList<Artifact> Parents;
    private ArrayList<Artifact> Children;

    public Artifact() {
        this.id = UUID.randomUUID();
        this.position = 0;
        this.type = NodeType.LEAF;
        this.materialized = false;
        this.materializeCost = 1000000000;
        Parents = new ArrayList<Artifact>();
        Children = new ArrayList<Artifact>();
    }



    public Artifact(UUID id, int position, NodeType type, boolean materialized, int materializeCost, ArrayList<Artifact> parents, ArrayList<Artifact> children) {
        this.id = id;
        this.position = position;
        this.type = type;
        this.materialized = materialized;
        this.materializeCost = materializeCost;
        Parents = parents;
        Children = children;
    }

     enum NodeType {
        ANDNODE,
        ORNODE,
        LEAF,
        ROOT;

         NodeType() {
        }
    }

}
