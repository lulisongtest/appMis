package com.user


import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONObject


@Transactional(readOnly = true)
class RequestmapController extends grails.plugin.springsecurity.ui.RequestmapController {
    def springSecurityService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def test() {//判断用户是否有权限访问
        render "success"
        return
    }

    def readRequestmap() {//UserStore.js的初始值
        String jsonStr
        if (params.sort != null) {
            jsonStr = params.sort
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
            JSONObject obj = new JSONObject(jsonStr);
            params.sort = obj.get('property')
            params.order = obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true

        }
        params.offset = params.start as int
        params.max = params.limit as int
        def list1
        try {
            list1 = Requestmap.list(params);
        } catch (e) {
            params.sort = null
            params.order = null
            list1 = Requestmap.list(params);
        }
        render(contentType: "text/json") {
            requestmaps   list1.collect() {
                [
                        id             : it.id,
                        url            : it?.url,
                        configAttribute: it?.configAttribute,
                        chineseUrl     : it?.chineseUrl,
                        roleList       : it?.roleList,
                        httpMethod     : it?.httpMethod,
                        treeId         : it?.treeId,
                        glyph          : it?.glyph,
                ]
            }
            totalCount  Requestmap.count()
        }
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Requestmap.list(params), model: [requestmapInstanceCount: Requestmap.count()]
    }

    def show(Requestmap requestmapInstance) {
        respond requestmapInstance
    }

    def create() {
        respond new Requestmap(params)
    }

    @Transactional
    def save() {
        def requestmapInstance = new Requestmap(params)
        if (requestmapInstance == null) {
            notFound()
            return
        }
        if (requestmapInstance.hasErrors()) {
            requestmapInstance.errors.each {
               // println it
            }
            respond requestmapInstance.errors, view: 'create'
            return
        }
        String query = "from Role"
        def list2 = Role.findAll(query);
        String role1 = ""
        try {//测试是多个用户访问还是单个用户访问
            List role = params.roleList
            for (int i = 0; i < role.size(); i++) {
                if (role[i] == "普通用户") {
                    role1 = "permitAll"
                    break
                }
                for (int j = 0; j < list2.size(); j++) {
                    if (role[i] == list2[j].chineseAuthority) {
                        if (role1 == "") {
                            role1 = list2[j].authority
                        } else {
                            role1 = role1 + "," + list2[j].authority
                        }
                        break
                    }
                }
            }
        } catch (e) {//是单个用户访问
            String roles = params.roleList
            if (roles == "普通用户") {
                role1 = "permitAll"
            } else {
                for (int j = 0; j < list2.size(); j++) {
                    if (roles == list2[j].chineseAuthority) {
                        if (role1 == "") {
                            role1 = list2[j].authority
                        } else {
                            role1 = role1 + "," + list2[j].authority
                        }
                        break
                    }
                }
            }
        }
        requestmapInstance.setProperties(params)
        requestmapInstance.setConfigAttribute(role1)


        try {
            if (requestmapInstance.save()) {
                springSecurityService.clearCachedRequestmaps()//让requestmap修改后生效
                render "success"
                return
            } else {
                requestmapInstance.errors.each {
                   // println it
                }
                render "failure"
                return
            }
        } catch (e) {
            render "failure"
            return
        }
    }

    def edit(Requestmap requestmapInstance) {
        respond requestmapInstance
    }

    @Transactional
    def update() {
        Requestmap requestmapInstance = Requestmap.findById(params.id)
        String oldChineseUrl = requestmapInstance.chineseUrl
        String oldUrl = requestmapInstance.url
        String oldRoleList = requestmapInstance.roleList
        String oldTreeId = requestmapInstance.treeId
        String oldGlyph = requestmapInstance.glyph
        if ((oldChineseUrl != params.chineseUrl) || (oldUrl != params.url) || (oldTreeId != params.treeId) || (oldGlyph != params.glyph)) {
//是否修改了访问表chineseUrl或url或treeId
            requestmapInstance.setProperties(params)
        }
        if (oldRoleList != params.roleList) {//判断是否修改了访问表的角色
            String query = "from Role"
            def list2 = Role.findAll(query);
            String role1 = ""
            // println("update ---params.roleList======="+(List)params.roleList)
            try {//测试是多个用户访问还是单个用户访问
                List role = params.roleList
                for (int i = 0; i < role.size(); i++) {
                    if (role[i] == "普通用户") {
                        role1 = "permitAll"
                        break
                    }
                    for (int j = 0; j < list2.size(); j++) {
                        if (role[i] == list2[j].chineseAuthority) {
                            if (role1 == "") {
                                role1 = list2[j].authority
                            } else {
                                role1 = role1 + "," + list2[j].authority
                            }
                            break
                        }
                    }
                }
            } catch (e) {//是单个用户访问
                //  println("------")
                String roles = params.roleList
                if (roles == "普通用户") {
                    role1 = "permitAll"
                } else {
                    for (int j = 0; j < list2.size(); j++) {
                        if (roles == list2[j].chineseAuthority) {
                            if (role1 == "") {
                                role1 = list2[j].authority
                            } else {
                                role1 = role1 + "," + list2[j].authority
                            }
                            break
                        }
                    }
                }
            }
            requestmapInstance.setProperties(params)
            /*if(role1.length()>22){
               if((role1.substring(0,22)=="isFullyAuthenticated()")){//【注册用户】isFullyAuthenticated()不能放在第一
                  role1=role1.substring(23)+",isFullyAuthenticated()"
               }
            }*/
            requestmapInstance.setConfigAttribute(role1)
        }
        if (requestmapInstance == null) {
            notFound()
            return
        }
        if (requestmapInstance.hasErrors()) {
            requestmapInstance.clearErrors()
            requestmapInstance.errors.each {
                //println it
            }
            return
        }

        try {
            if (requestmapInstance.save()) {
                springSecurityService.clearCachedRequestmaps()//让requestmap修改后生效
                render "success"
                return
            } else {
                requestmapInstance.errors.each {
                   // println it
                }
                render "failure"
                return
            }
        } catch (e) {
            render "failure"
            return
        }
    }

    @Transactional
    def delete() {

        def requestmapInstance = Requestmap.get(params.id)
        if (requestmapInstance == null) {
            notFound()
            return
        }
        try {
            if (requestmapInstance.delete() == null) {
                springSecurityService.clearCachedRequestmaps()//让requestmap修改后生效
                render "success"
                return
            } else {
                requestmapInstance.errors.each {
                   // println it
                }
                render "failure"
                return
            }
        } catch (e) {
            render "failure"
            return
        }
    }


}
