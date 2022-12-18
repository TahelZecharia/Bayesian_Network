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

    private int addCounter = 0;
    private int mulCounter = 0;

    // get a BayesianNetwork and a query string (e.g P(B=T|J=T,M=T) )
    public VariableEliminationAlgo(BayesianNetwork bayesianNetwork, String q){

        System.out.println("\n\n ***************** VariableEliminationAlgo *****************\n\n");
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
//        Node node = ;
        Factor factor = new Factor(net.getNode(query[0]).getCPT(), evidences);
        factorsList.add(factor);
        System.out.println( "Query Factor: " + factor);

        for (String evidence : evidences.keySet()) {
            factor = new Factor(net.getNode(evidence).getCPT(), evidences);
            if (factor.getVariables().size() > 0){
                factorsList.add(factor);
                System.out.println("Evidences Factor: " + factor);

            }
        }
    }

    Double CalculateQuery() {

        ArrayList<String> sortHidden = new ArrayList<>(RemoveUnnecessaryVariables());
        sortHidden.sort(String::compareToIgnoreCase);
        // factors:
        for (String hidden : sortHidden) {

            Node node = net.getNode(hidden);
            Factor factor = new Factor(node.getCPT(), evidences);
            factorsList.add(factor);
            System.out.println(factor);
        }

        for (String hidden : sortHidden) {

            Factor factor = Join(hidden);

            System.out.println("Join ");

            Elimination(hidden, factor);
        }
        return Normalize();
    }

    private HashSet<String> RemoveUnnecessaryVariables() {

        HashSet <String> relevant = new HashSet<>();
        HashSet <String> tempSet = new HashSet<>();

        for (String evidence : evidences.keySet()) {

            for (String parent : net.getNode(evidence).getParents()) {

                if ( (! evidences.containsKey(parent)) && (!Objects.equals(parent, query[0])) ) {

                    tempSet.add(parent);
                }
            }
        }

        for (String parent : net.getNode(query[0]).getParents()) {

            if (! evidences.containsKey(parent)) tempSet.add(parent);

        }

        ArrayList <String> tempArr = new ArrayList<>(tempSet);

        while (! tempArr.isEmpty()) {

            Node currNode = net.getNode(tempArr.remove(0));

            for (String parent : currNode.getParents()) {

                if ( (! evidences.containsKey(parent)) && (!Objects.equals(parent, query[0])) ) {

                    tempArr.add(parent);
                }
            }
            relevant.add(currNode.getName());
        }

        return relevant;
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

            joinFactors.sort(Factor::compareTo);
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
                        mulCounter++;
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

        while (factor.getSize() > 0) {

            HashMap<String, String> hash = new HashMap<>(factor.getTable().remove(0));
            for (int i = 0; i < factor.getSize(); i++) {

                boolean flag = true;
                for (String key : variables) {

                    if (!Objects.equals(hash.get(key), factor.getTable().get(i).get(key))) {

                        flag = false;
                        break;
                    }
                }

                if (flag) {

//                    if (! hash.containsKey("P")) hash.put("P", factor.getTable().get(i).get("P"))) System.out.println("NNNNNNNNNNNOOOOOOOOOOOOOOO!!!!!!!!!!");

                    if (! hash.containsKey("P")) System.out.println("NNNNNNNNNNNOOOOOOOOOOOOOOO!!!!!!!!!!");
                    addCounter++;
                    hash.put("P", String.valueOf(Double.parseDouble(hash.get("P"))+Double.parseDouble(factor.getTable().get(i).get("P"))));
                    factor.getTable().remove(i);
                    i--;
                }
            }
            arr.add(hash);
        }
        if ( arr.size() > 1)  {
            Factor f = new Factor(variables, arr);
            factorsList.add(f);
            System.out.println("Elimination Factor: " + f);
        }

    }

    public Double Normalize() {

        Factor last = Join(query[0]);
        System.out.println("last Factor: " + last);
        double numerator = 0.0;
        double denominator = 0.0;

        for (HashMap<String, String> hash : last.getTable()) {

            if (Objects.equals(hash.get(query[0]), query[1])) {

                if (numerator == 0) numerator = Double.parseDouble(hash.get("P"));

                else {
                    addCounter++;
                    numerator += Double.parseDouble(hash.get("P"));
                }
            }
            else {

                if (denominator == 0) denominator = Double.parseDouble(hash.get("P"));

                else {
                    addCounter++;
                    denominator += Double.parseDouble(hash.get("P"));
                }
            }
        }
        addCounter++;
        return (numerator/(numerator+denominator));
    }

    public int getAddCounter(){

        return addCounter;
    }

    public int getMulCounter() {

        return mulCounter;
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

//    public static class Comparators {
//
//        public static Comparator<HashMap<String, ArrayList<String>>> ABC = new Comparator<HashMap<String, ArrayList<String>>>() {
//
//            @Override
//            public int compare(HashMap<String, ArrayList<String>> map1, HashMap<String, ArrayList<String>> map2) {
//                return map1..compareToIgnoreCase(s2)
//            }
//
//            @Override
//            public int compare(Factor f1, Factor f2) {
//                // check by size:
//                if (f1.getTableSize() > f2.getTableSize()) {
//                    return 1;
//                } else if (f1.getTableSize() < f2.getTableSize()) {
//                    return -1;
//                }
//
//                // if they have the same size:
//                int s1 = 0;
//                int s2 = 0;
//
//                for (String var : f2.variables) {
//                    for (int i = 0; i < var.length(); i++) {
//                        s1 += var.charAt(i);
//                    }
//                }
//
//                for (String var : f2.getVariables()) {
//                    for (int i = 0; i < var.length(); i++) {
//                        s2 += var.charAt(i);
//                    }
//                }
//
//                // check ascii size:
//                return Integer.compare(s1, s2);
//            }
//        };
//
//        public static Comparator<Factor> AGE = new Comparator<Student>() {
//            @Override
//            public int compare(Student o1, Student o2) {
//                return o1.age - o2.age;
//            }
//        };
//        public static Comparator<Student> NAMEANDAGE = new Comparator<Student>() {
//            @Override
//            public int compare(Student o1, Student o2) {
//                int i = o1.name.compareTo(o2.name);
//                if (i == 0) {
//                    i = o1.age - o2.age;
//                }
//                return i;
//            }
//        };
//    }
}
