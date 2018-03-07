package com.lantaiyuan.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.comparators.ComparableComparator;
import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.collections4.comparators.NullComparator;

public class CommonUtil {

    /**
     * bean 
     * @param list
     * @param filedName
     * @param ascFlag
     */
    @SuppressWarnings("unchecked")
    public static void sort(List list, String filedName, boolean ascFlag) {

        if (list.size() == 0 || filedName.equals("")) {
            return;
        }
        Comparator<?> cmp = ComparableComparator.INSTANCE;
        // 降順の場合
        if (ascFlag) {
            cmp = ComparatorUtils.nullLowComparator(cmp);
        } else {
            cmp = ComparatorUtils.reversedComparator(cmp);

        }
        Collections.sort(list, new BeanComparator(filedName, cmp));
    }
    
    /**
     * beanのある属性によってソートする場合
     * @param beans
     * @param sortParam
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void sortExecute(List<?> beans, Map<String, String> sortParam) {

        // 検索結果リスト対象またはソースパラメータ対象がNULLである場合
        if (beans.size() == 0 || sortParam.keySet().size() == 0) {
            // 処理終了
            return;
        }

        // 検索結果リスト対象またはソースパラメータ対象のサイズがゼロ件の場合
        if (beans.isEmpty() || sortParam.isEmpty()) {
            // 処理終了
            return;
        }
        ComparatorChain comparator = new ComparatorChain();
        boolean sortMethod = false;
        for (String itemName : sortParam.keySet()) {
            sortMethod = false;
            if ("desc".equals(sortParam.get(itemName))) {
                sortMethod = true;
            }
            comparator.addComparator(new BeanComparator(itemName, new NullComparator()), sortMethod);
        }
        Collections.sort(beans, comparator);
    }

}