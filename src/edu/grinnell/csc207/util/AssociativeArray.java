package edu.grinnell.csc207.util;

import static java.lang.reflect.Array.newInstance;

/**
 * A basic implementation of Associative Arrays with keys of type K
 * and values of type V. Associative Arrays store key/value pairs
 * and permit you to look up values by key.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @author David William Stroud
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V>[] pairs;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings({ "unchecked" })
  public AssociativeArray() {
    // Creating new arrays is sometimes a PITN.
    this.pairs = (KVPair<K, V>[]) newInstance((new KVPair<K, V>()).getClass(),
        DEFAULT_CAPACITY);
    this.size = 0;
  } // AssociativeArray()

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   *
   * @return a new copy of the array
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> cloned = new AssociativeArray<>();
    for (KVPair<K, V> pair : this.pairs) {
      if (pair == null) {
        // We have found the first null, which indicates the end of the useful part of the array.
        break;
      } // if
      try {
        cloned.set(pair.key, pair.val);
      } catch (NullKeyException err) {
        // This should never happen,
        // since every key in this associative array should be non-null,
        // so do nothing.
      } // try-catch
    } // for
    return cloned;
  } // clone()

  /**
   * Convert the array to a string.
   *
   * @return a string of the form "{Key0:Value0, Key1:Value1, ... KeyN:ValueN}"
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("{");

    for (KVPair<K, V> pair : this.pairs) {
      if (pair == null) {
        // We have found the first null, which indicates the end of the useful part of the array.
        break;
      } // if

      if (sb.length() != 1) {
        sb.append(", ");
      } // if

      sb.append(pair.toString());
    } // for

    sb.append('}');

    return sb.toString();
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to
   * get(key) will return value.
   *
   * @param key
   *   The key whose value we are seeting.
   * @param value
   *   The value of that key.
   *
   * @throws NullKeyException
   *   If the client provides a null key.
   */
  public void set(K key, V value) throws NullKeyException {
    if (key == null) {
      throw new NullKeyException();
    } // if

    try {
      int index = this.find(key);
      this.pairs[index].val = value;
    } catch (KeyNotFoundException err) {
      if (this.pairs.length <= this.size()) {
        this.expand();
      } // if
      this.pairs[this.size()] = new KVPair<>(key, value);
      this.size++;
    } // try-catch
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @param key
   *   A key
   *
   * @throws KeyNotFoundException
   *   when the key is null or does not appear in the associative array.
   *
   * @return The value associated with that key.
   */
  public V get(K key) throws KeyNotFoundException {
    return this.pairs[this.find(key)].val;
  } // get(K)

  /**
   * Determine if key appears in the associative array. Should
   * return false for the null key.
   *
   * @param key The key for which to search in this associative array.
   *
   * @return Whether that key exists in this associative array.
   */
  public boolean hasKey(K key) {
    try {
      this.find(key);
      return true;
    } catch (KeyNotFoundException err) {
      return false;
    } // try-catch
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls
   * to get(key) will throw an exception. If the key does not appear
   * in the associative array, does nothing.
   *
   * @param key The key to remove.
   */
  public void remove(K key) {
    try {
      int index = this.find(key);
      if (index == (this.size() - 1)) {
        this.pairs[index] = null;
      } else {
        this.pairs[index] = this.pairs[this.size() - 1];
        this.pairs[this.size() - 1] = null;
      } // if-else
      this.size--;
    } catch (KeyNotFoundException err) {
      // We do not need to do anything special if the key does not exist in the array.
    } // try-catch
  } // remove(K)

  /**
   * Determine how many key/value pairs are in the associative array.
   *
   * @return The number of key/value pairs there are in this associative array.
   */
  public int size() {
    return this.size;
  } // size()

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Expand the underlying array.
   */
  void expand() {
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key.
   * If no such entry is found, throws an exception.
   *
   * @param key
   *   The key of the entry.
   *
   * @throws KeyNotFoundException
   *   If the key does not appear in the associative array.
   *
   * @return The index in this.pairs of the pair containing the key.
   */
  int find(K key) throws KeyNotFoundException {
    for (int i = 0; i < this.size(); i++) {
      if (this.pairs[i].key.equals(key)) {
        return i;
      } // if
    } // for

    throw new KeyNotFoundException(
            "Key '"
                    + (key == null ? "null" : key.toString())
                    + "' is missing");
  } // find(K)
} // class AssociativeArray
