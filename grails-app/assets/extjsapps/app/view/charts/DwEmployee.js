/**
 * Grouped 3D Columns are column charts where categories are grouped next to each
 * other. This is typically done to visually represent the total of all categories for a
 * given period or value.
 */
Ext.define('salaryMis.view.charts.DwEmployee', {
    extend: 'Ext.Panel',
    xtype: 'dw-employee-3d',
    requires: [
        'Ext.chart.theme.Muted',
        'Ext.chart.theme.Category3',
        'salaryMis.view.charts.DwEmployeeController'
    ],
    controller: 'dw-employee-3d',//指定控制器，否则就到app.js中查找
    bodyStyle: 'background: transparent !important',
    layout: {
        type: 'vbox',
        pack: 'center'
    },
    otherContent: [{
        // type: 'Controller',
        // path: 'classic/samples/view/charts/column3d/GroupedController.js'
    }, {
        // type: 'Store',
        // path: 'classic/samples/store/TwoYearSales.js'
    }],
    // </example>
    // width: 600,
    width: '100%',

    items: [
        {
            xtype: 'cartesian',
            reference: 'chart2',
            id:'chart2',
            //width: '100%',
            width: 1000,
            height: 460,
            theme: 'Muted',
            insetPadding: '70 40 0 40',
            interactions: ['itemhighlight'],
            animation: {
                duration: 200
            },
            store: 'DepartmentStore1',
           // legend: {docked: 'bottom'},
            sprites: [{
                type: 'text',
                text: '克拉玛依职业技术学院部门职工人数对照表（单位：人）',
                textAlign: 'center',
                fontSize: 18,
                fontWeight: 'bold',
                width: 1000,
                height: 20,
                x: 325, // the sprite x position
                y: 20  // the sprite y position
            }
                /*,
                 {
                 type: 'text',
                 text: '克拉玛依职业技术学院',
                 textAlign: 'center',
                 fontSize: 14,
                 x: 325,
                 y: 60
                 }
                 ,{
                 type: 'text',
                 text: 'Source: http://www.w3schools.com/',
                 fontSize: 10,
                 x: 120,
                 y: 85
                 }*/
            ],
            axes: [{
                type: 'category3d',
                position: 'bottom',
                fields: 'department',
                title: {
                    text: '部门职工人数（单位：人）',
                    translationX: -30
                },
                label: {
                    textAlign: 'center',
                    rotate: {degrees: -85}//轴上数据文本显示  比如label: { rotate: { degrees: 315} } 旋转315°
                },
                grid: true
            }, {
                type: 'numeric3d',
                position: 'left',
                fields: 'syrs',
                // fields: ['2016'],
                minimum: 0,
                //  maximum: 17000,
                majorTickSteps: 5,
                adjustByMajorUnit: true,
                grid: true,
                title: '部门职工人数 单位：人',
                renderer: 'onAxisLabelRender'
            }],

            series: {
                type: 'bar3d',
                stacked: false,
                yField: 'syrs',
                xField: 'department',
             // title: ['2015年', '2016年'],
                label: {
                    field: 'syrs',
                    display: 'insideEnd',
                    renderer: 'onSeriesLabelRender'
                },
                highlight: true,
                style: {
                    inGroupGapWidth: -7
                },
                tooltip: {
                    renderer: 'onSeriesTooltipRender'
                }
            }
            //<example>
        }
        /*{
            xtype: 'cartesian',
            flipXY: true,
            //width: '100%',
            //reference: 'chart1',
            width: 1000,
            height: 700,
            theme: 'Muted',
            insetPadding: '70 40 0 40',
            interactions: ['itemhighlight'],
            animation: {
                duration: 200
            },
            store: 'TwoYearSales',
            legend: {
                docked: 'bottom'
            },
            sprites: [{
                type: 'text',
                text: '近两年部门-低值易耗品对照表（单位：元）',
                textAlign: 'center',
                fontSize: 18,
                fontWeight: 'bold',
                width: 1000,
                height: 20,
                x: 325, // the sprite x position
                y: 10  // the sprite y position
            }
            ],
            axes: [{
                type: 'category3d',
                position: 'left',
                fields: 'quarter',
                title: {
                    text: '低值易耗品金额（单位：元）',
                    translationX: -30
                },
                label: {
                    textAlign: 'center',
                    rotate: {degrees: 0}//轴上数据文本显示  比如label: { rotate: { degrees: 315} } 旋转315°
                },
                grid: true
            }, {
                type: 'numeric3d',
                position: 'bottom',
                fields: ['2015', '2016'],
                minimum: 0,
                maximum: 10000,
                majorTickSteps: 10,
                grid: true,
                title: '人民币 单位：元',
                renderer: 'onAxisLabelRender'
            }],

            series: {
                type: 'bar3d',
                stacked: false,

                yField: ['2015', '2016'],
                xField: 'quarter',

                title: ['2015年', '2016年'],

                label: {
                    field: ['2015', '2016'],
                    display: 'insideEnd',
                    renderer: 'onSeriesLabelRender'
                },
                highlight: true,
                style: {
                    inGroupGapWidth: -7
                },
                tooltip: {
                    renderer: 'onSeriesTooltipRender'
                }
            }
        },*/
        /*{
            xtype: 'container',
            style: 'margin-top: 10px;',
            layout: {
                type: 'hbox',
                pack: 'center'
            },
            width: '100%',
            items: [{
                xtype: 'gridpanel',
                width: 400,
                columns: {
                    defaults: {
                        sortable: false,
                        menuDisabled: true,
                        renderer: 'onGridColumnRender'
                    },
                    items: [
                        {text: '部门', width: 150,dataIndex: 'quarter', renderer: Ext.identityFn},
                        {text: '2015年', dataIndex: '2015'},
                        {text: '2016年', dataIndex: '2016'}
                    ]
                },
                //store: {type: 'two-year-sales'},
                store: 'TwoYearSales'
            }]
            //</example>
        }*/
    ],
    tbar: [
        '->',
        {
            text: '切换主题',
            handler: 'onThemeSwitch'
        },
        /*{
            xtype: 'segmentedbutton',
            width: 200,
            defaults: {ui: 'default-toolbar'},
            items: [
                {
                    text: '堆叠'

                },
                {
                    text: '组',
                    pressed: true
                }
            ],
            listeners: {
                toggle: 'onStackGroupToggle'
            }
        },*/
        {
            text: '预览',
            handler: 'onPreview'
        }
    ]

});
