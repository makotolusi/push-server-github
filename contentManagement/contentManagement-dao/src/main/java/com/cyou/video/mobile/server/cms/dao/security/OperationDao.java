package com.cyou.video.mobile.server.cms.dao.security;

import java.util.List;

import com.cyou.video.mobile.server.cms.model.security.Operation;

/**
 * CMS操作项持久化接口
 * @author jyz
 */
public interface OperationDao {

  /**
   * 创建操作项
   * @param operation 操作项
   * @return 操作项id
   * @throws Exception
   */
  public int createOperation(Operation operation) throws Exception;

  /**
   * 获取操作项
   * @param id 操作项id
   * @return 操作项
   * @throws Exception
   */
  public Operation getOperationById(int id) throws Exception;

  /**
   * 获取操作项
   * @param manageItemId 管理项id
   * @param name 操作项名称
   * @return 操作项
   * @throws Exception
   */
  public Operation getOperation(int manageItemId, String name) throws Exception;

  /**
   * 更新操作项
   * @param operation 操作项
   * @throws Exception
   */
  public void updateOperation(Operation operation) throws Exception;

  /**
   * 删除操作项
   * @param id 操作项id
   * @throws Exception
   */
  public void deleteOperation(int id) throws Exception;

  /**
   * 获取操作项列表
   * @return 操作项列表
   * @throws Exception
   */
  public List<Operation> listOperation() throws Exception;

  /**
   * 获取操作项列表
   * @param managerId 管理员id
   * @return 操作项列表
   * @throws Exception
   */
  public List<Operation> listOperationByManager(int managerId) throws Exception;

  /**
   * 删除操作项
   * @param manageItemId 管理项id
   * @throws Exception
   */
  public void deleteOperationByManageItem(int manageItemId) throws Exception;
}
