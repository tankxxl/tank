package com.thinkgem.jeesite.common.web;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by rgz on 14/12/2016.
 */
public class JxlsExcelView extends AbstractView {
    private static final String CONTENT_TYPE = "application/vnd.ms-excel";

    private String templatePath;
    private String exportFileName;
    private boolean useFastFormulaProcessor = true;

    public JxlsExcelView(String templatePath, String exportFileName) {
        this(templatePath, exportFileName, true);
    }

    public JxlsExcelView(String templatePath, String exportFileName, boolean useFastFormulaProcessor) {
        this.templatePath = templatePath;
        this.useFastFormulaProcessor = useFastFormulaProcessor;

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

        response.setContentType(getContentType());
        response.setHeader("content-disposition",
                "attachment;filename=" + exportFileName + ".xls");
        ServletOutputStream os = response.getOutputStream();

        InputStream is = null;

        try {
//            is = getClass().getClassLoader()
//                    .getResourceAsStream("WEB-INF/excel/project/" + templatePath);

            is = getClass().getClassLoader().getResourceAsStream(templatePath);
            System.out.print(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (useFastFormulaProcessor) {
            JxlsHelper.getInstance().processTemplate(is, os, context);
        } else {
            JxlsHelper.getInstance()
                    .setUseFastFormulaProcessor(false)
                    .processTemplate(is, os, context);
        }


        os.flush();
        is.close();
    }
}
