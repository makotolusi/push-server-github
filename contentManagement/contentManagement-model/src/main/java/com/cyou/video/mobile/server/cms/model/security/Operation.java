package com.cyou.video.mobile.server.cms.model.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CMS操作项类
 * 
 * @author lusi
 */
@Document(collection = "Security_Operation")
public class Operation {

  private static final long serialVersionUID = -9031731862085772713L;

  @Id
  private String id; // 操作项id

  private String name; // 操作项名称

  private String url; // 操作项地址

  @DBRef
  private ManageItem manageItem; // 所属管理项id

  // private int assignable = Constants.STATUS.TRUE.getValue(); //是否可配置
  @DBRef
  private List<Role> roles=new ArrayList<Role>();

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ManageItem getManageItem() {
    return manageItem;
  }

  public void setManageItem(ManageItem manageItem) {
    this.manageItem = manageItem;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
    builder.append("id", id);
    builder.append("name", name);
    builder.append("url", url);
    return builder.toString();
  }
}
