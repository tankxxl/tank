package com.thinkgem.jeesite.modules.act.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.AbstractLobStreamingResultSetExtractor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 注意，流程定义发布后，若你使用了该流程，其就会在缓存中缓存了流程定义的解析后的对象，供整个引擎使用，
 * 这时你更改了流程定义的XML后，那份缓存并没有同步更改，
 * 因此，需要告诉引擎，让他清空缓存中的该流程定义即可。如何清空流程定义的缓存，请参考以下代码：
 *
 * ((ProcessEngineConfigurationImpl)processEngineConfiguration).getProcessDefinitionCache().remove(actDefId);
 */
public class ActGeByteArrayDao {
    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 取得流程定义的XML
     * @param deployId
     * @return
     */
    public String getDefXmlByDeployId(String deployId) {
        String sql = "select a.* from ACT_GE_BYTEARRAY a where NAME_ LIKE '%bpmn20.xml' and DEPLOYMENT_ID_= ? ";
        final LobHandler lobHandler = new DefaultLobHandler(); // reusable
        final ByteArrayOutputStream contentOs = new ByteArrayOutputStream();
        String defXml = null;

        try{
            jdbcTemplate.query(sql, new Object[]{ deployId }, new AbstractLobStreamingResultSetExtractor<Object>() {
                        public void streamData(ResultSet rs) throws SQLException, IOException {
                            FileCopyUtils.copy(lobHandler.getBlobAsBinaryStream(rs, "BYTES_"), contentOs);
                        }
                    }
            );
            defXml = new String(contentOs.toByteArray(), "UTF-8");
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return defXml;
    }

    /**
     * 把修改过的xml更新至回流程定义中
     *
     * @param deployId
     * @param defXml
     */
    public void writeDefXml(final String deployId, String defXml) {
        try {
            LobHandler lobHandler = new DefaultLobHandler();
            final byte[] btyesXml = defXml.getBytes("UTF-8");
            String sql = "update ACT_GE_BYTEARRAY set BYTES_=? where NAME_ LIKE '%bpmn20.xml' and DEPLOYMENT_ID_= ? ";
            jdbcTemplate.execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                @Override
                protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
                    lobCreator.setBlobAsBytes(ps, 1, btyesXml);
                    ps.setString(2, deployId);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
