package DirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class provides to calculate the maximum flow in directed graphs using
 * the Ford-Fulkerson Algorithm.
 *
 * @author Ruben Beyer
 */
public class MaximumFlow {


    private DirectedGraph g;


    public MaximumFlow(DirectedGraph k) {
        this.g = k;
    }

    /**
     * Main method just for testing
     *
     * @param args
     *            will be ignored
     */

    /**
     * This method actually calculates the maximum flow by using the
     * Ford-Fulkerson Algorithm.
     *
     * @param source The object identifying the source node of the flow
     * @param sink   The object identifying the sink node of the flow
     * @return A HashMap for the edges, giving every edge in the graph a value
     * which shows the part of the edge's capacity that is used by the
     * flow
     */
    public HashMap<Edge, Integer> getMaxFlow(Object source,
                                             Object sink) {
        // The path from source to sink that is found in each iteration
        LinkedList<Edge> path;
        // The flow, i.e. the capacity of each edge that is actually used
        HashMap<Edge, Integer> flow = new HashMap<Edge, Integer>();
        // Create initial empty flow.
        for (Edge e : g.getEdges()) {
            flow.put(e, 0);
        }

        // The Algorithm itself
        while ((path = bfs(source, sink, flow)) != null) {
            // Activating this output will illustrate how the algorithm works
            // System.out.println(path);
            // Find out the flow that can be sent on the found path.
            int minCapacity = Integer.MAX_VALUE;
            Object lastNode = source;
            for (Edge edge : path) {
                int c;
                // Although the edges are directed they can be used in both
                // directions if the capacity is partially used, so this if
                // statement is necessary to find out the edge's actual
                // direction.
                if (edge.getStart().equals(lastNode)) {
                    c = edge.getCapacity() - flow.get(edge);
                    lastNode = edge.getTarget();
                } else {
                    c = flow.get(edge);
                    lastNode = edge.getStart();
                }
                if (c < minCapacity) {
                    minCapacity = c;
                }
            }

            // Change flow of all edges of the path by the value calculated
            // above.
            lastNode = source;
            for (Edge edge : path) {
                // If statement like above
                if (edge.getStart().equals(lastNode)) {
                    flow.put(edge, flow.get(edge) + minCapacity);
                    lastNode = edge.getTarget();
                } else {
                    flow.put(edge, flow.get(edge) - minCapacity);
                    lastNode = edge.getStart();
                }
            }
        }



        return flow;
    }



    public ArrayList<Integer> getRisidualGraph(Object source,
                                             Object sink) {
        // The path from source to sink that is found in each iteration
        LinkedList<Edge> path;
        // The flow, i.e. the capacity of each edge that is actually used
        HashMap<Edge, Integer> flow = new HashMap<Edge, Integer>();
        // Create initial empty flow.
        for (Edge e : g.getEdges()) {
            flow.put(e, 0);
        }

        // The Algorithm itself
        while ((path = bfs(source, sink, flow)) != null) {
            // Activating this output will illustrate how the algorithm works
            // System.out.println(path);
            // Find out the flow that can be sent on the found path.
            int minCapacity = Integer.MAX_VALUE;
            Object lastNode = source;
            for (Edge edge : path) {
                int c;
                // Although the edges are directed they can be used in both
                // directions if the capacity is partially used, so this if
                // statement is necessary to find out the edge's actual
                // direction.
                if (edge.getStart().equals(lastNode)) {
                    c = edge.getCapacity() - flow.get(edge);
                    lastNode = edge.getTarget();
                } else {
                    c = flow.get(edge);
                    lastNode = edge.getStart();
                }
                if (c < minCapacity) {
                    minCapacity = c;
                }
            }

            // Change flow of all edges of the path by the value calculated
            // above.
            lastNode = source;
            for (Edge edge : path) {
                // If statement like above
                if (edge.getStart().equals(lastNode)) {
                    flow.put(edge, flow.get(edge) + minCapacity);
                    lastNode = edge.getTarget();
                } else {
                    flow.put(edge, flow.get(edge) - minCapacity);
                    lastNode = edge.getStart();
                }
            }
        }

        HashMap<Integer, ArrayList<Edge>> RG = new HashMap<>();
        for(Map.Entry<Edge,Integer> e:flow.entrySet()){
            if(e.getKey().getCapacity()==e.getValue() || e.getValue()==0){

            }else{
                ArrayList<Edge> tmp;
                ArrayList<Edge> tmp_2;
                if(RG.containsKey(e.getKey().getTarget())){
                    tmp = RG.get(e.getKey().getTarget());
                    tmp.add(new Edge(e.getKey().getTarget(),e.getKey().getStart(),e.getValue()));
                    RG.put((Integer)e.getKey().getTarget(),tmp);
                }else{
                    tmp = new ArrayList<>();
                    tmp.add(new Edge(e.getKey().getTarget(),e.getKey().getStart(),e.getValue()));
                    RG.put((Integer)e.getKey().getTarget(),tmp);
                }
                if(RG.containsKey(e.getKey().getStart())){
                    tmp_2 = RG.get(e.getKey().getStart());
                    tmp_2.add(e.getKey());
                    RG.put((Integer)e.getKey().getStart(),tmp_2);
                }else{
                    tmp_2 = new ArrayList<>();
                    tmp_2.add(e.getKey());
                    RG.put((Integer)e.getKey().getStart(),tmp_2);
                }
            }

        }
        return visitedNodes(RG);
    }

