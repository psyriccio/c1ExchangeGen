/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.codegen;

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
import java.util.stream.Collectors;

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

        CodeGenerator cgen = new CodeGenerator();

        module = "\n" + "//Сгенерировано c1ExchangeGen" + "\n";

        module += cgen.proc(
                "СериализоватьСсылку",
                new String[]{"СсылкаЗнч"},
                true,
                true,
                new String[]{
                    "Возврат \"~~~@REF:\" + Строка(ТипЗнч(СсылкаЗнч)) + \"/\" + Строка(СсылкаЗнч.УникальныйИдентификатор());",
                    ""
                }
        ) + "\n";

        module += cgen.proc(
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
                    "   Возврат Строка(Значен);",
                    "КонецЕсли;",
                    ""
                }) + "\n";

        module += cgen.proc(
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
                    "   Для Каждого ЭлСтр Из Эл Цикл",
                    "       ЗаписьJSON.ЗаписатьИмяСвойства(ЭлСтр.Ключ);",
                    "       ЗаписьJSON.ЗаписатьЗначение(_ПрЗнч(ЭлСтр.Значение));",
                    "   КонецЦикла;",
                    "   ЗаписьJSON.ЗаписатьКонецОбъекта();",
                    "КонецЦикла;",
                    "ЗаписьJSON.ЗаписатьКонецМассива();",
                    ""
                }) + "\n";

        MappingNode root = (MappingNode) model.getRoot();

        Collections.list(root.children()).stream().forEach((ch) -> {
            MappingNode child = (MappingNode) ch;
            if (child.getState() == NodeStateContainer.NodeState.Good
                    || child.getState() == NodeStateContainer.NodeState.Warning) {
                HashMap<String, Object> struct = new HashMap<>();
                struct.put("_ИД", null);
                struct.put("_Тип", null);
                struct.put("_Ссылка", null);
                Collections.list(child.children()).stream().forEach((subch) -> {
                    if (subch instanceof MappingNode) {
                        MappingNode subchild = (MappingNode) subch;
                        if (subchild.getState() == NodeStateContainer.NodeState.Good) {
                            struct.put(subchild.getInObject().getName(), null);
                        }
                    }
                });
                try {

                    objects.add(child.getInObject().getFullName().replace(".", ""));

                    module += cgen.proc(
                            "СоздатьСтруктуру_"
                            + child.getInObject().getFullName().replace(".", ""),
                            new String[]{"Объект = Неопределено"},
                            true, true, new String[]{
                                cgen.structConstruct("Стркт", struct),
                                "Если Объект <> Неопределено Тогда",
                                "   ЗаполнитьЗначенияСвойств(Стркт, Объект);",
                                "   Стркт._ИД = Строка(Объект.УникальныйИдентификатор());",
                                "   Стркт._Тип = Строка(ТипЗнч(Объект));",
                                "   Стркт._Ссылка = СериализоватьСсылку(Объект);",
                                "КонецЕсли;",
                                "",
                                "Возврат Стркт;",
                                ""
                            });
                    module += "\n";

                    module += cgen.proc(
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
                                "   Стркт = СоздатьСтруктуру_"
                                + child.getInObject().getFullName().replace(".", "")
                                + "(Выборка.Ссылка);",
                                "   _Рез.Добавить(Стркт);",
                                "КонецЦикла;",
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

        module += cgen.proc(
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
                        return 
                                "\n\tДля Каждого Эл Из ВыгрузитьВСтруктуры_" + itm + "() Цикл\n" 
                                + "\t\t_ММ.Добавить(Эл);\n" + ""
                                + "\tКонецЦикла;\n"; })
                    .reduce("", String::concat),
                "СериализоватьМассивСтруктур(Зп, _ММ);",
                "Возврат Зп.Закрыть();",
                ""
            }) + "\n";

    }

    public String getModule() {
        return module;
    }

}
