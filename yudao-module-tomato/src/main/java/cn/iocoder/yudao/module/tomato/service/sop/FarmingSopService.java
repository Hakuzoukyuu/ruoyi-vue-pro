package cn.iocoder.yudao.module.tomato.service.sop;

import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.SopCompleteTaskReqVO;
import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.SopTodoItemRespVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingSopTemplateDO;

import java.util.List;

public interface FarmingSopService {

    /**
     * 获取大棚的待办提醒列表
     */
    List<SopTodoItemRespVO> getTodoList(Long greenhouseId);

    /**
     * 完成一个待办任务
     */
    void completeTask(Long userId, SopCompleteTaskReqVO reqVO);

    /**
     * 获取所有SOP模板
     */
    List<FarmingSopTemplateDO> getAllTemplates();

    /**
     * 获取指定生长期的SOP模板
     */
    List<FarmingSopTemplateDO> getTemplatesByStage(Integer stage);
}
