import java.util.*;

public class VariableEliminationAlgo {

    private BayesianNetwork net;

    // Hashmap with the query variable and his outcomes
    private String[] query = new String[2];
    // Hashmap with the query variable and his outcomes
    private HashMap<String, ArrayList<String>> notQuery = new HashMap<>();
    // Hashmap of all the evidence variables and their outcomes
    private HashMap<String, String> evidences = new HashMap<>();
    // Hashmap of all the hidden variables and their outcomes
    private HashMap<String, ArrayList<String>> hiddens = new HashMap<>();
    // Array of all the Factors
    private ArrayList<Factor> factorsList = new ArrayList<>();

    // get a BayesianNetwork and a query string (e.g P(B=T|J=T,M=T) )
    public VariableEliminationAlgo(BayesianNetwork bayesianNetwork, String q){

        net = bayesianNetwork;

        q = q.replace("P","");
        q = q.replace("(","");
        q = q.replace(")","");

        String[] quer=q.split("\\|");

        String[] qqq =quer[0].split("=");
        query[0] = qqq[0];
        query[1] = qqq[1];

        if(quer.length>=2){
            String[] evidences=quer[1].split(",");
            for (String evidence : evidences) {
                this.evidences.put(evidence.split("=")[0], evidence.split("=")[1]);
            }
        }

        // not query:
        notQuery.put(this.query[0], new ArrayList<>());
        for (String outcome : bayesianNetwork.getNode(this.query[0]).getOutcomes()){
            if (!Objects.equals(this.query[1], outcome)){
                notQuery.get(this.query[0]).add(outcome);
            }
        }

        // hidden:
        for (String node : bayesianNetwork.getNodes().keySet()) {

            if (!q.contains(node)){
                hiddens.put(node, new ArrayList<>());
                for (String outcome : bayesianNetwork.getNode(node).getOutcomes()){
                    hiddens.get(node).add(outcome);
                }
            }
        }

        // factors:
        for (Node node : bayesianNetwork.getNodes().values()) {

            Factor factor = new Factor(node.getCPT(), evidences);
            factorsList.add(factor);
            System.out.println(factor);
        }
    }

    Double CalculateQuery() {
        // sort hiddens
        for (String hidden : hiddens.keySet()) {

            Factor factor = Join(hidden);

            Elimination(hidden, factor);
        }
        return Normalize();
    }

    Factor Join (String name) {

        ArrayList<Factor> joinFactors = new ArrayList<>();

        for (int i = 0; i < factorsList.size(); i++) {

            if (factorsList.get(i).getVariables().contains(name)) {
                joinFactors.add(factorsList.remove(i));
                i--;
            }
        }

        while (joinFactors.size() > 1) {

            // sort joinFactors!!!!!!!!!!
            Factor first = joinFactors.remove(0);
            Factor second = joinFactors.remove(0);

            HashSet<String> intersectionVars = new HashSet<>(first.getVariables());
            intersectionVars.retainAll(second.getVariables());
            HashSet<String> unionVars = new HashSet<>(first.getVariables());
            unionVars.addAll(second.getVariables());
            ArrayList<HashMap<String, String>> arr = new ArrayList<>();

            for (HashMap<String, String> firstHash : first.getTable()) {

                for (HashMap<String, String> secondHash : second.getTable()) {

                    boolean flag = true;
                    for (String var : intersectionVars) {

                        if (!Objects.equals(firstHash.get(var), secondHash.get(var))) {

                            flag = false;
                            break;
                        }
                    }

                    if (flag) {

                        HashMap<String, String> hash = new HashMap<>(firstHash);
                        hash.putAll(secondHash);
                        hash.put("P", String.valueOf((Double.parseDouble(firstHash.get("P")))*(Double.parseDouble(secondHash.get("P")))));
                        arr.add(hash);
                    }
                }
            }
            joinFactors.add(new Factor(unionVars, arr));
        }

        return joinFactors.get(0);
    }

    public void Elimination(String name, Factor factor) {

        ArrayList<HashMap<String, String>> arr = new ArrayList<>();
        HashSet<String> variables = new HashSet<>(factor.getVariables());
        variables.remove(name);

        while (factor.size() > 0) {

            HashMap<String, String> hash = new HashMap<>(factor.getTable().remove(0));
            for (int i = 0; i < factor.size(); i++) {

                boolean flag = true;
                for (String key : variables) {

                    if (!Objects.equals(hash.get(key), factor.getTable().get(i).get(key))) {

                        flag = false;
                        break;
                    }
                }

                if (flag) {

                    hash.put("P", String.valueOf(Double.parseDouble(hash.get("P"))+Double.parseDouble(factor.getTable().get(i).get("P"))));
                    factor.getTable().remove(i);
                    i--;
                }
            }
            arr.add(hash);
        }
        factorsList.add(new Factor(variables, arr));
    }

    public Double Normalize() {

        Factor last = Join(query[0]);
        System.out.println(last);
        double numerator = 0.0;
        double denominator = 0.0;

        for (HashMap<String, String> hash : last.getTable()) {

            if (Objects.equals(hash.get(query[0]), query[1])) {

                numerator += Double.parseDouble(hash.get("P"));
            }
            else {
                denominator += Double.parseDouble(hash.get("P"));
            }
        }
        return (numerator/(numerator+denominator));
    }

    @Override
    public String toString() {

        String st = "";

        st+="query: " + Arrays.toString(query) + ". ";

        st+="not query: " + notQuery.toString() + ". ";

        st+= "evidence: " + evidences.toString() + ". ";

        st+= "hidden: {";
        for (String string : hiddens.keySet()) {

            st += string + ": ";
            st += hiddens.get(string).toString()+", ";
        }

        st+= "factors: " + factorsList.toString() + ". ";
        st+= "}.";

        return "SimpleAlgo: { " + st + " }";
    }
}
