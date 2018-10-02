 //使用service抽取共性:
		app.service('branService',function($http){
			    //名称                                     以及所需要的参数因为之抽取http这一部分内容
			this.findBid=function(id){
				//如果参数是$scope.xxx类型的那么在服务的声明中也应该进行传递
				//  参数和我们后台大致都一样你控制器调用service不传参数怎么调用
				return $http.get("../brand/findBid.do?id="+id);
			    }
			 this.findPage =function(page,size){
				 return $http.get('../brand/pageLook.do?pageIndex='+page +'&pageSize='+size);
			 }
			 this.deletes=function(deleteid){
				 return $http.get("../brand/delete.do?ids="+deleteid);
			 }
			 this.search=function(index,size,searchEntity){ //如果参数是
				 return $http.post("../brand/search.do?pageIndex="+index+"&pageSize="+size,searchEntity);
			 }
			 this.updatetbBrand=function(entity){
				 return $http.post("../brand/updatetbBrand.do",entity)
			 }
			 this.tbBrandAdd=function(entity){
				 return $http.post("../brand/tbBrandAdd.do",entity)
			 }
			 this.findOptionLists=function(){
				return $http.get("../brand/findOptionList.do");
			 }
			
		});