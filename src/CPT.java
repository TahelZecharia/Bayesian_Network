import java.util.*;

public class CPT {

    String[][] table;

    public CPT(BayesianNetwork net, Node node, String[] cpt){

        int cols = node.parentsSize() + 2;
        int rows = cpt.length +1;

        table = new String[rows][cols];

        table[0][cols-1] = "P";
        table[0][cols-2] = node.getName();

        ArrayList<String> parents = node.getParents();

        for (int j = 0; j < cols - 2; j++) {

            table[0][j] = parents.get(j);
        }

        int slice = 1;
        ArrayList<String> outcomes = node.getOutcomes();

        for (int j = cols-2; j >= 0; j--) {

            int in_count = 0;
            int out_count = 0;

            for (int i = 1; i < rows; i++) {

                if (in_count < slice) {
                    table[i][j] = outcomes.get(out_count);
                    in_count++;
                }

                if (in_count >= slice && out_count < outcomes.size()-1) {
                    out_count++;
                    in_count = 0;
                }

                else if (in_count >= slice && out_count == outcomes.size()-1) {
                    out_count = 0;
                    in_count = 0;
                }

            }
            if(j != 0) {

                outcomes = net.getNode(table[0][j - 1]).getOutcomes();
                slice = slice * outcomes.size();
            }
        }

        for (int i = 1, j=0; i < rows; i++,j++){
            table[i][cols-1] = cpt[j];
        }

    }

    Double getProb(HashMap<String, String> outcomes){

        for (String[] strings : table) {

            boolean flag = true;

            for (String outcome : outcomes.keySet()) {

                if (flag) {

                    for (int j = 0; j < table[0].length; j++) {

                        if (Objects.equals(table[0][j], outcome)) {

                            if (!Objects.equals(strings[j], outcomes.get(outcome))) {

                                flag = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (flag) return Double.parseDouble(strings[table[0].length - 1]);
        }
        return -1.0;
    }


    @Override
    public String toString() {
        String st = "";

        for (String[] strings : table) {

            st += Arrays.toString(strings) + "\n";
        }
        return "CPT: { \n" + st + "}";
    }
}
