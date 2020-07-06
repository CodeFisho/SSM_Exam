<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="../common/header.jsp"%>
<div class="easyui-layout" data-options="fit:true">
    <!-- Begin of toolbar -->
    <div id="wu-toolbar">
        <div class="wu-toolbar-button">
        <%@include file="../common/menus.jsp"%>
        <%-- <c:forEach items="${thirdMenuList}" var="thirdMenu">
        <a href="#" class="easyui-linkbutton" iconCls="${thirdMenu.icon }" onclick="${thirdMenu.url }" plain="true">${thirdMenu.name }</a>
        </c:forEach> --%>
           
        </div>
        
        <div class="wu-toolbar-search">
            <label>试题标题：</label><input  id="search-title" class="wu-text " style="width:100px">
			<label>试题类型：</label>
			<select  id="search-questionType" class="easyui-combobox" panelHeight="auto" style="width:100px">
            	<option value="-1">全部</option>
            	<option value="0">单选</option>
            	<option value="1">多选</option>
            	<option value="2">判断</option>
            </select>
            <label>试题科目：</label>
            <select  id="search-subject" class="easyui-combobox" panelHeight="auto" style="width:100px">
            	<option value="-1">全部</option>
            	<c:forEach items="${subjectList}" var="subject">
            		<option value="${subject.id}">${subject.name }</option>
            	</c:forEach>
            	
            </select>
            <a href="#"  id="search-btn" class="easyui-linkbutton" iconCls="icon-search">搜索</a>
        </div>
    </div>
    <!-- End of toolbar -->
    <table id="data-datagrid" class="easyui-datagrid" toolbar="#wu-toolbar"></table>
</div>

</style>
<!-- Begin of easyui-dialog -->
<div id="add-dialog" class="easyui-dialog" data-options="closed:true,iconCls:'icon-save'" style="width:430px; padding:10px;">
	<form id="add-form" method="post">
        <table>
            <tr>
                <td align="right">试题类型:</td>
                <td>
                	<select  id="add-questionType" name="questionType" class="easyui-combobox" panelHeight="auto" style="width:268px">
		            	<option value="-1">全部</option>
		            	<option value="0">单选</option>
		            	<option value="1">多选</option>
		            	<option value="2">判断</option>
            		</select>
                </td>
            </tr>
            <tr>
            <td align="right">所属科目:</td>
                <td>
                	<select name="subjectId" class="easyui-combobox easyui-validatebox" panelHeight="auto" style="width:268px" data-options="required:true, missingMessage:'请选择考试科目'">
                		<option value="-1"></option>
		                <c:forEach items="${subjectList}" var="subject">
			            	<option value="${subject.id}">${subject.name}</option>
		            	</c:forEach>
		            </select>
                </td>
            </tr>
        	<tr>
                <td align="right">题目:</td>
                <td><input  type="text" id="add-title" name="title"  class="wu-text easyui-validatebox" data-options="required:true,missingMessage:'请填写题目'" ></input></td>
            </tr>
           <tr>
                <td align="right">选项A:</td>
                <td><input  type="text" id="add-attrA" name="attrA"  class="wu-text easyui-validatebox" data-options="required:true,missingMessage:'请填写选项A'" ></input></td>
            </tr>
            <tr>
                <td align="right">选项B:</td>
                <td><input  type="text" id="add-attrB" name="attrB"  class="wu-text easyui-validatebox" data-options="required:true,missingMessage:'请填写选项B'" ></input></td>
            </tr>
            <tr>
                <td align="right">选项C:</td>
                <td><input  type="text" id="add-attrC" name="attrC"  class="wu-text easyui-validatebox"  ></input></td>
            </tr>
            <tr>
                <td align="right">选项D:</td>
                <td><input  type="text" id="add-attrD" name="attrD"  class="wu-text easyui-validatebox"  ></input></td>
            </tr>
            <tr>
                <td align="right">答案:</td>
                <td><input  type="text" id="add-answer" name="answer"  class="wu-text easyui-validatebox" data-options="required:true,missingMessage:'请填写答案'" ></input></td>
            </tr>
        </table>
    </form>
