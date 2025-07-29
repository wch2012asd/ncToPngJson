package com.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import com.geovis.receiver.pojo.DecodeConstants;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.*;

/**
 * 读取格点文件数据
 */
public class NcReaderUtils {

    public static String lon = "lon";
    public static String lat = "lat";


    /**
     * 读取格距
     *
     * @param dataMap
     * @param lonName
     * @param latName
     * @return
     */
    public static double[] getGridInterval(Map<String, Object> dataMap, String lonName, String latName, String fileName) {
        float[] lons = null;
        float[] lats = null;
        if (fileName.contains("dens_front") || fileName.contains("saln_front") || fileName.contains("svel_front") || fileName.contains("temp_front") || fileName.contains("front_product") || fileName.contains("FRONT_SALT") || fileName.contains("FRONT_TEMP")) {
            lons = getTwoArrayDatas(dataMap, lonName);
            lats = getTwoArrayDatas(dataMap, latName);
        } else {
            Array array_lon = getArray(dataMap, lonName);
            Array array_lat = getArray(dataMap, latName);
            //		float[] lons = (float[])getArray(dataMap, lonName).copyToNDJavaArray();
            if (array_lon == null) {
                lon = "longitude";
                lat = "latitude";
                array_lon = getArray(dataMap, lon);
                array_lat = getArray(dataMap, lat);
            }
            if (array_lon == null) {
                lon = "lon1";
                lat = "lat1";
                array_lon = getArray(dataMap, lon);
                array_lat = getArray(dataMap, lat);
            }
            if (array_lon == null) {
                lon = "x";
                lat = "y";
                array_lon = getArray(dataMap, lon);
                array_lat = getArray(dataMap, lat);
            }
            if (array_lon == null) {
                lon = "xc";
                lat = "yc";
                array_lon = getArray(dataMap, lon);
                array_lat = getArray(dataMap, lat);
            }
            if (array_lon == null) {
                lon = "lon_0";
                lat = "lat_0";
                array_lon = getArray(dataMap, lon);
                array_lat = getArray(dataMap, lat);
            }
            int rank = array_lon.getRank();
            Object obj_lon = null;
            Object obj_lat = null;
            if (rank == 3) {
                lon = "xc";
                lat = "yc";
                array_lon = getArray(dataMap, lon);
                array_lat = getArray(dataMap, lat);
                obj_lon = array_lon.copyToNDJavaArray();
                obj_lat = array_lat.copyToNDJavaArray();
            } else {
                obj_lon = array_lon.copyTo1DJavaArray();
                obj_lat = array_lat.copyTo1DJavaArray();
            }
            lons = toFloats(obj_lon);
            lats = toFloats(obj_lat);
        }

//		return new double[]{Math.abs(lons[0] - lons[1]), Math.abs(lats[0] - lats[1])};
        return new double[]{lons[1] - lons[0], lats[1] - lats[0]};
    }


    /**
     * 读取二维经纬度 方法一
     *
     * @param datasMap
     * @param elem
     * @return
     */
    public static float[] getArrayDatas(Map<String, Object> datasMap, String elem) {

        Object array = NcReaderUtils.getArray(datasMap, elem).copyToNDJavaArray();
        float[] result = null;
        if (array.getClass() == double[].class) {
            double[] datas = (double[]) array;
            result = new float[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = (float) datas[i];
            }
        } else if (array.getClass() == float.class) {
            float[] datas = (float[]) array;
            result = new float[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = (float) datas[i];
            }
        } else {
            Object obj = null;
            Array array2 = NcReaderUtils.getArray(datasMap, elem);
            int rank = array2.getRank();

            // 用来处理 快速部署 经纬度情况
            // 目前 可能 无用
//            if (rank <= 1) {
//                if (elem.equals("lat")) {
//                    elem = "xc";
//                }
//                if (elem.equals("lon")) {
//                    elem = "yc";
//                }
//                array2 = NcReaderUtils.getArray(datasMap, elem);
//
//                // 此if 用来处理 各别情况 经纬度 是二维时
//            } else
            if ((elem.equals("longitude") || elem.equals("latitude") || elem.equals("lon") || elem.equals("lat")) && rank == 2) {

                if (elem.equals("latitude") || elem.equals("lat")) {
                    double[][] doubles = NcReaderUtils.readByNameLayer(datasMap, elem, "1");
                    result = new float[doubles[0].length];
                    for (int i = 0, count = doubles[0].length; i < count; i++) {
                        result[i] = (float) doubles[0][i];
                    }
                    return result;
                } else if (elem.equals("longitude") || elem.equals("lon")) {
                    double[][] doubles = NcReaderUtils.readByNameLayer(datasMap, elem, "1");
                    result = new float[doubles.length];
                    for (int i = 0, count = doubles.length; i < count; i++) {
                        result[i] = (float) doubles[i][0];
                    }
                    return result;
                }
            }
            obj = array2.copyTo1DJavaArray();
            if (obj.getClass() == double[].class) {
                double[] datas = (double[]) obj;
                result = new float[datas.length];
                for (int i = 0, count = datas.length; i < count; i++) {
                    result[i] = (float) datas[i];
                }
            } else {
                result = (float[]) obj;
            }
        }

        return result;
    }

