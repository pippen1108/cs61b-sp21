package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    static final File GITLET_DIR = join(CWD, ".gitlet");
    static final File REF_DIR = join(GITLET_DIR, "ref");
    static final File HEADS_DIR = join(REF_DIR, "heads");
    static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    static final File BOLB_DIR = join(OBJECTS_DIR, "blobs");
    static final File HEADS_F = join(REF_DIR, "HEAD");
    static final File STAGE_DIR = join(GITLET_DIR, "stage");
    static final File ADDITION_F = join(STAGE_DIR, "addition");
    static final File REMOVAL_F = join(STAGE_DIR, "removal");
    /** a lot of commits
     */
    /* TODO: fill in the rest of this class. */



    public static void initialCommit () throws IOException {
        /**
         * TODO: crate other needed file system
         * TODO: create initial commit
         * TODO: HEAD pointer
         * TODO: master branch
         */
        if (GITLET_DIR.exists()){
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        REF_DIR.mkdir();
        HEADS_DIR.mkdir();
        HEADS_F.createNewFile();
        OBJECTS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BOLB_DIR.mkdir();
        STAGE_DIR.mkdir();
        ADDITION_F.createNewFile();
        REMOVAL_F.createNewFile();
        createStage();
        Commit init = new Commit();
        String commit_name = sha1(serialize(init));
        init.saveCommit(commit_name);
        createBranch("master", commit_name);
        setHEADpointer("master");
    }

    public static void add(String file_name) throws IOException {
        File file_file = join(CWD, file_name);
        if (!file_file.exists()){
            throw new GitletException("File does not exist. ");
        }
        // file content hash in the CWD
        String content_hash = sha1(serialize(readContentsAsString(file_file)));

        //current commit
        Commit current_commit = Commit.CurrentCommit();
        TreeMap<String, String> stage_addition = readSageAddition();
        TreeSet<String> stage_removal = readSageRemoval();
        if (current_commit.blobmap.containsKey(file_name) && content_hash.equals(current_commit.blobmap.get(file_name))){
            stage_addition.remove(file_name);
        } else {
            stage_addition.put(file_name, content_hash);
            //create blob
            Blob add = new Blob(file_name);
            add.saveBlob();
        }
        stage_removal.remove(file_name);


        //update the staging area file
        writeStageAddition(stage_addition);
        writeStageRemoval(stage_removal);

    }


    public static void rm(String fileName) {
        TreeMap<String, String> stage_addition = readSageAddition();
        TreeSet<String> stage_removal = readSageRemoval();

        if (stage_addition.remove(fileName) != null){
            //update the staging area file
            writeStageAddition(stage_addition);
        } else {
            Commit current_commit = Commit.CurrentCommit();
            if (current_commit.blobmap.containsKey(fileName)) {
                stage_removal.add(fileName);
                restrictedDelete(fileName);

                writeStageRemoval(stage_removal);
            } else {
                throw new GitletException("No reason to remove the file.");
            }
        }



    }

    public static void commit(String message) throws IOException {
        if (message.isEmpty()) {
            throw new GitletException("Please enter a commit message.");
        }

        TreeMap<String, String> stage_addition = readSageAddition();
        TreeSet<String> stage_removal = readSageRemoval();
        if (stage_addition.isEmpty() && stage_removal.isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

        Commit current = Commit.CurrentCommit();

        Commit new_commit = new Commit(message, sha1(serialize(current)));

        for (String key : stage_addition.keySet()){
            new_commit.blobmap.put(key, stage_addition.get(key));
            stage_addition.remove(key);
        }
        for (String key : stage_removal){
            new_commit.blobmap.remove(key);
            stage_removal.remove(key);
        }


        //update the staging area file
        writeStageAddition(stage_addition);
        writeStageRemoval(stage_removal);

        //store the commit file
        String commitName = sha1(serialize(new_commit));
        new_commit.saveCommit(commitName);

        setBranch(getCurrentBranch(), commitName);
    }


    public static void log(){
        StringBuilder log = new StringBuilder();
        Commit last = Commit.CurrentCommit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);

        while (last != null){
            log.append("===\n");
            log.append(String.format("commit %s\n", sha1(serialize(last))));
            String formatDate = dateFormat.format(last.getTimestamp());
            log.append(String.format("Date: %s\n", formatDate));
            log.append(last.getMessage());
            log.append("\n\n");

            if (last.getParent_string() == null) {
                break;
            }

            last = Commit.readCommit(last.getParent_string());
        }
        System.out.println(log);
    }


    public static void globalLog(){
        StringBuilder log = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        List<String> allCommits = plainFilenamesIn(COMMITS_DIR);
        assert allCommits != null;
        for (String commit : allCommits){
            Commit commitObject = Commit.readCommit(commit);
            log.append("===\n");
            log.append(String.format("commit %s\n", commit));
            String formatDate = dateFormat.format(commitObject.getTimestamp());
            log.append(String.format("Date: %s\n", formatDate));
            log.append(commitObject.getMessage());
            log.append("\n\n");
        }
        System.out.println(log);
    }

    public static void checkout(String fileName) {
        Commit current = Commit.CurrentCommit();
        if (!current.blobmap.containsKey(fileName)){
            throw new GitletException("File does not exist in that commit.");
        } else {
            String contentString = readObject(join(BOLB_DIR, current.blobmap.get(fileName)), String.class);
            writeContents(join(CWD, fileName), contentString);
        }
    }

    public static void checkout(String fileName, String commitHash) {
        Commit current = Commit.readCommit(commitHash);
        if (!current.blobmap.containsKey(fileName)){
            throw new GitletException("File does not exist in that commit.");
        } else {
            String contentString = readObject(join(BOLB_DIR, current.blobmap.get(fileName)), String.class);
            writeContents(join(CWD, fileName), contentString);
        }
    }

    public static void checkoutBranch(String branchName) {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        assert allBranches != null;
        if (!allBranches.contains(branchName)) {
            throw new GitletException("No such branch exists.");
        }
        if (getCurrentBranch().equals(branchName)){
            throw new GitletException("No need to checkout the current branch.");
        }
        Commit current = Commit.CurrentCommit();
        String commitHash = readContentsAsString(join(HEADS_DIR, branchName));
        Commit targetBranch = Commit.readCommit(commitHash);
        List<String> CWDFiles = plainFilenamesIn(CWD);

        //if (plainFilenamesIn(CWD))
        // If a working file is untracked in the current branch and would be
        // overwritten by the checkout, print There is an untracked file in
        // the way; delete it, or add and commit it first.



        setHEADpointer(branchName);
    }

    public static void branch (String branchName) throws IOException {
        Commit current = Commit.CurrentCommit();
        String commitHash = sha1(serialize(current));
        createBranch(branchName, commitHash);
    }

    public static void rmBranch (String branchName) {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        assert allBranches != null;
        if (!allBranches.contains(branchName)) {
            throw new GitletException("branch with that name does not exist.");
        }
        if (getCurrentBranch().equals(branchName)){
            throw new GitletException("Cannot remove the current branch.");
        }
        join(HEADS_DIR, branchName).delete();

    }



    public static TreeMap readSageAddition(){
        return readObject(ADDITION_F, TreeMap.class);
    }

    public static TreeSet readSageRemoval(){
        return readObject(REMOVAL_F, TreeSet.class);
    }


    public static void  writeStageAddition (TreeMap<String, String> stage) {
        writeObject(ADDITION_F, stage);
    }

    public static void writeStageRemoval(TreeSet<String> stage) {
        writeObject(REMOVAL_F, stage);
    }

    public static void createStage() {
        TreeMap<String, String> stage_addition = new TreeMap<>();
        writeObject(ADDITION_F, stage_addition);
        TreeSet<String> stage_removal = new TreeSet<>();
        writeObject(REMOVAL_F, stage_removal);
    }


    public static void setHEADpointer(String branch) {
        writeContents(HEADS_F, branch);
    }

    public static void createBranch(String name, String commitHash) throws IOException {
        File branch_file = join(HEADS_DIR, name);
        branch_file.createNewFile();
        writeContents(branch_file, commitHash);
    }

    private static String getCurrentBranch(){
        return readContentsAsString(Repository.HEADS_F);
    }

    public static void setBranch(String branch, String commitHash){
        writeContents(join(HEADS_DIR, branch), commitHash);
    }



}
