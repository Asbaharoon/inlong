/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.manager.common.pojo.sink.postgresql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.inlong.manager.common.enums.ErrorCodeEnum;
import org.apache.inlong.manager.common.exceptions.BusinessException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * PostgreSQL sink info
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostgreSQLSinkDTO {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @ApiModelProperty("JDBC URL of the PostgreSQL server")
    private String jdbcUrl;

    @ApiModelProperty("Username for JDBC URL")
    private String username;

    @ApiModelProperty("User password")
    private String password;

    @ApiModelProperty("Target database name")
    private String dbName;

    @ApiModelProperty("Target table name")
    private String tableName;

    @ApiModelProperty("Primary key")
    private String primaryKey;

    @ApiModelProperty("Properties for PostgreSQL")
    private Map<String, Object> properties;

    /**
     * Get the dto instance from the request
     */
    public static PostgreSQLSinkDTO getFromRequest(PostgreSQLSinkRequest request) {
        return PostgreSQLSinkDTO.builder()
                .jdbcUrl(request.getJdbcUrl())
                .username(request.getUsername())
                .password(request.getPassword())
                .dbName(request.getDbName())
                .primaryKey(request.getPrimaryKey())
                .tableName(request.getTableName())
                .properties(request.getProperties())
                .build();
    }

    /**
     * Get PostgreSQL sink info from JSON string
     *
     * @param extParams JSON string
     * @return PostgreSQL sink DTO
     */
    public static PostgreSQLSinkDTO getFromJson(@NotNull String extParams) {
        try {
            OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return OBJECT_MAPPER.readValue(extParams, PostgreSQLSinkDTO.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.SINK_INFO_INCORRECT.getMessage());
        }
    }

    /**
     * Get PostgreSQL table info
     */
    public static PostgreSQLTableInfo getTableInfo(PostgreSQLSinkDTO pgSink, List<PostgreSQLColumnInfo> columnList) {
        PostgreSQLTableInfo tableInfo = new PostgreSQLTableInfo();
        tableInfo.setDbName(pgSink.getDbName());
        tableInfo.setTableName(pgSink.getTableName());
        tableInfo.setPrimaryKey(pgSink.getPrimaryKey());
        tableInfo.setColumns(columnList);
        return tableInfo;
    }

}