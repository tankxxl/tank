package com.thinkgem.jeesite.common.tags;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.servlet.tags.form.AbstractFormTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import javax.servlet.jsp.JspException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

/**
 * 自动后成前台js验证代码
 *
 * Created by rgz on 30/12/2016.
 */
public class JsValidateTag extends AbstractFormTag {
    private TagWriter tagWriter;
    private String modelAttribute;

    public void setModelAttribute(String modelAttribute) {
        this.modelAttribute = modelAttribute;
    }

    public String getModelAttribute() throws JspException {
        String resolvedModelAttribute = (String) evaluate("modelAttribute", this.modelAttribute);
        return (resolvedModelAttribute != null ? resolvedModelAttribute : "");
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        Object model;
        if(getRequestContext().getModel()!=null)
            model=getRequestContext().getModel().get(getModelAttribute());
        else
            model=this.pageContext.getRequest().getAttribute(getModelAttribute());
        if(model!=null){
            Map<String, List<String[]>> fieldValidateMap=new HashMap<String, List<String[]>>();

            try {
                Field[] theFields=model.getClass().getDeclaredFields();
                if(theFields!=null&& theFields.length>0){
                    for(Field field : theFields){
                        String fieldName=field.getName();
                        List<String[]> fieldValidateList=new ArrayList<String[]>();
                        NotEmpty notEmpty=field.getAnnotation(NotEmpty.class);
                        if(notEmpty!=null){
                            String messageName=notEmpty.message();
                            fieldValidateList.add(new String[]{"required","true",getRequestContext().getMessage(messageName.substring(1, messageName.length()-1))});
                        }

                        Email email=field.getAnnotation(Email.class);
                        if(email!=null){
                            String messageName=email.message();
                            fieldValidateList.add(new String[]{"email","true",getRequestContext().getMessage(messageName.substring(1, messageName.length()-1))});
                        }

                        Range range=field.getAnnotation(Range.class);
                        if(range!=null){
                            String messageName=range.message();
                            fieldValidateList.add(new String[]{"range","["+range.min()+","+range.max()+"]",getRequestContext().getMessage(messageName.substring(1, messageName.length()-1))});
                        }


                        if(fieldValidateList.size()>0){
                            fieldValidateMap.put(fieldName, fieldValidateList);
                        }
                    }
                }
            }catch (SecurityException e1) {
                e1.printStackTrace();
            }

            if(fieldValidateMap.size()>0){
                StringBuilder rulesBuilder=new StringBuilder();
                StringBuilder messagesBuilder=new StringBuilder();

                rulesBuilder.append("rules:{");
                messagesBuilder.append("messages:{");

                int i=0;
                Iterator<Entry<String, List<String[]>>> iterator=fieldValidateMap.entrySet().iterator();
                while(iterator.hasNext()){
                    Entry<String, List<String[]>> entry=iterator.next();
                    rulesBuilder.append(entry.getKey()).append(":{");
                    messagesBuilder.append(entry.getKey()).append(":{");

                    int j=0;
                    for(String[] array : entry.getValue()){
                        rulesBuilder.append(array[0]).append(":").append(array[1]);
                        messagesBuilder.append(array[0]).append(":\"").append(array[2]).append("\"");

                        if(j<entry.getValue().size()-1){
                            rulesBuilder.append(",");
                            messagesBuilder.append(",");
                        }
                        j++;
                    }

                    rulesBuilder.append("}");
                    messagesBuilder.append("}");

                    if(i<fieldValidateMap.size()-1){
                        rulesBuilder.append(",");
                        messagesBuilder.append(",");
                    }
                    i++;
                }

                rulesBuilder.append("},");
                messagesBuilder.append("}");
                tagWriter.startTag("script");
                tagWriter.writeAttribute("type", "text/javascript");
                tagWriter.appendValue("$(function() {");
                tagWriter.appendValue("$(\"#");
                tagWriter.appendValue(getModelAttribute());
                tagWriter.appendValue("\").validate({");
                //在失去焦点时验证
                tagWriter.appendValue("onfocusout:function(element){$(element).valid();},");
                tagWriter.appendValue(rulesBuilder.toString());
                tagWriter.appendValue(messagesBuilder.toString());
                tagWriter.appendValue("});");
                tagWriter.appendValue("});");
                tagWriter.endTag(true);
            }
        }

        this.tagWriter=tagWriter;
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.tagWriter = null;
    }
}
