package com.example;

import com.example.NumberUtils;
import com.example.NcDataModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.nc2.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Gengfangdong
 * @Description:
 * @FileName: NcReader
 * @Date: 2022/12/2 22:49
 * @Version: 1.0
 */
@Slf4j
public class NcReader {

    /**
     * 要素列表转map key为shortname value为要素
     *
     * @param filePath 要素列表
     * @return
     */
    public static Map<String, Variable> variablesTranToShortNameVariableMap(String filePath) throws Exception {
        try (NetcdfFile netcdfFile = NetcdfFile.open(filePath);) {
            return netcdfFile.getVariables().stream().collect(Collectors.toMap(CDMNode::getShortName, item -> item));
        }
    }

    /**
     * 要素列表转map key为shortname value为要素
     *
     * @param variableList 要素列表
     * @return
     */
    public static Map<String, Variable> variablesTranToShortNameVariableMap(List<Variable> variableList) {
        return variableList.stream().collect(Collectors.toMap(CDMNode::getShortName, item -> item));
    }

    /**
     * 从指定nc文件中读取指定要素的一维数据
     * 如果为多维的话也会转换为1维数据
     *
     * @param variableName 要素名
     * @param filePath     文件路径
     * @return
     * @throws Exception
     */
    public static double[] readNcDouble(String variableName, String filePath) throws Exception {
        return readNcDouble(variableName, filePath, null, null);
    }

    /**
     * 读取文件下这个要素的指定范围的数据
     *
     * @param variableName
     * @param filePath
     * @param org
     * @param sha
     * @return
     * @throws Exception
     */
    public static double[] readNcDouble(String variableName, String filePath, int[] org, int[] sha) throws Exception {
        if (!new File(filePath).exists()) {
            throw new FileNotFoundException("文件 " + filePath + "不存在!");
        }
        try (NetcdfFile dataSet = NetcdfFile.open(filePath);) {
            List<Variable> variables = dataSet.getVariables();
            Variable variable = null;
            for (Variable item : variables) {
                if (StringUtils.equals(item.getShortName(), variableName)) {
                    variable = item;
                    break;
                }
            }
            if (variable != null) {
                return readNcDoubleTrue(variable, org, sha);
            }
            return null;
        }
    }


    /**
     * 从要素中提取一维double一维数组 如果要素为空 返回空
     *
     * @param variable 要素
     * @return
     * @throws Exception
     */
    public static double[] readNcDoubleFromVariable(Variable variable) throws Exception {
        return readNcDoubleFromVariableAndRange(variable, null, null);
    }

    /**
     * 读取指定范围的nc数据 并且输出为double 一维数组
     *
     * @param variable 要素
     * @param org      起始
     * @param sha      终止
     * @return
     */
    public static double[] readNcDoubleFromVariableAndRange(Variable variable, int[] org, int[] sha) throws Exception {
        if (variable != null) {
            return readNcDoubleTrue(variable, org, sha);
        }
        return null;
    }

