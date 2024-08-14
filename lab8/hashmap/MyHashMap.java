package hashmap;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author pippenchen
 */
public class MyHashMap<K, V> implements Map61B<K, V> {


    private static int initialSize = 16;
    private int size = initialSize;
    private int pairs = 0;
    private static double loadFactor = 0.75;
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(initialSize);
    }

    public MyHashMap(int size) {
        initialSize = size;
        buckets = createTable(size);
        for (int i = 0; i < size; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param size initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int size, double maxLoad) {
        initialSize = size;
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }



    @Override
    public void clear() {
        buckets = createTable(size);
        for (int i = 0; i < size; i++) {
            buckets[i] = createBucket();
        }
        pairs = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        int i = Math.floorMod(key.hashCode(), size);
        for (Node n : buckets[i]){
            if (n.key.equals(key)){
                return n.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return pairs;
    }

    private void resize(int resize) {
        MyHashMap<K, V> temp = new MyHashMap<>(resize);
        for (int i = 0; i < size; i++) {
            for (Node n : buckets[i]) {
                temp.put(n.key, n.value);
            }
        }
        this.size  = temp.size;
        this.pairs  = temp.pairs;
        this.buckets = temp.buckets;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (value == null) {
            remove(key);
        }

        // double table size if average length of list >= 10
        if ((double) pairs / size >= loadFactor) {
            resize(2 * size);
        }
        Node item = createNode(key, value);
        int i = Math.floorMod(key.hashCode(), size);
        if (buckets[i] == null) {
            buckets[i] = createBucket();
        }

        for (Node n : buckets[i]) {
            if (n.key.equals(key)) {
                n.value = value; // Update the existing value
                return;
            }
        }
        buckets[i].add(createNode(key, value)); // Add new key-value pair
        pairs++;
    }

    @Override
    public Set<K> keySet() {
        return Set.of();
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }

}
