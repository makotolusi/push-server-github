package com.cyou.video.mobile.server.cms.web.controller.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.model.security.ManageItem;
import com.cyou.video.mobile.server.cms.model.security.Manager;
import com.cyou.video.mobile.server.cms.model.security.Operation;
import com.cyou.video.mobile.server.cms.service.security.ManageItemService;
import com.cyou.video.mobile.server.cms.service.security.ManagerService;
import com.cyou.video.mobile.server.cms.web.aspect.LogAnno;
import com.cyou.video.mobile.server.common.Constants;
import com.cyou.video.mobile.server.common.utils.JacksonUtil;

/**
 * CMS管理员controller
 * 
 * @author jyz
 */
@Controller
@RequestMapping("/web/manager")
public class ManagerController {
  
  private Logger logger=LoggerFactory.getLogger(ManagerController.class);

  @Autowired
  private ManagerService managerService;

  @Autowired
  private ManageItemService manageItemService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ModelAndView login(@RequestPart Manager manager, HttpServletRequest request) {
    ModelAndView view = null;
    try {
      List<Operation> operations = managerService.login(manager, request);
      if(operations != null && operations.size() > 0) { // 如果获取操作项列表并登录成功
        List<ManageItem> itemList = manageItemService.listManageItem();
        if(itemList != null && itemList.size() > 0) { // 如果获取管理项成功
          view = new ModelAndView("/ui/index"); // 跳转到欢迎页面
          view.addObject("operations", JacksonUtil.getJsonMapper().writeValueAsString(operations));
          view.addObject("manageItems", JacksonUtil.getJsonMapper().writeValueAsString(itemList));
        }
        else {
          view = new ModelAndView("/login").addObject("message", "加载管理项失败");
        }
      }
      else {
        view = new ModelAndView("/login").addObject("message", "加载操作项失败");
      }
    }
    catch(Exception e) {
      logger.error("[method: login()] Manager login : error! " + e.getMessage(), e);
      view = new ModelAndView("/login").addObject("message", e.getMessage());
      e.printStackTrace();
    }
    return view;
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public ModelAndView logout(HttpServletRequest request) {
    request.getSession().setAttribute(Consts.SESSION_MANAGER, null);
    return new ModelAndView("/logout");
  }

  @RequestMapping(value = "/password/edit", method = RequestMethod.GET)
  public ModelAndView editPasswordPage() {
    return new ModelAndView("/security/editPassword");
  }

  @RequestMapping(value = "/password/edit", method = RequestMethod.POST)
  public ModelAndView editPassword(@RequestParam("password") String password, HttpServletRequest request) {
    ModelAndView view = new ModelAndView("/security/editPassword");
    try {
      managerService.editPassword(password, request);
      view.addObject("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: editPassword()] Edit password : error! " + e.getMessage(), e);
      view.addObject("message", e.getMessage());
      e.printStackTrace();
    }
    return view;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listManagerPage() {
    return new ModelAndView("/security/listManager");
  }

  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public ModelMap listManager(@RequestParam("status") int status, ModelMap model, HttpServletRequest request) {
    try {
      logger.info("[method: listManager()] Get manager list by params : {status=" + status + "}");
      List<Manager> list = managerService.listManager(status, request);
      model.addAttribute("list", list);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: listManager()] Get manager list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_ADD, desc = "创建新管理员", modelName = "管理员管理")
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap createManager(@RequestBody Manager manager, ModelMap model) {
    try {
      logger.info("[method: createManager()] Create manager by params : " + JacksonUtil.getJsonMapper().writeValueAsString(manager));
      int id = managerService.createManager(manager);
      model.addAttribute("id", id);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: createManager()] Create manager : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_UPDATE, desc = "密码重置", modelName = "管理员管理")
  @RequestMapping(value = "{id}/password/reset", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap passwordReset(@PathVariable("id") int managerId, ModelMap model) {
    try {
      logger.info("[method: passwordReset()] Reset manager password by params : {managerId=" + managerId + "}");
      managerService.resetPassword(managerId);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: passwordReset()] Reset manager password : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_UPDATE, desc = "更新指定管理员状态", modelName = "管理员管理")
  @RequestMapping(value = "{id}/status", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updateStatus(@PathVariable("id") int managerId, @RequestParam("status") int status, ModelMap model) {
    try {
      logger.info("[method: updateStatus()] Update manager Status by params : {managerId=" + managerId + ",status=" + status + "}");
      managerService.updateStatus(managerId, status);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: updateStatus()] Update manager Status : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_UPDATE, desc = "更新制定管理员信息", modelName = "管理员管理")
  @RequestMapping(value = "{id}", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updateManager(@PathVariable("id") int managerId, @RequestBody Manager manager, ModelMap model) {
    try {
//      manager.setId(managerId);
      logger.info("[method: updateManager()] Update manager by params : " + JacksonUtil.getJsonMapper().writeValueAsString(manager));
//      managerService.updateManager(manager);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: updateManager()] Update manager : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "{id}/roleRela", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap getRoleRela(@PathVariable("id") int managerId, ModelMap model) {
    try {
      logger.info("[method: getRoleRela()] Get the relation between role and manager by params : {managerId=" + managerId + "}");
//      List<ManagerRoleRela> list = managerService.listManagerRoleRela(managerId);
//      model.addAttribute("list", list);
//      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: getRoleRela()] Get the relation between role and manager : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_ADD, desc = "保持管理员与角色之间关联关系", modelName = "管理员管理")
  @RequestMapping(value = "{id}/roleRela", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap saveRoleRela(@PathVariable("id") int managerId, @RequestBody List<String> roleId, ModelMap model) {
    try {
      logger.info("[method: saveRoleRela()] Save the relation between role and manager : {managerId=" + managerId + ",roleId=" + JacksonUtil.getJsonMapper().writeValueAsString(roleId) + "}");
//      managerService.saveManagerRoleRela(managerId, roleId);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: saveRoleRela()] Save the relation between role and manager : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }
}
