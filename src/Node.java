/**
 * @author Erik Reed
 */
public class Node {

  public int id;
  public final String name;
  public final int numStates;

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
    int expectedCptSize = numStates;
    if (parents != null) {
      for (Node p : parents) {
        expectedCptSize *= p.numStates;
      }
    }
    int cptSize = 0;
    for (double[] row : cpt) {
      cptSize += row.length;
      if (row.length != numStates) {
        throw new Error("invalid CPT size -- unexpected num of cols");
      }
      double sum = 0;
      for (double cp : row) {
        sum += cp;
      }
      if (sum <= BayesianNetwork.DOUBLE_EPSILON) {
        throw new Error("invalid CPT entries -- not summing to 1");
      }
    }
    if (cptSize != expectedCptSize) {
      throw new Error("invalid CPT entries -- unexpected num of rows");
    }
  }

  @Override
  public int hashCode() {
    return Integer.valueOf(id).hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Node) {
      Node o = (Node) other;
      if (this.id == o.id) {
        assert this.name.equals(o.name);
        return true;
      }
    }
    return false;
  }

  public int getTopologicalSortOrder() {
    return topologicalSortHelper(this, 0);
  }

  private static int topologicalSortHelper(Node n, int depth) {
    if (n.parents == null) {
      return depth;
    }
    int maxDepth = depth;
    for (Node p : n.parents) {
      maxDepth = Math.max(topologicalSortHelper(p, depth + 1), maxDepth);
    }
    return maxDepth;
  }

  @Override
  public String toString() {
    return name;
  }
}
