package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static gitlet.Utils.*;

public class Blob {
    private final byte[] content;
    public Blob(String filename){
        content = serialize(readContentsAsString(join(Repository.CWD, filename)));
    }

    public void saveBlob() throws IOException {
        File blob_file = join(Repository.BOLB_DIR, sha1(content));
        blob_file.createNewFile();
        writeContents(blob_file, content);
    }
}
