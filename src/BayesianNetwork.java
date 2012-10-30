import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Erik Reed
 */
public class BayesianNetwork {

  public static enum InferenceType {
    FullJoint, VariableElimination, JunctionTree
  }

  private final List<Node> nodes = new ArrayList<Node>();

  public static final double DOUBLE_EPSILON = .999999;

  public BayesianNetwork(Node... nodes) {
    addNodes(nodes);
    topologicalSort();
  }

  private void addNodes(Node... newNodes) {
    for (Node node : newNodes) {
      node.id = nodes.size();
      nodes.add(node);
    }
  }

  public Node getNode(String name) {
    name = name.toLowerCase();
    for (Node n : nodes) {
      if (n.name.equals(name)) {
        return n;
      }
    }
    throw new Error("node not found");
  }

  public Node getNode(int id) {
    for (Node n : nodes) {
      if (n.id == id) {
        return n;
      }
    }
    throw new Error("node not found");
  }

  public double stateProbability(Map<Node, Integer> states) {
    if (states.size() != nodes.size()) {
      throw new Error("states != num nodes");
    }

    return 0;
  }

  private void query(Node n, Map<Node, Integer> states) {

  }

  public void query(String name) {
    Node n = getNode(name);
  }

  private void topologicalSort() {
    final Map<Node, Integer> depths = new HashMap<Node, Integer>();
    for (Node n : nodes) {
      depths.put(n, n.getTopologicalSortOrder());
    }
    Comparator<Node> c = new Comparator<Node>() {
      @Override
      public int compare(Node n1, Node n2) {
        return Integer.compare(depths.get(n2), depths.get(n1));
      }
    };
    Collections.sort(nodes, c);
  }
}
