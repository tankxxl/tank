package com.thinkgem.jeesite.common.web;

import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rgz on 14/12/2016.
 */
public class JxlsExcelView extends AbstractView {
    private static final String CONTENT_TYPE = "application/vnd.ms-excel";

    private String templatePath;
    private String exportFileName;

    /**
     *
     * @param templatePath 模版相对于当前classpath路径
     * @param exportFileName 导出文件名
     */
    public JxlsExcelView(String templatePath, String exportFileName) {
        this.templatePath = templatePath;
        if (exportFileName != null) {
            try {
                exportFileName = URLEncoder.encode(exportFileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        this.exportFileName = exportFileName;
        setContentType(CONTENT_TYPE);
    }

    @Override
    protected void renderMergedOutputModel(
            Map<String, Object> model,
            HttpServletRequest httpServletRequest,
            HttpServletResponse response) throws Exception {

        Context context = new Context(model);

        // 设定文件输出类型
        response.setContentType(getContentType());
        response.setHeader("content-disposition",
                "attachment;filename=" + exportFileName);

        ServletOutputStream os = response.getOutputStream();

        InputStream is = null;

        try {
//            is = getClass().getClassLoader()
//                    .getResourceAsStream("WEB-INF/excel/project/" + templatePath);

            is = getClass().getClassLoader().getResourceAsStream(templatePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // simple
        // JxlsHelper.getInstance().processTemplate(is, os, context);

        // 可以传递自定义函数方式2
        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer = jxlsHelper.createTransformer(is, os);
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();
        Map<String, Object> funcs = new HashMap<>();
        funcs.put("utils", new DictUtils()); // 添加自定义功能
        evaluator.getJexlEngine().setFunctions(funcs);
        jxlsHelper.processTemplate(context, transformer);


        os.flush();
        is.close();
        os.close();
    }
}
