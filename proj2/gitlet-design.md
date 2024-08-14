# Gitlet Design Document

**Name**: pippen

## Classes and Data Structures

### Main
This is the entry point to our program. 
It takes in arguments from the command line and based on the command (the first element of the args array) calls the corresponding command in Repository 
which will actually execute the logic of the command. It also validates the arguments based on the command to ensure that enough arguments were passed in.

### Fields
This class has no fields and hence no associated state:
it simply validates arguments and defers the execution to the CapersRepository class.



### Repostory
This is where the main logic of our program will live. This file will handle all of the actual capers commands by reading/writing from/to the correct file, setting up persistence, and additional error checking.

It will also be responsible for setting up all persistence within capers. This includes creating the .capers folder as well as the folder and file where we store all Dog objects and the current story.


#### Fields

* `static final File CWD = new File(System.getProperty("user.dir"));` The Current Working Directory. Since it has the package-private access modifier (i.e. no access modifier), other classes in the package may use this field. It is useful for the other File objects we need to use.
* `static final File GITLET_DIR = join(CWD, ".gitlet");` The hidden .gitlet directory. This is where all of the state of the Repository will be stored, including additional things like the Commit, Bolb objects and the HEAD pointer, branches, staging area. It is also package private as other classes will use it to store their state.
* `static final File REF_DIR = join(GITLET_DIR, "ref");` The directory where store the reference of HEAD pointer and Branches
* `static final File HEADS_DIR = join(REF_DIR, "heads");` The file store information of HEAD pointer 
* `static final File OBJECTS_DIR = join(GITLET_DIR, "objects");` 
* `static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");` 
* `static final File BOLB_DIR = join(OBJECTS_DIR, "blobs");`
* `static final File HEADS_F = join(REF_DIR, "HEAD");` 
* `static final File INDEX_F = join(GITLET_DIR, "index");`
  These fields are both static since we don’t actually instantiate a Repository object: we simply use it to house functions. If we had additional non-static state (like the Commit class), we’d need to serialize it and save it to a file.

## Algorithms

## Persistence

