import java.util.*;

public class Factor implements Comparable<Factor>{

    private HashSet<String> variables  = new HashSet<>();
    private ArrayList<HashMap<String, String>> table = new ArrayList<>(); // ? table? ArrayList?

    public Factor(CPT cpt, HashMap<String, String> evidences){

        // variables:
        HashMap<String, String> factorEvidences = new HashMap<>();
        for (String var : cpt.getVariables()){

            if (! evidences.containsKey(var)) {

                variables.add(var);
            }
            else factorEvidences.put(var, evidences.get(var));
        }

        // table:
        for (int i = 1; i < cpt.getRows(); i++) {

            boolean flag = true;

            for (String evidence : factorEvidences.keySet()) {

                if (!Objects.equals(cpt.getOutcome(evidence, i), factorEvidences.get(evidence))){

                    flag = false;
                    break;
                }
            }

            if (flag) {

                HashMap<String, String> hash = new HashMap<>();
                for (int j = 0; j < cpt.getCols(); j++) {

                    if (! evidences.containsKey(cpt.getTable(0, j))) {

                        hash.put(cpt.getTable(0, j), cpt.getTable(i, j));
                    }
                }
                table.add(hash);
            }
        }
    }

    public Factor(HashSet<String> set, ArrayList<HashMap<String, String>> arr) {

        variables = set;
        table = arr;
    }

    public ArrayList<HashMap<String, String>> getTable() {

        return table;
    }

    public HashSet<String> getVariables() {

        return variables;
    }

    public int getSize() {

        return table.size();
    }

    public int getTableSize() {

        return table.size() * table.get(0).size();
    }

    @Override
    public String toString() {

        return "Factor { " +
                "variables: '" + variables.toString() + '\'' +
                ", table: '" + table.toString() + '\'' +
                "}";
    }

    @Override
    public int compareTo(Factor o) {

        // check by size:
        if (this.getTableSize() > o.getTableSize()) {
            return 1;
        }
        else if(this.getTableSize() < o.getTableSize()) {
            return -1;
        }

        // if they have the same size:
        int s1 = 0;
        int s2 = 0;

        for (String var : this.variables) {
            for (int i = 0; i < var.length(); i++) {
                s1 += var.charAt(i);
            }
        }

        for (String var : o.getVariables()) {
            for (int i = 0; i < var.length(); i++) {
                s2 += var.charAt(i);
            }
        }

        // check ascii size:
        return Integer.compare(s1, s2);
    }
}



