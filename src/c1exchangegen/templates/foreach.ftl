<#include "base.ftl">
<#assign title="foreach">
Для Каждого ${item} Из ${container} Цикл

<#list lines as line>
    ${line}
</#list>

КонецЦикла;