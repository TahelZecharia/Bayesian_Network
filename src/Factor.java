import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Factor {

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

    public int size() {

        return table.size();
    }

    @Override
    public String toString() {

        return "Factor { " +
                "variables: '" + variables.toString() + '\'' +
                ", table: '" + table.toString() + '\'' +
                "}";
    }
}