</div>
<!-- <!-- 修改窗口 -->
<div id="edit-dialog" class="easyui-dialog" data-options="closed:true,iconCls:'icon-save'" style="width:450px; padding:10px;">
	<form id="edit-form" method="post">
        <input type="hidden" name="id" id="edit-id">
         <table>
            <tr>
                <td align="right">试题类型:</td>
                <td>
                	<select  id="edit-questionType" name="questionType" class="easyui-combobox" panelHeight="auto" style="width:268px">
		            	<option value="-1">全部</option>
		            	<option value="0">单选</option>
		            	<option value="1">多选</option>
		            	<option value="2">判断</option>
            		</select>
                </td>
            </tr>
             <tr>
            <td align="right">所属科目:</td>
                <td>
                	<select id="edit-subject" name="subjectId" class="easyui-combobox easyui-validatebox" panelHeight="auto" style="width:268px" data-options="required:true, missingMessage:'请选择考试科目'">
                		<option value="-1"></option>
		                <c:forEach items="${subjectList}" var="subject">
			            	<option value="${subject.id}">${subject.name}</option>
		            	</c:forEach>
		            </select>
                </td>
            </tr>
        	<tr>
                <td align="right">题目:</td>
                <td><input  type="text" id="edit-title" name="title"  class="wu-text easyui-validatebox" data-options="required:true,missingMessage:'请填写题目'" ></input></td>
            </tr>
           <tr>
                <td align="right">选项A:</td>
                <td><input  type="text" id="edit-attrA" name="attrA"  class="wu-text easyui-validatebox" data-options="required:true,missingMessage:'请填写选项A'" ></input></td>
            </tr>
            <tr>
                <td align="right">选项B:</td>
                <td><input  type="text" id="edit-attrB" name="attrB"  class="wu-text easyui-validatebox" data-options="required:true,missingMessage:'请填写选项B'" ></input></td>
            </tr>
            <tr>
                <td align="right">选项C:</td>
                <td><input  type="text" id="edit-attrC" name="attrC"  class="wu-text easyui-validatebox"  ></input></td>
            </tr>
            <tr>
                <td align="right">选项D:</td>
                <td><input  type="text" id="edit-attrD" name="attrD"  class="wu-text easyui-validatebox"  ></input></td>
            </tr>
            <tr>
                <td align="right">答案:</td>
                <td><input  type="text" id="edit-answer" name="answer"  class="wu-text easyui-validatebox" data-options="required:true,missingMessage:'请填写答案'" ></input></td>
            </tr>
        </table>
    </form>
</div> -->
<!-- 导入试题窗口 -->
<div id="import-dialog" class="easyui-dialog" data-options="closed:true,iconCls:'icon-save'" style="width:500px; padding:10px;">
        <table>
            <tr>
                <td align="right">请选择文件:</td>
                <td><input type="text" id="import-name" name="filename" readonly="true" class="wu-text easyui-validatebox" data-options="required:true, missingMessage:'请选择文件'" ></td>
                <td><a onclick="uploadFile()" href="javascript:void(0)"  id="select-file-btn" class="easyui-linkbutton" iconCls="icon-upload">选择文件</a></td>
            </tr>  
            <tr>
            	<td align="right">所属科目:</td>
                <td>
                	<select  id="import-subjectId" name="subjectId" class="easyui-combobox easyui-validatebox" panelHeight="auto" style="width:268px" data-options="required:true, missingMessage:'请选择考试科目'">
                		<option value="-1"></option>
		                <c:forEach items="${subjectList}" var="subject">
			            	<option value="${subject.id}">${subject.name}</option>
		            	</c:forEach>
		            </select>
                </td>
            </tr>         
        </table>
</div>
<div id="process-dialog" class="easyui-dialog" data-options="closed:true,iconCls:'icon-upload',title:'文件加载中'" style="width:450px; padding:10px;">
	<div id="p" class="easyui-progressbar" style="width:400px" data-options="text:'请稍等...'"></div>
