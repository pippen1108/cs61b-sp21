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
public class Repository2 {
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
        StagingArea staging = new StagingArea();
        staging.save();
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

        StagingArea staging = StagingArea.load();


        TreeMap<String, String> commitBlob = currentCommit.getBlobmap();
        if (commitBlob.containsKey(fileName) && contentHash.equals(commitBlob.get(fileName))) {
            staging.unstage(fileName);
        } else {
            staging.add(fileName, contentHash);
            //create blob
            Blob add = new Blob(fileName);
            add.saveBlob();
        }

        staging.save();

    }


    public static void rm(String fileName) {
        StagingArea staging = StagingArea.load();
        Commit head = Commit.currentCommit();
        TreeMap<String, String> commitBlobs = head.getBlobmap();

        boolean isStagedForAdd = staging.getAdditions().containsKey(fileName);
        boolean isTrackedInCommit = commitBlobs.containsKey(fileName);

        if (isStagedForAdd) {
            staging.unstage(fileName); // 取消 staged for addition
        } else if (isTrackedInCommit) {
            staging.remove(fileName); // 加入 removals
            restrictedDelete(fileName);
        } else {
            throw new GitletException("No reason to remove the file.");
        }
        staging.save();

    }

    public static void commit(String message, String mergeTargetCommit) throws IOException {
        if (message.isEmpty()) {
            throw new GitletException("Please enter a commit message.");
        }

        // 讀入 staging area
        StagingArea staging = StagingArea.load();
        if (staging.isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

        // 取得當前 commit
        Commit current = Commit.currentCommit();
        String parentSha1 = sha1(serialize(current));

        // 建立新的 commit，設定父 commit hash
        Commit newCommit = new Commit(message, parentSha1, mergeTargetCommit);

        // 複製上一個 commit 的 blob map（深拷貝）
        TreeMap<String, String> commitBlob = new TreeMap<>(current.getBlobmap());

        // 加入 staging 中的新增/修改
        for (Map.Entry<String, String> entry : staging.getAdditions().entrySet()) {
            commitBlob.put(entry.getKey(), entry.getValue());
        }

        // 移除 staging 中標記刪除的檔案
        for (String fileName : staging.getRemovals()) {
            commitBlob.remove(fileName);
        }

        // 設定新的 blob map
        newCommit.setBlobMap(commitBlob);

        // 清空 staging area 並儲存
        staging.clear();
        staging.save();

        // 儲存 commit 物件
        String commitName = sha1(serialize(newCommit));
        newCommit.saveCommit(commitName);

        // 更新 HEAD 分支
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
            if (last.getMergeParentString() != null) {
                log.append(String.format("Merge: %s %s\n",
                        last.getParentString().substring(0, 7),
                        last.getMergeParentString().substring(0, 7)));
            }
            String formatDate = dateFormat.format(last.getTimestamp());
            log.append(String.format("Date: %s\n", formatDate));
            log.append(last.getMessage());

            if (last.getParentString() == null) {
                break;
            }
            log.append("\n\n");
            last = Commit.readCommit(last.getParentString());
        }
        System.out.println(log);
    }

    public static void merge(String targetBranchName) throws IOException {
        validateBranch(targetBranchName);
        if (getCurrentBranch().equals(targetBranchName)) {
            throw new GitletException("Cannot merge a branch with itself.");
        }
        validateStageArea();

        String targetCommitId = readContentsAsString(join(HEADS_DIR, targetBranchName));
        Commit currentCommit = Commit.currentCommit();
        Commit targetCommit = Commit.readCommit(targetCommitId);

        validateUntrackedFiles(targetCommit);

        // Fast-forward checks
        if (CommitGraph.getAllAncestors(currentCommit.getId()).contains(targetCommitId)) {
            throw new GitletException("Given branch is an ancestor of the current branch.");
        }
        if (CommitGraph.getAllAncestors(targetCommitId).contains(currentCommit.getId())) {
            checkoutBranch(targetBranchName);
            throw new GitletException("Current branch fast-forwarded.");
        }

        // Execute merge using MergeEngine
        MergeEngine engine = new MergeEngine(currentCommit, targetCommit);
        engine.runMerge();
        Map<String, String> mergedFiles = engine.getMergedFiles();

        // Write merged contents to working directory and stage them
        for (Map.Entry<String, String> entry : mergedFiles.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();
            writeContents(join(CWD, fileName), content);
            add(fileName);  // stage the merged version
        }

        // Print conflict message if any
        if (engine.hasConflict()) {
            System.out.println("Encountered a merge conflict.");
        }

        // Create merge commit
        String mergeMsg = String.format("Merged %s into %s.", targetBranchName, getCurrentBranch());
        commit(mergeMsg, targetCommitId);  // second parent is targetCommit
    }


    private static void validateBranch(String branchName) {
        List<String> allBranches = plainFilenamesIn(HEADS_DIR);
        assert allBranches != null;
        if (!allBranches.contains(branchName)) {
            throw new GitletException("A branch with that name does not exist.");
        }
    }

    private static void validateStageArea() {
        StagingArea staging = StagingArea.load();
        if (!staging.isEmpty()) {
            throw new GitletException("You have uncommitted changes.");
        }
    }

    public static void globalLog() {
        StringBuilder log = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH
        );
        String[] commitDirs = Objects.requireNonNull(COMMITS_DIR.list());
        
        for (String commitDir : commitDirs) {
            List<String> allCommits = plainFilenamesIn(join(COMMITS_DIR, commitDir));
            assert allCommits != null;
            for (String commit : allCommits) {
                Commit commitObject = Commit.readCommit(commit);
                log.append("===\n");
                log.append(String.format("commit %s\n", commit));
                String formatDate = dateFormat.format(commitObject.getTimestamp());
                log.append(String.format("Date: %s\n", formatDate));
                log.append(commitObject.getMessage());
                if (!commitObject.getMessage().endsWith("\n")) {
                    log.append("\n");
                }
                log.append("\n");
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
        StagingArea staging = StagingArea.load();
        staging.clear();
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
        StagingArea staging = StagingArea.load();
        staging.clear();
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
        StagingArea staging = StagingArea.load();

        Set<String> stagedFiles =  staging.getAdditions().keySet();
        status.append("=== Staged Files ===\n");
        if (!stagedFiles.isEmpty()) {
            for (String stagedFile : stagedFiles) {
                status.append(String.format("%s\n", stagedFile));
            }
        }
        status.append("\n");

        TreeSet<String> removedFiles = staging.getRemovals();
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


    public static void setHEADpointer(String branch) {
        writeContents(HEADS_F, branch);
    }

    public static void createBranch(String name, String commitHash) throws IOException {
        File branchFile = join(HEADS_DIR, name);
        branchFile.createNewFile();
        writeContents(branchFile, commitHash);
    }

    private static String getCurrentBranch() {
        return readContentsAsString(Repository2.HEADS_F);
    }

    public static void setBranch(String branch, String commitHash) {
        writeContents(join(HEADS_DIR, branch), commitHash);
    }


}
