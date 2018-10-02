 //控制层 
app.controller('addressController' ,function($scope,$controller ,addressService){	
	$controller('cartController', {
		$scope : $scope
	});// 继承
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=addressService.update( $scope.entity ); //修改  
		}else{
			serviceObject=addressService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.flag){
					//重新查询 
		        	$scope.findAddressList()//重新加载
				}else{
					alert(response.messAge);
				}
			}		
		);				
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		addressService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
 
	$scope.searchEntity={};//定义搜索对象 
	
	
	
	$scope.findAddressList=function(){
		 addressService.findAddressList().success(
		  function(response){
			  $scope.addressList=response;
			  for(var i=0;i<$scope.addressList.length;i++){
				  if($scope.addressList[i].isDefault==1){
					 $scope.flagAddress($scope.addressList[i]);
					 break;
				  }
				  
			  }
		  }		
		);
	}
	
	
	$scope.flagAddress=function(address){
		$scope.address=address;
	}
	$scope.isAddress=function(address){  
		if(address==$scope.address){
			return  true;
		}
	
	return false;
	}
	
	//批量删除 
	$scope.dele=function(addressId){			
		//获取选中的复选框			
		addressService.dele( addressId ).success(
			function(response){
				if(response.flag){
					$scope.findAddressList();
				}						
			}		
		);				
	}
	
	//订单相关         --------------------------------------
	$scope.order={paymentType:'1',invoiceType:'1'};//订单对象,先给一个默认的支付方式 微信
	
	$scope.checkPaymentType=function(type){
		$scope.order.paymentType=type;
	}
	
	$scope.checksetInvoiceType=function(invoiceType){
		$scope.order.invoiceType=invoiceType;
	}
	$scope.freight=10;
	//发票类型(普通发票，电子发票，增值税发票
	//订单保存通用controller单独的使用
	
	$scope.addOrder=function(){
		$scope.order.postFee=$scope.freight;
		$scope.order.receiverAreaName=$scope.address.address;
		$scope.order.receiverMobile=$scope.address.mobile;
		$scope.order.receiver=$scope.address.contact;
		addressService.addOrder($scope.order).success(
		   function(response){
			   if(response.flag){
				  if($scope.order.paymentType=="1"){
					  location.href="pay.html"
				  }else{
					  location.href="paysuccess.html";
				  }
			   }
		   }		
		);
		
	}
	
    
});	
