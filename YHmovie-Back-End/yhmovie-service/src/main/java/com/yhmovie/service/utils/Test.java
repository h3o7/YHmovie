package com.yhmovie.service.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Test {
    public static void main(String[] args) {
        String st = "10-10";
        String[] split = st.split("-");
        String result = split[0] + "排" + split[1] + "座";
        System.out.println(result);
    }
}
