/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.codegen;

import c1c.bsl.BSLFormatter;
import c1c.bsl.gen.CodeTemplateProcessor;
import c1c.bsl.gen.Module;
import c1c.meta.generated.MetaObjectClass;
import c1exchangegen.mapping.MappingNode;
import c1exchangegen.mapping.MappingTreeModel;
import c1exchangegen.mapping.NodeStateContainer;
import freemarker.core.ParseException;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import static c1c.bsl.gen.Build.*;
import java.util.Map;

/**
 *
 * @author psyriccio
 */
public class ExchangeModuleGenerator {

    private String module;
    private List<String> objects;

    public ExchangeModuleGenerator() {
    }

    private void defineUtilProcs(Module.ModuleBuilder mod) {

        mod.def(
                proc("СтрСплит", args("Стр", "Разд"), true, true,
                        block(
                                "Рез = Новый Массив;",
                                "Ост = Стр;"
                        ),
                        whileLoop("Ост <> \"\"",
                                block("ПР = Найти(Ост, Разд);"),
                                ifThenElse("ПР = 0",
                                        block(
                                                "Рез.Добавить(Ост);",
                                                "Ост = \"\";"
                                        ),
                                        block(
                                                "Нк = Лев(Ост, ПР-1);",
                                                "Рез.Добавить(Нк);",
                                                "Ост = Прав(Ост, СтрДлина(Ост) - (ПР + СтрДлина(Разд) - 1));"
                                        )
                                )
                        )
                )
        );

        mod.def(
                proc("_ПрЗнч", args("Значен"), true, true,
                        ifThenElse(
                                "Справочники.ТипВсеСсылки().СодержитТип(ТипЗнч(Значен))"
                                + "Или Документы.ТипВсеСсылки().СодержитТип(ТипЗнч(Значен))",
                                _return("СериализоватьСсылку(Значен)"),
                                _return("Формат(Значен, \"ЧРД=.; ЧРГ=' '; ЧГ=0; ДФ=yyyyMMddHHmmss; БЛ=Ложь; БИ=Истина\")")
                        )
                )
        );

    }

    private void defineRefsSerializationProcs(Module.ModuleBuilder mod) {

        mod.def(
                proc("СериализоватьСсылку", args("СсылкаЗнч"), true, true,
                        tryCatch(
                                ifThenElse("Найти(Строка(ТипЗнч(СсылкаЗнч)), \"Перечисление.\") <> 0",
                                        _return("\"~~~@REF:\" + Строка(СсылкаЗнч.Метаданные().ПолноеИмя())"),
                                        elseIf(
                                                "Найти(Строка(ТипЗнч(СсылкаЗнч)), \"Справочник табличная часть строка\") <> 0",
                                                _return("\"~~~@REF:\" + \"СтрокаТЧ\"  + \"/\" + Строка(СсылкаЗнч)")),
                                        elseIf(
                                                "Найти(Строка(ТипЗнч(СсылкаЗнч)) ,\"Документ табличная часть строка\") <> 0",
                                                _return("\"~~~@REF:\" + \"СтрокаТЧ\"  + \"/\" + Строка(СсылкаЗнч)")),
                                        elseIf("", "Возврат \"~~~@REF:\" + Строка(СсылкаЗнч.Метаданные().ПолноеИмя()) + \"/\" + Строка(СсылкаЗнч.УникальныйИдентификатор());")
                                ),
                                block(
                                        "Сообщить(\"Ошибка сериализации ссылки, \" + Строка(СсылкаЗнч) + \" \" + Строка(ТипЗнч(СсылкаЗнч)));",
                                        "Сообщить(ОписаниеОшибки());"
                                )
                        )
                )
        );

        mod.def(
                proc("ДесереализоватьСсылку", args("Стрк"), true, true,
                        block("Чст = СтрСплит(Стрк, \"@REF:\");"),
                        ifThen(
                                "Чст.Количество() = 2",
                                ifThen(
                                        "Чст[0] = \"~~~\"",
                                        block(
                                                "Пр = СтрСплит(Чст[1], \"/\");",
                                                "Имена = СтрСплит(Пр[0], \".\");"
                                        ),
                                        ifThen(
                                                "Имена[0] = \"Справочник\"",
                                                _return("Справочники[Имена[1]].ПолучитьСсылку(Новый УникальныйИдентификатор(Пр[1]))")
                                        ),
                                        ifThen(
                                                "Имена[0] = \"Документ\"",
                                                _return("Документы[Имена[1]].ПолучитьСсылку(Новый УникальныйИдентификатор(Пр[1]))")
                                        ),
                                        ifThen(
                                                "Имена[0] = \"Перечисление\"",
                                                forEach(
                                                        "ПерЗн", "Перечисления[Имена[1]]",
                                                        ifThen(
                                                                "ПерЗн.Метаданные().Имя = Пр[1]",
                                                                _return("ПерЗн")
                                                        )
                                                ),
                                                _return("Перечисления[Имена[1]].ПустаяСсылка()")
                                        )
                                )
                        ),
                        _return("Неопределено")
                )
        );

    }

