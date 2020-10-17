package com.ymfomen.alimsg.Controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/*
pom.xml
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.5.3</version>
</dependency>
*/
@Controller
@RequestMapping(value = "/indexMsg")
@PropertySource(value = {"classpath:ali/ali.properties"})
public class AliMsgTestController {

    @Value("${ali.AccessKeyID}")
    private static String AccessKeyID;
    @Value("${ali.AccessKeySecret}")
    private static String AccessKeySecret;
    @Value("${ali.SignName}")
    private static String SignName;
    @Value("${ali.TemplateCode}")
    private static String TemplateCode;

    @RequestMapping(value = "/sendsms",method = RequestMethod.POST,produces = {"application/json"})
    @ResponseBody
    public String SendSms(HttpServletRequest request) {

        String PhoneNumbers = request.getParameter("PHONENumbers");
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyID, AccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest sendSmsRequest = new CommonRequest();
        sendSmsRequest.setSysMethod(MethodType.POST);
        sendSmsRequest.setSysDomain("dysmsapi.aliyuncs.com");
        sendSmsRequest.setSysVersion("2017-05-25");
        sendSmsRequest.setSysAction("SendSms");
        sendSmsRequest.putQueryParameter("RegionId", "cn-hangzhou");
//        需接收短信的手机号码从前端传入
        sendSmsRequest.putQueryParameter("PhoneNumbers", PhoneNumbers);
//        短信签名名称 阿里短信服务提供
        sendSmsRequest.putQueryParameter("SignName", SignName);
//        短信模版ID 阿里短信服务提供
        sendSmsRequest.putQueryParameter("TemplateCode", TemplateCode);
        try {
            CommonResponse response = client.getCommonResponse(sendSmsRequest);
            System.out.println(response.getData());
            return response.getData();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return "null";
    }
}
