package com.example;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: Gengfangdong
 * @Description:
 * @FileName: NcToPngUtils
 * @Date: 2023/6/14 18:30
 * @Version: 1.0
 */
@Slf4j
public class NcToPngUtils {

    static final long SECONDS_FROM_1900_TO_1970 = 2208988800L;
    public static int minRank = 2;

    public static void main(String[] args) {
        List<NcBeanModel> ncBeanModelList = ncToPng("E:\\mnt\\data\\HQOH\\new1.nc", "E:\\mnt\\data\\HQOH\\1");
        for (NcBeanModel ncBeanModel : ncBeanModelList) {
            System.out.println(ncBeanModel);
        }
    }

    /**
     * nc 转 png
     * 所有的要素
     *
     * @param filePath
     * @param pngPath
     */
    public static List<NcBeanModel> ncToPng(String filePath, String pngPath) {
        List<NcBeanModel> ncBeanModelList = new ArrayList<>();
        try (NetcdfFile netcdfFile = NetcdfFile.open(filePath)) {
            // 获取要素
            List<Variable> variables = netcdfFile.getVariables();
            Map<String, Variable> variableMap = NcReader.variablesTranToShortNameVariableMap(variables);
            for (Map.Entry<String, Variable> variableEntry : variableMap.entrySet()) {
                // 简单过滤一下
                Variable variable = variableEntry.getValue();
                if (variable.getRank() >= minRank && !StringUtils.equalsAnyIgnoreCase(variable.getShortName(), "time")) {
                    try {
                        ncBeanModelList.addAll(variableToPng(pngPath, variable, variableMap));
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("要素:{},出图失败!", variable.getShortName());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ncBeanModelList;
    }

    /**
     * nc 转 png
     *
     * @param filePath
     * @param pngPath
     */
    public static List<NcBeanModel> ncToPng(String filePath, String pngPath, String variableName) {
        List<NcBeanModel> ncBeanModelList = new ArrayList<>();
        try (NetcdfFile netcdfFile = NetcdfFile.open(filePath)) {
            // 获取要素
            List<Variable> variables = netcdfFile.getVariables();
            Map<String, Variable> variableMap = NcReader.variablesTranToShortNameVariableMap(variables);
            // 获取要读取的要素
            Variable variable = variableMap.get(variableName);
            if (variable == null) {
                throw new RuntimeException("要素:" + variableName + "不存在");
            }
            ncBeanModelList.addAll(variableToPng(pngPath, variable, variableMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ncBeanModelList;
    }

    /**
     * variable 转 png
     *
     * @param pngPath
     * @param variable
     * @param variableMap
     */
    private static List<NcBeanModel> variableToPng(String pngPath, Variable variable, Map<String, Variable> variableMap) {
        List<NcBeanModel> ncBeanModelList = new ArrayList<>();
        String variableName = variable.getShortName();
        int rank = variable.getRank();
        // 要素维度小于2的话 不是 geo2D 直接剔除
        if (rank < minRank) {
            log.error("要素:{} 维度为:{},最少为2维!", variableName, rank);
            return Collections.emptyList();
        }
        // 读取经纬度数据
        if (rank == minRank) {
            // 如果为 2维 的 直接转png
            ncBeanModelList.addAll(variableToPng(variable, null, null, variableMap, pngPath, variableName, null, null));
        } else {
            deepBuildDimensionData(variableMap, variable.getDimensions(), 0, new ArrayList<>(), variableName, pngPath, variable, ncBeanModelList);
        }
        return ncBeanModelList;
    }

    /**
     * 获取当前层的所有节点数据
     *
     * @param variableMap     要素map
     * @param dimensionList   维度名称
     * @param layerIndex      当前维度层 layer
     * @param preOrg          读取起始位置
     * @param prefix          路径前缀
     * @param dataVariable    数据要素
     * @param filePath        文件路径
     * @param ncBeanModelList 存储bean
     * @return
     */
    private static void deepBuildDimensionData(Map<String, Variable> variableMap, List<Dimension> dimensionList, Integer layerIndex, List<Integer> preOrg, String prefix, String filePath, Variable dataVariable, List<NcBeanModel> ncBeanModelList) {
        if (layerIndex >= dimensionList.size()) {
            // 代表超过了 维度 直接返回
            return;
        }
        // 获取维度名称
        String shortName = dimensionList.get(layerIndex).getShortName();
        Variable variable = variableMap.get(shortName);
        double[] rasterData = null;
        if (variable != null) {
            // 如果要素不为空 解析数据
            rasterData = readDimensionData(variable, false);
        } else {
            if (StringUtils.equalsAny(shortName, "pressure")) {
                rasterData = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
            } else if (StringUtils.equalsAny(shortName, "single_lev")) {
                rasterData = new double[]{0};
            }
        }
        if (rasterData == null) {
            throw new RuntimeException(MessageFormatter.format("维度:{}不存在!", shortName).getMessage());
        }
        try {
            // 每一条数据
            for (int dimensionIndex = 0; dimensionIndex < rasterData.length; dimensionIndex++) {
                double dimensionItem = rasterData[dimensionIndex];
                // 复制list 下一层节点 读取数据的时候 前面所有层的前缀
                List<Integer> copyPreOrg = new CopyOnWriteArrayList<>();
                List<Integer> copyPreSha = new CopyOnWriteArrayList<>();
                for (Integer org : preOrg) {
                    copyPreOrg.add(org.intValue());
                    copyPreSha.add(1);
                }
                copyPreOrg.add(dimensionIndex);
                copyPreSha.add(1);
                // 当前节点的维度前缀
                String prefixNew = prefix + "_";
                Long time = null;
                if (StringUtils.equalsAnyIgnoreCase(shortName, "time")
                        || StringUtils.equalsAnyIgnoreCase(shortName, "valid_time")) {
                    time = new Double(dimensionItem).longValue();
                    log.info("时间为:{}", time);
//                    Instant instant;

//                    if (time < 1086999){
//                        instant = Instant.ofEpochSecond(time*3600 - SECONDS_FROM_1900_TO_1970);
//                    }else {
//                        instant = Instant.ofEpochSecond(time);
//                    }

//                    DateTimeFormatter formatter = DateTimeFormatter
//                            .ofPattern("yyyyMMdd_HH",Locale.CHINA)
//                            .withZone(ZoneId.of("UTC")); // 使用系统默认时区

                    //String formattedDate = formatter.format(instant);
                    String timeUnits = variable.findAttribute("units").toString();
                    String[] timeStrings = timeUnits.split(" ");
                    String Date = timeStrings[4];

                    DecimalFormat df = new DecimalFormat("00");
                    String timeString = df.format(time);
                    String formattedDate = Date + "_" + timeString;

                    prefixNew += formattedDate;
                } else {
                    prefixNew += String.valueOf(dimensionItem);
                }
                // 下一个维度的索引
                Integer next = layerIndex + 1;
                // 判断当层节点 这里多减1 是因为 从 0开始的
                if (layerIndex == dimensionList.size() - 3) {
                    try {
                        String level = String.valueOf(dimensionItem);
                        if (StringUtils.equals(shortName, "time")) {
                            level = null;
                        }
                        ncBeanModelList.addAll(variableToPng(dataVariable, copyPreOrg, copyPreSha, variableMap, filePath, prefixNew, level, time));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    deepBuildDimensionData(variableMap, dimensionList, next, copyPreOrg, prefixNew, filePath, dataVariable, ncBeanModelList);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param variable    要解析的要素
     * @param org         起始数据 int数组
     * @param sha         读取要素大小 int 数组
     * @param variableMap 要素map 用来读取纬度和经度
     * @param pngPath     要生成的png 路径
     * @param namePrefix  要生成的png 前缀 用于递归 初始为 文件名
     * @param level       层次
     * @return
     */
    private static List<NcBeanModel> variableToPng(Variable variable, List<Integer> org, List<Integer> sha, Map<String, Variable> variableMap, String pngPath, String namePrefix, String level, Long time) {
        String variableName = variable.getShortName();
        // 获取面的数据
        NcDataModel ncDataModel = getFaceData(variable, org, sha);
        if (ncDataModel == null) {
            throw new RuntimeException("要素:" + variableName + "读取失败!");
        }

        // 代码补丁----start
        // 温度类
//        if (StringUtils.equalsAnyIgnoreCase(variableName, "Temperature_isobaric")) {
//            ncDataModel.setAddOffset(-273.15);
//        }


        // 获取经纬度数据
        String latName = ncDataModel.getLatName();
        Variable latVariable = variableMap.get(latName);
        if (latVariable == null) {
            throw new RuntimeException(MessageFormatter.format("纬度:{}不存在!", latName).getMessage());
        }
        String lonName = ncDataModel.getLonName();
        Variable lonVariable = variableMap.get(lonName);
        if (lonVariable == null) {
            throw new RuntimeException(MessageFormatter.format("经度:{}不存在!", lonName).getMessage());
        }
        // 直接读取数据
        double[] lat = readDimensionData(latVariable, false);
        double[] lon = readDimensionData(lonVariable, true);

        // 判断图片渲染方式 ---- start
        // 默认从左下角开始图片渲染
        boolean fromBottom = true;
        boolean fromLeft = true;
        int latLength = lat.length;
        int lonLength = lon.length;
        // 维度从大到小，修改为从上向下开始渲染
        if (latLength > 2 && lat[0] > lat[1]) {
            fromBottom = false;
        }
        // 经度从大到小，修改为从右向左开始渲染
        if (lonLength > 2 && lon[0] > lon[1]) {
            fromLeft = false;
        }
        // 判断图片渲染方式 ---- end

        // 生产要素(包含U分量V分量)
        String toPngPath;
        List<NcBeanModel> ncBeanModelList = new ArrayList<>();
        String timeStr = "_" + new Date().getTime();
        {
            toPngPath = pngPath + File.separator + variableName + File.separator + namePrefix + timeStr + ".png";
            // log.info("开始输出要素:{},红黑图:{}", variableName, toPngPath);
            toPngPath = toPngPath.replace("/", File.separator).replace("\\", File.separator);
            ncDataModel.toPng(toPngPath, lat, lon, fromLeft, fromBottom);
            ncBeanModelList.add(new NcBeanModel().setPngPath(toPngPath).setVariableName(variableName).setLevel(level).setTime(time));
        }
        // 判断是否为风
//        String[] uNamePrefixArray = new String[]{"uu", "UU", "u_", "U_", "u", "U","U_component_of_wind_isobaric",};
//        String[] vNamePrefixArray = new String[]{"vv", "VV", "v_", "V_", "v", "V","V_component_of_wind_isobaric",};
//        String[] uvNamePrefixArray = new String[]{"uv", "UV", "uv_", "UV_", "uv", "UV","UV_component_of_wind_isobaric",};
//        String uPrefix, vPrefix, uvPrefix;
//        for (int i = 0; i < uNamePrefixArray.length; i++) {
//            uPrefix = uNamePrefixArray[i];
//            vPrefix = vNamePrefixArray[i];
//            uvPrefix = uvNamePrefixArray[i];
//            if (StringUtils.startsWith(variableName, uPrefix)) {
//                log.info("输出风的红黑图!");
//                String vVariableName = variableName.replaceFirst(uPrefix, vPrefix);
//                // 读取 v 风 的要素
//                Variable vVariable = variableMap.get(vVariableName);
//                // 判断是否为UV要素
//                if (vVariable == null) {
//                    continue;
//                }
//                NcDataModel vNcDataModel = getFaceData(vVariable, org, sha);
//                ncDataModel.setVDataArray(vNcDataModel.getDataArray())
//                        .setUDataArray(ncDataModel.getDataArray());
//                String uvVariableName = variableName.replaceFirst(uPrefix, uvPrefix);
//                toPngPath = pngPath + File.separator + uvVariableName + File.separator + namePrefix.replaceAll(variableName, uvVariableName) + timeStr + ".png";
//                ncDataModel.toWindPng(toPngPath, lat, lon);
//                ncBeanModelList.add(new NcBeanModel().setPngPath(toPngPath).setVariableName(uvVariableName).setLevel(level).setTime(time));
//                break;
//            }
//        }
        // 设置为null 方便gc
        ncDataModel = null;
        return ncBeanModelList;
    }

    @Data
    public static class Context {
        Variable variable;
        List<Integer> org;
        List<Integer> sha;
        Map<String, Variable> variableMap;
        String pngPath;
        String namePrefix;
        String level;
        Boolean isWind;
        Variable uVariable;
        Variable vVariable;

    }

    /**
     * 获取面数据
     *
     * @param variable 要素实体
     * @param org      起始
     * @param sha      尺寸
     * @return
     */
    private static NcDataModel getFaceData(Variable variable, List<Integer> org, List<Integer> sha) {
        // 如果都为空 代表直接读取数据
        if (org == null && sha == null) {
            return NcReader.readNcDataTrue(variable, null, null);
        }
        if (org == null) {
            org = new ArrayList<>();
        }
        if (sha == null) {
            sha = new ArrayList<>();
        }
        List<Dimension> dimensionList = variable.getDimensions();
        // 获取最后两个维度的rank
        Dimension dimensionLast = dimensionList.get(dimensionList.size() - 1);
        Dimension dimensionPre = dimensionList.get(dimensionList.size() - 2);

        // step 1、增加 为 维度或者经度的起始和偏移
        Integer[] orgInt = org.toArray(new Integer[org.size() + 2]);
        Integer[] shaInt = sha.toArray(new Integer[sha.size() + 2]);
        orgInt[orgInt.length - 2] = 0;
        orgInt[orgInt.length - 1] = 0;
        shaInt[shaInt.length - 2] = dimensionPre.getLength();
        shaInt[shaInt.length - 1] = dimensionLast.getLength();

        // step 2、读取数据
        return NcReader.readNcDataTrue(variable, NumberUtils.convert2IntArray(orgInt), NumberUtils.convert2IntArray(shaInt));
    }

    /**
     * 读取维度的数据
     *
     * @param variable
     * @return
     */
    public static double[] readDimensionData(Variable variable, Boolean row) {
        NcDataModel faceData = getFaceData(variable, null, null);
        if (faceData.getRank() == 1) {
            return faceData.convertDoubleArray();
        } else if (faceData.getRank() == 2 && row) {
            return readTwoArrayRowDataToArray(NumberUtils.transformToDoubleTwoRank(faceData.getDataArray().copyToNDJavaArray()));
        } else if (faceData.getRank() == 3 && row) {
            return readTwoArrayRowDataToArray(NumberUtils.transformToDoubleTwoRank(faceData.getDataArray().reduce().copyToNDJavaArray()));
        }
        if (faceData.getRank() == 3) {
            return readTwoArrayColDataToArray(NumberUtils.transformToDoubleTwoRank(faceData.getDataArray().reduce().copyToNDJavaArray()));
        } else {
            return readTwoArrayColDataToArray(NumberUtils.transformToDoubleTwoRank(faceData.getDataArray().copyToNDJavaArray()));

        }
    }

    /**
     * @param twoArray
     * @return
     */
    private static double[] readTwoArrayRowDataToArray(double[][] twoArray) {
        // 取第一行的数据
        double[] oneArray = new double[twoArray[0].length];
        for (int i = 0; i < twoArray[0].length; i++) {
            oneArray[i] = twoArray[0][i];
        }
        return oneArray;
    }

    /**
     * @param twoArray
     * @return
     */
    private static double[] readTwoArrayColDataToArray(double[][] twoArray) {
        // 取第一行的数据
        double[] oneArray = new double[twoArray.length];
        for (int i = 0; i < twoArray.length; i++) {
            oneArray[i] = twoArray[i][0];
        }
        return oneArray;
    }
}