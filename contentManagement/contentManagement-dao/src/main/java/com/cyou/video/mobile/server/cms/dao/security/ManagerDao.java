package com.cyou.video.mobile.server.cms.dao.security;

import java.util.List;

import com.cyou.video.mobile.server.cms.model.security.Manager;

/**
 * CMS管理员持久化接口
 * @author jyz
 */

public interface ManagerDao {

  /**
   * 创建管理员
   * @param manager 管理员对象
   * @return 管理员id
   * @throws Exception
   */
  public int createManager(Manager manager) throws Exception;

  /**
   * 获取管理员
   * @param id 管理员id
   * @return 管理员对象
   * @throws Exception
   */
  public Manager getManagerById(int id) throws Exception;

  /**
   * 获取管理员
   * @param username 登录用户名
   * @return 管理员对象
   * @throws Exception
   */
  public Manager getManager(String username) throws Exception;

  /**
   * 更新管理员
   * @param manager 管理员对象
   * @throws Exception
   */
  public void updateManager(Manager manager) throws Exception;

  /**
   * 删除管理员
   * @param id 管理员id
   * @throws Exception
   */
  public void deleteManager(int id) throws Exception;

  /**
   * 获取管理员列表
   * @param status 管理员状态
   * @return 管理员列表
   * @throws Exception
   */
  public List<Manager> listManager(int status) throws Exception;
}
