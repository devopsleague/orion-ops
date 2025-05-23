/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.controller;

import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.data.DataExportRequest;
import cn.orionsec.ops.handler.exporter.IDataExporter;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据导出 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 14:02
 */
@Api(tags = "数据导出")
@RestController
@RequestMapping("/orion/api/data-export")
public class DataExportController {

    @DemoDisableApi
    @PostMapping("/export")
    @ApiOperation(value = "导出数据")
    @EventLog(EventType.DATA_EXPORT)
    public void exportData(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        ExportType exportType = Valid.notNull(ExportType.of(request.getExportType()));
        IDataExporter.create(exportType, request, response).doExport();
    }

}
