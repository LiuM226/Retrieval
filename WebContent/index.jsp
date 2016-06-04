<%@ page language="java" contentType="text/html; charset=UTF-8" 
import="java.util.*" import="buaa.gui.SourceModel"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
   <title>基于相关反馈的文本排序系统</title>
   <link href="Retrieval/css/bootstrap.min.css" rel="stylesheet">
   <script src="Retrieval/jquery/jquery-2.1.4.min.js"></script>
   <script src="Retrieval/js/bootstrap.min.js"></script>
<style type="text/css">
  body,html{
	margin:0px;
	height:98%;
	}
  table
  { 
  	  width:*; 
 	  table-layout : fixed ; 
  } 
  a.hidshow {  
     display:block;   
     text-decoration: none;  
     overflow:hidden;  
     white-space:nowrap;  
     text-overflow: ellipsis;
  }
  .Tablescroll{
  	height:99%;
  	overflow-y:scroll
  }
  .div-a{float:left;border:1px;display：block}
  .div-b{border:1px;display：block;}   
  .test-table{margin:0px auto;}
  .sur{width:20%;}
</style>
<body>
	<div class = "div-1">
		<h2 align="center">基于相关反馈的文本排序系统</h2>
	</div>
	<div class = "div-2 ">
		<div class="div-a col-md-4">
			<form action="Retrieval/upLoad"  method="post" id="form1" name="form1"  
				enctype="multipart/form-data" target="hidden_frame" >
				<input type="file" id="upfile" name="upfile" >
				<button type="submit" class="btn btn-default" onclick="dealFile()">上传</button>
				<b id="alert"></b>
				<iframe name='hidden_frame' id="hidden_frame" style="display:none"></iframe>
			</form>
		</div>
		<div class="div-b col-md-4 ">
			<form role="form" id="form2" name="form2" class="form-inline" >
				<div class="div-tab-1">
					<table class="test-table">
						<tr align="center" id="first">
							<td>
								<button type="button" class="btn btn-default add" onclick="addtext()">+</button>
								<button type="button" class="btn btn-default dec" onclick="dectext()">-</button>
								<div class="form-group">
      								<input type="text" class="form-control" name="keywords1" id="keywords1" placeholder="请输入查询词" value="">
    							</div>
    						</td>
						</tr>
					</table>
				</div>
				<div class="div-tab-2">
					<table class="test-table" style="display:none" id="second">
						<tr align="center" >
							<td>
								<select class="form-control" id="link2" name="link2" data-size="10" style="width:70px;height:31px;">
									<option value="0">与</option>
									<option value="1">或</option>
									<option value="2">非</option>
								</select> 
    							<div class="form-group ">
      								<input type="text" class="form-control" name="keywords2" id="keywords2" placeholder="请输入查询词" value="">
    							</div>
    						</td>
						</tr>
					</table>
				</div>
				<div class="div-tab-3">
					<table class="test-table"  style="display:none" id="third">
						<tr align="center">
							<td>
								<select class="form-control" id="link3" name="link3" data-size="10" style="width:70px;height:31px;">
									<option value="0">与</option>
									<option value="1">或</option>
									<option value="2">非</option>
								</select>
    							<div class="form-group ">
     	 							<input type="text" class="form-control" name="keywords3" id="keywords3" placeholder="请输入查询词" value="">
   								</div>
    						</td>
						</tr>
					</table>
				</div>
			</form>
		</div >
		<div class="div-c col-md-4 ">
		</div>
	</div>
	<br><br><br>
	<div class = "div-3" >
		<div align = "center">
			<button type="button" class="btn btn-primary sur"  onclick="search()">确定</button>
		</div>
	</div>
	<div class = "div-4" style="height:65%">
		<form action="" id="form3" name="form3" style="height:100%">
			<div class="Tablescroll" style="overflow-y:scroll" >
				<table id="showdata"  class="table table-bordered"  align="center">
		<!--     <caption>边框表格布局</caption>
		-->
   					<thead >
      					<tr >
		 					<th align="center" width="50px">序号</th>
