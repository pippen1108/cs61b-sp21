package gitlet;

import java.io.CharArrayWriter;
import java.io.IOException;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new GitletException("Please enter a command.");
            }
            String firstArg = args[0];
            String text;
            switch (firstArg) {
                case "init":
                    Repository.initialCommit();
                    break;
                case "add":
                    validateRepo();
                    validateNumArgs("add", args, 2);
                    text = args[1];
                    Repository.add(text);
                    break;
                case "commit":
                    validateRepo();
                    validateNumArgs("commit", args, 2);
                    text = args[1];
                    Repository.commit(text, null);
                    break;
                case "log":
                    validateRepo();
                    validateNumArgs("log", args, 1);
                    Repository.log();
                    break;
                case "global-log":
                    validateNumArgs("global-log", args, 1);
                    Repository.globalLog();
                    break;
                case "status":
                    validateRepo();
                    validateNumArgs("status", args, 1);
                    Repository.status();
                    break;
                case "checkout":
                    validateRepo();
                    if (args.length == 3 && args[1].equals("--")) {
                        String fileName = args[2];
                        Repository.checkout(fileName);
                    } else if (args.length == 4 && args[2].equals("--")) {
                        String commitId = args[1];
                        String fileName = args[3];
                        Repository.checkout(fileName, commitId);
                    } else if (args.length == 2) {
                        String branch = args[1];
                        Repository.checkoutBranch(branch);
                    } else {
                        throw new GitletException("Incorrect operands.");
                    }
                    break;
                case "find":
                    validateRepo();
                    validateNumArgs("find", args, 2);
                    text = args[1];
                    Repository.find(text);
                    break;
                case "reset":
                    validateRepo();
                    validateNumArgs("reset", args, 2);
                    text = args[1];
                    Repository.reset(text);
                    break;
                case "branch":
                    validateRepo();
                    validateNumArgs("branch", args, 2);
                    text = args[1];
                    Repository.branch(text);
                    break;
                case "rm-branch":
                    validateRepo();
                    validateNumArgs("rm-branch", args, 2);
                    text = args[1];
                    Repository.rmBranch(text);
                    break;
                case "rm":
                    validateRepo();
                    validateNumArgs("rm", args, 2);
                    text = args[1];
                    Repository.rm(text);
                    break;
                case "merge":
                    validateRepo();
                    validateNumArgs("merge", args, 2);
                    text = args[1];
                    Repository.merge(text);
                default:
                    throw new GitletException("No command with that name exists.");
            }
        } catch (IOException e) {
            System.err.println("An IO error occurred: " + e.getMessage());
        }
    }

    public static void validateRepo() {
        if (!join(Repository.GITLET_DIR).exists()) {
            throw new GitletException("Not in an initialized Gitlet directory.");
        }
    }



    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new GitletException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}

