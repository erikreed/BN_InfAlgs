import java.util.ArrayList;
import java.util.List;


/**
 * @author Erik Reed
 */
public class BayesianNetwork {

  private final List<Node> nodes = new ArrayList<Node>();

  public static final double DOUBLE_EPSILON = .999999;

  public BayesianNetwork() {

  }

  public void addNodes(Node... newNodes) {
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

}
