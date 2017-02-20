package com.mvc.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.mvc.entityReport.LinenCount;
import com.mvc.entityReport.LinenExpend;
import com.mvc.entityReport.MiniCount;
import com.mvc.entityReport.MiniExpend;
import com.mvc.entityReport.RoomCount;
import com.mvc.entityReport.RoomExpend;
import com.mvc.entityReport.WashCount;
import com.mvc.entityReport.WashExpend;
import com.utils.Pager;

import net.sf.json.JSONObject;

/**
 * 耗品统计
 * 
 * @author wq
 * @date 2017年1月13日
 */
public interface ExpendFormService {

	// 布草总数统计
	LinenCount linenTotleCount(Map<String, Object> map);
	
	// 房间耗品总数统计
	RoomCount roomTotleCount(Map<String, Object> map);
		
	// 卫生间耗品总数统计
	WashCount washTotleCount(Map<String, Object> map);
	
	// 迷你吧总数统计
	MiniCount miniTotleCount(Map<String, Object> map);

	// 布草统计分页
	List<LinenExpend> selectLinenPage(Map<String, Object> map, Pager pager);

	// 导出布草使用量统计表
	ResponseEntity<byte[]> exportLinenExpendForm(Map<String, Object> map, String path, String tempPath);

	// 查询布草总条数
	Long countlinenTotal(Map<String, Object> map);

	// 查询房间耗品总条数
	Long countroomTotal(Map<String, Object> map);

	// 查询卫生间耗品总条数
	Long countwashTotal(Map<String, Object> map);
	
	// 查询迷你吧总条数
	Long countminiTotal(Map<String, Object> map);
	
	//查询员工领取耗品总条数
	Long countStaTotal(Map<String, Object> map);

	// 布草统计分析
	String selectLinenExpendAnalyse(Map<String, Object> map);

	// 房间耗品统计分页
	List<RoomExpend> selectRoomExpend(Map<String, Object> map, Pager pager);

	// 导出房间耗品使用量统计表
	ResponseEntity<byte[]> exportRoomExpendForm(Map<String, Object> map, String path, String tempPath);

	// 房间耗品统计分析
	String selectRoomExpendAnalyse(Map<String, Object> map);

	// 卫生间耗品统计分页
	List<WashExpend> selectWashExpend(Map<String, Object> map, Pager pager);

	// 导出卫生间耗品使用量统计表
	ResponseEntity<byte[]> exportWashExpendForm(Map<String, Object> map, String path, String tempPath);

	// 卫生间耗品统计分析
	String selectWashExpendAnalyse(Map<String, Object> map);
	
	// 迷你吧统计分页
	List<MiniExpend> selectMiniPage(Map<String, Object> map, Pager pager);
	
	// 导出迷你吧使用量统计表
	ResponseEntity<byte[]> exportMiniExpendForm(Map<String, Object> map, String path, String tempPath);

	// 迷你吧统计分析
	String selectMiniExpendAnalyse(Map<String, Object> map);

	// 导出耗品统计表excel
	ResponseEntity<byte[]> exportExpendExcel(Map<String, Object> map);

	/********** zjn添加 **********/
	// 导出房间或卫生间耗品分析图
	ResponseEntity<byte[]> exportRoomOrWashExpendPic(Map<String, String> map);

	// 导出布草或迷你吧用量分析图
	ResponseEntity<byte[]> exportLinenOrMiniExpendPic(Map<String, String> map);
	/********** zjn结束 **********/
	
	//获取布草统计列表
	List<LinenExpend> getLinenExpendList(Map<String, Object> map);

	//员工领取耗品统计分页
	JSONObject selectStaExpendPage (Map<String, Object> map, Pager pager);
	
	// 导出员工领取布草量统计表
	ResponseEntity<byte[]> exportStaLinen(Map<String, Object> map, String path, String tempPath);
	
	// 导出员工领取房间耗品量统计表
	ResponseEntity<byte[]> exportStaRoom(Map<String, Object> map, String path, String tempPath);
		
	// 导出员工领取卫生间耗品量统计表
	ResponseEntity<byte[]> exportStaWash(Map<String, Object> map, String path, String tempPath);

	// 导出员工领取迷你吧量统计表
	ResponseEntity<byte[]> exportStaMini(Map<String, Object> map, String path, String tempPath);

	// 导出员工领取耗品统计
	ResponseEntity<byte[]> exportStaExpendExcel(Map<String, Object> map);
}
