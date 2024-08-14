package gitlet;

import java.io.IOException;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
             throw new IllegalArgumentException("Must have at least one argument");
        }
        String firstArg = args[0];
        String text;
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.initialCommit();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                text = args[1];
                Repository.add(text);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                text = args[1];
                Repository.commit(text);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    String fileName = args[2];
                    Repository.checkout(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
                    String commitId = args[1];
                    String fileName = args[3];
                    Repository.checkout(fileName, commitId);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + firstArg);
            // TODO: FILL THE REST IN
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
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}

