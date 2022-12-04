import java.util.*;

public class SimpleAlgo {

    private BayesianNetwork net;

    // Hashmap with the query variable and his outcomes
    private String[] query = new String[2];
    // Hashmap with the query variable and his outcomes
    private HashMap<String, ArrayList<String>> notQuery = new HashMap<>();
    // Hashmap of all the evidence variables and their outcomes
    private HashMap<String, String> evidences = new HashMap<>();
    // Hashmap of all the hidden variables and their outcomes
    private HashMap<String, ArrayList<String>> hiddens = new HashMap<>();


    // get a BayesianNetwork and a query string (e.g P(B=T|J=T,M=T) )
    public SimpleAlgo(BayesianNetwork bayesianNetwork, String q){

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
    }

    public Double CalculateQuery(){

        HashMap<String, ArrayList<String>> numerator = new HashMap<>();
        HashMap<String, ArrayList<String>> denominator = new HashMap<>();

        numerator.putAll(hiddens);
        ArrayList<String> a = new ArrayList<>();
        a.add(query[1]);
        numerator.put(query[0], a);

        denominator.putAll(hiddens);
        denominator.putAll(notQuery);

        double numeratorAns = CalculateProb(combinations(numerator));
        double denominatorAns = CalculateProb(combinations(denominator));

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

                x *= node.getCPT().getProb(parents);
            }
            ans+=x;
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
        st+= "}.";

        return "SimpleAlgo: { " + st + " }";
    }

}
