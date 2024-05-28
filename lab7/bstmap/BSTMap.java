package bstmap;

import com.sun.jdi.Value;

import java.security.Key;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

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
    public boolean containsKey(K key) {
        return containKey(root, key);
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
    public V get(K key) {
        return get(root, key);
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
    public void put(K key, V value) {
        if (get(key) == null) {
            size++;
        }
        root = put(root, key, value);
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
    public void printInOrder(){
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }
}
