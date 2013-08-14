package com.facebook.presto.byteCode.instruction;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.objectweb.asm.MethodVisitor;
import com.facebook.presto.byteCode.ByteCodeNode;
import com.facebook.presto.byteCode.ByteCodeVisitor;
import com.facebook.presto.byteCode.OpCodes;
import com.facebook.presto.byteCode.ParameterizedType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static com.facebook.presto.byteCode.OpCodes.*;
import static com.facebook.presto.byteCode.ParameterizedType.type;

public abstract class FieldInstruction implements InstructionNode
{
    public static FieldInstruction getFieldInstruction(Field field)
    {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        return new GetFieldInstruction(isStatic, type(field.getDeclaringClass()), field.getName(), type(field.getType()));
    }

    public static FieldInstruction putFieldInstruction(Field field)
    {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        return new PutFieldInstruction(isStatic, type(field.getDeclaringClass()), field.getName(), type(field.getType()));
    }

    public static FieldInstruction getFieldInstruction(ParameterizedType classType, String fieldName, ParameterizedType fieldType)
    {
        return new GetFieldInstruction(false, classType, fieldName, fieldType);
    }

    public static FieldInstruction getFieldInstruction(Class<?> classType, String fieldName, Class<?> fieldType)
    {
        return new GetFieldInstruction(false, classType, fieldName, fieldType);
    }

    public static FieldInstruction putFieldInstruction(ParameterizedType classType, String fieldName, ParameterizedType fieldType)
    {
        return new PutFieldInstruction(false, classType, fieldName, fieldType);
    }

    public static FieldInstruction putFieldInstruction(Class<?> classType, String fieldName, Class<?> fieldType)
    {
        return new PutFieldInstruction(false, classType, fieldName, fieldType);
    }

    public static FieldInstruction getStaticInstruction(ParameterizedType classType, String fieldName, ParameterizedType fieldType)
    {
        return new GetFieldInstruction(true, classType, fieldName, fieldType);
    }

    public static FieldInstruction getStaticInstruction(Class<?> classType, String fieldName, Class<?> fieldType)
    {
        return new GetFieldInstruction(true, classType, fieldName, fieldType);
    }

    public static FieldInstruction putStaticInstruction(ParameterizedType classType, String fieldName, ParameterizedType fieldType)
    {
        return new PutFieldInstruction(true, classType, fieldName, fieldType);
    }

    public static FieldInstruction putStaticInstruction(Class<?> classType, String fieldName, Class<?> fieldType)
    {
        return new PutFieldInstruction(true, classType, fieldName, fieldType);
    }

    private final boolean isStatic;

    private final OpCodes opCode;

    private final ParameterizedType classType;

    private final String fieldName;

    private final ParameterizedType fieldType;

    private FieldInstruction(boolean isStatic, OpCodes opCode, ParameterizedType classType, String fieldName, ParameterizedType fieldType)
    {
        this.isStatic = isStatic;
        this.opCode = opCode;
        this.classType = classType;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public void accept(MethodVisitor visitor)
    {
        visitor.visitFieldInsn(opCode.getOpCode(), classType.getClassName(), fieldName, fieldType.getType());
    }

    @Override
    public List<ByteCodeNode> getChildNodes()
    {
        return ImmutableList.of();
    }

    @Override
    public <T> T accept(ByteCodeNode parent, ByteCodeVisitor<T> visitor)
    {
        return visitor.visitFieldInstruction(parent, this);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("isStatic", isStatic)
                .add("classType", classType)
                .add("fieldName", fieldName)
                .add("fieldType", fieldType)
                .toString();
    }

    public static class GetFieldInstruction extends FieldInstruction
    {
        public GetFieldInstruction(boolean isStatic, ParameterizedType classType, String fieldName, ParameterizedType fieldType)
        {
            super(isStatic, isStatic ? GETSTATIC : GETFIELD, classType, fieldName, fieldType);
        }

        public GetFieldInstruction(boolean isStatic, Class<?> classType, String fieldName, Class<?> fieldType)
        {
            super(isStatic, isStatic ? GETSTATIC : GETFIELD, type(classType), fieldName, type(fieldType));
        }

        @Override
        public <T> T accept(ByteCodeNode parent, ByteCodeVisitor<T> visitor)
        {
            return visitor.visitGetField(parent, this);
        }
    }

    public static class PutFieldInstruction extends FieldInstruction
    {
        public PutFieldInstruction(boolean isStatic, ParameterizedType classType, String fieldName, ParameterizedType fieldType)
        {
            super(isStatic, isStatic ? PUTSTATIC : PUTFIELD, classType, fieldName, fieldType);
        }

        public PutFieldInstruction(boolean isStatic, Class<?> classType, String fieldName, Class<?> fieldType)
        {
            super(isStatic, isStatic ? PUTSTATIC : PUTFIELD, type(classType), fieldName, type(fieldType));
        }

        @Override
        public <T> T accept(ByteCodeNode parent, ByteCodeVisitor<T> visitor)
        {
            return visitor.visitPutField(parent, this);
        }
    }
}
