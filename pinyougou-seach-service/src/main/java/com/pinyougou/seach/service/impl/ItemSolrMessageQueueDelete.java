package com.pinyougou.seach.service.impl;

import java.util.Arrays;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.seach.service.SeachItemService;

@Component
public class ItemSolrMessageQueueDelete implements MessageListener {
	@Autowired
	private SeachItemService seachItemService;

	@Override
	public void onMessage(Message arg0) {
		try {

			ObjectMessage objectMessage = (ObjectMessage) arg0;
			Long[] index = (Long[]) objectMessage.getObject();
			seachItemService.deleteSolr(index);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
