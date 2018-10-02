app.service('payService', function($http) {
	this.payInit=function(){
		return $http.get('../pay/wxPay.do');
	}
	this.payStatus=function(goodsId){
		return $http.get('../pay/wxPayStatus.do?goodsId='+goodsId);
	}
	
})