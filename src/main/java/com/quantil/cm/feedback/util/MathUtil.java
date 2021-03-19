package com.quantil.cm.feedback.util;

import java.math.BigDecimal;

public class MathUtil {

    /**
     * 计算 cnt 占 total 的百分比. 保留小数点后一位有效数字
     * @param cnt
     * @param totalCnt
     * @return
     */
    public static BigDecimal dividePercent(int cnt, int totalCnt) {
        double rate = 0.0;
        if (totalCnt != 0) {
            rate = (cnt * 100.0) / (totalCnt * 1.0);
        }
        BigDecimal doubleNum = BigDecimal.valueOf(rate);
        return doubleNum.setScale(1, BigDecimal.ROUND_HALF_UP);
    }
}
