package com.user

import grails.gorm.transactions.Transactional

import static org.springframework.http.HttpStatus.*


@Transactional(readOnly = true)
class UserRoleController {

    //static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def test(){//判断有无访问该表数据的权限
        render "success"
        return
    }
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond UserRole.list(params), model: [userRoleInstanceCount: UserRole.count()]
    }

    def show(UserRole userRoleInstance) {
        respond userRoleInstance
    }

    def create() {
        respond new UserRole(params)
    }

    @Transactional
    def save(UserRole userRoleInstance) {
       userRoleInstance.save flush: true
    }

    def edit(UserRole userRoleInstance) {
        respond userRoleInstance
    }

    @Transactional
    def update(UserRole userRoleInstance) {
        userRoleInstance.save flush: true
    }

    @Transactional
    def delete(UserRole userRoleInstance) {
       userRoleInstance.delete flush: true
    }

}
