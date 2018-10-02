package com.pinyougou.user.service.impl;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entitygroup.OrderIndent;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderIdToString;
import com.pinyougou.pojo.TbOrderItemExample;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.pojo.TbUserExample.Criteria;
import com.pinyougou.user.service.UserService;

import entity.PageResult;
import util.SmsMessage6Util;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		userMapper.insert(user);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKeySelective(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}
			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
		
		@Autowired
		private RedisTemplate redisTemplate;
		
		@Autowired
		private JmsTemplate jmsTemplate;
		@Autowired
		private Destination queueSmsDestination;
		
		@Value("${signName}")
		private String signName;
		@Value("${templateCode}")
		private String templateCode;

		@Override
		public void smsSend(final String phoneNumber) {
			//调用微服务短信功能,在配置文件中都是由密钥的所以引过来之后,直接调用即可
			//在消费者那一端使用的是一个Map类型的接收数据类型的参数, 所以在发送的时候也应该给对应的Map参数
			final String smsMessage = SmsMessage6Util.smsMessageRandom();	
			
			
			//发送短信
	           jmsTemplate.send(queueSmsDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
				MapMessage createMapMessage = session.createMapMessage();
				createMapMessage.setString("mobile", phoneNumber);
				createMapMessage.setString("signName", signName);
				createMapMessage.setString("templateCode", templateCode);
				createMapMessage.setString("params", "{\"code\":\""+smsMessage+"\"}");
					return createMapMessage;
				}
			});
	           
	           //放入缓存
	           redisTemplate.boundHashOps("smsCode").put(phoneNumber, smsMessage);
			
			
		}

		@Override
		public boolean checkPhone(String phone, String statusCode) {
			//判断缓存的数据是否给页面输入的数据是保持一致性的.
			if(phone ==""||phone==null||statusCode==null||statusCode==""){
				return false;
			}
			String code = (String)redisTemplate.boundHashOps("smsCode").get(phone);
			if(code==null){
				return false;
			}
			if(!code.equals(statusCode)){
				return false;
			}
			
			return true;
		}

		@Override
		public boolean flagData(String nowDate) {
			
			return false;
		}

		@Override
		public List findStatusList(String status,String userName) {
			List<OrderIndent> orderStatus = new ArrayList();
			 TbOrderExample example = new TbOrderExample();
			 com.pinyougou.pojo.TbOrderExample.Criteria criteria = example.createCriteria();
			 criteria.andStatusEqualTo(status); 
			 criteria.andUserIdEqualTo(userName);
			List<TbOrder> byExample = orderMapper.selectByExample(example );
			for (TbOrder tbOrder : byExample) {
				OrderIndent orderIndent= new OrderIndent();
				TbOrderItemExample itemExample = new TbOrderItemExample();
				com.pinyougou.pojo.TbOrderItemExample.Criteria criteria2 = itemExample.createCriteria();
				criteria2.andOrderIdEqualTo(tbOrder.getOrderId());
				orderIndent.setTbOrder(addTbOrderIdToString(tbOrder));
				orderIndent.setTbOrderItem(tbOrderItemMapper.selectByExample(itemExample ));
				orderStatus.add(orderIndent);
				
			}
			return orderStatus;
		}
		
		//没办法转化pojo操作
	    private TbOrderIdToString addTbOrderIdToString( TbOrder tbOrder){
	    	TbOrderIdToString tbOrderIdToString = new TbOrderIdToString();
			tbOrderIdToString.setOrderId(Long.toString(tbOrder.getOrderId()));
			tbOrderIdToString.setBuyerMessage(tbOrder.getBuyerMessage());
			tbOrderIdToString.setBuyerNick(tbOrder.getBuyerNick());
			tbOrderIdToString.setBuyerRate(tbOrder.getBuyerRate());
			tbOrderIdToString.setCloseTime(tbOrder.getCloseTime());
			tbOrderIdToString.setConsignTime(tbOrder.getConsignTime());
			tbOrderIdToString.setCreateTime(tbOrder.getCreateTime());
			tbOrderIdToString.setEndTime(tbOrder.getEndTime());
			tbOrderIdToString.setExpire(tbOrder.getExpire());
			tbOrderIdToString.setInvoiceType(tbOrder.getInvoiceType());
			tbOrderIdToString.setPayment(tbOrder.getPayment());
			tbOrderIdToString.setPaymentTime(tbOrder.getPaymentTime());
			tbOrderIdToString.setPaymentType(tbOrder.getPaymentType());
			tbOrderIdToString.setPostFee(tbOrder.getPostFee());
			tbOrderIdToString.setReceiver(tbOrder.getReceiver());
			tbOrderIdToString.setReceiverAreaName(tbOrder.getReceiverAreaName());
			tbOrderIdToString.setReceiverMobile(tbOrder.getReceiverMobile());
			tbOrderIdToString.setReceiverZipCode(tbOrder.getReceiverZipCode());
			tbOrderIdToString.setSellerId(tbOrder.getSellerId());
			tbOrderIdToString.setShippingCode(tbOrder.getShippingCode());
			tbOrderIdToString.setShippingName(tbOrder.getShippingName());
			tbOrderIdToString.setSourceType(tbOrder.getSourceType());
			tbOrderIdToString.setStatus(tbOrder.getStatus());
			tbOrderIdToString.setUpdateTime(tbOrder.getUpdateTime());
			tbOrderIdToString.setUserId(tbOrder.getUserId());
			return tbOrderIdToString;
	    }

		@Override
		public TbUser findPic(String name) {
			TbUserExample example = new TbUserExample();
			Criteria criteria = example.createCriteria();
			criteria.andUsernameEqualTo(name);
			return userMapper.selectByExample(example ).get(0);
		}

		
	
}
