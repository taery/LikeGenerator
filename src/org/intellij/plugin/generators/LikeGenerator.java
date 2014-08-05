package org.intellij.plugin.generators;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugin.builders.LikeBuilder;

import java.util.List;

/**
 * Created by alavrenko
 * on 05/08/14.
 */
public class LikeGenerator extends AnAction {
    private static final String TITLE = "Select Fields for like method";
    private static final String LABEL_TEXT = "Fields to include in like:";
    private LikeBuilder likeBuilder = new LikeBuilder();


    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromEvent(e);
        GenerateDialog dlg = new GenerateDialog(psiClass, TITLE, LABEL_TEXT);
        dlg.show();
        if (dlg.isOK()) {
            generateLikeMenthod(psiClass, dlg.getFields());
        }
    }

    public void generateLikeMenthod(final PsiClass psiClass, final List<PsiField> fields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {
                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
                final JavaCodeStyleManager javaCodeStyleManager = JavaCodeStyleManager.getInstance(psiClass.getProject());

                final String likeMethodAsString = likeBuilder.generateLikeMethod(psiClass, fields);
                PsiMethod likeMethod = elementFactory.createMethodFromText(likeMethodAsString, psiClass);
                PsiElement likeElement = psiClass.add(likeMethod);
                javaCodeStyleManager.shortenClassReferences(likeElement);

            }

        }.execute();
    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromEvent(e);
        e.getPresentation().setEnabled(psiClass != null);
    }

    public PsiClass getPsiClassFromEvent(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }
}
