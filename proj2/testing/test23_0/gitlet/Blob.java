package gitlet;

import java.io.File;
import java.io.IOException;
import static gitlet.Utils.*;

public class Blob {
    private final byte[] content;
    public Blob(String filename) {
        content = serialize(readContentsAsString(join(Repository.CWD, filename)));
    }

    public void saveBlob() throws IOException {
        File blobFile = join(Repository.BOLB_DIR, sha1(content));
        blobFile.createNewFile();
        writeContents(blobFile, content);
    }
}