<!-- 		 				<th align="center" width="80px">热点词</th>      -->
		 					<th align="center" width="280px">标题</th> 
							<th align="center">内容</th> 
		 					<th align="center" width="80px">相关度</th> 
		 					<th align="center" width="80px">相关与否</th>
      					</tr>
   					</thead>

  					<tbody>
  				<%	for(int i=0;i<30;i++){	%>
     					<tr>
							<td align="left" ><p></p></td>
<!--  					    <td align="left" ><p></p></td>      			-->
							<td align="left" ><p></p></td>
							<td align="left" ><p></p></td>
							<td align="left" ><p></p></td>
							<td align="left" ><p></p></td>
      					</tr>
    			<%   }       %>
   					</tbody>

				</table>
			</div>
		</form>
	</div>
	<div class="div-4" style="height:5%">
		<b id = "hotwords"></b>
	</div>
	<div  class = "div-5">
		<div align = "center">
			<button type="button" class="btn btn-primary sur"  onclick="reback()">提交</button>
		</div>
	</div>
</body>
</html>
<script type="text/javascript">
	var now = 1;
	$(".add").click(function(){
		if(now==1){
			$(".div-a").attr("class","div-a col-md-3");
			$(".div-c").attr("class","div-c col-md-3");
			
			$(".div-b").attr("class","div-b col-md-6");
			$(".div-tab-1").attr("class","div-tab-1 col-md-6");
			$(".div-tab-2").attr("class","div-tab-2 col-md-6");
			
			now ++;
		}
		else if(now==2){
			$(".div-a").attr("class","div-a col-md-3");
			$(".div-c").attr("class","div-c col-md-1");
			
			$(".div-b").attr("class","div-b col-md-8");
			$(".div-tab-1").attr("class","div-tab-1 col-md-4");
			$(".div-tab-2").attr("class","div-tab-2 col-md-4");
			$(".div-tab-3").attr("class","div-tab-3 col-md-4");
			now ++;
		}
	});
	$(".dec").click(function(){
		if(now==3){
			$(".div-a").attr("class","div-a col-md-3");
			$(".div-c").attr("class","div-c col-md-3");
			
			$(".div-b").attr("class","div-b col-md-6");
			$(".div-tab-1").attr("class","div-tab-1 col-md-6");
			$(".div-tab-2").attr("class","div-tab-2 col-md-6");
			now--
		}
		else if(now==2){
			$(".div-a").attr("class","div-a col-md-3");
			$(".div-c").attr("class","div-c col-md-3");
			
			$(".div-b").attr("class","div-b col-md-6");
			$(".div-tab-1").attr("class","div-tab-1 col-md-12");
			now--
		}
	});
