/*
 * Copyright 2010-2017 JetBrains s.r.o.
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

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInspection.*
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.idea.caches.resolve.analyzeFully
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.addRemoveModifier.addModifier
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Project kotlin Created by Andrei.
 */
class MissingConstInspection : AbstractKotlinInspection() {
    override fun buildVisitor(
            holder: ProblemsHolder,
            isOnTheFly: Boolean,
            session: LocalInspectionToolSession
    ): PsiElementVisitor = object : KtVisitorVoid() {
        override fun visitDeclaration(declaration: KtDeclaration) {
            super.visitDeclaration(declaration)

            when (declaration) {
                is KtProperty ->
                    if ((declaration.isTopLevel || declaration.containingClassOrObject is KtObjectDeclaration) && !declaration.isVar) {
                        val context = declaration.analyzeFully()
                        val typeReference = declaration.typeReference
                        val type = context.get(BindingContext.TYPE, typeReference) ?: return
                        if (KotlinBuiltIns.isPrimitiveType(type) || KotlinBuiltIns.isString(type)) {
                            val hasConstModifier = declaration.hasModifier(KtTokens.CONST_KEYWORD)
                            if (!hasConstModifier) holder.registerProblem(
                                    declaration,
                                    "Missing const modifier",
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                    MissingConstFix()
                            )
                        }
                    }
            }
        }
    }
    private class MissingConstFix : LocalQuickFix {
        override fun getName() = "You might be missing const modifier"
        override fun getFamilyName() = name

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val declaration = descriptor.psiElement as? KtDeclaration ?: return
            val modifierListOwner = declaration.getParentOfType<KtModifierListOwner>(false) ?: return
            addModifier(modifierListOwner, KtTokens.CONST_KEYWORD)
        }
    }
}
