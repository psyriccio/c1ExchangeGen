<#include "base.ftl">
<#assign title="proc">
<#if isFunc>Функция<#else>Процедура</#if> ${name}(<#list params as param>${param}<#sep>, </#list>) <#if isExport>Экспорт</#if>

<#list lines as line>
    ${line}
</#list>
Конец<#if isFunc>Функции<#else>Процедуры</#if>
