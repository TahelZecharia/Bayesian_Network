import java.lang.reflect.Array;
import java.util.*;

public class Node {

    private String name;
    public ArrayList<String> outcomes;
    public ArrayList<String> parents; // ??? has to?
    private CPT cpt;

    public Node(String name){

        this.name = name;
        this.outcomes = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

    public String getName(){

        return name;
    }

    public void addOutcome(String outcome){

        this.outcomes.add(outcome);
    }

    public void addParent(String parent){

        this.parents.add(parent);
    }

    public ArrayList<String> getParents(){

        return parents;
    }

    public int parentsSize(){

        return parents.size();
    }

    public ArrayList<String> getOutcomes(){

        return outcomes;
    }

    public int outcomesSize(){

        return outcomes.size();
    }
    public CPT getCPT(){

        return cpt;
    }

    public void setCPT(CPT cpt){

        this.cpt = cpt;
    }

    @Override
    public String toString() {
        return "Node { " +
                "Name: '" + name + '\'' +
                ", Parents: '" + parents + '\'' +
                ", Outcomes=" + outcomes +
                ", cpt=" + cpt +
                "}";
    }
}
