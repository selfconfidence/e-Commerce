//控制层 
app.controller('itemCatController', function($scope, $controller,
		itemCatService, typeTemplateService) {

	$controller('baseController', {
		$scope : $scope
	});//继承

	//读取列表数据绑定到表单中  
	//更改了商品分类的controller的查询所有方法用来做模块
	$scope.brandList = null;

	$scope.findType = function() {
		typeTemplateService.findType().success(function(response) {
			$scope.brandList = response;

		});

	}
	$scope.selectAll=function($event){
		var flag = $event.target.checked;
		$scope.selectIds=[];
		if(flag){
		for(var i=0;i<	$scope.list.length;i++){
			$scope.selectIds.push($scope.list[i].id);
		}
		}
	}
	

	$scope.selectIds = [];
	$scope.deleteTypeId = function($event, ids) {
		if ($event.target.checked) {
			$scope.selectIds.push(ids);
		} else {
			var index = $scope.selectIds.indexOf(ids);
			$scope.selectIds.splice(index, 1);
		}

	}
	/*
	 * 
	 * */

	//分页
	$scope.findPage = function(page, rows) {
		itemCatService.findPage(page, rows).success(function(response) {
			$scope.list = response.pageRows;
			$scope.paginationConf.totalItems = response.pagetotal;//更新总记录数
		});
	}

	//查询实体 
	$scope.findOne = function(id) {
		itemCatService.findOne(id).success(function(response) {
			$scope.pojo = response;
		});
	}

	//保存 
	$scope.save = function() {

		//类型模块id

		var serviceObject;//服务层对象  

		if ($scope.pojo.id != null) {//如果有ID
			serviceObject = itemCatService.update($scope.pojo); //修改  

		} else {

			$scope.pojo.parentId = $scope.parentId;

			serviceObject = itemCatService.add($scope.pojo);//增加 

		}
		serviceObject.success(function(response) {
			if (response.flag) {
				//重新查询 
				if ($scope.id == 1) {
					$scope.brandLook({
						id : 0
					});
				}
				if ($scope.id == 2) {
					$scope.brandLook($scope.brand);
				}
				if ($scope.id == 3) {
					$scope.brandLook($scope.brand1);
				}
			} else {
				alert(response.messAge);
			}
		});
	}

	//批量删除 
	$scope.dele = function() {
		//获取选中的复选框
		if($scope.selectIds == "" || $scope.selectIds==null){
			alert("当前没有可删除项!")
		}else{
		itemCatService.dele($scope.selectIds).success(function(response) {
			if (response.flag) {
				if ($scope.id == 1) {
					$scope.brandLook({
						id : 0
					});
				}
				if ($scope.id == 2) {
					$scope.brandLook($scope.brand);
				}
				if ($scope.id == 3) {
					$scope.brandLook($scope.brand1);
				}
				alert(response.messAge);
				$scope.selectIds = [];
			} else {
				alert(response.messAge)
			}
		});
		}
	}

	$scope.searchEntity = {};//定义搜索对象 

	//搜索

	$scope.id = 1;
	$scope.setGrade = function(value) {
		$scope.id = value;
	}
	$scope.brandLook = function(p_entity) {
		if ($scope.id == 1) {
			$scope.brand = null;
			$scope.brand1 = null;
		}
		if ($scope.id == 2) {
			$scope.brand = p_entity;
			$scope.brand1 = null;
		}
		if ($scope.id == 3) {
			$scope.brand1 = p_entity;
		}
		if ($scope.id == 4) {
			alert("客官已经是最后一级目录了哦!")
			$scope.findParentId($scope.brand1.id);
		}else{
			$scope.findParentId(p_entity.id);
		}

	}
	$scope.parentId = 0;
	$scope.findParentId = function(parentId) {
		$scope.parentId = parentId;

		itemCatService.findParentId(parentId).success(function(response) {
			if (response == null || response == '') {
				alert("当前没有下一级了")

			} else {
				$scope.list = response;
			}

		});

	}
	

});
