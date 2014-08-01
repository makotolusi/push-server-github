package com.cyou.video.mobile.server.cms.web.aspect;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.model.security.Manager;
import com.cyou.video.mobile.server.cms.model.sys.ManagerLog;
import com.cyou.video.mobile.server.cms.service.sys.ManagerLogService;
import com.cyou.video.mobile.server.common.utils.HttpUtil;
import com.cyou.video.mobile.server.common.utils.JacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

@Aspect
@Component
public class ManagerLogAspect {

  @Autowired
  private ManagerLogService userlogInfoService;

  @AfterReturning("within(com.cyou.video.mobile.server.cms.web.controller..*) && @annotation(log)")
  public void addUserLog(JoinPoint jp, LogAnno log) {
    try {
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest(); 
      Object[] parames = jp.getArgs();// 获取目标方法体参数
      String params = parseParames(parames); // 解析目标方法体的参数
      String signature = jp.getSignature().toString();// 获取目标方法签名
      String name = signature.substring(signature.lastIndexOf(".") + 1, signature.indexOf("("));
      String url = request.getRequestURI();
      String ip = HttpUtil.getIP(request);
      
      ManagerLog logInfo =new ManagerLog();
      logInfo.setType(log.type());
      logInfo.setDesc(log.desc());
      logInfo.setParams(params);
      logInfo.setName(name);
      logInfo.setModelName(log.modelName());
      logInfo.setUrl(url);
      logInfo.setUserIp(ip);
      logInfo.setCreateDate(new Date(System.currentTimeMillis()));
      Manager manager = (Manager) request.getSession().getAttribute(Consts.SESSION_MANAGER);
      if(manager != null){
        logInfo.setCreateUser(manager.getUsername());
        userlogInfoService.createUserLog(logInfo);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  @AfterThrowing(pointcut="within(com.cyou.video.mobile.server.cms.web.controller..*) && @annotation(log)", throwing="ex") 
  public void addUserLogError(JoinPoint jp,LogAnno log,Exception ex){ //如果拦截的方法中已经对异常进行处理了，就走不到此方法
    try {
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest(); 
      Object[] parames = jp.getArgs();// 获取目标方法体参数
      String params = parseParames(parames); // 解析目标方法体的参数
      String signature = jp.getSignature().toString();// 获取目标方法签名
      String name = signature.substring(signature.lastIndexOf(".") + 1, signature.indexOf("("));
      String url = request.getRequestURI();
      String ip = getIpAddrByRequest(request);
      
      ManagerLog logInfo =new ManagerLog();
      logInfo.setType(log.type());
      logInfo.setDesc(log.desc());
      logInfo.setParams(params);
      logInfo.setName(name);
      logInfo.setModelName(log.modelName());
      logInfo.setUrl(url);
      logInfo.setUserIp(ip);
      logInfo.setCreateDate(new Date(System.currentTimeMillis()));
      Manager manager = (Manager) request.getSession().getAttribute(Consts.SESSION_MANAGER);
      if(manager != null){
        logInfo.setCreateUser(manager.getUsername());
        userlogInfoService.createUserLog(logInfo);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 解析方法参数
   * @param parames 方法参数
   * @return 解析后的方法参数
   * @throws JsonProcessingException 
   */
  private String parseParames(Object[] parames) throws JsonProcessingException {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < parames.length; i++) {
      if(parames[i] instanceof HttpServletRequest || parames[i] instanceof MultipartHttpServletRequest){//这些没办法解析成json，只能跳过
        continue;
      }
      else {
        String json = JacksonUtil.getJsonMapper().writeValueAsString(parames[i]);
        if(i == parames.length - 1) {
          sb.append(json);
        }
        else {
          sb.append(json + ",");
        }
      }
    }
    String params = sb.toString();
    params = params.replaceAll("(\"\\w+\":\"\",)", "");
    params = params.replaceAll("(,\"\\w+\":\"\")", "");
    return params;
  }
  
  /**
   * 解析当前访问IP
   * @param request
   * @return
   */
  public String getIpAddrByRequest(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

}
