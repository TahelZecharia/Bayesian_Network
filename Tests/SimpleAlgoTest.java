

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


class SimpleAlgoTest {

    BayesianNetwork myNet = new BayesianNetwork().readXML("alarm_net.xml");
    SimpleAlgo mySimpleAlgo1 = new SimpleAlgo(myNet, "P(B=T|J=T,M=T)");
    SimpleAlgo mySimpleAlgo2 = new SimpleAlgo(myNet, "P(J=T|B=T)");

    @Test
    void calculateQuery() {

        System.out.println(mySimpleAlgo1);

        System.out.printf("%.5f%n", mySimpleAlgo1.CalculateQuery()); // 0.28417
        System.out.printf("%.5f%n", mySimpleAlgo2.CalculateQuery()); // 0.84902
    }

    @Test
    void combinations() {
//        System.out.println(mySimpleAlgo1.combinations());
    }
}