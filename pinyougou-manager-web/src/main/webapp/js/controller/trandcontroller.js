		
		app.controller('brandController',function($scope,$http,branService,$controller){
			
			$controller("BrandController",{$scope:$scope});
			
			
			
			
			
			//分页 
			$scope.findPage=function(page,size){
				branService.findPage(page,size).success(
					function(response){
						$scope.list=response.pageRows;//显示当前页数据 	
						$scope.paginationConf.totalItems=response.pagetotal;//更新总记录数 
					}		
				);				
			}
			//定义此方法,等待激活,使用的是post请求 传参也是entity定义的对象.
            $scope.save = function(){
				var object=null;
				if($scope.entity.id != null){
					//有id那肯定是更新操作
				object	= branService.updatetbBrand($scope.entity);
				}else{
					//没有id肯定是新增的操作
				object =	branService.tbBrandAdd($scope.entity);
				
				}
				//由于拼接变量值来分区所对应请求的到底是增加还是更新操作.   参数都是一样的,无论是新增还是更改都是用到了
				                                                   //entity对象区别在于更新肯定有id新增肯定没有id
            	object.success(
            			
            		function(messAge){
            			if(messAge.flag){
            				$scope.reloadList();
            			}else{
            			alert(messAge.messAge);
            			}
            		}
            	);
            }
			//根据id查询数据进行数据的回显操作.
			$scope.findBid = function(id){
				//将传递的id异步发送到页面
				branService.findBid(id).success(
				function(response){
					//响应回来的数据就是指定查询的数据,将它赋值到entity这个对象中,在编辑框中已经定义个entity.xxx之类的属性
			     $scope.entity =response;
				}		
				)
			}
			
			$scope.selectAll=function($event){
			var flag=$event.target.checked;
			$scope.deleteid=[];
			if(flag){
			for(var i=0;i<$scope.list.length;i++){
				$scope.deleteid.push($scope.list[i].id);
			}
			}
			}
			//delete操作:
				$scope.deleteid=[];       
				$scope.updateSelection=function($event,ids){
					// 得到复选框的根 判断复选框是否被选中 如果选中添加数组,如果没有选中就删除操作,这样就可实现规范操作
				if($event.target.checked){
					     //如果选中那么就执行添加的操作
					   $scope.deleteid.push(ids);
				}else{
					//如果没有选中那么就执行获得数据选择并进行删除如果没有也不报错
					  //获取在此数组中重复数据的下标.
				var index= $scope.deleteid.indexOf(ids);
					//index --指定数据的下标并删除,    1 --删除一个
					$scope.deleteid.splice(index,1);
				}
				
				
			}
		
			
			
			$scope.searchEntity={};//定义空否则如果条件为空的话那么就会报错:未定义的 Undefined
			                                       
			$scope.search=function(index,size){ //接收参数                                           虽然是post的请求但是我们也可以这样玩参数的拼接
				branService.search(index,size,$scope.searchEntity).success(
					function(response){
						$scope.list=response.pageRows;//显示当前页数据 	
						$scope.paginationConf.totalItems=response.pagetotal;//更新总记录数 
					}	
				
				);
			}
		
			$scope.deletes=function(){
				if(confirm("确定删除?")){            //获取定义的数组当参数传递后台
					branService.deletes($scope.deleteid).success(
						function(response){
							if(response.flag){
	            				$scope.reloadList();
	            			}else{
	            			alert(response.messAge);
	            			}
						}
				);
				}
			}
			

		});
		