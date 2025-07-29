package com.example;

import com.geovis.tools.json.JsonUtils;
import com.geovis.tools.png.PngUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import ucar.ma2.Array;
import ucar.nc2.Dimension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: Gengfangdong
 * @Description:
 * @FileName: NcDataModel
 * @Date: 2022/12/10 17:45
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class NcDataModel {
    /**
     * 要素名
     */
    String variableName;
    /**
     * 数据对象
     */
    Array dataArray;
    /**
     * u数据
     */
    Array uDataArray;
    /**
     * v 数据
     */
    Array vDataArray;
    /**
     * 是否为风
     */
    Boolean isWind;

    /**
     * 维度
     */
    int rank;
    /**
     * 无效填充值
     */
    double fillValue = Double.MAX_VALUE;
    /**
     * 缩放倍数
     */
    double scaleFactor = 1.0;
    /**
     * 偏移量
     */
    double addOffset = 0.0;

    String unit;
    /**
     * 维度名称 顺序
     */
    List<String> dimensionList;
    /**
     * 所有的维度
     */
    Map<String, Dimension> dimensionMap;


    /**
     * 是否需要反转
     */
    boolean needReversal = false;
    /**
     * 维度名称
     */
    String latName;

    /**
     * 经度名称
     */
    String lonName;

    /**
     * 读取第一个数
     *
     * @return
     */
    public double readFirst() {
        return NumberUtils.transformToDouble(dataArray.copyTo1DJavaArray())[0];
    }

    /**
     * 转换为2维数组
     * 第一维为lat 第二维为lon
     *
     * @return
     */
    public double[][] convertDouble2Array(Array array) {
        // 判断需要需要翻转
        checkReversal();
        if (rank != 2) {

        }
        double[][] twoRank = NumberUtils.transformToDoubleTwoRank(array.reduce().copyToNDJavaArray());
        for (int i = 0; i < twoRank.length; i++) {
            for (int j = 0; j < twoRank[0].length; j++) {
                if (twoRank[i][j] != fillValue) {
                    twoRank[i][j] = twoRank[i][j] * scaleFactor + addOffset;
                } else {
                    twoRank[i][j] = NumberUtils.inputInvalidValue;
                }
            }
        }
        if (needReversal) {
            return NumberUtils.reversal2doubleArray(twoRank);
        } else {
            return twoRank;
        }
    }


    /**
     * 转换为1维数组
     *
     * @return
     */
    public double[] convertDoubleArray() {
        // 判断需要需要翻转
        checkReversal();
        if (rank != 1) {
        }
        double[] rank = NumberUtils.transformToDouble(dataArray.reduce().copyTo1DJavaArray());
        for (int i = 0; i < rank.length; i++) {
            if (rank[i] != fillValue) {
                rank[i] = rank[i] * scaleFactor + addOffset;
            } else {
                rank[i] = NumberUtils.inputInvalidValue;
            }
        }
        return rank;
    }

    /**
     * 校验是否需要反转
     * 默认应该为 纬度在前 经度在后
     */
    public void checkReversal() {
        int latIndex = 0;
        int lonIndex = 0;
        for (int i = 0; i < dimensionList.size(); i++) {
            String dimensionName = dimensionList.get(i);
            if (StringUtils.equalsAnyIgnoreCase(dimensionName, "lat", "latitude", "lat_0", "eta_rho", "x", "null", "south_north", "YC")) {
                latIndex = i;
//                if (StringUtils.equalsAnyIgnoreCase(dimensionName, "eta_rho")) {
//                    latName = "lat_rho";
//                } else
                if (StringUtils.equalsAnyIgnoreCase(dimensionName, "x", "null")) {
                    latName = "Latitude";
                } else if (StringUtils.equalsAnyIgnoreCase(dimensionName, "south_north")) {
                    latName = "XLAT";
                } else {
                    latName = dimensionName;
                }
            }
            if (StringUtils.equalsAnyIgnoreCase(dimensionName, "lon", "longitude", "lon_0", "xi_rho", "y", "null", "west_east", "XC")) {
                lonIndex = i;
//                if (StringUtils.equalsAnyIgnoreCase(dimensionName, "xi_rho")) {
//                    lonName = "lon_rho";
//                } else
                if (StringUtils.equalsAnyIgnoreCase(dimensionName, "y", "null")) {
                    lonName = "Longitude";
                } else if (StringUtils.equalsAnyIgnoreCase(dimensionName, "west_east")) {
                    lonName = "XLONG";
                } else {
                    lonName = dimensionName;
                }
            }
        }
        if (latIndex > lonIndex) {
            needReversal = true;
        }
    }

    public String getLatName() {
        checkReversal();
        return latName;
    }

    public String getLonName() {
        checkReversal();
        return lonName;
    }


    /**
     * 直接转png
     *
     * @param path
     */
    public void toPng(String path, double[] lat, double[] lon, boolean fromLeft, boolean fromBottom) {
        double[][] data = convertDouble2Array(dataArray);
        // 获取最小值
        double latMin = NumberUtils.minDoubleArray(lat);
        double lonMin = NumberUtils.minDoubleArray(lon);
        // 获取最大值
        double latMax = NumberUtils.maxDoubleArray(lat);
        double lonMax = NumberUtils.maxDoubleArray(lon);
        // 写入到json
        int width = lon.length;
        int height = lat.length;

        double lonStep = 0.0;
        if (lon.length > 1) {
            lonStep = lon[1] - lon[0];
        }

        double latStep = 0.0;
        if (lat.length > 1) {
            latStep = lat[1] - lat[0];
        }
        double min = 999999;
        double max = -999999;
        for (int i = 0, count = data.length; i < count; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == -999 || data[i][j] <= -998 || data[i][j] == 999999 || data[i][j] == -9999 || data[i][j] > 999999 || data[i][j] == 9999 || data[i][j] == NumberUtils.inputInvalidValue || data[i][j] == fillValue) {
                    data[i][j] = NumberUtils.inputInvalidValue;
                    continue;
                }
                if (min > data[i][j]) {
                    min = data[i][j];
                }
                if (max < data[i][j]) {
                    max = data[i][j];
                }
            }
        }
        max = Double.parseDouble(NumberUtils.scienceD(max));
        min = Double.parseDouble(NumberUtils.scienceD(min));

        JsonUtils.writeJsonToFile(path.replace(".png", ".json"), String.valueOf(min), String.valueOf(max), String.valueOf(width), String.valueOf(height), String.valueOf(lonMin), String.valueOf(latMin), String.valueOf(lonMax), String.valueOf(latMax),
                String.valueOf(Math.abs(lonStep)), String.valueOf(Math.abs(latStep)), String.valueOf(unit != null ? unit : ""));
        if (fromLeft && fromBottom) {
            // 从左下角开始渲染
            PngUtils.writeDataToPngFromLeftBottom(path, data, data[0].length, data.length);
        } else if (fromLeft && !fromBottom) {
            // 从左上角开始渲染
            PngUtils.writeDataToPngFromLeftTop(path, data, data[0].length, data.length);
        } else if (!fromLeft && fromBottom) {
            // 从右下角开始渲染
            //  暂时没有实现方法
        } else if (!fromLeft && !fromBottom) {
            // 从右上角开始渲染
            //  暂时没有实现方法
        }
    }

    /**
     * 直接转png
     *
     * @param path
     */
    public void toWindPng(String path, double[] lat, double[] lon) {
        double[][] uData = convertDouble2Array(uDataArray);
        double[][] vData = convertDouble2Array(vDataArray);
        // 获取最小值
        double latMin = NumberUtils.minDoubleArray(lat);
        double lonMin = NumberUtils.minDoubleArray(lon);
        // 获取最大值
        double latMax = NumberUtils.maxDoubleArray(lat);
        double lonMax = NumberUtils.maxDoubleArray(lon);
        // 写入到json
        int width = lon.length;
        int height = lat.length;

        double lonStep = 0.0;
        if (lon.length > 1) {
            lonStep = lon[1] - lon[0];
        }

        double latStep = 0.0;
        if (lat.length > 1) {
            latStep = lat[1] - lat[0];
        }
        double uMin = 999999;
        double uMax = -999999;
        double[] uPeek = peekValue(uData);
        uMin = Double.parseDouble(NumberUtils.scienceD(uPeek[0]));
        uMax = Double.parseDouble(NumberUtils.scienceD(uPeek[1]));

        double vMin = 999999;
        double vMax = -999999;
        double[] vPeek = peekValue(vData);
        vMin = Double.parseDouble(NumberUtils.scienceD(vPeek[0]));
        vMax = Double.parseDouble(NumberUtils.scienceD(vPeek[1]));
        // 写入风
        JsonUtils.writeJsonToFile(path.replace(".png", ".json"), String.valueOf(uMin), String.valueOf(vMin), String.valueOf(uMax), String.valueOf(vMax), String.valueOf(width), String.valueOf(height), String.valueOf(lonMin), String.valueOf(latMin), String.valueOf(lonMax), String.valueOf(latMax),
                String.valueOf(Math.abs(lonStep)), String.valueOf(Math.abs(latStep)), String.valueOf(unit != null ? unit : ""));
        // 从左下角开始渲染
        //PngUtils.writeUVDataToPngFromLeftBottom(path, uData, vData, uData[0].length, uData.length);
       // PngUtils.writeDataToPngFromLeftTop(path, data, data[0].length, data.length);
    }

    double[] peekValue(double[][] data) {
        double[] peek = new double[2];

        double min = 999999;
        double max = -999999;
        for (int i = 0, count = data.length; i < count; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == -999 || data[i][j] <= -998 || data[i][j] == 999999 || data[i][j] == -9999 || data[i][j] > 999999 || data[i][j] == 9999 || data[i][j] == NumberUtils.inputInvalidValue || data[i][j] == fillValue) {
                    data[i][j] = NumberUtils.inputInvalidValue;
                    continue;
                }
                if (min > data[i][j]) {
                    min = data[i][j];
                }
                if (max < data[i][j]) {
                    max = data[i][j];
                }
            }
        }
        peek[1] = max;
        peek[0] = min;
        return peek;
    }
}