    private void defineArrayOfStructsSerializationProcs(Module.ModuleBuilder mod) {

        mod.def(
                proc("СериализоватьМассивСтруктур", args("ЗаписьJSON", "Мас"), true, false,
                        block("ЗаписьJSON.ЗаписатьНачалоМассива();"),
                        forEach("Зп", "Мас",
                                block("ЗаписьJSON.ЗаписатьНачалоОбъекта();"),
                                ppIfThen("Клиент", "Сообщить(\"json_export \" + Строка(Эл._Ссылка));"),
                                forEach("ЭлСтр", "Эл",
                                        ppIfThen("Клиент", "Сообщить(\"json_export_field \" + Строка(ЭлСтр.Ключ) + \" = \" + Строка(ЭлСтр.Значение));"),
                                        block(
                                                "ЗаписьJSON.ЗаписатьИмяСвойства(ЭлСтр.Ключ);",
                                                "ЗаписьJSON.ЗаписатьЗначение(_ПрЗнч(ЭлСтр.Значение));"
                                        )
                                ),
                                block("ЗаписьJSON.ЗаписатьКонецОбъекта();")
                        ),
                        block("ЗаписьJSON.ЗаписатьКонецМассива();")
                )
        );

        mod.def(
                proc("ДесереализоватьМассивСтруктур", args("ЧтениеJSON"), true, true,
                        block(
                                "_ММ = Новый Массив;",
                                "Структ = Неопределено;",
                                "ИмяСв = \"\";"
                        ),
                        whileLoop("ЧтениеJSON.Прочитать()",
                                ifThen("ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.НачалоОбъекта",
                                        "Структ = Новый Структура;"
                                ),
                                ifThen("ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.КонецОбъекта",
                                        ppIfThen("Клиент", "Сообщить(\"json_import \" + Структ._Ссылка);"),
                                        block(
                                                "Структ._Ссылка = ДесереализоватьСсылку(Структ._Ссылка);",
                                                "_ММ.Добавить(Структ);"
                                        )
                                ),
                                ifThen("ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.ИмяСвойства",
                                        "ИмяСв = ЧтениеJSON.ТекущееЗначение;"
                                ),
                                ifThen("ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.Строка\n"
                                        + "Или ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.Число\n"
                                        + "Или ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.Булево",
                                        "Структ.Вставить(ИмяСв, ЧтениеJSON.ТекущееЗначение);"
                                )
                        ),
                        _return("_ММ")
                )
        );

    }

    private void deficeCreateObjectsProc(Module.ModuleBuilder mod) {

        mod.def(
                proc("СоздатьОбъектыИзМассиваСтруктур", args(), true, true,
                        block("_Об = Новый Массив;"),
                        forEach("Струк", "Мас",
                                ppIfThen("Клиент",
                                        "ОбработкаПрерыванияПользователя();",
                                        "Сообщить(\"construct \" + Струк._Тип + \"/\" + Струк._ИД);"
                                ),
                                block("Объект = Струк._Ссылка.ПолучитьОбъект();"),
                                ifThen("Объект = Неопределено",
                                        block("Имена = СтрСплит(Струк._Тип, \".\");"),
                                        ifThen("Имена[0] = \"Справочник\"",
                                                block(
                                                        "ЭтоГруппа = Ложь;",
                                                        "Струк.Свойство(\"ЭтоГруппа\", ЭтоГруппа);"
                                                ),
                                                ifThen("ЭтоГруппа = Неопределено", "ЭтоГруппа = Ложь;"),
                                                ifThenElse("ЭтоГруппа",
                                                        block("Объект = Справочники[Имена[1]].СоздатьГруппу();"),
                                                        block("Объект = Справочники[Имена[1]].СоздатьЭлемент();")
                                                )
                                        ),
                                        ifThen("Имена[0] = \"Документ\"",
                                                "Объект = Документы[Имена[1]].СоздатьДокумент();"
                                        ),
                                        block("Объект.УстановитьСсылкуНового(Струк._Ссылка);")
                                ),
                                forEach("СтрСтр", "Струк",
                                        ifThen("Лев(СтрСтр.Ключ, 1) = \"_\"", "Продолжить"),
                                        block(
                                                "ТипЗн = Неопределено;",
                                                "Зн = Неопределено;"
                                        ),
                                        tryCatch(
                                                block("Выполнить(\"ТипЗн = ТипЗнч(Объект.\"+ СтрСтр.Ключ +\")\");"),
                                                ppIfThen("Клиент", "Сообщить(ОписаниеОшибки());")
                                        ),
                                        ifThen("ТипЗн <> Неопределено",
                                                ifThen("Справочники.ТипВсеСсылки().СодержитТип(ТипЗн)\n"
                                                        + "Или Документы.ТипВсеСсылки().СодержитТип(ТипЗн)",
                                                        "Зн = ДесереализоватьСсылку(СтрСтр.Значение);"
                                                ),
                                                ifThen("ТипЗн = Тип(\"Строка\")",
                                                        "Зн = Строка(СтрСтр.Значение);"
                                                ),
                                                ifThen("ТипЗн = Тип(\"Число\")",
                                                        tryCatch("Зн = Число(СтрСтр.Значение)", "Зн = 0")
                                                ),
                                                ifThen("ТипЗн = Тип(\"Булево\")",
                                                        "Зн = (Строка(СтрСтр.Значение) = \"Истина\");"
                                                ),
                                                ifThen("ТипЗн = Тип(\"Дата\")",
                                                        trySuppress("Зн = Дата(Строка(СтрСтр.Значение));")
                                                )
                                        ),
                                        trySuppress("Выполнить(\"Объект.\" + СтрСтр.Ключ + \" = Зн;\");")
                                ),
                                tryCatch(
                                        "Объект.Записать();",
                                        ppIfThen("Клиент", "Сообщить(ОписаниеОшибки());")
                                ),
                                block("_Об.Добавить(Объект);")
                        ),
                        _return("_Об")
                )
        );

    }

