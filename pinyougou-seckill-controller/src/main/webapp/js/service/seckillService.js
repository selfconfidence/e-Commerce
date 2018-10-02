app.service("seckillService",function($http){
	this.findSeckillList=function(){
		return $http.get('../seckillGoods/findList.do');
	}
	this.findOneGoods=function(goodsId){
		return $http.get('../seckillGoods/findOneGoods.do?goodsId='+goodsId);
	}
	this.saveSeckillOrder=function(seckillGoodsId){
		return $http.get('../seckillGoods/saveSeckillOrder.do?seckillGoodsId='+seckillGoodsId)
	}
})