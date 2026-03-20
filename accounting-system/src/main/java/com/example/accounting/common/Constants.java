package com.example.accounting.common;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String USER_FILE = "users.csv";
    public static final String RECORD_FILE = "records.csv";

    public static final int TYPE_INCOME = 1;
    public static final int TYPE_EXPENSE = 2;

    public static final List<String> INCOME_CATEGORIES = Arrays.asList(
            "薪资", "奖金", "投资收益", "兼职收入", "礼金", "其他收入"
    );

    public static final List<String> EXPENSE_CATEGORIES = Arrays.asList(
            "餐饮", "购物", "交通", "住房", "娱乐", "医疗", "教育", "其他支出"
    );

    public static boolean isValidCategory(int type, String category) {
        if (type == TYPE_INCOME) {
            return INCOME_CATEGORIES.contains(category);
        } else if (type == TYPE_EXPENSE) {
            return EXPENSE_CATEGORIES.contains(category);
        }
        return false;
    }
}