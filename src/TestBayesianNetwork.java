import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.JUnitCore;


/**
 * @author Erik Reed
 */
public class TestBayesianNetwork {
  public static void main(String[] args) {
    JUnitCore.runClasses(TestBayesianNetwork.class);
  }

  @Test
  public void testCpt1() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new double[][] {{.5, .5}});
  }

  @Test(expected = Error.class)
  public void testCptFail1() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new double[][] {{.5, .5, .3}});
  }

  @Test(expected = Error.class)
  public void testCptFail2() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new double[][] { {.5, .5}, {.3, .7}});
  }

  @Test(expected = Error.class)
  public void testCptFail3() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new double[][] {{.5, .6}});
  }

  @Test(expected = Error.class)
  public void testCptFail4() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new double[][] {{.5, .3}});
  }

  @Test
  public void testSprinkler1() {
    BayesianNetwork bn = makeSprinkerBN();

    Map<Node, double[]> probs = bn.jointProbability();
    assertTrue(Arrays.equals(probs.get(bn.getNode("cloudy")), new double[] {.5, .5}));
    assertTrue(Arrays.equals(probs.get(bn.getNode("rain")), new double[] {.5, .5}));
    assertTrue(Arrays.equals(probs.get(bn.getNode("sprinkler")), new double[] {.7, .3}));
    assertTrue(Arrays.equals(roundToNDigits(probs.get(bn.getNode("grass")), 4), new double[] {
        .4015, .5985}));
  }

  @Test
  public void testSprinkler2() {
    BayesianNetwork bn = makeSprinkerBN();

    Map<Node, Integer> states = bn.mostProbableExplanation();
    assertTrue(states.get(bn.getNode("sprinkler")) == 0);
    assertTrue(states.get(bn.getNode("grass")) == 1);
  }

  @Test
  public void testSprinklerInf1() {
    BayesianNetwork bn = makeSprinkerBN();
    bn.clampNode("cloudy", 1);

    Map<Node, double[]> probs = bn.jointProbability();
    assertTrue(Arrays.equals(probs.get(bn.getNode("cloudy")), new double[] {0.0, 1.0}));
    assertTrue(Arrays.equals(probs.get(bn.getNode("rain")), new double[] {.2, .8}));
    assertTrue(Arrays.equals(probs.get(bn.getNode("sprinkler")), new double[] {.9, .1}));
    assertTrue(Arrays.equals(roundToNDigits(probs.get(bn.getNode("grass")), 4), new double[] {
        .2548, .7452}));
  }

  @Test
  public void testSprinklerInf2() {
    BayesianNetwork bn = makeSprinkerBN();
    bn.clampNode("rain", 0);

    Map<Node, double[]> probs = bn.jointProbability();
    assertTrue(Arrays.equals(probs.get(bn.getNode("cloudy")), new double[] {.8, .2}));
    assertTrue(Arrays.equals(probs.get(bn.getNode("rain")), new double[] {1.0, 0.0}));
    assertTrue(Arrays.equals(probs.get(bn.getNode("sprinkler")), new double[] {.58, .42}));
    assertTrue(Arrays.equals(roundToNDigits(probs.get(bn.getNode("grass")), 4), new double[] {
        .6220, .3780}));
  }

  public static BayesianNetwork makeSprinkerBN() {
    Node cloudy = new Node("cloudy", 2);
    cloudy.setCPT(new double[][] {{.5, .5}});

    Node sprinkler = new Node("sprinkler", 2, cloudy);
    sprinkler.setCPT(new double[][] { {.5, .5}, {.9, .1}});

    Node rain = new Node("rain", 2, cloudy);
    rain.setCPT(new double[][] { {.8, .2}, {.2, .8}});

    Node grass = new Node("grass", 2, rain, sprinkler);
    grass.setCPT(new double[][] { {1.0, 0.0}, {.1, .9}, {.1, .9}, {.01, .99}});

    BayesianNetwork bn = new BayesianNetwork(cloudy, sprinkler, rain, grass);
    return bn;
  }

  public static double roundToNDigits(double d, int n) {
    BigDecimal bd = new BigDecimal(d);
    bd = bd.round(new MathContext(n));
    return bd.doubleValue();
  }

  public static double[] roundToNDigits(double[] d, int n) {
    for (int i = 0; i < d.length; i++) {
      d[i] = roundToNDigits(d[i], n);
    }
    return d;
  }
}
