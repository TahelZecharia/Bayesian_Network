import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgoTest {

    BayesianNetwork alarmNet = new BayesianNetwork().readXML("alarm_net.xml");
    BayesianNetwork bigNet = new BayesianNetwork().readXML("big_net.xml");

    @Test
    void calculateQuery() {

        String ans;

        // 1) Test from input:

        Algo algo1 = new Algo(alarmNet, "P(B=T|J=T,M=T)");
        ans = (String.format("%.5f", algo1.CalculateQuery(1))+ "," + algo1.getAddCounter() + "," + algo1.getMulCounter());
        assertEquals(ans, "0.28417,7,32");
        algo1 = new Algo(alarmNet, "P(B=T|J=T,M=T)");
        ans = (String.format("%.5f", algo1.CalculateQuery(2))+ "," + algo1.getAddCounter() + "," + algo1.getMulCounter());
        assertEquals(ans, "0.28417,7,16");
        algo1 = new Algo(alarmNet, "P(B=T|J=T,M=T)");
        ans = (String.format("%.5f", algo1.CalculateQuery(3))+ "," + algo1.getAddCounter() + "," + algo1.getMulCounter());
        assertEquals(ans, "0.28417,7,16");

        Algo algo2 = new Algo(alarmNet, "P(J=T|B=T)");
        ans = (String.format("%.5f", algo2.CalculateQuery(1))+ "," + algo2.getAddCounter() + "," + algo2.getMulCounter());
        assertEquals(ans, "0.84902,15,64");
        algo2 = new Algo(alarmNet, "P(J=T|B=T)");
        ans = (String.format("%.5f", algo2.CalculateQuery(2))+ "," + algo2.getAddCounter() + "," + algo2.getMulCounter());
        assertEquals(ans, "0.84902,7,12");
        algo2 = new Algo(alarmNet, "P(J=T|B=T)");
        ans = (String.format("%.5f", algo2.CalculateQuery(3))+ "," + algo2.getAddCounter() + "," + algo2.getMulCounter());
        assertEquals(ans, "0.84902,5,8");

        // 2) an exists prob:

        Algo algo3 = new Algo(alarmNet, "P(A=T|B=F,E=T)");
        ans = (String.format("%.5f", algo3.CalculateQuery(1))+ "," + algo3.getAddCounter() + "," + algo3.getMulCounter());
        assertEquals(ans, "0.29000,0,0");
        algo3 = new Algo(alarmNet, "P(A=T|B=F,E=T)");
        ans = (String.format("%.5f", algo3.CalculateQuery(2))+ "," + algo3.getAddCounter() + "," + algo3.getMulCounter());
        assertEquals(ans, "0.29000,0,0");
        algo3 = new Algo(alarmNet, "P(A=T|B=F,E=T)");
        ans = (String.format("%.5f", algo3.CalculateQuery(3))+ "," + algo3.getAddCounter() + "," + algo3.getMulCounter());
        assertEquals(ans, "0.29000,0,0");

        // 3) big net:
        Algo algo4 = new Algo(bigNet, "P(B0=v3|C3=T,B2=F,C2=v3)");
        ans = (String.format("%.5f", algo4.CalculateQuery(1))+ "," + algo4.getAddCounter() + "," + algo4.getMulCounter());
        assertEquals(ans, "0.42307,383,3840");
        algo4 = new Algo(bigNet, "P(B0=v3|C3=T,B2=F,C2=v3)");
        ans = (String.format("%.5f", algo4.CalculateQuery(2))+ "," + algo4.getAddCounter() + "," + algo4.getMulCounter());
        assertEquals(ans, "0.42307,13,27");
        algo4 = new Algo(bigNet, "P(B0=v3|C3=T,B2=F,C2=v3)");
        ans = (String.format("%.5f", algo4.CalculateQuery(3))+ "," + algo4.getAddCounter() + "," + algo4.getMulCounter());
        assertEquals(ans, "0.42307,13,27");

    }

    @Test
    void net3() {

        BayesianNetwork net3 = new BayesianNetwork().readXML("net3.xml");
        Algo algo1 = new Algo(net3, "P(S=ok|M=N)");
        String ans = (String.format("%.5f", algo1.CalculateQuery(1))+ "," + algo1.getAddCounter() + "," + algo1.getMulCounter());
        assertEquals(ans, "0.37606,17,54");

    }
}