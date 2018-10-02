app.controller("seckillController",function($scope,seckillService,$location,$interval){
	$scope.findSeckillList=function(){
		seckillService.findSeckillList().success(
				function(response){
					$scope.list=response;
				}
				)
	}
	$scope.findOneGoods=function(){
		
		var goodsId = $location.search()['goodsId']
		seckillService.findOneGoods(goodsId).success(
		       function(response){
		    	  $scope.entity=response;
		    	  var stop=  Math.floor(new Date($scope.entity.endTime).getTime());
		    		
		    	  
		    	  //定时调用此逻辑,完成定时操作:
		    	  var stopTime=$interval(function(){
		    			
		    		$scope.showTiem	= $scope.methHold(stop-1);
		    			if(stop<=0){
		    				$interval.cancel(stopTime);
		    				alert("改商品秒杀结束!");
		    				location.href="http://localhost:9108/"
		    			}
		    		},1000);//指定时间来进行执行1秒
		    	  
		       }		
		);
	}
	$scope.methHold=function(tiem){
	
		var s1 = new Date().getTime(),s2 = tiem;
		var total = Math.floor((s2 - s1)/1000);
		 
		var day =  Math.floor(total / (24*60*60));//计算整数天数
		var afterDay = total - day*24*60*60;//取得算出天数后剩余的秒数
		var hour =  Math.floor(afterDay/(60*60));//计算整数小时数
		var afterHour = total - day*24*60*60 - hour*60*60;//取得算出小时数后剩余的秒数
		var min =  Math.floor(afterHour/60);//计算整数分
		var afterMin = total - day*24*60*60 - hour*60*60 - min*60;//取得算出分后剩余的秒数
		
	
		return day+"天:"+hour+":"+min+":"+afterMin;
	}

	 //保存秒杀订单操作
	$scope.saveSeckillOrder=function(){
		seckillService.saveSeckillOrder($scope.entity.id).success(
		 function(response){
			 if(response.flag){
				 location.href="http://localhost:9108/pay.html"
			 }else{
				 alert(response.messAge);
			 }
		 }		
		);
	}
	
})