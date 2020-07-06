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
			<label>所属试卷：</label>
			<select  id="search-exam" class="easyui-combobox" panelHeight="auto" style="width:100px">
            	<option value="-1">全部</option>
            	<c:forEach items="${examList}" var="exam">
            		<option value="${exam.id}">${exam.name}</option>
            	</c:forEach>
            </select>
           <label>所属考生：</label>
			<select  id="search-student" class="easyui-combobox" panelHeight="auto" style="width:100px">
            	<option value="-1">全部</option>
            	<c:forEach items="${studentList}" var="student">
            		<option value="${student.id}">${student.name}</option>
            	</c:forEach>
            </select>
            <label>所属试题：</label>
			<select  id="search-question" class="easyui-combobox" panelHeight="auto" style="width:100px">
            	<option value="-1">全部</option>
            	<c:forEach items="${questionList}" var="question">
            		<option value="${question.id}">${question.title}</option>
            	</c:forEach>
            </select>
            <a href="#"  id="search-btn" class="easyui-linkbutton" iconCls="icon-search">搜索</a>
        </div>
    </div>
    <!-- End of toolbar -->
    <table id="data-datagrid" class="easyui-datagrid" toolbar="#wu-toolbar"></table>
</div>

</style>

<!-- <!-- 修改窗口 -->
<div id="edit-dialog" class="easyui-dialog" data-options="closed:true,iconCls:'icon-save'" style="width:450px; padding:10px;">
	<form id="edit-form" method="post">
        <input type="hidden" name="id" id="edit-id">
          <table>
            <tr>
                <td align="right">所属考试:</td>
                <td>
                	<select id="edit-examId" name="examId" class="easyui-combobox easyui-validatebox" panelHeight="auto" style="width:268px" data-options="required:true, missingMessage:'请选择试卷科目'">
                		<option value="-1"></option>
		                <c:forEach items="${examList}" var="exam">
			            	<option value="${exam.id}">${exam.name}</option>
		            	</c:forEach>
		            </select>
                </td>
            </tr>
            <tr>
                <td align="right">所属学生:</td>
                <td>
                	<select id="edit-studentId" name="studentId" class="easyui-combobox easyui-validatebox" panelHeight="auto" style="width:268px" data-options="required:true, missingMessage:'请选择试卷科目'">
                		<option value="-1"></option>
		                <c:forEach items="${studentList}" var="student">
			            	<option value="${student.id}">${student.name}</option>
		            	</c:forEach>
		            </select>
                </td>
            </tr>
            
        </table>
    </form>
</div> -->
<%@include file="../common/footer.jsp"%>
<!-- End of easyui-dialog -->
<script type="text/javascript">
	
	
	//搜索按钮监听
	$("#search-btn").click(function(){
		var option={};		
		var examId=$("#search-exam").combobox('getValue');
		var studentId=$("#search-student").combobox('getValue');
		var questionId=$("#search-question").combobox('getValue');
		if(examId!=-1){
			option.examId=examId;
		}
		if(studentId!=-1){
			option.studentId=studentId;
		}
		if(questionId!=-1){
			option.questionId=questionId;
		}
		$('#data-datagrid').datagrid('reload',option);
	});
	
	
	
	
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
			{ field:'examId',title:'所属考试',width:160,formatter:function(value,index,row){
				var examList = $("#search-exam").combobox("getData");
				for(var i=0;i<examList.length;i++){
					if(examList[i].value == value)return examList[i].text;
				}
				return value;
			}},
			{ field:'examPaperId',title:'试卷ID',width:100,sortable:true},
			{ field:'studentId',title:'所属考生',width:160,formatter:function(value,index,row){
				var studentList = $("#search-student").combobox("getData");
				for(var i=0;i<studentList.length;i++){
					if(studentList[i].value == value)return studentList[i].text;
				}
				return value;
			}},
			{ field:'questionId',title:'所属试题',width:160,formatter:function(value,index,row){
				var questiontList = $("#search-question").combobox("getData");
				for(var i=0;i<questiontList.length;i++){
					if(questiontList[i].value == value)return questiontList[i].text;
				}
				return value;
			}},
			{ field:'answer',title:'提交答案',width:100,sortable:true},
			{ field:'isCorrect',title:'是否正确',width:200,formatter:function(value,index,row){
				if(value==0) return '错误';
				return '正确';
			}},
		]]		
	});
</script>