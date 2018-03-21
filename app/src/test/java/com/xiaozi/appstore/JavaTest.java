package com.xiaozi.appstore;

import org.junit.Test;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by fish on 18-1-22.
 */

public class JavaTest {

    @Test
    public void RunTest() {

    }

    private List<Integer> getSubStrPositions(String originStr, String subStr) {
        if (!originStr.contains(subStr)) return null;
        ArrayList<Integer> result = new ArrayList<>();
        int ptr = 0;
        System.out.print(originStr.split(subStr).length);
        for (String s : originStr.split(subStr)) {
            System.out.println(s);
            ptr += s.length();
            result.add(ptr);
            ptr += subStr.length();
        }
//        result.remove(result.size() - 1);
        return result;
    }

    @Test
    public void testFilter() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            list.add(i);
        }
    }


    private <E> List<E> filter(Collection<E> list, IFilter<E> filter) {
        ArrayList<E> result = new ArrayList<E>();
        for (E elem : list) {
            if (filter.predicateBoolean(elem)) {
                result.add(elem);
            }
        }
        return result;
    }

    @Test
    public void testUrlEncode() {
        System.out.println(URLEncoder.encode("~"));
    }

    interface IFilter<T> {
        boolean predicateBoolean(T t);
    }
}
