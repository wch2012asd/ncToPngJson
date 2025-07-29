package com.example;

import com.example.NcDataModel;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author: gengfangdong
 * @Description
 * @FileName: NumberUtils
 * @Date: 2022/10/31 14:29
 * @Version: 1.0
 */
public class NumberUtils {

    public static final Double inputInvalidValue = -9999.0;

    public static final Double inputFilledValue = -99999.0;

    /**
     * 查找指定值在数组中的第一个位置
     *
     * @param data  数组
     * @param value 指定的值
     * @return 第一个位置 如果不存在 返回 -1
     */
    public static int findValueFirstIndexOfData(double[] data, double value) {

        if (data == null) {
            return -1;
        }
        int index = -1;
        double step = 0.0009;
        if (data.length >= 2) {
            step = Math.abs(data[0] - data[1]);
        }
        double minGap = Math.abs(step);
        for (int i = 0; i < data.length; i++) {
            if (Double.valueOf(value) == data[i]) {
                index = i;
                break;
            }
            if (Math.abs(Double.valueOf(value) - data[i]) < minGap) {
                index = i;
                minGap = Math.abs(Double.valueOf(value) - data[i]);
            }
        }
        return index;
    }

    /**
     * 格式化小数位数
     *
     * @param value       数据
     * @param decimalSize 小数位数
     * @return
     */
    public static Double formatDecimal(Double value, Integer decimalSize) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(decimalSize, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * object 转 double
     *
     * @param arrayObj 数组对象 二维
     * @param xIndex   x位置
     * @param yIndex   y位置
     * @return
     */
    public static Double getPointFromDataAndReversal(Object arrayObj, Integer xIndex, Integer yIndex, Boolean reversal) {
        if (arrayObj == null) {
            return null;
        }
        if (arrayObj instanceof double[][]) {
            double[][] data = (double[][]) arrayObj;
            if (reversal) {
                return data[yIndex][xIndex];
            }
            return data[xIndex][yIndex];
        }
        if (arrayObj instanceof short[][]) {
            double[][] data = transformShortToDouble((short[][]) arrayObj);
            if (reversal) {
                return data[yIndex][xIndex];
            }
            return data[xIndex][yIndex];
        }
        if (arrayObj instanceof float[][]) {
            double[][] data = transformFloatToDouble((float[][]) arrayObj);
            if (reversal) {
                return data[yIndex][xIndex];
            }
            return data[xIndex][yIndex];
        }
        if (arrayObj instanceof int[][]) {
            double[][] data = transformIntToDouble((int[][]) arrayObj);
            if (reversal) {
                return data[yIndex][xIndex];
            }
            return data[xIndex][yIndex];
        }
        return null;
    }

    /**
     * 一维数组 转 二维数组
     *
     * @param doubleArray 一维数组
     * @param xLength     一维长度
     * @param yLength     二维长度
     * @return
     */
    public static double[][] oneArrayToTwoArray(double[] doubleArray, int xLength, int yLength) {
        double[][] data = new double[xLength][yLength];
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {
                data[i][j] = doubleArray[i * yLength + j];
            }
        }
        return data;
    }

    /**
     * 计算二维数组中的最大值和最小值
     *
     * @param doubleArray
     * @return {maxValue,minValue}
     */
    public static double[] calcMaxAndMinInTwoArray(double[][] doubleArray) {
        int yLength = doubleArray.length;
        if (yLength <= 0) {
            return null;
        }
        int xLength = doubleArray[0].length;
        if (xLength <= 0) {
            return null;
        }

        double maxData = 0.0, minData = 0.0;
        for (int i = 0; i < yLength; i++) {
            Arrays.sort(doubleArray[i]);
            double maxCandidate = doubleArray[i][xLength - 1];
            double minCandidate = doubleArray[i][0];
            int index = 0;
            while (minCandidate == NumberUtils.inputInvalidValue && index < xLength) {
                minCandidate = doubleArray[i][index];
            }
            if (maxData < maxCandidate || maxData == 0.0) {
                maxData = maxCandidate;
            }
            if (minData > minCandidate || minData == 0.0) {
                minData = minCandidate;
            }
        }

        return new double[]{maxData, minData};
    }

