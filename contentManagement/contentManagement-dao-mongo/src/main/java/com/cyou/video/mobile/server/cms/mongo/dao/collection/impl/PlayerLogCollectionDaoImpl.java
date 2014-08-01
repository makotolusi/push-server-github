package com.cyou.video.mobile.server.cms.mongo.dao.collection.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.model.collection.PlayerAdv;
import com.cyou.video.mobile.server.cms.model.collection.PlayerApps;
import com.cyou.video.mobile.server.cms.model.collection.PlayerAvList;
import com.cyou.video.mobile.server.cms.model.collection.PlayerLogCollection;
import com.cyou.video.mobile.server.cms.model.collection.PlayerPvList;
import com.cyou.video.mobile.server.cms.model.collection.StatisticJobLastUpdateTime;
import com.cyou.video.mobile.server.cms.model.push.PushTagCollection;
import com.cyou.video.mobile.server.cms.mongo.dao.collection.PlayerLogCollectionDao;
import com.cyou.video.mobile.server.common.Constants;
import com.mongodb.WriteResult;

@Repository("playerLogCollectionDao")
public class PlayerLogCollectionDaoImpl implements PlayerLogCollectionDao {

	@Autowired
	private MongoOperations mongoTemplate;

	@Override
	public int insertBatch(List<PlayerLogCollection> collections) {
		mongoTemplate.insertAll(collections);

		return collections.size();
	}

	@Override
	public List<StatisticJobLastUpdateTime> getStatisticJobLastUpdateTime() {

		return mongoTemplate
				.find(new Query(), StatisticJobLastUpdateTime.class);
	}

	private void query(Map<String, Object> params, Query query, String tag) {
		if (params.get("dateFrom") != null) {
			if (params.get("dateTo") == null) {
				if ("1".equals(params.get("collectionName"))) {
					query.addCriteria(Criteria.where("lastUpdate").lte(
							params.get("dateFrom").toString()));
				} else {
					query.addCriteria(Criteria.where("statdate").lte(
							params.get("dateFrom").toString()));
				}

			} else {
				if ("1".equals(params.get("collectionName"))) {
					query.addCriteria(Criteria.where("lastUpdate")
							.lte(params.get("dateTo").toString())
							.gte(params.get("dateFrom").toString()));
				} else {
					query.addCriteria(Criteria.where("statdate")
							.lte(params.get("dateTo").toString())
							.gte(params.get("dateFrom").toString()));
				}
			}
		}

		if (params.get("appid") != null) {
			Pattern pattern = Pattern.compile("^.*"
					+ params.get("appid").toString() + ".*$");
			query.addCriteria(Criteria.where(tag + "appid").regex(pattern));
		} else if (params.get("appid2") != null) {
			query.addCriteria(Criteria.where(tag + "appid").is(
					params.get("appid2").toString()));
		}
	}

	@Override
	public List<PlayerLogCollection> getPlayerCollection(
			PlayerLogCollection collections, int page, int size,
			Map<String, Object> params) {
		Query query = new Query();
		query.limit(size);
		query.skip(page);
		query.with(new Sort(Sort.Direction.DESC, "lastUpdate"));

		query(params, query, "");

		return mongoTemplate.find(query, PlayerLogCollection.class);
	}

	@Override
	public List<PlayerPvList> getPV(PlayerPvList collections, int page,
			int size, Map<String, Object> params) {

		Query query = new Query();
		query.limit(size);
		query.skip(page);
		query.with(new Sort(Sort.Direction.DESC, "statdate"));

		query(params, query, "");
		return mongoTemplate.find(query, PlayerPvList.class);
	}

	@Override
	public List<PlayerAvList> getAV(PlayerAvList collections, int page,
			int size, Map<String, Object> params) {
		Query query = new Query();
		query.limit(size);
		query.skip(page);
		query.with(new Sort(Sort.Direction.DESC, "statdate"));
		query(params, query, "");

		return mongoTemplate.find(query, PlayerAvList.class);
	}

	@Override
	public List<PlayerAdv> listAdv(PlayerAdv collections, int page, int size,
			Map<String, Object> params) {
		Query query = new Query();
		query.limit(size);
		query.skip(page);
		// query.with(new Sort(Sort.Direction.DESC, "pv"));
		query(params, query, "");

		return mongoTemplate.find(query, PlayerAdv.class);
	}

	public List<PlayerApps> listApps(PlayerApps collections, int page,
			int size, Map<String, Object> params) {
		Query query = new Query();
		query.limit(size);
		query.skip(page);
		// query.with(new Sort(Sort.Direction.DESC, "pv"));
		query(params, query, "");

		return mongoTemplate.find(query, PlayerApps.class);
	}

