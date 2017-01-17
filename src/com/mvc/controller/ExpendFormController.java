package com.mvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.base.constants.ReportFormConstants;
import com.mvc.entityReport.ExpendAnalyse;
import com.mvc.entityReport.LinenExpend;
import com.mvc.entityReport.RoomExpend;
import com.mvc.entityReport.WashExpend;
import com.mvc.service.ExpendFormService;
import com.utils.StringUtil;

import net.sf.json.JSONObject;

/**
 * 
 * @author wq
 * @date 2017年1月12日
 */
@Controller
@RequestMapping("/customerService")
public class ExpendFormController {

	@Autowired
	ExpendFormService expendFormService;

	@RequestMapping("/toReportFormPage.do")
	public String InvoiceReceivePage() {
		return "customerService/index";
	}

	/**
	 * 布草统计
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("/selectLinenExpendFormByLlimits.do")
	public @ResponseBody String selectLinenExpendForm(HttpServletRequest request) {
		JSONObject jsonObject = JSONObject.fromObject(request.getParameter("llimit"));

		Map<String, Object> map = JsonObjToMap(jsonObject);
		List<LinenExpend> list = expendFormService.selectLinenExpend(map);
		
		jsonObject = new JSONObject();
		jsonObject.put("list", list);
		return jsonObject.toString();
	}
	
	/**
	 * 导出布草消耗，word格式
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/exportLinenExpendForm.do")
	public ResponseEntity<byte[]> exportLinenExpendForm(HttpServletRequest request) {
		String formType = null;
		String formName = null;
		String startTime = null;
		String endTime = null;

		if (StringUtil.strIsNotEmpty(request.getParameter("formType"))) {
			formType = request.getParameter("formType");// 报表类型
		}
		if (StringUtil.strIsNotEmpty(request.getParameter("formName"))) {
			formName = request.getParameter("formName");// 报表类型名称
		}
		if (StringUtil.strIsNotEmpty(request.getParameter("startTime"))) {
			startTime = StringUtil.dayFirstTime(request.getParameter("startTime"));// 开始时间
		}
		if (StringUtil.strIsNotEmpty(request.getParameter("endTime"))) {
			endTime = StringUtil.dayLastTime(request.getParameter("endTime"));// 结束时间
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("formType", formType);
		map.put("formName", formName);
		map.put("startTime", startTime);
		map.put("endTime", endTime);

		String path = request.getSession().getServletContext().getRealPath(ReportFormConstants.SAVE_PATH);// 上传服务器的路径
		String tempPath = request.getSession().getServletContext().getRealPath(ReportFormConstants.LINENEXPEND_PATH);// 模板路径
		ResponseEntity<byte[]> byteArr = expendFormService.exportLinenExpendForm(map, path, tempPath);

		return byteArr;
	}
	
	/**
	 *
	 * 布草使用量分析
	 */
	@RequestMapping("/selectLinenExpendAnalyseByLlimits.do")
	public @ResponseBody String selectLinenExpendAnalyse(HttpServletRequest request){
		JSONObject jsonObject = JSONObject.fromObject(request.getParameter("allimit"));

		Map<String, Object> map = JsonObjToMap(jsonObject);
		List<ExpendAnalyse> list = expendFormService.selectLinenExpendAnalyse(map);
		
		jsonObject = new JSONObject();
		jsonObject.put("list", list);
		return jsonObject.toString();
	}

	/**
	 * 房间耗品统计
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/selectRoomExpendFormByRlimits.do")
	public @ResponseBody String selectRoomExpendForm(HttpServletRequest request) {
		JSONObject jsonObject = JSONObject.fromObject(request.getParameter("rlimit"));

		Map<String, Object> map = JsonObjToMap(jsonObject);
		List<RoomExpend> list = expendFormService.selectRoomExpend(map);
		
		jsonObject = new JSONObject();
		jsonObject.put("list", list);
		return jsonObject.toString();
	}
	
	/**
	 * 导出房间消耗，word格式
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/exportRoomExpendForm.do")
	public ResponseEntity<byte[]> exportRoomExpendForm(HttpServletRequest request) {
		String formType = null;
		String formName = null;
		String startTime = null;
		String endTime = null;

		if (StringUtil.strIsNotEmpty(request.getParameter("formType"))) {
			formType = request.getParameter("formType");// 报表类型
		}
		if (StringUtil.strIsNotEmpty(request.getParameter("formName"))) {
			formName = request.getParameter("formName");// 报表类型名称
		}
		if (StringUtil.strIsNotEmpty(request.getParameter("startTime"))) {
			startTime = StringUtil.dayFirstTime(request.getParameter("startTime"));// 开始时间
		}
		if (StringUtil.strIsNotEmpty(request.getParameter("endTime"))) {
			endTime = StringUtil.dayLastTime(request.getParameter("endTime"));// 结束时间
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("formType", formType);
		map.put("formName", formName);
		map.put("startTime", startTime);
		map.put("endTime", endTime);

		String path = request.getSession().getServletContext().getRealPath(ReportFormConstants.SAVE_PATH);// 上传服务器的路径
		String tempPath = request.getSession().getServletContext().getRealPath(ReportFormConstants.ROOMEXPEND_PATH);// 模板路径
		ResponseEntity<byte[]> byteArr = expendFormService.exportRoomExpendForm(map, path, tempPath);

		return byteArr;
	}

	/**
	 *
	 * 房间耗品使用量分析
	 */
	@RequestMapping("/selectRoomExpendAnalyseByRlimits.do")
	public @ResponseBody String selectRoomExpendAnalyse(HttpServletRequest request){
		JSONObject jsonObject = JSONObject.fromObject(request.getParameter("arlimit"));

		Map<String, Object> map = JsonObjToMap(jsonObject);
		List<ExpendAnalyse> list = expendFormService.selectRoomExpendAnalyse(map);
		
		jsonObject = new JSONObject();
		jsonObject.put("list", list);
		return jsonObject.toString();
	}
	
