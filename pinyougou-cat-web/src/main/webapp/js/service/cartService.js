//服务层
app.service('cartService',function($http){
	    	

   //查看购物车内容
	this.initCart=function(){
		return $http.get('/cat/lookcat.do')
	}
	
	this.addNumItemCart=function(itemId,num){
		return $http.get('/cat/catinsert.do?itemId='+itemId+"&num="+num);
	}
	this.deleteItem=function(itemId){
		
		return $http.get('/cat/deleteItemCart.do?itemId='+itemId);
	}
});
