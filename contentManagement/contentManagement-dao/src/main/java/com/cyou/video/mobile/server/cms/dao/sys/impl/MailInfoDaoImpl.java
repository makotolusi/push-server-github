package com.cyou.video.mobile.server.cms.dao.sys.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.dao.sys.MailInfoDao;
import com.cyou.video.mobile.server.cms.model.sys.MailInfo;
import com.cyou.video.mobile.server.common.Constants;

/**
 * 邮件信息持久化实现
 * @author jyz
 */
@Repository("mailInfoDao")
public class MailInfoDaoImpl implements MailInfoDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @SuppressWarnings({"rawtypes"})
  private static final class MailInfoMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int rn) throws SQLException {
      MailInfo info = new MailInfo();
      info.setId(rs.getInt("id"));
      info.setAddressee(rs.getString("addressee"));
      info.setContent(rs.getString("content"));
      if(rs.getTimestamp("create_date") != null) {
        info.setCreateDate(new Date(rs.getTimestamp("create_date").getTime()));
      }
      info.setProject(rs.getString("project"));
      if(rs.getTimestamp("send_date") != null) {
        info.setSendDate(new Date(rs.getTimestamp("send_date").getTime()));
      }
      info.setStatus(rs.getInt("status"));
      info.setTimes(rs.getInt("times"));
      info.setTitle(rs.getString("title"));
      info.setType(rs.getString("type"));
      return info;
    }
  }

  @Override
  public int createMailInfo(final MailInfo mailInfo) throws Exception {
    final String sql = "INSERT INTO cms_sys_mailInfo(id, title, content, addressee, create_date, status, times, project, type) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
        ps.setInt(++ pos, mailInfo.getId());
        ps.setString(++ pos, mailInfo.getTitle());
        ps.setString(++ pos, mailInfo.getContent());
        ps.setString(++ pos, mailInfo.getAddressee());
        ps.setTimestamp(++ pos, new Timestamp(mailInfo.getCreateDate().getTime()));
        ps.setInt(++ pos, mailInfo.getStatus());
        ps.setInt(++ pos, mailInfo.getTimes());
        ps.setString(++ pos, mailInfo.getProject());
        ps.setString(++ pos, mailInfo.getType());
        return ps;
      }
    }, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  public MailInfo getMailInfo(int id) throws Exception {
    String sql = "SELECT * FROM cms_sys_mailInfo WHERE id = ?";
    MailInfo mailInfo = null;
    try {
      mailInfo = (MailInfo) jdbcTemplate.queryForObject(sql, new MailInfoMapper(), id);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return mailInfo;
  }

  @Override
  public void deleteMailInfo(int id) throws Exception {
    String sql = "DELETE FROM cms_sys_mailInfo WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public void updateMailInfo(final MailInfo mailInfo) throws Exception {
    String sql = "UPDATE cms_sys_mailInfo SET send_date = ?, status = ?, times = ? WHERE id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setTimestamp(++ pos, new Timestamp(mailInfo.getSendDate().getTime()));
        ps.setInt(++ pos, mailInfo.getStatus());
        ps.setInt(++ pos, mailInfo.getTimes());
        ps.setInt(++ pos, mailInfo.getId());
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<MailInfo> listMailInfo(int status, String project, int curPage, int pageSize) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "SELECT * FROM cms_sys_mailInfo WHERE 1 = 1";
    if(status != Constants.STATUS.IGNORE.getValue()) {
      sql += " AND status = ?";
      param.add(status);
    }
    if(StringUtils.isNotBlank(project)) {
      sql += " AND project = ?";
      param.add(project);
    }
    if(status == Constants.STATUS.ON.getValue()) {
      sql += " ORDER BY send_date DESC";
    }
    if(curPage >= 0 && pageSize > 0) {
      sql += " LIMIT ?, ?";
      param.add(curPage);
      param.add(pageSize);
    }
    return jdbcTemplate.query(sql, new MailInfoMapper(), param.toArray());
  }

  @Override
  public int listMailInfoCount(int status, String project) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "SELECT COUNT(1) FROM cms_sys_mailInfo WHERE 1 = 1";
    if(status != Constants.STATUS.IGNORE.getValue()) {
      sql += " AND status = ?";
      param.add(status);
    }
    if(StringUtils.isNotBlank(project)) {
      sql += " AND project = ?";
      param.add(project);
    }
    return jdbcTemplate.queryForObject(sql, param.toArray(), Integer.class);
  }

}
