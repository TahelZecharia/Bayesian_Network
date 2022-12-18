import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableEliminationAlgoTest {

    BayesianNetwork myNet = new BayesianNetwork().readXML("alarm_net.xml");
    VariableEliminationAlgo Algo1 = new VariableEliminationAlgo(myNet, "P(B=T|J=T,M=T)");
    VariableEliminationAlgo Algo2 = new VariableEliminationAlgo(myNet, "P(J=T|B=T)");

    @Test
    void calculateQuery() {

//        VariableEliminationAlgo Algo3 = new VariableEliminationAlgo(myNet, "P(A=T|B=F,E=T)");
//        System.out.printf("%.5f%n", Algo3.CalculateQuery()); // 0.29
//        System.out.println(Algo3.getAddCounter()); // 0
//        System.out.println(Algo3.getMulCounter()); // 0

        BayesianNetwork myBigNet = new BayesianNetwork().readXML("big_net.xml");
        VariableEliminationAlgo Algo4 = new VariableEliminationAlgo(myBigNet, "P(B0=v3|C3=T,B2=F,C2=v3)");
        System.out.printf("%.5f%n", Algo4.CalculateQuery(2)); // 0.42307,10,21
        System.out.println(Algo4.getAddCounter()); // 10
        System.out.println(Algo4.getMulCounter()); // 21



//        System.out.println(Algo1);
//        System.out.printf("%.5f%n", Algo1.CalculateQuery()); // 0.28417
//        System.out.printf("%.5f%n", Algo2.CalculateQuery()); // 0.84902
//        assertEquals(String.format("%.5f", Algo1.CalculateQuery()), "0.28417");
        assertEquals(String.format("%.5f", Algo1.CalculateQuery(2)), "0.28417");
        assertEquals(String.format("%.5f", Algo2.CalculateQuery(2)), "0.84902");
    }

    @Test
    void join() {
    }

    @Test
    void elimination() {
    }

    @Test
    void normalize() {
    }
}