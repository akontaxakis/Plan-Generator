package Entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPlan {

    private HashMap<NArtifact, Integer> Artifacts;
    private int cost;

    public NPlan(NPlan p1) {
        this.cost = p1.getCost();
        Artifacts =  new HashMap<>();
        Artifacts.putAll(p1.getArtifacts());
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }


    public NPlan() {
        Artifacts = new HashMap<>();
        cost = 0;
    }

    @Override
    public String toString() {
        String tmp = "Τhe Size of the plan " + Artifacts.size() + "\n";

        for (Map.Entry<NArtifact,Integer> child : Artifacts.entrySet()) {
            tmp = tmp + child.getKey().getPosition() + "!" + child.getValue() +"-" + child.getKey().getType() + "|";

        }
        tmp = tmp + " the cost is:" + cost;
        return tmp;
    }

    public void copy(NArtifact a, int cost) {
        Integer k = Artifacts.get(a);
        if (k == null) {
            Artifacts.put(a, cost);
            this.cost = this.cost + cost;
        }
    }

    public int findCombinedCost(NPlan p1) {
        for (Map.Entry<NArtifact, Integer> a : p1.Artifacts.entrySet()) {
            this.copy(a.getKey(), a.getValue());
        }
        return this.cost;
    }

    public void copy(NPlan p1) {
        for (Map.Entry<NArtifact, Integer> a : p1.Artifacts.entrySet()) {
            this.copy(a.getKey(), a.getValue());
        }

    }

    public HashMap<NArtifact, Integer> getArtifacts() {
        return Artifacts;
    }

    public void setArtifacts(HashMap<NArtifact, Integer> artifacts) {
        Artifacts = artifacts;
    }

    public boolean isValid() {
        for (NArtifact a : Artifacts.keySet()) {
            if (a.isOR()) {
                if(a.getParents()==null){
                    return false;
                }
                if (!(a.getParents().size() == 1)) {
                    return false;
                }
                for (NArtifact j : a.getParents().keySet()) {
                    setValid(j.getId(),true,Artifacts);
                }
            } else if (a.isAND()) {
                for (NArtifact j : a.getParents().keySet()) {
                    setValid(j.getId(),true,Artifacts);
                }
            }
        }
        return true;
    }

    private void setValid(UUID id, boolean b, HashMap<NArtifact, Integer> artifacts) {
        for(NArtifact a: artifacts.keySet()){
            if(a.getId()==id){
                a.setValid(b);
                break;
            }
        }
    }

    public NPlan removeUnValidNodes() {
        NPlan fp=new NPlan();
        for(Map.Entry<NArtifact,Integer> a: Artifacts.entrySet()){
           if(a.getKey().isValid() == true){
               fp.copy(a.getKey(),a.getValue());
            }
        }
    return fp;
    }

}
