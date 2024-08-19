import java.util.*;

class Edge {
    int u, v, weight;
    Edge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }
}

public class RoadNetwork {

    // Function to run Bellman-Ford algorithm
    public static int[] bellmanFord(int n, List<Edge> edges, int source) {
        int[] distances = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;

        for (int i = 0; i < n - 1; i++) {
            for (Edge edge : edges) {
                if (distances[edge.u] != Integer.MAX_VALUE &&
                        distances[edge.u] + edge.weight < distances[edge.v]) {
                    distances[edge.v] = distances[edge.u] + edge.weight;
                }
            }
        }
        return distances;
    }

    // Function to modify roads under construction
    public static List<Edge> modifyRoads(int n, List<Edge> roads, int source, int destination, int target) {
        List<Edge> edges = new ArrayList<>();
        List<Edge> constructionEdges = new ArrayList<>();

        for (Edge road : roads) {
            if (road.weight == -1) {
                constructionEdges.add(new Edge(road.u, road.v, 1)); // Start with weight 1
                edges.add(new Edge(road.u, road.v, 1)); // Add to edge list with weight 1
            } else {
                edges.add(road);
            }
        }

        // Initial Bellman-Ford to check current shortest path
        int[] distances = bellmanFord(n, edges, source);
        if (distances[destination] == target) {
            return roads;
        }

        // Try modifying construction edges to achieve the target time
        for (Edge edge : constructionEdges) {
            edges.removeIf(e -> e.u == edge.u && e.v == edge.v && e.weight == 1); // Remove old edge
            edges.add(new Edge(edge.u, edge.v, target)); // Attempt to use the target directly

            distances = bellmanFord(n, edges, source);

            if (distances[destination] == target) {
                // Update the original roads list with new weights
                for (Edge road : roads) {
                    if (road.weight == -1) {
                        road.weight = target;
                    }
                }
                return roads;
            }

            // Remove the temporary edge if it doesn't work
            edges.removeIf(e -> e.u == edge.u && e.v == edge.v && e.weight == target);
            edges.add(new Edge(edge.u, edge.v, 1)); // Re-add the original edge
        }

        return Collections.emptyList();
    }

    public static void main(String[] args) {
        int n = 5;
        List<Edge> roads = Arrays.asList(
                new Edge(4, 1, -1),
                new Edge(2, 0, -1),
                new Edge(0, 3, -1),
                new Edge(4, 3, -1)
        );
        int source = 0;
        int destination = 1;
        int target = 5;

        List<Edge> result = modifyRoads(n, roads, source, destination, target);

        if (!result.isEmpty()) {
            for (Edge edge : result) {
                System.out.println("[" + edge.u + ", " + edge.v + ", " + edge.weight + "]");
            }
        } else {
            System.out.println("No valid modification found.");
        }
    }
}
