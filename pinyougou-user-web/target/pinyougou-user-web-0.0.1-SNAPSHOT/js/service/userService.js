//服务层
app.service('userService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../goods/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../user/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../user/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity,phone){
		return  $http.post('../user/add.do?phoneCode='+phone,entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../user/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../user/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../user/search.do?page='+page+"&rows="+rows, searchEntity);
	} 
	this.sendPhone=function(phone){
		return $http.get('../user/phoneNumber.do?phone='+phone);
	}
	this.gain=function(){
		return $http.get('../user/gainUserName.do');
	}
	this.initOrder=function(){
		return $http.get('../user/findOrder.do');
	}
	this.skipPay=function(orderId){
		return $http.get('../user/skipPay.do?goodsId='+orderId);
	}
});
