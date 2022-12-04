import java.util.*;

public class SimpleAlgo {

    private BayesianNetwork net;
    // Hashmap with the query variable and his outcomes
    private HashMap<String, String> query = new HashMap<>();
    // Hashmap with the query variable and his outcomes
    private HashMap<String, ArrayList<String>> notQuery = new HashMap<>();
    // Hashmap of all the evidence variables and their outcomes
    private HashMap<String, String> evidences = new HashMap<>();
    // Hashmap of all the hidden variables and their outcomes
    private HashMap<String, ArrayList<String>> hiddens = new HashMap<>();


    // get a BayesianNetwork and a query string (e.g P(B=T|J=T,M=T) )
    public SimpleAlgo(BayesianNetwork bayesianNetwork, String q){

        net = bayesianNetwork;

        // query:
        query.put(""+q.charAt(2), ""+q.charAt(4));

        // not query:
        notQuery.put(""+q.charAt(2), new ArrayList<>());
        for (String outcome : bayesianNetwork.getNode(""+q.charAt(2)).getOutcomes()){
            if (query.get(""+q.charAt(2)) != outcome){
                notQuery.get(""+q.charAt(2)).add(outcome);
            }
        }
        // evidence:
        for (int i = 6; i < q.length(); i+=4) {
            evidences.put(""+q.charAt(i), ""+q.charAt(i+2));
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

        ArrayList<HashMap<String, String>> combinations  =  combinations(hiddens);
        double ans = 0;

        for (HashMap<String, String> comb : combinations){

            comb.putAll(query);
            comb.putAll(evidences);
            double x = 1;

            for (Node node : net.getNodes().values()){

                HashMap<String, String> parents = new HashMap<>();
                parents.put(node.getName(), comb.get(node.getName()));

                for (String perent : node.getParents()){
                    parents.put(perent, comb.get(perent));
                }

                System.out.println(node.getCPT().getProb(parents));

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

        st+="query: " + query.toString() + ". ";

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
