<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@include file="../common/user_header.jsp"%>
<style>
		h2{font-size:14px; margin:20px 0 10px 0;}
		.tm_param_list a{color:#000}
		.tm_param_list a:hover{color:#f00}

		.tm_blocker{float:left; width:50%;min-width:450px}
		.tm_blocker2{float:left; width:800px;}
</style>
<body>
	
    <div class="tm_main" style="min-width:1000px">
    	
        
        <div class="tm_container">
        	<div class="tm_navtitle">
            	<h1>欢迎使用</h1>
                <span>欢迎使用专业知识检测系统</span>
            </div>
        </div>

		



			<script type="text/javascript">
				var tm = {
					startExam : function(e){
						if(!tmCheckBrowserSupport()){
							alert("抱歉，您的浏览器不被支持，如需继续使用，请更换为：Chrome、Firefox、360极速浏览器。");
							return false;
						}
						var tr=$("#"+e);
						var eid=tr.attr("data-key");
						var html = [];
						html.push('<div style="margin:20px">');
						html.push('	<p style="line-height:20px">确定进入试卷并开始考试吗？</p>');
						
						html.push('	<table style="margin:0 auto; width:350px" border="0" cellpadding="0" cellspacing="0">');
						html.push('	<tr>');
						html.push('		<td width="80"><img src="../../resources/home/images/exam.png" width="60" /></td>');
						html.push('		<td><p><b>试卷名称</b>：'+tr.find("td").eq(0).text()+'<p>');
						html.push('			<p><b>考试时长</b>：'+tr.find("td").eq(2).text()+'<p>');
						html.push('			<p><b>卷面总分</b>：'+tr.find("td").eq(4).text()+'<p>');
						html.push('			<p><b>及格分数</b>：'+tr.find("td").eq(5).text()+'<p>');
						html.push('		</td>');
						html.push('	</tr>');
						html.push('</table>');

						html.push('<p style="text-align:center; margin-top:30px">');
						html.push('<button class="confir-exam tm_btn tm_btn_primary" type="button" onclick="tm.joinExam(\''+eid+'\')">确定</button>');
						html.push('</p>');

						html.push('</div>');

						layer.open({
						  type: 1,
						  title: '开始考试',
						  shadeClose: true,
						  shade: 0.8,
						  area: ['450px', '310px'],
						  content: html.join("")
						}); 

						return false;
					},
					joinExam : function(eid){
						$(".confir-exam").text('请稍等...');
						$("confir-exam").attr("disabled", true);
						$.ajax({
							url:'../exam/start_exam',
							dataType:'json',
							type:'post',
							data:{"examId":eid},
							success:function(data){
								if(data.type=='success'){
									//top.window.location="../exam/examing?examId="+eid;
									window.open("../exam/examing?examId="+eid);
								}else{
									alert(data.msg);
									window.location.reload();
								}
							},
							error: function(){
								//$(".tm_btn_primary").text('登录');
								alert('系统忙，请稍后再试');
								window.location.reload();
							}
						});
						setTimeout(function(){
							window.location.href="";
						}, 3000);
					}
				};


				function showTipOfTimeOut(user_starttime, exam_duration, exam_enddate){
					var wdesc = [];
					wdesc.push("<b>什么是超时未提交？</b>");
					wdesc.push("<br/>");
					wdesc.push("1、该试卷需在 <font color='red'>"+exam_duration+"分钟</font> 内或 <font color='red'>"+exam_enddate+"</font> 前提交答卷（先到为准）。");
					wdesc.push("<br/>");
					wdesc.push("2、该试卷您的开考时间为：<font color='red'>"+user_starttime+"</font>，当前已超时，无法提交。");
					wdesc.push("<br/>");
					wdesc.push("3、管理员可根据需要，使用您最后一次的答卷快照作为统计分值的依据。");

					layer.open({
					  type: 1,
					  title: false,
					  closeBtn: 1,
					  shadeClose: true,
						  area: ['600px', '150px'],
					  content: '<div style="padding:20px 20px; line-height:25px">'+wdesc.join("")+'</div>'
					});
				}
			</script>



			<div class="tm_container">
				<div class="tm_blocker2">
					<h2>进行中的考试</h2>
					<table width="100%" cellpadding="10" border="0" class="tm_table_list">
						<thead>
							<tr>
								<th>考试名称</th>
								<th>时间设定</th>
								<th>考试时长</th>
								<th>考试科目</th>
								<th>卷面总分</th>
								<th>及格分数</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							
								<c:if test="${empty examList}">
									<tr>
										<td colspan="7">没有进行中的考试</td>
									</tr>
								</c:if>
								
								
							<c:forEach items="${examList }" var="exam">
									<tr id=tr-${exam.id} data-key="${exam.id}">
										<td >${exam.name }</td>
										<td ><fmt:formatDate value="${exam.startTime}" pattern="yyyy-MM-dd hh:mm:ss"/>---<fmt:formatDate value="${exam.endTime}" pattern="yyyy-MM-dd hh:mm:ss"/></td>
										<td >${exam.avaliableTime }</td>
										<td >${subject.name }</td>
										<td >${exam.totalScore }</td>
										<td >${exam.passScore }</td>
										<td ><button class="tm_btn"  onclick="tm.startExam('tr-${exam.id}');"/>开始考试</td>
									</tr>
							</c:forEach> 
						</tbody>
					</table>
				</div>
			</div>

			<div class="tm_container">
				<div class="tm_blocker2">
					<h2>参加过的考试</h2>
					<table width="100%" cellpadding="10" border="0" class="tm_table_list">
						<thead>
							<tr>
								<th>试卷名称</th>
								<th>考试时长</th>
								<th>考试耗时</th>
								<th>考试时间</th>
								<th>试卷得分</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
								<c:if test="${empty historyList}">
									<tr>
										<td colspan="7">你还没有考过试题</td>
									</tr>
								</c:if>
								
								
									<c:forEach items="${historyList }" var="history">
									<tr>
										<td>${history.exam.name }</td>
										<td><span class="tm_label">${history.exam.avaliableTime}</span> 分钟</td>
										<td><span class="tm_label">${history.useTime}</span> 分钟</td>
										<td>
											<fmt:formatDate value="${history.startExamTime}" pattern="yyyy-MM-dd hh:mm:ss"/><br/><fmt:formatDate value="${history.endExamTime}" pattern="yyyy-MM-dd hh:mm:ss"/>
										</td>
										<td>
											
												
													<span class="tm_label">${history.score}</span>
													
														<c:if test="${history.exam.passScore>history.score }">
														<font color="red"><b>不及格</b></font>
														</c:if>
													
												
												
											
										</td>
										<td>																		
											<a href="review_exam?examId=${history.exam.id}&examPaperId=${history.id}" tartget="_blank" class="tm_btn tm_btn_primary" style="text-decoration:none;color:white;">回顾试卷</a>		
										</td>
									</tr>
									</c:forEach>			
						</tbody>
					</table>
				</div>
			</div>

			<div class="tm_container"></div>

		
        
        
    </div>
	
	<p>&nbsp;</p>

</body>
</html>