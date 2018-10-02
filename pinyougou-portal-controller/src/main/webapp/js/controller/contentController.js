 //控制层 
app.controller('contentController' ,function($scope   ,contentService){	
	

	$scope.statusShow=["不启用","启用"];
	//在网页首页展示数据的话就会有非常多的图片数据如果每次查询都是用List就会非常乱
	//采用一种措施能够统一点进行操作
	$scope.itemsList=[];
	$scope.findcateGoryId=function(cateGoryId){
		contentService.findCateGoryId(cateGoryId).success(
		function(response){
			$scope.itemsList[cateGoryId]=response;
		}		
		);
	}
	//进行跳转搜索页面操作
	$scope.skip=function(){
		if($scope.keywords==null||$scope.keywords==''){
			alert("搜索不能为空哦.")
			return
		}
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
    
});	
