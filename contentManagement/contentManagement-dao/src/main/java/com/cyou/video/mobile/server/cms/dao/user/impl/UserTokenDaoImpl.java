package com.cyou.video.mobile.server.cms.dao.user.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.dao.user.UserTokenDao;
import com.cyou.video.mobile.server.cms.model.user.UserToken;
import com.cyou.video.mobile.server.cms.model.user.UserTokenBindBaidu;
import com.cyou.video.mobile.server.common.Constants;

/**
 * CMS用户令牌持久化实现
 * 
 * @author jyz
 */
@Repository("userTokenDao")
public class UserTokenDaoImpl implements UserTokenDao {

  private Logger logger = LoggerFactory.getLogger(UserTokenDaoImpl.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private static final BeanPropertyRowMapper<UserToken> rowMapper = new BeanPropertyRowMapper<UserToken>(
      UserToken.class);

  private static final BeanPropertyRowMapper<UserTokenBindBaidu> UserTokenBindBaiduMapper = new BeanPropertyRowMapper<UserTokenBindBaidu>(
      UserTokenBindBaidu.class);

  @Override
  public int createToken(final UserToken token) throws Exception {
    int id = 0;
    final String sql = "INSERT IGNORE INTO cms_support_user_token(id, token, create_date, plat, last_req_date, req_count, status, os, ua, res, channel, mid) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
        ps.setInt(++pos, token.getId());
        ps.setString(++pos, token.getToken());
        ps.setTimestamp(++pos, new Timestamp(token.getCreateDate().getTime()));
        ps.setInt(++pos, token.getPlat());
        ps.setTimestamp(++pos, new Timestamp(token.getLastReqDate().getTime()));
        ps.setInt(++pos, token.getReqCount());
        ps.setInt(++pos, token.getStatus());
        ps.setString(++pos, token.getOs());
        ps.setString(++pos, token.getUa());
        ps.setString(++pos, token.getRes());
        ps.setString(++pos, token.getChannel());
        ps.setString(++pos, token.getMid());
        return ps;
      }
    }, keyHolder);
    if(keyHolder != null && keyHolder.getKey() != null) {
      id = keyHolder.getKey().intValue();
    }
    return id;
  }

  @Override
  public UserToken getTokenById(int id) throws Exception {
    String sql = "SELECT * FROM cms_support_user_token WHERE id = ?";
    UserToken token = null;
    try {
      token = jdbcTemplate.queryForObject(sql, rowMapper, id);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return token;
  }

  @Override
  public List<String> getBaiduIdsByToken(List<String> tokens) throws Exception {

    List<String> baiduIds = null;
    Map<String, List<String>> param = Collections.singletonMap("tokens", tokens);
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
    String sql = "select b.baidu_user_id from cms_support_user_token_bind_baidu b, cms_support_user_token a where a.id=b.token_id and token in (:tokens) ";
    baiduIds = namedParameterJdbcTemplate.queryForList(sql, param, String.class);
    return baiduIds;
  }

  @Override
  public UserToken getToken(String token) throws Exception {
    String sql = "SELECT * FROM cms_support_user_token WHERE token = ?";
    UserToken userToken = null;
    try {
      userToken = jdbcTemplate.queryForObject(sql, rowMapper, token);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return userToken;
  }

  @Override
  public void updateToken(final UserToken token) throws Exception {
    String sql = "UPDATE cms_support_user_token SET status = ?, token = ?, os = ? WHERE id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setInt(++pos, token.getStatus());
        ps.setString(++pos, token.getToken());
        ps.setString(++pos, token.getOs());
        ps.setInt(++pos, token.getId());
      }
    });
  }

  @Override
  public void deleteToken(int id) throws Exception {
    String sql = "DELETE FROM cms_support_user_token WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public int countToken(int plat, int status, Date createFrom, Date createTo, Date lastFrom, Date lastTo, String channel)
      throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "SELECT COUNT(1) FROM cms_support_user_token WHERE 1 = 1";
    if(plat != Constants.STATUS.IGNORE.getValue()) {
      sql += " AND plat = ?";
      param.add(plat);
    }
    if(status != Constants.STATUS.IGNORE.getValue()) {
      sql += " AND status = ?";
      param.add(status);
    }
    if(createFrom != null) {
      sql += " AND create_date >= ?";
      param.add(createFrom);
    }
    if(createTo != null) {
      sql += " AND create_date < ?";
      param.add(createTo);
    }
    if(lastFrom != null) {
      sql += " AND last_req_date >= ?";
      param.add(lastFrom);
    }
    if(lastTo != null) {
      sql += " AND last_req_date < ?";
      param.add(lastTo);
    }
    if(StringUtils.isNotBlank(channel)) {
      sql += " AND channel = ?";
      param.add(channel);
    }
    return jdbcTemplate.queryForObject(sql, param.toArray(), Integer.class);
  }

  @Override
  public void updateTokenByToken(final String token, final Date reqDate) throws Exception {
    String sql = "UPDATE cms_support_user_token SET req_count = req_count + 1, last_req_date = ? WHERE token = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setTimestamp(++pos, new Timestamp(reqDate.getTime()));
        ps.setString(++pos, token);
      }
    });
  }

  public void updateTokenById(final int id, final Date reqDate) throws Exception {
    String sql = "UPDATE cms_support_user_token SET req_count = req_count + 1, last_req_date = ? WHERE id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setTimestamp(1, new Timestamp(reqDate.getTime()));
        ps.setInt(2, id);
      }
    });
  }

  @Override
  public List<UserToken> listTokens(int curPage, int pageSize) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "SELECT * FROM cms_support_user_token LIMIT ?, ?";
    param.add(curPage);
    param.add(pageSize);
    return jdbcTemplate.query(sql, rowMapper, param.toArray());
  }

  @Override
  public int createBaiduIdBindToken(final UserTokenBindBaidu tokenBindBaidu) throws Exception {
    int id = 0;
    final String sql = "INSERT INTO cms_support_user_token_bind_baidu (token_id, baidu_user_id, baidu_channel_id, baidu_app_id) VALUES(?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
        ps.setInt(++pos, tokenBindBaidu.getTokenId());
        ps.setString(++pos, tokenBindBaidu.getBaiduUserId());
        ps.setString(++pos, tokenBindBaidu.getBaiduChannelId());
        ps.setString(++pos, tokenBindBaidu.getBaiduAppId());
        return ps;
      }
    }, keyHolder);
    if(keyHolder != null && keyHolder.getKey() != null) {
      id = keyHolder.getKey().intValue();
    }
    return id;
  }

  @Override
  public List<UserTokenBindBaidu> getUserTokenBindBaidu(int tokenId) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "SELECT * FROM cms_support_user_token_bind_baidu WHERE token_Id = "+tokenId;
    return jdbcTemplate.query(sql, UserTokenBindBaiduMapper, param.toArray());
  }

  @Override
  public String getBaiduUserId(int tokenId) {

    String sql = "SELECT baidu_user_id FROM cms_support_user_token_bind_baidu WHERE token_Id = ? limit 1 ";
    String tokenBindBaidu = null;
    try {
      tokenBindBaidu = jdbcTemplate.queryForObject(sql, String.class, tokenId);
    }
    catch(Exception e) {
      return null;
    }
    return tokenBindBaidu;
  }

  @Override
  public List<UserToken> getTokenBaiduUidChannelInfo(int curPage, int pageSize, Date lastMDate) {
    List<Object> param = new ArrayList<Object>();
    String sql = "select a.baidu_user_id as baiduUserID,b.id,b.token,b.channel,b.cur_used_version,b.os,b.plat from "
        + "cms_support_user_token_bind_baidu a left join cms_support_user_token b on b.id=a.token_id where ((b.channel is not null and b.channel !='') or ( cur_used_version is not null and cur_used_version !='')) and b.token is not null ";
    if(lastMDate != null) {
      sql += " and last_vc_time > ?  ";
      param.add(lastMDate);
    }
    sql += " LIMIT ?, ?";
    param.add(curPage);
    param.add(pageSize);
    List<UserToken> tokenBindBaidu = null;
    try {
      tokenBindBaidu = jdbcTemplate.query(sql, rowMapper, param.toArray());
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
    return tokenBindBaidu;
  }

  @Override
  public List<UserToken> getTokenBaiduUidChannelInfoForXinGe(int curPage, int pageSize, Date lastMDate) {
    List<Object> param = new ArrayList<Object>();
    String sql = "select * from  cms_support_user_token b  where ((b.channel is not null and b.channel !='') or ( cur_used_version is not null and cur_used_version !='')) and b.token is not null ";
    if(lastMDate != null) {
      sql += " and last_vc_time > ?  ";
      param.add(lastMDate);
    }
    sql += " LIMIT ?, ?";
    param.add(curPage);
    param.add(pageSize);
    List<UserToken> tokenBindBaidu = null;
    try {
      tokenBindBaidu = jdbcTemplate.query(sql, rowMapper, param.toArray());
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
    return tokenBindBaidu;
  }
  
  @Override
  public int countTokenBaiduUidChannelInfoForXinGe(Date lastMDate) {
    List<Object> param = new ArrayList<Object>();
    String sql = "select count(b.id) from cms_support_user_token b on b.id=a.token_id where ((b.channel is not null and b.channel !='') or ( cur_used_version is not null and cur_used_version !='')) and b.token is not null ";
    if(lastMDate != null) {
      sql += " and last_vc_time > ?  ";
      param.add(Constants.formatDate(Constants.SDF.YYYYMMDDHHMMSS.toString(), lastMDate));
    }
    try {
      return jdbcTemplate.queryForObject(sql, param.toArray(), Integer.class);
    }
    catch(Exception e) {
      return 0;
    }
  }
  
  @Override
  public int countTokenBaiduUidChannelInfo(Date lastMDate) {
    List<Object> param = new ArrayList<Object>();
    String sql = "select count(b.id) from "
        + "cms_support_user_token_bind_baidu a left join cms_support_user_token b on b.id=a.token_id where ((b.channel is not null and b.channel !='') or ( cur_used_version is not null and cur_used_version !='')) and b.token is not null ";
    if(lastMDate != null) {
      sql += " and last_vc_time > ?  ";
      param.add(Constants.formatDate(Constants.SDF.YYYYMMDDHHMMSS.toString(), lastMDate));
    }

    List<UserToken> tokenBindBaidu = null;
    try {
      return jdbcTemplate.queryForObject(sql, param.toArray(), Integer.class);
    }
    catch(Exception e) {
      return 0;
    }
  }

  @Override
  public void updateTokenChannel(final String token, final String channel, final String curVersion) throws Exception {
    String sql = "UPDATE cms_support_user_token SET channel = ?, cur_used_version = ?,last_vc_time= ? WHERE token = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setString(++pos, channel);
        ps.setString(++pos, curVersion);
        ps.setTimestamp(++pos, new Timestamp(new Date().getTime()));
        ps.setString(++pos, token);
      }
    });
  }

  @Override
  public void updateTokenChannelTagState(final int tagState, final int tokenId) throws Exception {
    String sql = "UPDATE cms_support_user_token_bind_baidu SET tag_state = ? WHERE token_id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setInt(++pos, tagState);
        ps.setInt(++pos, tokenId);
      }
    });
  }

  @Override
  public void updateTokenMobileEmail(final String mobileNumber, final String email, final String token) throws Exception {
    String sql = "UPDATE cms_support_user_token SET email = ?, mobile_number = ? WHERE token = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setString(++pos, email);
        ps.setString(++pos, mobileNumber);
        ps.setString(++pos, token);
      }
    });
  }

}
