
Ext.define('appMis.view.student.StudentAdd' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.studentadd',
    width:1120,
    height: 400,
    title : '新增学生信息',
    autoShow: true,
    modal: true,//创建模态窗口
    layout: 'column',
    initComponent: function() {
        this.items = [
            {
                xtype:'panel',
                padding:'20 10 0 0',
                columnWidth:0.78,
                layout: 'column',
                items: [
                    {
                        xtype: 'form',
                        height: 300,
                        layout: 'column',
                        items: [
                            {
                                xtype: 'textfield',
                                name: 'username',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '用户名',
                                allowBlank: false,
                                emptyText: '请输入用户名',
                                value: "未知",
                                blankText: '用户名不能为空，请输入用户名'
                            },
                            {
                                xtype: 'textfield',
                                name: 'truename',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '真实姓名',
                                allowBlank: false,
                                blankText: '真实姓名不能为空，请输入真实姓名',//验证错误时提示信息
                                value: "未知",
                                emptyText: '请输入真实姓名'
                            },
                            {
                                xtype: 'textfield',
                                name: 'email',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '电子邮件',
                                allowBlank: true,
                                value: "",
                                emptyText: '请输入电子邮件'
                            },
                            {
                                xtype: 'textfield',
                                name: 'department',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '班级',
                                allowBlank: true,
                                value: "",
                                emptyText: '请输入班级'
                            },
                            {
                                xtype: 'textfield',
                                name: 'major',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '专业',
                                allowBlank: true,
                                value: "",
                                emptyText: '请输入专业'
                            },
                            {
                                xtype: 'textfield',
                                name: 'college',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '院系',
                                allowBlank: true,
                                value: "",
                                emptyText: '请输入院系'
                            },
                            {
                                xtype: 'textfield',
                                name: 'phone',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '本人联系电话',
                                allowBlank: true,
                                value: "",
                                emptyText: '请输入本人联系电话'
                            },
                            {
                                xtype: 'textfield',
                                name: 'homephone',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '家长联系电话',
                                allowBlank: true,
                                value: "",
                                emptyText: '请输入家长联系电话'
                            },

                            {
                                xtype: 'textfield',
                                name: 'idNumber',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '身份证号',
                                allowBlank: false,
                                blankText: '身份证号不能为空，请输入身份证号',//验证错误时提示信息
                                value: "未知",
                                emptyText: '请输入身份证号',
                                listeners: {
                                    blur: function () {//渲染完成后，初始时会激活一次change
                                        var idNumber=this.getValue()
                                        if(idNumber.length==18){
                                            Ext.getCmp('birthDateadd').setValue(""+idNumber.substr(6,4)+"-"+idNumber.substr(10,2)+"-"+idNumber.substr(12,2))
                                        }else{
                                            Ext.getCmp('birthDateadd').setValue("19"+idNumber.substr(6,2)+"-"+idNumber.substr(8,2)+"-"+idNumber.substr(10,2))
                                        }
                                    }
                                }
                            }, {
                                xtype: 'datefield',
                                name: 'birthDate',
                                id:'birthDateadd',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '出生日期',
                                blankText: '出生日期不能为空，请输入出生日期',//验证错误时提示信息
                                //renderer : Ext.util.Format.dateRenderer('Y年m月d日'),
                                format:'Y年m月d日',
                                //  format:'Y-m-d',
                                emptyText: '请输入出生日期'
                            },{
                                xtype: 'combobox',
                                name: 'sex',
                                fieldLabel: '性别',
                                allowBlank: false,
                                labelStyle: 'padding-left:10px',
                                emptyText: '请输入性别',
                                value: "未知",
                                blankText: '性别不能为空，请输入性别',
                                displayField:'name',
                                store:Ext.create('Ext.data.Store',{
                                    fields:['name'],
                                    data:[{'name':'男'},{'name':'女'}]
                                })
                            },
                            {
                                xtype: 'combobox',
                                name: 'race',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '族别',
                                allowBlank: false,
                                blankText: '族别不能为空，请输入族别',//验证错误时提示信息
                                value: "未知",
                                emptyText: '请输入族别',
                                displayField:'name',
                                store:Ext.create('Ext.data.Store',{
                                    fields:['name'],
                                    data:[{'name':'汉族'},{'name':'回族'},{'name':'维吾尔族'},{'name':'哈萨克族'},{'name':'俄罗斯族'},{'name':'柯尔克孜族'},
                                        {'name':'乌兹别克族'},{'name':'塔塔尔族'},{'name':'蒙古族'},{'name':'锡伯族'},{'name':'塔吉克族'},{'name':'达擀尔族'},{'name':'羌族'},
                                        {'name':'鄂温克族'},{'name':'藏族'},{'name':'土家族'},{'name':'土族'},{'name':'壮族'},{'name':'苗族'},{'name':'彝族'},{'name':'傣族'},{'name':'东乡族'},
                                        {'name':'纳西族'},{'name':'景颇族'},{'name':'满族'},{'name':'白族'},{'name':'高山族'},{'name':'佤族'},{'name':'傈僳族'},{'name':'哈尼族'},{'name':'黎族'},
                                        {'name':'布依族'},{'name':'赫哲族'},{'name':'瑶族'},{'name':'朝鲜族'},{'name':'侗族'},{'name':'畲族'},{'name':'鄂伦春族'},{'name':'仫佬族'},{'name':'布朗族'},
                                        {'name':'怒族'},{'name':'普米族'},{'name':'毛南族'},{'name':'撒拉族'},{'name':'仡佬族'},{'name':'阿昌族'},{'name':'拉祜族'},{'name':'水族'},{'name':'德昂族'},
                                        {'name':'京族'},{'name':'门巴族'},{'name':'独龙族'},{'name':'裕固族'},{'name':'保安族'},{'name':'珞巴族'},{'name':'基诺族'},{'name':'外籍人士'},{'name':'其他'}]
                                })
                            },
                            {
                                xtype: 'combobox',
                                name: 'politicalStatus',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '政治面貌',
                                blankText: '政治面貌不能为空，请输入政治面貌',//验证错误时提示信息
                                value: "未知",
                                emptyText: '请输入政治面貌',
                                displayField:'name',
                                store:Ext.create('Ext.data.Store',{
                                    fields:['name'],
                                    data:[{'name':'中共党员'},{'name':'中共预备党员'},{'name':'共青团员'},{'name':'民建会员'},{'name':'无党派民主人士'},{'name':'台盟盟员'},
                                        {'name':'群众'},{'name':'致工党党员'},{'name':'民进会员'},{'name':'农工党党员'},{'name':'民盟盟员'},{'name':'九三学社社员'},
                                        {'name':'民革会员'},{'name':'其它'}]
                                }),
                            },{
                                xtype: 'datefield',
                                name: 'enrollDate',
                                labelStyle: 'padding-left:10px',
                                fieldLabel: '入学日期',
                                blankText: '入学日期不能为空，请输入参加入学日期',//验证错误时提示信息
                                //renderer : Ext.util.Format.dateRenderer('Y年m月d日'),
                                format:'Y年m月d日',
                                //  format:'Y-m-d',
                                emptyText: '请输入入学日期'
                            },
                            {
                                xtype: 'textfield',
                                name: 'treeId',
                                hidden:true
                            },
                            {
                                xtype: 'textfield',
                                name: 'id',
                                hidden:true
                            },
                            {
                                xtype: 'textfield',
                                name: 'currentStatus',
                                hidden:true
                            }
                ]
            }
                ]
            },
            {
                xtype:'panel',
                columnWidth:0.22,
                layout : 'column',
                items:[
                    {
                        xtype: 'image',
                        id:"photoAdd",
                        src:'/appMis/student/displayPhoto?username=&time=' +Math.random(),
                        columnWidth:1,
                        autoShow : true,
                        padding:'10 20 10 20',
                        height: 210
                    },
                    {
                        xtype: 'button',
                        text: '导入照片',
                        columnWidth:0.31,
                        action: 'importPhotoAdd'
                    },
                    {
                        xtype: 'form',
                        id: 'studentPhotoForm1',
                        height:40,
                        columnWidth:0.68,
                        url:"tmp",//上传服务器的地址
                        fileUpload: true,
                        items: [{
                            xtype: 'fileuploadfield',
                            labelWidth: 0,
                            name: 'studentPhoto1',
                            id: 'studentPhoto1',
                            buttonText: '选择照片文件',
                            blankText: '照片文件不能为空'
                        }]
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

