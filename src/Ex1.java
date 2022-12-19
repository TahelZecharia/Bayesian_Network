import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Ex1 {

    public static void main(String[] args) {

        try{
            // open output file:
            FileWriter outputFile = new FileWriter("output1.txt");

            try{

                // open input file:
                File inputFile = new File("input2.txt");
                Scanner scanner = new Scanner(inputFile);

                BayesianNetwork myNet = new BayesianNetwork();

                // read bayesian network:
                try {

                    myNet = myNet.readXML(scanner.nextLine());
                    System.out.println(myNet);

                }catch (Exception e){

                    System.out.println("XML not found");
                    e.printStackTrace();
                }

                while (scanner.hasNextLine()) {

                    String data = scanner.nextLine();
                    int algo = Integer.parseInt(data.substring(data.length()-1));
                    String query = data.substring(0, data.length()-2);
//
//                    double ans = 0.0;
//                    int add = 0;
//                    int mul = 0;

//                    if (Objects.equals(algo, "1")) {
//
//                        SimpleAlgo myAlgo = new SimpleAlgo(myNet, query);
//                        ans = myAlgo.CalculateQuery();
//                        add = myAlgo.getAddCounter();
//                        mul = myAlgo.getMulCounter();
//
//                    }
//                    else if (Objects.equals(algo, "2")) {
////                    else{
//
//                        VariableEliminationAlgo myAlgo = new VariableEliminationAlgo(myNet, query);
//                        ans = myAlgo.CalculateQuery(2);
//                        add = myAlgo.getAddCounter();
//                        mul = myAlgo.getMulCounter();
//                    }
//
//                    else{
//
//                        VariableEliminationAlgo myAlgo = new VariableEliminationAlgo(myNet, query);
//                        ans = myAlgo.CalculateQuery(3);
//                        add = myAlgo.getAddCounter();
//                        mul = myAlgo.getMulCounter();
//                    }

                    Algo myAlgo = new Algo(myNet, query);
                    double ans = myAlgo.CalculateQuery(algo);
                    int add = myAlgo.getAddCounter();
                    int mul = myAlgo.getMulCounter();


                    outputFile.write(String.format("%.5f", ans) + "," + add + "," + mul);

                    if (scanner.hasNextLine()) {
                        outputFile.write("\n");
                    }
                }
                scanner.close();
            }
            catch (Exception e){

                e.printStackTrace();
            }

            outputFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
