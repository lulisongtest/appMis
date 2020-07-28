/*<%=new Date()%>陆立松自动创建
*/

<%=packageName ? "package ${packageName}" : ''%>

import java.sql.Blob

class ${className} {<%
     for(int i=2;i<domain.size();i++){%>
        ${domain[i++]} ${domain[i++]};//${domain[i]}<%
    }%>
        static constraints ={<%
         for(int i=2;i<domain.size();i++){
             if(domain[++i]=="username"){%>
                 ${domain[i]}(blank:false,nullable:false);<%
             }else{%>
                 ${domain[i]}(blank:true,nullable:true);<%
             }
             i++
         }%>
        }
        static searchable=true;//可以全文搜索
    }

