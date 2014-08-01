package com.cyou.video.mobile.server.cms.service.collection.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.model.Pagination;
import com.cyou.video.mobile.server.cms.model.collection.PlayerAdv;
import com.cyou.video.mobile.server.cms.model.collection.PlayerApps;
import com.cyou.video.mobile.server.cms.model.collection.PlayerAvList;
import com.cyou.video.mobile.server.cms.model.collection.PlayerLogCollection;
import com.cyou.video.mobile.server.cms.model.collection.PlayerPvList;
import com.cyou.video.mobile.server.cms.model.collection.StatisticJobLastUpdateTime;
import com.cyou.video.mobile.server.cms.mongo.dao.collection.PlayerLogCollectionDao;
import com.cyou.video.mobile.server.cms.service.collection.PlayerLogCollectionService;
import com.cyou.video.mobile.server.cms.service.common.RedisTemplate;

/**
 * 
 * @author lusi
 * 
 */
@Service("playerLogCollectionService")
public class PlayerLogCollectionServiceImpl implements PlayerLogCollectionService {

  @Autowired
  private PlayerLogCollectionDao playerLogCollectionDao;


  @Resource(name = "redisTemplate")
  private RedisTemplate redisTemplate;


  /**
   * 测试类暂时使用
   */
  @Override
  public int collectLogInfo(List<PlayerLogCollection> collections) {
    return playerLogCollectionDao.insertBatch(collections);
  }
  
  @Override
  public String getAdvCode(){

    return playerLogCollectionDao.getAdvCode();
  }

  @Override
public String getAdvState(String appid) {
	// TODO Auto-generated method stub
	return playerLogCollectionDao.getAdvState(appid);
}

  @Override
	public int disableAdvertising(String id) throws Exception {
	  //status:0表示停用，1表示启用
		PlayerAdv ad = playerLogCollectionDao.getAdvertisingById(id);
		if (Integer.parseInt(ad.getState()) == 0) {//停用广告，已经停用返回
			return 1;
		}
		int result= playerLogCollectionDao.disableAdvertising(id);		//停用 返回0表示失败，其他表示成功
		if(result!=0){
			//停用所有app里的广告
			String islocal="1";
			if(ad.getType().equals("trdparty")) islocal="0";
			result=playerLogCollectionDao.updateAdvStatePlayerApps(islocal, "0");
		}
		return result;
	}

	@Override
	public int enableAdvertising(String id) throws Exception {
		PlayerAdv ad = playerLogCollectionDao.getAdvertisingById(id);//启用广告
		if (Integer.parseInt(ad.getState()) == 1) {//已经启用返回
			return 1;
		}
		int result = playerLogCollectionDao.enableAdvertising(id);//数据库更新
		if(result!=0){
			//启用所有app里的广告
			String islocal="1";
			if(ad.getType().equals("trdparty")) islocal="0";
			result=playerLogCollectionDao.updateAdvStatePlayerApps(islocal, "1");
		}
		return result;
	}
	
	

  @Override
	public PlayerAdv getAdvertisingById(String id) throws Exception {
		// TODO Auto-generated method stub
	  PlayerAdv ad = playerLogCollectionDao.getAdvertisingById(id);
	  return ad;
	}





  @Override
  public int getTotalNum(String collectionName) throws Exception {
    Map<String, Object> params = new HashMap<String, Object>();
    return (int) playerLogCollectionDao.getPVCountByName(collectionName, params, "");
  }
 

	@Override
public int updateApps(PlayerApps playerApps) throws Exception {
	// TODO Auto-generated method stub
	if(playerApps==null) throw new Exception();
	return playerLogCollectionDao.updateApps(playerApps);
}
	

	@Override
	public int updateAdv(PlayerAdv playerAdv) throws Exception {
		// TODO Auto-generated method stub
		if(playerAdv==null) throw new Exception();
		return playerLogCollectionDao.updateAdv(playerAdv);
	}

	//@Override
	public Pagination getPlayerLogCollection(Map<String, Object> params) throws Exception {
	  Pagination pagination = null;
	  pagination = new Pagination();
	  int curPage = Integer.parseInt(params.get("curPage").toString());
	  int pageSize = Integer.parseInt(params.get("pageSize").toString());
	  pagination.setCurPage(curPage);
	  pagination.setPageSize(pageSize);
	  curPage = (curPage - 1) * pageSize;
	  params.remove("curPage");
	  params.put("curPage", curPage);
	  List<PlayerLogCollection> list = playerLogCollectionDao.getPlayerCollection(null, curPage, pageSize, params);
	  pagination.setRowCount(9999);
	  pagination.setContent(list);
	  return pagination;
	}

