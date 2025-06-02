package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Repository2.BOLB_DIR;
import static gitlet.Repository2.rm;
import static gitlet.Utils.readContentsAsString;

public class MergeEngine {
    private final Commit current;
    private final Commit given;
    private final Commit split;
    private final Map<String, String> currentMap;
    private final Map<String, String> givenMap;
    private final Map<String, String> splitMap;

    private final Map<String, String> mergedFiles = new HashMap<>();
    private boolean hasConflict = false;

    public MergeEngine(Commit current, Commit given) {
        this.current = current;
        this.given = given;
        String splitId = CommitGraph.getLCA(current.getId(), given.getId());
        assert splitId != null;
        this.split = Commit.readCommit(splitId);
        this.currentMap = current.getBlobmap();
        this.givenMap = given.getBlobmap();
        this.splitMap = (split == null) ? new HashMap<>() : split.getBlobmap();
    }

    public void runMerge() {
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(currentMap.keySet());
        allFiles.addAll(givenMap.keySet());
        allFiles.addAll(splitMap.keySet());

        for (String file : allFiles) {
            String splitBlob = splitMap.get(file);
            String currentBlob = currentMap.get(file);
            String givenBlob = givenMap.get(file);

            // case 0: same on both sides or same changes
            if (Objects.equals(currentBlob, givenBlob)) {
                if (currentBlob != null) {
                    mergedFiles.put(file, readBlob(currentBlob));
                }
                continue;
            }

            // case 1: split == current => take given (added or modified)
            if (Objects.equals(splitBlob, currentBlob)) {
                if (givenBlob == null) {
                    rm(file);
                    continue;
                }
                mergedFiles.put(file, readBlob(givenBlob));
                continue;
            }

            // case 2: split == given => take current
            if (Objects.equals(splitBlob, givenBlob)) {
                if (currentBlob == null) {
                    continue;
                }
                mergedFiles.put(file, readBlob(currentBlob));
                continue;
            }

            // case 3: file added in both branches with different content
            if (splitBlob == null && currentBlob != null
                    && givenBlob != null && !Objects.equals(currentBlob, givenBlob)) {
                String conflictContent = "<<<<<<< HEAD\n" + readBlob(currentBlob)
                        + "=======\n" + readBlob(givenBlob) + ">>>>>>>\n";
                mergedFiles.put(file, conflictContent);
                hasConflict = true;
                continue;
            }

            // case 4: conflict – all others
            String currentContent = currentBlob == null ? "" : readBlob(currentBlob);
            String givenContent = givenBlob == null ? "" : readBlob(givenBlob);
            String conflictContent = "<<<<<<< HEAD\n" + currentContent
                    + "=======\n" + givenContent + ">>>>>>>\n";
            mergedFiles.put(file, conflictContent);
            hasConflict = true;
        }
    }


    public Map<String, String> getMergedFiles() {
        return mergedFiles;
    }

    public boolean hasConflict() {
        return hasConflict;
    }

    public Commit getSplitCommit() {
        return split;
    }

    private String readBlob(String blobId) {
        if (blobId == null) {
            return "";
        }
        File blob = new File(BOLB_DIR, blobId);
        return readContentsAsString(blob);
    }
}
