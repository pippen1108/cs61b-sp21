package gitlet;


import java.io.File;
import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

public class CommitGraph {

    /**
     * 從 commit ID 載入 Commit 物件
     */
    public static Commit read(String commitId) {
        String commitDirString = commitId.substring(0, 2);
        File commitDir = join(Repository.COMMITS_DIR,  commitDirString);
        File commitFile = join(commitDir, commitId);
        return readObject(commitFile, Commit.class);
    }

    /**
     * 取得所有祖先 commit 的 ID（含自身）
     */
    public static List<String> getAllAncestors(String commitId) {
        List<String> ancestors = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(commitId);
        visited.add(commitId);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            ancestors.add(curr);
            Commit commit = read(curr);
            for (String parent : commit.getAllParents()) {
                if (!visited.contains(parent)) {
                    visited.add(parent);
                    queue.add(parent);
                }
            }
        }

        return ancestors;
    }

    /**
     * 找出最近共同祖先（Lowest Common Ancestor, LCA）
     */
    public static String getLCA(String id1, String id2) {
        Set<String> ancestors1 = new HashSet<>(getAllAncestors(id1));
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(id2);
        visited.add(id2);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            if (ancestors1.contains(curr)) {
                return curr;
            }

            Commit commit = read(curr);
            for (String parent : commit.getAllParents()) {
                if (!visited.contains(parent)) {
                    visited.add(parent);
                    queue.add(parent);
                }
            }
        }

        return null; // 應該不會發生
    }
}
