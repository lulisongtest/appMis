Ext.define('appMis.view.department.DepartmentAdd' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.departmentadd',
    width:1200,
    height: 440,
    title : '新增单位信息',
    //layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    // buttonAlign:'left',
    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                height: 330,
                layout:'absolute',
                items: [
                    {
                        xtype: 'textfield',
                        name: 'department',
                        x:5,y:5,
                        labelWidth: 110,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '单位名称',
                        allowBlank: false,
                        blankText: '单位名称不能为空，请输入单位名称',//验证错误时提示信息
                        value: "lulisong",
                        emptyText: '请输入单位名称'
                    },{
                        xtype: 'textfield',
                        name: 'dwjc',
                        x:400,y:5,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '单位简称',
                        allowBlank: false,
                        blankText: '单位简称不能为空，请输入单位简称',//验证错误时提示信息
                        value: "lulisong",
                        emptyText: '请输入单位简称'
                    },{
                        xtype: 'textfield',
                        name: 'dwtjsl',
                        x:775,y:5,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '单位统计数量',
                        allowBlank: false,
                        blankText: '单位统计数量不能为空，请输入单位统计数量',//验证错误时提示信息
                        value: "lulisong",
                        emptyText: '请输入单位统计数量'
                    },{
                        xtype: 'textfield',
                        name: 'gzzescbm',
                        x:5,y:41,
                        labelWidth: 110,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '工资总额手册编码',
                        allowBlank: false,
                        blankText: '工资总额手册编码不能为空，请输入工资总额手册编码',//验证错误时提示信息
                        value: "lulisong",
                        emptyText: '请输入工资总额手册编码'
                    },
                    {
                        xtype: 'textfield',
                        name: 'departmentCode',
                        x:400,y:41,
                        labelWidth: 85,
                        fieldLabel: '组织机构代码',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入组织机构代码',
                        value: "001001",
                        blankText: '组织机构代码不能为空，请输入组织机构代码'
                    },{
                        xtype: 'combobox',
                        name: 'lsgx',
                        x:775,y:41,
                        labelWidth: 85,
                        fieldLabel: '隶属关系',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入隶属关系',
                        value: "机关",
                        blankText: '隶属关系不能为空，请输入隶属关系',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'党委'},{'name':'人大'},{'name':'政府'},{'name':'政协'},{'name':'法院'},{'name':'检察院'},{'name':'民主党派和工商联'},{'name':'党群和社团'},{'name':'事业单位'},{'name':'其它'}]
                        }),
                    },{
                        xtype: 'combobox',
                        name: 'lsxt',
                        x:5,y:77,
                        labelWidth: 110,
                        fieldLabel: '隶属系统',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入隶属系统',
                        value: "机关",
                        blankText: '隶属系统不能为空，请输入隶属系统',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'中央'},{'name':'省（区、市）'},{'name':'地（市、州、盟）'},{'name':'县（市、区、旗）'},{'name':'乡（镇）'}]
                        }),
                    },{
                        xtype: 'combobox',
                        name: 'xzhq',
                        x:400,y:77,
                        width:325,
                        labelWidth: 95,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '行政划区',
                        allowBlank: false,
                        blankText: '行政划区不能为空，请输入行政划区',//验证错误时提示信息
                        value: "lulisong",
                        emptyText: '请输入行政划区',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'新疆维吾尔自治区克拉玛依市'}]
                        })
                    },

                    {
                        xtype: 'combobox',
                        name: 'dwxz',
                        x:775,y:77,
                        labelWidth: 85,
                        fieldLabel: '单位性质',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入单位性质',
                        value: "机关",
                        blankText: '单位性质不能为空，请输入单位性质',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'机关单位'},{'name':'全额事业（参公）'},{'name':'公益一类事业'},{'name':'公益二类事业'},{'name':'企业化管理'},{'name':'义务教育单位'},{'name':'基层医疗卫生单位'}]
                        })
                    },{
                        xtype: 'combobox',
                        name: 'czbkxs',
                        x:5,y:113,
                        labelWidth: 110,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '财政拨款形式',
                        allowBlank: false,
                        blankText: '财政拨款形式不能为空，请输入财政拨款形式',//验证错误时提示信息
                        value: "全额拨款",
                        emptyText: '请输入财政拨款形式',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'全额拨款'},{'name':'差额拨款'},{'name':'自收自支'},{'name':'其它'}]
                        }),
                    },{
                        xtype: 'combobox',
                        name: 'jblq',
                        x:400,y:113,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '艰边类区',
                        allowBlank: false,
                        blankText: '艰边类区不能为空，请输入艰边类区',//验证错误时提示信息
                        emptyText: '请输入艰边类区',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'一类'},{'name':'二类'},{'name':'三类'},{'name':'四类'},{'name':'五类'},{'name':'六类'}]
                        })
                    },
                    {
                        xtype: 'combobox',
                        name: 'dwjb',
                        x:775,y:113,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '单位级别',
                        allowBlank: false,
                        blankText: '单位级别不能为空，请输入单位级别',//验证错误时提示信息
                        value: "县（处）级",
                        emptyText: '请输入单位级别',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'正省级'},{'name':'副省级'},{'name':'正厅级'},{'name':'副厅级'},{'name':'正处级'},{'name':'副处级'},{'name':'正科级'},{'name':'副科级'},{'name':'股级'},{'name':'无级别'}]
                        })
                    },
                    {
                        xtype: 'combobox',
                        name: 'sshy',
                        x:5,y:149,
                        labelWidth: 110,
                        fieldLabel: '所属行业',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入所属行业',
                        value: "是",
                        blankText: '所属行业不能为空，请输入所属行业',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'农林牧渔'},{'name':'采矿'},{'name':'制造'},{'name':'电力热力燃气及水生产和供应'},{'name':'建筑'},
                                {'name':'批发和零售'},{'name':'交通运输仓储和邮政'},{'name':'住宿和餐饮'},{'name':'信息传输软件和信息技术服务'},
                                {'name':'金融'}, {'name':'房地产'},{'name':'租赁和商务服务'},{'name':'科研和技术服务'},
                                {'name':'水利环境和公共设施管理'},{'name':'居民服务修复和其它服务'},{'name':'教育'},{'name':'卫生和社会工作'},
                                {'name':'文化体育和娱乐业'},{'name':'公共管理社会保障和社会组织'},{'name':'国际组织'},{'name':'其它行业'}]
                        })
                    },
                    {
                        xtype: 'combobox',
                        name: 'cgqk',
                        x:400,y:149,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '垂管情况',
                        allowBlank: false,
                        blankText: '垂管情况不能为空，请输入垂管情况',//验证错误时提示信息
                        emptyText: '请输入垂管情况',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'垂管主管部门'},{'name':'被垂管单位'}]
                        })
                    },{
                        xtype: 'combobox',
                        name: 'zgbmqk',
                        x:775,y:149,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '主管部门情况',
                        allowBlank: false,
                        blankText: '主管部门情况不能为空，请输入主管部门情况',//验证错误时提示信息
                        emptyText: '请输主管部门情况',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'主管部门'},{'name':'所属下级单位'}]
                        })
                    },{
                        xtype: 'combobox',
                        name: 'sjgzzgbm',
                        x:5,y:185,
                        labelWidth: 110,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '上级工资主管部门',
                        allowBlank: false,
                        blankText: '上级工资主管部门不能为空，请输入上级工资主管部门',//验证错误时提示信息
                        emptyText: '请输入上级工资主管部门',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'克拉玛依市工资科'}]
                        })
                    },
                    {
                        xtype: 'combobox',
                        name: 'bzqk',
                        x:400,y:185,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '编制情况',
                        allowBlank: false,
                        blankText: '编制情况不能为空，请输入编制情况',//验证错误时提示信息
                        emptyText: '请输入编制情况',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data: [{'name': '正式编制'}, {'name': '临时编制'}]
                        })
                    },{
                        xtype: 'textfield',
                        name: 'bzpwqk',
                        x:775,y:185,
                        width:410,
                        labelWidth: 95,
                        fieldLabel: '编制批文情况',
                        allowBlank: true,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入编制批文情况',
                        value: "100",
                        blankText: '请输入编制批文情况'
                    },{
                        xtype: 'textfield',
                        name: 'bzs',
                        x:5,y:221,
                        labelWidth: 110,
                        fieldLabel: '编制数',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入编制数',
                        value: "100",
                        blankText: '编制数不能为空，请输入编制数'
                    },{
                        xtype: 'textfield',
                        name: 'syrs',
                        x:400,y:221,
                        labelWidth: 85,
                        fieldLabel: '实用人数',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入实用人数',
                        value: "100",
                        blankText: '实用人数不能为空，请输入实用人数'
                    },{
                        xtype: 'combobox',
                        name: 'hsxz',
                        x:775,y:221,
                        labelWidth: 85,
                        fieldLabel: '核算性质',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入核算性质',
                        value: "集中核算",
                        blankText: '核算性质不能为空，请输入核算性质',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'集中核算'},{'name':'独立核算'}]
                        })
                    },{
                        xtype: 'textfield',
                        name: 'leader',
                        x:5,y:257,
                        labelWidth: 110,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '单位负责人',
                        allowBlank: false,
                        blankText: '单位负责人不能为空，请输入单位负责人',//验证错误时提示信息
                        emptyText: '请输入单位负责人'
                    },{
                        xtype: 'textfield',
                        name: 'contacts',
                        x:400,y:257,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '工资经办人',
                        allowBlank: false,
                        blankText: '工资经办人不能为空，请输入工资经办人',//验证错误时提示信息
                        emptyText: '请输入工资经办人'
                    },
                    {
                        xtype: 'textfield',
                        name: 'phone',
                        x:775,y:257,
                        labelWidth: 85,
                        fieldLabel: '经办人电话',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入经办人电话',
                        value: "",
                        blankText: '经办人电话不能为空，请输入经办人电话'
                    },{
                        xtype: 'textfield',
                        name: 'fax',
                        x:5,y:293,
                        labelWidth: 110,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '传真号码',
                        allowBlank: false,
                        blankText: '传真号码不能为空，请输入传真号码',//验证错误时提示信息
                        emptyText: '请输入传真号码'
                    },
                    {
                        xtype: 'textfield',
                        name: 'postcode',
                        x:400,y:293,
                        labelWidth: 85,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '邮政编码',
                        allowBlank: false,
                        blankText: '邮政编码不能为空，请输入单位邮编',//验证错误时提示信息
                        emptyText: '请输入单位邮编'
                    },
                    {
                        xtype: 'textfield',
                        name: 'address',
                        x:775,y:293,
                        width:410,
                        labelWidth: 95,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '单位地址',
                        allowBlank: false,
                        blankText: '单位地址不能为空，请输入单位地址',//验证错误时提示信息
                        emptyText: '请输入单位地址'
                    },
                    {
                        xtype: 'textfield',
                        name: 'treeId',//树结点不能修改
                        hidden:true
                    },
                    {
                        xtype: 'textfield',
                        name: 'glyph',//树结点不能修改
                        hidden:true
                    },
                    {
                        xtype: 'textfield',
                        name: 'id',//树结点不能修改
                        hidden:true
                    }
                ]
            }
        ];
        this.buttons = [
            {
                text: '保存',
                glyph:'xf0c7@FontAwesome',
                action: 'save'
            },
            {
                text: '关闭',
                glyph:'xf00d@FontAwesome',
                scope: this,
                handler: this.close
            }
        ];
        this.callParent(arguments);
    }
});

