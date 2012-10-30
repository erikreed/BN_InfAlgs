/**
 * @author Erik Reed
 */
public class Main {

  public static void main(String[] args) {
    BayesianNetwork bn = new BayesianNetwork();

    Node cloudy = new Node("cloudy", 2);
    cloudy.setCPT(new double[][] {{.5, .5}});

    Node sprinkler = new Node("sprinkler", 2, cloudy);
    sprinkler.setCPT(new double[][] { {.5, .5}, {.9, .1}});

    Node rain = new Node("rain", 2, cloudy);
    rain.setCPT(new double[][] { {.8, .2}, {.2, .8}});

    Node grass = new Node("grass", 2, rain, sprinkler);
    grass.setCPT(new double[][] { {1, 0}, {.1, .9}, {.1, .9}, {.01, .99}});

    bn.addNodes(cloudy, sprinkler, rain, grass);
  }

}
