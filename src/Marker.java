import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        int k = 0;
        for (int i = 1; i < m; i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) {
                k = pi[k - 1];
            }
            if (p.charAt(i) == p.charAt(k)) {
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        if (p == null || p.isEmpty() || text == null || text.length() < p.length()) {
            return new int[0];
        }
        int[] pi = prefixFunction(p);
        List<Integer> matches = new ArrayList<>();
        int k = 0;
        int m = p.length();
        int n = text.length();
        for (int i = 0; i < n; i++) {
            while (k > 0 && (k == m || text.charAt(i) != p.charAt(k))) {
                k = pi[k - 1];
            }
            if (text.charAt(i) == p.charAt(k)) {
                k++;
            }
            if (k == m) {
                matches.add(i - m + 1);
            }
        }
        int[] res = new int[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            res[i] = matches.get(i);
        }
        return res;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (a.length() < k || b.length() < k) {
            return 0;
        }
        long BASE = 131L;
        long MOD = 1_000_000_007L;

        long top = 1L;
        for (int i = 1; i < k; i++) {
            top = (top * BASE) % MOD;
        }

        Map<Long, List<Integer>> bMap = new HashMap<>();
        long hb = 0;
        for (int i = 0; i < k; i++) {
            hb = (hb * BASE + b.charAt(i)) % MOD;
        }
        bMap.computeIfAbsent(hb, key -> new ArrayList<>()).add(0);

        for (int j = 1; j + k <= b.length(); j++) {
            hb = (hb - (b.charAt(j - 1) * top) % MOD + MOD) % MOD;
            hb = (hb * BASE + b.charAt(j + k - 1)) % MOD;
            bMap.computeIfAbsent(hb, key -> new ArrayList<>()).add(j);
        }

        int count = 0;
        long ha = 0;
        for (int i = 0; i < k; i++) {
            ha = (ha * BASE + a.charAt(i)) % MOD;
        }
        if (bMap.containsKey(ha) && matchesAny(a, 0, b, bMap.get(ha), k)) {
            count++;
        }

        for (int i = 1; i + k <= a.length(); i++) {
            ha = (ha - (a.charAt(i - 1) * top) % MOD + MOD) % MOD;
            ha = (ha * BASE + a.charAt(i + k - 1)) % MOD;
            if (bMap.containsKey(ha) && matchesAny(a, i, b, bMap.get(ha), k)) {
                count++;
            }
        }

        return count;
    }

    private static boolean matchesAny(String a, int aStart, String b, List<Integer> bStarts, int k) {
        for (int bStart : bStarts) {
            boolean eq = true;
            for (int t = 0; t < k; t++) {
                if (a.charAt(aStart + t) != b.charAt(bStart + t)) {
                    eq = false;
                    break;
                }
            }
            if (eq) return true;
        }
        return false;
    }
}
