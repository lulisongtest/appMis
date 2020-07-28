

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.user.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.user.UserRole'
grails.plugin.springsecurity.authority.className = 'com.user.Role'
grails.plugin.springsecurity.requestMap.className = 'com.user.Requestmap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]

//mail configuration
grails {
	mail {
		host = "smtp.163.com"
		username = "lulisongtest@163.com"
		password = "123abc"
		port = 465
		props = ["mail.smtp.auth":"true",
				 "mail.smtp.socketFactory.port":"465",
				 "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
				 "mail.smtp.socketFactory.fallback":"false"]
	}
}

grails{
	plugin{
		springsecurity {
			ui {
				encodePassword = false
				register {
					emailBody = '''\
尊敬的 $user.username （先生/女士）：<br/>
<br/>
你（或用你名义的其它人）用这个Emai地址创建了一个帐户，<br/>
<br/>
如果是你自己进行的操作, 请点击&nbsp;<a href="$url">这里</a> 完成注册工作。
'''
					emailFrom = 'lulisongtest@163.com'
					emailSubject = '新用户'
					defaultRoleNames = ['ROLE_USER']
					postRegisterUrl = null // use defaultTargetUrl if not set
				}
				forgotPassword {
					emailBody = '''\
尊敬的： $user.username  （先生/女士）,<br/>
<br/>
你（或用你名义的其它人）请求该帐户密码重置，<br/><br/>
<br/>
如果不是你自己的请求，请忽略该邮件;你的密码没有任何改变。<br/>
<br/>
如果是你自己进行的操作, 请点击&nbsp;<a href="$url">这里</a> 完成密码重置工作。
'''
					emailFrom = 'lulisongtest@163.com'
					emailSubject = '密码重置'
					postResetUrl = null // use defaultTargetUrl if not set
				}
			}
		}
	}
}
