Ext.define('appMis.view.user.SelectDepartmentUser', {
    extend: 'Ext.window.Window',
    xtype: 'selectDepartmentUser',
    alias : 'widget.selectDepartmentUser',
   // id: 'selectDepartment',
    width: 570,
    height: 590,
    title: '选择部门用户信息',
    layout: 'column',
    autoShow: true,
    autoScroll: true,
    modal: true,//创建模态窗口
    initComponent : function() {
        this.modal=true;
        this.buttons = [
            {
                text: '保存',
                icon: 'tupian/save.png',
                action: 'save'
            },
            {
                text: '取消',
                icon: 'tupian/cancel.png',
                scope: this,
                handler: this.close
            }
        ];
        this.items=[
           /* {
                 xtype: 'button',
                 columnWidth:.2,
                 action:'save',
                 text:"确定"
            },{
                xtype: 'button',
                columnWidth:.2,
                text:"取消"
             },*/
            {
                xtype: 'treepanel',
                columnWidth:1,
                id:'tree-panel-user',
                split:true, //可以调节大小
                width: 220,
                height:500, //默认高度为500px
                minSize:150, //最小高度为150px
                autoScroll:true,//允许滚动条
                // tree-specific configs:
                // 树控件的特有的配置选项
                rootVisible:true,//隐藏根结点
                lines:true,
                singleExpand: false,//同时只能打开一个树,当打开其中任何一个树时,将会关闭其他已经打开的树目录
                useArrows:false,//树形目录使用visita中树目录显示效果(三角形代替+号)
                expanded:true, // 在树形菜单中是否展开
                root: Ext.getCmp("viewModelDataRoot").getViewModel().get('systemTreeUserMenu')//viewModelDataRoot在App.js中。很奇怪，按钮菜单要加上[],而树则不能加[]
            }];
        this.callParent(arguments);
    }
});
