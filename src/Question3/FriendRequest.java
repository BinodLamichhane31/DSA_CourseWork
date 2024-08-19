package Question3;

import java.util.*;

public class FriendRequest {
    static class UnionFind {
        int[] parent;
        int[] rank;

        public UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }

    public static List<String> processRequests(int n, int[][] restrictions, int[][] requests) {
        UnionFind uf = new UnionFind(n);

        boolean[][] restricted = new boolean[n][n];
        for (int[] restriction : restrictions) {
            restricted[restriction[0]][restriction[1]] = true;
            restricted[restriction[1]][restriction[0]] = true;
        }

        List<String> result = new ArrayList<>();

        for (int[] request : requests) {
            int x = request[0];
            int y = request[1];

            int rootX = uf.find(x);
            int rootY = uf.find(y);

            boolean conflict = false;
            if (rootX != rootY) {
                for (int i = 0; i < n; i++) {
                    if (uf.find(i) == rootX) {
                        for (int j = 0; j < n; j++) {
                            if (uf.find(j) == rootY && restricted[i][j]) {
                                conflict = true;
                                break;
                            }
                        }
                    }
                    if (conflict) break;
                }
            }

            if (conflict) {
                result.add("denied");
            } else {
                uf.union(x, y);
                result.add("approved");
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int n = 5;
        int[][] restrictions = {{0, 1}, {1, 2}, {2, 3}};
        int[][] requests = {{0, 4}, {1, 2}, {3, 1}, {3, 4}};
        System.out.println(processRequests(n, restrictions, requests));
    }
}
