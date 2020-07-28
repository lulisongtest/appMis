<html>
<head>
	<meta name="layout" content="${layoutRegister}"/>
	<s2ui:title messageCode='spring.security.ui.resetPasswordx.title'/>
</head>
<body>
<s2ui:formContainer type='resetPasswordx' focus='password' width='475px'>
	<s2ui:form beanName='resetPasswordxCommand'>
		<g:hiddenField name='t' value='${token}'/>
		<div class="sign_in">
			<br/>
			<h3><g:message code='spring.security.ui.resetPassword.description'/></h3>
			<table>
				<s2ui:textFieldRow name='username' size='40' labelCodeDefault='name'/>
				<s2ui:passwordFieldRow name='oldPassword' size='40' labelCodeDefault='oldPassword'/>
				<s2ui:passwordFieldRow name='password' labelCodeDefault='Password'/>
				<s2ui:passwordFieldRow name='password2' labelCodeDefault='Password (again)'/>
			</table>
			<s2ui:submitButton elementId='submit' messageCode='spring.security.ui.resetPassword.submit'/>
		</div>
	</s2ui:form>
</s2ui:formContainer>
</body>
</html>
