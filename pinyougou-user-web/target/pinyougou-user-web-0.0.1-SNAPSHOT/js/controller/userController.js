 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService,$http){	
	

	
   
	
	//保存 
	$scope.add=function(){				
		
		if($scope.status!="1"){
			alert("请勾选同意手册哦!");
			return ;
		}
		if($scope.phone==null){
			alert("请输入验证码!");
			return ;
		}
		if($scope.entity.username==''&&$scope.entity.password==''){
			alert("请填写完整的数据信息!");
			return;
		}
		if($scope.entity.password!=$scope.password){
			alert("两次密码不一致哦!");
			$scope.entity.password="";
			$scope.password="";
			return ;
		}		
		
		userService.add($scope.entity,$scope.phone).success(
			function(response){
				if(response.flag){
					alert(response.messAge);
					$scope.entity={};
					$scope.password="";
		        	
				}else{
					alert(response.messAge);
				}
			}		
		);				
	}
	
	$scope.sendPhone=function(phone){
		userService.sendPhone(phone).success(
		function(response){
			if(response.flag){
				alert(response.messAge);
				
			}else{
				alert(response.messAge);
				location.reload();
				
			}
		}		
		)
	}
	$scope.gain=function(){
		userService.gain().success(
		function(response){
			$scope.userName=response.userName;
		}		
		);
	}
	$scope.order=[];
	
	$scope.initOrder=function(){
		userService.initOrder().success(
		  function(response){
			  $scope.orderList=response;
			
		  }		
		)
	}
	$scope.skipPay=function(orderId){
		userService.skipPay(orderId).success(
		  function(response){
			  if(response.flag){
				location.href="pay.html"
			  }else{
				  alert(response.messAge);
			  }
		  }		
		);
	}
	$scope.initOrderid=function(){
		
		$http.get('../user/initId.do').success(
		  function(response){
			  for(var i=0;i<response.length;i++){
				 alert(response[i].orderId)
			  }
		  }		
		);
	}
	 

    
});	
