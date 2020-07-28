package com.user

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

    private static final long serialVersionUID = 1

    String username
    String truename;//真实姓名
    String password
    String email;//电子邮件
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    String department;//单位名称
    String phone;//联系电话
    String chineseAuthority;//角色
    Date regdate;//注册日期
    Date lastlogindate;//最后一次登录时间
    String treeId;//单位码


    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    static constraints = {
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
        truename nullable: true, blank: true

        department nullable: true, blank: true
        phone nullable: true, blank: true
        chineseAuthority nullable: true, blank: true
        regdate nullable: true, blank: true
        lastlogindate nullable: true, blank: true
        treeId nullable: true, blank: true
    }

    static mapping = {
	    password column: '`password`'
    }
}
