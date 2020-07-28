/**
 * This class is the view model for the Main view of the application.
 */
Ext.define('appMis.view.main.MainModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main',

    data: {
        name: 'appMis',
        //系统信息
        system: {
            name: '克拉玛依市人社局工资管理系统',
            version: ' 版本号5.2015.0',
            iconUrl: 'images/salaryMis/log2.png',
            iconUrl1: 'images/salaryMis/kzy2.png'
        },
        //用户信息
        user: {
            department: '克拉玛依职业技术学院',
            treeid: '001',
            name: '未知',
            role: '普通用户'
        },
        //服务单位和服务人员信息
        service: {
            department: '单位：克拉玛依市人社局  ',
            name: '徐勇',
            phonenumber: '15309923872',
            qq: '387861782',
            email: 'lulisongtest@163.com',
            copyright: '  系统版权所有Copyright 克拉玛依市人社局  '
        },
        menuType: {
            value: 'toolbar'
        },
        monetary: { // 金额单位
            value: 'tenthousand' // 默认万元，以后可以从后台取得个人偏好设置，或者存放在cookies中
        },
        //树状方式显示菜单
        treeMenu: {
            treemenu: ""
        },
        // 系统按钮菜单的定义，这个菜单是从后台通过ajax传过来的
        systemButtonMenu: [{}],
        // （单位）系统树状菜单的定义，这个菜单是从后台通过ajax传过来的
        systemTreeMenu: [{}],

        // （没有筛选过全部单位用户可复选）系统树状菜单的定义，这个菜单是从后台通过ajax传过来的
        systemTreeUserMenu: [{}],

        // （根据登录用户已经筛选过全部单位用户不可复选）系统树状菜单的定义，这个菜单是从后台通过ajax传过来的
        systemTreeUserMenu1: [{}],

        // （根据登录用户已经筛选过不可复选）系统树状菜单的定义，这个菜单是从后台通过ajax传过来的
        //systemTreeUserMenu2: [{}],


        // （单位）系统设置树状菜单的定义
        sysSettingMainMenu:{
            text: '系统设置', expanded: true,
            children: [
                {text: '用户管理', id:'s0001',leaf: true},
                {text: '权限管理', id:'s0002',leaf: true},
                {text: '角色管理', id:'s0003',leaf: true},
                {text: '数据表管理', id:'s0004',leaf: true},
                {text: '数据字典', id:'s0005', children: [
                        {text: '享受待遇', id:'s000401',leaf: true},
                        {text: '职级类型', id:'s000402',leaf: true},
                        {text: '工种类型',id:'s000403', leaf: true},
                        {text: '津贴类型',id:'s000404', leaf: true}
                    ]},
                {text: '工资标准', id:'s0006', children: [
                        {text: '公务员类', id:'s000501',leaf: true},
                        {text: '参照公务员法管理人员', id:'s000502',leaf: true},
                        {text: '其他事业类',id:'s000503', leaf: true},
                        {text: '义务教育类',id:'s000504', leaf: true},
                        {text: '基层医疗卫生类',id:'s000505', leaf: true},
                        // {text: '公安类',id:'s000509', leaf: true},
                        {text: '员额制类',id:'s000508', leaf: true},
                        {text: '石油企业工资类',id:'s000506', leaf: true},
                        {text: '其他津补贴',id:'s000507', leaf: true}
                    ]},
                {text: '系统数据管理', id:'s0007',children: [
                        {text: '初始化数据', id:'s000701',leaf: true},
                        {text: '数据备份', id:'s000702',leaf: true},
                        {text: '数据还原', id:'s000703',leaf: true}
                    ]}
            ]
        }
    }
});