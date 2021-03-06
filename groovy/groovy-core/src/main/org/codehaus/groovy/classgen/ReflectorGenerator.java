/*
 $Id$

 Copyright 2003 (C) James Strachan and Bob Mcwhirter. All Rights Reserved.

 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
    statements and notices.  Redistributions must also contain a
    copy of this document.

 2. Redistributions in binary form must reproduce the
    above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other
    materials provided with the distribution.

 3. The name "groovy" must not be used to endorse or promote
    products derived from this Software without prior written
    permission of The Codehaus.  For written permission,
    please contact info@codehaus.org.

 4. Products derived from this Software may not be called "groovy"
    nor may "groovy" appear in their names without prior written
    permission of The Codehaus. "groovy" is a registered
    trademark of The Codehaus.

 5. Due credit should be given to The Codehaus -
    http://groovy.codehaus.org/

 THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.codehaus.groovy.classgen;

import groovy.lang.MetaMethod;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;

/**
 * Code generates a Reflector 
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class ReflectorGenerator implements Opcodes {

    private List methods;
    private ClassVisitor cw;
    private MethodVisitor cv;
    private BytecodeHelper helper = new BytecodeHelper(null);
    private String classInternalName;

    public ReflectorGenerator(List methods) {
        this.methods = methods;
    }

    public void generate(ClassVisitor cw, String className) {
        this.cw = cw;

        classInternalName = BytecodeHelper.getClassInternalName(className);
        cw.visit(ClassGenerator.asmJDKVersion, ACC_PUBLIC + ACC_SUPER, classInternalName, (String)null, "org/codehaus/groovy/runtime/Reflector", null);

        cv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitMethodInsn(INVOKESPECIAL, "org/codehaus/groovy/runtime/Reflector", "<init>", "()V");
        cv.visitInsn(RETURN);
        cv.visitMaxs(1, 1);

        generateInvokeMethod();

        cw.visitEnd();
    }

    protected void generateInvokeMethod() {
        int methodCount = methods.size();

        cv =
            cw.visitMethod(
                ACC_PUBLIC,
                "invoke",
                "(Lgroovy/lang/MetaMethod;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null);
        helper = new BytecodeHelper(cv);

        cv.visitVarInsn(ALOAD, 1);
        cv.visitMethodInsn(INVOKEVIRTUAL, "groovy/lang/MetaMethod", "getMethodIndex", "()I");
        Label defaultLabel = new Label();
        Label[] labels = new Label[methodCount];
        int[] indices = new int[methodCount];
        for (int i = 0; i < methodCount; i++) {
            labels[i] = new Label();

            MetaMethod method = (MetaMethod) methods.get(i);
            method.setMethodIndex(i + 1);
            indices[i] = method.getMethodIndex();

            //System.out.println("Index: " + method.getMethodIndex() + " for: " + method);
        }

        cv.visitLookupSwitchInsn(defaultLabel, indices, labels);
        //cv.visitTableSwitchInsn(minMethodIndex, maxMethodIndex, defaultLabel, labels);

        for (int i = 0; i < methodCount; i++) {
            cv.visitLabel(labels[i]);

            MetaMethod method = (MetaMethod) methods.get(i);
            invokeMethod(method);
            if (method.getReturnType() == void.class) {
                cv.visitInsn(ACONST_NULL);
            }
            cv.visitInsn(ARETURN);
        }

        cv.visitLabel(defaultLabel);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitVarInsn(ALOAD, 2);
        cv.visitVarInsn(ALOAD, 3);
        cv.visitMethodInsn(
            INVOKEVIRTUAL,
            classInternalName,
            "noSuchMethod",
            "(Lgroovy/lang/MetaMethod;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        cv.visitInsn(ARETURN);
        cv.visitMaxs(4, 4);
    }

    protected void invokeMethod(MetaMethod method) {
        /** simple
        cv.visitVarInsn(ALOAD, 2);
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
        */
        Class callClass = method.getInterfaceClass();
        boolean useInterface = false;
        if (callClass == null) {
            callClass = method.getCallClass();
        }
        else {
            useInterface = true;
        }
        String type = BytecodeHelper.getClassInternalName(callClass.getName());
        String descriptor = BytecodeHelper.getMethodDescriptor(method.getReturnType(), method.getParameterTypes());

        //        System.out.println("Method: " + method);
        //        System.out.println("Descriptor: " + descriptor);

        if (method.isStatic()) {
            loadParameters(method, 3);
            cv.visitMethodInsn(INVOKESTATIC, type, method.getName(), descriptor);
        }
        else {
            cv.visitVarInsn(ALOAD, 2);
            helper.doCast(callClass);
            loadParameters(method, 3);
            cv.visitMethodInsn((useInterface) ? INVOKEINTERFACE : INVOKEVIRTUAL, type, method.getName(), descriptor);
        }

        helper.box(method.getReturnType());
    }

    /*
    protected void generateInvokeSuperMethod() {
        List superMethods = new ArrayList(methods);
        for (Iterator iter = methods.iterator(); iter.hasNext();) {
            MetaMethod method = (MetaMethod) iter.next();
            if (!validSuperMethod(method)) {
                superMethods.remove(method);
            }
        }
        int methodCount = superMethods.size();
        if (methodCount == 0) {
            return;
        }
        cv =
            cw.visitMethod(
                ACC_PUBLIC,
                "invokeSuper",
                "(Lgroovy/lang/MetaMethod;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null);
        helper = new BytecodeHelper(cv);

        cv.visitVarInsn(ALOAD, 1);
        cv.visitMethodInsn(INVOKEVIRTUAL, "groovy/lang/MetaMethod", "getMethodIndex", "()I");
        Label defaultLabel = new Label();
        Label[] labels = new Label[methodCount];
        int[] indices = new int[methodCount];
        for (int i = 0; i < methodCount; i++) {
            labels[i] = new Label();

            MetaMethod method = (MetaMethod) superMethods.get(i);
            method.setMethodIndex(i + 1);
            indices[i] = method.getMethodIndex();

            //System.out.println("Index: " + method.getMethodIndex() + " for: " + method);
        }

        cv.visitLookupSwitchInsn(defaultLabel, indices, labels);
        //cv.visitTableSwitchInsn(minMethodIndex, maxMethodIndex, defaultLabel, labels);

        for (int i = 0; i < methodCount; i++) {
            MetaMethod method = (MetaMethod) superMethods.get(i);
            cv.visitLabel(labels[i]);

            invokeSuperMethod(method);
            if (method.getReturnType() == void.class) {
                cv.visitInsn(ACONST_NULL);
            }
            cv.visitInsn(ARETURN);
        }

        cv.visitLabel(defaultLabel);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitVarInsn(ALOAD, 2);
        cv.visitVarInsn(ALOAD, 3);
        cv.visitMethodInsn(
            INVOKEVIRTUAL,
            classInternalName,
            "noSuchMethod",
            "(Lgroovy/lang/MetaMethod;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        cv.visitInsn(ARETURN);
        cv.visitMaxs(4, 4);
    }

    protected boolean validSuperMethod(MetaMethod method) {
        return !method.isStatic() && (method.getModifiers() & (Modifier.FINAL | Modifier.ABSTRACT)) == 0 && theClass == method.getDeclaringClass();
    }

    protected void invokeSuperMethod(MetaMethod method) {
        Class ownerClass = method.getDeclaringClass();
        String type = helper.getClassInternalName(ownerClass.getName());
        String descriptor = helper.getMethodDescriptor(method.getReturnType(), method.getParameterTypes());

//        System.out.println("Method: " + method.getName());
//        System.out.println("Descriptor: " + descriptor);

        cv.visitVarInsn(ALOAD, 2);
        //helper.doCast(ownerClass);
        loadParameters(method, 3);
        cv.visitMethodInsn(INVOKESPECIAL, type, method.getName(), descriptor);

        helper.box(method.getReturnType());
    }
*/
    
    protected void loadParameters(MetaMethod method, int argumentIndex) {
        Class[] parameters = method.getParameterTypes();
        int size = parameters.length;
        for (int i = 0; i < size; i++) {
            cv.visitVarInsn(ALOAD, argumentIndex);
            helper.pushConstant(i);
            cv.visitInsn(AALOAD);

            // we should cast to something
            Class type = parameters[i];
            if (type.isPrimitive()) {
                helper.unbox(type);
            }
            else {
                helper.doCast(type);
            }
        }
    }
}
