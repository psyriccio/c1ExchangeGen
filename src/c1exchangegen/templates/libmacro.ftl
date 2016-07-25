<#macro inline value><#compress>
    ${value}
</#compress></#macro>
<#macro const_value value><#compress>
    <#if value?is_string && value?starts_with("~!")>${value[2..]}
    <#elseif value?is_boolean><#if value>Истина<#else>Ложь</#if>
    <#elseif value?is_string>"${value}"
    <#elseif value?is_date_like>Дата('${value?string["yyyyMMdd"]}')
    <#else>${value}
    </#if>
</#compress></#macro>
