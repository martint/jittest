package com.facebook.presto.byteCode.instruction;

import com.google.common.collect.ImmutableList;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import com.facebook.presto.byteCode.ByteCodeNode;
import com.facebook.presto.byteCode.ByteCodeVisitor;
import com.facebook.presto.byteCode.MethodDefinition;
import com.facebook.presto.byteCode.OpCodes;
import com.facebook.presto.byteCode.ParameterizedType;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.collect.Iterables.transform;
import static com.facebook.presto.byteCode.MethodDefinition.methodDescription;
import static com.facebook.presto.byteCode.OpCodes.INVOKEDYNAMIC;
import static com.facebook.presto.byteCode.OpCodes.INVOKEINTERFACE;
import static com.facebook.presto.byteCode.OpCodes.INVOKESPECIAL;
import static com.facebook.presto.byteCode.OpCodes.INVOKESTATIC;
import static com.facebook.presto.byteCode.OpCodes.INVOKEVIRTUAL;
import static com.facebook.presto.byteCode.ParameterizedType.toParameterizedType;
import static com.facebook.presto.byteCode.ParameterizedType.type;

public class InvokeInstruction implements InstructionNode
{
    //
    // Invoke Static
    //

    public static InstructionNode invokeStatic(Method method)
    {
        return invoke(INVOKESTATIC, method);
    }

    public static InstructionNode invokeStatic(MethodDefinition method)
    {
        return invoke(INVOKESTATIC, method);
    }