    /**
     * 读取二维经纬度 方法二 针对与 水文 海洋锋
     *
     * @param datasMap
     * @param elem
     * @return
     */
    public static float[] getTwoArrayDatas(Map<String, Object> datasMap, String elem) {
        Object array = NcReaderUtils.getArray(datasMap, elem).copyToNDJavaArray();
        float[] result = null;
        if (array.getClass() == double[].class) {
            double[] datas = (double[]) array;
            result = new float[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = (float) datas[i];
            }
        } else if (array.getClass() == float.class) {
            float[] datas = (float[]) array;
            result = new float[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = (float) datas[i];
            }
        } else {
            Object obj = null;
            Array array2 = NcReaderUtils.getArray(datasMap, elem);
            int rank = array2.getRank();

            if (elem.equals("lon") || elem.equals("lat") && rank == 2) {

                if (elem.equals("lon")) {
                    double[][] doubles = NcReaderUtils.readByNameLayer(datasMap, elem, "1");
                    result = new float[doubles[0].length];
                    for (int i = 0, count = doubles[0].length; i < count; i++) {
                        result[i] = (float) doubles[0][i];
                    }
                    return result;
                } else if (elem.equals("lat")) {
                    double[][] doubles = NcReaderUtils.readByNameLayer(datasMap, elem, "1");
                    result = new float[doubles.length];
                    for (int i = 0, count = doubles.length; i < count; i++) {
                        result[i] = (float) doubles[i][0];
                    }
                    return result;
                }
            }
            obj = array2.copyTo1DJavaArray();
            if (obj.getClass() == double[].class) {
                double[] datas = (double[]) obj;
                result = new float[datas.length];
                for (int i = 0, count = datas.length; i < count; i++) {
                    result[i] = (float) datas[i];
                }
            } else {
                result = (float[]) obj;
            }
        }

        return result;
    }

    public static float[] toFloats(Object obj) {
        float[] result = null;
        if (obj.getClass() == double[].class) {
            double[] datas = (double[]) obj;
            result = new float[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = (float) datas[i];
            }
        } else if (obj.getClass() == int[].class) {
            int[] datas = (int[]) obj;
            result = new float[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = datas[i];
            }
        } else if (obj.getClass() == long[].class) {
            long[] datas = (long[]) obj;
            result = new float[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = datas[i];
            }
        } else {
            result = (float[]) obj;
        }

        return result;
    }

    public static double[] toDouble(Object obj) {
        double[] result = null;
        if (obj.getClass() == float[].class) {
            float[] datas = (float[]) obj;
            result = new double[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = (double) datas[i];
            }
        } else if (obj.getClass() == int[].class) {
            int[] datas = (int[]) obj;
            result = new double[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = datas[i];
            }
        } else if (obj.getClass() == long[].class) {
            long[] datas = (long[]) obj;
            result = new double[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = datas[i];
            }
        } else if (obj.getClass() == short[].class) {
            short[] datas = (short[]) obj;
            result = new double[datas.length];
            for (int i = 0, count = datas.length; i < count; i++) {
                result[i] = datas[i];
            }
        } else {
            result = (double[]) obj;
        }

        return result;
    }

    public static double[] getLonWestLatSouth(Map<String, Object> dataMap, String lonName, String latName) {
        Array lonArray = getArray(dataMap, lonName);
        Array latArray = getArray(dataMap, latName);
        if (lonArray == null || latArray == null) {
            lon = "longitude";
            lat = "latitude";
            lonArray = getArray(dataMap, lon);
            latArray = getArray(dataMap, lat);
        }
        if (lonArray == null || latArray == null) {
            lon = "lon1";
            lat = "lat1";
            lonArray = getArray(dataMap, lon);
            latArray = getArray(dataMap, lat);
        }
        if (lonArray == null || latArray == null) {
            lon = "lon_0";
            lat = "lat_0";
            lonArray = getArray(dataMap, lon);
            latArray = getArray(dataMap, lat);
        }
        Object lonObj = lonArray.copyTo1DJavaArray();
        Object latObj = latArray.copyTo1DJavaArray();
//		float[] lons = (float[]) lonArray.copyToNDJavaArray();
//		float[] lats = (float[])latArray.copyToNDJavaArray();
        float[] lons = toFloats(lonObj);
        float[] lats = toFloats(latObj);

        return new double[]{lons[0], lats[0]};
    }


    public static String[] getDimensions(Map<String, Object> dataMap, String elName) {
        elName = elName.toLowerCase();
        Variable v = (Variable) dataMap.get(elName);
        List<Dimension> dimensions = v.getDimensions();
        int count = dimensions.size();
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = dimensions.get(i).getShortName();
        }

