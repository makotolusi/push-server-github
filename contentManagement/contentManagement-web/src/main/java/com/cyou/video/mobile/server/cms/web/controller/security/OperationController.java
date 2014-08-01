package com.cyou.video.mobile.server.cms.web.controller.security;

import java.util.List;

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

import com.cyou.video.mobile.server.cms.model.security.Operation;
import com.cyou.video.mobile.server.cms.service.security.OperationService;
import com.cyou.video.mobile.server.cms.web.aspect.LogAnno;
import com.cyou.video.mobile.server.common.Constants;
import com.cyou.video.mobile.server.common.utils.JacksonUtil;

/**
 * CMS操作项controller
 * 
 * @author jyz
 */
@Controller
@RequestMapping("/web/operation")
public class OperationController {
  
  private Logger logger=LoggerFactory.getLogger(OperationController.class);

  @Autowired
  private OperationService operationService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ModelMap listOperation(ModelMap model) {
    try {
      List<Operation> list = operationService.listOperation();
      model.addAttribute("list", list);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: listOperation()] Get operation list : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_ADD, desc = "创建新操作项", modelName = "操作项管理")
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap createOperation(@RequestBody Operation operation, ModelMap model) {
    try {
      logger.info("[method: createOperation()] Create operation by params : " + JacksonUtil.getJsonMapper().writeValueAsString(operation));
      operationService.createOperation(operation);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: createOperation()] Create operation : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_UPDATE, desc = "更新指定操作项信息", modelName = "操作项管理")
  @RequestMapping(value = "{id}", method = RequestMethod.POST)
  @ResponseBody
  public ModelMap updateOperation(@PathVariable("id") int operationId, @RequestBody Operation operation, ModelMap model) {
    try {
      logger.info("[method: updateOperation()] Update operation by params : " + JacksonUtil.getJsonMapper().writeValueAsString(operation));
      operationService.updateOperation(operation);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: updateOperation()] Update operation : error!" + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }

  @LogAnno(type = Constants.LOG_TYPE_DELETE, desc = "删除指定操作项", modelName = "操作项管理")
  @RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
  @ResponseBody
  public ModelMap deleteOperation(@PathVariable("id") int operationId, ModelMap model) {
    try {
      logger.info("[method: deleteOperation()] Delete operation by params : {operationId = " + operationId + "}");
      operationService.deleteOperation(operationId);
      model.addAttribute("message", Constants.CUSTOM_ERROR_CODE.SUCCESS.toString());
    }
    catch(Exception e) {
      logger.error("[method: deleteOperation()] Delete operation : error! " + e.getMessage(), e);
      model.addAttribute("message", e.getMessage());
      e.printStackTrace();
    }
    return model;
  }
}
