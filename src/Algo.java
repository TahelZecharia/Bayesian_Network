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


        String[] q1=q.split("\\|");

        // query:
        String[] q2 =q1[0].split("=");
        query[0] = q2[0];
        query[1] = q2[1];

        // evidences:
        if(q1.length>=2){

            String[] evidences=q1[1].split(",");
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

            if (!evidences.containsKey(node) && !query[0].equals(node)){
                hiddens.put(node, new ArrayList<>());
                for (String outcome : bayesianNetwork.getNode(node).getOutcomes()){
                    hiddens.get(node).add(outcome);
                }
            }
        }
    }

    /**
     * The function checks whether the solution to the query already appears
     * in one of the tables. If so, it returns the result without calculation.
     * If not, it sends the query to the desired algorithm (1, 2 or 3).
     */
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


    /**
     * The main function to calculate the first algorithm.
     * The function calculates all possible combinations to calculate the
     * probability, and for each of them calculates the probability.
     * Finally, the function adds all the results and normalizes.
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

    /**
     * The function calculates the probability for each of the combinations
     * and returns the sum of the probabilities.
     */
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

    /**
     * The function calculates all possible combinations for the variables
     * and the list of their outcomes that it receives as input.
     */
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


    /**
     * The main function that calculates algorithms 2 and 3.
     * The function initializes a list of factors, removes the irrelevant hidden,
     * then calculates the probability according to the desired algorithm (2 or 3)
     * and finally normalizes the result.
     */
    Double VariableEliminationAlgo(int algo) {

        // adding the factor of the query:
        Factor factor = new Factor(net.getNode(query[0]).getCPT(), evidences);
        factorsList.add(factor);
//        System.out.println( "Query Factor: " + factor);

        // adding the factors of the evidences:
        for (String evidence : evidences.keySet()) {
            factor = new Factor(net.getNode(evidence).getCPT(), evidences);
            if (factor.getVariables().size() > 0){
                factorsList.add(factor);
//                System.out.println("Evidences Factor: " + factor);
            }
        }

        ArrayList<String> sortHidden = new ArrayList<>(RemoveUnnecessaryVariables());

        // factors:
        for (String hidden : sortHidden) {

            Node node = net.getNode(hidden);
            factor = new Factor(node.getCPT(), evidences);
            factorsList.add(factor);
//            System.out.println(factor);
        }

        if (algo == 2) {

            ABCElimination(sortHidden);
        }

        if (algo == 3) {

            HeuristicElimination(sortHidden);
        }

        return Normalize();
    }

    /**
     * The function sends the variables for elimination when the
     * order of elimination of the variables is according to the ABC order.
     */
    void ABCElimination (ArrayList<String> hiddenList) {

        hiddenList.sort(String::compareToIgnoreCase);

        for (String hidden : hiddenList) {

            Factor factor = Join(hidden);

            Elimination(hidden, factor);
        }
    }

    /**
     * The function removes all variables that are unnecessary for the query
     * and returns a set containing the necessary variables.
     */
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

    /**
     * The function multiplies all the factors that contain the input variable
     * and returns a final factor after all the multiplications.
     */
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

    /**
     * The function accepts a factor and a variable, and connects the
     * identical rows containing different values of the variable. If the created
     * factor contains only one line, the function will not return it to the list of factors.
     */
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
//            System.out.println("Elimination Factor: " + f);
        }

    }

    /**
     * The last step in the algorithm. The function normalizes the query result
     * according to the last remaining factors, which contain only the query variable,
     * and finally returns the query result.
     */
    public Double Normalize() {

        Factor last = Join(query[0]);
//        System.out.println("last Factor: " + last);
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

    /**
     * **************************** PART 3 : Variable Elimination Algo With Heuristic Elimination ****************************
     */

    /**
     * The function sends the variables for elimination when the
     * order of elimination of the variables is according to the heuristic order:
     * The variable that is connected to the smallest factors is eliminated first.
     */
    void HeuristicElimination (ArrayList<String> hiddenList) {

        PriorityQueue<String> queue = new PriorityQueue<>(new Factors1());
        queue.addAll(hiddenList);

        while (!queue.isEmpty()) {

            String curr = queue.poll();

            int countS1 = 0;


            for (Factor factor : factorsList) {

                if (factor.getVariables().contains(curr)) countS1+= factor.getTableSize();
            }

            Factor factor = Join(curr);

            Elimination(curr, factor);

            queue.add(curr);
            queue.remove(curr);
        }
    }

    /**
     * This comparator compares two strings of variable names.
     * * The variable that is connected to the smallest factors is smallest.
     */
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

//    class Factors2 implements Comparator<String> {
//
//        @Override
//        public int compare(String s1, String s2) {
//
//            int countS1 = 0;
//            int countS2 = 0;
//
//            HashSet<String> set1 = new HashSet<>();
//            HashSet<String> set2 = new HashSet<>();
//
//            for (Factor factor : factorsList) {
//
//                if (factor.getVariables().contains(s1)) set1.addAll(factor.getVariables());
//                if (factor.getVariables().contains(s2)) set2.addAll(factor.getVariables());
//            }
//
//            set1.remove(s1);
//            set2.remove(s2);
//
//            for (String var : set1) {
//                countS1 += net.getNode(var).outcomesSize();
//            }
//            for (String var : set2) {
//                countS2 += net.getNode(var).outcomesSize();
//            }
//
//            return Integer.compare(countS1, countS2);
//        }
//    };

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

        return "Algo: { " + st + " }";
    }
}
