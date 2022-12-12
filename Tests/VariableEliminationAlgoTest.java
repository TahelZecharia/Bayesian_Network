import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableEliminationAlgoTest {

    BayesianNetwork myNet = new BayesianNetwork().readXML("alarm_net.xml");
//    VariableEliminationAlgo Algo1 = new VariableEliminationAlgo(myNet, "P(B=T|J=T,M=T)");
    VariableEliminationAlgo Algo2 = new VariableEliminationAlgo(myNet, "P(J=T|B=T)");

    @Test
    void calculateQuery() {

//        System.out.println(Algo1);
//        System.out.printf("%.5f%n", Algo1.CalculateQuery()); // 0.28417
        System.out.printf("%.5f%n", Algo2.CalculateQuery()); // 0.84902
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