    private ArrayList<Integer> visitedNodes(HashMap<Integer, ArrayList<Edge>> rg) {
        ArrayList<Integer> visited = new ArrayList<>();
        int current = 0;
        visited.add(current);
        ArrayList<Edge> unvisited = rg.get(current);
        while(!unvisited.isEmpty()){
           Edge current_edge = unvisited.remove(0);
           if(!visited.contains(current_edge.getTarget())){
               visited.add((Integer)current_edge.getTarget());
                unvisited.addAll(rg.get(current_edge.getTarget()));
           }
        }
        return visited;
    }


    /**
     * This method gives the actual flow value by adding all flow values of the
     * out leading edges of the source.
     *
     * @param flow   A HashMap of the form like getMaxFlow produces them
     * @param source The object identifying the source node of the flow
     * @return The value of the given flow
     */
    public int getFlowSize(HashMap<Edge, Integer> flow,
                           Object source) {
        int maximumFlow = 0;
        Node sourceNode = g.getNode(source);
        for (int i = 0; i < sourceNode.getOutLeadingOrder(); i++) {
            maximumFlow += flow.get(sourceNode.getEdge(i));
        }
        return maximumFlow;
    }

    /**
     * Simple breadth first search in the directed graph
     *
     * @param start  The object that identifying the start node of the search
     * @param target The object that identifying the target node of the search
     * @param flow   A HashMap of the form like getMaxFlow produces them. If an
     *               edge has a value > 0 in it, it will also be used in the
     *               opposite direction. Also edges that have a value equal to its
     *               capacity will be ignored.
     * @return A list of all edges of the found path in the order in which they
     * are used, null if there is no path. If the start node equals the
     * target node, an empty list is returned.
     */
    public LinkedList<Edge> bfs(Object start, Object target,
                                HashMap<Edge, Integer> flow) {
        // The edge by which a node was reached.
        HashMap<Object, Edge> parent = new HashMap<Object, Edge>();
        // All outer nodes of the current search iteration.
        LinkedList<Object> fringe = new LinkedList<Object>();
        // We need to put the start node into those two.
        parent.put(start, null);
        fringe.add(start);
        // The actual algorithm
        all:
        while (!fringe.isEmpty()) {
            // This variable is needed to prevent the JVM from having a
            // concurrent modification
            LinkedList<Object> newFringe = new LinkedList<Object>();
            // Iterate through all nodes in the fringe.
            for (Object nodeID : fringe) {
                Node node = g.getNode(nodeID);
                // Iterate through all the edges of the node.
                for (int i = 0; i < node.getOutLeadingOrder(); i++) {
                    Edge e = node.getEdge(i);
                    // Only add the node if the flow can be changed in an out
                    // leading direction. Also break, if the target is reached.
                    if (e.getStart().equals(nodeID)
                            && !parent.containsKey(e.getTarget())
                            && flow.get(e) < e.getCapacity()) {
                        parent.put(e.getTarget(), e);
                        if (e.getTarget().equals(target)) {
                            break all;
                        }
                        newFringe.add(e.getTarget());
                    } else if (e.getTarget().equals(nodeID)
                            && !parent.containsKey(e.getStart())
                            && flow.get(e) > 0) {
                        parent.put(e.getStart(), e);
                        if (e.getStart().equals(target)) {
                            break all;
                        }
                        newFringe.add(e.getStart());
                    }
                }
            }
            // Replace the fringe by the new one.
            fringe = newFringe;
        }

        // Return null, if no path was found.
        if (fringe.isEmpty()) {
            return null;
        }
        // If a path was found, reconstruct it.
        Object node = target;
        LinkedList<Edge> path = new LinkedList<Edge>();
        while (!node.equals(start)) {
            Edge e = parent.get(node);
            path.addFirst(e);
            if (e.getStart().equals(node)) {
                node = e.getTarget();
            } else {
                node = e.getStart();
            }
        }

        // Return the path.
        return path;
    }

    public void printFlow(HashMap<Edge, Integer> flow, int i) {
        for (Map.Entry<Edge, Integer> e : flow.entrySet()) {
            System.out.println(e.getKey().toString()+" - " + e.getValue());
        }
    }
}