package com.xiangrikui.api.mc.common.util;

/**
 * Holder for a key and value.
 *
 * @version 
 */
public class KeyValueHolder<K, V> {

    private K key;
    private V value;

    public KeyValueHolder(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeyValueHolder<K, V> that = (KeyValueHolder<K, V>) o;

        if (key != null ? !key.equals(that.key) : that.key != null) {
            return false;
        } else if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return key + " -> " + value;
    }
}
