package com.pinyougou.seach.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.seach.service.SeachItemService;

@SuppressWarnings("all")
@Service(timeout = 5000)
public class SeachItemServiceImpl implements SeachItemService {
	private String cateGory = null;
	@Autowired
	private SolrTemplate solrTemplate;
	// 引入redis缓存技术
	@Autowired
	private RedisTemplate redisTemplate;

	/*
	 * @Override // 为什么参数以及返回值都是map集合? public Map seachItem(Map condition) { //
	 * 在商品搜索过程中用户搜索的关键字不可能是单一的有可能还是非常多的field掺杂到一起的所以使用Map来分装条件 Query query = new
	 * SimpleQuery(); // copy域起到了作用 不单一的擦查询字段,而是总结词条进行搜索 //手动指定一个key Criteria
	 * criteria = new Criteria("item_keywords").is(condition.get("keywords"));
	 * //添加条件 query.addCriteria(criteria); ScoredPage<TbItem> queryForPage =
	 * solrTemplate.queryForPage(query, TbItem.class); Map<String, Object> map =
	 * new HashMap<>(); //List集合 map.put("rows", queryForPage.getContent());
	 * return map; }
	 */
	private Map findSpecBrand(String cateGoryName) {
		Map map = new HashMap<>();
		Long type = (Long) redisTemplate.boundHashOps("itemCat").get(cateGoryName);
		if (type != null) {
			List specList = (List) redisTemplate.boundHashOps("specList").get(type);
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(type);
			map.put("specList", specList);
			map.put("brandList", brandList);
		}

		return map;
	}

	@Override
	public Map seachItem(Map condition) {
		Map<String, Object> map = new HashMap<>();
		List<String> findGroup = findGroup(condition);
		map.put("cateGroup", findGroup);
		map.putAll(findHighlight(condition));
		String cateGory = (String) condition.get("cateGory");
		if (cateGory != null) {
			Map findSpecBrand = findSpecBrand(cateGory);
			map.putAll(findSpecBrand);
		}
		if (findGroup.size() > 0) {
			Map findSpecBrand = findSpecBrand(findGroup.get(0));
			map.putAll(findSpecBrand);

		}

		return map;
	}

