import java.util.*;

public class Node {

    private String name;
    public ArrayList<String> outcomes;
    public ArrayList<String> parents; // ??? has to?
    private CPT cpt;

    public Node(String name){

        this.name = name;
        this.outcomes = new ArrayList<>();;
        this.parents = new ArrayList<>();
    }

    public void addOutcome(String outcome){

        this.outcomes.add(outcome);
    }

    public void addParent(String parent){

        this.parents.add(parent);
    }

    public void setCPT(CPT cpt){

        this.cpt = cpt;
    }
}
