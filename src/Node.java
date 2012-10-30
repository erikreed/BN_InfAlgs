/**
 * @author Erik Reed
 */
public class Node {

  public int id;
  public final String name;
  public final int numStates;

  private final int currentState = -1;
  public Node[] parents = null;

  private double[][] cpt = null;



  public Node(String name, int states, Node... parents) {
    this(name, states);
    this.parents = parents;
  }

  public Node(String name, int states) {
    this.name = name.toLowerCase();
    this.numStates = states;
  }

  public void setCPT(double[][] cpt) {
    this.cpt = cpt;
    validate();
  }

  private void validate() {
    if (cpt.length == 0) {
      throw new Error("invalid CPT size");
    }
    for (double[] row : cpt) {
      if (row.length != numStates) {
        throw new Error("invalid CPT size");
      }
      double sum = 0;
      for (double cp : row) {
        sum += cp;
      }
      if (sum <= BayesianNetwork.DOUBLE_EPSILON) {
        throw new Error("invalid CPT entries");
      }
    }
  }
}
