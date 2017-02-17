package org.yqj.boot.demo;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yaoqijun.
 * Date:2016-02-05
 * Email:yaoqj@terminus.io
 * Descirbe:
 */
public class TestContent {
    public static void main(String[] args) {
        List<Integer> test = Lists.newArrayList(2, 3, 5, 5, 23, 41, 23, 123, 1);
        Collections.sort(test, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2-o1;
            }
        });
        System.out.println(test);
    }
}
