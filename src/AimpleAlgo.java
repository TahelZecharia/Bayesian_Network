//import java.util.*;
//
//public class SimpleAlgo {
//
//    private BayesianNetwork BN;
//    // Hashmap with the query variable and his outcomes
//    private HashMap<String, String> query;
//    // Hashmap of all the evidence variables and their outcomes
//    private HashMap<Integer, String> evidences;
//    // Hashmap of all the hidden variables and their outcomes
//    private HashMap<Integer, ArrayList<String>> hiddens;
//    private String query;
//    private ArrayList<String> hiddens;
//    private ArrayList<String> evidences;
//
//    public SimpleAlgo(BayesianNetwork bayesianNetwork, String query, ArrayList<String> hiddens, ArrayList<String> evidences){
//
//        this.bayesianNetwork = bayesianNetwork;
//        this.query = query;
//        this.hiddens = hiddens;
//        this.evidences = evidences;
//    }
//
//    public Double CalculateQuery(){
//
//        ArrayList<String> Q =  new ArrayList<>();
//        Q.add(this.query);
//        Q.addAll(this.hiddens);
//        Q.addAll(this.hiddens);
//        return 0.0;
//    }
//
//}
