
Ext.define('appMis.view.rules.ExportRules', {
    extend: 'Ext.window.Window',
    xtype: 'exportRules',
    id: 'exportRules',
    width: '100%',
    height: '100%',
    title: '查看文件',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    requires: [ ],
    initComponent: function () {
        var wjlx=rec.get('wjlx');
        var src="/appMis/rules/"+rec.get('titleCode')+"_"+rec.get('title')+"."+wjlx;
        this.items = [
            //{ html: '<embed width="100%" height="100%" src="'+src+'"></embed>'// html: '<iframe width="100%" height="100%" src="notice/7997b127bd13484fb1af2d23ddd332d1_ExtJS Web应用程序开发指南（第2版）.pdf"></iframe>'}
            {
                xtype:'panel',
                flex: 1,
                padding: "0 0 0 0" ,
                autoScroll: false,
                items: [{
                    xtype: 'component',
                    id: "displayRules",
                    autoEl: {
                        tag:'iframe',
                        style:'height:100%;width:100%;border:none',
                        src:'/appMis/rules/displayRules?recId='+rec.get('id')+'&title='+rec.get('title') +'&time='+ new Date()//
                        //src: 'http://' + window.location.host + '/KzykyMis/static/photoD/xxx.pdf'
                    }
                }]
            }
        ];
        this.callParent(arguments);
    }
});
