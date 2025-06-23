package org.senthilvsh.saffron.stdlib;

import org.senthilvsh.saffron.ast.NativeFunctionDefinition;
import org.senthilvsh.saffron.stdlib.console.ReadLine;
import org.senthilvsh.saffron.stdlib.console.Write;
import org.senthilvsh.saffron.stdlib.console.WriteNL;
import org.senthilvsh.saffron.stdlib.conversion.BooleanToString;
import org.senthilvsh.saffron.stdlib.conversion.NumberToString;
import org.senthilvsh.saffron.stdlib.conversion.StringToBoolean;
import org.senthilvsh.saffron.stdlib.conversion.StringToNumber;
import org.senthilvsh.saffron.stdlib.string.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeFunctionsRegistry {
    public static Map<String, NativeFunctionDefinition> getAll() {
        List<NativeFunction> nativeFunctions = new ArrayList<>();

        nativeFunctions.add(new Write());
        nativeFunctions.add(new WriteNL());
        nativeFunctions.add(new ReadLine());

        nativeFunctions.add(new StringLength());
        nativeFunctions.add(new StringSubString());
        nativeFunctions.add(new StringReplace());
        nativeFunctions.add(new StringTrim());
        nativeFunctions.add(new StringContains());
        nativeFunctions.add(new StringStartsWith());
        nativeFunctions.add(new StringEndsWith());

        nativeFunctions.add(new StringToNumber());
        nativeFunctions.add(new StringToBoolean());
        nativeFunctions.add(new NumberToString());
        nativeFunctions.add(new BooleanToString());

        Map<String, NativeFunctionDefinition> definitions = new HashMap<>();

        for (NativeFunction nf : nativeFunctions) {
            definitions.put(nf.getName(), new NativeFunctionDefinition(nf.getName(), nf.getArguments(), nf));
        }

        return definitions;
    }
}
