package com.mvc.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mvc.dao.CheckHouseDao;
import com.mvc.entityReport.CheckHouse;
import com.mvc.entityReport.RobEfficiency;
import com.mvc.service.CheckHouseService;
import com.utils.CollectionUtil;
import com.utils.FileHelper;
import com.utils.StringUtil;
import com.utils.WordHelper;

/**
 * 领班查房效率相关的service层实现
 * 
 * @author zjn
 * @date 2017年1月17日
 */
@Service("checkHouseServiceImpl")
public class CheckHouseServiceImpl implements CheckHouseService {
	@Autowired
	CheckHouseDao checkHouseDao;

	// 查询领班查房效率列表
	@Override
	public List<CheckHouse> getCheckHouseList(String startTime, String endTime) {
		List<Object> listSource = checkHouseDao.getCheckHouseTime(startTime, endTime);

		Iterator<Object> it = listSource.iterator();
		List<CheckHouse> listGoal = objToCheckHouse(it);
		return listGoal;
	}

	// List<Object>类型转换成List<CheckHouse>
	private List<CheckHouse> objToCheckHouse(Iterator<Object> it) {
		Object[] obj = null;
		String efficiency = "";
		CheckHouse checkHouse = null;
		String checkTime = "";
		String totalTime = "";
		List<CheckHouse> listGoal = new ArrayList<CheckHouse>();
		int i = 0;
		while (it.hasNext()) {
			i++;
			obj = (Object[]) it.next();
			checkHouse = new CheckHouse();

			checkHouse.setOrderNum(String.valueOf((i)));
			checkHouse.setStaffNo(obj[0].toString());
			checkHouse.setStaffName(obj[1].toString());
			checkTime = obj[2].toString();
			totalTime = obj[3].toString();
			efficiency = StringUtil.divide(checkTime, totalTime);
			checkHouse.setCheckTime(checkTime);// 查房总用时
			checkHouse.setTotalTime(totalTime);// 当班总用时
			checkHouse.setEfficiency(((int) (Float.parseFloat(efficiency) * 100)) / 100.0f);// 查房效率

			listGoal.add(checkHouse);
		}
		return listGoal;
	}

	// 导出领班查房效率列表
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResponseEntity<byte[]> exportCheckHouseList(Map<String, Object> map) {
		String startTime = (String) map.get("startTime");
		String endTime = (String) map.get("endTime");
		String path = (String) map.get("path");
		String modelPath = (String) map.get("modelPath");
		String fileName = "客房部领班查房工作效率统计表.docx";
		WordHelper wh = new WordHelper();
		ResponseEntity<byte[]> byteArr = null;
		Map<String, Object> listMap = new HashMap<String, Object>();// 多个实体list放到Map中，在WordHelper中解析
		Map<String, Object> contentMap = new HashMap<String, Object>();// 获取文本数据

		path = FileHelper.transPath(fileName, path);// 解析后的上传路径

		// 获取列表和文本信息
		List<Object> listSource = checkHouseDao.getCheckHouseTime(startTime, endTime);
		Iterator<Object> it = listSource.iterator();
		List<CheckHouse> checkHouseList = objToCheckHouse(it);

		listMap.put("0", checkHouseList);// 注意：key存放该list在word中表格的索引，value存放list
		contentMap.put("${startDate}", startTime);
		contentMap.put("${endDate}", endTime);

		String analyseResult = getAnalyseResult(checkHouseList, "orderNum");
		contentMap.put("${analyseResult}", analyseResult);

		try {
			OutputStream out = new FileOutputStream(path);// 保存路径
			wh.export2007Word(modelPath, listMap, contentMap, 1, out, -1);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byteArr = FileHelper.downloadFile(fileName, path);
		return byteArr;
	}

	@Override
	public String getAnalyseResult(List<CheckHouse> checkHouseList, String writeField) {
		boolean ascFlag = false;
		StringBuilder analyseResult = new StringBuilder();
		CollectionUtil.sort(checkHouseList, "workEffeciencyAvg", ascFlag);
		CollectionUtil<CheckHouse> collectionUtil = new CollectionUtil<CheckHouse>();
		collectionUtil.writeSort(checkHouseList, writeField);
		if (checkHouseList.size() > 3) {
			analyseResult.append("查房效率最高的三名员工为：");
			analyseResult.append(checkHouseList.get(0).getStaffName());
			analyseResult.append("("
					+ StringUtil.strfloatToPer(((int) (checkHouseList.get(0).getEfficiency() * 100)) / 100f) + ")、");
			analyseResult.append(checkHouseList.get(1).getStaffName());
			analyseResult.append("("
					+ StringUtil.strfloatToPer(((int) (checkHouseList.get(1).getEfficiency() * 100)) / 100f) + ")、");
			analyseResult.append(checkHouseList.get(2).getStaffName());
			analyseResult.append("("
					+ StringUtil.strfloatToPer(((int) (checkHouseList.get(2).getEfficiency() * 100)) / 100f) + ")、");
		} else if (checkHouseList.size() == 1) {
			analyseResult.append("抢房效率最高的员工为：");
			analyseResult.append(checkHouseList.get(0).getStaffName());
			analyseResult.append(
					"(" + StringUtil.strfloatToPer(((int) (checkHouseList.get(0).getEfficiency() * 100)) / 100f) + ")");
		}
		return analyseResult.toString();
	}

}
