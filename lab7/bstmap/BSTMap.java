package bstmap;

import java.security.Key;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B{

    private BSTNode root;
    private int size = 0;

    private class BSTNode {
        K key;
        V value;
        BSTNode leftNode;
        BSTNode rightNode;
        public BSTNode(K k, V v) {
            key = k;
            value = v;
        }
    }


    public  BSTMap(){
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(Object key) {
        return containKey(root,(K) key);
    }

    private boolean containKey(BSTNode n, K key){
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        if (n == null) {
            return false;
        }
        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            return containKey(n.leftNode, key);
        }
        else if (cmp > 0){
            return containKey(n.rightNode, key);
        }
        else{
            return true;
        }
    }

    @Override
    public Object get(Object key) {
        return get(root, (K) key);
    }

    private V get(BSTNode n, K key){
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        if (n == null) {
            return null;
        }
        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            return get(n.leftNode, key);
        }
        else if (cmp > 0){
            return get(n.rightNode, key);
        }
        else{
            return n.value;
        }
    }

    @Override
    public int size() {
        if (root == null){
            return 0;
        }
        return size;
    }

    @Override
    public void put(Object key, Object value) {
        if (get(key) == null) {
            size++;
        }
        root = put(root, (K) key, (V) value);
    }

    private BSTNode put(BSTNode n, K key, V value) {
        if (n == null) {
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(n.key);
        if (cmp < 0){
            n.leftNode  = put(n.leftNode,  key, value);
        }
        else if (cmp > 0) {
            n.rightNode = put(n.rightNode, key, value);
        }
        else{
            n.value = value;
        }
        return n;
    }


    @Override
    public Set keySet() {
        return Set.of();
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public Object remove(Object key, Object value) {
        return null;
    }

    @Override
    public Iterator iterator() {
        return null;
    }
}
