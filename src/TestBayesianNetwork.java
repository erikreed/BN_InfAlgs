import org.junit.Test;
import org.junit.runner.JUnitCore;


/**
 * @author Erik Reed
 */
public class TestBayesianNetwork {
  public static void main(String[] args) {
    JUnitCore.runClasses(TestBayesianNetwork.class);
  }

  @Test
  public void testCpt1() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new Double[][] {{.5, .5}});
  }

  @Test(expected = Error.class)
  public void testCptFail1() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new Double[][] {{.5, .5, .3}});
  }

  @Test(expected = Error.class)
  public void testCptFail2() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new Double[][] {{.5, .5}, {.3, .7}});
  }

  @Test(expected = Error.class)
  public void testCptFail3() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new Double[][] {{.5, .6}});
  }

  @Test(expected = Error.class)
  public void testCptFail4() {
    Node n = new Node("BN_Node", 2);
    n.setCPT(new Double[][] {{.5, .3}});
  }
}
