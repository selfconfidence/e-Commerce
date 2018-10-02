package com.pinyougou.seach.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.seach.service.SeachItemService;
@Component
public class ItemSolrMessageQueueUpdate implements MessageListener {
    @Autowired
    private SeachItemService seachItemService;
	
	@Override
	public void onMessage(Message arg0) {
		TextMessage textMessage = (TextMessage) arg0;
        try {
        	//将消息转成List即可再次传递即可  ,解决耦合性问题 OK
			List<TbItem> parseArray = JSON.parseArray(textMessage.getText(),TbItem.class);
			seachItemService.updateSolr(parseArray);
        } catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
