<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="../common/user_header.jsp"%>
<body>

	<div class="tm_main">
    	
		
        <div class="tm_container">
        	<div class="tm_navtitle">
				<h1>修改账户信息</h1>
                <span>在下列表单中修改您的账户信息</span>
            </div>
        </div>
        
        <br/>
        <div class="tm_container">
			<form method="post" id="form_user_load">
        	<table width="100%" cellpadding="5" border="0" class="tm_table_form">
            	<tbody>
                    <tr>
                        <th width="120">用户名 : </th>
                        <td>${student.name}</td>
                    </tr>
                    <tr>
                        <th width="120">所属学科 : </th>
                        <td>${subject.name}</td>
                    </tr>
					<tr>
                        <th>真实姓名 : </th>
                        <td>
							<input type="text" id="trueName" name="trueName" class="validate[required] tm_txt" size="50" maxlength="30" value="${student.trueName}" />
							<span class="tm_required">*</span> 
							<span class="tm_tip">填写用户的真实姓名</span>
						</td>
                    </tr>
					
					<tr>
                        <th>联系电话 : </th>
                        <td><input type="text" id="tel" name="tel" class="tm_txt" size="50" maxlength="30" value="${student.tel}" /></td>
                    </tr>
					
                </tbody>
                
                <tfoot>
                	<tr>
                    	<th></th>
                        <td>
                        	<button class="tm_btn tm_btn_primary" type="button" onclick="tmProfile.doUpdate();">提交</button>
                        </td>
                    </tr>
                </tfoot>
            </table>

			</form>
        </div>
        
        
    </div>

	<script type="text/javascript">
		$(document).ready(function() { 
			
		});

		var tmProfile = {
			doUpdate : function(){
				var formcheck = $("#form_user_load").validationEngine('validate');
				if(formcheck){
					var wcm = window.confirm('修改用户信息需要重新审核，请确认');
					if(!wcm){
						return;
					}
					$.ajax({
						url:'updateInfo',
						type:'POST',
						dataType:'JSON',
						data:{trueName:$("#trueName").val(),tel:$("#tel").val()},
						success:function(data){
						if(data.type=='success'){
							window.location.reload();
						}
					}
					});

				}else{
					return false;
				}
			}
		};
	</script>
</body>
</html>
