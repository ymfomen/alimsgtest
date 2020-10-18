package com.ymfomen.alimsg.Controller;

import com.alibaba.fastjson.JSON;
import com.ymfomen.alimsg.Utli.AliMsgUtil;
import com.ymfomen.alimsg.Utli.OperatorJudgeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
@PropertySource(value = {"classpath:ali/ali.properties"}, encoding = "UTF-8")
public class AliMsgTestController {
    @Value("${ali.AccessKeyID}")
    private String AccessKeyID;
    @Value("${ali.AccessKeySecret}")
    private String AccessKeySecret;
    @Value("${ali.SignName}")
    private String SignName;
    @Value("${ali.TemplateCode}")
    private String TemplateCode;
    @Value("${ali.RegionId}")
    private String RegionId;
    @Value("${ali.SysDomain}")
    private String sysDomain;
    @Value("${ali.SysVersion}")
    private String sysVersion;
    @Value("${ali.SendSms}")
    private String sendSms;

    /**
     * 阿里云短信服务 获取短信验证码 将验证码存放至session中 设置5分钟有效期
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "/AliSendSms", method = RequestMethod.POST, produces = {"application/json"})
    public String AliSendSms(HttpServletRequest request, HttpSession session) {
        String phoneNumbers = request.getParameter("PhoneNumbers");
        String operator = OperatorJudgeUtil.checkOperator(phoneNumbers);
        if (!operator.equals("telecom")) {
            Map<String, String> map = AliMsgUtil.SendSmsUtil(RegionId, AccessKeyID, AccessKeySecret,
                    sysDomain, sysVersion, sendSms,
                    SignName, TemplateCode, phoneNumbers);
            String data = map.get("Data");
            String code = map.get("code");
            if (data.equals("ok")) {
                session.setAttribute(phoneNumbers, code);
                //TimerTask实现5分钟后从session中删除checkCode
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        session.removeAttribute(phoneNumbers);
                        System.out.println(phoneNumbers + "删除成功");
                        timer.cancel();
                    }
                }, 5 * 60 * 1000);
                return "index";
            } else {
                return "indexDx";
            }
        } else {
            return "该号码属于电信用户";
        }
    }

    /**
     * 请求提交验证短信验证码是否有效
     * @param request request
     * @param session session
     * @return
     */
    @RequestMapping(value = "/Submint", method = RequestMethod.POST, produces = {"application/json"})
    public String AliIndex(HttpServletRequest request, HttpSession session){
        String phoneNumbers = request.getParameter("PhoneNumbers");
        /**
         * 获取session中存放的验证码
         */
        Object attribute = session.getAttribute(phoneNumbers);
        if (attribute != null) {
//            JSON.parse(attribute.toString());
             Map maps = (Map)JSON.parse(attribute.toString());
            String sessionCode = (String) maps.get("code");
                    String code = request.getParameter("CODE");
                    if (code.equals(sessionCode)) {
                        return "/index";
                    } else {
                        return "验证码输入错误请重新输入";
                    }
            } else {
                return "请先获取验证码";
            }
    }
}