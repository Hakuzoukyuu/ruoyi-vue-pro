package cn.iocoder.yudao.module.tomato.controller.admin.sop.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 农事统计看板 Response VO
 */
@Data
public class FarmingStatisticsRespVO {

    /** 今日完成数量 */
    private Integer todayCount;

    /** 昨日完成数量（环比对照） */
    private Integer yesterdayCount;

    /** 本周完成总数 */
    private Integer weekCount;

    /** 本周每日趋势 (key=日期, value=完成数) */
    private List<DailyTrend> weekTrend;

    @Data
    public static class DailyTrend {
        private String date;
        private Integer count;
    }
}
