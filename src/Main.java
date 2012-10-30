/**
 * @author Erik Reed
 */
public class Main {

  public static void main(String[] args) {

    BayesianNetwork bn = makeSprinkerBN();
    bn.query("cloudy");
  }

  public static BayesianNetwork makeSprinkerBN() {
    Node cloudy = new Node("cloudy", 2);
    cloudy.setCPT(new Double[][] {{.5, .5}});

    Node sprinkler = new Node("sprinkler", 2, cloudy);
    sprinkler.setCPT(new Double[][] { {.5, .5}, {.9, .1}});

    Node rain = new Node("rain", 2, cloudy);
    rain.setCPT(new Double[][] { {.8, .2}, {.2, .8}});

    Node grass = new Node("grass", 2, rain, sprinkler);
    grass.setCPT(new Double[][] { {1.0, 0.0}, {.1, .9}, {.1, .9}, {.01, .99}});

    BayesianNetwork bn = new BayesianNetwork(cloudy, sprinkler, rain, grass);
    return bn;
  }
}
