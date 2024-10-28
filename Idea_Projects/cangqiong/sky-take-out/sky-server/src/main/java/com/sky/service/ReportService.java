package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Service
public interface ReportService {
    /**
     * 统计begin-end日期内的营业额
     * @param begin
     * @param end
     * @return 日期StringList、营业额StringList
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计begin-end日期内的用户数和新增用户数
     * @param begin
     * @param end
     * @return 日期StringList、用户数totalUserList、newUserList
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计begin-end日期内的订单总数、有效订单数和有效率
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量统计排名
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);

    /**
     * 导出运营数据报表
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
