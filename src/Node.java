

/**
 * @author Erik Reed
 */
public class Node {

  public int id;
  public final String name;
  public final int numStates;

  private final int currentState = -1;
  public Node[] parents = null;

  private double[] priors = null;



  public Node(String name, int states, Node... parents) {
    this(name, states);
    this.parents = parents;
  }

  public Node(String name, int states) {
    this.name = name.toLowerCase();
    this.numStates = states;
  }

  public void setPriors(double[] priors) {
    assert(valid());
  }

  private boolean valid() {
    if (priors.length != numStates) {
      return false;
    }
    double sum = 0;
    for (double d : priors) {
      sum += d;
    }
    return sum <= BayesianNetwork.DOUBLE_EPSILON;
  }

}