</script>
<script type="text/javascript">
	var index = 1;
	var checked = false;
	var number;
	function search(){
		if(!sure())  return ;
		
		var keywords1 = $("#keywords1").val();  
	    var keywords2 = $("#keywords2").val();  
	    var keywords3 = $("#keywords3").val();  
	    var link2 = $("#link2").val();  
	    var link3 = $("#link3").val(); 
		$.ajax( {  
		      type : "POST",  
		      url : "Retrieval/Search?keywords1="+keywords1+"&keywords2="+keywords2+"&keywords3="+keywords3+
		    		      "&link2="+link2+"&link3="+link3,  
		      data: $('#form2').serialize(),  //整个表单提交
		      success : function(dataSet) {
		    	  var data = eval(dataSet);
		    	  if(data.length==0){
		    		  alert("关键字查询不存在！ 请重新输入...");
		    	  }
		    	  insert(dataSet);
		       }  
		});   	   
	}
	function reback(){
		if(!OK())  return ;
		$.ajax( {  
		      type : "POST",  
		      url : "Retrieval/Reback?number="+number, 
		      data: $('#form3').serialize(),
		      success : function(dataSet) { 
		    	  insert(dataSet);
		      }
		});
	}
	function insert(dataSet){
	  var data = eval(dataSet);
	  checked = false;
  	  number = data.length;
  	  var tab = document.getElementById("showdata");
//先删除表格中原有的所有行,除了表头之外  	  
  	  var rowsNum = tab.rows.length;  //获取当前行数
  	  var now;
  	  while(rowsNum>1){
  		  tab.deleteRow(rowsNum-1);
  		  rowsNum--;
  	  }
  	  
  	  for(var i=0;i<data.length-1;i++){
  		  rowsNum = tab.rows.length;  //获取当前行数
	    	  var myNewRow = tab.insertRow(rowsNum);//插入新行
	    	  
	    	  var newTdObj0=myNewRow.insertCell(0);
	    	  newTdObj0.innerHTML="<a>"+data[i].index+"</a>"+
	    	  "<input type='hidden' id='number"+i+"'  name='number"+i+"' value='"+data[i].number+"'/>";
	    	  
//	    	  var newTdObj1=myNewRow.insertCell(1);
//	    	  newTdObj1.innerHTML=
	    	  
	    	
	    	  var newTdObj1=myNewRow.insertCell(1);
	    	  newTdObj1.innerHTML="<div>"+
	    	   "<a class='hidshow' data-toggle='collapse' data-parent='#accordion' href='#title"+i+"'>"+data[i].title+"</a></div>"+
	    	   "<div id='title"+i+"' class='panel-collapse collapse'>"+
	    	   "<div class='panel-body'>"+data[i].title+"</div></div>";
	    	   

	    	  var newTdObj2=myNewRow.insertCell(2);
	    	  newTdObj2.innerHTML="<div>"+
	    	   "<a class='hidshow' data-toggle='collapse' data-parent='#accordion' href='#content"+i+"'>"+data[i].content+"</a></div>"+
	    	   "<div id='content"+i+"' class='panel-collapse collapse'>"+
	    	   "<div class='panel-body'>"+data[i].content+"</div></div>";
	    	   			    	  
	    	  var newTdObj3=myNewRow.insertCell(3);
	    	  newTdObj3.innerHTML="<a>"+data[i].score+"</a>";
	    	  
	    	  var newTdObj4=myNewRow.insertCell(4);
	    	  newTdObj4.innerHTML="<span onclick='getRadio()'>"+
	    	    "<label ><input type='radio' name='radio"+i+"' id='radio"+i+"' value='1'/>是</label>"+
	    	    "<label ><input type='radio' name='radio"+i+"' id='radio"+i+"' value='0'/>否</label>"+
	    	    "</span>";
  	  }
  	  var hotwords = data[data.length-1].freWords;
  	  document.getElementById("hotwords").innerHTML="<b style=\"color:red\">热点词："+ hotwords +"</b>";
	}
	function callback(msg)  
	{  
		alert(msg);
		document.getElementById("alert").innerHTML="";
	} 
	function dealFile()  
	{  
		document.getElementById("alert").innerHTML="<b style=\"color:red\">文件上传与处理中，请稍后...</b>";
	} 
	function addtext(){
    	if(index==1){
    		document.getElementById("second").style.display='block';
    		index++;
    	}
    	else if(index==2){
    		document.getElementById("third").style.display='block';
    		index++;
    	}
    }
    function dectext(){
    	if(index==2){
    		document.getElementById("second").style.display='none';
    		document.getElementById("keywords2").value="";
    		document.getElementById("link2").options[0].selected = true ;
    		index--;
    	}
    	else if(index==3){
    		document.getElementById("third").style.display='none';
    		document.getElementById("keywords3").value="";
    		document.getElementById("link3").options[0].selected = true ;
    		index--;
    	}
   }
    function sure(){
    	if(document.getElementById("keywords1").value==""&&
    			document.getElementById("keywords2").value==""&&
    			document.getElementById("keywords3").value==""){
    		alert("查询输入不能为空 !!!");
    		return false ;
    	}
    	else return true;
    }
    function getRadio(){ 
     	e = event.srcElement;
     	if(e.type=="radio"){
     		checked= true;
     	}
    }
    function OK(){    
    	if(checked==false){
    		alert("请先选择反馈信息 !!!");
    		return false
    	}
    	return true;
    }
</script>					