	@Override
	public int updateApps(PlayerApps playerApps) {
		// TODO Auto-generated method stub
		try {
			Update update = new Update();
			update.set("appNameNew", playerApps.getAppNameNew());
			update.set("localState", playerApps.getLocalState());
			WriteResult wr = mongoTemplate.updateMulti(new Query(Criteria
					.where("appid").is(playerApps.getAppid())), update,
					PlayerApps.class);
			return wr.getN();
		} catch (Exception e) {

			return 0;
		}
	}

	public int updateAdv(PlayerAdv playerAdv) {
		// TODO Auto-generated method stub
		try {
			Update update = new Update();
			update.set("advcode", playerAdv.getAdvcode());
			WriteResult wr = mongoTemplate.updateFirst(new Query(Criteria
					.where("_id").is(playerAdv.getId())), update,
					PlayerAdv.class);
			return wr.getN();
		} catch (Exception e) {

			return 0;
		}
	}

	@Override
	public int insert(Object o) {
		try {
			mongoTemplate.insert(o);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	@Override
	public void save(Object o) {
		mongoTemplate.save(o);
	}

	@Override
	public Object updateInc(String incField, Query q, Class cla) {
		return mongoTemplate.findAndModify(q, new Update().inc(incField, 1),
				cla);
	}

	@Override
	public Object findByField(Class o, String field, String value) {
		try {
			return mongoTemplate.findOne(
					new Query(Criteria.where(field).is(value)), o);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void remove(Object o) {
		mongoTemplate.remove(o);
	}

	@Override
	public void remove(Class o, Query query) {
		mongoTemplate.remove(query, o);
	}

	@Override
	public List<Object> findByCondition(Class o, Query query) {
		try {
			return mongoTemplate.find(query, o);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long countByCondition(Class o, Query query) {
		try {
			return mongoTemplate.count(query, o);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<StatisticJobLastUpdateTime> getPVLastUpdate(
			String collectionName, Map<String, Object> params) {
		Query query = new Query();

		// query.addCriteria(Criteria.where("statisicJobName").is(collectionName));

		return mongoTemplate.find(query, StatisticJobLastUpdateTime.class);
	}

	@Override
	public long getCount(String name) {
		Query query = new Query();

		return mongoTemplate.count(query, name);
	}

	@Override
	public long getCount(Class name) {
		Query query = new Query();

		return mongoTemplate.count(query, name);
	}

	@Override
	public long getCountByQuery(Query query, String name) {

		return mongoTemplate.count(query, name);
	}

	@Override
	public long getPVCountByName(String collectionName,
			Map<String, Object> params, String tag) {
		Query query = new Query();
		query(params, query, tag);
		return mongoTemplate.count(query, collectionName);
	}

	@Override
	public String getAdvCode() {
		// TODO Auto-generated method stub
		Query query = new Query();
		PlayerAdv advRecord = mongoTemplate.findOne(query, PlayerAdv.class);
		String adv = advRecord.getAdvcode();
		return adv;
	}

	@Override
	public String getAdvState(String appid) {
		// TODO Auto-generated method stub
		PlayerApps advRecord = mongoTemplate.findOne(
				new Query(Criteria.where("appid").is(appid)), PlayerApps.class);
		if (advRecord == null)
			return "2";// appid不存在
		String state = advRecord.getAdvState();
		return state;
	}

	@Override
	public PlayerAdv getAdvertisingById(String id) {
		// TODO Auto-generated method stub
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		PlayerAdv advRecord = mongoTemplate.findOne(query, PlayerAdv.class);
		return advRecord;
	}

	@Override
	public int disableAdvertising(String id) {
		// TODO Auto-generated method stub
		try {
			Update update = new Update();
			update.set("state", "0");
			WriteResult wr = mongoTemplate.updateFirst(new Query(Criteria
					.where("_id").is(id)), update, PlayerAdv.class);
			return wr.getN();
		} catch (Exception e) {

			return 0;
		}
	}

	@Override
	public int enableAdvertising(String id) {
		// TODO Auto-generated method stub
		try {
			Update update = new Update();
			update.set("state", "1");
			WriteResult wr = mongoTemplate.updateFirst(new Query(Criteria
					.where("_id").is(id)), update, PlayerAdv.class);
			return wr.getN();
		} catch (Exception e) {

			return 0;
		}
	}

	@Override
	public int updateAdvStatePlayerApps(String islocal, String state) {
		// TODO Auto-generated method stub
		try {
			Update update = new Update();
			update.set("advState", state);
			WriteResult wr = mongoTemplate
					.updateMulti(
							new Query(Criteria.where("localState").is(islocal)),
							update, PlayerApps.class);
			return wr.getN();
		} catch (Exception e) {

			return 0;
		}
	}

}
