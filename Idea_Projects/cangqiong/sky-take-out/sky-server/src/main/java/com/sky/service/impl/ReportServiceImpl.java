package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 统计begin-end日期内的营业额
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //构建从begin-end的日期列表  以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = begin;
        dateList.add(begin);
        while(!date.equals(end)) {
            date = date.plusDays(1);
            dateList.add(date);
        }
        //将日期类型转String 并且加上,分隔
        String dateListString = StringUtils.join(dateList,",");

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //查询每个日期的已完成状态的订单金额合计
            LocalDateTime beginOfTheDate = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endOfTheDate = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("beginOfTheDate", beginOfTheDate);
            map.put("endOfTheDate", endOfTheDate);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumTurnoverByMap(map);
            if (turnover == null) {
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }
        String turnoverListString = StringUtils.join(turnoverList,",");

        return TurnoverReportVO.builder()
                .dateList(dateListString)
                .turnoverList(turnoverListString)
                .build();
    }

    /**
     * 统计begin-end日期内的用户数和新增用户数
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = begin;
        dateList.add(begin);
        while(!date.equals(end)){
            date = date.plusDays(1);
            dateList.add(date);
        }
        String dateListString = StringUtils.join(dateList,",");

        //新增和总的用户数公用一个动态sql  只不过新增的判断注册日期还要大于今天.Min
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            LocalDateTime beginOfTheDate = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endOfTheDate = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap<>();
            //截至今天总数
            map.put("endOfTheDate", endOfTheDate);
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);

            //今天新增
            map.put("beginOfTheDate", beginOfTheDate);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(dateListString)
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    /**
     * 统计begin-end日期内的订单总数、有效订单数和有效率
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = begin;
        dateList.add(begin);
        while(!date.equals(end)){
            date = date.plusDays(1);
            dateList.add(date);
        }
        String dateListString = StringUtils.join(dateList,",");

        //每天订单数
        List<Integer> orderCountList = new ArrayList<>();
        //每天有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            LocalDateTime beginOfTheDate = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endOfTheDate = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("beginOfTheDate", beginOfTheDate);
            map.put("endOfTheDate", endOfTheDate);
            Integer orderCount = orderMapper.getOrderCount(map);
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrderCount(map);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //该请求时间内的订单总和和有效订单总和
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        return OrderReportVO.builder()
                .dateList(dateListString)
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .build();
    }

    /**
     * 销量统计排名
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        Map map = new HashMap<>();
        map.put("beginTime", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("endTime", LocalDateTime.of(end, LocalTime.MAX));

        List<GoodsSalesDTO> goodsSalesDTOList = orderDetailMapper.getSalesTop10(map);
        List<String> goodsnameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> goodsNumList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        String nameList = StringUtils.join(goodsnameList,",");
        String numberList = StringUtils.join(goodsNumList,",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1.查询数据库 获得30天内所有数据
        LocalDate beginDay = LocalDateTime.now().toLocalDate().minusDays(30);
        LocalDate endDay = LocalDateTime.now().toLocalDate();
        LocalDateTime begin = LocalDateTime.of(beginDay, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDay, LocalTime.MAX);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);

        //2.向模板文件写文件
        try {
            //读取当前类路径下的资源文件
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/template.xlsx");

            //基于模板文件新建xlsx
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheetAt(0);

            //填充时间
            sheet.getRow(1).getCell(1).setCellValue("时间："+ beginDay+"-"+endDay);

            //填充概览数据

            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());


            //填充30天详细数据
            for(int i = 0;i<30;i++){
                LocalDate eachDay = beginDay.plusDays(i);
                BusinessDataVO eachDayBusinessDataVO = workspaceService.getBusinessData(
                        LocalDateTime.of(eachDay, LocalTime.MIN),
                        LocalDateTime.of(eachDay, LocalTime.MAX));
                XSSFRow row = sheet.getRow(i+7);
                row.getCell(1).setCellValue(eachDay.toString());
                row.getCell(2).setCellValue(eachDayBusinessDataVO.getTurnover());
                row.getCell(3).setCellValue(eachDayBusinessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(eachDayBusinessDataVO.getOrderCompletionRate());
                row.getCell(5).setCellValue(eachDayBusinessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(eachDayBusinessDataVO.getNewUsers());


            }


            //通过response对象获得输出流，文件下载到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            outputStream.close();
            excel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