        return result;
    }

    /**
     * 读取指定要素和层次的数据
     *
     * @param dataMap
     * @param elName
     * @param layer
     * @return
     */
    public static double[][] readByNameLayer(Map<String, Object> dataMap, String elName, String layer) {
        double[][] result = null;
        elName = elName.toLowerCase();
        Variable v = (Variable) dataMap.get(elName);
        if (v == null) {
            return result;
        }
        List<Attribute> attributes = v.getAttributes();
        double missValue = (double) -999999;

        for (Attribute att : attributes) {
            if (att.getFullName().equals("missing_value")) {
                if (att.getDataType() == DataType.SHORT) {
                    missValue = (short) att.getNumericValue();
                } else if (att.getDataType() == DataType.FLOAT) {
                    missValue = (float) att.getNumericValue();
                }
            }
        }
        int rank = v.getRank();
//		long time = System.currentTimeMillis();
//		Object copyToNDJavaArray = getArray(dataMap, elName).copyToNDJavaArray();
        Array array = getArray(dataMap, elName);
        Object objArray = array.copyToNDJavaArray();
        array = null;
//		System.out.println("读 " + elName + "_" + layer + " 数据耗时: " + (System.currentTimeMillis() - time));
        Class<? extends Object> arrayClass = objArray.getClass();
        if (arrayClass == byte[][].class || arrayClass == byte[][][].class || arrayClass == byte[][][][].class || arrayClass == byte[][][][][].class) {
            byte[][] data = null;
            if (rank == 2) {
                data = ((byte[][]) objArray);
            } else if (rank == 3) {
                byte[][][] datas = ((byte[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                byte[][][] datas = ((byte[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                        //result[i][j] = (byte) DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == short[][].class || arrayClass == short[][][].class || arrayClass == short[][][][].class || arrayClass == short[][][][][].class) {
            short[][] data = null;
            if (rank == 2) {
                data = ((short[][]) objArray);
            } else if (rank == 3) {
                short[][][] datas = ((short[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                short[][][] datas = ((short[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = (short) DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == float[][].class || arrayClass == float[][][].class || arrayClass == float[][][][].class || arrayClass == float[][][][][].class) {
            float[][] data = null;
            if (rank == 2) {
                data = ((float[][]) objArray);
            } else if (rank == 3) {
                float[][][] datas = ((float[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                float[][][] datas = ((float[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }else if (rank == 5) {
                float[][][][] datas = ((float[][][][][]) objArray)[0];
                int num = 0;
//                int index = getLayerIndex(dataMap, elName, layer);
                int index = Integer.parseInt(layer);
                data = datas[num][index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == double[][].class || arrayClass == double[][][].class || arrayClass == double[][][][].class || arrayClass == double[][][][][].class) {
            double[][] data = null;
            if (rank == 2) {
                data = ((double[][]) objArray);
            } else if (rank == 3) {
                double[][][] datas = ((double[][][]) objArray);
                //double[][][] test = test(datas);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                double[][][] datas = ((double[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 5) {
                double[][][][] datas = ((double[][][][][]) objArray)[0];
                int num = 0;
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[num][index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                        //result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == int[][].class || arrayClass == int[][][].class || arrayClass == int[][][][].class || arrayClass == int[][][][][].class) {
            int[][] data = null;
            if (rank == 2) {
                data = ((int[][]) objArray);
            } else if (rank == 3) {
                int[][][] datas = ((int[][][]) objArray);
                //double[][][] test = test(datas);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                int[][][] datas = ((int[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 5) {
                int[][][][] datas = ((int[][][][][]) objArray)[0];
                int num = 0;
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[num][index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == long[][].class || arrayClass == long[][][].class || arrayClass == long[][][][].class || arrayClass == long[][][][][].class) {
            long[][] data = null;
            if (rank == 2) {
                data = ((long[][]) objArray);
            } else if (rank == 3) {
                long[][][] datas = ((long[][][]) objArray);
                //double[][][] test = test(datas);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                long[][][] datas = ((long[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 5) {
                long[][][][] datas = ((long[][][][][]) objArray)[0];
                int num = 0;
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[num][index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        }

        return result;
    }

    /**
     * 读取指定要素和层次的数据
     *
     * @param dataMap
     * @param elName
     * @param layer
     * @return
     */
    public static double[][] readByNameLayer(Map<String, Object> dataMap, String elName, String layer, int timeSize) {
        double[][] result = null;
        elName = elName.toLowerCase();
        Variable v = (Variable) dataMap.get(elName);
        if (v == null) {
            return result;
        }
        List<Attribute> attributes = v.getAttributes();
        double missValue = (double) -999999;

        for (Attribute att : attributes) {
            if (att.getFullName().equals("missing_value")) {
                if (att.getDataType() == DataType.SHORT) {
                    missValue = (short) att.getNumericValue();
                } else if (att.getDataType() == DataType.FLOAT) {
                    missValue = (float) att.getNumericValue();
                }
            }
        }
        int rank = v.getRank();
//		long time = System.currentTimeMillis();
//		Object copyToNDJavaArray = getArray(dataMap, elName).copyToNDJavaArray();
        Array array = getArray(dataMap, elName);
//		System.out.println("000000000000000000000000: " + (System.currentTimeMillis() - time));
//		time = System.currentTimeMillis();
        Object objArray = array.copyToNDJavaArray();
//		System.out.println("读 " + elName + "_" + layer + " 数据耗时: " + (System.currentTimeMillis() - time));
        Class<? extends Object> arrayClass = objArray.getClass();
        if (arrayClass == byte[][].class || arrayClass == byte[][][].class || arrayClass == byte[][][][].class || arrayClass == byte[][][][][].class) {
            byte[][] data = null;
            if (rank == 2) {
                data = ((byte[][]) objArray);
            } else if (rank == 3) {
                byte[][][] datas = ((byte[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                byte[][][] datas = ((byte[][][][]) objArray)[timeSize];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = (byte) DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == short[][].class || arrayClass == short[][][].class || arrayClass == short[][][][].class || arrayClass == short[][][][][].class) {
            short[][] data = null;
            if (rank == 2) {
                data = ((short[][]) objArray);
            } else if (rank == 3) {
                short[][][] datas = ((short[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                short[][][] datas = ((short[][][][]) objArray)[timeSize];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = (short) DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == float[][].class || arrayClass == float[][][].class || arrayClass == float[][][][].class || arrayClass == float[][][][][].class) {
            float[][] data = null;
            if (rank == 2) {
                data = ((float[][]) objArray);
            } else if (rank == 3) {
                float[][][] datas = ((float[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                float[][][] datas = ((float[][][][]) objArray)[timeSize];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == double[][].class || arrayClass == double[][][].class || arrayClass == double[][][][].class || arrayClass == double[][][][][].class) {
            double[][] data = null;
            if (rank == 2) {
                data = ((double[][]) objArray);
            } else if (rank == 3) {
                double[][][] datas = ((double[][][]) objArray);
                //double[][][] test = test(datas);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                double[][][] datas = ((double[][][][]) objArray)[timeSize];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 5) {
                double[][][][] datas = ((double[][][][][]) objArray)[0];
                int num = 0;
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[num][index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == int[][].class || arrayClass == int[][][].class || arrayClass == int[][][][].class || arrayClass == int[][][][][].class) {
            int[][] data = null;
            if (rank == 2) {
                data = ((int[][]) objArray);
            } else if (rank == 3) {
                int[][][] datas = ((int[][][]) objArray);
                //double[][][] test = test(datas);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                int[][][] datas = ((int[][][][]) objArray)[timeSize];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 5) {
                int[][][][] datas = ((int[][][][][]) objArray)[0];
                int num = 0;
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[num][index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == long[][].class || arrayClass == long[][][].class || arrayClass == long[][][][].class || arrayClass == long[][][][][].class) {
            long[][] data = null;
            if (rank == 2) {
                data = ((long[][]) objArray);
            } else if (rank == 3) {
                long[][][] datas = ((long[][][]) objArray);
                //double[][][] test = test(datas);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                long[][][] datas = ((long[][][][]) objArray)[timeSize];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 5) {
                long[][][][] datas = ((long[][][][][]) objArray)[0];
                int num = 0;
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[num][index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        }

        return result;
    }

    public static double[][][] test(double[][][] doubles) {
        //double[][][] doubles1 = new double[];
        double[] bigDouble = new double[doubles[0][0].length * doubles.length * doubles[0].length];
        //System.out.println(bigDouble[26994239]);
//		System.out.println(doubles[0][0].length*doubles.length*doubles[0].length);
        for (int i = 0; i < doubles.length; i++) {
            //System.out.println();
            for (int x = 0; x < doubles[i].length; x++) {
                //System.out.println();
                for (int y = 0; y < doubles[i][x].length; y++) {
                    //System.out.print(doubles[y][x][i]+" ");
                    //bigDouble[(i*721)+(x*26)+y] = doubles[i][x][y];
                    bigDouble[(i * doubles[i][x].length) + (x * doubles[i].length) + y] = doubles[i][x][y];
                    //			System.out.println(bigDouble[(i*doubles[i][x].length)+(x*doubles[i].length)+y]);
                }
            }
        }
//		System.out.println(bigDouble[26994240]);
        //每一层有多少个数 1440*721
        int depthNum = bigDouble.length / doubles[0][0].length;

        //每一层的每一行有多少数 //721
        int longNum = depthNum / doubles.length;

        double[][][] result = new double[doubles[0][0].length][doubles.length][longNum];

        for (int i = 0; i < doubles[0][0].length; i++) {
            for (int x = 0; x < longNum; x++) {
                for (int y = 0; y < doubles.length; y++) {
                    int index = (y) + (x * doubles.length) + (i * depthNum);
                    if (index == 26994240) {
                        //System.out.println(index);
                    }
                    result[i][y][x] = bigDouble[index];
                }
            }
        }

//		double[][][] result = new double[doubles[0][0].length][doubles.length][doubles[0].length];
//		for(int depth = 0;depth < doubles[0][0].length;depth++){
//			//循环层 取出每一层的数据
//			//声明一个二维数组 用来装每一层
//			double[][] ploy = new double[doubles.length][doubles[0].length];
//			for(int x = 0;x < doubles.length;x++){
//				for (int y=0;y < doubles[0].length;y++){
//					ploy[x][y] = doubles[x][y][depth];
//				}
//				result[depth] = ploy;
//			}
//		}


//		System.out.println(bigDouble);
        return result;
        //return doubles1;
    }

    public static double[][][] readByNameLayerF(Map<String, Object> dataMap, String elName, String layer) {
        double[][][] result = null;
        elName = elName.toLowerCase();
        Variable v = (Variable) dataMap.get(elName);
        List<Attribute> attributes = v.getAttributes();
        float missValue = -999999f;
        for (Attribute att : attributes) {
            if (att.getFullName().equals("missing_value")) {
                missValue = (float) att.getNumericValue();
            }
        }
//		int rank = v.getRank();
//		long time = System.currentTimeMillis();
//		Object copyToNDJavaArray = getArray(dataMap, elName).copyToNDJavaArray();
        Array array = getArray(dataMap, elName);
//		System.out.println("000000000000000000000000: " + (System.currentTimeMillis() - time));
//		time = System.currentTimeMillis();
        Object objArray = array.copyToNDJavaArray();
//		System.out.println("读 " + elName + "_" + layer + " 数据耗时: " + (System.currentTimeMillis() - time));
        Class<? extends Object> arrayClass = objArray.getClass();
        if (arrayClass == byte[][][][][].class) {
            byte[][][] data = null;
            byte[][][][] datas = ((byte[][][][][]) objArray)[0];
            int index = getLayerIndex(dataMap, elName, layer);
            data = datas[index];
            result = new double[data.length][data[0].length][data[0][0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        if (data[i][j][k] == missValue) {
                           // result[i][j][k] = (byte) DecodeConstants.UNDEF_INT_VALUE;
                        } else {
                            result[i][j][k] = data[i][j][k];
                        }
                    }
                }
            }
        } else if (arrayClass == short[][][][][].class) {
            short[][][] data = null;

            short[][][][] datas = ((short[][][][][]) objArray)[0];
            int index = getLayerIndex(dataMap, elName, layer);
            data = datas[index];
            result = new double[data.length][data[0].length][data[0][0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        if (data[i][j][k] == missValue) {
                           // result[i][j][k] = (short) DecodeConstants.UNDEF_INT_VALUE;
                        } else {
                            result[i][j][k] = data[i][j][k];
                        }
                    }
                }
            }
        } else if (arrayClass == float[][][][][].class) {
            float[][][] data = null;

            float[][][][] datas = ((float[][][][][]) objArray)[0];
            int index = getLayerIndex(dataMap, elName, layer);
            data = datas[index];

            result = new double[data.length][data[0].length][data[0][0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        if (data[i][j][k] == missValue) {
                            //result[i][j][k] = (float) DecodeConstants.UNDEF_INT_VALUE;
                        } else {
                            result[i][j][k] = data[i][j][k];
                        }
                    }
                }
            }
        } else if (arrayClass == double[][][][][].class) {
            double[][][] data = null;

            double[][][][] datas = ((double[][][][][]) objArray)[0];
            int index = getLayerIndex(dataMap, elName, layer);
            data = datas[index];

            result = new double[data.length][data[0].length][data[0][0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        if (data[i][j][k] == missValue) {
                          //  result[i][j][k] = (float) DecodeConstants.UNDEF_INT_VALUE;
                        } else {
                            result[i][j][k] = data[i][j][k];
                        }
                    }
                }
            }
        }

        return result;
    }

    public static double[] readByName(Map<String, Object> dataMap, String elName) {
        double[] result = null;
        elName = elName.toLowerCase();
        Variable v = (Variable) dataMap.get(elName);
        if (v == null) {
            result = new double[]{-1};
            return result;
        }
        int rank = v.getRank();
        Array array = null;
        try {
            array = v.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (array == null) {
            return result;
        }
        Object copyToNDArray = array.copyToNDJavaArray();
        if (copyToNDArray.getClass() == byte[][].class || copyToNDArray.getClass() == byte[][][].class || copyToNDArray.getClass() == byte[][][][].class) {
            byte[][] data = null;
            if (rank == 2) {
                data = ((byte[][]) copyToNDArray);
            } else if (rank == 3) {
                data = ((byte[][][]) copyToNDArray)[0];
            } else if (rank == 4) {
                byte[][][] datas = ((byte[][][][]) copyToNDArray)[0];
                int x = datas.length;
                int y = datas[0].length;
                int z = datas[0][0].length;
                result = new double[x * y * z];
                for (int i = 0; i < x; i++) {
                    for (int j = 0; j < y; j++) {
                        for (int k = 0; k < z; k++) {
                            result[i * y * z + (j * z + k)] = datas[i][j][k];
                        }
                    }
                }
            }
        } else if (copyToNDArray.getClass() == short[][].class || copyToNDArray.getClass() == short[][][].class || copyToNDArray.getClass() == short[][][][].class) {
            short[][] data = null;
            if (rank == 2) {
                data = ((short[][]) copyToNDArray);
            } else if (rank == 3) {
                data = ((short[][][]) copyToNDArray)[0];
            } else if (rank == 4) {
                short[][][] datas = ((short[][][][]) copyToNDArray)[0];
                int x = datas.length;
                int y = datas[0].length;
                int z = datas[0][0].length;
                result = new double[x * y * z];
                for (int i = 0; i < x; i++) {
                    for (int j = 0; j < y; j++) {
                        for (int k = 0; k < z; k++) {
                            result[i * y * z + (j * z + k)] = datas[i][j][k];
                        }
                    }
                }
            }
        } else if (copyToNDArray.getClass() == float[].class || copyToNDArray.getClass() == float[][].class || copyToNDArray.getClass() == float[][][].class || copyToNDArray.getClass() == float[][][][].class) {
            float[][] data = null;
            if (rank == 1) {
                float[] temp = (float[]) copyToNDArray;
                int count = temp.length;
                result = new double[count];
                for (int i = 0; i < count; i++) {
                    result[i] = temp[i];
                }
            } else if (rank == 2) {
                data = ((float[][]) copyToNDArray);
            } else if (rank == 3) {
                data = ((float[][][]) copyToNDArray)[0];
            } else if (rank == 4) {
                float[][][] datas = ((float[][][][]) copyToNDArray)[0];
                int x = datas.length;
                int y = datas[0].length;
                int z = datas[0][0].length;
                result = new double[x * y * z];
                for (int i = 0; i < x; i++) {
                    for (int j = 0; j < y; j++) {
                        for (int k = 0; k < z; k++) {
                            result[i * y * z + (j * z + k)] = datas[i][j][k];
                        }
                    }
                }
            }
        }


        return result;
    }

    /**
     * 解析多组 文件重名问题
     *
     * @param filePath 解析文件地址
     * @return
     */
    public static List<Map<String, Object>> getDatasMapGroup(String filePath) {
        ArrayList<Map<String, Object>> groupMaps = new ArrayList<>();

        try {
            if (filePath.endsWith("gbx9") || filePath.endsWith("ncx3")) {
                new File(filePath).delete();
            }
            boolean del = new File(filePath + ".ncx3").delete();
            boolean del2 = new File(filePath + ".gbx9").delete();

            NetcdfFile dataset = NetcdfFile.open(filePath);

            List<Group> groups = dataset.getRootGroup().getGroups();
            if (groups.size() > 0) {
                for (Group group : groups) {
                    Map<String, Object> result = new ConcurrentHashMap<String, Object>();
                    List<Variable> variables = group.getVariables();
                    for (Variable v : variables) {
                        // 获取文件中每一个要素 的结构并且存到map中
                        String name = v.getShortName().toLowerCase();
                        result.put(name, v);
                    }
                    groupMaps.add(result);
                }
            } else {
                Map<String, Object> result = new ConcurrentHashMap<String, Object>();
                List<Variable> variables = dataset.getVariables();

                for (Variable v : variables) {
                    // 获取文件中每一个要素 的结构并且存到map中
                    String name = v.getShortName().toLowerCase();
                    result.put(name, v);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return groupMaps;
        }
        return groupMaps;
    }


    public static Map<String, Object> getDatasMap(String filePath) {
        Map<String, Object> result = new ConcurrentHashMap<String, Object>();
        try {
            if (filePath.endsWith("gbx9") || filePath.endsWith("ncx3")) {
                new File(filePath).delete();
            }
            boolean del = new File(filePath + ".ncx3").delete();
            boolean del2 = new File(filePath + ".gbx9").delete();

            NetcdfFile dataset = NetcdfFile.open(filePath);


            List<Variable> variables = dataset.getVariables();

            for (Variable v : variables) {
                // 获取文件中每一个要素 的结构并且存到map中
                String name = v.getShortName().toLowerCase();
                result.put(name, v);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }


        return result;
    }

    public static String getElementUnit(Map<String, Object> datasMap, String elName) {
        String prefix = "";
//		if(elName.startsWith("xxx"))
//		{
//			prefix = elName.substring(0, 4);
//		}
        String result = null;
        Variable variable = (Variable) datasMap.get(elName.toLowerCase());
        if (variable != null) {
            result = ((Variable) datasMap.get(prefix + elName)).getUnitsString();
        }
        if (result == null) {
            result = "";
        }
        result = result.replace(" ", ".").replace("**", "");

        return result;
    }

    /**
     * 获取格点数据集中 最外层 关于时间的 层级
     *
     * @param datasMap
     * @param elName
     * @return
     */
    public static String[] getDimensionNameAndUnitOne(Map<String, Object> datasMap, String elName) {
        String prefix = "";
        if (elName.startsWith("xxx")) {
            prefix = elName.substring(0, 4);
        }
        String[] result = new String[4];
        Variable variable = (Variable) datasMap.get(elName.toLowerCase());
        if (variable != null) {
            String tempStr = null;
            int count = variable.getDimensions().size();
            if (count >= 3) {
                // 获取有关 时间 空间信息 的下标
                int i;
                for (i = 0; i < variable.getDimensions().size(); i++) {
                    String replace = variable.getDimensions().get(i).toString().replace(";", "").replace(" ", "");
                    if (replace.contains("time_pre") || replace.contains("time")) {
                        tempStr = replace;
                    }
                }
                //tempStr = variable.getDimensions().get(i-1).toString().replace(";", "").replace(" ", "");
                if (tempStr.contains("UNLIMITED")) {
                    result[0] = tempStr.replace("UNLIMITED//(", "").replace("currently)", "");
                }
                String[] split = null;
                if (elName.equals("hs") || elName.equals("th") || elName.equals("tp") || elName.equals("tz") || elName.equals("windx") || elName.equals("windx")) {
                    split = result[0].split("=");
                } else {
                    split = tempStr.split("=");
                }

                if (split[1].contains("UNLIMITED")) {
                    result[0] = "";
                    result[1] = "";
                } else if (Integer.parseInt(split[1].trim()) >= 1) {
                    result[0] = split[0];
                    result[1] = ((Variable) datasMap.get(prefix + result[0])).getUnitsString();
                    // result[1] = ((Variable) datasMap.get("time_pre")).getUnitsString();
                    if (result[1] == null) {
                        result[1] = "";
                    }
                } else {
                    result[0] = "";
                    result[1] = "";
                }
            }
        }

        return result;
    }

    /**
     * 获取对应要素的第一个属性以及对应单位
     *
     * @param datasMap
     * @param elName
     * @return
     */
    public static String[] getDimensionNameAndUnit(Map<String, Object> datasMap, String elName) {
        String prefix = "";
        if (elName.startsWith("xxx")) {
            prefix = elName.substring(0, 4);
        }
        String[] result = new String[4];
        Variable variable = (Variable) datasMap.get(elName.toLowerCase());
        if (variable != null) {
            String tempStr = null;
            int count = variable.getDimensions().size();
            if (count > 3) {
                tempStr = variable.getDimensions().get(1).toString();
                result[0] = tempStr.substring(0, tempStr.indexOf("=")).replace(" ", "").toLowerCase();
                result[1] = ((Variable) datasMap.get(prefix + result[0])).getUnitsString();////数据单位
                if (result[1] == null) {
                    result[1] = "";
                }
                tempStr = variable.getDimensions().get(2).toString();
                result[2] = tempStr.substring(0, tempStr.indexOf("=")).replace(" ", "").toLowerCase();
                result[3] = ((Variable) datasMap.get(prefix + result[2])).getUnitsString();
                if (result[3] == null) {
                    result[3] = "";
                }
            } else if (count == 3) {
                // 获取有关 时间 空间信息 的下标
                int i;
                for (i = 0; i < variable.getDimensions().size(); i++) {
                    String replace = variable.getDimensions().get(i).toString().replace(";", "").replace(" ", "");
                    if (replace.contains("level") || replace.contains("depth") || replace.contains("lev") || replace.contains("time_pre") || replace.contains("altitude") || replace.contains("time") || replace.contains("z")) {
                        tempStr = replace;
                    }

                }
                //tempStr = variable.getDimensions().get(i-1).toString().replace(";", "").replace(" ", "");
                if (tempStr.contains("UNLIMITED")) {
                    result[0] = tempStr.replace("UNLIMITED//(", "").replace("currently)", "");
                }
                String[] split = null;
                //|| elName.equals("tp")
                if (elName.equals("hs") || elName.equals("th") || elName.equals("tz") || elName.equals("windx") || elName.equals("windy")) {
                    split = result[0].split("=");
                } else {
                    result = tempStr.split("=");
                }


//                if (split[1].contains("UNLIMITED")) {
//                    result[0] = "";
//                    result[1] = "";
//                } else if (Integer.parseInt(split[1].trim()) >= 1) {
//                    result[0] = split[0];
//                    result[1] = ((Variable) datasMap.get(prefix + result[0])).getUnitsString();
//                    // result[1] = ((Variable) datasMap.get("time_pre")).getUnitsString();
//                    if (result[1] == null) {
//                        result[1] = "";
//                    }
//                } else {
//                    result[0] = "";
//                    result[1] = "";
//                }
            }
        }

        return result;
    }

    /**
     * 获取相应层的数据数组的索引
     *
     * @param dataMap
     * @param elName
     * @param layer
     * @return
     */
    private static int getLayerIndex(Map<String, Object> dataMap, String elName, String layer) {
        int result = 0;
        if (layer == null) {
            return result;
        }
        String[] name = null;
//		Variable v = (Variable) dataMap.get(elName);
//		Dimension dimension = v.getDimension(0);
//		String name = dimension.getShortName().toLowerCase();
        name = NcReaderUtils.getDimensionNameAndUnit(dataMap, elName);

        if (name[0].equals("z")) {
            name[0] = "depth";
        }
        Array array = getArray(dataMap, name[0]);

        DataType dataType = array.getDataType();

        float[] layers = null;
        if (dataType == DataType.INT) {
            int[] layersInt = (int[]) array.copyTo1DJavaArray();
            layers = new float[layersInt.length];
            for (int i = 0, count = layers.length; i < count; i++) {
                layers[i] = layersInt[i];
            }
        } else if (dataType == DataType.FLOAT) {
            layers = (float[]) array.copyTo1DJavaArray();
        } else if (dataType == DataType.DOUBLE) {
            double[] layersD = (double[]) array.copyTo1DJavaArray();
            layers = new float[layersD.length];
            for (int i = 0, count = layersD.length; i < count; i++) {
                layers[i] = (float) layersD[i];
            }
        } else if (dataType == DataType.LONG) {
            long[] layersD = (long[]) array.copyTo1DJavaArray();
            layers = new float[layersD.length];
            for (int i = 0, count = layersD.length; i < count; i++) {
                layers[i] = (float) layersD[i];
            }
        }
//		float[] layers = getElementLayers(dataMap, elName);
        float layerNum = Float.parseFloat(layer);
        for (int i = 0, count = layers.length; i < count; i++) {
            if (layers[i] == layerNum) {
                result = i;
                break;
            }
        }

        return result;
    }

    /**
     * 读取属性有多少层 以及每一层的深度
     *
     * @param dataMap
     * @param elName
     * @return
     */
    public synchronized static Array getArray(Map<String, Object> dataMap, String elName) {
        Array result = null;
        Variable v = (Variable) dataMap.get(elName);
        if (v == null) {
            return result;
        }
        try {
            result = v.read();
        } catch (Exception e) {
            try {
                result = v.read();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return result;
        }

        return result;
    }


    public static String[] getLonLatName(Map<String, Object> datasMap) {
        String[] result = new String[]{"lon", "lat"};
        String elName = null;
        for (String key : datasMap.keySet()) {
            Variable v = (Variable) datasMap.get(key);
            int rank = v.getRank();
            if (rank > 2) {
                elName = key;
                break;
            }
        }
        Variable variable = (Variable) datasMap.get(elName.toLowerCase());
        if (variable != null) {

            for (String keyset : datasMap.keySet()) {

                // 如果数组纬度 大于一维 则不是经纬度 直接跳过
                Variable v = (Variable) datasMap.get(keyset);
                if (v.getRank() > 1) {
                    continue;
                }
                if (keyset.equals("latitude")) {
                    if (keyset.equals("latitude")) {
                        result[1] = "latitude";
                    } else {
                        result[1] = keyset;
                    }
                }
                if (keyset.equals("longitude")) {
                    if (keyset.equals("longitude")) {
                        result[0] = "longitude";
                    } else {
                        result[0] = keyset;
                    }
                }
                if (keyset.equals("lon")) {
                    if (keyset.equals("lon")) {
                        result[0] = "lon";
                    } else {
                        result[0] = keyset;
                    }
                }

                if (keyset.equals("lat")) {
                    if (keyset.equals("lat")) {
                        result[1] = "lat";
                    } else {
                        result[1] = keyset;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 给 只有两个标签的数据获取经纬度
     *
     * @param datasMap
     * @return
     */
    public static String[] getLonLatNameTwo(Map<String, Object> datasMap) {
        String[] result = new String[]{"lon", "lat"};
        String elName = null;
        for (String key : datasMap.keySet()) {
            Variable v = (Variable) datasMap.get(key);
            int rank = v.getRank();
            if (rank > 1) {
                elName = key;
                break;
            }
        }
        Variable variable = (Variable) datasMap.get(elName.toLowerCase());
        if (variable != null) {

            for (String keyset : datasMap.keySet()) {
                if (keyset.contains("lon") && !keyset.contains("mid_lon")) {
                    if (keyset.equals("lon")) {
                        result[0] = "lon";
                    } else {
                        result[0] = keyset;
                    }
                }
                if (keyset.contains("lat") && !keyset.contains("mid_lat")) {
                    if (keyset.equals("lat")) {
                        result[1] = "lat";
                    } else {
                        result[1] = keyset;
                    }
                }

            }
        }
        return result;
    }


    public static double[][] readByNameLayers(Map<String, Object> dataMap, String layerName, String elName, String layer) {
        double[][] result = null;
        elName = elName.toLowerCase();
        Variable v = (Variable) dataMap.get(elName);
        if (v == null) {
            return result;
        }
        List<Attribute> attributes = v.getAttributes();
        float missValue = -999999f;
        for (Attribute att : attributes) {
            if (att.getFullName().equals("missing_value")) {
                missValue = (float) att.getNumericValue();
            }
        }
        int rank = v.getRank();
//		long time = System.currentTimeMillis();
//		Object copyToNDJavaArray = getArray(dataMap, elName).copyToNDJavaArray();
        Array array = getArray(dataMap, elName);
//		System.out.println("000000000000000000000000: " + (System.currentTimeMillis() - time));
//		time = System.currentTimeMillis();
        Object objArray = array.copyToNDJavaArray();
//		System.out.println("读 " + elName + "_" + layer + " 数据耗时: " + (System.currentTimeMillis() - time));
        Class<? extends Object> arrayClass = objArray.getClass();
        if (arrayClass == byte[][].class || arrayClass == byte[][][].class || arrayClass == byte[][][][].class || arrayClass == byte[][][][][].class) {
            byte[][] data = null;
            if (rank == 2) {
                data = ((byte[][]) objArray);
            } else if (rank == 3) {
                byte[][][] datas = ((byte[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                byte[][][] datas = ((byte[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                      //  result[i][j] = (byte) DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == short[][].class || arrayClass == short[][][].class || arrayClass == short[][][][].class || arrayClass == short[][][][][].class) {
            short[][] data = null;
            if (rank == 2) {
                data = ((short[][]) objArray);
            } else if (rank == 3) {
                short[][][] datas = ((short[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                short[][][] datas = ((short[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = (short) DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == float[][].class || arrayClass == float[][][].class || arrayClass == float[][][][].class || arrayClass == float[][][][][].class) {
            float[][] data = null;
            if (rank == 2) {
                data = ((float[][]) objArray);
            } else if (rank == 3) {
                float[][][] datas = ((float[][][]) objArray);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                float[][][] datas = ((float[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                      //  result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        } else if (arrayClass == double[][].class || arrayClass == double[][][].class || arrayClass == double[][][][].class || arrayClass == double[][][][][].class) {
            double[][] data = null;
            if (rank == 2) {
                data = ((double[][]) objArray);
            } else if (rank == 3) {
                double[][][] datas = ((double[][][]) objArray);
                //double[][][] test = test(datas);
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 4) {
                double[][][] datas = ((double[][][][]) objArray)[0];
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[index];
            } else if (rank == 5) {
                double[][][][] datas = ((double[][][][][]) objArray)[0];
                int num = 0;
                int index = getLayerIndex(dataMap, elName, layer);
                data = datas[num][index];
            }
            result = new double[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j] == missValue) {
                       // result[i][j] = DecodeConstants.UNDEF_INT_VALUE;
                    } else {
                        result[i][j] = data[i][j];
                    }
                }
            }
        }

        return result;
    }

    public synchronized static double[][] readByElemNameLayerSlice(Map<String, Object> dataMap, String elName, String layer) {
        double[][][] result = null;
        double[][] result2 = null;
        elName = elName.toLowerCase();
        Variable v = (Variable) dataMap.get(elName);
        if (v == null) {
            return result2;
        }
        List<Attribute> attributes = v.getAttributes();
        float missValue = -999999f;
        for (Attribute att : attributes) {
            if (att.getFullName().equals("missing_value")) {
                Number numericValue = att.getNumericValue();
                Class<? extends Number> aClass = numericValue.getClass();
                if (aClass == short.class) {
                    missValue = Float.valueOf(String.valueOf(att.getNumericValue()));
                } else if (aClass == float.class) {
                    missValue = (float) att.getNumericValue();
                }
            }
        }
        int rank = v.getRank();
        int[] sOrigin = new int[rank];
        int[] sShape = new int[rank];
        for (int i = 0; i < rank; i++) {
            sOrigin[i] = 0;
            sShape[i] = 1;
        }

        int[] shape = v.getShape();
        sShape[rank - 2] = shape[rank - 2];
        sShape[rank - 1] = shape[rank - 1];
        int layerIndex = getLayerIndex(dataMap, elName, layer);
        sOrigin[rank - 3] = layerIndex;
        try {
            Array array = v.read(sOrigin, sShape);

            Object objArray = array.copyToNDJavaArray();

            result = toDoubleArray(objArray, rank, dataMap, elName, layer, missValue);

        } catch (InvalidRangeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (double[][] doubles : result) {
            result2 = doubles;
        }
        return result2;
    }

    private static double[][][] toDoubleArray(Object objArray, int rank, Map<String, Object> dataMap, String elName, String layer, float missValue) {
        double[][][] result = null;
        Class<? extends Object> arrayClass = objArray.getClass();
        if (arrayClass == byte[][].class || arrayClass == byte[][][].class || arrayClass == byte[][][][].class || arrayClass == byte[][][][][].class) {
            byte[][][] data = null;
            if (rank == 2) {
                data = ((byte[][][]) objArray);
            } else if (rank == 3) {
                data = ((byte[][][]) objArray);
            } else if (rank == 4) {
                byte[][][] datas = ((byte[][][][]) objArray)[0];
//				int index = getLayerIndex(dataMap, elName, layer);
                data = datas;
            }
            result = new double[data.length][data[0].length][data[0][0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        if (data[i][j][k] == missValue) {
                           // result[i][j][k] = (byte) DecodeConstants.UNDEF_INT_VALUE;
                        } else {
                            result[i][j][k] = data[i][j][k];
                        }
                    }
                }
            }
        } else if (arrayClass == short[][].class || arrayClass == short[][][].class || arrayClass == short[][][][].class || arrayClass == short[][][][][].class) {
            short[][][] data = null;
            if (rank == 2) {
                data = ((short[][][]) objArray);
            } else if (rank == 3) {
                data = ((short[][][]) objArray);
            } else if (rank == 4) {
                short[][][] datas = ((short[][][][]) objArray)[0];
//				int index = getLayerIndex(dataMap, elName, layer);
                data = datas;
            }
            result = new double[data.length][data[0].length][data[0][0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        if (data[i][j][k] == missValue) {
                          //  result[i][j][k] = (short) DecodeConstants.UNDEF_INT_VALUE;
                        } else {
                            result[i][j][k] = data[i][j][k];
                        }
                    }
                }
            }
        } else if (arrayClass == float[][].class || arrayClass == float[][][].class || arrayClass == float[][][][].class || arrayClass == float[][][][][].class) {
            float[][][] data = null;
            if (rank == 2) {
                data = ((float[][][]) objArray);
            } else if (rank == 3) {
                data = ((float[][][]) objArray);
            } else if (rank == 4) {
                float[][][] datas = ((float[][][][]) objArray)[0];
//				int index = getLayerIndex(dataMap, elName, layer);
                data = datas;
            }
            result = new double[data.length][data[0].length][data[0][0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        if (data[i][j][k] == missValue) {
                          //  result[i][j][k] = (float) DecodeConstants.UNDEF_INT_VALUE;
                        } else {
                            result[i][j][k] = data[i][j][k];
                        }
                    }
                }
            }
        } else if (arrayClass == double[][].class || arrayClass == double[][][].class || arrayClass == double[][][][].class || arrayClass == double[][][][][].class) {
            double[][][] data = null;
            if (rank == 2) {
                data = ((double[][][]) objArray);
            } else if (rank == 3) {
                data = ((double[][][]) objArray);
            } else if (rank == 4) {
                double[][][] datas = ((double[][][][]) objArray)[0];
//				int index = getLayerIndex(dataMap, elName, layer);
                data = datas;
            } else if (rank == 5) {
                double[][][][] datas = ((double[][][][][]) objArray)[0];
                int num = 0;
//				int index = getLayerIndex(dataMap, elName, layer);
                data = datas[num];
            }
            result = new double[data.length][data[0].length][data[0][0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        if (data[i][j][k] == missValue) {
                           // result[i][j][k] = (double) DecodeConstants.UNDEF_INT_VALUE;
                        } else {
                            result[i][j][k] = data[i][j][k];
                        }
                    }
                }
            }
        }

        return result;
    }
}
