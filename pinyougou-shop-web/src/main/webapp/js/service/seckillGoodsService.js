//服务层
app.service('seckillGoodsService',function($http){

	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../goods/seckillSearch.do?page='+page+"&rows="+rows, searchEntity);
	}    	
});
