//服务层
app.service('seckillGoodsService',function($http){

	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../goods/seckillSearch.do?page='+page+"&rows="+rows, searchEntity);
	} 
	//更改秒杀商品的审核状态
	this.checkStatus=function(ids,status){
		return $http.get('../goods/checkStatus.do?ids='+ids+"&status="+status);
	}
});
