package Question4;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}

public class LargestMagicalGrove {


    // Global variable to keep track of the maximum sum
    static int maxSum = 0;

    // Helper function to find the largest magical grove
    public static int[] helper(TreeNode node) {
        // Base case: If the node is null, it is a valid BST with sum 0
        if (node == null) {
            return new int[]{1, Integer.MAX_VALUE, Integer.MIN_VALUE, 0}; // {isBST, min, max, sum}
        }

        // Recur for left and right subtrees
        int[] left = helper(node.left);
        int[] right = helper(node.right);

        // Current node must be greater than max in left subtree and less than min in right subtree
        if (left[0] == 1 && right[0] == 1 && node.val > left[2] && node.val < right[1]) {
            int sum = left[3] + right[3] + node.val;
            maxSum = Math.max(maxSum, sum);
            return new int[]{1, Math.min(left[1], node.val), Math.max(right[2], node.val), sum};
        }

        return new int[]{0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0}; // Not a BST
    }

    // Main function to find the largest magical grove
    public static int findLargestMagicalGrove(TreeNode root) {
        maxSum = 0;
        helper(root);
        return maxSum;
    }

    // Function to create a binary tree from an array representation
    public static TreeNode createTree(Integer[] nodes) {
        if (nodes.length == 0 || nodes[0] == null) return null;
        TreeNode root = new TreeNode(nodes[0]);
        TreeNode[] treeNodes = new TreeNode[nodes.length];
        treeNodes[0] = root;

        for (int i = 1; i < nodes.length; i++) {
            if (nodes[i] != null) {
                TreeNode newNode = new TreeNode(nodes[i]);
                treeNodes[i] = newNode;
                if (i % 2 == 1) {
                    treeNodes[(i - 1) / 2].left = newNode;
                } else {
                    treeNodes[(i - 2) / 2].right = newNode;
                }
            }
        }
        return root;
    }

    // Main method to test the functionality
    public static void main(String[] args) {
        Integer[] forest = {1, 4, 3, 2, 4, 2, 5, null, null, null, null, null, null, 4, 6};
        TreeNode root = createTree(forest);
        int largestMagicalGrove = findLargestMagicalGrove(root);
        System.out.println("The largest magical grove has a total coin value of: " + largestMagicalGrove);  // Output: 20
    }
}
