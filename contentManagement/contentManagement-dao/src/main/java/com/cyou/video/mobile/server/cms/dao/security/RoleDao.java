package com.cyou.video.mobile.server.cms.dao.security;

import java.util.List;

import com.cyou.video.mobile.server.cms.model.security.Role;

/**
 * CMS角色持久化接口
 * @author jyz
 */
public interface RoleDao {

  /**
   * 创建角色
   * @param role 角色对象
   * @return 角色id
   * @throws Exception
   */
  public int createRole(Role role) throws Exception;

  /**
   * 获取角色
   * @param id 角色id
   * @return 角色对象
   * @throws Exception
   */
  public Role getRoleById(int id) throws Exception;

  /**
   * 获取角色
   * @param name 角色名称
   * @return 角色对象
   * @throws Exception
   */
  public Role getRole(String name) throws Exception;

  /**
   * 更新角色
   * @param role 角色对象
   * @throws Exception
   */
  public void updateRole(Role role) throws Exception;

  /**
   * 删除角色
   * @param id 角色id
   * @throws Exception
   */
  public void deleteRole(int id) throws Exception;

  /**
   * 获取角色列表
   * @return 角色列表
   * @throws Exception
   */
  public List<Role> listRole() throws Exception;
}
