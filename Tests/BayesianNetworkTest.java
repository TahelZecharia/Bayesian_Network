import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


class BayesianNetworkTest {

    BayesianNetwork myNet = new BayesianNetwork();

    @Test
    void getNodes() {
    }

    @org.junit.jupiter.api.Test
    void getNode() {
    }

    @org.junit.jupiter.api.Test
    void addNode() {
    }

    @org.junit.jupiter.api.Test
    void addParent() {
    }

    @org.junit.jupiter.api.Test
    void addChild() {

        HashMap<String, String> a = new HashMap<>();
        a.put("a", "b");
        a.put("a", "c");
        System.out.println();

    }

    @org.junit.jupiter.api.Test
    void readXML() {

        myNet = myNet.readXML("alarm_net.xml");
        System.out.println(myNet.toString());
        HashMap<String, String> outcomes = new HashMap<>();
        outcomes.put("E", "T");
        outcomes.put("B", "F");
        outcomes.put("A", "T");
        Assertions.assertEquals(myNet.getNode("A").getCPT().getProb(outcomes), 0.29);
    }
}