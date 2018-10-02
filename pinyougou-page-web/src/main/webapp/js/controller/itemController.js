
           app.controller("itemController",function($scope,$http){
        	   
          $scope.addNum=function(value){
        $scope.num+=value;
          if( $scope.num <=0){
             $scope.num=1;
          }
          }
          
          
          
          
          
          
          $scope.specObj={};
          $scope.specAdd=function(key,value){
          $scope.specObj[key]=value;
           $scope.searchSku();
          }



          $scope.flagChecked=function(key,value){
                  if ($scope.specObj[key]==value) {
                      return true;
                  }else{
                  	return false;
                  }
          }
          //初始化页面操作
        $scope.initSku=function(){
        	  $scope.sku=itemList[0];
        	  //首先赋值默认选中
        	      $scope.specObj = JSON.parse(JSON.stringify($scope.sku.spec));
        	    
        }
     
        //这些定义的方法主要用来和页面中的标题和价格进行同步的操作   
              
        
        
        $scope.staticSku=function(map1,map2){
        	//定义此方法 来判断当前数据是否相等  
        	//为什么使用两边的遍历操作: 原因由于有可能多参数但是数据却第3个吻合导致数据不稳定所以需要判定2次
             for(var k in map1){
             	
                if (map1[k]!=map2[k]) {
                     return false;
                }
             }
             for(var k in map2){
                if (map2[k]!=map1[k]) {
                	return false;
                }
             }
             return true;
        }

        $scope.searchSku=function(){
        	//遍历SKU的列表如果列表中的数据和当前点击的数据符合那么就更换页面中的title以及price
        	for(var i=0;i<itemList.length;i++){
                 if ($scope.staticSku( $scope.specObj,itemList[i].spec)) {
                       $scope.sku=itemList[i];
                     

                       return;
                 }
        	}
        	//如果以上条件不成立那么说明用户点击的规格没有来源所以初始化
      $scope.sku={
         "title":"暂无",
	      "price":"暂无"

      }

        }
        //加入购物车  //js直接的跨域操作
        $scope.addCat=function(){
        	              ///这段请求是直接跨域的请求所以需要进行一些配置才可以                           //这段操作关于是否启动cookie发送的服务器交互在服务端同意之后,请求端必须也要同意
        	                //js跨域是根据双方必须都同意才可以.
        	$http.get('http://localhost:9107/cat/catinsert.do?itemId='+$scope.sku.id+"&num="+$scope.num,{'withCredentials':true}).success(
        	                  function(response){
        	                	  if(response.flag){
        	                		  location.href="http://localhost:9107/cart.html";
        	                	  }else{
        	                		  alert(response.messAge);
        	                	  }
        	                  }		
        	)
        	
        }
        	   
           })
          
          
         
        	   
           

