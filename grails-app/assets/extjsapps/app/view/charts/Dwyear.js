/**
 * Grouped 3D Columns are column charts where categories are grouped next to each
 * other. This is typically done to visually represent the total of all categories for a
 * given period or value.
 */
var currentyear=(new Date()).getFullYear();

Ext.define('salaryMis.view.charts.Dwyear', {
    extend: 'Ext.Panel',
    xtype: 'dw-year-3d',
    requires: [
        'Ext.chart.theme.Muted',
        'Ext.chart.theme.Category3',
        'salaryMis.view.charts.DwyearController'
    ],
    controller: 'dw-year-3d',//指定控制器，否则就到app.js中查找
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
            reference: 'chart1',
            id:'chart1',
            //width: '100%',
             width: 1000,
            height: 500,
            theme: 'Muted',
            insetPadding: '70 40 0 40',
            interactions: ['itemhighlight'],
            animation: {
                duration: 200
            },
            //store: {type: 'two-year-sales'},
            store: 'TwoYearSales',
            legend: {
                docked: 'bottom'
            },
            sprites: [{
                type: 'text',
                text: '克拉玛依市近两年部门-工资对照表（单位：元）',
                textAlign: 'center',
                fontSize: 18,
                fontWeight: 'bold',
                width: 1000,
                height: 20,
                x: 325, // the sprite x position
                y: 30  // the sprite y position
            }
            ],
            axes: [{
                type: 'category3d',
                position: 'bottom',
                fields: 'quarter',
                title: {
                    text: '工资（单位：元）',
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
                fields: ['2015', '2016'],
                // fields: ['2016'],
                minimum: 0,
                //  maximum: 17000,
                majorTickSteps: 5,
                adjustByMajorUnit: true,
                grid: true,
                title: '人民币 单位：元',
                renderer: 'onAxisLabelRender'
            }],
            series: {
                type: 'bar3d',
                stacked: false,
                yField:  ['2015', '2016'],
                xField: 'quarter',
                title: [(currentyear-1)+"年", currentyear+"年"],
                label: {
                    field:  ['2015', '2016'],
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
        },
        {
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
                        {text: (currentyear-1)+"年", dataIndex: '2015'},
                        {text: currentyear+"年", dataIndex: '2016'}
                    ]
                },
                //store: {type: 'two-year-sales'},
                store: 'TwoYearSales'
            }]
            //</example>
        }
    ],
    tbar: [
        '->',
        {
            text: '切换主题',
            handler: 'onThemeSwitch'
        },
        {
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
        },
        {
            text: '预览',
            handler: 'onPreview'
        }
    ]

});
