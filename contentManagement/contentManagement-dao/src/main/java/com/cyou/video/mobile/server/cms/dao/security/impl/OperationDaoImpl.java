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

import com.cyou.video.mobile.server.cms.dao.security.OperationDao;
import com.cyou.video.mobile.server.cms.model.security.Operation;

/**
 * CMS操作项持久化实现
 * @author jyz
 */
@Repository("operationDao")
public class OperationDaoImpl implements OperationDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private static final BeanPropertyRowMapper<Operation> rowMapper = new BeanPropertyRowMapper<Operation>(Operation.class);

  @Override
  public int createOperation(final Operation operation) throws Exception {
    final String sql = "INSERT INTO cms_security_operation(id, name, url, manage_item_id, assignable) VALUES(?, ?, ?, ?, ?);";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
//        ps.setInt(++ pos, operation.getId());
        ps.setString(++ pos, operation.getName());
        ps.setString(++ pos, operation.getUrl());
//        ps.setInt(++ pos, operation.getManageItemId());
//        ps.setInt(++ pos, operation.getAssignable());
        return ps;
      }
    }, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @Override
  public Operation getOperationById(int id) throws Exception {
    String sql = "SELECT * FROM cms_security_operation WHERE id = ?";
    Operation operation = null;
    try {
      operation = jdbcTemplate.queryForObject(sql, rowMapper, id);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return operation;
  }

  @Override
  public void updateOperation(final Operation operation) throws Exception {
    String sql = "UPDATE cms_security_operation SET name = ?, url = ?, assignable = ? WHERE id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setString(++ pos, operation.getName());
        ps.setString(++ pos, operation.getUrl());
//        ps.setInt(++ pos, operation.getAssignable());
//        ps.setInt(++ pos, operation.getId());
      }
    });
  }

  @Override
  public void deleteOperation(int id) throws Exception {
    String sql = "DELETE FROM cms_security_operation WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public List<Operation> listOperation() throws Exception {
    String sql = "SELECT * FROM cms_security_operation";
    return jdbcTemplate.query(sql, rowMapper);
  }

  @Override
  public List<Operation> listOperationByManager(int managerId) throws Exception {
    String sql = "SELECT distinct o.* FROM cms_security_operation o, cms_security_manager_role_rela m, cms_security_role_operation_rela r, cms_security_manageItem i " +
    		         "WHERE m.manager_id = ? AND m.role_id = r.role_id AND o.id = r.operation_id AND o.manage_item_id = i.id ORDER BY i.order_num, o.id";
    return jdbcTemplate.query(sql, rowMapper, managerId);
  }

  @Override
  public Operation getOperation(int manageItemId, String name) throws Exception {
    String sql = "SELECT * FROM cms_security_operation WHERE manage_item_id = ? AND name = ?";
    Operation operation = null;
    try {
      operation = jdbcTemplate.queryForObject(sql, rowMapper, manageItemId, name);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return operation;
  }

  @Override
  public void deleteOperationByManageItem(int manageItemId) throws Exception {
    String sql = "DELETE FROM cms_security_operation WHERE manage_item_id = ?";
    jdbcTemplate.update(sql, manageItemId);
  }

}
