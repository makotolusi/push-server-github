package com.cyou.video.mobile.server.cms.dao.security.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.dao.security.ManageItemDao;
import com.cyou.video.mobile.server.cms.model.security.ManageItem;

/**
 * CMS管理项持久化实现
 * @author jyz
 */
@Repository("manageItemDao")
public class ManageItemDaoImpl implements ManageItemDao {

//  @Autowired
  private JdbcTemplate jdbcTemplate;

  private static final BeanPropertyRowMapper<ManageItem> rowMapper = new BeanPropertyRowMapper<ManageItem>(ManageItem.class);

  @Override
  public int createManageItem(final ManageItem manageItem) throws Exception {
    final String sql = "INSERT INTO cms_security_manageItem (id, name, url, status, order_num) VALUES(?, ?, ?, ?, ?);";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int pos = 0;
        ps.setInt(++ pos, manageItem.getId());
        ps.setString(++ pos, manageItem.getName());
        ps.setString(++ pos, manageItem.getUrl());
//        ps.setInt(++ pos, manageItem.getStatus());
        ps.setInt(++ pos, manageItem.getOrderNum());
        return ps;
      }
    }, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @Override
  public ManageItem getManageItemById(int id) throws Exception {
    String sql = "SELECT * FROM cms_security_manageItem WHERE id = ?";
    ManageItem manageItem = null;
    try {
      manageItem = jdbcTemplate.queryForObject(sql, rowMapper, id);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return manageItem;
  }

  @Override
  public void updateManageItem(final ManageItem manageItem) throws Exception {
    String sql = "UPDATE cms_security_manageItem i SET i.name = ?, i.url = ?, i.status = ?, i.order_num = ? WHERE i.id = ?";
    jdbcTemplate.update(sql, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 0;
        ps.setString(++ pos, manageItem.getName());
        ps.setString(++ pos, manageItem.getUrl());
//        ps.setInt(++ pos, manageItem.getStatus());
        ps.setInt(++ pos, manageItem.getOrderNum());
        ps.setInt(++ pos, manageItem.getId());
      }
    });
  }

  @Override
  public void deleteManageItem(int id) throws Exception {
    String sql = "DELETE FROM cms_security_manageItem WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public List<ManageItem> listManageItem() throws Exception {
    String sql = "SELECT * FROM cms_security_manageItem i ORDER BY i.order_num";
    return jdbcTemplate.query(sql, rowMapper);
  }

  @Override
  public ManageItem getManageItem(String name) throws Exception {
    String sql = "SELECT * FROM cms_security_manageItem WHERE name = ?";
    ManageItem manageItem = null;
    try {
      manageItem = jdbcTemplate.queryForObject(sql, rowMapper, name);
    }
    catch(EmptyResultDataAccessException e) {
    }
    return manageItem;
  }

  @Override
  public void updateManageItemOrder(int id, int order) throws Exception {
    String sql = "UPDATE cms_security_manageItem i SET i.order_num = ? WHERE i.id = ?";
    jdbcTemplate.update(sql, order, id);
  }

}
