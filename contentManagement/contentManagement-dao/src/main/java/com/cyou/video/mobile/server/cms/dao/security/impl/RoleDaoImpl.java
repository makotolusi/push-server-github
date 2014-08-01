package com.cyou.video.mobile.server.cms.dao.security.impl;

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

import com.cyou.video.mobile.server.cms.dao.security.RoleDao;
import com.cyou.video.mobile.server.cms.model.security.Role;

/**
 * CMS角色持久化实现
 * @author jyz
 */
@Repository("roleDao")
public class RoleDaoImpl implements RoleDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private static final BeanPropertyRowMapper<Role> rowMapper = new BeanPropertyRowMapper<Role>(Role.class);

  @Override
  public int createRole(final Role role) throws Exception {
    final String sql = "INSERT INTO cms_security_role(id, name) VALUES(?, ?);";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
//        ps.setInt(++ pos, role.getId());
        ps.setString(++ pos, role.getName());
        return ps;
      }
    }, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @Override
  public Role getRoleById(int id) throws Exception {
    String sql = "SELECT * FROM cms_security_role WHERE id = ?";
    Role role = null;
    try {
      role = jdbcTemplate.queryForObject(sql, rowMapper, id);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return role;
  }

  @Override
  public void updateRole(final Role role) throws Exception {
    String sql = "UPDATE cms_security_role SET name = ? WHERE id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setString(++ pos, role.getName());
//        ps.setInt(++ pos, role.getId());
      }
    });
  }

  @Override
  public void deleteRole(int id) throws Exception {
    String sql = "DELETE FROM cms_security_role WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public List<Role> listRole() throws Exception {
    String sql = "SELECT * FROM cms_security_role";
    return jdbcTemplate.query(sql, rowMapper);
  }

  @Override
  public Role getRole(String name) throws Exception {
    String sql = "SELECT * FROM cms_security_role WHERE name = ?";
    Role role = null;
    try {
      role = jdbcTemplate.queryForObject(sql, rowMapper, name);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return role;
  }

}
