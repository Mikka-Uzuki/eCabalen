package com.example.ecabalen;

import java.util.Dictionary;
import java.util.Enumeration;

public class DictionaryKapampangan
{
    Dictionary<String, String> contractions = new Dictionary<String, String>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Enumeration<String> keys() {
            return null;
        }

        @Override
        public Enumeration<String> elements() {
            return null;
        }

        @Override
        public String get(Object o) {
            return null;
        }

        @Override
        public String put(String s, String s2) {
            return null;
        }

        @Override
        public String remove(Object o) {
            return null;
        }
    };
    public DictionaryKapampangan()
    {
        contractions.put("banana", "\tˈgɑpis ");
    }

    public Dictionary<String, String> getContractions()
    {
        return contractions;
    }
}
