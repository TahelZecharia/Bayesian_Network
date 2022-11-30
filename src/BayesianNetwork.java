import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BayesianNetwork {

    // Hashmap of all the nodes.
    private HashMap<String, Node> Nodes;
    // Hashmap of all the parents of a node (incoming edges)
    private HashMap<Integer, ArrayList<String>> Parents;
    // Hashmap of all the children of a node (out edges)
    private HashMap<Integer, ArrayList<String>> Children;

    public BayesianNetwork(){

        this.Nodes = new HashMap<>();
        this.Parents = new HashMap<>();
        this.Children = new HashMap<>();
    }

    public HashMap<String, Node> getNodes(){

        return this.Nodes;
    }

    public Node getNode(String name) {

        if (Nodes.containsKey(name))
            return Nodes.get(name);

        return null;
    }

    public void addNode(String name, Node node){

        this.Nodes.put(name, node);
    }

    public BayesianNetwork readXML(String fileName){

        BayesianNetwork net = new BayesianNetwork();

        try {

            File xmlFile = new File(fileName);
            Scanner scanner = new Scanner(xmlFile);

            while (scanner.hasNextLine()) {

                String data = scanner.nextLine();
                if(data.matches("(.*<VAR.*)")){
                    readVariables(net, scanner);
                }
                else if(data.matches("(.*<DEF.*)")) {
                    readDefinitions(net, scanner);
                }
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return net;
    }

    // from each part of Variable in the xml file,
    // this method create bayesian node and append it to the Bayesian Network
    private static void readVariables(BayesianNetwork net, Scanner scanner){

        String data = scanner.nextLine();
        String name = getData(data);
        ArrayList<String> outcomes = new ArrayList<>();
        data = scanner.nextLine();

        while (data.matches("(.*)<OUTCOME>(.*)</OUTCOME>(.*)")){
            String value = getData(data);
            outcomes.add(value);
            data = scanner.nextLine();
        }
        Node node = new Node(name);

        for (String outcome : outcomes){

            node.addOutcome(outcome);
        }

        net.addNode(name, node);
    }

    // this method set the data of each Bayesian Node from the xml file
    private static void readDefinitions(BayesianNetwork net, Scanner scanner){
        // the main data we want to get:

        ArrayList<String> parents = new ArrayList<>();
//        ArrayList<Double> probs = new ArrayList<>();

        //name
        String data = scanner.nextLine();
        String name = getData(data);

        //parents
        data = scanner.nextLine();
        while (data.matches("(.*)<GIVEN>(.*)</GIVEN>(.*)")){
            parents.add(getData(data));
            data = scanner.nextLine();
        }

        //initialize parents
        for (String parent : parents){

            net.getNode(name).addParent(parent);
        }

        //probabilities:
        String[] probs = data.split(">")[1].split("<")[0].split(" ");
        CPT cpt = new CPT();
        for (String str : probs){
            cpt.addProb(Double.parseDouble(str));
        }


    }

    private static String getData(String line){
        String value = line.split(">")[1].split("<")[0];
        return value;
    }
}
