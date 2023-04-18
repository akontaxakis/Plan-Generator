package DirectedGraph;
import java.util.ArrayList;

/**
 * The node class that is used for DirectedGraph
 *
 * @author Ruben Beyer
 */
public class Node {

    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private int position;
    public void  addEdge(Edge edge) {
        this.edges.add(edge);
    }
    public Edge getEdge(int number) {
        if (this.edges.size() <= number) {
            return null;
        } else {
            return this.edges.get(number);
        }
    }
    public int getOutLeadingOrder() {
        return this.edges.size();
    }

}