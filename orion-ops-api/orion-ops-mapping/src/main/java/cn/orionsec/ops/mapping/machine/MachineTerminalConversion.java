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
package cn.orionsec.ops.mapping.machine;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.entity.config.TerminalConnectConfig;
import cn.orionsec.ops.entity.domain.MachineTerminalDO;
import cn.orionsec.ops.entity.domain.MachineTerminalLogDO;
import cn.orionsec.ops.entity.vo.machine.MachineTerminalLogVO;
import cn.orionsec.ops.entity.vo.machine.MachineTerminalManagerVO;
import cn.orionsec.ops.entity.vo.machine.MachineTerminalVO;

import java.util.Optional;

/**
 * 机器终端 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:14
 */
public class MachineTerminalConversion {

    static {
        TypeStore.STORE.register(MachineTerminalDO.class, MachineTerminalVO.class, p -> {
            MachineTerminalVO vo = new MachineTerminalVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setTerminalType(p.getTerminalType());
            vo.setBackgroundColor(p.getBackgroundColor());
            vo.setFontColor(p.getFontColor());
            vo.setFontSize(p.getFontSize());
            vo.setFontFamily(p.getFontFamily());
            vo.setEnableWebLink(p.getEnableWebLink());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(TerminalConnectConfig.class, MachineTerminalManagerVO.class, p -> {
            MachineTerminalManagerVO vo = new MachineTerminalManagerVO();
            vo.setUserId(p.getUserId());
            vo.setUserName(p.getUsername());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            vo.setMachineTag(p.getMachineTag());
            vo.setLogId(p.getLogId());
            vo.setConnectedTime(p.getConnectedTime());
            Optional.ofNullable(p.getConnectedTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setConnectedTimeAgo);
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(MachineTerminalLogDO.class, MachineTerminalLogVO.class, p -> {
            MachineTerminalLogVO vo = new MachineTerminalLogVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUsername());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineTag(p.getMachineTag());
            vo.setMachineHost(p.getMachineHost());
            vo.setAccessToken(p.getAccessToken());
            vo.setConnectedTime(p.getConnectedTime());
            vo.setDisconnectedTime(p.getDisconnectedTime());
            Optional.ofNullable(p.getConnectedTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setConnectedTimeAgo);
            Optional.ofNullable(p.getDisconnectedTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setDisconnectedTimeAgo);
            vo.setCloseCode(p.getCloseCode());
            vo.setCreateTime(p.getCreateTime());
            return vo;
        });
    }

}
