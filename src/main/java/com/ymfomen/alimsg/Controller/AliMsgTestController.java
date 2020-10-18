package com.ymfomen.alimsg.Controller;
import com.ymfomen.alimsg.Utli.AliMsgUtil;
import com.ymfomen.alimsg.Utli.OperatorJudgeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    @Value("${ali.AccessKeyID}")
    private  String AccessKeyID;
    @Value("${ali.AccessKeySecret}")
    private  String AccessKeySecret;
    @Value("${ali.SignName}")
    private  String SignName;
    @Value("${ali.TemplateCode}")
    private  String TemplateCode;
    @Value("${ali.RegionId}")
    private  String RegionId;
    @Value("${ali.SysDomain}")
    private  String sysDomain;
    @Value("${ali.SysVersion}")
    private  String sysVersion;
    @Value("${ali.SendSms}")
    private  String sendSms;
    @RequestMapping(value = "/AliSendSms",method = RequestMethod.POST,produces = {"application/json"})
    public String AliSendSms(HttpServletRequest request){
        String phoneNumbers = request.getParameter("PhoneNumbers");
        String operator = OperatorJudgeUtil.checkOperator(phoneNumbers);
        if(!operator.equals("telecom")) {
            Map<String, String> map = AliMsgUtil.SendSmsUtil(RegionId, AccessKeyID, AccessKeySecret,
                    sysDomain, sysVersion, sendSms,
                    SignName, TemplateCode, phoneNumbers);
            String data = map.get("Data");
            String code = map.get("code");
            if (data.equals("ok")) {
                request.setAttribute("code",code);
                return "index";
            } else {
                return "indexDx";
            }
        }else{
            return "该号码属于电信用户";
        }
    }
}