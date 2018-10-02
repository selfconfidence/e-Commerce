package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemTemplateService;
@Component
public class TopicCreatePage implements MessageListener{
	@Autowired
	private ItemTemplateService itemTemplateService;
	

	@Override
	public void onMessage(Message arg0) {
		TextMessage textMessage = (TextMessage)arg0;
		try {
			itemTemplateService.itemTemplate(Long.parseLong(textMessage.getText()));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
