<#include "base.ftl">
<#assign title="struct_construct">
<#macro struct_iter map prefix name>
<#local _map=map _prefix=prefix _name=name>
<#if _prefix=""><#local _prefix=_name+".">${_name} = Новый Структура;</#if>
<#list map as key, value>
<#if !value!'~~~Неопределено'?is_hash>
    ${_prefix}Вставить("${key}", <@const_value value!'~~~Неопределено' />);
</#if>
<#if value!'~~~Неопределено'?is_hash>
    ${_prefix}Вставить("${key}", Новый Структура());<#local _nested_prefix=_prefix+key+"."><#rt>
    <@struct_iter value _nested_prefix "" />
</#if>
</#list>
</#macro>
<@struct_iter map "" name/>
