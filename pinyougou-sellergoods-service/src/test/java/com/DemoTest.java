package com;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;

public class DemoTest {
	@Test
  public void demo(){
		String string = new String("撒旦撒阿斯达啊 ");
         String json = JSON.toJSONString(string);
         System.out.println(json.toString());
	}

}