  @Override
  public Pagination getPV(Map<String, Object> params) throws Exception {
    Pagination pagination = null;
    pagination = new Pagination();
    int curPage = Integer.parseInt(params.get("curPage").toString());
    int pageSize = Integer.parseInt(params.get("pageSize").toString());
    pagination.setCurPage(curPage);
    pagination.setPageSize(pageSize);
    curPage = (curPage - 1) * pageSize;
    params.remove("curPage");
    params.put("curPage", curPage);
	List<PlayerPvList> list = playerLogCollectionDao.getPV(null, curPage, pageSize, params);
    pagination.setRowCount(9999);
    pagination.setContent(list);
    return pagination;
  }
  


  
@Override
public Pagination getAV(Map<String, Object> params) throws Exception {
	Pagination pagination = null;
    pagination = new Pagination();
    int curPage = Integer.parseInt(params.get("curPage").toString());
    int pageSize = Integer.parseInt(params.get("pageSize").toString());
    pagination.setCurPage(curPage);
    pagination.setPageSize(pageSize);
    curPage = (curPage - 1) * pageSize;
    params.remove("curPage");
    params.put("curPage", curPage);
	List<PlayerAvList> list = playerLogCollectionDao.getAV(null, curPage, pageSize, params);
    pagination.setRowCount(9999);
    pagination.setContent(list);
    return pagination;
}

public Pagination listAdv(Map<String, Object> params) throws Exception {
	Pagination pagination = null;
    pagination = new Pagination();
    int curPage = Integer.parseInt(params.get("curPage").toString());
    int pageSize = Integer.parseInt(params.get("pageSize").toString());
    pagination.setCurPage(curPage);
    pagination.setPageSize(pageSize);
    curPage = (curPage - 1) * pageSize;
    params.remove("curPage");
    params.put("curPage", curPage);
	List<PlayerAdv> list = playerLogCollectionDao.listAdv(null, curPage, pageSize, params);
    pagination.setRowCount(9999);
    pagination.setContent(list);
    return pagination;
}


  @Override
public Pagination listApps(Map<String, Object> params) throws Exception {
	  Pagination pagination = null;
	    pagination = new Pagination();
	    int curPage = Integer.parseInt(params.get("curPage").toString());
	    int pageSize = Integer.parseInt(params.get("pageSize").toString());
	    pagination.setCurPage(curPage);
	    pagination.setPageSize(pageSize);
	    curPage = (curPage - 1) * pageSize;
	    params.remove("curPage");
	    params.put("curPage", curPage);
		List<PlayerApps> list = playerLogCollectionDao.listApps(null, curPage, pageSize, params);
	    pagination.setRowCount(9999);
	    pagination.setContent(list);
	    return pagination;
}

@Override
  public List<StatisticJobLastUpdateTime> getPVLastUpdateTime(Map<String, Object> params) throws Exception {
    return playerLogCollectionDao.getPVLastUpdate(Consts.COLLECTION_USER_ITEM_OPERATE_PV_NAME, params);
  }

  @Override
  public Long getCount(String name) {

    return playerLogCollectionDao.getCount(name);
  }

  @Override
  public Long getCount(Query query, String name) {

    return playerLogCollectionDao.getCountByQuery(query, name);
  }

 

  public void setPkgStatus(String pkg, String flag) {
    Jedis jedis = null;
    try {
      jedis = redisTemplate.jedis();
      jedis.set("android_installed_game:"+pkg, flag + "", "NX", "EX", (long) 1296000);
    }
    catch(JedisConnectionException e) {
      if(null != jedis) {
        redisTemplate.returnBrokenResource(jedis);
        jedis = null;
      }
    }
    finally {
      if(null != jedis) {
        redisTemplate.returnResource(jedis);
      }
    }

  }

 

  public int getPkgStatus(String pkg) {
    Jedis jedis = null;
    try {
      jedis = redisTemplate.jedis();
      String v = jedis.get("android_installed_game:"+pkg);
      if(StringUtils.isEmpty(v)) {
        return 0;
      }
      else {
        return Integer.parseInt(v);
      }
    }
    catch(JedisConnectionException e) {
      if(null != jedis) {
        redisTemplate.returnBrokenResource(jedis);
        jedis = null;
      }
      return 0;
    }
    catch(Exception e) {
      return 0;
    }
    finally {
      if(null != jedis) {
        redisTemplate.returnResource(jedis);
      }
    }
  }
}