    private void defineStructureCreationProc(Module.ModuleBuilder mod, String name, Map<String, Object> struct) {

        mod.def(
                proc("СоздатьСтруктуру_" + name, args("Объект = Неопределено"), true, true,
                        structConstruct("Стркт", struct),
                        ifThen("Объект <> Неопределено",
                                ppIfThen("Клиент", "Сообщить(\"struct \" + Строка(Объект));"),
                                block(
                                        "Стркт._ИД = Строка(Объект.УникальныйИдентификатор());",
                                        "Стркт._Тип = Строка(Объект.Метаданные().ПолноеИмя());",
                                        "Стркт._Ссылка = СериализоватьСсылку(Объект);"
                                )
                        ),
                        _return("Стркт")
                )
        );

    }

    private void defineCreateTableSectionStructureCreationProc(Module.ModuleBuilder mod, String name, Map<String, Object> strct) {

        mod.def(
                proc("СоздатьСтруктуру_" + name, args("Объект = Неопределено"), true, true,
                        ifThen("Объект <> Неопределено",
                                ppIfThen("Клиент", "Сообщить(\"struct \" + Строка(Объект) + \"" + name + "\");"),
                                block("_Рез = Новый Массив;"),
                                forEach("СтрТаб", "Объект." + name,
                                        structConstruct("Стркт", strct),
                                        block(
                                                "ЗаполнитьЗначенияСвойств(Стркт, СтрТаб);",
                                                "Стркт._Владелец = СериализоватьСсылку(Объект.Ссылка);",
                                                "Стркт._Н = _ПрЗнч(СтрТаб.НомерСтроки);",
                                                "Стркт._Ссылка = СериализоватьСсылку(СтрТаб);",
                                                "Стркт._Имя = \"" + name + "\";",
                                                "_Рез.Добавить(Стркт);"
                                        )
                                )
                        ),
                        _return("_Рез")
                )
        );

    }

    private void defineExportToStructuresProc(Module.ModuleBuilder mod, String name, String qualName, HashMap<String, HashMap<String, Object>> tables) {

        mod.def(
                proc("ВыгрузитьВСтруктуры_" + name, args("Отбор = Неопределено"), true, true,
                        block("_Рез = Новый Массив;",
                                ("Выборка = " + qualName)
                                .replace("Справочник", "Справочники")
                                .replace("Документ", "Документы")
                                + ".Выбрать(,,);"),
                        whileLoop("Выборка.Следующий()",
                                ppIfThen("Клиент", "Сообщить(\"struct_unload \" + Строка(Выборка.Ссылка));"),
                                block("Стркт = СоздатьСтруктуру_"
                                        + name
                                        + "(Выборка.Ссылка);",
                                        "   _Рез.Добавить(Стркт);"),
                                block(
                                        tables.keySet().stream()
                                        .map(
                                                (itm) -> forEach("СтрСтр", "СоздатьСтруктуру_"
                                                        + name
                                                        + "_ТЧ_" + itm + "(Выборка.Ссылка)",
                                                        block("_Рез.Добавить(СтрСтр);")
                                                ).produce()
                                        ).reduce("", String::concat))
                        ),
                        _return("_Рез")
                ));
    }