    /**
     * 获取真实的数据 数据会经历 data * scaleFactor + addOffset
     *
     * @param variable
     * @param org
     * @param sha
     * @return
     */
    public static NcDataModel readNcDataTrue(Variable variable, int[] org, int[] sha) {
        try {
            if (variable != null) {
                // 获取填充值
                List<Attribute> attributes = variable.getAttributes();
                // 读取数据
                double fillValue = Double.MAX_VALUE;
                // 获取填充值
                for (Attribute attribute : attributes) {
                    String shortName = attribute.getShortName();
                    if (StringUtils.equals(shortName, "_FillValue")) {
                        fillValue = getVariableAttributeValue(attribute, fillValue);
                    }
                }
                // 获取倍数
                double scaleFactor = 1.0;
                // 获取填充值
                for (Attribute attribute : attributes) {
                    String shortName = attribute.getShortName();
                    if (StringUtils.equals(shortName, "scale_factor")) {
                        scaleFactor = getVariableAttributeValue(attribute, scaleFactor);
                    }
                }
                // 获取偏移
                double addOffset = 0.0;
                // 获取填充值
                for (Attribute attribute : attributes) {
                    String shortName = attribute.getShortName();
                    if (StringUtils.equals(shortName, "add_offset")) {
                        addOffset = getVariableAttributeValue(attribute, addOffset);
                    }
                }
                // 获取维度
                int rank = variable.getRank();
                // 填充维度
                List<Dimension> dimensions = variable.getDimensions();
                Map<String, Dimension> dimensionMap = new HashMap<>();
                List<String> dimensionList = new LinkedList<>();
                for (Dimension dimension : dimensions) {
                    dimensionMap.put(dimension.getShortName(), dimension);
                    dimensionList.add(dimension.getShortName());
                }
                // 起始维度数组 要素读取数据的大小
                Array array = null;
                if (org != null && sha != null) {
                    if (rank != org.length || rank != sha.length) {
                        throw new RuntimeException("请求数据维度不正确!");
                    }
                    array = variable.read(org, sha);
                } else {
                    array = variable.read();
                }
                String unit = variable.getUnitsString();
                NcDataModel ncDataModel = new NcDataModel();
                ncDataModel.setDataArray(array)
                        .setUnit(unit)
                        .setAddOffset(addOffset)
                        .setDimensionList(dimensionList)
                        .setRank(rank)
                        .setFillValue(fillValue)
                        .setScaleFactor(scaleFactor)
                        .setDimensionMap(dimensionMap)
                        .setVariableName(variable.getShortName());
                return ncDataModel;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("读取要素:{} 数据失败!", variable.getShortName());
        }
        return null;
    }

    /**
     * 获取真实的数据 数据会经历 data * scaleFactor + addOffset
     *
     * @param variable
     * @param org
     * @param sha
     * @return
     */
    public static double[] readNcDoubleTrue(Variable variable, int[] org, int[] sha) {
        try {
            if (variable != null) {
                NcDataModel ncDataModel = readNcDataTrue(variable, org, sha);
                if (ncDataModel != null) {
                    // 1维数据
                    Object objectArray = ncDataModel.getDataArray().copyTo1DJavaArray();
                    if (objectArray != null) {
                        double[] transform = NumberUtils.transformToDouble(objectArray);
                        double[] result = new double[transform.length];
                        for (int i = 0; i < transform.length; i++) {
                            if (transform[i] == (double) ncDataModel.getFillValue()) {
                                result[i] = NumberUtils.inputInvalidValue;
                            } else {
                                result[i] = transform[i] * ncDataModel.getScaleFactor() + ncDataModel.getAddOffset();
                            }
                        }
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取描述的数据信息
     *
     * @param attribute
     * @param defaultValue
     * @return
     */
    public static double getVariableAttributeValue(Attribute attribute, double defaultValue) {
        double value = defaultValue;
        DataType dataType = attribute.getDataType();
        if (DataType.SHORT.equals(dataType)) {
            value = (short) attribute.getValue(0);
        }
        if (DataType.DOUBLE.equals(dataType)) {
            value = (double) attribute.getValue(0);
        }
        if (DataType.FLOAT.equals(dataType)) {
            value = (float) attribute.getValue(0);
        }
        if (DataType.INT.equals(dataType)) {
            value = (int) attribute.getValue(0);
        }
        if (DataType.LONG.equals(dataType)) {
            value = (long) attribute.getValue(0);
        }
        return value;
    }


    /**
     * 数据要素的单位
     *
     * @param variableName
     * @param filePath
     * @return
     */
    public static String getElementUnit(String variableName, String filePath) throws Exception {
        try (NetcdfFile dataSet = NetcdfFile.open(filePath);) {
            List<Variable> variables = dataSet.getVariables();
            Variable variable = null;
            for (Variable item : variables) {
                if (StringUtils.equals(item.getShortName(), variableName)) {
                    variable = item;
                    break;
                }
            }
            if (variable != null) {
                String result = variable.getUnitsString();

                if (result == null) {
                    result = "";
                }
                result = result.replace(" ", ".").replace("**", "");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断经纬度点是否需要反转数据，如果维度在前 不需要转换 经度在前需要转换
     *
     * @param variable        要素
     * @param lonVariableName 经度要素名
     * @param latVariableName 维度要素名
     */
    public static Boolean checkGeo2DNeedReversal(Variable variable, String latVariableName, String lonVariableName) {
        List<Dimension> dimensions = variable.getDimensions();
        Boolean latFirst = false;
        Boolean lonFirst = false;
        for (Dimension dimension : dimensions) {
            if (StringUtils.equalsAny(dimension.getShortName(), "lat", "order_lat_1", latVariableName)) {
                // 如果是维度
                if (!lonFirst) {
                    latFirst = true;
                }
                continue;
            } else if (StringUtils.equalsAny(dimension.getShortName(), "lon", "order_lon_2", lonVariableName)) {
                // 如果是经度
                if (!latFirst) {
                    lonFirst = true;
                }
                continue;
            }
        }
        return lonFirst;
    }
}