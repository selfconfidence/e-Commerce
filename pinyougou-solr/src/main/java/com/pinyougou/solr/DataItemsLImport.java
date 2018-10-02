package com.pinyougou.solr;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class DataItemsLImport {
	@Autowired
    private TbItemMapper itemMapper;
	@Autowired
	private SolrTemplate solrTemplate;
	
public void dataImportSolr(){
	TbItemExample example = new TbItemExample();
	Criteria criteria = example.createCriteria();
	criteria.andStatusEqualTo("1");
	List<TbItem> itemList = itemMapper.selectByExample(example );
    for (TbItem tbItem : itemList) {
    	Map specMap= JSON.parseObject(tbItem.getSpec());//将spec字段中的json字符串转换为map
    	tbItem.setSpecMap(specMap);//给带注解的字段赋值
    }
	solrTemplate.saveBeans(itemList);
	solrTemplate.commit();
}
 public static void main(String[] args) {
	  //使用原生的方式去加载Spring的xml配置文件信息等
	ApplicationContext loadXml = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
    DataItemsLImport bean = loadXml.getBean(DataItemsLImport.class);
    bean.dataImportSolr();
    
 }

 public void deleSolr(){
	 
	 Query query = new SimpleQuery("*:*");
	
	solrTemplate.delete(query);
	solrTemplate.commit();
 }
 @Test
 public void deleSolrs(){
	 ApplicationContext loadXml = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
	    DataItemsLImport bean = loadXml.getBean(DataItemsLImport.class);
	    bean.deleSolr();
 }
}
