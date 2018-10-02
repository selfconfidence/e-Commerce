 //控制层 
app.controller('specificationController' ,function($scope,$controller ,specificationService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		specificationService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		specificationService.findPage(page,rows).success(
				
			function(response){
				
				$scope.list=response.pageRows;	
				$scope.paginationConf.totalItems=response.pagetotal;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		specificationService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.tbSpecification.id!=null && $scope.entity.tbSpecification.id !=''){//如果有ID
			serviceObject=specificationService.update( $scope.entity ); //修改  
		}else{
			serviceObject=specificationService.add( $scope.entity  );//增加 
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
		specificationService.dele( $scope.selectIds ).success(
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
	$scope.search=function(page,rows){			
		specificationService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.pageRows;	
				$scope.paginationConf.totalItems=response.pagetotal;//更新总记录数
			}			
		);
	}
	
	$scope.entity={};//定义为数组否则在页面就是未定义的错误
	$scope.entity.tbSpecification=null;
	$scope.entity.tbSpecificationOptions=[];  //和上面一样的思路 如果未定义的.tablesrown也是报错
	//进行新增加功能操作      
	$scope.addTables=function(){
		//每次单击触发此事件,触发之后push一个空那么size就会多一个在前台就会对遍历一条数据
		$scope.entity.tbSpecificationOptions.push({});
	}
	
    $scope.deletetables=function(){
    	$scope.entity.tbSpecification=null;
    	$scope.entity.tbSpecificationOptions=[];
    }
	//按照索引删除操作
	$scope.deletetable=function(index){
		$scope.entity.tbSpecificationOptions.splice(index,1);
	}
	//复选判断删除操作:
	$scope.selectIds=[];
	$scope.deletes=function($event,ids){
		if($event.target.checked){
			$scope.selectIds.push(ids);
		}else{
			var index = $scope.selectIds.indexOf(ids);
			$scope.selectIds.splice(index,1);
		}
	}
    
});	