    /**
     * 合并两个float数组中的最大值变成一个
     *
     * @param data1 数组1
     * @param data2 数组2
     * @return
     */
    public static float[] floatMergeMax(float[] data1, float[] data2) {
        if (data1 == null) {
            return data2;
        }
        if (data2 == null) {
            return data1;
        }
        if (data1.length != data2.length) {
            throw new RuntimeException("数据维度不一致!");
        }
        int length = data1.length;
        float[] result = new float[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.max(data1[i], data2[i]);
        }
        return result;
    }

    /**
     * 合并两个float数组中的最小值变成一个
     *
     * @param data1 数组1
     * @param data2 数组2
     * @return
     */
    public static float[] floatMergeMin(float[] data1, float[] data2) {
        if (data1 == null) {
            return data2;
        }
        if (data2 == null) {
            return data1;
        }
        if (data1.length != data2.length) {
            throw new RuntimeException("数据维度不一致!");
        }
        int length = data1.length;
        float[] result = new float[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.min(data1[i], data2[i]);
        }
        return result;
    }

    /**
     * 合并两个double数组中的最大值变成一个
     *
     * @param data1 数组1
     * @param data2 数组2
     * @return
     */
    public static double[] doubleMergeMax(double[] data1, double[] data2) {
        if (data1 == null) {
            return data2;
        }
        if (data2 == null) {
            return data1;
        }
        if (data1.length != data2.length) {
            throw new RuntimeException("数据维度不一致!");
        }
        int length = data1.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.max(data1[i], data2[i]);
        }
        return result;
    }

    /**
     * 合并两个double数组中的最小值变成一个
     *
     * @param data1 数组1
     * @param data2 数组2
     * @return
     */
    public static double[] doubleMergeMin(double[] data1, double[] data2) {
        if (data1 == null) {
            return data2;
        }
        if (data2 == null) {
            return data1;
        }
        if (data1.length != data2.length) {
            throw new RuntimeException("数据维度不一致!");
        }
        int length = data1.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.min(data1[i], data2[i]);
        }
        return result;
    }

    /**
     * object 转 double
     *
     * @param data
     * @return
     */
    public static double[] transformToDouble(Object data) {
        if (data == null) {
            return null;
        }
        if (data instanceof double[]) {
            return (double[]) data;
        } else if (data instanceof float[]) {
            return NumberUtils.transformFloatToDouble((float[]) data);
        } else if (data instanceof char[]) {
            return NumberUtils.transformCharToDouble((char[]) data);
        } else if (data instanceof short[]) {
            return NumberUtils.transformShortToDouble((short[]) data);
        } else if (data instanceof int[]) {
            return NumberUtils.transformIntToDouble((int[]) data);
        } else if (data instanceof long[]) {
            return NumberUtils.transformLongToDouble((long[]) data);
        }
        return null;
    }

    /**
     * object 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformToDoubleTwoRank(Object data) {
        if (data == null) {
            return null;
        }
        if (data instanceof double[][]) {
            return (double[][]) data;
        } else if (data instanceof float[][]) {
            return NumberUtils.transformFloatToDouble((float[][]) data);
        } else if (data instanceof char[][]) {
            return NumberUtils.transformCharToDouble((char[][]) data);
        } else if (data instanceof short[][]) {
            return NumberUtils.transformShortToDouble((short[][]) data);
        } else if (data instanceof int[][]) {
            return NumberUtils.transformIntToDouble((int[][]) data);
        } else if (data instanceof long[][]) {
            return NumberUtils.transformLongToDouble((long[][]) data);
        } else if (data instanceof byte[][]) {
            return NumberUtils.transformByteToDouble((byte[][]) data);
        }
        return null;
    }

    /**
     * float 转 double
     *
     * @param data
     * @return
     */
    public static double[] transformFloatToDouble(float[] data) {
        if (data == null) {
            return null;
        }
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i];
        }
        return result;
    }

    /**
     * float 2维 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformFloatToDouble(float[][] data) {
        if (data == null) {
            return null;
        }
        double[][] result = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                result[i][j] = data[i][j];
            }
        }
        return result;
    }

    /**
     * short 转 double
     *
     * @param data
     * @return
     */
    public static double[] transformShortToDouble(short[] data) {
        if (data == null) {
            return null;
        }
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i];
        }
        return result;
    }


    /**
     * short 2维 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformShortToDouble(short[][] data) {
        if (data == null) {
            return null;
        }
        double[][] result = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                result[i][j] = data[i][j];
            }
        }
        return result;
    }

    /**
     * char一维数组 转 double一维数组
     *
     * @param data
     * @return
     */
    public static double[] transformCharToDouble(char[] data) {
        if (data == null) {
            return null;
        }
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i];
        }
        return result;
    }

    /**
     * char 2维 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformCharToDouble(char[][] data) {
        if (data == null) {
            return null;
        }
        double[][] result = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                result[i][j] = data[i][j];
            }
        }
        return result;
    }

    /**
     * int一维数组 转 double一维数组
     *
     * @param data
     * @return
     */
    public static double[] transformIntToDouble(int[] data) {
        if (data == null) {
            return null;
        }
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i];
        }
        return result;
    }

    /**
     * int 2维 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformIntToDouble(int[][] data) {
        if (data == null) {
            return null;
        }
        double[][] result = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                result[i][j] = data[i][j];
            }
        }
        return result;
    }

    /**
     * long一维数组 转 double一维数组
     *
     * @param data
     * @return
     */
    public static double[] transformLongToDouble(long[] data) {
        if (data == null) {
            return null;
        }
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i];
        }
        return result;
    }

    /**
     * long 2维 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformLongToDouble(long[][] data) {
        if (data == null) {
            return null;
        }
        double[][] result = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                result[i][j] = data[i][j];
            }
        }
        return result;
    }
    /**
     * long 2维 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformByteToDouble(byte[][] data) {
        if (data == null) {
            return null;
        }
        double[][] result = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                result[i][j] = data[i][j];
            }
        }
        return result;
    }

    /**
     * short 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformShortToDouble(short[][][] data, NcDataModel ncDataModel) {
        if (data == null) {
            return null;
        }
        double scaleFactor = ncDataModel.getScaleFactor();
        double addOffset = ncDataModel.getAddOffset();
        double[][] result = new double[data[0].length][data[0][0].length];
        for (int i = 0; i < data[0].length; i++) {
            for (int j = 0; j < data[0][0].length; j++) {
                result[i][j] = Double.valueOf(data[0][i][j]) * scaleFactor + addOffset;
            }
        }
        return result;
    }

    /**
     * short 转 double
     *
     * @param data
     * @return
     */
    public static double[][] transformShortToDouble(short[][][][] data) {
        if (data == null) {
            return null;
        }
        double[][] result = new double[data[0][0].length][data[0][0][0].length];
        for (int i = 0; i < data[0][0].length; i++) {
            for (int j = 0; j < data[0][0][0].length; j++) {
                result[i][j] = Double.valueOf(data[0][0][i][j]);
            }
        }
        return result;
    }

    /**
     * 获取数组中的最小值
     *
     * @param data
     * @return
     */
    public static double minDoubleArray(double[] data) {
        double min = 999999;
        for (int index = 0; index < data.length; index++) {
            if ((data[index] == -999 || data[index] == -9999 || data[index] > 999999 || data[index] == 9999 || data[index] < -99999 || Double.isNaN(data[index]))) {
                continue;
            }
            // 判断数组元素的最小值
            if (data[index] < min) {
                // 把最小值存储Min变量
                min = data[index];
            }
        }
        return min;
    }


    /**
     * 获取数组中的最小值
     *
     * @param data
     * @return
     */
    public static short minShortArray(short[] data) {
        short min = Short.MAX_VALUE;
        for (int index = 0; index < data.length; index++) {
            if (data[index] == Short.MIN_VALUE || data[index] == Short.MAX_VALUE || data[index] == Short.MIN_VALUE + 1 || data[index] == Short.MAX_VALUE - 2) {
                continue;
            }
            // 判断数组元素的最小值
            if (data[index] < min) {
                // 把最小值存储Min变量
                min = data[index];
            }
        }
        return min;
    }

    /**
     * 获取数组中的最小值
     *
     * @param data
     * @return
     */
    public static double maxDoubleArray(double[] data) {
        double max = -999999;
        for (int index = 0; index < data.length; index++) {
            if ((data[index] == -999.0 || data[index] == -9999.0 || data[index] > 999999.0 || data[index] == 9999.0 || data[index] < -99999.0 || Double.isNaN(data[index]))) {
                continue;
            }
            // 判断数组元素的最小值
            if (data[index] > max) {
                // 把最大值存储Min变量
                max = data[index];
            }
        }
        return max;
    }

    /**
     * 获取数组中的最小值
     *
     * @param data
     * @return
     */
    public static short maxShortArray(short[] data) {
        short max = Short.MIN_VALUE;
        for (int index = 0; index < data.length; index++) {
            if (data[index] == Short.MIN_VALUE || data[index] == Short.MAX_VALUE || data[index] == Short.MIN_VALUE + 1 || data[index] == Short.MAX_VALUE - 2) {
                continue;
            }
            // 判断数组元素的最小值
            if (data[index] > max) {
                // 把最大值存储Min变量
                max = data[index];
            }
        }
        return max;
    }

    /**
     * @param num
     * @return
     */
    public static String scienceD(double num) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        //设置保留多少为小数
        nf.setMaximumFractionDigits(5);
        //取消科学计数法
        nf.setGroupingUsed(false);


        return nf.format(num);
    }

    /**
     * Double 数组转 double 数组
     *
     * @param arr
     * @return
     */
    public static double[] convert2doubleArray(Double[] arr) {
        if (ArrayUtils.isNotEmpty(arr)) {
            return Arrays.stream(arr)
                    .filter(val -> val != null)
                    .mapToDouble(Double::doubleValue)
                    .toArray();
        }
        return null;
    }

    /**
     * Double 数组转 double 数组
     *
     * @param arr
     * @return
     */
    public static int[] convert2IntArray(Integer[] arr) {
        if (ArrayUtils.isNotEmpty(arr)) {
            return Arrays.stream(arr)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .toArray();
        }
        return null;
    }


    /**
     * 2维数组行转列
     *
     * @param arr
     * @return
     */
    public static double[][] reversal2doubleArray(double[][] arr) {
        if (arr == null) {
            return null;
        }
        double[][] data = new double[arr[0].length][arr.length];
        for (int i = 0; i < arr[0].length; i++) {
            for (int j = 0; j < arr.length; j++) {
                data[i][j] = arr[j][i];
            }
        }
        return data;
    }

    /**
     * double 数组转 Double 数组
     *
     * @param arr
     * @return
     */
    public static Double[] convert2doubleArray(double[] arr) {
        return ArrayUtils.toObject(arr);
    }

    public static Boolean numberMaxEqualsCompare(Object a, String b) {
        if (a instanceof Double) {
            Double aDouble = (Double) a;
            Double bDouble = Double.valueOf(b);
            return aDouble >= bDouble;
        } else if (a instanceof Integer) {
            Integer aInteger = (Integer) a;
            Integer bInteger = Integer.valueOf(b);
            return aInteger >= bInteger;
        } else if (a instanceof Float) {
            Float aFloat = (Float) a;
            Float bFloat = Float.valueOf(b);
            return aFloat >= bFloat;
        } else if (a instanceof Short) {
            Short aShort = (Short) a;
            Short bShort = Short.valueOf(b);
            return aShort >= bShort;
        }
        return false;
    }

    public static Boolean numberMaxCompare(Object a, String b) {
        if (a instanceof Double) {
            Double aDouble = (Double) a;
            Double bDouble = Double.valueOf(b);
            return aDouble > bDouble;
        } else if (a instanceof Integer) {
            Integer aInteger = (Integer) a;
            Integer bInteger = Integer.valueOf(b);
            return aInteger > bInteger;
        } else if (a instanceof Float) {
            Float aFloat = (Float) a;
            Float bFloat = Float.valueOf(b);
            return aFloat > bFloat;
        } else if (a instanceof Short) {
            Short aShort = (Short) a;
            Short bShort = Short.valueOf(b);
            return aShort > bShort;
        }
        return false;
    }

    public static Boolean numberMinEqualsCompare(Object a, String b) {
        if (a instanceof Double) {
            Double aDouble = (Double) a;
            Double bDouble = Double.valueOf(b);
            return aDouble <= bDouble;
        } else if (a instanceof Integer) {
            Integer aInteger = (Integer) a;
            Integer bInteger = Integer.valueOf(b);
            return aInteger <= bInteger;
        } else if (a instanceof Float) {
            Float aFloat = (Float) a;
            Float bFloat = Float.valueOf(b);
            return aFloat <= bFloat;
        } else if (a instanceof Short) {
            Short aShort = (Short) a;
            Short bShort = Short.valueOf(b);
            return aShort <= bShort;
        }
        return false;
    }

    public static Boolean numberMinCompare(Object a, String b) {
        if (a instanceof Double) {
            Double aDouble = (Double) a;
            Double bDouble = Double.valueOf(b);
            return aDouble < bDouble;
        } else if (a instanceof Integer) {
            Integer aInteger = (Integer) a;
            Integer bInteger = Integer.valueOf(b);
            return aInteger < bInteger;
        } else if (a instanceof Float) {
            Float aFloat = (Float) a;
            Float bFloat = Float.valueOf(b);
            return aFloat < bFloat;
        } else if (a instanceof Short) {
            Short aShort = (Short) a;
            Short bShort = Short.valueOf(b);
            return aShort < bShort;
        }
        return false;
    }

    /**
     * 数组翻转
     *
     * @param ncDoubleTrue
     * @return
     */
    public static double[] toArrayReversal(double[] ncDoubleTrue, Integer oldXSize, Integer oldYSize) {
        double[] reversal = new double[ncDoubleTrue.length];
        for (int i = 0; i < oldXSize; i++) {
            for (int j = 0; j < oldYSize; j++) {
                reversal[j * oldXSize + i] = ncDoubleTrue[i * oldYSize + j];
            }
        }
        return reversal;
    }

    /**
     * 根据最大最小和间隔 切分数据
     *
     * @param min
     * @param max
     * @param interval
     * @return
     */
    public static double[] splitByIntervalFromMinToMax(double min, double max, double interval) {
        int size = (int) ((max - min) / interval);
        double[] data = new double[size + 1];
        System.out.println(size + 1);
        for (int i = 0; i < size + 1; i++) {
            data[i] = i * interval + min;
        }
        return data;
    }


    /**
     * 线性插值算法
     *
     * @param oldInterval 旧的间隔
     * @param newInterval 新的间隔
     * @param sourceData  原始数据
     * @return 插值以后的数据
     */
    public static double[] linearInterpolation(double[] oldInterval, double[] newInterval, double[] sourceData) {
        int newIndex = 1;
        double[] interpolateData = new double[newInterval.length];
        double lastData = sourceData[0];
        double lastInterval = oldInterval[0];
        // 正序还是逆序
        Boolean isAsc = oldInterval[1] > oldInterval[0];
        interpolateData[0] = sourceData[0];
        for (int oldIndex = 1; oldIndex < oldInterval.length && newIndex < newInterval.length; ) {
            if (isAsc) {
                if (oldInterval[oldIndex] >= newInterval[newIndex]) {
                    double thisPointData = sourceData[oldIndex];
                    double newData = 0.0;
                    if (lastData == inputInvalidValue || thisPointData == inputInvalidValue) {
                        newData = inputInvalidValue;
                    } else {
                        newData = lastData + (thisPointData - lastData) * ((newInterval[newIndex] - lastInterval) / (oldInterval[oldIndex] - lastInterval));
                    }
                    interpolateData[newIndex] = newData;
                    newIndex++;
                } else {
                    lastData = sourceData[oldIndex];
                    lastInterval = oldInterval[oldIndex];
                    oldIndex++;
                }
            } else {
                if (oldInterval[oldIndex] <= newInterval[newIndex]) {
                    double thisPointData = sourceData[oldIndex];
                    double newData = 0.0;
                    if (lastData == inputInvalidValue || thisPointData == inputInvalidValue) {
                        newData = inputInvalidValue;
                    } else {
                        newData = lastData + (thisPointData - lastData) * ((newInterval[newIndex] - lastInterval) / (oldInterval[oldIndex] - lastInterval));
                    }
                    interpolateData[newIndex] = newData;
                    newIndex++;
                } else {
                    lastData = sourceData[oldIndex];
                    lastInterval = oldInterval[oldIndex];
                    oldIndex++;
                }
            }
        }
        if (newInterval[newInterval.length - 1] == oldInterval[oldInterval.length - 1]) {
            interpolateData[newInterval.length - 1] = sourceData[oldInterval.length - 1];
        }
        return interpolateData;
    }

    /**
     * 线性插值算法
     *
     * @param data         数据
     * @param newResolving 新的分辨率
     * @return 插值以后的数据
     */
    public static double[] linearResolvingResize(double[] data, double newResolving) {
        int newIndex = 0;
        double oldResolving = Math.abs(data[1] - data[0]);
        // 代表不需要新的分辨率划分
        if (oldResolving == newResolving) {
            return data;
        }
        // 需要新的分辨率划分
        double max = maxDoubleArray(data);
        double min = minDoubleArray(data);
        int size = (int) ((max - min) / newResolving);
        double[] reSizeData = new double[size + 1];
        if (data[1] - data[0] > 0) {
            for (int i = 0; i < size + 1; i++) {
                reSizeData[i] = data[0] + i * newResolving;
            }
        } else {
            for (int i = 0; i < size + 1; i++) {
                reSizeData[i] = data[0] - i * newResolving;
            }
        }
        return reSizeData;
    }


    /**
     * 线性插值算法
     *
     * @param oldInterval 旧的间隔
     * @param newInterval 新的间隔
     * @param sourceData  原始数据
     * @return 插值以后的数据
     */
    public static double[] linearDecimation(double[] oldInterval, double[] newInterval, double[] sourceData) {
        int newIndex = 0;
        double[] decimationData = new double[newInterval.length];

        double step = Math.abs(oldInterval[1] - oldInterval[0]);
        for (int oldIndex = 0; oldIndex < oldInterval.length && newIndex < newInterval.length; oldIndex++) {
            if (Math.abs(oldInterval[oldIndex] - newInterval[newIndex]) <= step) {
                decimationData[newIndex] = sourceData[oldIndex];
                newIndex++;
            }
        }
        return decimationData;
    }

    public static void main(String[] args) throws Exception {
        double[] oldData = splitByIntervalFromMinToMax(100, 10, -1);
        for (int i = 0; i < oldData.length; i++) {
            System.out.println(oldData[i]);
        }
        System.out.println("修改以后的");
        double[] resolvingResize = linearResolvingResize(oldData, 20);
        for (int i = 0; i < resolvingResize.length; i++) {
            System.out.println(resolvingResize[i]);
        }
//        MetaInfo metaInfo = BinUtils.readHdr("D:\\602\\data\\602\\AVGAT\\1979\\01\\01\\SURF\\AVGAT_1979_01_01_SURF.hdr");
//
//        double max = 60 + (0.5 * (metaInfo.getLines() - 1));
//        double min = 60;
//        double interval = 0.8;
//        double[] newInterval = splitByIntervalFromMinToMax(min, max, interval);
//        double[] oldInterval = splitByIntervalFromMinToMax(min, max, 0.5);
//        double[][] pointData = BinUtils.readBinForDouble("D:\\602\\data\\602\\AVGAT\\1979\\01\\01\\SURF\\AVGAT_1979_01_01_SURF.bin", metaInfo.getLines(), metaInfo.getSamples(), metaInfo.getDataType());
//        for (double[] aFloat : pointData) {
//            for (double v : aFloat) {
//                //System.out.println(v);
//            }
//        }
//        for (int i = 0; i < oldInterval.length; i++) {
//            //System.out.println(oldInterval[i]);
//        }
//        for (int i = 0; i < newInterval.length; i++) {
//            //System.out.println(newInterval[i]);
//        }
//        double[] linearInterpolation = linearDecimation(oldInterval, newInterval, pointData[0]);
//        for (int i = 0; i < linearInterpolation.length; i++) {
//            System.out.println(linearInterpolation[i]);
//        }
    }

    /**
     * 二维数据转一维数据
     *
     * @param data
     * @return
     */
    public static double[] twoRankToOneRank(double[][] data) {
        if (data == null) {
            return null;
        }
        double[] oneRankData = new double[data.length * data[0].length];
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                oneRankData[index] = data[i][j];
                index++;
            }
        }
        return oneRankData;
    }

    /**
     * 二维数据转一维数据
     *
     * @param data
     * @return
     */
    public static double[] twoRankToOneRank(Double[][] data) {
        if (data == null) {
            return null;
        }
        double[] oneRankData = new double[data.length * data[0].length];
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                oneRankData[index] = data[i][j];
                index++;
            }
        }
        return oneRankData;
    }

    /**
     * 逆转数组
     *
     * @param data
     * @return
     */
    public static double[] inverseArray(double[] data) {
        // 数组逆转
        double[] inverse = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            inverse[data.length - i - 1] = data[i];
        }
        return inverse;
    }

}
