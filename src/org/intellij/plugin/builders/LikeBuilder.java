package org.intellij.plugin.builders;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

import java.util.List;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * Created by alavrenko
 * on 05/08/14.
 */
public class LikeBuilder {

    public static final String LIKE_BUILDER = "com.openwaygroup.ssd.emulator.screen.LikeBuilder";

    public String generateLikeMethod(PsiClass psiClass, List<PsiField> fields) {
        StringBuilder builder = new StringBuilder("public boolean like(");
        String parameterType = psiClass.getName();
        String parameterName = uncapitalize(parameterType);
        builder.append(parameterType).append(" ").append(parameterName).append(") {\n");

        builder.append(" return equals(").append(parameterName).append(") || (");
        builder.append("new ").append(LIKE_BUILDER).append("()");
        for (PsiField field : fields) {
            builder.append(".append(this.").append(field.getName()).append(", ").append(parameterName).append(".get").append(capitalize(field.getName())).append("())");
        }

        builder.append(".isLike());\n}");
        return builder.toString();
    }
}
