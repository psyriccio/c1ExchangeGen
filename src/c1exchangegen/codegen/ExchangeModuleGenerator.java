/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.codegen;

import c1c.bsl.BSLFormatter;
import c1c.bsl.gen.CodeTemplateProcessor;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author psyriccio
 */
public class ExchangeModuleGenerator {

    private String module;
    private List<String> objects;

    public ExchangeModuleGenerator() {
    }

    public void generate(MappingTreeModel model) throws ParseException, IOException, TemplateException {

        objects = new ArrayList<>();

        CodeTemplateProcessor tpl = new CodeTemplateProcessor();

        module = "\n" + "// Сгенерировано c1ExchangeGen" + "\n"
                + "// -----------------------------\n\n";

        module += tpl.proc(
                "СериализоватьСсылку",
                new String[]{"СсылкаЗнч"},
                true,
                true,
                new String[]{
                    "Если Найти(Строка(ТипЗнч(СсылкаЗнч)), \"Перечисление.\") <> 0 Тогда ",
                    "   Возврат \"~~~@REF:\" + Строка(СсылкаЗнч.Метаданные().ПолноеИмя());",
                    "ИначеЕсли Найти(Строка(ТипЗнч(СсылкаЗнч)), \"СправочникТабличнаяЧастьСтрока\") <> 0 Тогда",
                    "   Возврат \"~~~@REF:\" + \"СтрокаТЧ\"  + \"/\" + Строка(СсылкаЗнч);",
                    "ИначеЕсли Найти(Строка(ТипЗнч(СсылкаЗнч)) ,\"ДокументТабличнаяЧастьСтрока\") <> 0 Тогда",
                    "   Возврат \"~~~@REF:\" + \"СтрокаТЧ\"  + \"/\" + Строка(СсылкаЗнч);",
                    "Иначе",
                    "   Возврат \"~~~@REF:\" + Строка(СсылкаЗнч.Метаданные().ПолноеИмя()) + \"/\" + Строка(СсылкаЗнч.УникальныйИдентификатор());",
                    "КонецЕсли;",
                    ""
                }
        ) + "\n";

        module += tpl.proc(
                "СтрСплит",
                new String[]{
                    "Стр", "Разд"
                },
                true,
                true, new String[]{
                    "Рез = Новый Массив;",
                    "Ост = Стр;",
                    "Пока Ост <> \"\" Цикл",
                    "	ПР = Найти(Ост, Разд);",
                    "	Если ПР = 0 Тогда",
                    "		Рез.Добавить(Ост);",
                    "		Ост = \"\";",
                    "	Иначе",
                    "		Нк = Лев(Ост, ПР-1);",
                    "		Рез.Добавить(Нк);",
                    "		Ост = Прав(Ост, СтрДлина(Ост) - (ПР + СтрДлина(Разд) - 1));",
                    "	КонецЕсли;",
                    "КонецЦикла;",
                    "",
                    "Возврат Рез;",
                    ""
                }
        ) + "\n";

        module += tpl.proc(
                "ДесереализоватьСсылку",
                new String[]{
                    "Стрк"
                },
                true,
                true,
                new String[]{
                    "Чст = СтрСплит(Стрк, \"@REF:\");",
                    "Если Чст.Количество() = 2 Тогда",
                    "   Если Чст[0] = \"~~~\" Тогда",
                    "       Пр = СтрСплит(Чст[1], \"/\");",
                    "       Имена = СтрСплит(Пр[0], \".\");",
                    "       Если Имена[0] = \"Справочник\" Тогда",
                    "           Возврат Справочники[Имена[1]].ПолучитьСсылку(Новый УникальныйИдентификатор(Пр[1]));",
                    "       КонецЕсли;",
                    "       Если Имена[0] = \"Документ\" Тогда",
                    "           Возврат Документы[Имена[1]].ПолучитьСсылку(Новый УникальныйИдентификатор(Пр[1]));",
                    "       КонецЕсли;",
                    "       Если Имена[0] = \"Перечисление\" Тогда",
                    "           Для Каждого ПерЗн Из Перечисления[Имена[1]] Цикл",
                    "               Если ПерЗн.Метаданные().Имя = Пр[1] Тогда",
                    "                   Возврат ПерЗн;",
                    "               КонецЕсли;",
                    "           КонецЦикла;",
                    "           Возврат Перечисления[Имена[1]].ПустаяСсылка();",
                    "       КонецЕсли;",
                    "   КонецЕсли;",
                    "КонецЕсли;",
                    "",
                    "Возврат Неопределено;",
                    ""
                }) + "\n";

        module += tpl.proc(
                "_ПрЗнч",
                new String[]{
                    "Значен"
                },
                true,
                true,
                new String[]{
                    "Если Справочники.ТипВсеСсылки().СодержитТип(ТипЗнч(Значен)) \n"
                    + "     Или Документы.ТипВсеСсылки().СодержитТип(ТипЗнч(Значен)) Тогда",
                    "   Возврат СериализоватьСсылку(Значен);",
                    "Иначе",
                    "   Возврат Формат(Значен, \"ЧРД=.; ЧРГ=' '; ЧГ=0; ДФ=yyyyMMddHHmmss; БЛ=Ложь; БИ=Истина\");",
                    "КонецЕсли;",
                    ""
                }) + "\n";

        module += tpl.proc(
                "СериализоватьМассивСтруктур",
                new String[]{
                    "ЗаписьJSON",
                    "Мас"
                },
                true,
                false,
                new String[]{
                    "ЗаписьJSON.ЗаписатьНачалоМассива();",
                    "Для Каждого Эл Из Мас Цикл",
                    "   ЗаписьJSON.ЗаписатьНачалоОбъекта();",
                    "   #Если Клиент Тогда",
                    "       Сообщить(\"json_export \" + Строка(Эл._Ссылка));",
                    "   #КонецЕсли",
                    "   Для Каждого ЭлСтр Из Эл Цикл",
                    "     #Если Клиент Тогда",
                    "       Сообщить(\"json_export_field \" + Строка(ЭлСтр.Ключ) + \" = \" + Строка(ЭлСтр.Значение));",
                    "     #КонецЕсли",
                    "       ЗаписьJSON.ЗаписатьИмяСвойства(ЭлСтр.Ключ);",
                    "       ЗаписьJSON.ЗаписатьЗначение(_ПрЗнч(ЭлСтр.Значение));",
                    "   КонецЦикла;",
                    "   ЗаписьJSON.ЗаписатьКонецОбъекта();",
                    "КонецЦикла;",
                    "ЗаписьJSON.ЗаписатьКонецМассива();",
                    ""
                }) + "\n";

        module += tpl.proc(
                "ДесереализоватьМассивСтруктур",
                new String[]{
                    "ЧтениеJSON"
                },
                true,
                true,
                new String[]{
                    "_ММ = Новый Массив;",
                    "Структ = Неопределено;",
                    "ИмяСв = \"\";",
                    "Пока ЧтениеJSON.Прочитать() Цикл",
                    "   Если ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.НачалоОбъекта Тогда",
                    "       Структ = Новый Структура;",
                    "   КонецЕсли;",
                    "   Если ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.КонецОбъекта Тогда",
                    "       #Если Клиент Тогда",
                    "           Сообщить(\"json_import \" + Структ._Ссылка);",
                    "       #КонецЕсли",
                    "       Структ._Ссылка = ДесереализоватьСсылку(Структ._Ссылка);",
                    "       _ММ.Добавить(Структ);",
                    "   КонецЕсли;",
                    "   Если ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.ИмяСвойства Тогда",
                    "       ИмяСв = ЧтениеJSON.ТекущееЗначение;",
                    "   КонецЕсли;",
                    "   Если ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.Строка \n"
                    + "\t\t\tИли ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.Число \n"
                    + "\t\t\tИли ЧтениеJSON.ТипТекущегоЗначения = ТипЗначенияJSON.Булево Тогда\n",
                    "       Структ.Вставить(ИмяСв, ЧтениеJSON.ТекущееЗначение);",
                    "   КонецЕсли;",
                    "КонецЦикла;",
                    "",
                    "Возврат _ММ;",
                    ""
                }) + "\n";

        module += tpl.proc(
                "СоздатьОбъектыИзМассиваСтруктур",
                new String[]{"Мас"},
                true,
                true,
                new String[]{
                    "_Об = Новый Массив;",
                    "Для Каждого Струк Из Мас Цикл",
                    "   #Если Клиент Тогда",
                    "       ОбработкаПрерыванияПользователя();",
                    "       Сообщить(\"construct \" + Струк._Тип + \"/\" + Струк._ИД);",
                    "   #КонецЕсли",
                    "   Объект = Струк._Ссылка.ПолучитьОбъект();",
                    "   Если Объект = Неопределено Тогда",
                    "       Имена = СтрСплит(Струк._Тип, \".\");",
                    "       Если Имена[0] = \"Справочник\" Тогда",
                    "           ЭтоГруппа = Ложь;",
                    "           Струк.Свойство(\"ЭтоГруппа\", ЭтоГруппа);",
                    "           Если ЭтоГруппа = Неопределено Тогда",
                    "               ЭтоГруппа = Ложь;",
                    "           КонецЕсли;",
                    "           Если ЭтоГруппа Тогда",
                    "               Объект = Справочники[Имена[1]].СоздатьГруппу();",
                    "           Иначе",
                    "               Объект = Справочники[Имена[1]].СоздатьЭлемент();",
                    "           КонецЕсли;",
                    "       КонецЕсли;",
                    "       Если Имена[0] = \"Документ\" Тогда",
                    "           Объект = Документы[Имена[1]].СоздатьДокумент();",
                    "       КонецЕсли;",
                    "       Объект.УстановитьСсылкуНового(Струк._Ссылка);",
                    "   КонецЕсли;",
                    "   Для Каждого СтрСтр Из Струк Цикл",
                    "       Если Лев(СтрСтр.Ключ, 1) = \"_\" Тогда",
                    "           Продолжить;",
                    "       КонецЕсли;",
                    "       ТипЗн = Неопределено;",
                    "       Зн = Неопределено;",
                    "       Попытка \n"
                    + "\t\t\tВыполнить(\"ТипЗн = ТипЗнч(Объект.\"+ СтрСтр.Ключ +\")\"); \n"
                    + "\t\tИсключение \n"
                    + "\t\t\t#Если Клиент Тогда\n"
                    + "\t\t\t\tСообщить(ОписаниеОшибки());\n"
                    + "\t\t\t#КонецЕсли\n"
                    + "\t\tКонецПопытки;\n",
                    "       Если ТипЗн <> Неопределено Тогда",
                    "           Если Справочники.ТипВсеСсылки().СодержитТип(ТипЗн) \n"
                    + "\t\t\t\tИли Документы.ТипВсеСсылки().СодержитТип(ТипЗн) Тогда",
                    "               Зн = ДесереализоватьСсылку(СтрСтр.Значение);",
                    "           КонецЕсли;",
                    "           Если ТипЗн = Тип(\"Строка\") Тогда",
                    "               Зн = Строка(СтрСтр.Значение);",
                    "           КонецЕсли;",
                    "           Если ТипЗн = Тип(\"Число\") Тогда",
                    "               Попытка Зн = Число(СтрСтр.Значение); Исключение Зн = 0 КонецПопытки;",
                    "           КонецЕсли;",
                    "           Если ТипЗн = Тип(\"Булево\") Тогда",
                    "               Зн = (Строка(СтрСтр.Значение) = \"Истина\");",
                    "           КонецЕсли;",
                    "           Если ТипЗн = Тип(\"Дата\") Тогда",
                    "               Попытка",
                    "                   Зн = Дата(Строка(СтрСтр.Значение));",
                    "               Исключение",
                    "               КонецПопытки;",
                    "           КонецЕсли;",
                    "       КонецЕсли;",
                    "       Попытка",
                    "           Выполнить(\"Объект.\" + СтрСтр.Ключ + \" = Зн;\");",
                    "       Исключение КонецПопытки;",
                    "   КонецЦикла;",
                    "   Попытка",
                    "       Объект.Записать();",
                    "   Исключение",
                    "       #Если Клиент Тогда",
                    "           Сообщить(ОписаниеОшибки());",
                    "       #КонецЕсли",
                    "   КонецПопытки;",
                    "   _Об.Добавить(Объект);",
                    "КонецЦикла;",
                    "",
                    "Возврат _Об;",
                    ""
                }) + "\n";

        MappingNode root = (MappingNode) model.getRoot();

        Collections.list(root.children()).stream().forEach((ch) -> {
            MappingNode child = (MappingNode) ch;
            if (child.getState() == NodeStateContainer.NodeState.Good
                    || child.getState() == NodeStateContainer.NodeState.Warning) {
                HashMap<String, Object> struct = new HashMap<>();
                HashMap<String, HashMap<String, Object>> tables = new HashMap<String, HashMap<String, Object>>();
                struct.put("_ИД", null);
                struct.put("_Тип", null);
                struct.put("_Ссылка", null);
                Collections.list(child.children()).stream().forEach((subch) -> {
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
                });
                try {

                    objects.add(child.getInObject().getFullName().replace(".", ""));

                    module += tpl.proc("СоздатьСтруктуру_"
                            + child.getInObject().getFullName().replace(".", ""),
                            new String[]{"Объект = Неопределено"},
                            true, true, new String[]{
                                tpl.structConstruct("Стркт", struct),
                                "Если Объект <> Неопределено Тогда",
                                "   #Если Клиент Тогда",
                                "       Сообщить(\"struct \" + Строка(Объект));",
                                "   #КонецЕсли",
                                "   ЗаполнитьЗначенияСвойств(Стркт, Объект);",
                                "   Стркт._ИД = Строка(Объект.УникальныйИдентификатор());",
                                "   Стркт._Тип = Строка(Объект.Метаданные().ПолноеИмя());",
                                "   Стркт._Ссылка = СериализоватьСсылку(Объект);",
                                "КонецЕсли;",
                                "",
                                "Возврат Стркт;",
                                ""
                            });
                    module += "\n";

                    List<String> modAddings = new ArrayList<>();
                    tables.forEach((name, strct) -> {

                        try {
                            modAddings.add(tpl.proc("СоздатьСтруктуру_" + child.getInObject().getFullName().replace(".", "") + "_ТЧ_" + name,
                                    new String[]{"Объект = Неопределено"},
                                    true, true, new String[]{
                                        "Если Объект <> Неопределено Тогда",
                                        "   #Если Клиент Тогда",
                                        "       Сообщить(\"struct \" + Строка(Объект) + \"" + name + "\");",
                                        "   #КонецЕсли",
                                        "   _Рез = Новый Массив;",
                                        "   Для Каждого СтрТаб Из Объект." + name + " Цикл",
                                        "\t" + tpl.structConstruct("Стркт", strct),
                                        "      ЗаполнитьЗначенияСвойств(Стркт, СтрТаб);",
                                        "      Стркт._Владелец = СериализоватьСсылку(Объект.Ссылка);",
                                        "      Стркт._Н = _ПрЗнч(СтрТаб.НомерСтроки);",
                                        "      Стркт._Имя = \"" + name + "\";",
                                        "       _Рез.Добавить(Стркт);",
                                        "   КонецЦикла;",
                                        "КонецЕсли;",
                                        "",
                                        "Возврат _Рез;",
                                        ""
                                    }) + "\n"
                            );
                        } catch (TemplateException | IOException ex) {
                            Logger.getLogger(ExchangeModuleGenerator.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    });

                    for(String mad : modAddings) {
                        module += mad;
                    }
                    
                    module += tpl.proc(
                            "ВыгрузитьВСтруктуры_"
                            + child.getInObject().getFullName().replace(".", ""),
                            new String[]{
                                "Отбор = Неопределено"
                            },
                            true,
                            true,
                            new String[]{
                                "_Рез = Новый Массив;",
                                "Выборка = " + child.getInObject().getFullName()
                                .replace("Справочник", "Справочники")
                                .replace("Документ", "Документы")
                                + ".Выбрать(,,);",
                                "Пока Выборка.Следующий() Цикл",
                                "   #Если Клиент Тогда",
                                "       Сообщить(\"struct_unload \" + Строка(Выборка.Ссылка));",
                                "   #КонецЕсли",
                                "   Стркт = СоздатьСтруктуру_"
                                + child.getInObject().getFullName().replace(".", "")
                                + "(Выборка.Ссылка);",
                                "   _Рез.Добавить(Стркт);",
                                tables.keySet().stream()
                                        .map(
                                                (itm) -> "\t\tДля Каждого СтрСтр Из СоздатьСтруктуру_" 
                                                        + child.getInObject().getFullName().replace(".", "") 
                                                        + "_ТЧ_" + itm + "(Выборка.Ссылка) Цикл\n"
                                                        + "\t\t\t_Рез.Добавить(СтрСтр);\n"
                                                        + "\t\tКонецЦикла;\n\n")
                                    .reduce("\n", String::concat) +
                                "\t\tКонецЦикла;",
                                "",
                                "Возврат _Рез;",
                                ""
                            }
                    ) + "\n";

                } catch (TemplateException | IOException ex) {
                    Logger.getLogger(ExchangeModuleGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        module += tpl.proc(
                "Выгрузить",
                new String[]{},
                true,
                true,
                new String[]{
                    "Зп = Новый ЗаписьJSON;",
                    "Зп.УстановитьСтроку();",
                    "_ММ = Новый Массив;",
                    objects.stream()
                    .map((itm) -> {
                        return "\n\tДля Каждого Эл Из ВыгрузитьВСтруктуры_" + itm + "() Цикл\n"
                                + "\t\t_ММ.Добавить(Эл);\n" + ""
                                + "\tКонецЦикла;\n";
                    })
                    .reduce("", String::concat),
                    "СериализоватьМассивСтруктур(Зп, _ММ);",
                    "Возврат Зп.Закрыть();",
                    ""
                }) + "\n";

        module += tpl.proc(
                "Загрузить",
                new String[]{
                    "Текст"
                },
                true,
                true,
                new String[]{
                    "Чт = Новый ЧтениеJSON;",
                    "Чт.УстановитьСтроку(Текст);",
                    "_ММ = ДесереализоватьМассивСтруктур(Чт);",
                    "_Об = СоздатьОбъектыИзМассиваСтруктур(_ММ);",
                    "#Если Клиент Тогда",
                    "   Сообщить(\"Загружено объектов: \" + Строка(_Об.Количество()));",
                    "#КонецЕсли",
                    ""
                }) + "\n";

    }

    public String getModule() {
        return BSLFormatter.format(module);
    }

}
