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
package cn.orionsec.ops.service.impl;

import cn.orionsec.ops.constant.alarm.AlarmGroupNotifyType;
import cn.orionsec.ops.dao.AlarmGroupNotifyDAO;
import cn.orionsec.ops.entity.domain.AlarmGroupNotifyDO;
import cn.orionsec.ops.service.api.AlarmGroupNotifyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 报警组通知方式服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 10:29
 */
@Service("alarmGroupNotifyService")
public class AlarmGroupNotifyServiceImpl implements AlarmGroupNotifyService {

    @Resource
    private AlarmGroupNotifyDAO alarmGroupNotifyDAO;

    @Override
    public List<AlarmGroupNotifyDO> selectByGroupId(Long groupId) {
        LambdaQueryWrapper<AlarmGroupNotifyDO> wrapper = new LambdaQueryWrapper<AlarmGroupNotifyDO>()
                .eq(AlarmGroupNotifyDO::getGroupId, groupId);
        return alarmGroupNotifyDAO.selectList(wrapper);
    }

    @Override
    public List<AlarmGroupNotifyDO> selectByGroupIdList(List<Long> groupIdList) {
        LambdaQueryWrapper<AlarmGroupNotifyDO> wrapper = new LambdaQueryWrapper<AlarmGroupNotifyDO>()
                .in(AlarmGroupNotifyDO::getGroupId, groupIdList);
        return alarmGroupNotifyDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByGroupId(Long groupId) {
        LambdaQueryWrapper<AlarmGroupNotifyDO> wrapper = new LambdaQueryWrapper<AlarmGroupNotifyDO>()
                .eq(AlarmGroupNotifyDO::getGroupId, groupId);
        return alarmGroupNotifyDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByWebhookId(Long webhookId) {
        LambdaQueryWrapper<AlarmGroupNotifyDO> wrapper = new LambdaQueryWrapper<AlarmGroupNotifyDO>()
                .eq(AlarmGroupNotifyDO::getNotifyId, webhookId)
                .eq(AlarmGroupNotifyDO::getNotifyType, AlarmGroupNotifyType.WEBHOOK.getType());
        return alarmGroupNotifyDAO.delete(wrapper);
    }

}
