import java.util.Scanner;

public class ChallengingHikingTrail {

    public static int longestContinuousHike(int[] trial, int k) {
        int n = trial.length;
        int[] dp = new int[n];
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
        }
        int max = 1;
        for (int i = 1; i < n; i++) {
            if(trial[i] > trial[i-1]  && (trial[i] - trial[i-1]) <= k) {
                dp[i] = dp[i-1] + 1;
            }
            max = Math.max(max, dp[i]);
        }
        return max;

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the length of the trail: ");
        int n = scanner.nextInt();
        int[] trail = new int[n];
        System.out.println("Enter the trail elements: ");
        for (int i = 0; i < n; i++) {
            trail[i] = scanner.nextInt();
        }
        System.out.print("Enter the elevation gain limit (k): ");
        int k = scanner.nextInt();
        System.out.println("Longest continuous hike length: " + longestContinuousHike(trail, k));


    }
}
