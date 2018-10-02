package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemTemplateService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

import freemarker.template.Configuration;
import freemarker.template.Template;
@Service
public class ItemTemplateServiceImpl implements ItemTemplateService {
  @Autowired
  private TbGoodsDescMapper  tbGoodsDescMapper;
  @Autowired
  private TbGoodsMapper  tbGoodsMapper;
  @Value("${templateUrl}")  //引用路径
  private String URL;
  @Autowired
  private TbItemCatMapper tbItemCatMapper;
  @Autowired   //使用Spring提供的管理网页静态的实现类
  private FreeMarkerConfigurer freeMarkerConfigurer;
  @Autowired 
  private TbItemMapper tbItemMapper;
  
	@Override
	public boolean itemTemplate(Long goodsId) {
		try { 
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			 Map map = new HashMap<>();
			 TbGoods selectByPrimaryKey = tbGoodsMapper.selectByPrimaryKey(goodsId);
			 map.put("goods",selectByPrimaryKey );
			 TbGoodsDesc byPrimaryKey = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
			 map.put("goodsDesc",byPrimaryKey);
			 String categoryId1 = tbItemCatMapper.selectByPrimaryKey(selectByPrimaryKey.getCategory1Id()).getName();
			 String categoryId2 = tbItemCatMapper.selectByPrimaryKey(selectByPrimaryKey.getCategory2Id()).getName();
			 String categoryId3 = tbItemCatMapper.selectByPrimaryKey(selectByPrimaryKey.getCategory3Id()).getName();
			 map.put("categoryId1", categoryId1);
			 map.put("categoryId2", categoryId2);
			 map.put("categoryId3", categoryId3);
			 TbItemExample example = new TbItemExample();
			 com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
			 createCriteria.andGoodsIdEqualTo(selectByPrimaryKey.getId());
			 createCriteria.andStatusEqualTo("1");
			 example.setOrderByClause("is_default desc");
			 List<TbItem> selectByExample = tbItemMapper.selectByExample(example);
			 map.put("itemCat", selectByExample);
			 Writer out  = new FileWriter(URL+goodsId+".html");
			template.process(map, out);
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	@Override
	public boolean deleteItem(Long goodsId) {
		
		try{
			new File(URL+goodsId+".html").delete();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}

}