	private Map findHighlight(Map condition) {
		// 使用高亮显示:
		Map<String, Object> map = new HashMap<>();
		// 创建高亮显示对象
		HighlightQuery query = new SimpleHighlightQuery();
		// 指定一个需要高亮显示的字段 可以指定多个高亮显示字段
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>"); // 指定前缀
		highlightOptions.setSimplePostfix("</em>"); // 指定后缀
		query.setHighlightOptions(highlightOptions);// 添加高亮数据

		// 1.1按照指定字段进行查询
		if (condition.get("keywords") != "") {
			Criteria criteria = new Criteria("item_keywords").is(condition.get("keywords"));
			query.addCriteria(criteria);

		}

		// 1.2按照分类进行查询
		if (!"".equals(condition.get("cateGory"))) {

			Criteria filterCriteria = new Criteria("item_category").is(condition.get("cateGory"));
			FilterQuery filter = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filter);
		}
		// 1.3按照品牌查询
		if (!"".equals(condition.get("brand"))) {

			Criteria filterCriteria = new Criteria("item_brand").is(condition.get("brand"));

			FilterQuery filter = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filter);
		}

		// 1.4按照指定规格添加查询条件 solr的动态查询指定条件
		if (condition.get("spec") != null) {
			// 由于是一个Map类型的集合所以需要进行遍历才可以
			Map<String, String> specList = (Map) condition.get("spec");
			for (String key : specList.keySet()) {
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specList.get(key));
				FilterQuery filter = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filter);
			}

		}
		// 1.5按照价格的区间进行少筛选数据 //如果价格筛选参数不为"" null的话
		if (!"".equals(condition.get("price"))) {
			// 判断价格的区间 起始为0 ---*
			// 具体思路
			String stringObject = (String) condition.get("price");
			// 0 - 500 不
			String[] stringPrice = stringObject.split("-");
			if (!stringPrice[0].equals("0")) {// 如果当前第一个数值不等于0的话那么肯定是一个大于0的数据
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(stringPrice[0]);
				FilterQuery filter = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filter);
				// 如果是0的话去第 2个值 equals让它无上限

			}
			if (stringPrice.length >= 2) {
				if (!stringPrice[1].equals("")) {
					Criteria filterCriteria = new Criteria("item_price").lessThanEqual(stringPrice[1]);
					FilterQuery filter = new SimpleFilterQuery(filterCriteria);
					query.addFilterQuery(filter); // 500 - 100 3000
				}
			}
		}

		// 看这句话:
		// 如果数据为 0 - 500 那么第一条判断肯定过不去走第二条,第二条判断是最高价格不是*(最大)的话就走小于等于
		// 如果数据为3000 那么直接走第一条 大于等于
		// 如果数据为 500-1000 那么第一条就是 大于等于500 第二条就是小于等于1000

		// 接收前台传递的当前页数,以及要查询的总页数
		// 如果为空那么我们需要默认给出参数,
		Integer pageIndex;
		Integer pageSize;
		if (condition.get("pageIndex") == null || condition.get("pageIndex") == "") {
			// 如果前台没有参数默认给出当前页数
			pageIndex = 1;
		} else {
			pageIndex = (Integer) condition.get("pageIndex");
		}
		if (condition.get("pageSize") == null || condition.get("pageSize") == "") {
			pageSize = 20;
		} else {
			pageSize = (Integer) condition.get("pageSize");
		}
		// 这样做保证在任何时候都是有数据可循的
		// 设置分页信息
		query.setOffset((pageIndex - 1) * pageSize);

		query.setRows(pageSize);

		// 为了做到通用性我们的操作应该由前台来决定要怎么进行查询:
		// 1.6价格的升序以及排序操作:
		String sortValue = (String) condition.get("sort");
		String sortField = (String) condition.get("sortField");
		//判断当前的两个参数是否是"" 值如果为""那就忽略sort这一项逻辑
		if ((!sortValue.equals("") && !sortField.equals(""))) {
			//升序操作
			if (sortValue.equals("ASC")) {
				Sort sort = new Sort(Direction.ASC, "item_"+sortField);
				query.addSort(sort);
			}
			//降序操作
			if(sortValue.equals("DESC")){
				Sort sort = new Sort(Direction.DESC, "item_"+sortField);
				query.addSort(sort);
			}
		}

		// 1.3按照品牌进行高亮显示操作
		HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
		// 获取的是一个高亮的集合数据 但是还需要进行设置才可以 这种形式是一种集合套集合的形式
		TbItem entity = null;
		List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : highlighted) {// 循环高亮入口集合设置

			try {
				if (highlightEntry.getHighlights().get(0).getSnipplets().size() > 0
						&& highlightEntry.getHighlights().size() > 0) {
					// 为什么这么多集合以及get(0)的操作:
					// 1: 我们查询的是一个字段的形式,但是系统不会这样认为,我们也可以指定多个高亮显示的字段
					entity = highlightEntry.getEntity();// 获取实体类
					cateGory = entity.getCategory();
					// 重新对实体类的title赋值 格式就是指定的<em>格式</em>
					entity.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
					// 上面的操作都是和query查询的数据有关联的,所以在这循环操作的数据直接影响查询的实体具体数据
				}
			} catch (Exception e) {
				System.err.println("数组越界.....");
			}
		}

		map.put("rows", highlightPage.getContent());
		// 需要将总记录数以及总页数当作数据响应前台
		map.put("pageNumbers", highlightPage.getTotalPages());// 获取总页数
		map.put("pageRecord", highlightPage.getTotalElements());// 获取总记录数
		return map;
	}

	// 分组查询操作 使用关键字group
	private List<String> findGroup(Map condition) {
		List<String> list = new ArrayList<String>();
		Query query = new SimpleQuery();
		// 查询条件
		Criteria criteria = new Criteria("item_keywords").is(condition.get("keywords"));
		query.addCriteria(criteria);
		// 按照指定字段名称进行分组操作 也可以添加多个字段名称
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 获取分组页
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		// 查询分组入口
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		// 开启分组通道
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for (GroupEntry<TbItem> groupEntry : content) {
			// 获取分组的数据
			String group = groupEntry.getGroupValue();
			list.add(group);
		}

		return list;
	}

	@Override //索引库的保存操作  在solr的service服务中声明方法,注意泛型必须指定否则就会报错
	public void updateSolr(List<TbItem> list) {
		//如果list是空的那么solr会报错所以避免加判断
		if(list.size()!=0){
			  solrTemplate.saveBeans(list);	
			     solrTemplate.commit();
		}
   
	}

	@Override
	public void deleteSolr(Long[] goodsId) {
    //删除索引操操作删除SPU            
		Query query = new SimpleQuery("*:*");
		                                  //根据具体的字段名称作为条件进行匹配删除
		Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(goodsId));
		query.addCriteria(criteria );
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
}
