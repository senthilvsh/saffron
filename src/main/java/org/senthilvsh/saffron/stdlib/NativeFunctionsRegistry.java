package org.senthilvsh.saffron.stdlib;

import org.senthilvsh.saffron.ast.NativeFunctionDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NativeFunctionsRegistry {
    public static Map<String, NativeFunctionDefinition> getAll() {
        List<NativeFunction> nativeFunctions = new ArrayList<>();
        nativeFunctions.add(new WriteLineNumber());
        nativeFunctions.add(new WriteLineString());
        nativeFunctions.add(new WriteLineBoolean());

        Map<String, NativeFunctionDefinition> definitions = new HashMap<>();

        for (NativeFunction nf : nativeFunctions) {
            String signature = nf.getName();
            String argTypes = nf.getArguments()
                    .stream()
                    .map(a -> a.getType().getName().toLowerCase())
                    .collect(Collectors.joining("_"));
            if (!argTypes.isEmpty()) {
                signature += ("_" + argTypes);
            }
            definitions.put(signature, new NativeFunctionDefinition(nf.getName(), nf.getArguments(), nf.getReturnType(), nf));
        }

        return definitions;
    }
}
