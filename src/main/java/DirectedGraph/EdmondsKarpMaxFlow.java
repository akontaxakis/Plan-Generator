package DirectedGraph;
import java.util.*;

public class EdmondsKarpMaxFlow {

    private static int[][] graph; // the capacity graph
    private static int[] parent; // array to store the parent of each node
    private static int[] flow; // array to store the flow along each path

    public static int maxFlow(int[][] capacityGraph, int source, int sink) {
        int n = capacityGraph.length;
        graph = new int[n][n];
        parent = new int[n];
        flow = new int[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                graph[i][j] = capacityGraph[i][j];
            }
        }

        int maxFlow = 0;

        while (bfs(source, sink)) {
            int pathFlow = Integer.MAX_VALUE;
            int node = sink;

            while (node != source) {
                int prevNode = parent[node];
                pathFlow = Math.min(pathFlow, graph[prevNode][node]);
                node = prevNode;
            }

            node = sink;

            while (node != source) {
                int prevNode = parent[node];
                flow[node] += pathFlow;
                graph[prevNode][node] -= pathFlow;
                graph[node][prevNode] += pathFlow;
                node = prevNode;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private static boolean bfs(int source, int sink) {
        Arrays.fill(parent, -1);
        Arrays.fill(flow, 0);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        flow[source] = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            int node = queue.poll();

            for (int neighbor = 0; neighbor < graph.length; neighbor++) {
                if (parent[neighbor] == -1 && graph[node][neighbor] > 0) {
                    parent[neighbor] = node;
                    flow[neighbor] = Math.min(flow[node], graph[node][neighbor]);

                    if (neighbor == sink) {
                        return true;
                    }

                    queue.add(neighbor);
                }
            }
        }

        return false;
    }
}