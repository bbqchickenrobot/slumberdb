package info.slumberdb;

import java.util.*;

public class InMemoryStringKeyValueStore implements StringKeyValueStore {


    SortedMap<String, String> map = new TreeMap<>();

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void putAll(Map<String, String> values) {

        map.putAll(values);
    }

    @Override
    public void removeAll(Iterable<String> keys) {
        for (String key : keys) {
            map.remove(key);
        }
    }

    @Override
    public void updateAll(Iterable<CrudOperation> updates) {
        throw new RuntimeException("Not implemented");
    }



    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public KeyValueIterable<String, String> search(String startKey) {

        final Set<Map.Entry<String, String>> entries = map.tailMap(startKey).entrySet();
        final Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        return new KeyValueIterable<String, String>() {
            @Override
            public void close() {

            }

            @Override
            public Iterator<Entry<String, String>> iterator() {
                return new Iterator<Entry<String, String>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<String, String> next() {
                        Map.Entry<String, String> next = iterator.next();
                        return new Entry<>(next.getKey(), next.getValue());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    @Override
    public KeyValueIterable<String, String> loadAll() {

        final Set<Map.Entry<String, String>> entries = map.entrySet();
        final Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        return new KeyValueIterable<String, String>() {
            @Override
            public void close() {

            }

            @Override
            public Iterator<Entry<String, String>> iterator() {
                return new Iterator<Entry<String, String>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<String, String> next() {
                        Map.Entry<String, String> next = iterator.next();
                        return new Entry<>(next.getKey(), next.getValue());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
