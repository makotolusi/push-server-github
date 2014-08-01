package com.cyou.video.mobile.server.cms.dao.sys.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.dao.sys.MailAddresseeDao;
import com.cyou.video.mobile.server.cms.model.sys.MailAddressee;

/**
 * 邮件接收者持久化实现
 * @author jyz
 */
@Repository("mailAddresseeDao")
public class MailAddresseeDaoImpl implements MailAddresseeDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private static final BeanPropertyRowMapper<MailAddressee> rowMapper = new BeanPropertyRowMapper<MailAddressee>(MailAddressee.class);

  @Override
  public int createMailAddressee(final MailAddressee mailAddressee) throws Exception {
    final String sql = "INSERT INTO cms_sys_mailAddressee(id, project, type, nickname, email) VALUES(?, ?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
        ps.setInt(++ pos, mailAddressee.getId());
        ps.setString(++ pos, mailAddressee.getProject());
        ps.setString(++ pos, mailAddressee.getType());
        ps.setString(++ pos, mailAddressee.getNickname());
        ps.setString(++ pos, mailAddressee.getEmail());
        return ps;
      }
    }, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @Override
  public MailAddressee getMailAddressee(int id) throws Exception {
    String sql = "SELECT * FROM cms_sys_mailAddressee WHERE id = ?";
    MailAddressee mailAddressee = null;
    try {
      mailAddressee = jdbcTemplate.queryForObject(sql, rowMapper, id);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return mailAddressee;
  }

  @Override
  public void updateMailAddressee(final MailAddressee mailAddressee) throws Exception {
    String sql = "UPDATE cms_sys_mailAddressee SET nickname = ?, email = ? WHERE id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setString(++ pos, mailAddressee.getNickname());
        ps.setString(++ pos, mailAddressee.getEmail());
        ps.setInt(++ pos, mailAddressee.getId());
      }
    });
  }

  @Override
  public void deleteMailAddressee(int id) throws Exception {
    String sql = "DELETE FROM cms_sys_mailAddressee WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public List<MailAddressee> getMailAddresseeList(String project, String type) throws Exception {
    String sql = "SELECT * FROM cms_sys_mailAddressee WHERE project = ? AND type = ?";
    return jdbcTemplate.query(sql, rowMapper, project, type);
  }

  @Override
  public List<String> getTypeListByProject(String project) throws Exception {
    String sql = "SELECT DISTINCT(type) FROM cms_sys_mailAddressee WHERE project = ?";
    return jdbcTemplate.queryForList(sql, String.class, project);
  }

  @Override
  public List<String> getProjectList() throws Exception {
    String sql = "SELECT DISTINCT(project) FROM cms_sys_mailAddressee";
    return jdbcTemplate.queryForList(sql, String.class);
  }

}
