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
    Map<Node, Double[]> states = new HashMap<Node, Double[]>();
    for (Node n : nodes) {
      assert !states.containsKey(n);
      if (n.parents == null) {
        states.put(n, n.getCPT()[0]);
      } else {
        Double[][] cpt = n.getCPT();
        Double[] nodeStates = zeros(new Double[n.numStates]);

        for (int j = 0; j < n.numCptRows(); j++) {
          int[] parentStates = rowIndexToParentStates(j, n.parents);
          double parentProbs = 1;
          for (int k = 0; k < n.parents.length; k++) {
            parentProbs *= states.get(n.parents[k])[parentStates[k]];
          }
          for (int i = 0; i < n.numStates; i++) {
            nodeStates[i] += parentProbs * cpt[j][i];
          }
        }
        states.put(n, nodeStates);
      }
    }
    printStates(states);
  }

  private int[] rowIndexToParentStates(int row, Node[] parents) {
    int[] parentStates = new int[parents.length];
    int currentIndex = parents[0].numStates;
    parentStates[0] = row % currentIndex;
    for (int k = 1; k < parents.length; k++) {
      parentStates[k] = row / currentIndex % parents[k].numStates;
      currentIndex *= parents[k].numStates;
    }
    return parentStates;
  }

  private void printStates(Map<Node, Double[]> states) {
    for (Entry<Node, Double[]> entry : states.entrySet()) {
      System.out.println(entry.getKey() + ": " + Arrays.toString(entry.getValue()));
    }
  }

  private int rowMaxIndex(double[] row) {
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
