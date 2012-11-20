/**
 * @author Erik Reed
 */
public class Main {

  public static void main(String[] args) {

    BayesianNetwork bn = TestBayesianNetwork.makeSprinkerBN();

    bn.clampNode("rain", 1);
    BayesianNetwork.printStates(bn.jointProbability());
    System.out.println(bn.mostProbableExplanation());
    System.out.println(bn.nodes);
  }
}
