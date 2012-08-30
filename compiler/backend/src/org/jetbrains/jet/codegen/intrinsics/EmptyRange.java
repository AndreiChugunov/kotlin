/*
 * Copyright 2010-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.codegen.intrinsics;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.asm4.Type;
import org.jetbrains.asm4.commons.InstructionAdapter;
import org.jetbrains.jet.codegen.ExpressionCodegen;
import org.jetbrains.jet.codegen.state.GenerationState;
import org.jetbrains.jet.codegen.StackValue;
import org.jetbrains.jet.lang.psi.JetExpression;
import org.jetbrains.jet.lang.resolve.java.JvmClassName;
import org.jetbrains.jet.lang.types.lang.PrimitiveType;

import java.util.List;

/**
 * @author Evgeny Gerashchenko
 * @since 08.08.12
 */
public class EmptyRange implements IntrinsicMethod {
    private final PrimitiveType elementType;

    public EmptyRange(PrimitiveType elementType) {
        this.elementType = elementType;
    }

    @Override
    public StackValue generate(
            ExpressionCodegen codegen,
            InstructionAdapter v,
            @NotNull Type expectedType,
            @Nullable PsiElement element,
            @Nullable List<JetExpression> arguments,
            StackValue receiver,
            @NotNull GenerationState state
    ) {
        JvmClassName name = JvmClassName.byFqNameWithoutInnerClasses(elementType.getRangeClassName().getFqName());
        v.getstatic(name.toString(), "EMPTY", "L" + name + ";");
        return StackValue.onStack(expectedType);
    }
}