</div>
<input type="file" id="excel-file" style="display:none;" onchange="selected()">
<%@include file="../common/footer.jsp"%>
<!-- End of easyui-dialog -->
<script type="text/javascript">
	function start(){
		var value = $('#p').progressbar('getValue');
		if (value < 100){
			value += Math.floor(Math.random() * 10);
			$('#p').progressbar('setValue', value);
			setTimeout(arguments.callee, 200);
		}
	};

	function uploadFile(){
		$("#excel-file").click();
	}
	function selected(){
		$("#import-name").val($("#excel-file").val());
	}
	function upload(){		
		if($("#excel-file").val() =='') return;
		var formData=new FormData();
		formData.append('excelFile',document.getElementById('excel-file').files[0]);
		formData.append('subjectId',$("#import-subjectId").combobox('getValue'));
		$("#process-dialog").dialog('open');
		start();
		$.ajax({
			url:'uploap_file',
			type:'post',
			data:formData,
			contentType:false,
			processData:false,
			success:function(data){
				$("#process-dialog").dialog('close');
				if(data.type='success'){
					$('#data-datagrid').datagrid('reload');
					$('#import-dialog').dialog('close'); 
					formData.delete("excelFile");
				}
				if(data.type='error'){
					$.messager.alert("消息提醒",data.msg,"warning");
				}
			},
			error:function(data){
				$("#process-dialog").dialog('close');
				$.messager.alert("消息提醒","上传失败","warning");
				
			}
		});
	}
	
	//导入试题窗口
	function openImport(){
		//$('#add-form').form('clear');
		$('#import-dialog').dialog({
			closed: false,
			modal:true,
            title: "导入试题",
            buttons: [{
                text: '确定',
                iconCls: 'icon-ok',
                handler: upload
            }, {
                text: '取消',
                iconCls: 'icon-cancel',
                handler: function () {
                    $('#import-dialog').dialog('close');                    
                }
            }],
            onBeforeOpen:function(){
            	
            }
        });
	}

	/**
	*  添加记录
	*/
	function add(){
		var validate = $("#add-form").form("validate");
		if(!validate){
			$.messager.alert("消息提醒","请检查你输入的数据!","warning");
			return;
		}
		var data = $("#add-form").serialize();
		$.ajax({
			url:'add',
			dataType:'json',
			type:'post',
			data:data,
			success:function(data){
				if(data.type=='success'){
					$.messager.alert('信息提示','添加成功！','info');
					$("#add-name").val('');
					$("#add-password").val('');
					$("#add-tel").val('');
					$("#add-trueName").val('');
					$('#add-dialog').dialog('close');
					$('#add-dialog').dialog('close');
					$('#data-datagrid').datagrid('reload');
				}
				else
				{
					$.messager.alert('信息提示',data.msg,'warning');
				}
			}
		});
	}

	
	/**
	* Name 修改记录
	*/
	function edit(){
		var validate = $("#edit-form").form("validate");
		if(!validate){
			$.messager.alert("消息提醒","请检查你输入的数据!","warning");
			return;
		}
		var data = $("#edit-form").serialize();
		$.ajax({
			url:'edit',
			dataType:'json',
			type:'post',
			data:data,
			success:function(data){
				if(data.type == 'success'){
					$.messager.alert('信息提示','修改成功！','info');
					$('#edit-dialog').dialog('close');
					$('#data-datagrid').datagrid('reload');
				}else{
					$.messager.alert('信息提示',data.msg,'warning');
				}
			}
		});
	}
	
	
	/**
	* 删除记录
	*/
	function remove(){
		$.messager.confirm('信息提示','确定要删除该记录？', function(result){
			if(result){
				var item = $('#data-datagrid').datagrid('getSelected');
				if(item == null || item.length == 0){
					$.messager.alert('信息提示','请选择要删除的数据！','info');
					return;
				}
				$.ajax({
					url:'delete',
					dataType:'json',
					type:'post',
					data:{id:item.id},
					success:function(data){
						if(data.type == 'success'){
							$.messager.alert('信息提示','删除成功！','info');
							$('#data-datagrid').datagrid('reload');
							$('#data-datagrid').datagrid('clearSelections');
						}else{
							$.messager.alert('信息提示',data.msg,'warning');
						}
					}
				});
			}	
		});
	}
	
	/**
	* Name 打开添加窗口
	*/
	function openAdd(){
		//$('#add-form').form('clear');
		$('#add-dialog').dialog({
			closed: false,
			modal:true,
            title: "添加试题信息",
            buttons: [{
                text: '确定',
                iconCls: 'icon-ok',
                handler: add
            }, {
                text: '取消',
                iconCls: 'icon-cancel',
                handler: function () {
                    $('#add-dialog').dialog('close');                    
                }
            }],
            onBeforeOpen:function(){
            	
            }
        });
	}

	/**
	* 打开修改窗口
	*/
	function openEdit(){
		//$('#edit-form').form('clear');
		var item = $('#data-datagrid').datagrid('getSelections');
		if(item == null || item.length == 0){
			$.messager.alert('信息提示','请选择要修改的数据！','info');
			return;
		}
		if(item.length>1){
			$.messager.alert('信息提示','请只选择一条数据！','info');
			return;
		}
		item=item[0];
		$('#edit-dialog').dialog({
			closed: false,
			modal:true,
            title: "修改试题信息",
            buttons: [{
                text: '确定',
                iconCls: 'icon-ok',
                handler: edit
            }, {
                text: '取消',
                iconCls: 'icon-cancel',
                handler: function () {
                    $('#edit-dialog').dialog('close');                    
                }
            }],
            onBeforeOpen:function(){
            	$("#edit-id").val(item.id);
            	$("#edit-title").val(item.title);
            	$("#edit-subject").combobox('setValue',item.subjectId);
            	$("#edit-questionType").combobox('setValue',item.questionType);
            	$("#edit-attrA").val(item.attrA);
            	$("#edit-attrB").val(item.attrB);
            	$("#edit-attrC").val(item.attrC);
            	$("#edit-attrD").val(item.attrD);
            	$("#edit-answer").val(item.answer);
            }
        });
	}	
	
	//搜索按钮监听
	$("#search-btn").click(function(){
		var option={title:$("#search-title").val()};		
		var questionType=$("#search-questionType").combobox('getValue');
		var subjectId=$("#search-subject").combobox('getValue');
		if(questionType!=-1){
			option.questionType=questionType;
		}
		if(subjectId!=-1){
			option.subjectId=subjectId;
		}
		$('#data-datagrid').datagrid('reload',option);
	});
	
	
	function add0(m){return m<10?'0'+m:m }
	function format(shijianchuo){
	//shijianchuo是整数，否则要parseInt转换
		var time = new Date(shijianchuo);
		var y = time.getFullYear();
		var m = time.getMonth()+1;
		var d = time.getDate();
		var h = time.getHours();
		var mm = time.getMinutes();
		var s = time.getSeconds();
		return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
	}
	
	
	/** 
	* 载入数据
	*/
	$('#data-datagrid').datagrid({
		url:'list',
		rownumbers:true,
		singleSelect:false,
		pageSize:20,           
		pagination:true,
		multiSort:true,
		fitColumns:true,
		idField:'id',
	    treeField:'name',
	    nowrap:false,
		fit:true,
		columns:[[
			{ field:'chk',checkbox:true},			
			{ field:'title',title:'题目',width:100,sortable:true},
			{ field:'score',title:'分值',width:50,sortable:true},
			{ field:'questionType',title:'试题类型',width:100,formatter:function(value,row,index){
				var questionType=$("#search-questionType").combobox('getData');
				for(var i=0;i<questionType.length;i++){
					if(value==questionType[i].value) return questionType[i].text;
				}
				return value;
			}},
			{ field:'subjectId',title:'试题学科',width:100,formatter:function(value,row,index){
				var subject=$("#search-subject").combobox('getData');
				for(var i=0;i<subject.length;i++){
					if(value==subject[i].value) return subject[i].text;
				}
				return value;
			}},
			{ field:'attrA',title:'选项A',width:100,sortable:true},
			{ field:'attrB',title:'选项B',width:100,sortable:true},
			{ field:'attrC',title:'选项C',width:100,formatter:function(value,row,index){
				if(value==""){
					return "/";
				}
				return value;
			}},
			{ field:'attrD',title:'选项D',width:100,formatter:function(value,row,index){
				if(value==""){
					return "/";
				}
				return value;
			}},
			{ field:'answer',title:'答案',width:100,sortable:true},
			{ field:'createTime',title:'创建日期',width:100,formatter:function(value,row,index){
				return format(value);
			}}
		]]		
	});
</script>