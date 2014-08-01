package com.cyou.video.mobile.server.cms.dao.user;

import java.util.Date;
import java.util.List;

import com.cyou.video.mobile.server.cms.model.user.UserToken;
import com.cyou.video.mobile.server.cms.model.user.UserTokenBindBaidu;

/**
 * CMS用户令牌持久化接口
 * @author jyz
 */
public interface UserTokenDao {

  /**
   * 创建令牌
   * @param token 用户令牌
   * @return 令牌id
   * @throws Exception
   */
  public int createToken(UserToken token) throws Exception;

  /**
   * 获取令牌
   * @param id 令牌id
   * @return 用户令牌
   * @throws Exception
   */
  public UserToken getTokenById(int id) throws Exception;

  /**
   * 获取令牌
   * @param token 令牌值
   * @return 用户令牌
   * @throws Exception
   */
  public UserToken getToken(String token) throws Exception;

  /**
   * 更新令牌
   * @param token 用户令牌
   * @throws Exception
   */
  public void updateToken(UserToken token) throws Exception;

  /**
   * 更新令牌
   * @param token 令牌值
   * @param reqDate 最近请求日期
   * @throws Exception
   */
  public void updateTokenByToken(String token, Date reqDate) throws Exception;
  
  /**
   * 更新令牌
   * @param token 令牌值
   * @param reqDate 最近请求日期
   * @throws Exception
   */
  public void updateTokenById(int id, Date reqDate) throws Exception;

  /**
   * 删除令牌
   * @param id 令牌id
   * @throws Exception
   */
  public void deleteToken(int id) throws Exception;

  /**
   * 令牌计数
   * @param plat 令牌来源
   * @param status 令牌状态
   * @param createFrom 本日期之后创建
   * @param createTo 本日期之前创建
   * @param lastFrom 最近访问在本日期之后
   * @param lastTo 最近访问在本日期之前
   * @return 令牌计数
   * @throws Exception
   */
  public int countToken(int plat, int status, Date createFrom, Date createTo, Date lastFrom, Date lastTo, String channel) throws Exception;
  
  /**
   * 获取指定条数令牌
   * @return
   * @throws Exception
   */
  public List<UserToken> listTokens(int curPage, int pageSize) throws Exception;

  /**
   * 用户baidu id绑定
   * @param tokenBindBaidu
   * @return
   * @throws Exception
   */
  int createBaiduIdBindToken(UserTokenBindBaidu tokenBindBaidu) throws Exception;

  /**
   * 根据tokenid 取得 绑定信息
   * @param tokenId
   * @return
   * @throws Exception
   */
  List<UserTokenBindBaidu> getUserTokenBindBaidu(int tokenId) throws Exception;
  
  /**
   * 变更令牌所属渠道
   * @param token 令牌值
   * @throws Exception
   */
  public void updateTokenChannel(String token, String channel, String curVersion) throws Exception;

String getBaiduUserId(int tokenId) throws Exception;

/**
 * 批量查询 token对应的 baidu id
 * @param tokens
 * @return
 * @throws Exception
 */
List<String> getBaiduIdsByToken(List<String> tokens) throws Exception;

/**
 * 取得用户渠道上的内容
 * @return
 */
List<UserToken> getTokenBaiduUidChannelInfo(int curPage, int pageSize,Date lastMDate);

/***
 * 发送tag 为了增量
 * @param tagState
 * @param tokenId
 * @throws Exception
 */
void updateTokenChannelTagState(int tagState,  int tokenId) throws Exception;

/**
 * 渠道的总数
 * @return
 */
int countTokenBaiduUidChannelInfo(Date lastMDate);
  

void updateTokenMobileEmail(String mobileNumber,String email,String token) throws Exception;

/**
 * 信鸽用 
 * @param lastMDate
 * @return
 */
int countTokenBaiduUidChannelInfoForXinGe(Date lastMDate);

/**
 * 信鸽用
 * @param curPage
 * @param pageSize
 * @param lastMDate
 * @return
 */
List<UserToken> getTokenBaiduUidChannelInfoForXinGe(int curPage, int pageSize, Date lastMDate);

  /*public int countTodayToken(String channel, String fieldName) throws Exception;
  
  public int countYesterDayToken(String channel) throws Exception;
  
  public int countMonthToken(String channel) throws Exception;
  
  public int countTokenByChannel(String channel) throws Exception;*/
}
