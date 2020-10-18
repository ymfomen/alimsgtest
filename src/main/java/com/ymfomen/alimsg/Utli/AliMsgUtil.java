package com.ymfomen.alimsg.Utli;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里短信服务工具类
 */
public class AliMsgUtil {
    // 以下为测试代码，随机生成验证码
    private static int newcode;
    // 随机生成的4位验证码
    public static String getNewcode() {
        return "{\"code\":\"" + newcode + "\"}";
    }
    public static void setNewcode() {
        newcode = (int) (Math.random() * 9999) + 100;  //每次调用生成一次四位数的随机数
    }

    /**
     * 阿里云短信服务接口工具类
     * @param RegionId RegionId
     * @param AccessKeyID AccessKeyID
     * @param AccessKeySecret AccessKeySecret
     * @param sysDomain sysDomain
     * @param sysVersion sysVersion
     * @param sendSms sendSms
     * @param SignName SignName
     * @param TemplateCode TemplateCode
     * @param phoneNumbers phoneNumbers
     * @return
     */
    public static Map<String ,String> SendSmsUtil(String RegionId, String AccessKeyID, String AccessKeySecret,
                                  String sysDomain, String sysVersion, String sendSms,
                                  String SignName, String TemplateCode, String phoneNumbers) {
        setNewcode();
        String code = getNewcode();
        HashMap<String, String> aliHashMap = new HashMap<>();
        DefaultProfile profile = DefaultProfile.getProfile(RegionId, AccessKeyID, AccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest sendSmsRequest = new CommonRequest();
        sendSmsRequest.setSysMethod(MethodType.POST);
        sendSmsRequest.setSysDomain(sysDomain);
        sendSmsRequest.setSysVersion(sysVersion);
        sendSmsRequest.setSysAction(sendSms);
        sendSmsRequest.putQueryParameter("RegionId", RegionId);
//        需接收短信的手机号码从前端传入
        sendSmsRequest.putQueryParameter("PhoneNumbers", phoneNumbers);
//        短信签名名称 阿里短信服务提供
        sendSmsRequest.putQueryParameter("SignName", SignName);
//        短信模版ID 阿里短信服务提供
        sendSmsRequest.putQueryParameter("TemplateCode", TemplateCode);
//         可选:模板中的变量替换JSON串,如模板内容为"亲爱的用户,您的验证码为${code}"时,此处的值为
        sendSmsRequest.putQueryParameter("TemplateParam", code);
        try {
            CommonResponse response = client.getCommonResponse(sendSmsRequest);
            String data = response.getData();
            HashMap hashMap = JSON.parseObject(data, HashMap.class);
            Object msg = hashMap.get("Code");
            // System.out.println(msg);
            if (msg.equals("OK")) {
                //System.out.print("短信发送成功");
                aliHashMap.put("code",code);
                aliHashMap.put("Data","ok");
                return aliHashMap;
            } else {
                //  System.out.println("短信发送失败");
                aliHashMap.put("Data","短信发送失败");
                return aliHashMap;
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return aliHashMap;
    }
}
