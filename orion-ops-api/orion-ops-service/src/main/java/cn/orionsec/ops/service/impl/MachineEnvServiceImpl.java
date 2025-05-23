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

import cn.orionsec.kit.lang.define.collect.MutableLinkedHashMap;
import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Charsets;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.command.CommandConst;
import cn.orionsec.ops.constant.env.EnvConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.history.HistoryOperator;
import cn.orionsec.ops.constant.history.HistoryValueType;
import cn.orionsec.ops.constant.machine.MachineConst;
import cn.orionsec.ops.constant.machine.MachineEnvAttr;
import cn.orionsec.ops.dao.MachineEnvDAO;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.entity.domain.MachineEnvDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.request.machine.MachineEnvRequest;
import cn.orionsec.ops.entity.vo.machine.MachineEnvVO;
import cn.orionsec.ops.service.api.HistoryValueService;
import cn.orionsec.ops.service.api.MachineEnvService;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 环境变量服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 11:44
 */
@Service("machineEnvService")
public class MachineEnvServiceImpl implements MachineEnvService {

    @Resource
    private MachineEnvDAO machineEnvDAO;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private HistoryValueService historyValueService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEnv(MachineEnvRequest request) {
        // 查询
        Long machineId = request.getMachineId();
        String key = request.getKey();
        // 重复检查
        MachineEnvDO env = machineEnvDAO.selectOneRel(machineId, key);
        // 修改
        if (env != null) {
            SpringHolder.getBean(MachineEnvService.class).updateEnv(env, request);
            return env.getId();
        }
        // 新增
        MachineEnvDO insert = new MachineEnvDO();
        insert.setMachineId(machineId);
        insert.setAttrKey(key);
        insert.setAttrValue(request.getValue());
        insert.setDescription(request.getDescription());
        machineEnvDAO.insert(insert);
        // 插入历史值
        Long id = insert.getId();
        historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, HistoryOperator.ADD, null, request.getValue());
        return id;
    }

    @Override
    public Integer updateEnv(MachineEnvRequest request) {
        // 查询
        Long id = request.getId();
        MachineEnvDO before = machineEnvDAO.selectById(id);
        Valid.notNull(before, MessageConst.ENV_ABSENT);
        return SpringHolder.getBean(MachineEnvService.class).updateEnv(before, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateEnv(MachineEnvDO before, MachineEnvRequest request) {
        // 检查是否修改了值
        Long id = before.getId();
        String beforeValue = before.getAttrValue();
        String afterValue = request.getValue();
        if (Const.IS_DELETED.equals(before.getDeleted())) {
            // 设置新增历史值
            historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, HistoryOperator.ADD, null, afterValue);
            // 恢复
            machineEnvDAO.setDeleted(id, Const.NOT_DELETED);
        } else if (afterValue != null && !afterValue.equals(beforeValue)) {
            // 检查是否修改了值 增加历史值
            historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, HistoryOperator.UPDATE, beforeValue, afterValue);
        }
        // 修改
        MachineEnvDO update = new MachineEnvDO();
        update.setId(id);
        update.setAttrValue(afterValue);
        update.setDescription(request.getDescription());
        update.setUpdateTime(new Date());
        return machineEnvDAO.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteEnv(List<Long> idList) {
        int effect = 0;
        for (Long id : idList) {
            // 获取元数据
            MachineEnvDO env = machineEnvDAO.selectById(id);
            Valid.notNull(env, MessageConst.ENV_ABSENT);
            String key = env.getAttrKey();
            Valid.isTrue(MachineEnvAttr.of(key) == null, "{} " + MessageConst.FORBID_DELETE, key);
            // 删除
            effect += machineEnvDAO.deleteById(id);
            // 插入历史值
            historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, HistoryOperator.DELETE, env.getAttrValue(), null);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, effect);
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEnv(Long machineId, Map<String, String> env) {
        MachineEnvService self = SpringHolder.getBean(MachineEnvService.class);
        // 倒排
        List<Map.Entry<String, String>> entries = Lists.newList(env.entrySet());
        for (int i = entries.size() - 1; i >= 0; i--) {
            // 更新
            Map.Entry<String, String> entry = entries.get(i);
            MachineEnvRequest request = new MachineEnvRequest();
            request.setMachineId(machineId);
            request.setKey(entry.getKey());
            request.setValue(entry.getValue());
            self.addEnv(request);
        }
    }

    @Override
    public Integer mergeEnv(Long sourceMachineId, Long targetMachineId) {
        LambdaQueryWrapper<MachineEnvDO> sourceWrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, sourceMachineId);
        LambdaQueryWrapper<MachineEnvDO> targetWrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, targetMachineId);
        List<MachineEnvDO> sourceEnvList = machineEnvDAO.selectList(sourceWrapper);
        List<MachineEnvDO> targetEnvList = machineEnvDAO.selectList(targetWrapper);
        Valid.notEmpty(sourceEnvList);
        Valid.notEmpty(targetEnvList);
        int effect = 0;
        for (MachineEnvDO sourceEnv : sourceEnvList) {
            Optional<MachineEnvDO> targetOption = targetEnvList.stream()
                    .filter(t -> t.getAttrKey().equals(sourceEnv.getAttrKey()))
                    .findFirst();
            if (targetOption.isPresent()) {
                // 更新
                MachineEnvRequest update = new MachineEnvRequest();
                update.setId(targetOption.get().getId());
                update.setValue(sourceEnv.getAttrValue());
                update.setDescription(sourceEnv.getDescription());
                effect += this.updateEnv(update);
            } else {
                // 插入
                MachineEnvDO insertEnv = new MachineEnvDO();
                insertEnv.setMachineId(targetMachineId);
                insertEnv.setAttrKey(sourceEnv.getAttrKey());
                insertEnv.setAttrValue(sourceEnv.getAttrValue());
                insertEnv.setDescription(sourceEnv.getDescription());
                effect += machineEnvDAO.insert(insertEnv);
            }
        }
        return effect;
    }

    @Override
    public DataGrid<MachineEnvVO> listEnv(MachineEnvRequest request) {
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .like(Strings.isNotBlank(request.getKey()), MachineEnvDO::getAttrKey, request.getKey())
                .like(Strings.isNotBlank(request.getValue()), MachineEnvDO::getAttrValue, request.getValue())
                .like(Strings.isNotBlank(request.getDescription()), MachineEnvDO::getDescription, request.getDescription())
                .eq(MachineEnvDO::getMachineId, request.getMachineId())
                .orderByDesc(MachineEnvDO::getUpdateTime);
        return DataQuery.of(machineEnvDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(MachineEnvVO.class);
    }

    @Override
    public MachineEnvVO getEnvDetail(Long id) {
        MachineEnvDO env = machineEnvDAO.selectById(id);
        Valid.notNull(env, MessageConst.UNKNOWN_DATA);
        return Converts.to(env, MachineEnvVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncMachineEnv(MachineEnvRequest request) {
        Long id = request.getId();
        Long machineId = request.getMachineId();
        List<Long> targetMachineIdList = request.getTargetMachineIdList();
        // 获取self
        MachineEnvService self = SpringHolder.getBean(MachineEnvService.class);
        List<MachineEnvDO> envList;
        if (Const.L_N_1.equals(id)) {
            // 全量同步
            LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                    .eq(MachineEnvDO::getMachineId, machineId)
                    .orderByAsc(MachineEnvDO::getUpdateTime);
            envList = machineEnvDAO.selectList(wrapper);
        } else {
            // 查询数据
            MachineEnvDO env = machineEnvDAO.selectById(id);
            Valid.notNull(env, MessageConst.UNKNOWN_DATA);
            envList = Lists.singleton(env);
        }
        // 同步
        for (Long targetMachineId : targetMachineIdList) {
            for (MachineEnvDO syncEnv : envList) {
                MachineEnvRequest addRequest = new MachineEnvRequest();
                addRequest.setMachineId(targetMachineId);
                addRequest.setKey(syncEnv.getAttrKey());
                addRequest.setValue(syncEnv.getAttrValue());
                addRequest.setDescription(syncEnv.getDescription());
                self.addEnv(addRequest);
            }
        }
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.ENV_COUNT, envList.size());
        EventParamsHolder.addParam(EventKeys.MACHINE_COUNT, targetMachineIdList.size());
    }

    @Override
    public String getMachineEnv(Long machineId, String env) {
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId)
                .eq(MachineEnvDO::getAttrKey, env)
                .last(Const.LIMIT_1);
        return Optional.ofNullable(machineEnvDAO.selectOne(wrapper))
                .map(MachineEnvDO::getAttrValue)
                .orElse(null);
    }

    @Override
    public MutableLinkedHashMap<String, String> getMachineEnv(Long machineId) {
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId)
                .orderByAsc(MachineEnvDO::getId);
        machineEnvDAO.selectList(wrapper).forEach(e -> env.put(e.getAttrKey(), e.getAttrValue()));
        return env;
    }

    @Override
    public MutableLinkedHashMap<String, String> getFullMachineEnv(Long machineId) {
        // 查询机器
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        env.put(EnvConst.MACHINE_PREFIX + EnvConst.MACHINE_ID, machine.getId() + Strings.EMPTY);
        env.put(EnvConst.MACHINE_PREFIX + EnvConst.MACHINE_NAME, machine.getMachineName());
        env.put(EnvConst.MACHINE_PREFIX + EnvConst.MACHINE_TAG, machine.getMachineTag());
        env.put(EnvConst.MACHINE_PREFIX + EnvConst.MACHINE_HOST, machine.getMachineHost());
        env.put(EnvConst.MACHINE_PREFIX + EnvConst.MACHINE_PORT, machine.getSshPort() + Strings.EMPTY);
        env.put(EnvConst.MACHINE_PREFIX + EnvConst.MACHINE_USERNAME, machine.getUsername());
        // 查询环境变量
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId)
                .orderByAsc(MachineEnvDO::getId);
        machineEnvDAO.selectList(wrapper).forEach(e -> env.put(EnvConst.MACHINE_PREFIX + e.getAttrKey(), e.getAttrValue()));
        return env;
    }

    @Override
    public void initEnv(Long machineId) {
        List<String> keys = MachineEnvAttr.getKeys();
        for (String key : keys) {
            MachineEnvDO env = new MachineEnvDO();
            MachineEnvAttr attr = MachineEnvAttr.of(key);
            env.setMachineId(machineId);
            env.setDescription(attr.getDescription());
            env.setAttrKey(attr.getKey());
            switch (attr) {
                case TAIL_OFFSET:
                    env.setAttrValue(Const.TAIL_OFFSET_LINE + Strings.EMPTY);
                    break;
                case TAIL_CHARSET:
                case SFTP_CHARSET:
                    env.setAttrValue(Const.UTF_8);
                    break;
                case TAIL_DEFAULT_COMMAND:
                    env.setAttrValue(CommandConst.TAIL_FILE_DEFAULT);
                    break;
                case CONNECT_TIMEOUT:
                    env.setAttrValue(MachineConst.CONNECT_TIMEOUT + Strings.EMPTY);
                    break;
                case CONNECT_RETRY_TIMES:
                    env.setAttrValue(MachineConst.CONNECT_RETRY_TIMES + Strings.EMPTY);
                    break;
                default:
                    break;
            }
            machineEnvDAO.insert(env);
            // 插入历史记录
            historyValueService.addHistory(env.getId(), HistoryValueType.MACHINE_ENV, HistoryOperator.ADD, null, env.getAttrValue());
        }
    }

    @Override
    public Integer deleteEnvByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .in(MachineEnvDO::getMachineId, machineIdList);
        return machineEnvDAO.delete(wrapper);
    }

    @Override
    public String getSftpCharset(Long machineId) {
        String charset = this.getMachineEnv(machineId, MachineEnvAttr.SFTP_CHARSET.getKey());
        return Charsets.isSupported(charset) ? charset : Const.UTF_8;
    }

    @Override
    public Integer getTailOffset(Long machineId) {
        String value = this.getMachineEnv(machineId, MachineEnvAttr.TAIL_OFFSET.getKey());
        int offset = Strings.isInteger(value) ? Integer.parseInt(value) : Const.TAIL_OFFSET_LINE;
        return Math.max(offset, 0);
    }

    @Override
    public String getTailCharset(Long machineId) {
        String charset = this.getMachineEnv(machineId, MachineEnvAttr.TAIL_CHARSET.getKey());
        return Charsets.isSupported(charset) ? charset : Const.UTF_8;
    }

    @Override
    public String getTailDefaultCommand(Long machineId) {
        String command = this.getMachineEnv(machineId, MachineEnvAttr.TAIL_DEFAULT_COMMAND.getKey());
        return Strings.isBlank(command) ? CommandConst.TAIL_FILE_DEFAULT : command;
    }

    @Override
    public Integer getConnectTimeout(Long machineId) {
        String value = this.getMachineEnv(machineId, MachineEnvAttr.CONNECT_TIMEOUT.getKey());
        int timeout = Strings.isInteger(value) ? Integer.parseInt(value) : MachineConst.CONNECT_TIMEOUT;
        return Math.max(timeout, 0);
    }

    @Override
    public Integer getConnectRetryTimes(Long machineId) {
        String value = this.getMachineEnv(machineId, MachineEnvAttr.CONNECT_RETRY_TIMES.getKey());
        int times = Strings.isInteger(value) ? Integer.parseInt(value) : MachineConst.CONNECT_RETRY_TIMES;
        return Math.max(times, 0);
    }

}
