import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author Erik Reed
 */
public class BayesianNetwork {

  public static enum InferenceType {
    FullJoint, VariableElimination, JunctionTree
  }

  public final List<Node> nodes = new ArrayList<Node>();
  private final Map<Node, Integer> clampedNodes = new HashMap<Node, Integer>();

  public InferenceType inferenceType = InferenceType.FullJoint;
  public static final double DOUBLE_EPSILON = 1 - 1e-12;

  public BayesianNetwork(Node... nodes) {
    addNodes(nodes);
  }

  private void addNodes(Node... newNodes) {
    for (Node node : newNodes) {
      node.id = nodes.size();
      nodes.add(node);
    }
    updateChildren();
  }

  private void updateChildren() {
    // update children of each node.
    Map<Node, ArrayList<Node>> nodeChildren = new HashMap<Node, ArrayList<Node>>();
    for (final Node node : nodes) {
      if (node.parents != null) {
        for (final Node p : node.parents) {
          if (nodeChildren.containsKey(p)) {
            nodeChildren.get(p).add(node);
          } else {
            ArrayList<Node> children = new ArrayList<Node>();
            children.add(node);
            nodeChildren.put(p, children);
          }
        }
      }
    }
    for (Entry<Node, ArrayList<Node>> node : nodeChildren.entrySet()) {
      node.getKey().children = node.getValue().toArray(new Node[node.getValue().size()]);
    }
  }

  // TODO: use a hashmap instead
  public Node getNode(String name) {
    name = name.toLowerCase();
    for (Node n : nodes) {
      if (n.name.equals(name)) {
        return n;
      }
    }
    throw new Error("node not found");
  }

  private Node getNode(int id) {
    for (Node n : nodes) {
      if (n.id == id) {
        return n;
      }
    }
    throw new Error("node not found");
  }

  private void clampNode(Node n, int state) {
    assert nodes.contains(n);
    clampedNodes.put(n, state);
  }

  public void clampNode(String name, int state) {
    Node n = getNode(name);
    clampNode(n, state);
  }

  public void clearClampedNodes() {
    clampedNodes.clear();
  }

  public Map<Node, double[]> jointProbability() {
    return inference(clampedStates());
  }

  public Map<Node, Integer> mostProbableExplanation() {
    return mostProbableExplanation(jointProbability());
  }

  private Map<Node, Integer> mostProbableExplanation(Map<Node, double[]> states) {
    Map<Node, Integer> expected = new HashMap<Node, Integer>();
    for (Entry<Node, double[]> node : states.entrySet()) {
      expected.put(node.getKey(), argMax(node.getValue()));
    }
    return expected;
  }

  private Map<Node, double[]> clampedStates() {
    Map<Node, double[]> states = new HashMap<Node, double[]>();
    for (Entry<Node, Integer> node : clampedNodes.entrySet()) {
      double[] probs = new double[node.getKey().numStates];
      probs[node.getValue()] = 1.0;
      states.put(node.getKey(), probs);
    }
    return states;
  }

  private Map<Node, double[]> inference(Map<Node, double[]> states) {
    topologicalSort();
    switch (inferenceType) {
      case FullJoint:
        return jointTable(states);
      case VariableElimination:
      case JunctionTree:
      default:
        throw new Error("Unknown or unimplemented inference type");
    }
  }

  private Map<Node, double[]> jointTable(Map<Node, double[]> states) {
    for (Node n : nodes) {
      if (states.containsKey(n)) {
        // Node is clamped.
        continue;
      }
      if (n.parents == null) {
        states.put(n, n.getCPT()[0]);
      } else {
        double[][] cpt = n.getCPT();
        double[] nodeStates = new double[n.numStates];

        for (int j = 0; j < n.numCptRows(); j++) {
          double parentProbs = parentProbs(j, n.parents, states);
          for (int i = 0; i < n.numStates; i++) {
            nodeStates[i] += parentProbs * cpt[j][i];
          }
        }
        states.put(n, nodeStates);
      }
    }
    return states;
  }

  private double parentProbs(int row, Node[] parents, Map<Node, double[]> states) {
    int currentIndex = 1;
    double parentProbs = 1;
    for (int k = 0; k < parents.length; k++) {
      parentProbs *= states.get(parents[k])[row / currentIndex % parents[k].numStates];
      currentIndex *= parents[k].numStates;
    }
    return parentProbs;
  }

  public static void printStates(Map<Node, double[]> states) {
    for (Entry<Node, double[]> entry : states.entrySet()) {
      System.out.println(entry.getKey() + ": " + Arrays.toString(entry.getValue()));
    }
  }

  private int argMax(double[] row) {
    assert row.length > 0;
    int index = 0;
    double max = row[0];
    for (int i = 1; i < row.length; i++) {
      if (max < row[i]) {
        max = row[i];
        index = i;
      }
    }
    return index;
  }

  private double[] logArray(double[] row) {
    for (int i = 0; i < row.length; i++) {
      row[i] = Math.log(row[i]);
    }
    return row;
  }

  private double[] expArray(double[] row) {
    for (int i = 0; i < row.length; i++) {
      row[i] = Math.exp(row[i]);
    }
    return row;
  }

  private double[] setToValue(double[] row, double val) {
    for (int i = 0; i < row.length; i++) {
      row[i] = val;
    }
    return row;
  }

  private double[] zeros(double[] row) {
    return setToValue(row, 0.0);
  }

  private void topologicalSort() {
    final Map<Node, Integer> depths = new HashMap<Node, Integer>();
    for (Entry<Node, Integer> node : clampedNodes.entrySet()) {
      depths.put(node.getKey(), 0);
    }
    for (Node n : nodes) {
      depths.put(n, n.getTopologicalSortOrder(depths));
    }
    Comparator<Node> c = new Comparator<Node>() {
      @Override
      public int compare(Node n1, Node n2) {
        return Integer.compare(depths.get(n1), depths.get(n2));
      }
    };
    Collections.sort(nodes, c);
  }
}
