package cn.iocoder.yudao.module.tomato.service.sop;

import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingCustomReminderCreateReqVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingCustomReminderDO;

import java.util.List;

public interface FarmingCustomReminderService {

    /**
     * 创建自定义提醒
     */
    void createReminder(Long userId, FarmingCustomReminderCreateReqVO reqVO);

    /**
     * 标记提醒为已完成
     */
    void completeReminder(Long id);

    /**
     * 获取大棚的自定义提醒列表
     */
    List<FarmingCustomReminderDO> getRemindersByGreenhouse(Long greenhouseId);

    /**
     * 获取大棚未完成的自定义提醒
     */
    List<FarmingCustomReminderDO> getPendingReminders(Long greenhouseId);
}
