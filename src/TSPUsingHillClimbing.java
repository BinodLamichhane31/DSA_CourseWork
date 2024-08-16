import java.util.Arrays;
import java.util.Random;

public class TSPUsingHillClimbing {

    private static final Random RANDOM = new Random(42);

    public static void main(String[] args) {
        int numCities = 10;
        double[][] cities = generateCities(numCities);
        int[] bestRoute = hillClimbing(cities);
        double bestDistance = calculateDistance(cities, bestRoute);

        System.out.println("Best Route: " + Arrays.toString(bestRoute));
        System.out.println("Best Distance: " + bestDistance);
    }

    // Generate random city coordinates
    private static double[][] generateCities(int numCities) {
        double[][] cities = new double[numCities][2];
        for (int i = 0; i < numCities; i++) {
            cities[i][0] = RANDOM.nextDouble();
            cities[i][1] = RANDOM.nextDouble();
        }
        return cities;
    }

    // Calculate the total distance of a route
    private static double calculateDistance(double[][] cities, int[] route) {
        double totalDistance = 0.0;
        for (int i = 0; i < route.length; i++) {
            int from = route[i];
            int to = route[(i + 1) % route.length];
            totalDistance += Math.hypot(cities[from][0] - cities[to][0], cities[from][1] - cities[to][1]);
        }
        return totalDistance;
    }
    private static int[] createInitialRoute(int numCities) {
        int[] route = new int[numCities];
        for (int i = 0; i < numCities; i++) {
            route[i] = i;
        }
        for (int i = 0; i < numCities; i++) {
            int j = RANDOM.nextInt(numCities);
            int temp = route[i];
            route[i] = route[j];
            route[j] = temp;
        }
        return route;
    }

    // Hill Climbing algorithm
    private static int[] hillClimbing(double[][] cities) {
        int numCities = cities.length;
        int[] currentRoute = createInitialRoute(numCities);
        double currentDistance = calculateDistance(cities, currentRoute);

        boolean improved;
        do {
            improved = false;
            for (int i = 0; i < numCities - 1; i++) {
                for (int j = i + 1; j < numCities; j++) {
                    int[] neighbor = currentRoute.clone();
                    int temp = neighbor[i];
                    neighbor[i] = neighbor[j];
                    neighbor[j] = temp;

                    double neighborDistance = calculateDistance(cities, neighbor);
                    if (neighborDistance < currentDistance) {
                        currentRoute = neighbor;
                        currentDistance = neighborDistance;
                        improved = true;
                    }
                }
            }
        } while (improved);

        return currentRoute;
    }
}
