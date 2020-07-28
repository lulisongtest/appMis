

/*
<%=new Date()%>陆立松自动创建
//可以使用的参数
\${className}=${className}
\${fullName}=${fullName}
\${packageName}=${packageName}
\${packagePath}=${packagePath}
\${propertyName}=${propertyName}
\${lowerCaseName}=${lowerCaseName}
\${domain}=${domain}
//字段数==${domain[1]}
*/

Ext.define('appMis.model.${className}Model', {
    extend: 'Ext.data.Model',
    requires: [
        'Ext.data.Field'
    ],
    fields: ['id',<%
for(int i=2;i<domain.size();i++){
    if(domain[i]=="Date"){%>
        { name:'${domain[++i]}',dateFormat:'Y-m-d',type:'date'},<%
        i++;
    }else{
        if(domain[i]=="Blob"){
            i++;i++;
            continue
        }else{%>'${domain[++i]}',<%
        i++;
        }
    }
}%>
    ]
});