	/**
	 * 卫生间耗品统计
	 * 
	 * @param request
	 * @return
	 */
	  @RequestMapping("/selectWashExpendFormByWlimits.do")
	  public @ResponseBody String selectWashExpendForm(HttpServletRequest request) {
		  JSONObject jsonObject = JSONObject.fromObject(request.getParameter("wlimit"));
	  
	      Map<String, Object> map = JsonObjToMap(jsonObject);
	      List<WashExpend> list = expendFormService.selectWashExpend(map);
  
	      jsonObject = new JSONObject();
	      jsonObject.put("list", list);
	      return jsonObject.toString();
	   }
	  
	  /**
		 * 导出卫生间耗品消耗，word格式
		 * 
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping("/exportWashExpendForm.do")
		public ResponseEntity<byte[]> exportWashExpendForm(HttpServletRequest request) {
			String formType = null;
			String formName = null;
			String startTime = null;
			String endTime = null;

			if (StringUtil.strIsNotEmpty(request.getParameter("formType"))) {
				formType = request.getParameter("formType");// 报表类型
			}
			if (StringUtil.strIsNotEmpty(request.getParameter("formName"))) {
				formName = request.getParameter("formName");// 报表类型名称
			}
			if (StringUtil.strIsNotEmpty(request.getParameter("startTime"))) {
				startTime = StringUtil.dayFirstTime(request.getParameter("startTime"));// 开始时间
			}
			if (StringUtil.strIsNotEmpty(request.getParameter("endTime"))) {
				endTime = StringUtil.dayLastTime(request.getParameter("endTime"));// 结束时间
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("formType", formType);
			map.put("formName", formName);
			map.put("startTime", startTime);
			map.put("endTime", endTime);

			String path = request.getSession().getServletContext().getRealPath(ReportFormConstants.SAVE_PATH);// 上传服务器的路径
			String tempPath = request.getSession().getServletContext().getRealPath(ReportFormConstants.WASHEXPEND_PATH);// 模板路径
			ResponseEntity<byte[]> byteArr = expendFormService.exportWashExpendForm(map, path, tempPath);

			return byteArr;
		}
	
		/**
		*
		* 卫生间耗品使用量分析
		*/
		@RequestMapping("/selectWashExpendAnalyseByWlimits.do")
		public @ResponseBody String selectWashExpendAnalyse(HttpServletRequest request){
			JSONObject jsonObject = JSONObject.fromObject(request.getParameter("wrlimit"));

			Map<String, Object> map = JsonObjToMap(jsonObject);
			List<ExpendAnalyse> list = expendFormService.selectWashExpendAnalyse(map);

			jsonObject = new JSONObject();
			jsonObject.put("list", list);
			return jsonObject.toString();
		}

	/**
	 * 将JsonObject转换成Map
	 * 
	 * @param jsonObject
	 * @return
	 */
	private Map<String, Object> JsonObjToMap(JSONObject jsonObject) {
		String formType = null;
		String formName = null;
		String startTime = null;
		String endTime = null;
		if (jsonObject.containsKey("formType")) {
			if (StringUtil.strIsNotEmpty(jsonObject.getString("formType"))) {
				formType = jsonObject.getString("formType");// 报表类型
			}
		}
		if (jsonObject.containsKey("formName")) {
			if (StringUtil.strIsNotEmpty(jsonObject.getString("formName"))) {
				formType = jsonObject.getString("formName");// 报表类型名称
			}
		}
		if (jsonObject.containsKey("startTime")) {
			if (StringUtil.strIsNotEmpty(jsonObject.getString("startTime"))) {
				startTime = StringUtil.dayFirstTime(jsonObject.getString("startTime"));// 开始时间
			}
		}
		if (jsonObject.containsKey("endTime")) {
			if (StringUtil.strIsNotEmpty(jsonObject.getString("endTime"))) {
				endTime = StringUtil.dayLastTime(jsonObject.getString("endTime"));// 结束时间
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("formType", formType);
		map.put("formName", formName);
		map.put("startTime", startTime);
		map.put("endTime", endTime);

		return map;
	}

}
