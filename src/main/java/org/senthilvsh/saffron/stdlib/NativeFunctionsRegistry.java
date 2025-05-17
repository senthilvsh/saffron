package org.senthilvsh.saffron.stdlib;

import org.senthilvsh.saffron.ast.NativeFunctionDefinition;
import org.senthilvsh.saffron.stdlib.console.*;
import org.senthilvsh.saffron.stdlib.string.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NativeFunctionsRegistry {
    public static Map<String, NativeFunctionDefinition> getAll() {
        List<NativeFunction> nativeFunctions = new ArrayList<>();

        nativeFunctions.add(new WriteNumber());
        nativeFunctions.add(new WriteString());
        nativeFunctions.add(new WriteBoolean());

        nativeFunctions.add(new WriteNumberNL());
        nativeFunctions.add(new WriteStringNL());
        nativeFunctions.add(new WriteBooleanNL());

        nativeFunctions.add(new ReadLine());

        nativeFunctions.add(new StringLength());
        nativeFunctions.add(new StringSubString());
        nativeFunctions.add(new StringSubStringToEnd());
        nativeFunctions.add(new StringReplace());
        nativeFunctions.add(new StringTrim());
        nativeFunctions.add(new StringContains());
        nativeFunctions.add(new StringStartsWith());
        nativeFunctions.add(new StringEndsWith());

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
