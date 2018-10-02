 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService,$http,uploadService){	
	$controller('addressController',{$scope:$scope});//继承
	
   
	
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
	//3中disabled的方法  开始----
	$scope.flagPage=function(pageNumIndex){
		if(pageNumIndex==$scope.pageStart){
			return true;
		}else{
			return false;
		}
	}
	$scope.flagPageIndex=function(){
		if($scope.pageStart==1){
			return true;
		}else{
			return false;
		}
	}
	$scope.flagPageStop=function(){
		if($scope.pageStart==$scope.pagetotal){
			return true;
		}else{
			return false;
		}
	}
	
	//结束----
	$scope.order=[];
	$scope.pageStart=1;
	$scope.pagetotals=5;
	$scope.initOrder=function(pageStart){
		if(pageStart<=0){
			alert("没有-1页的数据哦!");
			$scope.pagetotalSize=1;
			return;
			
		}
		if(pageStart> $scope.pagetotal){
			alert("订单页数暂无下一页");
			$scope.pagetotalSize=1;
			return;
		}
		$scope.pageStart=pageStart;
		userService.initOrder($scope.pageStart,$scope.pagetotals).success(
		  function(response){
			  $scope.orderList=response.pageRows;
			  $scope.pagetotal=response.pagetotal;
			  $scope.pageInitList($scope.pagetotal);
		  }		
		)
	}
	
	$scope.pageIndex=[];
	$scope.pageInitList=function(pageTotal){
		for(var i=1;i<=pageTotal;i++){
			$scope.pageIndex.push(i);
		}
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
	/*$scope.initOrderid=function(){
		
		$http.get('../user/initId.do').success(
		  function(response){
			  for(var i=0;i<response.length;i++){
				 alert(response[i].orderId)
			  }
		  }		
		);
	}*/
	$scope.findStatus=function(status){
		userService.findStatus(status).success(
			 function(response){
				$scope.statusList=response; 
			 }	
		);
	}
	
	$scope.uploadHeadPic=function(){
		uploadService.upload().success(
		  function(response){
			  if(response.flag){
				  alert("添加头像信息成功!");
				  $scope.showPic=response.messAge;
			  }else{
				  alert(response.messAge);
			  }
			 
		  }		
		);
	}
	$scope.userEntity={}
	$scope.updateUser=function(){
		$scope.userEntity.birthday=$scope.s1+'年'+$scope.s2+'月'+$scope.s3+'日';
		
		userService.updateUser($scope.userEntity).success(
		  function(response){
			  
				 $scope.userEntity= response;
			  
				  
		  }		
		);
	}
	 

    
});	
