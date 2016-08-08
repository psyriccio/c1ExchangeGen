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
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author psyriccio
 */
public class ExchangeModuleGenerator {

    private String module;

    public ExchangeModuleGenerator() {
    }

    public void generate(MappingTreeModel model) throws ParseException, IOException, TemplateException {

        
        CodeGenerator cgen = new CodeGenerator();

        module = "\n" + "//Сгенерировано c1ExchangeGen" + "\n";

        module += cgen.proc(
                "СериализоватьСсылку", 
                new String[]{"СсылкаЗнч"}, 
                true, 
                true, 
                new String[]{
                    "Возврат \"~~~@REF:\" + Строка(ТипЗнч(СсылкаЗнч)) + \"/\" + Строка(СсылкаЗнч.УникальныйИдентификатор());"
                }
        );
        
        MappingNode root = (MappingNode) model.getRoot();

        Collections.list(root.children()).stream().forEach((ch) -> {
            MappingNode child = (MappingNode) ch;
            if (child.getState() == NodeStateContainer.NodeState.Good
                    || child.getState() == NodeStateContainer.NodeState.Warning) {
                HashMap<String, Object> struct = new HashMap<>();
                struct.put("_ИД", null);
                Collections.list(child.children()).stream().forEach((subch) -> {
                    if (subch instanceof MappingNode) {
                        MappingNode subchild = (MappingNode) subch;
                        if (subchild.getState() == NodeStateContainer.NodeState.Good) {
                            struct.put(subchild.getInObject().getName(), null);
                        }
                    }
                });
                try {
                    module += cgen.proc(
                            "СоздатьСтруктуру_"
                            + child.getInObject().getFullName().replace(".", ""),
                            new String[]{ "Объект = Неопределено" }, 
                            true, true, new String[]{
                                cgen.structConstruct("Стркт", struct),
                                "Если Объект <> Неопределено Тогда",
                                "   ЗаполнитьЗначенияСвойств(Стркт, Объект);",
                                "   Стркт._ИД = Строка(Объект.УникальныйИдентификатор());",
                                "КонецЕсли;",
                                "",
                                "Возврат Стркт;",
                                ""
                            });
                    module += "\n";

                } catch (TemplateException | IOException ex) {
                    Logger.getLogger(ExchangeModuleGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public String getModule() {
        return module;
    }

}
