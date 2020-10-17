package com.ymfomen.alimsg.Controller;


import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.ymfomen.alimsg.Utli.OperatorJudgeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/*
pom.xml
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.5.3</version>
</dependency>
*/
@RestController
@RequestMapping(value = "/indexMsg")
@PropertySource(value = {"classpath:ali/ali.properties"}, encoding="UTF-8")
public class AliMsgTestController {
    /**
     * 阿里短信服务 SebdSms服务
     *
     * @param request 前端请求参数
     * @return 返回成功或失败页面
     */
    @Value("${ali.AccessKeyID}")
    private  String AccessKeyID;
    @Value("${ali.AccessKeySecret}")
    private  String AccessKeySecret;
    @Value("${ali.SignName}")
    private  String SignName;
    @Value("${ali.TemplateCode}")
    private  String TemplateCode;
    //    ali.RegionId
    @Value("${ali.RegionId}")
    private  String RegionId;
    @Value("${ali.SysDomain}")
    private  String sysDomain;
    @Value("${ali.SysVersion}")
    private  String sysVersion;
    @Value("${ali.SendSms}")
    private  String sendSms;

    @RequestMapping(value = "/sendsms", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public String SendSms(HttpServletRequest request) {
        String phoneNumbers = request.getParameter("PhoneNumbers");
        String operator = OperatorJudgeUtil.checkOperator(phoneNumbers);
        if(operator.equals("telecom")) {
            System.out.println("该号码属于电信用户");
            return "indexDX";
        }else{
            String sendSms = SendSmsUtil(phoneNumbers);
            if (sendSms.equals("ok")) {
                return "/index";
            } else {
                return "/error";
            }
        }

    }

    /**
     * 阿里短信服务 SendSms接口工具类
     * @param phoneNumbers 需接收验证码的手机号
     * @return 返回ok则代表信息发送成功 否则失败
     */
    public String SendSmsUtil(String phoneNumbers) {
        setNewcode();
        String code = getNewcode();
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
                return "ok";
            } else {
              //  System.out.println("短信发送失败");
                return "error";
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return "error";
    }
    public void AddShortUrl() {
        DefaultProfile profile = DefaultProfile.getProfile(RegionId, AccessKeyID, AccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("AddShortUrl");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        try {
            CommonResponse response = client.getCommonResponse(request);
 //           System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }







    // 以下为测试代码，随机生成验证码
    private  int newcode;

    // 随机生成的4位验证码
    public String getNewcode() {
        return "{\"code\":\"" + newcode + "\"}";
    }

    public void setNewcode() {
        newcode = (int) (Math.random() * 9999) + 100;  //每次调用生成一次四位数的随机数
    }

}




//    /**
//     * 阿里短信服务 SendSms接口工具类
//     *
//     * @param phoneNumbers 需接收验证码的手机号
//     * @param code         验证码
//     * @return 返回ok则代表信息发送成功 否则失败
//     */
//    private String SendSmsUtil(String phoneNumbers, String code) {
//        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyID, AccessKeySecret);
//        IAcsClient client = new DefaultAcsClient(profile);
//        CommonRequest sendSmsRequest = new CommonRequest();
//        sendSmsRequest.setSysMethod(MethodType.POST);
//        sendSmsRequest.setSysDomain("dysmsapi.aliyuncs.com");
//        sendSmsRequest.setSysVersion("2017-05-25");
//        sendSmsRequest.setSysAction("SendSms");
//        sendSmsRequest.putQueryParameter("RegionId", "cn-hangzhou");
////        需接收短信的手机号码从前端传入
//        sendSmsRequest.putQueryParameter("PhoneNumbers", phoneNumbers);
////        短信签名名称 阿里短信服务提供
//        sendSmsRequest.putQueryParameter("SignName", SignName);
////        短信模版ID 阿里短信服务提供
//        sendSmsRequest.putQueryParameter("TemplateCode", TemplateCode);
////         可选:模板中的变量替换JSON串,如模板内容为"亲爱的用户,您的验证码为${code}"时,此处的值为
//        sendSmsRequest.putQueryParameter("TemplateParam", code);
//        try {
//            CommonResponse response = client.getCommonResponse(sendSmsRequest);
//            System.out.println(response.getData());
//            if (response.getData().equals("OK")) {
//                System.out.print("短信发送成功");
//                return "ok";
//            } else {
//                System.out.println("短信发送失败");
//                return "error";
//            }
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }
//        return "error";
//    }