    public static InstructionNode invokeStatic(Class<?> target, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        return invoke(INVOKESTATIC, target, name, returnType, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeStatic(Class<?> target, String name, Class<?> returnType, Iterable<Class<?>> parameterTypes)
    {
        return invoke(INVOKESTATIC, target, name, returnType, parameterTypes);
    }

    public static InstructionNode invokeStatic(ParameterizedType target, String name, ParameterizedType returnType, ParameterizedType... parameterTypes)
    {
        return invoke(INVOKESTATIC, target, name, returnType, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeStatic(ParameterizedType target, String name, ParameterizedType returnType, Iterable<ParameterizedType> parameterTypes)
    {
        return invoke(INVOKESTATIC, target, name, returnType, parameterTypes);
    }

    //
    // Invoke Virtual
    //

    public static InstructionNode invokeVirtual(Method method)
    {
        return invoke(INVOKEVIRTUAL, method);
    }

    public static InstructionNode invokeVirtual(MethodDefinition method)
    {
        return invoke(INVOKEVIRTUAL, method);
    }

    public static InstructionNode invokeVirtual(Class<?> target, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        return invoke(INVOKEVIRTUAL, target, name, returnType, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeVirtual(Class<?> target, String name, Class<?> returnType, Iterable<Class<?>> parameterTypes)
    {
        return invoke(INVOKEVIRTUAL, target, name, returnType, parameterTypes);
    }

    public static InstructionNode invokeVirtual(ParameterizedType target, String name, ParameterizedType returnType, ParameterizedType... parameterTypes)
    {
        return invoke(INVOKEVIRTUAL, target, name, returnType, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeVirtual(ParameterizedType target, String name, ParameterizedType returnType, Iterable<ParameterizedType> parameterTypes)
    {
        return invoke(INVOKEVIRTUAL, target, name, returnType, parameterTypes);
    }

    //
    // Invoke Interface
    //

    public static InstructionNode invokeInterface(Method method)
    {
        return invoke(INVOKEINTERFACE, method);
    }

    public static InstructionNode invokeInterface(MethodDefinition method)
    {
        return invoke(INVOKEINTERFACE, method);
    }

    public static InstructionNode invokeInterface(Class<?> target, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        return invoke(INVOKEINTERFACE, target, name, returnType, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeInterface(Class<?> target, String name, Class<?> returnType, Iterable<Class<?>> parameterTypes)
    {
        return invoke(INVOKEINTERFACE, target, name, returnType, parameterTypes);
    }

    public static InstructionNode invokeInterface(ParameterizedType target, String name, ParameterizedType returnType, ParameterizedType... parameterTypes)
    {
        return invoke(INVOKEINTERFACE, target, name, returnType, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeInterface(ParameterizedType target, String name, ParameterizedType returnType, Iterable<ParameterizedType> parameterTypes)
    {
        return invoke(INVOKEINTERFACE, target, name, returnType, parameterTypes);
    }

    //
    // Invoke Constructor
    //

    public static InstructionNode invokeConstructor(Constructor<?> constructor)
    {
        return invokeConstructor(constructor.getDeclaringClass(), constructor.getParameterTypes());
    }

    public static InstructionNode invokeConstructor(Class<?> target, Class<?>... parameterTypes)
    {
        return invokeConstructor(type(target), transform(ImmutableList.copyOf(parameterTypes), toParameterizedType()));
    }

    public static InstructionNode invokeConstructor(Class<?> target, Iterable<Class<?>> parameterTypes)
    {
        return invokeConstructor(type(target), transform(parameterTypes, toParameterizedType()));
    }

    public static InstructionNode invokeConstructor(ParameterizedType target, ParameterizedType... parameterTypes)
    {
        return invokeConstructor(target, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeConstructor(ParameterizedType target, Iterable<ParameterizedType> parameterTypes)
    {
        return invokeSpecial(target, "<init>", type(void.class), parameterTypes);
    }

    //
    // Invoke Special
    //

    public static InstructionNode invokeSpecial(Method method)
    {
        return invoke(INVOKESPECIAL, method);
    }

    public static InstructionNode invokeSpecial(MethodDefinition method)
    {
        return invoke(INVOKESPECIAL, method);
    }

    public static InstructionNode invokeSpecial(Class<?> target, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        return invoke(INVOKESPECIAL, target, name, returnType, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeSpecial(Class<?> target, String name, Class<?> returnType, Iterable<Class<?>> parameterTypes)
    {
        return invoke(INVOKESPECIAL, target, name, returnType, parameterTypes);
    }

    public static InstructionNode invokeSpecial(ParameterizedType target, String name, ParameterizedType returnType, ParameterizedType... parameterTypes)
    {
        return invoke(INVOKESPECIAL, target, name, returnType, ImmutableList.copyOf(parameterTypes));
    }

    public static InstructionNode invokeSpecial(ParameterizedType target, String name, ParameterizedType returnType, Iterable<ParameterizedType> parameterTypes)
    {
        return invoke(INVOKESPECIAL, target, name, returnType, parameterTypes);
    }

    //
    // Generic
    //

    private static InstructionNode invoke(OpCodes invocationType, Method method)
    {
        return new InvokeInstruction(invocationType,
                type(method.getDeclaringClass()),
                method.getName(),
                type(method.getReturnType()),
                transform(ImmutableList.copyOf(method.getParameterTypes()), toParameterizedType()));
    }

    private static InstructionNode invoke(OpCodes invocationType, MethodDefinition method)
    {
        return new InvokeInstruction(invocationType,
                method.getDeclaringClass().getType(),
                method.getName(),
                method.getReturnType(),
                method.getParameterTypes());
    }

    private static InstructionNode invoke(OpCodes invocationType, ParameterizedType target, String name, ParameterizedType returnType, Iterable<ParameterizedType> parameterTypes)
    {
        return new InvokeInstruction(invocationType,
                target,
                name,
                returnType,
                parameterTypes);
    }

    private static InstructionNode invoke(OpCodes invocationType, Class<?> target, String name, Class<?> returnType, Iterable<Class<?>> parameterTypes)
    {
        return new InvokeInstruction(invocationType,
                type(target),
                name,
                type(returnType),
                transform(parameterTypes, toParameterizedType()));
    }

    //
    // Invoke Dynamic
    //

    public static InstructionNode invokeDynamic(String name,
            ParameterizedType returnType,
            Iterable<ParameterizedType> parameterTypes,
            Method bootstrapMethod,
            Iterable<Object> bootstrapArguments)
    {
        return new InvokeDynamicInstruction(name,
                returnType,
                parameterTypes,
                bootstrapMethod,
                ImmutableList.copyOf(bootstrapArguments));
    }


    public static InstructionNode invokeDynamic(String name,
            ParameterizedType returnType,
            Iterable<ParameterizedType> parameterTypes,
            Method bootstrapMethod,
            Object... bootstrapArguments)
    {
        return new InvokeDynamicInstruction(name,
                returnType,
                parameterTypes,
                bootstrapMethod,
                ImmutableList.copyOf(bootstrapArguments));
    }

    public static InstructionNode invokeDynamic(String name,
            MethodType methodType,
            Method bootstrapMethod,
            Iterable<Object> bootstrapArguments)
    {
        return new InvokeDynamicInstruction(name,
                type(methodType.returnType()),
                transform(methodType.parameterList(), toParameterizedType()),
                bootstrapMethod,
                ImmutableList.copyOf(bootstrapArguments));
    }


    public static InstructionNode invokeDynamic(String name,
            MethodType methodType,
            Method bootstrapMethod,
            Object... bootstrapArguments)
    {
        return new InvokeDynamicInstruction(name,
                type(methodType.returnType()),
                transform(methodType.parameterList(), toParameterizedType()),
                bootstrapMethod,
                ImmutableList.copyOf(bootstrapArguments));
    }


    private final OpCodes opCode;
    private final ParameterizedType target;
    private final String name;
    private final ParameterizedType returnType;
    private final List<ParameterizedType> parameterTypes;

    public InvokeInstruction(OpCodes opCode,
            ParameterizedType target,
            String name,
            ParameterizedType returnType,
            Iterable<ParameterizedType> parameterTypes)
    {
        this.opCode = opCode;
        this.target = target;
        this.name = name;
        this.returnType = returnType;
        this.parameterTypes = ImmutableList.copyOf(parameterTypes);
    }

    public OpCodes getOpCode()
    {
        return opCode;
    }

    public ParameterizedType getTarget()
    {
        return target;
    }

    public String getName()
    {
        return name;
    }

    public ParameterizedType getReturnType()
    {
        return returnType;
    }

    public List<ParameterizedType> getParameterTypes()
    {
        return parameterTypes;
    }

    public String getMethodDescription()
    {
        return methodDescription(returnType, parameterTypes);
    }

    @Override
    public void accept(MethodVisitor visitor)
    {
        visitor.visitMethodInsn(opCode.getOpCode(), target.getClassName(), name, getMethodDescription());
    }

    @Override
    public List<ByteCodeNode> getChildNodes()
    {
        return ImmutableList.of();
    }

    @Override
    public <T> T accept(ByteCodeNode parent, ByteCodeVisitor<T> visitor)
    {
        return visitor.visitInvoke(parent, this);
    }

    public static class InvokeDynamicInstruction extends InvokeInstruction
    {
        private final Method bootstrapMethod;
        private final List<Object> bootstrapArguments;

        public InvokeDynamicInstruction(String name,
                ParameterizedType returnType,
                Iterable<ParameterizedType> parameterTypes,
                Method bootstrapMethod,
                List<Object> bootstrapArguments)
        {
            super(INVOKEDYNAMIC, null, name, returnType, parameterTypes);
            this.bootstrapMethod = bootstrapMethod;
            this.bootstrapArguments = ImmutableList.copyOf(bootstrapArguments);
        }

        @Override
        public void accept(MethodVisitor visitor)
        {
            Handle bootstrapMethodHandle = new Handle(Opcodes.H_INVOKESTATIC,
                    type(bootstrapMethod.getDeclaringClass()).getClassName(),
                    bootstrapMethod.getName(),
                    methodDescription(
                            bootstrapMethod.getReturnType(),
                            bootstrapMethod.getParameterTypes()));

            visitor.visitInvokeDynamicInsn(getName(),
                    getMethodDescription(),
                    bootstrapMethodHandle,
                    bootstrapArguments.toArray(new Object[bootstrapArguments.size()]));
        }

        public Method getBootstrapMethod()
        {
            return bootstrapMethod;
        }

        public List<Object> getBootstrapArguments()
        {
            return bootstrapArguments;
        }

        @Override
        public List<ByteCodeNode> getChildNodes()
        {
            return ImmutableList.of();
        }

        @Override
        public <T> T accept(ByteCodeNode parent, ByteCodeVisitor<T> visitor)
        {
            return visitor.visitInvokeDynamic(parent, this);
        }
    }
}
