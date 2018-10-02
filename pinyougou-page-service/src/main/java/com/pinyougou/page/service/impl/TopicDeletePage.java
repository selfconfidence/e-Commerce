package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemTemplateService;

@Component
public class TopicDeletePage implements MessageListener {
	
	@Autowired
	private ItemTemplateService itemTemplateService;

	@Override
	public void onMessage(Message arg0) {
         ObjectMessage objectMessage = (ObjectMessage)arg0;
        try {
			Long[] goodsId = (Long[]) objectMessage.getObject();
		   for(int i=0;i<goodsId.length;i++){
				itemTemplateService.deleteItem(goodsId[i]);
		   }
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
