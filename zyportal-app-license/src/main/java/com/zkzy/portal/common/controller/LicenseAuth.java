package com.zkzy.portal.common.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by paul on 2018/7/12.
 */
@Configuration
public class LicenseAuth {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseAuth.class);

    private static final int SPLITLENGTH = 4;


    public static String appid;

    public static String path;

    @Value("${spring.application.name}")
    public void setAppid(String appid) {
        Properties prop = new Properties();
        InputStream inputStream = null;
        String instanceName = "";
        String loadPath = "";
        try {
            // 此处的getClassLoader不能少，否则回报nullpointer异常           
            inputStream = this.getClass().getClassLoader().getResourceAsStream("application.yml");
            // 加载属性列表
            prop.load(inputStream);
            instanceName = prop.getProperty("name");
            int a = instanceName.indexOf("#");
            if (a > 0) {
                instanceName = instanceName.substring(0, a).trim();
            }

            String active = prop.getProperty("active");
            inputStream = this.getClass().getClassLoader().getResourceAsStream("application-" + active + ".yml");
            prop.load(inputStream);
            loadPath = prop.getProperty("work-folder-name");


            int b = loadPath.indexOf("#");
            if (b > 0) {
                loadPath = loadPath.substring(0, b).trim();
            }
            inputStream.close();
            File file = new File(loadPath + File.separator + "lic" + File.separator + instanceName);
            //如果文件夹不存在则创建
            if (!file.exists() && !file.isDirectory()) {
                LOGGER.info("目录创建成功");
                file.mkdirs();
            } else {
                LOGGER.info("目录已存在");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LicenseAuth.path = loadPath;
        LicenseAuth.appid = instanceName;
    }

    public static String getMachineCode() throws Exception {
        Set<String> result = new HashSet<>();
        String mac = getMac();
/*        result.add(mac);
        Properties props = System.getProperties();
        String javaVersion = props.getProperty("java.version");
        result.add(javaVersion);
        String javaVMVersion = props.getProperty("java.vm.version");
        result.add(javaVMVersion);
        String osVersion = props.getProperty("os.version");
        result.add(osVersion);
        String code = Encrpt.GetMD5Code(result.toString());*/
        return mac;

    }

    public static String auth(String machineCode) throws Exception {
        String newCode = "(sy@zy-iot.com)["
                + machineCode.toUpperCase() + "](ZKZY)";
        String code = Encrpt.GetMD5Code(newCode)
                .toUpperCase() + machineCode.length();
        return getSplitString(code);
    }

    private static HashMap<String, String> genDataFromArrayByte(byte[] b) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(b)));
        HashMap<String, String> data = new HashMap<String, String>();
        String str = null;
        while ((str = br.readLine()) != null) {
            if (StringUtils.isNotEmpty(str)) {
                str = str.trim();
                int pos = str.indexOf("=");
                if (pos <= 0) continue;
                if (str.length() > pos + 1) {
                    data.put(str.substring(0, pos).trim().toUpperCase(), str.substring(pos + 1).trim());
                } else {
                    data.put(str.substring(0, pos).trim().toUpperCase(), "");
                }
            }
        }
        return data;
    }

    private static String getSplitString(String str) {
        return getSplitString(str, "-", SPLITLENGTH);
    }

    private static String getSplitString(String str, String split, int length) {
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i % length == 0 && i > 0) {
                temp.append(split);
            }
            temp.append(str.charAt(i));
        }
        String[] attrs = temp.toString().split(split);
        StringBuilder finalMachineCode = new StringBuilder();
        for (String attr : attrs) {
            if (attr.length() == length) {
                finalMachineCode.append(attr).append(split);
            }
        }
        String result = finalMachineCode.toString().substring(0,
                finalMachineCode.toString().length() - 1);
        return result;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static String getMac() {
        try {
      /*      Enumeration<NetworkInterface> el = NetworkInterface
                    .getNetworkInterfaces();
            while (el.hasMoreElements()) {
                byte[] mac = el.nextElement().getHardwareAddress();
                if (mac == null)
                    continue;
                String hexstr = bytesToHexString(mac);
                return getSplitString(hexstr, "-", 2).toUpperCase();
            }*/
            return appid;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

/*    public static void getLicense(String isNoTimeLimit, String licenseLimit, String machineCode, String licensePath) throws Exception {
        String[] liccontent = {
                "LICENSEID=sy@zy-iot.com",
                "LICENSENAME=zkzylicense",
                MessageFormat.format("LICENSETYPE={0}", isNoTimeLimit),
                MessageFormat.format("EXPIREDAY={0}", licenseLimit), //日期采用yyyy-MM-dd日期格式
                MessageFormat.format("MACHINECODE={0}", machineCode),
                ""
        };

        //将lic内容进行混合签名并写入内容
        StringBuilder sign = new StringBuilder();
        for (String item : liccontent) {
            sign.append(item + "zkzylog");
        }
        liccontent[5] = MessageFormat.format("LICENSESIGN={0}", Encrpt.GetMD5Code(sign.toString()));
        FileUtil.createFileAndWriteLines(licensePath, liccontent);
        //将写入的内容整体加密替换
        String filecontent = FileUtil.readFileToString(licensePath);
        String encrptfilecontent = Encrpt.EncriptWRSA_Pri(filecontent);
        File file = new File(licensePath);
        file.delete();
        FileUtil.createFile(licensePath, encrptfilecontent);
    }*/

    public static boolean authLicense() {
        boolean isauth = false;
        try {
            Encrpt encrpt = new Encrpt();
            //获取证书路径
            String pubkpath = "/META-INF/Zkzylogkey.crt";

            //获取里出license路径
            String licpath = path + File.separator + "lic" + File.separator + appid + File.separator;
            //获取license流
            String[] filelist = encrpt.getPathInputStream(licpath);

            if (filelist.length > 0) {
                for (int i = 0; i < filelist.length; i++) {
                    if (filelist[i].contains(".lic")) {
                        File readfile = new File(licpath + filelist[i]);
                        if (readfile.isFile()) {
                            String liccontent = FileUtil.readFileToString(readfile);
                            InputStream myins2 = encrpt.getPathInputStream2(pubkpath);
                            String decriptliccontent = Encrpt.DecriptWithRSA_Pub2(liccontent, myins2);
                            myins2.close();
                            HashMap<String, String> props = genDataFromArrayByte(decriptliccontent.getBytes());
                            String licenseid = java.net.URLDecoder.decode(props.get("LICENSEID"), "utf-8");
                            String licensename = java.net.URLDecoder.decode(props.get("LICENSENAME"), "utf-8");
                            String licensetype = java.net.URLDecoder.decode(props.get("LICENSETYPE"), "utf-8");
                            String liclimit = java.net.URLDecoder.decode(props.get("EXPIREDAY"), "utf-8");
                            String machinecode = java.net.URLDecoder.decode(props.get("MACHINECODE"), "utf-8");
                            String lincensesign = java.net.URLDecoder.decode(props.get("LICENSESIGN"), "utf-8");
                            //验证签名
                            String allinfogroup = "LICENSEID=" + licenseid + "zkzylog" + "LICENSENAME=" + licensename + "zkzylog" +
                                    "LICENSETYPE=" + licensetype + "zkzylog" + "EXPIREDAY=" + liclimit + "zkzylog" + "MACHINECODE=" + machinecode + "zkzylogzkzylog";

                            if (lincensesign.equals(Encrpt.GetMD5Code(allinfogroup))) {
                                //验证机器码
                                if (getMachineCode().equals(machinecode)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date bt = new Date();
                                    Date et = sdf.parse(liclimit);
                                    //验证时间

                                    if ("0".equals(licensetype) || bt.compareTo(et) <= 0) {
                                        isauth = true;
                                        LOGGER.info("证书:" + filelist[i] + "已验证");
                                        LOGGER.info("证书到期时间:"+liclimit);
                                        break;
                                    } else {
                                        LOGGER.error("证书:" + filelist[i] + "过期");
                                    }
                                } else {
                                    LOGGER.error("证书:" + filelist[i] + "机器码不一致");
                                }
                            } else {
                                LOGGER.error("证书:" + filelist[i] + "签名不一致");
                            }
                        }
                    }
                }
            } else {
                LOGGER.error("证书不存在");
                return false;
            }
            return isauth;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("签名读取失败");
            return false;
        }

    }
}
