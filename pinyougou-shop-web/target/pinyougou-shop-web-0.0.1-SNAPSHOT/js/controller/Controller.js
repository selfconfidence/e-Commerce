
           app.controller("baseController",function($scope){
        	   
        	 //分页控件配置currentPage:当前页   totalItems :总记录数  itemsPerPage:每页记录数  perPageOptions :分页选项  onChange:当页码变更后自动触发的方法 
   			$scope.paginationConf = {
   				currentPage: 1,
   				totalItems: 10,
   				itemsPerPage: 10,
   				perPageOptions: [10, 20, 30, 40, 50],
   				onChange: function(){
   					$scope.reloadList();
   				}
   			};
   			/*paginationConf 变量各属性的意义：
   			currentPage：当前页码
   			totalItems:总记录数
   			itemsPerPage: 查询几页数据  size
   			perPageOptions：页码选项
   			onChange：更改页面时触发事件*/
   			//调用异步操作列表
   			$scope.reloadList=function(){ //调用此方法并再次执行条件查询方法,传入当前页以及查询提交的逻辑数
   				$scope.findPage( $scope.paginationConf.currentPage ,  $scope.paginationConf.itemsPerPage);
   			}
   			
   			//更新操作的具体思路   将最开始的分页原封不动只不过需要加入条件进行搜索.
			$scope.searchs=function(){
				$scope.reloadList();
			}
			$scope.jsonTOobject=function(jsonString,key){
				//需要将页面显示的处理做出处理,此方法用于只要指定的值,
				//转换为对象
			var json =JSON.parse(jsonString);
			var value="";
			for(var i=0;i<json.length;i++){
				if(json[i]!=null && json[i]!='' && json[i] !=","){
					if(i>0){
						value+=",";
						}
						
					value += json[i][key];
					
				}
					
					}
					return value;
						
					
				}
			
				//判断指定元素是否存在于数组中  判断的是attributeName
			$scope.flagArrays=function(list,key,keyvalue){
				for(var i=0;i<list.length;i++){
					if(list[i][key]==keyvalue){
						return list[i];
					}
				}
				return null;
				
			}
        	   
           })
        	   
           

