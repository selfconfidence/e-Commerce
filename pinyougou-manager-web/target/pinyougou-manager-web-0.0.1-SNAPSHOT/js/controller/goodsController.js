app
		.controller(
				'goodsController',
				function($scope, $controller, $location,goodsService, uploadService,
						itemCatService, typeTemplateService) {

					$controller('baseController', {
						$scope : $scope
					});// 继承

					// 读取列表数据绑定到表单中
					$scope.findAll = function() {
						goodsService.findAll().success(function(response) {
							$scope.list = response;
						});
					}

					// 分页
					$scope.findPage = function(page, rows) {
						goodsService
								.findPage(page, rows)
								.success(
										function(response) {
											$scope.list = response.pageRows;
											$scope.paginationConf.totalItems = response.pagetotal;// 更新总记录数
										});
					}

					// 查询实体
					$scope.findOne = function(id) {
						  //接收外界的参数   
						if($location.search()['id']!=null){
							var id= $location.search()['id'];//获取参数值
							
						}
						
					   
						goodsService.findOne(id).success(function(response) {
							$scope.entity = response;
							
							 //为什么转化为对象:因为从后台过来的数据全部都是字符串所以使用到了转换对象操作
							//将大文本数据转回:     转换之后就能正常显示
							editor.html($scope.entity.desc.introduction);
							//将图片转回
							$scope.entity.desc.itemImages=JSON.parse($scope.entity.desc.itemImages);
							//将扩展属性转换   这里有个小问题
							$scope.entity.desc.customAttributeItems=JSON.parse($scope.entity.desc.customAttributeItems);
							//用于遍历判断是否勾选状态: 转化规格对象勾选操作做准备
							$scope.entity.desc.specificationItems=JSON.parse(	$scope.entity.desc.specificationItems);
							//由于Item是一个集合所以需要进行迭代操作  //将SPU的spec转为对象
							for(var i=0;i<$scope.entity.itemList.length;i++){
								$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
							}
						});
					}

					// 保存
					$scope.save = function() {
						var serviceObject;// 服务层对象
						$scope.entity.desc.introduction = editor.html();
						if ($scope.entity.goods.id != null) {// 如果有ID
							
							serviceObject = goodsService.update($scope.entity); // 修改
						} else {
							serviceObject = goodsService.add($scope.entity);// 增加
						}
						serviceObject.success(function(response) {
							if (response.flag) {
								// 重新查询
								alert("保存成功")
								$scope.entity = {};
								// 清空编辑器内容
								editor.html('');
								location.reload();
							} else {
								alert(response.messAge);
							}
						});
					}
					// 添加SPU操作
				/**	$scope.add = function() {
						// 添加编辑器中的数据
						$scope.entity.desc.introduction = editor.html();
						goodsService.add($scope.entity).success(
								function(response) {
									if (response.flag) {
										// 重新查询
										alert("添加成功")
										$scope.entity = {};
										// 清空编辑器内容
										editor.html('');
										location.reload();
									} else {
										alert(response.messAge);
									}
								});
					}*/
					$scope.selectIds=[];
					$scope.deletes=function($event,id){
						if($event.target.checked){
							$scope.selectIds.push(id);
						}else{
							var index=$scope.selectIds.indexOf(id);
							$scope.selectIds.splice(index,1);
						}
					}

					// 批量删除
					$scope.dele = function() {
						// 获取选中的复选框
						goodsService.dele($scope.selectIds).success(
								function(response) {
									if (response.flag) {
										$scope.reloadList();// 刷新列表
										$scope.selectIds = [];
									}else{
										alert(response.messAge);
									}
								});
					}

					$scope.searchEntity = {};// 定义搜索对象

					// 搜索
					$scope.search = function(page, rows) {
						goodsService
								.search(page, rows, $scope.searchEntity)
								.success(
										function(response) {
											$scope.list = response.pageRows;
											$scope.paginationConf.totalItems = response.pagetotal;// 更新总记录数
										});
					}
					// 文件下载controller的编写:
					$scope.image_entity = {}
					$scope.fileUpload = function() {
						uploadService.upload().success(function(response) {
							if (response.flag) {
								// 将路径返回到变量中:
								$scope.image_entity.url = response.messAge;

							} else {
								alert(resposne.messAge);
							}
						});
					}
					// 定义真正存储到数据库的变量数据
					$scope.entity = {
						desc : {
							itemImages : [ {} ],
							specificationItems : [ {} ]
						}
					}
					$scope.fileSave = function() {

						// 每次点击就会触发方法 将数据添加到数组中
						$scope.entity.desc.itemImages.push($scope.image_entity);

					}

					$scope.deleteImages = function(index) {
						$scope.entity.desc.itemImages.splice(index, 1);
						
					}
					$scope.readLoad = function() {
						$scope.entity.desc.itemImages.splice(0, 1);
						$scope.entity.desc.specificationItems.splice(0,1)
					}
					// 省市联动效果: 查找的是商品分类模块的表将这个业务的service引入即可传入parentId去查找
					$scope.findCategory1Id = function() {
						// 首先加载顶级层级目录: 默认初始化
						itemCatService.findParentId(0).success(
								function(response) {
									$scope.items1List = response;
								});
					}
					// 使用变量如果改变就会触发angularjs原生提供的方法
					// //参数寓意newValue代表entity.desc.Category1Id之前的数据
					// oldValue 代表entity.desc.Category1Id之后的数据也就是被执行过的数据不是立即数据
					$scope.$watch("entity.goods.category1Id", function(
							newValue, oldValue) {
						itemCatService.findParentId(newValue).success(

						function(response) {

							$scope.items2List = response;
						});

					})
					$scope.$watch("entity.goods.category2Id", function(
							newValue, oldValue) {
						itemCatService.findParentId(newValue).success(
								function(response) {
									$scope.items3List = response;
								});

					})
					$scope.$watch("entity.goods.category3Id", function(
							newValue, oldValue) {
						itemCatService.findOne(newValue).success(
								function(response) {
									$scope.value = response.typeId;
								});
					})
					$scope
							.$watch(
									"value",
									function(newValue, oldValue) {
										typeTemplateService
												.findOne(newValue)
												.success(
														function(response) {
															$scope.typeTemplate = response;// 获取类型模板
															$scope.typeTemplate.brandIds = JSON
																	.parse($scope.typeTemplate.brandIds);// 品牌列表
															//罪魁祸首 进行判断
															if($location.search()['id']==null){
															$scope.entity.desc.customAttributeItems = JSON
																	.parse($scope.typeTemplate.customAttributeItems);
															}

														});
										typeTemplateService
												.serachSpsc(newValue)
												.success(function(response) {
													$scope.spscList = response;
												});
									})

					$scope.specificationItems = function($event, attributeName,
							attributeValue) {
						var obj = $scope.flagArrays(
								$scope.entity.desc.specificationItems,
								"attributeName", attributeName);
						if (obj != null) {
							if ($event.target.checked) {
								obj.attributeValue.push(attributeValue);

							} else {

								var index = obj.attributeValue
										.indexOf(attributeValue);
								obj.attributeValue.splice(index, 1);
								if (obj.attributeValue.length == 0) {
									$scope.entity.desc.specificationItems
											.splice(
													$scope.entity.desc.specificationItems
															.indexOf(obj), 1);
								}

							}
						} else {

							$scope.entity.desc.specificationItems.push({
								"attributeName" : attributeName,
								"attributeValue" : [ attributeValue ]
							});
						}

					}
					$scope.createItemList=function(){
						$scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}]
						var items=$scope.entity.desc.specificationItems;
						for(var i = 0 ;i<items.length;i++){
							$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
						}
					}
					//在内置controller定义方法时也可以省略$scope
					addColumn=function(list,columnName,conlumnValues){
						var newList=[]; //新的集合
						for(var i=0;i<list.length;i++){
							var oldRow=list[i]
							for(var j=0;j<conlumnValues.length;j++){
								//深克隆
								var newRow=JSON.parse(JSON.stringify(oldRow));
								newRow.spec[columnName]=conlumnValues[j];
								newList.push(newRow);
							}
						}
						return newList;
					}
					$scope.auditStatus=['未审核','已审核','已驳回','关闭'];
					
					$scope.findItemList=[];
					$scope.showItems=function(){
						itemCatService.findAll().success(
						  function(response){
							  //循环商品类目所有数据
							  for(var i=0;i<response.length;i++){
								  //                  将对应的id值和name属性首先对应好 在页面直接传入ID就获取到了name
								  $scope.findItemList[response[i].id]=response[i].name;
							  }
						  }		
						);
					}
					$scope.specificationChecked=function(attributeName,attributeValue){
						  //判断attributeName是否存在如果它都不存在就直接返回false就行 如果存在对attrubute进行判断即可
						//将刚刚转化的specificationItems是个集合转化的对象进行判断操作
						 var object =  $scope.flagArrays($scope.entity.desc.specificationItems,"attributeName",attributeName);
						  if(object!=null){
							  //如果attributeName不等于null 就要判断attributeValue有没有数据  它是一个数据
							 //看不懂这一步的话就去看看传入集合条件的来源  
							  var flag=object.attributeValue.indexOf(attributeValue);
							  if(flag>=0){
								  return true;
							  }else{
								  return false;
							  }
							  
						  }else{
							  return false;
						  }
						
						
					}
					$scope.audit=function(status){
						goodsService.checkAudit($scope.selectIds,status).success(
							function(response){
								if(response.flag){
									alert("状态更改成功!")
									$scope.reloadList();
									$scope.selectIds=[];
								}else
									{
									alert(response.messAge);
									
								}
							}	
						);
					}

				});
