package com.cyou.video.mobile.server.cms.dao.sys;

import java.util.List;

import com.cyou.video.mobile.server.cms.model.sys.MailInfo;

/**
 * 邮件信息持久化接口
 * @author jyz
 */
public interface MailInfoDao {

  /**
   * 创建邮件信息
   * @param mailInfo 邮件信息
   * @return 邮件信息id
   * @throws Exception
   */
  public int createMailInfo(MailInfo mailInfo) throws Exception;

  /**
   * 获取邮件信息
   * @param id 邮件信息id
   * @return 邮件信息
   * @throws Exception
   */
  public MailInfo getMailInfo(int id) throws Exception;

  
  /**
   * 删除邮件信息
   * @param id 邮件信息id
   * @throws Exception
   */
  public void deleteMailInfo(int id) throws Exception;

  /**
   * 更新邮件信息
   * @param mailInfo 邮件信息
   * @throws Exception
   */
  public void updateMailInfo(MailInfo mailInfo) throws Exception;

  /**
   * 获取邮件信息列表
   * @param status 邮件信息状态
   * @param project 应用项目
   * @param curPage 当前页码
   * @param pageSize 每页数量
   * @return 邮件信息列表
   * @throws Exception
   */
  public List<MailInfo> listMailInfo(int status, String project, int curPage, int pageSize) throws Exception;

  /**
   * 获取邮件信息数量
   * @param status 邮件信息状态
   * @param project 应用项目
   * @return 邮件信息数量
   * @throws Exception
   */
  public int listMailInfoCount(int status, String project) throws Exception;
}
