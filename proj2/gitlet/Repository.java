package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author pippen
 */
public class Repository {
    /** spec
     *
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



    
    
    
    public static void initialCommit() throws IOException {
        if (GITLET_DIR.exists()) {
            throw new GitletException(
                    "A Gitlet version-control system already exists in the current directory."
            );
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
        createEmptyStage();
        Commit init = new Commit();
        String commitName = sha1(serialize(init));
        init.saveCommit(commitName);
        createBranch("master", commitName);
        setHEADpointer("master");
    }

    public static void add(String fileName) throws IOException {
        File fileFile = join(CWD, fileName);
        if (!fileFile.exists()) {
            throw new GitletException("File does not exist. ");
        }
        // file content hash in the CWD
        String contentHash = sha1(serialize(readContentsAsString(fileFile)));

        //current commit
        Commit currentCommit = Commit.currentCommit();
        TreeMap<String, String> stageAddition = readStageAddition();
        TreeSet<String> stageRemoval = readStageRemoval();
        TreeMap<String, String> commitBlob = currentCommit.getBlobmap();
        if (commitBlob.containsKey(fileName) && contentHash.equals(commitBlob.get(fileName))) {
            stageAddition.remove(fileName);
        } else {
            stageAddition.put(fileName, contentHash);
            //create blob
            Blob add = new Blob(fileName);
            add.saveBlob();
        }
        stageRemoval.remove(fileName);


        //update the staging area file
        writeStageAddition(stageAddition);
        writeStageRemoval(stageRemoval);

    }


    public static void rm(String fileName) {
        TreeMap<String, String> stageAddition = readStageAddition();
        TreeSet<String> stageRemoval = readStageRemoval();

        if (stageAddition.remove(fileName) != null) {
            //update the staging area file
            writeStageAddition(stageAddition);
        } else {
            Commit currentCommit = Commit.currentCommit();
            TreeMap<String, String> commitBlob = currentCommit.getBlobmap();
            if (commitBlob.containsKey(fileName)) {
                stageRemoval.add(fileName);
                restrictedDelete(fileName);

                writeStageRemoval(stageRemoval);
            } else {
                throw new GitletException("No reason to remove the file.");
            }
        }



    }

    public static void commit(String message, String mergeTargetCommit) throws IOException {
        if (message.isEmpty()) {
            throw new GitletException("Please enter a commit message.");
        }

        TreeMap<String, String> stageAddition = readStageAddition();
        TreeSet<String> stageRemoval = readStageRemoval();
        if (stageAddition.isEmpty() && stageRemoval.isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

        Commit current = Commit.currentCommit();

        Commit newCommit = new Commit(message, sha1(serialize(current)), mergeTargetCommit);

        TreeMap<String, String> commitBlob = newCommit.getBlobmap();
        for (String key : stageAddition.keySet()) {
            commitBlob.put(key, stageAddition.get(key));
        }
        for (String key : stageRemoval) {
            commitBlob.remove(key);
        }


        //update the staging area file
        createEmptyStage();

        //store the commit file
        String commitName = sha1(serialize(newCommit));
        newCommit.saveCommit(commitName);

        setBranch(getCurrentBranch(), commitName);
    }


    public static void log() {
        StringBuilder log = new StringBuilder();
        Commit last = Commit.currentCommit();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH
        );

        while (last != null) {
            log.append("===\n");
            log.append(String.format("commit %s\n", sha1(serialize(last))));
            String formatDate = dateFormat.format(last.getTimestamp());
            log.append(String.format("Date: %s\n", formatDate));
            log.append(last.getMessage());
            log.append("\n\n");

            if (last.getParentString() == null) {
                break;
            }

            last = Commit.readCommit(last.getParentString());
        }
        System.out.println(log);
    }

    public static void merge(String targetBranchString) throws IOException {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        assert allBranches != null;
        if (!allBranches.contains(targetBranchString)) {
            throw new GitletException("No such branch exists.");
        }
        if (getCurrentBranch().equals(targetBranchString)) {
            throw new GitletException("Cannot merge a branch with itself.");
        }
        TreeMap<String, String> stageAddition = readStageAddition();
        TreeSet<String> stageRemoval = readStageRemoval();
        if (!stageAddition.isEmpty() || !stageRemoval.isEmpty()) {
            throw new GitletException("You have uncommitted changes.");
        }
        String targetCommitString = readContentsAsString(join(HEADS_DIR, targetBranchString));
        Commit targetCommit = Commit.readCommit(targetCommitString);
        validateUntrackedFiles(targetCommit);
        List<String> currentParentCommits = getCommitsParentsList(Commit.currentCommit());
        if (currentParentCommits.contains(targetCommitString)) {
            System.out.println("Given branch is an ancestor of the current branch.");
        }
        List<String> targetCurrentParentCommits = getCommitsParentsList(targetCommit);
        String currentCommitString = readContentsAsString(join(HEADS_DIR, getCurrentBranch()));

        if (targetCurrentParentCommits.contains(currentCommitString)) {
            checkout(targetBranchString);
            System.out.println("Current branch fast-forwarded.");
        }
        Commit splitPoint = getLatestCommonAncestor(currentParentCommits,
                targetCurrentParentCommits);
        assert splitPoint != null;
        Set<String> splitBlob = splitPoint.getBlobmap().keySet();
        Set<String> currentBlob = Commit.currentCommit().getBlobmap().keySet();
        Set<String> targetBlob = targetCommit.getBlobmap().keySet();
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitBlob);
        allFiles.addAll(currentBlob);
        allFiles.addAll(targetBlob);
        for (String fileName : allFiles) {
            if (!splitBlob.contains(fileName)) {
                if (!currentBlob.contains(fileName)) {
                    add(fileName);
                } else {
                    if (targetBlob.contains(fileName)) {
                        mergeConflict(targetCommit, fileName);
                    }
                }
            } else {
                if (currentBlob.contains(fileName)) {
                    if (splitPoint.getBlobmap().get(fileName).equals(
                            Commit.currentCommit().getBlobmap().get(fileName))) {
                        if (targetBlob.contains(fileName)) {
                            add(fileName);
                        } else {
                            rm(fileName);
                        }
                    } else {
                        if (targetBlob.contains(fileName)) {
                            if (!splitPoint.getBlobmap().get(fileName).equals(
                                    targetCommit.getBlobmap().get(fileName))) {
                                mergeConflict(targetCommit, fileName);
                            }
                        } else {
                            mergeConflict(targetCommit, fileName);
                        }
                    }
                } else {
                    if (targetBlob.contains(fileName)) {
                        if (splitPoint.getBlobmap().get(fileName).equals(
                                targetCommit.getBlobmap().get(fileName))) {
                            rm(fileName);
                        }
                    } else {
                        mergeConflict(targetCommit, fileName);
                    }
                }
            }
        }
        String commitMessage = String.format("Merged %s into %s", currentCommitString, targetCommitString);
        commit(commitMessage, targetCommitString);
    }


    private static void mergeConflict(Commit targetCommit, String fileName) throws IOException {
        StringBuilder conflict = new StringBuilder("<<<<<<< HEAD");
        String currentFileContent = readContentsAsString(join(BOLB_DIR,
                Commit.currentCommit().getBlobmap().get(fileName)));
        conflict.append(currentFileContent);
        conflict.append("=======");
        String targetFileContent = readContentsAsString(join(
                BOLB_DIR, targetCommit.getBlobmap().get(fileName)));
        conflict.append(targetFileContent);
        conflict.append(">>>>>>>");
        writeContents(join(CWD, fileName), conflict);
        add(fileName);

    }

    private static LinkedList<String> getCommitsParentsList(Commit targetCommit) {
        LinkedList<String> result = new LinkedList<>();

        Queue<Commit> fringe = new LinkedList<>();
        fringe.offer(targetCommit);

        while (!fringe.isEmpty()) {
            Commit nowCommit =  fringe.poll();
            assert nowCommit != null;
            for (String parentCommit : nowCommit.getAllParents()) {
                Commit parentCommitObject = Commit.readCommit(parentCommit);
                fringe.offer(parentCommitObject);
                result.addFirst(parentCommit);
            }
        }
        return result;
    }

    private static Commit getLatestCommonAncestor(List<String> currentCommitParentsList,
                                                  List<String> givenCommitParentsList) {
        for (String commitHash : currentCommitParentsList) {
            if (givenCommitParentsList.contains(commitHash)) {
                return Commit.readCommit(commitHash);
            }
        }
        return null;
    }

    public static void globalLog() {
        StringBuilder log = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH
        );
        String[] commitDirs = Objects.requireNonNull(COMMITS_DIR.list());
        List<String> commitDirList = Arrays.asList(commitDirs);
        Collections.shuffle(commitDirList);

        for (String commitDir : commitDirList) {
            List<String> allCommits = plainFilenamesIn(join(COMMITS_DIR, commitDir));
            assert allCommits != null;
            for (String commit : allCommits) {
                Commit commitObject = Commit.readCommit(commit);
                log.append("===\n");
                log.append(String.format("commit %s\n", commit));
                String formatDate = dateFormat.format(commitObject.getTimestamp());
                log.append(String.format("Date: %s\n", formatDate));
                log.append(commitObject.getMessage());
                log.append("\n\n");
            }
        }
        System.out.println(log);
    }

    public static void checkout(String fileName) {
        Commit currentCommit = Commit.currentCommit();
        TreeMap<String, String> commitBlob = currentCommit.getBlobmap();
        if (!commitBlob.containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        } else {
            String contentString = readObject(
                    join(BOLB_DIR, commitBlob.get(fileName)), String.class
            );
            writeContents(join(CWD, fileName), contentString);
        }
    }

    public static void checkout(String fileName, String commitHash) {
        Commit targetCommit = Commit.findCommitWithPrefix(commitHash);
        if (targetCommit == null) {
            throw new GitletException("No commit with that id exists.");
        }
        TreeMap<String, String> commitBlob = targetCommit.getBlobmap();
        if (!commitBlob.containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        } else {
            String contentString = readObject(
                    join(BOLB_DIR, commitBlob.get(fileName)), String.class
            );
            writeContents(join(CWD, fileName), contentString);
        }
    }

    public static void checkoutBranch(String branchName) throws IOException {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        assert allBranches != null;
        if (!allBranches.contains(branchName)) {
            throw new GitletException("No such branch exists.");
        }
        if (getCurrentBranch().equals(branchName)) {
            throw new GitletException("No need to checkout the current branch.");
        }

        String commitHash = readContentsAsString(join(HEADS_DIR, branchName));
        Commit targetCommit = Commit.readCommit(commitHash);

        validateUntrackedFiles(targetCommit);

        // put the version of file of the target commit and overwrite if it exists in the cwd
        overwriteWorkingDirectory(targetCommit);
        cleanUpFilesNotInTargetBranch(targetCommit);
        createEmptyStage();
        setHEADpointer(branchName);
    }


    private static void validateUntrackedFiles(Commit targetCommit) {
        Commit currentCommit = Commit.currentCommit();
        TreeMap<String, String> currentBlobs = currentCommit.getBlobmap();
        List<String> allCwdFiles = plainFilenamesIn(CWD);
        assert allCwdFiles != null;

        for (String fileName : allCwdFiles) {
            if (!currentBlobs.containsKey(fileName)
                    && targetCommit.getBlobmap().containsKey(fileName)
            ) {
                throwUntrackedFileException();
            }
        }
    }

    private static void throwUntrackedFileException() {
        throw new GitletException(
                "There is an untracked file in the way; delete it, or add and commit it first."
        );
    }

    private static void overwriteWorkingDirectory(Commit targetCommit) throws IOException {
        TreeMap<String, String> targetBlobs = targetCommit.getBlobmap();
        for (String targetBlob : targetBlobs.keySet()) {
            File newFile = join(CWD, targetBlob);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            String contentString = readObject(
                    join(BOLB_DIR, targetBlobs.get(targetBlob)), String.class);
            writeContents(newFile, contentString);
        }
    }



    private static void cleanUpFilesNotInTargetBranch(Commit targetCommit) {
        Commit currentCommit = Commit.currentCommit();
        TreeSet<String> copyCurrentBlobs = new TreeSet<>(currentCommit.getBlobmap().keySet());
        TreeMap<String, String> targetBlobs = targetCommit.getBlobmap();

        copyCurrentBlobs.removeAll(targetBlobs.keySet());

        for (String remainBlob : copyCurrentBlobs) {
            File deletedFile = join(CWD, remainBlob);
            if (deletedFile.exists()) {
                deletedFile.delete();
            }
        }
    }

    public static void reset(String commitHash) throws IOException {
        Commit targetCommit = Commit.findCommitWithPrefix(commitHash);
        if (targetCommit == null) {
            throw new GitletException("No commit with that id exists.");
        }
        validateUntrackedFiles(targetCommit);
        overwriteWorkingDirectory(targetCommit);
        cleanUpFilesNotInTargetBranch(targetCommit);
        createEmptyStage();
        setBranch(getCurrentBranch(), commitHash);
    }

    public static void status() {
        StringBuilder status = new StringBuilder();
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        assert allBranches != null;
        status.append("=== Branches ===\n");
        for (String branch : allBranches) {
            if (branch.equals(getCurrentBranch())) {
                status.append(String.format("*%s\n", branch));
                continue;
            }
            status.append(String.format("%s\n", branch));
        }
        status.append("\n");

        Set<String> stagedFiles =  readStageAddition().keySet();
        status.append("=== Staged Files ===\n");
        if (!stagedFiles.isEmpty()) {
            for (String stagedFile : stagedFiles) {
                status.append(String.format("%s\n", stagedFile));
            }
        }
        status.append("\n");

        TreeSet<String> removedFiles =  readStageRemoval();
        status.append("=== Removed Files ===\n");
        if (!removedFiles.isEmpty()) {
            for (String removedFile : removedFiles) {
                status.append(String.format("%s\n", removedFile));
            }
        }
        status.append("\n");

        // extra credits
        status.append("=== Modifications Not Staged For Commit ===\n");
        status.append("\n");
        status.append("=== Untracked Files ===\n");
        status.append("\n");
        System.out.println(status);
    }

    public static void branch(String branchName) throws IOException {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        if (allBranches.contains(branchName)) {
            throw new GitletException("A branch with that name already exists.");
        }
        Commit current = Commit.currentCommit();
        String commitHash = sha1(serialize(current));
        createBranch(branchName, commitHash);
    }

    public static void find(String message) {
        StringBuilder find = new StringBuilder();
        for (String commitDir : Objects.requireNonNull(COMMITS_DIR.list())) {
            List<String> allCommits = plainFilenamesIn(join(COMMITS_DIR, commitDir));
            assert allCommits != null;
            for (String commitString : allCommits) {
                Commit commitObject = Commit.readCommit(commitString);
                if (commitObject.getMessage().equals(message)) {
                    find.append(String.format("%s\n", commitString));
                }
            }
        }
        if (find.isEmpty()) {
            throw new GitletException("Found no commit with that message.\n");
        }
        System.out.println(find);
    }


    public static void rmBranch(String branchName) {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        assert allBranches != null;
        if (!allBranches.contains(branchName)) {
            throw new GitletException("branch with that name does not exist.");
        }
        if (getCurrentBranch().equals(branchName)) {
            throw new GitletException("Cannot remove the current branch.");
        }
        join(HEADS_DIR, branchName).delete();

    }



    public static TreeMap readStageAddition() {
        return readObject(ADDITION_F, TreeMap.class);
    }

    public static TreeSet readStageRemoval() {
        return readObject(REMOVAL_F, TreeSet.class);
    }


    public static void  writeStageAddition(TreeMap<String, String> stage) {
        writeObject(ADDITION_F, stage);
    }

    public static void writeStageRemoval(TreeSet<String> stage) {
        writeObject(REMOVAL_F, stage);
    }

    public static void createEmptyStage() {
        TreeMap<String, String> stageAddition = new TreeMap<>();
        writeObject(ADDITION_F, stageAddition);
        TreeSet<String> stageRemoval = new TreeSet<>();
        writeObject(REMOVAL_F, stageRemoval);
    }


    public static void setHEADpointer(String branch) {
        writeContents(HEADS_F, branch);
    }

    public static void createBranch(String name, String commitHash) throws IOException {
        File branchFile = join(HEADS_DIR, name);
        branchFile.createNewFile();
        writeContents(branchFile, commitHash);
    }

    private static String getCurrentBranch() {
        return readContentsAsString(Repository.HEADS_F);
    }

    public static void setBranch(String branch, String commitHash) {
        writeContents(join(HEADS_DIR, branch), commitHash);
    }



}
