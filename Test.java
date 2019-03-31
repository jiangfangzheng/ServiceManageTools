package me.jfz.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.json.JSONUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 描述
 *
 * @author Sandeepin
 * @date 2019/3/31 0031
 */
public class Test {

    private static String winscpPath = null;

    private static String sshMsg = null;

    private static String localFilePath = null;

    private static String serviceFilePath = null;

    private static String filePath = null;

    private static String serviceName = null;

    private static Map<String, String[]> serviceMsgMap = null;

    private static Map<String, String[]> envMsgMap = null;

    public static void main(String[] args) {
        // 获取本地文件路径、微服务名
        if (args.length > 0) {
            filePath = args[0];
            serviceName = getServiceName(filePath);

        }
        // 获取微服务信息  服务名:微服务节点,部署包存放路径,服务安装路径
        serviceMsgMap = getConfigFileMsg("D:\\Sandeepin\\Desktop\\service.txt");
        // 获取环境信息 节点名:大网IP,小网IP,其它信息
        envMsgMap = getConfigFileMsg("D:\\Sandeepin\\Desktop\\env.txt");

        Console.log(filePath);
        Console.log(serviceName);
        Console.log(JSONUtil.toJsonStr(serviceMsgMap));
        Console.log(JSONUtil.toJsonStr(envMsgMap));

        // 上传文件
        winscpPath = "D:\\Soft\\Tools-Programming\\WinSCP\\WinSCP.exe";
        localFilePath = filePath;
        serviceFilePath = serviceMsgMap.get(serviceName)[1];
        System.out.println("localFilePath = " + localFilePath);
        System.out.println("serviceFilePath = " + serviceFilePath);
        String ip = envMsgMap.get("Manage")[0];
        sshMsg = "root:RbHvY2qyw1lt@" + ip + ":22";
        System.out.println("sshMsg = " + sshMsg);
        if (winscpPath != null && sshMsg != null && localFilePath != null && serviceFilePath != null) {
            startCMD(winscpPath, sshMsg, localFilePath, serviceFilePath);
        }

        System.out.println("Sandeepin poi");

    }

    public static String getServiceName(String filePath) {
        String serviceName = null;

        String[] arr = filePath.split("\\\\");
        String tmp = arr[arr.length - 1];
        arr = tmp.split("\\.");
        if (arr.length > 0) {
            tmp = arr[0];
            arr = tmp.split("-");
            if (arr.length > 0) {
                serviceName = arr[0];
            }
        }

        return serviceName;
    }

    public static Map<String, String[]> getConfigFileMsg(String cfgFilePath) {
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file(cfgFilePath));
        List<CsvRow> rows = data.getRows();

        Map<String, String[]> serviceMsgMap = new TreeMap<>();

        int i = 0;
        for (CsvRow csvRow : rows) {
            List<String> stringList = csvRow.getRawList();
            String[] arr = stringList.get(0).split("\t");
            if (arr.length < 4) {
                continue;
            }

            String[] serviceMsgArr = new String[3];
            for (int j = 0; j < 3; j++) {
                serviceMsgArr[j] = arr[j + 1];
            }
            serviceMsgMap.put(arr[0], serviceMsgArr);
            i++;
        }

        return serviceMsgMap;
    }


//    public static void test() {
//        TicTocUtil.tic();
//
//        CsvReader reader = CsvUtil.getReader();
//        // 从文件中读取CSV数据
//        CsvData data = reader.read(FileUtil.file("D:\\Sandeepin\\Desktop\\env.txt"));
//        List<CsvRow> rows = data.getRows();
//        // 遍历行
//        for (CsvRow csvRow : rows) {
//            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
//            List<String> stringList = csvRow.getRawList();
//            Console.log(stringList.size());
//        }
//
//        TicTocUtil.toc();
//
//    }

    public static void startCMD(String winscpPath, String sshMsg, String localFilePath, String serviceFilePath) {
        String command = "cmd /k start {0} /console /command \"option batch continue\" \"option confirm off\" \"open " +
                "sftp://{1}\" \"option transfer binary\" \"put {2} {3}\" \"\" /log=log_file.txt";
        command = command.replace("{0}", winscpPath);
        command = command.replace("{1}", sshMsg);
        command = command.replace("{2}", localFilePath);
        command = command.replace("{3}", serviceFilePath);
        Console.log(command);

        try {
            Runtime run = Runtime.getRuntime();
            run.exec(command);
        } catch (IOException e) {
            System.out.println("startCMD{} ERROR!");
            e.printStackTrace();
        }
    }
}
