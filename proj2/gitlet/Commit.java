package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private String parent_string;;
    public TreeMap<String, String> blobmap = new TreeMap<>();
    /** for the initial commit*/
    public Commit () {
        message = "initial commit";
        timestamp = new Date(0);
        parent_string = null;
    }

    public Commit (String message, String prentid) {
        this.message = message;
        timestamp = new Date();
        parent_string = prentid;
        blobmap = readCommit(prentid).blobmap;
    }

    public String getMessage() {
        return message;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public String getParent_string() {
        return parent_string;
    }

    // write commit to file
    public void saveCommit(String name) throws IOException {
        File commit_file = join(Repository.COMMITS_DIR, name);
        commit_file.createNewFile();
        writeObject(commit_file, this);
    }

    public static Commit CurrentCommit(){
        String current_branch = readContentsAsString(Repository.HEADS_F);
        String commit_hash = readContentsAsString(join(Repository.HEADS_DIR, current_branch));
        return readCommit(commit_hash);
    }


    public static Commit readCommit(String commithash){
        File commit_file = join(Repository.COMMITS_DIR, commithash);
        return readObject(commit_file, Commit.class);
    }

    /* TODO: fill in the rest of this class. */
}
