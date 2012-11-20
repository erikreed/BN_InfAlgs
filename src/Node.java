import java.util.Map;

/**
 * @author Erik Reed
 */
public class Node {

  public int id;
  public final String name;
  public final int numStates;

  public Node[] parents = null;
  public Node[] children = null;

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
      if (sum <= BayesianNetwork.DOUBLE_EPSILON || sum > 1) {
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

  public int getTopologicalSortOrder(Map<Node, Integer> depths) {
    return topologicalSortHelper(this, 0, depths);
  }

  private static int topologicalSortHelper(Node n, int depth, Map<Node, Integer> depths) {
    if (depths.containsKey(n)) {
      return depths.get(n) + depth;
    }
    int maxDepth1 = depth;
    int maxDepth2 = depth;

    if (n.parents != null) {
      for (Node p : n.parents) {
        maxDepth1 = Math.max(topologicalSortHelper(p, depth + 1, depths), maxDepth1);
      }
    }

    depth = Math.max(maxDepth1, maxDepth2);
    depths.put(n, depth);

    if (n.children != null) {
      for (Node p : n.children) {
        maxDepth2 = Math.min(topologicalSortHelper(p, depth + 1, depths), maxDepth2);
      }
      depth = Math.max(maxDepth1, maxDepth2);
      depths.put(n, depth);
    }

    return depth;
  }

  @Override
  public String toString() {
    return name;
  }

  public double[][] getCPT() {
    return cpt;
  }

  public int numCptRows() {
    return cpt.length;
  }
}
