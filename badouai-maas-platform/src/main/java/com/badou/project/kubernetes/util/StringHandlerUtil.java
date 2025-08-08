package com.badou.project.kubernetes.util;

import com.alibaba.fastjson.JSONArray;
import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.DictionarySelect;
import com.badou.brms.util.InstanceFactory;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelwarehouse.model.WareHouseVllmParamEntity;
import com.badou.tools.common.encrypt.RsaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import com.badou.tools.common.util.StringUtils;
import org.springframework.security.core.parameters.P;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName StringUtil
 * @Description 字符串处理工具类
 * @date 2022/9/23 11:39
 * @Version 1.0
 */
@Slf4j
public class StringHandlerUtil extends StringUtils {
    /**
     * 整数改成K
     * 4000 转成 4K
     * 4096 转成 4K
     * 30000 转成 30K
     * 130000 转成130K
     * 400 转成0.4K
     * 100 转成0.1K
     * 小于100 转成0k
     *
     * @param number 数字
     * @return 转换的数字
     */
    public static double convertToK(double number) {
        if (number < 100) {
            return 0;
        }
        return number / 1000;
    }

    /**
     * 跟上面的相反的 用来还原
     * K 改成整数
     * 4K 转成 4000
     * 0.4K 转成 400
     * 0.1K 转成 100
     * 0K 转成 0
     *
     * @param kNumber K 单位的数字
     * @return 转换的整数
     */
    public static int convertFromK(double kNumber) {
        return (int) (kNumber * 1000);
    }

    /**
     * 替换占位符
     *
     * @param paramsMap
     * @param content
     * @return
     * @throws Exception
     */
    public static String replaceVarParams(Map<String, String> paramsMap, String content) throws Exception {
        if (content.contains("{") && content.contains("}")) {
            String value = content;
            int startK = value.indexOf("{");
            int endK = value.indexOf("}");
            String paramName = value.substring(startK + 1, endK);
            String result = paramsMap.get(paramName);
            if (result == null) {
                throw new Exception("参数" + paramName + "无效!请确认字段设置的值正确!");
            }
            return value.substring(0, startK) + result + value.substring(endK + 1);
        }
        return content;
    }

    /**
     * 判断当前的值是小数还是整数 不要.0的值
     *
     * @param a
     * @return
     */
    public static Object convertDouble(Double a) {
        if (a == Math.floor(a) && !Double.isInfinite(a)) {
            return a.intValue();
        }
        return a;
    }

    /**
     * 构建VLLM参数
     *
     * @param params
     * @return
     */
    public static String buildVllmParams(List<WareHouseVllmParamEntity> params) throws Exception {
        StringBuilder builder = new StringBuilder();
        //预准备选项 用来判断值是否合法
        Map<String, String> selectionMap = new HashMap<>();
        DictionaryLib.getDictionaryByCode(MaasConst.DIC_VLLM_PARAMS_SELECTIONS).getItems().forEach(e -> {
            selectionMap.put(e.getCode(), e.getValue());
        });

        //参考格式
        //--dtype=half  ${QUANTIZATION_MEHOD} --trust-remote-code --gpu-memory-utilization 1
        for (WareHouseVllmParamEntity param : params) {
            if ((GlobalConsts.ZERO + "").equals(param.getValue())) {
                if (!"ENV_MAX-PROMPT-ADAPTER-TOKEN".equals(param.getCode())) {
                    log.info(param.getCode() + "的值为" + GlobalConsts.ZERO + ",默认值不参与任务.跳过该参数");
                    continue;
                }
            }
            if (StringUtils.isEmpty(param.getValue())) {
                log.info(param.getCode() + "的值为空.空值不参与任务.跳过该参数");
                continue;
            }
            if (param.getValue().contains("自动控制") || param.getName().contains("parallel-size")) {
                log.info(param.getCode() + "的值为显卡自动值.该值不参与任务.跳过该参数");
                continue;
            }
            if ("ON".equals(param.getValue()) || "OFF".equals(param.getValue())) {
                //如果在VLLM选项字典里 代表是支持开关的 如果不在 只在VLLM 代表一般是输入框 禁止输入ON OFF
                if (selectionMap.get(param.getCode()) == null) {
                    throw new DataValidException(param.getCode() + "设置了非法的参数");
                }
            }
            String value = param.getValue();
            String leftStr = "--" + param.getCode().replace("ENV_", "").toLowerCase();
            String right = "=" + value + "  ";
            if (value.equals("${ON}") || value.equals("ON")) {
                builder.append(leftStr + "  ");
                continue;
            }
            if (value.equals("${OFF}") || value.equals("OFF")) {
                continue;
            }
            builder.append(leftStr + right);
        }
        return builder.toString();
    }

    /**
     * 拼接多机多卡情况下的rank
     *
     * @param name
     * @param rank
     * @return
     */
    public static String createSlaveName(String name, Integer rank) {
        return name + "-slave" + rank;
    }

