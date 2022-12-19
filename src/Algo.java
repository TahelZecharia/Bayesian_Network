import java.util.*;

public class Algo {

    private BayesianNetwork net;

    // Array with the query variable and his outcomes
    private String[] query = new String[2];
    // Hashmap with the query variable and his outcomes
    private HashMap<String, ArrayList<String>> notQuery = new HashMap<>();
    // Hashmap of all the evidence variables and their outcomes
    private HashMap<String, String> evidences = new HashMap<>();
    // Hashmap of all the hidden variables and their outcomes
    private HashMap<String, ArrayList<String>> hiddens = new HashMap<>();
    // ArrayList of all the factors
    private ArrayList<Factor> factorsList = new ArrayList<>();

    private int addCounter = 0;
    private int mulCounter = 0;

    // get a BayesianNetwork and a query string (e.g P(B=T|J=T,M=T) )
    public Algo (BayesianNetwork bayesianNetwork, String q){

        net = bayesianNetwork;

        q = q.replace("P","");
        q = q.replace("(","");
        q = q.replace(")","");


        String[] quer=q.split("\\|");

        // query:
        String[] qqq =quer[0].split("=");
        query[0] = qqq[0];
        query[1] = qqq[1];

        // evidences:
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
    }

    public Double CalculateQuery(int algo){

        if (net.getNode(query[0]).getParents().containsAll(evidences.keySet()) && evidences.keySet().containsAll(net.getNode(query[0]).getParents())){

            HashMap<String, String> outcomes = new HashMap<>(evidences);
            outcomes.put(query[0], query[1]);

            return net.getNode(query[0]).getCPT().getProb(outcomes);
        }

        if (algo == 1) {

            return SimpleAlgo();
        }

        else if (algo == 2 || algo == 3) {

            return VariableEliminationAlgo(algo);
        }

        return -1.0;
    }

    public int getAddCounter(){

        return addCounter;
    }

    public int getMulCounter() {

        return mulCounter;
    }

    /**
     * **************************** PART 1 : Simple Algo ****************************
     */

    public Double SimpleAlgo(){

        HashMap<String, ArrayList<String>> denominator = new HashMap<>();
        HashMap<String, ArrayList<String>> numerator = new HashMap<>(hiddens);

        ArrayList<String> a = new ArrayList<>();
        a.add(query[1]);
        numerator.put(query[0], a);

        denominator.putAll(hiddens);
        denominator.putAll(notQuery);

        double numeratorAns = CalculateProb(combinations(numerator));
        double denominatorAns = CalculateProb(combinations(denominator));

        addCounter++;
        return numeratorAns/(numeratorAns+denominatorAns);
    }

    public Double CalculateProb(ArrayList<HashMap<String, String>> combinations){

        double ans = 0;

        for (HashMap<String, String> comb : combinations){

            comb.putAll(evidences);
            double x = 1;

            for (Node node : net.getNodes().values()){

                HashMap<String, String> parents = new HashMap<>();
                parents.put(node.getName(), comb.get(node.getName()));

                for (String perent : node.getParents()){
                    parents.put(perent, comb.get(perent));
                }

                if (x == 1) x = node.getCPT().getProb(parents);

                else {
                    mulCounter++;
                    x *= node.getCPT().getProb(parents);
                }
            }

            if (ans == 0) ans = x;

            else {
                addCounter++;
                ans+=x;
            }
        }
        return ans;
    }

    public ArrayList<HashMap<String, String>> combinations (HashMap<String, ArrayList<String>> outcomes){

        ArrayList<HashMap<String, String>> ans = new ArrayList<>();
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        for (String hidden : outcomes.keySet()){

            for (String outcome : outcomes.get(hidden)){

                if (ans.isEmpty()){

                    HashMap<String, String> tempHash = new HashMap<>();
                    tempHash.put(hidden, outcome);
                    temp.add(tempHash);
                }

                else {
                    for (HashMap<String, String> a : ans) {

                        HashMap<String, String> tempHash = new HashMap<>();
                        tempHash.put(hidden, outcome);
                        tempHash.putAll(a);
                        temp.add(tempHash);
                    }
                }
            }
            ans.clear();
            ans.addAll(temp);
            temp.clear();
        }
        return ans;
    }

    /**
     * **************************** PART 2 : Variable Elimination Algo ****************************
     */

    Double VariableEliminationAlgo(int algo) {

        // adding the factor of the query:
        Factor factor = new Factor(net.getNode(query[0]).getCPT(), evidences);
        factorsList.add(factor);
        System.out.println( "Query Factor: " + factor);

        // adding the factors of the evidences:
        for (String evidence : evidences.keySet()) {
            factor = new Factor(net.getNode(evidence).getCPT(), evidences);
            if (factor.getVariables().size() > 0){
                factorsList.add(factor);
                System.out.println("Evidences Factor: " + factor);
            }
        }

        ArrayList<String> sortHidden = new ArrayList<>(RemoveUnnecessaryVariables());

        // factors:
        for (String hidden : sortHidden) {

            Node node = net.getNode(hidden);
            factor = new Factor(node.getCPT(), evidences);
            factorsList.add(factor);
            System.out.println(factor);
        }

        if (algo == 2) {

            ABCElimination(sortHidden);
        }

        if (algo == 3) {

            HeuristicElimination(sortHidden);
        }

        return Normalize();
    }

    void ABCElimination (ArrayList<String> hiddenList) {

        hiddenList.sort(String::compareToIgnoreCase);

        for (String hidden : hiddenList) {

            Factor factor = Join(hidden);

            System.out.println("Join ");

            Elimination(hidden, factor);
        }
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

    /**
     * **************************** PART 3 : Variable Elimination Algo With Heuristic Elimination ****************************
     */

    void HeuristicElimination (ArrayList<String> hiddenList) {

        PriorityQueue<String> queue = new PriorityQueue<>(new Factors1());
        queue.addAll(hiddenList);

        while (!queue.isEmpty()) {

            String curr = queue.poll();

            Factor factor = Join(curr);

            System.out.println("Join ");

            Elimination(curr, factor);

            queue.add(curr);
            queue.remove(curr);
        }
    }

    class Factors1 implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {

            int countS1 = 0;
            int countS2 = 0;


            for (Factor factor : factorsList) {

                if (factor.getVariables().contains(s1)) countS1+= factor.getTableSize();
                if (factor.getVariables().contains(s2)) countS2+= factor.getTableSize();
            }

            return Integer.compare(countS1, countS2);
        }
    };

    class Factors2 implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {

            int countS1 = 0;
            int countS2 = 0;

            HashSet<String> set1 = new HashSet<>();
            HashSet<String> set2 = new HashSet<>();

            for (Factor factor : factorsList) {

                if (factor.getVariables().contains(s1)) set1.addAll(factor.getVariables());
                if (factor.getVariables().contains(s2)) set2.addAll(factor.getVariables());
            }

            for (String var : set1) {
                countS1 += net.getNode(var).outcomesSize();
            }
            for (String var : set2) {
                countS2 += net.getNode(var).outcomesSize();
            }

            return Integer.compare(countS1, countS2);
        }
    };
}
