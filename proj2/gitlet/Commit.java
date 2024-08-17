package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; 
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
    public void saveCommit(String name) throws IOException {
        File commitFile = join(Repository.COMMITS_DIR, name);
        commitFile.createNewFile();
        writeObject(commitFile, this);
    }

    public static Commit currentCommit() {
        String currentBranch = readContentsAsString(Repository.HEADS_F);
        String commitHash = readContentsAsString(join(Repository.HEADS_DIR, currentBranch));
        return readCommit(commitHash);
    }


    public static Commit readCommit(String commithash) {
        File commitFile = join(Repository.COMMITS_DIR, commithash);
        return readObject(commitFile, Commit.class);
    }

    public TreeMap<String, String> getBlobmap() {
        return blobmap;
    }
}