    /**
     * 移除最后一个逗号
     *
     * @return
     */
    public static String removeLastComma(String value) {
        if (StringUtils.isNotBlank(value)) {
            char c = value.charAt(value.length() - 1);
            if (",".equals(c + "")) {
                return value.substring(0, (value.length() - 1));
            }
        }
        return value;
    }

    /**
     * 使用 AES 算法进行加密并将结果转换为 Base64 编码
     *
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 转换AI提供的JSONArray
     *
     * @param feedbackx'c'x'c'x'c
     * @return
     */
//    public static JSONArray coverJsonArray(String feedback) {
//        feedback = feedback.replace("```json", "");
//        feedback = feedback.replace("```", "");
//        feedback = feedback.replace("“", "");
//        System.out.println("回答");
//        System.out.println(feedback);
//        System.out.println("回答");
//        if (!feedback.contains("[")) {
//            feedback = "[" + feedback;
//        }
//        if (!feedback.contains("]")) {
//            feedback = feedback + "]";
//        }
//        if ("[]".equals(feedback)) {
//            return new JSONArray();
//        }
//        try {
//            int start = feedback.indexOf("[");
//            int end = feedback.indexOf("]");
//            if (start != -1 && end != -1) {
//                feedback = feedback.substring(start, end + 1);
//            }
//            return JSONArray.parseArray(feedback);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new JSONArray();
//        }
//    }

    /**
     * @deprecated IOUtils.readAll
     */
    public static String readTextAli(InputStream inputStream) {
        try {
            InputStreamReader dataReader = new InputStreamReader(inputStream);
            String result = IOUtils.toString(dataReader);
            dataReader.close();
            inputStream.close();
            return result;
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    /**
     * 检查字符串是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 检查是否包含大写字母
     *
     * @param text
     * @return
     */
    public static boolean containsUpperCase(String text) {
        if (text == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[A - Z]");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * 检查字符串首字是否是字母
     *
     * @param str 检查的字符串
     * @return 是返回true 否返回false
     */
    public static boolean containsAlphabetic(String str) {
        return str.matches("^[a-zA-Z].*");
    }

    /**
     * 检查AI返回的AI数组 老是格式不对 这里来处理
     *
     * @param data
     * @return
     */
    public static String checkAiJsonArray(String data) {
        data = data.replace("```json", "");
        data = data.replace("```", "");
        data = data.replace("“", "");
        if (!data.contains("[")) {
            data = "[" + data;
        }
        if (!data.contains("]")) {
            data = data + "]";
        }
        int start = data.indexOf("[");
        int end = data.indexOf("]");
        if (start != -1 && end != -1) {
            data = data.substring(start, end + 1);
        }
        return data;
    }

    /**
     * 移除字符串第一个或最后一个, 如果是,的话
     *
     * @param str
     * @return
     */
    public static String removeFirstOrLastPoint(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        char c = str.charAt(str.length() - 1);
        if (",".equals(c + "")) {
            return str.substring(0, (str.length() - 1));
        }
        char firstC = str.charAt(0);
        if (",".equals(firstC + "")) {
            return str.substring(1);
        }

        return str;
    }

    /**
     * 根据字节大小转换成对应的合适单位
     *
     * @param size 原大小
     * @return 转换完成的字符串
     */
    public static String getNetFileSizeToMB(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");

        if (size >= 1024L * 1024L * 1024L * 1024L) {
            double i = (size / (1024.0 * 1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("T");
        } else if (size >= 1024L * 1024L * 1024L) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("G");
        } else if (size >= 1024L * 1024L) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("M");
        } else if (size >= 1024L) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

    public static long parseNetFileSizeToByte(String sizeStr) {
        if (sizeStr == null || sizeStr.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: empty string");
        }

        // 更新正则表达式以支持 G 和 T 单位
        Pattern pattern = Pattern.compile("^([0-9.]+)(T|G|M|KB|B)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sizeStr);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid format: " + sizeStr);
        }

        // 提取数值和单位
        double value = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toUpperCase();

        long bytes = 0;
        switch (unit) {
            case "M":
                bytes = (long) (value * 1024 * 1024);      // 1M = 1024KB = 1024^2 B
                break;
            case "G":
                bytes = (long) (value * 1024 * 1024 * 1024); // 1G = 1024MB = 1024^3 B
                break;
            case "T":
                bytes = (long) (value * 1024 * 1024 * 1024 * 1024); // 1T = 1024GB = 1024^4 B
                break;
            case "KB":
                bytes = (long) (value * 1024);            // 1KB = 1024B (1024^1 B)
                break;
            case "B":
                bytes = (long) value;                     // 1B = 1B
                break;
            default:
                throw new IllegalArgumentException("Unsupported unit: " + unit);
        }

        return bytes;
    }

}
