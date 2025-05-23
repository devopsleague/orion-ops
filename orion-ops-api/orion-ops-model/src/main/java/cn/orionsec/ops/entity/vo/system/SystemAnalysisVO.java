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
package cn.orionsec.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统分析响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/16 22:25
 */
@Data
@ApiModel(value = "系统分析响应")
public class SystemAnalysisVO {

    @ApiModelProperty(value = "临时文件数量")
    private Integer tempFileCount;

    @ApiModelProperty(value = "临时文件大小")
    private String tempFileSize;

    @ApiModelProperty(value = "日志文件数量")
    private Integer logFileCount;

    @ApiModelProperty(value = "日志文件大小")
    private String logFileSize;

    @ApiModelProperty(value = "交换文件数量")
    private Integer swapFileCount;

    @ApiModelProperty(value = "交换文件大小")
    private String swapFileSize;

    @ApiModelProperty(value = "构建产物版本数")
    private Integer distVersionCount;

    @ApiModelProperty(value = "构建产物大小")
    private String distFileSize;

    @ApiModelProperty(value = "应用仓库版本数")
    private Integer repoVersionCount;

    @ApiModelProperty(value = "应用仓库大小")
    private String repoVersionFileSize;

    @ApiModelProperty(value = "录屏文件数")
    private Integer screenFileCount;

    @ApiModelProperty(value = "录屏文件大小")
    private String screenFileSize;

    @ApiModelProperty(value = "文件清理阈值")
    private Integer fileCleanThreshold;

    @ApiModelProperty(value = "自动清理文件")
    private Boolean autoCleanFile;

}
