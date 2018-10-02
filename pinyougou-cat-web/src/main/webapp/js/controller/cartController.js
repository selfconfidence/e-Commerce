//控制层 
app.controller('cartController', function($scope, cartService) {

	$scope.initCart = function() {
		cartService.initCart().success(function(response) {
			$scope.cartTables = response;
			//调用统计总金额操作
			$scope.countNumber();
		}

		)

	}
	
	$scope.addNumItemCart=function(itemId,num){
		cartService.addNumItemCart(itemId,num).success(
		  function(response){
			  if(response.flag){
				  $scope.initCart();
			  }else{
				  alert(response.messAge);
			  }
		  }		
		);
	}
	
	
	//数量以及总金额累加的操作  统计逻辑
	$scope.countNumber=function(){
		$scope.money=0;
		$scope.count=0;
		for(var i=0;i<$scope.cartTables.length;i++){
			for(var k=0;k<$scope.cartTables[i].orderItem.length;k++){
			$scope.count+=$scope.cartTables[i].orderItem[k].num;
			$scope.money+=$scope.cartTables[i].orderItem[k].totalFee;
			}
		}
	}
	$scope.deleteItem=function(itemId){
		cartService.deleteItem(itemId).success(
		function(response){
			if(response.flag){
				$scope.initCart();
			}else{
				alert(response.messAge);
			}
		}		
		);
	}
		
	

});
