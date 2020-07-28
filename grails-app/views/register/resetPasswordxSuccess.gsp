<html>
<head>
	<meta name="layout" content="${layoutRegister}"/>
	<s2ui:title messageCode='重置密码成功'/>
</head>
<body>
<s2ui:formContainer type='resetPassword' focus='' width='775px'>
	<s2ui:form beanName=''>
		<g:hiddenField name='t' value='${token}'/>
		<div class="sign_in">
			<br/>
			<h3><g:message code='重置密码成功'/></h3>
			<table>

			</table>
			<s2ui:submitButton elementId='submit' messageCode='返回登录界面'/>
		</div>
	</s2ui:form>
</s2ui:formContainer>
</body>
</html>
