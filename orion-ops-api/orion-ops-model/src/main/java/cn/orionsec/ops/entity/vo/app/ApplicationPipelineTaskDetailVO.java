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
package cn.orionsec.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 流水线明细详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:37
 */
@Data
@ApiModel(value = "流水线明细详情响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线任务id")
    private Long taskId;

    @ApiModelProperty(value = "引用id")
    private Long relId;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用唯一标识")
    private String appTag;

    /**
     * @see cn.orionsec.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

    @ApiModelProperty(value = "阶段操作配置")
    private ApplicationPipelineStageConfigVO config;

    /**
     * @see cn.orionsec.ops.constant.app.PipelineDetailStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止")
    private Integer status;

    @ApiModelProperty(value = "执行开始时间")
    private Date startTime;

    @ApiModelProperty(value = "执行开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "执行结束时间")
    private Date endTime;

    @ApiModelProperty(value = "执行结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

}
