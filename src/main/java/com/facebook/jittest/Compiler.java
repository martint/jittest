package com.facebook.jittest;

import com.facebook.presto.byteCode.Block;
import com.facebook.presto.byteCode.ClassDefinition;
import com.facebook.presto.byteCode.ClassInfoLoader;
import com.facebook.presto.byteCode.CompilerContext;
import com.facebook.presto.byteCode.DumpByteCodeVisitor;
import com.facebook.presto.byteCode.DynamicClassLoader;
import com.facebook.presto.byteCode.LocalVariableDefinition;
import com.facebook.presto.byteCode.MethodDefinition;
import com.facebook.presto.byteCode.ParameterizedType;
import com.facebook.presto.byteCode.SmartClassWriter;
import com.facebook.presto.byteCode.control.ForLoop;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.facebook.presto.byteCode.Access.FINAL;
import static com.facebook.presto.byteCode.Access.PUBLIC;
import static com.facebook.presto.byteCode.Access.a;
import static com.facebook.presto.byteCode.ParameterizedType.type;
import static com.facebook.presto.byteCode.ParameterizedType.typeFromPathName;

public class Compiler
{
    private static final AtomicLong CLASS_ID = new AtomicLong();

    private static final boolean DUMP_BYTE_CODE_TREE = false;
    private static final boolean DUMP_BYTE_CODE_RAW = false;
    private static final boolean RUN_ASM_VERIFIER = false; // verifier doesn't work right now
    private static final AtomicReference<String> DUMP_CLASS_FILES_TO = new AtomicReference<>();

    public Compiler()
    {
    }

    public Runnable compile()
    {
        DynamicClassLoader classLoader = createClassLoader();

        // class
        ClassDefinition classDefinition = new ClassDefinition(new CompilerContext(null),
                a(PUBLIC, FINAL),
                typeFromPathName("Runnable_" + CLASS_ID.incrementAndGet()),
                type(Object.class),
                type(Runnable.class));

        // constructor
        classDefinition.declareConstructor(new CompilerContext(null),
                a(PUBLIC))
                .getBody()
                .comment("super()")
                .pushThis()
                .invokeSpecial(Object.class, "<init>", void.class)
                .ret();

        // method
        MethodDefinition method = classDefinition.declareMethod(new CompilerContext(null),
                a(PUBLIC),
                "run",
                type(void.class));


        CompilerContext compilerContext = method.getCompilerContext();

        LocalVariableDefinition result = compilerContext.declareVariable(int.class, "result");
        LocalVariableDefinition i = compilerContext.declareVariable(int.class, "i");

        method.getBody()
                .comment("int result = 0;")
                .putVariable(result, 0);

        ForLoop forLoop = ForLoop.forLoopBuilder(compilerContext)
                .comment("for (i = 0; Util.belowLimit(i); i++)")
                .initialize(new Block(compilerContext).putVariable(i, 0))
                .condition(new Block(compilerContext)
                        .getVariable(i)
                        .invokeStatic(Util.class, "belowLimit", boolean.class, int.class))
                .update(new Block(compilerContext).incrementVariable(i, (byte) 1))
                .comment("result += Util.pow10(i)")
                .body(new Block(compilerContext)
                        .getVariable(i)
                        .invokeStatic(Util.class, "pow10", int.class, int.class)
                        .getVariable(result)
                        .invokeStatic(Util.class, "add", int.class, int.class, int.class)
                        .putVariable(result)
                )
                .build();

        method.getBody()
                .append(forLoop)
                .comment("Util.consume(result)")
                .getVariable(result)
                .invokeStatic(Util.class, "consume", void.class, int.class)
                .ret();

        Class<? extends Runnable> runnableClass = defineClasses(ImmutableList.of(classDefinition), classLoader).values().iterator().next().asSubclass(Runnable.class);

        try {
            return runnableClass.newInstance();
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private DynamicClassLoader createClassLoader()
    {
        return new DynamicClassLoader();
    }



    private static Map<String, Class<?>> defineClasses(List<ClassDefinition> classDefinitions, DynamicClassLoader classLoader)
    {
        ClassInfoLoader classInfoLoader = ClassInfoLoader.createClassInfoLoader(classDefinitions, classLoader);

        if (DUMP_BYTE_CODE_TREE) {
            DumpByteCodeVisitor dumpByteCode = new DumpByteCodeVisitor(System.out);
            for (ClassDefinition classDefinition : classDefinitions) {
                dumpByteCode.visitClass(classDefinition);
            }
        }

        Map<ParameterizedType, byte[]> byteCodes = new LinkedHashMap<>();
        for (ClassDefinition classDefinition : classDefinitions) {
            ClassWriter cw = new SmartClassWriter(classInfoLoader);
            classDefinition.visit(cw);
            byte[] byteCode = cw.toByteArray();
            if (RUN_ASM_VERIFIER) {
                ClassReader reader = new ClassReader(byteCode);
                CheckClassAdapter.verify(reader, classLoader, true, new PrintWriter(System.out));
            }
            byteCodes.put(classDefinition.getType(), byteCode);
        }

        String dumpClassPath = DUMP_CLASS_FILES_TO.get();
        if (dumpClassPath != null) {
            for (Map.Entry<ParameterizedType, byte[]> entry : byteCodes.entrySet()) {
                File file = new File(dumpClassPath, entry.getKey().getClassName() + ".class");
                try {
                    Files.createParentDirs(file);
                    Files.write(entry.getValue(), file);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (DUMP_BYTE_CODE_RAW) {
            for (byte[] byteCode : byteCodes.values()) {
                ClassReader classReader = new ClassReader(byteCode);
                classReader.accept(new TraceClassVisitor(new PrintWriter(System.err)), ClassReader.SKIP_FRAMES);
            }
        }
        return classLoader.defineClasses(byteCodes);
    }
}
