package cn.iocoder.yudao.module.tomato.service.sop;

import cn.iocoder.yudao.module.tomato.controller.admin.sop.vo.FarmingCustomReminderCreateReqVO;
import cn.iocoder.yudao.module.tomato.dal.dataobject.FarmingCustomReminderDO;
import cn.iocoder.yudao.module.tomato.dal.mysql.FarmingCustomReminderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class FarmingCustomReminderServiceImpl implements FarmingCustomReminderService {

    @Resource
    private FarmingCustomReminderMapper customReminderMapper;

    @Override
    public void createReminder(Long userId, FarmingCustomReminderCreateReqVO reqVO) {
        FarmingCustomReminderDO reminder = FarmingCustomReminderDO.builder()
                .greenhouseId(reqVO.getGreenhouseId())
                .reminderName(reqVO.getReminderName())
                .reminderContent(reqVO.getReminderContent())
                .reminderDate(reqVO.getReminderDate())
                .isCompleted(0)
                .remark(reqVO.getRemark())
                .createBy(userId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        customReminderMapper.insert(reminder);
    }

    @Override
    public void completeReminder(Long id) {
        FarmingCustomReminderDO reminder = customReminderMapper.selectById(id);
        if (reminder != null) {
            reminder.setIsCompleted(1);
            reminder.setUpdateTime(LocalDateTime.now());
            customReminderMapper.updateById(reminder);
        }
    }

    @Override
    public List<FarmingCustomReminderDO> getRemindersByGreenhouse(Long greenhouseId) {
        return customReminderMapper.selectListByGreenhouseId(greenhouseId);
    }

    @Override
    public List<FarmingCustomReminderDO> getPendingReminders(Long greenhouseId) {
        return customReminderMapper.selectPendingByGreenhouseId(greenhouseId);
    }
}
