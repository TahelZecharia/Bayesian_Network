import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgoTest {

    BayesianNetwork alarmNet = new BayesianNetwork().readXML("alarm_net.xml");

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

    }
}