    private void defineMainImportExportProcs(Module.ModuleBuilder mod) {

        mod.def(
                proc("Выгрузить", args(), true, true,
                        block(
                                "Зп = Новый ЗаписьJSON;",
                                "Зп.УстановитьСтроку();",
                                "_ММ = Новый Массив;"
                        ),
                        objects.stream()
                        .map((itm) -> {
                            return forEach("Эл", "ВыгрузитьВСтруктуры_" + itm + "()", "_ММ.Добавить(Эл);");
                        })
                        .reduce(block(""), (acc, itm) -> block(acc.produce(), itm.produce())),
                        block("СериализоватьМассивСтруктур(Зп, _ММ);"),
                        _return("Зп.Закрыть()")
                )
        );

        mod.def(
                proc("Загрузить", args("Текст"), true, true,
                        block(
                                "Чт = Новый ЧтениеJSON;",
                                "Чт.УстановитьСтроку(Текст);",
                                "_ММ = ДесереализоватьМассивСтруктур(Чт);",
                                "_Об = СоздатьОбъектыИзМассиваСтруктур(_ММ);"
                        ),
                        ppIfThen("Клиент", "Сообщить(\"Загружено объектов: \" + Строка(_Об.Количество()));")
                )
        );

    }

    private void processSubchildForDefines(Module.ModuleBuilder mod, Object subch, HashMap<String, HashMap<String, Object>> tables, HashMap<String, Object> struct) {
        if (subch instanceof MappingNode) {
            MappingNode subchild = (MappingNode) subch;
            if (subchild.getState() == NodeStateContainer.NodeState.Good
                    || subchild.getState() == NodeStateContainer.NodeState.Warning) {
                if (subchild.getInObject().getObjClass() == MetaObjectClass.TabularSection) {
                    HashMap<String, Object> tblStruct = new HashMap<>();
                    tblStruct.put("_Ссылка", null);
                    tblStruct.put("_Имя", null);
                    tblStruct.put("_Владелец", null);
                    tblStruct.put("_Н", null);
                    Collections.list(subchild.children()).forEach((tblPropCh) -> {
                        if (tblPropCh instanceof MappingNode) {
                            MappingNode tblPropNode = (MappingNode) tblPropCh;
                            if (tblPropNode.getState() == NodeStateContainer.NodeState.Good
                                    || tblPropNode.getState() == NodeStateContainer.NodeState.Warning) {
                                tblStruct.put(tblPropNode.getInObject().getName(), null);
                            }
                        }
                    });
                    tables.put(subchild.getInObject().getName(), tblStruct);
                } else {
                    struct.put(subchild.getInObject().getName(), null);
                }
            }
        }

    }

    private void processChildForDefines(Module.ModuleBuilder mod, MappingNode child) {
        HashMap<String, Object> struct = new HashMap<>();
        HashMap<String, HashMap<String, Object>> tables = new HashMap<String, HashMap<String, Object>>();
        struct.put("_ИД", null);
        struct.put("_Тип", null);
        struct.put("_Ссылка", null);
        Collections.list(child.children()).stream().forEach((subch) -> {
            processSubchildForDefines(mod, subch, tables, struct);
        });
        objects.add(child.getInObject().getFullName().replace(".", ""));

        defineStructureCreationProc(
                mod,
                child.getInObject().getFullName().replace(".", ""),
                struct
        );

        List<String> modAddings = new ArrayList<>();
        tables.forEach((name, strct) -> {

            defineCreateTableSectionStructureCreationProc(
                    mod,
                    child.getInObject().getFullName().replace(".", "") + "_ТЧ_" + name,
                    strct
            );

        });

        defineExportToStructuresProc(mod, child.getInObject().getFullName().replace(".", ""), child.getInObject().getFullName(), tables);

    }

    public void generate(MappingTreeModel model) throws ParseException, IOException, TemplateException {

        objects = new ArrayList<>();

        CodeTemplateProcessor tpl = new CodeTemplateProcessor();

        Module.ModuleBuilder mod = Module.__()
                .variablesSection(
                        comment(
                                "",
                                "Сгенерировано c1ExchangeGen",
                                " -----------------------------",
                                ""
                        ));

        defineUtilProcs(mod);
        defineRefsSerializationProcs(mod);
        defineArrayOfStructsSerializationProcs(mod);
        deficeCreateObjectsProc(mod);

        MappingNode root = (MappingNode) model.getRoot();

        Collections.list(root.children()).stream().forEach((ch) -> {
            MappingNode child = (MappingNode) ch;
            if (child.getState() == NodeStateContainer.NodeState.Good
                    || child.getState() == NodeStateContainer.NodeState.Warning) {
                processChildForDefines(mod, child);
            }
        });

        defineMainImportExportProcs(mod);

        module = mod.__().produce();

    }

    public String getModule() {
        return BSLFormatter.format(module);
    }

}
