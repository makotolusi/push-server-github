package com.cyou.video.mobile.server.cms.web.controller.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyou.video.mobile.server.cms.model.Pagination;
import com.cyou.video.mobile.server.cms.model.sys.MailAddressee;
import com.cyou.video.mobile.server.cms.model.sys.MailInfo;
import com.cyou.video.mobile.server.cms.service.sys.MailAddresseeService;
import com.cyou.video.mobile.server.cms.service.sys.MailInfoService;
import com.cyou.video.mobile.server.cms.web.aspect.LogAnno;
import com.cyou.video.mobile.server.common.Constants;
import com.cyou.video.mobile.server.common.Constants.CUSTOM_ERROR_CODE;
import com.cyou.video.mobile.server.common.utils.JacksonUtil;
import com.cyou.video.mobile.server.common.utils.MailUtil;

/**
 * 邮件信息controller
 * @author jyz
 */
@Controller
@RequestMapping("/web/sys/mail")
public class MailInfoController {

  private Logger logger = LoggerFactory.getLogger(MailInfoController.class);

  @Autowired
  private MailInfoService mailInfoService;

  @Autowired
  private MailAddresseeService mailAddresseeService;

  @RequestMapping(value = "info", method = RequestMethod.GET)
  public ModelAndView listMailPage() {
    return new ModelAndView("/system/mailInfo");
  }

  @RequestMapping(value = "info", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap listMail(@RequestBody Map<String, String> params, ModelMap model) {
    try {
      String project = params.get("project");
      int status = Integer.parseInt(params.get("status"));
      int curPage = Integer.parseInt(params.get("curPage"));
      logger.info("[method: listMail()] Get mail info list by params:{project=" + project + ", status=" + status + ", curPage=" + curPage + "}");
      Pagination pagination = mailInfoService.listMailInfo(status, project, curPage, Pagination.PAGESIZE);
      model.addAttribute("page", pagination);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: listMail()] Get mail info list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "info/{id}/reSend", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap reSendMail(@PathVariable("id") int id, ModelMap model) {
    try {
      logger.info("[method: reSendMail()] send mail again by params: {id=" + id + "}");
      MailInfo mailInfo = mailInfoService.getMailInfo(id);
      if(mailInfo != null) {
        List<Map<String, String>> toList = mailInfo.getAddresseeMap();
        List<String> mailList = new ArrayList<String>();
        for(Map<String, String> map : toList) {
          mailList.add(map.get("email"));
        }
        CUSTOM_ERROR_CODE code = MailUtil.sendMail(mailInfo.getTitle(), mailInfo.getContent(), mailList.toArray(new String[0]), true);
        model.addAttribute("message", code.toString());
      }
    }
    catch(Exception e) {
      logger.error("[method: reSendMail()] send mail again : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "project", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap getProject(ModelMap model) {
    try {
      logger.info("[method: getProject()] get project");
      List<String> list = mailAddresseeService.getProjectList();
      model.addAttribute("projectList", list);
    }
    catch(Exception e) {
      logger.error("[method: getProject()] get project : error! " + e.getMessage(), e);
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "project/{project}/type", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap getType(@PathVariable("project") String project, ModelMap model) {
    try {
      logger.info("[method: getType()] get type list by project : " + project);
      List<String> list = mailAddresseeService.getTypeListByProject(project);
      model.addAttribute("typeList", list);
    }
    catch(Exception e) {
      logger.error("[method: getType()] get type list by project : error! " + e.getMessage(), e);
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "project/{project}/type/{type}/addressee", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap getAddressee(@PathVariable("project") String project, @PathVariable("type") String type, ModelMap model) {
    try {
      logger.error("[method: getAddressee()] get addressee list by project : " + project + " and type : " + type);
      List<MailAddressee> list = mailAddresseeService.getMailAddresseeList(project, type);
      model.addAttribute("addresseeList", list);
    }
    catch(Exception e) {
      logger.error("[method: getAddressee()] get addressee list by project and type : error! " + e.getMessage(), e);
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_ADD, desc = "增加收件人", modelName = "邮件管理")
  @RequestMapping(value = "addressee/create", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap createAddressee(@RequestBody MailAddressee mailAddressee, ModelMap model) {
    try {
      logger.info("[method: createAddressee()] create addressee : " + JacksonUtil.getJsonMapper().writeValueAsString(mailAddressee));
      int id = mailAddresseeService.createMailAddressee(mailAddressee);
      model.addAttribute("id", id);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: createAddressee()] create addressee : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_UPDATE, desc = "更新指定收件人信息", modelName = "邮件管理")
  @RequestMapping(value = "addressee/{id}/update", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updateAddressee(@PathVariable("id") int id, @RequestBody MailAddressee mailAddressee, ModelMap model) {
    try {
      logger.info("[method: updateAddressee()] update addressee : " + JacksonUtil.getJsonMapper().writeValueAsString(mailAddressee));
      mailAddressee.setId(id);
      mailAddresseeService.updateMailAddressee(mailAddressee);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: updateAddressee()] update addressee : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_DELETE, desc = "删除指定收件人", modelName = "邮件管理")
  @RequestMapping(value = "addressee/{id}/delete", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap deleteAddressee(@PathVariable("id") int id, ModelMap model) {
    try {
      logger.info("[method: deleteAddressee()] delete addressee : " + id);
      mailAddresseeService.deleteMailAddressee(id);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: deleteAddressee()] delete addressee : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }
}
