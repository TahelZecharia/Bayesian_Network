

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


class SimpleAlgoTest {

    BayesianNetwork myNet = new BayesianNetwork().readXML("alarm_net.xml");
    SimpleAlgo mySimpleAlgo1 = new SimpleAlgo(myNet, "P(B=T|J=T,M=T)");
    SimpleAlgo mySimpleAlgo2 = new SimpleAlgo(myNet, "P(J=T|B=T)");

    @Test
    void calculateQuery() {

        System.out.println(mySimpleAlgo1);

//        System.out.printf("%.5f%n", mySimpleAlgo1.CalculateQuery()); // 0.28417
//        System.out.printf("%.5f%n", mySimpleAlgo2.CalculateQuery()); // 0.84902

        assertEquals(String.format("%.5f", mySimpleAlgo1.CalculateQuery()), "0.28417");
        assertEquals(String.format("%.5f", mySimpleAlgo2.CalculateQuery()), "0.84902");

        // an exists prob:
        SimpleAlgo mySimpleAlgo3 = new SimpleAlgo(myNet, "P(A=T|B=F,E=T,J=T)");
        System.out.println(myNet.toString());
        System.out.printf("%.5f%n", mySimpleAlgo3.CalculateQuery()); // 0.29
        System.out.println(mySimpleAlgo3.getAddCounter()); // 0
        System.out.println(mySimpleAlgo3.getMulCounter()); // 0

    }

    @Test
    void combinations() {
//        System.out.println(mySimpleAlgo1.combinations());
    }
}