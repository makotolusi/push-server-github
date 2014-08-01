package com.cyou.video.mobile.server.cms.service.security.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.cyou.video.mobile.server.cms.model.VerifyException;
import com.cyou.video.mobile.server.cms.model.security.ManageItem;
import com.cyou.video.mobile.server.cms.model.security.Operation;
import com.cyou.video.mobile.server.cms.service.security.OperationService;
import com.cyou.video.mobile.server.common.Constants;

/**
 * CMS操作项业务实现
 * 
 * @author jyz
 */
@Service("operationService")
public class OperationServiceImpl implements OperationService {

  @Autowired
  private MongoOperations mongoTemplate;

  @Override
  public List<Operation> listOperation() throws Exception {
    return null;// operationDao.listOperation();
  }

  @Override
  public void createOperation(Operation operation) throws Exception {
    if(operation == null) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.getValue(),
          Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.toString() + "_operation");
    }
    ManageItem manageItem = mongoTemplate.findOne(new Query(new Criteria("id").is(operation.getManageItem().getId())),
        ManageItem.class);
    List<Operation> operations = manageItem.getOperations();
    
    operations.add(operation);
    mongoTemplate.updateFirst(new Query(new Criteria("id").is(operation.getManageItem().getId())),
        new Update().set("operations", operations), ManageItem.class);//更新菜单下的功能列表
  }

  @Override
  public void updateOperation(Operation operation) throws Exception {
    if(operation == null) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.getValue(),
          Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.toString() + "_operation");
    }
    mongoTemplate.save(operation);
  }

  @Override
  public void deleteOperation(int id) throws Exception {
    if(id < 1) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_INVALID.getValue(),
          Constants.CUSTOM_ERROR_CODE.PARAMATER_INVALID.toString() + "_operation.id");
    }
    // roleOperationRelaDao.deleteRoleOperationRelaByOperation(id);
    // //删除每一个操作项都要先删除与角色的关联关系
    // operationDao.deleteOperation(id);
  }

}
