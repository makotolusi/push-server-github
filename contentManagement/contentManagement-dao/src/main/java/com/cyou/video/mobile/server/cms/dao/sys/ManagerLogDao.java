package com.cyou.video.mobile.server.cms.dao.sys;

import java.util.Date;
import java.util.List;

import com.cyou.video.mobile.server.cms.model.sys.ManagerLog;

/**
 * CMS用户日志持久化接口
 * @author zs
 */

public interface ManagerLogDao {
  
  /**
   * 记录用户日志
   * @param log 日志信息
   * @return id
   * @throws Exception
   */
  public int createUserLog(ManagerLog log) throws Exception;
  
  /**
   * 获取用户日志条数
   * @param type 操作类型
   * @param userName 当前用户
   * @param modelName 所属模块
   * @param from 开始时间
   * @param to 结束时间
   * @return 个数
   * @throws Exception
   */
  public int countUserLog(int type, String userName, String modelName, Date from, Date to) throws Exception;
  
  /**
   * 获取用户日志列表
   * @param type 操作类型
   * @param userName 当前用户
   * @param modelName 所属模块
   * @param from 开始时间
   * @param to 结束时间
   * @return 用户日志列表
   * @throws Exception
   */
  public List<ManagerLog> ListUserLog(int curPage, int pageSize, int type, String userName, String modelName, Date from, Date to) throws Exception;

}
