import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        if (n == 0) return new boolean[0];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        parent[0] = 0;

        int[] order = new int[n];
        int head = 0, tail = 0;
        order[tail++] = 0;

        while (head < tail) {
            int v = order[head++];
            for (int u : nb[v]) {
                if (parent[u] == -1) {
                    parent[u] = v;
                    order[tail++] = u;
                }
            }
        }

        long[] take = new long[n];
        long[] skip = new long[n];
        for (int i = 0; i < n; i++) {
            take[i] = cost[i];
        }

        for (int h = n - 1; h >= 0; h--) {
            int v = order[h];
            if (v != 0) {
                int p = parent[v];
                take[p] += Math.min(take[v], skip[v]);
                skip[p] += take[v];
            }
        }

        boolean[] tested = new boolean[n];
        tested[0] = take[0] <= skip[0];
        for (int h = 1; h < n; h++) {
            int v = order[h];
            int p = parent[v];
            tested[v] = !tested[p] || (take[v] <= skip[v]);
        }
        return tested;
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n <= 1) return 0;
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        parent[0] = 0;

        int[] order = new int[n];
        int head = 0, tail = 0;
        order[tail++] = 0;

        while (head < tail) {
            int v = order[head++];
            for (int u : nb[v]) {
                if (parent[u] == -1) {
                    parent[u] = v;
                    order[tail++] = u;
                }
            }
        }

        int[] top1 = new int[n];
        int[] top2 = new int[n];
        int chain = 0;

        for (int h = n - 1; h >= 0; h--) {
            int v = order[h];
            chain = Math.max(chain, top1[v] + top2[v]);
            if (v != 0) {
                int p = parent[v];
                int hh = top1[v] + 1;
                if (hh > top1[p]) {
                    top2[p] = top1[p];
                    top1[p] = hh;
                } else if (hh > top2[p]) {
                    top2[p] = hh;
                }
            }
        }
        return chain;
    }
}
