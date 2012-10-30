import java.util.List;


public class Node {

  private final int id;
  private final String name;
  private final int numStates;

  private final int currentState = -1;
  private final List<Node> parents;

  public Node(String name, int id, int states, List<Node> parents) {
    this.id = id;
    this.name = name;
    this.numStates = states;
    this.parents = parents;
  }

}
