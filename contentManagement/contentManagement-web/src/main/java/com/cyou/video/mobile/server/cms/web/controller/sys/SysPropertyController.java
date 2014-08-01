package com.cyou.video.mobile.server.cms.web.controller.sys;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyou.video.mobile.server.cms.model.sys.SysProperty;
import com.cyou.video.mobile.server.cms.service.sys.SysPropertyService;
import com.cyou.video.mobile.server.cms.web.aspect.LogAnno;
import com.cyou.video.mobile.server.common.Constants;
import com.cyou.video.mobile.server.common.utils.HttpUtil;
import com.cyou.video.mobile.server.common.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 系统项controller
 * 
 * @author zs
 */
@Controller
@RequestMapping("/web/sys/sysProperty")
public class SysPropertyController {
  
  private Logger logger = LoggerFactory.getLogger(SysPropertyController.class);

  @Autowired
  private SysPropertyService sysPropertyService;
  
  @Value("${app.list}")
  private String getZqAppUrl;

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listPropertyPage() {
    return new ModelAndView("/system/sysProperty");
  }

  @LogAnno(type = Constants.LOG_TYPE_ADD,desc="为系统增加新的参数",modelName="系统参数")
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap createSysProperty(@RequestBody
  SysProperty sysPro, ModelMap model) {
    try {
      logger.info("[method: createSysProperty()] Create system property by params : "+JacksonUtil.getJsonMapper().writeValueAsString(sysPro));
      sysPropertyService.createSysProperty(sysPro);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: createSysProperty()] Create system property : error!"+e.getMessage(),e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "/list", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap listSysProperty(@RequestBody
  SysProperty sysPro, ModelMap model) {
    try {
      logger.info("[method: listSysProperty()] Get system property list by params : "+JacksonUtil.getJsonMapper().writeValueAsString(sysPro));
      List<SysProperty> proList = sysPropertyService.listSysProperty(sysPro);
      model.addAttribute("proList", proList);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: listSysProperty()] Get system property list : error!"+e.getMessage(),e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_UPDATE,desc="修改指定的系统参数信息",modelName="系统参数")
  @RequestMapping(value = "{id}", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updateSysProperty(@PathVariable("id")
  int id, @RequestBody
  SysProperty sysPro, ModelMap model) {
    try {
      sysPro.setId(id);
      logger.info("[method: updateSysProperty()] Update system property by params : "+JacksonUtil.getJsonMapper().writeValueAsString(sysPro));
      sysPropertyService.updateSysProperty(sysPro);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
      
    }
    catch(Exception e) {
      logger.error("[method: updateSysProperty()] Update system property : error!"+e.getMessage(),e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_UPDATE,desc="修改指定系统参数的状态",modelName="系统参数")
  @RequestMapping(value = "{id}/status", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updateStatus(@PathVariable("id")int id, @RequestParam("status")int status, @RequestParam("version")
  String version, @RequestParam("plat")int plat, @RequestParam("channel")String channel, ModelMap model) {
    try {
      if(StringUtils.isBlank(channel)){
        channel = "";
      }
      logger.info("[method: updateStatus()] Update system property status by params : {id="+id+",status="+status+",plat="+plat+",version="+version+",channel="+channel+"}");
      sysPropertyService.updateStatus(id, status, plat, version,channel);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: updateStatus()] Update system property status : error!"+e.getMessage(),e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_DELETE,desc="删除指定的系统参数",modelName="系统参数")
  @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap deleteSysProperty(@PathVariable("id")int id, @RequestParam("version")String version, @RequestParam("plat")int plat, @RequestParam("channel")String channel, ModelMap model) {
    try {
      if(StringUtils.isBlank(channel)){
        channel = "";
      }
      logger.info("[method: deleteSysProperty()] Delete system property  by params : {id="+id+",plat="+plat+",version="+version+",channel="+channel+"}");
      sysPropertyService.deleteSysProperty(id,plat,version,channel);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: deleteSysProperty()] Delete system property : error!"+e.getMessage(),e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }
  
  @RequestMapping(value = "/zqapp", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap listZqAppList(ModelMap model) {
    try {
      Map<String,String> map = new HashMap<String, String>();
      String data = HttpUtil.syncGet(getZqAppUrl, null, null, null);
      if(StringUtils.isNotBlank(data) && JacksonUtil.getJsonMapper().readTree(data).has("data")){
        Iterator<JsonNode> i = JacksonUtil.getJsonMapper().readTree(data).get("data").elements();
        while(i.hasNext()) {
          JsonNode v = i.next();
          String sx = v.get("androidOpenAppUrl").asText();
          if(StringUtils.isNotEmpty(sx) && sx.contains(".")){
            String name = v.get("appName").asText();
            map.put(sx.substring(sx.lastIndexOf(".")+1), name);
          }
        }
      }
      model.addAttribute("appList", map);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: listSysProperty()] Get system property list : error!"+e.getMessage(),e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

}
