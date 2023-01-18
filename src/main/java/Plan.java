import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Plan {

    private HashMap<Artifact, Integer> Artifacts;
    private int cost;

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }


    public Plan() {
        Artifacts = new HashMap<>();
        cost = 0;
    }

    @Override
    public String toString() {
        String tmp = "";
        for (Map.Entry<Artifact,Integer> child : Artifacts.entrySet()) {
            tmp = tmp + child.getKey().getPosition() + "!" + child.getValue() +"-";
        }
        tmp = tmp + " the cost is:" + cost;
        return tmp;
    }

    public void copy(Artifact a, int cost) {
        Integer k = Artifacts.get(a);
        if (k == null) {
            Artifacts.put(a, cost);
            this.cost = this.cost + cost;
        }
    }

    public int findCombinedCost(Plan p1) {
        for (Map.Entry<Artifact, Integer> a : p1.Artifacts.entrySet()) {
            this.copy(a.getKey(), a.getValue());
        }
        return this.cost;
    }

    public void copy(Plan p1) {
        for (Map.Entry<Artifact, Integer> a : p1.Artifacts.entrySet()) {
            this.copy(a.getKey(), a.getValue());
        }

    }

    public HashMap<Artifact, Integer> getArtifacts() {
        return Artifacts;
    }

    public void setArtifacts(HashMap<Artifact, Integer> artifacts) {
        Artifacts = artifacts;
    }

    public boolean isValid() {
        for (Artifact a : Artifacts.keySet()) {
            if (a.isOR()) {
                if(a.getParents()==null){
                    return false;
                }
                if (!(a.getParents().size() == 1)) {
                    return false;
                }
                for (Artifact j : a.getParents().keySet()) {
                    setValid(j.getId(),true,Artifacts);
                }
            } else if (a.isAND()) {
                for (Artifact j : a.getParents().keySet()) {
                    setValid(j.getId(),true,Artifacts);
                }
            }
        }
        return true;
    }

    private void setValid(UUID id, boolean b, HashMap<Artifact, Integer> artifacts) {
        for(Artifact a: artifacts.keySet()){
            if(a.getId()==id){
                a.setValid(b);
                break;
            }
        }
    }

    public Plan removeUnValidNodes() {
        Plan fp=new Plan();
        for(Map.Entry<Artifact,Integer> a: Artifacts.entrySet()){
           if(a.getKey().isValid() == true){
               fp.copy(a.getKey(),a.getValue());
            }
        }
    return fp;
    }

}
