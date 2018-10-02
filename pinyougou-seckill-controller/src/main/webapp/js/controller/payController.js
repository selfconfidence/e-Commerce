
 //控制层 
app.controller('payController' ,function($scope ,payService,$location){	

	$scope.payInit=function(){
		payService.payInit().success(
		function(response){
			$scope.out_trade_no=response.out_trade_no;
			$scope.total_fee=(response.total_fee/100).toFixed(2);
			 var qr = window.qr = new QRious({
			      element: document.getElementById('qrious'),
			      size: 250,
		 		  level:'H',
			      value: response.code_url
			    })

			 $scope.payStatus();
			
		}		
		);
	}
	 //前台调用后端进行订单状态的查询
	$scope.payStatus=function(){
		payService.payStatus($scope.out_trade_no).success(
		 function(response){
			 if(response.flag){  //如果为true那么就是i支付OK的状态,直接跳转支付页面顺带将支付金额传递过去进行展示
				 location.href="paysuccess.html#?money="+$scope.total_fee+"&orderId="+$scope.out_trade_no;
			 }else{
				          //如果值为它那么说明二维码过去重新调用获取二维码方法进行更替
				 if(response.messAge=="refresh"){
					alert("二维码失效");
					location.href="seckill-index.html"
					 return ;
				 }
				 //如果上面两种都不成立,那么就是后台出错,跳转到错误页面即可.
					location.href="payfail.html";
				 
			 }
			
		 }		
		
		);
	}
	$scope.getMoney=function(){
		    $scope.seckillOrderId=$location.search()['orderId'];
	  return	$location.search()['money'];
	}
	
});	
