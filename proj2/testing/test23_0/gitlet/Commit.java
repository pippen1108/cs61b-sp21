package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author pippen
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    private final Date timestamp;
    private final String parentString;
    private TreeMap<String, String> blobmap = new TreeMap<>();
    /** for the initial commit*/
    public Commit() {
        message = "initial commit";
        timestamp = new Date(0);
        parentString = null;
    }

    public Commit(String message, String prentid) {
        this.message = message;
        timestamp = new Date();
        parentString = prentid;
        blobmap = readCommit(prentid).blobmap;
    }

    public String getMessage() {
        return message;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public String getParentString() {
        return parentString;
    }

    // write commit to file
    public void saveCommit(String commitHash) throws IOException {
        String commitDirString = commitHash.substring(0, 2);
        File commitDir = join(Repository.COMMITS_DIR,  commitDirString);
        commitDir.mkdir();
        File commitFile = join(commitDir, commitHash);
        commitFile.createNewFile();
        writeObject(commitFile, this);
    }

    public static Commit currentCommit() {
        String currentBranch = readContentsAsString(Repository.HEADS_F);
        String commitHash = readContentsAsString(join(Repository.HEADS_DIR, currentBranch));
        return readCommit(commitHash);
    }

    public static Commit findCommitWithPrefix(String prefix) {
        String commitDirString = prefix.substring(0, 2);
        if (!join(Repository.COMMITS_DIR, commitDirString).exists()) {
            return null;
        } else {
            List<String> commits =  plainFilenamesIn(join(Repository.COMMITS_DIR, commitDirString));
            assert commits != null;
            for (String commit : commits) {
                if (commit.startsWith(prefix)) {
                    return readCommit(commit);
                }
            }
        }
        return null;
    }

    public static Commit readCommit(String commitHash) {
        String commitDirString = commitHash.substring(0, 2);
        File commitDir = join(Repository.COMMITS_DIR,  commitDirString);
        File commitFile = join(commitDir, commitHash);
        return readObject(commitFile, Commit.class);
    }

    public TreeMap<String, String> getBlobmap() {
        return blobmap;
    }
}
