package com.cyou.video.mobile.server.cms.dao.sys;

import java.util.List;

import com.cyou.video.mobile.server.cms.model.sys.SysProperty;

/**
 * CMS系统参数持久化接口
 * @author zs
 */
public interface SysPropertyDao {
	/**
	   * 创建令牌
	   * @param sysPro 系统参数
	   * @return 参数id
	   * @throws Exception
	   */
	  public int createSysProperty(SysProperty sysPro) throws Exception;

	  /**
	   * 获取系统参数列表
	   * @param sysPro 系统参数
	   * @return 系统参数列表
	   * @throws Exception
	   */
	  public List<SysProperty> listSysProperty(SysProperty sysPro) throws Exception;

	  /**
	   * 更新系统参数
	   * @param sysPro 系统参数
	   * @throws Exception
	   */
	  public void updateSysProperty(SysProperty sysPro) throws Exception;

	  /**
	   * 删除参数
	   * @param id 参数id
	   * @throws Exception
	   */
	  public void deleteSysProperty(int id) throws Exception;
	  
	  /**
	   * 获取系统参数对象
	   * @param sysPro 系统参数
	   * @return 系统参数
	   * @throws Exception
	   */
	  public List<SysProperty> getSysProperty(SysProperty sysPro) throws Exception;
	  
	  /**
	   * 获取系统参数对象
	   * @param id 系统参数id
	   * @return 系统参数
	   * @throws Exception
	   */
	  public SysProperty getSysPropertyById(int id) throws Exception;


}
