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

  private final List<Node> nodes = new ArrayList<Node>();
  private final Map<Node, Integer> clampedNodes = new HashMap<Node, Integer>();

  public InferenceType inferenceType = InferenceType.FullJoint;
  public static final double DOUBLE_EPSILON = 1 - 1e-12;

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

  private Node getNode(String name) {
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

  public Map<Node, Double[]> jointProbability() {
    return inference(clampedStates());
  }

  public Map<Node, Integer> mostProbableExplanation() {
    return mostProbableExplanation(clampedStates());
  }

  private Map<Node, Integer> mostProbableExplanation(Map<Node, Double[]> states) {
    Map<Node, Integer> expected = new HashMap<Node, Integer>();
    for (Entry<Node, Double[]> node : states.entrySet()) {
      expected.put(node.getKey(), argMax(node.getValue()));
    }
    return expected;
  }

  // TODO: Double to double (no longer necessary)

  private Map<Node, Double[]> clampedStates() {
    Map<Node, Double[]> states = new HashMap<Node, Double[]>();
    for (Entry<Node, Integer> node : clampedNodes.entrySet()) {
      Double[] probs = zeros(new Double[node.getKey().numStates]);
      probs[node.getValue()] = 1.0;
      states.put(node.getKey(), probs);
    }
    return states;
  }

  private Map<Node, Double[]> inference(Map<Node, Double[]> states) {
    switch (inferenceType) {
      case FullJoint:
        return jointTable(states);
      case VariableElimination:
      case JunctionTree:
      default:
        throw new Error("Unknown or unimplemented inference type");
    }
  }

  private Map<Node, Double[]> jointTable(Map<Node, Double[]> states) {
    for (Node n : nodes) {
      assert !states.containsKey(n);
      if (n.parents == null) {
        states.put(n, n.getCPT()[0]);
      } else {
        Double[][] cpt = n.getCPT();
        Double[] nodeStates = zeros(new Double[n.numStates]);

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

  private double parentProbs(int row, Node[] parents, Map<Node, Double[]> states) {
    int currentIndex = 1;
    double parentProbs = 1;
    for (int k = 0; k < parents.length; k++) {
      parentProbs *= states.get(parents[k])[row / currentIndex % parents[k].numStates];
      currentIndex *= parents[k].numStates;
    }
    return parentProbs;
  }

  public static void printStates(Map<Node, Double[]> states) {
    for (Entry<Node, Double[]> entry : states.entrySet()) {
      System.out.println(entry.getKey() + ": " + Arrays.toString(entry.getValue()));
    }
  }

  private int argMax(Double[] row) {
    assert row.length > 0;
    int index = 0;
    Double max = row[0];
    for (int i = 1; i < row.length; i++) {
      if (max < row[i]) {
        max = row[i];
        index = i;
      }
    }
    return index;
  }

  private Double[] logArray(Double[] row) {
    Double[] logArray = new Double[row.length];
    for (int i = 0; i < row.length; i++) {
      logArray[i] = Math.log(row[i]);
    }
    return logArray;
  }

  private Double[] setToValue(Double[] row, double val) {
    for (int i = 0; i < row.length; i++) {
      row[i] = val;
    }
    return row;
  }

  private Double[] zeros(Double[] row) {
    return setToValue(row, 0.0);
  }

  private void topologicalSort() {
    final Map<Node, Integer> depths = new HashMap<Node, Integer>();
    for (Node n : nodes) {
      depths.put(n, n.getTopologicalSortOrder());
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
