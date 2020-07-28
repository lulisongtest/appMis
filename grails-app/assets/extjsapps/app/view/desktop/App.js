var currentProcessDates=new Date()
var currentProcessDates=new Date()
var currentGzjsDate=new Date()


//当前登录用户账号username
var loginUsername;
function setLoginUsername(x) {//用户名（账号）
    loginUsername = x;
    return
}
//当前登录用户真实姓名truename
var loginTruename;
function setLoginTruename(x) {//用户名（真实姓名）
    loginTruename = x;
    return
}
//当前登录用户角色，中文角色是：管理员、科研负责人、科研管理员、单位负责人、普通用户
var loginUserRole = "no";
function setLoginUserRole(x) {//用户角色
    loginUserRole = x;
    return
}
//当前登录用户角色，中文角色是：管理员、科研负责人、科研管理员、单位负责人、普通用户
var Roleflag = "no";//应该取消
function setRoleflag(x) {
    Roleflag = x;
    return
}
//当前登录用户的单位名department如：克拉玛依市==>政府==>市公安局
var loginUserDepartment;
function setloginUserDepartment(x) {//当前用户的单位
    loginUserDepartment = x;
    return
}
//当前登录用户的在单位编码
var loginUserDepartmentTreeid;
function setLoginUserDepartmentTreeid(x) {//当前用户的单位码
    loginUserDepartmentTreeid = x;
    return
}
//树状目录选中的节点码（单位码），
var currentTreeNode = "p1000";
function setCurrentTreeNode(x) {//当前用户的单位码
    currentTreeNode = x;
    return
}
//树状目录选中的带上下级的单位名称（xx==>yy==>zz）
var departmentDetailTitle
//树状目录选中的不带上下级的单位名称（zz）
var departmentName=""
//树状目录选中的不带上下级的单位名称（zz）
var majorName=""
//树状目录选中的不带上下级的单位名称（zz）
var collegeName=""
//在表格中选中的用户名
var selectUsername=""
//判断网页是否超时，由store的load()激活，由拦截器controller\appmis\TimeoutInterceptor监控拦截load获取数据库的action，超时在application.yml设置
function timeout1(x, y) {
   // alert(x+"-----访问" + y + "数据，时间超时了！！！！")
    if (x == -1) {
        //alert("访问" + y + "数据，时间超时了！！！！")
        Ext.Msg.show({
            title: '操作提示 ',
            msg: "访问" + y + "数据，登录超时或用户在其他设备登录！！！！",
            buttons: Ext.MessageBox.OK
        });
        setTimeout(function () {
            parent.location.href = "/appMis/logoff";
            Ext.Msg.hide()
        }, 3500);
    }
}
//=============================================================================================
var departmentName = "克拉玛依市";//选中的单位的详细名称，如：克拉玛依市==>XX==>pppp,单位名称:pppp
var name = "";//取当前点击的人员的职工姓名
var b;
var manageDep="";//临时聘用人员管理单位
var pageSize=25
var UserNameflag;//当前登录用户账号username
var Userflag;//当前登录用户真实姓名truename
var Departmentflag;//当前登录用户的单位department
var Treeidflag;//当前登录用户的在单位编码
var AUTHflag = true;
function relogin(x) {//判断网页是否登录重名
    // alert("x===="+x)
    if (x == "relogin") {
        //if(!x) {
        Ext.Msg.show({
            title: '操作提示 ',
            msg: "【没有登录】 或 【登录重名了】，请重新登录！！！！！！！",
            buttons: Ext.MessageBox.OK
        });
        setTimeout(function () {
            parent.location.href = "/appMis/logoff";
            Ext.Msg.hide()
        }, 15000);
    }
}
function setDepartmentName(x) {//单位名称
    if(x=="全部"){
        departmentName="全部"
    }else{
        departmentName = x.split("==>")[x.split("==>").length-1];
    }
    return
}
function setAUTHflag(x) {//用户权限，设置Hidden的属性，true:隐藏， false:不隐藏
    AUTHflag = x;          //管理员权限
    return
}
//**********************************************************
function setUserNameflag(x) {//用户名（账号）
    UserNameflag = x;
    return
}
function setUserflag(x) {//用户名（真实姓名）
    Userflag = x;
    return
}
function setDepartmentflag(x) {//单位
    Departmentflag = x;
    return
}
function setAUTHflag(x) {//用户权限，设置Hidden的属性，true:隐藏， false:不隐藏
    AUTHflag = x;          //管理员权限
    return
}
function setTreeidflag(x) {//单位码
    Treeidflag = x;
    return
}
function setManageDep(x){//
    manageDep=x;
}
//对系统基本变量进行初始化，根据用户角色的权限用来确定界面上的内容哪些隐藏或不隐藏,生成操作树。
Ext.Ajax.request({
    url: '/appMis/authentication/userLogin?departmentName='+departmentName,
    async: false,//同步请求数据, true异步
    success://回调函数
        function (resp, opts) {//成功后的回调方法
            var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
            setLoginUsername(obj.username1);//用户名
            setLoginTruename(obj.truename1);//用户真实姓名
            setRoleflag(obj.chineseAuthority1);//中文角色
            setLoginUserRole(obj.chineseAuthority1);//中文角色
            setloginUserDepartment(obj.department1);//登录用户单位名
            setLoginUserDepartmentTreeid(obj.treeid1);//登录用户单位treeId
            setCurrentTreeNode("p" + obj.treeid1);//设置登录用户的单位码！！！或currentTreeNode="p"+LoginUserDepartmentTreeid//***********
            //==================================
            setAUTHflag((obj.chineseAuthority1) != "管理员");//中文角色，设置管理员标志，管理员：AUTHflag=false,非管理员：AUTHflag=true,
            setDepartmentName(obj.department1);//选中单位名,departmentName
            setAUTHflag((obj.chineseAuthority1) != "管理员");//设置管理员标志，管理员：AUTHflag=false,非管理员：AUTHflag=true,
            setUserNameflag(obj.username1);
            setUserflag(obj.truename1);
            setDepartmentflag(obj.department1);
            if(obj.chineseAuthority1=='区临时聘用人员管理员'){
                //取区临时聘用人员管理单位名称
                var dep=obj.truename1
                dep=dep.substring(0,(dep.length-3))
                setManageDep(dep)
            }
            setTreeidflag(obj.treeid1);
            Ext.define('appMis.view.desktop.App', {
                extend: 'Ext.ux.desktop.App',
                requires: [
                    'appMis.view.desktop.TeacherWindow',
                    'appMis.view.main.TeacherMain',
                    'Ext.window.MessageBox',
                    'appMis.view.desktop.model.ShortcutModel',//增加此项是为了解决汉字显示问题 //'Ext.ux.desktop.ShortcutModel',
                    'appMis.view.desktop.SystemStatus',
                    'appMis.view.desktop.VideoWindow',
                    'appMis.view.desktop.GridWindow',
                    'appMis.view.desktop.TabWindow',
                    'appMis.view.desktop.AccordionWindow',
                    'appMis.view.desktop.Notepad',
                    'appMis.view.desktop.BogusMenuModule',
                    'appMis.view.desktop.BogusModule',
                    'appMis.view.desktop.Settings',
                    'appMis.view.main.MainModel',
                    'appMis.view.main.UserMain',
                    'appMis.view.desktop.UserWindow',
                    'appMis.view.desktop.DepartmentWindow',
                    'appMis.view.main.DepartmentMain',
                    'appMis.view.desktop.SystemSettingWindow',
                    'appMis.view.main.SystemSettingMain',
                    'appMis.view.desktop.ProcessAdminWindow',
                    'appMis.view.main.ProcessAdminMain',
                    'appMis.view.desktop.StudentWindow',
                    'appMis.view.main.StudentMain'
                ],
                init: function() {
                    // custom logic before getXYZ methods get called...
                   // //自定义Grid的summary统计结果放置的位置'top'、'bottom'//用extjs6.0.0的Ext.define('Ext.grid.feature.Summary'替换extjs6.2.0
                    //如用extjs6.2.0则LOCKED某列后，汇总结果固定，不能随着滚动条水平移动
                    //extjs6.2.0或extjs6.0.0，LOCKED某列后，分类汇总groupingsummary中..getView().getFeature('group')获取无效
                    //要重写的方法//Ext.override(Ext.grid.feature.Summary, {extend: 'Ext.grid.feature.AbstractSummary',alias: 'feature.summary',
                    Ext.override(Ext.grid.feature.Summary, {
                        //要重写的方法
                        extend: 'Ext.grid.feature.AbstractSummary',
                        alias: 'feature.summary',
                        /**
                         * @cfg {String} dock
                         * Configure `'top'` or `'bottom'` top create a fixed summary row either above or below the scrollable table.
                         *
                         */
                        dock: undefined,
                        dockedSummaryCls: Ext.baseCSSPrefix + 'docked-summary',
                        panelBodyCls: Ext.baseCSSPrefix + 'summary-',
                        // turn off feature events.
                        hasFeatureEvent: false,
                        fullSummaryTpl: [
                            '{%',
                            'var me = this.summaryFeature,',
                            '    record = me.summaryRecord,',
                            '    view = values.view,',
                            '    bufferedRenderer = view.bufferedRenderer;',
                            'this.nextTpl.applyOut(values, out, parent);',
                            'if (!me.disabled && me.showSummaryRow && view.store.isLast(values.record)) {',
                            'if (bufferedRenderer) {',
                            '    bufferedRenderer.variableRowHeight = true;',
                            '}',
                            'me.outputSummaryRecord((record && record.isModel) ? record : me.createSummaryRecord(view), values, out, parent);',
                            '}',
                            '%}', {
                                priority: 300,
                                beginRowSync: function (rowSync) {
                                    rowSync.add('fullSummary', this.summaryFeature.summaryRowSelector);
                                },
                                syncContent: function(destRow, sourceRow, columnsToUpdate) {
                                    destRow = Ext.fly(destRow, 'syncDest');
                                    sourceRow = Ext.fly(sourceRow, 'sycSrc');
                                    var owner = this.owner,
                                        selector = owner.summaryRowSelector,
                                        destSummaryRow = destRow.down(selector, true),
                                        sourceSummaryRow = sourceRow.down(selector, true);
                                    // Sync just the updated columns in the summary row.
                                    if (destSummaryRow && sourceSummaryRow) {
                                        // If we were passed a column set, only update those, otherwise do the entire row
                                        if (columnsToUpdate) {
                                            this.summaryFeature.view.updateColumns(destSummaryRow, sourceSummaryRow, columnsToUpdate);
                                        } else {
                                            Ext.fly(destSummaryRow).syncContent(sourceSummaryRow);
                                        }
                                    }
                                }
                            }
                        ],
                        init: function(grid) {
                            var me = this,
                                view = me.view,
                                dock = me.dock;
                            me.callParent(arguments);
                            if (dock) {
                                grid.headerCt.on({
                                    add: me.onStoreUpdate,
                                    afterlayout: me.onStoreUpdate,
                                    scope: me
                                });
                                grid.on({
                                    beforerender: function() {
                                        var tableCls = [me.summaryTableCls];
                                        if (view.columnLines) {
                                            tableCls[tableCls.length] = view.ownerCt.colLinesCls;
                                        }
                                        me.summaryBar = grid.addDocked({
                                            childEls: ['innerCt', 'item'],
                                            renderTpl: [
                                                '<div id="{id}-innerCt" data-ref="innerCt" role="presentation">',
                                                '<table id="{id}-item" data-ref="item" cellPadding="0" cellSpacing="0" class="' + tableCls.join(' ') + '">',
                                                '<tr class="' + me.summaryRowCls + '"></tr>',
                                                '</table>',
                                                '</div>'
                                            ],
                                            scrollable: {
                                                x: false,
                                                y: false
                                            },
                                            hidden: !me.showSummaryRow,
                                            itemId: 'summaryBar',
                                            cls: [ me.dockedSummaryCls, me.dockedSummaryCls + '-' + dock ],
                                            xtype: 'component',
                                            dock: dock,
                                            weight: 10000000
                                        })[0];
                                    },
                                    afterrender: function() {
                                        grid.body.addCls(me.panelBodyCls + dock);
                                        view.on('scroll', me.onViewScroll, me);
                                        me.onStoreUpdate();
                                    },
                                    single: true
                                });
                                // Stretch the innerCt of the summary bar upon headerCt layout
                                grid.headerCt.afterComponentLayout = Ext.Function.createSequence(grid.headerCt.afterComponentLayout, function() {
                                    var width = this.getTableWidth(),
                                        innerCt = me.summaryBar.innerCt;
                                    me.summaryBar.item.setWidth(width);
                                    // "this" is the HeaderContainer. Its tooNarrow flag is set by its layout if the columns overflow.
                                    // Must not measure+set in after layout phase, this is a write phase.
                                    if (this.tooNarrow) {
                                        width += Ext.getScrollbarSize().width;
                                    }
                                    innerCt.setWidth(width);
                                });
                            } else {
                                if (grid.bufferedRenderer) {
                                    me.wrapsItem = true;
                                    view.addRowTpl(Ext.XTemplate.getTpl(me, 'fullSummaryTpl')).summaryFeature = me;
                                    view.on('refresh', me.onViewRefresh, me);
                                } else {
                                    me.wrapsItem = false;
                                    me.view.addFooterFn(me.renderSummaryRow);
                                }
                            }
                            grid.ownerGrid.on({
                                beforereconfigure: me.onBeforeReconfigure,
                                columnmove: me.onStoreUpdate,
                                scope: me
                            });
                            me.bindStore(grid, grid.getStore());
                        },
                        onBeforeReconfigure: function(grid, store) {
                            this.summaryRecord = null;
                            if (store) {
                                this.bindStore(grid, store);
                            }
                        },
                        bindStore: function(grid, store) {
                            var me = this;
                            Ext.destroy(me.storeListeners);
                            me.storeListeners = store.on({
                                scope: me,
                                destroyable: true,
                                update: me.onStoreUpdate,
                                datachanged: me.onStoreUpdate
                            });
                            me.callParent([grid, store]);
                        },
                        renderSummaryRow: function(values, out, parent) {
                            var view = values.view,
                                me = view.findFeature('summary'),
                                record, rows;
                            // If we get to here we won't be buffered
                            if (!me.disabled && me.showSummaryRow) {
                                record = me.summaryRecord;
                                out.push('<table cellpadding="0" cellspacing="0" class="' +  me.summaryItemCls + '" style="table-layout: fixed; width: 100%;">');
                                me.outputSummaryRecord((record && record.isModel) ? record : me.createSummaryRecord(view), values, out, parent);
                                out.push('</table>');
                            }
                        },
                        toggleSummaryRow: function(visible /* private */, fromLockingPartner) {
                            var me = this,
                                bar = me.summaryBar;
                            me.callParent([visible, fromLockingPartner]);
                            if (bar) {
                                bar.setVisible(me.showSummaryRow);
                                me.onViewScroll();
                            }
                        },
                        getSummaryBar: function() {
                            return this.summaryBar;
                        },
                        vetoEvent: function(record, row, rowIndex, e) {
                            return !e.getTarget(this.summaryRowSelector);
                        },
                        onViewScroll: function() {
                            this.summaryBar.setScrollX(this.view.getScrollX());
                        },
                        onViewRefresh: function(view) {
                            var me = this,
                                record, row;
                            // Only add this listener if in buffered mode, if there are no rows then
                            // we won't have anything rendered, so we need to push the row in here
                            if (!me.disabled && me.showSummaryRow && !view.all.getCount()) {
                                record = me.createSummaryRecord(view);
                                row = Ext.fly(view.getNodeContainer()).createChild({
                                    tag: 'table',
                                    cellpadding: 0,
                                    cellspacing: 0,
                                    cls: me.summaryItemCls,
                                    style: 'table-layout: fixed; width: 100%'
                                }, false, true);
                                row.appendChild(Ext.fly(view.createRowElement(record, -1)).down(me.summaryRowSelector, true));
                            }
                        },
                        createSummaryRecord: function (view) {
                            var me = this,
                                columns = view.headerCt.getVisibleGridColumns(),
                                remoteRoot = me.remoteRoot,
                                summaryRecord = me.summaryRecord,
                                colCount = columns.length, i, column,
                                dataIndex, summaryValue, modelData;
                            if (!summaryRecord) {
                                modelData = {
                                    id: view.id + '-summary-record'
                                };
                                summaryRecord = me.summaryRecord = new Ext.data.Model(modelData);
                            }
                            // Set the summary field values
                            summaryRecord.beginEdit();
                            if (remoteRoot) {
                                summaryValue = me.generateSummaryData();
                                if (summaryValue) {
                                    summaryRecord.set(summaryValue);
                                }
                            }
                            else {
                                for (i = 0; i < colCount; i++) {
                                    column = columns[i];
                                    // In summary records, if there's no dataIndex, then the value in regular rows must come from a renderer.
                                    // We set the data value in using the column ID.
                                    dataIndex = column.dataIndex || column.getItemId();
                                    // We need to capture this value because it could get overwritten when setting on the model if there
                                    // is a convert() method on the model.
                                    summaryValue = me.getSummary(view.store, column.summaryType, dataIndex);
                                    summaryRecord.set(dataIndex, summaryValue);
                                    // Capture the columnId:value for the summaryRenderer in the summaryData object.
                                    me.setSummaryData(summaryRecord, column.getItemId(), summaryValue);
                                }
                            }
                            summaryRecord.endEdit(true);
                            // It's not dirty
                            summaryRecord.commit(true);
                            summaryRecord.isSummary = true;
                            return summaryRecord;
                        },
                        onStoreUpdate: function() {
                            var me = this,
                                view = me.view,
                                selector = me.summaryRowSelector,
                                dock = me.dock,
                                record, newRowDom, oldRowDom, p;
                            if (!view.rendered) {
                                return;
                            }
                            record = me.createSummaryRecord(view);
                            newRowDom = Ext.fly(view.createRowElement(record, -1)).down(selector, true);
                            if (!newRowDom) {
                                return;
                            }
                            // Summary row is inside the docked summaryBar Component
                            if (dock) {
                                p = me.summaryBar.item.dom.firstChild;
                                oldRowDom = p.firstChild;
                            }
                                // Summary row is a regular row in a THEAD inside the View.
                            // Downlinked through the summary record's ID'
                            else {
                                oldRowDom = me.view.el.down(selector, true);
                                p = oldRowDom ? oldRowDom.parentNode : null;
                            }
                            if (p) {
                                p.insertBefore(newRowDom, oldRowDom);
                                p.removeChild(oldRowDom);
                            }
                            // If docked, the updated row will need sizing because it's outside the View
                            if (dock) {
                                me.onColumnHeaderLayout();
                            }
                        },
                        // Synchronize column widths in the docked summary Component
                        onColumnHeaderLayout: function() {
                            var view = this.view,
                                columns = view.headerCt.getVisibleGridColumns(),
                                column,
                                len = columns.length, i,
                                summaryEl = this.summaryBar.el,
                                el;
                            for (i = 0; i < len; i++) {
                                column = columns[i];
                                el = summaryEl.down(view.getCellSelector(column), true);
                                if (el) {
                                    Ext.fly(el).setWidth(column.width || (column.lastBox ? column.lastBox.width : 100));
                                }
                            }
                        },
                        destroy: function() {
                            var me = this;
                            me.summaryRecord = me.storeListeners = Ext.destroy(me.storeListeners);
                            me.callParent();
                        }
                    });
                    //自定义Grid列标题titleAlign的对齐方式
                    Ext.override(Ext.grid.column.Column, {
                        //Ext.grid.Column.override({
                        //要重写的方法
                        afterRender: function() {
                            var me = this,
                                el = me.el;
                            me.callParent(arguments);
                            //me.height=  80;//在此定义了所有Grid表头的高度！！！？
                            me.titleAlign = me.titleAlign || me.align;
                            el.addCls(Ext.baseCSSPrefix + 'column-header-align-' + me.titleAlign).addClsOnOver(me.overCls);
                        }
                    });
                    Ext.override(Ext.ux.desktop.Desktop, {
                        viewModel: 'main',//'appMis.view.main.MainModel'这里面放的是树状目录所需的各类值
                        id: 'viewModelDataRoot',//为了获取viewModel从而获取树状目录的值
                        //登录后用户信息存入ViewModel //图标自动多列显示
                        initShortCut: function () {
                            var btnHeight = 64;
                            var btnWidth = 64;
                            var btnPadding = 30;
                            var col = {
                                index: 1,
                                x: btnPadding
                            };
                            var row = {
                                index: 1,
                                y: btnPadding
                            };
                            var bottom;
                            var numberOfItems = 0;
                            var taskBarHeight = Ext.query(".ux-taskbar")[0].clientHeight + 40;
                            //alert("taskBarHeight ====="+taskBarHeight)
                            var bodyHeight = Ext.getBody().getHeight() - taskBarHeight;
                            var items = Ext.query(".ux-desktop-shortcut");
                            for (var i = 0, len = items.length; i < len; i++) {
                                numberOfItems += 1;
                                bottom = row.y + btnHeight;
                                if (((bodyHeight < bottom) ? true : false)
                                    && bottom > (btnHeight + btnPadding)) {
                                    numberOfItems = 0;
                                    col = {
                                        index: col.index++,
                                        x: col.x + btnWidth + btnPadding
                                    };
                                    row = {
                                        index: 1,
                                        y: btnPadding
                                    };
                                }
                                Ext.fly(items[i]).setXY([col.x, row.y]);
                                row.index++;
                                row.y = row.y + btnHeight + btnPadding;
                            }
                        },
                        //在createDataView 中添加一个监听    //换了这个createDataView，解决关闭窗口后，返回桌面，桌面无异常！
                        createDataView: function () {
                            var me = this;
                            return {
                                xtype: 'dataview',
                                overItemCls: 'x-view-over',
                                trackOver: true,
                                itemSelector: me.shortcutItemSelector,
                                store: me.shortcuts,
                                //以下两行解决关闭窗口后，返回桌面，桌面无异常！
                                style: { position: 'absolute'},
                                x: 0, y: 0,
                                tpl: new Ext.XTemplate(me.shortcutTpl),
                                listeners: {
                                    resize: me.initShortCut //在createDataView 中添加一个监听
                                }
                            };
                        },
                        //在afterRender 渲染结束时调用函数
                        afterRender: function () {
                            var me = this;
                            me.callParent();
                            me.el.on('contextmenu', me.onDesktopMenu, me);
                            Ext.Function.defer(me.initShortCut, 1);//延迟1 millsecond 再执行initShortCut
                        },
                        shortcutTpl: [
                            '<tpl for=".">',
                            '<div class="ux-desktop-shortcut" id="{nameId}-shortcut">',
                            '<div class="ux-desktop-shortcut-icon {iconCls}">',
                            '<img src="', Ext.BLANK_IMAGE_URL, '" title="{name}">',
                            '</div>',
                            '<span class="ux-desktop-shortcut-text">{name}</span>',
                            '</div>',
                            '</tpl>',
                            '<div class="x-clear"></div>'
                        ],
                        createWindowMenu: function () {
                            var me = this;
                            return {
                                defaultAlign: 'br-tr',
                                items: [
                                    {text: '还原', handler: me.onWindowMenuRestore, scope: me},
                                    {text: '最小化', handler: me.onWindowMenuMinimize, scope: me},
                                    {text: '最大化', handler: me.onWindowMenuMaximize, scope: me},
                                    '-',
                                    {text: '关闭', handler: me.onWindowMenuClose, scope: me}
                                ],
                                listeners: {
                                    beforeshow: me.onWindowMenuBeforeShow,
                                    hide: me.onWindowMenuHide,
                                    scope: me
                                }
                            };
                        },
                        createDesktopMenu: function () {
                            var me = this, ret = {
                                items: me.contextMenuItems || []
                            };
                            if (ret.items.length) {
                                ret.items.push('-');
                            }
                            ret.items.push(
                                {text: '瓦片', handler: me.tileWindows, scope: me, minWindows: 1},
                                {text: '层叠', handler: me.cascadeWindows, scope: me, minWindows: 1}
                            );
                            return ret;
                        },
                        initComponent: function () {
                            if("没有登录"==obj.truename1){
                                relogin("relogin")//没有登录，请重新登录！！！
                            }
                            //setDepartmentName(this.getViewModel().get('departmentName'))
                            this.getViewModel().set('user.name', loginTruename);//当前用户存入ViewModel
                            this.getViewModel().set('user.role', loginUserRole);//当前角色存入ViewModel
                            this.getViewModel().set('user.department', loginUserDepartment);//当前用户单位存入ViewModel
                            this.getViewModel().set('user.treeid', loginUserDepartmentTreeid);//当前用户单位存入ViewModel
                            // this.getViewModel().set('systemButtonMenu', eval('(' + obj1.roots + ')'));//当前角色存入ViewModel//
                            this.getViewModel().set('systemTreeMenu', eval('(' + obj.roots + ')'));//当前单位存入ViewModel
                            // this.getViewModel().set('systemTreeUserMenu', eval('(' + obj.roots1 + ')'));//当前单位用户存入ViewModel，有复选
                            ////this.getViewModel().set('systemTreeUserMenu1', eval('(' + (obj.roots1).replace(/,checked:false/g,"") + ')'));//当前单位用户存入ViewModel，无复选
                            // this.getViewModel().set('systemTreeUserMenu1', eval('(' + obj.roots2 + ')'));//当前单位用户存入ViewModel，无复选
                            this.callParent();
                        }
                    });
                    //重新定义桌面的【start】变为【开始】菜单
                    Ext.override(Ext.ux.desktop.TaskBar, {//重新定义桌面的菜单
                        startBtnText: '开始',
                        height: 35,//任务栏的高度
                        bodyPadding: '0 0 0 0',
                        border: '0 0 0 0',
                        margins: '0 0 0 0',
                        padding: '0 0 0 0'
                    });
                    this.callParent();
                    // now ready...
                },
                getModules : function(){
                    var data0;//根据不同的用户角色，在桌面上创建不同的应用图标的'Ext.ux.desktop.Module'
                    switch(loginUserRole) {
                        case '管理员' :
                            data0 = [
                                new appMis.view.desktop.TeacherWindow(),
                                new appMis.view.desktop.ProcessAdminWindow(),
                                new appMis.view.desktop.VideoWindow(),
                                new appMis.view.desktop.SystemStatus(),
                                new appMis.view.desktop.GridWindow(),
                                new appMis.view.desktop.TabWindow(),
                                new appMis.view.desktop.AccordionWindow(),
                                new appMis.view.desktop.Notepad(),
                                new appMis.view.desktop.BogusMenuModule(),
                                new appMis.view.desktop.BogusModule(),
                                new appMis.view.desktop.UserWindow(),
                                new appMis.view.desktop.DepartmentWindow(),
                                new appMis.view.desktop.SystemSettingWindow(),
                                new appMis.view.desktop.StudentWindow()
                            ];
                            break;
                        case '单位管理员' :
                            data0 = [
                                new appMis.view.desktop.TeacherWindow(),
                                new appMis.view.desktop.ProcessAdminWindow(),
                                new appMis.view.desktop.VideoWindow(),
                                new appMis.view.desktop.SystemStatus(),
                                new appMis.view.desktop.GridWindow(),
                                new appMis.view.desktop.TabWindow(),
                                new appMis.view.desktop.AccordionWindow(),
                                new appMis.view.desktop.Notepad(),
                                new appMis.view.desktop.BogusMenuModule(),
                                new appMis.view.desktop.BogusModule(),
                                new appMis.view.desktop.UserWindow(),
                                new appMis.view.desktop.SystemSettingWindow(),
                                new appMis.view.desktop.StudentWindow()
                            ];
                            break;
                        case '单位审核人' :
                            data0 = [
                                new appMis.view.desktop.TeacherWindow(),
                                new appMis.view.desktop.ProcessAdminWindow(),
                                new appMis.view.desktop.VideoWindow(),
                                new appMis.view.desktop.SystemStatus(),
                                new appMis.view.desktop.GridWindow(),
                                new appMis.view.desktop.TabWindow(),
                                new appMis.view.desktop.AccordionWindow(),
                                new appMis.view.desktop.Notepad(),
                                new appMis.view.desktop.BogusMenuModule(),
                                new appMis.view.desktop.BogusModule(),
                                new appMis.view.desktop.UserWindow(),
                                new appMis.view.desktop.SystemSettingWindow(),
                                new appMis.view.desktop.StudentWindow()
                            ];
                            break;
                        case '单位普通用户' :
                            data0 = [
                                new appMis.view.desktop.TeacherWindow(),
                                new appMis.view.desktop.ProcessAdminWindow(),
                                new appMis.view.desktop.VideoWindow(),
                                new appMis.view.desktop.SystemStatus(),
                                new appMis.view.desktop.GridWindow(),
                                new appMis.view.desktop.TabWindow(),
                                new appMis.view.desktop.AccordionWindow(),
                                new appMis.view.desktop.Notepad(),
                                new appMis.view.desktop.BogusMenuModule(),
                                new appMis.view.desktop.BogusModule(),
                                new appMis.view.desktop.UserWindow(),
                                new appMis.view.desktop.SystemSettingWindow(),
                                new appMis.view.desktop.StudentWindow()
                            ];
                            break;
                        case '普通用户' :
                            data0 = [
                                new appMis.view.desktop.TeacherWindow(),
                                new appMis.view.desktop.ProcessAdminWindow(),
                                new appMis.view.desktop.VideoWindow(),
                                new appMis.view.desktop.SystemStatus(),
                                new appMis.view.desktop.GridWindow(),
                                new appMis.view.desktop.TabWindow(),
                                new appMis.view.desktop.AccordionWindow(),
                                new appMis.view.desktop.Notepad(),
                                new appMis.view.desktop.BogusMenuModule(),
                                new appMis.view.desktop.BogusModule(),
                                new appMis.view.desktop.UserWindow(),
                                new appMis.view.desktop.SystemSettingWindow(),
                                new appMis.view.desktop.StudentWindow()
                            ];
                            break;
                    }
                    return data0;
                },
                getDesktopConfig: function () {
                    var data1;//根据不同的用户角色，在桌面上创建不同的应用图标
                    switch(loginUserRole) {
                        case '管理员' :
                            data1 = [
                                {name: '教师基本信息', nameId: 'teacher',iconCls: 'employee-shortcut',module: 'teacher'},
                                { name: '表格窗口', nameId:'grid-win',iconCls: 'grid-shortcut', module: 'grid-win' },
                                { name: '标签窗口', nameId:'tab-win',iconCls: 'grid-shortcut', module: 'tab-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'accordion-shortcut', module: 'acc-win' },
                                { name: '记事本',nameId:'notepad', iconCls: 'notepad-shortcut', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'cpu-shortcut', module: 'systemstatus'},
                                {name: '单位信息', nameId: 'department', iconCls: 'department-shortcut', module: 'department'},
                                {name: '个人信息', nameId: 'user', iconCls: 'user-shortcut', module: 'user'},
                                {name: '系统设置', nameId: 'systemSetting',iconCls: 'systemSetting-shortcut',module: 'systemSetting'},
                                {name: '事务管理', nameId: 'processAdmin', iconCls: 'processadmin-shortcut', module: 'processAdmin'},
                                {name: '学生信息', nameId: 'student',iconCls: 'employee-shortcut',module: 'student'}
                            ];
                            break;
                        case '单位管理员' :
                            data1 = [
                                {name: '教师基本信息', nameId: 'teacher',iconCls: 'employee-shortcut',module: 'teacher'},
                                { name: '表格窗口', nameId:'grid-win',iconCls: 'grid-shortcut', module: 'grid-win' },
                                { name: '标签窗口', nameId:'tab-win',iconCls: 'grid-shortcut', module: 'tab-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'accordion-shortcut', module: 'acc-win' },
                                { name: '记事本',nameId:'notepad', iconCls: 'notepad-shortcut', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'cpu-shortcut', module: 'systemstatus'},
                                {name: '个人信息', nameId: 'user', iconCls: 'user-shortcut', module: 'user'},
                                {name: '系统设置', nameId: 'systemSetting',iconCls: 'systemSetting-shortcut',module: 'systemSetting'},
                                {name: '事务管理', nameId: 'processAdmin', iconCls: 'processadmin-shortcut', module: 'processAdmin'},
                                {name: '学生信息', nameId: 'student',iconCls: 'employee-shortcut',module: 'student'}
                            ];
                            break;
                        case '单位审核人' :
                            data1 = [
                                {name: '教师基本信息', nameId: 'teacher',iconCls: 'employee-shortcut',module: 'teacher'},
                                { name: '表格窗口', nameId:'grid-win',iconCls: 'grid-shortcut', module: 'grid-win' },
                                { name: '标签窗口', nameId:'tab-win',iconCls: 'grid-shortcut', module: 'tab-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'accordion-shortcut', module: 'acc-win' },
                                { name: '记事本',nameId:'notepad', iconCls: 'notepad-shortcut', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'cpu-shortcut', module: 'systemstatus'},
                                {name: '个人信息', nameId: 'user', iconCls: 'user-shortcut', module: 'user'},
                                {name: '系统设置', nameId: 'systemSetting',iconCls: 'systemSetting-shortcut',module: 'systemSetting'},
                                {name: '事务管理', nameId: 'processAdmin', iconCls: 'processadmin-shortcut', module: 'processAdmin'},
                                {name: '学生信息', nameId: 'student',iconCls: 'employee-shortcut',module: 'student'}
                            ];
                            break;
                        case '单位普通用户' :
                            data1 = [
                                {name: '教师基本信息', nameId: 'teacher',iconCls: 'employee-shortcut',module: 'teacher'},
                                { name: '表格窗口', nameId:'grid-win',iconCls: 'grid-shortcut', module: 'grid-win' },
                                { name: '标签窗口', nameId:'tab-win',iconCls: 'grid-shortcut', module: 'tab-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'accordion-shortcut', module: 'acc-win' },
                                { name: '记事本',nameId:'notepad', iconCls: 'notepad-shortcut', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'cpu-shortcut', module: 'systemstatus'},
                                {name: '个人信息', nameId: 'user', iconCls: 'user-shortcut', module: 'user'},
                                {name: '系统设置', nameId: 'systemSetting',iconCls: 'systemSetting-shortcut',module: 'systemSetting'},
                                {name: '事务管理', nameId: 'processAdmin', iconCls: 'processadmin-shortcut', module: 'processAdmin'},
                                {name: '学生信息', nameId: 'student',iconCls: 'employee-shortcut',module: 'student'}
                            ];
                            break;
                        case '普通用户' :
                            data1 = [
                                {name: '教师基本信息', nameId: 'teacher',iconCls: 'employee-shortcut',module: 'teacher'},
                                { name: '表格窗口', nameId:'grid-win',iconCls: 'grid-shortcut', module: 'grid-win' },
                                { name: '标签窗口', nameId:'tab-win',iconCls: 'grid-shortcut', module: 'tab-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'accordion-shortcut', module: 'acc-win' },
                                { name: '记事本',nameId:'notepad', iconCls: 'notepad-shortcut', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'cpu-shortcut', module: 'systemstatus'},
                                {name: '个人信息', nameId: 'user', iconCls: 'user-shortcut', module: 'user'},
                                {name: '系统设置', nameId: 'systemSetting',iconCls: 'systemSetting-shortcut',module: 'systemSetting'},
                                {name: '事务管理', nameId: 'processAdmin', iconCls: 'processadmin-shortcut', module: 'processAdmin'},
                                {name: '学生信息', nameId: 'student',iconCls: 'employee-shortcut',module: 'student'}
                            ];
                            break;
                    }
                    var me = this, ret = me.callParent();
                    return Ext.apply(ret, {
                        contextMenuItems: [
                            { text: '改变设置', handler: me.onSettings, scope: me }
                        ],
                        shortcuts: Ext.create('Ext.data.Store', {
                            model: 'appMis.view.desktop.model.ShortcutModel',//增加此项是为了解决汉字显示问题 // model: 'Ext.ux.desktop.ShortcutModel',//这个点问题，不能显示汉字！！！
                            data: data1
                        }),
                        wallpaper: 'build/development/appMis/resources/images/wallpapers/desk.jpg',
                        wallpaperStretch: false
                    });
                },
                // config for the start menu
                getStartConfig : function() {
                    var me = this, ret = me.callParent();
                    return Ext.apply(ret, {
                        title: '管理系统',
                        iconCls: 'user',
                        height: 300,
                        toolConfig: {
                            width: 100,
                            items: [
                                {
                                    text:'设置',
                                    iconCls:'settings',
                                    handler: me.onSettings,
                                    scope: me
                                },
                                '-',
                                {
                                    text:'退出',
                                    iconCls:'logout',
                                    handler: me.onLogout,
                                    scope: me
                                }
                            ]
                        }
                    });
                },
                getTaskbarConfig: function () {
                    var data2;//根据不同的用户角色，在任务栏上创建不同的应用步图标
                    switch(loginUserRole) {
                        case '管理员' :
                            data2 = [
                                {name: '教师基本信息', iconCls: 'emplyee-shortcut', module: 'teacher'},
                                { name: '表格窗口',nameId:'grid-win', iconCls: 'notepad-small', module: 'grid-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'notepad-small', module: 'acc-win' },
                                { name: '记事本', nameId:'notepad',iconCls: 'notepad-small', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'notepad-small', module: 'systemstatus'},
                                {name: '单位信息', iconCls: 'grid-shortcut', module: 'department'},
                                {name: '个人信息', iconCls: 'user-shortcut-small', module: 'user'},
                                {name: '系统设置', iconCls: 'systemSetting-shortcut', module: 'systemSetting'},
                                {name: '事务管理', iconCls: 'processAdmin-small', module: 'processAdmin'},
                                {name: '学生信息', iconCls: 'emplyee-shortcut', module: 'student'}
                            ];
                            break;
                        case '单位管理员' :
                            data2 = [
                                {name: '教师基本信息', iconCls: 'emplyee-shortcut', module: 'teacher'},
                                {name: '表格窗口',nameId:'grid-win', iconCls: 'notepad-small', module: 'grid-win'},
                                {name: '手风琴窗口',nameId:'acc-win', iconCls: 'notepad-small', module: 'acc-win'},
                                {name: '记事本', nameId:'notepad',iconCls: 'notepad-small', module: 'notepad'},
                                {name: '系统状态',nameId:'systemstatus', iconCls: 'notepad-small', module: 'systemstatus'},
                                {name: '个人信息', iconCls: 'user-shortcut-small', module: 'user'},
                                {name: '系统设置', iconCls: 'systemSetting-shortcut', module: 'systemSetting'},
                                {name: '事务管理', iconCls: 'processAdmin-small', module: 'processAdmin'},
                                {name: '学生信息', iconCls: 'emplyee-shortcut', module: 'student'}
                            ];
                            break;
                        case '单位审核人' :
                            data2 = [
                                {name: '教师基本信息', iconCls: 'emplyee-shortcut', module: 'teacher'},
                                { name: '表格窗口',nameId:'grid-win', iconCls: 'notepad-small', module: 'grid-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'notepad-small', module: 'acc-win' },
                                { name: '记事本', nameId:'notepad',iconCls: 'notepad-small', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'notepad-small', module: 'systemstatus'},
                                {name: '个人信息', iconCls: 'user-shortcut-small', module: 'user'},
                                {name: '系统设置', iconCls: 'systemSetting-shortcut', module: 'systemSetting'},
                                {name: '事务管理', iconCls: 'processAdmin-small', module: 'processAdmin'},
                                {name: '学生信息', iconCls: 'emplyee-shortcut', module: 'student'}
                            ];
                            break;
                        case '单位普通用户' :
                            data2 = [
                                {name: '教师基本信息', iconCls: 'emplyee-shortcut', module: 'teacher'},
                                { name: '表格窗口',nameId:'grid-win', iconCls: 'notepad-small', module: 'grid-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'notepad-small', module: 'acc-win' },
                                { name: '记事本', nameId:'notepad',iconCls: 'notepad-small', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'notepad-small', module: 'systemstatus'},
                                {name: '个人信息', iconCls: 'user-shortcut-small', module: 'user'},
                                {name: '系统设置', iconCls: 'systemSetting-shortcut', module: 'systemSetting'},
                                {name: '事务管理', iconCls: 'processAdmin-small', module: 'processAdmin'},
                                {name: '学生信息', iconCls: 'emplyee-shortcut', module: 'student'}
                            ];
                            break;
                        case '普通用户' :
                            data2 = [
                                {name: '教师基本信息', iconCls: 'emplyee-shortcut', module: 'teacher'},
                                { name: '表格窗口',nameId:'grid-win', iconCls: 'notepad-small', module: 'grid-win' },
                                { name: '手风琴窗口',nameId:'acc-win', iconCls: 'notepad-small', module: 'acc-win' },
                                { name: '记事本', nameId:'notepad',iconCls: 'notepad-small', module: 'notepad' },
                                { name: '系统状态',nameId:'systemstatus', iconCls: 'notepad-small', module: 'systemstatus'},
                                {name: '个人信息', iconCls: 'user-shortcut-small', module: 'user'},
                                {name: '系统设置', iconCls: 'systemSetting-shortcut', module: 'systemSetting'},
                                {name: '事务管理', iconCls: 'processAdmin-small', module: 'processAdmin'},
                                {name: '学生信息', iconCls: 'emplyee-shortcut', module: 'student'}
                            ];
                            break;
                    }
                    var ret = this.callParent();
                    return Ext.apply(ret, {
                        quickStart: data2,
                        trayItems: [
                            {xtype: 'label', flex: 1,text:"用户："+loginTruename},
                            {xtype: 'label', flex: 1,text:"角色："+loginUserRole},
                            {xtype: 'trayclock', flex: 1}
                        ]
                    });
                },
                onLogout: function () {
                     // Ext.Msg.confirm('退出', '确认要退出吗?');
                    //window.close();//浏览器为了安全考虑，只允许js关闭由js打开的页面，即只能关闭window.open打开的页面。
                    window.removeEventListener('beforeunload',beforeUnloadHandler,true);
                    Ext.Msg.confirm("提示!","您确定要退出管理系统吗?",function(btn){
                        if(btn=="yes"){
                            parent.location.href = "/appMis/logoff";
                        }
                    })
                },
                onSettings: function () {
                    var dlg = new appMis.view.desktop.Settings({
                        desktop: this.desktop
                    });
                    dlg.show();
                }
            });
        }
});

