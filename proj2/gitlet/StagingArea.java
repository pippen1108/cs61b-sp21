package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** 暫存區（Staging Area），儲存將加入與移除的檔案。 */
public class StagingArea implements Serializable {
    private final TreeMap<String, String> additions; // fileName -> contentHash
    private final TreeSet<String> removals;          // fileName

    public StagingArea() {
        additions = new TreeMap<>();
        removals = new TreeSet<>();
    }

    /** 將檔案加入暫存區（新增或修改） */
    public void add(String fileName, String contentHash) {
        additions.put(fileName, contentHash);
        removals.remove(fileName); // 如果之前標記刪除，現在又加回來，就取消刪除
    }

    /** 將檔案標記為刪除 */
    public void remove(String fileName) {
        additions.remove(fileName); // 如果之前有 add，就不 add 了
        removals.add(fileName);
    }

    /** 從暫存區移除（取消暫存） */
    public void unstage(String fileName) {
        additions.remove(fileName);
        removals.remove(fileName);
    }

    /** 取得暫存的新增檔案 */
    public TreeMap<String, String> getAdditions() {
        return additions;
    }

    /** 取得暫存的刪除檔案 */
    public TreeSet<String> getRemovals() {
        return removals;
    }

    /** 是否為空的 staging area（沒有新增也沒有刪除） */
    public boolean isEmpty() {
        return additions.isEmpty() && removals.isEmpty();
    }

    /** 儲存 staging area 到兩個檔案 */
    public void save() {
        writeObject(ADDITION_F, additions);
        writeObject(REMOVAL_F, removals);
    }

    /** 從檔案載入 staging area（如果檔案不存在，就建立空的） */
    @SuppressWarnings("unchecked")
    public static StagingArea load() {
        StagingArea staging = new StagingArea();

        if (ADDITION_F.exists()) {
            staging.additions.putAll(readObject(ADDITION_F, TreeMap.class));
        }

        if (REMOVAL_F.exists()) {
            staging.removals.addAll(readObject(REMOVAL_F, TreeSet.class));
        }

        return staging;
    }

    /** 清除 staging area 並儲存變更 */
    public void clear() {
        additions.clear();
        removals.clear();
        save();
    }

}
