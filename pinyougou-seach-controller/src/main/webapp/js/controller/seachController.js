app.controller('seachController', function($scope,$location,seachservice) {
	
	
	//首先定义查询条件进行初始化数据  查询词条                 查询分类                  查询品牌               规格选项 由于规格是多选的所以使用对象的操作形式
	$scope.condition={'keywords':'','cateGory':'','brand':'','spec':{},"price":'',"pageIndex":1,"pageSize":20,"sort":'',"sortField":''}
	
	$scope.serachQuerys=function(key,value){
		if(key=='cateGory'||key=='brand'){
			               //为什么要执行以上的判断在此执行,就就看上面的定义默认初始化格式类型
			//categGory分类  brand 品牌  在对象中的表示以及spec{}  
			//一个                                                                                     多个的原因
			$scope.condition[key]=value;
		}else{
			$scope.condition.spec[key]=value;
		}
		$scope.seachItem();
		
	}
	//撤销选择操作
	$scope.removeItem=function(key){
		if(key=='price'){
			$scope.condition.price='';
		}
		if (key == 'cateGory' || key == 'brand') {
		
			$scope.condition[key] = '';//恢复原样即可
		} else {
			//它是有key的所以使用新的方法
			delete $scope.condition.spec[key];
		}
		$scope.seachItem();
	}
	
	$scope.seachItem=function(){
		if($scope.condition.keywords==''){
			alert("请输入内容搜索哦");
			return
		}
		$scope.condition.keywords=Trim($scope.condition.keywords, "g");
		seachservice.seachItem($scope.condition).success(
		function(response){
			$scope.itemsMap = response;
			$scope.pagess();
			
		}		
		);
	}
	$scope.searchPrice=function(price){
		$scope.condition.price=price;
		
		$scope.seachItem();
	}
	$scope.pagess=function(){
    //判断当前页数
		var fastpage=1;//这是当前遍历的显示页数
		var lastpage=$scope.itemsMap.pageNumbers;//这是截至遍历页数
		$scope.flaglt=true;//如果改变量为true那么显示前面小点
		$scope.flaggt=true;//如果改变量为true显示后面小点
		if($scope.itemsMap.pageNumbers>5){//如果这个总页数小于5的话那么就让它不走逻辑
			if($scope.condition.pageIndex<=3){//如果大于5 判断当前页数是否小于等于3
				lastpage=5;
				$scope.flaglt=false;
				//如果小于等于3 那么就让截至页数为5    1 2 3 4 5 这样的页数进行显示
			}else if($scope.condition.pageIndex>=lastpage-2){ //如果当前页数大于等于总页数-2
				fastpage=lastpage-4;
				$scope.flaggt=false;
				
			}else{
				fastpage=$scope.condition.pageIndex-2;//上面逻辑都不通的话 那就走这个   1 = 总页数+2
				lastpage=$scope.condition.pageIndex+2; 
			}
		}else{
			$scope.flaglt=false;
			$scope.flaggt=false;
		}
		
		
		$scope.totalS=[]; //循环遍历页数  让它保持5位变量页码数
		for(var i=fastpage;i<=lastpage;i++){
	   $scope.totalS.push(i);
			
		}
	}
	$scope.pages=function(pageIndex){
	  
		if(pageIndex<=0||pageIndex>$scope.itemsMap.pageNumbers){
			return;
		}
		$scope.condition.pageIndex=parseInt(pageIndex);
		$scope.seachItem();
	}
	$scope.judgeIndex=function(){
	      if($scope.condition.pageIndex==1){
	    	
	    	return true;  
	      }else{
	    	  return false;
	      }
	}
	$scope.judgetotal=function(){
		if($scope.condition.pageIndex==$scope.itemsMap.pageNulbers){
			alert("已经到达尾页了哦...");
			return true;
		}else{
			return false;
		}
	}
	//去除空格操作:
	function Trim(str,is_global)
	  {
	   var result;
	   result = str.replace(/(^\s+)|(\s+$)/g,"");
	   if(is_global.toLowerCase()=="g")
	   {
	    result = result.replace(/\s/g,"");
	    }
	   return result;
	}
	//前台对后台指定需要排序的参数
	$scope.sortPrice=function(sort,field){
		$scope.condition.sort=sort;
		$scope.condition.sortField=field;
		$scope.seachItem();
		
	}
	$scope.flagBrand=function(){
	 var brand= $scope.itemsMap.brandList;
	 var brandentity= $scope.condition.keywords;
	 
	    for(var i=0;i<brand.length;i++){
	    	//循环判断当前的brand品牌中搜索框的变量是否有此数据的内容
	    	if(brandentity.indexOf(brand[i].text)!=-1){
	    	
	    		return false;
	    	}
	    }
	    return true;
	}
	//注入$location服务 获取参数并查找数据
	$scope.indexParameter=function(){
		$scope.condition.keywords=  $location.search()['keywords'];
		$scope.seachItem();
	}
	
})