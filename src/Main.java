
/**
 * @author Erik Reed
 */
public class Main {

  public static void main(String[] args) {
    BayesianNetwork bn = new BayesianNetwork();

    Node cloudy = new Node("cloudy", 2);
    Node sprinkler = new Node("sprinkler", 2, cloudy);
    Node rain = new Node("rain", 2, cloudy);
    Node grass = new Node("grass", 2, rain, sprinkler);

    bn.addNodes(cloudy, sprinkler, rain, grass);
  }

}
