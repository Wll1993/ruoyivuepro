package cn.iocoder.yudao.module.bpm.service.definition;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.controller.admin.definition.vo.model.*;
import org.activiti.bpmn.model.BpmnModel;

import javax.validation.Valid;

/**
 * 流程模型接口
 *
 * @author yunlongn
 */
public interface BpmModelService extends BpmModelCommonService {


    /**
     * 将流程模型，部署成一个流程定义
     *
     * @param id 编号
     */
    void deployModel(String id);

    /**
     * 删除模型
     *
     * @param id 编号
     */
    void deleteModel(String id);

    /**
     * 修改模型的状态，实际更新的部署的流程定义的状态
     *
     * @param id 编号
     * @param state 状态 {@link org.activiti.engine.impl.persistence.entity.SuspensionState}
     */
    void updateModelState(String id, Integer state);

    /**
     * 获得流程模型编号对应的 BPMN Model
     *
     * @param id 流程模型编号
     * @return BPMN Model
     */
    BpmnModel getBpmnModel(String id);

}
