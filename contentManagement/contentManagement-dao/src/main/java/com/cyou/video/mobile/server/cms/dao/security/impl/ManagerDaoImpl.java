package com.cyou.video.mobile.server.cms.dao.security.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

import com.cyou.video.mobile.server.cms.dao.security.ManagerDao;
import com.cyou.video.mobile.server.cms.model.security.Manager;
import com.cyou.video.mobile.server.common.Constants;

/**
 * CMS管理员持久化实现
 * @author jyz
 */
@Repository("managerDao")
public class ManagerDaoImpl implements ManagerDao {

//  @Autowired
  private JdbcTemplate jdbcTemplate;

  private static final BeanPropertyRowMapper<Manager> rowMapper = new BeanPropertyRowMapper<Manager>(Manager.class);

  @Override
  public int createManager(final Manager manager) throws Exception {
    final String sql = "INSERT INTO cms_security_manager(id, username, password, email, status) VALUES(?, ?, ?, ?, ?);";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
//        ps.setInt(++ pos, manager.getId());
        ps.setString(++ pos, manager.getUsername());
        ps.setString(++ pos, manager.getPassword());
        ps.setString(++ pos, manager.getEmail());
//        ps.setInt(++ pos, manager.getStatus());
        return ps;
      }
    }, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @Override
  public Manager getManagerById(int id) throws Exception {
    String sql = "SELECT * FROM cms_security_manager WHERE id = ?";
    Manager manager = null;
    try {
      manager = jdbcTemplate.queryForObject(sql, rowMapper, id);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return manager;
  }

  @Override
  public Manager getManager(String username) throws Exception {
    String sql = "SELECT * FROM cms_security_manager WHERE username = ?";
    Manager manager = null;
    try {
      manager = jdbcTemplate.queryForObject(sql, rowMapper, username);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return manager;
  }

  @Override
  public void updateManager(final Manager manager) throws Exception {
    String sql = "UPDATE cms_security_manager SET username = ?, password = ?, email = ?, status = ? WHERE id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setString(++ pos, manager.getUsername());
        ps.setString(++ pos, manager.getPassword());
        ps.setString(++ pos, manager.getEmail());
//        ps.setInt(++ pos, manager.getStatus());
//        ps.setInt(++ pos, manager.getId());
      }
    });
  }

  @Override
  public void deleteManager(int id) throws Exception {
    String sql = "DELETE FROM cms_security_manager WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public List<Manager> listManager(int status) throws Exception {
    List<Object> param = new ArrayList<Object>();
    String sql = "SELECT * FROM cms_security_manager WHERE 1 = 1";
    if(status != Constants.STATUS.IGNORE.getValue()) {
      sql += " AND status = ?";
      param.add(status);
    }
    return jdbcTemplate.query(sql, rowMapper, param.toArray());
  }

}
