 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,branService,specificationService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,pageRows){			
		typeTemplateService.findPage(page,pageRows).success(
			function(response){
				$scope.list=response.pagepageRows;	
				$scope.paginationConf.pagetotalItems=response.pagepagetotal;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//由于后台是json格式传到这里就是一个字符串我们需要转为对象在能显示数据:
				   $scope.entity.brandIds = JSON.parse (response.brandIds);
				   $scope.entity.specIds = JSON.parse (response.specIds);
				   $scope.entity.customAttributeItems = JSON.parse (response.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
		
			$scope.entity.customAttributeItems.push($scope.pojo)
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.flag){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.messAge);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.flag){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,pageRows){			
		typeTemplateService.search(page,pageRows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.pageRows;	
				$scope.paginationConf.pagetotalItems=response.pagetotal;//更新总记录数
			}			
		);
	}
	$scope.brandList={data:[{}]}
	$scope.findOptionList=function(){
		branService.findOptionLists().success(
		function(response){
			//要符合对应的格式 data为主
			$scope.brandList ={data:response};
		}		
		);
		
	}
	$scope.specificationList={data:[{}]};
	$scope.findspecificationList=function(){
		specificationService.findspecificationLists().success(
		function(response){
			$scope.specificationList = {data:response}
		}		
		)
	}
	
	$scope.addArrays=function(){
		$scope.entity.customAttributeItems.push({});
	}
	$scope.deleteArrays=function(index){
		//var number = $scope.Arrays.indexOf(index);
		$scope.entity.customAttributeItems.splice(index,1);
	}
	$scope.selectIds=[];
	$scope.deleteTypeId=function($event,ids){
		if($event.target.checked){
			$scope.selectIds.push(ids);
		}else{
			var index = $scope.selectIds.indexOf(ids);
			$scope.selectIds.splice(index,1);
		}
		
	}
    
});	
